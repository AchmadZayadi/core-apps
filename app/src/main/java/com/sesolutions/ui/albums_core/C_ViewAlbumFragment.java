package com.sesolutions.ui.albums_core;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.Result;
import com.sesolutions.responses.album.StaggeredAlbums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.ViewAlbumAdapter;
import com.sesolutions.ui.albums.ViewAlbumFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommentLikeHelper_basic;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.photo.UploadPhotoFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C_ViewAlbumFragment extends CommentLikeHelper_basic implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    private static final int REFRESH_LIST = 102;
    private static final int RECYCLE_ITEM_THRESHOLD = 24;
    //public View v;
    public List<StaggeredAlbums> videoList;
    public ViewAlbumAdapter adapter;
    public ImageView ivCoverPhoto1;
    public ImageView ivUserImage;
    // public ImageView ivAlbumImage;
    public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;
    public TextView tvDescription;
    public TextView ivAlbumDate;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result result;
    private ProgressBar pb;
    private Albums album;
    private int albumId;
    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean openComment, uploadPhotos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle bundle;

    public static C_ViewAlbumFragment newInstance(int albumId, boolean openComment, Bundle bundle) {
        C_ViewAlbumFragment frag = new C_ViewAlbumFragment();
        frag.albumId = albumId;
        frag.openComment = openComment;
        frag.bundle = bundle;
        return frag;
    }

    public static C_ViewAlbumFragment newInstance(int albumId) {
        return C_ViewAlbumFragment.newInstance(albumId, false, null);
    }

    public static C_ViewAlbumFragment newInstance(int albumId, boolean openComment) {
        return C_ViewAlbumFragment.newInstance(albumId, openComment, null);
    }

    public static C_ViewAlbumFragment newInstance(boolean uploadPhotos, int albumId) {
        C_ViewAlbumFragment frag = new C_ViewAlbumFragment();
        frag.albumId = albumId;
        frag.uploadPhotos = uploadPhotos;
        return frag;
    }

    public static C_ViewAlbumFragment newInstance(int albumId, Bundle bundle) {
        return C_ViewAlbumFragment.newInstance(albumId, false, bundle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivCoverPhoto1.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            //  toolbar.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            collapsingToolbar.setTitle(bundle.getString(Constant.Trans.IMAGE));
            /*setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                    callMusicAlbumApi(1);
                }
            });*/
            //   v.findViewById(R.id.rlUpper).setTransitionName(bundle.getString(Constant.Trans.LAYOUT));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
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
                        .into(ivCoverPhoto1);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }

        v = inflater.inflate(R.layout.fragment_album_view, container, false);
        applyTheme(v);
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
        Log.e("Albums","Albums");
        if (SPref.getInstance().isLoggedIn(context))
            callBottomCommentLikeApi(albumId, Constant.ResourceType.ALBUM, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(albumId, Constant.ResourceType.ALBUM);
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        MenuItem item = menu.findItem(R.id.option);
        item.setVisible(SPref.getInstance().isLoggedIn(context));
        // return true;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(album.getShare());
                    break;
                case R.id.option:
                    if (null != result) {
                        View vItem = getActivity().findViewById(R.id.option);
                        showPopup(result.getMenus(), vItem, 10);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {

        initCollapsingToolbar();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);


        // ((TextView) v.findViewById(R.id.tvTitle)).setText("");
        recyclerView = v.findViewById(R.id.recyclerview);
        ivCoverPhoto1 = v.findViewById(R.id.ivCoverPhoto1);
        ivUserImage = v.findViewById(R.id.ivUserImage);
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
        tvDescription = v.findViewById(R.id.tvDescription);
        ivAlbumDate = v.findViewById(R.id.ivAlbumDate);
        pb = v.findViewById(R.id.pb);
        //  v.findViewById(R.id.ivBack).setOnClickListener(this);
        //  v.findViewById(R.id.ivShare).setOnClickListener(this);
        //  v.findViewById(R.id.ivOption).setOnClickListener(this);

        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);

        // tvUserTitle.setTypeface(iconFont);
        ivAlbumDate.setTypeface(iconFont);
        tvAlbumDetail.setTypeface(iconFont);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                swipeRefreshLayout.setEnabled(verticalOffset == 0);
            }
        });

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

        //initSlide();
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));

    }

    private void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ViewAlbumAdapter(videoList, context, this, this, Constant.FormType.TYPE_PHOTO);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            //CustomLog.e(e);
        }
    }

    private void updateUpperLayout() {
        try {
            showHideOptionIcon();
            // ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
            collapsingToolbar.setTitle(album.getTitle());
            tvAlbumTitle.setText(album.getTitle());
            if (null == bundle)
                Util.showImageWithGlide(ivCoverPhoto1, album.getFirstCover(), context, R.drawable.dummyy);

            tvUserTitle.setText(Constant.TXT_BY + album.getUserTitle());
            ivAlbumDate.setText(Constant.FontIcon.CALENDAR);
            tvAlbumDate.setText(Util.changeDateFormat(context, album.getCreationDate()));

            Util.showImageWithGlide(ivUserImage, album.getUserImage(), context, R.drawable.placeholder_square);
            tvAlbumDetail.setText(getDetail(album));
            tvDescription.setText(album.getDescription());
            if (null != album.getPermission() && album.getPermission().canEdit()
                  && null != album.getCoverImageOptions()  || result.getLoggedinUserId() == album.getOwnerId()) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera).setOnClickListener(this);
            } else {
                v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public String getDetail(Albums album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()
                + "  \uf075 " + album.getCommentCount();
            return detail;
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(getStrings(R.string.MSG_NO_SONG_ALBUM));
        v.findViewById(R.id.tvNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);

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
                    showPopup(result.getMenus(), view, 10);
                    break;

                case R.id.ivCamera:
                    try {
                        showPopup(album.getCoverImageOptions(), v.findViewById(R.id.ivCamera), 100);
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
        if (null != menus && menus.size() > 0) {
            try {
                PopupMenu menu = new PopupMenu(context, v);
                for (int index = 0; index < menus.size(); index++) {
                    Options s = menus.get(index);
                    if(!s.getLabel().equalsIgnoreCase("Manage Album")){
                        menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
                    }
                }
                menu.show();
                menu.setOnMenuItemClickListener(this);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    private void callRemoveImageApi(String url) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM);
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
                    hideLoaders();
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                hideLoaders();

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
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
                    //  HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_ALBUM_VIEW);
                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "album/view/" + albumId + Constant.POST_URL);
                    request.params.put(Constant.KEY_LIMIT, RECYCLE_ITEM_THRESHOLD);
                    /*if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    // request.params.put(Constant.KEY_ALBUM_ID, albumId);
                    if (req == UPDATE_UPPER_LAYOUT) {
                        //this can refresh user current page -- result.getCurrentPage()
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }

                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            try {
                                String response = (String) msg.obj;


                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(v.findViewById(R.id.cvDetail));
                                        hideView(v.findViewById(R.id.pbMain));
                                        AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);
                                        result = resp.getResult();
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }

                                        if (req == UPDATE_UPPER_LAYOUT) {
                                            album = result.getAlbum();
                                            updateUpperLayout();
                                        } else {
                                            wasListEmpty = videoList.size() == 0;
                                            if (null != result.getPhotos())
                                                videoList.addAll(result.getStaggeredAlbum());
                                            album = result.getAlbum();
                                            if (req != REFRESH_LIST) {
                                                updateUpperLayout();
                                            }
                                            updateAdapter();
                                            if (uploadPhotos) {
                                                uploadPhotos = false;
                                                new Handler().postDelayed(() -> goToUploadPhoto(), 100);
                                            }
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }
                                hideLoaders();
                            } catch (Exception e) {
                                hideLoaders();
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
                hideLoaders();
                notInternetMsg(v);
            }
        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
        }
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
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
            Options opt = null;
            //boolean isCover = false;

            if (itemId > 100) {
                itemId = itemId - 100;
                opt = album.getCoverImageOptions().get(itemId - 1);

            } else {
                itemId = itemId - 10;
                opt = result.getMenus().get(itemId - 1);
            }
            /*itemId = itemId - 10;
            opt = result.getMenus().get(itemId - 1);*/

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
                case Constant.OptionType.ADD_MORE_PHOTOS:
                    goToUploadPhoto();
                    break;
                case Constant.OptionType.remove_profile_photo:
                    showImageRemoveDialog(false, Constant.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_PHOTO);
                    break;
                case Constant.OptionType.view_profile_photo:
                    goToGalleryFragment(album.getPhotoId(), resourceType, album.getImages().getMain());
                    break;
                case Constant.OptionType.CHOOSE_FROM_ALBUMS:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_ALBUM_ID, resourceId);
                    map.put(Constant.KEY_ID, resourceId);
                    openSelectAlbumFragment(Constant.URL_ALBUM_UPLOAD_COVER, map);
                    break;
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_MUSIC_UPLOAD_PHOTO, album.getImages().getMain(), Constant.TITLE_EDIT_MUSIC_PHOTO);
                    break;
                case Constant.OptionType.upload_cover:
                    gToAlbumImage(Constant.URL_ALBUM_UPLOAD_COVER, album.getFirstCover(), Constant.TITLE_EDIT_COVER);
                    break;
                case Constant.OptionType.view_cover_photo:
                    //TODO COVER ID IMAGE
                    goToGalleryFragment(album.getPhotoId(), resourceType, album.getFirstCover());

                    break;
                case Constant.OptionType.remove_cover_photo:
                    showImageRemoveDialog(true, Constant.MSG_COVER_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_COVER);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {

               /* lists.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, lists.size());*/
                showBaseLoader(false);
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
                                        activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                        onBackPressed();
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


    private void goToUploadPhoto() {
        Map<String, Object> map = new HashMap<>();

        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_RESOURCE_ID, albumId);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM);

        fragmentManager.beginTransaction()
                .add(R.id.container, UploadPhotoFragment.newInstance(map,
                        Constant.BASE_URL + Constant.URL_UPLOAD_ADD_MORE_PHOTO + albumId
                                + Constant.POST_URL, Constant.TITLE_UPLOAD_PHOTOS))
                .addToBackStack(null)
                .commit();
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
        map.put(Constant.KEY_MODULE, Constant.ModuleName.ALBUM);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_ALBUM, map,
                Constant.BASE_URL + Constant.URL_EDIT_ALBUM + album.getAlbumId() + Constant.POST_URL)).addToBackStack(null).commit();
    }

    private void goToReportFragment() {
        String guid = Constant.ResourceType.ALBUM + "_" + album.getAlbumId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfiguration.isAdEnabled && activity.mInterstitialAd.isLoaded()) {
            activity.mInterstitialAd.show();
        } else {
            updateOnStart();
        }
    }

    public void updateOnStart() {
        if (activity.taskPerformed == Constant.TASK_ADD_MORE_PHOTO
                || activity.taskPerformed == Constant.TASK_IMAGE_DELETED) {
            activity.taskPerformed = 0;
            result = null;
            videoList.clear();
            callMusicAlbumApi(REFRESH_LIST);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_ALBUM) {
            activity.taskPerformed = 0;
            callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
        }
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_ALBUM);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi();
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? R.string.TXT_REMOVE_COVER : R.string.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callRemoveImageApi(url);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        String imageUrl;
        int photoId;
        switch (object1) {
            case Constant.Events.IMAGE_1:
                photoId = videoList.get(postion).getFirstAlbum().getPhotoId();
                imageUrl = videoList.get(postion).getFirstAlbum().getImages().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_2:
                photoId = videoList.get(postion).getSecondAlbum().getPhotoId();
                imageUrl = videoList.get(postion).getSecondAlbum().getImages().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_3:
                photoId = videoList.get(postion).getThirdAlbum().getPhotoId();
                imageUrl = videoList.get(postion).getThirdAlbum().getImages().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_4:
                photoId = videoList.get(postion).getFourthAlbum().getPhotoId();
                imageUrl = videoList.get(postion).getFourthAlbum().getImages().getMain();
                openLighbox(photoId, imageUrl);
                break;
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    private void openLighbox(int photoId, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_TYPE, Constant.ACTIVITY_TYPE_ALBUM);
        map.put(Constant.KEY_IMAGE, imageUrl);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM_PHOTO);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }



/*    public void handleResponse(String response) {
        try {
            if (!(new JSONObject(response).get("result") instanceof String)) {
                CommentLike resp = new Gson().fromJson(response, CommentLike.class);
                if (null != resp.getResult() && null != resp.getResult().getStats()) {
                    stats = resp.getResult().getStats();
                    updateItemLikeFavorite22();
                }
            }
        } catch (JSONException e) {
            CustomLog.e(e);
        }
    }

    public void updateItemLikeFavorite22() {
        Log.e("Like","Like");
        ((TextView) v.findViewById(R.id.tvLike)).setText(stats.getIsLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
        ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
        ((TextView) v.findViewById(R.id.tvLike)).setTextColor(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
    }


    public void updateItemLikeFavorite22(boolean isflag) {
        Log.e("Like","Like");
        ((TextView) v.findViewById(R.id.tvLike)).setText(isflag? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
        ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(isflag ? Constant.colorPrimary : Constant.text_color_1));
        ((TextView) v.findViewById(R.id.tvLike)).setTextColor(Color.parseColor(isflag ? Constant.colorPrimary : Constant.text_color_1));
    }*/



}
