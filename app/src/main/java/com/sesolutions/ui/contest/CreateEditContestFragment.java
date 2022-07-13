package com.sesolutions.ui.contest;


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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateEditContestFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private HashMap<String, Integer> nameTagList;

   /* public static CreateEditPageFragment newInstance(int formType, String url, int categoryId) {
        CreateEditPageFragment frag = new CreateEditPageFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static CreateEditContestFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result) {
        CreateEditContestFragment fragment = new CreateEditContestFragment();
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
        /*calling api only if null response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            createFormUi(result);
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_CONTEST ? R.string.title_create_contest : R.string.title_edit_contest));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("vote_type")) {
                    String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());

                    if (null != key) {
                        boolean hideShow = key.equals("1");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            String name = tagList.get(i);
                            if (name.equals("result_date") || name.equals("result_time")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideShow);
                            }
                        }
                    }
                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            } else if ("end_date".equals(baseFormElement.getName())) {
                CustomLog.e("end_date",baseFormElement.getName());
                mFormBuilder.getAdapter().setValueAtTag(getTagIdByName("result_date"),baseFormElement.getValue());
                mFormBuilder.getAdapter().notifyDataSetChanged();
            } else if ("end_time".equals(baseFormElement.getName())) {
                CustomLog.e("end_time",baseFormElement.getName());
                mFormBuilder.getAdapter().setValueAtTag(getTagIdByName("result_time"),baseFormElement.getValue());
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
        nameTagList = new HashMap<>();
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            nameTagList.put(tagList.get(i), tag);
            String name = tagList.get(i);
            if (name.equals("result_date") || name.equals("result_time")) {
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
                                    result = vo.getResult();
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


}
