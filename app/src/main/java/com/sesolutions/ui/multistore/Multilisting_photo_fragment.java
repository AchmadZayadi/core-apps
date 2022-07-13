package com.sesolutions.ui.multistore;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Group;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;


public class Multilisting_photo_fragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int userId;
    private int text2;
    private Group resp;
    private boolean isContentLoaded;
    // private boolean showToolbar;

    public static Multilisting_photo_fragment newInstance(int userId, boolean isToolbarHidden) {
        Multilisting_photo_fragment frag = new Multilisting_photo_fragment();
        frag.userId = userId;
        //  frag.showToolbar = isToolbarHidden;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_multilisting_photo, container, false);
        try {
            applyTheme(v);
          } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
            text2 = Color.parseColor(Constant.text_color_2);
            callMusicAlbumApi();
        }
    }

    private void init() {
        v.findViewById(R.id.mScrollView).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        if (resp != null) {
            llMain = v.findViewById(R.id.llInfo);
            if (!TextUtils.isEmpty(resp.getTitle())) {
                v.findViewById(R.id.tvGroupTitle).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvGroupTitle)).setText(resp.getTitle());
            }
            if (!TextUtils.isEmpty(resp.getCategoryName())) {
                v.findViewById(R.id.tvGroupCategory).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvGroupCategory)).setText(resp.getCategoryName());
            }
            if (!TextUtils.isEmpty(resp.getDescription())) {
                v.findViewById(R.id.tvGroupDesc).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvGroupDesc)).setText(resp.getDescription());
            }
            if (!TextUtils.isEmpty(resp.getOwnerTitle())) {
                v.findViewById(R.id.tvOwnerTitle).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvOwnerTitle)).setText(resp.getOwnerTitle());
            }
            if (resp.getViewCount() != null) {
                v.findViewById(R.id.tvTotalView).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvTotalView)).setText(resp.getViewCount().getAsString());
            }
            if (!TextUtils.isEmpty(resp.getMemberCount())) {
                v.findViewById(R.id.tvTotalMember).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvTotalMember)).setText(resp.getMemberCount());
            }
            if (!TextUtils.isEmpty(resp.getModifiedDate())) {
                v.findViewById(R.id.tvLastUpdated).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvLastUpdated)).setText(getStrings(R.string.last_updated) + " " + resp.getModifiedDate());
            }
            isContentLoaded = true;
        }
    }

    private void callMusicAlbumApi() {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    showView(v.findViewById(R.id.pbMain));
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CGROUP_INFO);
                    request.params.put(Constant.KEY_ID, userId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    // request.headres.put("Content-Type", "application/x-www-form-urlencoded");
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideView(v.findViewById(R.id.pbMain));
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        //String result=
                                        JsonElement res = new Gson().fromJson(response, JsonElement.class);
                                        resp = new Gson().fromJson(res.getAsJsonObject().get("result"), Group.class);


                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                                init();

                            } catch (Exception e) {
                                hideView(v.findViewById(R.id.pbMain));
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideView(v.findViewById(R.id.pbMain));
                    Log.d(Constant.TAG, "Error while login" + e);
                }
                Log.d(Constant.TAG, "login Stop");
            } else {

                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                /*case R.id.ivBack:
                    onBackPressed();
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
