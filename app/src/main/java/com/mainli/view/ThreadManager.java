package com.mainli.view;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import java.security.InvalidParameterException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ThreadManager {
    public static final int THREAD_UI = 0;
    private static final int THREAD_SIZE = 1;

    private static final Handler[] HANDLERS = new Handler[THREAD_SIZE];
    private static final String[] THREAD_NAMES = {"thread_ui"};

    public static void post(int index, Runnable r) {
        postDelayed(index, r, 0);
    }

    public static void postDelayed(int index, @NonNull Runnable r, long delayMillis) {
        Handler handler = getHandler(index);
        handler.postDelayed(r, delayMillis);
    }

    public static void removeCallbacks(int index, @Nullable Runnable r) {
        Handler handler = getHandler(index);
        handler.removeCallbacks(r);
    }

    public static Handler getHandler(int index) {
        if (index < 0 || index >= THREAD_SIZE) {
            throw new InvalidParameterException();
        }

        if (HANDLERS[index] == null) {
            synchronized (HANDLERS) {
                if (HANDLERS[index] == null) {
                    if (index == THREAD_UI) {
                        HANDLERS[THREAD_UI] = new Handler(Looper.getMainLooper());
                    } else {
                        HandlerThread thread = new HandlerThread(THREAD_NAMES[index]);
                        thread.setPriority(Thread.MIN_PRIORITY);
                        thread.start();
                        HANDLERS[index] = new Handler(thread.getLooper());
                    }
                }
            }
        }
        return HANDLERS[index];
    }

    public static void destroyItem(int index) {
        if (HANDLERS[index] != null) {
            HANDLERS[index].removeCallbacks(null);
            HANDLERS[index] = null;
        }
    }
}