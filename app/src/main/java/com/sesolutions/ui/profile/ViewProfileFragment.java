package com.sesolutions.ui.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.profile.ProfileInfo;
import com.sesolutions.responses.profile.ProfileResponse;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.SearchAlbumFragment;
import com.sesolutions.ui.blogs.SearchBlogFragment;
import com.sesolutions.ui.clickclick.me.FollowFollowingUser;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.customviews.SquareImageView;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.member.MemberFragment;
import com.sesolutions.ui.member.SomeDrawable;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.SearchMusicAlbumFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.storyview.MyStory;
import com.sesolutions.ui.storyview.StoryContent;
import com.sesolutions.ui.storyview.StoryHighlightAdapter;
import com.sesolutions.ui.storyview.StoryModel;
import com.sesolutions.ui.video.SearchVideoFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;
import static com.sesolutions.utils.Constant.OptionType.edit_profile_location;
import static com.sesolutions.utils.URL.BASE_URL;

public class ViewProfileFragment extends CommentLikeHelper implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, TabLayout.OnTabSelectedListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    private final int REQ_HIGHLIGHT = 671;
    public ImageView ivCoverPhoto;
    public TextView tvTabbed1;
    public ImageView ivTabbed1;
    public TextView tvTabbed2;
    public ImageView ivTabbed2;
    public TextView tvTabbed3;
    public ImageView ivTabbed3;
    public TextView tvTabbed4;
    public ImageView ivTabbed4;
    public TextView tvUserTitle;
    public TextView tvStatus;
    public LinearLayoutCompat llTabOption;
    ProfileResponse.Result result;
    ImageView ivAlbumImage;
    ImageView ivVerify;
    List<ProfileInfo> profileList;
    AppCompatTextView tvFriend1;
    AppCompatTextView tvFriend2;
    AppCompatTextView tvFriend3;
    AppCompatTextView tvFriend4;
    AppCompatTextView tvFriend5;
    AppCompatTextView tvFriend6;
    SquareImageView ivFriend1;
    SquareImageView ivFriend2;
    SquareImageView ivFriend3;
    SquareImageView ivFriend4;
    SquareImageView ivFriend5;
    SquareImageView ivFriend6;
    int textColor1;
    int colorPrimary;
    Bundle bundle;
    int userId;
    ShimmerFrameLayout mShimmerViewContainer;


    public static ViewProfileFragment newInstance(int albumId) {
        ViewProfileFragment frag = new ViewProfileFragment();
        frag.userId = albumId;
        return frag;
    }

    public static ViewProfileFragment newInstance(int albumId, Bundle bundle) {
        ViewProfileFragment frag = new ViewProfileFragment();
        frag.userId = albumId;
        frag.bundle = bundle;
        return frag;
    }


    String profile_image = "", profile_title = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        try {
            v = inflater.inflate(R.layout.fragment_view_profile, container, false);
            applyTheme(v);
            init();
            if (bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    profile_title = bundle.getString(Constant.Trans.IMAGE);
                    profile_image = bundle.getString(Constant.Trans.IMAGE_URL);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
            //  callMusicAlbumApi(1);
        } catch (Exception e) {
            CustomLog.e(e);
        }


        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        ivAlbumImage.setTransitionName(profile_title);
        tvUserTitle.setText(profile_title);
        try {
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
                    .load(profile_image)
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
        callMusicAlbumApi(1);
    }

    private List<StoryContent> highlightList;
    private StoryHighlightAdapter adapterHighlight;

    private void initHighlightsList() {
        v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
        RecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
        //highlightList = new ArrayList<>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterHighlight = new StoryHighlightAdapter(highlightList, context, this);
        rvPhotos.setAdapter(adapterHighlight);
       /* if(highlightList.size()>0){
            v.findViewById(R.id.rlRecent).setVisibility(View.VISIBLE);
        }else {
            v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
        }*/
    }

    private void callHighlightAPI() {
        Map<String, Object> map = new HashMap<>();
        map.put("highlight", 1);
        map.put(Constant.KEY_USER_ID, userId);
        new ApiController(Constant.URL_STORY_BROWSE, map, context, this, REQ_HIGHLIGHT).execute();
    }

    private void init() {
        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            colorPrimary = Color.parseColor(Constant.colorPrimary);
            textColor1 = Color.parseColor(Constant.text_color_1);
            ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
            tvStatus = v.findViewById(R.id.tvStatus);
            llTabOption = v.findViewById(R.id.llTabOption);
            ivAlbumImage = v.findViewById(R.id.ivAlbumImage);
            ivVerify = v.findViewById(R.id.iv_verify);
            mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);

            tvUserTitle = v.findViewById(R.id.tvUserTitle);

            tvTabbed1 = v.findViewById(R.id.tvTabbed1);
            ivTabbed1 = v.findViewById(R.id.ivTabbed1);
            tvTabbed2 = v.findViewById(R.id.tvTabbed2);
            ivTabbed2 = v.findViewById(R.id.ivTabbed2);
            tvTabbed3 = v.findViewById(R.id.tvTabbed3);
            ivTabbed3 = v.findViewById(R.id.ivTabbed3);
            tvTabbed4 = v.findViewById(R.id.tvTabbed4);
            ivTabbed4 = v.findViewById(R.id.ivTabbed4);


            v.findViewById(R.id.llTabbed1).setOnClickListener(this);
            v.findViewById(R.id.llTabbed2).setOnClickListener(this);
            v.findViewById(R.id.llTabbed3).setOnClickListener(this);
            v.findViewById(R.id.llTabbed4).setOnClickListener(this);
            v.findViewById(R.id.tvFindFriends).setOnClickListener(this);

            initComposer();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmerAnimation();

            v.findViewById(R.id.rlUpper).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        } catch (Exception e) {
            CustomLog.e(e);
        }

        initCollapsingToolbar();
    }

    CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.backgroundColor));
        // toolbar.setTitle("");
        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset <= 200) {
                        if (result != null) {
                            collapsingToolbar.setTitle(result.getProfile().getDisplayname());
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
        inflater.inflate(R.menu.view_menu_option, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {

                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getGutterMenu(), vItem, 10, this);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComposer() {
        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

            tvFriend1 = v.findViewById(R.id.tvFriend1);
            tvFriend2 = v.findViewById(R.id.tvFriend2);
            tvFriend3 = v.findViewById(R.id.tvFriend3);
            tvFriend4 = v.findViewById(R.id.tvFriend4);
            tvFriend5 = v.findViewById(R.id.tvFriend5);
            tvFriend6 = v.findViewById(R.id.tvFriend6);
            ivFriend1 = v.findViewById(R.id.ivFriend1);
            ivFriend2 = v.findViewById(R.id.ivFriend2);
            ivFriend3 = v.findViewById(R.id.ivFriend3);
            ivFriend4 = v.findViewById(R.id.ivFriend4);
            ivFriend5 = v.findViewById(R.id.ivFriend5);
            ivFriend6 = v.findViewById(R.id.ivFriend6);

            ivFriend1.setOnClickListener(this);
            ivFriend2.setOnClickListener(this);
            ivFriend3.setOnClickListener(this);
            ivFriend4.setOnClickListener(this);
            ivFriend5.setOnClickListener(this);
            ivFriend6.setOnClickListener(this);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setFriendImages() {
        try {
            List<Friends> friends = new ArrayList<>();
            if (null != result.getMutualFriends()) {
                ((TextView) v.findViewById(R.id.tvTitleFriend)).setText(R.string.MUTUAL_FRINEDS);
                friends = result.getMutualFriends();
            }
            if (null != result.getProfileFriends()) {
                ((TextView) v.findViewById(R.id.tvTitleFriend)).setText(R.string.TITLE_FRIENDS);
                friends = result.getProfileFriends();
            }

            if (!TextUtils.isEmpty(result.getProfile().getTotalFriendCount())) {
                v.findViewById(R.id.tvTotalFriend).setVisibility(View.VISIBLE);
                v.findViewById(R.id.vDot).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvTotalFriend)).setText(result.getProfile().getTotalFriendCount());
            } else {
                v.findViewById(R.id.tvTotalFriend).setVisibility(View.GONE);
                v.findViewById(R.id.vDot).setVisibility(View.GONE);
            }

            int size = friends.size();
            if (size > 0) {
                v.findViewById(R.id.llFriend).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llRow1).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llFriend1).setVisibility(View.VISIBLE);
                tvFriend1.setText(friends.get(0).getTitle());
                Util.showImageWithGlide(ivFriend1, friends.get(0).getUserImage(), context, R.drawable.placeholder_square);
            }
            if (size > 1) {
                v.findViewById(R.id.llFriend2).setVisibility(View.VISIBLE);
                tvFriend2.setText(friends.get(1).getTitle());
                Util.showImageWithGlide(ivFriend2, friends.get(1).getUserImage(), context, R.drawable.placeholder_square);
            }
            if (size > 2) {
                v.findViewById(R.id.llFriend3).setVisibility(View.VISIBLE);
                tvFriend3.setText(friends.get(2).getTitle());
                Util.showImageWithGlide(ivFriend3, friends.get(2).getUserImage(), context, R.drawable.placeholder_square);
            }
            if (size > 3) {
                v.findViewById(R.id.llRow2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llFriend4).setVisibility(View.VISIBLE);
                tvFriend4.setText(friends.get(3).getTitle());
                Util.showImageWithGlide(ivFriend4, friends.get(3).getUserImage(), context, R.drawable.placeholder_square);
            }
            if (size > 4) {
                v.findViewById(R.id.llFriend5).setVisibility(View.VISIBLE);
                tvFriend5.setText(friends.get(4).getTitle());
                Util.showImageWithGlide(ivFriend5, friends.get(4).getUserImage(), context, R.drawable.placeholder_square);
            }
            if (size > 5) {
                v.findViewById(R.id.llFriend6).setVisibility(View.VISIBLE);
                tvFriend6.setText(friends.get(5).getTitle());
                Util.showImageWithGlide(ivFriend6, friends.get(5).getUserImage(), context, R.drawable.placeholder_square);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer clickType, Object value, int postion) {
        switch (clickType) {
            case Constant.Events.TAB_OPTION:
                handleTabOptionClicked("" + value, postion);
                break;
            case Constant.Events.TAB_OPTION_PROFILE:
                // handleTabOptionClicked("" + value, postion);
                callFragment(value);

                break;

            case Constant.Events.MENU_MAIN:
                value = profileList.get(postion).getValue();
                if (("" + value).startsWith("www")) {
                    value = "http://" + value;
                }
                if (("" + value).startsWith("http")) {
                    if (("" + value).contains("facebook")) {
                        openFbPage("" + value);
                    } else {
                        openWebView("" + value, profileList.get(postion).getLabel());
                    }
                } else if (profileList.get(postion).getKey().equals("location")) {
                    openMapIntent(postion);
                }
                break;
            case Constant.Events.VIEW_STORY:

                StoryModel model2 = new StoryModel(result.getProfile().getUserPhoto(), result.getProfile().getDisplayname(), userId);
                List<StoryContent> list = new ArrayList<StoryContent>();
                list.add(highlightList.get(postion));
                model2.setImages(list);
                boolean isOwner = SPref.getInstance().getLoggedInUserId(context) == userId;
                fragmentManager.beginTransaction().replace(R.id.container, MyStory.newInstance(model2, isOwner)).addToBackStack(null).commit();
                break;

            case REQ_HIGHLIGHT:
                try {
                    if (null != value) {
                        StoryResponse resp = new Gson().fromJson("" + value, StoryResponse.class);
                        if (resp.isSuccess()) {
                            highlightList = resp.getResult().getStories().get(0).getImages();
                            initHighlightsList();
                        } else {
                            v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    CustomLog.e(e);
                }
                break;
        }
        return super.onItemClicked(clickType, value, postion);

    }

    private void callFragment(Object value) {
        Options opt = (Options) value;
        Intent intent2 = null;
        Log.e("Tag", "" + opt.getName());
        switch (opt.getName()) {
            case Constant.TabOption.INFO:


                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;

            case Constant.TabOption.FOLLOWER_TAG:
                try {

                    intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.FOLLOWER_ACTIVITY);
                    intent2.putExtra(Constant.KEY_ID, userId);
                    intent2.putExtra(Constant.KEY_TITLE, "Followers");
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                    // if(Integer.parseInt(opt.getTotalCount())<2){
                    //     adapter.addFragment(FollowFollowingUser.newInstance(userId, "Followers"), "Follower");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.TabOption.RECENTVIEWEDBYME:
                try {
                    intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_RECNTVIEW);
                    intent2.putExtra(Constant.KEY_ID, userId);
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                    // if(Integer.parseInt(opt.getTotalCount())<2){
                    //     adapter.addFragment(FollowFollowingUser.newInstance(userId, "Followers"), "Follower");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.TabOption.RECENTVIEWEDME:
                try {
                    intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_RECNTVIEWME);
                    intent2.putExtra(Constant.KEY_ID, userId);
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                    // if(Integer.parseInt(opt.getTotalCount())<2){
                    //     adapter.addFragment(FollowFollowingUser.newInstance(userId, "Followers"), "Follower");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;


            case Constant.TabOption.FOLLOWING_TAG:
                try {

                    intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.FOLLOWFOLLOWING_ACTIVITY);
                    intent2.putExtra(Constant.KEY_ID, userId);
                    intent2.putExtra(Constant.KEY_TITLE, "Following");
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);

                    // if(Integer.parseInt(opt.getTotalCount())<2){
                    //     adapter.addFragment(FollowFollowingUser.newInstance(userId, "Followers"), "Follower");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;


/*
            case Constant.TabOption.INFO:


                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;
                */

            case Constant.TabOption.UPDATES:
                // adapter.addFragment(FeedFragment.newInstance(userId, Constant.ResourceType.USER), opt.getLabel());
                break;

            case Constant.TabOption.OVERVIEW:
                        /*Map<String, Object> map = new HashMap<>();
                        map.put(Constant.TEXT, result.getContest().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());*/
                break;

            case Constant.TabOption.FRIENDS:
                //  goToMemberFragment(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_FRIEND);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //adapter.addFragment(MemberFragment.newInstance(userId, false), opt.getLabel());
                break;
            case Constant.TabOption.ALBUM:
            case Constant.TabOption.PHOTOS:
                //goToSearchAlbumFragment(userId);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_ALBUM);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;
            case Constant.TabOption.PHOTO:
                //goToSearchAlbumFragment(userId);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_PHOTO);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.EVENT:
                break;
            case Constant.TabOption.BLOG:
                // goToSearchBlogFragment(userId);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_BLOCK);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(SearchBlogFragment.newInstance(userId), opt.getLabel());
                break;
            case Constant.TabOption.MUSIC:
                //  goToSearchMusicFragment(userId);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_MUSIC);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchMusicAlbumFragment.newInstance(userId), opt.getLabel());
                break;
            case Constant.TabOption.VIDEO:
                //     goToSearchVideoFragment(userId);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_VIDEO);
                intent2.putExtra(Constant.KEY_ID, userId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.MAP:
                bundle = new Bundle();
                HashMap<String, Object> map = new HashMap<>();
                bundle.putString(Constant.KEY_URI, Constant.URL_PROFILE_MAP);
                bundle.putInt(Constant.KEY_ID, userId);
                map = new HashMap<>();
                bundle.putSerializable(Constant.POST_REQUEST, map);


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_INFO_MAP);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;
        }
    }

    private void openMapIntent(int postion) {
        ProfileInfo vo = profileList.get(postion);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + vo.getLat() + "," + vo.getLng()));
/*
        saddr=20.344,34.34&
*/
        startActivity(intent);
    }

    private void handleTabOptionClicked(String value, int postion) {
        switch (value) {
            case Constant.TabOption.INFO:
                goToProfileInfo(userId, false);
                break;
            case Constant.TabOption.FRIENDS:
                goToMemberFragment(userId, true);
                break;
            case Constant.TabOption.ALBUM:
                goToSearchAlbumFragment(userId);
                break;
            case Constant.TabOption.EVENT:
                break;
            case Constant.TabOption.BLOG:
                goToSearchBlogFragment(userId);
                break;
            case Constant.TabOption.MUSIC:
                goToSearchMusicFragment(userId);
                break;
            case Constant.TabOption.VIDEO:
                goToSearchVideoFragment(userId);
                break;
        }
    }

    private void handleFriendClick(int pos) {
        List<Friends> friends = null;
        if (null != result.getMutualFriends()) {
            friends = result.getMutualFriends();
        } else if (null != result.getProfileFriends()) {
            friends = result.getProfileFriends();
        }
        if (userId != friends.get(pos).getUserId())
            goToProfileFragment(friends.get(pos).getUserId());
    }

    private void handleOptionClick(int position) {
        if (position == 3 && !result.isSelf()) {
            /*show popup if menu is more than 3 for other users*/
            int size = result.getProfileTabbedMenu().size();
            showPopup(result.getProfileTabbedMenu().subList(3, size), ivTabbed4, 150, this);
            return;
        }
        try {
            String type = result.getProfileTabbedMenu().get(position).getName();
            switch (type) {
                case Constant.OptionType.EDIT_POST:
                    openUserProfileEditForm();
                    break;

                case Constant.OptionType.PRIVACY_SETTING:
                    goToGeneralSettingForm(Constant.URL_GENERAL_PRIVACY, Constant.TITLE_PRIVACY_SETTING);
                    break;
                case Constant.OptionType.NOTIFICATION:
                    goToGeneralSettingForm(Constant.URL_GENERAL_NOTIFICATIONS, Constant.TITLE_NOTIFICATION_SETTING);
                    break;
                case Constant.OptionType.POST:
                    goToPostFeed(SPref.getInstance().getComposerOptions(context), -1, userId, Constant.ResourceType.USER);
                    break;
                default:
                    performClick(result.getProfileTabbedMenu().get(position),
                            false, position, true);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openFbPage(String url) {
        Intent intent;
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.ic_facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            CustomLog.e("SESOLUTION", "facebook app not installed");
        }
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void showHideOptionIcon() {
        try {
         //   Objects.requireNonNull(getActivity()).findViewById(R.id.option).setVisibility((result.getGutterMenu() != null && result.getGutterMenu().size() > 0) ? View.VISIBLE : View.GONE);
            requireActivity().findViewById(R.id.option).setVisibility((result.getGutterMenu() != null && result.getGutterMenu().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateUpperLayout() {
        showHideOptionIcon();
        try {
            //  ((TextView) v.findViewById(R.id.tvTitle)).setText(result.getProfile().getDisplayname());
            ((CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar)).setTitle(" ");
            //     v.findViewById(R.id.tabs).setVisibility(View.VISIBLE);

            //      profiletabs
            setupViewPager();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadScreenData(0);
                }
            }, 200);

            tvUserTitle.setText(result.getProfile().getDisplayname());
            //   collapsingToolbar.setTitle(result.getProfile().getDisplayname());
//            if (TextUtils.isEmpty(result.getProfile().getStatus())) {
//                tvStatus.setVisibility(View.GONE);
//            } else {
//                tvStatus.setVisibility(View.VISIBLE);
//                try {
//                    tvStatus.setText(Util.stripHtml(result.getProfile().getStatus()));
//                    CustomLog.d("hasilnyaaprofile",result.getProfile().getStatus());
//                    // tvStatus.setText(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(result.getProfile().getStatus())));
//                } catch (Exception e) {
//                    CustomLog.e("Profile", "unable to parse emoji");
//                    //  tvStatus.setText(StringEscapeUtils.unescapeHtml4(result.getProfile().getStatus()));
//                    tvStatus.setText(Util.stripHtml(result.getProfile().getStatus()));
//                    //  tvStatus.setText(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(result.getProfile().getStatus())));
//                }
//            }
            if (result.getProfile().getLevelId() == 3){
                ivVerify.setImageResource(R.drawable.ic_verified);
            }

            Util.showImageWithGlide(ivAlbumImage, result.getProfile().getUserPhoto(), context/*, R.drawable.placeholder_square*/);
//            if (!TextUtils.isEmpty(result.getCoverPhoto())) {
//                Util.showImageWithGlide(ivCoverPhoto, result.getCoverPhoto(), context, R.drawable.placeholder_square);
//            } else {
//
//                try {
//                //    ivCoverPhoto.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_profile_cover));
//                    SomeDrawable drawable21 = new SomeDrawable(Color.parseColor(Constant.backgroundColor),Color.parseColor(Constant.backgroundColor),Color.parseColor(Constant.backgroundColor),1,Color.parseColor(Constant.backgroundColor),0);
//                    ivCoverPhoto.setBackgroundDrawable(drawable21);
//                    }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//
//
//            }
            if (result.isSelf() && null != result.getProfileImageOption()) {
                v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llAlbumImage).setOnClickListener(this);
            } else {
                v.findViewById(R.id.llAlbumImage).setOnClickListener(this);
                v.findViewById(R.id.ivCamera2).setVisibility(View.GONE);
            }

            if (result.isSelf() && null != result.getCoverImageOption()) {
                // v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCoverPhoto).setOnClickListener(this);
            } else {
                v.findViewById(R.id.ivCamera).setVisibility(View.GONE);
            }


            setFriendImages();

            /* updating userVo in share preference
             * ,so that when user goes back to main activity
             * ,his profile pic will be updated
             */
            UserMaster vo = SPref.getInstance().getUserMasterDetail(context);
            if (vo.getUserId() == userId) {
                vo.setPhotoUrl(result.getProfile().getUserPhoto());
                SPref.getInstance().saveUserMaster(context, vo, null);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //set tab bar items
    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        if (result.getProfileTabs() != null) {
            setupViewPager();
            tabLayout.clearOnTabSelectedListeners();
            //   tabLayout.setupWithViewPager(viewPager, true);
            tabLayout.addOnTabSelectedListener(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadScreenData(0);
                }
            }, 200);
        } else {
            tabLayout.setVisibility(View.GONE);
        }
    }


    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(getFragmentManager());
            adapter.showTab(true);
            List<Options> list = result.getProfileTabs();
            for (Options opt : list) {
                switch (opt.getName()) {
                    case Constant.TabOption.INFO:
                        adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());
                        break;


                    case Constant.TabOption.UPDATES:
                        adapter.addFragment(FeedFragment.newInstance(userId, Constant.ResourceType.USER), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        /*Map<String, Object> map = new HashMap<>();
                        map.put(Constant.TEXT, result.getContest().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());*/
                        break;

                    case Constant.TabOption.FRIENDS:
                        adapter.addFragment(MemberFragment.newInstance(userId, false), opt.getLabel());
                        break;
                    case Constant.TabOption.ALBUM:
                        adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                        break;
                    case Constant.TabOption.EVENT:
                        break;
                    case Constant.TabOption.BLOG:
                        adapter.addFragment(SearchBlogFragment.newInstance(userId), opt.getLabel());
                        break;
                    case Constant.TabOption.MUSIC:
                        adapter.addFragment(SearchMusicAlbumFragment.newInstance(userId), opt.getLabel());
                        break;
                    case Constant.TabOption.VIDEO:
                        adapter.addFragment(SearchVideoFragment.newInstance(userId), opt.getLabel());
                        break;
                    case Constant.TabOption.MAP:
                        bundle = new Bundle();
                        HashMap<String, Object> map = new HashMap<>();
                        bundle.putString(Constant.KEY_URI, Constant.URL_PROFILE_MAP);
                        bundle.putInt(Constant.KEY_ID, userId);
                        map = new HashMap<>();
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(ProfileMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    /*default:
                        adapter.addFragment(PageMemberFragment.newInstance(null), opt.getLabel());
                        break;*/

                }
            }

            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[result.getProfileTabs().size()];
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(isLoaded.length);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private boolean[] isLoaded;

    private void loadScreenData(int position) {
        // do not load tab if already loaded
        try {
            if (!isLoaded[position] && isNetworkAvailable(context)) {
                isLoaded[position] = true;
                adapter.getItem(position).initScreenData();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loadScreenData(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        try {
            (adapter.getItem(tab.getPosition())).onRefresh();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerViewProfileInfo() {
        try {
            RecyclerView recycleViewInfo = v.findViewById(R.id.rvProfileInfo);
            View view21 = v.findViewById(R.id.view21);

            if (null != result.getProfileInfo() && result.getProfileInfo().size() > 0) {
                recycleViewInfo.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                profileList = new ArrayList<>();
                profileList.addAll(result.getProfileInfo());
                recycleViewInfo.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                recycleViewInfo.setLayoutManager(layoutManager);

                recycleViewInfo.setNestedScrollingEnabled(false);

            } else {
                recycleViewInfo.setVisibility(View.GONE);
                view21.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setProfileTabbedMenus() {
        try {
            String packageName = context.getPackageName();
            List<Options> menu = result.getProfileTabbedMenu();
            if (menu.size() > 0) {
                tvTabbed1.setText(menu.get(0).getLabel());
                int id = context.getResources().getIdentifier(menu.get(0).getName().replace("-", "_"), "drawable", packageName);
                ivTabbed1.setImageResource(id);
                v.findViewById(R.id.llTabbed1).setVisibility(View.VISIBLE);
            }
            if (menu.size() > 1) {
                tvTabbed2.setText(menu.get(1).getLabel());
                int id = context.getResources().getIdentifier(menu.get(1).getName().replace("-", "_"), "drawable", packageName);
                ivTabbed2.setImageResource(id);
                v.findViewById(R.id.llTabbed2).setVisibility(View.VISIBLE);

            }
            if (menu.size() > 2) {
                tvTabbed3.setText(menu.get(2).getLabel());
                int id = context.getResources().getIdentifier(menu.get(2).getName().replace("-", "_"), "drawable", packageName);
                ivTabbed3.setImageResource(id);
                v.findViewById(R.id.llTabbed3).setVisibility(View.VISIBLE);

            }
            if (menu.size() > 3) {
                if (result.isSelf()) {
                    tvTabbed4.setText(menu.get(3).getLabel());
                    int id = context.getResources().getIdentifier(menu.get(3).getName().replace("-", "_"), "drawable", packageName);
                    ivTabbed4.setImageResource(id);
                } else {
                    tvTabbed4.setText(Constant.TEXT_MORE);
                    //  int id = context.getResources().get);
                    ivTabbed4.setImageResource(R.drawable.more_profile);
                }
                v.findViewById(R.id.llTabbed4).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivOption:
                    showPopup(result.getGutterMenu(), view, 10, this);
                    break;

                case R.id.llAlbumImage:
                    if (result.isSelf())
                        showPopup(result.getProfileImageOption(), v.findViewById(R.id.ivCamera2), 100, this);
                    else {
                        //  goToGalleryFragment(result.getProfile().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                        fragmentManager.beginTransaction()
                                .replace(R.id.container
                                        , SinglePhotoFragment.newInstance(result.getProfile().getUserPhoto()))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case R.id.ivCoverPhoto:
                    if (null != result.getCoverImageOption())
                        showPopup(result.getCoverImageOption(), v.findViewById(R.id.ivCamera), 1000, this);
                    break;
                case R.id.llTabbed1:
                    handleOptionClick(0);
                    break;
                case R.id.llTabbed2:
                    handleOptionClick(1);

                    break;
                case R.id.llTabbed3:
                    handleOptionClick(2);

                    break;
                case R.id.llTabbed4:
                    handleOptionClick(3);
                    break;
                case R.id.ivFriend1:
                    handleFriendClick(0);
                    break;
                case R.id.ivFriend2:
                    handleFriendClick(1);

                    break;
                case R.id.ivFriend3:
                    handleFriendClick(2);

                    break;
                case R.id.ivFriend4:
                    handleFriendClick(3);
                    break;

                case R.id.ivFriend5:
                    handleFriendClick(4);

                    break;
                case R.id.ivFriend6:
                    handleFriendClick(5);
                    break;

                case R.id.tvFindFriends:
                    goToMemberFragment(-1, true);
                    break;

             /*   case R.id.ivCoverPhoto:
                    showPopup(result..getCoverImageOptions(), v.findViewById(R.id.ivCamera), 1000);
                    break;*/

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.TASK_IMAGE_UPLOAD
                || activity.taskPerformed == Constant.FormType.EDIT_USER) {
            activity.taskPerformed = 0;
            callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
        }

    }


    private void callRemoveImageApi(String url) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_USER_ID, userId);
                    request.params.put(Constant.KEY_RESOURCE_ID, userId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.USER);
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
        try {
            if (isNetworkAvailable(context)) {

                //  showBaseLoader(false);
                try {
                    if (req == 1) {
                        //  showView(v.findViewById(R.id.pbMain));

                    }
                    HttpRequestVO request = new HttpRequestVO(BASE_URL + "profile/" + userId + Constant.POST_URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideAllLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    showView(v.findViewById(R.id.llDetailMain));
                                    ProfileResponse resp = new Gson().fromJson(response, ProfileResponse.class);
                                    result = resp.getResult();
                                    updateUpperLayout();
                                    RecyclerView profiletabs = v.findViewById(R.id.profiletabs);

                                    profiletabs.setBackgroundColor(Color.parseColor(Constant.backgroundColor));
                                    if (result.getProfileTabs() != null && result.getProfileTabs().size() > 0) {
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                        profiletabs.setLayoutManager(layoutManager);
                                        List<Options> profillist = new ArrayList<>();
                                        try {
                                            for (int j = 0; j < result.getProfileTabs().size(); j++) {
                                                if (!result.getProfileTabs().get(j).getName().equalsIgnoreCase("sescontest")) {
                                                    profillist.add(result.getProfileTabs().get(j));
                                                }
                                            }
                                            ProfileTabsAdapter adapter1 = new ProfileTabsAdapter(profillist, context, this);
                                            profiletabs.setAdapter(adapter1);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }


                                        profiletabs.setVisibility(View.VISIBLE);

                                    } else {
                                        profiletabs.setVisibility(View.GONE);
                                    }

                                    try {
                                        profile_title = result.getProfile().getDisplayname();
                                        profile_image = result.getProfile().getUserPhoto();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    setRecyclerViewProfileInfo();
                                    if (req != UPDATE_UPPER_LAYOUT) {
                                        setProfileTabbedMenus();
                                    }
                                    if (AppConfiguration.isStoryEnabled) {
                                        callHighlightAPI();
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }

                                mShimmerViewContainer.stopShimmerAnimation();
                                mShimmerViewContainer.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            hideAllLoader();
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideAllLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideAllLoader();
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
                opt = result.getCoverImageOption().get(itemId - 1);

            } else if (itemId > 150) {
                itemId = itemId - 150;
                opt = result.getProfileTabbedMenu().get(itemId - 1 + 3);
                itemId = itemId + 3;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getProfileImageOption().get(itemId - 1);

            } else {
                itemId = itemId - 10;
                opt = result.getGutterMenu().get(itemId - 1);
            }
            performClick(opt, isCover, itemId - 1, false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void performClick(Options opt, boolean isCover, int position, boolean isTabbed) {
        try {
            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    CustomLog.e("edit", "edit");
                    // goToFormFragment();
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;
                case Constant.OptionType.REMOVE:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_REMOVE, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.BLOCK:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_BLOCK, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.UNBLOCK:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_UNBLOCK, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.FOLLOW:
                case Constant.OptionType.UNFOLLOW:
                    callGutterApi(isTabbed, Constant.URL_FOLLOW_MEMBER, opt.getParams().getUserId(), position);
                    break;
                case "edit_profile_interests":
//                    fragmentManager.beginTransaction().replace(R.id.container, new ChooseInterestFragment())
//                            .addToBackStack(null).commit();
                    String url = BASE_URL + "sesinterest/index/interests/user_id/" + result.getLoggedInUserId();
                    openWebView(url, "Choose Interests");
                    break;
                case "add_profile_location":
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("id", result.getLoggedInUserId());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.ADD_LOCATION, map2, Constant.URL_PROFILE_ADD_LOCATION))
                            .addToBackStack(null)
                            .commit();
                case Constant.OptionType.ADD:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_ADD, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.CANCEL:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_REJECT, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.CONFIRM:
                    callGutterApi(isTabbed, Constant.URL_MEMBER_CONFIRM, opt.getParams().getUserId(), position);
                    break;
                case Constant.OptionType.remove_profile_photo:
                    showImageRemoveDialog(false, Constant.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION, Constant.URL_PROFILE_REMOVE_PHOTO);
                    break;
                case Constant.OptionType.view_profile_photo:
                    goToGalleryFragment(result.getProfile().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.CHOOSE_FROM_ALBUMS:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_USER_ID, userId);
                    map.put(Constant.KEY_ID, userId);
                    openSelectAlbumFragment(isCover ? Constant.URL_MEMBER_UPLOAD_COVER : Constant.URL_MEMBER_UPLOAD_PHOTO, map);
                    break;
                case Constant.OptionType.UPLOAD_PHOTO:
                case Constant.OptionType.EDIT_PROFILE_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_PROFILE_PHOTO, result.getProfile().getUserPhoto(), Constant.TITLE_EDIT_PROFILE_PHOTO);
                    break;
                case Constant.OptionType.upload_cover:
                    String coverPhoto = result.getCoverPhoto();
                    gToAlbumImage(Constant.URL_MEMBER_UPLOAD_COVER, TextUtils.isEmpty(coverPhoto) ? Constant.EMPTY : coverPhoto, Constant.TITLE_EDIT_COVER);
                    break;
                case Constant.OptionType.view_cover_photo:
                    goToGalleryFragment(result.getProfile().getCover(), resourceType, result.getCoverPhoto());
                    break;
                case Constant.OptionType.remove_cover_photo:
                    showImageRemoveDialog(true, Constant.MSG_COVER_DELETE_CONFIRMATION, Constant.URL_PROFILE_REMOVE_COVER);
                    break;
                case Constant.OptionType.SEND_MESSAGE:
                    List<Item_user> list = new ArrayList<>();
                    Item_user vo = new Item_user();
                    vo.setUser_id(result.getProfile().getUserId());
                    vo.setTitle(result.getProfile().getDisplayname());
                    vo.setUser_image(result.getProfile().getUserPhoto());
                    list.add(vo);
                    openComposeActivity(list);
                    break;

                case edit_profile_location:
                    String url2 = BASE_URL + "member/edit-location/id/" + result.getLoggedInUserId();
                    openWebView(url2, "Edit profile location");

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_USER_ID, userId);
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }

    private void goToReportFragment() {
        String guid = Constant.ResourceType.USER + "_" + result.getProfile().getUserId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }


    private void callGutterApi(final boolean isTabbed, final String url, int userId, final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);
                    request.params.put(Constant.KEY_GUTTER, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoader();
                            try {
                                String response = (String) msg.obj;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (url.contains("block")) {
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            Util.showSnackbar(v, res.getResult());
                                            // onBackPressed();

                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).toggleBlock();
                                                setProfileTabbedMenus();
                                            } else {
                                                result.getGutterMenu().get(position).toggleBlock();
                                            }
                                        } else if (url.equals(Constant.URL_MEMBER_REJECT)) {
                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).setLabel(Constant.ADD_FRIEND);
                                                result.getProfileTabbedMenu().get(position).setName(Constant.KEY_ADD);

                                                setProfileTabbedMenus();

                                            } else {
                                                result.getGutterMenu().get(position).setLabel(Constant.ADD_FRIEND);
                                                result.getGutterMenu().get(position).setName(Constant.KEY_ADD);

                                            }
                                        } else if (url.equals(Constant.URL_MEMBER_ADD)) {
                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).setLabel(getStrings(R.string.cancel_request));
                                                result.getProfileTabbedMenu().get(position).setName(Constant.KEY_CANCEL);
                                                setProfileTabbedMenus();
                                            } else {
                                                result.getGutterMenu().get(position).setLabel(getStrings(R.string.cancel_request));
                                                result.getGutterMenu().get(position).setName(Constant.KEY_CANCEL);
                                            }
                                        } else if (url.equals(Constant.URL_FOLLOW_MEMBER)) {
                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).toggleFollow();
                                                setProfileTabbedMenus();
                                            } else {
                                                result.getGutterMenu().get(position).toggleFollow();
                                              /*  Options opt = result.getProfileTabbedMenu().get(position).toggleFollow();
                                                result.getProfileTabbedMenu().get(position).setLabel(opt.getLabel());
                                                result.getProfileTabbedMenu().get(position).setName(opt.getName());*/
                                            }
                                        } else if (url.equals(Constant.URL_MEMBER_REMOVE)) {
                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).setLabel(Constant.ADD_FRIEND);
                                                result.getProfileTabbedMenu().get(position).setName(Constant.KEY_ADD);
                                                setProfileTabbedMenus();
                                            } else {
                                                result.getGutterMenu().get(position).setLabel(getStrings(R.string.remove_friend));
                                                result.getGutterMenu().get(position).setName(Constant.KEY_REMOVE);
                                            }
                                        } else if (url.equals(Constant.URL_MEMBER_CONFIRM)) {
                                            if (isTabbed) {
                                                result.getProfileTabbedMenu().get(position).setLabel(getStrings(R.string.remove_friend));
                                                result.getProfileTabbedMenu().get(position).setName(Constant.KEY_REMOVE);
                                                setProfileTabbedMenus();
                                            } else {
                                                result.getGutterMenu().get(position).setLabel(getStrings(R.string.approve_friend));
                                                result.getGutterMenu().get(position).setName(Constant.KEY_CONFIRM);
                                            }
                                        } else {
                                            Object obj = new JSONObject(response).get("result");
                                            if (!(obj instanceof String)) {
                                                // BaseResponse<JsonElement> res = new Gson().fromJson(response, BaseResponse.class);
                                                CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                                // result = resp.getResult();
                                                // Notifications member=new Gson().fromJson(res.getResult());
                                                if (null != resp.getResult().getMember()) {
                                                    String newLabel = resp.getResult().getMember().getMembership().getLabel();
                                                    String newName = resp.getResult().getMember().getMembership().getAction();
                                                    result.getGutterMenu().get(position).setLabel(newLabel);
                                                    result.getGutterMenu().get(position).setName(newName);
                                                }
                                            }
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {
                                hideAllLoader();

                                CustomLog.e(e);
                            }
                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideAllLoader();
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            hideAllLoader();
        }

    }

    private void hideAllLoader() {
        hideBaseLoader();
        hideView(v.findViewById(R.id.pbMain));
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
}
