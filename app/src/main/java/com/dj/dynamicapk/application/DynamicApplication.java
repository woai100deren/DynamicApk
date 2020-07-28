package com.dj.dynamicapk.application;

import android.app.Application;
import android.content.Context;

import com.dj.dynamicapk.study.AMNHook.PSAMNHookManager;
import com.dj.pluginlib.manager.PluginManager;

import dalvik.system.DexClassLoader;

public class DynamicApplication extends Application {
    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);

        //这种hook方式，拦截了整个工程的页面跳转
        PSAMNHookManager.hook();

        PluginManager.getInstance().extractAssets(this,"dynamicPlugin1-debug.apk");
        try {
            DexClassLoader classLoader = PluginManager.getInstance().getAssetsPluginApkDexClassLoader(this, "dynamicPlugin1-debug.apk");
            Class mLoadBean = classLoader.loadClass("com.dj.dynamicplugin1.DYPlugin1Application");
            Application application = (Application)mLoadBean.newInstance();
            if(application!=null){
                application.onCreate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    public static Context getContext() {
        return sContext;
    }
}
