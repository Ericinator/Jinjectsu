package com.example.ericlouw.demo;

public class ExampleRepository implements IExampleRepository {
    @Override
    public String loadText() {
        return "Example";
    }
}
