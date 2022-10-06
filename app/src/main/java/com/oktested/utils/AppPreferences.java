package com.oktested.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private static final String APP_SHARED_PREFS = "com.webtexo";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static volatile AppPreferences instance;

    private AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public synchronized static AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    public String getPreferencesString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void savePreferencesString(String key, String value) {
        this.editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void removePreferencesString(String key) {
        this.editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}