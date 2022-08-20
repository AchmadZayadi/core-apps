package com.sesolutions.ui.signup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.otp_view.mukesh.OnOtpCompletionListener;
import com.sesolutions.otp_view.smsCatcher.OnSmsCatchListener;
import com.sesolutions.otp_view.smsCatcher.SmsVerifyCatcher;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.responses.signin.OtpCustomParam;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPFragment extends BaseFragment implements OnOtpCompletionListener, OnSmsCatchListener<String>, OnUserClickedListener<Integer, Object>, View.OnClickListener {
    public static final int FROM_FORGOT = 10;
    public static final int FROM_SIGNIN = 20;
    public static final int FROM_SIGNUP = 30;
    public static final int FROM_NUMBER_CHANGE = 40;
    EditText otpView;
    SmsVerifyCatcher smsVerifyCatcher;
    private View v;
    private TextView mOtpTextView;
    private OtpCustomParam customParams;
    //private List<Dummy.Formfields> formData;
    private String enteredOTP, readOtp, password, email;
    private int previousScreen;
    private Map<String, Object> mapPrevious;
    private String previousResp;
    LinearLayout llBack;
    AppCompatImageView ivImage387837;

    public static OTPFragment newInstance(int previousScreen, Map<String, Object> mapPrevious, String response) {
        OTPFragment frag = new OTPFragment();
        frag.mapPrevious = mapPrevious;
        frag.previousScreen = previousScreen;
        frag.previousResp = response;
        return frag;
    }

    // 7023154448

    public static OTPFragment newInstance(int previousScreen, String email, String password) {
        OTPFragment frag = new OTPFragment();
        frag.email = email;
        frag.password = password;
        frag.previousScreen = previousScreen;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_otp_3, container, false);
        try {
            applyTheme(v);
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    // private CircularProgressBar circularProgressBar;
    private TextView tvProgress;
    private CountDownTimer timer;

    private void init() {
        getModuleData();
        mOtpTextView = v.findViewById(R.id.tv_otp);
        //ivImage387837 = v.findViewById(R.id.ivImage387837);
        otpView = v.findViewById(R.id.otp_view);
        llBack = v.findViewById(R.id.llBack);
        //      otpView.setOtpCompletionListener(this);
        //    circularProgressBar = (CircularProgressBar) v.findViewById(R.id.cpb);
        tvProgress = v.findViewById(R.id.tvProgress);
        tvProgress.setTextColor(Color.parseColor("#000000"));
        // circularProgressBar.setColor(Color.parseColor(Constant.colorPrimary)); 7023154448

        smsVerifyCatcher = new SmsVerifyCatcher(activity, this, this);

        v.findViewById(R.id.bVerify).setOnClickListener(this);
        v.findViewById(R.id.bResend).setOnClickListener(this);
        fetchOtpData();

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = fragmentManager.getBackStackEntryCount();
                    Log.e("fragmentCount", "" + count);
                    for (int i = 0; i < count - 1; ++i) {
                        fragmentManager.popBackStackImmediate();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //  fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2()).commit();
            }
        });

//        Log.e("imagelogin",""+ SPref.getInstance().getString(
//                context,
//                SPref.IMAGE_LOGIN_BG
//        ));
//        Util.showImageWithGlide(
//                ivImage387837, SPref.getInstance().getString(
//                        context,
//                        SPref.IMAGE_LOGIN_BG
//                ), context /*, R.drawable.placeholder_3_2*/
//        );

    }

    @Override
    public void onBackPressed() {
        try {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < count - 1; ++i) {
                fragmentManager.popBackStackImmediate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void initScreenData() {
        try {

            // circularProgressBar.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor.replace("#", "#67")));
            cancelTimer();
            //   circularProgressBar.setProgressWithAnimation(100, 200); // Default duration = 1500ms

            //smsVerifyCatcher.setPhoneNumberFilter("IM-001122");

            timer = new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    v.findViewById(R.id.layout_send_again).setVisibility(View.GONE);
                    tvProgress.setText(String.format(
                            "%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            )
                    ));
//                    if (millisUntilFinished > 1000) {
//                        tvProgress.setText(Util.milliSecondsToTimer(millisUntilFinished));
//                      //  circularProgressBar.setProgressWithAnimation(Util.getProgressPercentage(millisUntilFinished, customParams.getOtpsmsDuration() * 1000), 1000);
//
//                       // changeProgressColor(millisUntilFinished);
//
//                    } else {
//
//                    }
                }

                @Override
                public void onFinish() {
                    v.findViewById(R.id.bVerify).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.layout_send_again).setVisibility(View.VISIBLE);


                }
            };

            timer.start();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void cancelTimer() {
        if (null != timer) timer.cancel();
        timer = null;
    }

    /*private void changeProgressColor(long millisUntilFinished) {
        if (millisUntilFinished > 60000) {
            circularProgressBar.setColor(ContextCompat.getColor(context, R.color.green));
        } else if (millisUntilFinished > 30000) {
            circularProgressBar.setColor(ContextCompat.getColor(context, R.color.contest_type));
        } else {
            circularProgressBar.setColor(ContextCompat.getColor(context, R.color.red));
        }
    }*/


    @Override
    public void onOtpCompleted(String otp) {
        // do Stuff
        enteredOTP = otp;
    }


    private void showLoader() {
        //    v.findViewById(R.id.pbBar).setVisibility(View.VISIBLE);
        showBaseLoader(false);
    }


    @Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
        activity.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(Color.BLACK);
        smsVerifyCatcher.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onSmsCatch(String message) {
        try {
            mOtpTextView.setText(message);
            if (!TextUtils.isEmpty(message)) {
                int index = message.indexOf("Use ");
                readOtp = message.substring(index + 4, index + 10);
                otpView.setText(readOtp);
                CustomLog.e("readOtp", "" + readOtp);
                if (!TextUtils.isEmpty(readOtp))
                    new Handler().postDelayed(() -> callVerifyOtpApi(readOtp), 2000);
            }
        } catch (Exception e) {
            CustomLog.e(e);

        }
    }


    private final int REQ_VERIFY = -1, REQ_RESEND = -2, REQ_FETCH = -3;

    @Override
    public boolean onItemClicked(Integer eventType, Object response, int position) {
        switch (eventType) {
            case REQ_VERIFY:
                hideLoaders();
                if (null != response) {
                    String rst = (String) response;
                    JSONObject json = null;
                    try {
                        json = new JSONObject(rst);

                        if (json.get(Constant.KEY_RESULT) instanceof String) {
                            String result = json.getString(Constant.KEY_RESULT);
                            goToScreenAsPerResult(result);
                        } else {
                            SignInResponse res = new Gson().fromJson("" + response, SignInResponse.class);
                            if (TextUtils.isEmpty(res.getError())) {
                                handleLoginResponse(res, context);
                            } else {
                                Util.showSnackbar(v, res.getErrorMessage());
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case REQ_RESEND:
                initScreenData();
                break;
            case REQ_FETCH:
                hideLoaders();
                try {
                    CustomLog.e("repsonse", "" + response);
                    if (response != null) {
                        Dummy vo = new Gson().fromJson("" + response, Dummy.class);
                        if (vo.isSuccess()) {
                            customParams = vo.getResult().getCustomParams(OtpCustomParam.class);
                            getModuleData2();
                            initScreenData();
                            // createFormUi(vo.getResult());
                        } else {
                            Util.showSnackbar(v, vo.getErrorMessage());
                        }
                    } else {
                        somethingWrongMsg(v);
                    }
                } catch (Exception e) {
                    somethingWrongMsg(v);
                    CustomLog.e(e);
                }
                break;
        }

        return false;
    }


    private void goToScreenAsPerResult(String result) {
        switch (result) {
            case Constant.RESULT_FORM_INTEREST2:
                goToSignUpFragment(Constant.VALUE_GET_FORM_INTEREST);
                break;
            case Constant.RESULT_FORM_1:
                goToSignUpFragment(Constant.VALUE_GET_FORM_2);
                break;
            case Constant.RESULT_FORM_OTP:
            case Constant.RESULT_FORM_OTP_LOGIN:
                openOtpFragment(OTPFragment.FROM_SIGNUP, "", null);
                break;
            case Constant.RESULT_FORM_2:
                goToProfileImageFragment();
                break;
            case Constant.RESULT_FORM_INTEREST:
                goToProfileImageFragment();
                break;
            case Constant.RESULT_FORM_3:
                openWebView(Constant.URL_SUBSCRIPTION, Constant.TITLE_SUBSCRIPTION);
                break;
            case Constant.RESULT_FORM_4:
                hideBaseLoader();
                fragmentManager.beginTransaction().replace(R.id.container, new JoinFragment()).commit();
                break;
            default:
                hideBaseLoader();
                fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2()).commit();
                break;
        }
    }


    private void hideLoaders() {
//        v.findViewById(R.id.pbBar).setVisibility(View.GONE);
        hideBaseLoader();
    }

    private void fetchOtpData() {
        if (null != previousResp) {
            new Handler().postDelayed(() -> onItemClicked(REQ_FETCH, previousResp, -1), 200);
        } else if (isNetworkAvailable(context)) {
            showLoader();
            new ApiController(URL, mapFetch, context, this, REQ_FETCH).execute();
        }
    }

    //7023154448

    private void callResendOtpApi() {
        cancelTimer();
        if (isNetworkAvailable(context)) {
           /* Map<String, Object> map = new HashMap<>();
            if (previousScreen == FROM_FORGOT) {
                map.put("formType", FORM_TYPE);
                map.put(Constant.KEY_EMAIL, email);
            } else {
                map.put(Constant.KEY_USER_ID, customParams.getUserId());
            }

            if (null != password) {
                map.put(Constant.KEY_PASSWORD, password);
            }*/

            new ApiController(URL_RESEND, mapResend, context, this, REQ_RESEND).execute();
            Util.showSnackbar(v, getString(R.string.msg_otp_resent));
        }
    }

    private void callVerifyOtpApi(String otp) {
        timer.cancel();
        if (isNetworkAvailable(context)) {
            showLoader();
            /*Map<String, Object> map = new HashMap<>();
            if (previousScreen == FROM_FORGOT) {
                map.put("uid", customParams.getUserId());
                map.put(Constant.KEY_GET_FORM, 1);
            } else {
                map.put(Constant.KEY_USER_ID, customParams.getUserId());
            }*/

            mapVerify.put("code", otpView.getText().toString());
            new ApiController(URL_VERIFY, mapVerify, context, this, REQ_VERIFY).execute();

        }
    }

    private String URL, URL_VERIFY, URL_RESEND;
    private Map<String, Object> mapResend, mapVerify, mapFetch;

    private void getModuleData2() {
        mapResend = new HashMap<>();
        mapVerify = new HashMap<>();

        switch (previousScreen) {
            case FROM_SIGNIN:
                mapResend.put(Constant.KEY_USER_ID, customParams.getUserId());

                mapVerify.put(Constant.KEY_USER_ID, customParams.getUserId());

                URL_VERIFY = Constant.URL_OTP_VERIFY;
                URL_RESEND = Constant.URL_OTP_RESEND;
                break;
            case FROM_FORGOT:
                URL_VERIFY = Constant.URL_PASSWORD_RESET;
                URL_RESEND = Constant.URL_OTP_LOGIN;

                mapResend.put("uid", customParams.getUserId());
                mapResend.put("formType", "forgot");
                mapResend.put(Constant.KEY_EMAIL, email);
                mapResend.put(Constant.KEY_GET_FORM, 1);

                mapVerify.put("uid", customParams.getUserId());
                mapVerify.put(Constant.KEY_GET_FORM, 1);

                break;

            case FROM_NUMBER_CHANGE:

                URL_VERIFY = Constant.URL_ACCOUNT_NUMBER;
                URL_RESEND = Constant.URL_ACCOUNT_NUMBER;

                mapResend.putAll(mapPrevious);
                mapResend.put("step", 2);
                mapResend.put("resend", 1);

                mapVerify.put("step", 3);

                //mapVerify.put(Constant.KEY_VALIDATE_OTP_FORM, 1);

                break;

            case FROM_SIGNUP:

                URL_VERIFY = Constant.URL_SIGNUP;
                URL_RESEND = Constant.URL_SIGNUP;

                mapResend.put(Constant.KEY_GET_FORM, "otpsms");

                mapVerify.put(Constant.KEY_VALIDATE_OTP_FORM, 1);

                break;
        }
    }

    private void getModuleData() {
        mapFetch = new HashMap<>();


        switch (previousScreen) {
            case FROM_SIGNIN:
                URL = Constant.URL_OTP_LOGIN;
                mapFetch.put(Constant.KEY_EMAIL, email);
                mapFetch.put(Constant.KEY_PASSWORD, password);
                mapFetch.put("formType", "login");

                break;
            case FROM_FORGOT:
                URL = Constant.URL_OTP_LOGIN;

                mapFetch.put("formType", "forgot");
                mapFetch.put(Constant.KEY_EMAIL, email);

                break;

            case FROM_NUMBER_CHANGE:
                URL = Constant.URL_ACCOUNT_NUMBER;
                mapFetch.putAll(mapPrevious);
                break;

            case FROM_SIGNUP:
                URL = Constant.URL_SIGNUP;
                mapFetch.put(Constant.KEY_GET_FORM, "otpsms");
                break;
        }
    }

    private void handleLoginResponse(SignInResponse vo, Context context) {
        if (previousScreen == FROM_FORGOT) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uid", customParams.getUserId());
            map.put("code", TextUtils.isEmpty(readOtp) ? enteredOTP : readOtp);
            openFormFragment(Constant.FormType.RESET_PASSWORD, map, Constant.URL_PASSWORD_RESET);

        } else if (previousScreen == FROM_NUMBER_CHANGE) {
            Util.showSnackbar(v, vo.getResult().getSuccess());
            goDoubleback();
        } else {
            UserMaster userVo = vo.getResult();
            userVo.setAuthToken(vo.getAouthToken());
            userVo.setLoggedinUserId(userVo.getUserId());
            SPref.getInstance().saveUserMaster(context, userVo, vo.getSessionId());
            SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, vo.getAouthToken());
            SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
            SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());


            goToDashboard();
            CustomLog.d("userVo", new Gson().toJson(userVo));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bResend:
                callResendOtpApi();
                break;
            case R.id.bVerify:
                if (otpView.getText().toString().length() > 0)
                    callVerifyOtpApi(readOtp);
                else if (!TextUtils.isEmpty(enteredOTP))
                    callVerifyOtpApi(enteredOTP);
                break;
        }
    }
}
