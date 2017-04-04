package com.ericlouw.jinjectsu.test;

import com.ericlouw.jinjectsu.jinjectsu.configuration.IJinjectsuConfiguration;
import com.ericlouw.jinjectsu.jinjectsu.Jinjectsu;
import com.ericlouw.jinjectsu.jinjectsu.JinjectsuAnalyzer;
import com.ericlouw.jinjectsu.jinjectsu.configuration.JinjectsuConfiguration;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidAnnotationClassException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidScopeContextException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.InvalidScopeException;
import com.ericlouw.jinjectsu.test.testmodels.CustomAnnotationModel;
import com.ericlouw.jinjectsu.test.testmodels.CustomAnnotationTest;
import com.ericlouw.jinjectsu.test.testmodels.CyclicDependencyA;
import com.ericlouw.jinjectsu.test.testmodels.CyclicDependencyB;
import com.ericlouw.jinjectsu.test.testmodels.DependencyWithConstructorException;
import com.ericlouw.jinjectsu.test.testmodels.DependencyWithPrivateConstructor;
import com.ericlouw.jinjectsu.test.testmodels.ICyclicDependencyA;
import com.ericlouw.jinjectsu.test.testmodels.ICyclicDependencyB;
import com.ericlouw.jinjectsu.test.testmodels.ITestInterfaceA;
import com.ericlouw.jinjectsu.test.testmodels.ITestInterfaceB;
import com.ericlouw.jinjectsu.test.testmodels.ITestInterfaceC;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteA;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteB;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteC;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteC2;
import com.ericlouw.jinjectsu.test.testmodels.TestModel;
import com.ericlouw.jinjectsu.test.testmodels.TestModelWithNoFields;

import org.junit.Assert;
import org.junit.Test;

import com.ericlouw.jinjectsu.jinjectsu.exceptions.ConstructorResolutionException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.CyclicDependencyException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.TypeAlreadyRegisteredException;
import com.ericlouw.jinjectsu.jinjectsu.exceptions.UnregisteredTypeException;

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
    public void givenJinjectsuWithBoundType_WhenBindingSameTypeAgain_ThrowsTypeAlreadyRegisteredException(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(TestConcreteC.class).lifestyleTransient(TestConcreteC.class);

        try{
            jinjectsu.bind(TestConcreteC.class).lifestyleSingleton(TestConcreteC.class);
            Assert.assertTrue(false);
        }
        catch(TypeAlreadyRegisteredException e){
            Assert.assertTrue(true);
            Assert.assertTrue(e.getMessage().contains("TRANSIENT"));
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
    public void givenScopedWithContext_WhenResolvingOutsideScope_ThrowsTypeNotRegisteredException(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(TestConcreteC.class).providedByScope();

        TestConcreteC dependencyA = new TestConcreteC();

        jinjectsu.beginScope(dependencyA);
        jinjectsu.endScope();

        try {
            jinjectsu.resolve(TestConcreteC.class);
            Assert.assertTrue(false);
        }
        catch (UnregisteredTypeException e){
            Assert.assertTrue(true);
        }
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

    @Test
    public void givenJinjectsuWithMissingRegistrations_WhenValidatingRegistration_ReturnsInvalid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class);


        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.validateTypeRegistration();

        Assert.assertFalse(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithCompleteRegistrations_WhenValidatingRegistration_ReturnsValid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).lifestyleSingleton(() -> new TestConcreteC());

        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.validateTypeRegistration();

        Assert.assertTrue(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithScopeContextRegistration_WhenValidatingRegistration_ReturnsValid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleSingleton(TestConcreteB.class)
                .bind(ITestInterfaceC.class).providedByScope().satisfiedBy(TestConcreteC.class);

        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.validateTypeRegistration();

        Assert.assertTrue(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithMissingRegistrations_WhenDryRunning_ReturnsInvalid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class);

        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.dryRun();

        Assert.assertFalse(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithExceptionThrowingDependencies_WhenDryRunning_ReturnsInvalid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(DependencyWithConstructorException.class).lifestyleTransient(DependencyWithConstructorException.class);

        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.dryRun();

        Assert.assertFalse(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithCompleteRegistrations_WhenDryRunning_ReturnsValid() {
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu
                .bind(ITestInterfaceA.class).lifestyleTransient(TestConcreteA.class)
                .bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).lifestyleTransient(TestConcreteC.class);

        JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(jinjectsu);

        boolean validRegistrations = analyzer.validateTypeRegistration();

        Assert.assertTrue(validRegistrations);
    }

    @Test
    public void givenJinjectsuWithScopeContextDependencies_WhenResolveInsideScopeWithoutContext_ThrowsException(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).providedByScope().satisfiedBy(TestConcreteC.class);

        try{
            jinjectsu.beginScope();
            jinjectsu.resolve(ITestInterfaceC.class);
            jinjectsu.endScope();
        }
        catch(InvalidScopeException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenJinjectsuWithScopeContextDependencies_WhenResolveInsideWrongContext_ThrowsException(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).providedByScope().satisfiedBy(TestConcreteC.class);

        try{
            jinjectsu.beginScope(new Object());
                jinjectsu.resolve(ITestInterfaceC.class);
            jinjectsu.endScope();
        }
        catch(InvalidScopeContextException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void givenJinjectsuWithScopeContextDependenciesSatisfiedByOne_WhenResolveInsideContext_ResolvesCorrectly(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).providedByScope().satisfiedBy(TestConcreteC.class);

        jinjectsu.beginScope(new TestConcreteC());
            ITestInterfaceC resolved = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();

        Assert.assertNotNull(resolved);
    }

    @Test
    public void givenJinjectsuWithScopeContextDependenciesSatisfiedByMany_WhenResolveInsideContext_ResolvesCorrectly(){
        Jinjectsu jinjectsu = new Jinjectsu();

        jinjectsu.bind(ITestInterfaceB.class).lifestyleTransient(TestConcreteB.class)
                .bind(ITestInterfaceC.class).providedByScope().satisfiedBy(TestConcreteC2.class, TestConcreteC.class);

        jinjectsu.beginScope(new TestConcreteC());
        ITestInterfaceC resolved = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();

        jinjectsu.beginScope(new TestConcreteC2());
        ITestInterfaceC resolved2 = jinjectsu.resolve(ITestInterfaceC.class);
        jinjectsu.endScope();

        Assert.assertNotNull(resolved);
        Assert.assertNotNull(resolved2);

        Assert.assertEquals(resolved.getClass(), TestConcreteC.class);
        Assert.assertEquals(resolved2.getClass(), TestConcreteC2.class);
    }

    @Test
    public void givenJinjectsu_WhenInjectingIntoCustomAnnotation_InjectsCorrectly(){
        Jinjectsu jinjectsu = new Jinjectsu(new JinjectsuConfiguration().withCustomAnnotation(CustomAnnotationTest.class));

        jinjectsu.bind(ITestInterfaceC.class).lifestyleTransient(TestConcreteC.class);

        CustomAnnotationModel testModel = new CustomAnnotationModel();

        jinjectsu.inject(testModel);

        Assert.assertNotNull(testModel.dependency1);
        Assert.assertNull(testModel.dependency2);
    }

    @Test
    public void givenJinjectsuConfiguration_WhenSettingInvalidCustomAnnotationType_ThrowsInvalidAnnotationException(){
        JinjectsuConfiguration config = new JinjectsuConfiguration();

        try{
            config.withCustomAnnotation(TestConcreteC.class);
            Assert.assertTrue(false);
        }
        catch(InvalidAnnotationClassException e){
            Assert.assertTrue(true);
        }
    }
}