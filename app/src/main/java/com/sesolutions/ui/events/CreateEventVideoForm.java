package com.sesolutions.ui.events;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
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
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

/**
 * Created by root on 6/12/17.
 */

public class CreateEventVideoForm extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;
    private String module;


    //  AppCompatEditText etEmail;

    // private AppCompatButton bSubmit;
    //private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
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
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_ADD_NEW_VIDEO);
        v.findViewById(R.id.ivBack).setOnClickListener(this);

        //isVideoSelected = true;
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
                                    // BaseResponse<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    //  Dummy.Result result = vo.getResult();
                                    createFormUi(vo.getResult());
                                    hideInitially();
                                } else {
                                    notInternetMsg(v);
                                    //   bSignIn.setText(Constant.TXT_SIGN_IN);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                            return true;
                        }
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


    public static CreateEventVideoForm newInstance(int formType, Map<String, Object> map, String url) {
        CreateEventVideoForm frag = new CreateEventVideoForm();
        frag.url = url;
        frag.map = map;
        frag.FORM_TYPE = formType;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
       /* resource_video_type
        rotation,upload_video
                url*/
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("type")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = (String) Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());
                    if (null != key && key.equals("17")) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("url")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            } else if (name.equals("embedUrl")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                            }/* else if (name.equals("embedUrl") || name.equals("upload_video")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                            }*/
                            //CustomLog.d("tag1", "" + tag);
                        }
                    } else if (null != key) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("url")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                            } else if (name.equals("embedUrl")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            }
                        }
                    }

                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        try {
            if (null != result) {
                String filePath = ((List<String>) result).get(0);
                //TODO FETCH THUMBNAIL
                if (canShowThumbnail) {
                    mFormBuilder.getAdapter().setThumbnailAtTag(clickedFilePostion, getThumbnailPathForLocalFile(activity, Constant.videoUri));
                } else {
                    mFormBuilder.getAdapter().setThumbnailAtTag(clickedFilePostion, filePath);
                }
                mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
                Constant.videoUri = null;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }
}
