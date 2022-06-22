package com.lucas.security.signature.aop;

import com.lucas.security.signature.model.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation used to decrypt message.
 * Declare a Secured param or its subclass in method signature.
 * read:
 *      -   Secured.encryptedContent that must contains the message to decrypt
 * set result in:
 *      -   Secured.decryptedContent
 * if signature is not valid throws SecurityException
 * @see Secured
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
    String chiperAlg() default "RSA/ECB/PKCS1Padding";
    boolean decodeBase64() default true;
    int blockSize() default 256;
}
