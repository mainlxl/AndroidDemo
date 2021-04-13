package com.mainli.view.MultiPointTouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mainli.utils.SizeUtil;

/**
 * 多点触控之无关联连多点
 */
public class MultiPointNoAssociationView extends View {

    private Paint mPaint;
    private float mOffsetX;
    private float mOffsetY;

    private PointF[] mPoints;

    public MultiPointNoAssociationView(Context context) {
        super(context);
    }

    public MultiPointNoAssociationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(SizeUtil.dp2Px(5));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPoints = new PointF[]{new PointF(), new PointF()};
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPoints[0].x = event.getX();
                mPoints[0].y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                mPoints[0].x = event.getX(0);
                mPoints[0].y = event.getY(0);
                if (pointerCount >= 2) {
                    mPoints[1].x = event.getX(1);
                    mPoints[1].y = event.getY(1);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = event.getActionIndex();
                mPoints[1].x = event.getX(index);
                mPoints[1].y = event.getY(index);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 2; i++) {
            PointF point = mPoints[i];
            canvas.drawPoint(point.x, point.y, mPaint);
        }
    }
}
