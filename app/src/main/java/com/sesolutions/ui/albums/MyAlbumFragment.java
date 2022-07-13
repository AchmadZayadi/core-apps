package com.sesolutions.ui.albums;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.Result;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAlbumFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object> {

    private static final int CODE_ALBUM = 100;
    private static final int CODE_PLAYLISTS = 200;
    public MyAlbumAdapter adapter;
    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private String searchKey;
    private Result resultA;
    private ProgressBar pb;
    private OnUserClickedListener<Integer, Object> listener;
    private List<Albums> lists;
    private boolean isAlbumSelected;
    private int colorPrimary;
    private TextView tvAlbums;
    private TextView tvPlaylists;
    private boolean isAlbumLoaded;
    private boolean isPlaylistLoaded;
    private List<Albums> albumsList;
    private RelativeLayout hiddenPanel;
    private int loggedinId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedScreen;

    public static MyAlbumFragment newInstance(AlbumParentFragment parent, String selectedScreen) {
        MyAlbumFragment frag = new MyAlbumFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_album, container, false);
        applyTheme(v);
        //  new ThemeManager().applyTheme((ViewGroup) v, context);
        return v;
    }

    private void setFeedUpdateRecycleView(int position) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(resultA.getMenus(), context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        resultA = null;
        init();
        isAlbumSelected = true;
        colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
        albumsList = new ArrayList<>();
        loggedinId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        setRecyclerView();
        callMusicAlbumApi(CODE_ALBUM, Constant.URL_MY_ALBUM);
    }

    private void init() {
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);

        // v = getView();
        // if (!((MusicParentFragment) getParentFragment()).isBlogLoaded) {

        try {
            recyclerView = v.findViewById(R.id.rvSetting);
            v.findViewById(R.id.llToggle).setVisibility(View.GONE);
            pb = v.findViewById(R.id.pb);
            hiddenPanel = v.findViewById(R.id.hidden_panel);
            tvAlbums = v.findViewById(R.id.tvAlbum);
            tvPlaylists = v.findViewById(R.id.tvPlaylist);
            //  tvAlbums.setOnClickListener(this);
            //   tvPlaylists.setOnClickListener(this);
            hiddenPanel.setOnClickListener(this);
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
            adapter = new MyAlbumAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.hidden_panel:
                    hideSlidePanel();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callMusicAlbumApi(final int req, String url) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == CODE_ALBUM) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    request.params.put(Constant.KEY_TYPE, Constant.VALUE_MANAGE);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    request.params.put(Constant.KEY_PAGE, null != resultA ? resultA.getNextPage() : 1);
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
                                hideLoaders();

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            albumsList.clear();
                                            lists.clear();
                                        }
                                        AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);

                                        isAlbumLoaded = true;
                                        resultA = resp.getResult();
                                        if (null != resultA.getAlbums()) {
                                            albumsList.addAll(resultA.getAlbums());
                                            lists.addAll(resultA.getAlbums());
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
                    hideLoaders();

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


    //public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    public void showDeleteDialog(final int albumId, final int position) {
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_ALBUM);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(albumId, position);
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

    private void callDeleteApi(final int albumId, final int position) {

        try {
            if (isNetworkAvailable(context)) {

                lists.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, lists.size());

                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "album/delete/" + albumId + Constant.POST_URL);
                    // request.params.put(Constant.KEY_ALBUM_ID, albumId);

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

                                        // Util.showSnackbar(v, new JSONObject(response).getString("result"));
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
        hideLoaders();

        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);

        ((TextView) v.findViewById(R.id.tvNoData)).setText(isAlbumSelected ? Constant.MSG_NO_ALBUMS :
                Constant.MSG_NO_PLAYLIST);
        Log.e("albumselected",""+isAlbumSelected);
        Log.e("listSize",""+lists.size());
        recyclerView.setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.llNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setVisibility(lists.size()>0 ? View.VISIBLE:View.GONE);
        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, resultA.getTotal());
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (isAlbumSelected && resultA != null && !isLoading) {
                if (resultA.getCurrentPage() < resultA.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, Constant.URL_MY_ALBUM);
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
                //setFeedUpdateRecycleView(postion);
                //slideUpDown();
                Util.showOptionsPopUp((View) value, postion, resultA.getMenus(), this);
                break;

            case Constant.Events.MUSIC_MAIN:
                goToViewAlbumFragment(value, postion);
                // goToViewAlbumFragment(lists.get(postion).getAlbumId());
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                //slideUpDown();
                Options vo = resultA.getMenus().get(postion);
                int albumId = lists.get(Integer.parseInt("" + value)).getAlbumId();
                performMusicOptionClick(albumId, vo, Integer.parseInt("" + value), postion);
                break;
        }
        return false;
    }

    private void goToViewAlbumFragment(Object view, int position) {

        try {
            MyAlbumAdapter.ContactHolder holder = (MyAlbumAdapter.ContactHolder) view;
            String transitionName = lists.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
            ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);

            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, lists.get(position).getImages().getMain());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewAlbumFragment.newInstance(lists.get(position).getAlbumId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToViewAlbumFragment(lists.get(position).getAlbumId(), false);
        }

    }

    private void performMusicOptionClick(int albumId, Options vo, int listPosition, int postion) {
        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                gotoFormFragment(Constant.FormType.EDIT_ALBUM, albumId
                        , Constant.BASE_URL + Constant.URL_EDIT_ALBUM + albumId + Constant.POST_URL
                        , lists.get(listPosition).getAlbumId());
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(lists.get(listPosition).getAlbumId(), listPosition);
                break;
        }
    }

    private void gotoFormFragment(int editAlbum, int categoryId, String url, int albumId) {

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_MODULE, Constant.ModuleName.ALBUM);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(editAlbum, map, url, albumId)).addToBackStack(null).commit();
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

    private void hideSlidePanel() {
        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(context, R.anim.bootom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH, Constant.URL_MY_ALBUM);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
