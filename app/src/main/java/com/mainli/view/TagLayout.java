package com.mainli.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 标签布局
 * TODO 目前只支持从左侧开始显示,单行top对齐,待开发行内竖直对齐以及每一行居左居右或居中整体对齐方式
 */
public class TagLayout extends ViewGroup {
    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthUsed = 0, heightUsed = 0, lineMaxWidth = 0, lineMaxHeight = 0;
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int childViewMeasuredWidth = childView.getMeasuredWidth();
            int childViewMeasuredHeightAndMargin = childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            //更新当前行最高高度
            lineMaxHeight = Math.max(lineMaxHeight, childViewMeasuredHeightAndMargin);
            if (checkNextLine(widthSpec, widthUsed, layoutParams, childViewMeasuredWidth)) {//需要换行
                lineMaxWidth = Math.max(lineMaxWidth, widthUsed);//换行前更新下最大宽度
                heightUsed += lineMaxHeight;
                lineMaxHeight = childViewMeasuredHeightAndMargin;
                widthUsed = childViewMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            } else {
                widthUsed += childViewMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            }
        }
        lineMaxWidth = Math.max(lineMaxWidth, widthUsed);//防止只有一行时最大宽度未更新
        int width = lineMaxWidth + getPaddingLeft() + getPaddingRight();
        int height = heightUsed + lineMaxHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    //检测是否需要换行
    private boolean checkNextLine(int widthSpec, int widthUsed, MarginLayoutParams layoutParams, int childViewMeasuredWidth) {
        return widthSpec < widthUsed + childViewMeasuredWidth + layoutParams.leftMargin;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int widthSpec = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0, widthUsed = 0, lineMaxHeight = 0, heightUsed = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            int childViewMeasuredWidth = childView.getMeasuredWidth();
            int childViewMeasuredHeight = childView.getMeasuredHeight();
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            //更新当前行最高高度
            lineMaxHeight = Math.max(lineMaxHeight, childViewMeasuredHeight + layoutParams.topMargin + layoutParams.bottomMargin);
            if (checkNextLine(widthSpec, widthUsed, layoutParams, childViewMeasuredWidth)) {//需要换行
                //更新使用高度
                heightUsed += lineMaxHeight;
                lineMaxHeight = childViewMeasuredHeight + layoutParams.topMargin + layoutParams.bottomMargin;
                int top = heightUsed + layoutParams.topMargin + getPaddingTop();
                childView.layout(getPaddingLeft() + layoutParams.leftMargin, top, getPaddingLeft() + layoutParams.leftMargin + childViewMeasuredWidth, top + childViewMeasuredHeight);
                widthUsed = childViewMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin;//补足右边界 这种情况是在右边可以顶格
            } else {
                int top = heightUsed + layoutParams.topMargin + getPaddingTop();
                widthUsed += layoutParams.leftMargin;//第一行需要补足 首先加上
                childView.layout(getPaddingLeft() + widthUsed, top, getPaddingLeft() + widthUsed + childViewMeasuredWidth, top + childViewMeasuredHeight);
                widthUsed += childViewMeasuredWidth + layoutParams.rightMargin;
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}
