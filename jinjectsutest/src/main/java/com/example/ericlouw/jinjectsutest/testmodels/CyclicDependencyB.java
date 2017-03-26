package com.example.ericlouw.jinjectsutest.testmodels;

public class CyclicDependencyB implements ICyclicDependencyB {
    private ICyclicDependencyA cyclicDependencyA;

    public CyclicDependencyB(ICyclicDependencyA cyclicDependencyA) {
        this.cyclicDependencyA = cyclicDependencyA;
    }

    @Override
    public ICyclicDependencyA getDependencyA() {
        return this.cyclicDependencyA;
    }
}
