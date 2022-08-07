package com.sesolutions.ui.settings;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.responses.ValidateFieldError;
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
import me.riddhimanadib.formmaster.listener.OnTextClickListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementButton;
import me.riddhimanadib.formmaster.model.FormElementCheckbox;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextPassword;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;

public class GeneralSettingFragment extends BaseFragment implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;

    private View v;
    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;

    private List<String> tagList;
    private List<Dummy.Formfields> formList;
    private Map<String, Map<String, String>> commonMap;
    private AppCompatTextView tvTitle;
    private String url;
    private String title;
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
        try {
       applyTheme( v);

            init();
            callSignUpApi();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        //   bConitinue.setVisibility(View.GONE);
        //  cbTnC = v.findViewById(R.id.cbTnC);
        //  cbTnC.setOnCheckedChangeListener(this);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
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
                       // formItems.add(element);
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
                    elementSelect.setValue(vo.getMultiOptions().get(vo.getValue()));
                    elementSelect.setOptions(vo.getMultiOptionsList());
                    elementSelect.setTag(tag);
                    elementSelect.setTitle(vo.getLabel());
                    elementSelect.setRequired(vo.isRequired());
                    commonMap.put(vo.getName(), vo.getMultiOptions());
                    formItems.add(elementSelect);

                    break;
                case Constant.MULTI_CHECKBOX:

                    FormElementPickerMulti multiSelect = FormElementPickerMulti.createInstance();
                    List<String> selectedValues = new ArrayList<>();
                    Map<String, String> options = vo.getMultiOptions();

                    if (vo.instanceOfJsonArray()) {
                        List<String> selelctedKeys = new Gson().fromJson(vo.getValueString(), List.class);

                        for (String key : selelctedKeys) {
                            selectedValues.add(options.get(key));
                        }
                    }


                    multiSelect.setOptionsSelected(selectedValues);
                    multiSelect.setOptions(vo.getMultiOptionsList());
                    multiSelect.setTag(tag);
                    multiSelect.setTitle(vo.getLabel());
                    multiSelect.setRequired(vo.isRequired());
                    commonMap.put(vo.getName(), vo.getMultiOptions());
                    formItems.add(multiSelect);

                    break;

                case Constant.CHECKBOX:

                    FormElementCheckbox elementCheckbox = FormElementCheckbox.createInstance();
                    elementCheckbox.setTag(tag);
                    elementCheckbox.setValue(vo.getValue());
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
                    elementButton.setClickListener(new OnTextClickListener() {
                        @Override
                        public void onTextClicked(int tag) {
                            CustomLog.d("button", "formButton clicked");
                            closeKeyboard();
                            callSignUpApi(fetchFormValue());
                        }
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

    public Map<String, Object> fetchFormValue() {
        Map<String, Object> request = new HashMap<>();
        String name;
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            name = tagList.get(i);
            //CustomLog.d("tag1", "" + tag);
            BaseFormElement targetElement = mFormBuilder.getFormElement(tag);
            if (targetElement != null) {
                if (targetElement instanceof FormElementPickerMulti) {
                    List<String> selectedValues = ((FormElementPickerMulti) targetElement).getOptionsSelected();
                    List<String> allOptions = ((FormElementPickerMulti) targetElement).getOptions();
                    String target = "";
                    for (String targetValue : allOptions) {
                        target = Util.getKeyFromValue2(commonMap.get(name), targetValue);
                        request.put(name + "[" + target + "]", selectedValues.contains(targetValue) ? 1 : 0);
                    }

                }
                else {
                    String targetValue = targetElement.getValue();
                    CustomLog.d(name, "" + targetValue);
                    if (null != targetValue) {

                        //  targetValue = (String) Util.getKeyFromValue(commonMap, targetValue);
                        if (commonMap.containsKey(name)) {

                            if(name.equalsIgnoreCase("timezone")){
                                targetValue = Util.getKeyFromValue21(commonMap.get(name), targetValue);
                            }else {
                                targetValue = Util.getKeyFromValue(commonMap.get(name), targetValue);
                            }

                        }
                        request.put(name, targetValue);

                    } else {
                        request.put(name, Constant.EMPTY);
                    }
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
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.putAll(params);
                    // request.params.put(Constant.KEY_GET_FORM, Constant.VALUE_GET_FORM);
                    // request.params.put(Constant.KEY_VALIDATE_FIELD_FORM, 1);
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
                                    JSONObject json = new JSONObject(response);
                                    if (json.get(Constant.KEY_RESULT) instanceof String) {
                                        String result = json.getString(Constant.KEY_RESULT);
                                        Util.showSnackbar(v, result);
                                    } else {

                                       /* svn co http://34.193.87.123/repos/sesandroid
                                        prinkal  / prinkal123@*/
                                        FormError res = new Gson().fromJson(response, FormError.class);
                                        List<ValidateFieldError> errorList = res.getResult().getValdatefieldserror();
                                        CustomLog.e("from_vo", "" + new Gson().toJson(errorList));
                                        Util.showSnackbar(v, res.getResult().fetchFirstNErrors());
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

    public static Fragment newInstance(String url, String title) {
        GeneralSettingFragment fragment = new GeneralSettingFragment();
        fragment.url = url;
        fragment.title = title;
        return fragment;
    }
}
