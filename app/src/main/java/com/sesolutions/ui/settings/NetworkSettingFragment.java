package com.sesolutions.ui.settings;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.tabs.TabLayout;
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
import com.sesolutions.responses.Networks;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class NetworkSettingFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, String> {

    private View v;
    private RecyclerView rvSetting;
    private NetworkAdapter adapter;
    private List<Networks> settingList;
    private boolean isNetworkASelected;
    // private TextView tvNetworkA;
    // private TextView tvNetworkM;
    private int menuButtonBackgroundColor;
    private int menuButtonTitleColor;
    private int menuButtonActiveTitleColor;
    private TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_network_settings, container, false);
        try {
            applyTheme(v);
            menuButtonBackgroundColor = Color.parseColor(Constant.menuButtonBackgroundColor);
            menuButtonTitleColor = Color.parseColor(Constant.menuButtonTitleColor);
            menuButtonActiveTitleColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void toggleTab() {
        if (isNetworkASelected) {
           /* tvNetworkA.setTextColor(menuButtonActiveTitleColor);
            tvNetworkA.setBackgroundColor(menuButtonBackgroundColor);
            tvNetworkM.setBackgroundColor(menuButtonBackgroundColor);
            tvNetworkM.setTextColor(menuButtonTitleColor);*/
            callSubmitApi(2);
        } else {
           /* tvNetworkA.setBackgroundColor(menuButtonBackgroundColor);
            tvNetworkA.setTextColor(menuButtonTitleColor);
            tvNetworkM.setBackgroundColor(menuButtonBackgroundColor);
            tvNetworkM.setTextColor(menuButtonActiveTitleColor);*/
            callSubmitApi(1);
        }
    }

    private void init() {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_NETWORK_SETTING);
        rvSetting = v.findViewById(R.id.rvSetting);
        /*tvNetworkA = v.findViewById(R.id.tvNetworkA);
        tvNetworkM = v.findViewById(R.id.tvNetworkM);
        tvNetworkA.setOnClickListener(this);
        tvNetworkM.setOnClickListener(this);*/

        initRecyclerView();

        isNetworkASelected = true;
        toggleTab();
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(menuButtonActiveTitleColor);
        tabLayout.setTabTextColors(menuButtonTitleColor, menuButtonActiveTitleColor);
        tabLayout.setBackgroundColor(menuButtonBackgroundColor);
        tabLayout.addTab(tabLayout.newTab().setText(Constant.available_networks), true);
        tabLayout.addTab(tabLayout.newTab().setText(Constant.my_networks));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isNetworkASelected = tab.getPosition() == 0;
                toggleTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        v.findViewById(R.id.ivBack).setOnClickListener(this);
    }


    private void initRecyclerView() {
        try {
            settingList = new ArrayList<>();
            rvSetting.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvSetting.setLayoutManager(layoutManager);
            adapter = new NetworkAdapter(settingList, context, this);
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
               /* case R.id.tvNetworkA:
                    if (!isNetworkASelected) {
                        isNetworkASelected = true;
                        toggleTab();
                    }
                    break;
                case R.id.tvNetworkM:
                    if (isNetworkASelected) {
                        isNetworkASelected = false;
                        toggleTab();
                    }
                    break;*/

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callSubmitApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_NETWORK_SETTING);
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
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        settingList.clear();
                                        if (req == 1) {
                                            if (null != comResp.getResult().getNetworkSelected()) {
                                                settingList.addAll(comResp.getResult().getNetworkSelected());
                                                adapter.setMine(true);
                                            }
                                        } else {
                                            if (null != comResp.getResult().getNetworkAvailable()) {
                                                settingList.addAll(comResp.getResult().getNetworkAvailable());
                                                adapter.setMine(false);
                                            }
                                        }
                                        updateAdapter();
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

    private void callJoinLeaveApi(int networkId, final int position) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_NETWORK_SETTING);

                    request.params.put(isNetworkASelected ? Constant.KEY_JOIN_ID : Constant.KEY_LEAVE_ID, networkId);
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
                                        settingList.remove(position);
                                        adapter.notifyDataSetChanged();

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
        adapter.notifyDataSetChanged();        runLayoutAnimation(rvSetting);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(isNetworkASelected ? Constant.MSG_NO_NETWORK :
                Constant.MSG_NOTHING_TO_JOIN);
        rvSetting.setVisibility(settingList.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.tvNoData).setVisibility(settingList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        callJoinLeaveApi(object1, postion);
        return false;
    }


    private void goToFragment(String url, String title) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, GeneralSettingFragment.newInstance(url, title))
                .addToBackStack(null).commit();
    }
}
