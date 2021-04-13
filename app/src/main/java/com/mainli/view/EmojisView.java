package com.mainli.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lixiaoliang on 2018-4-27.
 */
public class EmojisView extends View implements GestureDetector.OnGestureListener {
    private int rowCount = 3;
    private int columnsCount = 7;
    private int emojiPadding = 20;
    private int emojiTextSize = 80;
    private int placeHolderSize = emojiPadding * 2 + emojiTextSize;
    private int gapRow = 20;
    private int gapColumns = 20;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String[][] emojis = {//
            {"\ud83d\ude04", "\ud83d\ude02", "\ud83d\ude43", "\ud83d\ude18", "\ud83d\ude0d", "\ud83d\ude1b", "\ud83e\udd11"}, //
            {"\ud83d\ude0e", "\ud83d\ude0f", "\ud83d\ude12", "\ud83d\ude21", "\ud83e\udd24", "\ud83d\ude24", "\ud83e\udd21"}, //
            {"\ud83d\ude36", "\ud83d\ude31", "\ud83d\ude33", "\ud83d\ude30", "\ud83d\ude2d", "\ud83e\udd22", "\ud83e\udd10"}};
    private Rect[][] emojisRects = new Rect[rowCount][columnsCount];
    private float mFixHeight;
    private OnEmojiListener mOnSelectEmoji;
    private GestureDetector mGestureDetector;

    public EmojisView(Context context) {
        this(context, null);
    }

    public EmojisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EmojisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setClickable(true);
        paint.setTextSize(emojiTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        mFixHeight = (paint.descent() + paint.ascent()) / 2;
        mGestureDetector = new GestureDetector(context, this);
    }

    public void setEmojis(String[][] emojis) {
        this.emojis = emojis;
        rowCount = emojis.length;
        columnsCount = emojis[0].length;
        emojisRects = new Rect[rowCount][columnsCount];
    }

    public void setOnSelectEmoji(OnEmojiListener onSelectEmoji) {
        mOnSelectEmoji = onSelectEmoji;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(//
                getWidthSize(widthMeasureSpec, MeasureSpec.getMode(widthMeasureSpec)), //
                getHeightSize(heightMeasureSpec, MeasureSpec.getMode(heightMeasureSpec)));
    }


    private int getHeightSize(int heightMeasureSpec, int heightMode) {
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            int size = height - getPaddingTop() - getPaddingBottom();
            int targetSize = placeHolderSize * rowCount;
            if (size >= targetSize) {
                if (placeHolderSize >= 1) {
                    gapColumns = (int) ((size - targetSize) * 1f / (rowCount - 1));
                } else {
                    gapColumns = 0;
                }
            } else {
                gapColumns = 0;
                placeHolderSize = size / rowCount;
            }
        } else {
            height = (placeHolderSize + gapColumns) * rowCount - gapColumns + getPaddingTop() + getPaddingBottom();
        }
        return height;
    }

    private int getWidthSize(int widthMeasureSpec, int widthMode) {
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            int size = width - getPaddingLeft() - getPaddingRight();
            int targetSize = placeHolderSize * columnsCount;
            if (size >= targetSize) {
                if (placeHolderSize >= 1) {
                    gapRow = (int) ((size - targetSize) * 1f / (columnsCount - 1));
                } else {
                    gapRow = 0;
                }
            } else {
                gapRow = 0;
                placeHolderSize = size / columnsCount;
            }
        } else {
            width = (placeHolderSize + gapRow) * columnsCount - gapRow + getPaddingLeft() + getPaddingRight();
        }
        return width;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int itemSizeWidth = gapRow + placeHolderSize;
        int itemSizeHeight = gapColumns + placeHolderSize;
        for (int i = 0; i < rowCount; i++) {
            int heightSize = i * itemSizeHeight;
            int top = paddingTop + heightSize;
            int bottom = paddingTop + heightSize + placeHolderSize;
            for (int j = 0; j < columnsCount; j++) {
                int widthSize = j * itemSizeWidth;
                int left = paddingLeft + widthSize;
                int right = paddingLeft + widthSize + placeHolderSize;
                emojisRects[i][j] = new Rect(left, top, right, bottom);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                Rect rect = emojisRects[i][j];
                String text = emojis[i][j];
                if (TextUtils.isEmpty(text)) {
                    canvas.drawText("←", rect.centerX(), rect.centerY() - mFixHeight, paint);
                } else {
                    canvas.drawText(text, rect.centerX(), rect.centerY() - mFixHeight, paint);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mOnSelectEmoji != null) {
            float x = e.getX();
            float y = e.getY();
            for (int i = 0; i < rowCount; i++) {
                Rect row = emojisRects[i][0];
                if (row.top <= y && row.bottom >= y) {//先判断行
                    for (int j = 0; j < columnsCount; j++) {
                        Rect columns = emojisRects[i][j];
                        if (columns.left <= x && columns.right >= x) {//后判断列
                            String emojiUnicode = emojis[i][j];
                            if (TextUtils.isEmpty(emojiUnicode)) {
                                mOnSelectEmoji.onDeleteTheEmojiBefore();
                            } else {
                                mOnSelectEmoji.onInsertEmoji(emojiUnicode);
                            }
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnEmojiListener {
        void onInsertEmoji(String emojiUnicode);

        void onDeleteTheEmojiBefore();
    }
}
