package com.dj.dynamicapk.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;
import com.dj.dynamicapk.study.AMNHook.PSAMNHook;
import com.dj.dynamicapk.study.instrumentationHook.PSInstrumentationHook;

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
    }
}
