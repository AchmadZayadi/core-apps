package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */

import android.animation.ObjectAnimator;
import android.view.View;


public class SlideUpAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1, 0),
                ObjectAnimator.ofFloat(target, "translationY", 0, -target.getBottom())
        );
    }
}