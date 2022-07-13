package com.sesolutions.ui.store;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.store.StoreResponse;
import com.sesolutions.responses.store.StoreVo;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class StoreFragment extends StoreHelper<StoreAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String STORE_CATEGORY_VIEW = "12";

    public String selectedScreen = "";
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public RelativeLayout rlSearchFilter;
    public AppCompatTextView etStoreSearch;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public String url;
    public OnUserClickedListener<Integer, Object> parent;
    public StoreParentFragment parentFragment;
    public AppCompatImageView ivListIcon, ivGridIcon;

    //variable used when called from store view -> associated
    private int mStoreId;


    public static StoreFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        StoreFragment frag = new StoreFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static StoreFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static StoreFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        StoreFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }

    public static StoreFragment newInstance(String TYPE, int storeId) {
        StoreFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mStoreId = storeId;
        return frag;
    }


    public static StoreFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_store, container, false);
        applyTheme(v);
        return v;
    }

    public void init() {
        try {
            recyclerView = v.findViewById(R.id.rv_stores);
            rlSearchFilter = v.findViewById(R.id.rlSearchFilter);
            etStoreSearch = v.findViewById(R.id.etStoreSearch);
            etStoreSearch.setOnClickListener(this);
            ivListIcon =  v.findViewById(R.id.ivListView);
            ivListIcon.setOnClickListener(this);
            ivGridIcon = v.findViewById(R.id.ivGridView);
            ivGridIcon.setOnClickListener(this);
            pb = v.findViewById(R.id.pb);

        } catch (Exception e) {
            CustomLog.e("init_store", e.toString());
        }

        txtNoData = R.string.NO_STORE_AVAILABLE;
        switch (selectedScreen) {
            case MenuTab.Store.MY_STORE:
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = URL.MY_STORE;
                break;
            case MenuTab.Store.HOT:
                url = URL.STORE_HOT;
                break;
            case MenuTab.Store.CATEGORY_STORE:
                url = URL.STORE_CATEGORIES;
                break;
            case MenuTab.Store.CATEGORY_PRODUCT:
                url = URL.PRODUCT_CATEGORIES;
                break;
            case MenuTab.Store.SEARCH:
                url = URL.STORE_SEARCH_FILTER;
                break;
            case STORE_CATEGORY_VIEW:
                url = URL.STORE_BROWSE;
                break;
            default:
                url = URL.STORE_BROWSE;
                break;
        }
    }

    private void changeLayoutType(boolean isGrid) {
        if (isGrid) {
            adapter.setStoreLayoutGrid(true);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setStoreLayoutGrid(false);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

            adapter = new StoreAdapter(videoList, context, this, this, false);
            adapter.setType(selectedScreen);
            //for change the view of store's list
            changeLayoutType(false);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();

        if(selectedScreen.equals(MenuTab.Store.STORE_BROWSE)) {
            rlSearchFilter.setVisibility(View.VISIBLE);
            setRecyclerView();
            callStoreApi(1);
        }else {
//            rlSearchFilter.setVisibility(View.GONE);
            setRecyclerView();
            callStoreApi(1);
        }
    }

    public void callStoreApi(final int req) {


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
                request.params.put("filter_sort", selectedScreen);

                // used when this screen called from store view -> associated
                if (mStoreId > 0) {
                    request.params.put(Constant.KEY_STORE_ID, mStoreId);
                }// used when this screen called from store view -> associated
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
                            CustomLog.e("store_response", "" + response);
                            if (response != null) {
                                StoreResponse resp = new Gson().fromJson(response, StoreResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    if (null != parent) {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                                    }
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();

                                    /*add category list */
                                    if (null != result.getCategory()) {
                                        videoList.add(new StoreVo(adapter.VT_CATEGORY, result.getCategory()));
                                    }
                                    if (null != result.getPopularStores()) {
                                        videoList.add(new StoreVo(adapter.VT_SUGGESTION, result.getPopularStores()));
                                    }
                                    if (null != result.getCategories()) {
                                        videoList.addAll(result.getCategories(adapter.VT_CATEGORIES));
                                    }
                                    if (null != result.getStores()) {
//                                        videoList.add(new StoreVo(selectedScreen, result.getAllStores()));
                                        videoList.addAll(result.getStores(selectedScreen));
                                    }
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    goIfPermissionDenied(resp.getError());
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
                    callStoreApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivListView:
                ivGridIcon.setVisibility(View.VISIBLE);
                ivListIcon.setVisibility(View.GONE);
                changeLayoutType(false);
                break;
            case R.id.ivGridView:
                ivListIcon.setVisibility(View.VISIBLE);
                ivGridIcon.setVisibility(View.GONE);
                changeLayoutType(true);
                break;
            case R.id.etStoreSearch:
//                goToSearchFragment()
                fragmentManager.beginTransaction().replace(R.id.container, new SearchStoreFragment()).addToBackStack(null).commit();
                break;
        }
    }

//    @Override
//    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
//        switch (object1) {
//            case Constant.Events.MUSIC_MAIN:
////                this.goToCategoryFragment(postion);
//                StoreUtil.openViewStoreFragment(fragmentManager,postion);
//                break;
//
//        }
//
////            StoreUtil.openViewStoreFragment(fragmentManager, postion);
//
//        return false;
//    }


    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callStoreApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

}
