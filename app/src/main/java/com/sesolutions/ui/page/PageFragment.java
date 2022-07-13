package com.sesolutions.ui.page;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class PageFragment extends PageHelper<PageAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TYPE_HOME = "sespage_main_home";
    public static final String TYPE_BROWSE = "sespage_main_browse";
    public static final String TYPE_CATEGORY = "sespage_main_categories";
    public static final String TYPE_FAVOURITE = "2";
    public static final String TYPE_LOCATIONS = "3";
    public static final String TYPE_FEATURED = "sespage_main_featured";
    public static final String TYPE_VERIFIED = "sespage_main_verified";
    public static final String TYPE_SPONSORED = "sespage_main_sponsored";
    public static final String TYPE_HOT = "sespage_main_hot";
    public static final String TYPE_MANAGE = "sespage_main_manage";
    public static final String TYPE_PACKAGE = "sespage_main_manage_package";
    public static final String TYPE_ALBUM_HOME = "9";
    public static final String TYPE_ALBUM_BROWSE = "sespage_main_pagealbumbrowse";
    public static final String TYPE_VIDEO_BROWSE = "sespagevideo_main_browsehome";
    public static final String TYPE_REVIEW_BROWSE = "sespage_main_pagereviews";

    public static final String TYPE_ASSOCIATE = "11";
    public static final String TYPE_CATEGORY_VIEW = "12";
    public static final String TYPE_SEARCH = "13";
    public static final String TYPE_SEARCH_MANAGE = "14";
    public static final String TYPE_REVIEW_SEARCH = "15";
    public static final String TYPE_CREATE = "sespage_main_create";

    public String selectedScreen = "";
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public String url;
    public OnUserClickedListener<Integer, Object> parent;

    //variable used when called from page view -> associated
    private int mPageId;


    public static PageFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        PageFragment frag = new PageFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static PageFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static PageFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        PageFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }

    public static PageFragment newInstance(String TYPE, int pageId) {
        PageFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mPageId = pageId;
        return frag;
    }

    boolean istoolbar=false;
    public static PageFragment newInstance(String TYPE, int pageId, boolean istoolbar) {
        PageFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mPageId = pageId;
        frag.istoolbar = istoolbar;
        return frag;
    }



    public static PageFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        applyTheme(v);

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.video);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            initScreenData();
        }
        
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.NO_PAGE_AVAILABLE;
        switch (selectedScreen) {
            case TYPE_MANAGE:
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = Constant.URL_MANAGE_PAGE;
                break;
            case TYPE_BROWSE:
                url = Constant.URL_BROWSE_PAGE;
                break;
            case TYPE_HOT:
                url = Constant.URL_BROWSE_HOT;
                break;
            case TYPE_FEATURED:
                url = Constant.URL_BROWSE_FEATURED;
                break;
            case TYPE_VERIFIED:
                url = Constant.URL_BROWSE_VERIFIED;
                break;
            case TYPE_SPONSORED:
                url = Constant.URL_BROWSE_SPONSERED;
                break;
            case TYPE_CATEGORY:
                url = Constant.URL_BROWSE_CATEGORIES;
                break;
            case TYPE_ASSOCIATE:
                url = Constant.URL_PAGE_ASSOCIATED;
                break;
            case TYPE_CATEGORY_VIEW:
                url = Constant.URL_BROWSE_PAGE;
                break;
            case TYPE_SEARCH:
                url = Constant.URL_SEARCH_PAGE;
                break;
          /*  case TYPE_CATEGORY_VIEW:
                url = Constant.URL_BROWSE_PAGE;
                break;*/
            default:
                url = Constant.URL_BROWSE_PAGE;
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
            adapter = new PageAdapter(videoList, context, this, this);
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
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }

                // used when this screen called from page view -> associated
                if (mPageId > 0) {
                    request.params.put(Constant.KEY_PAGE_ID, mPageId);
                }// used when this screen called from page view -> associated
                    /*if (categoryId > 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    }*/

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params.put(Constant.KEY_SEARCH, searchKey);
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
                                    PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();

                                    /*add category list */
                                    Log.e("Screen",""+selectedScreen);
                                    Log.e("TYPE_CATEGORY",""+TYPE_CATEGORY);

                                    if(!selectedScreen.equalsIgnoreCase(TYPE_CATEGORY)){
                                        if (null != result.getCategory()) {
                                            videoList.add(new PageVo(adapter.VT_CATEGORY, result.getCategory()));
                                        }
                                        if (null != result.getPopularPages()) {
                                            videoList.add(new PageVo(adapter.VT_SUGGESTION, result.getPopularPages()));
                                        }
                                        if (null != result.getCategories()) {
                                            videoList.addAll(result.getCategories(adapter.VT_CATEGORIES));
                                        }
                                        if (null != result.getPages()) {
                                            videoList.addAll(result.getPages(selectedScreen));
                                        }
                                    }else {
                                        if (null != result.getCategory()) {
                                            videoList.add(new PageVo(adapter.VT_CATEGORY_SINGLE, result.getCategory()));
                                        }
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

    @Override
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
