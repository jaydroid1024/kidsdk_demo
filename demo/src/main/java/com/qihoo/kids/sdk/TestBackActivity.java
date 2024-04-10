package com.qihoo.kids.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import qihoo.sdk.QWatch;

/**
 * 测试 360 儿童手表退出后台后免杀的 Demo
 */
public class TestBackActivity extends FragmentActivity {

    private static final String TAG = TestBackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_back);

        //1. 初始化 QWatch SDK
//        QWatch.init(this); TestApp中已经调用
        //2. 申请后台免杀权
        QWatch.setAllowAppIfBackground(true);

        initView();
    }

    private void initView() {
        TextView tvObtainBgWhitelistPermissions = findViewById(R.id.tv_obtain_bg_whitelist_permissions);
        tvObtainBgWhitelistPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickImageActivity();
            }
        });
    }

    private void startPickImageActivity() {
        String ACTION_PICK_PHOTO = "com.qihoo.kids.gallery.ACTION_PICK";
        String KEY_PICK_FROM_CAMERA_ENABLE = "PICK_FROM_CAMERA_ENABLE";
        int REQUEST_CODE_PICK_IMAGE = 1000;
        String KEY_EXTRA_ACTION_TYPE = "key_extra_action_type";
        int OK = 0;
        try {
            Intent intent = new Intent(ACTION_PICK_PHOTO);
            intent.setType("image/*");
            intent.putExtra(KEY_PICK_FROM_CAMERA_ENABLE, true);
            intent.putExtra(KEY_EXTRA_ACTION_TYPE, OK);
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startPickerImage: e= " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //3. 释放后台免杀权
        QWatch.setAllowAppIfBackground(false);
    }

}
