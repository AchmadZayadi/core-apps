package com.sesolutions.ui.video;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyVideoFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "MyVideoFragment";

    private static final int CODE_ALBUM = 100;
    private static final int CODE_PLAYLISTS = 200;
    private static final int CODE_CHANNEL = 300;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private String searchKey;
    private Result result;
    private ProgressBar progressBar;
    private OnUserClickedListener<Integer, Object> listener;
    //private List<Albums> videoList;
    private List<Videos> lists;
    private boolean isVideoSelected = true;
    private boolean isChannelSelected;
    private boolean isPlaylistSelected;
    private TextView tvVideos;
    private TextView tvChannel;
    private TextView tvPlaylists;
    private View vChannel;
    private View vVideo;
    private View vPlaylistV;
    public MyVideoAdapter adapter;
    public View v;
    private String msgNoData;
    private int menuButtonBackgroundColor;
    private int menuButtonTitleColor;
    private int menuButtonActiveTitleColor;
    private String selectedScreen;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onResume() {
        super.onResume();
        if(BaseActivity.backcoverchange == Constant.TASK_DELETE_Video){
            try {
                toggleTab();
                activity.backcoverchange=0;
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }else if(BaseActivity.backcoverchange == Constant.TASK_DELETE_CHANNEL){
            try {
                toggleTab();
                activity.backcoverchange=0;
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_album, container, false);
        applyTheme(v);
        swipeRefreshLayout=v.findViewById(R.id.swipeRefreshLayout);
        //  v.findViewById(R.id.swipeRefreshLayout).setEnabled(false);
        menuButtonBackgroundColor = Color.parseColor(Constant.menuButtonBackgroundColor);
        menuButtonTitleColor = Color.parseColor(Constant.menuButtonTitleColor);
        menuButtonActiveTitleColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);
        return v;
    }


    @Override
    public void onRefresh() {
        try {
            toggleTab();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void toggleTab() {
        try {
            if (isVideoSelected) {
                tvVideos.setTextColor(menuButtonActiveTitleColor);
                tvVideos.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setBackgroundColor(menuButtonBackgroundColor);
                tvChannel.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setTextColor(menuButtonTitleColor);
                tvChannel.setTextColor(menuButtonTitleColor);
                msgNoData = Constant.MSG_NO_VIDEO_BY_YOU;

                vChannel.setBackgroundColor(menuButtonBackgroundColor);
                vVideo.setBackgroundColor(menuButtonActiveTitleColor);
                vPlaylistV.setBackgroundColor(menuButtonBackgroundColor);

                callMusicAlbumApi(CODE_ALBUM, Constant.URL_VIDEO_BROWSE);

            } else if (isPlaylistSelected) {
                tvVideos.setBackgroundColor(menuButtonBackgroundColor);
                tvVideos.setTextColor(menuButtonTitleColor);
                tvPlaylists.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setTextColor(menuButtonActiveTitleColor);
                tvChannel.setBackgroundColor(menuButtonBackgroundColor);
                tvChannel.setTextColor(menuButtonTitleColor);
                msgNoData = Constant.MSG_NO_PLAYLIST;

                vChannel.setBackgroundColor(menuButtonBackgroundColor);
                vVideo.setBackgroundColor(menuButtonBackgroundColor);
                vPlaylistV.setBackgroundColor(menuButtonActiveTitleColor);

                callMusicAlbumApi(CODE_PLAYLISTS, Constant.URL_BROWSE_PLAYLIST_VIDEO);
            } else if (isChannelSelected) {
                tvVideos.setBackgroundColor(menuButtonBackgroundColor);
                tvVideos.setTextColor(menuButtonTitleColor);
                tvPlaylists.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setTextColor(menuButtonTitleColor);
                tvChannel.setBackgroundColor(menuButtonBackgroundColor);
                tvChannel.setTextColor(menuButtonActiveTitleColor);
                msgNoData = Constant.MSG_NO_CHANNEL;

                vChannel.setBackgroundColor(menuButtonActiveTitleColor);
                vVideo.setBackgroundColor(menuButtonBackgroundColor);
                vPlaylistV.setBackgroundColor(menuButtonBackgroundColor);

                callMusicAlbumApi(CODE_CHANNEL, Constant.URL_CHANNEL_BROWSE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setFeedUpdateRecycleView(int position) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(lists.get(position).getMenus(), context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        try {
            v.findViewById(R.id.llToggle).setVisibility(View.GONE);

            //hide tab for playlist and channel if core plugin is enabled
            v.findViewById(R.id.llToggleV).setVisibility(ModuleUtil.getInstance().isCoreVideoEnabled(context) ? View.GONE : View.VISIBLE);

            recyclerView = v.findViewById(R.id.rvSetting);
            progressBar = v.findViewById(R.id.pb);
            tvVideos = v.findViewById(R.id.tvVideos);
            tvPlaylists = v.findViewById(R.id.tvPlaylists);
            tvChannel = v.findViewById(R.id.tvChannel);
            vChannel = v.findViewById(R.id.vChannel);
            vVideo = v.findViewById(R.id.vVideo);
            vPlaylistV = v.findViewById(R.id.vPlaylistV);
            tvVideos.setOnClickListener(this);
            tvChannel.setOnClickListener(this);
            tvPlaylists.setOnClickListener(this);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView() {
        try {
            lists = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MyVideoAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvVideos:
                    if (!isVideoSelected) {
                        isVideoSelected = true;
                        isPlaylistSelected = false;
                        isChannelSelected = false;
                        toggleTab();
                    }
                    break;
                case R.id.tvPlaylists:
                    if (!isPlaylistSelected) {
                        isPlaylistSelected = true;
                        isVideoSelected = false;
                        isChannelSelected = false;
                        toggleTab();
                    }
                    break;

                case R.id.tvChannel:
                    if (!isChannelSelected) {
                        isChannelSelected = true;
                        isPlaylistSelected = false;
                        isVideoSelected = false;
                        toggleTab();
                    }
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        toggleTab();
    }

    public void initScreenData(int subType) {
        isVideoSelected = subType == 0;
        isChannelSelected = subType == 1;
        isPlaylistSelected = subType == 2;
        initScreenData();
    }

    private void callMusicAlbumApi(final int req, String url) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;

                swipeRefreshLayout.setRefreshing(false);
                try {
                    if (req == REQ_LOAD_MORE) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        result = null;
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_TYPE, Constant.VALUE_MANAGE);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            lists.clear();

                            try {
                                String response = (String) msg.obj;
                                isLoading = false;
                                swipeRefreshLayout.setRefreshing(false);
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult();
                                        if (null != result.getVideos()) {
                                            lists.addAll(result.getVideos());
                                        } else if (null != result.getPlaylists()) {
                                            lists.addAll(result.getPlaylists());
                                        } else if (null != result.getChannels()) {
                                            lists.addAll(result.getChannels());
                                        }

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }



                                updateAdapter();

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
                    progressBar.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

                progressBar.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }


    }


    //public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {

    public void showDeleteDialog(final int listPostion) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            String postString = "?";
            if (isVideoSelected) postString = "video?";
            if (isPlaylistSelected) postString = "playlist?";
            if (isChannelSelected) postString = "channel?";
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_PRE + postString);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(listPostion);
                    //callSaveFeedApi( Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int listPostion) {

        try {
            if (isNetworkAvailable(context)) {


                try {
                    String url = "";
                    String key = "";
                    int value;
                    if (isVideoSelected) {
                        url = Constant.URL_DELETE_VIDEO;
                        key = Constant.KEY_VIDEO_ID;
                        value = lists.get(listPostion).getVideoId();
                    } else if (isPlaylistSelected) {
                        url = Constant.URL_DELETE_VIDEO_PLAYLIST;
                        key = Constant.KEY_PLAYLIST_ID;
                        value = lists.get(listPostion).getPlaylistId();

                    } else {
                        url = Constant.URL_DELETE_CHANNEL;
                        key = Constant.KEY_CHANNEL_ID;
                        value = lists.get(listPostion).getChannelId();
                    }


                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(key, value);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;


                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        lists.remove(listPostion);
                                        updateAdapter();
                                        //VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);


                                        // updateAdapter();
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
                        }
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

    private void updateAdapter() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        //  v.findViewById(R.id.swipeRefreshLayout).setEnabled(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(msgNoData);
        //   v.findViewById(R.id.swipeRefreshLayout).setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);

        v.findViewById(R.id.swipeRefreshLayout).setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.llNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.tvNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);

        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
           /* if(isVideoSelected){
                listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, lists.size());
            }else {
                listener.onItemClicked(Constant.Events.SET_MYVIDEOCHANNEL, selectedScreen, result.getTotal());
            }*/
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
                listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, lists.size());
            }
        }
    }

    public static MyVideoFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        MyVideoFragment frag = new MyVideoFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    String url = "";
                    if (isPlaylistSelected) url = Constant.URL_BROWSE_PLAYLIST_VIDEO;
                    if (isVideoSelected) url = Constant.URL_VIDEO_BROWSE;
                    if (isChannelSelected) url = Constant.URL_CHANNEL_BROWSE;
                    callMusicAlbumApi(REQ_LOAD_MORE, url);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        CustomLog.e("My video" + value, "" + object1);
        switch (object1) {
            case Constant.Events.SUCCESS:
               break;
            case  Constant.FormType.KEY_EDIT_VIDEO:
            case  Constant.FormType.TYPE_EDIT_CHANNEL:
                try {
                    toggleTab();
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;


            case Constant.Events.CLICKED_OPTION:
                setFeedUpdateRecycleView(postion);

                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                Options vo = lists.get(Integer.parseInt("" + value)).getMenus().get(postion);
                int categoryId = lists.get(Integer.parseInt("" + value)).getCategoryId();
                performMusicOptionClick(categoryId, vo, Integer.parseInt("" + value), postion);
                break;
            case Constant.Events.MUSIC_MAIN:
                if (isChannelSelected) {
                    goToViewChannelFragment(lists.get(postion).getChannelId());
                } else if (isPlaylistSelected) {
                    goToViewPlaylistFragment(lists.get(postion).getPlaylistId());
                } else if (isVideoSelected) {
                    goToViewVideoFragment(lists.get(postion).getVideoId());
                }
                break;
            case Constant.Events.MUSIC_EDIT:
                Options vo12 = lists.get(Integer.parseInt("" + value)).getMenus().get(0);
                int categoryId12 = lists.get(Integer.parseInt("" + value)).getCategoryId();
                performMusicOptionClick(categoryId12, vo12, Integer.parseInt("" + value), postion);
                break;
            case Constant.Events.MUSIC_DELETE:
                Options vo121 = lists.get(Integer.parseInt("" + value)).getMenus().get(1);
                int categoryId121 = lists.get(Integer.parseInt("" + value)).getCategoryId();
                performMusicOptionClick(categoryId121, vo121, Integer.parseInt("" + value), postion);
                break;

        }
        return false;
    }

    private void goToViewChannelFragment(int channelId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewChannelFragment.newInstance(channelId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewVideoFragment(int videoId) {
        goTo(Constant.GoTo.VIDEO, videoId);
       /* fragmentManager.beginTransaction()
                .replace(R.id.container, ViewVideoFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();*/
    }

    private void goToViewPlaylistFragment(int playlist) {
        goTo(Constant.GoTo.VIEW_VIDEO_PLAYLIST, playlist);
       /* fragmentManager.beginTransaction()
                .replace(R.id.container, ViewPlaylistVideoFragment.newInstance(playlist, null, null))
                .addToBackStack(null)
                .commit();*/
    }

    private void performMusicOptionClick(int categoryId, Options vo, int listPosition, int postion) {
        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                String url = Constant.EMPTY;
                int type = 0;
                if (isPlaylistSelected) {
                    map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
                    map.put(Constant.KEY_PLAYLIST_ID, lists.get(listPosition).getPlaylistId());
                    url = Constant.URL_MUSIC_EDIT_PLAYLIST;
                    type = Constant.FormType.TYPE_PLAYLIST_VIDEO;
                }
                if (isChannelSelected) {
                    map.put(Constant.KEY_CHANNEL_ID, lists.get(listPosition).getChannelId());
                    url = Constant.URL_EDIT_CHANNEL;
                    type = Constant.FormType.TYPE_EDIT_CHANNEL;
                }
                if (isVideoSelected) {
                    map.put(Constant.KEY_VIDEO_ID, lists.get(listPosition).getVideoId());
                    url = Constant.URL_EDIT_VIDEO;
                    type = Constant.FormType.KEY_EDIT_VIDEO;
                }
                map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
                gotoFormFragment(type, map, url);
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(listPosition);
                break;
        }
    }

    private void gotoFormFragment(int editAlbum, Map<String, Object> map, String url) {
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(editAlbum, map, url,this)).addToBackStack(null).commit();
    }



}
