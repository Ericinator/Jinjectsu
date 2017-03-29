package com.ericlouw.jinjectsu.demo;

public class ExampleService implements IExampleService {

    private IExampleRepository repository;
    private IPreferencesManager preferencesManager;

    public ExampleService(IExampleRepository repository,
                          IPreferencesManager preferencesManager) {
        this.repository = repository;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public String getText() {
        return this.repository.loadText();
    }
}
