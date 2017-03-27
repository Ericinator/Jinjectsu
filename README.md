# Jinjectsu
Jinjectsu is a lightweight IoC container designed with Android in mind.
Jinjectsu is primarily meant for constructor injection but supports property injection to provide an injection entry point for Android projects.

## Example setup
Jinjectsu setup will typically look as follows:

```Java
new Jinjectsu()
    .bind(IInterfaceA.class).lifestyleSingleton(ImplementationA.class)
    .bind(IInterfaceB.class).lifestyleScoped(ImplementationB.class)
    .bind(IInterfaceD.class).lifestyleTransient(ImplementationD.class)
    .bind(IInterfaceC.class).instance(new ImplementationC());
```

## Example usage

Android projects will typically consume Jinsectsu like so:
```Java
@Inject
private IDependencyA depA;

protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    jinjectsuInstance.inject(this);
}
```
The initial injection happens through property injection. From there on out the dependency tree is resolved through constructor injection.

The repo contains an example app to show usage in more detail.

## Multiple lifecycle support
Jinjectsu supports *transient*, *singleton*, *instance* and *scoped* type registrations.

### Transient Binding
Each resolve will provide a new instance of the implementation registered under the interface type.

### Singleton Binding
Each resolve will provide the same instance of the dependency.

Jinjectsu also supports singleton factory method registration if you wish to execute some code when your singleton is first created:
```Java
jinjectsu.bind(ITestA.class).lifestyleSingleton(() -> new TestA());
```

### Instance Binding
The consuming code can new up an appropriate instace of a dependency which  will be provided to all dependent classes.

### Scoped Binding
Consuming code can define custom resolve scopes. Dependencies resolved within a scope will act as a singleton for the lifetime of the scope.
Scopes are primarily intended for Android activities and unit testing.
Jinjectsu also supports nested scopes. For example:

```Java
container.beginScope();
    IDependencyA depA1 = container.resolve(IDependencyA.class);
    IDependencyA depA2 = container.resolve(IDependencyA.class);
    // depA1 == depA2
    container.beginScope();
        IDependencyA depA3 = container.resolve(IDependencyA.class);
        // depA3 != depA1
    container.endScope();
container.endScope();
```
*Please note that the code snippet above is a terrible example of how to use this container. Calling the resolve method manually most likely indicates incorrect usage.*

## Unit test setup
Jinjectsu provides the *validateTypeRegistration* helper to help you ensure that your container is not missing any dependency registrations.

```Java
@Test
public void givenJinjectsuContainer_WhenValidatingRegistration_ReturnsValid() {
    Jinjectsu yourJinjectsuContainer = yourJinectsuSetupMethod();

    JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(yourJinjectsuContainer);

    boolean validRegistration = analyzer.validateTypeRegistration();

    Assert.assertTrue(validRegistration);
}
```

Additionally the *dryRun* method can be used to initiate a full dependency tree resolution in a unit test. This can be useful to identify erronous constructor logic.
```Java
@Test
public void givenJinjectsuContainer_WhenValidatingRegistration_ReturnsValid() {
    Jinjectsu yourJinjectsuContainer = yourJinectsuSetupMethod();

    JinjectsuAnalyzer analyzer = new JinjectsuAnalyzer(yourJinjectsuContainer);

    boolean validRegistration = analyzer.dryRun();

    Assert.assertTrue(validRegistration);
}
```
*Take care not to use dryRun() in actual production code as singletons will be created and every constructor in your dependency tree will be invoked.*

## Upcoming
The next feature to be completed is the concept of scope contexts. Althoud this has been partially (and generically) implemented, the main idea is to support Android context injection. Consider the following:

```Java
@Inject
private MyDependency dependency;

protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    jinjectsu.beginScope(this);
    jinjectsu.inject(this);
}
```

Given the activity context is now bound to the scope, any dependency on an Android *Context* can now be constructor injected. Your presenter (or other dependency) could for example be defined as:

```Java
public MyDependency(IService someService, Context context){
    ...
}
```
