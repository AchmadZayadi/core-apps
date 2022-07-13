package com.sesolutions.ui.qna;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.qna.FormCustomParam;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateEditQAFragment extends FormHelper implements View.OnClickListener, OnUserClickedListener<Integer, List<String>> {
    private static int MAX_OPTION_COUNT;//}, ParserCallbackInterface {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private List<String> questionOptionList;

   /* public static CreateEditPageFragment newInstance(int formType, String url, int categoryId) {
        CreateEditPageFragment frag = new CreateEditPageFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static CreateEditQAFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result) {
        CreateEditQAFragment fragment = new CreateEditQAFragment();
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
        questionOptionList = new ArrayList<>();
        /*calling api only if no response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            applyCustomChange();
            createFormUi(result);
            hideInitially();
        }
    }

    public void applyCustomChange() {

        if (null != result.getCustomParams()) {
            FormCustomParam customParam = result.getCustomParams(FormCustomParam.class);
            if (null != customParam.getOptions()) {
                questionOptionList.addAll(customParam.getOptions());
                MAX_OPTION_COUNT = customParam.getMaxOptions();
            }
        }
        Dummy.Formfields fld = result.getFormFielsByName("poll_question");
        if (null != fld) {
            fld.setType(Constant.GROUP_QUESTION);
            fld.setLabel(getString(R.string.add_options));
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_QA ? R.string.title_create_question : R.string.title_edit_question));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    @Override
    public Map<String, Object> fetchFormValue() {
        Map<String, Object> map = super.fetchFormValue();
        // if (map.containsKey("approval") && "1".equals(map.get("approval"))) {
        if (questionOptionList.size() > 0) {
            map.put("is_poll", 1);
            for (int i = 0; i < questionOptionList.size(); i++) {
                if (!TextUtils.isEmpty(questionOptionList.get(i))) {
                    map.put("optionsArray[" + i + "]", questionOptionList.get(i));
                }
            }
        } else {
            map.put("is_poll", 0);
        }
        //}
        return map;
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("mediatype")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());

                    if (null != key) {
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("photo")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !(key.equals("1")));
                            }
                            if (name.equals("video")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !(key.equals("2")));
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
        String value = "";
        int tagMedia = 0;
        try {
            for (int i = 0; i < tagList.size(); i++) {
                int tag = 1011 + i;
                //key = tagList.get(i);
                String name = tagList.get(i);
                if (name.equals("mediatype")) {
                    tagMedia = tag;
                    value = mFormBuilder.getFormElement(tag).getValue();
                }
                if (name.equals("photo")) {
                    mFormBuilder.getAdapter().setHiddenAtTag(tag, !("1".equals(value)));
                }
                if (name.equals("video")) {
                    mFormBuilder.getAdapter().setHiddenAtTag(tag, !("2".equals(value)));
                }
            }
            if (TextUtils.isEmpty(value)) {
                mFormBuilder.getAdapter().setHiddenAtTag(tagMedia, true);
            }
        } catch (Exception e) {
            CustomLog.e(e);
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
                                    result = vo.getResult();
                                    //result.getFormFielsByName("networks").setType(Constant.MULTI_CHECKBOX);
                                    applyCustomChange();
                                    createFormUi(result);
                                    hideInitially();
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


    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        if (reqCode == FORM_TYPE) {
            //show add question popup
            AddPollOptionsDialog.newInstance(questionOptionList, this, (int) result, MAX_OPTION_COUNT).show(getChildFragmentManager(), Constant.GROUP_QUESTION);
        } else if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }


    @Override
    public boolean onItemClicked(Integer eventType, List<String> data, int position) {
        switch (eventType) {
            case Constant.Events.POPUP:
                questionOptionList = data;
                break;
        }
        return false;
    }
}
