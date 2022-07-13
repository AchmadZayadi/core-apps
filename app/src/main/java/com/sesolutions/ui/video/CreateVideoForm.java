package com.sesolutions.ui.video;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
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

/**
 * Created by root on 6/12/17.
 */

public class CreateVideoForm extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final String TAG = "CreateVideoForm";

    private static final int CODE_LOGIN = 100;
    private String module;

    //  AppCompatEditText etEmail;

    // private AppCompatButton bSubmit;
    //private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
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
        //   bConitinue.setVisibility(View.GONE);
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

                    if(RESUMEID!=0){
                        request.params.put(Constant.KEY_RESUME_ID, RESUMEID);
                    }

                    request.params.put(Constant.KEY_AUTH_TOKEN, Constant.EMPTY);
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
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        createFormUi(vo.getResult());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                    //  Dummy.Result result = vo.getResult();

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


    public static CreateVideoForm newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        CreateVideoForm frag = new CreateVideoForm();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }

    public static CreateVideoForm newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener,int resumeid) {
        CreateVideoForm frag = new CreateVideoForm();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
        frag.RESUMEID=resumeid;
       /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }
    String statId="";

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {

        super.onValueChanged(baseFormElement);


        if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
            String name = baseFormElement.getName();
            CustomLog.e("createvideo",""+name);
            try {
                if (name.equals(Constant.KEY_COUNTRY_ID)
                ) {
                    statId = "";
                    statId = Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());

                    statId = statId.replaceAll(" ", "%20");
                    CustomLog.e("STATEID", "" + statId);
                    String url;// = name.equals(Constant.KEY_CATEGORY_ID) ? Constant.URL_SUB_CATEGORY : Constant.URL_SUB_SUB_CATEGORY;
                    if (name.equals(Constant.KEY_COUNTRY_ID)) {
                        //  map.put(Constant.KEY_COUNTRY_ID, catId);

                        url = Constant.BASE_URL + "eresume/index/states/id/" + statId.trim() + Constant.POST_URL;
                    }
                    if (name.equals(Constant.KEY_COUNTRY_ID)) {
                        //  map.put(Constant.KEY_COUNTRY_ID, catId);

                        url = Constant.BASE_URL + "/eresume/index/states/id/" + statId + Constant.POST_URL;
                    } else {

                        map.put(Constant.KEY_SUB_CATEGORY_ID, statId);
                        url = Constant.URL_SUB_SUB_CATEGORY;
                    }
                    url=url.replaceAll(" ","%20");
                    callCategoryApi(null, url, baseFormElement.getTag(),1);
                }

                if (name.equals(Constant.KEY_STATE_ID)
                ) {

                    String catId = Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());
                    String url;// = name.equals(Constant.KEY_CATEGORY_ID) ? Constant.URL_SUB_CATEGORY : Constant.URL_SUB_SUB_CATEGORY;
                    if (name.equals(Constant.KEY_STATE_ID)) {
                        //  map.put(Constant.KEY_COUNTRY_ID, catId);
                        url = Constant.BASE_URL + "eresume/index/cities/id/" + catId.replaceAll(" ","%20") + "/country/" + statId + Constant.POST_URL;
                    } else {

                        //   map.put(Constant.KEY_SUB_CATEGORY_ID, catId);
                        url = Constant.URL_SUB_SUB_CATEGORY;
                    }
                    url.replaceAll(" ","%20");
                    callCategoryApi(null, url, baseFormElement.getTag(),2);
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }

        }
    }


    private void callCategoryApi(Map<String, Object> map, String url, final int tag, int tagid) {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = "GET";
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
                                    CommonResponse vo = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        if (vo.getResult().isSubCategoryNotNull()) {
                                            updateFormItemOptions(tag, vo.getResult().getSubCategory());
                                        } else if (vo.getResult().isSubSubCategoryNotNull()) {
                                            updateFormItemOptions(tag, vo.getResult().getSubSubCategory());
                                        }else if (vo.getResult().isresults()) {
                                            updateFormItemOptions(tag, vo.getResult().getresults());

                                            if ( vo.getResult().getresults().size() <1) {
                                                if(tagid==1){
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+1, true);
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+2, true);
                                                }else {
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+1, true);
                                                }

                                                mFormBuilder.getAdapter().notifyDataSetChanged();
                                            }else {
                                                if(tagid==1){
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+1, false);
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+2, true);
                                                }else {
                                                    mFormBuilder.getAdapter().setHiddenAtTag(tag+1, false);
                                                }

                                                mFormBuilder.getAdapter().notifyDataSetChanged();
                                            }


                                        }
                                    }/* else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }*/
                                }
                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e("FORM_TYPE", "" + FORM_TYPE);
                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void updateFormItemOptions(int tag, Map<String, String> subCategory) {
        if (subCategory.size() > 0) {
            mFormBuilder.getAdapter().setOptionAtTag(tag + 1, getMultiOptionsList(subCategory));
            FormElementPickerSingle element = (FormElementPickerSingle) mFormBuilder.getFormElement(tag + 1);
            commonMap.put(element.getName(), subCategory);
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
}
