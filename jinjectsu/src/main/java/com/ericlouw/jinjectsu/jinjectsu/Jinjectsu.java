package com.ericlouw.jinjectsu.jinjectsu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericlouw.jinjectsu.jinjectsu.exceptions.ConstructorResolutionException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.InjectionException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.TypeAlreadyRegisteredException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.UnregisteredTypeException;
import com.ericlouw.jinjectsu.jinjectsu.interfaces.IFactoryMethod;

public class Jinjectsu {
    InstanceContainer instanceContainer;
    TransientContainer transientContainer;
    SingletonContainer singletonContainer;
    ScopedContainer scopedContainer;
    ScopeContextContainer scopeContextContainer;
    Map<Class, RegistrationType> registrationTypeMap;
    private Map<RegistrationType, com.ericlouw.jinjectsu.jinjectsu.interfaces.ITypeResolver> resolverMap;
    private CyclicDependencyChecker cyclicDependencyChecker;

    public Jinjectsu() {
        this.instanceContainer = new InstanceContainer();
        this.transientContainer = new TransientContainer();
        this.singletonContainer = new SingletonContainer();
        this.scopedContainer = new ScopedContainer();
        this.scopeContextContainer = new ScopeContextContainer(this.scopedContainer);
        this.registrationTypeMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.resolverMap.put(RegistrationType.INSTANCE, this.instanceContainer);
        this.resolverMap.put(RegistrationType.TRANSIENT, this.transientContainer);
        this.resolverMap.put(RegistrationType.SINGLETON, this.singletonContainer);
        this.resolverMap.put(RegistrationType.SCOPED, this.scopedContainer);
        this.resolverMap.put(RegistrationType.SCOPE_CONTEXT, this.scopeContextContainer);
        this.cyclicDependencyChecker = new CyclicDependencyChecker();
    }

    public void inject(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }

            field.setAccessible(true);

            try {
                field.set(target, this.resolve(field.getType()));
            } catch (IllegalAccessException e) {
                throw new InjectionException(String.format("Could not inject property %s of type %s because it is inaccessible.", field.getName(), target.getClass().getName()), e);
            }
        }

    }

    public com.ericlouw.jinjectsu.jinjectsu.interfaces.ITypeBinder bind(Class abstractType) {
        if(this.registrationTypeMap.containsKey(abstractType)){
            throw new TypeAlreadyRegisteredException(String.format("Type %s has already been registered under lifestyle %s." ,abstractType.getName(), this.registrationTypeMap.get(abstractType).toString()));
        }
        return new TypeBinder(abstractType, this);
    }

    public <TInterface> TInterface resolve(Class abstractType) {
        if (!this.registrationTypeMap.containsKey(abstractType)) {
            throw new UnregisteredTypeException(String.format("Type %s was not registered.", abstractType.getName()));
        }

        RegistrationType registrationType = this.registrationTypeMap.get(abstractType);

        com.ericlouw.jinjectsu.jinjectsu.interfaces.ITypeResolver resolver = this.resolverMap.get(registrationType);

        try {
            return (TInterface) (resolver.resolve(abstractType, this));
        } catch (InstantiationException e) {
            throw new ConstructorResolutionException(String.format("An error occurred while resolving type {%s}.", abstractType.getName()), e);
        } catch (IllegalAccessException e) {
            throw new ConstructorResolutionException(String.format("Could not constructor inject type {%s} because its constructor is inaccessible.", abstractType.getName()), e);
        } catch (InvocationTargetException e) {
            throw new ConstructorResolutionException(String.format("An error occurred while resolving type {%s}.", abstractType.getName()), e);
        }
    }

    public void beginScope() {
        this.scopedContainer.push(new ScopedSingletonContainer());
    }

    public void endScope() {
        ScopedSingletonContainer container = this.scopedContainer.pop();

        Object context = container.getContext();

        if(context != null){
            this.registrationTypeMap.remove(context.getClass());
        }
    }

    public void beginScope(Object context){
        ScopedSingletonContainer container = new ScopedSingletonContainer();

        container.setContext(context);

        this.scopedContainer.push(container);
    }

    Class getTypeRegisteredUnder(Class registeredType) {

        if (this.singletonContainer.isTypeRegistered(registeredType)) {
            return this.singletonContainer.getTypeToResolveFor(registeredType);
        }

        if (this.transientContainer.isTypeRegistered(registeredType)) {
            return this.transientContainer.getTypeToResolveFor(registeredType);
        }

        if (this.scopedContainer.isTypeRegistered(registeredType)) {
            return this.scopedContainer.getTypeToResolveFor(registeredType);
        }

        if (this.instanceContainer.isTypeRegistered(registeredType)) {
            return this.instanceContainer.getTypeToResolveFor(registeredType);
        }

        throw new UnregisteredTypeException(String.format("Type %s was not regstered.", registeredType.getName()));
    }

    <TConcrete> void registerInstance(Class abstractType, TConcrete instance) {
        this.registrationTypeMap.put(abstractType, RegistrationType.INSTANCE);
        this.instanceContainer.register(abstractType, instance);
    }

    void registerTransient(Class abstractType, Class concreteType) {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.TRANSIENT);
        this.transientContainer.register(abstractType, concreteType);
    }

    void registerSingleton(Class abstractType, Class concreteType) {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SINGLETON);
        this.singletonContainer.register(abstractType, concreteType);
    }

    void registerSingleton(Class abstractType, IFactoryMethod factoryMethod) {
        Class concreteType = factoryMethod.getClass().getMethods()[0].getReturnType();
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SINGLETON);
        this.singletonContainer.register(abstractType, factoryMethod);
    }

    void registerScoped(Class abstractType, Class concreteType) {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SCOPED);
        this.scopedContainer.register(abstractType, concreteType);
    }

    void registerScopeContext(Class abstractType, Class... concreteContexts) {
        this.registrationTypeMap.put(abstractType, RegistrationType.SCOPE_CONTEXT);
        this.scopeContextContainer.register(abstractType, concreteContexts);
    }

    Object ConstructorResolve(Class type) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = type.getDeclaredConstructors()[0];

        Class[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return constructor.newInstance();
        }

        Object[] parameterValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterValues[i] = this.resolve(parameterTypes[i]);
        }

        return constructor.newInstance(parameterValues);
    }

    Class[] getConstructorDependenciesForType(Class type) {
        Constructor constructor = type.getDeclaredConstructors()[0];

        return constructor.getParameterTypes();
    }

}
