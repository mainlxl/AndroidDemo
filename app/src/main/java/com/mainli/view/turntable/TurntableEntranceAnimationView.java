package com.mainli.view.turntable;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.mainli.R;
import com.mainli.utils.BitmapUtils;

public class TurntableEntranceAnimationView extends View {

    private Bitmap mTurntable, mStar2, mStar1, mPoint, mLightEffect, mBottom;
    private int mHalfWidth, mBottomStartX, mBottomStartY, mPointStartX, mPointStartY, turntablePadding;
    private Matrix mMatricesLightEffect = new Matrix(), mMatricesTurntable = new Matrix();
    private Paint paintStart1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintStart2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ValueAnimator animator;

    public TurntableEntranceAnimationView(Context context) {
        super(context);
    }

    public TurntableEntranceAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TurntableEntranceAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);//这里保持宽高一致
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);
    }

    private int getCenterHorizontalStart(int width, Bitmap bitmap) {
        return (width - bitmap.getWidth()) >> 1;
    }


    private void init(int w, int h) {
        mHalfWidth = w >> 1;
        mLightEffect = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_light_effect, w);
        mStar1 = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_star1, w);
        mStar2 = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_star2, w);
        mBottom = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_bottom, (int) (w * 36f / 64f + 0.5f));
        mTurntable = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_turntable, (int) (w * 54f / 64f + 0.5f));
        mPoint = BitmapUtils.getTargetWidthBitmap(getResources(), R.drawable.turntable_entrance_point, (int) (w * 13f / 64f + 0.5f));

        mBottomStartX = getCenterHorizontalStart(w, mBottom);
        mBottomStartY = h - mBottom.getHeight();

        turntablePadding = getCenterHorizontalStart(w, mTurntable);
        mMatricesTurntable.setTranslate(turntablePadding, turntablePadding);

        mPointStartX = getCenterHorizontalStart(w, mPoint);
        mPointStartY = (w - mPoint.getHeight()) >> 1;
        setTurntableRotate(0);
        setStarAlpha(255);
    }

    private void setTurntableRotate(float degrees) {
        mMatricesLightEffect.setRotate(-degrees, mHalfWidth, mHalfWidth);
        mMatricesTurntable.setTranslate(turntablePadding, turntablePadding);
        mMatricesTurntable.postRotate(degrees, mHalfWidth, mHalfWidth);
    }

    private void setStarAlpha(int alpha) {
        paintStart1.setAlpha(alpha);
        paintStart2.setAlpha(255 - alpha);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0x99000000);
        canvas.drawBitmap(mLightEffect, mMatricesLightEffect, null);
        canvas.drawBitmap(mBottom, mBottomStartX, mBottomStartY, null);
        canvas.drawBitmap(mTurntable, mMatricesTurntable, null);
        canvas.drawBitmap(mPoint, mPointStartX, mPointStartY, null);
        canvas.drawBitmap(mStar1, 0, 0, paintStart1);
        canvas.drawBitmap(mStar2, 0, 0, paintStart2);
    }

    public void playAnimation() {
        if (animator == null) {
            animator = ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("TurntableRotate", 0, -360),//
                    PropertyValuesHolder.ofInt("StarAlpha", 255, 102, 255, 102,
                            255, 102, 255, 102,
                            255, 102, 255, 102,
                            255, 102, 255, 102)).setDuration(5000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    postInvalidateOnAnimation();
                }
            });
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
        } else if (animator.isRunning()) {
            animator.end();
        }
        animator.start();
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.end();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }
}
