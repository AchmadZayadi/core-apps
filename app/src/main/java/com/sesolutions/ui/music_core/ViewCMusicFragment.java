package com.sesolutions.ui.music_core;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCMusicFragment extends CMusicHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ResultView result;
    private ProgressBar pb;
    private AlbumView album;
    //public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;
    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage;
    public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;


    private int albumId;
    private NestedScrollView mScrollView;
    private boolean isLoggedIn;
    private boolean openComment;
    private Bundle bundle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivAlbumImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            tvAlbumTitle.setText(bundle.getString(Constant.Trans.IMAGE));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                startPostponedEnterTransition();
                                return false;
                            }
                        })
                        .into(ivAlbumImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_view_3, container, false);
        try {
            applyTheme();
            init();
            setRecyclerView();
            callMusicAlbumApi(1);
            if (openComment) {
                //change value of openComment otherwise it prevents coming back from next screen
                openComment = false;
                goToCommentFragment(albumId, Constant.ATTACHMENT_TYPE_MUSIC_ALBUM);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.TASK_SONG_DELETED) {
            result = null;
            albumsList.clear();
            callMusicAlbumApi(1);
        }
    }

    private void init() {

        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        recyclerView = v.findViewById(R.id.recyclerview);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        ivAlbumImage = v.findViewById(R.id.ivAlbumImage);
        tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);

        /*tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
        tvFavorite = v.findViewById(R.id.tvFavorite);
        tvComment = v.findViewById(R.id.tvComment);
        tvImageComment = v.findViewById(R.id.tvImageComment);
        ivImageLike = v.findViewById(R.id.ivImageLike);
        tvLike = v.findViewById(R.id.tvLike);*/
        tvUserTitle = v.findViewById(R.id.tvUserTitle);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);
        pb = v.findViewById(R.id.pb);
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
        //   v.findViewById(R.id.ivShare).setOnClickListener(this);
        //  v.findViewById(R.id.ivOption).setOnClickListener(this);

        v.findViewById(R.id.ivStar1).setOnClickListener(this);
        v.findViewById(R.id.ivStar2).setOnClickListener(this);
        v.findViewById(R.id.ivStar3).setOnClickListener(this);
        v.findViewById(R.id.ivStar4).setOnClickListener(this);
        v.findViewById(R.id.ivStar5).setOnClickListener(this);

        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);


        ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);

        tvAlbumDetail.setTypeface(iconFont);

        mScrollView = v.findViewById(R.id.mScrollView);
        //   setListner();

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

            int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                    .getScrollY()));

            if (diff == 0) {
                // your pagination code
                loadMore();
            }
        });
        initCollapsingToolbar();
    }

    private CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
        // toolbar.setTitle("");
        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (album != null) {
                        collapsingToolbar.setTitle(album.getTitle());
                    }
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                showShareDialog(album.getShare());
                break;
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
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CMusicAdapter(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
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


            tvAlbumTitle.setText(album.getTitle());
            Util.showImageWithGlide(ivAlbumImage, album.getImageUrl(), context, R.drawable.placeholder_square);
            Util.showImageWithGlide(ivCoverPhoto, album.getImages().getMain(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(album.getUserTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context, album.getCreationDate()));

            tvAlbumDetail.setText(getDetail(album));


            v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
            v.findViewById(R.id.ivCamera2).setVisibility(View.GONE);

            setRatingStars();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRatingStars() {
        v.findViewById(R.id.llStar).setVisibility(View.INVISIBLE);
    }


    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_SONG_PLAYLIST);
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivShare:
                    showShareDialog(album.getShare());
                    break;

                case R.id.ivOption:
                    showPopup(album.getMenus(), view, 10);
                    break;

                case R.id.llAlbumImage:
                    showPopup(album.getProfileImageOptions(), view, 100);
                    break;

                case R.id.ivCoverPhoto:
                    showPopup(album.getCoverImageOptions(), v.findViewById(R.id.ivCamera), 1000);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private boolean isLoggedIn() {
        if (!isLoggedIn) {
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
        }
        return isLoggedIn;
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

    private void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;


            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
//                HttpRequestVO request = new HttpRequestVO(URL.CMUSIC_VIEW);
                HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "music/" + albumId + Constant.POST_URL);

                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
//                request.params.put(Constant.KEY_PLAYLIST_ID, albumId);
                if (req == UPDATE_UPPER_LAYOUT) {
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideLoaders();
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                showView(v.findViewById(R.id.cvDetail));
                                MusicView resp = new Gson().fromJson(response, MusicView.class);
                                result = resp.getResult();
                                if (req == UPDATE_UPPER_LAYOUT) {
                                    album = result.getPlaylist();
                                    updateUpperLayout();
                                } else {
                                    if (null != result.getSongs())
                                        albumsList.addAll(result.getSongs());
                                    album = result.getPlaylist();
                                    updateUpperLayout();
                                    updateAdapter();
                                    callBottomCommentLikeApi(albumId, album.getResourceType(), Constant.URL_VIEW_COMMENT_LIKE);
                                }
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                                goIfPermissionDenied(err.getError());
                            }
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }

    public void hideLoaders() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }

    public static ViewCMusicFragment newInstance(int albumId, Bundle bundle) {
        return newInstance(albumId, false, bundle);
    }

    public static ViewCMusicFragment newInstance(int albumId, boolean openComment) {
        return newInstance(albumId, openComment, null);
    }


    public static ViewCMusicFragment newInstance(int albumId) {
        return ViewCMusicFragment.newInstance(albumId, false, null);
    }

    public static ViewCMusicFragment newInstance(int albumId, boolean openComment, Bundle bundle) {
        ViewCMusicFragment frag = new ViewCMusicFragment();
        frag.albumId = albumId;
        frag.openComment = openComment;
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
            Options opt;
            boolean isCover = false;

            itemId = itemId - 10;
            opt = album.getMenus().get(itemId - 1);

            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment();
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PLAYLIST_ID, album.getPlaylistId());
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_MUSIC_ALBUM, map, URL.CMUSIC_EDIT)).addToBackStack(null).commit();
    }

    private void goToReportFragment() {
        String guid = "music_playlist" + "_" + album.getPlaylistId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PLAYLIST_ID, album.getPlaylistId());
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_PLAYLIST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi();

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    String url = URL.CMUSIC_DELETE;
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_PLAYLIST_ID, albumId);

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

                                        activity.taskPerformed = Constant.TASK_PLAYLIST_DELETED;
                                        onBackPressed();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e(e);
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
        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }
}
