package com.ericlouw.jinjectsu.jinjectsu.exceptions;

public class UnregisteredTypeException extends RuntimeException {
    public UnregisteredTypeException(String message){
        super(message);
    }
}
