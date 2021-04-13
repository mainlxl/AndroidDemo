package com.mainli.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.DrawableRes;

/**
 * BitmapFactory.decodeResource占用内存 = 图片宽度/inSampleSize*inTargetDensity/inDensity*图片高度/inSampleSize**inTargetDensity/inDensity*每个像素所占的内存
 * <p>
 * 格式           位数   占用内存字节
 * ALPHA_8  	  8	        1
 * RGB_565  	  16	    2
 * ARGB_4444	  16	    2
 * ARGB_8888	  32	    4
 */
public final class BitmapUtils {
    private BitmapUtils() {
    }

    /**
     * 获取目标[宽度]Bitmap
     * 高度等比缩放
     */
    public static Bitmap getTargetWidthBitmap(Resources resources, @DrawableRes int drawableId, int targetWidth) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawableId, opts);
        opts.inTargetDensity = targetWidth;
        opts.inDensity = opts.outWidth;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, drawableId, opts);
    }

    /**
     * 获取目标[高度]Bitmap
     * 宽度等比缩放
     */
    public static Bitmap getTargetHeightBitmap(Resources resources, @DrawableRes int drawableId, int targetheight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawableId, opts);
        opts.inTargetDensity = targetheight;
        opts.inDensity = opts.outHeight;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, drawableId, opts);
    }
}
