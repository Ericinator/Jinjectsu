package com.example.ericlouw.demo;

import android.content.Context;

import com.example.ericlouw.jinjectsu.Jinjectsu;

public class InjectionContainer
{
    private static Jinjectsu instance;

    public static Jinjectsu getInstalledInstance(){
        if(instance == null){
            instance = new Jinjectsu();
            configure(instance);
        }

        return instance;
    }

    private static void configure(Jinjectsu jinjectsu){
        try {
            jinjectsu
                    .bind(IExamplePresenter.class).lifeStyleScoped(ExamplePresenter.class)
                    .bind(IExampleService.class).lifestyleSingleton(ExampleService.class)
                    .bind(IExampleRepository.class).lifestyleSingleton(ExampleRepository.class)
                    .bind(Context.class).providedByScope();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
