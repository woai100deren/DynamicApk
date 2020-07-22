package com.dj.usercenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dj.pluginlib.PluginBaseActivity;

public class RegisterActivity extends PluginBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btn_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHostPageActivity("com.dj.dynamicapk.GoodsDetailActivity");
            }
        });
    }
}
