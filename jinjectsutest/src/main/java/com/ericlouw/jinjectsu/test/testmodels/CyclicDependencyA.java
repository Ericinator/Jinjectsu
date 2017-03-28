package com.ericlouw.jinjectsu.test.testmodels;

public class CyclicDependencyA implements ICyclicDependencyA {
    private ICyclicDependencyB cyclicDependencyB;

    public CyclicDependencyA(ICyclicDependencyB cyclicDependencyB) {
        this.cyclicDependencyB = cyclicDependencyB;
    }

    @Override
    public ICyclicDependencyB getDependencyB() {
        return this.cyclicDependencyB;
    }
}
