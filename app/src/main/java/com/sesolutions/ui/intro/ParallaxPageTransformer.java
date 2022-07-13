package com.sesolutions.ui.intro;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.sesolutions.R;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(@NonNull View view, float position) {

        int pageWidth = view.getWidth();


        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1) { // [-1,1]

            view.findViewById(R.id.ivImage).setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}
