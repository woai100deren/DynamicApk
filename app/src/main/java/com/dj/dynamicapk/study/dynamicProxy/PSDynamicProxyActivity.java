package com.dj.dynamicapk.study.dynamicProxy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.dynamicapk.utils.Utils;

public class PSDynamicProxyActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            Utils.extractAssets(newBase, "dynamicPlugin1-debug.apk");

            PSLoadedApkClassLoaderHookHelper.hookLoadedApkInActivityThread(
                    getFileStreamPath("dynamicPlugin1-debug.apk"));

            PSAMSHookHelper.hookAMN();
            PSAMSHookHelper.hookActivityThread();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_dynamic_proxy);

        findViewById(R.id.hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent();
                t.setComponent(new ComponentName("com.dj.dynamicplugin1",
                                "com.dj.dynamicplugin1.DynamicMainActivity"));

                startActivity(t);
            }
        });
    }
}
