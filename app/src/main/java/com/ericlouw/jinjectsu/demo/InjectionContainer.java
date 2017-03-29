package com.ericlouw.jinjectsu.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ericlouw.jinjectsu.jinjectsu.Jinjectsu;
import com.ericlouw.jinjectsu.jinjectsu.interfaces.IFactoryMethod;

public class InjectionContainer
{
    private static Jinjectsu instance;

    public static Jinjectsu getInstalledInstance(Context context){
        if(instance == null){
            instance = new Jinjectsu();
            configure(context, instance);
        }

        return instance;
    }

    private static void configure(final Context context, Jinjectsu jinjectsu){
        try {
            jinjectsu
                    .bind(IExamplePresenter.class).lifeStyleScoped(ExamplePresenter.class)
                    .bind(IExampleService.class).lifestyleSingleton(ExampleService.class)
                    .bind(IExampleRepository.class).lifestyleSingleton(ExampleRepository.class)
                    .bind(IPreferencesManager.class).lifestyleSingleton(PreferencesManager.class)
                    .bind(SharedPreferences.class).lifestyleSingleton(new IFactoryMethod<SharedPreferences>() {
                        @Override
                        public SharedPreferences create() {
                            return PreferenceManager.getDefaultSharedPreferences(context);
                        }
                    })
                    .bind(Context.class).providedByScope().satisfiedBy(MainActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
