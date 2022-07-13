package com.sesolutions.ui.events;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateEditEventFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {


    private Dummy.Result result;
    private Map<String, Integer> nameTagList;

    public static CreateEditEventFragment newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        CreateEditEventFragment frag = new CreateEditEventFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
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
        this.callSignUpApi();
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(FORM_TYPE == Constant.FormType.CREATE_EVENT ? R.string.title_create_event : R.string.BECOME_PROFESSIONAL);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            String name = (baseFormElement).getName();
            boolean hasToUpdate = false;
            if (null != name) {
                switch (name) {
                    case "is_custom_term_condition":
                        hasToUpdate = true;
                        boolean hideShow = "1".equals("" + baseFormElement.getValue());
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("custom_term_condition"), !hideShow);
                        break;

                    case "include_social_links":
                        hasToUpdate = true;
                        hideShow = "1".equals("" + baseFormElement.getValue());
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("facebook_url"), !hideShow);
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("twitter_url"), !hideShow);
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("website_url"), !hideShow);
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("linkdin_url"), !hideShow);
                        mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("googleplus_url"), !hideShow);
                        break;

                    case "choose_host":
                        hasToUpdate = true;
                        String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());

                        if ("new".equals(key)) {
                            // this means user choose "create new host"

                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_type"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("host_type"), "");
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("event_host"), "");
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("selectonsitehost"), "");


                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_name"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_email"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_phone"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_description"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_photo"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("include_social_links"), false);

                            mFormBuilder.getAdapter().setValueAtTag(getTagIdByName("include_social_links"), "0");

                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("facebook_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("twitter_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("website_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("linkdin_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("googleplus_url"), true);
                        } else if ("choose_host".equals(key)) {
                            // this means user choose "select from existing user"

                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_type"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("event_host"), "");
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("selectonsitehost"), "");
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);

                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_name"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_email"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_phone"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_description"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_photo"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("include_social_links"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("facebook_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("twitter_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("website_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("linkdin_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("googleplus_url"), true);

                        } else {
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_type"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);

                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("host_type"), "");
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("event_host"), "");
                            mFormBuilder.getAdapter().setValueAtTagNonRefresh(getTagIdByName("selectonsitehost"), "");

                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_name"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_email"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_phone"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_description"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("host_photo"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("include_social_links"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("facebook_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("twitter_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("website_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("linkdin_url"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("googleplus_url"), true);
                        }
                        break;

                    //case "selecthost":
                    case "host_type":
                        hasToUpdate = true;
                        key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());
                        if ("offsite".equals(key)) {
                            //this means user selects "off site" option
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), false);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);
                        } else if ("site".equals(key)) {
                            //this means user selects "on site" option
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), false);
                        } else if ("myself".equals(key)) {
                            //this means user selects "Myself" option
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);
                        } else {
                            //this means user selects nothing
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("event_host"), true);
                            mFormBuilder.getAdapter().setHiddenAtTag(getTagIdByName("selectonsitehost"), true);
                        }
                        break;
                }
            }

            if (hasToUpdate) {
                mFormBuilder.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private int getTagIdByName(String name) {
        try {
            return nameTagList.get(name);
        } catch (Exception e) {
            CustomLog.e("tag_id", "not tag for" + name);
            return -1;
        }
    }

    private void hideInitially() {
        nameTagList = new HashMap<>();
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            nameTagList.put(tagList.get(i), tag);
            //key = tagList.get(i);
            String name = tagList.get(i);
            switch (name) {

                case "custom_term_condition":

                    // case "choose_host": //used to choose host ["create new "or "select from existing"]
                    //   case "host_type": // choose host type
                case "event_host":  //offsite hosts list
                case "selectonsitehost":  //onsite hosts list

                    //new host creation fields
                case "host_name":
                case "host_phone":
                case "host_email":
                case "host_description":
                case "host_photo":
                case "include_social_links":
                case "facebook_url":
                case "twitter_url":
                case "website_url":
                case "linkdin_url":
                case "googleplus_url":
                    CustomLog.e("isEmail", "" + name.equals("host_email"));
                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                    break;
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }

    @Override
    public Map<String, Object> fetchFormValue() {
        Map<String, Object> request = super.fetchFormValue();
        /*if (request.containsKey("include_social_links")) {
            if ("0".equals(request.get("include_social_links"))) {
                if (request.containsKey("facebook_url"))
                    request.remove("facebook_url");
                if (request.containsKey("twitter_url"))
                    request.remove("twitter_url");
                if (request.containsKey("website_url"))
                    request.remove("website_url");
                if (request.containsKey("linkdin_url"))
                    request.remove("linkdin_url");
                if (request.containsKey("googleplus_url"))
                    request.remove("googleplus_url");
            }
        }

        if (request.containsKey("choose_host")) {
            int itemPosition=((FormElementPickerSingle) mFormBuilder.getFormElement(nameTagList.get("choose_host"))).getItemPosition();
            if (itemPosition)) {
                if (request.containsKey("facebook_url"))
                    request.remove("facebook_url");
                if (request.containsKey("twitter_url"))
                    request.remove("twitter_url");
                if (request.containsKey("website_url"))
                    request.remove("website_url");
                if (request.containsKey("linkdin_url"))
                    request.remove("linkdin_url");
                if (request.containsKey("googleplus_url"))
                    request.remove("googleplus_url");
            }
        }*/
        Set<Map.Entry<String, Object>> set = request.entrySet();
        List<String> str = new ArrayList<>();
        for (Map.Entry<String, Object> vo : set) {
            if (null == vo.getValue()) {
                CustomLog.e("Entry", vo.getKey() + "_____" + vo.getValue());
                str.add(vo.getKey());
                //request.remove(vo.getKey());
            }
        }
        for (String s : str) {
            request.put(s, "");
        }
        set = request.entrySet();
        for (Map.Entry<String, Object> vo : set) {
            CustomLog.e("Entry", vo.getKey() + "__2___" + vo.getValue());
        }

        return request;
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
                    if (null != map) {
                        request.params.putAll(map);
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
                                        createFormUi(result);
                                        hideInitially();
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                        goIfPermissionDenied(vo.getError());
                                    }
                                } else {
                                    somethingWrongMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                                somethingWrongMsg(v);
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

    public static CreateEditEventFragment newInstance(int type, Map<String, Object> map, String url) {
        CreateEditEventFragment fragment = new CreateEditEventFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }

}
