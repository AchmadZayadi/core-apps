package com.sesolutions.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SesGestureAdapter extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }

}
