package com.sesolutions.ui.courses.course;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.sesolutions.responses.Courses.course.CourseResponse;
import com.sesolutions.ui.courses.adapters.CourseAdapter;
import com.sesolutions.ui.store.StoreParentFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class MyCourseFragment extends CourseHelper<CourseAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String selectedScreen = MenuTab.Store.MY_STORE;
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public RelativeLayout rlSearchFilter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public boolean isTag;
    public String url;
    public OnUserClickedListener<Integer, Object> parent;
    public StoreParentFragment parentFragment;
    public AppCompatImageView ivListIcon, ivGridIcon;
    public AppCompatTextView etStoreSearch;

    //variable used when called from store view -> associated
    private int mStoreId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_course, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void initScreenData() {
        init();
        rlSearchFilter.setVisibility(View.VISIBLE);
        setRecyclerView();
        callStoreApi(1);
    }

    public void init() {
        try {
            recyclerView = v.findViewById(R.id.rv_stores);
            rlSearchFilter = v.findViewById(R.id.rlSearchFilter);
            ivListIcon = v.findViewById(R.id.ivListView);
            etStoreSearch = v.findViewById(R.id.etStoreSearch);
            ivListIcon.setOnClickListener(this);
            etStoreSearch.setOnClickListener(this);
            ivGridIcon = v.findViewById(R.id.ivGridView);
            ivGridIcon.setOnClickListener(this);
            pb = v.findViewById(R.id.pb);

            txtNoData = R.string.NO_COURSE_AVAILABLE;
        } catch (Exception e) {
            CustomLog.e("init_store", e.toString());
        }
    }


    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new CourseAdapter(videoList, context, this, this);
            adapter.setType(selectedScreen);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
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
                HttpRequestVO request = new HttpRequestVO(URL.URL_MY_COURSE); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }
//                request.params.put("filter_sort", selectedScreen);

                // used when this screen called from store view -> associated
                if (mStoreId > 0) {
                    request.params.put(Constant.KEY_CLASSROOM_ID, mStoreId);
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
                                CourseResponse resp = new Gson().fromJson(response, CourseResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
//                                    if (null != parent) {
//                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
//                                    }
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();

                                    /*add category list */
//                                    if (null != result.getCategory()) {
//                                        videoList.add(new ClassroomVo(adapter.VT_CATEGORY, result.getCategory()));
//                                    }
//                                    if (null != result.getClassrooms()) {
//                                        videoList.add(new ClassroomVo(adapter.VT_SUGGESTION, result.getPopularClassrooms()));
//                                    }
//                                    if (null != result.getCategories()) {
//                                        videoList.addAll(result.getCategories(adapter.VT_CATEGORIES));
//                                    }
                                    if (null != result.getCourses()) {
//                                        videoList.add(new StoreVo(selectedScreen, result.getAllStores()));
                                        videoList.addAll(result.getCourses(selectedScreen));
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

            case R.id.etStoreSearch:
//                goToSearchFragment()
                fragmentManager.beginTransaction().replace(R.id.container, new SearchCourseFragment()).addToBackStack(null).commit();
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
