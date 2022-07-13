package com.sesolutions.ui.signup;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class JoinFragment extends BaseFragment implements View.OnClickListener {//}, ParserCallbackInterface {


    private View v;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_join, container, false);
        try {
            applyTheme(v);
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        v.findViewById(R.id.rlMain).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.bJoin).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
     //   fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bJoin:
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


}
