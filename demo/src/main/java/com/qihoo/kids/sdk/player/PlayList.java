package com.qihoo.kids.sdk.player;

import java.util.ArrayList;

public class PlayList {

    ArrayList<Song> mList = new ArrayList<>();
    int mIndex;

    public PlayList(){
        mList.add(new Song("http://music.163.com/song/media/outer/url?id=447925558.mp3", "DJ测试音乐1111", 35*1000));
        mList.add(new Song("http://music.163.com/song/media/outer/url?id=447925558.mp3", "DJ测试音乐2222", 35*1000));
        mList.add(new Song("http://music.163.com/song/media/outer/url?id=447925558.mp3", "DJ测试音乐3333", 35*1000));
    }


    public Song next(){
        mIndex++;
        return mList.get(mIndex%mList.size());
    }

    public Song pre() {
        mIndex--;
        if(mIndex < 0){
            mIndex += mList.size();
        }
        return mList.get(mIndex%mList.size());
    }

}
