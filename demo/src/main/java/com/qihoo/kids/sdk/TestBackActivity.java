package com.qihoo.kids.sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLDecoder;

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


        initView();
    }

    private void initView() {
        TextView tvObtainBgWhitelistPermissions = findViewById(R.id.tv_obtain_bg_whitelist_permissions);
        tvObtainBgWhitelistPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2. 申请后台免杀权
                QWatch.setAllowAppIfBackground(true);
                startPickImageActivity();
            }
        });
    }

    private final int REQUEST_CODE_PICK_IMAGE = 1000;
    private final String ACTION_PICK_PHOTO = "com.qihoo.kids.gallery.ACTION_PICK";
    private final String KEY_PICK_FROM_CAMERA_ENABLE = "PICK_FROM_CAMERA_ENABLE";
    private final String KEY_EXTRA_ACTION_TYPE = "key_extra_action_type";
    private final int OK = 0;

    private void startPickImageActivity() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //3. 释放后台免杀权
        QWatch.setAllowAppIfBackground(false);
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data == null) {
                        return;
                    }
                    Uri uri;
                    if (data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
                        uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    } else {
                        uri = data.getData();
                    }
                    if (uri != null) {
                        String path = URLDecoder.decode(uri.getAuthority() + uri.getPath(), "UTF-8");
                        if (!TextUtils.isEmpty(path)) {
                            Log.i(TAG, "onActivityResult: image path : " + path);
                            Toast.makeText(this, "path=" + path, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: e= " + e.getMessage());
                }
            }
        }
    }

}
