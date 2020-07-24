package com.dj.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dj.pluginlib.PluginBaseActivity;
import com.dj.pluginlib.consts.PluginConst;

public class LoginActivity extends PluginBaseActivity {
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.xrk));

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(that,RegisterActivity.class));
            }
        });

        findViewById(R.id.btn_baidu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pluginPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/baidumap-debug.apk";
                startOtherPluginActivity(null,pluginPath,"com.dj.baidumap.BaiduMapActivity", PluginConst.LaunchModel.STANDARD);
            }
        });

        TextView txt = (TextView) findViewById(R.id.textView);
        txt.setText(getSecret());
    }

    public static native String getSecret();
}
