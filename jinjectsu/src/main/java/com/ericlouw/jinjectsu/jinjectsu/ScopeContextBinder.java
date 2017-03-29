package com.ericlouw.jinjectsu.jinjectsu;

import com.ericlouw.jinjectsu.jinjectsu.interfaces.IScopeContextBinder;

import java.util.List;


class ScopeContextBinder implements IScopeContextBinder {
    private Class abstractType;
    private Jinjectsu jinjectsu;

    public ScopeContextBinder(Jinjectsu jinjectsu, Class abstractType){
        this.jinjectsu = jinjectsu;
        this.abstractType = abstractType;
    }

    @Override
    public Jinjectsu satisfiedBy(Class... concreteScopeContexts) {
        this.jinjectsu.registerScopeContext(this.abstractType, concreteScopeContexts);
        return this.jinjectsu;
    }
}
