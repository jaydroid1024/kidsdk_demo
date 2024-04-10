

## 一，引入工程



### 方式一（仅v0.0.6版本）:
(1)在工程的根目录下面的build.gradle中配置maven路径:
```java

...
    allprojects {
        repositories {
            google()
            jcenter()
            maven { url "http://maven.geelib.360.cn/nexus/repository/QihooWear/" }
        }
    }
```

(2)在module中的build.gradle中进行引入:

```java
    ...
    dependencies {
        ...
        implementation 'qihoo.kids:watch:0.0.6'
        ...
    }
```


### 方式二

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
    //1. 全量依赖包引入
    implementation(name: 'watch-0.0.7', ext: 'aar')

    //2. 单独引入主SDK和依赖包，用以解决依赖冲突问题
    implementation(name: 'watch-0.0.7-single', ext: 'aar')
    implementation(name: 'PersistentConnManager', ext: 'jar')
    implementation(name: 'QihooLoggerManager', ext: 'jar')
    implementation(name: 'QOpenSdkLib_AppSDKBaseLib', ext: 'jar')
    ...
}
```
上面的两种AAR 依赖方式人选其一

(3)混淆配置

```java
-keep class qihoo.** { *; }
-keep class com.qihoo.** { *; }
-dontwarn qihoo.**
-dontwarn com.qihoo.**
```





## 二，WidgetPlayer



调用WidgetPlayer UI实现可以在后台播放的demo

### 1.初始化


在Application onCreate的时候调用:

```java
QWatch.init(this);
```
### 2.WidgetPlayer调用


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





## 三，获取后台免杀权（v0.0.7之后支持）

调用 **setAllowAppIfBackground（true）** 实现可以退出后台后不被系统查杀，

### 1.初始化

在Application onCreate的时候调用:

```java
QWatch.init(this);
```



### 2.申请后台免杀权

```java
QWatch.setAllowAppIfBackground(true);
```



### 3.释放后台免杀权

```java
QWatch.setAllowAppIfBackground(false);
```



**特别提醒：请严格按照规范使用该权利，仅在需要时授权，用完及时释放，否则经内部审核未按照规范使用，应用会被强制下架**



### 4.以选相册为例测试代码

```java
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
```



