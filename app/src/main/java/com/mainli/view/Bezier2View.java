package com.mainli.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Mainli on 2016/12/2mRadius.
 * https://github.com/mainlxl/bezier/blob/master/app/src/main/java/com/mainli/bezier/view/Bezier2View.java
 */

public class Bezier2View extends View {
    private int mWitch;
    private int mHeight;
    private Path mPath;
    private Paint mPaintBezier;
    private Paint mPaintText;
    private Paint mPaintText2;
    private Paint mPaintLine;
    private Point mFlagPoint;
    private int mRadius;

    public Bezier2View(Context context) {
        this(context, null);
    }

    public Bezier2View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bezier2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaintBezier = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBezier.setStyle(Paint.Style.STROKE);
        mPaintBezier.setStrokeWidth(3f);
        mPaintBezier.setColor(Color.RED);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(1f);

        mPaintText = new Paint();
        mPaintText.setTextSize(16);
        mPaintText2 = new Paint();
        mPaintText2.setTextSize(16);
        mPaintText2.setTextAlign(Paint.Align.RIGHT);
        mPath = new Path();
        mRadius = 8;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWitch = w;
        mHeight = h;
        mFlagPoint = new Point(mWitch / 2, mHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(mRadius, mHeight / 2);
        mPath.quadTo(mFlagPoint.x, mFlagPoint.y, mWitch - mRadius, mHeight / 2);
        canvas.drawPath(mPath, mPaintBezier);

        canvas.drawLine(mRadius, mHeight / 2, mFlagPoint.x, mFlagPoint.y, mPaintLine);
        canvas.drawLine(mWitch - mRadius, mHeight / 2, mFlagPoint.x, mFlagPoint.y, mPaintLine);

        canvas.drawText("起点(0," + (mHeight / 2 - mPaintText.ascent() + mRadius) + ")",
                0, mHeight / 2 - mPaintText.ascent() + mRadius, mPaintText);
        canvas.drawText("控制点(" + mFlagPoint.x + "," + (mFlagPoint.y - mRadius) + ")", mFlagPoint.x, mFlagPoint.y - mRadius, mPaintText);
        canvas.drawText("终点(" + mWitch + "," + (mHeight >> 1) + ")", mWitch, mHeight / 2 - mPaintText.ascent() + mRadius, mPaintText2);

        canvas.drawCircle(mRadius, mHeight / 2, mRadius, mPaintText);
        canvas.drawCircle(mFlagPoint.x, mFlagPoint.y, mRadius, mPaintText);
        canvas.drawCircle(mWitch - mRadius, mHeight / 2, mRadius, mPaintText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mFlagPoint.x = (int) event.getX();
                mFlagPoint.y = (int) event.getY();
                break;
        }
        invalidate();
        return true;
    }
}