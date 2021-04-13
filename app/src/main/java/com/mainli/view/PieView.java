package com.mainli.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mainli.utils.SizeUtil;

/**
 * 饼图
 */
public class PieView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();
    private int[] angles = new int[]{100, 120, 80, 60};
    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN};
    private int currentPostion = 1;
    private float offcenterLenght = SizeUtil.dp2Px(20);

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int margin = (int) SizeUtil.dp2Px(50);
        int halfW = (w - margin * 2) >> 1;
        int halfH = h >> 1;
        mRectF.set(margin, halfH - halfW, w - margin, halfH + halfW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int currentAngle = 0;
        for (int i = 0; i < angles.length; i++) {
            if (currentPostion == i) {
                double angle = Math.toRadians(currentAngle + (angles[i] >> 1));
                canvas.save();
                canvas.translate((int) (Math.cos(angle) * offcenterLenght), (int) (Math.sin(angle) * offcenterLenght));
            }
            mPaint.setColor(colors[i]);
            canvas.drawArc(mRectF, currentAngle, angles[i], true, mPaint);
            currentAngle += angles[i];
            if (currentPostion == i) {
                canvas.restore();
            }
        }
    }

    public void setCurrentPostion(int postion) {
        currentPostion = postion % angles.length;
        invalidate();
    }
}
