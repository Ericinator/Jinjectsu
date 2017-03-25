package com.example.ericlouw.jinjectsu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import exceptions.ConstructorResolutionException;
import exceptions.InjectionException;
import exceptions.UnregisteredTypeException;

public class Jinjectsu {
    private InstanceContainer instanceContainer;
    private TransientContainer transientContainer;
    private SingletonContainer singletonContainer;
    private ScopedContainer scopedContainer;
    private Map<Class, RegistrationType> registrationTypeMap;
    private Map<RegistrationType, ITypeResolver> resolverMap;
    private CyclicDependencyChecker cyclicDependencyChecker;

    public Jinjectsu() {
        this.instanceContainer = new InstanceContainer();
        this.transientContainer = new TransientContainer();
        this.singletonContainer = new SingletonContainer();
        this.scopedContainer = new ScopedContainer();
        this.registrationTypeMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
        this.resolverMap.put(RegistrationType.INSTANCE, this.instanceContainer);
        this.resolverMap.put(RegistrationType.TRANSIENT, this.transientContainer);
        this.resolverMap.put(RegistrationType.SINGLETON, this.singletonContainer);
        this.resolverMap.put(RegistrationType.SCOPED, this.scopedContainer);
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

    public ITypeBinder bind(Class abstractType) {
        return new TypeBinder(abstractType, this);
    }

    public <TInterface> TInterface resolve(Class abstractType) {
        if (!this.registrationTypeMap.containsKey(abstractType)) {
            throw new UnregisteredTypeException(String.format("Type %s was not registered.", abstractType.getName()));
        }

        RegistrationType registrationType = this.registrationTypeMap.get(abstractType);

        ITypeResolver resolver = this.resolverMap.get(registrationType);

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
        this.scopedContainer.push();
    }

    public void endScope() {
        this.scopedContainer.pop();
    }

    public boolean validateTypeRegistration() {
        Set<Class> allRegisteredTypes = new HashSet<>();

        allRegisteredTypes.addAll(this.singletonContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.transientContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.instanceContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.scopedContainer.getRegisteredTypes());

        for (Class type : allRegisteredTypes) {
            Class concreteType = this.getTypeRegisteredUnder(type);

            Class[] dependecies = this.getConstructorDependenciesForType(concreteType);

            for (Class dependency : dependecies) {
                try {
                    if (!allRegisteredTypes.contains(dependency)) {
                        throw new UnregisteredTypeException(String.format("Type %s was not regstered.", dependency.getName()));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    private Class getTypeRegisteredUnder(Class registeredType) {

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

    void registerScoped(Class abstractType, Class concreteType) {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SCOPED);
        this.scopedContainer.register(abstractType, concreteType);
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

    private Class[] getConstructorDependenciesForType(Class type) {
        Constructor constructor = type.getDeclaredConstructors()[0];

        return constructor.getParameterTypes();
    }

}
