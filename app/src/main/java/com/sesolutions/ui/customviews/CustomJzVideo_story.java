package com.sesolutions.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;

import newd.JzvdStd;


public class CustomJzVideo_story extends JzvdStd {
    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public void setListener(OnUserClickedListener<Integer, Object> listener) {
        this.listener = listener;
    }

    private OnUserClickedListener<Integer, Object> listener;

    public CustomJzVideo_story(Context context) {
        super(context);
    }


    public CustomJzVideo_story(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean showWifiDialog() {
        return true;
    }

    @Override
    public void changeUiToPauseShow() {
        // super.changeUiToPauseShow();
        setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
        updateStartImage();
    }

    @Override
    public void changeUiToPlayingClear() {
        setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
    }

    /* public void setProgressAndText(int progress, long position, long duration) {
        super.setProgressAndText(progress, position, duration);
//        Log.d(TAG, "setProgressAndText: progress=" + progress + " position=" + position + " duration=" + duration);
        if (!mTouchingProgressBar) {
            if (progress != 0) progressBar.setProgress(progress);
        }
        if (position != 0) currentTimeTextView.setText(JZUtils.stringForTime(position));
        totalTimeTextView.setText(JZUtils.stringForTime(duration));
    }*/


    @Override
    public void onStatePrepared() {
        super.onStatePrepared();
        listener.onItemClicked(Constant.Events.MUSIC_PREPARED, null, index);

    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        listener.onItemClicked(Constant.Events.MUSIC_PROGRESS, null, index);
    }

    @Override
    public void onStateError() {
        super.onStateError();
       // CustomLog.e("onStateErrorCustom", "onStateError");
        listener.onItemClicked(Constant.Events.NEXT, null, index);

    }

    @Override
    public void changeUiToPlayingShow() {
        setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
    }
}
