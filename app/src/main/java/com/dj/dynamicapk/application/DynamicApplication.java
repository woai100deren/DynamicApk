package com.dj.dynamicapk.application;

import android.app.Application;

import com.dj.pluginlib.manager.PluginManager;

public class DynamicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);
    }
}
