package com.mainli.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mainli.utils.SizeUtil;

/**
 * 仪表盘
 */
public class DashboardView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();
    private Path mPath = new Path();
    private final int DIAL_NUMBER = 20;
    private int mAngle = 120;
    private int mRadius = (int) SizeUtil.dp2Px(150);
    private int mPointerLenght = (int) SizeUtil.dp2Px(80);
    private PathDashPathEffect mEffect;

    private int mCurrerPointerCount = 5;

    public DashboardView(Context context) {
        super(context);
    }

    public DashboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SizeUtil.dp2Px(2));
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.setEmpty();
        int halfW = w >> 1;
        int halfH = h >> 1;
        mRectF.set(halfW - mRadius, halfH - mRadius, halfW + mRadius, halfH + mRadius);
        mPath.reset();
        mPath.addArc(mRectF, 90 + (mAngle >> 1), 360 - mAngle);
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(mPath, false);
        float length = pathMeasure.getLength();
        float dialWidth = SizeUtil.dp2Px(2);
        float advance = (length - dialWidth) / DIAL_NUMBER;
        mPath.reset();
        mPath.addRect(0, 0, dialWidth, SizeUtil.dp2Px(5), Path.Direction.CW);
        mEffect = new PathDashPathEffect(mPath, advance, 0, PathDashPathEffect.Style.ROTATE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setPathEffect(mEffect);
        canvas.drawArc(mRectF, 90 + (mAngle >> 1), 360 - mAngle, false, mPaint);
        mPaint.setPathEffect(null);
        canvas.drawArc(mRectF, 90 + (mAngle >> 1), 360 - mAngle, false, mPaint);
        canvas.drawLine(mRectF.centerX(), mRectF.centerY(), getCurrentPointEndX(), getCurrentPointEndY(), mPaint);
    }

    private float getCurrentPointEndX() {
        int angle = (mAngle >> 1) + 90 + ((360 - mAngle) * mCurrerPointerCount / DIAL_NUMBER);
        return mRectF.centerX() + (float) (Math.cos(Math.toRadians(angle)) * mPointerLenght);
    }

    private float getCurrentPointEndY() {
        int angle = (mAngle >> 1) + 90 + ((360 - mAngle) * mCurrerPointerCount / DIAL_NUMBER);
        return mRectF.centerY() + (float) (Math.sin(Math.toRadians(angle)) * mPointerLenght);
    }

    public void setPointerCount(int count) {
        mCurrerPointerCount = count % DIAL_NUMBER;
        invalidate();
    }
}
