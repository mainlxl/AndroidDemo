package com.mainli.log;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;


/**
 * Log记录类
 */
public class L {
    private static LogBuffer sLogBuffer = null;
    private static DateFileFormatter sFormatter = null;
    private static String SUFFIX_LOG = ".log";
    private static String BUFFER_NAME = "buffer.logCacher";

    public static final void init(File pathFile, int burrferCapacity, boolean compress, LogBuffer.LibLoader libLoader) {
        if (sLogBuffer != null) {
            throw new RuntimeException("请不要重复初始化Log配置");
        }
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String logName = new StringBuilder().append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))).append(SUFFIX_LOG).toString();
        String bufferPath = new File(pathFile, BUFFER_NAME).getAbsolutePath();
        sLogBuffer = new LogBuffer(bufferPath, burrferCapacity, new File(pathFile, logName).getAbsolutePath(), compress, libLoader);
        sFormatter = new DateFileFormatter();
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    public static void v(String tag, String msg) {
        println(VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        println(DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        println(INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        println(WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(WARN, tag, msg + "\n" + getStackTraceString(tr));
    }

    public static void w(String tag, Throwable tr) {
        println(WARN, tag, getStackTraceString(tr));
    }

    public static void e(String tag, String msg) {
        println(ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR, tag, msg + "\n" + getStackTraceString(tr));
    }

    public static void e(String tag, Throwable tr) {
        println(ERROR, tag, getStackTraceString(tr));
    }

    public static void println(int logLevel, String tag, String msg) {
        if (sLogBuffer != null) {
            sLogBuffer.write(sFormatter.format(logLevel, tag, msg));
//            Log.println(logLevel, tag, msg);
        }
    }

    public static void flush() {
        if (sLogBuffer != null) {
            sLogBuffer.flushAsync();
        }
    }

    public static void release() {
        if (sLogBuffer != null) {
            sLogBuffer.release();
        }
        sLogBuffer = null;
    }

}
