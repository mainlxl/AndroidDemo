package com.mainli.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.method.MovementMethod;
import android.util.AttributeSet;

import com.mainli.R;

/**
 * Created by Mainli on 2018/4/8.
 */

public class NumberCaptchaInputView extends AppCompatEditText {
    private int mTextNumberOccupyWidth;
    private int mTextNumberOccupyHeight;
    private float mGapWidth,cursorLineSize;
    private OnCaptchaListener mListener;
    private CaptchaDraw mCaptchaDraw = null;
    private int mCaptchaSize = 4;
    private Paint mCursorPaint;

    public void setListener(OnCaptchaListener listener) {
        mListener = listener;
    }

    public NumberCaptchaInputView(Context context) {
        this(context, null, 0);
    }

    public NumberCaptchaInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberCaptchaInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberCaptcha, defStyleAttr, 0);
        mGapWidth = a.getDimension(R.styleable.NumberCaptcha_gapWidth, dp2Px(2));
        cursorLineSize = a.getDimension(R.styleable.NumberCaptcha_cursorLineSize, dp2Px(2));
        mCaptchaSize = a.getInt(R.styleable.NumberCaptcha_captchaSize, 4);
        a.recycle();
        setFocusableInTouchMode(true);
        setFocusable(true);
        super.setInputType(InputType.TYPE_CLASS_NUMBER);
        super.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mCaptchaSize)});
        super.setCursorVisible(false);//隐藏光标
        if (mCaptchaDraw == null) {
            setCaptchaDraw(new DefaultCaptchaDraw(cursorLineSize));
        }
    }

    @Override
    @Deprecated
    public void setCursorVisible(boolean visible) {
    }

    @Override
    @Deprecated
    public void setFilters(InputFilter[] filters) {
    }

    @Override
    @Deprecated
    public void setInputType(int type) {
    }

    public void setCaptchaDraw(CaptchaDraw captchaDraw) {
        mCaptchaDraw = captchaDraw;
        mCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCaptchaDraw.initPaint(getPaint(), mCursorPaint);
    }

    /**
     * 单行情况下保证光标始终在最后位置,解决点击开头部分不会删除问题
     * @param x
     * @param y
     * @return
     */
    @Override
    public int getOffsetForPosition(float x, float y) {
        return getText().length();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        TextPaint paint = getPaint();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        mTextNumberOccupyWidth = (int) (fontMetrics.bottom - fontMetrics.top + 0.5f);
        mTextNumberOccupyHeight = mTextNumberOccupyWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            int size = width - getPaddingLeft() - getPaddingRight();
            int targetSize = mCaptchaSize * mTextNumberOccupyWidth;
            if (size >= targetSize) {
                if (mCaptchaSize >= 1) {
                    mGapWidth = (int) ((size - targetSize) * 1f / (mCaptchaSize - 1));
                } else {
                    mGapWidth = 0;
                }
            } else {
                mGapWidth = 0;
                mTextNumberOccupyWidth = size / mCaptchaSize;
            }
        } else {
            width = (int) ((mTextNumberOccupyWidth + mGapWidth) * mCaptchaSize - mGapWidth + getPaddingLeft() + getPaddingRight());
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = mTextNumberOccupyHeight + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
        if (mCaptchaDraw != null) {
            mCaptchaDraw.onDrawMeasure(mTextNumberOccupyWidth, mTextNumberOccupyHeight, mGapWidth,
                    //可绘制区域
                    getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom(),
                    //文本居中绘制修正
                    (fontMetrics.descent + fontMetrics.ascent) / 2);
        }
    }

    //设置不可滑动
    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCaptchaDraw == null) {
            return;
        }
        for (int i = 0; i < mCaptchaSize; i++) {
            mCaptchaDraw.onItemDraw(canvas, i, super.getText().toString(), getPaint(), mCursorPaint);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    private final float density = getResources().getDisplayMetrics().density;

    private float dp2Px(int dp) {
        return density * dp + 0.5f;
    }

    public void clearCaptchal() {
        setText(null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (mListener != null && text != null) {
            int length = text.length();
            if (length == mCaptchaSize) {
                mListener.onCaptchaInputFinish(text);
            }
            mListener.onCaptchaTextSize(text, length);
        }
    }

    public interface OnCaptchaListener {
        void onCaptchaInputFinish(CharSequence captcha);

        void onCaptchaTextSize(CharSequence captcha, int size);
    }

    public interface CaptchaDraw {
        void initPaint(Paint numberPaint, Paint cursorPaint);

        void onItemDraw(Canvas canvas, int position, CharSequence text, Paint numberPaint, Paint cursorPaint);

        void onDrawMeasure(int numberOccupyWidth, int numberOccupyHeight, float gapWitch, int left, int top, int right, int bottom, float textFixHeight);
    }

    private static class DefaultCaptchaDraw implements CaptchaDraw {
        private final int CURRENT_CURSOR_COLOR = 0xFF687FFF;
        private final int DEFAULT_CURSOR_COLOR = 0x42000000;
        private final int NUMBER_COLOR = 0xDE000000;
        private float textFixHeight;
        private float cornersSize, gapWitch;

        private int numberOccupyWidth, left, top, bottom;


        public DefaultCaptchaDraw(float cursorLineSize) {
            this.cornersSize = cursorLineSize;
        }

        @Override
        public void initPaint(Paint numberPaint, Paint cursorPaint) {
            numberPaint.setTextAlign(Paint.Align.CENTER);
            numberPaint.setColor(NUMBER_COLOR);
            cursorPaint.setStyle(Paint.Style.STROKE);
            cursorPaint.setStrokeWidth(cornersSize);
        }

        @Override
        public void onItemDraw(Canvas canvas, int position, CharSequence text, Paint numberPaint, Paint cursorPaint) {
            int length = text.length();
            int start = (int) ((numberOccupyWidth + gapWitch) * position + left);
            if (length != 0 && position < length) {
                cursorPaint.setColor(CURRENT_CURSOR_COLOR);
                canvas.drawText(String.valueOf(text.charAt(position)), start + (numberOccupyWidth >> 1), ((bottom + top) >> 1) - textFixHeight, numberPaint);
                canvas.drawLine(start, bottom, start + numberOccupyWidth, bottom, cursorPaint);
            } else {
                cursorPaint.setColor(DEFAULT_CURSOR_COLOR);
                canvas.drawRoundRect(start, top, start + numberOccupyWidth, bottom, cornersSize, cornersSize, cursorPaint);
            }

        }

        @Override
        public void onDrawMeasure(int numberOccupyWidth, int numberOccupyHeight, float gapWitch, int left, int top, int right, int bottom, float textFixHeight) {
            this.numberOccupyWidth = numberOccupyWidth;
            this.gapWitch = gapWitch;
            this.left = left;
            this.top = top;
            this.top = top;
            this.bottom = bottom;
            this.textFixHeight = textFixHeight;
        }
    }
}