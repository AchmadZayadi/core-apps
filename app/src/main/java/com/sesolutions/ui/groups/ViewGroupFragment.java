

package com.sesolutions.ui.groups;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.groups.GroupContent;
import com.sesolutions.responses.groups.GroupResponse;
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.PhotoViewFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageLikeDialogFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.page.PageServicesFragment;
import com.sesolutions.ui.page.PageVideoFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.poll.profile_poll.ProfilePollFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.profile.ProfileTabsAdapter;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class ViewGroupFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {

    private static final int REQ_UPDATE_UPPER = 99;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_CANCEL = 404;
    private final int REQ_DELETE_TOPIC = 405;

    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private GroupResponse.Result result;
    private int mGroupId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<GroupContent> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewGroupFragment newInstance(int groupId) {
        ViewGroupFragment frag = new ViewGroupFragment();
        frag.mGroupId = groupId;
        return frag;
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getGroup().getImages().setMain(activity.stringValue);
                        updateProfilePhoto(activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getGroup().getCoverImage().setMain(activity.stringValue);
                        updateCoverPhoto(activity.stringValue);
                    }
                    activity.stringValue = null;
                    activity.taskPerformed = 0;
                    break;
                case REQ_DELETE_TOPIC:
                case Constant.FormType.CREATE_FORUM_TOPIC:
                    int pos1 = getTabPositionByName(Constant.TabOption.FORUM);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos1, true);
                    (adapter.getItem(pos1)).onRefresh();
                    break;
                case Constant.FormType.CREATE_ALBUM_OTHERS:
                    activity.taskPerformed = 0;
                    int pos = getTabPositionByName(Constant.TabOption.ALBUM);
                    //collapse app bar layout
                    appBarLayout.setExpanded(false, true);
                    //set album tab and refresh data
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();

                    //open view album
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put(Constant.KEY_GROUP_ID, mGroupId);
                    map1.put(Constant.KEY_ALBUM_ID, activity.taskId);
                    map1.put(Constant.KEY_URI, Constant.URL_GROUP_ALBUM_VIEW);
                    map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP_ALBUM);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ViewPageAlbumFragment.newInstance(map1, null))
                            .addToBackStack(null).commit();
                    break;

                case Constant.FormType.CREATE_PAGE_VIDEO:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.FormType.CREATE_POLL:
                case Constant.TASK_ALBUM_DELETED:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.POLL);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    openViewPollFragment(MenuTab.Group.TYPE_PROFILE_POLL, activity.taskId);
                    break;
                case Constant.FormType.JOIN_GROUP:
                    activity.taskPerformed = 0;
                    updateItemOption(activity.stringValue);
                    break;

               /* case Constant.Task.ALBUM_DELETED:
                case Constant.Task.NOTE_DELETED:
                    activity.taskPerformed = 0;
                    swipeRefreshLayout.setEnabled(true);
                    onRefresh();
                    break;*/
            }


            if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                result.getGroup().getImages().setMain(activity.stringValue);
                updateProfilePhoto(activity.stringValue);
            } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                result.getGroup().getCoverImage().setMain(activity.stringValue);
                updateCoverPhoto(activity.stringValue);
            }

            if (Constant.TASK_POST) {
                Constant.TASK_POST = false;
                appBarLayout.setExpanded(false, true);
                viewPager.setCurrentItem(0, true);
                adapter.getItem(0).onRefresh();
             }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    public void updateItemOption(String response) {
        try {
            SuccessResponse res = new Gson().fromJson(response, SuccessResponse.class);
            Util.showSnackbar(v, res.getResult().getMessage());
            callMusicAlbumApi(REQ_UPDATE_UPPER);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateCoverPhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivCoverPhoto), url, context, R.drawable.placeholder_square);
    }

    private void updateProfilePhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivPageImage), url, context, R.drawable.placeholder_square);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_page, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

        callMusicAlbumApi(1);

        return v;
    }

    private PagePhotoAdapter adapterPhoto;

    private void initPhoto() {
        RecyclerView rvPhotos = v.findViewById(R.id.rvPhotos);
        photoList = new ArrayList<Albums>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterPhoto = new PagePhotoAdapter(photoList, context, this);
        rvPhotos.setAdapter(adapterPhoto);
    }

    private SuggestionGroupAdapter adapterRelated;

    private void initRelatedGroupUI() {
        MultiSnapRecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
        relatedList = new ArrayList<GroupContent>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterRelated = new SuggestionGroupAdapter(relatedList, context, this, true);
        rvPhotos.setAdapter(adapterRelated);
        // /pageIndicatorView.setCount(adapter.getItemCount());

    }


    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req == 1) {
                        showBaseLoader(true);
                    } else if (req == REQ_UPDATE_UPPER) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_GROUP);
                    request.params.put(Constant.KEY_GROUP_ID, mGroupId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoaders();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        GroupResponse commonResponse = new Gson().fromJson(response, GroupResponse.class);
                                        if (commonResponse.getResult() != null) {
                                            result = commonResponse.getResult();
                                        }

                                        if (req == REQ_UPDATE_UPPER) {
                                            setUpperUIData();
                                        } else {
                                            initUI();
                                        }

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                            } catch (Exception e) {
                                somethingWrongMsg(v);
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
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void hideAllLoaders() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            hideBaseLoader();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


   /* @Override
    public void onBackPressed() {

        activity.finish();
    }*/

    private void initUI() {
        try {
            v.findViewById(R.id.cl).setVisibility(View.VISIBLE);
            // getActivity().invalidateOptionsMenu();
            initCollapsingToolbar();
            initPhoto();
            initRelatedGroupUI();
            setUpperUIData();
            initTablayout();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    //listener for gutter menu item click

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void setUpperUIData() {

        mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        if (result.getGroup() != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            GroupContent resp = result.getGroup();
            ((TextView) v.findViewById(R.id.tvPageTitle)).setText(resp.getTitle());


            if (SPref.getInstance().isLoggedIn(context)) {
                v.findViewById(R.id.bContact).setVisibility(View.VISIBLE);
            }else {
                v.findViewById(R.id.bContact).setVisibility(View.GONE);
            }
            v.findViewById(R.id.bContact).setOnClickListener(this);
            if (null != resp.getCustom_url()) {
                v.findViewById(R.id.tvStats).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvStats)).setText("@" + resp.getCustom_url());
            } else {
                v.findViewById(R.id.tvStats).setVisibility(View.GONE);
            }
            if (resp.getUpdateCoverPhoto() != null) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera).setOnClickListener(this);
            }
            if (resp.getUpdateProfilePhoto() != null) {
                v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera2).setOnClickListener(this);
            }
            // ImageView ivCoverFoto = v.findViewById(R.id.ivCoverPhoto);
            // ImageView ivPageImage = v.findViewById(R.id.ivPageImage);

            updateCoverPhoto(resp.getCoverImageUrl());
            updateProfilePhoto(resp.getMainImageUrl());
            //Util.showImageWithGlide(ivCoverFoto, resp.getCoverImageUrl(), context, 1);

            // Util.showImageWithGlide(ivPageImage, resp.getMainImageUrl(), context, 1);

            v.findViewById(R.id.seeAllPhotos).setOnClickListener(this);
            if (result.getCallToAction() != null) {
                v.findViewById(R.id.bCallAction).setVisibility(View.VISIBLE);
                ((AppCompatButton) v.findViewById(R.id.bCallAction)).setText(result.getCallToAction().getLabel());
                v.findViewById(R.id.bCallAction).setOnClickListener(this);
            } else {
                v.findViewById(R.id.bCallAction).setVisibility(View.GONE);
            }
            addUpperTabItems();
            setAboutUI();

            updatePhotoAdapter();
            updateRelatedGroupAdapter();
        }
    }

    //set tab bar items
    private void initTablayout() {
    //    tabLayout = v.findViewById(R.id.tabs);
        if (result.getMenus() != null) {
            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[result.getMenus().size()];

            setupViewPager();
      //      tabLayout.clearOnTabSelectedListeners();
        //    tabLayout.setupWithViewPager(viewPager, true);
            RecyclerView profiletabs = v.findViewById(R.id.profiletabs);
            if(result.getMenus()!=null && result.getMenus().size()>0){
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                profiletabs.setLayoutManager(layoutManager);
                ProfileTabsAdapter adapter1    = new ProfileTabsAdapter(result.getMenus(), context, this);
                profiletabs.setAdapter(adapter1);
                profiletabs.setVisibility(View.VISIBLE);

            }else {
                profiletabs.setVisibility(View.GONE);
            }
            applyTabListener();
            new Handler().postDelayed(() -> loadScreenData(0), 200);
        } else {
          //  tabLayout.setVisibility(View.GONE);
        }
    }

    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            List<Options> list = result.getMenus();
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {
                    case Constant.TabOption.INFO:
                        Bundle bundle = new Bundle();
                        adapter.addFragment(GroupInfoFragment.newInstance(mGroupId), opt.getLabel());
                        break;

                    case "feed_link":
                        try {
                            for(int k=0;k<result.getOptions().size();k++){
                                try {
                                    if(result.getOptions().get(k).getUrl()!=null && result.getOptions().get(k).getName().equalsIgnoreCase("feed_link")){
                                        if(result.getOptions().get(k).getUrl()!=null && result.getOptions().get(k).getUrl().length()>0 &&
                                                !result.getOptions().get(k).getUrl().equalsIgnoreCase("null")){
                                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
                                                    .getSystemService(Context.CLIPBOARD_SERVICE);
                                            android.content.ClipData clip = android.content.ClipData
                                                    .newPlainText("message", "" +  result.getOptions().get(k).getUrl());
                                            clipboard.setPrimaryClip(clip);
                                        }
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                            Util.showSnackbar(v, getString(R.string.copy_clipboard));
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        break;

                    case Constant.TabOption.POSTS:
                        adapter.addFragment(FeedFragment.newInstance(mGroupId, Constant.ResourceType.GROUP), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        map.put(Constant.KEY_URI, Constant.URL_GROUP_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.MAP:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MAP);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getGroup().getDescription());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;
                    case Constant.TabOption.POLL:
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        adapter.addFragment(ProfilePollFragment.newInstance(MenuTab.Group.TYPE_PROFILE_POLL, this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.ANNOUNCE:
                        bundle = new Bundle();
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_ANNOUNCE);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case Constant.TabOption.SERVICES:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_SERVICES);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                        break;



                    case Constant.TabOption.VIDEO:
                        adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.GROUP, mGroupId, this), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(GroupMemberFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case Constant.TabOption.FORUM:
                        adapter.addFragment(GroupTopicsFragment.Companion.newInstance(mGroupId), opt.getLabel());
                        break;
                    case "associateGroups":
                        adapter.addFragment(GroupFragment.newInstance(GroupFragment.TYPE_ASSOCIATE, mGroupId), opt.getLabel());
                        break;
                    case "claim":
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_GROUP_CLAIM), opt.getLabel());
                        break;
                    default:
                        CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Lable: " + opt.getLabel());

                        break;

                }
            }

            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(isLoaded.length);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyTabListener() {
       // tabLayout.addOnTabSelectedListener(this);
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
                    /*if (tab.getPosition() == 0) {
                        ((VideoHelper) adapter.getItem(tab.getPosition())).scrollToStart();
                    }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void loadScreenData(int position) {
        // do not load tab if already loaded
        if (!isLoaded[position] && isNetworkAvailable(context)) {
            isLoaded[position] = true;
            adapter.getItem(position).initScreenData();
        }
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
        appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    //  swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getGroup().getTitle());
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

    private void addUpperTabItems() {
        if (!SPref.getInstance().isLoggedIn(context)) return;

        //add post item
        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        //add favourite item
        if (result.getGroup().canLike()) {

            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(result.getGroup().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getGroup().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getGroup().hasToChangeFollowLike()) {
                    callLikeApi(REQ_FOLLOW, view2, Constant.URL_GROUP_FOLLOW, false);
                }
                callLikeApi(REQ_LIKE, view1, Constant.URL_GROUP_LIKE, true);
            });
            llTabOptions.addView(view1);
        }

        //add favourite item
        if (result.getGroup().canFavourite()) {
            final View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(result.getGroup().isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view3, Constant.URL_GROUP_FAVORITE, true));
            llTabOptions.addView(view3);
        }

        //add Follow item
        if (mUserId != result.getGroup().getOwner_id() && result.getGroup().canFollow()) { //don't show follow button to Group Owner

            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(result.getGroup().isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, result.getGroup().isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).clearColorFilter();
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getGroup().hasToChangeFollowLike()) {
                    callLikeApi(REQ_LIKE, view1, Constant.URL_GROUP_LIKE, false);
                }
                callLikeApi(REQ_FOLLOW, view2, Constant.URL_GROUP_FOLLOW, true);
            });
            llTabOptions.addView(view2);
        }

        //add more item
        if (null != result.getGroup().getButtons()) {
            final View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(R.string.TEXT_MORE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.more_profile));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
            view.setOnClickListener(v -> showPopup(result.getGroup().getButtons(), view, 2000));
            llTabOptions.addView(view);
        }
    }


    private void updatePhotoAdapter() {
        try {
            photoList.clear();
            if (result.getPhoto() != null && result.getPhoto().size() > 0) {
                v.findViewById(R.id.rlPhotos).setVisibility(View.VISIBLE);
                photoList.addAll(result.getPhoto());
                adapterPhoto.notifyDataSetChanged();
            } else {
                v.findViewById(R.id.rlPhotos).setVisibility(View.GONE);
            }
            adapterPhoto.notifyDataSetChanged();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateRelatedGroupAdapter() {
        try {
            relatedList.clear();
            if (result.getRelatedGroups() != null && result.getRelatedGroups().size() > 0) {
                v.findViewById(R.id.rlRecent).setVisibility(View.VISIBLE);
                relatedList.addAll(result.getRelatedGroups());
            } else {
                v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
            }
            adapterRelated.notifyDataSetChanged();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setAboutUI() {
        LinearLayoutCompat llAbout = v.findViewById(R.id.llAbout);
        llAbout.removeAllViews();
        if (result.getAbout() != null) {
            llAbout.setVisibility(View.VISIBLE);
            //add about layout items
            for (final Options opt : result.getAbout()) {
                switch (opt.getName()) {
                    case Constant.OptionType.SEE_ALL:
                        View view = getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llAbout, false);
                        view.setOnClickListener(v -> performAboutOptionClick(opt));
                        llAbout.addView(view);
                        break;
                    case Constant.OptionType.CREATE_DATE:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(Util.changeDate(opt.getValue()));
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_post));
                        llAbout.addView(view);
                        break;
                    case Constant.OptionType.TAG:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(SpanUtil.getHashTags(result.getGroup().getTag(), this));
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                        llAbout.addView(view);
                        break;
                    default:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(opt.getValue());
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                        view.setOnClickListener(v -> performAboutOptionClick(opt));
                        llAbout.addView(view);
                        break;
                }
            }
        } else {
            llAbout.setVisibility(View.GONE);
        }
    }


    private void performAboutOptionClick(Options opt) {
        switch (opt.getName()) {
            case Constant.OptionType.CATEGORY:
                openViewCategory();
                break;
            case Constant.OptionType.WEBSITE:
                openWebView(opt.getValue(), opt.getValue());
                break;
            case Constant.OptionType.PHONE:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + opt.getValue()));
                startActivity(intent);
                break;
            case Constant.OptionType.MAIL:
                ShareCompat.IntentBuilder.from(activity)
                        .setType("message/rfc822")
                        .addEmailTo(opt.getValue())
                        .setSubject("")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle(opt.getLabel())
                        .startChooser();
                break;
            case Constant.OptionType.TAG:
                break;
            case Constant.OptionType.SEE_ALL:
              /*  appBarLayout.setExpanded(false, true);
                viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.INFO));*/

                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_GRIUP_INFO);
                intent2.putExtra(Constant.KEY_ID, mGroupId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

        }
    }

    //open view category page
    private void openViewCategory() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(result.getGroup().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getOptions(), vItem, 10);
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);

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


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        try {
            // Not showing the option menu if the share is null.
            if (null != result && null != result.getGroup() && result.getGroup().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getGroup().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getOptions() != null) {
                menu.add(Menu.NONE, R.id.option, Menu.FIRST, "options")
                        .setIcon(R.drawable.vertical_dots)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            Options opt;
            boolean isCover = false;
            int itemId = item.getItemId();
            if (itemId > 2000) {
                itemId = itemId - 2000;
                opt = result.getGroup().getButtons().get(itemId - 1);
            } else if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getGroup().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getGroup().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditGroupFragment.newInstance(Constant.FormType.EDIT_GROUP, map, Constant.URL_GROUP_EDIT, null)).addToBackStack(null).commit();
                    break;
                case "feed_link":
                    try {
                        for(int k=0;k<result.getOptions().size();k++){
                            try {
                                if(result.getOptions().get(k).getUrl()!=null && result.getOptions().get(k).getName().equalsIgnoreCase("feed_link")){
                                    if(result.getOptions().get(k).getUrl()!=null && result.getOptions().get(k).getUrl().length()>0 &&
                                            !result.getOptions().get(k).getUrl().equalsIgnoreCase("null")){
                                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
                                                .getSystemService(Context.CLIPBOARD_SERVICE);
                                        android.content.ClipData clip = android.content.ClipData
                                                .newPlainText("message", "" +  result.getOptions().get(k).getUrl());
                                        clipboard.setPrimaryClip(clip);
                                    }
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        Util.showSnackbar(v, getString(R.string.copy_clipboard));
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    break;

                case Constant.OptionType.DASHBOARD:
                    openWebView(opt.getValue(), opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getGroup().getShare());
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ResourceType.GROUP + "_" + mGroupId);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    //openInviteForm(map, Constant.URL_GROUP_INVITE);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_GROUP_INVITE).show(fragmentManager, "social");
                    break;

                case "likeasyourgroup":
                    // PageLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mGroupId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
                        new ApiController(Constant.URL_LIKE_AS_GROUP, map, context, this, Constant.Events.LIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case "unlikeasyourgroup":
                    // PageLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mGroupId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
                        new ApiController(Constant.URL_UNLIKE_AS_GROUP, map, context, this, Constant.Events.UNLIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case "createAssociateGroup":
                    fetchFormData();
                    break;

                case Constant.OptionType.JOIN_SMOOTHBOX:
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                case Constant.OptionType.CANCEL:
                    showJoinLeaveDialog(opt);
                    break;

                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getGroup().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    map.put(Constant.KEY_ID, mGroupId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_GROUP_COVER : Constant.URL_UPLOAD_GROUP_PHOTO, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    map.put(Constant.KEY_IMAGE, "Filedata");

                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage2(Constant.URL_UPLOAD_GROUP_COVER, result.getGroup().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage2(Constant.URL_UPLOAD_GROUP_PHOTO, result.getGroup().getMainImageUrl(), opt.getLabel(), map);
                    }

                    break;

                case Constant.OptionType.view_cover_photo:
                case "view":
                    // goToGalleryFragment(result.getPage().getPage_id(), resourceType, result.getPage().getCoverImageUrl());
                    goToPhotoView(result.getGroup().getCoverImageUrl());
                    break;


                case "contact":
                    super.openGroupContactForm(result.getGroup().getOwner_id());
                    // onClick(v.findViewById(R.id.bContact));
                    break;

                case Constant.OptionType.remove_photo:
                    showImageRemoveDialog(isCover);
                    break;
                default:
                    performAboutOptionClick(opt);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    private void goToPhotoView(String main) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PhotoViewFragment.newInstance(main))
                .addToBackStack(null)
                .commit();
    }


    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_REMOVE_GROUP_COVER : Constant.URL_REMOVE_GROUP_PHOTO;
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(isCover ? R.string.MSG_COVER_DELETE_CONFIRMATION : R.string.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? R.string.TXT_REMOVE_COVER : R.string.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
                    new ApiController(url, map, context, ViewGroupFragment.this, Constant.Events.REMOVE_PHOTO).execute();
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

    private void showJoinLeaveDialog(Options opt) {

        //in case of public user ,send him to sign-in screen
        if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1);
            return;
        }

        String dialogMsg = Constant.EMPTY;
        String buttonTxt = Constant.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        switch (opt.getName()) {
            case Constant.OptionType.JOIN_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_join_group);
                buttonTxt = getStrings(R.string.join_group);
                url[0] = Constant.URL_GROUP_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_group);
                buttonTxt = getStrings(R.string.leave_group);
                url[0] = Constant.URL_GROUP_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_GROUP_JOIN;
                req[0] = REQ_REQUEST;
                break;
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_group);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_GROUP_CANCEL_MEMBER;
                req[0] = REQ_CANCEL;
                break;
        }

        /*if (Constant.OptionType.LEAVE_SMOOTHBOX.equals(opt.getName())
                || Constant.OptionType.JOIN_SMOOTHBOX.equals(opt.getName())
                || Constant.OptionType.CANCEL.equals(opt.getName())
                ) {*/

        if (!Constant.OptionType.REQUEST.equals(opt.getName())) {
            showLeaveDialog(dialogMsg, buttonTxt, url[0], req[0], -1);
        } else {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put(Constant.KEY_ID, mGroupId);
            map1.put(Constant.KEY_GROUP_ID, mGroupId);
            map1.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
            openGroupJoinForm(null, map1, url[0], buttonTxt, dialogMsg);
        }
    }


    private void showLeaveDialog(String dialogMsg, String buttonTxt, String url, int REQ, int position) {
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
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(dialogMsg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(buttonTxt);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ, url);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.seeAllPhotos:
                   /* appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.ALBUM));*/

                    Bundle bundle = new Bundle();
                    HashMap<String, Object> map = new HashMap<>();

                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    map.put(Constant.KEY_URI, Constant.URL_GROUP_ALBUM);
                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP_ALBUM);
                    bundle.putSerializable(Constant.POST_REQUEST, map);

                    Intent intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ALBUM);
                    intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);

                    break;

                case R.id.bCallAction:
                    openWebView(result.getCallToAction().getValue(), " ");
                    break;

                case R.id.bContact:
                    super.openGroupContactForm(result.getGroup().getOwner_id());
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getGroup().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    if (null != result.getGroup().getUpdateCoverPhoto())
                        showPopup(result.getGroup().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
               /* case R.id.like_heart:
                    callReactionApi(AppConstantSes.URL_LIKE + mGroupId, view);
                    resp.toggleLike();
                    ((ImageView) v.findViewById(R.id.ivLike)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentLike() ? R.drawable.gallery_like_active : R.drawable.gallery_like));

                    if (resp.isContentLike()) {
                        // view.setSelected(true);
                        ((SmallBangView) view).likeAnimation();
                    }

                    break;
                case R.id.favorite_heart:
                    callReactionApi(AppConstantSes.URL_FAVORITE, v);
                    resp.toggleFav();
                    ((ImageView) view.findViewById(R.id.ivFavorite)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFavourite() ? R.drawable.gallery_fav_selected : R.drawable.gallery_fav_unselected));
                    if (resp.isContentFavourite()) {
                        ((SmallBangView) view).likeAnimation();
                    }
                    break;
                case R.id.follow_heart:
                    callReactionApi(AppConstantSes.URL_FOLLOW, v);
                    resp.toggleFollow();
                    ((ImageView) view.findViewById(R.id.ivFollow)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFollow() ? R.drawable.gallery_follow_active : R.drawable.gallery_follow));
                    if (resp.isContentFollow()) {
                        ((SmallBangView) view).likeAnimation();
                    }
                    break;
                case R.id.appreciate_heart:
                    callReactionApi(AppConstantSes.URL_APPRECIATE + mGroupId, v);
                    resp.toggleFollow();
                    ((ImageView) view.findViewById(R.id.ivAppreciate)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFollow() ? R.drawable.gallery_appreciate : R.drawable.gallery_appreciate));
                    // if (resp.isContentFollow()) {
                    ((SmallBangView) view).likeAnimation();
                    //  }
                    break;*/

               /* case R.id.tvOwnerTitle:
                    int userId = resp.getOwnerId();

                    break;
                case R.id.ivCoverPhoto:
                    isCoverRequest = true;
                    if (null != resp.getCoverImageOptions())
                        showPopup(resp.getCoverImageOptions(), tvCoverOption, 1000);
                    break;

                case R.id.ivProfileImage:
                    isCoverRequest = false;
                    if (null != resp.getProfileImageOptions())
                        showPopup(resp.getProfileImageOptions(), tvProfileOption, 100);
                    // mGutterMenuUtils.showPopup(tvCoverOption, resp.getProfileOptionAsArray(), mBrowseList, ConstantVariables.USER_MENU_TITLE);
                    break;
    */
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private int getTabPositionByName(String name) {
        int position = 0;
        for (int i = 0; i < result.getMenus().size(); i++) {
            if (result.getMenus().get(i).getName().equals(name)) {
                position = i;
                break;
            }
        }
        return position;
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
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_GROUP));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_DELETE_GROUP);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, String url) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_GROUP_ID, mGroupId);
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
                                        if (REQ == REQ_DELETE) {
                                            CommonResponse res = new Gson().fromJson(response, CommonResponse.class);
                                            Util.showSnackbar(v, res.getResult().getSuccessMessage());
                                            activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                            onBackPressed();
                                        } else if (REQ > REQ_DELETE) {
                                            updateItemOption(response);

                                        }
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
    private void callFragment(Object value) {
        Options opt= (Options) value;
        Intent intent2=null;
        Bundle bundle = new Bundle();
        HashMap<String, Object> map = new HashMap<>();
        switch (opt.getName()){
            case Constant.TabOption.INFO:
                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_GRIUP_INFO);
                intent2.putExtra(Constant.KEY_ID, mGroupId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());
                break;
            case Constant.TabOption.ALBUM:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                map.put(Constant.KEY_URI, Constant.URL_GROUP_ALBUM);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP_ALBUM);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ALBUM);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.REVIEW:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();

                map.put(Constant.KEY_GROUP_ID, mGroupId);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP_REVIEW);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_REVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.MAP:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MAP);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MAP);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //   adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                break;
            case Constant.TabOption.OVERVIEW:
                bundle = new Bundle();
                map = new HashMap<>();
           //     map.put(Constant.TEXT, result.getPage().getDescription());
                map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));

                bundle.putSerializable(Constant.POST_REQUEST, map);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_OVERVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;
            case Constant.TabOption.POLL:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_GROUP_POLL);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.ANNOUNCE:
                bundle = new Bundle();

                //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_ANNOUNCE);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ANNOUNCE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.SERVICES:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_SERVICES);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);// mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_SERVICE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                break;

            case Constant.TabOption.MEMBERS:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                // adapter.addFragment(GroupMemberFragment.newInstance(bundle), opt.getLabel());
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.VIDEO:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_GROUP_ViIDEO);
                intent2.putExtra(Constant.KEY_ID, mGroupId);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PAGE, mPageId, this), opt.getLabel());
                break;

            case Constant.TabOption.ASSOCIATE:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ASSOCIATE);
                intent2.putExtra(Constant.KEY_ID, mGroupId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
               break;

            case Constant.TabOption.FORUM:

                fragmentManager.beginTransaction()
                        .replace(R.id.container, GroupTopicsFragment.Companion.newInstance(mGroupId,true))
                        .addToBackStack(null)
                        .commit();

               // adapter.addFragment(GroupTopicsFragment.Companion.newInstance(mGroupId), opt.getLabel());
                break;
            case "associateGroups":

                fragmentManager.beginTransaction()
                        .replace(R.id.container, GroupFragment.newInstance(GroupFragment.TYPE_ASSOCIATE, mGroupId,true))
                        .addToBackStack(null)
                        .commit();

                break;
            case "claim":
                map = new HashMap<>();
                map.put(Constant.KEY_GROUP_ID, mGroupId);
                adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_GROUP_CLAIM), opt.getLabel());
                break;
            default:
                CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Lable: " + opt.getLabel());

                break;


     /*

            case "claim":
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
                adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_PAGE_CLAIM), opt.getLabel());
                break;



            default:
                CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Lable: " + opt.getLabel());
                        *//*bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());*//*
                break;*/
        }
    }



    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        try {
            CustomLog.e("POPUP", "" + object2 + "  " + object2 + "  " + position);
            switch (object1) {
                case Constant.Events.TAB_OPTION_PROFILE:
                    // handleTabOptionClicked("" + value, postion);
                    callFragment(object2);

                    break;
                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    openViewGroupFragment(position);
                    break;
                case Constant.Events.IMAGE_1:
                    openLighbox(photoList.get(position).getPhotoId(), photoList.get(position).getImages().getNormal(), photoList.get(position).getAlbumId());
                    break;
                case Constant.Events.LIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsGroupResponse(object2, Constant.URL_LIKE_AS_GROUP);
                    break;

                case Constant.Events.UNLIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsGroupResponse(object2, Constant.URL_UNLIKE_AS_GROUP);
                    break;

                case Constant.Events.REMOVE_PHOTO:
                    hideBaseLoader();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }


    private void handleLikeAsGroupResponse(Object response, String url) {
        try {
            if (null != response) {
                ErrorResponse err = new Gson().fromJson((String) response, ErrorResponse.class);
                if (TextUtils.isEmpty(err.getErrorMessage())) {
                    JSONObject resp = new JSONObject((String) response);
                    PageLike res = new Gson().fromJson(resp.optJSONObject("result").toString(), PageLike.class);
                    if (res != null) {
                        res.setId(mGroupId);
                        PageLikeDialogFragment.newInstance(res, url, this, Constant.ResourceType.GROUP).show(fragmentManager, Constant.TITLE_PRIVACY);
                    } else {
                        somethingWrongMsg(v);
                    }
                } else {
                    Util.showSnackbar(v, err.getErrorMessage());
                }
            } else {
                somethingWrongMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void callLikeApi(final int REQ_CODE, final View view, String url, boolean showAnimation) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, result.getGroup(), showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mGroupId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
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

                                        if (REQ_CODE > REQ_DELETE) {
                                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                            List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                            result.getGroup().setButtons(opt);

                                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                        }
                                        /*if (REQ_CODE == REQ_LIKE) {
                                            videoList.get(position).setContentLike(!vo.isContentLike());
                                        } else if (REQ_CODE == REQ_FAVORITE) {
                                            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                        }
                                        adapter.notifyItemChanged(position);*/
                                    } else {
                                        //revert changes in case of error
                                        updateItemLikeFavorite(REQ_CODE, view, result.getGroup(), false);
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
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, View view, GroupContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getGroup().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, vo.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getGroup().isContentFollow() ? R.string.unfollow : R.string.follow);
        }

    }


    //TODO same method is on GroupParent make it common
    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GROUP_CREATE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(Constant.KEY_PARENT_ID, mGroupId);
                                        openSelectCategory(resp.getResult().getCategory(), map, Constant.ResourceType.GROUP);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put(Constant.KEY_PARENT_ID, mGroupId);
                                            openGroupCreateForm(vo.getResult(), map);
                                        }
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }

                            } else {
                                somethingWrongMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openLighbox(int photoId, String imageUrl, int albumId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_GROUP_ID, mGroupId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

}
