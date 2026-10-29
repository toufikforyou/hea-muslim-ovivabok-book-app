package com.toufikhasan.heamoslimovivabok;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.toufikhasan.heamoslimovivabok.data.ReadingPrefs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Apply the reader's chosen light/dark preference before any screen is shown.
        AppCompatDelegate.setDefaultNightMode(new ReadingPrefs(this).getNightMode());
    }
}
