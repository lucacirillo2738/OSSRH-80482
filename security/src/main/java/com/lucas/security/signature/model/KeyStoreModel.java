package it.sisal.digital.phoenix.utils.security.signature.model;

import it.sisal.digital.phoenix.utils.security.SecurityException;
import it.sisal.digital.phoenix.utils.security.util.CipherUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Base64;

/** A Pojo class that contains a public and eventually a private key of own or imported certificate
 * */
public class KeyStoreModel {

    private static Logger logger = LoggerFactory.getLogger(KeyStoreModel.class);

    PrivateKey privateKey;
    PublicKey publicKey;

    public KeyStoreModel(){}

    public KeyStoreModel(String certPath, String alias, String pwd) {
        build(certPath, alias, pwd);
    }

    public KeyStoreModel(String certPath, String alias, String pwd, String pkPwd)  {
        KeyStore keyStore = build(certPath, alias, pwd);
        if(keyStore != null && alias != null && pkPwd != null) {
            try {
                this.privateKey = getPrivateKey(keyStore, alias, pkPwd);
            }catch (Exception e){
                logger.warn(MessageFormat.format("Cannot get PrivateKey {0}", e.getMessage()));
            }
        }else{
            logger.warn("Cannot load PrivateKey. Check cert configuration");
        }
    }

    private KeyStore build(String certPath, String alias, String pwd){
        KeyStore keyStore = null;
        if(certPath != null && pwd != null){
            try {
                keyStore = loadKeyStore(certPath, pwd);
            }catch (Exception e){
                logger.warn("Cannot load KeyStore" + e.getMessage());
            }
            if(keyStore != null && alias != null) {
                try {
                    this.publicKey = getPublicKey(keyStore, alias);
                }catch (Exception e){
                    logger.warn("Cannot get PublicKey" + e.getMessage());
                }
            }else{
                logger.warn("Cannot load PublicKey. Check cert configuration");
            }
        }else{
            logger.warn("Cannot load KeyStore. Check cert configuration");
        }
        return keyStore;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String decrypt(String cipherContent, String chiperAlg, boolean decodeBase64, int blockSize) {
        String decoded;
        try{
            Cipher cipher = Cipher.getInstance(chiperAlg);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] cipherContentBytes;
            if(decodeBase64){
                cipherContentBytes = Base64.getDecoder().decode(cipherContent.getBytes());
            }else{
                cipherContentBytes = cipherContent.getBytes();
            }

            byte[] decryptedContent = CipherUtils.doBlock(cipher, cipherContentBytes, blockSize);

            decoded = new String(decryptedContent);
        }catch(Exception e){
            throw new SecurityException(e, "Error during encryption", SecurityException.ErrorCode.DECRYPTION_ERROR);
        }
        return decoded;
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

    public byte[] signMsg(byte[] msgToSing, String signAlg){
        byte[] signature = new byte[0];
        if(privateKey != null){
            logger.debug("Starting to sign message: [{}]", msgToSing);
            Signature sig;
            try {
                sig = Signature.getInstance(signAlg);
            } catch (NoSuchAlgorithmException e) {
                throw new SecurityException(MessageFormat.format("No algorithm ''{0}'' found", signAlg), SecurityException.ErrorCode.SIGNATURE_ERROR, e);
            }
            try {
                sig.initSign(privateKey);
            } catch (InvalidKeyException e) {
                throw new SecurityException("Error during Signature initialization", SecurityException.ErrorCode.SIGNATURE_ERROR, e);
            }
            try {
                sig.update(msgToSing);
            } catch (SignatureException e) {
                throw new SecurityException("Error during updating data to be signed", SecurityException.ErrorCode.SIGNATURE_ERROR, e);
            }
            try {
                signature = sig.sign();
            } catch (SignatureException e) {
                throw new SecurityException("Error during message sign", SecurityException.ErrorCode.SIGNATURE_ERROR, e);
            }

            logger.debug("Message correctly signed: [{}]", msgToSing);
        }else{
            logger.warn("Private key is null. Check cert configuration");
        }
        return signature;
    }

    private KeyStore loadKeyStore(String certPath, String pwd) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore;
        logger.info("Loading keystore from {}", certPath);
        FileInputStream fileInputStream = new FileInputStream(new File(certPath));
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(fileInputStream, pwd.toCharArray());
        logger.info("keystore loaded!");
        fileInputStream.close();
        return keyStore;
    }

    private PublicKey getPublicKey(KeyStore keyStore, String alias) throws KeyStoreException {
        logger.info("Get PublicKey : alias {} ", alias);
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate.getPublicKey();
    }

    private PrivateKey getPrivateKey(KeyStore keyStore, String alias, String pwd) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        logger.info("Get PrivateKey : alias {} password {}", alias, pwd);
        return (PrivateKey) keyStore.getKey(alias, pwd.toCharArray());
    }
}
