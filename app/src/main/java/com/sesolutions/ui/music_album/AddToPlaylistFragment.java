package com.sesolutions.ui.music_album;


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

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
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
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class AddToPlaylistFragment extends FormHelper implements View.OnClickListener {

    private AppCompatTextView tvTitle;
    private Dummy.Result result;
    private int notHideItemFromBottom = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);
            init();
            callSignUpApi();
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
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

    private void setTitle() {
        int id = R.string.EMPTY;
        switch (FORM_TYPE) {
            case Constant.FormType.TYPE_ADD_SONG:
                id = (R.string.TITLE_ADD_SONG);
                break;
            case Constant.FormType.TYPE_ADD_ALBUM:
                id = (R.string.TITLE_ADD_ALBUM);
                break;
            case Constant.FormType.ADD_VIDEO:
                id = (R.string.TITLE_ADD_VIDEO);
                break;
            case Constant.FormType.ADD_EVENT_LIST:
                id = (R.string.add_event_list);
                break;
            case Constant.FormType.CREATE_ALBUM_OTHERS:
                notHideItemFromBottom = 2;
                id = (R.string.TAB_TITLE_ALBUM_5);
                break;
            case Constant.FormType.TYPE_ADD_WISHLIST:
                id = (R.string.TITLE_ADD_WISHLIST);
                break;
            case Constant.FormType.TYPE_ADD_COURSE_WISHLIST:
                id = (R.string.TITLE_ADD_WISHLIST_COURSE);
                break;
            case Constant.FormType.TYPE_EDIT_WISHLIST:
                id = (R.string.TITLE_EDIT_WISHLIST);
                break;

        }
        tvTitle.setText(id);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                CustomLog.e("onValueChanged", "111111");
                String key = (String) Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());
                if (null != key) {
                    boolean hideOrShow = (key.equals("0") || key.equals(""));
                    for (int i = 1; i < formItems.size() - notHideItemFromBottom; i++) {
                        mFormBuilder.getAdapter().getDataset().get(i).setHidden(!hideOrShow);
                    }
                    mFormBuilder.getAdapter().notifyDataSetChanged();
                    //  mFormBuilder.getAdapter().notifyItemRangeRemoved(1, result.getFormfields().size() - 2);
                } /*else {
                    createFormUi(result);
                    mFormBuilder.getAdapter().setValueAtIndex(0, baseFormElement.getValue());
                }*/
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
//                    request.params.put(Constant.KEY_AUTH_TOKEN, "1641b1b8453a1ccc1555046244");
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
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        result = vo.getResult();
                                     /*   if (Constant.FormType.ADD_EVENT_LIST == FORM_TYPE) {
                                            Dummy.Formfields fld = result.getFormFielsByName("list_id");
                                            if (null != fld) {
                                                JsonElement element = fld.getOptionAsJson();
                                                if (null != element && !element.isJsonObject()) {
                                                    Map<String, String> multiOption = new LinkedHashMap<>();
                                                    List<String> option = new Gson().fromJson(element.toString(), List.class);
                                                    if (option.size() == 1) {
                                                        multiOption.put("0", option.get(0));
                                                    }

                                                    for (Dummy.Formfields fldd : result.getFormfields()) {
                                                        fldd.setMultiOptions(new Gson().toJsonTree(multiOption));
                                                    }
                                                }
                                            }
                                        } *//*else if (Constant.FormType.CREATE_ALBUM_OTHERS == FORM_TYPE) {
                                            try {
                                                Dummy.Formfields fld = result.getFormFielsByName("album");
                                                String label = fld.getMultiOptions().get("0");
                                                List<String> list = new ArrayList<String>();
                                                list.add(label);
                                                fld.setMultiOptions(new Gson().toJsonTree(list));
                                            } catch (Exception e) {
                                                CustomLog.e(e);
                                            }
                                        }*/

                                        if (Constant.FormType.CREATE_ALBUM_OTHERS == FORM_TYPE) {
                                            for (Dummy.Formfields fld : result.getFormfields()) {
                                                if ("album_photo".equals(fld.getName())) {
                                                    fld.setName("image");
                                                }
                                            }
                                        }
                                        if (Constant.FormType.TYPE_ADD_COURSE_WISHLIST == FORM_TYPE) {
                                            for (Dummy.Formfields fld : result.getFormfields()) {
                                                if ("mainphoto".equals(fld.getName())) {
                                                    fld.setName("file_type_mainphoto");
                                                }
                                            }
                                        }

                                        createFormUi(result);
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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

    public static AddToPlaylistFragment newInstance(int type, Map<String, Object> map, String url) {
        AddToPlaylistFragment fragment = new AddToPlaylistFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }

}
