package com.sesolutions.ui.signup;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.GetGcmId;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementButton;
import me.riddhimanadib.formmaster.model.FormElementCheckbox;
import me.riddhimanadib.formmaster.model.FormElementLocationSuggest;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextPassword;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;
    private String CURRENT_STEP = Constant.KEY_VALIDATE_ACCOUNT_FORM;
    private String FORM_TYPE = Constant.KEY_VALIDATE_ACCOUNT_FORM;
    private View v;
    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;
    private static final int REQ_EDITOR = 6989;
    private List<String> tagList;
    private List<Dummy.Formfields> formList;
    private Map<String, Map<String, String>> commonMap;
    private Map<String, Object> mapHiddenFields;
    String SubScritionId="";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);
            init();
            callSignUpApi(FORM_TYPE);
            //   printKeyStore();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        //v.findViewById(R.id.rlMain).setBackgroundColor(SesColorUtils.getAppBgColor(context));
        ((AppCompatTextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.txt_sign_up));
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

    private void callSignUpApi(String formType) {
        try {
            if (isNetworkAvailable(context)) {
                switch (formType) {
                    case Constant.VALUE_GET_FORM_1:
                        CURRENT_STEP = Constant.KEY_VALIDATE_ACCOUNT_FORM;
                        break;
                    case Constant.VALUE_GET_FORM_2:
                        CURRENT_STEP = Constant.KEY_VALIDATE_FIELD_FORM;
                        break;
                    case Constant.VALUE_GET_FORM_INTEREST:
                        CURRENT_STEP = Constant.KEY_VALIDATE_INTEREST_FORM;
                        break;
                    case Constant.VALUE_GET_FORM_3:
                        CURRENT_STEP = Constant.KEY_VALIDATE_PHOTO_FORM;
                        break;
                    case Constant.VALUE_GET_FORM_4:
                        CURRENT_STEP = Constant.KEY_VALIDATE_PHONE_FORM;
                        break;
                    case Constant.VALUE_GET_FORM_OTP:
                        CURRENT_STEP = Constant.KEY_VALIDATE_OTP_FORM;
                        break;
                }
                showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SIGNUP);
                    request.params.put(Constant.KEY_GET_FORM, formType);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                Dummy vo = new Gson().fromJson(response, Dummy.class);
                                Dummy.Result result = vo.getResult();
                                createFormUi(vo.getResult());

                                Constant.SESSION_ID = "PHPSESSID=" + vo.getSessionId() + ";";
                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_COOKIE, "PHPSESSID=" + vo.getSessionId() + ";");
                            } else {
                                notInternetMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQ_EDITOR:
                    if (resultCode == -1) {
                        if (data != null) {
                            CustomLog.e("desc", "not null");
                            String desc = data.getStringExtra(Constant.TEXT);
                            int tag = data.getIntExtra(Constant.TAG, -1);
                            CustomLog.e("desc", desc);
                            mFormBuilder.getAdapter().setValueAtTag(tag, desc);
                        } else {
                            CustomLog.e("desc", "null");
                        }

                    }
                    break;

                case LOCATION_AUTOCOMPLETE_REQUEST_CODE:
                    switch (resultCode) {
                        case RESULT_OK:
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            CharSequence address = place.getAddress();
                            Double lat = place.getLatLng().latitude;
                            Double lang = place.getLatLng().longitude;
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(lat, lang, 1);
                            CustomLog.e("lat:", "" + lat);
                            CustomLog.e("lang", "" + lang);
                            CustomLog.e("country", "" + addresses.get(0).getCountryName());
                            CustomLog.e("city", "" + addresses.get(0).getLocality());
                            CustomLog.e("ZIP", "" + addresses.get(0).getPostalCode());
                            CustomLog.e("state", "" + addresses.get(0).getAdminArea());
                            CustomLog.e("subLoc", "" + addresses.get(0).getSubLocality());
                            CustomLog.e("locale", "" + addresses.get(0).getLocale());

                            mapHiddenFields.put("ses_city", addresses.get(0).getLocality());
                            mapHiddenFields.put("ses_zip", addresses.get(0).getPostalCode());
                            mapHiddenFields.put("ses_country", addresses.get(0).getCountryName());
                            mapHiddenFields.put("ses_state", addresses.get(0).getAdminArea());
                            mapHiddenFields.put("ses_lat", lat);
                            mapHiddenFields.put("ses_lng", lang);


                            if (null != address) {
                                mFormBuilder.getAdapter().setValueAtTag(locationTag, address.toString());
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private final int LOCATION_AUTOCOMPLETE_REQUEST_CODE = 8976;
    private int locationTag;

    private void openPlaceAutoComplete(int tag) {
        locationTag = tag;
        // Retrieve the PlaceAutocompleteFragment.
        //PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        //autocompleteFragment.setOnPlaceSelectedListener(this);
        //PlaceAutocompleteFragment.setOnPlaceSelectedListener(this);
        if (!Places.isInitialized()) {
            Places.initialize(context, getString(R.string.places_api_key));
        }
        try {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

// Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(activity);
            startActivityForResult(intent, LOCATION_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            CustomLog.e(e);

        }
    }

    private void createFormUi(Dummy.Result result) {
        mFormBuilder = new FormBuilder(context, mRecyclerView);
        tagList = new ArrayList<>();
        commonMap = new HashMap<>();
        mapHiddenFields = new HashMap<>();

        formList = result.getFormfields();

        List<BaseFormElement> formItems = new ArrayList<>();

        for (Dummy.Formfields vo : formList) {

            tagList.add(vo.getName());
            int tag = 1010 + tagList.size();

            switch (vo.getType()) {
                case Constant.TEXT:
                    if (vo.getName().contains("email")) {
                        FormElementTextEmail element = FormElementTextEmail.createInstance();
                        String value = vo.getValue();
                        element.setValue(value);
                        element.setHint(vo.getDescription());

                        element.setTag(tag);
                        element.setTitle(vo.getLabel());
                        element.setRequired(vo.isRequired());
                        formItems.add(element);
                    } else if (vo.getName().contains("location")) {
                        formItems.add(FormElementLocationSuggest.createInstance(vo.getValue())
                                .setTag(tag)
                                .setName(vo.getName())
                                .setHint(getStrings(R.string.enter_location))
                                .setTitle(vo.getLabel())
                                .setClickListener(this::openPlaceAutoComplete)
                                .setRequired(vo.isRequired()));
                    } else {
                        FormElementTextSingleLine element = FormElementTextSingleLine.createInstance();
                        String value = vo.getValue();
                        element.setValue(value);
                        element.setTag(tag);
                        element.setHint(vo.getDescription());
                        element.setTitle(vo.getLabel());
                        element.setRequired(vo.isRequired());
                        formItems.add(element);
                    }
                    break;
                case Constant.TEXTAREA:
                    FormElementTextMultiLine element = FormElementTextMultiLine.createInstance();
                    String value = vo.getValue();
                    element.setValue(value);
                    element.setTag(tag);
                    element.setHint(vo.getDescription());
                    element.setTitle(vo.getLabel());
                    element.setRequired(vo.isRequired());
                    formItems.add(element);
                    break;
                case Constant.HIDDEN:
                    mapHiddenFields.put(vo.getName(), vo.getValue());
                    break;

                case Constant.PASSWORD:
                    FormElementTextPassword elementPassword = FormElementTextPassword.createInstance();
                    value = vo.getValue();
                    elementPassword.setValue(Constant.EMPTY);
                    elementPassword.setTag(tag);
                    elementPassword.setHint(vo.getDescription());
                    elementPassword.setTitle(vo.getLabel());
                    elementPassword.setRequired(vo.isRequired());
                    formItems.add(elementPassword);
                    break;

                case Constant.DATE:
                    FormElementPickerDate elementDate = FormElementPickerDate.createInstance();
                    value = vo.getValue();
                    elementDate.setValue(value);
                    elementDate.setTag(tag);
                    elementDate.setHint(getStrings(R.string.select_date));
                    elementDate.setTitle(vo.getLabel());
                    elementDate.setRequired(vo.isRequired());
                    formItems.add(elementDate);
                    break;

                case Constant.SELECT:
                    FormElementPickerSingle elementSelect = FormElementPickerSingle.createInstance();
                    value = vo.getValue();
                    elementSelect.setValue(vo.getMultiOptions().get(value));
                    elementSelect.setOptions(vo.getMultiOptionsList());
                    elementSelect.setTag(tag);
                    elementSelect.setPickerTitle((vo.getName().contains("subcat") || vo.getName().contains("category"))
                            ? Constant.TITLE_CHOOSE_CATEGORY
                            : getStrings(R.string.select_) + vo.getLabel());
                    elementSelect.setTitle(vo.getLabel());
                    elementSelect.setHint(TextUtils.isEmpty(vo.getDescription()) ? elementSelect.getPickerTitle() : vo.getDescription());
                    elementSelect.setRequired(vo.isRequired());
                    commonMap.put(vo.getName(), vo.getMultiOptions());
                    formItems.add(elementSelect);
                    break;

                case Constant.CHECKBOX:
                    FormElementCheckbox elementCheckbox = FormElementCheckbox.createInstance();
                    elementCheckbox.setTag(tag);
                    elementCheckbox.setTitle(vo.getLabel());
                    elementCheckbox.setRequired(vo.isRequired());
                    elementCheckbox.setPositiveText(getStrings(R.string.PLEASE_READ_TERMS));
                    elementCheckbox.setClickListener(tag1 -> openTermsPrivacyFragment(Constant.URL_TERMS_2));
                    formItems.add(elementCheckbox);
                    break;
                case Constant.MULTI_CHECKBOX:

                    FormElementPickerMulti multiSelect = FormElementPickerMulti.createInstance();
                    List<String> selectedValues = new ArrayList<>();
                    Map<String, String> options = vo.getMultiOptions();

                    try {
                        if (vo.instanceOfJsonArray()) {
                            List<String> selelctedKeys = new ArrayList<>();
                            JSONArray arr = new JSONArray(vo.getValueString());
                            if (arr.length() > 0) {
                                for (int x = 0; x < arr.length(); x++) {
                                    selelctedKeys.add(arr.getString(x));
                                }
                            }
                            // List<String> selelctedKeys = new Gson().fromJson(vo.getValueString(), List.class);

                            for (String key : selelctedKeys) {
                                selectedValues.add(options.get(key));
                            }
                        }
                    } catch (JSONException e) {
                        CustomLog.e(e);
                    }

                    multiSelect.setOptionsSelected(selectedValues);
                    multiSelect.setOptions(vo.getMultiOptionsList());
                    multiSelect.setTag(tag);
                 //   multiSelect.setKeyOptions(vo.getMultiOptions());

                    multiSelect.setHint(getStrings(R.string.select_) + vo.getLabel());
                    multiSelect.setName(vo.getName());
                    multiSelect.setTitle(vo.getLabel());
                    multiSelect.setRequired(vo.isRequired());
                    commonMap.put(vo.getName(), vo.getMultiOptions());
                    formItems.add(multiSelect);

                    break;

                case Constant.BUTTON:
                    FormElementButton elementButton = FormElementButton.createInstance();
                    elementButton.setTag(tag);
                    elementButton.setTitle(vo.getLabel());
                    elementButton.setClickListener(tag12 -> {
                        callSignUpApi(fetchFormValue());
                    });
                    formItems.add(elementButton);
                    break;


            }
        }
        mFormBuilder.addFormElements(formItems);
    }

    public Map<String, Object> fetchFormValue() {

        Map<String, Object> request = new HashMap<>(mapHiddenFields);
        String name;
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            name = tagList.get(i);
            BaseFormElement targetElement = mFormBuilder.getFormElement(tag);
            if (targetElement != null) {
                String targetValue = targetElement.getValue();
                CustomLog.d(name, "" + targetValue);
                if (null != targetValue) {
                    if (commonMap.containsKey(name)) {
                        CustomLog.d(name, "" + targetValue);
                        targetValue = Util.getKeyFromValue(commonMap.get(name), targetValue);
                    }
                    CustomLog.d(name, "" + targetValue);
                    request.put(name, targetValue);
                } else {
                    request.put(name, Constant.EMPTY);
                }
            }
        }
        return request;
    }

    private void callSignUpApi(Map<String, Object> params) {
        closeKeyboard();
        if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
            new GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
            return;
        }
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);

                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SIGNUP);
                    request.params.putAll(params);
                    request.params.put(CURRENT_STEP, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put(Constant.KEY_DEVICE_UID, Constant.GCM_DEVICE_ID);
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
                                        try {
                                            SubScritionId = json.getString("user_subscription_id");
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                            SubScritionId="";
                                        }
                                       goToScreenAsPerResult(result);
                                    } else {
                                        try {
                                            SignInResponse res = new Gson().fromJson(response, SignInResponse.class);
                                            if (res.isSuccess()) {

                                                SPref.getInstance().saveUserInfo(context, Constant.KEY_USERINFO_JSON,res);
                                                UserMaster userVo = res.getResult();
                                                String errors = userVo.fetchFirstNErrors();
                                                if (TextUtils.isEmpty(errors) && userVo.getUserId() > 0) {
                                                    userVo.setAuthToken(res.getAouthToken());
                                                    userVo.setLoggedinUserId(userVo.getUserId());
                                                    SPref.getInstance().saveUserMaster(context, userVo, res.getSessionId());
                                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, res.getAouthToken());
                                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());
                                                    goToDashboard();

                                                } else {
                                                    Util.showSnackbar(v, errors);
                                                }
                                            } else {
                                                Util.showSnackbar(v, res.getErrorMessage());
                                            }
                                        } catch (Exception e) {
                                            CustomLog.e(e);
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

    private void goToScreenAsPerResult(String result) {
        switch (result) {
            case Constant.RESULT_FORM_INTEREST2:
                goToSignUpFragment(Constant.VALUE_GET_FORM_INTEREST);
                break;
            case Constant.RESULT_FORM_OTP_SIGNUP:
                goToSignUpFragment(Constant.VALUE_PHONE_FORM_INTEREST);
                break;

            case Constant.RESULT_FORM_1:

                goToSignUpFragment(Constant.VALUE_GET_FORM_2);
                break;
            case Constant.RESULT_FORM_OTP:
            case Constant.RESULT_FORM_OTP_LOGIN:
                openOtpFragment(OTPFragment.FROM_SIGNUP, "", null);
                break;
            case Constant.RESULT_FORM_2:
//                goToSignUpFragment(Constant.VALUE_GET_FORM_2);
                goToProfileImageFragment();
                break;
            case Constant.RESULT_FORM_INTEREST:
                goToProfileImageFragment();
                break;
            case Constant.RESULT_FORM_3:
                openWebView(Constant.URL_SUBSCRIPTION+"&user_subscription_id="+SubScritionId, Constant.TITLE_SUBSCRIPTION);
                break;
            case Constant.RESULT_FORM_4:
                hideBaseLoader();
                fragmentManager.beginTransaction().replace(R.id.container, new JoinFragment()).commit();
                break;
            default:
                hideBaseLoader();
                fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                        .commit();
                break;
        }
    }

    public static Fragment newInstance(String FROM_TYPE) {
        SignUpFragment fragment = new SignUpFragment();
        fragment.FORM_TYPE = FROM_TYPE;
        return fragment;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        callSignUpApi(fetchFormValue());
        return false;
    }
}
