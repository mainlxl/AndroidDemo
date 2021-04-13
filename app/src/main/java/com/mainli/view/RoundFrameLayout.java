package com.mainli.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.mainli.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 用于使子视图圆角,子View必须撑满该View否则效果缺失
 */
public class RoundFrameLayout extends FrameLayout {
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private RectF mRectF;
    private int radius;
    private Path mPath;
    private RoundViewOutlineProvider mProvider;
    private boolean isUseViewOutlineProvider;

    public RoundFrameLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        int defValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        if (attrs == null) {
            radius = defValue;
        } else {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundFrameLayout);
            radius = a.getDimensionPixelSize(R.styleable.RoundFrameLayout_radius, defValue);
            a.recycle();
        }
        isUseViewOutlineProvider = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @SuppressLint("NewApi")
    public void setRadius(int pixelSize) {
        radius = pixelSize;
        if (mPath != null && mRectF != null) {
            mPath.reset();
            setMask(mPath, mRectF, radius);
            invalidate();
        }
        if (mProvider != null) {
            mProvider.setRadius(radius);
            invalidateOutline();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isUseViewOutlineProvider) {
            setClipToOutline(true);
            mProvider = new RoundViewOutlineProvider(new Rect(0, 0, w, h), radius);
            setOutlineProvider(mProvider);
        } else {
            if (mPorterDuffXfermode == null) {
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            }
            mPath = new Path();
            mRectF = new RectF(0, 0, w, h);
            setMask(mPath, mRectF, radius);
            setWillNotDraw(true);
            setBackground(null);
        }
    }

    /**
     * android P path范围缩小 如果只addRoundRect导致圆角失效
     */
    private void setMask(Path path, RectF bound, float radius) {
        path.addRect(bound, Path.Direction.CW);
        path.addRoundRect(bound, radius, radius, Path.Direction.CCW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isUseViewOutlineProvider) {
            super.dispatchDraw(canvas);
        } else {
            int save = canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            mPaint.setXfermode(mPorterDuffXfermode);
            canvas.drawPath(mPath, mPaint);//必须使用path 画圆角矩形才可以正常叠加 直接使用canvas绘制圆角矩形
            mPaint.setXfermode(null);
            canvas.restoreToCount(save);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class RoundViewOutlineProvider extends android.view.ViewOutlineProvider {
        private Rect mBound;
        private int mRadius;

        public RoundViewOutlineProvider(Rect bound, int radius) {
            mBound = bound;
            mRadius = radius;
        }

        public void setRadius(int radius) {
            mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(mBound, mRadius);
        }
    }
}