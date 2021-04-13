package com.mainli.log;

import android.os.Build;
import android.util.Log;

import java.util.Arrays;

public class DefaultErrorHandler implements CrashHandler.ErrorHandler {
    private StringBuilder mPrefixInfo = null;
    private int headLenght = 0;

    @Override
    public boolean errorHandler(Thread thread, Throwable t) {
        if (mPrefixInfo == null) {
            mPrefixInfo = new StringBuilder().append("\n***********************************\n")//
                    .append("versionName: ").append(BuildConfig.VERSION_NAME).append('\n')//
                    .append("versionCode: ").append(BuildConfig.VERSION_CODE).append('\n')//
                    .append("手机品牌: ").append(android.os.Build.BRAND).append('\n')//
                    .append("手机型号: ").append(android.os.Build.MODEL).append('\n')//
                    .append("Android-Version-Code: ").append(Build.VERSION.RELEASE).append('\n')//
                    .append("CPU-ABI: ");
            if (Build.VERSION.SDK_INT < 21) {
                mPrefixInfo.append(Arrays.asList(android.os.Build.CPU_ABI, android.os.Build.CPU_ABI2));
            } else {
                mPrefixInfo.append(Arrays.toString(android.os.Build.SUPPORTED_ABIS));
            }
            headLenght = mPrefixInfo.length();
        }
        if (mPrefixInfo.length() > headLenght) {
            mPrefixInfo.delete(headLenght, mPrefixInfo.length());
        }
        mPrefixInfo.append("\nCrash-Thread-Name: ");
        mPrefixInfo.append(thread.getName()).append('\n');
        mPrefixInfo.append(Log.getStackTraceString(t));
        mPrefixInfo.append("\n***********************************\n");
        L.e("CrashHandler", mPrefixInfo.toString());
        return false;
    }
}
