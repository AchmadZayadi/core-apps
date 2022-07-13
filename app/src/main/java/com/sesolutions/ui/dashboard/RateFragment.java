package com.sesolutions.ui.dashboard;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;


public class RateFragment extends BaseFragment implements View.OnClickListener {

    private View v;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_rating, container, false);
        try {
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bRate).setOnClickListener(this);
        v.findViewById(R.id.tvRemindLater).setOnClickListener(this);
        ImageView ivImage = v.findViewById(R.id.ivImage);
       // Util.showImageWithGlide(ivImage, SPref.getInstance().getString(context, SPref.IMAGE_RATE_US), context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            activity.setStatusBarColor(Color.BLACK);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
        super.onStop();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bRate:
                    rateApp();
                    break;

                case R.id.tvRemindLater:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }
}
