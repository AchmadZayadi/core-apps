package com.sesolutions.ui.business;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
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
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateEditBusinessFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;

   /* public static CreateEditBusinessFragment newInstance(int formType, String url, int categoryId) {
        CreateEditBusinessFragment frag = new CreateEditBusinessFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static CreateEditBusinessFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result) {
        CreateEditBusinessFragment fragment = new CreateEditBusinessFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.result = result;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);
            initScreenData();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        /*calling api only if no response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            //applyCustomChange();
            createFormUi(result);
            hideInitially();
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_BUSINESS ? R.string.title_create_business : R.string.title_edit_business));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("can_join")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());

                    if (null != key) {
                        boolean hideShow = key.equals("1");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            String name = tagList.get(i);
                            if (name.equals("member_title_plural") || name.equals("member_title_singular")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideShow);
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
            if (name.equals("member_title_singular") || name.equals("member_title_plural")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
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
                        if (map.containsKey(Constant.KEY_CATEGORY_ID)) {
                            map.remove(Constant.KEY_CATEGORY_ID);
                        }
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

                                    if (vo.isSuccess()) {
                                        result = vo.getResult();
                                        //applyCustomChange();
                                        createFormUi(result);
                                        hideInitially();
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                        goIfPermissionDenied(vo.getMessage());
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

    private void applyCustomChange() {
        for (Dummy.Formfields fld : result.getFormfields()) {
            if ("networks".equals(fld.getName())) {
                fld.setType(Constant.MULTI_CHECKBOX);
                break;
            }
        }
    }


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
}
