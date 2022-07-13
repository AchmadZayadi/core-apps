package com.sesolutions.ui.groups;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;

public class GroupJoinFragment extends FormHelper implements View.OnClickListener {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private String requestTitle;
    private String requestDesc;

    public static GroupJoinFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result, String title, String desc) {
        GroupJoinFragment fragment = new GroupJoinFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.result = result;
        fragment.requestTitle = title;
        fragment.requestDesc = desc;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_group_join, container, false);
        try {
            applyTheme(v);
            initScreenData();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();

        /*calling api only if no response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            ((TextView) v.findViewById(R.id.tvRequestTitle)).setText(requestTitle);
            ((TextView) v.findViewById(R.id.tvRequestDesc)).setText(requestDesc);
            createFormUi(result);
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.join_group));
        mRecyclerView = v.findViewById(R.id.recyclerView);
    }


    @Override
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

    private void callSignUpApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    result = vo.getResult();
                                    ((TextView) v.findViewById(R.id.tvRequestTitle)).setText(requestTitle);
                                    ((TextView) v.findViewById(R.id.tvRequestDesc)).setText(requestDesc);
                                    applyCustomChange();
                                    createFormUi(result);
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyCustomChange() {
        //apply Custom Change in FormData
        try {
            for (Dummy.Formfields fld : result.getFormfields()) {
                if (fld.getName().startsWith("dummytitle")) {
                    fld.setType(Constant.TITLE);
                    fld.setTitleBold(true);
                    //fld.setStringValue(result.getFormFielsByName("dummybody6").getValue());
                } else if (fld.getName().startsWith("dummybody")) {
                    //if ("dummybody6".equals(fld.getName())) {
                    fld.setType(Constant.TITLE);
                    //result.getFormFielsByName("dummytitle6").setStringValue(fld.getValue());
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }
}
