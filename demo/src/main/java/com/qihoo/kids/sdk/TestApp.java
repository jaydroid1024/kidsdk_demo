package com.qihoo.kids.sdk;

import android.app.Application;
import android.content.Intent;

import com.qihoo.kids.sdk.service.MyService;

import qihoo.sdk.QWatch;

public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //需要在Application 启动的时候后进行初始化
        QWatch.init(this);
        startService(new Intent(this, MyService.class));
    }
}
