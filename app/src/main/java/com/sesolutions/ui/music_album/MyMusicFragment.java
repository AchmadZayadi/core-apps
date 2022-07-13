package com.sesolutions.ui.music_album;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.music.MusicBrowse;
import com.sesolutions.responses.music.Result;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMusicFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object> {

    private static final int CODE_ALBUM = 100;
    private static final int CODE_PLAYLISTS = 200;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private Result resultA;
    private Result resultP;
    private ProgressBar pb;
    private MusicParentFragment parent;
    //private List<Albums> videoList;
    private List<Albums> lists;
    private boolean isAlbumSelected;
    private int colorPrimary;
    private TextView tvAlbums;
    private TextView tvPlaylists;
    private boolean isAlbumLoaded;
    private boolean isPlaylistLoaded;
    public MyMusicAdapter adapter;
    public View v;
    private List<Albums> albumsList;
    private List<Albums> playList;
    private RelativeLayout hiddenPanel;
    private int menuButtonBackgroundColor;
    private int menuButtonTitleColor;
    private int menuButtonActiveTitleColor;
    private View vAlbum;
    private View vPlaylistM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_album, container, false);
        applyTheme(v);
        v.findViewById(R.id.llToggle).setVisibility(View.VISIBLE);
        menuButtonBackgroundColor = Color.parseColor(Constant.menuButtonBackgroundColor);
        menuButtonTitleColor = Color.parseColor(Constant.menuButtonTitleColor);
        menuButtonActiveTitleColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        return v;
    }

    private void toggleTab() {
        try {
            if (isAlbumSelected) {
                tvAlbums.setTextColor(menuButtonActiveTitleColor);
                tvAlbums.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setTextColor(menuButtonTitleColor);
                lists.clear();
                albumsList.clear();
                vPlaylistM.setBackgroundColor(menuButtonBackgroundColor);
                vAlbum.setBackgroundColor(menuButtonActiveTitleColor);

                callMusicAlbumApi(CODE_ALBUM, Constant.URL_BROWSE_MUSIC_ALBUM);
                /*if (!isAlbumLoaded) {


                } else {
                    updateAdapter();
                }*/
            } else {
                /// tvAlbums.setBackgroundColor(Color.WHITE);
                //tvAlbums.setTextAppearance(context, R.style.primary_diselected);
                tvAlbums.setBackgroundColor(menuButtonBackgroundColor);
                tvAlbums.setTextColor(menuButtonTitleColor);
                tvPlaylists.setBackgroundColor(menuButtonBackgroundColor);
                tvPlaylists.setTextColor(menuButtonActiveTitleColor);
                lists.clear();
                playList.clear();
                vAlbum.setBackgroundColor(menuButtonBackgroundColor);
                vPlaylistM.setBackgroundColor(menuButtonActiveTitleColor);

                callMusicAlbumApi(CODE_PLAYLISTS, Constant.URL_BROWSE_PLAYLIST);
              /*  if (!isPlaylistLoaded) {
                    lists.clear();
                    lists.addAll(playList);
                    callMusicAlbumApi(CODE_PLAYLISTS, Constant.URL_BROWSE_PLAYLIST);
                } else {
                    updateAdapter();
                }*/
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
            recyclerView = v.findViewById(R.id.rvSetting);
            pb = v.findViewById(R.id.pb);
            hiddenPanel = v.findViewById(R.id.hidden_panel);
            tvAlbums = v.findViewById(R.id.tvAlbum);
            tvPlaylists = v.findViewById(R.id.tvPlaylist);
            vAlbum = v.findViewById(R.id.vAlbum);
            vPlaylistM = v.findViewById(R.id.vPlaylistM);
            tvAlbums.setOnClickListener(this);
            tvPlaylists.setOnClickListener(this);
            hiddenPanel.setOnClickListener(this);
            ((SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout)).setEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //  }


        //initSlide();
    }

    private void setRecyclerView() {
        try {
            lists = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MyMusicAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvAlbum:
                    if (!isAlbumSelected) {
                        isAlbumSelected = true;
                        toggleTab();
                    }
                    break;
                case R.id.tvPlaylist:
                    if (isAlbumSelected) {
                        isAlbumSelected = false;
                        toggleTab();
                    }
                    break;

                case R.id.hidden_panel:
                    hideSlidePanel();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        if (!parent.isMyAlbumLoaded) {
            init();
            isAlbumSelected = true;
            isAlbumLoaded = false;
            isPlaylistLoaded = false;
            colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
            albumsList = new ArrayList<>();
            playList = new ArrayList<>();
            setRecyclerView();
            toggleTab();
        }
    }

    private void callMusicAlbumApi(final int req, String url) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    request.params.put(Constant.KEY_TYPE, Constant.VALUE_MANAGE);

                    if (isAlbumSelected)
                        request.params.put(Constant.KEY_PAGE, null != resultA ? resultA.getNextPage() : 1);
                    else
                        request.params.put(Constant.KEY_PAGE, null != resultP ? resultP.getNextPage() : 1);

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
                                        parent.isMyAlbumLoaded = true;

                                        MusicBrowse resp = new Gson().fromJson(response, MusicBrowse.class);


                                        if (isAlbumSelected) {
                                            isAlbumLoaded = true;
                                            resultA = resp.getResult();
                                            if (null != resultA.getAlbums()) {
                                                albumsList.addAll(resultA.getAlbums());
                                                lists.addAll(resultA.getAlbums());
                                            }
                                        } else {
                                            isPlaylistLoaded = true;
                                            resultP = resp.getResult();
                                            if (null != resultP.getPlaylists()) {
                                                playList.addAll(resultP.getPlaylists());
                                                lists.addAll(resultP.getPlaylists());
                                            }
                                        }


                                        if(lists.size()>0){
                                            recyclerView.setVisibility(View.VISIBLE);
                                        }else {
                                            recyclerView.setVisibility(View.GONE);
                                        }
                                        updateAdapter();
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public void showDeleteDialog(final int optionPosition, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(isAlbumSelected ? R.string.MSG_DELETE_CONFIRMATION_ALBUM : R.string.MSG_DELETE_CONFIRMATION_PLAYLIST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(optionPosition, position);
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

    private void callDeleteApi(final int listPosition, final int optionPosition) {

        try {
            if (isNetworkAvailable(context)) {


                try {
                    String url = isAlbumSelected ? Constant.URL_DELETE_MUSIC_ALBUM : Constant.URL_DELETE_MUSIC_PLAYLIST;
                    HttpRequestVO request = new HttpRequestVO(url);
                    if (isAlbumSelected) {
                        request.params.put(Constant.KEY_ALBUM_ID, lists.get(listPosition).getAlbumId());
                    } else {
                        request.params.put(Constant.KEY_PLAYLIST_ID, lists.get(listPosition).getPlaylistId());
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
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        // MusicBrowse resp = new Gson().fromJson(response, MusicBrowse.class);
                                        (isAlbumSelected ? albumsList : playList).remove(listPosition);
                                        lists.remove(listPosition);
                                        updateAdapter();
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
        pb.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(isAlbumSelected ? (R.string.MSG_NO_ALBUMS) :
                (R.string.MSG_NO_PLAYLIST));
        v.findViewById(R.id.llNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);

        adapter = new MyMusicAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
        recyclerView.setAdapter(adapter);

    }

    public static MyMusicFragment newInstance(MusicParentFragment parent) {
        MyMusicFragment frag = new MyMusicFragment();
        frag.parent = parent;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (isAlbumSelected && resultA != null && !isLoading) {
                if (resultA.getCurrentPage() < resultA.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, Constant.URL_BROWSE_MUSIC_ALBUM);
                }
            } else if (!isAlbumSelected && resultP != null && !isLoading) {
                if (resultP.getCurrentPage() < resultP.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, Constant.URL_BROWSE_PLAYLIST);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        CustomLog.d("" + value, "" + object1);
        switch (object1) {
            case Constant.Events.CLICKED_OPTION:
                // goToPostFeed();
                setFeedUpdateRecycleView(postion);
                slideUpDown();
                break;
            case Constant.Events.MUSIC_MAIN:
                if (isAlbumSelected) {
                    goToViewMusicAlbumFragment(albumsList.get(postion).getAlbumId());
                } else {
                    goToViewPlaylistFragment(playList.get(postion).getPlaylistId());
                }
                break;



            case Constant.Events.MUSIC_EDIT:
                Options vo12 = lists.get(Integer.parseInt("" + value)).getMenus().get(0);
                performMusicOptionClick( vo12, Integer.parseInt("" + value), postion);
                break;
            case Constant.Events.MUSIC_DELETE:
                Options vo121 = lists.get(Integer.parseInt("" + value)).getMenus().get(1);
                performMusicOptionClick( vo121, Integer.parseInt("" + value), postion);
                break;



            case Constant.Events.FEED_UPDATE_OPTION:
                slideUpDown();
                Options vo = lists.get(Integer.parseInt("" + value)).getMenus().get(postion);
                // int categoryId = lists.get(Integer.parseInt(value)).getCategoryId();
                performMusicOptionClick(vo, Integer.parseInt("" + value), postion);
                break;
        }
        return false;
    }

    public void goToViewPlaylistFragment(int playlistId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPlaylistFragment.newInstance(playlistId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewMusicAlbumFragment(int albumId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(albumId))
                .addToBackStack(null)
                .commit();
    }

    private void performMusicOptionClick(Options vo, int listPosition, int postion) {
        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_MODULE, Constant.ModuleName.MUSIC);
                if (isAlbumSelected) {
                    map.put(Constant.KEY_ALBUM_ID, lists.get(listPosition).getAlbumId());
                    gotoFormFragment(Constant.FormType.EDIT_MUSIC_ALBUM,
                            Constant.URL_EDIT_MUSIC_ALBUM, map);
                } else {
                    map.put(Constant.KEY_PLAYLIST_ID, lists.get(listPosition).getPlaylistId());
                    gotoFormFragment(Constant.FormType.EDIT_MUSIC_PLAYLIST, Constant.URL_EDIT_MUSIC_PLAYLIST, map);
                }
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(listPosition, postion);
                break;
        }
    }

    private void gotoFormFragment(int editAlbum, String url, Map<String, Object> map) {
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(editAlbum, map, url)).addToBackStack(null).commit();
    }

    public void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bootom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            // isPanelShown = true;
        } else {
            // Hide the Panel
            hideSlidePanel();
            // isPanelShown = false;
        }
    }

    public void hideSlidePanel() {
        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(context, R.anim.bootom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }
}
