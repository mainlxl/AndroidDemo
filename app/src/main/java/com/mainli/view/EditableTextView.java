package com.mainli.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 来源 https://juejin.cn/user/923245500710296/posts
 */
@SuppressLint("AppCompatCustomView")
public class EditableTextView extends TextView {

    private RectF inputRect = new RectF();


    //边框颜色
    private int boxColor = Color.BLACK;

    //光标是否可见
    private boolean isCursorVisible = true;
    //光标
    private Drawable textCursorDrawable;
    //光标宽度
    private float cursorWidth = dp2px(2);
    //光标高度
    private float cursorHeight = dp2px(36);
    //光标闪烁控制
    private boolean isShowCursor;
    //字符数量控制
    private int inputBoxNum = 5;
    //间距
    private int mBoxSpace = 10;
    // box radius
    private float boxRadius = dp2px(0);

    InputFilter[] inputFilters = new InputFilter[]{
            new InputFilter.LengthFilter(inputBoxNum)
    };


    public EditableTextView(Context context) {
        this(context, null);
    }

    public EditableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setFocusable(true); //支持聚焦
        super.setFocusableInTouchMode(true); //支持触屏模式聚焦
        //可点击，因为在触屏模式可聚焦的view一般是可以点击的，这里你也可以设置个clickListener，效果一样
        super.setClickable(true);
        super.setGravity(Gravity.CENTER_VERTICAL);
        super.setMaxLines(1);
        super.setSingleLine();
        super.setFilters(inputFilters);
        super.setLongClickable(false);// 禁止复制、剪切
        super.setTextIsSelectable(false); // 禁止选中

        Drawable cursorDrawable = getTextCursorDrawable();
        if (cursorDrawable == null) {
            cursorDrawable = new PaintDrawable(Color.MAGENTA);
            setTextCursorDrawable(cursorDrawable);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.setPointerIcon(null);
        }
        super.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        //禁用ActonMode弹窗
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
        }
        mBoxSpace = (int) dp2px(10f);

    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return null;
    }

    @Override
    public boolean hasSelection() {
        return false;
    }

    @Override
    public boolean showContextMenu() {
        return false;
    }

    @Override
    public boolean showContextMenu(float x, float y) {
        return false;
    }

    public void setBoxSpace(int mBoxSpace) {
        this.mBoxSpace = mBoxSpace;
        postInvalidate();
    }

    public void setInputBoxNum(int inputBoxNum) {
        if (inputBoxNum <= 0) return;
        this.inputBoxNum = inputBoxNum;
        this.inputFilters[0] = new InputFilter.LengthFilter(inputBoxNum);
        super.setFilters(inputFilters);
    }

    @Override
    public void setClickable(boolean clickable) {

    }

    @Override
    public void setLines(int lines) {

    }

    @Override
    protected boolean getDefaultEditable() {
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        TextPaint paint = getPaint();

        float strokeWidth = paint.getStrokeWidth();
        if (strokeWidth == 0) {
            //默认Text是没有strokeWidth的，为了防止绘制边缘存在问题，这里强制设置 1dp
            paint.setStrokeWidth(dp2px(1));
            strokeWidth = paint.getStrokeWidth();
        }
        paint.setTextSize(getTextSize());

        float boxWidth = (getWidth() - strokeWidth * 2f - (inputBoxNum - 1) * mBoxSpace) / inputBoxNum;
        float boxHeight = getHeight() - strokeWidth * 2f;
        int saveCount = canvas.save();

        Paint.Style style = paint.getStyle();
        Paint.Align align = paint.getTextAlign();
        paint.setTextAlign(Paint.Align.CENTER);

        String text = getText().toString();
        int length = text.length();

        int color = paint.getColor();

        for (int i = 0; i < inputBoxNum; i++) {

            inputRect.set(i * (boxWidth + mBoxSpace) + strokeWidth,
                    strokeWidth,
                    strokeWidth + i * (boxWidth + mBoxSpace) + boxWidth,
                    strokeWidth + boxHeight);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(boxColor);
            //绘制边框
            canvas.drawRoundRect(inputRect, boxRadius, boxRadius, paint);

            //设置当前TextColor
            int currentTextColor = getCurrentTextColor();
            paint.setColor(currentTextColor);
            paint.setStyle(Paint.Style.FILL);
            if (text.length() > i) {
                // 绘制文字，这里我们不过滤空格，当然你可以在InputFilter中处理
                String CH = String.valueOf(text.charAt(i));
                int baseLineY = (int) (inputRect.centerY() + getTextPaintBaseline(paint));//基线中间点的y轴计算公式
                canvas.drawText(CH, inputRect.centerX(), baseLineY, paint);
            }

            //绘制光标
            if (i == length && isCursorVisible && length < inputBoxNum) {
                Drawable textCursorDrawable = getTextCursorDrawable();
                if (textCursorDrawable != null) {
                    if (!isShowCursor) {
                        textCursorDrawable.setBounds((int) (inputRect.centerX() - cursorWidth / 2f), (int) ((inputRect.height() - cursorHeight) / 2f), (int) (inputRect.centerX() + cursorWidth / 2f), (int) ((inputRect.height() - cursorHeight) / 2f + cursorHeight));
                        textCursorDrawable.draw(canvas);
                        isShowCursor = true; //控制光标闪烁 blinking
                    } else {
                        isShowCursor = false;//控制光标闪烁 no blink
                    }
                    removeCallbacks(invalidateCursor);
                    postDelayed(invalidateCursor, 500);
                }
            }
        }

        paint.setColor(color);
        paint.setStyle(style);
        paint.setTextAlign(align);

        canvas.restoreToCount(saveCount);
    }


    private Runnable invalidateCursor = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * 基线到中线的距离=(Descent+Ascent)/2-Descent
     * 注意，实际获取到的Ascent是负数。公式推导过程如下：
     * 中线到BOTTOM的距离是(Descent+Ascent)/2，这个距离又等于Descent+中线到基线的距离，即(Descent+Ascent)/2=基线到中线的距离+Descent。
     */
    public static float getTextPaintBaseline(Paint p) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    /**
     * 控制是否保存完整文本
     *
     * @return
     */
    @Override
    public boolean getFreezesText() {
        return true;
    }

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }

    /**
     * 控制光标展示
     *
     * @return
     */
    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ArrowKeyMovementMethod.getInstance();
    }

    @Override
    public boolean isCursorVisible() {
        return isCursorVisible;
    }

    @Override
    public void setTextCursorDrawable(@Nullable Drawable textCursorDrawable) {
//        super.setTextCursorDrawable(null);
        this.textCursorDrawable = textCursorDrawable;
        postInvalidate();
    }

    @Nullable
    @Override
    public Drawable getTextCursorDrawable() {
        return textCursorDrawable;  //支持android Q 之前的版本
    }

    @Override
    public void setCursorVisible(boolean cursorVisible) {
        isCursorVisible = cursorVisible;
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setBoxRadius(float boxRadius) {
        this.boxRadius = boxRadius;
        postInvalidate();
    }

    public void setBoxColor(int boxColor) {
        this.boxColor = boxColor;
        postInvalidate();
    }

    public void setCursorHeight(float cursorHeight) {
        this.cursorHeight = cursorHeight;
        postInvalidate();
    }

    public void setCursorWidth(float cursorWidth) {
        this.cursorWidth = cursorWidth;
        postInvalidate();
    }

    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        final long DELAY_BEFORE_HANDLE_FADES_OUT = 4000;
        if (delayMillis == DELAY_BEFORE_HANDLE_FADES_OUT
                && action.getClass().getName().startsWith("android.widget.Editor$InsertionHandleView$")) {
            delayMillis = 0;
        }
        return super.postDelayed(action, delayMillis);
    }
}


