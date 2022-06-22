package it.sisal.digital.phoenix.utils.security.signature.aop;


import it.sisal.digital.phoenix.utils.security.signature.model.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**Annotation used to sign a message.
 * Declare a Secured param or its subclass in method signature.
 * read:
 *      -   Secured.messageToSign that must contains the message to compare with the message in signature
 *      -   Secure.signature that must contains the signature
 * if signature is not valid throws SecurityException
 * @see Secured
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSignature {
    String clientName();
    boolean hashingMessage() default true;
    boolean decodeBase64() default true;
    String signAlg() default "SHA256withRSA";
    String digestAlg() default "SHA-256";
}
