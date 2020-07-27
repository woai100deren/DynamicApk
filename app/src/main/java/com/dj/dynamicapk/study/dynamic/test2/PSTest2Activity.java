package com.dj.dynamicapk.study.dynamic.test2;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.mypluginlibrary1.IDynamic;
import com.dj.pluginlib.manager.PluginManager;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PSTest2Activity extends Activity {
    private String apkName = "dynamicPlugin1-debug.apk";
    private IDynamic iDynamic = null;
    private String dexPath;
    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_test1);

        PluginManager.getInstance().extractAssets(this,apkName);

        final TextView textView = findViewById(R.id.textView);

        try {
            DexClassLoader classLoader = getAssetsPluginApkDexClassLoader(this,apkName);
            Class mLoadBean = classLoader.loadClass("com.dj.dynamicplugin1.Dynamic1Resource");
            Object beanObject = mLoadBean.newInstance();

            iDynamic = (IDynamic)beanObject;
        }catch (Exception e){
            e.printStackTrace();
        }

        findViewById(R.id.showResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadResource();
                textView.setText(iDynamic.getStringForResId(PSTest2Activity.this));
            }
        });
    }

    public DexClassLoader getAssetsPluginApkDexClassLoader(Context context, String pluginName) {
        File extractFile = context.getFileStreamPath(pluginName);
        dexPath = extractFile.getPath();
        //Dex优化后的缓存目录
        File odexFile = context.getDir("odex", Context.MODE_PRIVATE);
        //创建DexClassLoader加载器
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, odexFile.getAbsolutePath(), null, context.getClassLoader());
        return dexClassLoader;
    }

    private void loadResource(){
        try{
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",String.class);
            addAssetPath.invoke(assetManager,dexPath);
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
        if(mResources == null){
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
