package com.example.ericlouw.jinjectsu;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

interface ITypeResolver {
    Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    Class getTypeToResolveFor(Class type);

    Set<Class> getRegisteredTypes();

    boolean isTypeRegistered(Class registeredType);
}
