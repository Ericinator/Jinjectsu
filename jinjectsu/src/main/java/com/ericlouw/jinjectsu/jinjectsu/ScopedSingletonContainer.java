package com.ericlouw.jinjectsu.jinjectsu;

class ScopedSingletonContainer extends SingletonContainer {
    Object context;

    public Object getContext() {
        return context;
    }

    void setContext(Object context){
        this.context = context;
    }
}
