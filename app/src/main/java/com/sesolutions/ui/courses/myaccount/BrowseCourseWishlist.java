package com.sesolutions.ui.courses.myaccount;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.IconCategoryAdapter;
import com.sesolutions.ui.quotes.QuoteAdapter;
import com.sesolutions.ui.wish.WishHelper;
import com.sesolutions.ui.wish.WishParentFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class BrowseCourseWishlist extends WishHelper<QuoteAdapter> implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private String BROWSE_URL = Constant.URL_BROWSE_COURSEWISHLIST;


    public String searchKey;
    public CommonResponse.Result result;
    public int loggedinId;

    public String txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ProgressBar pb;

    private IconCategoryAdapter adapterCategory;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;


    public static BrowseCourseWishlist newInstance(WishParentFragment parent, int loggedInId, int categoryId) {
        BrowseCourseWishlist frag = new BrowseCourseWishlist();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static BrowseCourseWishlist newInstance(WishParentFragment parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }


    public static BrowseCourseWishlist newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_browse_quotes, container, false);
        txtNoData = getString(R.string.msg_no_wish_created);
        applyTheme(v);
        return v;
    }

    public void init() {

        try {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new QuoteAdapter(videoList, context, this, this, Constant.GoTo.WISH);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setCategoryRecycleView() {
        try {
            rvQuotesCategory = v.findViewById(R.id.rvQuotesCategory);
            categoryList = new ArrayList<>();
            rvQuotesCategory.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvQuotesCategory.setLayoutManager(layoutManager);
            adapterCategory = new IconCategoryAdapter(categoryList, context, this, this);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            rvQuotesCategory.setAdapter(adapterCategory);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        /*try {
            switch (v.getId()) {


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        setCategoryRecycleView();
        callMusicAlbumApi(1);
        // callCategoryApi(1);

    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(BROWSE_URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    if (isTag) {
                        request.params.put(Constant.KEY_TAG_ID, categoryId);
                    }

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
                                            parent.isBrowseLoaded = true;
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        //if screen is refreshed then clear previous data
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                            if (null != categoryList) {
                                                categoryList.clear();
                                            }
                                        }

                                        result = resp.getResult();
                                        if (null != result.getWishes())
                                            videoList.addAll(result.getWishes());
                                        if (null != categoryList && null != result.getWishCategories())
                                            categoryList.addAll(result.getWishCategories());

                                        updateAdapter();
                                        if (null != categoryList) {
                                            updateCategoryAdapter();
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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

                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                setRefreshing(swipeRefreshLayout, false);

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(swipeRefreshLayout,false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.updateTotal(0, result.getTotal());
        } else {
            updateTitle(result.getTotal());
        }
    }

    //this method will overridden by its child class
    public void updateTitle(int total) {
    }

    private void updateCategoryAdapter() {

        rvQuotesCategory.setVisibility(categoryList.size() > 0 ? View.VISIBLE : View.GONE);
        adapterCategory.notifyDataSetChanged();
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



   /*public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.:

                break;

        }
        return super.onItemClicked(object1, object2, postion);
    }*/

  /*  private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
