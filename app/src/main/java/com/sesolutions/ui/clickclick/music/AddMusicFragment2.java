package com.sesolutions.ui.clickclick.music;


import android.graphics.Color;
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
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.music.MusicView;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.ui.clickclick.discover.DiscoverAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_ALL_MUSIC;
import static com.sesolutions.utils.URL.URL_MUSIC_FAV;

public class AddMusicFragment2 extends AddMusicHelper<DiscoverAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public AppCompatTextView etStoreSearch;
    public String selectedScreen = "";
    public int size;
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public AddMusicAdapter adapter;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    private Handler handler = new Handler();
    public boolean isTag;
    public OnUserClickedListener<Integer, Object> parent;
    //variable used when called from page view -> associated
    private int mPageId;
    public ResultView result;
    private int selectedTab = 0;
    private List<Albums> profileList;
    public RelativeLayout rlSearchFilter;
    public RecyclerView recycleViewInfo;
    public MusicCategoryAdapter musicCategoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_add_music_list, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void init() {

        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.MSG_NO_ALBUM_MUSIC_SEARCH;

    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_FAVOURITE:
                    callLikeApi(postion);
                    break;
                case Constant.Events.MENU_MAIN:
                    for (int i = 0; i < profileList.size(); i++) {
                        profileList.get(i).setSelected(false);
                    }
                    musicCategoryAdapter.notifyDataSetChanged();
                    changeRecyclerView(postion);
                    break;
                case Constant.Events.MUSIC_FAB_PLAY:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    albumsList.get(postion).setPlaying(true);
                    playMusic(albumsList.get(postion));
                    adapter.notifyDataSetChanged();
                    String duration = (String) screenType;
                    int foo = Integer.parseInt(duration);
                    handler.removeCallbacksAndMessages(null);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < albumsList.size(); i++) {
                                albumsList.get(i).setPlaying(false);
                            }
                            ((AddMusicActivity) activity).stopMusicPlayer();
                            adapter.notifyDataSetChanged();
                        }
                    }, foo * 1000);
                    break;
                case Constant.Events.PRIVACY_CHANGED:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    ((AddMusicActivity) activity).pause();
                    adapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }


    public void changeRecyclerView(int pos) {
        try {
            selectedTab = pos;
            profileList.get(pos).setSelected(true);
            albumsList.clear();
            albumsList.addAll(result.getCategories().get(pos).getMusics().getResults());
            updateAdapter(true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callLikeApi(final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {

                    HttpRequestVO request = new HttpRequestVO(URL_MUSIC_FAV);

                    request.params.put(Constant.KEY_RESOURCE_ID, albumsList.get(position).getMusicid());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, "tickvideo_music");
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    JSONObject json = new JSONObject(response);
                                    String message = json.getString(Constant.KEY_RESULT);
                                    if (message.equalsIgnoreCase("Item Favourite Successfully")) {
                                        albumsList.get(position).setContentFavourite(true);
                                    } else {
                                        albumsList.get(position).setContentFavourite(false);
                                    }
                                    adapter.notifyItemChanged(position);
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void setRecyclerView(boolean fromSearch) {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new AddMusicAdapter(albumsList, context, this, this);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
            if (!fromSearch) {
                swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setOnRefreshListener(this);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView(false);
        callMusicAlbumApi(1, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    public void callMusicAlbumApi(final int req, boolean fromDiscover) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL_ALL_MUSIC); //url will change according to screenType
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
                                    MusicView resp = new Gson().fromJson(response, MusicView.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        albumsList.clear();
                                    }
                                    wasListEmpty = albumsList.size() == 0;
                                    result = resp.getResult();


                                    if (null != result.getCategories() && result.getCategories().size()>0) {
                                        if (selectedTab > 0) {
                                            if (null != result.getCategories().get(selectedTab).getMusics().getResults()) {
                                                albumsList.addAll(result.getCategories().get(selectedTab).getMusics().getResults());
                                            }
                                        } else {
                                            albumsList.addAll(result.getCategories().get(0).getMusics().getResults());
                                        }
                                    }
                                    updateAdapter(fromDiscover);
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

    public void updateAdapter(boolean fromdiscover) {
        hideLoaders();
//        if (fromdiscover) {
//            musicCategoryAdapter.notifyDataSetChanged();
//        }

        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
//        if (fromdiscover) {
//            runLayoutAnimation(recycleViewInfo);
//        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    private void setRecyclerViewProfileInfo() {
        try {
            recycleViewInfo = v.findViewById(R.id.rvCategories);

            if (null != result.getCategories() && result.getCategories().size() > 0) {
                recycleViewInfo.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                profileList = new ArrayList<>();
                profileList.addAll(result.getCategories());
                recycleViewInfo.setHasFixedSize(true);
                recycleViewInfo.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                musicCategoryAdapter = new MusicCategoryAdapter(profileList, context, this);
                recycleViewInfo.setAdapter(musicCategoryAdapter);
                recycleViewInfo.setNestedScrollingEnabled(false);
            } else {
                recycleViewInfo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, true);
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
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
