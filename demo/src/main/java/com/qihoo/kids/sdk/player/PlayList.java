package com.qihoo.kids.sdk.player;

import java.util.ArrayList;

public class PlayList {

    ArrayList<Song> mList = new ArrayList<>();
    int mIndex;

    public PlayList(){
        mList.add(new Song("http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3", "当你老了", 48*1000));
        mList.add(new Song("http://music.163.com/song/media/outer/url?id=281951.mp3", "我曾用心爱着你", 4*60*1000+25*1000));
        mList.add(new Song("http://music.163.com/song/media/outer/url?id=299601.mp3", "笑忘书", 2*60*1000+5*1000));
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
