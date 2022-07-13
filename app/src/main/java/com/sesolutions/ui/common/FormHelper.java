package com.sesolutions.ui.common;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.slidedatetimepicker.SlideDateTimeListener;
import com.sesolutions.slidedatetimepicker.SlideDateTimePicker;
import com.sesolutions.ui.blogs.ViewBlogFragment;
import com.sesolutions.ui.clickclick.ActivityClickClick;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.ui.comment.CommentFragment;
import com.sesolutions.ui.dashboard.ApiHelper;
import com.sesolutions.ui.editor.EditorExampleActivity;
import com.sesolutions.ui.poll_core.CCreatePollsFragment;
import com.sesolutions.ui.signup.OTPFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.welcome.FormError;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.listener.OnFormElementValueChangedListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementButton;
import me.riddhimanadib.formmaster.model.FormElementCheckbox;
import me.riddhimanadib.formmaster.model.FormElementFile;
import me.riddhimanadib.formmaster.model.FormElementGroupQuestion;
import me.riddhimanadib.formmaster.model.FormElementImage;
import me.riddhimanadib.formmaster.model.FormElementLocationSuggest;
import me.riddhimanadib.formmaster.model.FormElementMusicFile;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementPickerTime;
import me.riddhimanadib.formmaster.model.FormElementRating;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextPassword;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormElementTextView;
import me.riddhimanadib.formmaster.model.FormElementTitle;
import me.riddhimanadib.formmaster.model.FormElementUrl;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 15/12/17.
 */

public class FormHelper extends ApiHelper implements OnFormElementValueChangedListener/*, MyMultiPartEntity.ProgressListener*/ {


    public int FORM_TYPE;
    public static final int REQ_EDITOR=190;
    public Map<String, Object> map;
    public int RESUMEID=0;
    public View v;
    public OnUserClickedListener<Integer, Object> listener;
    public int[] arrTimeTag = {-1, -1};
    public String url;
    public RecyclerView mRecyclerView;
    public static FormBuilder mFormBuilder;
    public static Map<String, Object> mapHiddenFields;
    public List<String> tagList;
    public List<Dummy.Formfields> formList;
    public Map<String, Map<String, String>> commonMap;
    public List<BaseFormElement> formItems;
    public int clickedFilePostion;
    // public boolean isMusicSelected = false;
    public static int locationTag;
    public static int isCurrentllywork = 0;
    int resume_id = 0;


    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        Log.e("reqcode",""+reqCode);
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public void createFormUi(Dummy.Result result) {
        try {
            mFormBuilder = new FormBuilder(context, mRecyclerView, this);
            tagList = new ArrayList<>();
            commonMap = new HashMap<>();
            isCurrentllywork=0;
            mapHiddenFields = new HashMap<>();
            formList = result.getFormfields();
            // add_create them in a list
            formItems = new ArrayList<>();
            //int i = 0;
            for (Dummy.Formfields vo : formList) {
                tagList.add(vo.getName());
                int tag = 1010 + tagList.size();
                MAX_COUNT =1;

                switch (vo.getType()) {
                    case Constant.TEXT:
                        //   FormElement element = FormElement.createInstance();
                        if (vo.getName().contains("email")) {
                            FormElementTextEmail element1 = FormElementTextEmail.createInstance();
                            String value = vo.getValue();
                            element1.setValue(value)
                                    .setTag(tag)
                                    .setName(vo.getName())
                                    .setHint(vo.getDescription())
                                    .setTitle(vo.getLabel())
                                    .setRequired(vo.isRequired());
                            formItems.add(element1);
                        } else if (vo.getName().contains("location") || vo.getLabel().contains("location")) {
                            formItems.add(FormElementLocationSuggest.createInstance(vo.getValue())
                                    .setTag(tag)
                                    .setName(vo.getName())
                                    .setHint(getStrings(R.string.enter_location))
                                    .setTitle(vo.getLabel())
                                    .setClickListener(this::openPlaceAutoComplete)
                                    .setRequired(vo.isRequired()));
                        } else {
                             formItems.add(FormElementTextSingleLine.createInstance(vo.getValue())//.setValue(vo.getValue())
                                    .setTag(tag)
                                    .setName(vo.getName())
                                    .setHint(vo.getDescription())
                                    .setTitle(vo.getLabel())
                                    .setRequired(vo.isRequired()));
                        }
                        break;
                    case Constant.TITLE:
                        FormElementTitle element41 = new FormElementTitle();
                        element41.setValue(SpanUtil.getHtmlString(vo.getValue()));
                        element41.setType(BaseFormElement.TYPE_TITLE);
                        element41.setTag(tag);
                        element41.setName(vo.getName());
                        element41.setBoldText(vo.isTitleBold());
                        element41.setTitle(SpanUtil.getHtmlString(vo.getLabel()));
                        element41.setRequired(vo.isRequired());
                        formItems.add(element41);
                        break;
                    case Constant.IMAGE_VIEW:
                        MAX_COUNT = 1;
                        FormElementImage elementFileImage = FormElementImage.createInstance();
                        String value22  = vo.getValue();
                        elementFileImage.setName(vo.getName());
                        elementFileImage.setValue(value22);
                        elementFileImage.setTag(tag);
                        elementFileImage.setTitle(vo.getLabel());
                        elementFileImage.setRequired(vo.isRequired());
                        elementFileImage.setHint(vo.getLabel());

                      /*  elementFileImage.setClickListener(tag1 -> {
                            checkChooserOption(vo.getName());
                            clickedFilePostion = tag1;

                                if (isVideoSelected) {
//                                showImageDialog(getStrings(R.string.MSG_SELECT_VIDEO_SOURCE));
                                    openVideoPicker(true);
                                } else {
//                                showImageDialog(getStrings(R.string.MSG_SELECT_IMAGE_SOURCE));
                                    openImagePicker();
                                }

                        });*/
                        formItems.add(elementFileImage);
                        break;
                    case Constant.TINY_MCE:
                        FormElementTextView element4 = new FormElementTextView();
                        element4.setValue(vo.getValue());
                        element4.setType(BaseFormElement.TYPE_EDITOR);
                        element4.setTag(tag);
                        element4.setName(vo.getName());
                        element4.setTitle(vo.getLabel());
                        element4.setRequired(vo.isRequired());
                        element4.setClickListener(this::startEditorActivity);
                        formItems.add(element4);
                        break;
                    case Constant.TEXT_FIXED:
                        FormElementTextView elementFixed = new FormElementTextView();
                        elementFixed.setValue(SpanUtil.getHtmlString(vo.getValue()));
                        elementFixed.setType(BaseFormElement.TYPE_UNEDITABLE_TEXT);
                        elementFixed.setTag(tag);
                        elementFixed.setName(vo.getName());
                        elementFixed.setTitle(vo.getLabel());
                        elementFixed.setRequired(vo.isRequired());
                        //elementFixed.setClickListener(this::startEditorActivity);
                        formItems.add(elementFixed);
                        break;

                    case Constant.TEXT_FIXED_CENTER:
                        elementFixed = new FormElementTextView();
                        elementFixed.setValue(vo.getValue());
                        elementFixed.setType(BaseFormElement.TYPE_UNEDITABLE_TEXT_CENTER);
                        elementFixed.setTag(tag);
                        elementFixed.setName(vo.getName());
                        elementFixed.setTitle(vo.getLabel());
                        elementFixed.setRequired(vo.isRequired());
                        //elementFixed.setClickListener(this::startEditorActivity);
                        formItems.add(elementFixed);
                        break;

                    case Constant.NUMBER:
                        FormElementTextNumber eleNumber = FormElementTextNumber.createInstance();
                        eleNumber.setValue(vo.getValue());
                        eleNumber.setTag(tag);
                        eleNumber.setName(vo.getName());
                        eleNumber.setTitle(vo.getLabel());
                        eleNumber.setRequired(vo.isRequired());
                        //elementFixed.setClickListener(this::startEditorActivity);
                        formItems.add(eleNumber);
                        break;

                    case Constant.TEXTAREA:
                        FormElementTextMultiLine element3 = FormElementTextMultiLine.createInstance();
                        String value = vo.getValue();
                        element3.setValue(value);
                        element3.setTag(tag);
                        element3.setName(vo.getName());
                        element3.setHint(vo.getDescription());
                        element3.setTitle(vo.getLabel());
                        element3.setRequired(vo.isRequired());
                        formItems.add(element3);
                        break;

                   /* case Constant.HIDDEN:
                        FormElementTextSingleLine element = FormElementTextSingleLine.createInstance();
                        element.setValue(vo.getValue());
                        element.setTag(tag);
                        element.setTitle(vo.getLabel());
                        element.setRequired(vo.isRequired());
                        element.setHidden(true);
                        formItems.add(element);
                        break;*/
                    case Constant.HIDDEN:
                        mapHiddenFields.put(vo.getName(), vo.getValue());
                        break;
                    case Constant.PASSWORD:
                        FormElementTextPassword elementPassword = FormElementTextPassword.createInstance();
                        elementPassword.setValue(Constant.EMPTY);
                        elementPassword.setTag(tag);
                        elementPassword.setName(vo.getName());
                        elementPassword.setHint(vo.getDescription());
                        elementPassword.setTitle(vo.getLabel());
                        elementPassword.setRequired(vo.isRequired());
                        formItems.add(elementPassword);

                        break;

                    case Constant.DATE_ONLY:
                        FormElementPickerDate elementDate12 = FormElementPickerDate.createInstance();
                        value = vo.getValue();
                        elementDate12.setValue(value);
                        elementDate12.setTag(tag);
                        elementDate12.setHint(getStrings(R.string.select_date));
                        elementDate12.setTitle(vo.getLabel());
                        elementDate12.setRequired(vo.isRequired());
                        formItems.add(elementDate12);
                        break;
                    case Constant.DATE:
                        if (null != vo.getName() && vo.getName().contains("time")) {
                            FormElementPickerTime elementTime = FormElementPickerTime.createInstance();
                            value = vo.getValue();
                            elementTime.setValue(value);
                            elementTime.setTag(tag);
                            elementTime.setHint(getStrings(R.string.select_time));
                            elementTime.setTitle(vo.getLabel());
                            elementTime.setRequired(vo.isRequired());
                            formItems.add(elementTime);

                        } else {
                            FormElementPickerDate elementDate = FormElementPickerDate.createInstance();
                            value = vo.getValue();
                            elementDate.setValue(value);
                            elementDate.setTag(tag);
                            elementDate.setHint(getStrings(R.string.select_date));
                            elementDate.setTitle(vo.getLabel());
                            elementDate.setRequired(vo.isRequired());
                            formItems.add(elementDate);
                        }

                        break;
                    case Constant.TIME:
                        FormElementPickerDate elementTime = FormElementPickerDate.createInstance();
                        value = vo.getValue();
                        elementTime.setValue(value);
                        elementTime.setTag(tag);
                        elementTime.setHint(getStrings(R.string.select_time));
                        elementTime.setTitle(vo.getLabel());
                        elementTime.setRequired(vo.isRequired());
                        formItems.add(elementTime);
                        break;

                    case Constant.DATE_CALENDAR:
                        FormElementTextView elementDateTime = new FormElementTextView();
                        value = vo.getValue();
                        elementDateTime.setName(vo.getName());
                        elementDateTime.setValue(value);
                        elementDateTime.setTag(tag);
                        elementDateTime.setHint(getStrings(R.string.select_date));
                        elementDateTime.setName(vo.getName());
                        if (vo.getName().equals("start_time") || vo.getName().equals("starttime")) {
                            arrTimeTag[0] = tag;
                        } else {
                            arrTimeTag[1] = tag;
                        }
                        elementDateTime.setType(BaseFormElement.TYPE_EDITOR);
                        elementDateTime.setTitle(vo.getLabel());
                        elementDateTime.setRequired(vo.isRequired());
                        elementDateTime.setClickListener(this::selectDateTime);
                        formItems.add(elementDateTime);
                        break;

                    case Constant.FILE:

                        if(vo.getLabel().equalsIgnoreCase("Upload Music")){
                            MAX_COUNT = 5;
                            iWORdDOCUMENT=false;
                            FormElementMusicFile elementMusicFile = FormElementMusicFile.createInstance();
                            value = vo.getValue();
                            elementMusicFile.setName(vo.getName());
                            elementMusicFile.setValue(value);
                            elementMusicFile.setTag(tag);
                            if (TextUtils.isEmpty(vo.getLabel())) {
                                vo.setLabel(getString(R.string.txt_upload_music));
                            }
                            elementMusicFile.setHint(getStrings(R.string.browse_) + vo.getLabel());
                            elementMusicFile.setTitle(vo.getLabel());
                            elementMusicFile.setRequired(vo.isRequired());
                            elementMusicFile.setClickListener(tag12 -> {

                                clickedFilePostion = tag12;
                                showAudioChooser(true);
                            });
                            formItems.add(elementMusicFile);
                        }else  if(vo.getLabel().equalsIgnoreCase("Upload only PDF or Word Document Only.")){
                            FormElementFile elementFile = FormElementFile.createInstance();
                            value = vo.getValue();


                            iWORdDOCUMENT=true;
                            elementFile.setName(vo.getName());
                            elementFile.setValue(value);
                            elementFile.setTag(tag);
                            elementFile.setTitle(vo.getLabel());
                            elementFile.setRequired(vo.isRequired());
                            elementFile.setHint(vo.getLabel());

                            elementFile.setClickListener(tag1 -> {
                                checkChooserOption(vo.getName());
                                clickedFilePostion = tag1;
                                openDOCPicker();
                            });
                            formItems.add(elementFile);
                        }
                        else {
                            MAX_COUNT = 1;
                            iWORdDOCUMENT=false;
                            FormElementFile elementFile = FormElementFile.createInstance();
                            value = vo.getValue();
                            elementFile.setName(vo.getName());
                            elementFile.setValue(value);
                            elementFile.setTag(tag);
                            elementFile.setTitle(vo.getLabel());
                            elementFile.setHint(getStrings(R.string.Upload_) + vo.getLabel());
                            elementFile.setRequired(vo.isRequired());
                            elementFile.setClickListener(tag1 -> {
                                checkChooserOption(vo.getName());
                                clickedFilePostion = tag1;
                                if (isVideoSelected) {
//                                showImageDialog(getStrings(R.string.MSG_SELECT_VIDEO_SOURCE));
                                    openVideoPicker(true);
                                } else {
//                                showImageDialog(getStrings(R.string.MSG_SELECT_IMAGE_SOURCE));
                                    openImagePicker();
                                }
                            });
                            formItems.add(elementFile);
                        }




                        break;

                    case Constant.CUSTOM_URL:
                        MAX_COUNT = 1;
                        FormElementUrl elementFile11 = FormElementUrl.createInstance();
                        value = vo.getValue();
                        elementFile11.setName(vo.getName());
                        elementFile11.setValue(value);
                        elementFile11.setTag(tag);
                        elementFile11.setTitle(vo.getLabel());
                        elementFile11.setHint(getStrings(R.string.browse_) + vo.getLabel());
                        elementFile11.setRequired(vo.isRequired());
                        elementFile11.setClickListener(tag1 -> {
                            checkClicked(tag);
                        });
                        formItems.add(elementFile11);
                        break;

                    case Constant.GROUP_QUESTION:
                        FormElementGroupQuestion elementGroup = FormElementGroupQuestion.createInstance();
                        // value = vo.getValue();
                        elementGroup.setName(vo.getName());
                        //elementFile.setValue(value);
                        elementGroup.setTag(tag);
                        //elementFile.setTitle(vo.getLabel());
                        elementGroup.setHint(vo.getLabel());
                        //elementFile.setRequired(vo.isRequired());
                        elementGroup.setClickListener(tag1 -> {
                            //clickedFilePostion = tag1;
                            onResponseSuccess(FORM_TYPE, tag1);
                        });
                        formItems.add(elementGroup);
                        break;

                    case Constant.KEY_MUSIC_SONG:
                        MAX_COUNT = 5;
                        FormElementMusicFile elementMusicFile = FormElementMusicFile.createInstance();
                        value = vo.getValue();
                        elementMusicFile.setName(vo.getName());
                        elementMusicFile.setValue(value);
                        elementMusicFile.setTag(tag);
                        if (TextUtils.isEmpty(vo.getLabel())) {
                            vo.setLabel(getString(R.string.txt_upload_music));
                        }
                        elementMusicFile.setHint(getStrings(R.string.browse_) + vo.getLabel());
                        elementMusicFile.setTitle(vo.getLabel());
                        elementMusicFile.setRequired(vo.isRequired());
                        elementMusicFile.setClickListener(tag12 -> {

                            clickedFilePostion = tag12;
                            showAudioChooser(true);
                        });
                        formItems.add(elementMusicFile);

                        break;


                    case Constant.SELECT:
                    case Constant.RADIO:

                        if (vo.getName().contains("sescredit_site_offers")) {
                            FormElementPickerSingle elementSelect = FormElementPickerSingle.createInstance();
                            value = vo.getValue();
                            elementSelect.setValue(vo.getMultiOptions().get(value));
                            elementSelect.setOptions(vo.getMultiOptionsList());
                            elementSelect.setTag(tag);
                            elementSelect.setPickerTitle((vo.getName().contains("subcat") || vo.getName().contains("category"))
                                    ? Constant.TITLE_CHOOSE_CATEGORY
                                    : getStrings(R.string.select_) + vo.getLabel());
                            elementSelect.setName(vo.getName());
                            elementSelect.setHint(elementSelect.getPickerTitle());
                            elementSelect.setTitle(vo.getLabel());
                            elementSelect.setRequired(true);

                            commonMap.put(vo.getName(), vo.getMultiOptions());
                            formItems.add(elementSelect);
                        } else {
                            FormElementPickerSingle elementSelect = FormElementPickerSingle.createInstance();
                            value = vo.getValue();
                            elementSelect.setValue(vo.getMultiOptions().get(value));
                            elementSelect.setOptions(vo.getMultiOptionsList());
                            elementSelect.setTag(tag);
                            elementSelect.setPickerTitle((vo.getName().contains("subcat") || vo.getName().contains("category"))
                                    ? Constant.TITLE_CHOOSE_CATEGORY
                                    : getStrings(R.string.select_) + vo.getLabel());
                            elementSelect.setName(vo.getName());
                            elementSelect.setHint(elementSelect.getPickerTitle());
                            elementSelect.setTitle(vo.getLabel());
                            elementSelect.setRequired(vo.isRequired());

                            commonMap.put(vo.getName(), vo.getMultiOptions());
                            formItems.add(elementSelect);
                        }

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
                        multiSelect.setHint(getStrings(R.string.select_) + vo.getLabel());
                        multiSelect.setName(vo.getName());
                        multiSelect.setTitle(vo.getLabel());
                        multiSelect.setRequired(vo.isRequired());
                        commonMap.put(vo.getName(), vo.getMultiOptions());
                        formItems.add(multiSelect);

                        break;

                    case Constant.CHECKBOX:

                        FormElementCheckbox elementCheckbox = FormElementCheckbox.createInstance();
                        elementCheckbox.setTag(tag);
                        elementCheckbox.setName(vo.getName());
                        elementCheckbox.setValue("" + vo.getValue());
                        //CustomLog.d("checkbox", vo.getValue() + "****" + vo.getValueString());
                        elementCheckbox.setTitle(vo.getLabel());
                        elementCheckbox.setRequired(vo.isRequired());

                        if(vo.getName().equalsIgnoreCase("currentlywork") &&  vo.getValue().equalsIgnoreCase("1")){
                            isCurrentllywork=1;
                        }else {
                            isCurrentllywork=0;
                        }

                      /* elementCheckbox.setPositiveText("java java java");
                        elementCheckbox.setClickListener(new OnTextClickListener() {
                            @Override
                            public void onTextClicked(int tag) {
                                CustomLog.d("java", "java");
                            }
                        });*/
                        formItems.add(elementCheckbox);
                        break;

                    case Constant.RATE:
                        FormElementRating elementRating = FormElementRating.createInstance();
                        elementRating.setTag(tag);
                        elementRating.setName(vo.getName());
                        elementRating.setValue(TextUtils.isEmpty(vo.getValue()) ? "0" : vo.getValue());
                        elementRating.setTitle(vo.getLabel());
                        elementRating.setRequired(vo.isRequired());
                        formItems.add(elementRating);
                        break;

                    case Constant.BUTTON:

                        FormElementButton elementButton = FormElementButton.createInstance();
                        elementButton.setTag(tag);
                        elementButton.setTitle(vo.getLabel());
                        //elementButton.setRequired(vo.isRequired());
                        elementButton.setClickListener(tag13 -> onSubmitButtonPressed());
                        formItems.add(elementButton);
                        break;
                }

                //don not show description
                /*if (!TextUtils.isEmpty(vo.getDescription())) {
                    formItems.add(FormHeader.createInstance(vo.getDescription()));
                }*/

                // i++; //increamnt value [used to identify TYPE_FILE postition in list]
            }
            mFormBuilder.addFormElements(formItems);
            // mFormBuilder.getAdapter().getDataset().get(0).getValue();
            //  mFormBuilder.getAdapter().getDataset().get(0).setValue(mFormBuilder.getAdapter().getDataset().get(0).getValue());
            //  mFormBuilder.getAdapter().notifyItemChanged(0);
            //  CustomLog.e("first value", "" + mFormBuilder.getAdapter().getDataset().get(0).getValue());

            if(isCurrentllywork==1){
                for (int i = 0; i < tagList.size(); i++) {
                    int tag111 = 1011 + i;
                    //key = tagList.get(i);
                    String name223 = tagList.get(i);
                    //  Log.e("Keyvalue",""+Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue()));
                    if (name223.equals("tomonth") || name223.equals("toyear")) {
                        mFormBuilder.getAdapter().setHiddenAtTag(tag111, true);
                    }
                }
                mFormBuilder.getAdapter().notifyDataSetChanged();
            }

            //   mFormBuilder.();
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    public void checkClicked(int tag) {
        //Override this method on child
    }

    public void onSubmitButtonPressed() {
        closeKeyboard();
        callSignUpApi(fetchFormValue());
    }

    public void checkChooserOption(String name) {
        //override this method on child classes that has both file type : Image and Video
    }

    private final int LOCATION_AUTOCOMPLETE_REQUEST_CODE = 8976;


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
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(activity);
            startActivityForResult(intent, LOCATION_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            CustomLog.e(e);

        }
    }

    public void startEditorActivity(int tag) {
        closeKeyboard();
        Intent intent = new Intent(context, EditorExampleActivity.class);
        BaseFormElement element = mFormBuilder.getAdapter().getValueAtTag(tag);
        String value = element.getValue();
        String title = element.getTitle();
        Bundle bundle = new Bundle();
        bundle.putString(EditorExampleActivity.TITLE_PARAM, title);
        bundle.putString(EditorExampleActivity.CONTENT_PARAM, value);
        bundle.putInt(Constant.TAG, tag);
        bundle.putString(EditorExampleActivity.TITLE_PLACEHOLDER_PARAM,
                title);
        bundle.putString(EditorExampleActivity.CONTENT_PLACEHOLDER_PARAM,
                title);
        bundle.putInt(EditorExampleActivity.EDITOR_PARAM, EditorExampleActivity.USE_NEW_EDITOR);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQ_EDITOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
      /*  try {
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

                case                       :
                    switch (resultCode) {
                        case RESULT_OK:
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            CharSequence address = place.getAddress();
                            //to get latitude using places api
                            Double lat = place.getLatLng().latitude;
                            //to get longitude using places api
                            Double lang = place.getLatLng().longitude;
                            //Using Geocoder to get all the other fields of that place.
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            //getting exact location using geocoder.
                            List<Address> addresses = geocoder.getFromLocation(lat, lang, 1);

                            CustomLog.e("lat:", "" + lat);
                            CustomLog.e("lang", "" + lang);
                            CustomLog.e("country", "" + addresses.get(0).getCountryName());
                            CustomLog.e("city", "" + addresses.get(0).getLocality());
                            CustomLog.e("ZIP", "" + addresses.get(0).getPostalCode());
                            CustomLog.e("state", "" + addresses.get(0).getAdminArea());
                            CustomLog.e("subLoc", "" + addresses.get(0).getSubLocality());
                            CustomLog.e("locale", "" + addresses.get(0).getLocale());

                            //To send all the location fields with location in form
                            mapHiddenFields.put("ses_city", addresses.get(0).getLocality());
                            mapHiddenFields.put("ses_zip", addresses.get(0).getPostalCode());
                            mapHiddenFields.put("ses_country", addresses.get(0).getCountryName());
                            mapHiddenFields.put("ses_state", addresses.get(0).getAdminArea());
                            mapHiddenFields.put("ses_lat", lat);
                            mapHiddenFields.put("lat", lat);
                            mapHiddenFields.put("lng", lang);
                            mapHiddenFields.put("ses_lng", lang);

                            //setting value of location in the locationTag.
                            if (null != address) {
                                mFormBuilder.getAdapter().setValueAtTag(locationTag, address.toString());
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }


    private void selectDateTime(final int tag) {

        Date minDate = new Date();
        Date initDate = new Date();
        String value = mFormBuilder.getFormElement(tag).getValue();
        if (!TextUtils.isEmpty(value)) {
            initDate = Util.getDateFromString(value, Constant.DATE_FROMAT_FEED);
        }
        if (tag == arrTimeTag[1]) {
            //this means user selecting End date
            // set minimum date in this case
            String valueStartTime = mFormBuilder.getFormElement(arrTimeTag[0]).getValue();
            if (TextUtils.isEmpty(valueStartTime)) {
                Util.showSnackbar(v, getString(R.string.select_start_time));
                return;
            } else {
                minDate = Util.getDateFromString(valueStartTime, Constant.DATE_FROMAT_FEED);
            }
        } else {
            //this means user selecting Start date
            // dont set minimum date in this case
            minDate = null;
        }
        try {
            SlideDateTimePicker picker = new SlideDateTimePicker.Builder(activity.getSupportFragmentManager())
                    .setListener(new SlideDateTimeListener() {
                        @Override
                        public void onDateTimeSet(Date date) {
                            mFormBuilder.getAdapter().setValueAtTag(tag, Util.getCurrentdate(date, Constant.DATE_FROMAT_FEED));
                            if (tag == arrTimeTag[0])
                                mFormBuilder.getAdapter().setValueAtTag(arrTimeTag[1], "");
                            CustomLog.d("date1", Util.getCurrentdate(date, Constant.DATE_FROMAT_FEED));
                        }

                        @Override
                        public void onDateTimeCancel() {
                        }
                    })
                    .setInitialDate(initDate)
                    .setMinDate(minDate)
                    .setIndicatorColor(Color.parseColor(Constant.colorPrimary))
                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                    .build();

            try {
                picker.show();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public List<String> getMultiOptionsList(Map<String, String> map) {
        List<String> result = new ArrayList<>();
        //  Map<String, String> map = options;
        try {
            if (null != map) {
                CustomLog.e("multiOptions", new Gson().toJson(map));
                result.addAll(map.values());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private void updateFormItemOptions(int tag, Map<String, String> subCategory) {
        if (subCategory.size() > 0) {
            mFormBuilder.getAdapter().setOptionAtTag(tag + 1, getMultiOptionsList(subCategory));
            mFormBuilder.getAdapter().setValueAtTag(tag+1,"  ");
            FormElementPickerSingle element = (FormElementPickerSingle) mFormBuilder.getFormElement(tag + 1);
            commonMap.put(element.getName(), subCategory);
        }
    }


    public void callSignUpApi(Map<String, Object> params) {
        switch (FORM_TYPE) {
            case Constant.FormType.FILTER_CORE:
            case Constant.FormType.FILTER_ALBUM:
            case Constant.FormType.FILTER_PHOTO:
            case Constant.FormType.FILTER_BLOG:
            case Constant.FormType.FILTER_CLASSIFIED:
            case Constant.FormType.FILTER_ARTICLE:
            case Constant.FormType.FILTER_VIDEO:
            case Constant.FormType.FILTER_MUSIC_ALBUM:
            case Constant.FormType.FILTER_MUSIC_SONG:
            case Constant.FormType.FILTER_MUSIC_PLAYLIST:
            case Constant.FormType.FILTER_MEMBER:
            case Constant.FormType.FILTER_QUOTE:
            case Constant.FormType.FILTER_PRAYER:
            case Constant.FormType.FILTER_WISH:
            case Constant.FormType.FILTER_EVENT:
            case Constant.FormType.FILTER_THOUGHT:
            case Constant.FormType.FILTER_PAGE:
            case Constant.FormType.FILTER_PAGE_POLL:
            case Constant.FormType.FILTER_STORE:
            case Constant.FormType.FILTER_PRODUCT:
            case Constant.FormType.FILTER_COURSE:
            case Constant.FormType.FILTER_PROFESSIONAL:
            case Constant.FormType.FILTER_CONTEST:
            case Constant.FormType.FILTER_BUSINESS:
            case Constant.FormType.FILTER_POLL:
            case Constant.FormType.FILTER_GROUP:
            case Constant.FormType.FILTER_QA:
            case Constant.FormType.FILTER_PAGE_REVIEW:
            case Constant.FormType.FILTER_GROUP_REVIEW:
            case Constant.FormType.FILTER_BUSINESS_REVIEW:
                activity.filteredMap = params;
                activity.isBackFrom = Constant.FormType.FILTER_CORE;//FORM_TYPE;
                onBackPressed();
                return;

        }
        try {
            if (isNetworkAvailable(context)) {
                // showBaseLoader(false);

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.putAll(params);

                    if (FORM_TYPE == Constant.FormType.EDIT_USER) {
                        request.params.put(Constant.KEY_VALIDATE_FIELD_FORM, 1);
                    }

                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
//                    request.params.put(Constant.KEY_AUTH_TOKEN, "1641b1b8453a1ccc1555046244");
                    request.requestMethod = HttpPost.METHOD_NAME;

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        // hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (!err.isSuccess()) {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    return true;
                                }
                                JSONObject json = new JSONObject(response);
                                if (json.get(Constant.KEY_RESULT) instanceof String) {
                                    String result = json.getString(Constant.KEY_RESULT);
                                    Util.showSnackbar(v, result);
                                    goBackIfValid();
                                } else {
                                    String message = Constant.EMPTY;
                                    try {
                                        switch (FORM_TYPE) {
                                            case Constant.FormType.REPLY_TOPIC:
                                            case Constant.FormType.ADD_EVENT_LIST:
                                            case Constant.FormType.CREATE_NEWS:
                                            case Constant.FormType.CREATE_RECIPE:
                                            case Constant.FormType.CREATE_DISCUSSTION:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                          /*  case Constant.FormType.CREATE_BLOG:
                                                try {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                    Util.showSnackbar(v, message);
                                                    int   blogid = json.getJSONObject(Constant.KEY_RESULT).getInt("blog_id");
                                                    fragmentManager.beginTransaction()
                                                            .replace(R.id.container
                                                                    , ViewBlogFragment.newInstance(blogid))
                                                            .addToBackStack(null)
                                                            .commit();
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                }
                                                break;*/
                                            case Constant.FormType.CREATE_BLOG:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_BLOG_ID);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.MOVE_FORUM_TOPIC:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.EDIT_TOPIC:
                                            case Constant.FormType.EDIT_CLASSROOM:
                                                try{
                                                    if (SPref.getInstance().getDefaultInfo(getContext(), Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
                                                        message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                    } else {
                                                        message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                    }

                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                }
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },1000);
                                                break;
                                            case Constant.FormType.RENAME_FORUM_TOPIC:
                                            case Constant.FormType.REPLY_FORUM_TOPIC:
                                            case Constant.FormType.QUOTE_POST:
                                            case Constant.FormType.CREATE_FORUM_TOPIC:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.TYPE_JOB_EDIT:
                                            case Constant.FormType.TYPE_BLOG_EDIT:
                                            case Constant.FormType.TYPE_NEWS_EDIT:
                                            case Constant.FormType.TYPE_RSS_EDIT:

                                            case Constant.FormType.TYPE_RECIPE_EDIT:
                                            case Constant.FormType.EDIT_CLASSIFIED:
                                            case Constant.FormType.EDIT_CORE_POLL:
                                            case Constant.FormType.TYPE_ARTICLE_EDIT:
                                            case Constant.FormType.CREATE_ARTICLE:
                                            case Constant.FormType.POINT_PURCHASE:

                                            case Constant.FormType.EDIT_HOST:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                if (listener != null) {
                                                    listener.onItemClicked(Constant.Events.SUCCESS, "", 0);
                                                } else {
                                                    goBackIfValid();
                                                }
                                                break;
                                            case Constant.FormType.CREATE_TICK:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("video").getString("message");
                                                Util.showSnackbar(v, message);
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ClickClickFragment()).commit();
                                                break;
                                            case Constant.FormType.EDIT_ANNOUNCEMENT:
                                            case Constant.FormType.CREATE_ANNOUNCEMENT:
                                            case Constant.FormType.EDIT_ALBUM_OTHERS:
                                            case Constant.FormType.AWARD:
                                            case Constant.FormType.SEO:
                                            case Constant.FormType.OVERVIEW:
                                            case Constant.FormType.RULES:
                                            case Constant.FormType.EDIT_CONTACT:
                                            case Constant.FormType.EDIT_ENTRY:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.EDIT_REVIEW:
                                            case Constant.FormType.CREATE_REVIEW:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_REVIEW_ID);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.EDIT_CHANNEL:
                                                message = json.getString(Constant.KEY_RESULT);
                                                Util.showSnackbar(v, "H");
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ClickClickFragment()).commit();
                                                break;
                                            case Constant.FormType.EDIT_FUND:
                                            case Constant.FormType.CREATE_FUND:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_FUND_ID);
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_ALBUM:
                                            case Constant.FormType.CREATE_ALBUM_OTHERS:
                                            case Constant.FormType.EDIT_ALBUM:
                                            case Constant.FormType.CREATE_MUSIC:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                int albumId = json.getJSONObject(Constant.KEY_RESULT).optInt("playlist_id");
                                                if (albumId == 0) {
                                                    albumId = json.getJSONObject(Constant.KEY_RESULT).getInt("album_id");
                                                }
                                                if (listener != null) {
                                                    listener.onItemClicked(Constant.Events.SUCCESS, "", albumId);
                                                } else {
                                                    activity.taskId = albumId;
                                                    goBackIfValid();
                                                }
                                                break;
                                            case Constant.FormType.CREATE_CLASSIFIED:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                if (listener != null) {
                                                    int classifiedId = json.getJSONObject(Constant.KEY_RESULT).getInt("classified_id");
                                                    listener.onItemClicked(Constant.Events.SUCCESS, "", classifiedId);
                                                } else {
                                                    goBackIfValid();
                                                }
                                                break;


                                            case Constant.FormType.RESET_PASSWORD:
                                              //  fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment()).commit();

                                                fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                                                        .commit();
                                                break;

                                            case Constant.FormType.JOIN_GROUP:
                                                //fetch message ,If message is not present then show validation error
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");

                                                //send response to previous screen
                                                activity.stringValue = response;
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_QA:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                //activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_QUESTION_ID);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.EDIT_QA:
                                                //message = json.getJSONObject(Constant.KEY_RESULT).optString("success_message");
                                                //activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, getString(R.string.msg_question_edit_successfully));
                                                //activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_QUESTION_ID);
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_GROUP:
                                            case Constant.FormType.EDIT_GROUP:
                                                if (json.getJSONObject(Constant.KEY_RESULT).has("success_message")) {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                } else {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                }
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_GROUP_ID);
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_BUSINESS:
                                            case Constant.FormType.EDIT_BUSINESS:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_BUSINESS_ID);
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_QUOTE:
                                            case Constant.FormType.CREATE_PRAYER:
                                            case Constant.FormType.CREATE_THOUGHT:
                                            case Constant.FormType.CREATE_WISH:

                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                if (listener != null) {
                                                    int quoteId = json.getJSONObject(Constant.KEY_RESULT).optInt(Constant.KEY_ID);
                                                    listener.onItemClicked(Constant.Events.SUCCESS, "", quoteId);
                                                } else {
                                                    goBackIfValid();
                                                }
                                                break;

                                            case Constant.FormType.CHANGE_NUMBER:
                                                Dummy vo = new Gson().fromJson(response, Dummy.class);
                                                if (vo.getResult().getFormfields() != null) {
                                                    openOtpFragment(OTPFragment.FROM_NUMBER_CHANGE, request.params, response);
                                                } else {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("success");
                                                    Util.showSnackbar(v, message);
                                                    goBackIfValid();
                                                }
                                                break;

                                            case Constant.FormType.CREATE_VIDEO:
                                                BaseActivity.backcoverchange=Constant.FormType.CREATE_VIDEO;
                                                Util.showSnackbar(v, getStrings(R.string.txt_video_success));
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.CREATE_EVENT_VIDEO:
                                                Util.showSnackbar(v, getStrings(R.string.txt_video_success));
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.KEY_EDIT_VIDEO:
                                            case Constant.FormType.CREATE_PAGE_VIDEO:
                                                activity.taskPerformed=Constant.FormType.CREATE_VIDEO_DATA;
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt(Constant.KEY_VIDEO_ID);
                                                goBackIfValid();
                                                break;

                                            case Constant.FormType.CREATE_EVENT:
                                         //   case Constant.FormType.CREATE_GROUP_EVENT:
                                            case Constant.FormType.EDIT_EVENT:
                                                if (json.getJSONObject(Constant.KEY_RESULT).has("success_message"))
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                else
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("event_id");
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.CREATE_POLL:
                                            case Constant.FormType.EDIT_POLL:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("poll_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);


                                                break;
                                            case Constant.FormType.CLAIM:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                onResponseSuccess(FORM_TYPE, message);
                                                break;
                                            case Constant.FormType.CREATE_PAGE:
                                            case Constant.FormType.EDIT_PAGE:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("page_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);
                                                break;

                                            case Constant.FormType.CLAIM_CLASS:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, "Your request for claim has been sent to site owner. He will contact you soon.");
                                                break;
                                            case Constant.FormType.CREATE_LECTURE:
                                                try {
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("lecture_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("course_id");
                                                }
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },1000);

                                                break;
                                            case Constant.FormType.CREATE_COURSE:
                                                try {
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("course_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("lecture_id");
                                                }
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                 new Handler().postDelayed(()->{
                                                     goBackIfValid();
                                                  },1000);




                                                break;
                                           case Constant.FormType.URL_CREATE_JOB:
                                                try {
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("job_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("job_id");
                                                }
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                 new Handler().postDelayed(()->{
                                                     goBackIfValid();
                                                  },1000);

                                                break;
                                            case Constant.FormType.CREATE_CLASSROOM:
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("classroom_id");
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.BECOME_PROFESSIONAL:
//                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("classroom_id");
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.EDIT_SERVICE:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("service_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.CREATE_STORE:
                                            case Constant.FormType.EDIT_STORE:
                                                try {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                }catch (Exception ex){
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                    ex.printStackTrace();
                                                }
                                                try {
                                                    activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                    activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("store_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                }
                                                 Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.EDIT_COURSE:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("course_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.ADD_LOCATION:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;

                                            case Constant.FormType.CREATE_RESUME_EXPRIENCE:
                                            case Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT:
                                            case Constant.FormType.CREATE_RESUME_EDUCATION_EDIT:
                                            case Constant.FormType.CREATE_RESUME_EDUCATION:
                                            case Constant.FormType.CREATE_RESUME_PROJECT:
                                            case Constant.FormType.CREATE_RESUME_PROJECT_EDIT:
                                            case Constant.FormType.CREATE_RESUME_CERTIFICATE:
                                            case Constant.FormType.CREATE_RESUME_REFERENCE:
                                            case Constant.FormType.CREATE_RESUME_REFERENCE_EDIT:
                                            case Constant.FormType.CREATE_RESUME_CERTIFICATE_EDIT:
                                            case Constant.FormType.CREATE_RESUME_CARIOROBJECT:

                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                try {
                                                    resume_id = json.getJSONObject(Constant.KEY_RESULT).getInt("resume_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    resume_id=0;
                                                }
                                                Util.showSnackbar(v, message);
                                                goBackIfValid();
                                                break;
                                            case Constant.FormType.CREATE_RESUME_INFORMATION:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                try {
                                                    resume_id = json.getJSONObject(Constant.KEY_RESULT).getInt("resume_id");
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    resume_id=0;
                                                }
                                                Util.showSnackbar(v, message);
                                                //   goBackIfValid();
                                                break;


                                            case Constant.FormType.KEY_EDIT_LECTURE:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("lecture_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.CREATE_CONTEST:
                                            case Constant.FormType.EDIT_CONTEST:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                                activity.stringValue = json.getJSONObject(Constant.KEY_RESULT).optString("redirect");
                                                activity.taskId = json.getJSONObject(Constant.KEY_RESULT).getInt("contest_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.ADD_CHANNEL:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("chanel").getString("message");
                                                Constant.channelId = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("chanel").getInt("chanel_id");
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;
                                            case Constant.FormType.TYPE_ADD_WISHLIST:
                                            case Constant.FormType.TYPE_ADD_COURSE_WISHLIST:
                                            case Constant.FormType.TYPE_EDIT_WISHLIST:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                activity.taskPerformed = 89;
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);


                                                break;
                                            case Constant.FormType.TYPE_ADD_SONG:
                                                try {
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                                    activity.taskPerformed = 89;
                                                    Util.showSnackbar(v, message);
                                                    new Handler().postDelayed(()->{
                                                        goBackIfValid();
                                                    },3000);
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                    message = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("playlist").getString("message");
                                                    // if(TextUtils.isEmpty(message)){
                                                    Util.showSnackbar(v, message);
                                                    new Handler().postDelayed(()->{
                                                        goBackIfValid();
                                                    },3000);
                                                }
                                                break;
                                            case Constant.FormType.TYPE_ADD_ALBUM:
                                                message = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("playlist").getString("message");
                                                // if(TextUtils.isEmpty(message)){
                                                Util.showSnackbar(v, message);
                                                new Handler().postDelayed(()->{
                                                    goBackIfValid();
                                                },3000);

                                                break;

                                            default:
                                                FormError resp = new Gson().fromJson(response, FormError.class);
                                                if (TextUtils.isEmpty(resp.getError())) {
                                                    Util.showSnackbar(v, resp.getResult().fetchFirstNErrors());
                                                } else {
                                                    Util.showSnackbar(v, resp.getMessage());
                                                }
                                                break;
                                        }
                                    } catch (Exception e) {
                                        // ErrorResponse resp = new Gson().fromJson(response, ErrorResponse.class);
                                        // message = resp.getErrorMessage();
                                        FormError resp = new Gson().fromJson(response, FormError.class);
                                        //  CustomLog.e("from_vo", "" + new Gson().toJson(errorList));
                                        if (TextUtils.isEmpty(resp.getError())) {
                                            Util.showSnackbar(v, resp.getResult().fetchFirstNErrors());
                                        } else {
                                            Util.showSnackbar(v, resp.getMessage());

                                        }
                                    }
                                }
                            } else {
                                hideBaseLoader();
                                somethingWrongMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                            somethingWrongMsg(v);
                        }
                        return true;
                    };
                    new HttpImageRequestHandler(activity, new Handler(callback), true).run(request);
                } catch (Exception e) {
                    notInternetMsg(v);
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goBackIfValid() {
        switch (FORM_TYPE) {
            case Constant.FormType.EDIT_USER:
            case Constant.FormType.EDIT_ALBUM:
            case Constant.FormType.EDIT_ALBUM_OTHERS:
            case Constant.FormType.CREATE_ALBUM_OTHERS:
            case Constant.FormType.EDIT_QUOTE:
            case Constant.FormType.CREATE_QUOTE:
            case Constant.FormType.CREATE_CLASSIFIED:
            case Constant.FormType.EDIT_CLASSIFIED:
            case Constant.FormType.CREATE_PRAYER:
            case Constant.FormType.EDIT_CORE_POLL:
            case Constant.FormType.CREATE_WISH:
            case Constant.FormType.CREATE_THOUGHT:
            case Constant.FormType.EDIT_PRAYER:
            case Constant.FormType.EDIT_WISH:
            case Constant.FormType.EDIT_THOUGHT:
            case Constant.FormType.TYPE_EDIT_CHANNEL:
            case Constant.FormType.TYPE_SONGS:
            case Constant.FormType.TYPE_JOB_EDIT:
            case Constant.FormType.TYPE_BLOG_EDIT:
            case Constant.FormType.TYPE_NEWS_EDIT:
            case Constant.FormType.TYPE_RSS_EDIT:
            case Constant.FormType.TYPE_RECIPE_EDIT:
            case Constant.FormType.TYPE_ARTICLE_EDIT:
            case Constant.FormType.TYPE_PLAYLIST_VIDEO:
            case Constant.FormType.CREATE_GROUP:
            case Constant.FormType.EDIT_GROUP:
            case Constant.FormType.CREATE_EVENT:
         //   case Constant.FormType.CREATE_GROUP_EVENT:
            case Constant.FormType.EDIT_EVENT:
            case Constant.FormType.CREATE_POLL:
            case Constant.FormType.EDIT_POLL:
            case Constant.FormType.CREATE_MUSIC:
            case Constant.FormType.CREATE_PAGE:
            case Constant.FormType.CREATE_CLASSROOM:
            case Constant.FormType.CREATE_LECTURE:
            case Constant.FormType.CREATE_VIDEO:
            case Constant.FormType.CREATE_STORE:
            case Constant.FormType.EDIT_SERVICE:
            case Constant.FormType.CREATE_CONTEST:
            case Constant.FormType.EDIT_CONTEST:
            case Constant.FormType.EDIT_PAGE:
            case Constant.FormType.EDIT_STORE:
            case Constant.FormType.CREATE_DISCUSSTION:
            case Constant.FormType.EDIT_TOPIC:
            case Constant.FormType.CREATE_FORUM_TOPIC:
            case Constant.FormType.RENAME_FORUM_TOPIC:
            case Constant.FormType.MOVE_FORUM_TOPIC:
            case Constant.FormType.QUOTE_POST:
            case Constant.FormType.REPLY_TOPIC:
            case Constant.FormType.CREATE_REVIEW:
            case Constant.FormType.CREATE_COURSE:
            case Constant.FormType.CLAIM_CLASS:
            case Constant.FormType.EDIT_COURSE:
            case Constant.FormType.CREATE_EVENT_VIDEO:
            case Constant.FormType.CREATE_PAGE_VIDEO:
            case Constant.FormType.EDIT_REVIEW:
            case Constant.FormType.EDIT_HOST:
            case Constant.FormType.AWARD:
            case Constant.FormType.SEO:
            case Constant.FormType.OVERVIEW:
            case Constant.FormType.RULES:
            case Constant.FormType.EDIT_CONTACT:
            case Constant.FormType.EDIT_CLASSROOM:
            case Constant.FormType.EDIT_ENTRY:
            case Constant.FormType.JOIN_GROUP:
            case Constant.FormType.CREATE_BUSINESS:
            case Constant.FormType.CREATE_TICK:
            case Constant.FormType.EDIT_BUSINESS:
            case Constant.FormType.CLAIM:
            case Constant.FormType.CREATE_QA:
            case Constant.FormType.EDIT_QA:
            case Constant.FormType.EDIT_FUND:
            case Constant.FormType.KEY_EDIT_LECTURE:
            case Constant.FormType.CREATE_FUND:
            case Constant.FormType.CREATE_TEST:
            case Constant.FormType.EDIT_ANNOUNCEMENT:
            case Constant.FormType.CREATE_ANNOUNCEMENT:
            case Constant.FormType.ADD_LOCATION:
            case Constant.FormType.CHANGE_NUMBER:
            case Constant.FormType.BECOME_PROFESSIONAL:
            case Constant.FormType.EDIT_MUSIC_ALBUM:
            case Constant.FormType.EDIT_MUSIC_PLAYLIST:
            case Constant.FormType.CREATE_BLOG:
                //add FORM_TYPE as taskPerformed
                // then go back,and handle this task on previous screen
                activity.taskPerformed = FORM_TYPE;
                onBackPressed();
                break;
            case Constant.FormType.CREATE_RESUME_REFERENCE:
            case Constant.FormType.CREATE_RESUME_REFERENCE_EDIT:
            case Constant.FormType.CREATE_RESUME_CERTIFICATE_EDIT:
            case Constant.FormType.CREATE_RESUME_PROJECT_EDIT:
            case Constant.FormType.CREATE_RESUME_EXPRIENCE:
            case Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT:
            case Constant.FormType.CREATE_RESUME_EDUCATION_EDIT:
            case Constant.FormType.CREATE_RESUME_EDUCATION:
            case Constant.FormType.CREATE_RESUME_PROJECT:
            case Constant.FormType.CREATE_RESUME_CERTIFICATE:
            case Constant.FormType.CREATE_RESUME_CARIOROBJECT:
                //add FORM_TYPE as taskPerformed
                // then go back,and handle this task on previous screen
                Constant.backresume = FORM_TYPE;
                onBackPressed();
                break;

            case Constant.FormType.EDIT_CHANNEL:
            case Constant.FormType.ADD_CHANNEL:
              //  Intent returnIntent = new Intent();
               // activity.setResult(Constant.EDIT_CHANNEL_ME, returnIntent);
                onBackPressed();
                break;

            case Constant.FormType.STORY_ARCHIVE:
            case Constant.FormType.ADD_EVENT_LIST:
            case Constant.FormType.KEY_EDIT_VIDEO:

            case Constant.FormType.REPLY_FORUM_TOPIC:
            case Constant.FormType.TYPE_ADD_SONG:
            case Constant.FormType.TYPE_ADD_WISHLIST:
            case Constant.FormType.TYPE_EDIT_WISHLIST:
            case Constant.FormType.TYPE_ADD_COURSE_WISHLIST:
            case Constant.FormType.TYPE_ADD_ALBUM:
            case Constant.FormType.ADD_VIDEO:
            case Constant.FormType.PAGE_CONTACT:
            case Constant.FormType.INVITE:

                //Simply go back,nothing have to handle on previous screen
                onBackPressed();
                break;
        }
    }

    private void callCategoryApi(Map<String, Object> map, String url, final int tag) {
        try {
            if (isNetworkAvailable(context)) {
                // showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.putAll(map);
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
                                    CommonResponse vo = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        if (vo.getResult().isSubCategoryNotNull()) {
                                            updateFormItemOptions(tag, vo.getResult().getSubCategory());
                                        } else if (vo.getResult().isSubSubCategoryNotNull()) {
                                            updateFormItemOptions(tag, vo.getResult().getSubSubCategory());
                                        }
                                    }/* else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }*/
                                }
                            } catch (Exception e) {
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


    public Map<String, Object> fetchFormValue() {
        Map<String, Object> request = new HashMap<>(mapHiddenFields);

        request.put("subject", "");

        String name;
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            name = tagList.get(i);
            BaseFormElement targetElement = mFormBuilder.getFormElement(tag);
            if (targetElement != null) {
                String targetValue = targetElement.getValue();
                CustomLog.d(name, "" + targetValue);
                if (null != targetValue) {

                    if (targetElement instanceof FormElementPickerMulti) {
                        List<String> selectedValues = ((FormElementPickerMulti) targetElement).getOptionsSelected();
                        List<String> allOptions = ((FormElementPickerMulti) targetElement).getOptions();
                        String target = "";
                        for (String value : allOptions) {
                            target = Util.getKeyFromValue215(commonMap.get(name), value);
                            if (selectedValues.contains(value)) {
                                request.put(name + "[" + target + "]", target);
                            }
                        }
                    } else {

                        if (commonMap.containsKey(name)) {
                            //if item is a spinner then get key of the selected value
                            if(name.equalsIgnoreCase("timezone")){
                                targetValue = Util.getKeyFromValue21(commonMap.get(name), targetValue);
                            }else if(name.equalsIgnoreCase("country")){
                                targetValue = Util.getKeyFromValue216(commonMap.get(name), targetValue);
                            }else {
                                targetValue = Util.getKeyFromValue215(commonMap.get(name), targetValue);
                            }
                        }
                        if (targetElement instanceof FormElementFile) {
                            //change key to either "video" or "image" if item is a FILE_TYPE
                            if (FORM_TYPE == Constant.FormType.TYPE_ADD_WISHLIST || FORM_TYPE == Constant.FormType.TYPE_ADD_COURSE_WISHLIST) {
                                name = name;
                            } else if (FORM_TYPE >= Constant.FormType.FILTER_GROUP) {
                                name = Constant.FILE_TYPE + name;//+isVideoSelected ? "video" : "image";
                            } else {
                                name = Constant.FILE_TYPE + (isVideoSelected ? "video" : "image");
                            }
                            request.put(name, targetValue);
                        } else if (targetElement instanceof FormElementMusicFile) {
                            // check if this is a music file
                            if (((FormElementMusicFile) targetElement).isFileSelected()) {
                                //change key to musicupload[0],musicupload[1]
                                List<String> list = ((FormElementMusicFile) targetElement).getMusicList();
                                for (int j = 0; j < list.size(); j++) {
                                    request.put(Constant.FILE_TYPE + Constant.KEY_MUSIC_SONG + "[" + j + "]", list.get(j));
                                }
                            }
                        } else {
                            request.put(name, targetValue);
                        }
                    }
                } else {
                    if (targetElement instanceof FormElementFile) {
                        name = Constant.FILE_TYPE + (isVideoSelected ? "video" : "image");
                    }
                    request.put(name, Constant.EMPTY);
                }
            }
        }


        switch (FORM_TYPE) {
            case Constant.FormType.CREATE_ALBUM:
                request.put(Constant.KEY_MODULE, Constant.ModuleName.ALBUM);
                break;

            case Constant.FormType.CREATE_RESUME_INFORMATION:
                if(RESUMEID!=0){
                    request.put(Constant.KEY_RESUME_ID, RESUMEID);
                }
                break;
            case Constant.FormType.CREATE_RESUME_EXPRIENCE:
            case Constant.FormType.CREATE_RESUME_EDUCATION:
            case Constant.FormType.CREATE_RESUME_PROJECT:
            case Constant.FormType.CREATE_RESUME_CERTIFICATE:
            case Constant.FormType.CREATE_RESUME_REFERENCE:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                break;
            case Constant.FormType.CREATE_RESUME_REFERENCE_EDIT:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                request.put(Constant.KEY_REFERENCE_ID, map.get("reference_id"));
                break;
            case Constant.FormType.CREATE_RESUME_EDUCATION_EDIT:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                request.put(Constant.KEY_EDUCATION_ID, map.get(Constant.KEY_EDUCATION_ID));
                break;
            case Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                request.put(Constant.KEY_EXPERENCE_ID, map.get(Constant.KEY_EXPERENCE_ID));
                break;
            case Constant.FormType.CREATE_RESUME_CERTIFICATE_EDIT:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                request.put(Constant.KEY_CERTIFICATE_ID, map.get(Constant.KEY_CERTIFICATE_ID));
                break;
            case Constant.FormType.CREATE_RESUME_PROJECT_EDIT:
                request.put(Constant.KEY_RESUME_ID, map.get("resume_id"));
                request.put(Constant.KEY_PROJECT_ID, map.get(Constant.KEY_PROJECT_ID));
                break;
            case Constant.FormType.CREATE_RESUME_CARIOROBJECT:
                if(RESUMEID!=0){
                    request.put(Constant.KEY_RESUME_ID, RESUMEID);
                }
                break;

            case Constant.FormType.URL_CREATE_JOB:
            case Constant.FormType.CREATE_LECTURE:
            case Constant.FormType.EDIT_COURSE:
                request.put(Constant.KEY_COURSE_ID, map.get(Constant.KEY_COURSE_ID));
                break;
            case Constant.FormType.KEY_EDIT_LECTURE:
                request.put(Constant.KEY_LECTURE_ID, map.get(Constant.KEY_LECTURE_ID));
                break;
            case Constant.FormType.EDIT_CLASSROOM:
                request.put(Constant.KEY_CLASSROOM_ID, map.get(Constant.KEY_CLASSROOM_ID));
                break;
            case Constant.FormType.CREATE_QUOTE:
                request.put(Constant.KEY_MODULE, Constant.ModuleName.QUOTE);
                break;
            case Constant.FormType.CLAIM_CLASS:
                request.put(Constant.KEY_CLASSROOM_ID, map.get(Constant.KEY_CLASSROOM_ID));
                break;
            case Constant.FormType.ADD_LOCATION:
                request.put("id", map.get("id"));
                request.put("submit", 1);
                break;
            case Constant.FormType.EDIT_MUSIC_ALBUM:
                request.put(Constant.KEY_MODULE, Constant.ModuleName.MUSIC);
                request.put(Constant.KEY_ALBUM_ID, map.get(Constant.KEY_ALBUM_ID));
                break;
            case Constant.FormType.EDIT_MUSIC_PLAYLIST:
                request.put(Constant.KEY_MODULE, Constant.ModuleName.MUSIC);
                request.put(Constant.KEY_PLAYLIST_ID, map.get(Constant.KEY_PLAYLIST_ID));
                break;
            // case Constant.EDIT_ALBUM:
            case Constant.FormType.EDIT_ALBUM:
                request.put(Constant.KEY_MODULE, Constant.ModuleName.ALBUM);
                break;
         /*   case Constant.FormType.CREATE_GROUP_EVENT:
                request.put(Constant.KEY_RESOURCES_TYPE, "sesgroup_group");
                request.put(Constant.KEY_RESOURCE_ID, map.get(Constant.KEY_RESOURCE_ID));
                break;*/
         /*   case Constant.FormType.KEY_EDIT_VIDEO:
                request.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
                break;*/
            case Constant.FormType.CREATE_VIDEO:
                request.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
                if ("upload".equals(request.get("type")) || "3".equals(request.get("resource_video_type"))) {
                    request.remove("url");
                } else {
                    request.remove("rotation");
                    request.remove(Constant.FILE_TYPE + "video");
                }
                break;


            case Constant.FormType.CREATE_CONTEST:
            case Constant.FormType.CREATE_PAGE:
            case Constant.FormType.CREATE_STORE:
            case Constant.FormType.CREATE_BUSINESS:
            case Constant.FormType.CREATE_GROUP:
                if (map != null) {
                    if (map.containsKey(Constant.KEY_CATEGORY_ID)) {
                        map.remove(Constant.KEY_CATEGORY_ID);
                    }
                    request.putAll(map);
                }
                break;

            case Constant.FormType.CHANGE_NUMBER:
                request.put("step", 2);
                break;
            case Constant.FormType.STORY_ARCHIVE:
            case Constant.FormType.EDIT_ANSWER:
            case Constant.FormType.JOIN_GROUP:
            case Constant.FormType.AWARD:
            case Constant.FormType.SEO:
            case Constant.FormType.OVERVIEW:
            case Constant.FormType.RULES:
            case Constant.FormType.EDIT_CONTACT:
            case Constant.FormType.CREATE_EVENT_VIDEO:
            case Constant.FormType.CREATE_PAGE_VIDEO:
            case Constant.FormType.EDIT_ALBUM_OTHERS:
            case Constant.FormType.CREATE_ALBUM_OTHERS:
            case Constant.FormType.REPLY_TOPIC:
            case Constant.FormType.CREATE_DISCUSSTION:
            case Constant.FormType.EDIT_TOPIC:
            case Constant.FormType.CREATE_FORUM_TOPIC:
            case Constant.FormType.RENAME_FORUM_TOPIC:
            case Constant.FormType.MOVE_FORUM_TOPIC:
            case Constant.FormType.REPLY_FORUM_TOPIC:
            case Constant.FormType.QUOTE_POST:
            case Constant.FormType.PAGE_CONTACT:
            case Constant.FormType.ADD_EVENT_LIST:
            case Constant.FormType.EDIT_PAGE:
            case Constant.FormType.EDIT_STORE:
            case Constant.FormType.EDIT_BUSINESS:
            case Constant.FormType.EDIT_GROUP:
            case Constant.FormType.CREATE_TICK:
            case Constant.FormType.EDIT_EVENT:
            case Constant.FormType.EDIT_EVENT_LIST:
            case Constant.FormType.EDIT_CONTEST:
            case Constant.FormType.EDIT_ENTRY:
            case Constant.FormType.ADD_VIDEO:
            case Constant.FormType.EDIT_QUOTE:
            case Constant.FormType.EDIT_PRAYER:
            case Constant.FormType.EDIT_WISH:
            case Constant.FormType.EDIT_HOST:
            case Constant.FormType.EDIT_THOUGHT:
            case Constant.FormType.TYPE_EDIT_CHANNEL:
            case Constant.FormType.TYPE_ADD_SONG:
            case Constant.FormType.TYPE_ADD_WISHLIST:
            case Constant.FormType.TYPE_ADD_COURSE_WISHLIST:
            case Constant.FormType.TYPE_EDIT_WISHLIST:
            case Constant.FormType.TYPE_ADD_ALBUM:
            case Constant.FormType.TYPE_JOB_EDIT:
            case Constant.FormType.TYPE_BLOG_EDIT:
            case Constant.FormType.TYPE_NEWS_EDIT:
            case Constant.FormType.TYPE_RSS_EDIT:
            case Constant.FormType.TYPE_RECIPE_EDIT:
            case Constant.FormType.EDIT_CLASSIFIED:
            case Constant.FormType.EDIT_CORE_POLL:
            case Constant.FormType.TYPE_ARTICLE_EDIT:
            case Constant.FormType.KEY_EDIT_VIDEO:
            case Constant.FormType.TYPE_SONGS:
            case Constant.FormType.INVITE:
            case Constant.FormType.EDIT_SERVICE:
            case Constant.FormType.CLAIM:
            case Constant.FormType.CREATE_REVIEW:
            case Constant.FormType.EDIT_REVIEW:
            case Constant.FormType.TYPE_PLAYLIST_VIDEO:

            case Constant.FormType.EDIT_QA:
            case Constant.FormType.RESET_PASSWORD:
            case Constant.FormType.EDIT_FUND:
            case Constant.FormType.EDIT_ANNOUNCEMENT:
            case Constant.FormType.CREATE_ANNOUNCEMENT:
            case Constant.FormType.BECOME_PROFESSIONAL:
                if(map!=null){
                    request.putAll(map);
                }
              break;

        }
        return request;
    }


    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        CustomLog.e("onValueChanged", "222  " + baseFormElement.getValue() + "  222");
        if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
            String name = ((FormElementPickerSingle) baseFormElement).getName();
            if (name.equals(Constant.KEY_CATEGORY_ID)
                    || name.equals(Constant.KEY_SUB_CAT_ID)
                // || name.equals(Constant.KEY_SUB_SUB_CAT_ID)
            ) {

                String catId = (String) Util.getKeyFromValue2(commonMap.get(name), baseFormElement.getValue());
                Map<String, Object> map = getTypeByFormType();
                CustomLog.e("catId", "" + catId);
                String url;// = name.equals(Constant.KEY_CATEGORY_ID) ? Constant.URL_SUB_CATEGORY : Constant.URL_SUB_SUB_CATEGORY;

                if (name.equals(Constant.KEY_CATEGORY_ID)) {
                    map.put(Constant.KEY_CATEGORY_ID, catId);
                    url = Constant.URL_SUB_CATEGORY;
                }/*else if(name.equals(Constant.KEY_INDUSTRY_ID)){
                    map.put(Constant.KEY_INDUSTRY_ID, catId);
                    url = Constant.URL_SUB_SUB_CATEGORY;
                } */else {
                    map.put(Constant.KEY_SUB_CATEGORY_ID, catId);
                    url = Constant.URL_SUB_SUB_CATEGORY;
                }

                    callCategoryApi(map, url, baseFormElement.getTag());

            }
        }
    }

    private Map<String, Object> getTypeByFormType() {
        Map<String, Object> map = new HashMap<>();
        String type = "";
        String moduleName = "";
        switch (FORM_TYPE) {
            case Constant.FormType.TYPE_PLAYLIST_MUSIC:
            case Constant.FormType.FILTER_MUSIC_SONG:
            case Constant.FormType.TYPE_SONGS:
                type = "song";
                moduleName = Constant.VALUE_SES_MUSIC;
                break;
            case Constant.FormType.CREATE_ALBUM:
            case Constant.FormType.EDIT_ALBUM:
            case Constant.FormType.FILTER_ALBUM:
            case Constant.FormType.FILTER_PHOTO:
            case Constant.FormType.EDIT_ALBUM_SETTING:
                type = "album";
                moduleName = Constant.ModuleName.ALBUM;
                break;


            case Constant.FormType.TYPE_EDIT_CHANNEL:
            case Constant.FormType.TYPE_PLAYLIST_VIDEO:
            case Constant.FormType.CREATE_VIDEO:
            case Constant.FormType.FILTER_VIDEO:
            case Constant.FormType.KEY_EDIT_VIDEO:
            case Constant.FormType.ADD_VIDEO:
                type = "video";
                moduleName = Constant.VALUE_MODULE_VIDEO;
                break;
            case Constant.FormType.CREATE_BLOG:
            case Constant.FormType.CREATE_NEWS:
            case Constant.FormType.FILTER_BLOG:
            case Constant.FormType.TYPE_JOB_EDIT:
            case Constant.FormType.TYPE_BLOG_EDIT:
                type = Constant.ResourceType.BLOG;
                moduleName = Constant.ModuleName.BLOG;
                break;
            case Constant.FormType.TYPE_NEWS_EDIT:
                type = Constant.ResourceType.NEWS;
                moduleName = Constant.ModuleName.NEWS;
                break;
            case Constant.FormType.TYPE_RSS_EDIT:
                type = Constant.ResourceType.RSS;
                moduleName = Constant.ModuleName.RSS;
                break;
            case Constant.FormType.CREATE_RECIPE:
            case Constant.FormType.FILTER_RECIPE:
            case Constant.FormType.TYPE_RECIPE_EDIT:
                type = Constant.ResourceType.RECIPE;
                moduleName = Constant.ModuleName.RECIPE;
                break;
            case Constant.FormType.CREATE_TICK:
                type = "video";
                moduleName = "sesvideo";
                break;
            case Constant.FormType.CREATE_CLASSIFIED:
            case Constant.FormType.FILTER_CLASSIFIED:
            case Constant.FormType.EDIT_CLASSIFIED:
                type = Constant.ResourceType.CLASSIFIED;
                moduleName = Constant.ModuleName.CLASSIFIED;
                break;
            case Constant.FormType.EDIT_CORE_POLL:
                type = Constant.ResourceType.VIEW_CORE_POLL;
                moduleName = Constant.ModuleName.POLL;
                break;
            case Constant.FormType.CREATE_ARTICLE:
            case Constant.FormType.FILTER_ARTICLE:
            case Constant.FormType.TYPE_ARTICLE_EDIT:
                type = Constant.ResourceType.ARTICLE;
                moduleName = Constant.ModuleName.ARTICLE;
                break;
            case Constant.FormType.ADD_CHANNEL:
                type = Constant.ResourceType.VIDEO_CHANNEL;
                moduleName = Constant.VALUE_MODULE_VIDEO;
                break;
            case Constant.FormType.EDIT_QUOTE:
            case Constant.FormType.CREATE_QUOTE:
            case Constant.FormType.FILTER_QUOTE:
                type = Constant.ModuleName.QUOTE;
                moduleName = Constant.ModuleName.QUOTE;
                break;
            case Constant.FormType.EDIT_PRAYER:
            case Constant.FormType.CREATE_PRAYER:
            case Constant.FormType.FILTER_PRAYER:
                type = Constant.ResourceType.PRAYER;
                moduleName = Constant.ModuleName.PRAYER;
                break;
            case Constant.FormType.EDIT_WISH:
            case Constant.FormType.CREATE_WISH:
            case Constant.FormType.FILTER_WISH:
                type = Constant.ResourceType.WISH;
                moduleName = Constant.ModuleName.WISH;
                break;
            case Constant.FormType.FILTER_PAGE_REVIEW:
                type = Constant.ResourceType.PAGE_REVIEW;
                moduleName = Constant.ResourceType.PAGE_REVIEW;
                break;
            case Constant.FormType.FILTER_PRODUCT_REVIEW:
                type = Constant.ResourceType.PRODUCT_REVIEW;
                moduleName = Constant.ResourceType.PRODUCT_REVIEW;
                break;
            case Constant.FormType.FILTER_STORE_REVIEW:
                type = Constant.ResourceType.STORE_REVIEW;
                moduleName = Constant.ResourceType.STORE_REVIEW;
                break;
            case Constant.FormType.FILTER_GROUP_REVIEW:
                type = Constant.ResourceType.GROUP_REVIEW;
                moduleName = Constant.ResourceType.GROUP_REVIEW;
                break;
            case Constant.FormType.FILTER_BUSINESS_REVIEW:
                type = Constant.ResourceType.BUSINESS_REVIEW;
                moduleName = Constant.ResourceType.BUSINESS_REVIEW;
                break;
            case Constant.FormType.EDIT_THOUGHT:
            case Constant.FormType.CREATE_THOUGHT:
            case Constant.FormType.FILTER_THOUGHT:
                type = Constant.ResourceType.THOUGHT;
                moduleName = Constant.ModuleName.THOUGHT;
                break;
            case Constant.FormType.EDIT_GROUP:
            case Constant.FormType.CREATE_GROUP:
            case Constant.FormType.FILTER_GROUP:
                type = Constant.ResourceType.GROUP;
                moduleName = Constant.ModuleName.GROUP;
                break;
            case Constant.FormType.EDIT_BUSINESS:
            case Constant.FormType.CREATE_BUSINESS:
            case Constant.FormType.FILTER_BUSINESS:
                type = Constant.ResourceType.BUSINESS;
                moduleName = Constant.ModuleName.BUSINESS;
                break;
            case Constant.FormType.EDIT_EVENT:
            case Constant.FormType.CREATE_EVENT:
//            case Constant.FormType.CREATE_GROUP_EVENT:
            case Constant.FormType.FILTER_EVENT:
                type = Constant.ResourceType.SES_EVENT;
                moduleName = Constant.ModuleName.SES_EVENT;
                break;
            case Constant.FormType.CREATE_COURSE:
                type = Constant.ResourceType.COURSE;
                moduleName = Constant.ModuleName.COURSE;
                break;
            case Constant.FormType.EDIT_PAGE:
            case Constant.FormType.EDIT_STORE:
            case Constant.FormType.CREATE_PAGE:
            case Constant.FormType.CREATE_STORE:
            case Constant.FormType.FILTER_PAGE:
            case Constant.FormType.FILTER_PAGE_POLL:
                type = Constant.ResourceType.PAGE;
                moduleName = Constant.ModuleName.PAGE;
                break;
            case Constant.FormType.FILTER_STORE:
                type = Constant.ResourceType.STORE;
                moduleName = Constant.ModuleName.STORE;
                break;
            case Constant.FormType.FILTER_PRODUCT:
                type = Constant.ResourceType.PRODUCT;
                moduleName = Constant.ModuleName.PRODUCT;
                break;
            case Constant.FormType.EDIT_CONTEST:
            case Constant.FormType.CREATE_CONTEST:
            case Constant.FormType.FILTER_CONTEST:
                type = Constant.ResourceType.CONTEST;
                moduleName = Constant.ModuleName.CONTEST;
                break;
            case Constant.FormType.EDIT_QA:
            case Constant.FormType.CREATE_QA:
            case Constant.FormType.FILTER_QA:
                type = Constant.ResourceType.QA;
                moduleName = Constant.ModuleName.QA;
                break;
            case Constant.FormType.CREATE_CLASSROOM:
            case Constant.FormType.EDIT_CLASSROOM:
            case Constant.FormType.CLAIM_CLASS:
                type = Constant.ResourceType.CLASSROOM;
                moduleName = Constant.ModuleName.CLASSROOM;
                break;
            case Constant.FormType.URL_CREATE_JOB:
            case Constant.FormType.CREATE_LECTURE:
            case Constant.FormType.KEY_EDIT_LECTURE:
                type = Constant.ResourceType.LECTURE;
                moduleName = Constant.ModuleName.LECTURE;
                break;
        }

        map.put(Constant.KEY_TYPE, type);
        map.put(Constant.KEY_MODULE_NAME, moduleName);
        return map;
    }
}
