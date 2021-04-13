package com.mainli.spannable;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.ReplacementSpan;

/**
 * Created by lixiaoliang on 2018-5-3.
 */
public class LinkeSpan extends ReplacementSpan {
    private String url;

    public LinkeSpan(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (int) (paint.measureText(url) + 0.5f);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.drawText(url, x, y, paint);
    }
}
