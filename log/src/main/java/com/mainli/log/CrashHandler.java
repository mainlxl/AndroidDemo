package com.mainli.log;

import android.content.Context;
import android.content.Intent;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private ErrorHandler mErrorLogSave;

    public CrashHandler(Context context, Thread.UncaughtExceptionHandler defaultHandler, ErrorHandler errorHandler) {
        mContext = context;
        mDefaultHandler = defaultHandler;
        mErrorLogSave = errorHandler;
    }

    public static void init(Context context, ErrorHandler errorHandler) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, Thread.getDefaultUncaughtExceptionHandler(), errorHandler));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread, ex)) {
            // 未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
        } else {
            // 已经人为处理,系统自己退出
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            startApp(mContext);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void startApp(Context context) {
        if (context != null) {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(launchIntentForPackage);
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {// 异常是否为空
            return false;
        }
//        object : Thread() {
//            // 在主线程中弹出提示
//            override fun run() {
//                Looper.prepare()
//                Toast.makeText(mContext, "程序发生未知异常，将重启。", Toast.LENGTH_SHORT).show()
//                Looper.loop()
//            }
//        }.start()

        return  mErrorLogSave.errorHandler(thread, ex);
    }

    public interface ErrorHandler {
        boolean errorHandler(Thread thread, Throwable t);
    }


}
