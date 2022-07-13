package com.sesolutions.ui.poll;


import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.sesolutions.utils.VibratorUtils;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateEditPollFragment extends FormHelper implements View.OnClickListener, OnUserClickedListener<Integer, Object> {
    private static final int REMOVE = 801;
    private static final int GIF = 804;
    private static final int IMAGE = 803;

    private Dummy.Result result;
    private View llQuestion;
    private Map<String, Object> map;
    private int MAX_OPTION_COUNT;
    private Map<String, String> privacyMapOptions;
    private String selectedModule;

    public static CreateEditPollFragment newInstance(int type, Map<String, Object> map, String url) {
        CreateEditPollFragment fragment = new CreateEditPollFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }

    public static CreateEditPollFragment newInstance(int type, Map<String, Object> map, String url, String selectedModule) {
        CreateEditPollFragment fragment = new CreateEditPollFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.selectedModule = selectedModule;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_create_poll, container, false);
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
        this.callSignUpApi();
    }

    private AppCompatEditText etPollTitle, etPollDesc;
    private AppCompatCheckBox cbSearch;
    private AppCompatSpinner spCommentPrivacy;

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_POLL ? R.string.title_create_poll : R.string.title_edit_poll));
        etPollTitle = v.findViewById(R.id.etPollTitle);
        etPollDesc = v.findViewById(R.id.etPollDesc);
        llOptions = v.findViewById(R.id.llOptions);
        cbSearch = v.findViewById(R.id.cbSearch);
        llQuestion = v.findViewById(R.id.llQuestion);
        spCommentPrivacy = v.findViewById(R.id.spCommentPrivacy);
        bCreate = v.findViewById(R.id.bCreate);
        bCreate.setOnClickListener(this);
        llQuestion.setOnClickListener(this);
        v.findViewById(R.id.bCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                case R.id.bCancel:
                    onBackPressed();
                    break;

                case R.id.llQuestion:
                    addOptions(null);
                    showHideAddLayout();
                    break;

                case R.id.bCreate:
                    validateInputDataAndSubmit();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private LinearLayoutCompat llOptions;

    private int createdViewCount;

    private void addOptions(PollOption opt) {
        View view = getLayoutInflater().inflate(R.layout.layout_added_poll_option, llOptions, false);
        final int currentCount = createdViewCount;
        view.setTag(currentCount);
        view.findViewById(R.id.ivRemove).setOnClickListener(v -> onItemClicked(REMOVE, null, currentCount));
        view.findViewById(R.id.ivGIF).setOnClickListener(v -> onItemClicked(GIF, null, currentCount));
        view.findViewById(R.id.ivGallery).setOnClickListener(v -> onItemClicked(IMAGE, null, currentCount));

        if (opt != null) {

            if (opt.getImageType() == 0) {
                view.findViewById(R.id.ivPollImage).setVisibility(View.GONE);
            } else {
                //selectedImageIdMap.put(currentCount, "" + opt.getFileId());
                ImageView ivPollImage = view.findViewById(R.id.ivPollImage);
                ivPollImage.setVisibility(View.VISIBLE);
                Util.showAnimatedImageWithGlide(ivPollImage, opt.getOptionImage(), context);
            }
            selectedOptionIdMap.put(currentCount, "" + opt.getPollOptionId());
            ((EditText) view.findViewById(R.id.etOption)).setText(opt.getPollOption());
        }

        llOptions.addView(view);
        createdViewCount++;
    }

    private void showHideAddLayout() {
        if (llOptions.getChildCount() < MAX_OPTION_COUNT) {
            llQuestion.setVisibility(View.VISIBLE);
        } else {
            llQuestion.setVisibility(View.GONE);
        }

        CustomLog.e("count", "" + llOptions.getChildCount());
    }


    private Map<String, Dummy.Formfields> fieldMap;

    private void callSignUpApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    request.params.put(Constant.KEY_GET_FORM, 1);
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
                                result = vo.getResult();
                                fieldMap = new LinkedHashMap<>();
                                for (Dummy.Formfields fld : result.getFormfields()) {
                                    fieldMap.put(fld.getName(), fld);
                                }
                                fetchCustomParam();
                                createFormUI();

                                // hideInitially();
                            } else {
                                notInternetMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
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

    private List<PollOption> optionList;

    private void fetchCustomParam() {
        try {
            JsonObject obj = result.getCustomParams().getAsJsonObject();
            if (FORM_TYPE == Constant.FormType.EDIT_POLL) {
                JsonArray optionArr = obj.getAsJsonArray("poll_options");
                optionList = new ArrayList<>();
                for (int i = 0; i < optionArr.size(); i++) {
                    optionList.add(new Gson().fromJson(optionArr.get(i), PollOption.class));
                }
            }
            MAX_OPTION_COUNT = obj.get("max_options").getAsInt();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void validateInputDataAndSubmit() {
        Map<String, Object> request = new HashMap<>(map);
        String title = etPollTitle.getText().toString();
        //check title
        if (fieldMap.get("title").isRequired() && TextUtils.isEmpty(title)) {
            VibratorUtils.vibrate(context);
            etPollTitle.setError(etPollTitle.getHint());
            etPollTitle.requestFocus();
            return;
        }

        request.put("title", title);

        String desc = etPollDesc.getText().toString();
        //check title
        if (fieldMap.get("description").isRequired() && TextUtils.isEmpty(desc)) {
            VibratorUtils.vibrate(context);
            etPollDesc.setError(etPollDesc.getHint());
            etPollTitle.requestFocus();
            return;
        }
        request.put("description", desc);
        //check for options
        if (llOptions.getChildCount() < 2) {
            Util.showSnackbar(llOptions, getStrings(R.string.WARNING_LESS_OPTIONS_POLL));

           /* TextInputLayout asd = v.findViewById(R.id.tiTitle);
            asd.setEnabled(true);
            asd.setError("asdhfa sdf asdlhkf ");*/
            return;
        }

        request.put("auth_comment", Util.getKeyFromValue(privacyMapOptions, spCommentPrivacy.getSelectedItem()));
        request.put("search", cbSearch.isChecked() ? "1" : "0");

        int index = 0;
        boolean isImageGifSelected = selectedImageMap.size() > 0 || selectedGifMap.size() > 0;
        for (int i = 0; i < createdViewCount; i++) {
            View view = llOptions.findViewWithTag(i);
            if (null != view) {
                String text = ((EditText) view.findViewById(R.id.etOption)).getText().toString();
                if (TextUtils.isEmpty(text)) {
                    VibratorUtils.vibrate(context);
                    ((EditText) view.findViewById(R.id.etOption)).setError(getStrings(R.string.invalid_option));
                    view.findViewById(R.id.etOption).requestFocus();
                    return;
                }

                request.put("optionsArray[" + index + "]", text);

                String path = selectedImageMap.get(i);
                String gifId = selectedGifMap.get(i);
                if (isImageGifSelected && TextUtils.isEmpty(path) && TextUtils.isEmpty(gifId)) {
                    if (!selectedOptionIdMap.containsKey(i)) {
                        VibratorUtils.vibrate(context);
                        ((EditText) view.findViewById(R.id.etOption)).setError(getString(R.string.select_image_gif));
                        view.findViewById(R.id.etOption).requestFocus();
                        return;
                    }
                }
                if (FORM_TYPE == Constant.FormType.EDIT_POLL && selectedOptionIdMap.containsKey(i)) {
                    request.put("optionIds[" + index + "]", selectedOptionIdMap.get(i));

                }
                if (!TextUtils.isEmpty(path)) {
                    request.put(Constant.FILE_TYPE + "optionsImage[" + index + "]", path);
                } else if (!TextUtils.isEmpty(gifId)) {
                    request.put("optionsGif[" + index + "]", gifId);
                } /*else if (FORM_TYPE == Constant.FormType.EDIT_POLL) {
                    request.put("optionsGif[" + index + "]", "");
                    request.put("optionsImage[" + index + "]", "");
                }*/

                index++;
            }
        }

        callSignUpApi(request);
    }

    private AppCompatButton bCreate;

    private void createFormUI() {
        //v.findViewById(R.id.tiTitle).setVisibility(fieldMap.containsKey("title") ? View.VISIBLE : View.GONE);
        // v.findViewById(R.id.tiDesc).setVisibility(fieldMap.containsKey("description") ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.rlMain).setVisibility(View.VISIBLE);
        if (fieldMap.containsKey("title")) {
            v.findViewById(R.id.tiTitle).setVisibility(View.VISIBLE);
            etPollTitle.setHint(fieldMap.get("title").getLabel());
            etPollTitle.setText(fieldMap.get("title").getValue());
        } else {
            v.findViewById(R.id.tiTitle).setVisibility(View.GONE);
        }

        if (fieldMap.containsKey("description")) {
            v.findViewById(R.id.tiDesc).setVisibility(View.VISIBLE);
            etPollDesc.setHint(fieldMap.get("description").getLabel());
            etPollDesc.setText(fieldMap.get("description").getValue());
        } else {
            v.findViewById(R.id.tiDesc).setVisibility(View.GONE);
        }

        if (fieldMap.containsKey("auth_comment")) {
            v.findViewById(R.id.llCommentPrivacy).setVisibility(View.VISIBLE);
            privacyMapOptions = fieldMap.get("auth_comment").getMultiOptions();
            ((TextView) v.findViewById(R.id.tvCommentPrivacy)).setText(fieldMap.get("auth_comment").getLabel());
            List<String> list = fieldMap.get("auth_comment").getMultiOptionsList();
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, R.layout.spinner_custom_row, list);
            spCommentPrivacy.setAdapter(spinnerAdapter);
            //spCommentPrivacy.setSe
        } else {
            v.findViewById(R.id.llCommentPrivacy).setVisibility(View.GONE);
        }

        if (fieldMap.containsKey("search")) {
            cbSearch.setVisibility(View.VISIBLE);
            cbSearch.setText(fieldMap.get("search").getLabel());
            cbSearch.setChecked(fieldMap.get("search").getValue().equals("1"));
        } else {
            cbSearch.setVisibility(View.GONE);
        }


        ((AppCompatButton) v.findViewById(R.id.bCancel)).setText(fieldMap.get("cancel").getLabel());
        bCreate.setText(fieldMap.get("submit").getLabel());

        llOptions.removeAllViews();

        //if editing a poll ,then show previous option list ,otherwise show 2 options by default
        if (null != optionList) {
            for (PollOption opt : optionList) {
                addOptions(opt);
            }
        } else {
            addOptions(null);
            addOptions(null);
        }
    }


    /*public void callSignUpApi(Map<String, Object> params) {
        try {
            if (isNetworkAvailable(context)) {
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.putAll(params);
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
                                    goBackIfValid();
                                } else {
                            }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    private Map<Integer, String> selectedImageMap = new HashMap<>();
    //   private Map<Integer, String> selectedImageIdMap = new HashMap<>();
    private Map<Integer, String> selectedOptionIdMap = new HashMap<>();
    private Map<Integer, String> selectedGifMap = new HashMap<>();

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        if (null != result) {
            String filePath = ((List<String>) result).get(0);

            //add selected value in map
            selectedImageMap.put(fetchingDataForViewAt, filePath);

            //show selected image at corresponding ImageView
            ImageView ivPollImage = llOptions.findViewWithTag(fetchingDataForViewAt).findViewById(R.id.ivPollImage);
            ivPollImage.setVisibility(View.VISIBLE);
            ivPollImage.setImageDrawable(Drawable.createFromPath(filePath));

        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    private int fetchingDataForViewAt;

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case REMOVE:
                llOptions.removeView(llOptions.findViewWithTag(position));
                selectedImageMap.remove(position);
                selectedGifMap.remove(position);
                showHideAddLayout();
                break;
            case IMAGE:
                MAX_COUNT = 1;
                fetchingDataForViewAt = position;
//                showImageDialog(getString(R.string.MSG_CHOOSE_SOURCE));
                askForPermission(Manifest.permission.CAMERA);
                break;
            case GIF:
                SelectGifDialog.newInstance(this, position, selectedModule).show(fragmentManager, "GIF");
                break;
            case Constant.Events.POPUP:
                SearchVo vo = (SearchVo) data;
                selectedGifMap.put(position, "" + vo.getFileId());
                ImageView view = (ImageView) llOptions.findViewWithTag(position).findViewById(R.id.ivPollImage);
                view.setVisibility(View.VISIBLE);
                Util.showAnimatedImageWithGlide(view, vo.getImages(), context);
                break;
        }
        return false;
    }
}
