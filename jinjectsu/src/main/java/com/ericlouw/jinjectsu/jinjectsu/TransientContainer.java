package com.ericlouw.jinjectsu.jinjectsu;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ericlouw.jinjectsu.jinjectsu.exceptions.UnregisteredTypeException;

class TransientContainer implements ITypeResolver
{
    private Map<Class, Class> concreteTypeMap;

    TransientContainer()
    {
        this.concreteTypeMap = new HashMap<>();
    }

    void register(Class abstractType, Class concreteType)
    {
        this.concreteTypeMap.put(abstractType, concreteType);
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (this.concreteTypeMap.containsKey(abstractType))
        {
            return jinjectsu.ConstructorResolve(this.concreteTypeMap.get(abstractType));
        }

        throw new UnregisteredTypeException(String.format("Type {0} was not registered transiently.", abstractType.getName()));
    }

    @Override
    public Class getTypeToResolveFor(Class type) {
        return this.concreteTypeMap.get(type);
    }

    @Override
    public Set<Class> getRegisteredTypes() {
        return this.concreteTypeMap.keySet();
    }

    @Override
    public boolean isTypeRegistered(Class registeredType) {
        return this.concreteTypeMap.containsKey(registeredType);
    }
}
