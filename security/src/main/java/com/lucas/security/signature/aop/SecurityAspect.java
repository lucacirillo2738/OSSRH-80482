package com.lucas.security.signature.aop;

import com.lucas.security.signature.model.Secured;
import com.lucas.security.signature.service.SecurityService;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class SecurityAspect {

    @Resource(name = "SisalSignature")
    private SecurityService certService;

    /** Generate a signature from Secured.messagetToSign field and place results in Secured.signature .
     * Annotate a method with Sign annotation. The method must contains a Secured.class param or its subclass
     * @see Secured
     * @throws SecurityException
     * */
    @Before("@annotation(com.lucas.security.signature.aop.Sign)")
    public void sign(JoinPoint joinPoint) {
        Sign certAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Sign.class);

        if(certAnnotation != null) {
            Object[] objs = joinPoint.getArgs();
            Optional<Secured> signedRequest = Arrays.asList(objs).stream().filter(o -> o instanceof Secured).findFirst().map(o -> (Secured) o);

            if (signedRequest.isPresent()) {
                String signature;

                try {
                    signature = certService.signMsg(signedRequest.get().getMessageToSign(),
                            certAnnotation.hashingMessage(),
                            certAnnotation.encodeBase64(),
                            certAnnotation.signAlg(),
                            certAnnotation.digestAlg());
                    signedRequest.get().setSignature(signature);
                } catch (Exception e) {
                    throw new SecurityException(MessageFormat.format("Sign operation fail {0}", e));
                }
            } else {
                throw new SecurityException("Method annotated with @Sign needs a child of Secured as method parameter");
            }
        }
    }

    /** Check signature contained in Secured.signature field.
     * Annotate a method with CheckSign annotation. The method must contains a Secured.class param or its subclass
     * @see Secured
     * @throws SecurityException
     * */
    @Before("@annotation(com.lucas.security.signature.aop.CheckSignature)")
    public void checkSignature(JoinPoint joinPoint) {
        CheckSignature certAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CheckSignature.class);

        if(certAnnotation != null) {
            Object[] objs = joinPoint.getArgs();
            Optional<Secured> signedRequest = Arrays.asList(objs).stream().filter(o -> o instanceof Secured).findFirst().map(o -> (Secured) o);

            if (signedRequest.isPresent()) {
                boolean isCorrect;

                try {
                    isCorrect = certService.checkSignature(
                            certAnnotation.clientName(),
                            signedRequest.get().getSignature(),
                            signedRequest.get().getMessageToSign(),
                            certAnnotation.hashingMessage(),
                            certAnnotation.decodeBase64(),
                            certAnnotation.signAlg(),
                            certAnnotation.digestAlg());
                } catch (Exception e) {
                    throw new SecurityException(MessageFormat.format("Check signature operation fail  {0}", e));
                }
                if (!isCorrect) {
                    throw new SecurityException("Invalid signature");
                }
            } else {
                throw new SecurityException("Method annotated with @CheckSignature needs a child of Secured as method parameter");
            }
        }
    }

    /** Encrypt a message contained in Secured.contentToEncrypt field and place results in Secured.encryptedContent .
     * Annotate a method with Encrypt annotation. The method must contains a Secured.class param or its subclass
     * @see Secured
     * @throws SecurityException
     * */
    @Before("@annotation(com.lucas.security.signature.aop.Encrypt)")
    public void encrypt(JoinPoint joinPoint) {
        Encrypt certAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Encrypt.class);

        if(certAnnotation != null) {
            Object[] objs = joinPoint.getArgs();
            Optional<Secured> signedRequest = Arrays.asList(objs).stream().filter(o -> o instanceof Secured).findFirst().map(o -> (Secured) o);

            if (signedRequest.isPresent()) {
                String encryptedContent;

                try {
                    encryptedContent = certService.encrypt(
                            certAnnotation.clientName(),
                            signedRequest.get().getContentToEncrypt(),
                            certAnnotation.chiperAlg(),
                            certAnnotation.encodeBase64(),
                            certAnnotation.blockSize()
                    );
                } catch (Exception e) {
                    throw new SecurityException(MessageFormat.format("Encryption operation fail {0}", e));
                }
                signedRequest.get().setEncryptedContent(encryptedContent);
            } else {
                throw new SecurityException("Method annotated with @CheckSignature needs a child of Secured as method parameter");
            }
        }
    }

    /** Decrypt a message contained in Secured.encryptedContent field and place results in Secured.decryptedContent .
     * Annotate a method with Decrypt annotation. The method must contains a Secured.class param or its subclass
     * @see Secured
     * @throws SecurityException
     * */
    @Before("@annotation(com.lucas.security.signature.aop.Decrypt)")
    public void dencrypt(JoinPoint joinPoint) {
        Decrypt certAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Decrypt.class);

        if(certAnnotation != null) {
            Object[] objs = joinPoint.getArgs();
            Optional<Secured> signedRequest = Arrays.asList(objs).stream().filter(o -> o instanceof Secured).findFirst().map(o -> (Secured) o);

            if (signedRequest.isPresent()) {
                String dencryptedContent;

                try {
                    dencryptedContent = certService.decrypt(signedRequest.get().getEncryptedContent(),
                            certAnnotation.chiperAlg(),
                            certAnnotation.decodeBase64(),
                            certAnnotation.blockSize()
                    );
                } catch (Exception e) {
                    throw new SecurityException(MessageFormat.format("Decryption operation fail {0}", e));
                }
                signedRequest.get().setDecryptedContent(dencryptedContent);
            } else {
                throw new SecurityException("Method annotated with @Decrypt needs a child of Secured as method parameter");
            }
        }
    }

}
