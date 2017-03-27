package com.example.ericlouw.jinjectsu;

class ScopedSingletonContainer extends SingletonContainer {
    Object context;

    public Object getContext() {
        return context;
    }

    void setContext(Object context){
        this.context = context;

        Class contextType = context.getClass();

        this.singletonTypeMap.put(contextType, contextType);

        this.singletonLookup.put(contextType, context);
    }
}
