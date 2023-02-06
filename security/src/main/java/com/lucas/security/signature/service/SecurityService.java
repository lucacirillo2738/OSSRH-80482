package com.lucas.security.signature.service;

import com.lucas.security.SecurityException;
import com.lucas.security.signature.model.ClientsModel;
import com.lucas.security.signature.model.KeyStoreModel;
import com.lucas.security.util.CipherUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;
import java.util.function.Supplier;

/** Provides API to execute operation on KeyStoreModel objects.
 * Has one KeyStoreModel that holds own certificate and private key and one KeyStoreModel that holds the certificate of the caller
 * The two KeyStoreModel are generate from properties that must be defined in application.yml as follow
 *   cert:
 *     mine:
 *       certPath: "[path-to-keystore.jks]"
 *       alias: "[alias]"
 *       pwd: "[password]"
 *       ok-password: "[private-key-pasword]"
 *     clients:
 *       [client-name]:
 *          certPath: "[path-to-keystore.jks]"
 *          alias: "[alias]"
 *          pwd: "[alias-pasword]"
 *       [client-name]:
 *          certPath: "[path-to-keystore.jks]"
 *          alias: "[alias]"
 *          pwd: "[alias-pasword]"
 * @see KeyStoreModel
 * */
@Component("SisalSignature")
public class SecurityService {

    private static Logger logger = LoggerFactory.getLogger(SecurityService.class);


    @Autowired
    @Qualifier("mineKeyStore")
    KeyStoreModel mineKeyStore;

    @Autowired
    @Qualifier("callerKeyStore")
    ClientsModel callerKeyStore;


    private byte[] getHash(String msg, String digestAlg) {
        byte[] messageBytes = msg.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(digestAlg);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Error during hash message calculation", SecurityException.ErrorCode.SIGNATURE_ERROR, e);
        }
        return md.digest(messageBytes);
    }

    private Signature getSignature(KeyStoreModel ks, String signAlg) throws InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = null;
        if(ks.getPublicKey() != null){
            signature = Signature.getInstance(signAlg);
            signature.initVerify(ks.getPublicKey());
        }else{
            logger.warn("Public key is null. Check cert configuration");
        }
        return signature;
    }

    private String encrypt(Supplier<KeyStoreModel> supplier, String content, String chiperAlg, boolean encodeBase64, int blockSize){
        String encoded;
        try{
            byte[] contentBytes = content.getBytes();
            Cipher cipher = Cipher.getInstance(chiperAlg);
            cipher.init(Cipher.ENCRYPT_MODE, supplier.get().getPublicKey());

            byte[] cipherContent = CipherUtils.doBlock(cipher, contentBytes, blockSize);

            if(encodeBase64){
                encoded = Base64.getEncoder().encodeToString(cipherContent);
            }else{
                encoded = new String(cipherContent);
            }
        }catch(Exception e){
            throw new SecurityException(e, "Error during encryption", SecurityException.ErrorCode.ENCRYPTION_ERROR);
        }
        return encoded;
    }

    private byte[] append(byte[] prefix, byte[] suffix){
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i=0; i< prefix.length; i++){
            toReturn[i] = prefix[i];
        }
        for (int i=0; i< suffix.length; i++){
            toReturn[i+prefix.length] = suffix[i];
        }
        return toReturn;
    }

    private String signMsg(Supplier<KeyStoreModel> supplier, String msgToSing, boolean hashingMessage, boolean encodeBase64, String signAlg, String digestAlg) {
        String signature;

        byte[] byteMsg;
        if(hashingMessage){
            byteMsg = getHash(msgToSing, digestAlg);
        }else{
            byteMsg = msgToSing.getBytes();
        }

        byte[] signedMsg = supplier.get().signMsg(byteMsg, signAlg);

        if(encodeBase64){
            signature = Base64.getEncoder().encodeToString(signedMsg);
        }else{
            signature = new String(signedMsg);
        }
        return signature;
    }

    private boolean checkSignature(Supplier<KeyStoreModel> supplier, String sign, String messagePart, boolean hashingMessage, boolean decodeBase64, String signAlg, String digestAlg) {
        boolean isCorrect = false;
        try{
            byte[] byteMsg;
            if(hashingMessage){
                byteMsg = getHash(messagePart, digestAlg);
            }else{
                byteMsg = messagePart.getBytes();
            }
            byte[] signBytes;
            if(decodeBase64){
                signBytes = Base64.getDecoder().decode(sign);
            }else{
                signBytes = sign.getBytes();
            }
            Signature callerSignature = getSignature(supplier.get(), signAlg);
            if(callerSignature == null){
                throw new SecurityException("Error during hash message calculation", SecurityException.ErrorCode.SIGNATURE_ERROR);
            }
            callerSignature.update(byteMsg);
            isCorrect = callerSignature.verify(signBytes);
        }catch(Exception e){
            throw new SecurityException(e, "Error signature checking", SecurityException.ErrorCode.SIGNATURE_ERROR);
        }
        return isCorrect;
    }

    public boolean checkSignature(String clientName, String sign, String messagePart, boolean hashingMessage, boolean decodeBase64, String signAlg, String digestAlg) {
        return checkSignature(() -> callerKeyStore.getClients().get(clientName), sign, messagePart, hashingMessage, decodeBase64, signAlg, digestAlg);
    }

    public boolean checkSignature(KeyStoreModel ks, String sign, String messagePart, boolean hashingMessage, boolean decodeBase64, String signAlg, String digestAlg) {
        return checkSignature(() -> ks, sign, messagePart, hashingMessage, decodeBase64, signAlg, digestAlg);
    }

    public String encrypt(String clientName, String content, String chiperAlg, boolean encodeBase64, int blockSize) {
        return encrypt(() -> callerKeyStore.getClients().get(clientName), content, chiperAlg, encodeBase64, blockSize);
    }

    public String encrypt(KeyStoreModel ks, String content, String chiperAlg, boolean encodeBase64, int blockSize) {
        return encrypt(() -> ks, content, chiperAlg, encodeBase64, blockSize);
    }

    public String decrypt(String content, String chiperAlg, boolean encodeBase64, int blockSize) {
        return mineKeyStore.decrypt(content, chiperAlg, encodeBase64, blockSize);
    }

    public String signMsg(String msgToSing, boolean hashingMessage, boolean encodeBase64, String signAlg, String digestAlg) {
        return signMsg(() -> mineKeyStore, msgToSing, hashingMessage, encodeBase64, signAlg, digestAlg);
    }

    public String signMsg(KeyStoreModel ks, String msgToSing, boolean hashingMessage, boolean encodeBase64, String signAlg, String digestAlg) {
        return signMsg(() -> ks, msgToSing, hashingMessage, encodeBase64, signAlg, digestAlg);
    }

}
