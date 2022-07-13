package com.sesolutions.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChildAttachStateChangeListener implements RecyclerView.OnChildAttachStateChangeListener {
    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
//        try {
//            Jzvd jzvd = view.findViewById(R.id.videoplayer);
//            if (jzvd != null && null != jzvd.jzDataSource && jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
//                Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
//                if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
//                    Jzvd.releaseAllVideos();
//                }
//            }
//        } catch (Exception e) {
//            CustomLog.e(e);
//        }
//    }
    }
}