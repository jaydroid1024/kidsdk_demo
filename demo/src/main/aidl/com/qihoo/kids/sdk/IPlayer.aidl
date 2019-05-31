// IPlayer.aidl
package com.qihoo.kids.sdk;
import com.qihoo.kids.sdk.IPlayStateListener;

interface IPlayer {
    void play();
    void stop();
    void pre();
    void next();
    void updateProgress(int progress);
    void registStateListener(IPlayStateListener listener);
    void unregistStateListener(IPlayStateListener listener);
}
