package it.sisal.digital.phoenix.utils.security.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.util.Arrays;

public class CipherUtils {

    public static byte[] doBlock(Cipher cipher, byte[] contentBytes, int blockSize) throws BadPaddingException, IllegalBlockSizeException {
        byte[] cipherContent = new byte[0];

        int i = 0;
        while(i < contentBytes.length){
            byte[] b;
            if(i + blockSize < contentBytes.length){
                b = cipher.doFinal(Arrays.copyOfRange(contentBytes, i, i + blockSize));
            }else{
                b = cipher.doFinal(Arrays.copyOfRange(contentBytes, i, contentBytes.length));
            }
            cipherContent = append(cipherContent, b);
            i+=blockSize;
        }
        return cipherContent;
    }

    private static byte[] append(byte[] prefix, byte[] suffix){
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i=0; i< prefix.length; i++){
            toReturn[i] = prefix[i];
        }
        for (int i=0; i< suffix.length; i++){
            toReturn[i+prefix.length] = suffix[i];
        }
        return toReturn;
    }
}
