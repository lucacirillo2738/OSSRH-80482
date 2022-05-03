package com.lucas.json.serialize;


import com.lucas.json.AbstractJson;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = Serialize.JSON_PREFIX)
public class Serialize extends AbstractJson {

    public static final String JSON_PREFIX = "serialize";
    int i = 0;

    @Override
    public void execute() throws MojoExecutionException {
        Set<Class> classes = findAllMatchingTypes(Serializer.class);

        classes.forEach(c -> {
            try {
                writeClass(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void writeClass(Class c) throws NotFoundException, CannotCompileException, IOException {
        StringBuilder builderBody = serialize(c);

        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(getClassLoader(this.project)));

        CtClass ctClass = pool.get(c.getName());
        System.out.println(c);
        CtMethod builder = CtNewMethod.make("public " + String.class.getName() + " toJson() {" +
                builderBody.toString() +
                "}", ctClass);


        try {
            ctClass.addMethod(builder);
            ctClass.writeFile(targetDir);
            ctClass.toClass(new ClassLoader() {

                @Override
                public Class<?> loadClass(String s) throws ClassNotFoundException {
                    return getClassLoader(project).loadClass(s);
                }
            });
            ctClass.defrost();
        } catch (DuplicateMemberException e) {
            System.err.println(c + " has already a builder method than it will not be overwrite!");
        }
    }

    public StringBuilder serialize(Type type) {
        System.out.println(type);

        StringBuilder builder = new StringBuilder();
        builder.append(StringBuilder.class.getName());
        builder.append(" builder = new ");
        builder.append(StringBuilder.class.getName());
        builder.append("();");
        builder = serialize(builder, type, "this");
        builder.append("return builder.toString().replaceAll(\",,\", \",\").replaceAll(\",}\", \"}\").replaceAll(\",]\",\"]\");");
        return builder;
    }

    public StringBuilder serialize(StringBuilder builder, Type type, String name) {
        if (type instanceof Class) {
            Class clazz = (Class) type;

            String actualTypeString = "(("+clazz.getName()+")"+name+")";

            if (Number.class.isAssignableFrom((Class) type) 
                || Boolean.class.isAssignableFrom((Class) type)
                || short.class.isAssignableFrom((Class) type)
                || int.class.isAssignableFrom((Class) type)
                || long.class.isAssignableFrom((Class) type)
                || float.class.isAssignableFrom((Class) type)
                || double.class.isAssignableFrom((Class) type)
                || boolean.class.isAssignableFrom((Class) type)
                || byte.class.isAssignableFrom((Class) type)
                || Byte.class.isAssignableFrom((Class) type)
                ) {
                builder.append("builder.append(" + actualTypeString + ");");
                builder.append("builder.append(\",\");");
            } else if (LocalDateTime.class.isAssignableFrom((Class) type) || LocalDate.class.isAssignableFrom((Class) type) || LocalTime.class.isAssignableFrom((Class) type)) {
                builder.append("if("+name+" != null){");
                builder.append("builder.append(\"\\\"\"+" + actualTypeString + ".toString().replaceAll(\":\",\"-\")+\"\\\"\");");
                builder.append("} else {");
                builder.append("builder.append(\"null\");");
                builder.append("}");
                builder.append("builder.append(\",\");");
            } else if (String.class.isAssignableFrom((Class) type)) {
                builder.append("if("+name+" != null){");
                    builder.append("builder.append(\"\\\"\");");
                    builder.append("builder.append(" + actualTypeString + ");");
                    builder.append("builder.append(\"\\\"\");");
                builder.append("} else {");
                    builder.append("builder.append(\"null\");");
                builder.append("}");
                builder.append("builder.append(\",\");");
            }  else if(!name.equals("this") && JsonSerializable.class.isAssignableFrom((Class) type)){
                try {
                    writeClass((Class) type);
                } catch (Exception excptn) {
                    excptn.printStackTrace();
                }
                builder.append("builder.append((("+((Class) type).getName()+")" + name + ").toJson());");
                builder.append("builder.append(\",\");");
            } else {
                builder.append("builder.append(\"{\");");
                Field[] fields = clazz.getDeclaredFields();

                Map<String, Method> methods = new HashMap<>();
                Arrays.asList(clazz.getMethods()).stream().filter(m -> m.getName().startsWith("get")).forEach(m -> methods.put(m.getName().replaceFirst("get", "").toLowerCase(), m));

                Map<Field, Method> fieldGetterMap = new HashMap<>();
                for (Field field : fields) {
                    fieldGetterMap.put(field, methods.get(field.getName().toLowerCase()));
                }


                List<Map.Entry<Field, Method>> ordered = new ArrayList<>();

                for(int i = 0; i <= fieldGetterMap.entrySet().size(); i++){
                    for(Map.Entry<Field, Method>  entry : fieldGetterMap.entrySet()){
                        for (Annotation a : Arrays.asList(entry.getKey().getDeclaredAnnotations())){
                            if(a instanceof JsonOrder && ((JsonOrder)a).value() == i){
                                ordered.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                }

                List<Map.Entry<Field, Method>> unordered = fieldGetterMap.entrySet().stream().filter(e -> e.getKey().getDeclaredAnnotations() == null ||
                        e.getKey().getDeclaredAnnotations().length == 0 ||
                        Arrays.asList(e.getKey().getDeclaredAnnotations()).stream().anyMatch(a -> !(a instanceof JsonOrder))).collect(Collectors.toList());
                ordered.addAll(unordered);

                ordered.stream().forEach(e ->
                        {

                            Field field = e.getKey();
                            boolean skip = Arrays.asList(field.getDeclaredAnnotations()).stream().anyMatch(a -> a instanceof SkipSerialization);
                            if(!skip) {
                                Method getter = e.getValue();
                                if (getter != null) {
                                    if (String.class.isAssignableFrom(field.getType())) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");
                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");
                                        builder.append("builder.append(\"\\\"\"+");
                                        builder.append(actualTypeString);
                                        builder.append(".");
                                        builder.append(getter.getName());
                                        builder.append("()+\"\\\"");
                                        builder.append(",\");");
                                        builder.append("} else {");
                                        builder.append("builder.append(\"null\");");
                                        builder.append("}");

                                        builder.append("builder.append(\",\");");
                                    } else if (Number.class.isAssignableFrom(field.getType()) 
                                        || Boolean.class.isAssignableFrom(field.getType())
                                        || short.class.isAssignableFrom(field.getType())
                                        || int.class.isAssignableFrom(field.getType())
                                        || long.class.isAssignableFrom(field.getType())
                                        || float.class.isAssignableFrom(field.getType())
                                        || double.class.isAssignableFrom(field.getType())
                                        || boolean.class.isAssignableFrom(field.getType())
                                        || byte.class.isAssignableFrom(field.getType())
                                        || Byte.class.isAssignableFrom(field.getType())
                                        ) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");

                                        builder.append("builder.append(");
                                        builder.append(actualTypeString);
                                        builder.append(".");
                                        builder.append(getter.getName());
                                        builder.append("());");
                                        builder.append("builder.append(\",\");");
                                    } else if (LocalDateTime.class.isAssignableFrom(field.getType()) || LocalDate.class.isAssignableFrom(field.getType()) || LocalTime.class.isAssignableFrom(field.getType())) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");

                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");
                                        builder.append("builder.append(\"\\\"\"+" + actualTypeString + "." + getter.getName() + "().toString().replaceAll(\":\",\"-\")+\"\\\"\");");
                                        builder.append("} else {");
                                        builder.append("builder.append(\"null\");");
                                        builder.append("}");

                                        builder.append("builder.append(\",\");");
                                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");

                                        builder.append("builder.append(\"[\");");
                                        i++;

                                        Type listType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");
                                        builder.append("for(int i" + i + " = 0; i" + i + " < " + actualTypeString + "." + getter.getName() + "().size(); i" + i + "++){");


                                        if (Set.class.isAssignableFrom(field.getType())) {
                                            serialize(builder, listType, "new " + ArrayList.class.getName() + "(" + actualTypeString + "." + getter.getName() + "()).get(i" + i + ")");

                                        } else {
                                            serialize(builder, listType, actualTypeString + "." + getter.getName() + "().get(i" + i + ")");

                                        }
                                        builder.append("builder.append(\",\");");
                                        builder.append("}");
                                        builder.append("}");
                                        builder.append("builder.append(\"]\");");

                                        builder.append("builder.append(\",\");");
                                    } else if (Map.class.isAssignableFrom(field.getType())) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");

                                        builder.append("builder.append(\"{\");");

                                        Type key = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        Type value = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
                                        i++;

                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");

                                        builder.append(Iterator.class.getName() + " it" + i + " = " + actualTypeString + "." + getter.getName() + "().entrySet().iterator();");
                                        builder.append("while(it" + i + ".hasNext()){");

                                        builder.append(Object.class.getName() + " next = it" + i + ".next();");
                                        if (Number.class.isAssignableFrom((Class) key)
                                            || short.class.isAssignableFrom((Class) key)
                                            || int.class.isAssignableFrom((Class) key)
                                            || long.class.isAssignableFrom((Class) key)
                                            || float.class.isAssignableFrom((Class) key)
                                            || double.class.isAssignableFrom((Class) key)
                                            || byte.class.isAssignableFrom((Class) key)
                                            || Byte.class.isAssignableFrom((Class) key)
                                            ) {
                                            builder.append("builder.append(((" + Map.Entry.class.getName() + ")next).getKey());");
                                            builder.append("builder.append(\": \");");
                                        } else if (String.class.isAssignableFrom((Class) key)) {
                                            builder.append("builder.append(\"\\\"\");");
                                            builder.append("builder.append(((" + Map.Entry.class.getName() + ")next).getKey());");
                                            builder.append("builder.append(\"\\\":\");");
                                        }
                                        serialize(builder, value, "((" + Map.Entry.class.getName() + ")next).getValue()");

                                        builder.append("builder.append(\",\");");
                                        builder.append("}");
                                        builder.append("}");

                                        builder.append("builder.append(\"}\");");

                                        builder.append("builder.append(\",\");");
                                    } else if (JsonSerializable.class.isAssignableFrom(field.getType())) {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");

                                        try {
                                            writeClass(field.getType());
                                        } catch (Exception excptn) {
                                            excptn.printStackTrace();
                                        }
                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");
                                        builder.append("builder.append(" + actualTypeString + "." + getter.getName() + "().toJson());");
                                        builder.append("}else{builder.append(\"null\");}");

                                        builder.append("builder.append(\",\");");
                                    } else {
                                        builder.append("builder.append(\"\\\"");
                                        builder.append(field.getName());
                                        builder.append("\\\":\");");
                                        builder.append("if(" + actualTypeString + "." + getter.getName() + "() != null){");
                                        serialize(builder, field.getType(), actualTypeString + "." + getter.getName() + "()");
                                        builder.append("}else{builder.append(\"null\");}");

                                        builder.append("builder.append(\",\");");

                                    }
                                }
                            }
                        }
                );
                if (fieldGetterMap.size() > 0) {
                    builder.replace(builder.length() - "builder.append(\",\");".length(), builder.length(), "");
                }
                builder.append("builder.append(\"}\");");

                builder.append("if(!\""+name+"\".equals(\"this\")){builder.append(\",\");}");
            }

        } else if (type instanceof ParameterizedType) {
            if (Collection.class.isAssignableFrom((Class)((ParameterizedType) type).getRawType())) {
                String actualTypeString = "(("+type.getTypeName()+")"+name+")";

                builder.append("builder.append(\"[\");");
                i++;
                Type listType = ((ParameterizedType) type).getActualTypeArguments()[0];

                builder.append("if("+name+" != null){");
                builder.append("for(int i" + i + " = 0; i" + i + " < (("+Collection.class.getName()+") " + name + ").size(); i" + i + "++){");

                if(Set.class.isAssignableFrom(listType.getClass())){
                    serialize(builder, listType, "new "+ArrayList.class.getName()+"(("+List.class.getName()+")"+ name+ ")).get(i" + i + ")");

                }else{
                    serialize(builder, listType, "(("+List.class.getName()+")" + name + ").get(i" + i + ")");

                }

                builder.append("builder.append(\",\");");
                builder.append("}");
                builder.append("}");
                builder.append("builder.append(\"]\");");
            } else if (Map.class.isAssignableFrom((Class)((ParameterizedType) type).getRawType())) {
                builder.append("builder.append(\"{\");");

                Type key = ((ParameterizedType) type).getActualTypeArguments()[0];
                Type value = ((ParameterizedType) type).getActualTypeArguments()[1];
                i++;
                builder.append("if("+name+" != null){");

                builder.append(Iterator.class.getName()+" it"+i+" = (("+Map.class.getName()+")"+ name + ").entrySet().iterator();");
                builder.append("while(it"+i+".hasNext()){");

                builder.append(Object.class.getName()+" next = it"+i+".next();");

                if (Number.class.isAssignableFrom((Class) key)
                    || short.class.isAssignableFrom((Class) key)
                    || int.class.isAssignableFrom((Class) key)
                    || long.class.isAssignableFrom((Class) key)
                    || float.class.isAssignableFrom((Class) key)
                    || double.class.isAssignableFrom((Class) key)
                    || byte.class.isAssignableFrom((Class) key)
                    || Byte.class.isAssignableFrom((Class) key)
                    ) {
                    builder.append("builder.append((("+Map.Entry.class.getName()+")next).getKey());");
                    builder.append("builder.append(\":\");");
                } else if (String.class.isAssignableFrom((Class) key)) {
                    builder.append("builder.append(\"\\\"\");");
                    builder.append("builder.append((("+Map.Entry.class.getName()+")next).getKey());");
                    builder.append("builder.append(\"\\\":\");");
                }
                serialize(builder, value, "(("+Map.Entry.class.getName()+")next).getValue()");

                builder.append("builder.append(\",\");");
                builder.append("}");
                builder.append("}");


                builder.append("builder.append(\"}\");");
            }

        }


        return builder;
    }

}
