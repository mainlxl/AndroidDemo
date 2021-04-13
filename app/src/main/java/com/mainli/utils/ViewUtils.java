package com.mainli.utils;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

public final class ViewUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void clipRoundView(final View targetView, float radius) {
        targetView.setClipToOutline(true);
        targetView.setOutlineProvider(new ClipRoundViewOutlineProvider(radius));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static final class ClipRoundViewOutlineProvider extends ViewOutlineProvider {
        float radius = 0;

        public ClipRoundViewOutlineProvider(float radius) {
            this.radius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            if (radius > 0) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), radius);
            }
        }
    }

    /**
     * 产生shape类型的drawable
     *
     * @param solidColor
     * @param strokeColor
     * @param strokeWidth
     * @param radius
     * @return
     */
    public static GradientDrawable getShapeDrawable(int solidColor, int strokeColor, int strokeWidth, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(solidColor);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
