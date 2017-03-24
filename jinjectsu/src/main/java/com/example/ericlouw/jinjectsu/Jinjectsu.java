package com.example.ericlouw.jinjectsu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class Jinjectsu {
    private InstanceContainer instanceContainer;
    private TransientContainer transientContainer;
    private SingletonContainer singletonContainer;
    private ScopedContainer scopedContainer;
    private Map<Class, RegistrationType> registrationTypeMap;
    private Map<RegistrationType, ITypeResolver> resolverMap;
    private CyclicDependencyChecker cyclicDependencyChecker;

    public Jinjectsu(){
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

        for (Field field : fields){
            if(!field.isAnnotationPresent(Inject.class)){
                continue;
            }

            field.setAccessible(true);

            try {
                field.set(target, this.resolve(field.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public ITypeBinder bind(Class abstractType){
        return new TypeBinder(abstractType, this);
    }

    public <TInterface> TInterface resolve(Class abstractType) throws Exception {
        if (!this.registrationTypeMap.containsKey(abstractType))
        {
            throw new Exception(String.format("Type %s is not registered.", abstractType.getName()));
        }

        RegistrationType registrationType = this.registrationTypeMap.get(abstractType);

        ITypeResolver resolver = this.resolverMap.get(registrationType);

        return (TInterface)(resolver.resolve(abstractType, this));
    }

    public void beginScope(){
        this.scopedContainer.push();
    }

    public void endScope(){
        this.scopedContainer.pop();
    }

    <TConcrete> void registerInstance(Class abstractType, TConcrete instance) {
        this.registrationTypeMap.put(abstractType, RegistrationType.INSTANCE);
        this.instanceContainer.register(abstractType, instance);
    }

    void registerTransient(Class abstractType, Class concreteType) throws Exception {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.TRANSIENT);
        this.transientContainer.register(abstractType, concreteType);
    }

    void registerSingleton(Class abstractType, Class concreteType) throws Exception {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SINGLETON);
        this.singletonContainer.register(abstractType, concreteType);
    }

    void registerScoped(Class abstractType, Class concreteType) throws Exception {
        this.cyclicDependencyChecker.registerDependency(abstractType, Arrays.asList(this.getConstructorDependenciesForType(concreteType)));
        this.registrationTypeMap.put(abstractType, RegistrationType.SCOPED);
        this.scopedContainer.register(abstractType, concreteType);
    }

    Object ConstructorResolve(Class type) throws Exception {
        Constructor constructor = type.getDeclaredConstructors()[0];

        Class[] parameterTypes = constructor.getParameterTypes();

        if(parameterTypes.length == 0){
            return constructor.newInstance();
        }

        Object[] parameterValues = new Object[parameterTypes.length];

        for(int i = 0; i < parameterTypes.length; i++){
            parameterValues[i] = this.resolve(parameterTypes[i]);
        }

        return constructor.newInstance(parameterValues);
    }

    private Class[] getConstructorDependenciesForType(Class type){
        Constructor constructor = type.getDeclaredConstructors()[0];

        return constructor.getParameterTypes();
    }
}
