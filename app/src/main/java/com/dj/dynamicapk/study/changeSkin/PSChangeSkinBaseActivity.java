package com.dj.dynamicapk.study.changeSkin;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.dj.pluginlib.manager.PluginManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class PSChangeSkinBaseActivity extends Activity {
    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    protected Map<String,PSPluginInfo> pluginInfoList = new HashMap<>();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        PluginManager.getInstance().extractAssets(this,"skin1-debug.apk");

        loadPlugin("skin1-debug.apk");
    }

    private void loadPlugin(String pluginName){
        File extractFile = getFileStreamPath(pluginName);
        String dexPath = extractFile.getPath();
        //Dex优化后的缓存目录
        File odexFile = getDir("odex", Context.MODE_PRIVATE);
        //创建DexClassLoader加载器
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, odexFile.getAbsolutePath(), null, getClassLoader());

        PSPluginInfo psPluginInfo = new PSPluginInfo();
        psPluginInfo.setDexPath(dexPath);
        psPluginInfo.setDexClassLoader(dexClassLoader);
        pluginInfoList.put(pluginName,psPluginInfo);
    }

    protected void loadResources(String dexPath){
        try{
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getMethod("addAssetPath",String.class);
            method.invoke(assetManager,dexPath);
            mAssetManager = assetManager;

            mResources = new Resources(mAssetManager,super.getResources().getDisplayMetrics(),super.getResources().getConfiguration());
            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public AssetManager getAssets() {
        if(mAssetManager == null){
            return super.getAssets();
        }
        return mAssetManager;
    }

    @Override
    public Resources getResources() {
        if(mResources == null) {
            return super.getResources();
        }
        return mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        if(mTheme == null){
            return super.getTheme();
        }
        return mTheme;
    }
}
