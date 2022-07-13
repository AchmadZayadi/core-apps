package com.sesolutions.ui.store.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatTextView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class BillingShippingFragment extends FormHelper implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    public boolean loadWhenVisible = true;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_offset, container, false);
        try {
            applyTheme(v);
            if (loadWhenVisible) {
                init();
                callSignUpApi(1);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
//        this.callSignUpApi(1);
    }


    private void init() {
        mRecyclerView = v.findViewById(R.id.recyclerview);
//        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
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

    private void callSignUpApi(final int req) {

        if (isNetworkAvailable(context)) {
//            showBaseLoader(false);
            try {
                if (req == 1) {
                    showBaseLoader(true);
                }

                HttpRequestVO request = new HttpRequestVO(url);
                if (null != map) {
                    request.params.putAll(map);
                }
                if (FORM_TYPE == Constant.FormType.EDIT_USER) {
                    request.params.put(Constant.KEY_GET_FORM, "fields");
                } else {
                    request.params.put(Constant.KEY_GET_FORM, 1);
                }
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
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    createFormUi(vo.getResult());
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                somethingWrongMsg(v);
                            }
                        } catch (Exception e) {
                            somethingWrongMsg(v);
                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                CustomLog.e(e);
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
            hideBaseLoader();
        }
    }

//    @Override
//    public void onRefresh() {
//        try {
//            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
//                swipeRefreshLayout.setRefreshing(true);
//            }
//            callSignUpApi(Constant.REQ_CODE_REFRESH);
//        } catch (Exception e) {
//            CustomLog.e(e);
//        }
//    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public static BillingShippingFragment newInstance(int editAlbum, Map<String, Object> map, String url, int albumId) {
        BillingShippingFragment fragment = new BillingShippingFragment();
        fragment.FORM_TYPE = editAlbum;
        fragment.url = url;
        fragment.map = map;
        if (albumId == -1) {
            fragment.loadWhenVisible = false;
        }
        return fragment;
    }


    public static BillingShippingFragment newInstance(int editAlbum, Map<String, Object> map, String url) {
        return newInstance(editAlbum, map, url, 0);
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
                    String url="";// = name.equals(Constant.KEY_CATEGORY_ID) ? Constant.URL_SUB_CATEGORY : Constant.URL_SUB_SUB_CATEGORY;

                    if (name.equals(Constant.KEY_COUNTRY_ID)) {
                        //  map.put(Constant.KEY_COUNTRY_ID, catId);

                        url = Constant.BASE_URL + "estore/index/getstate" + Constant.POST_URL;
                        if(map==null){
                            map=new HashMap<>();
                        }
                        map.put("country_id", statId);
                    } /*else {

                        map.put(Constant.KEY_SUB_CATEGORY_ID, statId);
                        url = Constant.URL_SUB_SUB_CATEGORY;
                    }*/
                    url=url.replaceAll(" ","%20");
                    Log.e("Tagvalue",""+baseFormElement.getTag());
                    callCategoryApi(map, url, baseFormElement.getTag(),1);
                }

               /* if(name.equals(Constant.KEY_STATE_ID)){
                  String  statId22 = Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());
                    Log.e("STTTID",""+statId22);
                    map.put("state_id", statId22);
                }*/

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
                    request.params.putAll(map);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = "POST";
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
                                        if (vo.getResult().isresults()) {
                                            updateFormItemOptions(tag, vo.getResult().getresults());
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
            mFormBuilder.getAdapter().notifyDataSetChanged();
        }
    }

}
