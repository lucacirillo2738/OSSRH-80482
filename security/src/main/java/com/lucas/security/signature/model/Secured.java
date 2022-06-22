package it.sisal.digital.phoenix.utils.security.signature.model;

import it.sisal.digital.phoenix.utils.security.signature.aop.*;

/** A Pojo class used by SecurityAspect to read property and set result of a specific aspect operation.
 * A parameter of this type (or its subclass) must be provided in the signature of the method annotated with
 * Sign, CheckSignature, Encrypt or Decrypt annotation
 * @see SecurityAspect
 * @see Sign
 * @see CheckSignature
 * @see Encrypt
 * @see Decrypt
 * */
public class Secured {

    private String signature;
    private String encryptedContent;
    private String decryptedContent;
    private String contentToEncrypt;
    private String messageToSign;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getDecryptedContent() {
        return decryptedContent;
    }

    public void setDecryptedContent(String decryptedContent) {
        this.decryptedContent = decryptedContent;
    }

    public String getContentToEncrypt() {
        return contentToEncrypt;
    }

    public void setContentToEncrypt(String contentToEncrypt) {
        this.contentToEncrypt = contentToEncrypt;
    }

    public String getMessageToSign() {
        return messageToSign;
    }

    public void setMessageToSign(String messageToSign) {
        this.messageToSign = messageToSign;
    }
}
