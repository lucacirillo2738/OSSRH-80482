package com.lucas.security;

public class SecurityException extends RuntimeException {
    private static final long serialVersionUID = 930611243648914240L;

    public static class ErrorCode {
        private ErrorCode() {
        }

        public static final int GENERIC_ERROR = -1;
        public static final int ENCRYPTION_ERROR = -2;
        public static final int DECRYPTION_ERROR = -3;
        public static final int SIGNATURE_ERROR = -4;
        public static final int KEYSTORE_ERROR = -5;
    }

    private int code;


    public SecurityException(String message, int code) {
        super(message);
        this.code = code;
    }

    public SecurityException(Throwable cause, String message, int code) {
        super(message, cause);
        this.code = code;
    }


    public SecurityException(String message, int code, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }
}