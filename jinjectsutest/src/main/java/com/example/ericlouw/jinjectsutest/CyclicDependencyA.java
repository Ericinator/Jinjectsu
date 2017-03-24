package com.example.ericlouw.jinjectsutest;

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
