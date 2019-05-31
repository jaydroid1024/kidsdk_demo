package com.qihoo.kids.sdk;
import com.qihoo.kids.sdk.player.Song;

interface IPlayStateListener {

    void onStateChange(boolean b);

    void onSongChanged(out Song song);

    void onProgressChange(int max, int progress);
}
