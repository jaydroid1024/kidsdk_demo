package com.qihoo.kids.sdk.player;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String url;
    private String title;

    private int time;
    private boolean mPrepared;

    protected Song(Parcel in) {
        url = in.readString();
        title = in.readString();
        time = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Song(String url, String title, int time) {
        this.url = url;
        this.title = title;
        this.time = time;
    }

    public Song() {
    }

    public int getTime() {
        return time;
    }

    public void setTime(int duration) {
        time = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeInt(time);
    }

    public void readFromParcel(Parcel in) {
        url = in.readString();
        title = in.readString();
        time = in.readInt();
    }

    public void setPrepared(boolean b) {
        mPrepared = b;
    }

    public boolean ismPrepared(){
        return mPrepared;
    }

    @Override
    public String toString() {
        return "Song{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", mPrepared=" + mPrepared +
                '}';
    }
}