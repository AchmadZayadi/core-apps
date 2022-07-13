package com.sesolutions.imageeditengine;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sesolutions.ui.postfeed.StickerAdapter;
import com.sesolutions.ui.postfeed.UserEmotionAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class StickerDialogFragment extends BaseDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, String>, OnLoadMoreListener {

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
    private StickerAdapter adapter;
    private List<Emotion> emotionList;
    private List<Emotion> emotionList2;
    private String searchKey = "";
    private CommonResponse.Result result;
    private CommonResponse.Result result2;
    private UserEmotionAdapter adapter2;
    private ImageView ivAddSticker;
    private ImageView ivSearchSticker;
    private int colorPrimary;
    private boolean isAddToClicked;
    private boolean fromComment;
    private OnUserClick listener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_sticker, container, false);
        try {
            applyTheme(v);
            colorPrimary = Color.parseColor(Constant.colorPrimary);
            init();
            callFeelingApi(REQ_CODE_FEELING);
            //callStickerSubscriptionApi(REQ_CODE_FEELING);
            setRecyclerView();
            v.findViewById(R.id.llStickerAddBottom).setVisibility(View.GONE);
            //setBottomRecyclerView();
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    searchKey = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        etSearch.setText("");
                        closeKeyboard();
                        gotoStickerChild(null, searchKey);
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
        recyclerView = v.findViewById(R.id.rvFeeling);
        rvStickerBottom = v.findViewById(R.id.rvStickerBottom);
        v.findViewById(R.id.ivAddSticker).setOnClickListener(this);
        v.findViewById(R.id.ivSearchSticker).setOnClickListener(this);
        v.findViewById(R.id.ivSearchSticker).setBackgroundColor(colorPrimary);
        v.findViewById(R.id.ivAddSticker).setBackgroundColor(colorPrimary);
    }


    public void refreshBottomList() {
        try {
            if (isAddToClicked) {
                isAddToClicked = false;
                emotionList2.clear();
                result2 = null;
                callStickerSubscriptionApi(REQ_CODE_FEELING);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new StickerAdapter(emotionList, getContext(), this, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setBottomRecyclerView() {
        try {
            emotionList2 = new ArrayList<>();
            rvStickerBottom.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvStickerBottom.setLayoutManager(layoutManager);
            adapter2 = new UserEmotionAdapter(emotionList2, getContext(), this, this);
            rvStickerBottom.setAdapter(adapter2);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivAddSticker:
                    isAddToClicked = true;
                    goToAddSticker();
                    break;

                case R.id.ivSearchSticker:
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToAddSticker() {
        //fragmentManager.beginTransaction().replace(R.id.container, new StickerAddFragment()).addToBackStack(null).commit();
    }


    private void callFeelingApi(final int req) {

        try {
            if (isNetworkAvailable(getContext())) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_STICKERS);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(getContext()));

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
                                    }
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
                    new HttpRequestHandler(getContext(), new Handler(callback)).run(request);

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

    private void callStickerSubscriptionApi(final int req) {

        try {
            if (isNetworkAvailable(getContext())) {
                isLoading = true;


                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_STICKERS);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    request.params.put(Constant.KEY_USER_EMOJI, 1);
                    /*if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/

                    request.params.put(Constant.KEY_PAGE, null != result2 ? result2.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(getContext()));
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
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    result2 = comResp.getResult();
                                    /*if (req == REQ_CODE_SEARCH) {
                                        emotionList2.clear();
                                    }*/
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        if (comResp.getResult().isValidUserEmotion()) {
                                            int size = emotionList2.size();
                                            emotionList2.addAll(comResp.getResult().getUserEmotions());
                                            if (size == 0) {
                                                emotionList2.remove(0);
                                            }
                                        }
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                    }
                                    updateBottomAdapter();

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
        //runLayoutAnimation(recyclerView);
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
            if (object1 == Constant.Events.STICKER) {
                gotoStickerChild(null, emotionList.get(postion).getTitle());
            } else {
                gotoStickerChild(emotionList2.get(postion), null);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void gotoStickerChild(Emotion emotion, String searchKey) {
        listener.onItemClicked(Constants.Events.STICKER, searchKey, -1);
        onDismiss();
       /* if (activity instanceof ImageEditActivity) {
            fragmentManager.beginTransaction().add(R.id.container,
                    StickerChildFragment.newInstance(emotion, searchKey, fromComment),
                    StickerChildFragment.class.getName())
                    .addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container,
                    StickerChildFragment.newInstance(emotion, searchKey, fromComment),
                    StickerChildFragment.class.getName())
                    .addToBackStack(null).commit();
        }*/
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

    public static StickerDialogFragment newInstance(boolean fromComment, OnUserClick listener) {
        StickerDialogFragment frag = new StickerDialogFragment();
        frag.fromComment = fromComment;
        frag.listener = listener;
        return frag;
    }
}
