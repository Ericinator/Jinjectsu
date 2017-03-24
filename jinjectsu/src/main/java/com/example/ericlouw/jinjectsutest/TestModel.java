package com.example.ericlouw.jinjectsutest;

import javax.inject.Inject;

public class TestModel extends TestModelWithNoFields{
    @Inject
    private ITestInterfaceA testObjectA;

    @Inject
    private ITestInterfaceB testObjectB;

    @Inject
    private ITestInterfaceC testObjectC;

    public ITestInterfaceA getTestObjectA() {
        return testObjectA;
    }

    public ITestInterfaceB getTestObjectB() {
        return testObjectB;
    }

    public ITestInterfaceC getTestObjectC() {
        return testObjectC;
    }
}
