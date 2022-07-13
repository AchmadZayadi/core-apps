package com.sesolutions.ui.postfeed;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Emotion;
import com.sesolutions.responses.Gallary;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class StickerAddFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private View v;


    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private StickerAddAdapter adapter;
    private List<Gallary> emotionList;
    private String searchKey = "";
    private CommonResponse.Result result;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_feeling_activity, container, false);
        try {
            applyTheme(v);
            init();
            callStickerApi(REQ_CODE_FEELING);
            setRecyclerView();
            etSearch.setVisibility(View.GONE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {

        pb = v.findViewById(R.id.pb);
        etSearch = v.findViewById(R.id.etSearch);
        recyclerView = v.findViewById(R.id.rvFeeling);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.tvDone).setOnClickListener(this);
    }

    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new StickerAddAdapter(emotionList, context, this, this);
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
                case R.id.tvDone:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callStickerApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_STICKER_ADD);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    // request.params.put(Constant.KEY_GALLARY_ID, feelVo.getGalleryId());
                    // request.params.put(Constant.KEY_FEELING_TYPE, feelVo.getFeeling_type());

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

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
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        emotionList.addAll(comResp.getResult().getGallery());
                                        updateFeelingAdapter();
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                        goIfPermissionDenied(comResp.getError());
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
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

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
        runLayoutAnimation(recyclerView);
        pb.setVisibility(View.GONE);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_FEELINGS);
        v.findViewById(R.id.tvNoData).setVisibility(emotionList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(String object1, String object2, int postion) {
        Gallary vo = emotionList.get(postion);
        callAddRemoveApi(vo, postion);
        return false;
    }

    private void callAddRemoveApi(final Gallary vo, final int postion) {
        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_ADD_REMOVE_REACTION);
                    request.params.put(Constant.KEY_GALLARY_ID, vo.getGalleryId());
                    request.params.put(Constant.KEY_ACTION_ID, vo.isSelected() ? 0 : 1);
                    request.params.put("actionD", vo.isSelected() ? 0 : 1);

                    //  request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        pb.setVisibility(View.GONE);
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                // response = response.replace("â\u0080\u0099", "'");
                                BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                                // result = comResp.getResult();
                                if (TextUtils.isEmpty(comResp.getError())) {
                                    emotionList.get(postion).setSelected(!vo.isSelected());
                                    adapter.notifyItemChanged(postion);
                                    // emotionList.addAll(comResp.getResult().getGallery());
                                    //    updateFeelingAdapter();
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

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

    public static StickerAddFragment newInstance(Emotion feel) {
        StickerAddFragment frag = new StickerAddFragment();
        //frag.feelVo = feel;
        return frag;
    }
}
