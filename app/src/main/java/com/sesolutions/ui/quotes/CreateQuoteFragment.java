package com.sesolutions.ui.quotes;


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
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

public class CreateQuoteFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;
    private AppCompatTextView tvTitle;
    private Dummy.Result result;

    public static CreateQuoteFragment newinstance(int formType, String url, OnUserClickedListener<Integer, Object> listener) {
        CreateQuoteFragment frag = new CreateQuoteFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = listener;
        /* if (formType == Constant.FormType.CREATE_ALBUM) {
            frag.module = Constant.VALUE_MODULE_ALBUM;
        }*/
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
            if (FORM_TYPE == Constant.FormType.CREATE_WISH
                    || FORM_TYPE == Constant.FormType.CREATE_EVENT) {
                initScreenData();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        this.callSignUpApi();
    }

   /* @Override
    public void callSignUpApi(Map<String, Object> params) {

       *//* if (params.containsKey("mediatype")) {
            String mediatype = (String) params.get("mediatype");
        }*//*

        //mediatype==2 means user selected video source
        if (((String) params.get("mediatype")).equals("2")) {
            String uri = (String) params.get("video");
            if (!TextUtils.isEmpty(uri)) {
                validateVideoUrl(uri);
                return;
            }
        }
        super.callSignUpApi(params);

    }*/

    private void init() {
        if (FORM_TYPE == Constant.FormType.CREATE_WISH) {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_wish));
            //   setTitle();
        } else if (FORM_TYPE == Constant.FormType.CREATE_EVENT) {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.title_create_event));
            //   setTitle();
        }
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    }

   /* private void setTitle() {
        String title = "Form";
        switch (FORM_TYPE) {
            case Constant.FormType.TYPE_ADD_SONG:
                title = (Constant.TITLE_ADD_SONG);
                break;
            case Constant.FormType.TYPE_ADD_ALBUM:
                title = (Constant.TITLE_ADD_ALBUM);
                break;
            case Constant.FormType.ADD_VIDEO:
                title = (Constant.TITLE_ADD_VIDEO);
                break;
        }
        tvTitle.setText(title);
    }*/

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                String name = ((FormElementPickerSingle) baseFormElement).getName();
                if (name.equals("mediatype")) {
                    CustomLog.e("onValueChanged", "111111");
                    String key = (String) Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());
                    if (null != key) {
                        boolean hideOrShow = (key.equals("1") || key.equals(""));
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name2 = tagList.get(i);
                            if (name2.equals("photo")) {
                                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideOrShow);
                                //  break;
                            } else if (name2.equals("video")) {
                                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, hideOrShow);
                            }
                        }
                        mFormBuilder.getAdapter().notifyDataSetChanged();
                        //  mFormBuilder.getAdapter().notifyItemRangeRemoved(1, result.getFormfields().size() - 2);
                    } /*else {
                    createFormUi(result);
                    mFormBuilder.getAdapter().setValueAtIndex(0, baseFormElement.getValue());
                }*/
                } else if (name.equals("posttype")) {
                    CustomLog.e("onValueChanged", "121212");
                    String key = (String) Util.getKeyFromValue(commonMap.get(name), baseFormElement.getValue());
                    if (null != key) {
                        boolean isNetworkHidden = true;
                        boolean isListHidden = true;
                        boolean isMemberHidden = true;
                        switch (key) {
                            case "1":
                                isNetworkHidden = true;
                                isListHidden = true;
                                isMemberHidden = true;
                                break;
                            case "2":
                                isNetworkHidden = false;
                                isListHidden = true;
                                isMemberHidden = true;
                                break;
                            case "3":
                                isNetworkHidden = true;
                                isListHidden = false;
                                isMemberHidden = true;
                                break;
                            case "4":
                                isNetworkHidden = true;
                                isListHidden = true;
                                isMemberHidden = false;

                                break;
                        }
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name2 = tagList.get(i);
                            if (name2.equals("networks")) {
                                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, isNetworkHidden);
                                //  mFormBuilder.getAdapter().setHiddenAtTag(tag + 900, isNetworkHidden);
                                //  break;
                            } else if (name2.equals("lists")) {
                                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, isListHidden);
                                //  mFormBuilder.getAdapter().setHiddenAtTag(tag + 900, isListHidden);
                            } else if (name2.equals("user_id")) {
                                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, isMemberHidden);
                                //  mFormBuilder.getAdapter().setHiddenAtTag(tag + 900, isMemberHidden);
                            }
                        }
                        mFormBuilder.getAdapter().notifyDataSetChanged();
                        //  mFormBuilder.getAdapter().notifyItemRangeRemoved(1, result.getFormfields().size() - 2);
                    } /*else {
                    createFormUi(result);
                    mFormBuilder.getAdapter().setValueAtIndex(0, baseFormElement.getValue());
                }*/
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
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name2 = tagList.get(i);
            if (name2.equals("video")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            } else if (name2.equals("networks")) {
                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                mFormBuilder.getAdapter().setHiddenAtTag(tag + 1, true);
                //  break;
            } else if (name2.equals("lists")) {
                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                mFormBuilder.getAdapter().setHiddenAtTag(tag + 1, true);
            } else if (name2.equals("user_id")) {
                mFormBuilder.getAdapter().setValueAtTag(tag, "");
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
                mFormBuilder.getAdapter().setHiddenAtTag(tag + 1, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }

    private void validateVideoUrl(String uri) {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VALIDATE_QUOTE_VIDEO);

                    request.params.put(Constant.KEY_URI, uri);
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
                                    /*Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    result = vo.getResult();
                                    createFormUi(result);
                                    hideInitially();*/
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
                                    result = vo.getResult();
                                    for (Dummy.Formfields fld : result.getFormfields()) {
                                        String name = fld.getName();
                                        if (!TextUtils.isEmpty(name)) {
                                            if (name.equals("networks") || name.equals("lists") || name.equals("user_id")) {
                                                fld.setDescription("");
                                            }
                                        }
                                    }
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
        // (List<String>) result;

        switch (reqCode) {
            case REQ_CODE_IMAGE:
                if (null != result) {
                    String imagePath = ((List<String>) result).get(0);

                    /*if (imagePath != null && (new File(imagePath).exists())) {
                        Intent intent = new Intent(context, ImageEditActivity.class);
                        intent.putExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME, "stickers");
                        intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, true);
                        intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, true);
                        intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, true);
                        intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, false);
                        intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, true);
                        intent.putExtra(ImageEditor.EXTRA_IMAGE_PATH, imagePath);
                        startActivityForResult(intent, ImageEditor.RC_IMAGE_EDITOR);
                    } else {
                        Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show();
                    }*/
                    String title = mFormBuilder.getAdapter().getValueByName("title");
                    String source = mFormBuilder.getAdapter().getValueByName("source");
                    if (imagePath != null && (new File(imagePath).exists())) {
                        startActivityForResult(
                                new ImageEditor.Builder(getActivity(), imagePath)
                                        .setStickerAssets("stickers")
                                        .setQuote(title)
                                        .setQuoteSource(source)
                                        .getEditorIntent(),
                                ImageEditor.RC_IMAGE_EDITOR);
                    }
                } else {
                    somethingWrongMsg(v);
                }
                break;

            case ImageEditor.RC_IMAGE_EDITOR:
                String filePath = ((List<String>) result).get(0);
                mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
                break;
        }

    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {
    }

    public static CreateQuoteFragment newInstance(int type, Map<String, Object> map, String url) {
        CreateQuoteFragment fragment = new CreateQuoteFragment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }

}
