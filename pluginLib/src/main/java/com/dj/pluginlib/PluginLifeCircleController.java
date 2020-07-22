package com.dj.pluginlib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.dj.pluginlib.consts.PluginConst;
import com.dj.pluginlib.interfaces.PluginActivityInterface;
import com.dj.pluginlib.manager.PluginActivityStackManager;
import com.dj.pluginlib.manager.PluginManager;

/**
 * 调用插件Activity中的具体方法
 */
public class PluginLifeCircleController implements PluginActivityInterface {
    private PluginActivityInterface mPlugin;
    private Resources mResources;
    private ClassLoader classLoader;

    public void handleIntent(Intent intent, Activity activity) {
        String reallyActivity = intent.getExtras().getString(PluginConst.REALLY_ACTIVITY_NAME);
        String dexPath = intent.getExtras() .getString(PluginConst.DEX_PATH);
        initResources(dexPath);
        PluginActivityStackManager.getInstance().addActivity(activity, intent.getExtras());
        try {
            Class<?> aClass = PluginManager.getInstance().getPluginItem(dexPath).getClassLoader().loadClass(reallyActivity);
            Object o = aClass.newInstance();
            if(o instanceof PluginActivityInterface) {
                mPlugin = (PluginActivityInterface) o;
                Bundle bundle = new Bundle();
                //设置是插件跳转
                bundle.putBoolean(PluginConst.isPlugin, true);
                mPlugin.attach(activity);
                mPlugin.onCreate(bundle);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void initResources(String dexPath) {
        mResources = PluginManager.getInstance().getPluginItem(dexPath).getResources();
        classLoader = PluginManager.getInstance().getPluginItem(dexPath).getClassLoader();
    }

    @Override
    public void attach(Activity activity) {

    }

    @Override
    public void onCreate(Bundle bundle) {

    }


    @Override
    public void onStart() {
        if (mPlugin != null) {
            mPlugin.onStart();
        }
    }

    @Override
    public void onResume() {
        if (mPlugin != null) {
            mPlugin.onResume();
        }
    }

    @Override
    public void onRestart() {
        if(mPlugin != null) {
            mPlugin.onRestart();
        }
    }

    @Override
    public void onStop() {
        if(mPlugin != null) {
            mPlugin.onStop();
        }
    }

    @Override
    public void onPause() {
        if(mPlugin != null) {
            mPlugin.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if(mPlugin != null) {
            mPlugin.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public Resources getResources() {
        return mResources;
    }

    public AssetManager getAssets() {
        return mResources.getAssets();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
