package com.sesolutions.ui.profile;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.MemberGridMapAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class InfoFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private LinearLayoutCompat llMain;
    private List<Options> infoList;
    private int userId;
    private int text2;
    private boolean showToolbar;

    public static InfoFragment newInstance(int userId, boolean isToolbarHidden) {
        InfoFragment frag = new InfoFragment();
        frag.userId = userId;
        frag.showToolbar = isToolbarHidden;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_info, container, false);
        try {
            applyTheme(v);
            if (!showToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            } else {
                initScreenData();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        text2 = Color.parseColor(Constant.text_color_1);
        init();
        callMusicAlbumApi();
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_INFO);
        llMain = v.findViewById(R.id.llInfo);
    }

    private void addTextViews(List<Options> tabs) {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        try {
            for (int i = 0; i < tabs.size(); i++) {
                final Options tab = tabs.get(i);

                TextView tv = new TextView(context);
                tv.setLayoutParams(params);
                tv.setId(1000 + i);
                tv.setPadding(16, 8, 16, 8);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(text2);


                if (tab.getName().contains("heading")) {
                    tv.setText(tab.getValue());
                    tv.setGravity(Gravity.CENTER);
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                    tv.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_light));

                    llMain.addView(tv);

                } else {
                    LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View l = vi.inflate(R.layout.infotextlayout, null);

                    TextView leftTextView = (TextView) l.findViewById(R.id.tvTitleName);
                    leftTextView.setText(tab.getName()+": ");

                    TextView textvalue = (TextView) l.findViewById(R.id.titleValue);
                    textvalue.setText(tab.getValue());

                 //   tv.setText(tab.getName() + " : " + tab.getValue());

                    if (tab.getValue().startsWith("http") || tab.getValue().startsWith("www.")) {
                        l.setOnClickListener(v1 -> openWebView(tab.getValue(), "Website"));
                        textvalue.setTextColor(Color.parseColor("#0000FF"));
                        textvalue.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                    llMain.addView(l);
                }


            //    llMain.addView(tv);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callMusicAlbumApi() {
        try {
            if (isNetworkAvailable(context)) {

                try {
//                    if (showToolbar) {
                    showBaseLoader(true);
//                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_PROFILE_INFO);
                    request.params.put(Constant.KEY_ID, userId);
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
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        infoList = resp.getResult().getInfo();
                                        if (infoList != null) {
                                            addTextViews(infoList);
                                        }

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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
