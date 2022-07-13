package com.sesolutions.ui.member;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import org.apache.http.client.methods.HttpPost;

import java.util.List;
import java.util.Map;

public class MemnerFilterFormFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;

    /*  private View v;*/
/*    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;

    private List<String> tagList;
    private List<Dummy.Formfields> formList;
    private Map<String, Map<String, String>> commonMap;*/
    private AppCompatTextView tvTitle;
    /*    private int FORM_TYPE;*/
/*    private String url;*/
    private int albumId;

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
            applyTheme(v);

            init();
            callSignUpApi();
            //   printKeyStore();
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
        tvTitle = v.findViewById(R.id.tvTitle);
        setTitle();
        //   bConitinue.setVisibility(View.GONE);
        //  cbTnC = v.findViewById(R.id.cbTnC);
        //  cbTnC.setOnCheckedChangeListener(this);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    private void setTitle() {
        String title = "Form";
        switch (FORM_TYPE) {

            case Constant.FormType.FILTER_MEMBER:
                title = (Constant.TITLE_FILTER_SEARCH);
                break;
        }
        tvTitle.setText(title);
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
                   /* if (FORM_TYPE == Constant.EDIT_ALBUM) {
                        request.params.put(Constant.KEY_ALBUM_ID, albumId);
                    } else*/
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    request.params.put(Constant.KEY_GET_FORM, 1);

                    request.params.put(Constant.KEY_AUTH_TOKEN, Constant.EMPTY);
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
                                    if (null != vo.getResult()) {
                                        //List<Dummy.Formfields> list = vo.getResult().getFormfields();
                                        /*List<Dummy.Formfields> list2 = new ArrayList<>();
                                        boolean nowRemove = false;
                                        for (int i = 0; i < list.size() - 1; i++) {
                                            Dummy.Formfields fr = list.get(i);
                                            list2.add(fr);
                                            if (fr.getName().equals("is_vip")) {
                                                list2.add(list.get(list.size() - 1));
                                                break;
                                            }
                                        }*/
                                        //vo.getResult().setFormfields(vo.getResult().getFormfields());
                                        createFormUi(vo.getResult());
                                    }

                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
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
        // (List<String>) result;
        if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public static MemnerFilterFormFragment newInstance(int editAlbum, Map<String, Object> map, String url, int albumId) {
        MemnerFilterFormFragment fragment = new MemnerFilterFormFragment();
        fragment.FORM_TYPE = editAlbum;
        fragment.url = url;
        fragment.map = map;
        fragment.albumId = albumId;
        return fragment;
    }

    public static MemnerFilterFormFragment newInstance(int editAlbum, Map<String, Object> map, String url) {
        return newInstance(editAlbum, map, url, 0);
    }
}
