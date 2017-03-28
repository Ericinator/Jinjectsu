package com.ericlouw.jinjectsu.jinjectsu.exceptions;

public class ConstructorResolutionException extends RuntimeException {
    public ConstructorResolutionException(String message, Throwable throwable){
        super(message, throwable);
    }
}
