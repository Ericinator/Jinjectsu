package com.example.ericlouw.jinjectsu;

import java.lang.reflect.InvocationTargetException;

interface ITypeResolver {
    Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InvocationTargetException, InstantiationException;
}
