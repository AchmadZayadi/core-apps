package com.sesolutions.ui.page;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Review;
import com.sesolutions.responses.ReviewResponse;
import com.sesolutions.ui.business.BusinessFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.groups.GroupFragment;
import com.sesolutions.ui.review.PageReviewAdapter;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class PageReviewFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public View v;
    public String searchKey;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ReviewResponse.Result result;
    private ProgressBar pb;
    public List<Review> reviewList;
    public PageReviewAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;

    // private Map<String, Object> map;
    //private String url;
    // private int mObjectId;
    public String selectedScreen;
    private OnUserClickedListener<Integer, Object> listener;

    public static PageReviewFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> listener) {
        PageReviewFragment frag = new PageReviewFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        return frag;
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

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
    }

    public void setRecyclerView() {
        try {
            reviewList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PageReviewAdapter(reviewList, context, this, this, resourceType);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //@Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

               /* case R.id.cvPost:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mObjectId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT + "_" + mObjectId);
                    fragmentManager.beginTransaction().replace(R.id.container, ReviewCreateForm.newInstance(Constant.FormType.CREATE_REVIEW, map, Constant.URL_CREATE_REVIEW)).addToBackStack(null).commit();
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        getMapValues();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private String URL, resourceType;

    public void getMapValues() {
        try {

            //String resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
            switch (selectedScreen) {
                case PageFragment.TYPE_REVIEW_BROWSE:
                case PageFragment.TYPE_REVIEW_SEARCH:
                    URL = Constant.URL_PAGE_REVIEW_HOME;
                    resourceType = Constant.ResourceType.PAGE_REVIEW;
                    // mObjectId = (int) map.get(Constant.KEY_PAGE_ID);
                    break;
                case BusinessFragment.TYPE_REVIEW_BROWSE:
                    URL = Constant.URL_BUSINESS_REVIEW_HOME;
                    resourceType = Constant.ResourceType.BUSINESS_REVIEW;
                    break;
                case GroupFragment.TYPE_REVIEW_BROWSE:
                    URL = Constant.URL_GROUP_REVIEW_HOME;
                    resourceType = Constant.ResourceType.GROUP_REVIEW;
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                try {

                    HttpRequestVO request = new HttpRequestVO(URL);

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH_TEXT, searchKey);
                    }
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            hideAllLoaders();

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        reviewList.clear();
                                    }
                                    wasListEmpty = reviewList.size() == 0;
                                    ReviewResponse resp = new Gson().fromJson(response, ReviewResponse.class);
                                    result = resp.getResult();
                                    if (null != result.getReviews())
                                        reviewList.addAll(result.getReviews());
                                    showHideUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }

                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideAllLoaders();
                }
            } else {
                hideAllLoaders();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void showHideUpperLayout() {
       /* if (null != result.getPostButton()) {
            v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getPostButton().getLabel());
            v.findViewById(R.id.cvPost).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvPost).setVisibility(View.GONE);
        }*/
    }

    private void hideAllLoaders() {
        isLoading = false;
        //hideView(v.findViewById(R.id.pbMain));
        pb.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateAdapter() {

        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_review);
        v.findViewById(R.id.llNoData).setVisibility(reviewList.size() > 0 ? View.GONE : View.VISIBLE);

        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                    goToProfileFragment(reviewList.get(postion).getOwnerId());
                    return false;
                case Constant.Events.MUSIC_MAIN:
                    goToViewReviewFragment(resourceType, reviewList.get(postion).getReviewId());
                    return false;
                case Constant.Events.MENU_MAIN:
                    switch (resourceType) {
                        case Constant.ResourceType.PAGE_REVIEW:
                            openViewPageFragment(reviewList.get(postion).getContent(resourceType).getId());
                            break;
                        case Constant.ResourceType.GROUP_REVIEW:
                            openViewGroupFragment(reviewList.get(postion).getContent(resourceType).getId());
                            break;
                        case Constant.ResourceType.BUSINESS_REVIEW:
                            openViewBusinessFragment(reviewList.get(postion).getContent(resourceType).getId());
                            break;
                        case Constant.ResourceType.STORE_REVIEW:
                            StoreUtil.openViewStoreFragment(fragmentManager, reviewList.get(postion).getContent(resourceType).getId());
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }
}
