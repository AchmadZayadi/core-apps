package com.sesolutions.ui.clickclick;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import cn.jzvd.JzvdStd2;


public class JzvdStdClickClick extends JzvdStd2 {
    public JzvdStdClickClick(Context context) {
        super(context);
    }

    public JzvdStdClickClick(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        bottomContainer.setVisibility(GONE);
        topContainer.setVisibility(GONE);
        bottomProgressBar.setVisibility(GONE);
        thumbImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    //changeUiTo 真能能修改ui的方法
    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
        bottomContainer.setVisibility(GONE);
        topContainer.setVisibility(GONE);
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        bottomProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
        bottomProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        bottomProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
        bottomProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    public void onClickUiToggle() {
        super.onClickUiToggle();
        Log.i(TAG, "click blank");
        startButton.performClick();
        bottomContainer.setVisibility(GONE);
        topContainer.setVisibility(GONE);
        bottomProgressBar.setVisibility(VISIBLE);
    }
}