package com.sesolutions.ui.qna;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.qna.QAResponse;
import com.sesolutions.responses.qna.QuestionVo;
import com.sesolutions.ui.page.PageParentFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class QAFragment extends QAHelper<QAAdapter> implements SwipeRefreshLayout.OnRefreshListener {


    public static final String TYPE_FAVOURITE = "2";
    public static final String TYPE_LOCATIONS = "3";

    public static final String TYPE_ASSOCIATE = "11";
    public static final String TYPE_CATEGORY_VIEW = "12";
    public static final String TYPE_SEARCH = "13";
    public static final String TYPE_SEARCH_MANAGE = "14";

    public String selectedScreen = "";
    public String searchKey;
    // public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public String url;

    //variable used when called from page view -> associated
    private int mPageId;


    public static QAFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        QAFragment frag = new QAFragment();
        frag.parent = parent;
        // frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static QAFragment newInstance(PageParentFragment parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static QAFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        QAFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }

    public static QAFragment newInstance(String TYPE, int pageId) {
        QAFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mPageId = pageId;
        return frag;
    }


    public static QAFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        applyTheme(v);
        return v;
    }

    private boolean canShowReaction = true;

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.NO_PAGE_AVAILABLE;
        switch (selectedScreen) {
            case MenuTab.QnA.MANAGE:
                canShowReaction = false;
                // loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = Constant.URL_QA_MANAGE;
                break;
            case MenuTab.QnA.FEATURED:
            case MenuTab.QnA.SPONSORED:
            case MenuTab.QnA.HOT:
            case MenuTab.QnA.BROWSE:
                url = Constant.URL_QA_BROWSE;
                break;
            case MenuTab.QnA.CATEGORY:
                url = Constant.URL_QA_CATEGORIES;
                break;
            case TYPE_CATEGORY_VIEW:
                url = Constant.URL_QA_CATEGORY_VIEW;
                break;
            case TYPE_SEARCH:
                url = Constant.URL_QA_FILTER_SEARCH;
                break;
            default:
                url = Constant.URL_QA_BROWSE;
                break;
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new QAAdapter(videoList, context, this,canShowReaction);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  private int getHeaderType() {
        switch (selectedScreen) {
            case TYPE_CATEGORY:
                return 0;
            case TYPE_MANAGE:
                return selectedScreen;
           *//* case TYPE_BROWSE:
                return 3;*//*
     *//* case TYPE_PHOTO:
            case TYPE_TEXT:
            case TYPE_VIDEO:
                return 1;*//*
            default:
                return -2;
        }
    }
*/

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(url); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                /*if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }*/

                if (selectedScreen != null && !selectedScreen.equals(MenuTab.QnA.MANAGE)) {
                    request.params.put("search_type", selectedScreen.replace("sesqa_main_", ""));
                }

                /*// used when this screen called from page view -> associated
                if (mPageId > 0) {
                    request.params.put(Constant.KEY_PAGE_ID, mPageId);
                }*/

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params.put(Constant.KEY_SEARCHTEXT, searchKey);
                } else if (categoryId > 0) {
                    request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                }

                Map<String, Object> map = activity.filteredMap;
                if (null != map) {
                    request.params.putAll(map);
                }
                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                }

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
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    if (null != parent) {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                                    }
                                    QAResponse resp = new Gson().fromJson(response, QAResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();

                                    /*add category list */
                                    if (null != result.getCategory()) {
                                        videoList.add(new QuestionVo(adapter.VT_CATEGORY, result.getCategory()));
                                    }

                                    if (result.hasCategories()) {
                                        videoList.addAll(result.getCategories(adapter.VT_CATEGORIES));
                                    }
                                    if (result.hasQuestions()) {
                                        videoList.addAll(result.getQuestionsList(selectedScreen));
                                    }
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            somethingWrongMsg(v);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    public void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
