package com.example.ericlouw.jinjectsutest;

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