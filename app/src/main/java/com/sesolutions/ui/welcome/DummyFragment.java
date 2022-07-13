package com.sesolutions.ui.welcome;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.signup.SignUpFragment;
import com.sesolutions.utils.CustomLog;



public class DummyFragment extends BaseFragment implements View.OnClickListener {

    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_welcome, container, false);
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
        v.findViewById(R.id.bChoose25).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);
        //initSlide();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bSignIn:

                    break;

                case R.id.bSignUp:
                    fragmentManager.beginTransaction().replace(R.id.container, new SignUpFragment())
                            .addToBackStack(null)
                            .commit();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
