package com.qihoo.kids.sdk.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.qihoo.kids.sdk.IPlayStateListener;
import com.qihoo.kids.sdk.IPlayer;
import com.qihoo.kids.sdk.player.Player;
import com.qihoo.kids.sdk.player.Song;
import com.qihoo.kids.sdk.util.LogUtil;

import java.util.concurrent.ConcurrentHashMap;

import qihoo.sdk.QWatch;
import qihoo.sdk.event.OnSystemEventListener;
import qihoo.sdk.event.SystemEvent;
import qihoo.sdk.widget.WidgetPlayer;
import qihoo.sdk.widget.i.OnPlayWidgetEventListener;

/**
 * 启动一个Service进行音乐的播放
 */
public class MyService extends Service implements OnPlayWidgetEventListener,OnSystemEventListener {
    private static final String TAG = MyService.class.getSimpleName();
    private static final int FOREGROUND_ID = 0x11;
    private WidgetPlayer mWidgetPlayer;
    private Player mPlayer;
    private PlayerProxy mPlayerProxy;
    private ConcurrentHashMap<IBinder, StateReceiver> mReceivers = new ConcurrentHashMap();

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new Player();
        mPlayerProxy = new PlayerProxy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setOnAudioFocusChangeListener(focusChangeListener).build();
            audioFocusRequest.acceptsDelayedFocusGain();
            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            int result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // could not get audio focus.
                stopSelf();
            }
        }

        /**
         * 可以监听系统的一些自定义事件
         */
        QWatch.setOnSystemEventListener(this);
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS ){
                stopSelf();
            } else if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                mPlayer.pause();
            } else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
                if(mPlayer.isRunning()) {
                    mPlayer.play();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Test");
        startForeground(FOREGROUND_ID, builder.build());
        requestPlayerWidget();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取到Launcher中的播放控件
     */
    private void requestPlayerWidget() {

        Song song = mPlayer.getPlaying();
        /**
         * 获取Launcher中的播放控件焦点
         */
        mWidgetPlayer = QWatch.requestPlayerFocus(this);

        /**
         * 更新Launcher中播放控件的信息
         */
        if(mWidgetPlayer != null) {
            mWidgetPlayer.updatePlayInfo(song.getTime(), 0, song.getTitle(), null);
        }

        mPlayer.setPlayCallback(new Player.PlayCallback() {
            @Override
            public void onCurrentPositionUpdate(int max, int position) {
                LogUtil.d(TAG, "onCurrentPositionUpdate:" + position);
                if(mWidgetPlayer != null) {
                    mWidgetPlayer.updateCurrentPosition(position);
                }
                for(StateReceiver receiver:mReceivers.values()){
                    receiver.onProgressChange(max, position);
                }
            }

            @Override
            public void onPlay() {
                if(mWidgetPlayer != null) {
                    mWidgetPlayer.updatePlayState(true);
                }
                for(StateReceiver receiver:mReceivers.values()){
                    receiver.onStateChange(true);
                }
            }

            @Override
            public void onStop() {
                if(mWidgetPlayer != null) {
                    mWidgetPlayer.updatePlayState(false);
                }
                for(StateReceiver receiver:mReceivers.values()){
                    receiver.onStateChange(false);
                }
            }

            @Override
            public void onPrepared() {
                if(mWidgetPlayer != null) {
                    mWidgetPlayer.updateTotalTime(mPlayer.getPlaying().getTime());
                }
                for(StateReceiver receiver:mReceivers.values()){
                    receiver.onSongChanged(mPlayer.getPlaying());
                }
            }

            @Override
            public void onSongChanged(Song song) {
                if(mWidgetPlayer != null) {
                    mWidgetPlayer.updatePlayInfo(song.getTime(), 0, song.getTitle(), null);
                }
                for(StateReceiver receiver:mReceivers.values()){
                    receiver.onSongChanged(song);
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mPlayerProxy;
    }

    @Override
    public void onPre() {
        LogUtil.d(TAG, "onPre.....");
        mPlayer.pre();
    }

    @Override
    public void onNext() {
        LogUtil.d(TAG, "onNext.....");
        mPlayer.next();
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "onPause.....");
        mPlayer.pause();
    }

    @Override
    public void onPlay() {
        LogUtil.d(TAG, "onPlay.....");
        mPlayer.play();
    }

    @Override
    public void onExit() {
        mPlayer.exit();
    }

    @Override
    public void onProgressUpdate(int i) {
        LogUtil.d(TAG, "onProgressUpdate.....");
        mPlayer.updatePosition(i);
    }

    @Override
    public void onWigetClick() {
        LogUtil.d(TAG, "onWigetClick.....");
    }

    @Override
    public void onEvent(int i) {
        if(SystemEvent.CODE_SYSTEM_EVENT_TEMP_HIGH_EXCEPTION == i){
            Toast.makeText(getApplicationContext(),"手表有点热了,休息一会吧",Toast.LENGTH_SHORT).show();
            mPlayer.pause();
        }
    }

    private class PlayerProxy extends IPlayer.Stub {

        @Override
        public void play() throws RemoteException {
            mPlayer.play();
        }

        @Override
        public void stop() throws RemoteException {
            mPlayer.pause();
        }

        @Override
        public void pre() throws RemoteException {
            mPlayer.pre();
        }

        @Override
        public void next() throws RemoteException {
            mPlayer.next();
        }

        @Override
        public void updateProgress(int progress) throws RemoteException {
            mPlayer.updatePosition(progress);
        }

        @Override
        public void registStateListener(IPlayStateListener listener) throws RemoteException {
            StateReceiver receiver = getReceiver(listener);
            if (receiver != null) {
                receiver.onStateChange(mPlayer.isPlaying());
                receiver.onProgressChange(mPlayer.getPlaying().getTime(), mPlayer.getProgress());
                receiver.onSongChanged(mPlayer.getPlaying());
            }
        }

        @Override
        public void unregistStateListener(IPlayStateListener listener) throws RemoteException {
            removeListener(listener);
        }
    }

    private void removeListener(IPlayStateListener listener) {
        if (listener != null) {
            StateReceiver receiver = mReceivers.remove(listener.asBinder());
            if (receiver != null) {
                receiver.unlinkToDeadth();
            }
        }
    }

    private StateReceiver getReceiver(IPlayStateListener listener) {
        if (listener != null) {
            IBinder binder = listener.asBinder();
            StateReceiver recever = mReceivers.get(binder);
            if (recever == null) {
                recever = new StateReceiver(listener);
                recever.linkToDeadth();
                mReceivers.put(listener.asBinder(), recever);
            }
            return recever;
        }
        return null;
    }

    private class StateReceiver implements IBinder.DeathRecipient, IPlayStateListener {

        private final IPlayStateListener mListener;
        private final Object mKey;

        public StateReceiver(IPlayStateListener listener) {
            mKey = listener.asBinder();
            this.mListener = listener;
        }

        @Override
        public void binderDied() {
            removeListener(mListener);
        }

        @Override
        public void onStateChange(boolean b) {
            if(mListener != null){
                try {
                    mListener.onStateChange(b);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    removeListener(mListener);
                }
            }
        }

        @Override
        public void onSongChanged(Song song) {
            if(mListener != null){
                try {
                    mListener.onSongChanged(song);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    removeListener(mListener);
                }
            }
        }

        @Override
        public void onProgressChange(int max, int progress){
            if(mListener != null){
                try {
                    mListener.onProgressChange(max, progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    removeListener(mListener);
                }
            }
        }

        @Override
        public IBinder asBinder() {
            return mListener.asBinder();
        }

        public void linkToDeadth() {
            try {
                mListener.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void unlinkToDeadth() {
            mListener.asBinder().unlinkToDeath(this, 0);
        }
    }

    @Override
    public void onDestroy() {
        mPlayer.exit();
        mReceivers.clear();
        mWidgetPlayer.releaseFocus();
        super.onDestroy();
    }
}
