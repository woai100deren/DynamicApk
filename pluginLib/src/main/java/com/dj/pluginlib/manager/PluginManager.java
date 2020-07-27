package com.dj.pluginlib.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.dj.pluginlib.ProxyActivity;
import com.dj.pluginlib.consts.PluginConst;
import com.dj.pluginlib.model.PluginInfo;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * 插件加载等相关的管理类
 */
public class PluginManager {
    private volatile static PluginManager instance = null;
    private Context context;
    /**
     * String : 插件的DexPath路径
     * PlugItem：@Link #PluginItem
     */
    private HashMap<String, PluginInfo> pluginItemHashMap;

    private PluginManager() {}

    public static PluginManager getInstance() {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        pluginItemHashMap = new HashMap<>();
    }

    /**
     * 加载插件APK
     * @param apkPath APK或者jar或者dex的目录
     */
    public boolean loadPluginApk(String apkPath) {
        //创建DexClassLoader加载器
        DexClassLoader dexClassLoader = getPluginApkDexClassLoader(apkPath);
        //创建AssetManager，然后创建Resources
        Resources resources = null;
        Resources.Theme mTheme;
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.invoke(assetManager, apkPath);
            resources = new Resources(assetManager,
                    context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());
            mTheme = resources.newTheme();
            mTheme.setTo(context.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        pluginItemHashMap.put(apkPath, new PluginInfo(apkPath, dexClassLoader, resources,mTheme));
        return true;
    }

    /**
     * 获取插件DexPath对应的相关信息{@link #loadPluginApk(String)}
     * @param dexPath
     * @return
     */
    public PluginInfo getPluginItem(String dexPath) {
        return pluginItemHashMap.get(dexPath);
    }

    /**
     * 适用：宿主到插件，插件到插件（注：插件到宿主跳转不适用）
     * @param context
     * @param bundle
     */
    public void startActivity(Context context, Bundle bundle) {
        if(!checkPluginActivityIsExit(bundle)) {
            Toast.makeText(context, "未加载插件", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isCanJump = PluginActivityStackManager.getInstance().checkCanStartNewActivity(bundle);
        if (isCanJump) {
            Intent intent = new Intent(context, ProxyActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    /**
     *检查插件是否存在
     */
    private boolean checkPluginActivityIsExit(Bundle bundle) {
        String dexPath = bundle.getString(PluginConst.DEX_PATH);
        String reallyActivityName = bundle.getString(PluginConst.REALLY_ACTIVITY_NAME);
        ClassLoader classLoader = PluginManager.getInstance().getPluginItem(dexPath).getClassLoader();
        Class<?> aClass = null;
        try {
            aClass = classLoader.loadClass(reallyActivityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return aClass != null;
    }

    /**
     * 把Assets里面得文件复制到 /data/data/files 目录下
     *
     * @param context
     * @param sourceName
     */
    public void extractAssets(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }

    /**
     * 获取加载插件的DexClassLoader
     * @param context
     * @param pluginName assets下插件名称(包含后缀)
     */
    public DexClassLoader getAssetsPluginApkDexClassLoader(Context context,String pluginName) {
        File extractFile = context.getFileStreamPath(pluginName);
        String dexPath = extractFile.getPath();
        //Dex优化后的缓存目录
        File odexFile = context.getDir("odex", Context.MODE_PRIVATE);
        //创建DexClassLoader加载器
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, odexFile.getAbsolutePath(), null, context.getClassLoader());
        return dexClassLoader;
    }

    /**
     * 获取加载插件的DexClassLoader
     * @param apkPath APK或者jar或者dex的目录
     */
    public DexClassLoader getPluginApkDexClassLoader(String apkPath) {
        //Dex优化后的缓存目录
        File odexFile = context.getDir("odex", Context.MODE_PRIVATE);
        //创建DexClassLoader加载器
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, odexFile.getAbsolutePath(), null, context.getClassLoader());
        return dexClassLoader;
    }
}
