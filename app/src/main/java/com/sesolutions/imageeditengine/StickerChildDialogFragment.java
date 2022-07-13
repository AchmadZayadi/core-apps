package com.sesolutions.imageeditengine;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidninja.imageeditengine.Constants;
import com.droidninja.imageeditengine.OnUserClick;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Emotion;
import com.sesolutions.ui.common.BaseDialogFragment;
import com.sesolutions.ui.postfeed.StickerChildAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class StickerChildDialogFragment extends BaseDialogFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private View v;

    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private StickerChildAdapter adapter;
    private List<Emotion> emotionList;
    private String searchKey = "";
    private CommonResponse.Result result;
    private Emotion feelVo;

    private boolean fromComment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_feeling_activity, container, false);
        try {
            applyTheme(v);
            init();
            setRecyclerView();

            if (null != feelVo) {
                callStickerApi(REQ_CODE_FEELING);
            } else {
                etSearch.setText(searchKey);
                new Handler().postDelayed(() -> {
                    etSearch.setSelection(etSearch.getText().length());
                    callStickerApi(REQ_CODE_SEARCH);
                }, 200);
            }
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard(etSearch);
                    searchKey = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        result = null;
                        emotionList.clear();
                        callStickerApi(REQ_CODE_SEARCH);
                    }
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
       /* tvFeeling = v.findViewById(R.id.tvFeeling);
        tvSticker = v.findViewById(R.id.tvSticker);
        tvActivity = v.findViewById(R.id.tvActivity);*/
        pb = v.findViewById(R.id.pb);
        etSearch = v.findViewById(R.id.etSearch);
        recyclerView = v.findViewById(R.id.rvFeeling);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.tvDone).setVisibility(View.GONE);
        String title = feelVo != null ? feelVo.getTitle() : Constant.SEARCH_STICKERS;
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);

        /*ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);*/
        //initSlide();
    }

    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new StickerChildAdapter(emotionList, getContext(), this, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onDismiss();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callStickerApi(final int req) {

        try {
            if (isNetworkAvailable(getContext())) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_STICKERS_EMOJI);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    if (req != REQ_CODE_SEARCH) {
                        request.params.put(Constant.KEY_GALLARY_ID, feelVo.getGalleryId());
                        // request.params.put(Constant.KEY_FEELING_TYPE, feelVo.getFeeling_type());
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(getContext()));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            pb.setVisibility(View.GONE);
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    result = comResp.getResult();
                                    if (req == REQ_CODE_SEARCH) {
                                        emotionList.clear();
                                    }
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        emotionList.addAll(comResp.getResult().getEmotions());
                                        updateFeelingAdapter();
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                        //goIfPermissionDenied(comResp.getError());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(getContext(), new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateFeelingAdapter() {
        adapter.notifyDataSetChanged();
        // runLayoutAnimation(recyclerView);
        pb.setVisibility(View.GONE);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_FEELINGS);
        v.findViewById(R.id.tvNoData).setVisibility(emotionList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(String object1, String object2, int postion) {
        Emotion vo = emotionList.get(postion);
       /* Feelings vos = new Feelings();
        vos.setTitle(vo.getTitle());
        vos.setIcon(vo.getIcon());
        vos.setIs_string("is ");
        vos.setFeeling_title(feelVo.getTitle());*/

        mListener.onItemClicked(Constants.Events.STICKER, vo.getIcon(), Constants.TASK_STICKER);
        onDismiss();
        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callStickerApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private OnUserClick mListener;

    public static StickerChildDialogFragment newInstance(Emotion feel, String searchKey, OnUserClick mListener) {
        StickerChildDialogFragment frag = new StickerChildDialogFragment();
        frag.feelVo = feel;
        frag.mListener = mListener;
        frag.searchKey = searchKey;
        return frag;
    }

}
