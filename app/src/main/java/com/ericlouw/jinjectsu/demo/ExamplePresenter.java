package com.ericlouw.jinjectsu.demo;

import android.content.Context;

public class ExamplePresenter implements IExamplePresenter {
    private IExampleView view;
    private IExampleService service;
    private Context context;

    public ExamplePresenter(IExampleService service, Context context){
        this.service = service;
        this.context = context;
    }

    @Override
    public void takeView(IExampleView view) {
        this.view = view;
        this.view.setText(this.service.getText());
    }
}
