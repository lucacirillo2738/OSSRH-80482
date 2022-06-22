package com.lucas.security.signature.aop;

import com.lucas.security.signature.model.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation used to check signature.
 * Declare a Secured param or its subclass in method signature.
 * read:
 *      -   Secured.messageToSign that must contains the message to sign
 * set result in:
 *      -   Secured.signature
 * if signature is not valid throws SecurityException
 * @see Secured
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sign {
    boolean hashingMessage() default true;
    boolean encodeBase64() default true;
    String signAlg() default "SHA256withRSA";
    String digestAlg() default "SHA-256";
}
