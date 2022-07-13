package com.sesolutions.ui.groups;


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

public class CreateEditGroupFragment extends FormHelper implements View.OnClickListener, OnUserClickedListener<Integer, List<Dummy.Formfields>> {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private List<Dummy.Formfields> groupQuestionList;

   /* public static CreateEditGroupFragment newInstance(int formType, String url, int categoryId) {
        CreateEditGroupFragment frag = new CreateEditGroupFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static CreateEditGroupFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result) {
        CreateEditGroupFragment fragment = new CreateEditGroupFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.result = result;
        return fragment;
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
        groupQuestionList = new ArrayList<>();
        /*calling api only if no response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            applyCustomChange();
            createFormUi(result);
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_GROUP ? R.string.title_create_group : R.string.title_edit_group));
        mRecyclerView = v.findViewById(R.id.recyclerView);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (baseFormElement.getName().equals("can_join")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());

                    if (null != key) {
                        boolean hideShow = key.equals("1");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("member_title_plural") || name.equals("member_title_singular")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideShow);
                            }
                        }
                    }

                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
                else if (baseFormElement.getName().equals("approval")) {
                    String key = Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());

                    if (null != key) {
                        boolean hideShow = key.equals("0");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("join_question")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, hideShow);
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

   /* private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("member_title_singular") || name.equals("member_title_plural")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }*/

    private void callSignUpApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    if (null != map) {
                        request.params.putAll(map);
                        map.remove(Constant.KEY_CATEGORY_ID);
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

                                    applyCustomChange();

                                    createFormUi(result);
                                    // hideInitially();
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

    private void applyCustomChange() {
        //apply Custom Change in FormData
        for (Dummy.Formfields fld : result.getFormfields()) {
            if ("join_question".equals(fld.getName())) {
                fld.setType(Constant.GROUP_QUESTION);
                fld.setLabel(getStrings(R.string.add_questions));
            } else if (fld.getName().startsWith("questitle")) {
                groupQuestionList.add(fld);
            }
        }
    }


    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        if (reqCode == FORM_TYPE) {
            //show add question popup
            AddQuestionDialogFragment.newInstance(groupQuestionList, this, (int) result).show(getChildFragmentManager(), Constant.GROUP_QUESTION);
        } else if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public Map<String, Object> fetchFormValue() {
        Map<String, Object> map = super.fetchFormValue();
        if (map.containsKey("approval") && "1".equals(map.get("approval"))) {
            for (Dummy.Formfields fld : groupQuestionList) {
                if (!TextUtils.isEmpty(fld.getValue())) {
                    map.put(fld.getName(), fld.getValue());
                }
            }
        }

        //Log.e("Name: "+fld.getName(),"Tavalue: "+fld.getValue());

        return map;
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }


    @Override
    public boolean onItemClicked(Integer eventType, List<Dummy.Formfields> data, int position) {
        switch (eventType) {
            case Constant.Events.POPUP:
                groupQuestionList = data;
                break;
        }
        return false;
    }
}
