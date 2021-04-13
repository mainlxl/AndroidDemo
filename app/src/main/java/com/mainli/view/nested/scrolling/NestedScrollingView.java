package com.mainli.view.nested.scrolling;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;

/**
 * 整体流程
 * <p>
 * -> child.ACTION_DOWN
 *  |-> child.startNestedScroll
 *  |-> parent.onStartNestedScroll (如果返回false，则流程终止)
 *  |-> parent.onNestedScrollAccepted
 * -> child.ACTION_MOVE
 *  |-> child.dispatchNestedPreScroll
 *  |-> parent.onNestedPreScroll
 * -> child.ACTION_UP
 *  |-> chid.stopNestedScroll
 *  |-> parent.onStopNestedScroll
 * -> child.fling
 *  |-> child.dispatchNestedPreFling
 *  |-> parent.onNestedPreScroll
 *  |-> child.dispatchNestedFling
 *  |-> parent.onNestedFling
 * </p>
 * 作者：Mr_villain
 * 链接：https://www.jianshu.com/p/20efb9f65494
 * 来源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 */
public class NestedScrollingView extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {
    public NestedScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    //如果此ViewParent接受嵌套滚动操作，则返回true
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return true;
    }

    /**
     * <p>在* {@link #onStartNestedScroll（View，View，int）onStartNestedScroll}之后将调用此方法返回true。
     * 它为View及其超类提供了一个机会，可以为嵌套滚动执行初始配置*。如果存在一个，那么这个方法的实现应该总是调用它们的超类的*实现。
     * </p>
     */
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {

    }

    //嵌套滑动结束时会触发这个方法
    @Override
    public void onStopNestedScroll(@NonNull View target) {

    }

    /**
     * 子View滑动时会触发这个方法，dyConsumed，dyUnconsumed，比如RecyclerView滑到了边界，那么会有一部分y未消耗掉
     *
     * @param target
     * @param dxConsumed   代表子View滑动的距离
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed 代表子View本次滑动未消耗的距离
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    /**
     * 在目标View占用滚动的一部分之前，对正在进行的嵌套滚动作出反应。
     *
     * @param target
     * @param dx       表示滑动的X距离
     * @param dy       表示滑动的Y距离
     * @param consumed 数组代表父View要消耗的距离,假如consumed[1] = dy,那么子View就不会滑动了
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {

    }

    /**
     * 当子View fling时，会触发这个回调
     * 如RecyclerView滑动到了边界，那么它显然没法消耗本次的fling
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @param consumed  代表速度是否被子View消耗掉
     * @return
     */
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 当子View要开始fling时，会先询问父View是否要拦截本次fling，
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @return true表示要拦截，那么子View就不会惯性滑动了
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    /**
     * @return 表示目前正在进行的嵌套滑动的方向
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @see ViewCompat#SCROLL_AXIS_VERTICAL
     * @see ViewCompat#SCROLL_AXIS_NONE
     */
    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    //-----------------------------------------NestedScrollingChild-----------------------------------------------------------------------------

    /**
     * 启用或禁用此View的嵌套滚动,View默认实现好了
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {

    }

    //当前子View是否支持嵌套滑动
    @Override
    public boolean isNestedScrollingEnabled() {
        return false;
    }

    //开始嵌套滑动，对应Parent的onStartNestedScroll
    @Override
    public boolean startNestedScroll(int axes) {
        return false;
    }

    //停止本次嵌套滑动，对应Parent的onStopNestedScroll
    @Override
    public void stopNestedScroll() {

    }

    //true表示这个子View有一个支持嵌套滑动的父View
    @Override
    public boolean hasNestedScrollingParent() {
        return false;
    }

    /**
     * 通知父View子View开始滑动了，对应父View的onNestedScroll方法
     *
     * @param dxConsumed     此滚动步骤中此View消耗的水平距离（以像素为单位）
     * @param dyConsumed
     * @param dxUnconsumed   此View未消耗的水平滚动距离（以像素为单位）
     * @param dyUnconsumed
     * @param offsetInWindow
     * @return
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return false;
    }

    /**
     * View占用滚动的一部分之前，对正在进行的嵌套滚动作出反应。
     *
     * @param dx
     * @param dy
     * @param consumed       数组代表父View要消耗的距离,假如consumed[1] = dy,那么子View就不会滑动了
     * @param offsetInWindow 可选的。如果不为null，则返回时将包含此View在此操作之前,到完成之后的本地View坐标中的偏移量。View实现可以使用它来调整,预期的输入坐标跟踪
     * @return
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return false;
    }

    /**
     * 通知父View开始Fling了，对应Parent的onNestedFling方法
     *
     * @param velocityX
     * @param velocityY
     * @param consumed  如果子View消耗了，则为true，否则为false
     * @return 如果嵌套的滚动父级消耗或以其他方式响应fling，则返回true
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 通知父View要开始fling了，对应Parent的onNestedPreFling方法
     *
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return false;
    }
}
