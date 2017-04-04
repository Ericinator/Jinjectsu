package com.ericlouw.jinjectsu.test.testmodels;

import javax.inject.Inject;

public class CustomAnnotationModel {
    @CustomAnnotationTest
    public ITestInterfaceC dependency1;

    @Inject
    public ITestInterfaceC dependency2;
}
