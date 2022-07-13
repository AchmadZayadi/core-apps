package com.sesolutions.ui.contest.join;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.contest.ContestItem;
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

public class ContestJoinFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private Dummy.Result result;
    private ContestItem contestVo;

    private List<Dummy.Formfields> contestRules;
    private List<Dummy.Formfields> registration;
    private List<Dummy.Formfields> uploadContent;
    private String currentSelectedTab = "-1";
    private Map<String, Dummy.Formfields> filledMap;
    private PageIndicatorView pageIndicatorView;
    private SearchVo searchVo;
    private String enabledOptionList;

    public static ContestJoinFragment newInstance(ContestItem item) {
        ContestJoinFragment fragment = new ContestJoinFragment();
        fragment.contestVo = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_join, container, false);
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
        callSignUpApi();
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.title_join_contest);
        // v.findViewById(R.id.tvHeader1).setOnClickListener(this);
        // v.findViewById(R.id.tvHeader2).setOnClickListener(this);
        // v.findViewById(R.id.tvHeader3).setOnClickListener(this);
        v.findViewById(R.id.ivNext).setOnClickListener(this);
        v.findViewById(R.id.tvNext).setOnClickListener(this);
        v.findViewById(R.id.tvPrev).setOnClickListener(this);
        v.findViewById(R.id.ivPrev).setOnClickListener(this);
        pageIndicatorView = v.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(3);

        unselectAll();
        v.findViewById(R.id.llButton).setBackgroundColor(Color.parseColor(Constant.colorPrimary));

        //all filled form values saves here
        filledMap = new HashMap<>();

        //initalizing content for each tab
        contestRules = new ArrayList<>();
        registration = new ArrayList<>();
        uploadContent = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.tvHeader1:
                    onRuleSelected();
                    break;
                case R.id.tvHeader2:
                    onRegistrationSelected();
                    break;
                case R.id.tvHeader3:
                    onContentSelected();
                    break;
                case R.id.ivPrev:
                case R.id.tvPrev:
                    switch (currentSelectedTab) {
                        case "0":
                            onBackPressed();
                            //((ContestJoinRuleFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onPrevClick();
                            break;
                        case "1":
                            ((ContestJoinRegistrationFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onPrevClick();
                            break;
                        case "2":
                            ((ContestJoinContentFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onPrevClick();
                            break;
                    }

                    break;
                case R.id.ivNext:
                case R.id.tvNext:
                    switch (currentSelectedTab) {
                        case "0":
                            ((ContestJoinRuleFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onNextClick();
                            break;
                        case "1":
                            ((ContestJoinRegistrationFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onNextClick();
                            break;
                        case "2":
                            ((ContestJoinContentFragment) getChildFragmentManager().findFragmentByTag(currentSelectedTab)).onNextClick();
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void unselectAll() {
        int colorbg = Color.parseColor(Constant.backgroundColor);
        int colorText2 = Color.parseColor(Constant.text_color_2);
        if (currentSelectedTab.equals("-1")) {
            ((ImageView) v.findViewById(R.id.ivHeader1)).setColorFilter(colorbg);
            ((TextView) v.findViewById(R.id.tvHeader1)).setTextColor(colorText2);
            ((ImageView) v.findViewById(R.id.ivHeader2)).setColorFilter(colorbg);
            ((TextView) v.findViewById(R.id.tvHeader2)).setTextColor(colorText2);
            ((ImageView) v.findViewById(R.id.ivHeader3)).setColorFilter(colorbg);
            ((TextView) v.findViewById(R.id.tvHeader3)).setTextColor(colorText2);
        } else if (currentSelectedTab.equals("0")) {
            ((ImageView) v.findViewById(R.id.ivHeader1)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            ((TextView) v.findViewById(R.id.tvHeader1)).setTextColor(Color.parseColor(Constant.white));
            ((ImageView) v.findViewById(R.id.ivHeader2)).setColorFilter(colorbg);
            ((TextView) v.findViewById(R.id.tvHeader2)).setTextColor(colorText2);
        } else if (currentSelectedTab.equals("1")) {
            ((ImageView) v.findViewById(R.id.ivHeader2)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            ((TextView) v.findViewById(R.id.tvHeader2)).setTextColor(Color.parseColor(Constant.white));
            ((ImageView) v.findViewById(R.id.ivHeader3)).setColorFilter(colorbg);
            ((TextView) v.findViewById(R.id.tvHeader3)).setTextColor(colorText2);

        } else if (currentSelectedTab.equals("2")) {
            ((ImageView) v.findViewById(R.id.ivHeader3)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            ((TextView) v.findViewById(R.id.tvHeader3)).setTextColor(Color.parseColor(Constant.white));
        }

    }

    private void onRuleSelected() {
        if (!currentSelectedTab.equals("0")) {
            pageIndicatorView.setSelection(0);
            //  ((ImageView) v.findViewById(R.id.ivHeader1)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            //  ((TextView) v.findViewById(R.id.tvHeader1)).setTextColor(Color.parseColor(Constant.white));
            currentSelectedTab = "0";
            unselectAll();
            getChildFragmentManager().beginTransaction().replace(R.id.rule_container, ContestJoinRuleFragment.newInstance(contestRules, this), currentSelectedTab).addToBackStack(null).commit();
        }
    }

    private void onRegistrationSelected() {
        if (!currentSelectedTab.equals("1")) {
            pageIndicatorView.setSelection(1);
            // ((ImageView) v.findViewById(R.id.ivHeader2)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            // ((TextView) v.findViewById(R.id.tvHeader2)).setTextColor(Color.parseColor(Constant.white));
            currentSelectedTab = "1";
            unselectAll();
            for (Dummy.Formfields fld : registration) {
                if (filledMap.containsKey(fld.getName())) {
                    fld.setStringValue(filledMap.get(fld.getName()).getValue());
                }
            }
            getChildFragmentManager().beginTransaction().replace(R.id.rule_container, ContestJoinRegistrationFragment.newInstance(registration, this), currentSelectedTab).addToBackStack(null).commit();
        }
    }

    private void onContentSelected() {
        if (!currentSelectedTab.equals("2")) {
            pageIndicatorView.setSelection(2);

            //     ((ImageView) v.findViewById(R.id.ivHeader3)).setColorFilter(Color.parseColor(Constant.colorPrimary));
            //  ((TextView) v.findViewById(R.id.tvHeader3)).setTextColor(Color.parseColor(Constant.white));
            currentSelectedTab = "2";
            unselectAll();
            getChildFragmentManager().beginTransaction().replace(R.id.rule_container, ContestJoinContentFragment.newInstance(uploadContent, this, contestVo.getContestType(), searchVo,enabledOptionList), currentSelectedTab).addToBackStack("frag3").commit();
        }
    }


    private void callSignUpApi() {

        if (isNetworkAvailable(context)) {
            showBaseLoader(false);

            HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_JOIN);

            request.params.put(Constant.KEY_CONTEST_ID, contestVo.getContestId());
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
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (err.isSuccess()) {
                            Dummy vo = new Gson().fromJson(response, Dummy.class);
                            result = vo.getResult();
                            if (null != result && result.getFormfields() != null) {
                                if(null!=result.getCustomParams()){
                                     enabledOptionList=result.getCustomParams().getAsString();

                                }
                                //adding hardcoded element "photo" for image contest type
                                if ("2".equals(contestVo.getContestType())) {
                                    Dummy.Formfields fld = result.getFormFielsByName("photo");
                                    if (null == fld) {
                                        Dummy.Formfields fl = new Dummy.Formfields();
                                        fl.setType(Constant.FILE);
                                        fl.setName("photo");
                                        result.getFormfields().add(fl);
                                    }
                                }

                                //seperate RULE data from response
                                int type = 0;
                                for (Dummy.Formfields fld : result.getFormfields()) {
                                    if (canBeRemoved(fld)) {
                                        continue;
                                    }
                                    if ("save_third".equalsIgnoreCase(fld.getName()) || "tabs_form_contest_entry_create".equals(fld.getName()) || "tabs_form_contest_entry_create_1".equals(fld.getName()) || "tabs_form_contest_entry_create_2".equals(fld.getName())) {
                                        type = 2;
                                        continue;
                                    }
                                    if ("contest_user_info".equals(fld.getName()) || "contest_basic_info".equals(fld.getName())) {
                                        fld.setType(Constant.TITLE);
                                        type = 1;
                                    }
                                    if (type == 0) {
                                        contestRules.add(fld);
                                    } else if (type == 1) {
                                        if (Constant.BUTTON.equals(fld.getType())) {
                                            //hiding all butons for registration form
                                            fld.setType(Constant.HIDDEN);
                                        }
                                        registration.add(fld);
                                    } else {
                                        uploadContent.add(fld);
                                    }
                                }
                            }
                            setUIData();
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                            goIfPermissionDenied(err.getMessage());
                        }
                    } else {
                        somethingWrongMsg(v);
                    }

                } catch (Exception e) {
                    CustomLog.e(e);
                }
                return true;
            };
            new HttpRequestHandler(activity, new Handler(callback)).run(request);
        } else

        {
            notInternetMsg(v);
        }

    }

    private boolean canBeRemoved(Dummy.Formfields fld) {
        switch (fld.getName()) {
            //for text type
            case "photouploaderentry":
            case "contest_entry_photo_file":
            case "contest_entry_main_photo_preview":
            case "removeEntryImage":
            case "removeentryimage2":

                //for photo type
            case "contest_url_photo_preview":
            case "remove_fromurl_image":
            case "photouploader":
            case "contest_main_photo_preview":
            case "removeimage":
            case "removeimage2":
            case "contest_link_photo_preview":
            case "remove_link_image":

                //for video type
                //case "remove_link_video":
                // case "contest_link_video_preview":

                return true;

            // case "sescontest_video_file":
            case "sescontest_link_id":
                fld.setType(Constant.TEXT);
                break;
            case "sescontest_audio_file":
                fld.setType(Constant.FILE);
                break;
        }
        return false;
    }

    private void setUIData() {
        v.findViewById(R.id.llButton).setVisibility(View.VISIBLE);
        v.findViewById(R.id.llHeader).setVisibility(View.VISIBLE);
        onRuleSelected();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.DECLINE:
                if (postion > 0) {
                    filledMap.putAll((Map<String, Dummy.Formfields>) object2);
                    onRuleSelected();
                } else {
                    onBackPressed();
                }
                break;
            case Constant.Events.ENTRY:
                searchVo = postion == 1 ? (SearchVo) object2 : null;
                break;
            case Constant.Events.ACCEPT:
                //save selected value and move to next tab
                //  filledMap.put("save_second_1", "1");
                filledMap.putAll((Map<String, Dummy.Formfields>) object2);
                onRegistrationSelected();
                break;
            case Constant.Events.UPDATE_NEXT:
                ((TextView) v.findViewById(R.id.tvNext)).setText("" + object2);
                break;
            case Constant.Events.UPDATE_PREV:
                ((TextView) v.findViewById(R.id.tvPrev)).setText("" + object2);
                break;
            case Constant.Events.NEXT:
                filledMap.putAll((Map<String, Dummy.Formfields>) object2);
                onContentSelected();
                break;
            case Constant.Events.MUSIC_MAIN:
                if (isNetworkAvailable(context)) {
                    filledMap.putAll((Map<String, Dummy.Formfields>) object2);
                    List<Dummy.Formfields> emptyList = result.getFormfields();
                    Map<String, String> finalMap = new HashMap<String, String>();
                    for (Dummy.Formfields vo : emptyList) {

                        try {
                            if (filledMap.containsKey(vo.getName())) {
                                if (Constant.FILE.equals(vo.getType())) {
                                    if (!TextUtils.isEmpty(filledMap.get(vo.getName()).getValue())) {
                                        finalMap.put(Constant.FILE_TYPE + vo.getName(), filledMap.get(vo.getName()).getValue());
                                    }
                                } else if (null != filledMap.get(vo.getName())) {
                                    finalMap.put(vo.getName(), filledMap.get(vo.getName()).getValue());
                                }
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                            CustomLog.e("vo.getName()", "" + vo.getName());
                            finalMap.put(vo.getName(), "");
                        }
                    }
                    finalMap.put(Constant.KEY_CONTEST_ID, "" + contestVo.getContestId());
                    callSubmitApi(finalMap);
                    //new ApiController(Constant.URL_CONTEST_JOIN, finalMap, context, this, -1).execute();
                }
                break;

        }
        return false;
    }

    private void callSubmitApi(Map<String, String> finalMap) {

        try {
            HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_JOIN);
            request.params.putAll(finalMap);
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
                            JSONObject json = new JSONObject(response);
                            if (json.get(Constant.KEY_RESULT) instanceof String) {
                                String result = json.getString(Constant.KEY_RESULT);
                                Util.showSnackbar(v, result);
                                onBackPressed();
                            } else {
                                String message = Constant.EMPTY;
                                try {
                                    message = json.getJSONObject("result").getString("success_message");
                                    activity.taskPerformed = Constant.FormType.JOIN;
                                    activity.taskId = json.getJSONObject("result").getInt("entry_id");
                                    Util.showSnackbar(v, message);
                                    onBackPressed();
                                } catch (Exception e) {
                                    FormError resp = new Gson().fromJson(response, FormError.class);
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
                        notInternetMsg(v);
                    }
                    return true;
                }
            };
            new HttpImageRequestHandler(activity, new Handler(callback), true).run(request);
        } catch (Exception e) {
            notInternetMsg(v);
            CustomLog.e(e);
        }
    }
}
