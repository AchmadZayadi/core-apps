package com.sesolutions.ui.member;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.sesolutions.responses.Friends;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagSuggestionFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 20;
    private static final int REQ_CODE_FEELING = 10;
    private static final int REQ_ADD_TAG = 30;
    private static final int REQ_GET_TAG = 40;
    private static final int REQ_REMOVE_TAG = 50;
    private static final int REQ_LOAD_MORE = 60;
    private View v;

    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<Friends> emotionList;
    private String searchKey = "";
    private CommonResponse.Result result;
    private int photoId;
    private int userId;
    private boolean isAddRemove;
    private boolean isOwner;


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
            if (isAddRemove) {
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_PHOTO_ID, photoId);
                callStickerApi(REQ_GET_TAG, Constant.URL_TAGGED_USER, map);

            } else {
                callStickerApi(REQ_CODE_FEELING, Constant.URL_TAGLIST_SUGGESTION, null);
            }
            setRecyclerView();

            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        closeKeyboard();
                        searchKey = etSearch.getText().toString();
                        if (!TextUtils.isEmpty(searchKey)) {
                            result = null;
                            emotionList.clear();
                            callStickerApi(REQ_CODE_SEARCH, Constant.URL_TAGLIST_SUGGESTION, null);

                        }
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        try {
            pb = v.findViewById(R.id.pb);
            etSearch = v.findViewById(R.id.etSearch);
            etSearch.setVisibility(isAddRemove ? View.GONE : View.VISIBLE);
            v.findViewById(R.id.tvDone).setVisibility(isAddRemove ? View.GONE : View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(isAddRemove ? Constant.TITLE_TAGGED_USER : Constant.TITLE_ADD_TAG);
            recyclerView = v.findViewById(R.id.rvFeeling);
            v.findViewById(R.id.tvDone).setVisibility(View.GONE);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MemberAdapter(emotionList, this, context, this);
            adapter.setAddRemove(isAddRemove);
            adapter.setOwner(isOwner);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
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


    private void callStickerApi(final int req, String url, final Map<String, Object> map) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;

                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else {//if (req == REQ_CODE_FEELING || req == REQ_ADD_TAG || req == REQ_GET_TAG) {
                    showBaseLoader(true);
                }

                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    if (map != null) {
                        request.params.putAll(map);
                    }
                   /* if (req == REQ_ADD_TAG) {
                        request.params.put(Constant.KEY_PHOTO_ID, photoId);
                        request.params.put(Constant.KEY_USER_ID, userId);
                    }   else*/
                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_VALUE, searchKey);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
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
                                    if (req == REQ_ADD_TAG) {
                                        onBackPressed();
                                    } else {
                                        CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                        result = comResp.getResult();

                                        if (TextUtils.isEmpty(comResp.getError())) {

                                            if (isAddRemove) {
                                                if (req == REQ_REMOVE_TAG) {
                                                    emotionList.remove((int) map.get(Constant.KEY_POSITION));
                                                    adapter.notifyItemRemoved((int) map.get(Constant.KEY_POSITION));
                                                } else if (null != comResp.getResult().getTags()) {
                                                    emotionList.addAll(comResp.getResult().getTags());
                                                }
                                            } else {
                                                if (null != comResp.getResult().getFriends()) {
                                                    emotionList.addAll(comResp.getResult().getFriends());
                                                }
                                            }

                                            updateFeelingAdapter();
                                        } else {
                                            Util.showSnackbar(v, comResp.getErrorMessage());
                                            if (req == REQ_CODE_FEELING || req == REQ_GET_TAG) {
                                                goIfPermissionDenied(comResp.getError());
                                            }
                                        }
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
        try {
            adapter.notifyDataSetChanged();
            pb.setVisibility(View.GONE);
            isLoading = false;
            ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_USER);
            v.findViewById(R.id.tvNoData).setVisibility(emotionList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        if (isAddRemove) {
            switch (object1) {
                case Constant.Events.MEMBER_REMOVE:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_PHOTO_ID, photoId);
                    map.put(Constant.KEY_POSITION, postion);
                    map.put(Constant.KEY_TAGMAP_ID, emotionList.get(postion).getTagmapId());
                    callStickerApi(REQ_REMOVE_TAG, Constant.URL_REMOVE_TAGGED_USER, map);
                    break;
            }
            return false;
        }
        userId = emotionList.get(postion).getId();
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_USER_ID, userId);
        callStickerApi(REQ_ADD_TAG, Constant.URL_ADD_TAG, map);

        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callStickerApi(REQ_LOAD_MORE, Constant.URL_TAGLIST_SUGGESTION, null);

                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static TagSuggestionFragment newInstance(int photoId, boolean isAddRemove, boolean isOwner) {
        TagSuggestionFragment frag = new TagSuggestionFragment();
        frag.photoId = photoId;
        frag.isAddRemove = isAddRemove;
        frag.isOwner = isOwner;
        return frag;
    }

}
