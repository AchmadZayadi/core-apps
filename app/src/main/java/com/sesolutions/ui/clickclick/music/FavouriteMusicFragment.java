package com.sesolutions.ui.clickclick.music;


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
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
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
import java.util.Map;

import static com.sesolutions.utils.URL.URL_GET_MUSIC;
import static com.sesolutions.utils.URL.URL_MUSIC_FAV;

public class FavouriteMusicFragment extends AddMusicHelper<DiscoverAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String selectedScreen = "";
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
    private ResultView result;
    private boolean isPlaying = false;
    private int playingId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.MSG_NO_ALBUM_MUSIC_SEARCH;

    }

    public void setRecyclerView() {
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
                HttpRequestVO request = new HttpRequestVO(URL_GET_MUSIC); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_TYPE, "favourite");
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
                                    MusicView resp = new Gson().fromJson(response, MusicView.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        albumsList.clear();
                                    }

                                    wasListEmpty = albumsList.size() == 0;
                                    result = resp.getResult();

                                    if (null != result.getMusics()) {
                                        albumsList.addAll(result.getMusics());
                                    }
                                    /*add category list */

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

    @Override
    public void onResume() {
        super.onResume();
        ((AddMusicActivity) activity).showTop();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    public void updateAdapter() {
        hideLoaders();
        for (int i = 0; i < albumsList.size(); i++) {
            if (isPlaying && albumsList.get(i).getMusicid() == playingId) {
                albumsList.get(i).setPlaying(true);
            } else {
                albumsList.get(i).setPlaying(false);
            }
        }
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_FAB_PLAY:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    isPlaying = true;
                    playingId = albumsList.get(postion).getMusicid();
                    albumsList.get(postion).setPlaying(true);
                    playMusic(albumsList.get(postion));
                    adapter.notifyDataSetChanged();
                    String duration = (String) screenType;
                    int songDuration = Integer.parseInt(duration);
                    handler.removeCallbacksAndMessages(null);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < albumsList.size(); i++) {
                                albumsList.get(i).setPlaying(false);
                            }
                            isPlaying = false;
                            ((AddMusicActivity) activity).stopMusicPlayer();
                            adapter.notifyDataSetChanged();
                        }
                    }, songDuration * 1000);
                    break;
                case Constant.Events.MUSIC_FAVOURITE:
                    callFavoriteApi(postion);
                    break;

                case Constant.Events.ADD_MUSIC:
                    Constant.songObj = null;
                    ((AddMusicActivity) activity).hideMusicLayout();
                    Constant.songObj = (com.sesolutions.responses.music.Albums) screenType;
                    Constant.musicid = postion;
                    Constant.songtitle = Constant.songObj.getTitle();
                    ((AddMusicActivity) activity).pause();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                    break;
                case Constant.Events.PRIVACY_CHANGED:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    isPlaying = false;
                    ((AddMusicActivity) activity).pause();
                    adapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);

    }

    private void callFavoriteApi(final int position) {

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
                                    if (message.equalsIgnoreCase("Item Unfavourite Successfully")) {
                                        albumsList.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }
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
}
