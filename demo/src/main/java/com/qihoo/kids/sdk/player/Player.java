package com.qihoo.kids.sdk.player;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;

import com.qihoo.kids.sdk.util.LogUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Player implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = Player.class.getSimpleName();

    private static final int STATE_IDLE = 0;
    private static final int REFRESH_PROGRESS = 123;
    private static final int UPDATE_STATE_PLAY = 124;
    private static final int UPDATE_STATE_STOP = 125;
    private static final int UPDATE_STATE_PREPARED = 126;
    private static final int UPDATE_STATE_LOADING = 127;
    private final PlayList mPlayList;
    public MediaPlayer mediaPlayer; // 媒体播放器
    private PlayCallback playCallback;
    private Song mSong;
    private Song mLast;
    private static Executor executor = Executors.newFixedThreadPool(3);
    private int mState = STATE_IDLE;

    public Player() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayList = new PlayList();
    }

    private void updateProgress() {
        if (mediaPlayer == null) {
            return;
        }
        handler.sendEmptyMessage(REFRESH_PROGRESS); // 发送消息
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    handler.removeMessages(REFRESH_PROGRESS);
                    if ((mState == UPDATE_STATE_PLAY||mState==UPDATE_STATE_PLAY) && getPlaying().ismPrepared()) {
                        int position = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        if (duration > 0 && position <= duration) {
                            if (playCallback != null) {
                                playCallback.onCurrentPositionUpdate(duration, position);
                            }
                            if(position != duration) {
                                handler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 1000);
                            } else {
//                                next();
                            }
                        }
                    }
                    break;
                case UPDATE_STATE_PLAY:
                    mState = UPDATE_STATE_PLAY;
                    if (playCallback != null) {
                        playCallback.onPlay();
                    }
                    updateSongChange();
                    updateProgress();
                    break;
                case UPDATE_STATE_STOP:
                    mState = UPDATE_STATE_STOP;
                    if (playCallback != null) {
                        playCallback.onStop();
                    }
                    break;
                case UPDATE_STATE_LOADING:
                    mState = UPDATE_STATE_LOADING;
                    if (playCallback != null) {
                        playCallback.onPlay();
                    }
                    break;
                case STATE_IDLE:
                    mState = STATE_IDLE;
                    if (playCallback != null) {
                        playCallback.onStop();
                    }
                    break;
                case UPDATE_STATE_PREPARED:
                    mState = UPDATE_STATE_PREPARED;
                    if (playCallback != null) {
                        playCallback.onPrepared();
                    }
                    break;
            }
        }
    };

    private void updateSongChange() {
        if (mSong != mLast) {
            if (playCallback != null) {
                playCallback.onSongChanged(mSong);
            }
            mLast = mSong;
        }
    }

    public Song getPlaying() {
        //获取当前播放音乐信息
        if (mSong == null) {
            next();
        }
        return mSong;
    }


    public void next() {
        mSong = mPlayList.next();
        prepareSource();
        if(mState == STATE_IDLE){
            mState = UPDATE_STATE_STOP;
        }
        updateSongChange();
    }

    private void prepareSource() {
        try {
            if (mSong != null) {
                handler.removeMessages(REFRESH_PROGRESS);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mSong.setPrepared(false);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mSong.getUrl());
                mediaPlayer.prepareAsync();
                if (playCallback != null) {
                    playCallback.onCurrentPositionUpdate(0, 0);
                }
                LogUtil.d(TAG, "prepareSource end....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pause();
        }
    }

    public void play() {
        if (mState == STATE_IDLE) {
            next();
        } else if (mState == UPDATE_STATE_STOP) {
            if (!mediaPlayer.isPlaying()&&getPlaying().ismPrepared()) {
                mediaPlayer.start();
            } else {
                prepareSource();
            }
        }
        switchState(UPDATE_STATE_PLAY);
    }

    private void switchState(int state) {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(state);
    }

    public void pre() {
        mSong = mPlayList.pre();
        prepareSource();
        updateSongChange();
        if(mState == STATE_IDLE){
            mState = UPDATE_STATE_STOP;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void updatePosition(int i) {
        if (mediaPlayer.isPlaying() || mState == UPDATE_STATE_PREPARED || mState == UPDATE_STATE_PLAY) {
            mediaPlayer.seekTo(i);
            updateProgress();
        } else {
            if (playCallback != null) {
                playCallback.onCurrentPositionUpdate(getPlaying().getTime(), 0);
            }
        }
    }

    public int getProgress() {
        if (mState == UPDATE_STATE_PLAY || mState == UPDATE_STATE_PREPARED) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isRunning() {
        return mState != STATE_IDLE;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.d(TAG, "onError:"+what+";extra:"+extra);
        next();
        return false;
    }

    public interface PlayCallback {
        void onCurrentPositionUpdate(int max, int position);

        void onPlay();

        void onStop();

        void onPrepared();

        void onSongChanged(Song song);
    }

    public void setPlayCallback(PlayCallback update) {
        this.playCallback = update;
    }

    // 暂停
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (mState == UPDATE_STATE_PLAY) {
            switchState(UPDATE_STATE_STOP);
        }
    }

    // 停止
    public void exit() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        switchState(STATE_IDLE);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("mediaPlayer", "onPrepared mState:"+mState);
        getPlaying().setTime(mp.getDuration());
        mSong.setPrepared(true);

        if (mState == UPDATE_STATE_PLAY || mState == UPDATE_STATE_PREPARED) {
            switchState(UPDATE_STATE_PLAY);
            mp.start();
            updateProgress();
        }
        updateSongChange();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
        if (mState == UPDATE_STATE_PLAY) {
            next();
        }
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

}