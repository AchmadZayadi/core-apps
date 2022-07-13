package com.sesolutions.ui.common;

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

public class CreateProfileVideoForm extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

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

        isVideoSelected = true;
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

    @Override
    public void checkChooserOption(String name) {
        isVideoSelected = !("photo_id".equals(name));
    }

    private void callSignUpApi() {

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

                                if (FORM_TYPE == Constant.FormType.CREATE_PAGE_VIDEO) {
                                    int listSize = vo.getResult().getFormfields().size();
                                    Dummy.Formfields fld = new Dummy.Formfields();
                                    fld.setName("Filedata");
                                    fld.setType(Constant.FILE);
                                    fld.setLabel(getStrings(R.string.txt_upload_video));
                                    fld.setStringValue("");
                                    vo.getResult().getFormfields().add(listSize - 2, fld);

                                }
                                createFormUi(vo.getResult());
                                hideInitially();
                            } else {
                                somethingWrongMsg(v);

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

    }

    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            String name = tagList.get(i);
            if (name.equals("Filedata") || name.equals("url") || name.equals("rotation") || name.equals("embedUrl")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }


    public static CreateProfileVideoForm newInstance(int formType, Map<String, Object> map, String url) {
        CreateProfileVideoForm frag = new CreateProfileVideoForm();
        frag.url = url;
        frag.map = map;
        frag.FORM_TYPE = formType;
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
                    String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());
                    if (null != key) {
                        if (key.equals("0")) {
                            for (int i = 0; i < tagList.size(); i++) {
                                int tag = 1011 + i;
                                String name = tagList.get(i);
                                if (name.equals("url") || name.equals("Filedata")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                                }
                            }
                        } else if (key.equals("iframely")) {
                            for (int i = 0; i < tagList.size(); i++) {
                                int tag = 1011 + i;
                                String name = tagList.get(i);
                                if (name.equals("url")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                                } else if (name.equals("Filedata")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                                }
                            }
                        } else if (key.equals("3")) {
                            for (int i = 0; i < tagList.size(); i++) {
                                int tag = 1011 + i;
                                String name = tagList.get(i);
                                if (name.equals("Filedata")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                                } else if (name.equals("url")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                                }
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
