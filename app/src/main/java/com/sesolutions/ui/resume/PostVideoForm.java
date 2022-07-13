package com.sesolutions.ui.resume;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

import me.riddhimanadib.formmaster.model.BaseFormElement;

/**
 * Created by root on 6/12/17.
 */

public class PostVideoForm extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final String TAG = "PostVideoForm";

    private static final int CODE_LOGIN = 100;
    private String module;

    // AppCompatEditText etEmail;
    // private AppCompatButton bSubmit;
    // private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        applyTheme(v);
        return v;
    }

    public void initScreenData() {
        init();
        callSignUpApi();
    }

    private void init() {
        v.findViewById(R.id.appBar).setVisibility(View.GONE);
        isVideoSelected = true;
        //  cbTnC = v.findViewById(R.id.cbTnC);
        //  cbTnC.setOnCheckedChangeListener(this);
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
                   /* if (!TextUtils.isEmpty(module)) {
                        request.params.put(Constant.KEY_MODULE, module);
                    }*/
                    request.params.put(Constant.KEY_AUTH_TOKEN, Constant.EMPTY);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                Log.e(TAG, "handleMessage: " + response);
                                if (response != null) {
                                    // BaseResponse<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    // Dummy.Result result = vo.getResult();
                                    createFormUi(vo.getResult());
                                    hideInitially();
                                } else {
                                    notInternetMsg(v);
                                    // bSignIn.setText(Constant.TXT_SIGN_IN);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    Log.e(TAG, "callSignUpApi: " + e.getMessage());
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            Log.e(TAG, "callSignUpApi: " + e.getMessage());
        }

    }

    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("url")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
            } else if (name.equals("rotation") || name.equals("Filedata") || name.equals("upload_video")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }


    public static PostVideoForm newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        PostVideoForm frag = new PostVideoForm();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
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
                if (baseFormElement.getName().equals("type") || baseFormElement.getName().equals("resource_video_type")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());
                    if (null != key && key.equals("upload") || key.equals("3")) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("url")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            } else if (name.equals("rotation") || name.equals("Filedata") || name.equals("upload_video")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                            }
                            //CustomLog.d("tag1", "" + tag);
                        }
                    } else if (null != key) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("url")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                            } else if (name.equals("rotation") || name.equals("Filedata") || name.equals("upload_video")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            }
                        }
                    }

                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
            // {"url":"https://youtu.be/0BpTPiV0TjE","resource_video_type":"1","search":"1","module":"sesvideo","upload":"","artists[4]":"4","title":"nxnzns","description":"the needful at your earliest response","auth_comment":"everyone","auth_view":"everyone","location":"the nxnxmxndnd","subject":"","tags":"the needful at","category_id":"13","file_type_video":""}
        }
    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        try {
            if (null != result) {
                String filePath = ((List<String>) result).get(0);
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
