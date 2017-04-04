package com.ericlouw.jinjectsu.jinjectsu.configuration;

import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidAnnotationClassException;

public class JinjectsuConfiguration implements IJinjectsuConfiguration {
    private Class injectionAnnotation;

    public JinjectsuConfiguration withCustomAnnotation(Class customAnnotation){
        if(!customAnnotation.isAnnotation()){
            throw new InvalidAnnotationClassException(customAnnotation);
        }

        this.injectionAnnotation = customAnnotation;

        return this;
    }

    @Override
    public Class getInjectionAnnotation() {
        return injectionAnnotation;
    }
}
