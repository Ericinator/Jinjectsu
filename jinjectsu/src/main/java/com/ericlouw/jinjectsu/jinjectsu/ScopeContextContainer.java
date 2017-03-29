package com.ericlouw.jinjectsu.jinjectsu;

import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidScopeContextException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidScopeException;
import com.ericlouw.jinjectsu.jinjectsu.utils.ListUtilWrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ScopeContextContainer implements com.ericlouw.jinjectsu.jinjectsu.interfaces.ITypeResolver {
    private ScopedContainer scopedContainer;
    private Map<Class, List<Class>> concreteContextMap;

    public ScopeContextContainer(ScopedContainer scopedContainer) {
        this.scopedContainer = scopedContainer;
        this.concreteContextMap = new HashMap<>();
    }

    private void register(Class abstractType, Class concreteContext) {
        if (!this.concreteContextMap.containsKey(abstractType)) {
            this.concreteContextMap.put(abstractType, new ArrayList<Class>());
        }

        List<Class> concreteContexts = this.concreteContextMap.get(abstractType);

        concreteContexts.add(concreteContext);
    }

    public void register(Class abstractType, Class... concreteContexts) {
        for(Class c : concreteContexts){
            this.register(abstractType, c);
        }
    }

    @Override
    public Object resolve(Class abstractType, Jinjectsu jinjectsu) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        this.checkScopeSafety(abstractType);

        this.checkContextValidity(abstractType);

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

    private void checkScopeSafety(Class abstractType){
        ScopedSingletonContainer scope = this.scopedContainer.getCurrentScope();

        if(scope == null){
            throw new InvalidScopeContextException(String.format("Dependency of type %s can not be satisfied outside of a scope.", abstractType.getName()));
        }
    }

    private void checkContextValidity(Class abstractType){
        Object context = this.scopedContainer.getCurrentScope().getContext();

        if(context == null){
            throw new InvalidScopeException(String.format("Dependency of type %s could not be satisfied because it depends on the current scope context which is null.", abstractType));
        }

        Class scopeContextType = context.getClass();

        List<Class> requiredScopeContextTypes = this.concreteContextMap.get(abstractType);

        ListUtilWrapper<Class> classList = new ListUtilWrapper<>(requiredScopeContextTypes);

        if(!requiredScopeContextTypes.contains(scopeContextType)){
            throw new InvalidScopeContextException(String.format("Dependency of type %s could not be satisfied by the current scope context %s. Scope context must be one of [%s]",
                    abstractType.getName(),
                    scopeContextType.getName(),
                    classList.toDelimitedString(",")));
        }

    }
}
