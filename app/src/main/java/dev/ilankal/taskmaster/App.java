package dev.ilankal.taskmaster;

import android.app.Application;

import dev.ilankal.taskmaster.Data.DataManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.init(this);
    }
}