package com.ericlouw.jinjectsu.jinjectsu.interfaces;

import com.ericlouw.jinjectsu.jinjectsu.Jinjectsu;

public interface ITypeBinder {
    <TConcrete> Jinjectsu instance(TConcrete concrete);
    Jinjectsu lifestyleTransient(Class concreteType);
    Jinjectsu lifestyleSingleton(Class concreteType);
    IScopeContextBinder providedByScope();
    <TConcrete> Jinjectsu lifestyleSingleton(IFactoryMethod<TConcrete> factoryMethod);
    Jinjectsu lifeStyleScoped(Class concreteType);
}
