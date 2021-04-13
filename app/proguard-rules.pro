# 指定一个文本文件用来生成混淆后的名字。默认情况下，混淆后的名字一般为 a、b、c 这种。
# 通过使用配置的字典文件，可以使用一些非英文字符做为类名。成员变量名、方法名。字典文件中的空格，标点符号，重复的词，还有以'#'开头的行都会被忽略。
# 需要注意的是添加了字典并不会显著提高混淆的效果，只不过是更不利与人类的阅读。正常的编译器会自动处理他们，并且输出出来的jar包也可以轻易的换个字典再重新混淆一次。
# 最有用的做法一般是选择已经在类文件中存在的字符串做字典，这样可以稍微压缩包的体积。
# 查找了字典文件的格式：一行一个单词，空行忽略，重复忽略
-obfuscationdictionary proguard-dictionary.txt

# 指定一个混淆类名的字典，字典格式与 -obfuscationdictionary 相同
-classobfuscationdictionary proguard-dictionary.txt
# 指定一个混淆包名的字典，字典格式与 -obfuscationdictionary 相同
-packageobfuscationdictionary proguard-dictionary.txt

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保留日志类
-keep class com.mainli.log.LogBuffer{
    native <methods>;
}

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# okhttp
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Gson
-keep class com.example.testing.retrofitdemo.bean.**{*;} # 自定义数据模型的bean目录

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#MMKV
-keepclasseswithmembers,includedescriptorclasses class com.tencent.mmkv.** {
    native <methods>;
    long nativeHandle;
    private static *** onMMKVCRCCheckFail(***);
    private static *** onMMKVFileLengthError(***);
    private static *** mmkvLogImp(...);
}

#EventBus - START
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#EventBus - END

#flutter
-keep class io.flutter.** {*;}
-dontwarn io.flutter.**


