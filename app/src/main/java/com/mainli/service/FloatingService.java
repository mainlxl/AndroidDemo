package com.mainli.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mainli.R;
import com.mainli.floating.FloatingRootLayout;
import com.mainli.utils.SizeUtil;
import com.mainli.utils.ViewUtils;

import androidx.annotation.Nullable;

public class FloatingService extends Service {

    private FloatingRootLayout mBallLayout;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBall();
    }

    private void initBall() {
        mBallLayout = new FloatingRootLayout(this.getApplicationContext());
        mBallLayout.setStartPositionCenter(false, true);
        ImageView imageView = new ImageView(this.getApplicationContext());
        imageView.setImageResource(R.mipmap.logo);
        int size = SizeUtil.dp2PixelsInt(35);
        int padding = SizeUtil.dp2PixelsInt(6);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setBackground(ViewUtils.getShapeDrawable(0xffffffff, 0x66333333, SizeUtil.dp2PixelsInt(1), size));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        mBallLayout.addView(imageView, params);
        mBallLayout.attachDrag(true);
        mBallLayout.showFloating();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBallLayout != null) {
            mBallLayout.removeFloating();
        }
    }


}
