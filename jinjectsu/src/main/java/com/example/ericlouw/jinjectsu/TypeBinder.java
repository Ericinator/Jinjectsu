package com.example.ericlouw.jinjectsu;

class TypeBinder implements ITypeBinder{
    private Jinjectsu jinjectsu;
    private Class abstractType;

    TypeBinder(Class abstractType, Jinjectsu jinjectsu){
        this.jinjectsu = jinjectsu;
        this.abstractType = abstractType;
    }

    @Override
    public <TConcrete> Jinjectsu instance(TConcrete concrete){
        this.jinjectsu.registerInstance(this.abstractType, concrete);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu lifestyleSingleton(Class concreteType) {
        this.jinjectsu.registerSingleton(this.abstractType, concreteType);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu providedByScope() {
        this.jinjectsu.registerScopeContext(this.abstractType);
        return this.jinjectsu;
    }

    @Override
    public <TConcrete> Jinjectsu lifestyleSingleton(IFactoryMethod<TConcrete> factoryMethod) {
        this.jinjectsu.registerSingleton(this.abstractType, factoryMethod);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu lifeStyleScoped(Class concreteType) {
        this.jinjectsu.registerScoped(this.abstractType, concreteType);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu lifestyleTransient(Class concreteType) {
        this.jinjectsu.registerTransient(this.abstractType, concreteType);
        return this.jinjectsu;
    }
}