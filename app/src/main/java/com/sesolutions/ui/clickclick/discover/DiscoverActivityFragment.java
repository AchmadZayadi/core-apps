package com.sesolutions.ui.clickclick.discover;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.Events.MUSIC_CHANGED;
import static com.sesolutions.utils.Constant.Events.MUSIC_COMPLETED;
import static com.sesolutions.utils.Constant.Events.SEE_MORE;
import static com.sesolutions.utils.Constant.Events.STICKER_USER;
import static com.sesolutions.utils.URL.URL_ACT_HASH;
import static com.sesolutions.utils.URL.URL_DISCOVER_ACTIVITY;

public class DiscoverActivityFragment extends VideoHelper<DiscoverAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String selectedScreen = "";
    public AppCompatTextView tvHashtag;
    public AppCompatImageView ivHash;
    public AppCompatTextView tvActivity;
    public AppCompatImageView ivActivity;
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public boolean ishash = false;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public OnUserClickedListener<Integer, Object> parent;
    private int hashtagId = 0;
    public RelativeLayout rlSearchFilter;
    public AppCompatTextView etStoreSearch;
    private boolean isListEmpty = false;
    //variable used when called from page view -> associated
    private int mPageId;
    int menuTitleActiveColor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_discover, container, false);
        applyTheme(v);
        initScreenData();
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);

        tvActivity = v.findViewById(R.id.tvActivity);
        tvHashtag = v.findViewById(R.id.tvHashtag);
        ivActivity = v.findViewById(R.id.ivActivity);
        ivHash = v.findViewById(R.id.ivHash);
        rlSearchFilter = v.findViewById(R.id.rlSearchFilter);
        etStoreSearch = v.findViewById(R.id.etStoreSearch);
        etStoreSearch.setOnClickListener(this);
        tvHashtag.setOnClickListener(this);
        tvActivity.setOnClickListener(this);

        tvActivity.setTextColor(menuTitleActiveColor);
        ivActivity.setColorFilter(menuTitleActiveColor);
        ivHash.setColorFilter(menuTitleActiveColor);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.etStoreSearch:
//                goToSearchFragment()
                fragmentManager.beginTransaction().replace(R.id.container, new SearchDiscoverFragment()).addToBackStack(null).commit();
                break;
            case R.id.tvHashtag:
                if (!ishash) {
                    tvHashtag.setTextColor(menuTitleActiveColor);
                    tvHashtag.setTypeface(null, Typeface.BOLD);
                    tvActivity.setTextColor(Color.parseColor("#000000"));
                    tvActivity.setTypeface(null, Typeface.NORMAL);
                    ivActivity.setVisibility(View.GONE);
                    ivHash.setVisibility(View.VISIBLE);
                    ishash = true;
                    init();
                    setHashRecyclerView();
                    callHashtagApi(1);
                }
                break;
            case R.id.tvActivity:
                ivActivity.setVisibility(View.VISIBLE);
                ivHash.setVisibility(View.GONE);
                tvActivity.setTextColor(menuTitleActiveColor);
                tvActivity.setTypeface(null, Typeface.BOLD);
                tvHashtag.setTextColor(Color.parseColor("#000000"));
                tvHashtag.setTypeface(null, Typeface.NORMAL);
                if (ishash) {
                    ishash = false;
                    initScreenData();
                }
                break;
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case SEE_MORE:
                    try {
                        int videopos = Integer.parseInt(screenType.toString());
                        CustomLog.e("pos: ", "" + postion + "videopos: " + videopos);
                        if(videoList.size()>videopos){
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, ClickClickFragment.newInstance((List<Videos>) videoList.get(videopos).getValue(),
                                            true, postion, true)).addToBackStack(null)
                                    .commit();
                        }else {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, ClickClickFragment.newInstance((List<Videos>) videoList.get(videoList.size()-1).getValue(),
                                            true, postion, true)).addToBackStack(null)
                                    .commit();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case MUSIC_CHANGED:
                    fragmentManager.beginTransaction().replace(R.id.container, ClickClickFragment.newInstance((List<Videos>) videoList.get(postion).getValue(), true)).commit();
                    break;

                case MUSIC_COMPLETED:
                    fragmentManager.beginTransaction().
                            replace(R.id.container, ClickClickFragment.newInstance(hashtaglist.get(postion).getResult().getVideos(), true)).commit();
                break;
                case STICKER_USER:
                    CustomLog.e("pos:", "" + screenType.toString());
                    for (int i = 0; i < hashtaglist.size(); i++) {
                        for (int j = 0; j < hashtaglist.get(i).getResult().getVideos().size(); j++) {
                            if (hashtaglist.get(i).getResult().getVideos().get(j).getTitle().equalsIgnoreCase(screenType.toString())) {
                                hashtagId = i;
                                CustomLog.e("poss:", "" + hashtagId);
                            }
                        }
                    }

                    fragmentManager.beginTransaction().replace(R.id.container, ClickClickFragment.newInstance(hashtaglist.get(hashtagId).getResult().getVideos(), true, true, postion)).commit();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.MSG_NO_VIDEO;

    }

    public void setRecyclerView(boolean type) {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            if (type) {
                adapter3 = new SearchAdapter(videoList, context, this, this);
                adapter3.setType(selectedScreen);
                recyclerView.setAdapter(adapter3);
            } else {
                adapter = new DiscoverAdapter(videoList, context, this, this);
                adapter.setType(selectedScreen);
                recyclerView.setAdapter(adapter);
            }
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/

            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setHashRecyclerView() {
        try {
            hashtaglist = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter2 = new HashtagAdapter(hashtaglist, context, this, this);
            adapter2.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter2);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView(false);
        callMusicAlbumApi(1, false);
    }

    public void callMusicAlbumApi(final int req, boolean type) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL_DISCOVER_ACTIVITY); //url will change according to screenType
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
                    request.params.put("title", searchKey);
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
                                    VideoResponse resp = new Gson().fromJson(response, VideoResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();
                                    /*add category list */


                                    if (null != result.getRecently_created().getVideos() && result.getRecently_created().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getRecently_created().getVideos(), result.getRecently_created().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getRecently_created().getVideos(), result.getRecently_created().getTitle()));
                                        }
                                    }
                                    if (null != result.getMostViewed().getVideos() && result.getMostViewed().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getMostViewed().getVideos(), result.getMostViewed().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getMostViewed().getVideos(), result.getMostViewed().getTitle()));
                                        }
                                    }
                                    if (null != result.getMost_liked().getVideos() && result.getMost_liked().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getMost_liked().getVideos(), result.getMost_liked().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getMost_liked().getVideos(), result.getMost_liked().getTitle()));
                                        }
                                    }
                                    if (null != result.getMost_commented().getVideos() && result.getMost_commented().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getMost_commented().getVideos(), result.getMost_commented().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getMost_commented().getVideos(), result.getMost_commented().getTitle()));
                                        }
                                    }
                                    if (null != result.getFeatured().getVideos() && result.getFeatured().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getFeatured().getVideos(), result.getFeatured().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getFeatured().getVideos(), result.getFeatured().getTitle()));
                                        }
                                    }
                                    if (null != result.getHot().getVideos() && result.getHot().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getHot().getVideos(), result.getHot().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getHot().getVideos(), result.getHot().getTitle()));
                                        }
                                    }
                                    if (null != result.getSponsored().getVideos() && result.getSponsored().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getSponsored().getVideos(), result.getSponsored().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getSponsored().getVideos(), result.getSponsored().getTitle()));
                                        }
                                    }
                                    if (null != result.getMost_favourite().getVideos() && result.getMost_favourite().getVideos().size() > 0) {
                                        if (!type) {
                                            videoList.add(new VideoVo(adapter.VT_CATEGORY, result.getMost_favourite().getVideos(), result.getMost_favourite().getTitle()));
                                        } else {
                                            videoList.add(new VideoVo(adapter3.VT_CATEGORY, result.getMost_favourite().getVideos(), result.getMost_favourite().getTitle()));
                                        }
                                    }
                                    if (type) {
                                        updateAdapter(true);
                                    } else {
                                        updateAdapter(false);
                                    }
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

    public void callHashtagApi(final int req) {
        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL_ACT_HASH); //url will change according to screenType
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
                                    VideoResponse resp = new Gson().fromJson(response, VideoResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        hashtaglist.clear();
                                    }

                                    wasListEmpty = hashtaglist.size() == 0;
                                    result = resp.getResult();
                                    /*add category list */

                                    hashtaglist.addAll(result.getItems());

                                    updateHashAdapter();
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

    public void updateAdapter(boolean type) {
        hideLoaders();
        if (type) {
            adapter3.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        for (int i = 0; i < videoList.size(); i++) {
            if (videoList.get(i).getValue() != null) {
                isListEmpty = true;
            }
        }
        CustomLog.e("is empty? ", "" + isListEmpty);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    public void updateHashAdapter() {
        hideLoaders();
        adapter2.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(hashtaglist.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, false);
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
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH, false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
