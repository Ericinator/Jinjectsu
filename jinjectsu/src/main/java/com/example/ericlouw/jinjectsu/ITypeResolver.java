package com.example.ericlouw.jinjectsu;

interface ITypeResolver {
    Object resolve(Class abstractType, Jinjectsu jinjectsu) throws Exception;
}
