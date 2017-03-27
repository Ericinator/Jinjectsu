package com.example.ericlouw.jinjectsu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ScopedContainer implements  ITypeResolver {

    private Map<Class, Class> typeMap;

    private Stack<ScopedSingletonContainer> scopeContainerStack;

    ScopedContainer(){
        this.scopeContainerStack = new Stack<>();
        this.typeMap = new HashMap<>();
    }

    void push(ScopedSingletonContainer singletonContainer){

        for(Map.Entry<Class, Class> entry : this.typeMap.entrySet()){
            singletonContainer.register(entry.getKey(), entry.getValue());
        }

        this.scopeContainerStack.push(singletonContainer);
    }

    ScopedSingletonContainer pop(){
        return this.scopeContainerStack.pop();
    }

    ScopedSingletonContainer getCurrentScope(){
        return this.scopeContainerStack.peek();
    }

    void register(Class abstractType, Class concreteType)
    {
        this.typeMap.put(abstractType, concreteType);
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return this.getCurrentScope().resolve(abstractType, jinjectsu);
    }

    @Override
    public Class getTypeToResolveFor(Class type) {
        return this.typeMap.get(type);
    }

    @Override
    public Set<Class> getRegisteredTypes() {
        return this.typeMap.keySet();
    }

    @Override
    public boolean isTypeRegistered(Class registeredType) {
        return this.typeMap.containsKey(registeredType);
    }
}
