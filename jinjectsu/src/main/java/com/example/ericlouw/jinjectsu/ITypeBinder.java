package com.example.ericlouw.jinjectsu;

public interface ITypeBinder {
    <TConcrete> Jinjectsu instance(TConcrete concrete);
    Jinjectsu lifestyleTransient(Class concreteType) throws Exception;
    Jinjectsu lifestyleSingleton(Class concreteType) throws Exception;
    Jinjectsu lifeStyleScoped(Class concreteType) throws Exception;
}
