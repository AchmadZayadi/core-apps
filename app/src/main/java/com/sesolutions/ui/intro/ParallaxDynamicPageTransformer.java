package com.sesolutions.ui.intro;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class ParallaxDynamicPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(@NonNull View view, float position) {

        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]


            /*mBlur.setTranslationX((float) (-(1 - position) * 0.5 * pageWidth));
            mBlurLabel.setTranslationX((float) (-(1 - position) * 0.5 * pageWidth));

            mDim.setTranslationX((float) (-(1 - position) * pageWidth));
            mDimLabel.setTranslationX((float) (-(1 - position) * pageWidth));

            view.findViewById(R.id.ivImage).setTranslationX((float) (-(1 - position) * 1.5 * pageWidth));
            mDoneButton.setTranslationX((float) (-(1 - position) * 1.7 * pageWidth));
            // The 0.5, 1.5, 1.7 values you see here are what makes the view move in a different speed.
            // The bigger the number, the faster the view will translate.
            // The result float is preceded by a minus because the views travel in the opposite direction of the movement.

            mFirstColor.setTranslationX((position) * (pageWidth / 4));

            mSecondColor.setTranslationX((position) * (pageWidth / 1));

            mTint.setTranslationX((position) * (pageWidth / 2));

            mDesaturate.setTranslationX((position) * (pageWidth / 1));*/
            // This is another way to do it


        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }


    }
}
