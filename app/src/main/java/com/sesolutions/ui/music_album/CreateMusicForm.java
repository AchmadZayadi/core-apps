package com.sesolutions.ui.music_album;

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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

import me.riddhimanadib.formmaster.model.FormElementMusicFile;

public class CreateMusicForm extends FormHelper implements View.OnClickListener {

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
        v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
        //isVideoSelected = true;
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.add_songs));
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
        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            try {
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_GET_FORM, 1);
                   /* if (!TextUtils.isEmpty(module)) {
                        request.params.put(Constant.KEY_MODULE, module);
                    }*/
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse", "" + response);
                        if (response != null) {
                            // ErrorRes<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                            Dummy vo = new Gson().fromJson(response, Dummy.class);
                            if (vo.isSuccess()) {
                                Dummy.Result result = vo.getResult();
                                if (result.getFormfields() != null)
                                    for (Dummy.Formfields obj : result.getFormfields()) {
                                        if ("file".equals(obj.getName())) {
                                            /* name=file means it is music select block,
                                             *changing the name so that it can be identified on its super class*/
                                            obj.setType(Constant.KEY_MUSIC_SONG);
                                            break;
                                        }
                                    }
                                createFormUi(vo.getResult());
                            } else {
                                Util.showSnackbar(v, vo.getErrorMessage());
                                goIfPermissionDenied(vo.getMessage());
                            }
                            //hideInitially();
                        } else {
                            somethingWrongMsg(v);
                            onBackPressed();
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }

    /*private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("url")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
            } else if (name.equals("rotation") || name.equals("upload_video")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }*/


    public static CreateMusicForm newInstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        CreateMusicForm frag = new CreateMusicForm();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }

   /* @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
       *//* resource_video_type
        rotation,upload_video
                url*//*
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("resource_video_type")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = (String) Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());
                    if (null != key && key.equals("3")) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("url")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            } else if (name.equals("rotation") || name.equals("upload_video")) {
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
                            } else if (name.equals("rotation") || name.equals("upload_video")) {
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
    }*/

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        if (reqCode == REQ_CODE_MUSIC) {
            try {
                if (null != result) {
                    //  String filePath = ((List<String>) result).get(0);
                    FormElementMusicFile element = ((FormElementMusicFile) mFormBuilder.getFormElement(clickedFilePostion));
                    element.setMusicList((List<String>) result);
                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } else {
            if (null != result) {
                String filePath = ((List<String>) result).get(0);
                 if (canShowThumbnail) {
                    mFormBuilder.getAdapter().setThumbnailAtTag(clickedFilePostion, getThumbnailPathForLocalFile(activity, Constant.videoUri));
                } else {
                    mFormBuilder.getAdapter().setThumbnailAtTag(clickedFilePostion, filePath);
                }
                mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
            }
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }
}
