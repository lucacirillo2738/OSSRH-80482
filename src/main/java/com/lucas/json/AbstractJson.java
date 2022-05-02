package com.lucas.json;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractJson extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

    @Parameter( property ="sourceDir", required = true)
    private String sourceDir;

    @Parameter( property = "targetDir", defaultValue = "${project.build.outputDirectory}" )
    protected String targetDir;

    public <T extends Annotation>  Set<Class> findAllMatchingTypes(Class<T> toFind) {

        return findClassesSubPackage(sourceDir, toFind);
    }

    public <T extends Annotation> Set<Class> findClassesSubPackage(String packageName, Class<T> toFind){
        Set<Class> result = new HashSet<>();

        try{
            InputStream stream = getClassLoader(this.project).getResourceAsStream(packageName.replaceAll("[.]", "/"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            reader.lines()
                    .forEach(line -> {
                        if(line.endsWith(".class")){
                            Class<?> clazz = getClass(line, packageName);

                            if(clazz.isAnnotationPresent(toFind)){
                                result.add(clazz);
                            }
                        }else{
                            result.addAll(findClassesSubPackage(packageName + "." + line, toFind));
                        }
                    });

            result.remove(null);
        }catch (NullPointerException e){
            System.err.println("package not found!");
        }
        return result;
    }

    private <T> Class<?> getClass(String className, String packageName) {
        try {
            return getClassLoader(this.project).loadClass(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));

        } catch (ClassNotFoundException e) {
            System.err.println("CLASSNOTFOUND: " + className);
        }
        return null;
    }

    protected ClassLoader getClassLoader(MavenProject project)
    {
        try
        {
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i )
            {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURL();
            }
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        }
        catch ( Exception e )
        {
            getLog().debug( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }

    @Override
    public abstract void execute() throws MojoExecutionException, MojoFailureException;
}
