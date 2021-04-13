package com.mainli.behavior;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lixiaoliang on 2018-5-3.
 */
public class HideBottomBehavior extends CoordinatorLayout.Behavior<View> {
    public HideBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int changeMaxY = -1;

    int offsetTotal = 0;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (changeMaxY == -1) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            changeMaxY = child.getHeight() + layoutParams.bottomMargin;
        }
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        int old = offsetTotal;//记录现在的位置
        offsetTotal -= dyConsumed;//叠加上滑动的距离
        offsetTotal = middle(offsetTotal, -changeMaxY, 0);//滑动后的位置不能超出-child.getHeight()和0
        if (offsetTotal == old) {
            return;
        }
        int delta = offsetTotal - old;//计算出在原来的基础了滑动（偏移）了多少值
        child.offsetTopAndBottom(-delta);
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
