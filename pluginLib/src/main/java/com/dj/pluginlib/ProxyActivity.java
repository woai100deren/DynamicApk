package com.dj.pluginlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.dj.pluginlib.manager.PluginActivityStackManager;

/**
 * 代理activity类，宿主页面跳转插件页面时，统一跳转到了此页面
 */
public class ProxyActivity extends Activity {
    private PluginLifeCircleController mPluginController;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        mPluginController = new PluginLifeCircleController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginController.handleIntent(getIntent(),this);
    }

    @Override
    public Resources getResources() {
        // construct when loading apk
        Resources resources = mPluginController.getResources();
        return resources == null ? super.getResources() : resources;
    }

//    @Override
//    public Resources.Theme getTheme() {
//        Resources.Theme theme = mPluginController.getTheme();
//        return theme == null ? super.getTheme() : theme;
//    }

    @Override
    public AssetManager getAssets() {
        AssetManager assetManager = mPluginController.getAssets();
        return assetManager == null?super.getAssets():assetManager;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPluginController.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPluginController.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPluginController.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPluginController.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPluginController.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
//        String className = intent.getComponent().getClassName();
//        //此时是需要跳到宿主APP中
//        if(!"com.dj.pluginlib.ProxyActivity".equals(className)) {
//            PluginActivityStackManager.getInstance().clearActivityList();
//        }
        super.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PluginActivityStackManager.getInstance().removeLastActivity();
    }
}
