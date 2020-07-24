package com.dj.dynamicapk.application;

import android.app.Application;

import com.dj.dynamicapk.study.AMNHook.PSAMNHookManager;
import com.dj.pluginlib.manager.PluginManager;

public class DynamicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);

        //这种hook方式，拦截了整个工程的页面跳转
        PSAMNHookManager.hook();
    }
}
