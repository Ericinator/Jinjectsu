package com.example.ericlouw.jinjectsu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ScopeContextResolver implements ITypeResolver{
    private ScopedContainer scopedContainer;

    public ScopeContextResolver(ScopedContainer scopedContainer){
        this.scopedContainer = scopedContainer;
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.scopedContainer.getCurrentScope().getContext();
    }

    @Override
    public Class getTypeToResolveFor(Class type) {
        return null;
    }

    @Override
    public Set<Class> getRegisteredTypes() {
        Set<Class> registeredTypes = new HashSet<>();
        registeredTypes.add(this.scopedContainer.getCurrentScope().getContext().getClass());
        return registeredTypes;
    }

    @Override
    public boolean isTypeRegistered(Class registeredType) {
        return this.scopedContainer.getCurrentScope().getContext().getClass() == registeredType;
    }
}
