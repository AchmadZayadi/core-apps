package com.sesolutions.ui.music_album;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.music.MusicView;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPlaylistFragment extends HelperFragment5 implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;

    private ResultView result;
    private ProgressBar pb;
    private AlbumView album;
    public ImageView ivCoverPhoto;
    public ImageView ivFavorite;
    RelativeLayout llfavSOngdata;
    // public TextView tvAlbumTitle;

    private int playlistId;
    private NestedScrollView mScrollView;
    private Bundle bundle;
    LinearLayoutCompat llStar;
    AppCompatButton bSignIn2;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (/*!openComment &&*/ bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivCoverPhoto.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            ivFavorite.setTransitionName(bundle.getString(Constant.Trans.ICON));
            collapsingToolbar.setTitle(bundle.getString(Constant.Trans.IMAGE));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform()/*.centerCrop()*/)
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                CustomLog.e("onLoadFailed", "onLoadFailed");
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                CustomLog.e("onResourceReady", "onResourceReady");
                                //  ivAlbumImage.setImageDrawable(resource);
                                startPostponedEnterTransition();
                                return false;
                            }
                        })
                        .into(ivCoverPhoto);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_playlist, container, false);
        applyTheme();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
        return v;
    }

    private void init() {

        recyclerView = v.findViewById(R.id.recyclerview);
        llStar = v.findViewById(R.id.llStar);
        bSignIn2 = v.findViewById(R.id.bSignIn2);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        llfavSOngdata = v.findViewById(R.id.llfavSOngdata);

        pb = v.findViewById(R.id.pb);

        ivFavorite = v.findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(this);
        llfavSOngdata.setOnClickListener(this);
        mScrollView = v.findViewById(R.id.mScrollView);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                        .getScrollY()));

                if (diff == 0) {
                    // your pagination code
                    loadMore();
                }
            }
        });

        llStar.setVisibility(View.GONE);
        initCollapsingToolbar();

        bSignIn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((CommonActivity) activity).playSong(result.getSongs());
                     ((CommonActivity) activity).songPicked(result.getSongs().get(0));
                    ((CommonActivity) activity).showMusicLayout();
                    //((CommonActivity) activity).playSong(result.getSongs().get(0));
                    Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_anim);
                    bSignIn2.startAnimation(myAnim);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private CollapsingToolbarLayout collapsingToolbar;
    TextView titleplaylistid,discritpionstid;
    Toolbar toolbar;
    private void initCollapsingToolbar() {
        toolbar = v.findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        titleplaylistid = v.findViewById(R.id.titleplaylistid);
        discritpionstid = v.findViewById(R.id.discritpionstid);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
    }

    public void hideLoaders() {
        isLoading = false;
        // setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu_music2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.option:
                View vItem = getActivity().findViewById(R.id.option);
                showPopup(album.getMenus(), vItem, 10);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MusicAlbumAdapter3(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((album.getMenus() != null && album.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateUpperLayout() {
        showHideOptionIcon();
        try {
            // ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
           // collapsingToolbar.setTitle(album.getTitle());

            titleplaylistid.setText(""+album.getTitle());
            discritpionstid.setText(album.getFavouriteCount()+" Favorite | "+album.getPlayCount()+" Play");
            Log.e("TITLENAME",""+album.getAlbumTitle());
            updateFavoriteIcon();
            //  tvAlbumTitle.setText(album.getTitle());
            Util.showImageWithGlide(ivCoverPhoto, album.getImages().getMain(), context, R.drawable.placeholder_square);


           /* if (result.getPermission().getCanEdit() == 1
                    || result.getLoggedinUserId() == album.getOwnerId()) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCoverPhoto).setOnClickListener(this);
            } else {
                v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageDrawable(ContextCompat.getDrawable(context, album.getIsContentFavourite() ? R.drawable.favraite_selected : R.drawable.favorite));
    }


    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SONG_PLAYLIST);
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.bSignIn2).setVisibility(albumsList.size() > 0 ? View.VISIBLE : View.GONE);
    }


    /*private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareInside(album.getShare(), true);
                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(album.getShare());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

             /*   case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;
*/
                case R.id.ivOption:
                    showPopup(album.getMenus(), view, 10);
                    break;

                case R.id.ivFavorite:
                case R.id.llfavSOngdata:
                    callFavoriteApi(Constant.URL_MUSIC_FAVORITE);
                    break;

               /* case R.id.llAlbumImage:
                    showPopup(album.getProfileImageOptions(), view, 100);
                    break;

                case R.id.ivCoverPhoto:
                    showPopup(album.getCoverImageOptions(), v.findViewById(R.id.ivCamera), 1000);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
        PopupMenu menu = new PopupMenu(context, v);
        for (int index = 0; index < menus.size(); index++) {
            Options s = menus.get(index);
            menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
        }
        menu.show();
        menu.setOnMenuItemClickListener(this);
    }


    private void callRemoveImageApi(String url) {


        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_PLAYLIST_ID, album.getPlaylistId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getPlaylistId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PLAYLIST);
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
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
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

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showView(v.findViewById(R.id.pbMain));
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_PLAYLIST_VIEW);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    request.params.put(Constant.KEY_RESOURCE_ID, playlistId);
                    request.params.put(Constant.KEY_PLAYLIST_ID, playlistId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PLAYLIST);
                    if (req == UPDATE_UPPER_LAYOUT) {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideLoaders();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        //  showView(mScrollView);dsfas
                                        MusicView resp = new Gson().fromJson(response, MusicView.class);
                                        result = resp.getResult();
                                        if (req == UPDATE_UPPER_LAYOUT) {
                                            album = result.getAlbums();
                                            updateUpperLayout();
                                        } else {
                                            if (null != result.getSongs())
                                                albumsList.addAll(result.getSongs());
                                            album = result.getPlaylist();
                                            updateUpperLayout();
                                            updateAdapter();
                                            //  callBottomCommentLikeApi(playlistId, Constant.ResourceType.PLAYLIST);
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideLoaders();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDetelePlaylistApi() {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {

                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_DELETE_PLAYLIST);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    request.params.put(Constant.KEY_RESOURCE_ID, playlistId);
                    request.params.put(Constant.KEY_PLAYLIST_ID, playlistId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PLAYLIST);

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
                                        activity.taskPerformed = Constant.TASK_PLAYLIST_DELETED;
                                        onBackPressed();
                                        // MusicView resp = new Gson().fromJson(response, MusicView.class);
                                        //result = resp.getResult();
                                       /* if (req == UPDATE_UPPER_LAYOUT) {
                                            album = result.getAlbums();
                                            updateUpperLayout();
                                        } else {
                                            videoList.addAll(result.getSongs());
                                            album = result.getPlaylist();
                                            updateUpperLayout();
                                            updateAdapter();
                                        }*/
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

    public static ViewPlaylistFragment newInstance(int albumId) {
        ViewPlaylistFragment frag = new ViewPlaylistFragment();
        frag.playlistId = albumId;
        return frag;
    }

    public static ViewPlaylistFragment newInstance(int albumId, Bundle bundle) {
        ViewPlaylistFragment frag = new ViewPlaylistFragment();
        frag.playlistId = albumId;
        frag.bundle = bundle;
        return frag;
    }

    @Override
    public void onLoadMore() {
    }

    public void loadMore() {
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
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            boolean isCover = false;
            Options opt;
            if (itemId > 1000) {
                isCover = true;
                itemId = itemId - 1000;
                opt = album.getCoverImageOptions().get(itemId - 1);

            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = album.getProfileImageOptions().get(itemId - 1);

            } else {
                itemId = itemId - 10;
                opt = album.getMenus().get(itemId - 1);
            }
            //  opt = album.getMenus().get();

            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment();
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case "download":
                    downloadfile(album.getSongUrl(),album.getTitle());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;
                case Constant.OptionType.remove_profile_photo:
                    showImageRemoveDialog(false, Constant.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_PHOTO);
                    break;
                case Constant.OptionType.view_profile_photo:
                    goToPhotoView(album.getImages().getMain());
                    break;
                case Constant.OptionType.CHOOSE_FROM_ALBUMS:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_PLAYLIST_ID, resourceId);
                    map.put(Constant.KEY_ID, resourceId);
                    openSelectAlbumFragment(isCover ? Constant.URL_MUSIC_UPLOAD_COVER : Constant.URL_MUSIC_UPLOAD_PHOTO, map);

                    break;
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_MUSIC_UPLOAD_PHOTO, album.getImages().getMain(), Constant.TITLE_EDIT_MUSIC_PHOTO);
                    break;
                case Constant.OptionType.upload_cover:
                    gToAlbumImage(Constant.URL_MUSIC_UPLOAD_COVER, album.getCover().getMain(), Constant.TITLE_EDIT_COVER);
                    break;
                case Constant.OptionType.view_cover_photo:
                    goToPhotoView(album.getCover().getMain());
                    break;
              /*  case Constant.OptionType.remove_cover_photo:
                    showImageRemoveDialog(true, Constant.MSG_COVER_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_COVER);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void downloadfile(String songurl,String songname) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songurl));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            //  request.setTitle(Constant.DOWNLOADING_IMAGE);
            // request.setDescription("Downloading " + "Image" + ".png");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" +"Semusic_" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp3");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);

            Util.showSnackbar(v, "Music File saved successfully.");
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PLAYLIST_ID, album.getPlaylistId());
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_MUSIC_PLAYLIST, map, Constant.URL_EDIT_MUSIC_PLAYLIST)).addToBackStack(null).commit();
    }

    private void goToReportFragment() {
        String guid = Constant.ResourceType.PLAYLIST + "_" + album.getPlaylistId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid,true)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }

    private void goToPhotoView(String main) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PhotoViewFragment.newInstance(main))
                .addToBackStack(null)
                .commit();
    }

    public void showDeleteDialog() {
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_PLAYLIST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDetelePlaylistApi();
                    //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

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

    public void showImageRemoveDialog(boolean isCover, String msg, final String url) {
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
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? Constant.TXT_REMOVE_COVER : Constant.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callRemoveImageApi(url);
                    //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

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

    private void callFavoriteApi(String url) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getPlaylistId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PLAYLIST);
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
                                        album.setIsContentFavourite(!album.getIsContentFavourite());
                                        updateFavoriteIcon();
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






}
