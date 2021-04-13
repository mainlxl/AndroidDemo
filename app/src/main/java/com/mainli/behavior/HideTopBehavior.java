package com.mainli.behavior;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lixiaoliang on 2018-5-3.
 */
public class HideTopBehavior extends CoordinatorLayout.Behavior<View> {
    public HideTopBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int changeMaxY = -1;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (changeMaxY == -1) {
            changeMaxY = child.getHeight();
        }
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    int offsetTotal = 0;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        int old = offsetTotal;//记录现在的位置
        offsetTotal -= dyConsumed;//叠加上滑动的距离
        offsetTotal = middle(offsetTotal, -changeMaxY, 0);//滑动后的位置不能超出-child.getHeight()和0
        if (offsetTotal == old) {
            return;
        }
        child.offsetTopAndBottom(offsetTotal - old);//计算出在原来的基础了滑动（偏移）了多少值
    }


    /**
     * 取中间值，即：不能超过最大值，不能小于最小值。
     */
    private int middle(int cur, int min, int max) {
        if (cur < min) {
            return min;
        } else if (cur > max) {
            return max;
        } else {
            return cur;
        }
    }
}
