package com.dj.dynamicapk.study.changeSkin;

import dalvik.system.DexClassLoader;

public class PSPluginInfo {
    private String dexPath;
    private DexClassLoader dexClassLoader;

    public String getDexPath() {
        return dexPath;
    }

    public void setDexPath(String dexPath) {
        this.dexPath = dexPath;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setDexClassLoader(DexClassLoader dexClassLoader) {
        this.dexClassLoader = dexClassLoader;
    }
}
