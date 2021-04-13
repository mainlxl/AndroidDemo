package com.mainli.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Camera;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

public final class SizeUtil {

    private static final DisplayMetrics SYSTEM_DISPLAY_METRICS = Resources.getSystem().getDisplayMetrics();
    private static int sNavigationBarHeight;

    public static final int getScreenWidthPixels() {
        return SYSTEM_DISPLAY_METRICS.widthPixels;
    }

    public static final int getScreenHeightPixels() {
        return SYSTEM_DISPLAY_METRICS.heightPixels;
    }

    public static final float dp2Px(float dp) {
        return dp * SYSTEM_DISPLAY_METRICS.density;
    }

    public static final int dp2PixelsInt(float dp) {
        return (int) (dp2Px(dp) + 0.5F);
    }

    public static final float sp2Px(float sp) {
        return sp * SYSTEM_DISPLAY_METRICS.density;
    }

    public static final float sp2PixelsInt(float sp) {
        return (int) (sp2Px(sp) + 0.5F);
    }

    /**
     * 调整Camera绘制时翻转相机高度,适配手机
     * 默认 camera.setLocation(0,0,-8);
     */
    public static final void adjustCameraZHeight(Camera camera) {
        camera.setLocation(0, 0, -6 * SYSTEM_DISPLAY_METRICS.density);
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        if (sNavigationBarHeight != Integer.MIN_VALUE) {
            return sNavigationBarHeight;
        }
        synchronized (SizeUtil.class) {
            if (sNavigationBarHeight == Integer.MIN_VALUE)
                sNavigationBarHeight = getNavigationBarHeightInternal(context);
        }
        return sNavigationBarHeight;
    }

    private static int getNavigationBarHeightInternal(Context context) {
        if (isExceptProcessNavigationBar()) {
            return 0;
        }
        return getNavigationHeightFromResource(context);
    }

    private static boolean isExceptProcessNavigationBar() {
        String deviceModel = Build.MODEL;
        if (deviceModel.equals("ZTE U950") || deviceModel.equals("ZTE U817") || deviceModel
                .equals("ZTE V955") || deviceModel.equals("ZTE BV0701")
                || deviceModel.equals("GT-S5301L")
                || deviceModel.equals("LG-E425f") || deviceModel.equals("GT-S5303B")
                || deviceModel.equals("I-STYLE2.1") || deviceModel.equals("SCH-S738C")
                || deviceModel.equals("S120 LOIN") || deviceModel.equals("START 765")
                || deviceModel.equals("LG-E425j") || deviceModel.equals("Archos 50 Titanium")
                || deviceModel.equals("ZTE N880G") || deviceModel.equals("O+ 8.91")
                || deviceModel.equals("ZP330") || deviceModel.equals("Wise+")
                || deviceModel.equals("HUAWEI Y511-U30") || deviceModel.equals("Che1-L04")
                || deviceModel.equals("ASUS_T00I") || deviceModel.equals("Lenovo A319")
                || deviceModel.equals("Bird 72_wet_a_jb3") || deviceModel.equals("Sendtel Wise")
                || deviceModel.equals("cross92_3923") || deviceModel.equals("HTC X920e")
                || deviceModel.equals("ONE TOUCH 4033X") || deviceModel.equals("GSmart Roma")
                || deviceModel.equals("A74B") || deviceModel.equals("Doogee Y100 Pro")
                || deviceModel.equals("M4 SS1050") || deviceModel.equals("Ibiza_F2")
                || deviceModel.equals("Lenovo P70-A") || deviceModel.equals("Y635-L21")
                || deviceModel.equals("hi6210sft") || deviceModel.equals("TurboX6Z")
                || deviceModel.equals("ONE TOUCH 4015A") || deviceModel.equals("LENNY2")
                || deviceModel.equals("A66A*") || deviceModel.equals("ONE TOUCH 4033X")
                || deviceModel.equals("LENNY2") || deviceModel.equals("PGN606")
                || deviceModel.equals("MEU AN400") || deviceModel.equals("ONE TOUCH 4015X")
                || deviceModel.equals("4013M") || deviceModel.equals("n625ab")
                || deviceModel.equals("HUAWEI Y511-T00")
                || deviceModel.equals("Redmi Note 4") || deviceModel.equals("MIX")) {
            return true;
        }
        boolean egnoreOppo = "OPPO".equals(Build.MANUFACTURER) && Build.VERSION.SDK_INT < 23;
        return egnoreOppo || "Meizu".equals(Build.MANUFACTURER);
    }

    private static int getNavigationHeightFromResource(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int navigationBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        if (resourceId > 0) {
            boolean hasNav = resources.getBoolean(resourceId);
            if (hasNav) {
                resourceId = resources.getIdentifier("navigation_bar_height",
                        "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = resources
                            .getDimensionPixelSize(resourceId);
                }
            }
        }

        if (navigationBarHeight <= 0) {
            DisplayMetrics dMetrics = new DisplayMetrics();
            display.getMetrics(dMetrics);
            int screenHeight = Math.max(dMetrics.widthPixels, dMetrics.heightPixels);
            int realHeight = 0;
            try {
                Method mt = display.getClass().getMethod("getRealSize", Point.class);
                Point size = new Point();
                mt.invoke(display, size);
                realHeight = Math.max(size.x, size.y);
            } catch (NoSuchMethodException e) {
                Method mt = null;
                try {
                    mt = display.getClass().getMethod("getRawHeight");
                } catch (NoSuchMethodException e2) {
                    try {
                        mt = display.getClass().getMethod("getRealHeight");
                    } catch (NoSuchMethodException e3) {
                    }
                }
                if (mt != null) {
                    try {
                        realHeight = (Integer) mt.invoke(display);
                    } catch (Exception e1) {
                    }
                }
            } catch (Exception e) {
            }
            navigationBarHeight = realHeight - screenHeight;
        }
        return navigationBarHeight;
    }

    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(
                    tv.data, context.getResources().getDisplayMetrics()
            );
        }
        return 0;
    }

}
