package com.ericlouw.jinjectsu.test.testmodels;

public class TestConcreteB implements ITestInterfaceB {

    private ITestInterfaceC dependencyC;

    public TestConcreteB(ITestInterfaceC dependencyC) {
        this.dependencyC = dependencyC;
    }
    @Override
    public ITestInterfaceC getDependencyC() {
        return this.dependencyC;
    }

}