package com.mainli.view.turntable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mainli.R;
import com.mainli.utils.SizeUtil;


public class TurntableView extends FrameLayout implements View.OnClickListener {
    private View mLuckySpinButton;
    private TurntableBackgroundView mLuckySpin;
    private TurntableBackgroundView.LuckySpinListener mLuckySpinListener;
    public final static int NONE = -1;
    private int remainingTurntableCount = 0;
    private View mHand;
    private AnimatorSet mAnimationSet;

    public TurntableView(Context context) {
        super(context);
    }

    public TurntableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLuckySpin = findViewById(R.id.lucky_spin);
        mLuckySpinButton = findViewById(R.id.lucky_spin_button);
        mHand = findViewById(R.id.lucky_spin_hand);
        mLuckySpinButton.setOnClickListener(this);
        Bitmap[] bitmaps = new Bitmap[8];
        bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_vip);
        bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_wallpaper);
        bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_gift);
        bitmaps[3] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_theme);
        bitmaps[4] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_free_ad);
        bitmaps[5] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_sale);
        bitmaps[6] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_gift);
        bitmaps[7] = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_turntable_subscription);
        mLuckySpin.setupPrizes(bitmaps);
    }

    public void setRemainingTurntableCount(int remainingTurntableCount) {
        this.remainingTurntableCount = remainingTurntableCount;
    }

    @Override
    public void onClick(View v) {
        if (v == mLuckySpinButton && mLuckySpinListener != null) {
            if (!isNetworkAvailable()) {
                Toast.makeText(getContext(), "没有网络,请重试", Toast.LENGTH_SHORT).show();
                return;
            }
            if (remainingTurntableCount > 0) {
                if (mLuckySpin.isStopped()) {
                    mLuckySpin.luckyStart(mLuckySpinListener);
                }
            } else {
            mLuckySpinListener.onLuckyStart(false);
            }
        }
    }

    public void stopTurntable(long duration, final int idx, final int type) {
        mLuckySpin.setLuckyPos(duration, idx, type);
    }

    public void setLuckySpinListener(TurntableBackgroundView.LuckySpinListener luckySpinListener) {
        mLuckySpinListener = luckySpinListener;
    }

    public void playWelcomeAnimation(final Runnable runnable) {
        if (mAnimationSet == null) {
            float offsetY = SizeUtil.dp2Px(20);
            mHand.setTranslationY(offsetY);
            mAnimationSet = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mHand, "alpha", 0, 1).setDuration(100);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(mHand, "translationY", offsetY, 0).setDuration(200);
            ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(mLuckySpinButton, PropertyValuesHolder.ofFloat("scaleX", 1, 1.1f), PropertyValuesHolder.ofFloat("scaleY", 1, 1.1f)).setDuration(200);
            ObjectAnimator revertTranslationY = ObjectAnimator.ofFloat(mHand, "translationY", 0, offsetY).setDuration(300);
            ObjectAnimator revertScale = ObjectAnimator.ofPropertyValuesHolder(mLuckySpinButton, PropertyValuesHolder.ofFloat("scaleX", 1.1f, 1f), PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1f)).setDuration(300);
            ObjectAnimator revertAlpha = ObjectAnimator.ofFloat(mHand, "alpha", 1, 0).setDuration(100);
            mAnimationSet.play(translationY).after(alpha);
            mAnimationSet.play(scale).after(translationY);
            mAnimationSet.play(revertTranslationY).after(scale);
            mAnimationSet.play(revertScale).with(revertTranslationY);
            mAnimationSet.play(revertAlpha).after(revertScale);


            mAnimationSet.addListener(new RepeatAnimatorListener(1) {
                @Override
                public void onAnimationRepeatStop(Animator animation, boolean isEnd, boolean isFirstStop, int surplusRepeatCount) {
                    Log.d("Mainli", "onAnimationRepeatStop(isEnd = [" + isEnd + "], isFirstStop = [" + isFirstStop + "], surplusRepeatCount = [" + surplusRepeatCount + "])");
                    if (runnable != null && isFirstStop) {
                        runnable.run();
                    }
                    if (isEnd) {
                        mHand.setVisibility(GONE);
                    }
                }
            });
            mHand.setVisibility(VISIBLE);
            mAnimationSet.start();
        } else {
            if (mAnimationSet.isRunning()) {
                mAnimationSet.end();
            }
            mHand.setVisibility(VISIBLE);
            mAnimationSet.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimationSet != null) {
            mAnimationSet.cancel();
        }
    }

    /**
     * 检查是否有网络
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    private static class RepeatAnimatorListener implements Animator.AnimatorListener {
        private int surplusRepeatCount;
        private final int REPEAT_COUNT;

        public RepeatAnimatorListener(int extraRepeatCount) {
            this.REPEAT_COUNT = extraRepeatCount + 1;
            this.surplusRepeatCount = REPEAT_COUNT;
        }

        public void resetRepeat() {
            this.surplusRepeatCount = REPEAT_COUNT;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            surplusRepeatCount--;
        }

        @Override
        public final void onAnimationEnd(Animator animation) {
            boolean isEnd = surplusRepeatCount == 0;
            onAnimationRepeatStop(animation, isEnd, surplusRepeatCount + 1 == REPEAT_COUNT, surplusRepeatCount);
            if (!isEnd) {
                animation.start();
                onAnimationRepeat(animation);
            } else {
                resetRepeat();
            }
        }

        /**
         * @param animation
         * @param isEnd              是否真正结束
         * @param isFirstStop        是否是首次stop
         * @param surplusRepeatCount 剩余次数
         */
        public void onAnimationRepeatStop(Animator animation, boolean isEnd, boolean isFirstStop, int surplusRepeatCount) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            resetRepeat();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
