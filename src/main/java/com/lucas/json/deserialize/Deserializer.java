package com.lucas.json.deserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation used for tag all classes that requires to be deserialized.
 * It will allow the compile time generation of toObject(String json) method
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Deserializer {
}
