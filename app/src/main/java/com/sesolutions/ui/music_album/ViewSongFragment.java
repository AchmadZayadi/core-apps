package com.sesolutions.ui.music_album;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.music.ResultSongView;
import com.sesolutions.responses.music.SongView;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.signup.UserMaster;
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

public class ViewSongFragment extends HelperFragment5 implements View.OnClickListener, OnLoadMoreListener/*, OnUserClickedListener<Integer, String>*/, PopupMenu.OnMenuItemClickListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    private ResultSongView result;
    private AlbumView album;
    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage;
    public TextView tvAlbumTitle,tvAlbumDetails,tvAlbumDetails2,tvAlbumDetails3;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;
    private int songId;
    //private NestedScrollView mScrollView;
    private ImageView ivPlayPause;
    private String resourceType;
    private Map<String, Object> map;
    private TextView tvArtistName;
    private boolean isLoggedIn;
    private boolean openComment;
    private Bundle bundle;
    public BottomSheetDialog bottomSheetDialog;
    ImageView ivinfodetails;
    RelativeLayout font_awesome_container2;
    RelativeLayout lladdtoplaylist;
    ImageView ivSaveFeed,ivWhatsAppShare,ivFbShare;
    public  Drawable dSave;
    public  Drawable dUnsave;

    @Override
    public void onStart() {
        super.onStart();

        try {
            if(BaseActivity.backcoverchange==Constant.GO_TO_HOMEFRAGMENT){
                tvAlbumDetails3.setText(BaseActivity.commentcount+" Comments");
                BaseActivity.backcoverchange=0;
            }
           }catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            ((MainApplication) activity.getApplication()).getMusicService().setProgressListener(Constant.Listener.VIEW_SONG, this);
            if (activity.taskPerformed == Constant.FormType.TYPE_SONGS) {
                activity.taskPerformed = 0;
                callMusicAlbumApi(1);
            }
        } catch (Exception e) {
            CustomLog.d("Music_service", "is null");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ((MainApplication) activity.getApplication()).getMusicService().removeListener(Constant.Listener.VIEW_SONG);
        } catch (Exception e) {
            CustomLog.d("Music_service", "is null");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivAlbumImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            tvAlbumTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
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

    public void hideLoaders() {
        // setRefreshing(swipeRefreshLayout, false);
        // pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        setHasOptionsMenu(true);

        if (v != null) {
            updateIcon();
            return v;
        }
        v = inflater.inflate(R.layout.fragment_song_view, container, false);
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);
        applyTheme();
        init();

        setRecyclerView();
        callMusicAlbumApi(1);

        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(songId, resourceType);
        }
        callBottomCommentLikeApi(songId, resourceType, Constant.URL_VIEW_COMMENT_LIKE);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void init() {
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);

        // v = getView();
        // if (!((MusicParentFragment) getParentFragment()).isBlogLoaded) {

        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        // ((TextView) v.findViewById(R.id.tvTitle)).setText("");
         recyclerView = v.findViewById(R.id.recyclerview);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        ivAlbumImage = v.findViewById(R.id.ivAlbumImage);
        font_awesome_container2 = v.findViewById(R.id.font_awesome_container2);
        ivPlayPause = v.findViewById(R.id.ivPlayPause);
        ivPlayPause.setVisibility(View.VISIBLE);
        ivPlayPause.setOnClickListener(this);
        tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
        tvAlbumDetails = v.findViewById(R.id.tvAlbumDetails);
        tvAlbumDetails2 = v.findViewById(R.id.tvAlbumDetails2);
        tvAlbumDetails3 = v.findViewById(R.id.tvAlbumDetails3);
        lladdtoplaylist = v.findViewById(R.id.lladdtoplaylist);

        ivSaveFeed = v.findViewById(R.id.ivSaveFeed);
        ivWhatsAppShare = v.findViewById(R.id.ivWhatsAppShare);
        ivFbShare = v.findViewById(R.id.ivFbShare);

        tvArtistName = v.findViewById(R.id.tvArtistName);
        ivinfodetails=v.findViewById(R.id.ivinfodetails);

      /*  tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
        tvFavorite = v.findViewById(R.id.tvFavorite);
        tvComment = v.findViewById(R.id.tvComment);
        tvImageComment = v.findViewById(R.id.tvImageComment);*/
        tvUserTitle = v.findViewById(R.id.tvUserTitle);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);
        // pb = v.findViewById(R.id.pb);
        //  v.findViewById(R.id.ivBack).setOnClickListener(this);
        // v.findViewById(R.id.ivShare).setOnClickListener(this);
        // v.findViewById(R.id.ivOption).setOnClickListener(this);

        v.findViewById(R.id.ivStar1).setOnClickListener(this);
        v.findViewById(R.id.ivStar2).setOnClickListener(this);
        v.findViewById(R.id.ivStar3).setOnClickListener(this);
        v.findViewById(R.id.ivStar4).setOnClickListener(this);
        v.findViewById(R.id.ivStar5).setOnClickListener(this);
        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.like_song).setOnClickListener(this);
        v.findViewById(R.id.llComment_song).setOnClickListener(this);
        v.findViewById(R.id.llFavorite_song).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);


        ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);

        tvAlbumDetail.setTypeface(iconFont);
       /* tvImageComment.setTypeface(iconFont);
        tvImageFavorite.setTypeface(iconFont);

        tvImageFavorite.setText("\uf004");
        tvImageComment.setText("\uf075");*/
        initCollapsingToolbar();

       /* mScrollView = v.findViewById(R.id.mScrollView);

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
        });*/

        lladdtoplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPlaylistForm(album.getSongId());
            }
        });

        ivSaveFeed.setOnClickListener(this);
        ivWhatsAppShare.setOnClickListener(this);
        ivFbShare.setOnClickListener(this);

        v.findViewById(R.id.bottombarid).setVisibility(View.GONE);
        ivinfodetails.setOnClickListener(this);
        //initSlide();
    }

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        toolbar = v.findViewById(R.id.toolbar);
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

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((album.getMenus() != null && album.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateUpperLayout() {
        showHideOptionIcon();
        //((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
        setRatingStars();
      /*  tvFavorite.setText(album.getFavouriteCount() + " " + Constant.TXT_FAVORITE);
        tvComment.setText(album.getCommentCount() + " " + Constant.TXT_COMMENT);*/
        tvAlbumTitle.setText(album.getTitle());

        try {
            ivSaveFeed.setImageDrawable(album.getShortcut_save().isIs_saved() ?  dUnsave:dSave);
            isboolen = album.getShortcut_save().isIs_saved();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        tvAlbumDetails.setText(album.getLikeCount()+" Likes  ");
        tvAlbumDetails2.setText("  | "+album.getViewCount()+" Views | ");
        tvAlbumDetails3.setText(album.getCommentCount()+" Comments");

        Util.showImageWithGlide(ivAlbumImage, album.getImages().getMain(), context/*, R.drawable.placeholder_square*/);
        Util.showImageWithGlide(ivCoverPhoto, album.getCover().getMain(), context, R.drawable.placeholder_square);

        tvUserTitle.setText(album.getUserTitle());
        tvAlbumDate.setText(Util.changeDateFormat(context,album.getCreationDate()));
        tvAlbumDetail.setText(getDetail(album));
        if (result.getPermission().getCanEdit() == 1
                || result.getLoggedinUserId() == album.getOwnerId()) {
            v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
            v.findViewById(R.id.llAlbumImage).setOnClickListener(this);
            v.findViewById(R.id.ivCoverPhoto).setOnClickListener(this);
        } else {
            v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
            v.findViewById(R.id.ivCamera2).setVisibility(View.GONE);
        }
        updateIcon();
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


    boolean isboolen=false;
    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {

                case  R.id.ivSaveFeed:
                    try {

                        int actionId = album.getShortcut_save().getResource_id();
                        int shortcutid = 0;
                        if(isboolen){
                            shortcutid = album.getShortcut_save().getShortcut_id();
                        }
                        performFeedOptionClick(actionId,  1, isboolen,shortcutid);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case  R.id.ivWhatsAppShare:
                    sharingToSocialMedia(album.getShare(), "com.whatsapp");
                   break;
                case  R.id.ivFbShare:
                    sharingToSocialMedia(album.getShare(), "com.facebook.katana");
                    break;



                case  R.id.ivinfodetails:
                    showDialogsongview();
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivPlayPause:
                    playPauseMusic();
                    break;

                case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
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
                    }
                    callRatingApi(1);

                    break;
                case R.id.ivStar2:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(2);
                        setRatingStars();
                    }
                    callRatingApi(2);

                    break;
                case R.id.ivStar3:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(3);
                        setRatingStars();
                    }
                    callRatingApi(3);

                    break;
                case R.id.ivStar4:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(4);
                        setRatingStars();
                    }
                    callRatingApi(4);

                    break;
                case R.id.ivStar5:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        album.getRating().setTotalRatingAverage(5);
                        setRatingStars();
                    }
                    callRatingApi(5);

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

    private void updateIcon() {
        try {
            if (((CommonActivity) activity).isPlaying()) {
                int songId = ((CommonActivity) activity).getCurrentSongId();
                if (songId == album.getSongId()) {
                    ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
                } else {
                    ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_button));

                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void playPauseMusic() {
        ((CommonActivity) activity).showMusicLayout();
        boolean startNew = true;
        if (((CommonActivity) activity).isPlaying()) {
            int songId = ((CommonActivity) activity).getCurrentSongId();
            if (songId == album.getSongId()) {
                startNew = false;
                ((CommonActivity) activity).pause();
                ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_button));
            }
        } else if (((CommonActivity) activity).isPaused()) {
            int songId = ((CommonActivity) activity).getCurrentSongId();
            if (songId == album.getSongId()) {
                startNew = false;
                ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
                ((CommonActivity) activity).start();
            }
        }

        if (startNew) {
            ((CommonActivity) activity).songPicked(album.getAlbums());
            ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
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

    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getSongId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid,true)).addToBackStack(null).commit();
    }


    private void callRatingApi(int rating) {
        if (album.getRating().getCode() != 90) {
            Util.showSnackbar(v, album.getRating().getMessage());
            return;
        }

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_RATE_ALBUM);

                    request.params.put(Constant.KEY_RATING, rating);
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getSongId());
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


                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        Util.showSnackbar(v, res.getResult());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callRemoveImageApi(String url) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ALBUM_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getAlbumId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, album.getResourceType());
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
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
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

                    CustomLog.e(e);
                }

            } else {

                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
        }
    }

    private void callDeleteSongApi(final int songId) {
        try {
            if (isNetworkAvailable(context)) {

                showBaseLoader(true);

                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_SONG);

                    request.params.put(Constant.KEY_SONG_ID, songId);
                    //request.params.put(Constant.KEY_RESOURCE_ID, album.getAlbumId());
                    //  request.params.put(Constant.KEY_RESOURCES_TYPE, album.getResourceType());
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
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        //  callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
                                        activity.taskPerformed = Constant.TASK_SONG_DELETED;
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

    private void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {

            try {
                /*if (req == REQ_LOAD_MORE) {

                } else*/
                 showView(v.findViewById(R.id.pbMain));
                HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_SONG_VIEW);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    /*if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/

                if (req == UPDATE_UPPER_LAYOUT) {
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                }
                request.params.put(Constant.KEY_RESOURCE_ID, songId);
                request.params.put(Constant.KEY_SONG_ID, songId);
                request.params.putAll(map);

                request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
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
                                    showView(v.findViewById(R.id.llAlbumDetail));
                                    SongView resp = new Gson().fromJson(response, SongView.class);
                                    result = resp.getResult();
                                    if (req == UPDATE_UPPER_LAYOUT) {
                                        //  album = result.getAlbums();
                                        updateUpperLayout();
                                    } else {
                                        // videoList.addAll(result.getSongs());
                                        album = result.getSongs();

                                        if (null != result.getSongs())
                                            albumsList.addAll(result.getRelatedsongs());


                                        updateUpperLayout();
                                        updateLowerLayout();
                                        updateAdapter();
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
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
    }

    private void updateAdapter() {
        try {
            isLoading = false;
            adapter.notifyDataSetChanged();
            ((TextView) v.findViewById(R.id.tvNoData)).setText(getStrings(R.string.MSG_NO_SONG_AVAILABLE));
            v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    private void updateLowerLayout() {

        if(Typeflag==2){
            if (TextUtils.isEmpty(album.getLyrics())) {
                v.findViewById(R.id.llLyrics).setVisibility(View.GONE);
                v.findViewById(R.id.recyclerview).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rlmaintxt).setVisibility(View.VISIBLE);
            } else {
                v.findViewById(R.id.llLyrics).setVisibility(View.VISIBLE);
                v.findViewById(R.id.recyclerview).setVisibility(View.GONE);
                v.findViewById(R.id.rlmaintxt).setVisibility(View.GONE);
                ((TextView) v.findViewById(R.id.tvLyrics)).setText(album.getLyrics());
            }
        }


        if (TextUtils.isEmpty(album.getCategoryTitle())) {
            v.findViewById(R.id.llCategory).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.llCategory).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvCategoryName)).setText(album.getCategoryTitle());
        }

        if (TextUtils.isEmpty(album.getDescription())) {
            v.findViewById(R.id.llDescription).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.llDescription).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvDescription)).setText(album.getDescription());
        }

        if (null != album.getArtists()) {
            v.findViewById(R.id.llArtist).setVisibility(View.VISIBLE);
            tvArtistName.setText(addClickableArtist(album.getArtists()));
            tvArtistName.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.llArtist).setVisibility(View.GONE);
        }
    }


    public static ViewSongFragment newInstance(Map<String, Object> map, int songId, String resourceType, boolean openComment) {
        ViewSongFragment frag = new ViewSongFragment();
        frag.songId = songId;
        frag.map = map;
        frag.openComment = openComment;
        frag.resourceType = resourceType;
        return frag;
    }

    public static ViewSongFragment newInstance(Map<String, Object> map, int songId, String resourceType) {
        return ViewSongFragment.newInstance(map, songId, resourceType, false);

    }

    int Typeflag=0;
    public static ViewSongFragment newInstance(Map<String, Object> map, int songId, String resourceType, Bundle bundle,int typefrag) {
        ViewSongFragment frag = newInstance(map, songId, resourceType, false);
        frag.bundle = bundle;
        frag.Typeflag=typefrag;
        return frag;
    }

    public static ViewSongFragment newInstance(Map<String, Object> map, int songId, String resourceType, Bundle bundle) {
        ViewSongFragment frag = newInstance(map, songId, resourceType, false);
        frag.bundle = bundle;
        return frag;
    }

    @Override
    public void onLoadMore() {
    }

   /* public void loadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/



    private void openAddPlaylistForm(int songId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_SONG_ID, songId);

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(Constant.FormType.TYPE_ADD_SONG,
                                map, Constant.URL_CREATE_PLAYLIST
                        ))
                .addToBackStack(null)
                .commit();
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
                    openAddPlaylistForm(album.getSongId());
                    break;
                case "download":
                    downloadfile(album.getSongUrl());
                    break;
                case Constant.OptionType.EDIT:
                    goToFormFragment();
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog(album.getSongId());
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
                    map.put(Constant.KEY_SONG_ID, resourceId);
                    map.put(Constant.KEY_ID, resourceId);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_SONG_COVER : Constant.URL_UPLOAD_SONG_MAIN_PHOTO, map);

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
                case Constant.OptionType.remove_cover_photo:
                    showImageRemoveDialog(true, Constant.MSG_COVER_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_COVER);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_SONG_ID, album.getSongId());
        map.put(Constant.KEY_MODULE, Constant.VALUE_SES_MUSIC);
       /* case Constant.FormType.TYPE_SONGS:
        type = "song";
        moduleName = Constant.VALUE_SES_MUSIC;*/
        //  break;
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.TYPE_SONGS, map, Constant.URL_EDIT_MUSIC_SONG, 0)).addToBackStack(null).commit();
    }


    private void downloadfile(String songurl) {
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

    public void showDeleteDialog(final int songId) {
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_SONG);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteSongApi(songId);
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

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {


            switch (object1) {


                case Constant.Events.MUSIC_PROGRESS:
                    break;
                case Constant.Events.MUSIC_CHANGED:
                    break;
                case Constant.Events.PLAY:
                    if (songId == album.getSongId()) {
                        ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
                    }
                    break;
                case Constant.Events.PAUSE:
                    if (songId == album.getSongId()) {
                        ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_button));
                    }
                    break;
                case Constant.Events.STOP:
                    ((MainApplication) activity.getApplication()).getMusicService().removeListener(Constant.Listener.VIEW_SONG);
                    ivPlayPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_button));
                    break;

            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    View view_bt;
    public void showDialogsongview() {
        try {
            if (null != bottomSheetDialog && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }
            view_bt = getLayoutInflater().inflate(R.layout.bottomsheet_music_info, null);
            TextView tvAlbumTitle_bt=view_bt.findViewById(R.id.tvAlbumTitle_bt);
            TextView albumTitle_bt=view_bt.findViewById(R.id.albumTitle_bt);
            TextView tvAlbumDetails_bt=view_bt.findViewById(R.id.tvAlbumDetails_bt);
            TextView postedby=view_bt.findViewById(R.id.postedby);
            TextView createdby=view_bt.findViewById(R.id.createdby);
            ImageView canselid=view_bt.findViewById(R.id.canselid);
            tvAlbumTitle_bt.setText(album.getTitle());
            albumTitle_bt.setText(album.getAlbumTitle());
            postedby.setText(album.getUserTitle());
            createdby.setText(Util.changeDateFormat(context, album.getCreationDate()));

            try {
                tvAlbumDetails_bt.setText(""+tvAlbumDetails.getText().toString()+tvAlbumDetails2.getText().toString()+tvAlbumDetails3.getText().toString());
              }catch (Exception ex){
                ex.printStackTrace();
            }

            setRatingStarsBt();
            canselid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });


            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(view_bt);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bottomSheetDialog.show();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setRatingStarsBt() {
        view_bt.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
        Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
        float rating = album.getRating().getTotalRatingAverage();
        Log.e("rating_view",""+rating);
        ((TextView) view_bt.findViewById(R.id.ratingtextid)).setText(""+rating+" Rating");
        if (rating > 0) {
            ((ImageView) view_bt.findViewById(R.id.ivStar1)).setImageDrawable(dFilledStar);
            if (rating > 1) {
                ((ImageView) view_bt.findViewById(R.id.ivStar2)).setImageDrawable(dFilledStar);
                if (rating > 2) {
                    ((ImageView) view_bt.findViewById(R.id.ivStar3)).setImageDrawable(dFilledStar);
                    if (rating > 3) {
                        ((ImageView) view_bt.findViewById(R.id.ivStar4)).setImageDrawable(dFilledStar);
                        if (rating > 4) {
                            ((ImageView) view_bt.findViewById(R.id.ivStar5)).setImageDrawable(dFilledStar);
                        }
                    }
                }
            }
        }
    }

    public static final int REQ_CODE_OPTION_UNSAVE = 205;
    public static final int REQ_CODE_OPTION_SAVE = 206;
    private void performFeedOptionClick(int actionId, int actPosition,boolean save,int shortcutid) {
        if(save){
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_REMOVESHOIRTCUT, actionId,  actPosition,save,shortcutid);
        }else {
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, actionId,  actPosition,save,shortcutid);
        }
    }

    boolean isLoading=false;
    private void callFeedEventApi(final int reqCode, String url, int actionId,  final int actPosition,boolean issave,int shortcutid) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put("resource_id", actionId);
                    request.params.put("resource_type", "sesmusic_albumsong");
                    if(issave){
                        request.params.put("shortcut_id", shortcutid);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        //  hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_SAVE:
                                            hideBaseLoader();
                                            try {
                                                isboolen=false;
                                                ivSaveFeed.setImageDrawable(dSave);
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                            }
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            hideBaseLoader();
                                            try {
                                                isboolen=true;
                                                ivSaveFeed.setImageDrawable(dUnsave);
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                            }
                                            break;
                                    }
                                } else {
                                    hideBaseLoader();
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                            hideBaseLoader();
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    hideBaseLoader();
                }
            } else {
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            CustomLog.e(e);
            hideBaseLoader();
        }
    }



}
