package com.mainli.recyclerview;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.EdgeEffect;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 使RecyclerView的EdgeEffect具有类似IOS弹性效果
 * TODO: 待完善
 * 参考：
 * https://github.com/android/animation-samples/tree/master/Motion
 * https://juejin.cn/post/6844904126380244999
 */
public class SpringEdgeEffect extends RecyclerView.EdgeEffectFactory {

    private static final float FLING_TRANSLATION_MAGNITUDE = 0.5f;

    public static void applySpringEdgeEffect(RecyclerView recyclerView) {
        recyclerView.setEdgeEffectFactory(new SpringEdgeEffect());
    }

    @Override
    public EdgeEffect createEdgeEffect(RecyclerView recyclerView, int direction) {
        return new EdgeEffect(recyclerView.getContext()) {

            /**
             * 内容已经在顶部到达边界了，此时用户仍向下滑动时，会调用onPull方法及后续流畅，
             * 来更新当前视图，提示用户已经到边界了。
             */
            @Override
            public void onPull(float deltaDistance) {
                super.onPull(deltaDistance);
                handlePull(deltaDistance, direction, recyclerView);
            }

            @Override
            public void onPull(float deltaDistance, float displacement) {
                super.onPull(deltaDistance, displacement);
                handlePull(deltaDistance, direction, recyclerView);
            }

            private void handlePull(float deltaDistance, int direction, RecyclerView recyclerView) {
                int sign = direction == DIRECTION_BOTTOM ? -1 : 1;
                float translationYDelta = sign * recyclerView.getWidth() * deltaDistance * 0.8f;
                Log.d("qlli1234-pull", "deltDistance: " + translationYDelta);
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    if (ViewCompat.isAttachedToWindow(recyclerView.getChildAt(i))) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        viewHolder.itemView.setTranslationY(viewHolder.itemView.getTranslationY() + translationYDelta);
                    }
                }
            }

            /**
             * 松开，不向下滑动了，此时释放拉动的距离，并刷新界面消失当前的图形界面
             */
            @Override
            public void onRelease() {
                super.onRelease();
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    ValueAnimator animator = ValueAnimator.ofFloat(viewHolder.itemView.getTranslationY(), 0f).setDuration(500);
                    animator.setInterpolator(new DecelerateInterpolator(2.0f));
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            viewHolder.itemView.setTranslationY((float) animation.getAnimatedValue());
                        }
                    });
                    animator.start();
                }
            }

            @Override
            public void onAbsorb(int velocity) {
                super.onAbsorb(velocity);
                int sign = direction == DIRECTION_BOTTOM ? -1 : 1;
                float translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE;
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    if (ViewCompat.isAttachedToWindow(recyclerView.getChildAt(i))) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        // 在这里可以做动画
                    }
                }
            }

            @Override
            public boolean draw(Canvas canvas) {
                setSize(0, 0);
                return super.draw(canvas);
            }
        };
    }
}