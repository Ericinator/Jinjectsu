package com.ericlouw.jinjectsu.jinjectsu;

public interface ITypeBinder {
    <TConcrete> Jinjectsu instance(TConcrete concrete);
    Jinjectsu lifestyleTransient(Class concreteType);
    Jinjectsu lifestyleSingleton(Class concreteType);
    Jinjectsu providedByScope();
    <TConcrete> Jinjectsu lifestyleSingleton(IFactoryMethod<TConcrete> factoryMethod);
    Jinjectsu lifeStyleScoped(Class concreteType);
}
