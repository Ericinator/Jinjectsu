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
    public Jinjectsu lifestyleSingleton(Class concreteType) throws Exception {
        this.jinjectsu.registerSingleton(this.abstractType, concreteType);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu lifeStyleScoped(Class concreteType) throws Exception {
        this.jinjectsu.registerScoped(this.abstractType, concreteType);
        return this.jinjectsu;
    }

    @Override
    public Jinjectsu lifestyleTransient(Class concreteType) throws Exception {
        this.jinjectsu.registerTransient(this.abstractType, concreteType);
        return this.jinjectsu;
    }
}