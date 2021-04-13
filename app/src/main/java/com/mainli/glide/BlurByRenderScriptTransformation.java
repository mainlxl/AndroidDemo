package com.mainli.glide;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;
import com.mainli.MyApplication;

import java.security.MessageDigest;

/**
 * Created by lixiaoliang on 2018-4-11.
 */
public class BlurByRenderScriptTransformation extends BitmapTransformation {
    private float radius;
    private static final String ID = "com.mainli.glide.BlurByRenderScriptTransformation";

    public BlurByRenderScriptTransformation(float radius) {
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        //  bitmapResult = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
//            if (bitmapResult == null) {
//                bitmapResult = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
//            }
//            Canvas canvas = new Canvas(bitmapResult);
//            canvas.drawBitmap(toTransform, null, new RectF(0, 0, outWidth, outHeight), null);
        //Create renderscript
        return blur(toTransform, radius);
    }

    public static Bitmap blur(@NonNull Bitmap bitmap, float radius) {
        RenderScript rs = null;
        Allocation allocation = null;
        Type t = null;
        Allocation blurredAllocation = null;
        ScriptIntrinsicBlur blurScript = null;
        try {
            rs = RenderScript.create(MyApplication.getAppContext());
            //Create allocation from Bitmap
            allocation = Allocation.createFromBitmap(rs, bitmap);

            t = allocation.getType();

            //Create allocation with the same type
            blurredAllocation = Allocation.createTyped(rs, t);

            //Create script
            blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            //Set blur radius (maximum 25.0)
            blurScript.setRadius(radius);
            //Set input for script
            blurScript.setInput(allocation);
            //Call script for output allocation
            blurScript.forEach(blurredAllocation);
            //Copy script result into bitmap
            blurredAllocation.copyTo(bitmap);
        } finally {
            //Destroy everything to free memory
            if (allocation != null) {
                try {
                    allocation.destroy();
                } catch (Exception e) {
                }
            }
            if (blurredAllocation != null) {
                try {
                    blurredAllocation.destroy();
                } catch (Exception e) {
                }
            }
            if (blurScript != null) {
                try {
                    blurScript.destroy();
                } catch (Exception e) {
                }
            }
            if (t != null) {
                try {
                    t.destroy();
                } catch (Exception e) {
                }
            }
            if (rs != null) {
                try {
                    rs.destroy();
                } catch (Exception e) {
                }
            }
        }
        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        String s = ID + radius;
        messageDigest.update(s.getBytes(CHARSET));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BlurByRenderScriptTransformation)) {
            return false;
        }
        BlurByRenderScriptTransformation b = (BlurByRenderScriptTransformation) o;
        return b.radius == this.radius;
    }


    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(radius));
    }


}
