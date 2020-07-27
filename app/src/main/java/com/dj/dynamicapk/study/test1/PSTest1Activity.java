package com.dj.dynamicapk.study.test1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.mypluginlibrary1.IBean;
import com.dj.mypluginlibrary1.ICallback;
import com.dj.pluginlib.manager.PluginManager;

import java.io.File;

import dalvik.system.DexClassLoader;

public class PSTest1Activity extends Activity {
    private String apkName = "dynamicPlugin1-debug.apk";
    private IBean iBean = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_test1);

        PluginManager.getInstance().extractAssets(this,apkName);

        final TextView textView = findViewById(R.id.textView);

        //方式1
//        try {
//            DexClassLoader classLoader = PluginManager.getInstance().getAssetsPluginApkDexClassLoader(this,apkName);
//            Class mLoadBean = classLoader.loadClass("com.dj.dynamicplugin1.Dynamic1Bean");
//            Object beanObject = mLoadBean.newInstance();
//
//            IBean iBean = (IBean)beanObject;
//            iBean.setName("哈哈哈哈哈");
//            textView.setText(iBean.getName());
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        //方式二

        try {
            DexClassLoader classLoader = PluginManager.getInstance().getAssetsPluginApkDexClassLoader(this,apkName);
            Class mLoadBean = classLoader.loadClass("com.dj.dynamicplugin1.Dynamic1Bean");
            Object beanObject = mLoadBean.newInstance();

            iBean = (IBean)beanObject;
            iBean.setName("哈哈哈哈哈");
            iBean.register(new ICallback() {
                @Override
                public void sendResult(String result) {
                    textView.setText(result);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        findViewById(R.id.showResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iBean.clickButton();
            }
        });
    }
}
