package com.qihoo.kids.sdk;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.qihoo.kids.sdk.service.MyService;
import com.qihoo.kids.sdk.util.LogUtil;

import qihoo.sdk.QWatch;

public class TestApp extends Application {

    private static final String TAG = TestApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //需要在Application 启动的时候后进行初始化
        QWatch.init(this);
        Log.e(TAG, "device_id="+QWatch.getDeviceID(getApplicationContext()));
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(new Intent(this, MyService.class));
        } else {
            startForegroundService(new Intent(this, MyService.class));
        }
    }
}
