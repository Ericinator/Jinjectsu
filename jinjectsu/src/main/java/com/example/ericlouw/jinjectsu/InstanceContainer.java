package com.example.ericlouw.jinjectsu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public Class getTypeToResolveFor(Class type) {
        return this.instanceMap.get(type).getClass();
    }

    @Override
    public Set<Class> getRegisteredTypes() {
        return this.instanceMap.keySet();
    }

    @Override
    public boolean isTypeRegistered(Class registeredType) {
        return this.instanceMap.containsKey(registeredType);
    }
}
