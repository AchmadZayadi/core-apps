package com.sesolutions.ui.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.news.Check;
import com.sesolutions.responses.news.Result;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateRSSFragment extends FormHelper implements View.OnClickListener {


    private Dummy.Result result;
    private Result checkResult;

    public static CreateRSSFragment newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        CreateRSSFragment frag = new CreateRSSFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        this.callSignUpApi();
    }


    private void init() {
        v.findViewById(R.id.appBar).setVisibility(View.GONE);
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
        //  tvTitle = v.findViewById(R.id.tvTitle);
        //   setTitle();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

   /* private void setTitle() {
        String title = "Form";
        switch (FORM_TYPE) {
            case Constant.FormType.TYPE_ADD_SONG:
                title = (Constant.TITLE_ADD_SONG);
                break;
            case Constant.FormType.TYPE_ADD_ALBUM:
                title = (Constant.TITLE_ADD_ALBUM);
                break;
            case Constant.FormType.ADD_VIDEO:
                title = (Constant.TITLE_ADD_VIDEO);
                break;
        }
        tvTitle.setText(title);
    }*/

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                String name = ((FormElementPickerSingle) baseFormElement).getName();
                if (name.equals("show_start_time")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = (String) Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());
                    if (null != key) {
                        boolean hideOrShow = (key.equals("0") || key.equals(""));
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name2 = tagList.get(i);
                            if (name2.equals("starttime")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideOrShow);
                                break;
                            } /*else if (name2.equals("rotation") || name2.equals("upload_video")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            }*/
                        }
                        mFormBuilder.getAdapter().notifyDataSetChanged();
                        //  mFormBuilder.getAdapter().notifyItemRangeRemoved(1, result.getFormfields().size() - 2);
                    } /*else {
                    createFormUi(result);
                    mFormBuilder.getAdapter().setValueAtIndex(0, baseFormElement.getValue());
                }*/
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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

    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("starttime")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            } /*else if (name.equals("rotation") || name.equals("upload_video")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }*/
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
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
                                    applyCustomChange();
                                    createFormUi(result);
                                    hideInitially();
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception ignore) {

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void applyCustomChange() {

        result.getFormFielsByName("rss_link").setType(Constant.CUSTOM_URL);

    }

    public void checkClicked(int tag) {

        String url = mFormBuilder.getAdapter().getValueAtTag(tag).getValue();
        CustomLog.e("url", "" + url);

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CHECKURL_OF_RSS);
                    request.params.put("urlsubmit", url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("checkurl_repsonse", "" + response);

                                if (response != null) {

                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);

                                    if (TextUtils.isEmpty(err.getError())){

                                        Check vo = new Gson().fromJson(response, Check.class);
                                        checkResult = vo.getResult();

                                        Util.showSnackbar(v,  checkResult.toString());

                                        formItems.get(1).setValue(checkResult.getTitle());
                                        formItems.get(2).setValue(checkResult.getDescription());

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
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

                } catch (Exception ignore) {

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        // (List<String>) result;
        if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public static CreateRSSFragment newInstance(int type, Map<String, Object> map, String url) {
        CreateRSSFragment fragment = new CreateRSSFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }
}
