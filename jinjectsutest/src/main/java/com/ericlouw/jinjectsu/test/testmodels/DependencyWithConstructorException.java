package com.ericlouw.jinjectsu.test.testmodels;

public class DependencyWithConstructorException {
    public DependencyWithConstructorException(){
        throw new RuntimeException();
    }
}
