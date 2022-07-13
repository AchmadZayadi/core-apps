package com.sesolutions.utils;

import android.animation.ValueAnimator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class PausableAnimation extends Animation {

    private long mElapsedAtPause=0;
    private boolean mPaused=false;

   /* public PausableAnimation(float fromAlpha, float toAlpha) {
        super(fromAlpha, toAlpha);
    }*/

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if(mPaused && mElapsedAtPause==0) {
            mElapsedAtPause=currentTime-getStartTime();
        }
        if(mPaused)
            setStartTime(currentTime-mElapsedAtPause);
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause() {
        mElapsedAtPause=0;
        mPaused=true;
    }

    public void resume() {
        mPaused=false;
    }
}
