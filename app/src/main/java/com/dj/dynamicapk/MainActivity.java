package com.dj.dynamicapk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.dj.pluginlib.consts.PluginConst;
import com.dj.pluginlib.manager.PluginManager;

public class MainActivity extends AppCompatActivity {
    private String pluginPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/userCenter-debug.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadPlugin(View view) {
        boolean result = PluginManager.getInstance().loadPluginApk(pluginPath);
        if(result){
            showToast("加载插件成功");
        }else{
            showToast("加载插件失败");
        }
    }

    public void jumpPluginLogin(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(PluginConst.DEX_PATH, pluginPath);
        bundle.putString(PluginConst.REALLY_ACTIVITY_NAME, "com.dj.usercenter.LoginActivity");
        PluginManager.getInstance().startActivity(this,bundle);
    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}