package com.sesolutions.ui.settings;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;


public class ContactUsFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private EditText etName;
    private EditText etEmail;
    private EditText etMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        try {
            applyTheme(v);
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {

        etName = v.findViewById(R.id.etName);
        String text = "<font color=#484744>Nama</font> <font color=#FF0000>*</font>";
        etName.setHint(Html.fromHtml(text));
        etEmail = v.findViewById(R.id.etEmail);
        String text2 = "<font color=#484744>Email</font> <font color=#FF0000>*</font>";
        etEmail.setHint(Html.fromHtml(text2));
        etMessage = v.findViewById(R.id.etMessage);
        String text3 = "<font color=#484744>Pesan</font> <font color=#FF0000>*</font>";
        etMessage.setHint(Html.fromHtml(text3));

        try {
            ((CardView) v.findViewById(R.id.cvContact)).setCardBackgroundColor(Color.parseColor(Constant.colorPrimary));

        }catch (Exception ex){
            ex.printStackTrace();
        }

        v.findViewById(R.id.tvDone).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   onBackPressed();
                getActivity().finish();
            }
        });
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvDone:
                    sendMessageifValid();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void sendMessageifValid() {
        closeKeyboard();
        String message = etMessage.getText().toString();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Util.showSnackbar(v, Constant.MSG_NAME_MISSING);
        } else if (TextUtils.isEmpty(email)) {
            Util.showSnackbar(v, Constant.MSG_EMAIL_MISSING);
        } else if (isInvalidEmail(email)) {
            Util.showSnackbar(v, Constant.MSG_EMAIL_INVALID);
        } else if (TextUtils.isEmpty(message)) {
            Util.showSnackbar(v, Constant.MSG_MESSAGE_MISSING);
        } else {
            callSubmitApi(name, email, message);
        }
    }

    private boolean isInvalidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return !email.matches(emailPattern);
    }


    private void callSubmitApi(String name, String email, String message) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTACT_US);
                    request.params.put(Constant.KEY_NAME, name);
                    request.params.put(Constant.KEY_EMAIL, email);
                    request.params.put(Constant.KEY_BODY, message);
                    // request.params.put(Constant.KEY_FEELING_TYPE, feelVo.getFeeling_type());

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    // response = response.replace("â\u0080\u0099", "'");
                                    BaseResponse comResp = new Gson().fromJson(response, BaseResponse.class);
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        etEmail.setText(Constant.EMPTY);
                                        etName.setText(Constant.EMPTY);
                                        etMessage.setText(Constant.EMPTY);
                                        Util.showSnackbar(v, res.getResult());
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
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
            hideBaseLoader();
        }
    }
}
