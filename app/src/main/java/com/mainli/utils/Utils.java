package com.mainli.utils;

import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

public class Utils {
    /**
     * 获取整形位数
     *
     * @param num
     * @return
     */
    public static int getNumLength(long num) {
        num = num > 0 ? num : -num;
        if (num == 0) {
            return 1;
        }
        return (int) Math.log10(num) + 1;
    }

    /**
     * 默认语言环境的布局方向是否是从右到左
     * 适配阿拉伯语言从右到左时使用
     */
    public static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }
}
