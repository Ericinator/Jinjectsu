package com.ericlouw.jinjectsu.jinjectsu.exceptions;

public class CyclicDependencyException extends RuntimeException {
    public CyclicDependencyException(String message){
        super(message);
    }
}
