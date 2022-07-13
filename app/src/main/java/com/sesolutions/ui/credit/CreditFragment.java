package com.sesolutions.ui.credit;


import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sesolutions.responses.SesResponse;
import com.sesolutions.responses.credit.CreditResult;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

public class CreditFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private View v;
    public String selectedScreen = "";
    private OnUserClickedListener<Integer, Object> parent;

    //variable used when called from page view -> associated
    private int mPageId;


    public static CreditFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        CreditFragment frag = new CreditFragment();
        frag.parent = parent;
        frag.selectedScreen = TYPE;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_credit, container, false);
        applyTheme(v);
        return v;
    }


    public void init() {

    }


    public void initScreenData() {
        init();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {


        if (isNetworkAvailable(context)) {
            try {
                if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL.CREDIT_MANAGE); //url will change according to screenType

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;

                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                            if (TextUtils.isEmpty(resp.getError())) {

                                //if screen is refreshed then clear previous data


                                CreditResult result = resp.getResult(CreditResult.class);
                                initBodyLayout(result.getBody());
                                initHeaderLayout(result.getHeader());

                                updateAdapter();
                            } else {
                                Util.showSnackbar(v, resp.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void initHeaderLayout(List<Options> header) {
        try {
            if (null != header) {
                v.findViewById(R.id.llCreditTop).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvLabel)).setText(header.get(0).getLabel());
                ((TextView) v.findViewById(R.id.tvValue)).setText(header.get(0).getValue());
                ((TextView) v.findViewById(R.id.tvLabel2)).setText(header.get(1).getLabel());
                ((TextView) v.findViewById(R.id.tvValue2)).setText(header.get(1).getValue());
                ((TextView) v.findViewById(R.id.tvLabel3)).setText(header.get(2).getLabel());
                ((TextView) v.findViewById(R.id.tvValue3)).setText(header.get(2).getValue());
                ((TextView) v.findViewById(R.id.tvLabel4)).setText(header.get(3).getLabel());
                ((TextView) v.findViewById(R.id.tvValue4)).setText(header.get(3).getValue());
            } else {
                v.findViewById(R.id.llCreditTop).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e("Error", "header item is incorrect");
        }
    }

    private void initBodyLayout(List<Options> optList) {
        LinearLayoutCompat llBasic = v.findViewById(R.id.llBottom);
        llBasic.removeAllViews();
        if (null != optList) {
            llBasic.setVisibility(View.VISIBLE);
        } else {
            llBasic.setVisibility(View.GONE);
            return;
        }
        View view11 = getLayoutInflater().inflate(R.layout.item_credit_header, (ViewGroup) llBasic, false);
        llBasic.addView(view11);

        for (Options opt : optList) {

            View view1 = getLayoutInflater().inflate(R.layout.item_credit, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(opt.getLabel());
            ((TextView) view1.findViewById(R.id.tv2)).setText(opt.getValue());
            ((TextView) view1.findViewById(R.id.tv3)).setText(opt.getAction());
            llBasic.addView(view1);
        }
        applyTheme(llBasic);
    }


    public void updateAdapter() {

        if (parent != null) {
            parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
        }
    }
}
