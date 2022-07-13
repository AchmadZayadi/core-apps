package com.sesolutions.ui.postfeed;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Emotion;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class GifFragment extends BaseFragment implements  OnUserClickedListener<Integer, String>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private static final int REQ_CODE_STICKER_CONTENT = 4;
    private View v;

    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private RecyclerView rvStickerBottom;
    private GifViewAdapter adapter;
    private List<GifResponsemodel.ResultDTO.GifDTO> emotionList;
    private List<String> emotionList2;
    private String searchKey = "";
    private CommonResponse.Result result;
    private CommonResponse.Result result2;
    private UserEmotionAdapter adapter2;
    private ImageView ivAddSticker;
    private ImageView ivSearchSticker;
    private int colorPrimary;
    private boolean isAddToClicked;
    private boolean fromComment;
    ImageView ivBack;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_gifimage, container, false);
        try {
            applyTheme(v);
            colorPrimary = Color.parseColor(Constant.colorPrimary);
            init();
            callFeelingApi(REQ_CODE_FEELING);
            setRecyclerView();
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    searchKey = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        etSearch.setText("");
                        closeKeyboard();
                        callFeelingApi(REQ_CODE_FEELING);
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

        pb = v.findViewById(R.id.pb);
        etSearch = v.findViewById(R.id.etSearch);
        ivBack = v.findViewById(R.id.ivBack);
        recyclerView = v.findViewById(R.id.rvFeeling);

        GradientDrawable gdr = (GradientDrawable) etSearch.getBackground();
        gdr.setColor(SesColorUtils.getForegroundColor(context));
        etSearch.setBackground(gdr);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<GifResponsemodel.ResultDTO.GifDTO>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new GifViewAdapter(emotionList, context, this, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }







    private void callFeelingApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_GIFS);
                    //request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH_GIF, searchKey);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    GifResponsemodel comResp = new Gson().fromJson(response, GifResponsemodel.class);
                                        emotionList.clear();
                                        emotionList.addAll(comResp.result.gif);
                                        updateFeelingAdapter();

                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                isLoading = false;
                                pb.setVisibility(View.GONE);
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
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_STICKERS);
        v.findViewById(R.id.tvNoData).setVisibility(emotionList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    private void updateBottomAdapter() {
        adapter2.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData2)).setText(R.string.MSG_NO_STICKERS_EMOJI);
        rvStickerBottom.setVisibility(emotionList2.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.tvNoData2).setVisibility(emotionList2.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {

        try {
            if (object1 == Constant.Events.GIFSTIKER) {
                String urldata=""+object2;
                (activity).setGifurl(urldata);
                Log.e("gif_url",""+ BaseActivity.gifimageurl);
                onBackPressed();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {

                case EDIT_CHANNEL_ME:
                     onBackPressed();
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callFeelingApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static GifFragment newInstance(boolean fromComment) {
        GifFragment frag = new GifFragment();
        frag.fromComment = true;
        return frag;
    }
}
