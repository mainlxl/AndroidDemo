package com.mainli.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class AttachPosition implements View.OnLayoutChangeListener {
    private View mPositionView;
    private AttachPositionLayout mPositionLayout;
    private ViewGroup.MarginLayoutParams mMarginLayoutParams;

    public AttachPosition(View positionView, @Nullable ViewGroup.MarginLayoutParams marginLayoutParams, AttachPositionLayout positionLayout) {
        mPositionView = positionView;
        mPositionLayout = positionLayout;
        if (marginLayoutParams == null) {
            mMarginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        } else {
            mMarginLayoutParams = marginLayoutParams;
        }
    }

    @Override
    public void onLayoutChange(View parent, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (parent == mPositionView.getParent()) {
            measureChild(parent, mPositionView, mMarginLayoutParams);
            int width = mPositionView.getMeasuredWidth();
            int height = mPositionView.getMeasuredHeight();
            mPositionLayout.attachLayout(mPositionView, mMarginLayoutParams, parent.getLeft(), parent.getTop(), parent.getRight(), parent.getBottom(), width, height);
        }
    }

    private void measureChild(View parent, View childView, ViewGroup.LayoutParams layoutParams) {
        final int parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        final int parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.EXACTLY);
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,//
                parent.getPaddingRight() + parent.getPaddingLeft()//
                , layoutParams.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,//
                parent.getPaddingTop() + parent.getPaddingBottom()//
                , layoutParams.height);
        childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public interface AttachPositionLayout {
        /**
         * @param params 必须使用该params ,View本身LayoutParams不可使用
         *               <p>
         *               最后调用positionView.layout( left,  top,  right,  bottom)定位
         */
        void attachLayout(View positionView, ViewGroup.MarginLayoutParams params, int parentLeft, int parentTop, int parentRight, int parentBottom, int measuredPositionWidth, int measuredPositionHeight);
    }

    public static void attach(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams, AttachPositionLayout positionLayout) {
        assert (positionLayout != null);
        AttachPosition listener = new AttachPosition(positionView, layoutParams, positionLayout);
        targetView.addOnLayoutChangeListener(listener);
        /**
         * new ViewGroup.LayoutParams(0,0) 解决LinearLayout等布局 定位View占用空间问题
         */
        targetView.addView(positionView, new ViewGroup.LayoutParams(0, 0));
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------

    private static final class SimpleAttachPositionLayout implements AttachPositionLayout {
        private final static int CENTER = 0;
        private final static int RIGHT_TOP = 1;
        private final static int RIGHT_BOTTOM = 2;
        private final static int LEFT_TOP = 3;
        private final static int LEFT_BOTTOM = 4;
        private int mGravity = CENTER;

        public SimpleAttachPositionLayout(int gravity) {
            mGravity = gravity;
        }

        @Override
        public void attachLayout(View positionView, ViewGroup.MarginLayoutParams params,//
                                 int parentLeft, int parentTop, int parentRight, int parentBottom, //
                                 int measuredPositionWidth, int measuredPositionHeight) {
            switch (mGravity) {
                case RIGHT_TOP:
                    parentRight -= params.rightMargin;
                    parentTop = params.topMargin;
                    positionView.layout(parentRight - measuredPositionWidth, parentTop, parentRight, parentTop + measuredPositionHeight);
                    break;
                case RIGHT_BOTTOM:
                    parentRight -= params.rightMargin;
                    parentBottom -= params.bottomMargin;
                    positionView.layout(parentRight - measuredPositionWidth, parentBottom - measuredPositionHeight, parentRight, parentBottom);
                    break;
                case LEFT_TOP:
                    parentLeft = params.leftMargin;
                    parentTop = params.topMargin;
                    positionView.layout(parentLeft, parentTop, parentLeft + measuredPositionWidth, parentTop + measuredPositionHeight);
                    break;
                case LEFT_BOTTOM:
                    parentLeft = params.leftMargin;
                    parentBottom -= params.bottomMargin;
                    positionView.layout(parentLeft, parentBottom - measuredPositionHeight, parentLeft + measuredPositionWidth, parentBottom);
                    break;
                default:
                case CENTER:
                    int startLeft = (parentRight - parentLeft - measuredPositionWidth) >> 1;
                    int startTop = (parentBottom - parentTop - measuredPositionHeight) >> 1;
                    positionView.layout(startLeft, startTop, startLeft + measuredPositionWidth, startTop + measuredPositionHeight);
                    break;
            }
        }
    }

    private static void simpleAttach(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams, int gravity) {
        attach(targetView, positionView, layoutParams, new SimpleAttachPositionLayout(gravity));
    }

    public static void attachCenter(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams) {
        simpleAttach(targetView, positionView, layoutParams, SimpleAttachPositionLayout.CENTER);
    }

    public static void attachRightTop(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams) {
        simpleAttach(targetView, positionView, layoutParams, SimpleAttachPositionLayout.RIGHT_TOP);
    }

    public static void attachRightBottom(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams) {
        simpleAttach(targetView, positionView, layoutParams, SimpleAttachPositionLayout.RIGHT_BOTTOM);
    }

    public static void attachLeftTop(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams) {
        simpleAttach(targetView, positionView, layoutParams, SimpleAttachPositionLayout.LEFT_TOP);
    }

    public static void attachLeftBottom(ViewGroup targetView, final View positionView, @Nullable ViewGroup.MarginLayoutParams layoutParams) {
        simpleAttach(targetView, positionView, layoutParams, SimpleAttachPositionLayout.LEFT_BOTTOM);
    }

}
