package com.sesolutions.ui.dashboard;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.photoview.PhotoView;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;


public class PhotoViewFragment extends BaseFragment implements View.OnClickListener {

    private String url;
    private View v;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_photo, container, false);
        try {
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
       PhotoView ivProfileImage = v.findViewById(R.id.ivImage);
        Util.showImageWithGlide(ivProfileImage,url, context, R.drawable.placeholder_3_2);
    }


    @Override
    public void onClick(View v) {
    }

    public static PhotoViewFragment newInstance(String string) {
        PhotoViewFragment frag = new PhotoViewFragment();
        frag.url = string;
        return frag;
    }
}
