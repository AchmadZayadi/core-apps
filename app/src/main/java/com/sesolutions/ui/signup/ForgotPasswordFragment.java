package com.sesolutions.ui.signup;


import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordFragment extends BaseFragment implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;
    private View v;
    AppCompatEditText etEmail;

    private AppCompatButton bSubmit;
    private String email;
    private OnUserClickedListener<Integer, Object> listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        etEmail = v.findViewById(R.id.etEmail);
        bSubmit = v.findViewById(R.id.bSubmit);
        bSubmit.setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        if (!TextUtils.isEmpty(email)) {
            etEmail.setText(email);
        }
        ImageView ivImage = v.findViewById(R.id.ivImage);

        if (AppConfiguration.hasWelcomeVideo) {
            ivImage.setVisibility(View.GONE);
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, null, 0);
            }
        } else {
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, null, 1);
            }
            ivImage.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(ivImage, SPref.getInstance().getString(context, SPref.IMAGE_FORGOT_PASSWORD_BG), R.drawable.ses_bg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSubmit:
                if (isValid()) {
                    CustomLog.d("isValid()", "" + true);
                    callForgotPasswordApi();
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void callForgotPasswordApi() {
        try {
            if (isNetworkAvailable(context)) {
                //  bSignIn.setText(Constant.TXT_SIGNING_IN);
                showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FORGOT);
                    request.params.put(Constant.KEY_EMAIL, email);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse vo = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(vo.getError())) {
                                    JSONObject json = new JSONObject(response);

                                        if (json.get(Constant.KEY_RESULT) instanceof String) {
                                            goToScreenAsPerResult(json);
                                        }
                                        /*if (json.get(Constant.KEY_RESULT) instanceof String) {
                                            String str = json.getJSONObject("result").getString("success");
                                            Util.showSnackbar(v, str);
                                        } else {
                                            SignInResponse resp = new Gson().fromJson(response, SignInResponse.class);
                                            if (resp.getResult().isOtpEnabled()) {
                                                openOtpFragment(OTPFragment.FROM_FORGOT, email, null);
                                            }
                                        }*/
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }
                                    /*BaseResponse<UserMaster> vo = new Gson().fromJson(response, BaseResponse.class);
                                    if (vo.isSuccess()) {
                                        //   bSignIn.setText(Constant.TXT_SUCCESS);
                                        UserMaster userVo = vo.getResult();
                                        userVo.setAuthToken(vo.getAuthToken());
                                        CustomLog.d("userVo", new Gson().toJson(userVo));
                                        //  UserMaster userVo = new Gson().fromJson(vo.getResult(), UserMaster.class);
                                    } else {
                                        // bSignIn.setText(Constant.TXT_SIGN_IN);
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }*/
                                }
                            } catch (JSONException e) {
                                CustomLog.e(e);
                            }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void goToScreenAsPerResult(JSONObject json) {
        try {
            String result = json.getString("result");
            switch (result) {
                /*case Constant.RESULT_FORM_0:
                case Constant.RESULT_FORM_1:
                    goToSignUpFragment();
                    break;
                case Constant.RESULT_FORM_2:
                    goToProfileImageFragment();
                    break;
                case Constant.RESULT_FORM_3:
                    int id = json.getInt("user_subscription_id");
                    openWebView(Constant.URL_SUBSCRIPTION + "&user_subscription_id=" + id, getStrings(R.string.TITLE_SUBSCRIPTION));
                    break;
                case Constant.RESULT_FORM_4:
                    fragmentManager.beginTransaction().replace(R.id.container, new JoinFragment()).commit();
                    break;*/
                case Constant.RESULT_FORM_OTP:
                case Constant.RESULT_FORM_OTP_LOGIN:
                    openOtpFragment(OTPFragment.FROM_FORGOT, email, null);
                    break;
                default:
                    String str = json.getJSONObject("result").getString("success");
                    Util.showSnackbar(v, str);
                    break;
               /* default:
                    fragmentManager.beginTransaction().replace(R.id.container, new WelcomeFragment()).commit();
                    break;*/
            }
        } catch (JSONException e) {
            CustomLog.e(e);
        }
    }

    private boolean isValid() {
        boolean result = false;
        email = etEmail.getText().toString();
        //       password = etPassword.getText().toString();
        if (!TextUtils.isEmpty(email) /*&& EMAIL_ADDRESS.matcher(email).matches()*/) {
            result = true;
        } else {
            Util.showSnackbar(v, Constant.MSG_INVALID_EMAIL);
            // etEmail.setError(txtInvalidEmail);
        }

        return result;
    }

    public static Fragment newInstance(OnUserClickedListener<Integer, Object> listener, String email) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.email = email;
        fragment.listener = listener;
        return fragment;
    }

}
