package com.sesolutions.ui.video;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelInfoFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ChannelInfoFragment";

    private View v;

    private int channelId;
    private TextView tv12;
    private TextView tv22;
    private TextView tv32;
    private TextView tv42;
    private TextView tv52;
    private TextView tv11;
    private TextView tv21;
    private TextView tv31;
    private TextView tv41;
    private TextView tv51;
    private String resourceType;

    public static ChannelInfoFragment newInstance(int userId, String resourceType) {
        ChannelInfoFragment frag = new ChannelInfoFragment();
        frag.channelId = userId;
        frag.resourceType = resourceType;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_channel_info, container, false);
        try {
            new ThemeManager().applyTheme((ViewGroup) v, context);
            init();
            callMusicAlbumApi();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_INFO);
        tv12 = v.findViewById(R.id.tv12);
        tv22 = v.findViewById(R.id.tv22);
        tv32 = v.findViewById(R.id.tv32);
        tv42 = v.findViewById(R.id.tv42);
        tv52 = v.findViewById(R.id.tv52);
        tv11 = v.findViewById(R.id.tv11);
        tv21 = v.findViewById(R.id.tv21);
        tv31 = v.findViewById(R.id.tv31);
        tv41 = v.findViewById(R.id.tv41);
        tv51 = v.findViewById(R.id.tv51);
    }

    private void callMusicAlbumApi() {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_INFO);
                    request.params.put(Constant.KEY_ID, channelId);
                    request.params.put(Constant.KEY_CHANNEL_ID, channelId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    JSONObject json = new JSONObject(response);
                                    JSONArray info = json.getJSONObject("result").getJSONArray("info");
                                    for (int i = 0; i < info.length(); i++) {
                                        JSONObject child = info.getJSONObject(i);
                                        showChildItem(child);
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
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
            hideBaseLoader();
        }
    }

    private void showChildItem(JSONObject child) {
        try {
            if (child.has("category")) {
                String category = child.getJSONObject("category").getString("name");
                v.findViewById(R.id.ll1).setVisibility(View.VISIBLE);
                tv12.setText(category);
            } else if (child.has("subcategory")) {
                String category = child.getJSONObject("subcategory").getString("name");
                v.findViewById(R.id.ll2).setVisibility(View.VISIBLE);

                tv22.setText(category);
            } else if (child.has("subsubcategory")) {
                String category = child.getJSONObject("subsubcategory").getString("name");
                v.findViewById(R.id.ll3).setVisibility(View.VISIBLE);

                tv32.setText(category);
            } else if (child.has("tags")) {
                JSONArray tags = child.getJSONArray("tags");
                String tag = "";
                for (int i = 0; i < tags.length(); i++) {
                    String name = tags.getJSONObject(i).getString("name");
                    if (!TextUtils.isEmpty(name))
                        tag = tag + "\n" + name;
                }
                v.findViewById(R.id.ll4).setVisibility(View.VISIBLE);

                tv42.setText(tag.trim());

            } else if (child.has("description")) {
                String description = child.getString("description");
                v.findViewById(R.id.ll5).setVisibility(View.VISIBLE);
                tv52.setText(description);
            }
        } catch (JSONException e) {
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
}
