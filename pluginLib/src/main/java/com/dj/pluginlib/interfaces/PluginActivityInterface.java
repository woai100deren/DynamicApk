package com.dj.pluginlib.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 插件工程页面的生命周期接口
 */
public interface PluginActivityInterface {
    /**
     * 给插件Activity指定上下文
     *
     * @param activity
     */
    public void attach(Activity activity);

    // 以下全都是Activity生命周期函数,
    // 插件Activity本身 在被用作"插件"的时候不具备生命周期，由宿主里面的代理Activity类代为管理
    public void onCreate(Bundle bundle);
    public void onStart();
    public void onResume();
    public void onRestart();
    public void onStop();
    public void onPause();
    public void onDestroy();
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
