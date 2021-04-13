package com.mainli.utils;

import android.text.format.DateUtils;

import java.util.Calendar;

public class TimeUtil {

    /**
     * @param day 0为今天 -1为昨天 1为明天 以此类推
     * @return 凌晨0点0分0秒时间
     */
    public static long getZeroTime(int day) {
        Calendar instance = Calendar.getInstance();
        if (day != 0) {
            instance.add(Calendar.DAY_OF_YEAR, day);
        }
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    private static long todayZeroTime = 0;

    /**
     * @return 今日凌晨0点0分0秒时间
     */
    public static long getTodayZeroTime() {
        long time = System.currentTimeMillis() - todayZeroTime;
        if (todayZeroTime == 0 || time <= 0 || time > DateUtils.DAY_IN_MILLIS) {
            todayZeroTime = getZeroTime(0);
        }
        return todayZeroTime;
    }


    /**
     * 在自然日时间段中
     *
     * @return
     */
    public static boolean isTodayTime(long time) {
        long timeDifference = time - getTodayZeroTime();
        return timeDifference >= 0 && timeDifference <= DateUtils.DAY_IN_MILLIS;
    }
}
