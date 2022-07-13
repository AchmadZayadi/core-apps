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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.music.MusicView;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.CreateClickClick;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMusicAlbumFragment extends HelperFragment implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {


    private static final int UPDATE_UPPER_LAYOUT = 101;

    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ResultView result;
    private ProgressBar pb;
    private AlbumView album;
    //public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;
    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage,addtoplaylist;
    public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;


    private int albumId;
    private NestedScrollView mScrollView;
    private boolean isLoggedIn;
    private boolean openComment;
    private Bundle bundle;
    ImageView playallid;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivAlbumImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
          //  tvAlbumTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            tvAlbumTitle.setText(bundle.getString(Constant.Trans.IMAGE));
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
                        .into(ivAlbumImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }

    public void stopMusic(){
       try {
           for(int k=0;k<albumsList.size();k++){
               if(albumsList.get(k).isPlaying()){
                   albumsList.get(k).setPlaying(false);
                   recyclerView.getAdapter().notifyItemChanged(k);
               }
           }
       }catch (Exception ex){
           ex.printStackTrace();
       }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_view_2, container, false);
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
        Log.e("CoverPhoto4444444",""+activity.stringValue);
        Log.e("taskPerformed",""+activity.taskPerformed);
        Log.e("taskId",""+activity.taskId);
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {

                        albumsList.clear();
                        callMusicAlbumApi(1);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        albumsList.clear();
                        callMusicAlbumApi(1);
                    }
                    activity.taskPerformed = 0;
                    break;
                case Constant.TASK_SONG_DELETED:
                         result = null;
                        albumsList.clear();
                        callMusicAlbumApi(1);
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }


    }

    private void init() {

        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        recyclerView = v.findViewById(R.id.recyclerview);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        ivAlbumImage = v.findViewById(R.id.ivAlbumImage);
        tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
        addtoplaylist = v.findViewById(R.id.addtoplaylist);

        playallid = v.findViewById(R.id.playallid);

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
        v.findViewById(R.id.like_song).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llComment_song).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);
        v.findViewById(R.id.llFavorite_song).setOnClickListener(this);
        v.findViewById(R.id.bottombarid).setVisibility(View.GONE);


        ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);

        tvAlbumDetail.setTypeface(iconFont);

        mScrollView = v.findViewById(R.id.mScrollView);
        //   setListner();

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
        initCollapsingToolbar();

        playallid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(result.getSongs()!=null && result.getSongs().size()>0){
                        ((CommonActivity) activity).playSong(result.getSongs());
                        ((CommonActivity) activity).songPicked(result.getSongs().get(0));
                        ((CommonActivity) activity).showMusicLayout();
                        Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_anim);
                        playallid.startAnimation(myAnim);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });



        addtoplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPlaylistForm(album.getAlbumId());
            }
        });

    }

    private CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;
    private void initCollapsingToolbar() {

        toolbar = v.findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
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
                showShareDialog(Constant.TXT_SHARE_FEED);
                break;
            case R.id.option:
                View vItem = getActivity().findViewById(R.id.option);
                showPopup(album.getMenus(), vItem, 10);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public MusicAlbumAdapter2 adapter;

    private void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new MusicAlbumAdapter2(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
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
            //collapsingToolbar.setTitle(album.getTitle());
            setRatingStars();

            tvAlbumTitle.setText(album.getTitle());
            Util.showImageWithGlide(ivAlbumImage, album.getImages().getMain(), context, R.drawable.placeholder_square);
            Util.showImageWithGlide(ivCoverPhoto, album.getCover().getMain(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(album.getUserTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context,album.getCreationDate()));

           // tvAlbumDetail.setText(getDetail(album));

            tvAlbumDetail.setText(album.getLikeCount()+" Likes"+" | "+album.getViewCount()+" Views, "+album.getUserTitle());

            tvAlbumDetail.setTextColor(Color.parseColor("#FFFFFF"));
            if (null != result.getPermission() && result.getPermission().getCanEdit() == 1
                    || result.getLoggedinUserId() == album.getOwnerId()) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llAlbumImage).setOnClickListener(this);
                v.findViewById(R.id.ivCoverPhoto).setOnClickListener(this);
              } else {
                v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
                v.findViewById(R.id.ivCamera2).setVisibility(View.GONE);
             }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRatingStars() {
        v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
        Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
        float rating = album.getRating().getTotalRatingAverage();
        if (rating > 0) {
            ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dFilledStar);
            if (rating > 1) {
                ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dFilledStar);
                if (rating > 2) {
                    ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dFilledStar);
                    if (rating > 3) {
                        ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dFilledStar);
                        if (rating > 4) {
                            ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dFilledStar);
                        }
                    }
                }
            }
        }

    }

    private void unfilledAllStar() {
        Drawable dUnfilledStar = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dUnfilledStar);
    }

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);

        try {
            for (int k=0;k<albumsList.size();k++){
                albumsList.get(k).setPlaying(false);
            }
        }catch (Exception ex){

        }
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SONG_ALBUM);
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);

    }


    private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareInside(album.getShare(), true);
            });

            bShareOut.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareOutside(album.getShare());
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

                case R.id.ivStar1:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(1);
                        setRatingStars();
                        callRatingApi(1);
                    }
                    break;
                case R.id.ivStar2:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(2);
                        setRatingStars();
                        callRatingApi(2);
                    }
                    break;
                case R.id.ivStar3:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(3);
                        setRatingStars();
                        callRatingApi(3);
                    }
                    break;
                case R.id.ivStar4:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(4);
                        setRatingStars();
                        callRatingApi(4);
                    }
                    break;
                case R.id.ivStar5:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(5);
                        setRatingStars();
                        callRatingApi(5);
                    }
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


    private void callRatingApi(int rating) {
        if (album.getRating().getCode() != 90) {
            Util.showSnackbar(v, album.getRating().getMessage());
            return;
        }

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_RATE_ALBUM);

                    request.params.put(Constant.KEY_RATING, rating);
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, album.getResourceType());
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

    private void callRemoveImageApi(String url) {
        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, album.getResourceType());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
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
                        return true;
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

        if (isNetworkAvailable(context)) {
            isLoading = true;


            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_ALBUM_VIEW);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                request.params.put(Constant.KEY_ALBUM_ID, albumId);
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
                                    showView(v.findViewById(R.id.cvDetail));
                                    MusicView resp = new Gson().fromJson(response, MusicView.class);
                                    result = resp.getResult();
                                //    ((CommonActivity) activity).playSong(result.getSongs());
                                //    ((CommonActivity) activity).songPicked(result.getSongs().get(0));

                                    try {
                                        if (result.getSongs().size()>0){
                                            for(int k=0;k<result.getSongs().size();k++){
                                                if(result.getSongs().get(k).getSongUrl()==null || result.getSongs().get(k).getSongUrl().length()<1){
                                                    result.getSongs().get(k).setSongUrl(""+result.getSongs().get(k).getSongselfurl());
                                                }
                                            }
                                        }
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                    if (req == UPDATE_UPPER_LAYOUT) {
                                        album = result.getAlbums();
                                        updateUpperLayout();
                                    } else {
                                        if (null != result.getSongs())
                                            albumsList.addAll(result.getSongs());
                                        album = result.getAlbums();
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
                        // dialog.dismiss();
                        return true;
                    }
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
        // setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }

    public static ViewMusicAlbumFragment newInstance(int albumId, Bundle bundle) {
        return newInstance(albumId, false, bundle);
    }

    public static ViewMusicAlbumFragment newInstance(int albumId, boolean openComment) {
        return newInstance(albumId, openComment, null);
    }


    public static ViewMusicAlbumFragment newInstance(int albumId) {
        return ViewMusicAlbumFragment.newInstance(albumId, false, null);
    }

    public static ViewMusicAlbumFragment newInstance(int albumId, boolean openComment, Bundle bundle) {
        ViewMusicAlbumFragment frag = new ViewMusicAlbumFragment();
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
                case "add to playlist":
                    openAddPlaylistForm(album.getAlbumId());
                    break;
                case "download":
                    downloadfile(album.getSongUrl(),album.getTitle());
                    break;
                case Constant.OptionType.EDIT:
                    goToFormFragment();
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
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
                    map.put(Constant.KEY_ALBUM_ID, resourceId);
                    map.put(Constant.KEY_ID, resourceId);
                    openSelectAlbumFragment(isCover ? Constant.URL_MUSIC_UPLOAD_COVER : Constant.URL_MUSIC_UPLOAD_PHOTO, map);

                    break;
                case Constant.OptionType.UPLOAD_PHOTO:
                  //  gToAlbumImage(Constant.URL_MUSIC_UPLOAD_PHOTO, album.getImages().getMain(), Constant.TITLE_EDIT_MUSIC_PHOTO);

                    map = new HashMap<>();
                    map.put(Constant.KEY_IMAGE, "image");
                    map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
                    map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                    goToUploadAlbumImage2(Constant.URL_MUSIC_UPLOAD_PHOTO, album.getImages().getMain(), opt.getLabel(), map);

                    break;
                case Constant.OptionType.upload_cover:
                  // gToAlbumImage(Constant.URL_MUSIC_UPLOAD_COVER, album.getCover().getMain(), Constant.TITLE_EDIT_COVER);

                    map = new HashMap<>();
                    map.put(Constant.KEY_IMAGE, "image");
                    map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
                    map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                    goToUploadAlbumImage2(Constant.URL_MUSIC_UPLOAD_COVER, album.getCover().getMain(), opt.getLabel(), map);
                    break;
                case Constant.OptionType.view_cover_photo:
                    goToPhotoView(album.getCover().getMain());
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


    private void openAddPlaylistForm(int songId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, songId);

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(Constant.FormType.TYPE_ADD_ALBUM,
                                map, Constant.URL_CREATE_PLAYLIST
                        ))
                .addToBackStack(null)
                .commit();
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_MUSIC_ALBUM, map, Constant.URL_MUSIC_EDIT_ALBUM)).addToBackStack(null).commit();
    }

    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getAlbumId();
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

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    String url = Constant.URL_DELETE_MUSIC_ALBUM;//: Constant.URL_DELETE_MUSIC_PLAYLIST;
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
}
