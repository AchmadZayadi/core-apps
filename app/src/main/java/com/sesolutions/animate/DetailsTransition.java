package com.sesolutions.animate;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by WarFly on 5/7/2017.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DetailsTransition extends TransitionSet {
    // ChangeBounds changeBounds;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DetailsTransition() {

        // changeBounds = new ChangeBounds();
        //  changeBounds.setPathMotion(new ArcMotion());
        setOrdering(TransitionSet.ORDERING_TOGETHER).
                addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform())
                .setDuration(320)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setPathMotion(new ArcMotion())
        //    .  addTransition(new ChangeImageTransform())
        ;
    }
}