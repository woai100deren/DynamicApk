package com.dj.dynamicapk.study.AMNHook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;

public class PSAMNHook extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_amn_hook);
    }
}
