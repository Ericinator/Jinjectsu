package com.example.ericlouw.jinjectsutest;

import com.example.ericlouw.jinjectsu.Jinjectsu;

import org.junit.Assert;
import org.junit.Test;

import exceptions.ConstructorResolutionException;
import exceptions.CyclicDependencyException;


public class JinjectsuFixture {

    @Test
    public void givenJinjectsu_WhenResolvingUnboundType_ThrowsException() {
        Jinjectsu jinjectsu = new Jinjectsu();
        try {
            jinjectsu.resolve(ITestInterfaceA.class);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
            e.printStackTrace();
        }
    }

    @Test
    public void givenJinjectsu_WhenBindingInstance_ResolvesCorrectObject() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        TestConcreteA concrete = new TestConcreteA(new TestConcreteB(new TestConcreteC()));

        jinjectsu
                .bind(ITestInterfaceA.class)
                .instance(concrete);

        TestConcreteA resolvedConcrete = jinjectsu.resolve(ITestInterfaceA.class);

        Assert.assertEquals(concrete, resolvedConcrete);
    }

    @Test
    public void givenJinjectsu_WhenBindingTransient_ResolvesTreeCorrectly() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).lifestyleTransient(TestConcreteC.class);

        ITestInterfaceA resolved = jinjectsu.resolve(ITestInterfaceA.class);

        Assert.assertNotNull(resolved);
        Assert.assertNotNull(resolved.getDependencyB());
        Assert.assertNotNull(resolved.getDependencyB().getDependencyC());
    }

    @Test
    public void givenJinjectsu_WhenBindingTransientAndInstance_ResolvesTreeCorrectly() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        ITestInterfaceC testC = new TestConcreteC();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).instance(testC);

        ITestInterfaceA resolved = jinjectsu.resolve(ITestInterfaceA.class);

        Assert.assertNotNull(resolved);
        Assert.assertNotNull(resolved.getDependencyB());
        Assert.assertNotNull(resolved.getDependencyB().getDependencyC());
        Assert.assertEquals(testC, resolved.getDependencyB().getDependencyC());
    }

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
    public void givenJinjectsu_WhenBindingScoped_ResolvesTSameInstanceWithinScope() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceC.class).lifeStyleScoped(TestConcreteC.class);

        jinjectsu.beginScope();
        ITestInterfaceC resolved1 = jinjectsu.resolve(ITestInterfaceC.class);
        ITestInterfaceC resolved2 = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();

        jinjectsu.beginScope();
        ITestInterfaceC resolved3 = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();

        Assert.assertNotNull(resolved1);
        Assert.assertNotNull(resolved2);
        Assert.assertNotNull(resolved3);

        Assert.assertEquals(resolved1, resolved2);
        Assert.assertNotEquals(resolved3, resolved1);
        Assert.assertNotEquals(resolved3, resolved2);
    }

    @Test
    public void givenJinjectsu_WhenResolvingNestedScopes_ResolvesSameInstanceWithinScope() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceC.class).lifeStyleScoped(TestConcreteC.class);

        jinjectsu.beginScope();
        ITestInterfaceC resolved1 = jinjectsu.resolve(ITestInterfaceC.class);
        ITestInterfaceC resolved2 = jinjectsu.resolve(ITestInterfaceC.class);

        jinjectsu.beginScope();
        ITestInterfaceC resolved3 = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();
        jinjectsu.endScope();

        Assert.assertNotNull(resolved1);
        Assert.assertNotNull(resolved2);
        Assert.assertNotNull(resolved3);

        Assert.assertEquals(resolved1, resolved2);
        Assert.assertNotEquals(resolved3, resolved1);
        Assert.assertNotEquals(resolved3, resolved2);
    }

    @Test
    public void givenObjectWithInjectableFields_WhenInjecting_ResolvedCorrectly() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleSingleton(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleSingleton(TestConcreteB.class)
                .bind(ITestInterfaceC.class).lifestyleSingleton(TestConcreteC.class);

        TestModel model = new TestModel();

        jinjectsu.inject(model);

        Assert.assertNotNull(model.getTestObjectA());
        Assert.assertNotNull(model.getTestObjectB());
        Assert.assertNotNull(model.getTestObjectC());

        Assert.assertEquals(model.getTestObjectA().getDependencyB(), model.getTestObjectB());
        Assert.assertEquals(model.getTestObjectB().getDependencyC(), model.getTestObjectC());
    }

    @Test
    public void givenObjectWithInjectableFields_WhenInjectingIntoBaseType_ResolvedCorrectly() throws Exception {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleSingleton(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleSingleton(TestConcreteB.class)
                .bind(ITestInterfaceC.class).lifestyleSingleton(TestConcreteC.class);

        TestModelWithNoFields model = new TestModel();

        jinjectsu.inject(model);

        TestModel castedModel = (TestModel) model;

        Assert.assertNotNull(castedModel.getTestObjectA());
        Assert.assertNotNull(castedModel.getTestObjectB());
        Assert.assertNotNull(castedModel.getTestObjectC());

        Assert.assertEquals(castedModel.getTestObjectA().getDependencyB(), castedModel.getTestObjectB());
        Assert.assertEquals(castedModel.getTestObjectB().getDependencyC(), castedModel.getTestObjectC());
    }

    @Test
    public void givenClassesWithCyclicDependency_WhenRegisterTransiently_ThrowsException() {
        Jinjectsu jinjectsu = new Jinjectsu();

        try {
            jinjectsu
                    .bind(ICyclicDependencyA.class).lifestyleTransient(CyclicDependencyA.class)
                    .bind(ICyclicDependencyB.class).lifestyleTransient(CyclicDependencyB.class);

            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenClassesWithCyclicDependency_WhenRegisterSingleton_ThrowsException() {
        Jinjectsu jinjectsu = new Jinjectsu();

        try {
            jinjectsu
                    .bind(ICyclicDependencyA.class).lifestyleSingleton(CyclicDependencyA.class)
                    .bind(ICyclicDependencyB.class).lifestyleSingleton(CyclicDependencyB.class);

            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenClassesWithCyclicDependency_WhenRegisterScoped_ThrowsCyclicDependencyException() {
        Jinjectsu jinjectsu = new Jinjectsu();

        try {
            jinjectsu.beginScope();
            jinjectsu
                    .bind(ICyclicDependencyA.class).lifestyleSingleton(CyclicDependencyA.class)
                    .bind(ICyclicDependencyB.class).lifestyleSingleton(CyclicDependencyB.class);

            Assert.assertTrue(false);
        } catch (CyclicDependencyException e) {
            Assert.assertTrue(true);
        } catch (Exception exception) {
            Assert.assertTrue(false);
        }
        jinjectsu.endScope();
    }

    @Test
    public void givenDependencyWithInaccessibleConstructor_WhenResolving_ThrowsConstructorResolutionException() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(DependencyWithPrivateConstructor.class).lifestyleSingleton(DependencyWithPrivateConstructor.class);

        try {
            DependencyWithPrivateConstructor resolvedDependency = jinjectsu.resolve(DependencyWithPrivateConstructor.class);
            Assert.assertTrue(false);
        }
        catch(ConstructorResolutionException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenDependencyWithErrorInConstructor_WhenResolving_ThrowsConstructorResolutionException() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(DependencyWithConstructorException.class).lifestyleSingleton(DependencyWithConstructorException.class);

        try {
            DependencyWithPrivateConstructor resolvedDependency = jinjectsu.resolve(DependencyWithConstructorException.class);
            Assert.assertTrue(false);
        }
        catch(ConstructorResolutionException e){
            Assert.assertTrue(true);
        }
    }
}