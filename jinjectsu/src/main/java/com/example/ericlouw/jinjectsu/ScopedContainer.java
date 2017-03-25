package com.example.ericlouw.jinjectsu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ScopedContainer implements  ITypeResolver {

    private Map<Class, Class> typeMap;

    private Stack<SingletonContainer> scopeContainerStack;

    ScopedContainer(){
        this.scopeContainerStack = new Stack<>();
        this.typeMap = new HashMap<>();
    }

    void push(){
        SingletonContainer singletonContainer = new SingletonContainer();

        for(Map.Entry<Class, Class> entry : this.typeMap.entrySet()){
            singletonContainer.register(entry.getKey(), entry.getValue());
        }

        this.scopeContainerStack.push(singletonContainer);
    }

    SingletonContainer pop(){
        return this.scopeContainerStack.pop();
    }

    private SingletonContainer getCurrentScope(){
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
    public boolean isTypeRegistered(Class type) {
        return this.typeMap.containsKey(type);
    }

    @Override
    public Set<Class> getRegisteredTypes() {
        return this.typeMap.keySet();
    }
}
