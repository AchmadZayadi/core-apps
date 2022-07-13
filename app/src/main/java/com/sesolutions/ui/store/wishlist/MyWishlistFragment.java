package com.sesolutions.ui.store.wishlist;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatImageView;
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
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.store.StoreVo;
import com.sesolutions.responses.store.product.ProductResponse;
import com.sesolutions.ui.store.StoreParentFragment;
import com.sesolutions.ui.store.product.ProductAdapter;
import com.sesolutions.ui.store.product.ProductHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class MyWishlistFragment extends ProductHelper<ProductAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public String selectedScreen = "my_wishlist";
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
    public StoreParentFragment parentFragment;
    public AppCompatImageView ivListIcon, ivGridIcon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_product, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity.taskPerformed == 89) {
            onRefresh();
        }
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        ivListIcon = v.findViewById(R.id.ivListView);
        ivListIcon.setOnClickListener(this::onClick);
        ivGridIcon = v.findViewById(R.id.ivGridView);
        ivGridIcon.setOnClickListener(this::onClick);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.NO_WISHLIST_AVAILABLE;
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
            adapter = new ProductAdapter(videoList, context, this, this);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            changeLayoutType(false);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        v.findViewById(R.id.rlSearchFilter).setVisibility(View.GONE);
        setRecyclerView();
        callProductApi(1);

    }

    public void callProductApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL.URL_BROWSE_WISHLIST); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }

                // used when this screen called from page view -> associated
//                if (mPageId > 0) {
//                    request.params.put(Constant.KEY_PRODUCT_ID, mPageId);
//                }// used when this screen called from page view -> associated
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
                            CustomLog.e("product_response", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
//                                    if (null != parent) {
//                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
//                                    }
                                    ProductResponse resp = new Gson().fromJson(response, ProductResponse.class);
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
                                        videoList.add(new StoreVo(selectedScreen, result.getAllProducts()));
//                                        videoList.addAll(result.getStores(selectedScreen));
                                    }
                                    if (null != result.getWishlists()) {
//                                        videoList.add(new StoreVo(selectedScreen, result.getWishlists()));
                                        videoList.addAll(result.getWishlists(selectedScreen));
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
//        if (parent != null) {
//            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
//        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callProductApi(REQ_LOAD_MORE);
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
        }
    }

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callProductApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
