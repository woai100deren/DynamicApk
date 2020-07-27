package com.dj.dynamicapk.study.changeSkin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.R;

public class PSChangeSkinActivity extends PSChangeSkinBaseActivity {
    private View topView;
    private TextView textView;
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_change_skin);

        topView = findViewById(R.id.topView);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        findViewById(R.id.changeSkin1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PSPluginInfo pluginInfo = pluginInfoList.get("skin1-debug.apk");
                loadResources(pluginInfo.getDexPath());
                changeValue();
            }
        });
    }

    private void changeValue(){
        topView.setBackgroundColor(getResources().getColor(R.color.colorRed));
        textView.setText(getResources().getString(R.string.app_name));
        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.image_x));
    }
}
