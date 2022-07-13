package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */

import android.animation.ObjectAnimator;
import android.view.View;


public class SlideDownAnimator extends BaseViewAnimator {

    @Override
    public void prepare(View target) {
        int distance = target.getTop() + target.getHeight();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                ObjectAnimator.ofFloat(target, "translationY", -distance, 0)
        );
    }
}
