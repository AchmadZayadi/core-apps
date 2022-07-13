package com.sesolutions.ui.dashboard;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;

public class ShareSEFragment extends BaseFragment implements View.OnClickListener {

    private View v;

    private Share shareVo;
    private ImageView ivImage;
    private EditText etBody;
    private TextView tvImageDescription;
    private TextView tvImageTitle;
    private TextView tvShare;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_share_se, container, false);
        try {
            applyTheme(v);
            init();
            setData();

            new Handler().postDelayed(() -> {
                openKeyboard();
                etBody.requestFocus();
            }, 300);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void setData() {
        try {
            tvImageTitle.setText(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(shareVo.getTitle())));
            tvImageDescription.setText(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(shareVo.getDescription())));
            Glide.with(context).load(shareVo.getImageUrl()).into(ivImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        ivImage = v.findViewById(R.id.ivImage);
        etBody = v.findViewById(R.id.etBody);
        tvImageDescription = v.findViewById(R.id.tvImageDescription);
        tvImageDescription.setMovementMethod(new ScrollingMovementMethod());
        tvImageTitle = v.findViewById(R.id.tvImageTitle);
        tvShare = v.findViewById(R.id.tvShare);

        if (TextUtils.isEmpty(shareVo.getDescription())) {
            tvImageDescription.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(shareVo.getImageUrl())) {
            ivImage.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(shareVo.getTitle())) {
            tvImageTitle.setVisibility(View.GONE);
        }
        //bSave = v.findViewById(R.id.bSave);
       /* v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);*/
        tvShare.setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        //initSlide();
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvShare:
                    closeKeyboard();
                    String body = etBody.getText().toString();
                    if (TextUtils.isEmpty(body)) {
                        body = Constant.EMPTY;
                    } /*else {
                        body = StringEscapeUtils.escapeJava(body);
                    }*/
                    callShareSubmitApi(body);
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callShareSubmitApi(String body) {

        try {
            if (isNetworkAvailable(context)) {
                try {

                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_SHARE);

                    request.params.put(Constant.KEY_BODY, body);
                    request.params.put(Constant.KEY_TYPE, shareVo.getUrlParams().getType());
                    request.params.put(Constant.KEY_ID, shareVo.getUrlParams().getId());

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse3", "" + response);
                            if (response != null) {
                                hideBaseLoader();
                                BaseResponse<Object> res = new Gson().fromJson(response, BaseResponse.class);
                                if (TextUtils.isEmpty(res.getError())) {
                                    BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                    Util.showSnackbar(v, resp.getResult());
                                    onBackPressed();
                                } else {
                                    Util.showSnackbar(v, res.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
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
        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public static Fragment newInstance(Share serializable) {

        ShareSEFragment frag = new ShareSEFragment();
        frag.shareVo = serializable;
        return frag;
    }
}
