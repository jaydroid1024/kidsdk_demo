WidgetPlayer
=

调用WidgetPlayer UI实现可以在后台播放的demo

1.引入工程
-
(1)将qwatchlib-release.aar放到工程目录对应的lib目录下
(2)配置build.gradle进行引入

```java
repositories {
    flatDir {
        dirs ('libs')
    }
}

dependencies {
    ...
    api(name: 'qwatchlib', ext: 'aar')
    ...
}
```
2.初始化
-
在Application onCreate的时候调用:
```java
QWatch.init(this);
```
3.WidgetPlayer调用
-
WidgetPlayer是一个在桌面上的一个播放展示控件,app后台播放媒体音乐时要获取到WidgetPlayer的焦点,这样可以在桌面上看到后台播放媒体信息,进行播放控制等操作.
调用方式:
```java

...
/**
 * 获取Launcher中的播放控件焦点
*/
mWidgetPlayer = QWatch.requestPlayerFocus(mWidgetEventListener);
/**
* 更新Launcher中播放控件中展示的信息
* @param totalTime, 媒体音乐的总时长,单位:毫秒
* @param curPosition 当前播放的位置,默认从0开始,单位:毫秒
* @param title 媒体名称信息
* @param bitmap 在控件中展示的相关icon信息,可以传null
*/
if(mWidgetPlayer != null){
    mWidgetPlayer.updatePlayInfo(int totalTime, int curPosition, String title, Bitmap bitmap);
}
        
...

//更新当前播放进度,position为当前播放的位置,比如:mediaPlayer.getCurrentPosition()
mWidgetPlayer.updateCurrentPosition(position);

//更新播放控件的播放状态,true:当前为播放状态,false:当前为暂停状态.
mWidgetPlayer.updatePlayState(true);

//更新音频文件的总时长
mWidgetPlayer.updateTotalTime(mPlayer.getPlaying().getTime());

//更新展示的title信息
mWidgetPlayer.updateTitle(title);


/**
 *WidgetPlayer相关事件回调
 */
private OnPlayWidgetEventListener mWidgetEventListener = new OnPlayWidgetEventListener(){

        /*
         * 用户点击了"上一首"
         */
        @Override
        public void onPre() {

        }

        /*
         * 用户点击了"下一首"
         */
        @Override
        public void onNext() {

        }

        /*
         * 用户点击了"暂停"
         */
        @Override
        public void onPause() {

        }

        /*
         * 用户点击了"播放"
         */
        @Override
        public void onPlay() {

        }

        /*
         * 用户点击了"退出播放"
         */
        @Override
        public void onExit() {

        }
        /*
         * 用户拖动的进度条
         * @param i 播放的当前时间,给播放器调用,比如:mediaPlayer.seekTo(i)
         */
        @Override
        public void onProgressUpdate(int i) {

        }
  
       
    };


```
