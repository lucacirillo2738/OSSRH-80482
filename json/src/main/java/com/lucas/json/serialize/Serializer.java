package com.lucas.json.serialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation used for tag all classes that requires to be serialized.
 * It will allow the compile time generation of toJson() method
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Serializer {
}
