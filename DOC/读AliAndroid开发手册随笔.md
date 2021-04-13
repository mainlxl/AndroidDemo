# 读阿里Android开发手册摘要
--------------------------------------------------------------------------------------------------------------
### **三、Android基本组件**
1. **Activity 间的数据通信，对于数据量比较大的，避免使用 Intent + Parcelable
的方式，可以考虑 EventBus 等替代方案，以免造成 TransactionTooLargeException。**

2. **Activity 间通过隐式 Intent 的跳转，在发出 Intent 之前必须通过 resolveActivity
检查，避免找不到合适的调用组件，造成 ActivityNotFoundException 的异常。**
    ```java
    public void viewUrl(String url, String mimeType) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), mimeType);
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_
                            ONLY) != null) {
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    if (Config.LOGD) {
                        Log.d(LOGTAG, "activity not found for "
                        + mimeType + " over "+Uri.parse(url).getScheme(), e);
                    }
                }
            }
        }
    ```
    
3. **避免在 Service#onStartCommand()/onBind()方法中执行耗时操作，如果确
实有需求，应改用 IntentService 或采用其他异步机制完成。**

4. **避免在 BroadcastReceiver#onReceive()中执行耗时操作，如果有耗时工作，
应该创建 IntentService 完成，而不应该在 BroadcastReceiver 内创建子线程去做。**<br/>
说明：
由于该方法是在主线程执行，如果执行耗时操作会导致 UI 不流畅。可以使用
IntentService 、 创 建 HandlerThread 或 者 调 用 Context#registerReceiver
(BroadcastReceiver, IntentFilter, String, Handler)方法等方式，在其他 Wroker 线程
执行 onReceive 方法。BroadcastReceiver#onReceive()方法耗时超过 10 秒钟，可
能会被系统杀死。

5. **当前Activity的onPause方法执行结束后才会执行下一个Activity的onCreate
方法，所以在 onPause 方法中不适合做耗时较长的工作，这会影响到页面之间的跳
转效率**

6. **Activity或者Fragment中动态注册BroadCastReceiver时，registerReceiver()和 unregisterReceiver()要成对出现。**
说明：
如果 registerReceiver()和 unregisterReceiver()不成对出现，则可能导致已经注册的
receiver 没有在合适的时机注销，导致内存泄漏，占用内存空间，加重 SystemService
负担。
部分华为的机型会对 receiver 进行资源管控，单个应用注册过多 receiver 会触发管
控模块抛出异常，应用直接崩溃。

7. **添加Fragment时，确保FragmentTransaction#commit()在
Activity#onPostResume()或者 FragmentActivity#onResumeFragments()内调用。
不要随意使用FragmentTransaction#commitAllowingStateLoss()来代替，任何
commitAllowingStateLoss()的使用必须经过 code review，确保无负面影响。**
<br/>说明：<br/>
Activity 可能因为各种原因被销毁，Android支持页面被销毁前通过
Activity#onSaveInstanceState()保存自己的状态。但如果
FragmentTransaction.commit()发生在 Activity 状态保存之后，就会导致 Activity 重
建、恢复状态时无法还原页面状态，从而可能出错。为了避免给用户造成不好的体
验，系统会抛出 IllegalStateExceptionStateLoss 异常。推荐的做法是在 Activity 的
onPostResume() 或 onResumeFragments() （ 对 FragmentActivity ） 里 执 行
FragmentTransaction.commit()，如有必要也可在 onCreate()里执行。不要随意改用
FragmentTransaction.commitAllowingStateLoss()或者直接使用 try-catch 避免
crash，这不是问题的根本解决之道，当且仅当你确认 Activity 重建、恢复状态时，
本次 commit 丢失不会造成影响时才可这么做。

8. **【推荐】**不要在 Activity#onDestroy()内执行释放资源的工作，例如一些工作线程的
销毁和停止，因为 onDestroy()执行的时机可能较晚。可根据实际需要，在
Activity#onPause()/onStop()中结合 isFinishing()的判断来执行。

9. **【推荐】如非必须，避免使用嵌套的 Fragment**。
<br/>说明：<br/>
嵌套 Fragment 是在 Android API 17 添加到 SDK 以及 Support 库中的功能，
Fragment 嵌套使用会有一些坑，容易出现 bug，比较常见的问题有如下几种：
1) onActivityResult()方法的处理错乱，内嵌的 Fragment 可能收不到该方法的回调，
需要由宿主 Fragment 进行转发处理；
2) 突变动画效果；
3) 被继承的 setRetainInstance()，导致在 Fragment 重建时多次触发不必要的逻
辑。
非必须的场景尽可能避免使用嵌套 Fragment，如需使用请注意上述问题。

10. Service 需要以多线程来并发处理多个启动请求，建议使用 IntentService，
可避免各种复杂的设置。
<br/>说明：<br/>
Service 组件一般运行主线程，应当避免耗时操作，如果有耗时操作应该在 Worker
线程执行。 可以使用 IntentService 执行后台任务。

11. 【推荐】对于只用于应用内的广播，优先使用 LocalBroadcastManager 来进行注册
和发送，LocalBroadcastManager 安全性更好，同时拥有更高的运行效率。
说明：
对于使用 Context#sendBroadcast()等方法发送全局广播的代码进行提示。如果该广
播仅用于应用内，则可以使用 ```LocalBroadcastManager.getInstance(context).sendBroadcast(intent);``` 来避免广播泄漏以及广播被
拦截等安全问题，同时相对全局广播本地广播的更高效。


### **四、UI与布局**

1. **布局中不得不使用 ViewGroup 多重嵌套时，不要使用 LinearLayout 嵌套，
改用 RelativeLayout，可以有效降低嵌套数。**<br/>
说明：
Android 应用页面上任何一个 View 都需要经过 measure、layout、draw 三个步骤
才能被正确的渲染。从 xml layout 的顶部节点开始进行 measure，每个子节点都需
要向自己的父节点提供自己的尺寸来决定展示的位置，在此过程中可能还会重新
measure（由此可能导致 measure 的时间消耗为原来的 **2-3** 倍）。节点所处位置越
深，套嵌带来的 measure 越多，计算就会越费时。这就是为什么扁平的 View 结构
会性能更好。
同时，页面拥上的 View 越多，measure、layout、draw 所花费的时间就越久。要缩
短这个时间，关键是保持 View 的树形结构尽量扁平，而且要移除所有不需要渲染的
View。理想情况下，总共的 measure，layout，draw 时间应该被很好的**控制在 16ms
以内，以保证滑动屏幕时 UI 的流畅。**
要找到那些多余的 View（增加渲染延迟的 view），可以用 Android Studio Monitor
里的 Hierarachy Viewer 工具，可视化的查看所有的 view。

2. **在 Activity 中显示对话框或弹出浮层时，尽量使用 DialogFragment，而非
Dialog/AlertDialog，这样便于随Activity生命周期管理对话框/弹出浮层的生命周期。**

3. **在需要时刻刷新某一区域的组件时，建议通过以下方式避免引发全局 layout刷新:**<br/>
    1) 设置固定的 view 大小的高宽，如倒计时组件等；<br/>
    2) 调用 view 的 layout 方式修改位置，如弹幕组件等；<br/>
    3) 通过修改 canvas 位置并且调用 invalidate(int l, int t, int r, int b)等方式限定刷新
    区域；<br/>
    4) 通过设置一个是否允许 requestLayout 的变量，然后重写控件的 requestlayout、
    onSizeChanged 方法 ， 判 断 控 件 的大小 没 有 改 变 的 情况下 ， 当 进 入
    requestLayout 的时候，直接返回而不调用 super 的 requestLayout 方法。
4. **【强制】不能使用 ScrollView 包裹 ListView/GridView/ExpandableListVIew;因为这
样会把 ListView 的所有 Item 都加载到内存中，要消耗巨大的内存和 cpu 去绘制图
面。**<br/>
说明：
ScrollView 中嵌套 List 或 RecyclerView 的做法官方明确禁止。除了开发过程中遇到
的各种视觉和交互问题，这种做法对性能也有较大损耗。ListView 等 UI 组件自身有
垂直滚动功能，也没有必要在嵌套一层 ScrollView。目前为了较好的 UI 体验，更贴
近 Material Design 的设计，推荐使用 NestedScrollView。

### **五、进程、线程与消息通信**

1. **【强制】**不要通过 Intent 在 Android 基础组件之间传递大数据（binder transaction
缓存为 1MB），可能导致 OOM。

2. **【强制】**新建线程时，必须通过线程池提供（AsyncTask 或者 ThreadPoolExecutor
或者其他形式自定义的线程池），不允许在应用中自行显式创建线程。<br/>
说明：
使用线程池的好处是减少在创建和销毁线程上所花的时间以及系统资源的开销，解
决资源不足的问题。如果不使用线程池，有可能造成系统创建大量同类线程而导致
消耗完内存或者“过度切换”的问题。另外创建匿名线程不便于后续的资源使用分析，
对性能分析等会造成困扰。

3. **【强制】**线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方
式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。<br/>
**Executors 返回的线程池对象的弊端如下**：<br/>
    1) FixedThreadPool 和 SingleThreadPool ： 允 许 的 请 求 队 列 长 度 为
    Integer.MAX_VALUE，可能会堆积大量的请求，从而导致 OOM；<br/>
    2) CachedThreadPool 和 ScheduledThreadPool ： 允 许的 创建线 程 数量 为
    Integer.MAX_VALUE，可能会创建大量的线程，从而导致 OOM。

4. 【强制】不要在非 UI 线程中初始化 ViewStub，否则会返回 null。

5.  ThreadPoolExecutor 设置线程存活时间(setKeepAliveTime)，确保空闲时
线程能被释放。

6.  禁 止 在多 进 程 之 间 用 SharedPreferences 共 享数 据 ， 虽 然 可 以
(MODE_MULTI_PROCESS)，但官方已不推荐。

7. **谨慎使用 Android 的多进程，多进程虽然能够降低主进程的内存压力，但会
遇到如下问题：**<br/>
    1) 不能实现完全退出所有 Activity 的功能；<br/>
    2) 首次进入新启动进程的页面时会有延时的现象（有可能黑屏、白屏几秒，是白
    屏还是黑屏和新 Activity 的主题有关）；<br/>
    3) 应用内多进程时，Application 实例化多次，需要考虑各个模块是否都需要在所
    有进程中初始化；<br/>
    4) 多进程间通过 SharedPreferences 共享数据时不稳定。
    
### **六、文件与数据库**
1. **【强制】**任何时候不要硬编码文件路径，请使用 Android 文件系统 API 访问。
    说明：
    Android 应用提供内部和外部存储，分别用于存放应用自身数据以及应用产生的用
    户数据。可以通过相关 API 接口获取对应的目录，进行文件操作。
    android.os.Environment#getExternalStorageDirectory()
    android.os.Environment#getExternalStoragePublicDirectory()
    android.content.Context#getFilesDir()
    android.content.Context#getCacheDir
    
2. 【强制】应用间共享文件时，不要通过放宽文件系统权限的方式去实现，而应使用
FileProvider。
正例：
    ```java
        <!-- AndroidManifest.xml -->
<manifest>
...
<application>
    ...
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.example.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/provider_paths"/>
    </provider>
    ...
</application>
</manifest>
    <!-- res/xml/provider_paths.xml -->
<paths>
<files-path
    path="album/"
    name="myimages"/>
</paths>
    
     void getAlbumImage(String imagePath) {
        File image = new File(imagePath);
        Intent getAlbumImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = FileProvider.getUriForFile(this,
        "com.example.provider",
        image);
        getAlbumImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takePhotoIntent, REQUEST_GET_ALBUMIMAGE);
    }
    ```
    
3. SharedPreference 提交数据时，尽量使用 Editor#apply() ， 而非
Editor#commit()。一般来讲，仅当需要确定提交结果，并据此有后续操作时，才使
用Editor#commit()。<br/>
说明：
SharedPreference 相关修改使用 **apply 方法进行提交会先写入内存，然后异步写入
磁盘**，commit 方法是直接写入磁盘。如果频繁操作的话 apply 的性能会优于 commit，
apply 会将最后修改内容写入磁盘。但是如果希望立刻获取存储操作的结果，并据此
做相应的其他操作，应当使用 commit。

4. 多线程操作写入数据库时，需要使用事务，以免出现同步问题。
说明：
Android 的通过 SQLiteOpenHelper 获取数据库 SQLiteDatabase 实例，Helper 中会
自动缓存已经打开的 SQLiteDatabase 实例，单个 App 中应使用 SQLiteOpenHelper
的单例模式确保数据库连接唯一。由于 SQLite 自身是数据库级锁，单个数据库操作
是保证线程安全的（不能同时写入），transaction 时一次原子操作，因此处于事务中
的操作是线程安全的。
若同时打开多个数据库连接，并通过多线程写入数据库，会导致数据库异常，提示
数据库已被锁住。
正例：
    ```java
        public void insertUserPhoto(SQLiteDatabase db, String userId, String content){
            ContentValues cv = new ContentValues();
            cv.put("userId", userId);
            cv.put("content", content);
            db.beginTransaction();
            try {
                db.insert(TUserPhoto, null, cv);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                // TODO
            } finally {
                db.endTransaction();
            }
        }
```

5. 大数据写入数据库时，请使用事务或其他能够提高 I/O 效率的机制，保证执
行速度。
正例：
    ```java
      public void insertBulk(SQLiteDatabase db, ArrayList<UserInfo> users){
            db.beginTransaction();
            try {
                for (int i = 0; i < users.size; i++) {
                    ContentValues cv = new ContentValues();
                    cv.put("userId", users[i].userId);
                    cv.put("content", users[i].content);
                    db.insert(TUserPhoto, null, cv);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                // TODO
            } finally {
                db.endTransaction();
            }
        }
    ```

6. **【强制】**执行 SQL 语句时，应使用 SQLiteDatabase#insert()、update()、delete()，
不要使用 SQLiteDatabase#execSQL()，以免 SQL 注入风险。
正例：
    ```java
    public int updateUserPhoto(SQLiteDatabase db, String userId, String content) {
            ContentValues cv = new ContentValues();
            cv.put("content", content);
            String[] args = {String.valueOf(userId)};
            return db.update(TUserPhoto, cv, "userId=?", args);
        }
    ```

7. **【强制】**如果 ContentProvider 管理的数据存储在 SQL 数据库中，应该避免将不受
信任的外部数据直接拼接在原始 SQL 语句中，可使用一个用于将 ? 作为可替换参
数的选择子句以及一个单独的选择参数数组，会避免 SQL 注入。

    ```javascrip
    // 使用一个可替换参数
    String mSelectionClause = "var = ?";
    String[] selectionArgs = {""};
    selectionArgs[0] = mUserInput;
    ```
    反例:
    ```javascrip
    //拼接用户输入内容和列名
    String mSelectionClause = "var = " + mUserInput;
```

### **七、Bitmap、Drawable 与动画**
1. **【强制】**加载大图片或者一次性加载多张图片，应该在异步线程中进行。图片的加
载，涉及到 IO 操作，以及 CPU 密集操作，很可能引起卡顿。

    ```java
        class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
            ...
            // 在后台进行图片解码
            @Override
            protected Bitmap doInBackground(Integer... params) {
                final Bitmap bitmap = BitmapFactory.decodeFile("some path");
                return bitmap;
            }
            ...
        }
    ```

2. 应根据实际展示需要，压缩图片，而不是直接显示原图。手机屏幕比较小，
直接显示原图，并不会增加视觉上的收益，但是却会耗费大量宝贵的内存。
    ```java
      public static Bitmap decodeSampledBitmapFromResource(Resources res,
       int resId,int reqWidth, int reqHeight) {
            //首先通过inJustDecodeBounds=true获得图片的尺寸
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            // 然后根据图片分辨率以及我们实际需要展示的大小，计算压缩率
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // 设置压缩率，并解码
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }
    ```
3. 针对不同的屏幕密度，提供对应的图片资源，使内存占用和显示效果达到
合理的平衡。如果为了节省包体积，可以在不影响 UI 效果的前提下，省略低密度图
片。

4. **使用 inBitmap 重复利用内存空间，避免重复开辟新内存。**
    ```java
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, ImageCache cache) {
        final BitmapFactory . Options options = new BitmapFactory . Options ();
        ...
        BitmapFactory.decodeFile(filename, options);
        ...
    // 如果在 Honeycomb 或更新版本系统中运行，尝试使用 inBitmap
        if (Utils.hasHoneycomb()) {
            addInBitmapOptions(options, cache);
        }
        ...
        return BitmapFactory.decodeFile(filename, options);
    }
    //--------------------------------------------------------------
    private static void addInBitmapOptions(BitmapFactory.Options options,ImageCache cache) {
    // inBitmap 只处理可变的位图，所以强制返回可变的位图
        options.inMutable = true;
        if (cache != null) {
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
    }
    ```
    
5. 使用 ARGB_565 代替 ARGB_888，在不怎么降低视觉效果的前提下，减少内存占用。
<br/>说明：android.graphics.Bitmap.Config 类中关于图片颜色的存储方式定义：<br/>
1) ALPHA_8 代表 8 位 Alpha 位图；<br/>
2) ARGB_4444 代表 16 位 ARGB 位图；<br/>
3) ARGB_8888 代表 32 位 ARGB 位图；<br/>
4) RGB_565 代表 8 位 RGB 位图。<br/>
位图位数越高，存储的颜色信息越多，图像也就越逼真。大多数场景使用的是
ARGB_8888 和 RGB_565，RGB_565 能够在保证图片质量的情况下大大减少内存
的开销，是解决 oom 的一种方法。
但是一定要注意 RGB_565 是没有透明度的，如果图片本身需要保留透明度，那么
就不能使用 RGB_565。
    ```java
    Config config = drawableSave.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 :
    Config.RGB_565;
    Bitmap bitmap = Bitmap.createBitmap(w, h, config);
    ```
6. 尽量减少 Bitmap （BitmapDrawable）的使用，尽量使用纯色（ColorDrawable）、
渐变色（GradientDrawable）、StateSelector（StateListDrawable）等与 Shape 结
合的形式构建绘图。

7. 谨慎使用 gif 图片，注意限制每个页面允许同时播放的 gif 图片，以及单个
gif 图片的大小。

8. 在有强依赖 onAnimationEnd 回调的交互时，如动画播放完毕才能操作页
面 ， onAnimationEnd 可 能 会 因 各 种 异 常 没 被 回 调 （ 参 考 ：
https://stackoverflow.com/questions/5474923/onanimationend-is-not-getting-calle
d-onanimationstart-works-fine），建议加上超时保护或通过 postDelay 替代
onAnimationEnd。
    ```java
    View v = findViewById(R.id.xxxViewID);
    final FadeUpAnimation anim = new FadeUpAnimation(v);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.setDuration(1000);
    anim.setFillAfter(true);
    new Handler().postDelayed(new Runnable() {
        public void run() {
            if (v != null) {
                v.clearAnimation();
            }
        }
    }, anim.getDuration());
    v.startAnimation(anim);
    ```
    
9. **当View Animation 执行结束时，调用 View.clearAnimation()释放相关资源。**
  
    ```java
    View v = findViewById(R.id.xxxViewID);
    final FadeUpAnimation anim = new FadeUpAnimation(v);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.setDuration(1000);
    anim.setFillAfter(true);
    anim.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationEnd(Animation arg0) {
            //判断一下资源是否被释放了
            if (v != null) {
                v.clearAnimation();
            }
        }
    });
    v.startAnimation(anim);
    ```
    
### **八、安全**
1. 【强制】使用 PendingIntent 时，禁止使用空 intent，同时禁止使用隐式 Intent
<br/>说明：<br/>
1) 使用 PendingIntent 时，使用了空 Intent,会导致恶意用户劫持修改 Intent 的内
容。禁止使用一个空 Intent 去构造 PendingIntent，构造 PendingIntent 的 Intent
一定要设置 ComponentName 或者 action。<br/>
2) PendingIntent 可以让其他 APP 中的代码像是运行自己 APP 中。PendingIntent
的intent接收方在使用该intent时与发送方有相同的权限。在使用PendingIntent
时，PendingIntent 中包装的 intent 如果是隐式的 Intent，容易遭到劫持，导致
信息泄露。<br/>

2. **【强制】**将 android:allowbackup 属性设置为 false，防止 adb backup 导出数据。
说明：
在 AndroidManifest.xml 文件中为了方便对程序数据的备份和恢复在 Android API
level 8 以后增加了 android:allowBackup 属性值。**默认情况下这个属性值为 true**,故
当 allowBackup 标志值为 true 时，即可通过 adb backup 和 adb restore 来备份和恢
复应用程序数据。

3. **【强制】**Receiver/Provider 不能在毫无权限控制的情况下，将 android:export 设置
为 true。

4. 使用 Intent Scheme URL 需要做过滤。如果浏览器支持 Intent Scheme Uri 语法，如果过滤不当，那么恶意用户可能通过浏
览器 js 代码进行一些恶意行为，比如盗取 cookie 等。如果使用了 Intent.parseUri
函 数 ， 获 取 的 intent 必 须 严 格 过 滤 ， intent 至 少 包 含
addCategory(“android.intent.category.BROWSABLE”) ， setComponent(null) ，
setSelector(null)3 个策略。
    正例：
    ```java
    // 将 intent scheme URL 转换为 intent 对象
    Intent intent = Intent.parseUri(uri);
    // 禁止没有 BROWSABLE category 的情况下启动 activity
    intent.addCategory("android.intent.category.BROWSABLE");
    intent.setComponent(null);
    intent.setSelector(null);
    // 使用 intent 启动 activity
    context.startActivityIfNeeded(intent, -1)
    ```
    反例：
    ```java
    Intent intent = Intent.parseUri(uri.toString().trim().substring(15), 0);
    intent.addCategory("android.intent.category.BROWSABLE");
    context.startActivity(intent);
    ```
    扩展参考：
    ```java
    1) https://jaq.alibaba.com/community/art/show?articleid=265
    2) https://www.mbsd.jp/Whitepaper/IntentScheme.pdf
    ```
    
5. **【强制】**将所需要动态加载的文件放置在 apk 内部，或应用私有目录中，如果应用
必须要把所加载的文件放置在可被其他应用读写的目录中(比如 sdcard)，建议对不
可信的加载源进行完整性校验和白名单处理，以保证不被恶意代码注入。

6. **【强制】**使用 Android 的 AES/DES/DESede 加密算法时，不要使用默认的加密模式
ECB，应显示指定使用 CBC 或 CFB 加密模式。
<br/>说明：<br/>
加密模式 ECB、CBC、CFB、OFB 等，其中 ECB 的安全性较弱，会使相同的铭文
在不同的时候产生相同的密文，容易遇到字典攻击，建议使用 CBC 或 CFB 模式。<br/>
1) ECB：Electronic codebook，电子密码本模式<br/>
2) CBC：Cipher-block chaining，密码分组链接模式<br/>
3) CFB：Cipher feedback，密文反馈模式<br/>
4) OFB：Output feedback，输出反馈模式<br/>

7. 对于不需要使用 File 协议的应用，禁用 File 协议，显式设置 webView.
getSettings().setAllowFileAccess(false)，对于需要使用 File 协议的应用，禁止 File
协议调用 JavaScript，显式设置 webView.getSettings().setJavaScriptEnabled(false)。

8. Android5.0 以后安全性要求较高的应用应该使用 window.setFlag
(LayoutParam.FLAG_SECURE) 禁止录屏。