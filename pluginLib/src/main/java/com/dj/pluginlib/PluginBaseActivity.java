package com.dj.pluginlib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dj.pluginlib.consts.PluginConst;
import com.dj.pluginlib.interfaces.PluginActivityInterface;
import com.dj.pluginlib.manager.PluginManager;

public class PluginBaseActivity extends AppCompatActivity implements PluginActivityInterface{
    public final static String TAG = PluginBaseActivity.class.getSimpleName();
    protected Activity that;
    boolean isPlugin = false; // 是否是插件运行
    private int launchModel = -1;
    @Override
    public void attach(Activity proxy) {
        that = proxy;
        PluginLog.log(TAG,"that context:"+that.getLocalClassName());
    }

    @Override
    public void setContentView(int layoutResID) {
        if (!isPlugin) {
            super.setContentView(layoutResID);
        } else {
            that.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        if (!isPlugin) {
            super.setContentView(view);
        } else {
            that.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (!isPlugin) {
            super.setContentView(view, params);
        } else {
            that.setContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (!isPlugin) {
            return super.findViewById(id);
        } else {
            return that.findViewById(id);
        }
    }

    @Override
    public WindowManager getWindowManager() {
        if (!isPlugin) {
            return super.getWindowManager();
        } else {
            return that.getWindowManager();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (!isPlugin) {
            return super.getClassLoader();
        } else {
            return that.getClassLoader();
        }
    }

    @Override
    public Resources getResources() {
        if (!isPlugin) {
            return super.getResources();
        } else {
            return that.getResources();
        }
    }

    @Override
    public Context getApplicationContext() {
        if (!isPlugin) {
            return super.getApplicationContext();
        } else {
            return that.getApplicationContext();
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (!isPlugin) {
            return super.getMenuInflater();
        } else {
            return that.getMenuInflater();
        }
    }


    @Override
    public Window getWindow() {
        if (!isPlugin) {
            return super.getWindow();
        } else {
            return that.getWindow();
        }

    }

    @Override
    public Intent getIntent() {
        if (!isPlugin) {
            return super.getIntent();
        } else {
            return that.getIntent();
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (!isPlugin) {
            return super.getLayoutInflater();
        } else {
            return that.getLayoutInflater();
        }
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        if(saveInstanceState != null) {
            isPlugin = saveInstanceState.getBoolean(PluginConst.isPlugin, false);
        }
        PluginLog.log(TAG,"isPlugin:"+isPlugin);
        if(!isPlugin){
            super.onCreate(saveInstanceState);
            that = this;
        }
    }

    @Override
    public void onStart() {
        if(!isPlugin){
            super.onStart();
        }
    }

    @Override
    public void onResume() {
        if(!isPlugin){
            super.onResume();
        }
    }

    @Override
    public void onRestart() {
        if(!isPlugin){
            super.onRestart();
        }
    }

    @Override
    public void onStop() {
        if(!isPlugin){
            super.onStop();
        }
    }

    @Override
    public void onPause() {
        if(!isPlugin){
            super.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if(!isPlugin){
            super.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!isPlugin){
            super.onActivityResult(requestCode,requestCode,data);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(!isPlugin) {
            super.startActivity(intent);
        } else {
            String pluginPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/userCenter-debug.apk";
            Bundle bundle = setBundleData(intent.getExtras(), pluginPath, intent.getComponent().getClassName(), PluginConst.LaunchModel.SINGLE_TASK);
            PluginManager.getInstance().startActivity(that, bundle);
        }
    }

    /**
     * 一个插件跳Activity转到另一个插件Activity
     * @param bundleParam
     * @param dexPath
     * @param reallyActivityName
     * @param launchModel
     */
    public  void startOtherPluginActivity(Bundle bundleParam, String dexPath, String reallyActivityName, int launchModel) {
        if(!isPlugin) {
            Toast.makeText(that, "无法找到："+ reallyActivityName, Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = setBundleData(bundleParam, dexPath, reallyActivityName, launchModel);
        PluginManager.getInstance().startActivity(that, bundle);
    }

    /**
     * @param bundleParam
     * @param dexPath
     * @param reallyActivityName
     * @param launchModel
     * @return
     */
    private Bundle setBundleData(Bundle bundleParam, String dexPath, String reallyActivityName, int launchModel) {
        Bundle bundle = bundleParam == null ? new Bundle() : bundleParam;
        bundle.putString(PluginConst.DEX_PATH, dexPath);
        bundle.putString(PluginConst.REALLY_ACTIVITY_NAME, reallyActivityName);
        bundle.putInt(PluginConst.LAUNCH_MODEL, launchModel);
        return  bundle;
    }

    /**
     * 插件跳转到宿主指定页面
     * @param hostPage
     */
    public void startHostPageActivity(String hostPage) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.dj.dynamicapk", hostPage);
        intent.setComponent(componentName);
        that.startActivity(intent);
    }
}
