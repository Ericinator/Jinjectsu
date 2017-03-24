package com.example.ericlouw.jinjectsu;

import java.util.HashMap;
import java.util.Map;

class SingletonContainer implements ITypeResolver {
    private Map<Class, Object> singletonLookup;

    private Map<Class, Class> singletonTypeMap;

    SingletonContainer() {
        this.singletonLookup = new HashMap<>();

        this.singletonTypeMap = new HashMap<>();
    }

    void register(Class abstractType, Class concreteType)
    {
        this.singletonTypeMap.put(abstractType, concreteType);
    }

    private Object CreateSingleton(Class abstractClass, Jinjectsu jinjectsu) throws Exception {

        if (this.singletonTypeMap.containsKey(abstractClass)) {
            Class concreteClass = this.singletonTypeMap.get(abstractClass);
            return jinjectsu.ConstructorResolve(concreteClass);
        }

        throw new Exception(String.format("Type %s was not registered as a singleton.", abstractClass.getName()));
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu) throws Exception {
        boolean singletonExists = this.singletonLookup.containsKey(abstractType);

        if (!singletonExists)
        {
            this.singletonLookup.put(abstractType, this.CreateSingleton(abstractType, jinjectsu));
        }

        return this.singletonLookup.get(abstractType);
    }
}
