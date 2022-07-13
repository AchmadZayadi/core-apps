package com.sesolutions.ui.resume;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.DetailsTransition;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.ResultView;
import com.sesolutions.responses.videos.Tabs;
import com.sesolutions.responses.videos.VideoView;
import com.sesolutions.responses.videos.ViewVideo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.dashboard.FeedActivityAdapter;
import com.sesolutions.ui.dashboard.FeedHelper;
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.dashboard.composervo.ComposerOptions;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.music_album.ShowLyricsFragment;
import com.sesolutions.ui.photo.PhotoListFragment;
import com.sesolutions.ui.photo.UploadVideoFragment;
import com.sesolutions.ui.profile.ProfileChannelAdapter;
import com.sesolutions.ui.video.ChannelInfoFragment;
import com.sesolutions.ui.video.FollowerFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChannelFragment extends FeedHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "ViewChannelFragment";

    private ResultView result;
    private ViewVideo videoVo;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private RecyclerView recycleViewFeedMain;
    private static final int UPDATE_UPPER_LAYOUT = 101;
    private boolean isImageChangeSelected = false; // this will be checked when user returns to this screen

    // public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;

    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage;
    private ImageView ivProfileCompose;

    private TextView tvImage1;
    private TextView tvImage2;
    private TextView tvImage3;
    public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;

    private NestedScrollView mScrollView;

    public LinearLayoutCompat llTabOption;
    private LinearLayoutCompat llComposer;

    private AppCompatTextView tvOption1;
    private AppCompatTextView tvOption2;
    private AppCompatTextView tvOption3;
    private AppCompatTextView tvPostSomething;

    private int text2;
    private int channelId;

    private boolean isLoggedIn;
    private boolean openComment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_channel_view, container, false);
        applyTheme(v);
        cPrimary = Color.parseColor(Constant.colorPrimary);
        text2 = Color.parseColor(Constant.text_color_2);
        v.findViewById(R.id.llCoverPhoto).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
        callBottomCommentLikeApi(channelId, Constant.ResourceType.VIDEO_CHANNEL, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(channelId, Constant.ResourceType.VIDEO_CHANNEL);
        }
        return v;
    }

    private void init() {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        //  ((TextView) v.findViewById(R.id.tvTitle)).setText("");
        recycleViewFeedMain = v.findViewById(R.id.recyclerview);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        ivAlbumImage = v.findViewById(R.id.ivAlbumImage);
        tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
        llTabOption = v.findViewById(R.id.llTabOption);

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
        // v.findViewById(R.id.ivShare).setOnClickListener(this);
        // v.findViewById(R.id.ivOption).setOnClickListener(this);

        v.findViewById(R.id.ivStar1).setOnClickListener(this);
        v.findViewById(R.id.ivStar2).setOnClickListener(this);
        v.findViewById(R.id.ivStar3).setOnClickListener(this);
        v.findViewById(R.id.ivStar4).setOnClickListener(this);
        v.findViewById(R.id.ivStar5).setOnClickListener(this);

        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);

        TextView ivUserTitle = v.findViewById(R.id.ivUserTitle);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);
        tvAlbumDetail.setTypeface(iconFont);
        /*tvImageComment.setTypeface(iconFont);
        tvImageFavorite.setTypeface(iconFont);
        tvImageFavorite.setText("\uf004");
        tvImageComment.setText("\uf075");*/

        initComposer();
        mScrollView = v.findViewById(R.id.mScrollView);
        mScrollView.setVisibility(View.INVISIBLE);
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

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    boolean isShow = false;
    int scrollRange = -1;

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


            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        if (videoVo != null) {
                            collapsingToolbar.setTitle(videoVo.getTitle());
                        }
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
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
                showPopup(videoVo.getMenus(), vItem, 10);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComposer() {
        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

            llComposer = v.findViewById(R.id.llComposer);
            llComposer.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            tvOption1 = v.findViewById(R.id.tvOption1);
            tvOption2 = v.findViewById(R.id.tvOption2);
            tvOption3 = v.findViewById(R.id.tvOption3);
            tvImage1 = (AppCompatTextView) v.findViewById(R.id.tvImage1);
            tvImage2 = (AppCompatTextView) v.findViewById(R.id.tvImage2);
            tvImage3 = (AppCompatTextView) v.findViewById(R.id.tvImage3);
            tvImage1.setTypeface(iconFont);
            tvImage2.setTypeface(iconFont);
            tvImage3.setTypeface(iconFont);

            tvPostSomething = v.findViewById(R.id.tvPostSomething);
            ivProfileCompose = v.findViewById(R.id.ivProfile);
            //recycleViewFeedType = v.findViewById(R.id.rvFeedType);
            // recycleViewFeedType.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            /* hiddenPanel = v.findViewById(R.id.hidden_panel);
            hiddenPanel.setOnClickListener(this);*/
            tvPostSomething.setOnClickListener(this);
            tvOption1.setOnClickListener(this);
            tvOption2.setOnClickListener(this);
            tvOption3.setOnClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    protected void setContentLoaded() {

    }

    @Override
    public void updateComposerUI() {
        v.findViewById(R.id.llPostFeed).setVisibility(View.VISIBLE);
        if (composerOption.getResult().getEnableComposer()) {
            llComposer.setVisibility(View.VISIBLE);
            CustomLog.e("compose", "compose");
            // setAttachmentType();
            final List<ComposerOptions> list = composerOption.getResult().getComposerOptions();
            tvOption1.setText(list.get(0).getValue());
            tvOption2.setText(list.get(1).getValue());
            tvOption3.setText(list.get(2).getValue());
            tvImage1.setText(Util.getCode(list.get(0).getName(), false));
            tvImage2.setText(Util.getCode(list.get(1).getName(), false));
            tvImage3.setText(Util.getCode(list.get(2).getName(), false));

            tvImage1.setTextColor(Color.parseColor(Util.getCode(list.get(0).getName(), true)));
            tvImage2.setTextColor(Color.parseColor(Util.getCode(list.get(1).getName(), true)));
            tvImage3.setTextColor(Color.parseColor(Util.getCode(list.get(2).getName(), true)));

            Util.showImageWithGlide(ivProfileCompose, composerOption.getResult().getUser_image(), context, R.drawable.placeholder_3_2);

        } else {
            llComposer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void goToComment(int postion) {
        super.goToCommentFragment(resourceId, resourceType);
    }


 /*   private void createTabOptions(List<Tabs> tabs) {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams paramsView = new LinearLayoutCompat.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < tabs.size(); i++) {
            final Tabs tab = tabs.get(i);
            final int iFinal = i;
            TextView tv = new TextView(context);
            tv.setLayoutParams(params);
            tv.setId(1000 + i);
            tv.setPadding(16, 0, 16, 0);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextAppearance(context, R.style.primary_diselected);
            String text = tab.getLabel();
            if (tabs.get(i).getTotalCount() != 0) {
                text = text + " (" + tabs.get(i).getTotalCount() + ")";
            }
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(Constant.Events.TAB_OPTION, tab.getName(), iFinal);
                }
            });


            View v = new View(context);
            v.setLayoutParams(paramsView);
            v.setBackgroundColor(Color.GRAY);
            v.setPadding(0, 10, 0, 10);

            llTabOption.addView(tv);
            if (i != tabs.size() - 1)
                llTabOption.addView(v);
        }
    }*/

    private void createTabOptions(List<Tabs> tabs) {

        RecyclerView profiletabs = v.findViewById(R.id.profiletabs);
        if(tabs!=null && tabs.size()>0){
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            profiletabs.setLayoutManager(layoutManager);
            ProfileChannelAdapter adapter1    = new ProfileChannelAdapter(tabs, context, this);
            profiletabs.setAdapter(adapter1);
            profiletabs.setVisibility(View.VISIBLE);

        }else {
            profiletabs.setVisibility(View.GONE);
        }


       /* LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams paramsView = new LinearLayoutCompat.LayoutParams(2, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        int verticalMargin = context.getResources().getInteger(R.integer.vertical_tab_margin_profile);
        int horizontalMargin = context.getResources().getInteger(R.integer.horizontal_tab_margin_profile);
        try {
            for (int i = 0; i < tabs.size(); i++) {
                final Tabs tab = tabs.get(i);
                final int iFinal = i;
                TextView tv = new TextView(context);
                tv.setLayoutParams(params);
                tv.setId(1000 + i);
                tv.setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(text2);
                String text = tab.getLabel();
               *//* if (tabs.get(i).getTotalCount() != 0) {
                    text = text + " (" + tabs.get(i).getTotalCount() + ")";
                }*//*
                if (i == 0) {
                    tv.setTextColor(cPrimary);
                }
                tv.setText(text);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(Constant.Events.TAB_OPTION, tab.getName(), iFinal);
                    }
                });


                TextView v = new TextView(context);
                v.setLayoutParams(paramsView);
                v.setBackgroundColor(Color.LTGRAY);
                v.setGravity(Gravity.CENTER_VERTICAL);
                // v.setPadding(0, 16, 0, 16);

                llTabOption.addView(tv);
                if (i != tabs.size() - 1)
                    llTabOption.addView(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    @Override
    public boolean onItemClicked(Integer clickType, Object value, int postion) {
        switch (clickType) {
            case Constant.Events.TAB_OPTION:
                switch ("" + value) {
                    case Constant.TabOption.PHOTOS:
                        gotoPhotoList();
                        break;
                    case Constant.TabOption.VIDEOS:
                        goToViewPlaylistFragment();
                        break;
                    case Constant.TabOption.FOLLOWER:
                        goToViewFollowerFragment();
                        break;
                    case Constant.TabOption.INFO:
                        goToChannelInfo();
                        break;
                    case Constant.TabOption.OVERVIEW:
                        goToChannelOverview(result.getChannel().getTabs().get(postion).getLabel());
                        break;
                    case Constant.TabOption.DISCUSSION:
                      //  openComment(result.getChannel().getTabs().get(postion).getLabel());
                        goToCommentFragment(resourceId,"sesvideo_chanel");
                        break;
                    case Constant.TabOption.COMMENTS:
                      //  goToChannelOverview(result.getChannel().getTabs().get(postion).getLabel());
                        goToCommentFragment(resourceId,"sesvideo_chanel");
                        break;


                }
                break;
          /*  case Constant.Events.CLICKED_POST_SOMETHING:
                goToPostFeed();
                break;*/

        }
        return super.onItemClicked(clickType, value, postion);
    }

    private void goToChannelOverview(String title) {
        String lyrics = result.getChannel().getOverview();
        ShowLyricsFragment fragment = ShowLyricsFragment.newInstance(lyrics, title, true);
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementEnterTransition(new DetailsTransition());
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                fragment.setExitTransition(new Slide(Gravity.BOTTOM));
            /*    fragment.setEnterTransition(new Explode());
                fragment.setExitTransition(new Explode());*/
                fragment.setAllowEnterTransitionOverlap(true);
                fragment.setAllowReturnTransitionOverlap(false);
                fragment.setSharedElementReturnTransition(new DetailsTransition());
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    // .addSharedElement(cvLogin, res.getString(R.string.login_card))
                    //.addSharedElement(ivUserImage, res.getString(R.string.user_image))
                    //.addSharedElement(tvUserName, res.getString(R.string.username))
                    //     .addSharedElement(ivMobile, res.getString(R.string.login_mobile))
                    //    .addSharedElement(ivPassword, res.getString(R.string.login_password))
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            CustomLog.e(e);
            CustomLog.e("TRANSITION_ERROR", "Build.VERSION.SDK_INT =" + Build.VERSION.SDK_INT);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void goToPostFeed() {
        // goToPostFeed();
        // fragmentManager.beginTransaction().replace(R.id.container,
        goToPostFeed(composerOption, -1,
                resourceId, resourceType);
        //  .addToBackStack(null).commit();
    }

    private void goToViewPlaylistFragment() {
        Constant.viewVideoPlaylistChannelUrl = Constant.URL_CHANNEL_VIDEO;
        Constant.videoVo = videoVo;
        goTo(Constant.GoTo.VIEW_VIDEO_PLAYLIST, videoVo.getChannelId());

      /*  fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPlaylistVideoFragment.newInstance(videoVo.getChannelId(), Constant.URL_CHANNEL_VIDEO, videoVo))
                .addToBackStack(null)
                .commit();*/
    }


    private void goToViewFollowerFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , FollowerFragment.newInstance(videoVo.getChannelId(), Constant.ResourceType.VIDEO_CHANNEL))
                .addToBackStack(null)
                .commit();
    }

    private void goToChannelInfo() {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ChannelInfoFragment.newInstance(videoVo.getChannelId(), Constant.ResourceType.VIDEO_CHANNEL))
                .addToBackStack(null)
                .commit();
    }


    private void gotoPhotoList() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PhotoListFragment.newInstance(videoVo.getChannelId(), Constant.TITLE_PHOTOS))
                .addToBackStack(null)
                .commit();
    }

    private void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            feedActivityList = new ArrayList<>();
            recycleViewFeedMain.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedMain.setLayoutManager(layoutManager);
            adapterFeedMain = new FeedActivityAdapter(feedActivityList, context, this);
            recycleViewFeedMain.setAdapter(adapterFeedMain);
            recycleViewFeedMain.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((videoVo.getMenus() != null && videoVo.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateUpperLayout() {
      //  showHideOptionIcon();
        try {
            //  ((TextView) v.findViewById(R.id.tvTitle)).setText(videoVo.getTitle());
            collapsingToolbar.setTitle(" ");
            setRatingStars();
            createTabOptions(videoVo.getTabs());
            tvAlbumTitle.setText(videoVo.getTitle());
            Util.showImageWithGlide(ivAlbumImage, videoVo.getImages().getMain(), context, R.drawable.placeholder_square);
            if (null != videoVo.getCover()) {
                Util.showImageWithGlide(ivCoverPhoto, videoVo.getCover().getMain(), context, R.drawable.placeholder_square);
            } else {
                ivCoverPhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_3_2));
            }
            tvUserTitle.setText(videoVo.getUserTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context, videoVo.getCreationDate()));

            tvAlbumDetail.setText(getVideoDetail(videoVo, true));
            if (result.getLoggedinUserId() == videoVo.getOwnerId()) {
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
        float rating = videoVo.getRating().getTotalRatingAverage();
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

   /* private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SONG_ALBUM);
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);

    }*/


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
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareInside(videoVo.getShare(), true);
                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(videoVo.getShare());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
            progressDialog.findViewById(R.id.card3).setVisibility(View.GONE);
            progressDialog.findViewById(R.id.card1).setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
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
                case R.id.tvOption1:
                    goToPostFeed(composerOption, 0, resourceId, resourceType);
                    break;
                case R.id.tvOption2:
                    goToPostFeed(composerOption, 1, resourceId, resourceType);
                    break;
                case R.id.tvOption3:
                    goToPostFeed(composerOption, 2, resourceId, resourceType);
                    break;

               /* case R.id.hidden_panel:
                    hideSlideLayout();
                    break;*/
                case R.id.tvPostSomething:
                    goToPostFeed(composerOption, -1,
                            resourceId, resourceType);
                    //  goToPostFeed();
                    // onItemClicked(Constant.Events.CLICKED_POST_SOMETHING, "", 0);
                    break;

                case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;

                case R.id.ivOption:
                    showPopup(videoVo.getMenus(), view, 10);
                    break;

                case R.id.llAlbumImage:
                    showPopup(videoVo.getProfileImageOptions(), view, 100);
                    break;

                case R.id.ivCoverPhoto:
                    showPopup(videoVo.getCoverImageOptions(), v.findViewById(R.id.ivCamera), 1000);
                    break;

                case R.id.ivStar1:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        videoVo.getRating().setTotalRatingAverage(1);
                        setRatingStars();
                        callRatingApi(1);
                    }
                    break;
                case R.id.ivStar2:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        videoVo.getRating().setTotalRatingAverage(2);
                        setRatingStars();
                        callRatingApi(2);
                    }
                    break;
                case R.id.ivStar3:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        videoVo.getRating().setTotalRatingAverage(3);
                        setRatingStars();
                        callRatingApi(3);
                    }
                    break;
                case R.id.ivStar4:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        videoVo.getRating().setTotalRatingAverage(4);
                        setRatingStars();
                        callRatingApi(4);
                    }
                    break;
                case R.id.ivStar5:
                    if (isLoggedIn()) {
                        unfilledAllStar();
                        videoVo.getRating().setTotalRatingAverage(5);
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


    private void callRatingApi(int rating) {
        if (videoVo.getRating().getCode() != 100) {
            Util.showSnackbar(v, videoVo.getRating().getMessage());
            return;
        }

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_RATE_VIDEO);

                    request.params.put(Constant.KEY_RATING, rating);
                    request.params.put(Constant.KEY_RESOURCE_ID, videoVo.getChannelId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.VIDEO_CHANNEL);
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
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_CHANNEL_ID, videoVo.getChannelId());
                    request.params.put(Constant.KEY_RESOURCE_ID, videoVo.getChannelId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, videoVo.getResourceType());
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
                                   /* try {
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
                                    } catch (Exception e) {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }*/
                                    Object res = new JSONObject(response).get("result");
                                    if (res instanceof String) {
                                        Util.showSnackbar(v, (String) res);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                    }
                                   /* ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }*/
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

    @Override
    public void onStart() {
        super.onStart();
        if (isImageChangeSelected || activity.taskPerformed == Constant.FormType.TYPE_EDIT_CHANNEL) {
            isImageChangeSelected = false;
            activity.taskPerformed = 0;
            callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
        } else if (Constant.channelId > 0) {
            goToUploadVideoFragment(Constant.channelId);
            Constant.channelId = 0;
        }
        if (Constant.TASK_POST) {
            Constant.TASK_POST = false;
            callFeedApi(REQ_CODE_REFRESH);
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
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_CHANNEL_VIEW);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    request.params.put(Constant.KEY_CHANNEL_ID, channelId);
                    /*if (req == UPDATE_UPPER_LAYOUT) {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }*/
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
                                        showView(mScrollView);
                                        VideoView resp = new Gson().fromJson(response, VideoView.class);
                                        result = resp.getResult();
                                        videoVo = result.getChannel();
                                        updateUpperLayout();
                                        if (req != UPDATE_UPPER_LAYOUT) {
                                            callComposerOptionApi();
                                            //   updateAdapter();
                                        }
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


    public static ViewChannelFragment newInstance(int albumId, boolean openComment) {
        ViewChannelFragment frag = new ViewChannelFragment();
        frag.channelId = albumId;
        frag.openComment = openComment;
        return frag;
    }

    public static ViewChannelFragment newInstance(int albumId) {
        return ViewChannelFragment.newInstance(albumId, false);
    }


    @Override
    public void onLoadMore() {
    }

    public void loadMore() {
       /* try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
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
                opt = videoVo.getCoverImageOptions().get(itemId - 1);

            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = videoVo.getProfileImageOptions().get(itemId - 1);

            } else {
                itemId = itemId - 10;
                opt = videoVo.getMenus().get(itemId - 1);
            }
            //  opt = videoVo.getMenus().get();

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
                case Constant.OptionType.VIDEOS:
                    goToUploadVideoFragment(videoVo.getChannelId());
                    break;
                case Constant.OptionType.remove_profile_photo:
                    showImageRemoveDialog(false, Constant.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION, Constant.URL_CHANNEL_REMOVE_PHOTO);
                    break;
                case Constant.OptionType.view_profile_photo:
                    goToPhotoView(videoVo.getImages().getMain());
                    break;
                case Constant.OptionType.CHOOSE_FROM_ALBUMS:
                    isImageChangeSelected = true;
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_CHANNEL_ID, resourceId);
                    map.put(Constant.KEY_ID, resourceId);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_CHANNEL_COVER : Constant.URL_UPLOAD_CHANNEL_MAIN_PHOTO, map);
                    break;
                case Constant.OptionType.UPLOAD_PHOTO:
                    isImageChangeSelected = true;
                    String imageUrl = Constant.EMPTY;
                    if (null != videoVo.getImages()) {
                        imageUrl = videoVo.getImages().getMain();
                    }
                    gToAlbumImage(Constant.URL_UPLOAD_CHANNEL_MAIN_PHOTO, imageUrl, Constant.TITLE_EDIT_MUSIC_PHOTO);
                    break;
                case Constant.OptionType.upload_cover:
                    isImageChangeSelected = true;
                    imageUrl = Constant.EMPTY;
                    if (null != videoVo.getCover()) {
                        imageUrl = videoVo.getCover().getMain();
                    }
                    gToAlbumImage(Constant.URL_UPLOAD_CHANNEL_COVER, imageUrl, Constant.TITLE_EDIT_COVER);
                    break;
                case Constant.OptionType.view_cover_photo:
                    goToPhotoView(videoVo.getCover().getMain());
                    break;
                case Constant.OptionType.remove_cover_photo:
                    showImageRemoveDialog(true, Constant.MSG_COVER_DELETE_CONFIRMATION, Constant.URL_CHANNEL_REMOVE_COVER);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    private void goToUploadVideoFragment(int channelId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, UploadVideoFragment.newInstance(channelId, Constant.TITLE_UPLOAD_VIDEO))
                .addToBackStack(null)
                .commit();
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_CHANNEL_ID, videoVo.getChannelId());
        map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction()
                .replace(R.id.container, FormFragment.newInstance
                        (Constant.FormType.TYPE_EDIT_CHANNEL,
                                map, Constant.URL_EDIT_CHANNEL))
                .addToBackStack(null)
                .commit();
    }

    private void goToReportFragment() {
        String guid = Constant.ResourceType.VIDEO_CHANNEL + "_" + videoVo.getChannelId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_CHANNEL_ID, videoVo.getChannelId());
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
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_CHANNEL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(videoVo.getChannelId());
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


    private void callDeleteApi( final int ChannelId) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_CHANNEL);
                    request.params.put(Constant.KEY_CHANNEL_ID, ChannelId);
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

                                    onBackPressed();
                                    BaseActivity.backcoverchange = Constant.TASK_DELETE_CHANNEL;
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? Constant.TXT_REMOVE_COVER : Constant.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callRemoveImageApi(url);
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
