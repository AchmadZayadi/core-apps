package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */

import android.animation.ObjectAnimator;
import android.view.View;


public class ShakeAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
        );
    }
}
