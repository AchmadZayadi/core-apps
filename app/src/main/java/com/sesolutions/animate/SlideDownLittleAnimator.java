package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */

import android.animation.ObjectAnimator;
import android.view.View;


public class SlideDownLittleAnimator extends BaseViewAnimator {

    @Override
    public void prepare(View target) {
        int distance = target.getTop() + target.getHeight();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1, 1),
                ObjectAnimator.ofFloat(target, "translationY", -target.getHeight() / 9, 0)
        );
    }
}
