package com.sesolutions.ui.bookings;


import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.Bookings.ProfessionalResponse;
import com.sesolutions.responses.Bookings.ServiceResponse;
import com.sesolutions.responses.Courses.classroom.ClassroomResponse;
import com.sesolutions.responses.Courses.classroom.ClassroomVo;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.bookings.adapters.ServiceAdapter;
import com.sesolutions.ui.courses.adapters.ClassroomAdapter;

import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_SERVICE_BROWSE;

public class ProfileServices extends BookingHelper<ServiceAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TYPE_HOME = "eclassroom_main_home";
    public static final String TYPE_BROWSE = "eclassroom_main_browse";
    public static final String TYPE_CATEGORY = "eclassroom_main_categories";
    public static final String TYPE_FAVOURITE = "2";
    public static final String TYPE_LOCATIONS = "3";
    public static final String TYPE_FEATURED = "eclassroom_main_featured";
    public static final String TYPE_VERIFIED = "eclassroom_main_verified";
    public static final String TYPE_SPONSORED = "eclassroom_main_sponsored";
    public static final String TYPE_HOT = "eclassroom_main_hot";
    public static final String TYPE_MANAGE = "sesbusiness_main_manage";
    public static final String TYPE_PACKAGE = "sesbusiness_main_manage_package";
    public static final String TYPE_ALBUM_HOME = "9";
    public static final String TYPE_ALBUM_BROWSE = "sesbusiness_main_businessalbumbrowse";
    public static final String TYPE_ALBUM_BROWSE_CLASS = "eclassroom_main_albumbrowse";
    public static final String TYPE_VIDEO_BROWSE = "sesbusinessvideo_main_browsehome";
    public static final String TYPE_ASSOCIATE = "11";
    public static final String TYPE_CATEGORY_VIEW = "12";
    public static final String TYPE_SEARCH = "13";
    public static final String TYPE_SEARCH_MANAGE = "14";
    public static final String TYPE_CREATE = "eclassroom_main_create";
    public static final String TYPE_REVIEW_BROWSE = "eclassroom_main_review";
    public static final String TYPE_SEARCH2 = "eclassroom_main_albumbrowse";
    public ServiceResponse.Result result;
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


    //variable used when called from business view -> associated
    private int mBusinessId;


    public static ProfileServices newInstance(BookingParentFragment parent, int loggedInId, int categoryId) {
        ProfileServices frag = new ProfileServices();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static ProfileServices newInstance(BookingParentFragment parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static ProfileServices newInstance(String TYPE, BookingParentFragment parent) {
        ProfileServices frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }

    public static ProfileServices newInstance(String TYPE, int businessId) {
        ProfileServices frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.mBusinessId = businessId;
        return frag;
    }


    public static ProfileServices newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_follower, container, false);
        applyTheme(v);
        initScreenData();
        return v;
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

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        ((TextView) v.findViewById(R.id.tvTitle)).setText("Services");
        txtNoData = R.string.MSG_NO_SERVICE_FOUND;
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        switch (selectedScreen) {
            case TYPE_MANAGE:
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = Constant.URL_MANAGE_BUSINESS;
                break;
            case "profile_services":
                url = Constant.URL_PROFESSIONAL_PROFILE_SERVICES;
                break;

            default:
                url = URL_SERVICE_BROWSE;
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
            adapter = new ServiceAdapter(videoList, context, this, this);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
//            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
//            swipeRefreshLayout.setOnRefreshListener(this);
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

                if (null != selectedScreen) {
                    request.params.put("filter_sort", selectedScreen);
                }

                // used when this screen called from business view -> associated
                if (mBusinessId > 0) {
                    request.params.put(Constant.KEY_PROFESSIONAL_ID, mBusinessId);
                }// used when this screen called from business view -> associated
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
                Handler.Callback callback = msg -> {
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
                                    parent.onItemClicked(Constant.Events.SET_LOADED, TYPE_BROWSE, 0);
                                }
                                ServiceResponse resp = new Gson().fromJson(response, ServiceResponse.class);
                                //if screen is refreshed then clear previous data
                                if (req == Constant.REQ_CODE_REFRESH) {
                                    videoList.clear();
                                }

                                wasListEmpty = videoList.size() == 0;
                                result = resp.getResult();

                                if (null != result.getServices()) {
                                    videoList.addAll(result.getServices(selectedScreen));
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
        updateTitle();
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

    private void updateTitle() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText("Services" + " (" + result.getTotal() + ")");
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
