package com.ericlouw.jinjectsu.test.testmodels;

public class TestConcreteA implements ITestInterfaceA {

    private ITestInterfaceB dependencyB;

    public TestConcreteA(ITestInterfaceB dependencyB) {
        this.dependencyB = dependencyB;
    }

    @Override
    public ITestInterfaceB getDependencyB() {
        return this.dependencyB;
    }
}