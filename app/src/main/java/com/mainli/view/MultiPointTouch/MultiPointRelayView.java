package com.mainli.view.MultiPointTouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import com.mainli.R;
import com.mainli.utils.BitmapUtils;
import com.mainli.utils.SizeUtil;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 * 多点触控之接替滑动
 */
public class MultiPointRelayView extends View implements NestedScrollingChild {


    private Bitmap mBitmap;
    private int mOffsetX;
    private int mOffsetY;
    private int mTouchSlop;
    //父View提前消费
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];
    private final NestedScrollingChildHelper mChildHelper;

    public MultiPointRelayView(Context context) {
        super(context);
    }

    public MultiPointRelayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    {
        mChildHelper = new NestedScrollingChildHelper(this);
        mChildHelper.setNestedScrollingEnabled(true);
        mBitmap = BitmapUtils.getTargetWidthBitmap(getResources(), R.mipmap.logo_square, SizeUtil.dp2PixelsInt(200));
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private int mOldX;
    private int mOldY;
    private int mActivePointId;
    private boolean mIsBeingDragged;

    /**
     * 触摸点有(x,y,index,id)等数据
     * <p>
     * 当多个触摸点,其中之一抬起时 index 发生改变 id不会变
     * 我们记录当前触摸的id  发生ACTION_POINTER_UP时 判断是否是当前控制手指抬起,如果不是 什么也不做,如果是则根据策略选择下一手指接替
     * <p>
     * 1. 获取当前非ACTION_MOVE时间触发手指index 使用event.getActionIndex()方法
     * 2. 通过index转化得到ID 使用event.getPointerId(index)方法
     * 3. 通过id获取该id当前index 使用event.findPointerIndex(id)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mActivePointId = event.getPointerId(event.getActionIndex());
                mOldX = (int) event.getX();
                mOldY = (int) event.getY();
                mIsBeingDragged = false;
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL | ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_MOVE:
                int activeIndex = event.findPointerIndex(mActivePointId);
                int x = (int) event.getX(activeIndex);
                int y = (int) event.getY(activeIndex);
                int offsetX = x - mOldX;
                int offsetY = y - mOldY;
                //消耗触摸前分发
                if (dispatchNestedPreScroll(offsetX, offsetX, mScrollConsumed, mScrollOffset)) {
                    offsetX -= mScrollConsumed[0];
                    offsetX -= mScrollConsumed[1];
                    event.offsetLocation(mScrollOffset[0], mScrollOffset[1]);

                }
                if (!mIsBeingDragged && (Math.abs(offsetX) > mTouchSlop || Math.abs(offsetY) > mTouchSlop)) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (offsetX > 0) {
                        offsetX -= mTouchSlop;
                    } else {
                        offsetX += mTouchSlop;
                    }
                }
                if (mIsBeingDragged) {
                    offsetX += mScrollOffset[0];
                    offsetY += mScrollOffset[1];
                    //消耗触摸后分发
                    int tmpX = mOffsetX + offsetX;
                    int unconsumedX = 0, unconsumedY = 0;
                    int consumedX = 0, consumedY = 0;

                    if (tmpX < 0) {
                        consumedX = -mOffsetX;
                        unconsumedX = offsetX + mOffsetX;
                    } else if (tmpX > getWidth() - mBitmap.getWidth()) {
                        unconsumedX = (tmpX - (getWidth() - mBitmap.getWidth()));
                        consumedX = (offsetX - unconsumedX);
                    } else {
                        consumedX = offsetX;
                        unconsumedX = 0;
                    }
                    consumedY = offsetY;
                    unconsumedY = 0;
                    dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, mScrollOffset);
                    mOffsetX += consumedX;
                    mOffsetY += consumedY;
                    mOldX = x - mScrollOffset[0];
                    mOldY = y - mScrollOffset[1];
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                activeIndex = event.getActionIndex();
                mActivePointId = event.getPointerId(activeIndex);
                mOldX = (int) event.getX(activeIndex);
                mOldY = (int) event.getY(activeIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                activeIndex = event.getActionIndex();
                int pointerUpId = event.getPointerId(activeIndex);
                if (pointerUpId == mActivePointId) {//当前正在使用PointId手指抬起
                    /*抬起手指时找寻最靠前按下手指*/
//                    if (activeIndex == 0) {
//                        activeIndex = 1;
//                    } else {
//                        activeIndex = 0;
//                    }
                    /*抬起手指时找寻最靠后按下手指*/
                    if (activeIndex == event.getPointerCount() - 1) {
                        activeIndex--;
                    } else {
                        activeIndex = event.getPointerCount() - 1;
                    }
                    mOldX = (int) event.getX(activeIndex);
                    mOldY = (int) event.getY(activeIndex);
                    mActivePointId = event.getPointerId(activeIndex);
                }
                break;
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                mScrollOffset[0] = 0;
                mScrollOffset[1] = 0;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, mOffsetX, mOffsetY, null);
    }

    //---------------------嵌套滚动-----------------------------------------------

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }


    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }


    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
