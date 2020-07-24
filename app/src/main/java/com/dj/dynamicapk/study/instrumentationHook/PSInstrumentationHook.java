package com.dj.dynamicapk.study.instrumentationHook;

import android.content.Intent;
import android.view.View;

import com.dj.dynamicapk.R;

public class PSInstrumentationHook extends PSInstrumentationHookBaseActivity{
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_ps_instrumentation_hook);
    }

    public void jumpNext(View view) {
        startActivity(new Intent(this,PSInstrumentationHookSecond.class));
    }
}
