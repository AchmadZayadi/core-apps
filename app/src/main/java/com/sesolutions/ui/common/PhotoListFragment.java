package com.sesolutions.ui.common;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.ui.customviews.FlingLayout;
import com.sesolutions.ui.customviews.photoview.PhotoView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import kotlin.Unit;

public class PhotoListFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private Albums albums;
    private OnUserClickedListener<Integer, Object> listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_photo_list, container, false);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {

        final FlingLayout flingLayout = v.findViewById(R.id.fling_layout);
        PhotoView ivSongImage = v.findViewById(R.id.ivSongImage);

        flingLayout.setDismissListener(() -> {
            listener.onItemClicked(Constant.Events.ON_DISMISS, null, -1);
            return Unit.INSTANCE;
        });

        /*flingLayout.setPositionChangeListener((top, left, dragRangeRate) -> {
            flingLayout.setBackgroundColor(Color.argb(Math.round(255 * (1.0F - dragRangeRate)), 0, 0, 0));
            return Unit.INSTANCE;
        });*/

        Glide.with(context).load(albums.getImages().getMain()).into(ivSongImage);
        //ivSongImage.setOnOutsidePhotoTapListener(imageView -> CustomLog.e("PhotoView", "outside of image"));
        ivSongImage.setOnPhotoTapListener((view, x, y) -> {
            listener.onItemClicked(Constant.Events.IMAGE_1, null, -1);
        });
        ivSongImage.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> flingLayout.setDragEnabled(scaleFactor <= 1F));


    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static PhotoListFragment newInstance(Albums albums, OnUserClickedListener<Integer, Object> listener) {
        PhotoListFragment frag = new PhotoListFragment();
        frag.albums = albums;
        frag.listener = listener;
        return frag;
    }
}
