package com.dj.dynamicapk.study.dynamicService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.dynamicapk.application.DynamicApplication;
import com.dj.dynamicapk.study.dynamicProxy.PSAMSHookHelper;
import com.dj.dynamicapk.study.dynamicProxy.PSClassLoaderHookHelper;
import com.dj.dynamicapk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class PSDynamicServiceActivity extends Activity {
    private static final String TAG = PSDynamicServiceActivity.class.getName();
    private static final String apkName = "dynamicPlugin1-debug.apk";
    private Intent intent = new Intent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_dynamic_service);
        intent.setComponent(
                new ComponentName("com.dj.dynamicplugin1",
                        "com.dj.dynamicplugin1.DYPluginService1"));

        findViewById(R.id.serviceSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
            }
        });

        findViewById(R.id.serviceBind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.dj.dynamicplugin2",
                                "com.dj.dynamicplugin1.DYPluginService2"));
                bindService(intent,connection,BIND_AUTO_CREATE);
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG,"onServiceDisconnected");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        unbindService(connection);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            DynamicApplication.pluginServices.clear();
            Utils.extractAssets(newBase, apkName);

            File apkFile = getFileStreamPath(apkName);
            File optDexFile = Utils.getPluginOptDexDir(apkName);
//            PSBaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
            Log.e(TAG,optDexFile.getAbsolutePath());
//            PSClassLoaderHookHelper.hookV23(getClassLoader(), optDexFile, getCacheDir());
            PSClassLoaderHookHelper.loadClass(this,apkFile.getAbsolutePath());

            PSAMSHookHelper.hookAMN();
            PSAMSHookHelper.hookActivityThread();

            String strJSON = Utils.readZipFileString(apkFile.getAbsolutePath(), "assets/plugin_config.json");

            if(strJSON != null && !TextUtils.isEmpty(strJSON)) {
                JSONObject jObject = new JSONObject(strJSON.replaceAll("\r|\n", ""));
                JSONArray jsonArray = jObject.getJSONArray("plugins");
                for(int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    DynamicApplication.pluginServices.put(
                            jsonObject.optString("PluginService"),
                            jsonObject.optString("StubService"));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
