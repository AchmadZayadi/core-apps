package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.animation.Interpolator;


public abstract class BaseViewAnimator /*implements ViewPropertyTransition.Animator*/ {

    public static final long DURATION = 1000;

    private AnimatorSet mAnimatorSet;

    private long mDuration = DURATION;

    {
        mAnimatorSet = new AnimatorSet();
    }

  /*  @Override
    public void animate(View view) {
        prepare(view);
    }*/

    protected abstract void prepare(View target);

    public BaseViewAnimator setTarget(View target) {
        reset(target);
        prepare(target);
        return this;
    }

    public void animate() {
        start();
    }

    public void restart() {
        mAnimatorSet = mAnimatorSet.clone();
        start();
    }

    /**
     * reset the view to default status
     *
     * @param target
     */
    public void reset(View target) {
        ViewCompat.setAlpha(target, 1);
        ViewCompat.setScaleX(target, 1);
        ViewCompat.setScaleY(target, 1);
        ViewCompat.setTranslationX(target, 0);
        ViewCompat.setTranslationY(target, 0);
        ViewCompat.setRotation(target, 0);
        ViewCompat.setRotationY(target, 0);
        ViewCompat.setRotationX(target, 0);
    }

    /**
     * start to animate
     */
    public void start() {
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.start();
    }

    public BaseViewAnimator setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public void setStartDelay(long delay) {
        getAnimatorAgent().setStartDelay(delay);
    }

    public long getStartDelay() {
        return mAnimatorSet.getStartDelay();
    }

    public BaseViewAnimator addAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.addListener(l);
        return this;
    }

    public void cancel() {
        mAnimatorSet.cancel();
    }

    public boolean isRunning() {
        return mAnimatorSet.isRunning();
    }

    public boolean isStarted() {
        return mAnimatorSet.isStarted();
    }

    public void removeAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.removeListener(l);
    }

    public void removeAllListener() {
        mAnimatorSet.removeAllListeners();
    }

    public BaseViewAnimator setInterpolator(Interpolator interpolator) {
        mAnimatorSet.setInterpolator(interpolator);
        return this;
    }

    public long getDuration() {
        return mDuration;
    }

    public AnimatorSet getAnimatorAgent() {
        return mAnimatorSet;
    }

}
