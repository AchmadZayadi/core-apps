package com.sesolutions.ui.page;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
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
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.Result;
import com.sesolutions.responses.album.StaggeredAlbums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.ViewAlbumAdapter;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.photo.UploadPhotoFragment;
import com.sesolutions.ui.profile.ProfileMapFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPageAlbumFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

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
    public TextView ivAlbumDate;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result result;
    private ProgressBar pb;
    private Albums album;
    private int albumId;
    private NestedScrollView mScrollView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean openComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle bundle;
    //private int mObjectId;
    private Map<String, Object> map;
    private String url;
    boolean showToolbar=false;

    public static ViewPageAlbumFragment newInstance(int albumId, boolean openComment, Bundle bundle) {
        ViewPageAlbumFragment frag = new ViewPageAlbumFragment();
        frag.albumId = albumId;
        frag.openComment = openComment;
        frag.bundle = bundle;
        return frag;
    }

    public static ViewPageAlbumFragment newInstance(Bundle bundle, boolean showToolbar) {
        ViewPageAlbumFragment frag = new ViewPageAlbumFragment();
        frag.bundle = bundle;
        frag.showToolbar = showToolbar;
        frag.map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
//        frag.albumId = (int) bundle.getInt(Constant.KEY_ALBUM_ID);
        return frag;
    }


    public static ViewPageAlbumFragment newInstance(int albumId) {
        return ViewPageAlbumFragment.newInstance(albumId, false, null);
    }

    public static ViewPageAlbumFragment newInstance(int albumId, boolean openComment) {
        return ViewPageAlbumFragment.newInstance(albumId, openComment, null);
    }

    public static ViewPageAlbumFragment newInstance(Map<String, Object> map, Bundle bundle) {
        ViewPageAlbumFragment frag = new ViewPageAlbumFragment();
        frag.map = map;
        frag.bundle = bundle;
        frag.albumId = (int) map.get(Constant.KEY_ALBUM_ID);
        return frag;
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
        getMapValues();
        setRecyclerView();
        callMusicAlbumApi(1);
        if (SPref.getInstance().isLoggedIn(context))
            callBottomCommentLikeApi(albumId, resourceType, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(albumId, resourceType);
        }
        return v;
    }

    private void getMapValues() {
        try {
            if (map != null) {
                resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
                url = (String) map.get(Constant.KEY_URI);
                map.remove(Constant.KEY_URI);
               /* switch (resourceType) {
                    case Constant.ResourceType.PAGE_ALBUM:
                        mObjectId = (int) map.get(Constant.KEY_PAGE_ID);
                        break;
                    case Constant.ResourceType.SES_EVENT_ALBUM:
                        mObjectId = (int) map.get(Constant.KEY_EVENT_ID);
                        break;
                }*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
                    showShareDialog(Constant.TXT_SHARE_FEED);
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
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);

        // v = getView();
        // if (!((MusicParentFragment) getParentFragment()).isBlogLoaded) {
        initCollapsingToolbar();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);


        // ((TextView) v.findViewById(R.id.tvTitle)).setText("");
        recyclerView = v.findViewById(R.id.recyclerview);
        ivCoverPhoto1 = v.findViewById(R.id.ivCoverPhoto1);
        ivUserImage = v.findViewById(R.id.ivUserImage);
        tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);

        tvUserTitle = v.findViewById(R.id.tvUserTitle);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);
        ivAlbumDate = v.findViewById(R.id.ivAlbumDate);
        pb = v.findViewById(R.id.pb);

        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);
        ivAlbumDate.setTypeface(iconFont);
        tvAlbumDetail.setTypeface(iconFont);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
            }
        });
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

    private void initCollapsingToolbar() {
        toolbar = v.findViewById(R.id.toolbar);
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
            /*List<String> pics = album.getCoverPic();
            if (pics.size() > 0) {
                Util.showImageWithGlide(ivCoverPhoto1, pics.get(0), context, R.drawable.placeholder_square);
            }
            if (pics.size() > 1) {
                Util.showImageWithGlide(ivCoverPhoto2, pics.get(1), context, R.drawable.placeholder_square);
            } else {
                v.findViewById(R.id.llCoverPhoto2).setVisibility(View.GONE);
                ivCoverPhoto2.setVisibility(View.GONE);
            }
            if (pics.size() > 2) {
                Util.showImageWithGlide(ivCoverPhoto3, pics.get(2), context, R.drawable.placeholder_square);
            } else {
                ivCoverPhoto3.setVisibility(View.GONE);
            }*/
            tvUserTitle.setText(Constant.TXT_BY + album.getUserTitle());
            ivAlbumDate.setText(Constant.FontIcon.CALENDAR);
            tvAlbumDate.setText(Util.changeDateFormat(context, album.getCreationDate()));

            Util.showImageWithGlide(ivUserImage, album.getUserImage(), context, R.drawable.placeholder_square);
            tvAlbumDetail.setText(getDetail(album));
            if (null != album.getPermission() && album.getPermission().canEdit()
                    || result.getLoggedinUserId() == album.getOwnerId()) {
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
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
        ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_ALBUM_AVAILABLE);
        v.findViewById(R.id.tvNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
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
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;

                case R.id.ivOption:
                    showPopup(result.getMenus(), view, 10);
                    break;

                case R.id.ivCamera:
                    showPopup(album.getCoverImageOptions(), v.findViewById(R.id.ivCamera), 100);
                    break;

              /*  case R.id.llAlbumImage:
                    showPopup(album.getProfileImageOptions(), view, 100);
                    break;

*/
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
                    menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
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
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
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
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, RECYCLE_ITEM_THRESHOLD);
                    if (map != null) {
                        request.params.putAll(map);
                    }
                    // request.params.put(Constant.KEY_ALBUM_ID, albumId);
                    //  request.params.put(Constant.KEY_PAGE_ID, mPageId);


                    if (req == UPDATE_UPPER_LAYOUT || req == Constant.REQ_CODE_REFRESH) {
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
                    if (opt.getParams() != null) {
                        goToReportFragment(opt.getParams().getType() + "_" + opt.getParams().getId());
                    }
                    break;
                case Constant.OptionType.ADD_MORE_PHOTOS:
                case Constant.OptionType.UPLOAD_MORE_PHOTOS:
                    goToUploadPhoto();
                    break;
                case Constant.OptionType.remove_profile_photo:
                    showImageRemoveDialog(false, getString(R.string.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION), Constant.URL_MUSIC_REMOVE_PHOTO);
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
                    gToAlbumImage(Constant.URL_ALBUM_UPLOAD_COVER, album.getCover().getMain(), Constant.TITLE_EDIT_COVER);
                    break;
                case Constant.OptionType.view_cover_photo:
                    //TODO COVER ID IMAGE
                    goToGalleryFragment(album.getPhotoId(), resourceType, album.getCover().getMain());

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
                String url = Constant.BASE_URL + "album/delete/" + albumId + Constant.POST_URL;
                switch (resourceType) {
                    case Constant.ResourceType.PAGE_ALBUM:
                        url = Constant.URL_PAGE_ALBUM_DELETE;
                        break;
                    case Constant.ResourceType.SES_EVENT_ALBUM:
                        url = Constant.URL_EVENT_ALBUM_DELETE;
                        break;
                    case Constant.ResourceType.GROUP_ALBUM:
                        url = Constant.URL_GROUP_ALBUM_DELETE;
                        break;
                    case Constant.ResourceType.BUSINESS_ALBUM:
                        url = Constant.URL_BUSINESS_ALBUM_DELETE;
                        break;
                    case Constant.ResourceType.PRODUCT_ALBUM:
                        url = Constant.URL_PRODUCT_ALBUM_DELETE;
                        break;
                    case Constant.ResourceType.CLASSROOM:
                        url = Constant.URL_CLASSROOM_DELETE_ALBUM;
                        break;
                }
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ALBUM_ID, albumId);
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
       /* Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_PAGE_ID, mPageId);
        map.put(Constant.KEY_RESOURCE_ID, albumId);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);*/
        String url = null;
        switch (resourceType) {
            case Constant.ResourceType.PAGE_ALBUM:
                url = Constant.URL_PAGE_ADD_MORE_PHOTOS;
                break;
            case Constant.ResourceType.PRODUCT_ALBUM:
                url = Constant.URL_PRODUCT_ADD_MORE_PHOTOS;
                break;
            case Constant.ResourceType.CLASSROOM:
                url = Constant.URL_CLASSROOM_ADD_MORE_PHOTOS;
                break;
            case Constant.ResourceType.SES_EVENT_ALBUM:
                url = Constant.URL_EVENT_ADD_MORE_PHOTOS;
                break;
            case Constant.ResourceType.GROUP_ALBUM:
                url = Constant.URL_GROUP_ADD_MORE_PHOTOS;
                break;
            case Constant.ResourceType.BUSINESS_ALBUM:
                url = Constant.URL_BUSINESS_ADD_MORE_PHOTOS;
                break;
        }


        fragmentManager.beginTransaction()
                .replace(R.id.container, UploadPhotoFragment.newInstance(map,
                        url, getStrings(R.string.TITLE_UPLOAD_PHOTOS)))
                .addToBackStack(null)
                .commit();
    }

    private void goToFormFragment() {
        String url = Constant.BASE_URL + Constant.URL_EDIT_ALBUM + album.getAlbumId() + Constant.POST_URL;
        int formType = Constant.FormType.EDIT_ALBUM;
        switch (resourceType) {
            case Constant.ResourceType.PAGE_ALBUM:
                url = Constant.URL_PAGE_ALBUM_EDIT;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
            case Constant.ResourceType.SES_EVENT_ALBUM:
                url = Constant.URL_EVENT_ALBUM_EDIT;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
            case Constant.ResourceType.GROUP_ALBUM:
                url = Constant.URL_GROUP_ALBUM_EDIT;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
            case Constant.ResourceType.BUSINESS_ALBUM:
                url = Constant.URL_BUSINESS_ALBUM_EDIT;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
            case Constant.ResourceType.CLASSROOM:
                url = Constant.URL_CLASSROOM_EDIT_ALBUM;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
            case Constant.ResourceType.PRODUCT_ALBUM:
                url = Constant.URL_PRODUCT_ALBUM_EDIT;
                formType = Constant.FormType.EDIT_ALBUM_OTHERS;
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
        //map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_ALBUM);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(formType, map, url)).addToBackStack(null).commit();
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
        } else if (activity.taskPerformed == Constant.FormType.EDIT_ALBUM_OTHERS) {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_ALBUM);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
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
        Map<String, Object> map1 = new HashMap<>(map);
        map1.put(Constant.KEY_PHOTO_ID, photoId);
        //   map.put(Constant.KEY_PAGE_ID, mPageId);
        switch (resourceType) {
            case Constant.ResourceType.PAGE_ALBUM:
                map1.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE_PHOTO);
                break;
            case Constant.ResourceType.PRODUCT_ALBUM:
                map1.put(Constant.KEY_TYPE, Constant.ResourceType.PRODUCT_PHOTO);
                break;
            case Constant.ResourceType.SES_EVENT_ALBUM:
                map1.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT_PHOTO);
                break;
            case Constant.ResourceType.GROUP_ALBUM:
                map1.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP_PHOTO);
                break;
            case Constant.ResourceType.BUSINESS_ALBUM:
                map1.put(Constant.KEY_TYPE, Constant.ResourceType.BUSINESS_PHOTO);
                break;
        }
        //  map.put(Constant.KEY_ALBUM_ID, albumId);
        map1.put(Constant.KEY_IMAGE, imageUrl);
        //map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map1))
                .addToBackStack(null).commit();
    }
}
