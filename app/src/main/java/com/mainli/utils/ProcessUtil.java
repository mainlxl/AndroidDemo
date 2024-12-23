package com.mainli.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class ProcessUtil {
    private static String currentProcessName;

    /**
     * @return 当前进程名
     */
    @Nullable
    public static String getCurrentProcessName(@NonNull Context context) {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            currentProcessName = Application.getProcessName();
        } else {//28(Build.VERSION_CODES.P)以下刚好没限制反射
            // android 4.3_r2.1(18 Build.VERSION_CODES.JELLY_BEAN_MR2)以上
            try {
                final Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader()).getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
                declaredMethod.setAccessible(true);
                final Object invoke = declaredMethod.invoke(null, new Object[0]);
                if (invoke instanceof String) {
                    currentProcessName = (String) invoke;
                }
            } catch (Throwable e) {
            }
        }
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }
        if (context == null) {
            return null;
        }
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
            if (runningAppList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppList) {
                    if (processInfo.pid == pid) {
                        currentProcessName = processInfo.processName;
                    }
                }
            }
        }
        return currentProcessName;
    }

}