package com.sesolutions.ui.courses.course;


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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.course.CourseResponse;
import com.sesolutions.responses.Courses.course.CourseVo;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.courses.adapters.CourseAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseFragment extends CourseHelper<CourseAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TYPE_HOME = "sespage_main_home";
    public static final String TYPE_BROWSE = "sesgroup_main_browse";
    public static final String TYPE_CATEGORY = "sesgroup_main_categories";
    public static final String TYPE_FAVOURITE = "2";
    public static final String TYPE_LOCATIONS = "3";
    public static final String TYPE_FEATURED = "sesgroup_main_featured";
    public static final String TYPE_VERIFIED = "sesgroup_main_verified";
    public static final String TYPE_SPONSORED = "sesgroup_main_sponsored";
    public static final String TYPE_HOT = "sesgroup_main_hot";
    public static final String TYPE_CREATE = "courses_main_create";
    public static final String TYPE_MANAGE = "sesgroup_main_manage";
    public static final String TYPE_PACKAGE = "sesgroup_main_manage_package";
    public static final String TYPE_ALBUM_HOME = "9";
    public static final String TYPE_ALBUM_BROWSE = "sesgroup_main_groupalbumbrowse";
    public static final String TYPE_VIDEO_BROWSE = "sesgroupvideo_main_browsehome";
    public static final String TYPE_ASSOCIATE = "11";
    public static final String TYPE_CATEGORY_VIEW = "12";
    public static final String TYPE_SEARCH = "13";
    public static final String TYPE_SEARCH_MANAGE = "14";
    public static final String TYPE_CLASSROOM_COURSE = "15";
    public static final String TYPE_COURSE_UPSELL = "16";
    public static final String TYPE_REVIEW_BROWSE = "sesgroup_main_pagereviews";

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
    private List<Options> filterOptions;

    //variable used when called from page view -> associated
    private int mGroupId;
    private int mCourseId;
    private boolean isToolbar=false;


    public static CourseFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        CourseFragment frag = new CourseFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static CourseFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId,boolean isToolbar) {
        CourseFragment frag = new CourseFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        frag.isToolbar = isToolbar;
        return frag;
    }


    public static CourseFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static CourseFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        CourseFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }

    public static CourseFragment newInstance(String TYPE, int groupId) {
        CourseFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mCourseId = groupId;
        return frag;
    }


    public static CourseFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }

    public static CourseFragment newInstance(int categoryId,boolean isToolbar) {

        return newInstance(null, 0, categoryId,isToolbar);
    }




    public static CourseFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent, int storeId) {
        CourseFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mGroupId = storeId;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        applyTheme(v);

        if (!isToolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_UP_INFO);
            initScreenData();
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.NO_COURSE_AVAILABLE;
        switch (selectedScreen) {
            case TYPE_MANAGE:
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = Constant.URL_GROUP_MANAGE;
                filterOptions = new ArrayList<>();
                txtNoData = R.string.msg_no_group_created_you;
                break;
            case TYPE_CATEGORY:
                url = Constant.URL_GROUP_CATEGORIES;
                txtNoData = R.string.no_category_found;
                break;
            case TYPE_ASSOCIATE:
                url = Constant.URL_GROUP_ASSOCIATED;
                break;
            case TYPE_SEARCH:
                url = Constant.URL_SEARCH_GROUP;
                break;
            case TYPE_CLASSROOM_COURSE:
                url = Constant.URL_CLASSROOM_PROFILE_COURSE;
                break;
            case TYPE_COURSE_UPSELL:
                url = Constant.URL_COURSE_UPSELL;
                txtNoData = R.string.NO_COURSE_AVAILABLE_UPSELL;
                break;
            case "my_wishlist":
                url = URL.URL_BROWSE_COURSEWISHLIST;
                break;
            /*case TYPE_HOT:
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
            case TYPE_CATEGORY_VIEW:
                url = Constant.URL_BROWSE_GROUP;
                break;
            */

            default:
                url = Constant.URL_BROWSE_COURSE;
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
            adapter = new CourseAdapter(videoList, context, this, this);
            adapter.setType(selectedScreen);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

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

                // choose filter key value ,
                if (!TextUtils.isEmpty(mFilter)) {
                    request.params.put("search_filter", mFilter);
                }
                if (null != selectedScreen) {
                    request.params.put("filter_sort", selectedScreen);
                }

                // used when this screen called from page view -> associated
                if (mGroupId > 0) {
                    request.params.put(Constant.KEY_CLASSROOM_ID, mGroupId);
                }
                if (mCourseId > 0) {
                    request.params.put(Constant.KEY_COURSE_ID, mCourseId);
                }// used when this screen called from group view -> associated
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
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 0);
                                    }
                                    CourseResponse resp = new Gson().fromJson(response, CourseResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();

                                    /*add category list */
//                                    if (null != result.getCategory()) {
//                                        videoList.add(new CourseVo(adapter.VT_CATEGORY, result.getCategory()));
//                                    }
                                    if (null != result.getHotClassrooms()) {
                                        videoList.add(new CourseVo(adapter.VT_HOT, result.getHotClassrooms()));
                                    }
                                    if (null != result.getFeaturedClassrooms()) {
                                        videoList.add(new CourseVo(adapter.VT_FEATURED, result.getFeaturedClassrooms()));
                                    }
                                    if (null != result.getVerifiedClassrooms()) {
                                        videoList.add(new CourseVo(adapter.VT_VERIFIED, result.getVerifiedClassrooms()));
                                    }
                                   /* if (null != result.getRelatedGroups()) {
                                        videoList.add(new PageVo(adapter.VT_SUGGESTION, result.getRelatedGroups()));
                                    }*/
                                    if (null != result.getCategories()) {
                                        videoList.addAll(result.getCategories(adapter.VT_CATEGORIES));
                                    }
                                    if (null != result.getCourses()) {
                                        videoList.addAll(result.getCourses(selectedScreen));
                                    }
                                    if (null != result.getUpsell_course()) {
                                        videoList.addAll(result.getupsellCourses(selectedScreen));
                                    }
//                                    if (TYPE_MANAGE.equals(selectedScreen) && result.getFilterMenuOptions() != null) {
//                                        filterOptions.clear();
//                                        filterOptions.addAll(result.getFilterMenuOptions());
//                                    }

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

    //show all filter options
    public void onFilterClick(ImageView ivFilter) {
        if (filterOptions != null) {
            try {
                FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), -1, this, filterOptions);
                int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
                int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
                popup.showOnAnchor(ivFilter, vertPos, horizPos, true);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    private String mFilter;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case Constant.Events.FEED_UPDATE_OPTION:
                int listPosition = Integer.parseInt("" + screenType);
                if (listPosition > -1) {
                    // this means list item option is clicked
                    return super.onItemClicked(object1, screenType, postion);
                } else {
                    //this means filter option is clicked
                    //so get Filter value and refreash page
                    if (filterOptions != null && filterOptions.size() > 0) {
                        mFilter = filterOptions.get(postion).getName();
                        videoList.clear();
                        adapter.notifyDataSetChanged();
                        onRefresh();
                    }
                    return false;
                }

            default:
                return super.onItemClicked(object1, screenType, postion);

        }
    }
}
