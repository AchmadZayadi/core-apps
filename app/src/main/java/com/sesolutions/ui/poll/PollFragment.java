package com.sesolutions.ui.poll;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.sesolutions.responses.poll.PollResponse;
import com.sesolutions.ui.groups.GroupParentFragment;
import com.sesolutions.ui.groups.ViewGroupFragment;
import com.sesolutions.ui.page.PageParentFragment;
import com.sesolutions.ui.page.ViewPageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class PollFragment extends PollHelper<PollAdapter> implements SwipeRefreshLayout.OnRefreshListener {

    public OnUserClickedListener<Integer, Object> parent;
    public String mSort;
    public String selectedScreen=MenuTab.Page.TYPE_BROWSE_POLL;
    public String searchKey;
    public int loggedinId;
    public PollResponse.Result result;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public boolean isTag;
    public Map<String, Object> requestMap;

    public static PollFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        PollFragment frag = new PollFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static PollFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static PollFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        PollFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        txtNoData = R.string.MSG_NO_POLL_FOUND;
        applyTheme(v);
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);

        try {
            if (selectedScreen.equals(MenuTab.Poll.TYPE_MANAGE)) {
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getUserId();
            }
            pb = v.findViewById(R.id.pb);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        setUpModuleData();
    }

    public String URL, URL_CREATE,URL_FILTER,URL_POLL_GIF;

    public void setUpModuleData() {
        switch (selectedScreen) {
            case MenuTab.Page.TYPE_BROWSE_POLL:
                URL = Constant.URL_PAGE_POLLS;
                URL_FILTER = Constant.URL_PAGE_POLL_FILTER;
                URL_POLL_LIKE = Constant.URL_PAGE_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_PAGE_POLL_FAVORITE;
                break;
            case MenuTab.Group.TYPE_BROWSE_POLL:
                URL = Constant.URL_GROUP_POLLS;
                URL_FILTER = Constant.URL_GROUP_POLL_FILTER;
                URL_POLL_LIKE = Constant.URL_GROUP_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_GROUP_POLL_FAVORITE;
                break;
            case MenuTab.Business.TYPE_BROWSE_POLL:
                URL = Constant.URL_BUSINESS_POLLS;
                URL_FILTER = Constant.URL_BUSINESS_POLL_FILTER;
                URL_POLL_LIKE = Constant.URL_BUSINESS_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_BUSINESS_POLL_FAVORITE;
                break;
            case MenuTab.Page.TYPE_PROFILE_POLL:
                URL = Constant.URL_PAGE_PROFILE_POLLS;
                URL_CREATE = Constant.URL_PAGE_POLL_CREATE;
                URL_POLL_LIKE = Constant.URL_PAGE_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_PAGE_POLL_FAVORITE;
                break;
            case MenuTab.Group.TYPE_PROFILE_POLL:
                URL = Constant.URL_GROUP_PROFILE_POLLS;
                URL_CREATE = Constant.URL_GROUP_POLL_CREATE;
                URL_POLL_LIKE = Constant.URL_GROUP_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_GROUP_POLL_FAVORITE;
                URL_POLL_GIF = Constant.URL_GROUP_POLL_GIF;
                break;
            case MenuTab.Business.TYPE_PROFILE_POLL:
                URL = Constant.URL_BUSINESS_PROFILE_POLLS;
                URL_CREATE = Constant.URL_BUSINESS_POLL_CREATE;
                URL_POLL_LIKE = Constant.URL_BUSINESS_POLL_LIKE;
                URL_POLL_FAVORITE = Constant.URL_BUSINESS_POLL_FAVORITE;
                URL_POLL_GIF = Constant.URL_BUSINESS_POLL_GIF;
                break;
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PollAdapter(videoList, context, this);
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

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
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
                    HttpRequestVO request = new HttpRequestVO(URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    //put all data paased from previous screen
                    if (null != requestMap) {
                        request.params.putAll(requestMap);
                    }

                    //add all search parameters and values
                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    }

                    if (!TextUtils.isEmpty(mSort)) {
                        request.params.put("sort", mSort);
                    }

                    //add all search parameters and values
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
                                PollResponse resp = new Gson().fromJson(response, PollResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    if (null != parent) {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
                                    }
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }
                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();
                                    if (null != result.getPolls())
                                        videoList.addAll(result.getPolls());
                                    updateUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    //goIfPermissionDenied(resp.getError());
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
                    hideBaseLoader();
                }

            } else {
                hideLoaders();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void updateUpperLayout() {
        //override this method on child class
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
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
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case Constant.Events.LOAD_MORE:
                onLoadMore();
                break;
            case Constant.Events.CLICKED_HEADER_IMAGE3:

                Log.e("selectedscreen",""+selectedScreen);
                switch (selectedScreen) {
                    case MenuTab.Page.TYPE_BROWSE_POLL:
                        fragmentManager.beginTransaction()
                                .replace(R.id.container
                                        , ViewPageFragment.newInstance(videoList.get(postion).getPageId()))
                                .addToBackStack(null)
                                .commit();
                        break;
                    case MenuTab.Group.TYPE_BROWSE_POLL:
                          fragmentManager.beginTransaction().replace(R.id.container, ViewGroupFragment.newInstance(videoList.get(postion).getGroupId())).addToBackStack(null).commit();
                         break;
                    case MenuTab.Business.TYPE_BROWSE_POLL:
                        break;
                    case MenuTab.Page.TYPE_PROFILE_POLL:
                        break;
                    case MenuTab.Group.TYPE_PROFILE_POLL:
                        break;
                    case MenuTab.Business.TYPE_PROFILE_POLL:
                        break;
                }
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    @Override
    public void openViewPollFragment(int pollId,String sharemsg) {
        openViewPollFragment(selectedScreen, pollId,sharemsg);
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
  /*  private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
