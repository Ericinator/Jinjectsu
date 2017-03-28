package com.ericlouw.jinjectsu.demo;

public class ExampleRepository implements IExampleRepository {
    @Override
    public String loadText() {
        return "Example";
    }
}
