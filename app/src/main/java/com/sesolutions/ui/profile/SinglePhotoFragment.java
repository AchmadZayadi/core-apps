package com.sesolutions.ui.profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.photoview.PhotoView;


public class SinglePhotoFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    ImageView backbuttonid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_photo_list2, container, false);
        backbuttonid=v.findViewById(R.id.backbuttonid);

        try {
            init();
        } catch (Exception e) {
        e.printStackTrace();
        }
        backbuttonid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     onBackPressed();
            }
        });
        return v;
    }

    private void init() {
        PhotoView ivSongImage = v.findViewById(R.id.ivSongImage);
        Glide.with(getContext()).load(ImageUrl).into(ivSongImage);
     }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

            }
        } catch (Exception e) {
        }
    }

    String ImageUrl="";
    public static SinglePhotoFragment newInstance(String albumsimage) {
        SinglePhotoFragment frag = new SinglePhotoFragment();
        frag.ImageUrl=albumsimage;
        return frag;
    }
}
