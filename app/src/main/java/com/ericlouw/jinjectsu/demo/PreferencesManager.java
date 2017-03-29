package com.ericlouw.jinjectsu.demo;

import android.content.SharedPreferences;

public class PreferencesManager implements IPreferencesManager {

    private SharedPreferences sharedPreferences;

    public PreferencesManager(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
    }
}
