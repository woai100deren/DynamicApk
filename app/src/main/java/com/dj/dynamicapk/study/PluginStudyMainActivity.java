package com.dj.dynamicapk.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.dynamicapk.study.AMNHook.PSAMNHook;
import com.dj.dynamicapk.study.changeSkin.PSChangeSkinActivity;
import com.dj.dynamicapk.study.dynamic.test2.PSTest2Activity;
import com.dj.dynamicapk.study.dynamicProxy.PSDynamicProxyActivity;
import com.dj.dynamicapk.study.dynamicService.PSDynamicServiceActivity;
import com.dj.dynamicapk.study.instrumentationHook.PSInstrumentationHook;
import com.dj.dynamicapk.study.dynamic.test1.PSTest1Activity;

public class PluginStudyMainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_main);

        findViewById(R.id.instrumentationHook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSInstrumentationHook.class));
            }
        });

        findViewById(R.id.activityManagerHook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSAMNHook.class));
            }
        });

        findViewById(R.id.dynamicTest1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSTest1Activity.class));
            }
        });

        findViewById(R.id.dynamicTest2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSTest2Activity.class));
            }
        });

        findViewById(R.id.changeSkin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSChangeSkinActivity.class));
            }
        });

        findViewById(R.id.dynamicTest3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSDynamicProxyActivity.class));
            }
        });

        findViewById(R.id.dynamicService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PluginStudyMainActivity.this, PSDynamicServiceActivity.class));
            }
        });
    }
}
