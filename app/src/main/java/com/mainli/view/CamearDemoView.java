package com.mainli.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.getkeepsafe.relinker.ReLinker;
import com.mainli.MyApplication;
import com.mainli.R;
import com.mainli.blur.BitmapBlur;
import com.mainli.blur.LibLoader;
import com.mainli.utils.BitmapUtils;
import com.mainli.utils.SizeUtil;

import androidx.annotation.Nullable;

/**
 * Camera实现3D变换
 */
public class CamearDemoView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();
    private Camera mCamera = new Camera();
    private Bitmap mBitmap;

    public CamearDemoView(Context context) {
        super(context);
    }

    public CamearDemoView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mPaint.setStrokeWidth(SizeUtil.dp2Px(5));
        mCamera.rotateX(-50);
        mBitmap = BitmapUtils.getTargetWidthBitmap(getResources(), R.mipmap.logo_square, 500);
        mBitmap = BitmapBlur.blurBitmap(mBitmap, 8f, new LibLoader() {
            @Override
            public void loadLibrary(String name) {
                ReLinker.loadLibrary(MyApplication.getAppContext(), name);
            }
        });
        SizeUtil.adjustCameraZHeight(mCamera);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfImageWidth = mBitmap.getWidth() >> 1;
        int halfImageHeight = mBitmap.getHeight() >> 1;
        float left = mRectF.centerX() - halfImageWidth;
        float top = mRectF.centerY() - halfImageHeight;
        canvas.save();
        canvas.translate(left + halfImageWidth, top + halfImageHeight);
        canvas.rotate(-mAngle);
        mCamera.applyToCanvas(canvas);
        canvas.clipRect(-mBitmap.getWidth(), -mBitmap.getHeight(), mBitmap.getWidth(), 0);//扩大裁切面积 适应旋转后大小
        canvas.rotate(mAngle);
        canvas.translate(-halfImageWidth, -halfImageHeight);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);//倒叙书写绘制内容
        canvas.restore();
        //重复上述过程应用于下半段图形
        canvas.translate(left + halfImageWidth, top + halfImageHeight);
        canvas.rotate(-mAngle);
        canvas.clipRect(-mBitmap.getWidth(), 0, mBitmap.getWidth(), mBitmap.getHeight());
        canvas.rotate(mAngle);
        canvas.translate(-halfImageWidth, -halfImageHeight);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    private int mAngle = 0;

    public void setRotate(int angle) {
        mAngle = angle;
        invalidate();
    }
}
