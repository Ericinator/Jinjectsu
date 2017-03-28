package com.ericlouw.jinjectsu.test.testmodels;

public interface ICyclicDependencyA {
    ICyclicDependencyB getDependencyB();
}
