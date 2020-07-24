package com.dj.dynamicapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.dj.dynamicapk.study.PluginStudyMainActivity;
import com.dj.pluginlib.consts.PluginConst;
import com.dj.pluginlib.manager.PluginManager;

public class MainActivity extends AppCompatActivity {
    private String plugin1Path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/userCenter-debug.apk";
    private String plugin2Path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/baidumap-debug.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadPlugin(View view) {
        switch (view.getId()){
            case R.id.btn_load_uc: {
                boolean result = PluginManager.getInstance().loadPluginApk(plugin1Path);
                if (result) {
                    showToast("加载插件成功");
                } else {
                    showToast("加载插件失败");
                }
                break;
            }
            case R.id.btn_load_bm: {
                boolean result = PluginManager.getInstance().loadPluginApk(plugin2Path);
                if (result) {
                    showToast("加载插件成功");
                } else {
                    showToast("加载插件失败");
                }
                break;
            }
            default:
                break;
        }

    }

    public void jumpPluginLogin(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(PluginConst.DEX_PATH, plugin1Path);
        bundle.putString(PluginConst.REALLY_ACTIVITY_NAME, "com.dj.usercenter.LoginActivity");
        PluginManager.getInstance().startActivity(this,bundle);
    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void jumpPluginStudy(View view) {
        startActivity(new Intent(this, PluginStudyMainActivity.class));
    }
}