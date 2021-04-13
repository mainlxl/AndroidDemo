package com.mainli.view.turntable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class TurntableBackgroundView extends View implements Runnable {
    public TurntableBackgroundView(Context context) {
        super(context);
    }

    public static final String TAG = TurntableBackgroundView.class.getSimpleName();
    private static final double DEFAULT_SPEED = 15.0;
    private static final int DEFAULT_REFRESH_FREQUENCY = 10;
    private static final float INSIDE_RADIUS_SCALES = .86f;
    private static final int DEFAULT_LIGHT_REFRESH_FREQUENCY = 500;
    private static final int STARTED_LIGHT_REFRESH_FREQUENCY = 100;
    private static final int LIGHT_COUNT = 18;
    private static final float SLOW_STEP = 0.25f;

    private static final int LIGHT_SLOW_STEP = (DEFAULT_LIGHT_REFRESH_FREQUENCY - STARTED_LIGHT_REFRESH_FREQUENCY) / ((int) (DEFAULT_SPEED / SLOW_STEP + 1));

    private static final int STARTED = 0x01;
    private static final int STOPPING = 0x02;
    private static final int STOPPED = 0x03;

    private AtomicInteger mState = new AtomicInteger(STOPPED);

    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    /**
     * 抽奖的文字
     */
    private Bitmap mDefaultPrizeBmp;

    /**
     * 与文字对应图片的bitmap数组
     */
    private ArrayList<Bitmap> mImgsBitmap = new ArrayList<Bitmap>();
    /**
     * 盘块的个数
     */
    private int mItemCount;

    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();

    private RectF mViewRegion = new RectF();
    /**
     * 圆的直径
     */
    private int mDiameter;
    /**
     * 绘制盘快的画笔
     */
    private Paint mArcPaint;

    private Paint mDefaultPaint;

    private Paint mLightPaint;

    private PointF[] mLightPos = new PointF[18];

    private int[] mLightColor = new int[]{0xff9a6dff, 0xffff9e00, 0xff16cde1};

    private boolean mBlink = false;

    private Paint mShadowPaint;

    private int mLightRefresh = DEFAULT_LIGHT_REFRESH_FREQUENCY;

    private double bigArc; //每个大扇形的弧度
    private float bigAngle;
    private double paddingRadius = 0;
    private double expectRadius;
    private double bigRadius;
    private int centerX;
    private int centerY;

    /**
     * 滚动的速度
     */
    private double mSpeed;
    private double mDeltaAngle;
    private volatile float mCurAngle = 0;

    private int mWidth;
    private int mHeight;

    private boolean isInBackground = false; // 当前view是否显示
    private boolean hasNoPosition = true; // 没有设置目标位置

    private LuckySpinListener mListener;

    private final static boolean DEBUG = false;

    private Paint mTextPaint;

    private int mType;

    public interface LuckySpinListener {
        void onLuckyStart(boolean isStart);

        void onLuckyEnd(int flag);
    }


    /*
     * 为转盘添加奖品信息
     * */
    public void setupPrizes(Bitmap[] bitmaps) {
        mItemCount = bitmaps.length;
        mImgsBitmap.addAll(Arrays.asList(bitmaps));
        invalidate();
    }

    private Context mContext;

    public TurntableBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // setZOrderOnTop(true);// 设置画布 背景透明
        // mHolder.setFormat(PixelFormat.TRANSLUCENT);
        init(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /**
     * 设置控件为正方形
     */

    private void init(Context context) {

        mItemCount = 5;

        // 初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mArcPaint.setAntiAlias(true);

        mDefaultPaint = new Paint();
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setColor(0xFFFFE000);

        mLightPaint = new Paint();
        mLightPaint.setAntiAlias(true);
        mLightPaint.setColor(0xFFFF0000);

        mShadowPaint = new Paint();
        mShadowPaint.setColor(0xff0d112b);
        mShadowPaint.setAntiAlias(false);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(60);

        // 开启线程
        isRunning = true;

        //postDelayed(mLightRunnable, DEFAULT_LIGHT_REFRESH_FREQUENCY);

    }

    @Override
    public synchronized void run() {
        // 不断的进行draw
        if (mSpeed > 0 && isRunning) {
            if (!isInBackground) {
                if (hasNoPosition) {
                    invalidate();
                    mCurAngle += mSpeed;
                } else {
                    // 匀速一段时间，当到达可以减速的时候设置isShouldEnd=true
                    if (mDeltaAngle > 0) {
                        mDeltaAngle -= mSpeed;
                        if (mDeltaAngle <= 0) {
                            mState.set(STOPPING);
                        }
                    }
                    invalidate();
                    // 如果mSpeed不等于0，则相当于在滚动
                    mCurAngle += mSpeed;

                    // 点击停止时，设置mSpeed为递减，为0值转盘停止
                    if (mState.get() == STOPPING) {
                        mSpeed -= SLOW_STEP;
                        if (mLightRefresh < DEFAULT_LIGHT_REFRESH_FREQUENCY) {
                            mLightRefresh += LIGHT_SLOW_STEP;
                        }
                    }
                    if (mSpeed <= 0) {
                        mSpeed = 0;
                        mLightRefresh = DEFAULT_LIGHT_REFRESH_FREQUENCY;
                        stopBlink();
                        if (mListener != null) {
                            mListener.onLuckyEnd(mType);
                        }
                        postDelayed(this, 300);
                        return;
                    }
                }
            }
            postDelayed(this, DEFAULT_REFRESH_FREQUENCY);
        } else {
            mState.set(STOPPED);
        }
    }

    private Runnable mLightRunnable = new Runnable() {
        @Override
        public void run() {
            mBlink = !mBlink;
            invalidate();
            postDelayed(mLightRunnable, mLightRefresh);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
        isInBackground = true;
        removeCallbacks(this);
        removeCallbacks(mLightRunnable);
    }

    private void drawCenter(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(255, 250, 213, 21));
        paint.setAntiAlias(true);
        canvas.drawCircle(mRange.centerX(), mRange.centerY(), dp2px(56), mShadowPaint);
    }

    private void drawLight(Canvas canvas) {
        final int count = LIGHT_COUNT;
        final float roundRadius = (float) mDiameter / 2;
        float radius = (roundRadius - roundRadius * INSIDE_RADIUS_SCALES) / 2 * 0.45f;
        int offset = mBlink ? 1 : 0;
        for (int idx = 0; idx < count; ++idx) {
            mLightPaint.setColor(mLightColor[(idx + offset) % 3]);
            canvas.drawCircle(mLightPos[idx].x, mLightPos[idx].y, radius, mLightPaint);
        }
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域 绘制背景，不重要，完全为了美观
     */
    private void drawBg(Canvas canvas) {
        mDefaultPaint.setColor(0xffe5e6f1);
        canvas.drawCircle(mRange.centerX(), mRange.centerY(), (mDiameter) / 2, mDefaultPaint);
//        mDefaultPaint.setColor(Color.argb(255, 36, 41, 65));
//        canvas.drawCircle(mRange.centerX(), mRange.centerY(), (mDiameter * INSIDE_RADIUS_SCALES) / 2, mDefaultPaint);
    }

    private void drawSector(Canvas canvas) {
        canvas.save();
        float tmpAngle = mCurAngle;
        float angle = (float) (360 / mItemCount);
        for (int i = 0; i < mItemCount; i++) {
            //旋转画布
            if (i != 0) {
                canvas.rotate(-angle, 0, 0);
            } else {
                canvas.translate(mRange.centerX(), mRange.centerY());
                canvas.rotate(tmpAngle);
            }
            // 绘制快快
            if (i % 2 == 0) {
                mArcPaint.setColor(0xff16cde1);
            } else {
                mArcPaint.setColor(0xfff5dfad);
            }

            // 绘制扇形
            drawArcWithCircle(canvas, mArcPaint, 270 - angle / 2);
            // 绘制Icon
            drawIcon(canvas, i);

        }
        canvas.restore();
    }

    private void drawArcWithCircle(Canvas canvas, Paint paint, float startAngle) {
        double startArc = startAngle * Math.PI / 180d;
        float newCenterX = (float) (centerX + (Math.cos(startArc + bigArc / 2)) * paddingRadius);
        float newCenterY = (float) (centerY + (Math.sin(startArc + bigArc / 2)) * paddingRadius);
        //绘制扇形
        RectF arc = new RectF((float) (newCenterX - bigRadius), (float) (newCenterY - bigRadius), (float) (newCenterX + bigRadius), (float) (newCenterY + bigRadius));
        canvas.drawArc(arc, startAngle, bigAngle, true, paint);
    }

    /**
     * 绘制图片
     *
     * @param i
     */
    private void drawIcon(Canvas canvas, int i) {
        // 设置图片的宽度为直径的1/6
        int imgWidth = (int) (mDiameter / 6.2F);

        int x = 0;
        int y = -mDiameter * 2 / 6;
        //int y = (int)(mRange.centerY() - expectRadius / 2);
        // 确定绘制图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        canvas.drawBitmap(mImgsBitmap.get(i), null, rect, null);

        if (DEBUG) {
            canvas.drawText(String.valueOf(i + 1), rect.centerX(), rect.centerY(), mTextPaint);
        }

    }

    public void stopBlink() {
        removeCallbacks(mLightRunnable);
    }

    public void startBlink() {
        removeCallbacks(mLightRunnable);
        postDelayed(mLightRunnable, mLightRefresh);
    }

    public void destroy() {

        if (mDefaultPrizeBmp != null) {
            mDefaultPrizeBmp.recycle();
            mDefaultPrizeBmp = null;
        }
    }

    private void initLightPos() {
        final float roundRadius = (float) mDiameter / 2;
        float radius = (roundRadius * INSIDE_RADIUS_SCALES + roundRadius) / 2;
        float round = (float) Math.PI * 2;
        for (int idx = 0; idx < mLightPos.length; ++idx) {
            final float x = mRange.centerX() + radius * (float) Math.cos(idx * round / mLightPos.length);
            final float y = mRange.centerY() + radius * (float) Math.sin(idx * round / mLightPos.length);
            mLightPos[idx] = new PointF(x, y);
        }
    }

    private void initArcParams() {
        bigArc = 2 * Math.PI / mItemCount; //每个大扇形的弧度
        bigAngle = 360f / mItemCount;
        expectRadius = mDiameter / 2 * INSIDE_RADIUS_SCALES * 0.985;
        bigRadius = expectRadius * (expectRadius * Math.cos(bigArc / 2) - paddingRadius) / expectRadius / Math.cos(bigArc / 2);
        centerX = 0;
        centerY = 0;
    }

    private int getInternalHorizontalPadding() {
        return dp2px(0);
    }

    private int getInternalVerticalPadding() {
        return dp2px(0);
    }

    public synchronized void luckyStart(final LuckySpinListener listener) {
        if (mState.compareAndSet(STOPPED, STARTED)) {
            startBlink();
            mType = -1;
            mListener = listener;
            post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onLuckyStart(true);
                    }
                    mSpeed = DEFAULT_SPEED;
                    hasNoPosition = true;
                    TurntableBackgroundView.this.run();
                }
            });
        }
    }

    public synchronized void setLuckyPos(long duration, final int idx, final int type) {
        if (mState.get() == STARTED) {
            mType = type;
            final int delay = (int) duration / 2;
            post(new Runnable() {

                @Override
                public void run() {
                    mCurAngle = mCurAngle % 360;
                    mDeltaAngle = 0;
                    mSpeed = DEFAULT_SPEED;
                    mLightRefresh = STARTED_LIGHT_REFRESH_FREQUENCY;

                    // 每项角度大小
                    float angle = (float) (360 / mItemCount);

                    // 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210-270；）
                    int expectCount = delay / DEFAULT_REFRESH_FREQUENCY;
                    float fullAngle = expectCount * (float) DEFAULT_SPEED;

                    int index = idx - 1;

                    float from = 360 - (mCurAngle + fullAngle) % 360 + angle * index + (float) Math.random() * 15;

                    // 停下来时旋转的距离
                    float targetFrom = from;

                    int n = (int) (DEFAULT_SPEED / SLOW_STEP + 1);
                    float realAngle = (float) (DEFAULT_SPEED * n / 2.f) % 360;
                    mDeltaAngle = (targetFrom - realAngle) > 0 ? (targetFrom - realAngle) : (targetFrom - realAngle) + 360;
                    mDeltaAngle += fullAngle;
                    hasNoPosition = false;
                }
            });
        }
    }

    public synchronized void luckyEnd(final int luckyIndex) {
        final int state = mState.get();
        if (state == STARTED) {
            post(new Runnable() {
                @Override
                public void run() {
                    // 每项角度大小
                    float angle = (float) (360 / mItemCount);
                    // 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210-270；）
                    float from = 360 - mCurAngle % 360 + angle * luckyIndex;

                    Log.i(TAG, "mStartAngle=" + mCurAngle + ":" + (mCurAngle % 360) + " FROM=" + from + ":" + (from % 360));
                    float to = from + angle;

                    // 停下来时旋转的距离
                    float targetFrom = from;

                    int n = (int) (DEFAULT_SPEED / SLOW_STEP + 1);
                    float realAngle = (float) (DEFAULT_SPEED * n / 2.f) % 360;
                    mDeltaAngle = (targetFrom - realAngle) > 0 ? (targetFrom - realAngle) : (targetFrom - realAngle) + 360;
                }
            });
        }
    }

    public boolean isStart() {
        return mState.get() == STARTED;
    }

    public boolean isStopped() {
        return mState.get() == STOPPED;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mWidth = mWidth == 0 ? getMeasuredWidth() : mWidth;
        int internalHorizontalPadding = getInternalHorizontalPadding();
        final int internalVerticalPadding = getInternalVerticalPadding();
        mDiameter = mWidth - internalHorizontalPadding - internalHorizontalPadding;
        mHeight = mDiameter + internalVerticalPadding + internalVerticalPadding;

        mViewRegion.set(0, 0, mWidth, mHeight);
        mRange.set(internalHorizontalPadding, internalVerticalPadding, mDiameter + internalHorizontalPadding, mDiameter + internalVerticalPadding);

        setMeasuredDimension(mWidth, mHeight);
        initArcParams();
        initLightPos();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            // 获得canvas
            if (canvas != null) {
                drawBg(canvas);
                drawSector(canvas);
//                drawCenter(canvas);
                drawLight(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public int dp2px(float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
