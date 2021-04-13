package com.mainli;

import android.app.Application;
import android.content.Context;

import com.getkeepsafe.relinker.ReLinker;
import com.mainli.log.CrashHandler;
import com.mainli.log.DefaultErrorHandler;
import com.mainli.log.L;
import com.mainli.log.LogBuffer;
import com.seekting.demo_lib.DemoLib;
import com.tencent.mmkv.MMKV;

import java.io.File;



/**
 * Application基类
 * Created by shixiaoming on 16/12/6.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DemoLib.init(this);
        MyApplication.mContext = getApplicationContext();
        L.init(new File(getExternalCacheDir(), "logs"), 4028, false, new LogBuffer.LibLoader() {
            @Override
            public void loadLibrary(String libName) {
                ReLinker.loadLibrary(MyApplication.this, libName);
            }
        });
        CrashHandler.init(this, new DefaultErrorHandler());
        L.i("Mainli", "- - - 启动应用 - - -");
        //初始化key-value存储目录
        MMKV.initialize(new File(getExternalCacheDir(), "sharedP").getAbsolutePath(), new MMKV.LibLoader() {
            @Override
            public void loadLibrary(String libName) {
                ReLinker.recursively().loadLibrary(MyApplication.this, libName);
            }
        });
    }

    public static Context getAppContext() {
        return MyApplication.mContext;
    }
}
