package com.mainli.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.mainli.R;

import java.util.Arrays;

/**
 * Created by lixiaoliang on 2018-5-3.
 */
public class LinkedEditText extends AppCompatEditText {

    public LinkedEditText(Context context) {
        super(context);
    }

    public LinkedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinkedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String toMDString() {
        Editable text = getText();
        StringBuffer stringBuffer = new StringBuffer();
        LinkeSpan[] linkes = text.getSpans(0, text.length(), LinkeSpan.class);
        for (LinkeSpan linkeSpan : linkes) {
            linkeSpan.index = text.getSpanStart(linkeSpan);
        }
        Arrays.sort(linkes);
        char[] tmp;
        int start = 0;
        for (LinkeSpan linke : linkes) {
            int charCount = linke.index - start;
            int linkeNameSize = linke.urlName.length();
            if (charCount > 0) {
                tmp = new char[charCount];
                text.getChars(start, linke.index, tmp, 0);
                stringBuffer.append(tmp);
                stringBuffer.append(linke.toString());
                start = linke.index + linkeNameSize;
            } else if (charCount == 0) {
                stringBuffer.append(linke.toString());
                start += linkeNameSize;
            }
        }


        //补足剩余字符
        int length = text.length();
        if (start < length) {
            tmp = new char[length - start];
            text.getChars(start, length, tmp, 0);
            stringBuffer.append(tmp);
        }
        return stringBuffer.toString();
    }


    public void insertLinked(String name, String url) {
        insertMDLinked(getSelectionEnd(), name, url, convertMDLinked(name, url));
    }

    @NonNull
    private String convertMDLinked(String name, String url) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(name);
        stringBuilder.append(']');
        stringBuilder.append('(');
        stringBuilder.append(url);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private void insertMDLinked(int where, String name, String url, String mdLinked) {
        Editable text = getText();
        LinkeSpan span = new LinkeSpan(getContext(),name, url, mdLinked);
        text.insert(where, name);
        text.setSpan(span, where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ReplacementSelectSpan(), where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    private static class LinkeSpan extends ReplacementSpan implements Comparable<LinkeSpan> {
        private String urlName;
        private String md;
        private String url;
        private int index = 0;

        public void setIndex(int index) {
            this.index = index;
        }

        private static Drawable sDrawableLiked;
        private static int dp16 = 0;
        private static int dp32 = 0;
        private static int dp8 = 0;
        private static int dp4 = 0;

        public LinkeSpan(Context context, String urlName, String url, String md) {
            this.urlName = urlName;
            this.url = url;
            this.md = md;
            initParam(context);
        }

        private void initParam(Context context) {
            if (sDrawableLiked == null) {
                sDrawableLiked = ContextCompat.getDrawable(context, R.drawable.icon_linked_ff687fff);
                dp32 = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32F, context.getResources().getDisplayMetrics()) + 0.5F);
                dp16 = dp32 >> 1;
                dp8 = dp16 >> 1;
                dp4 = dp8 >> 1;
            }
        }

        @Override
        public String toString() {
            return md;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            return (int) (dp32 + dp4 + paint.measureText(urlName) + 0.5F);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            sDrawableLiked.setBounds((int) (x + dp8), top + ((bottom - top - sDrawableLiked.getMinimumHeight()) >> 1), (int) (x + dp16 + dp8), bottom);
            sDrawableLiked.draw(canvas);
            int color = paint.getColor();
            paint.setColor(0xFF687FFF);
            canvas.drawText(this.urlName, x + dp32, (float) y, paint);
            paint.setColor(color);
        }

        @Override
        public int compareTo(@NonNull LinkeSpan o) {
            return index - o.index;
        }
    }

    private static class ReplacementSelectSpan extends URLSpan implements NoCopySpan {
        public ReplacementSelectSpan() {
            super("");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
        }

        @Override
        public void onClick(View widget) {
        }
    }
}
