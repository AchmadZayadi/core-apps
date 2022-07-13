package com.sesolutions.ui.welcome;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.signup.SignUpFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import static android.view.View.GONE;

public class WelcomeFragment2 extends BaseFragment implements View.OnClickListener {

    private View v;
    TextView tvTitle;
    TextView tvDesc;

    private TextView tvSkip;
    private int type;
    private OnUserClickedListener<Integer, Object> listener;
    LinearLayout lineaidMain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(null);
            setExitTransition(null);
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }
    }

    public static WelcomeFragment2 newInstance(OnUserClickedListener<Integer, Object> listener, int type) {
        WelcomeFragment2 frag = new WelcomeFragment2();
        frag.type = type;
        frag.listener = listener;
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        // callbackManager = CallbackManager.Factory.create();
        v = inflater.inflate(R.layout.fragment_welcome2, container, false);
        try {
           /* textList = new ArrayList<WelcomeModel>();
            textList.add(new WelcomeModel("Feel Alive", "Enjoy awesome music tracks & Videos and share with your friends too."));
            textList.add(new WelcomeModel("Write to share your ideas, thoughts & stories and let the world know you.", Constant.EMPTY));
            textList.add(new WelcomeModel("Get Connected", "Find friends, make groups and share your memorable moments."));
            textList.add(new WelcomeModel("Explore things, join events and feel enthusiastic all the time.", Constant.EMPTY));
           */
            init();
            showHideSkipLogin();
            openScreen();




        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void showHideSkipLogin() {
        boolean isEnableSkipLogin = SPref.getInstance().getBoolean(context, Constant.KEY_ENABLE_SKIP);
        v.findViewById(R.id.id2).setVisibility(isEnableSkipLogin ? View.VISIBLE : View.GONE);
        tvSkip.setVisibility(isEnableSkipLogin ? View.VISIBLE : View.GONE);
    }

    private void init() {
        tvTitle = v.findViewById(R.id.tvTitle);
        tvDesc = v.findViewById(R.id.tvDesc);
        lineaidMain = v.findViewById(R.id.lineaidMain);
        tvSkip = v.findViewById(R.id.tvSkip);
        v.findViewById(R.id.bSignIn).setOnClickListener(this);
        v.findViewById(R.id.bSignUp).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);
       // v.findViewById(R.id.rlFacebook).setVisibility(AppConfiguration.IS_FB_LOGIN_ENABLED ? View.VISIBLE : View.INVISIBLE);
     //   v.findViewById(R.id.rlFacebook).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvPrivacy)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        ((TextView) v.findViewById(R.id.tvTerms)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        tvSkip.setTextColor(Color.parseColor(Constant.outsideTitleColor));
        ((WelcomeActivity) activity).updateText(0);
        v.findViewById(R.id.tvSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDashboard();
            }
        });

        lineaidMain.setBackground(getResources().getDrawable(R.drawable.back_welcome));
        GradientDrawable drawable = (GradientDrawable) lineaidMain.getBackground();
        drawable.setColor(Color.parseColor(Constant.outsideButtonBackgroundColor));

    }


    public void updateText(SlideShowImage vo) {
        try {
            tvDesc.setText(vo.getDescription());
            tvTitle.setText(vo.getTitle());
            tvDesc.setVisibility(TextUtils.isEmpty(vo.getDescription()) ? GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(vo.getTitleColor())) {
                tvTitle.setTextColor(Color.parseColor(vo.getTitleColor()));
            } else {
                tvTitle.setTextColor(Color.parseColor(Constant.outsideTitleColor));
            }
            if (!TextUtils.isEmpty(vo.getDescriptionColor())) {
                tvDesc.setTextColor(Color.parseColor(vo.getDescriptionColor()));
            } else {
                tvDesc.setTextColor(Color.parseColor(Constant.outsideTitleColor));
            }
            // changeImage(position);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openScreen() {
        switch (type) {
           /* case 0:
                fragmentManager.beginTransaction().replace(R.id.container, WelcomeFragment.newInstance(false))
                        .addToBackStack(null)
                        .commit();
                break;*/
            case 1:
            case 3:
                fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bSignIn:
                    if (activity instanceof WelcomeActivity) {
                        ((WelcomeActivity) activity).openSigninFragment();
                    }
                    break;

              /*  case R.id.rlFacebook:
                    fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                            .addToBackStack(null)
                            .commit();
                    break;*/

                case R.id.bSignUp:
                    SPref.getInstance().removeDataOnLogout(context);
                    goToSignUpFragment();
                    break;
                case R.id.tvTerms:
                    openTermsPrivacyFragment(Constant.URL_TERMS_2);
                    break;
                case R.id.tvPrivacy:
                    openTermsPrivacyFragment(Constant.URL_PRIVACY_2);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void goToSignUpFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SignUpFragment.newInstance(Constant.VALUE_GET_FORM_1))
                //.replace(R.id.container, new ProfileImageFragment())
                .addToBackStack(null)
                .commit();
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CustomLog.d("onActivityResult", "" + requestCode);
     *//*   ((WelcomeActivity) activity).*//*
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }*/


    @Override
    public void onBackPressed() {
        activity.finish();
    }

}
