package com.mainli.floating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mainli.utils.SizeUtil;

import androidx.annotation.NonNull;

/**
 * 悬浮窗实现
 */
public class FloatingRootLayout extends FrameLayout {
    private WindowManager.LayoutParams mWindowLayoutParams;
    private MyWindowManager mWindowManager;
    private boolean isAddToWindow = false;
    private FloatingOnTouchListener mFloatingOnTouchListener;
    private OnFloatingWindowKeyDownListener mOnFloatingWindowKeyDownListener;

    public FloatingRootLayout(@NonNull Context context) {
        this(context, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, //
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL //
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不获取焦点获取用户back按键
                        //将窗口放置在整个屏幕中，而忽略边框周围的装饰（例如状态栏） 结合WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR显示在状态栏下
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
    }

    /**
     * 贴START边 竖直基本居中(偏移半个宽高向下)
     * 需要修复偏移使用setStartPositionCenter方法设置初始位置
     */
    public FloatingRootLayout(@NonNull Context context, int width, int height, int flags) {
        this(context, width, height, 0, Integer.MIN_VALUE, flags);
    }

    public FloatingRootLayout(@NonNull Context context, int width, int height, int startX, int startY, int flags) {
        super(context);
        mWindowManager = new MyWindowManager(context);
        mWindowLayoutParams = createWindowLayoutParams(width, height, startX, startY, flags);
    }


    private WindowManager.LayoutParams createWindowLayoutParams(int width, int height, int startX, int startY, int flags) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.packageName = getContext().getPackageName();
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.x = startX == Integer.MIN_VALUE ? SizeUtil.getScreenWidthPixels() >> 1 : startX;
        layoutParams.y = startY == Integer.MIN_VALUE ? SizeUtil.getScreenHeightPixels() >> 1 : startY;
        //这里必须不能含有居中属性,会导致触摸错乱
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = flags;
        layoutParams.type = getWindowType();
        return layoutParams;
    }

    int isStartPositionCenter = 0;
    // 查看是否有起始位置设置标记
    int MASK_START_POSITION_CENTER_FLAG = 0x3;
    //水平起始位置居中标记
    int HORIZONTAL_START_POSITION_CENTER_FLAG = 0x1;
    //竖直起始位置居中标记
    int VERTICAL_START_POSITION_CENTER_FLAG = 0x2;

    /**
     * 设置起始位置 , 在不移动窗口的情况下  onSizeChanged中判断是移动 移动过大小改变则不再恢复初始位置
     *
     * @param horizontal 水平居中
     * @param vertical   竖直居中
     */
    public void setStartPositionCenter(boolean horizontal, boolean vertical) {
        if (horizontal) {
            isStartPositionCenter |= HORIZONTAL_START_POSITION_CENTER_FLAG;
        }
        if (vertical) {
            isStartPositionCenter |= VERTICAL_START_POSITION_CENTER_FLAG;
        }
    }

    /**
     * 显示悬浮窗,地址显示时会加入到WindowManager中 后续只会调用View的setVisibility
     */
    public void showFloating() {

        if (isAddToWindow() && getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            if (mHomeKeyEventListenerReceiver != null) {
                //显示时增加FLAG_NOT_FOCUSABLE使View可以获取到用户back按键操作
                mWindowLayoutParams.flags &= ~mWindowLayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager.updateViewLayout(this, mWindowLayoutParams);
            }
            return;
        }
        if (null != this.getParent()) {
            mWindowManager.removeView(this);
        }
        if (null != mWindowLayoutParams) {
            mWindowManager.addView(this, mWindowLayoutParams);
            isAddToWindow = true;
            try {
                getContext().registerReceiver(mHomeKeyEventListenerReceiver, mHomeKeyEventFilter);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void hideFloating() {
        if (isShown()) {
            if (mHomeKeyEventListenerReceiver != null) {
                //隐藏时去掉FLAG_NOT_FOCUSABLE使View可以获取不到用户back按键操作
                mWindowLayoutParams.flags |= mWindowLayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager.updateViewLayout(this, mWindowLayoutParams);
            }
            setVisibility(GONE);
        }
    }

    /**
     * 悬浮窗,显示隐藏状态切换
     */
    public void trigger() {
        if (!isAddToWindow() || getVisibility() != VISIBLE) {
            showFloating();
        } else {
            hideFloating();
        }
    }

    private boolean isAddToWindow() {
        return isAddToWindow && null != this.getParent();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //居中操作
        if ((mFloatingOnTouchListener == null || !mFloatingOnTouchListener.hasMoved) && (isStartPositionCenter & MASK_START_POSITION_CENTER_FLAG) != 0) {
            if ((isStartPositionCenter & HORIZONTAL_START_POSITION_CENTER_FLAG) > 0) {
                mWindowLayoutParams.x = (SizeUtil.getScreenWidthPixels() >> 1) - (w >> 1);
            }
            if ((isStartPositionCenter & VERTICAL_START_POSITION_CENTER_FLAG) > 0) {
                mWindowLayoutParams.y = (SizeUtil.getScreenHeightPixels() >> 1) - (h >> 1);
            }
            mWindowManager.updateViewLayout(this, mWindowLayoutParams);
        }
        //悬浮窗首次显示时贴标操作
        if (mFloatingOnTouchListener != null) {
            mFloatingOnTouchListener.tryAdsorption(this, w, h, mFloatingOnTouchListener.ADSORPTION_CRITICAL_VALUE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //清理拖动贴边任务
        if (mFloatingOnTouchListener != null) {
            mFloatingOnTouchListener.clear();
        }
    }

    //移除悬浮窗
    public void removeFloating() {
        if (isShown()) {
            mWindowManager.removeView(this);
            isAddToWindow = false;
        }
        getContext().unregisterReceiver(mHomeKeyEventListenerReceiver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                return onBack() ? true : super.dispatchKeyEvent(event);
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void setOnFloatingWindowKeyDownListener(OnFloatingWindowKeyDownListener onFloatingWindowKeyDownListener) {
        mOnFloatingWindowKeyDownListener = onFloatingWindowKeyDownListener;
    }


    private IntentFilter mHomeKeyEventFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    private BroadcastReceiver mHomeKeyEventListenerReceiver = new BroadcastReceiver() {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        //action内的某些reason
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";//home键旁边的最近程序列表键 方块键
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";//按下home键

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) { // 短按Home键
                    onHome();
                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {//Home键旁边的显示最近的程序的按钮
                    onMenu();
                }
            }
        }
    };

    /**
     * 设置悬浮窗可以拖动
     *
     * @param adsorption 拖动完成是否吸边
     */
    public void attachDrag(boolean adsorption) {
        if (mFloatingOnTouchListener != null) {
            mFloatingOnTouchListener.clear();
        }
        mFloatingOnTouchListener = new FloatingOnTouchListener(this, mWindowManager, mWindowLayoutParams, adsorption);
        setOnTouchListener(mFloatingOnTouchListener);
    }

    /**
     * 是否在拖动
     */
    public boolean isBeingDragged() {
        return mFloatingOnTouchListener == null ? false : mFloatingOnTouchListener.mIsBeingDragged;
    }

    /**
     * 悬浮窗拖动监听拦截,附加贴边吸附逻辑
     */
    private static class FloatingOnTouchListener implements OnTouchListener, Runnable {
        private final WindowManager.LayoutParams layoutParams;
        private final MyWindowManager windowManager;
        private View mView;
        //拖动完吸附相关
        private boolean adsorption;
        private int adsorptionType = 0x0;//0000
        private int ADSORPTIONTYPE_START_X = 0x1;//0001
        private int ADSORPTIONTYPE_END_X = 0x2;//0010
        private int ADSORPTIONTYPE_START_Y = 0x4;//0100
        private int ADSORPTIONTYPE_END_Y = 0x8;//1000
        private final int ADSORPTION_DELAY_TIME = 600;
        private static final int ADSORPTION_CRITICAL_VALUE = SizeUtil.dp2PixelsInt(40);
        private static final float ADSORPTION_HIDE_ALPHA_VALUE = 0.5f;
        //拖动相关
        private int x;
        private int y;
        private final int mTouchSlop;
        private boolean mIsBeingDragged;
        private boolean hasMoved = false;

        public FloatingOnTouchListener(View view, MyWindowManager windowManager, WindowManager.LayoutParams layoutParams, boolean adsorption) {
            this.windowManager = windowManager;
            this.layoutParams = layoutParams;
            this.adsorption = adsorption;
            this.mView = view;
            mTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    mIsBeingDragged = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    if (!mIsBeingDragged && (mTouchSlop <= Math.abs(movedX) || mTouchSlop <= Math.abs(movedY))) {
                        movedX += movedX > 0 ? mTouchSlop : -mTouchSlop;
                        movedY += movedY > 0 ? mTouchSlop : -mTouchSlop;
                        restoreViewState();
                        mIsBeingDragged = true;
                    }
                    if (mIsBeingDragged) {
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;

                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(view, layoutParams);
                        hasMoved = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (adsorption) {
                        tryAdsorption(view, view.getMeasuredWidth(), view.getMeasuredHeight(), ADSORPTION_CRITICAL_VALUE);
                    }
                    break;
            }
            return mIsBeingDragged;
        }

        private void restoreViewState() {
            mView.setTranslationX(0);
            mView.setTranslationY(0);
            mView.setAlpha(1f);
        }

        //尝试吸边 临界值为宽占时
        private void tryAdsorption(View view, int width, int height, int criticalValue) {
            int x = layoutParams.x;
            int y = layoutParams.y;
            boolean isUpdate = false;
            adsorptionType = 0;
            if (x < criticalValue) {
                isUpdate = true;
                layoutParams.x = 0;
                adsorptionType = ADSORPTIONTYPE_START_X;
            } else if ((x + criticalValue) >= (SizeUtil.getScreenWidthPixels() - width)) {
                isUpdate = true;
                layoutParams.x = SizeUtil.getScreenWidthPixels() - width;
                adsorptionType = ADSORPTIONTYPE_END_X;
            }
            if (y < criticalValue) {
                isUpdate = true;
                layoutParams.y = 0;
                adsorptionType |= ADSORPTIONTYPE_START_Y;
            } else if ((y + criticalValue) >= (SizeUtil.getScreenHeightPixels() - height)) {
                isUpdate = true;
                layoutParams.y = SizeUtil.getScreenHeightPixels() - height;
                adsorptionType |= ADSORPTIONTYPE_END_Y;
            }
            if (isUpdate) {
                windowManager.updateViewLayout(view, layoutParams);
                view.postDelayed(this, ADSORPTION_DELAY_TIME);
            }
        }

        @Override
        public void run() {
            adsorption();
        }

        /**
         * 吸附
         * tryAdsorption()发起吸附
         */
        private void adsorption() {
            if (adsorptionType != 0) {
                int translationX = 0, translationY = 0;
                if ((adsorptionType & ADSORPTIONTYPE_START_X) != 0) {
                    translationX = -(mView.getMeasuredWidth() >> 1);
                } else if ((adsorptionType & ADSORPTIONTYPE_END_X) != 0) {
                    translationX = (mView.getMeasuredWidth() >> 1);
                }
                mView.setTranslationX(translationX);
                if ((adsorptionType & ADSORPTIONTYPE_START_Y) != 0) {
                    translationY = -(mView.getMeasuredHeight() >> 1);
                } else if ((adsorptionType & ADSORPTIONTYPE_END_Y) != 0) {
                    translationY = (mView.getMeasuredHeight() >> 1);
                }
                mView.setTranslationY(translationY);
                mView.setAlpha(ADSORPTION_HIDE_ALPHA_VALUE);
            }
        }

        //清理拖动贴边任务
        private void clear() {
            mView.removeCallbacks(this);
        }
    }

    private final int getWindowType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    private static class MyWindowManager {
        private MyWindowManager(Context context) {
            mBase = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        private WindowManager mBase;

        public void updateViewLayout(View view, WindowManager.LayoutParams params) {
            try {
                mBase.updateViewLayout(view, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addView(View view, WindowManager.LayoutParams params) {
            try {
                mBase.addView(view, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void removeView(View view) {
            try {
                mBase.removeViewImmediate(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean onMenu() {
        if (mOnFloatingWindowKeyDownListener != null) {
            return mOnFloatingWindowKeyDownListener.onMenu(this);
        }
        return false;
    }

    protected boolean onBack() {
        if (mOnFloatingWindowKeyDownListener != null) {
            return mOnFloatingWindowKeyDownListener.onBack(this);
        }
        return false;
    }

    protected void onHome() {
        if (mOnFloatingWindowKeyDownListener != null) {
            mOnFloatingWindowKeyDownListener.onHome(this);
        }
    }

    public interface OnFloatingWindowKeyDownListener {
        boolean onMenu(FloatingRootLayout layout);

        boolean onBack(FloatingRootLayout layout);

        void onHome(FloatingRootLayout layout);
    }

}
