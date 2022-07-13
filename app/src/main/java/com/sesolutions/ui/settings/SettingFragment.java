package com.sesolutions.ui.settings;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Settings;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, String> {

    private View v;
    private RecyclerView rvSetting;
    private SettingAdapter adapter;
    private List<Settings> settingList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        try {
            applyTheme(v);
            init();
            initRecyclerView();
            callSubmitApi();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        rvSetting = v.findViewById(R.id.rvSetting);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_SETTINGS);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
    }


    private void initRecyclerView() {
        try {
            settingList = new ArrayList<>();
            rvSetting.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvSetting.setLayoutManager(layoutManager);
            adapter = new SettingAdapter(settingList, context, this);
            rvSetting.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callSubmitApi() {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SETTINGS);
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
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        if (null != comResp.getResult().getSettings()) {
                                            settingList.addAll(comResp.getResult().getSettings());
                                            updateAdapter();
                                        }
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                        goIfPermissionDenied(comResp.getError());
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

    private void callDeleteAccountApi() {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_ACCOUNT_DELETE);
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
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        //TODO change this this will exit app
                                        SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, "");
                                        SPref.getInstance().removeDataOnLogout(context);
                                        new Handler().postDelayed(() -> activity.finishAffinity(), 2500);

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

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(rvSetting);
        rvSetting.setVisibility(settingList.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.tvNoData).setVisibility(settingList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {

        switch (object2) {
            case Constant.SETTING_GENERAL:
                goToGeneralSettingForm(Constant.URL_GENERAL_SETTING, Constant.TITLE_GENERAL_SETTING);
                break;
            case Constant.SETTING_PRIVACY:
                goToGeneralSettingForm(Constant.URL_GENERAL_PRIVACY, Constant.TITLE_PRIVACY_SETTING);
                break;
            case Constant.SETTING_NETOWRK:
                goToNetworkFragment();
                break;
            case Constant.SETTING_NOTIFICATION:
                goToGeneralSettingForm(Constant.URL_GENERAL_NOTIFICATIONS, Constant.TITLE_NOTIFICATION_SETTING);
                break;
            case Constant.SETTING_PASSWORD:
                goToGeneralSettingForm(Constant.URL_GENERAL_PASSWORD, Constant.TITLE_PASSWORD_SETTING);
                break;
            case Constant.SETTING_NUMBER:
                openFormFragment(Constant.FormType.CHANGE_NUMBER, null, Constant.URL_ACCOUNT_NUMBER);
                break;
            case Constant.SETTING_DELETE:
                showDeleteDialog();
                break;
        }
        return false;
    }

    private void goToNetworkFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new NetworkSettingFragment()).addToBackStack(null).commit();
    }


    public void showDeleteDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_ACCOUNT_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText("Batal");

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteAccountApi();

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


}
