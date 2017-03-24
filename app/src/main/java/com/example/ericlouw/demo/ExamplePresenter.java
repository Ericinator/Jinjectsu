package com.example.ericlouw.demo;

public class ExamplePresenter implements IExamplePresenter {
    private IExampleView view;
    private IExampleService service;

    public ExamplePresenter(IExampleService service){
        this.service = service;
    }

    @Override
    public void takeView(IExampleView view) {
        this.view = view;
        this.view.setText(this.service.getText());
    }
}
