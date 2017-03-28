package com.ericlouw.jinjectsu.demo;

public class ExampleService implements IExampleService {

    private IExampleRepository repository;

    public ExampleService(IExampleRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getText() {
        return this.repository.loadText();
    }
}
