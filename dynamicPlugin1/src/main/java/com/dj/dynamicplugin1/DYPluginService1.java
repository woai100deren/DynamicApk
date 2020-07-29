package com.dj.dynamicplugin1;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class DYPluginService1 extends Service {
    private static final String TAG = DYPluginService1.class.getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,">>> onStartCommand <<<");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,">>> onBind <<<");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG,">>> onRebind <<<");
        super.onRebind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.e(TAG,">>> unbindService <<<");
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,">>> onDestroy <<<");
        super.onDestroy();
    }
}
