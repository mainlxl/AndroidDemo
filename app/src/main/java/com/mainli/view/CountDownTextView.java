package com.mainli.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mainli.R;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;


/**
 * 显示10:10:10控件
 */
public class CountDownTextView extends AppCompatTextView {
    private CountDownTimerListener mCountDownTimerListener;
    private CharSequence text;
    private static final StyleSpanFactory DEFAULT_SPAN_FACTORY = new StyleSpanFactory() {
        @Override
        public ReplacementSpan createSpan(float textGap, float gap) {
            return new DefaultNumberStyleSpan(textGap, gap);
        }
    };

    private float textPadding = -1;
    //数字两边间隙
    private float halfGap = -1;
    private CountDownTimer mCountDownTimer;
    private SpannableString mFinalText;
    private long mMillisUntilFinished = Long.MIN_VALUE;

    public CountDownTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public CountDownTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setCountDownTimerListener(CountDownTimerListener countDownTimerListener) {
        mCountDownTimerListener = countDownTimerListener;
    }

    /**
     * xml配置可能会走默认,init方法在构造方法后调用
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView);
            textPadding = a.getDimension(R.styleable.CountDownTextView_textPadding, dp2px(2));
            halfGap = a.getDimension(R.styleable.CountDownTextView_halfGap, dp2px(0.5f));
            a.recycle();
        } else {
            textPadding = dp2px(2);
            halfGap = dp2px(0.5f);
        }

    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        this.text = text;
        applySpacing();
    }

    /**
     * 添加应用空间
     */
    private void applySpacing() {
        if (this == null || this.text == null) return;
        mFinalText = new SpannableString(this.text);
        int length = this.text.length();
        if (length > 1) { // 如果当前TextView内容长度大于1，则进行空格添加
            for (int i = 0; i < length; i++) {
                char c = this.text.charAt(i);
                if (c >= '0' && c <= '9') {
                    mFinalText.setSpan(DEFAULT_SPAN_FACTORY.createSpan(textPadding, halfGap), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        super.setText(mFinalText, TextView.BufferType.SPANNABLE);
    }

    public void startCountDown(long time) {
        if (mCountDownTimer == null) {
            mCountDownTimer = new MyCountDownTimer(time, new CountDownTimerListener() {
                @Override
                public void onTick(String current, long millisUntilFinished, float percentage) {
                    if (mMillisUntilFinished != Long.MIN_VALUE) {//解决cancelAndCleanCountDown后没有马上停止
                        CountDownTextView.this.text = current;
                        applySpacing();
                        if (mCountDownTimerListener != null) {
                            mCountDownTimerListener.onTick(current, millisUntilFinished, percentage);
                        }
                        mMillisUntilFinished = millisUntilFinished;
                    }
                }

                @Override
                public void onFinish() {
                    if (mCountDownTimerListener != null) {
                        mCountDownTimerListener.onFinish();
                    }
                }
            });
            mMillisUntilFinished = 0;
            mCountDownTimer.start();
        } else {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
            startCountDown(time);
        }
    }

    /**
     * 清理并取消倒计时
     */
    public void cancelAndCleanCountDown() {
        if (mCountDownTimer == null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mMillisUntilFinished = Long.MIN_VALUE;
    }

    /**
     * 获取当前剩余秒数
     *
     * @return
     */
    public long getMillisUntilFinished() {
        return mMillisUntilFinished;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private interface StyleSpanFactory {
        ReplacementSpan createSpan(float textGap, float gap);
    }

    /**
     * 默认数字样式
     */
    static class DefaultNumberStyleSpan extends ReplacementSpan {
        private static int mSize = Integer.MIN_VALUE;
        private float textPadding;
        private float halfGap;

        public DefaultNumberStyleSpan(float textGap, float halfGap) {
            this.textPadding = textGap;
            this.halfGap = halfGap;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            if (mSize == Integer.MIN_VALUE) {//0比较宽按0测量大小
                mSize = (int) (paint.measureText("0", 0, 1) + (textPadding + halfGap) * 2 + 0.5f);
            }
            return mSize;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, start, end, x + (mSize >> 1), y, paint);
            int oldColor = paint.getColor();
            paint.setColor(0x4dffffff);
            canvas.drawRoundRect(new RectF(x + halfGap, top, x + mSize - halfGap, bottom), textPadding, textPadding, paint);
            paint.setColor(oldColor);
        }
    }

    private static float dp2px(float dp) {
        return Resources.getSystem().getDisplayMetrics().density * dp;
    }

    public interface CountDownTimerListener {
        void onTick(String current, long millisUntilFinished, float percentage);

        void onFinish();
    }

    private static final class MyCountDownTimer extends CountDownTimer {
        private CountDownTimerListener mCountDownTimerListener;
        private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
        private long totalTime;

        public MyCountDownTimer(long millisInFuture, CountDownTimerListener countDownTimerListener) {
            super(millisInFuture, 1000);
            totalTime = millisInFuture;
            mCountDownTimerListener = countDownTimerListener;
            mDateFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        }


        @Override
        public void onTick(long millisUntilFinished) {
            if (mCountDownTimerListener != null) {
                mCountDownTimerListener.onTick(mDateFormat.format(millisUntilFinished), millisUntilFinished, millisUntilFinished * 1f / totalTime);
            }
        }

        @Override
        public void onFinish() {
            if (mCountDownTimerListener != null) {
                mCountDownTimerListener.onFinish();
            }
        }
    }

}