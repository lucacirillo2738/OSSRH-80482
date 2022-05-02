package com.lucas.json.exceptions;

public class ParsingException extends RuntimeException{

    public ParsingException(Exception exception){
        super(exception);
    }

    public ParsingException(String message, Exception exception){
        super(message, exception);
    }
}
