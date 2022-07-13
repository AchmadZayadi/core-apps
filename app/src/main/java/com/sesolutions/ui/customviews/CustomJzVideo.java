package com.sesolutions.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;

import cn.jzvd.JzvdStd;
import cn.jzvd.JzvdStd2;

public class CustomJzVideo extends JzvdStd2 {
    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public void setListener(OnUserClickedListener<Integer, Object> listener) {
        this.listener = listener;
    }

    private OnUserClickedListener<Integer, Object> listener;

    public CustomJzVideo(Context context) {
        super(context);
    }


    public CustomJzVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showWifiDialog() {

    }

    @Override
    public void changeUiToPauseShow() {
        // super.changeUiToPauseShow();
        setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        updateStartImage();
    }

    @Override
    public void changeUiToPlayingClear() {
        setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
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
        setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
    }
}
