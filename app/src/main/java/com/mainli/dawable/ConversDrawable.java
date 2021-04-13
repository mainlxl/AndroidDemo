package com.mainli.dawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 通过draw前后去变动图片绘制
 * 注意不要调用getxxx系列方法,拿到的值不准确 因出现类似getBounds等final方法所以这里统一不重写getxxx方法
 */
public class ConversDrawable extends Drawable {
    private Drawable mBase;
    private ConversConfig conversConfig;

    public ConversDrawable setConversConfig(ConversConfig conversConfig) {
        this.conversConfig = conversConfig;
        return this;
    }

    /**
     * 如果是阿拉伯,请直接使用Drawable的setAutoMirrored方法设置true{@link #setAutoMirrored(boolean)}
     */
    public ConversDrawable lRConvers() {
        this.conversConfig = new ConversConfig() {
            @Override
            public void onDrawBefore(Drawable base, Canvas canvas) {
                canvas.save();
                Rect bounds = mBase.getBounds();
                int width = bounds.width() > 0 ? bounds.width() : (mBase.getIntrinsicWidth() > 0 ? mBase.getIntrinsicWidth() : mBase.getMinimumWidth());
                canvas.scale(-1, 1, width >> 1, 0);
            }

            @Override
            public void onDrawAfter(Drawable base, Canvas canvas) {
                canvas.restore();
            }
        };
        return this;
    }

    public ConversDrawable(Drawable mBase) {
        this.mBase = mBase;
    }


    public interface ConversConfig {
        void onDrawBefore(Drawable base, Canvas canvas);

        void onDrawAfter(Drawable base, Canvas canvas);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (conversConfig != null) {
            conversConfig.onDrawBefore(mBase, canvas);
        }
        mBase.draw(canvas);
        if (conversConfig != null) {
            conversConfig.onDrawAfter(mBase, canvas);
        }
    }


    @Override
    public void setBounds(@NonNull Rect bounds) {
        this.mBase.setBounds(bounds);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        return mBase.setVisible(visible, restart);
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        mBase.setAutoMirrored(mirrored);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        mBase.setBounds(left, top, right, bottom);
    }

    @Override
    public void setChangingConfigurations(int configs) {
        mBase.setChangingConfigurations(configs);
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mBase.setFilterBitmap(filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspot(float x, float y) {
        mBase.setHotspot(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTint(int tintColor) {
        mBase.setTint(tintColor);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTintList(@Nullable ColorStateList tint) {
        mBase.setTintList(tint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        mBase.setTintMode(tintMode);
    }

    @Override
    public void setColorFilter(int color, @NonNull PorterDuff.Mode mode) {
        mBase.setColorFilter(color, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        mBase.setHotspotBounds(left, top, right, bottom);
    }

    @Override
    public void setDither(boolean dither) {
        mBase.setDither(dither);
    }

    @Override
    public void setAlpha(int alpha) {
        mBase.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mBase.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mBase.getOpacity();
    }

    @Override
    public boolean isStateful() {
        return mBase.isStateful();
    }

    @Override
    public boolean setState(@NonNull int[] stateSet) {
        return mBase.setState(stateSet);
    }
}