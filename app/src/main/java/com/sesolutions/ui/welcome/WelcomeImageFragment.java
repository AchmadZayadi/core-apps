package com.sesolutions.ui.welcome;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.CustomLog;



public class WelcomeImageFragment extends BaseFragment {

    private View v;
    private int position;
    // @BindView(R.id.ivImage)
    public AppCompatImageView ivImage;
    private String title;
    private String description;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);

        v = inflater.inflate(R.layout.fragment_welcome_image, container, false);
        try {

            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        try {
            ivImage = v.findViewById(R.id.ivImage);
            //  CustomLog.e("atPosition", "" + position);
            switch (position) {
                /*case 0:
                    ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_welcome_1));
                    break;
                case 1:
                    ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_welcome_2));
                    break;
                case 2:
                    ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_welcome_3));
                    break;
                case 3:
                    ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_welcome_4));
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    public static Fragment newInstance(int position, String title, String description) {
        WelcomeImageFragment fragment = new WelcomeImageFragment();
        fragment.title = title;
        fragment.description = description;
        fragment.position = position;
        return fragment;
    }
}
