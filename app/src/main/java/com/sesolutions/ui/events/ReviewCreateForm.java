package com.sesolutions.ui.events;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;

/**
 * Created by root on 6/12/17.
 */

public class ReviewCreateForm extends FormHelper implements View.OnClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void initScreenData() {
        init();
        callSignUpApi();
    }

    private void init() {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(FORM_TYPE == Constant.FormType.EDIT_REVIEW ? R.string.edit_review : R.string.add_review);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
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
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.putAll(map);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    // BaseResponse<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    Dummy.Result result = vo.getResult();
                                    for (Dummy.Formfields f : result.getFormfields()) {
                                        if ("rate_value".equals(f.getName()) || f.getName().contains("review_parameter_value_")) {
                                            f.setType(Constant.RATE);
                                        } else if ("review_star".equals(f.getName()) || "review_parameters".equals(f.getName())) {
                                            f.setType(Constant.HIDDEN);
                                        }
                                    }
                                    createFormUi(result);
                                    hideInitially();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            } else {
                                notInternetMsg(v);
                                //   bSignIn.setText(Constant.TXT_SIGN_IN);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("embedUrl") || name.equals("embedUrl")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            } else if (name.equals("url") /*|| name.equals("upload_video")*/) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }


    public static ReviewCreateForm newInstance(int formType, Map<String, Object> map, String url) {
        ReviewCreateForm frag = new ReviewCreateForm();
        frag.url = url;
        frag.map = map;
        frag.FORM_TYPE = formType;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }
}
