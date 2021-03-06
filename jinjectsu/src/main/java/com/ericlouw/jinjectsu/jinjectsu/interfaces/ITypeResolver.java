package com.ericlouw.jinjectsu.jinjectsu.interfaces;

import com.ericlouw.jinjectsu.jinjectsu.Jinjectsu;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public interface ITypeResolver {
    Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    Class getTypeToResolveFor(Class type);

    Set<Class> getRegisteredTypes();

    boolean isTypeRegistered(Class registeredType);
}
