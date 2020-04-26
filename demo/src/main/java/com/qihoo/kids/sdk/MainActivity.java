package com.qihoo.kids.sdk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qihoo.kids.sdk.player.Song;
import com.qihoo.kids.sdk.service.MyService;
import com.qihoo.kids.sdk.util.LogUtil;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private IPlayer mPlayer;
    private CheckBox vPlayCheck;
    private TextView vTitle;
    private ProgressBar vProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this, MyService.class), mServiceConnection, 0);
        initView();
    }

    private void initView() {
        vPlayCheck = findViewById(R.id.check_play);
        vTitle = findViewById(R.id.title);
        vProgress = findViewById(R.id.progress_horizontal);

        vProgress.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                vProgress.getProgress();
                return false;
            }
        });

        vPlayCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (mPlayer != null) {
                        if (isChecked) {
                            mPlayer.play();
                        } else {
                            mPlayer.stop();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected...");
            mPlayer = IPlayer.Stub.asInterface(service);
            try {
                mPlayer.registStateListener(mPlayListenerStub);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected...");
            mPlayer = null;
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        if(mPlayer != null){
            try {
                mPlayer.unregistStateListener(mPlayListenerStub);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    public void playClick(View view) {
    }

    public void preClick(View view) {
        try {
            if (mPlayer != null) {
                mPlayer.pre();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void nextClick(View view) {
        try {
            if (mPlayer != null) {
                mPlayer.next();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IPlayStateListener mPlayListenerStub = new IPlayStateListener.Stub() {
        @Override
        public void onStateChange(boolean b) throws RemoteException {
            LogUtil.d(TAG, "onStateChange:"+b);
            vPlayCheck.setChecked(b);
        }

        @Override
        public void onSongChanged(Song song) throws RemoteException {
            LogUtil.d(TAG, "onSongChanged:"+song);
            vTitle.setText(song.getTitle());
        }

        @Override
        public void onProgressChange(int max, int progress) throws RemoteException {
            LogUtil.d(TAG, "onProgressChange:"+progress);
            if (vProgress.getMax() != max) {
                vProgress.setMax(max);
            }
            vProgress.setProgress(progress);
        }
    };
}
