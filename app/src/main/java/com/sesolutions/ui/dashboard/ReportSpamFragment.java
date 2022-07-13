package com.sesolutions.ui.dashboard;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.welcome.FormError;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementButton;
import me.riddhimanadib.formmaster.model.FormElementCheckbox;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextPassword;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;

public class ReportSpamFragment extends BaseFragment implements View.OnClickListener {//}, ParserCallbackInterface {

    // --Commented out by Inspection (23-08-2018 20:55):private static final int CODE_LOGIN = 100;

    private View v;
    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;

    private List<String> tagList;
    private List<Dummy.Formfields> formList;
    private Map<String, Map<String, String>> commonMap;
    private String guid;
    private Boolean showbar = false;
    private AppCompatTextView tvTitle;
    // --Commented out by Inspection (23-08-2018 20:55):private Map<String, Object> map;
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
        try {
            applyTheme(v);
            init();
            callSignUpApi();
            //   printKeyStore();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        if (!showbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvTitle.setText(Constant.TITLE_REPORT_SPAM);
        }

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
                //  bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_REPORT);


                    request.params.put(Constant.KEY_SUBJECT, guid);

                    request.params.put(Constant.KEY_GET_FORM, Constant.VALUE_GET_FORM_2);
                    request.params.put(Constant.KEY_AUTH_TOKEN, Constant.EMPTY);

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                // BaseResponse<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                                Dummy vo = new Gson().fromJson(response, Dummy.class);
                                //  Dummy.Result result = vo.getResult();
                                createFormUi(vo.getResult());
                            } else {
                                notInternetMsg(v);
                                //   bSignIn.setText(Constant.TXT_SIGN_IN);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
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

    private void createFormUi(Dummy.Result result) {
        mFormBuilder = new FormBuilder(context, mRecyclerView);
        tagList = new ArrayList<>();
        commonMap = new HashMap<>();

        formList = result.getFormfields();
        // declare form elements
        // FormHeader header = FormHeader.createInstance().setTitle("Personal Info");

        // add_create them in a list
        List<BaseFormElement> formItems = new ArrayList<>();
        //   formItems.add_create(header);

        for (Dummy.Formfields vo : formList) {

            // FormElement element = FormElement.createInstance();
            tagList.add(vo.getName());
            int tag = 1010 + tagList.size();

            CustomLog.d("tag", "" + tag);


            switch (vo.getType()) {
                case Constant.TEXT:
                    //   FormElement element = FormElement.createInstance();
                    if (vo.getName().contains("email")) {
                        FormElementTextEmail element = FormElementTextEmail.createInstance();
                        String value = vo.getValue();
                        element.setValue(value);
                        element.setTag(tag);
                        element.setTitle(vo.getLabel());
                        element.setRequired(vo.isRequired());
                        formItems.add(element);
                    } else {
                        FormElementTextSingleLine element = FormElementTextSingleLine.createInstance();
                        String value = vo.getValue();
                        element.setValue(value);
                        element.setTag(tag);
                        element.setTitle(vo.getLabel());
                        element.setRequired(vo.isRequired());
                        formItems.add(element);
                    }
                    break;
                case Constant.TEXTAREA:
                case Constant.TINY_MCE:
                    FormElementTextMultiLine element = FormElementTextMultiLine.createInstance();
                    String value = vo.getValue();
                    element.setValue(value);
                    element.setTag(tag);
                    element.setTitle(vo.getLabel());
                    element.setRequired(vo.isRequired());
                    formItems.add(element);
                    break;

                case Constant.PASSWORD:
                    FormElementTextPassword elementPassword = FormElementTextPassword.createInstance();
                    value = vo.getValue();
                    elementPassword.setValue(Constant.EMPTY);
                    elementPassword.setTag(tag);
                    elementPassword.setTitle(vo.getLabel());
                    elementPassword.setRequired(vo.isRequired());
                    formItems.add(elementPassword);

                    break;

                case Constant.DATE:
                    FormElementPickerDate elementDate = FormElementPickerDate.createInstance();
                    value = vo.getValue();
                    elementDate.setValue(value);
                    elementDate.setTag(tag);
                    elementDate.setTitle(vo.getLabel());
                    elementDate.setRequired(vo.isRequired());
                    formItems.add(elementDate);

                    break;

                case Constant.SELECT:
                case Constant.RADIO:
                    FormElementPickerSingle elementSelect = FormElementPickerSingle.createInstance();
                    value = vo.getValue();

                    elementSelect.setValue(vo.getMultiOptions().get(value));
                    elementSelect.setOptions(vo.getMultiOptionsList());
                    elementSelect.setTag(tag);
                    elementSelect.setTitle(vo.getLabel());
                    elementSelect.setRequired(vo.isRequired());
                    commonMap.put(vo.getName(), vo.getMultiOptions());
                    formItems.add(elementSelect);

                    break;

                case Constant.CHECKBOX:

                    FormElementCheckbox elementCheckbox = FormElementCheckbox.createInstance();
                    elementCheckbox.setTag(tag);
                    elementCheckbox.setTitle(vo.getLabel());
                    elementCheckbox.setRequired(vo.isRequired());
                  /* elementCheckbox.setPositiveText("java java java");
                    elementCheckbox.setClickListener(new OnTextClickListener() {
                        @Override
                        public void onTextClicked(int tag) {
                            CustomLog.d("java", "java");
                        }
                    });*/
                    formItems.add(elementCheckbox);

                    break;

                case Constant.BUTTON:

                    FormElementButton elementButton = FormElementButton.createInstance();
                    elementButton.setTag(tag);
                    elementButton.setTitle(vo.getLabel());
                    //elementButton.setRequired(vo.isRequired());
                    elementButton.setClickListener(tag1 -> {
                        closeKeyboard();
                        callSignUpApi(fetchFormValue());
                    });
                    formItems.add(elementButton);
                    break;


            }

            if (!TextUtils.isEmpty(vo.getDescription())) {
                formItems.add(FormHeader.createInstance(vo.getDescription()));
            }
// build and display the form

        }
        mFormBuilder.addFormElements(formItems);
        //   mFormBuilder.();

    }

    private Map<String, Object> fetchFormValue() {
        Map<String, Object> request = new HashMap<>();

        // TODO: 18/11/17 REMOVE THIS subject AND ADD A HIDDEN FROMELEMNT IN LIBRARY
        request.put("subject", "");

        String name;
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            name = tagList.get(i);
            //CustomLog.d("tag1", "" + tag);
            BaseFormElement targetElement = mFormBuilder.getFormElement(tag);
            if (targetElement != null) {
                String targetValue = targetElement.getValue();
                CustomLog.d(name, "" + targetValue);
                if (null != targetValue) {

                    //  targetValue = (String) Util.getKeyFromValue(commonMap, targetValue);
                    if (commonMap.containsKey(name)) {
                        targetValue = Util.getKeyFromValue(commonMap.get(name), targetValue);
                    }
                    request.put(name, targetValue);

                } else {
                    request.put(name, Constant.EMPTY);
                }
            }

        }

        return request;
    }

    private void callSignUpApi(Map<String, Object> params) {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                //  bSignIn.setText(Constant.TXT_SIGNING_IN);

                try {
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_REPORT);
                    request.params.putAll(params);
                    // request.params.put(Constant.KEY_GET_FORM, Constant.VALUE_GET_FORM);
                    request.params.put(Constant.KEY_SUBJECT, guid);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    // Saving boolean - true/false
                    // editor.commit();

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                // BaseResponse<FormVo> vo = new Gson().fromJson(response, BaseResponse.class);
                                JSONObject json = new JSONObject(response);
                                if (json.get(Constant.KEY_RESULT) instanceof String) {
                                    String result = json.getString(Constant.KEY_RESULT);
                                    Util.showSnackbar(v, "Reported Successfully.");
                                    onBackPressed();
                                    //  callSignUpApi(formType);
                                } else {

                                    FormError resp = new Gson().fromJson(response, FormError.class);
                                    if (TextUtils.isEmpty(resp.getError())) {
                                        Util.showSnackbar(v, resp.getResult().fetchFirstNErrors());
                                    } else {
                                        Util.showSnackbar(v, resp.getMessage());

                                    }


                                }
                            } else {
                                notInternetMsg(v);
                                //   bSignIn.setText(Constant.TXT_SIGN_IN);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
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


    public static Fragment newInstance(String guid) {
        ReportSpamFragment fragment = new ReportSpamFragment();
        fragment.guid = guid;
        return fragment;
    }

    public static Fragment newInstance(String guid, Boolean showBar) {
        ReportSpamFragment fragment = new ReportSpamFragment();
        fragment.guid = guid;
        fragment.showbar = showBar;
        return fragment;
    }

// --Commented out by Inspection START (23-08-2018 20:55):
//    public static Fragment newInstance(Map<String, Object> map) {
//        ReportSpamFragment fragment = new ReportSpamFragment();
//        fragment.map = map;
//        return fragment;
//    }
// --Commented out by Inspection STOP (23-08-2018 20:55)
}
