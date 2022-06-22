package it.sisal.digital.phoenix.utils.security.signature.aop;

import it.sisal.digital.phoenix.utils.security.signature.model.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation used to decrypt message.
 * Declare a Secured param or its subclass in method signature.
 * read:
 *      -   Secured.contentToEncrypt that must contains the message to encrypt
 * set result in:
 *      -   Secured.encryptedContent
 * if signature is not valid throws SecurityException
 * @see Secured
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
    String clientName();
    String chiperAlg() default "RSA/ECB/PKCS1Padding";
    boolean encodeBase64() default true;
    int blockSize() default 245;
}

