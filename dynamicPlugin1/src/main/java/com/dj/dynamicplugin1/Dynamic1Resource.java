package com.dj.dynamicplugin1;

import android.content.Context;

import com.dj.mypluginlibrary1.IDynamic;

public class Dynamic1Resource implements IDynamic {
    @Override
    public String getStringForResId(Context context) {
        return context.getResources().getString(R.string.dynamicPlugin1_hello_world);
    }
}
