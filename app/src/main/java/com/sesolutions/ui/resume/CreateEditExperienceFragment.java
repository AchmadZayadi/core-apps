package com.sesolutions.ui.resume;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

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

import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;

public class CreateEditExperienceFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private Boolean showToolbar;
   /* public static CreateEditPageFragment newInstance(int formType, String url, int categoryId) {
        CreateEditPageFragment frag = new CreateEditPageFragment();
        frag.url = url;subsubcategory
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static CreateEditExperienceFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result) {
        CreateEditExperienceFragment fragment = new CreateEditExperienceFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.result = result;
        return fragment;
    }

    public static CreateEditExperienceFragment newInstance(int type, Map<String, Object> map, String url, Dummy.Result result, Boolean showToolbar) {
        CreateEditExperienceFragment fragment = new CreateEditExperienceFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        fragment.result = result;
        fragment.showToolbar = showToolbar;
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
        /*calling api only if no response is coming from previous screen*/
        if (result == null) {
            this.callSignUpApi();
        } else {
            createFormUi(result);
            hideInitially();
        }
    }

    private void init() {

        v.findViewById(R.id.ivBack).setOnClickListener(this);

        if(FORM_TYPE== Constant.FormType.CREATE_RESUME_EXPRIENCE){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_exprience));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_EDUCATION){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_education));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_PROJECT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_projects));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_REFERENCE){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_reference));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_REFERENCE_EDIT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_reference_edit));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_PROJECT_EDIT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_project_edit));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_CERTIFICATE_EDIT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_certificate_edit));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_experience_edit));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_EDUCATION_EDIT){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_education_edit));
        }else if(FORM_TYPE== Constant.FormType.CREATE_RESUME_CERTIFICATE){
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_certificate_add));
        }




        mRecyclerView = v.findViewById(R.id.recyclerView);
        if(!showToolbar){
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        }
    }

    boolean isflag=false;
    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);

        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (baseFormElement.getName().equals("can_join")) {
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

                            if(key.equalsIgnoreCase("school")){
                                if (name.equals("toyear")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                                }
                            }
                            if(key.equalsIgnoreCase("university")){
                                if (name.equals("fromyear") || name.equals("toyear")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                                }
                            }

                        }
                    }
                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
                else {
                    String key = Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());
                    if(key.equalsIgnoreCase("school") || key.equalsIgnoreCase("university")){
                        boolean hideShow = key.equals("1");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                             if (name.equals("toyear")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                             }
                            if (name.equals("field_of_study")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                            }


                            if(key.equalsIgnoreCase("university")){
                                if (name.equalsIgnoreCase("fromyear") || name.equalsIgnoreCase("toyear") || name.equalsIgnoreCase("field_of_study")) {
                                    mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                                }


                                if (name.equalsIgnoreCase("school")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("University");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("University");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                } else  if (name.equalsIgnoreCase("degree")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("Degree");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("Degree");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }else  if (name.equalsIgnoreCase("fromyear")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("From Year");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("From Year");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                                else {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName(name);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }


                             }else {

                                if (name.equalsIgnoreCase("school")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("School");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("School");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }else  if (name.equalsIgnoreCase("degree")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("Stream");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("Stream");
                                     }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }else  if (name.equalsIgnoreCase("fromyear")) {
                                    try {
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("Which Year");
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("Which Year");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                                else {
                                    try {
                                    //    mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName(name);
                                        mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName(name);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        mFormBuilder.getAdapter().notifyDataSetChanged();
                    }
                }
            }

            if (BaseFormElement.TYPE_CHECKBOX == baseFormElement.getType()) {
                if(FormHelper.isCurrentllywork==1){
                    isflag=false;
                    FormHelper.isCurrentllywork=0;
                }else {
                    isflag=!isflag;
                }

                   // String key = Util.getKeyFromValue21(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());
                if(baseFormElement.getName().equalsIgnoreCase("currentlywork")){
                    for (int i = 0; i < tagList.size(); i++) {
                        int tag = 1011 + i;
                        //key = tagList.get(i);
                        String name = tagList.get(i);
                        if (name.equals("tomonth") || name.equals("toyear")) {
                            mFormBuilder.getAdapter().setHiddenAtTag(tag, isflag);
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

    boolean isboolean=true;
    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("member_title_singular") || name.equals("member_title_plural")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }

            try {

                if(FORM_TYPE== Constant.FormType.CREATE_RESUME_PROJECT || FORM_TYPE== Constant.FormType.CREATE_RESUME_PROJECT_EDIT){
                    isboolean=false;
                }
                if(mFormBuilder.getAdapter().getValueByName(name)!=null && !mFormBuilder.getAdapter().getValueByName(name).equalsIgnoreCase("University")){
                     if(( name.equals("toyear") || name.equalsIgnoreCase("field_of_study")) && isboolean){
                         mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                     }else {
                         mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                     }

                    if (name.equalsIgnoreCase("school")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("school");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("school");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }else  if (name.equalsIgnoreCase("degree")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("stream");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("stream");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }else  if (name.equalsIgnoreCase("fromyear")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("Which Year");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("Which Year");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    else {
                        try {
                             mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName(name);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }

                }
                else {
                    isboolean=false;
                    if (name.equalsIgnoreCase("school")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("University");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("University");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    } else  if (name.equalsIgnoreCase("degree")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("Degree");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("Degree");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }else  if (name.equalsIgnoreCase("fromyear")) {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setTitle("From Year");
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName("From Year");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    else {
                        try {
                            mFormBuilder.getAdapter().getValueAtIndex(tag-1011).setName(name);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }

                    mFormBuilder.getAdapter().setHiddenAtTag(tag, false);
                }
            }catch (Exception ex){
                ex.printStackTrace();
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
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        result = vo.getResult();
                                        //result.getFormFielsByName("networks").setType(Constant.MULTI_CHECKBOX);
                                        createFormUi(result);
                                        hideInitially();
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                        new Handler().postDelayed(() -> {
                                                onBackPressed();
                                        }, 2500);
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
