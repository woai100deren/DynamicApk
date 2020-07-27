package com.dj.dynamicplugin1;

import android.app.Application;
import android.util.Log;

public class DYPlugin1Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("123456","我是插件的application onCreate方法：123456789");
    }
}
