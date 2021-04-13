package com.mainli.log;

import android.util.Log;

/**
 * Created by pqpo on 2017/11/16.
 * 简化来自https://github.com/pqpo/Log4a项目mmap封装
 */
public class LogBuffer {

    private static final String TAG = "LogBuffer";

    private long ptr = 0;
    private String logPath;
    private String bufferPath;
    private int bufferSize;
    private boolean compress;
    private LibLoader libLoader;

    public interface LibLoader {
        void loadLibrary(String libName);
    }

    public LogBuffer(String bufferPath, int capacity, String logPath, boolean compress, LibLoader libLoader) {
        if (libLoader != null) {
            libLoader.loadLibrary("log4a-lib");
        } else {
            System.loadLibrary("log4a-lib");
        }
        this.libLoader = libLoader;
        this.bufferPath = bufferPath;
        this.bufferSize = capacity;
        this.logPath = logPath;
        this.compress = compress;
        try {
            ptr = initNative(bufferPath, capacity, logPath, compress);
        } catch (Exception e) {
            Log.e(TAG, L.getStackTraceString(e));
        }
    }

    public void changeLogPath(String logPath) {
        if (ptr != 0) {
            try {
                changeLogPathNative(ptr, logPath);
                this.logPath = logPath;
            } catch (Exception e) {
                Log.e(TAG, L.getStackTraceString(e));
            }
        }
    }

    public boolean isCompress() {
        return compress;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getBufferPath() {
        return bufferPath;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void write(String log) {
        if (ptr != 0) {
            try {
                writeNative(ptr, log);
            } catch (Exception e) {
                Log.e(TAG, L.getStackTraceString(e));
            }
        }
    }

    public void flushAsync() {
        if (ptr != 0) {
            try {
                flushAsyncNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, L.getStackTraceString(e));
            }
        }
    }

    public void release() {
        if (ptr != 0) {
            try {
                releaseNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, L.getStackTraceString(e));
            }
            ptr = 0;
        }
    }

    private native static long initNative(String bufferPath, int capacity, String logPath, boolean compress);

    private native void writeNative(long ptr, String log);

    private native void flushAsyncNative(long ptr);

    private native void releaseNative(long ptr);

    private native void changeLogPathNative(long ptr, String logPath);

}
