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


