package com.sesolutions.ui.multistore;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.group_core.CGroupGuestFragment;
import com.sesolutions.ui.group_core.CGroupInfoFragment;
import com.sesolutions.ui.group_core.CGroupPhotoFragment;
import com.sesolutions.ui.group_core.CoreGroupDiscussionFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultistoreMyListingPageFragment extends MultiStoreHelper implements View.OnClickListener, OnLoadMoreListener{

    RecyclerView recyclerview_mylisting;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public String searchKey;
    public CommonResponse.Result result;
    public int loggedinId;
    public int categoryId;
    public int userId;
    private int txtNoMsg = R.string.MSG_NO_RECIPE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_mystorelisting, container, false);
        applyTheme(v);
        v.findViewById(R.id.appBar).setVisibility(View.GONE);

        try {
            recyclerview_mylisting = v.findViewById(R.id.recyclerview_mylisting);
            videoList = new ArrayList<>();
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerview_mylisting.setLayoutManager(layoutManager);
            adapter = new MultiStoreAdapter(videoList, context, this, this,  Constant.FormType.BROWSE_MULTISTORE_MYLISTING);
            adapter.setLoggedInId(loggedinId);
            recyclerview_mylisting.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }

        callMusicAlbumApi(1);

        /*if loggedinid > 0 then this myRecipe screen otherwise it is browse recipe screen*/
        //txtNoMsg = loggedinId > 0 ? R.string.MSG_NO_RECIPE_CREATED_YOU : R.string.MSG_NO_RECIPE_CREATED;
        return v;
    }


    public void setRecyclerView() {

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public void initScreenData() {
      //  init();

    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_RECIPE_BROWSE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);

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
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (null != parent) {
                                            if (loggedinId == 0) {
                                                parent.onItemClicked(Constant.Events.SET_LOADED, null, 0);
                                            } else {
                                                parent.onItemClicked(Constant.Events.SET_LOADED, null, 2);
                                            }
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();
                                        menuItem = result.getMenus();
                                        if (null != result.getRecipies())
                                            videoList.addAll(result.getRecipies());

                                        updateAdapter();
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
                    isLoading = false;
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

               notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
           CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateAdapter() {
        isLoading = false;
         //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerview_mylisting);


        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoMsg);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            int index = loggedinId != 0 ? 2 : 0;
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, index, result.getTotal());
        }
    }

    public static MultistoreMyListingPageFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        MultistoreMyListingPageFragment frag = new MultistoreMyListingPageFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static MultistoreMyListingPageFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static MultistoreMyListingPageFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
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





}
