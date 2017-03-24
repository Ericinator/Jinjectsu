package com.example.ericlouw.jinjectsu;

import java.util.HashMap;
import java.util.Map;

class InstanceContainer implements ITypeResolver {
    private Map<Class, Object> instanceMap;

    InstanceContainer()
    {
        this.instanceMap = new HashMap<>();
    }

    <TConcrete> void register(Class interfaceType, TConcrete instance)
    {
        this.instanceMap.put(interfaceType, instance);
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu)
    {
        return this.instanceMap.get(abstractType);
    }
}
