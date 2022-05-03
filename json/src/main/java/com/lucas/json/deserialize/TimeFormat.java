package com.lucas.json.deserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Used for annotate all field that requires date conversion, converting the incoming timestamp to a LocalDateTime
 *  If the incoming timestamp is of type String can be use the attribute format to specify the date format
 *  If the incoming timestamp is numeric the format attribute will never used
 * @see java.time.LocalDateTime
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TimeFormat {
    String format() default "";
    JsonTimeType type();
}

