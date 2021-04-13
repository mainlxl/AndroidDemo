package com.mainli.view.MultiPointTouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mainli.R;
import com.mainli.utils.BitmapUtils;
import com.mainli.utils.SizeUtil;

/**
 * 多点触控之接替滑动
 */
public class MultiPointMeanView extends View {

    private Bitmap mBitmap;
    private float mOffsetX;
    private float mOffsetY;

    public MultiPointMeanView(Context context) {
        super(context);
    }

    public MultiPointMeanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mBitmap = BitmapUtils.getTargetWidthBitmap(getResources(), R.mipmap.logo_square, SizeUtil.dp2PixelsInt(200));
    }

    private float oldX, oldY;

    /**
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        float x, y, sumX = 0, sumY = 0;
        for (int i = 0; i < count; i++) {
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        x = sumX / count;
        y = sumY / count;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                oldX = x;
                oldY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mOffsetX += x - oldX;
                mOffsetY += y - oldY;
                oldX = x;
                oldY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("Mainli", "ACTION_POINTER_DOWN:" + event.getActionIndex());
                oldX = x;
                oldY = y;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                /**
                 * 执行到ACTION_POINTER_UP 事件还在需要手动去除
                 */
                int actionIndex = event.getActionIndex();
                oldX = (sumX - event.getX(actionIndex)) / (count - 1);
                oldY = (sumY - event.getY(actionIndex)) / (count - 1);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, mOffsetX, mOffsetY, null);
    }
}
