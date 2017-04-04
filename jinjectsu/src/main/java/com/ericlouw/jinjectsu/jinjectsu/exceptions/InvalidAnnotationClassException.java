package com.ericlouw.jinjectsu.jinjectsu.exceptions;

public class InvalidAnnotationClassException extends RuntimeException {
    public InvalidAnnotationClassException(Class attemptedAnnotationClass){
        super(String.format("Type %s was expected to be an annotation.", attemptedAnnotationClass.getName()));
    }
}
