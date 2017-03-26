package com.example.ericlouw.jinjectsutest;

import com.example.ericlouw.jinjectsu.Jinjectsu;
import com.example.ericlouw.jinjectsutest.testmodels.ITestInterfaceC;
import com.example.ericlouw.jinjectsutest.testmodels.TestConcreteC;

import org.junit.Assert;
import org.junit.Test;

import exceptions.TypeAlreadyRegisteredException;

public class SingletonFixture {
    @Test
    public void givenJinjectsu_WhenBindingSingleton_ResolvesTSameInstance() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceC.class).lifestyleSingleton(TestConcreteC.class);

        ITestInterfaceC resolved1 = jinjectsu.resolve(ITestInterfaceC.class);
        ITestInterfaceC resolved2 = jinjectsu.resolve(ITestInterfaceC.class);

        Assert.assertNotNull(resolved1);
        Assert.assertNotNull(resolved2);
        Assert.assertEquals(resolved1, resolved2);
    }

    @Test
    public void givenJinjectsu_WhenBindingSingletonFactoryMethod_FactoryIsInvokedAndResolvesTSameInstance() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        boolean[] factoryTracker = {false};

        jinjectsu.bind(ITestInterfaceC.class).lifestyleSingleton(() ->{
            factoryTracker[0] = true;
            return new TestConcreteC();
        });

        ITestInterfaceC resolved1 = jinjectsu.resolve(ITestInterfaceC.class);
        ITestInterfaceC resolved2 = jinjectsu.resolve(ITestInterfaceC.class);

        Assert.assertTrue(factoryTracker[0]);
        Assert.assertNotNull(resolved1);
        Assert.assertNotNull(resolved2);
        Assert.assertEquals(resolved1, resolved2);
    }

    @Test
    public void givenJinjectsu_WhenBindingSingletonFactoryMethodOfExistingRegistration_ThrowsTypeAlreadyRegisteredException() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(TestConcreteC.class).lifestyleSingleton(TestConcreteC.class);

        try {
            jinjectsu.bind(TestConcreteC.class).lifestyleSingleton(() -> new TestConcreteC());
            Assert.assertTrue(false);
        }
        catch (TypeAlreadyRegisteredException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenJinjectsu_WhenBindingSingletonOfExistingRegistration_ThrowsTypeAlreadyRegisteredException() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(TestConcreteC.class).lifestyleSingleton(() -> new TestConcreteC());

        try {
            jinjectsu.bind(TestConcreteC.class).lifestyleSingleton(TestConcreteC.class);
            Assert.assertTrue(false);
        }
        catch (TypeAlreadyRegisteredException e){
            Assert.assertTrue(true);
        }
    }
}
