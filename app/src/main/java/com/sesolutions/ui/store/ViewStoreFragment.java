package com.sesolutions.ui.store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.StoreResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.courses.course.CourseFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.groups.ClaimFormFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageFragment;
import com.sesolutions.ui.page.PageLikeDialogFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.page.PageServicesFragment;
import com.sesolutions.ui.page.PageVideoFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.poll.profile_poll.ProfilePollFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.profile.ProfileStoreAdapter;
import com.sesolutions.ui.profile.ProfileTabsAdapter;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.store.product.ProductFragment;
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

public class ViewStoreFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {

    private static final int REQ_UPDATE_UPPER = 99;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private StoreResponse.Result result;
    private int mStoreId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<StoreContent> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewStoreFragment newInstance(int groupId) {
        ViewStoreFragment frag = new ViewStoreFragment();
        frag.mStoreId = groupId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getStore().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getStore().getCoverImage().setMain(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }
                    activity.taskPerformed = 0;
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
                    map1.put(Constant.KEY_STORE_ID, mStoreId);
                    map1.put(Constant.KEY_ALBUM_ID, activity.taskId);
                    map1.put(Constant.KEY_URI, Constant.URL_BUSINESS_ALBUM_VIEW);
                    map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS_ALBUM);
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

                case Constant.FormType.EDIT_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;
                case Constant.FormType.CREATE_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    goToViewReviewFragment(Constant.ResourceType.STORE_REVIEW, activity.taskId);
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

               /* case Constant.Task.ALBUM_DELETED:
                case Constant.Task.NOTE_DELETED:
                    activity.taskPerformed = 0;
                    swipeRefreshLayout.setEnabled(true);
                    onRefresh();
                    break;*/
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
        v = inflater.inflate(R.layout.fragment_view_store, container, false);
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

    //ToDo
    private SuggestionStoreAdapter adapterRelated;

    private void initRelatedBusinessUI() {
        MultiSnapRecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
        relatedList = new ArrayList<StoreContent>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterRelated = new SuggestionStoreAdapter(relatedList, context, this, true);
        rvPhotos.setAdapter(adapterRelated);
//        pageIndicatorView.setCount(adapter.getItemCount());

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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_STORE);
                    request.params.put(Constant.KEY_STORE_ID, mStoreId);
//                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.BUSINESS);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoaders();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("view_store_response", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        StoreResponse commonResponse = new Gson().fromJson(response, StoreResponse.class);
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
            initRelatedBusinessUI();
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
        if (result.getStore() != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            StoreContent resp = result.getStore();
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
            // ToDo check calloption working
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
            updateRelatedBusinessAdapter();
        }
    }

    //set tab bar items
    private void initTablayout() {
      //  tabLayout = v.findViewById(R.id.tabs);
        if (result.getMenus() != null) {
            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[result.getMenus().size()];

            setupViewPager();
           // tabLayout.clearOnTabSelectedListeners();
           // tabLayout.setupWithViewPager(viewPager, true);
           // applyTabListener();
            RecyclerView profiletabs = v.findViewById(R.id.profiletabs);
            if(result.getMenus()!=null && result.getMenus().size()>0){
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                profiletabs.setLayoutManager(layoutManager);
                ProfileStoreAdapter adapter1    = new ProfileStoreAdapter(result.getMenus(), context, this);
                profiletabs.setAdapter(adapter1);
                profiletabs.setVisibility(View.VISIBLE);
            }else {
                profiletabs.setVisibility(View.GONE);
            }

            new Handler().postDelayed(() -> loadScreenData(0), 200);
        } else {
          //  tabLayout.setVisibility(View.GONE);
        }
    }

    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        //    adapter.showTab(true);
            List<Options> list = result.getMenus();
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {


                    case Constant.TabOption.POSTS:
                       /* Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER + resp.getGroupId() + Constant.POST_URL);
                        bundle.putInt(Constant.KEY_RESOURCE_ID,resp.getGroupId());
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);*/
                        adapter.addFragment(FeedFragment.newInstance(mStoreId, Constant.ResourceType.STORE), opt.getLabel());
                        break;


                    case Constant.TabOption.INFO:
                        Bundle bundle = new Bundle();
                        adapter.addFragment(StoreInfoFragment.newInstance(mStoreId), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        map.put(Constant.KEY_URI, Constant.URL_STORE_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;


                    case Constant.TabOption.PRODUCTS:
                        adapter.addFragment(ProductFragment.newInstance(MenuTab.Store.STORE_PRODUCT, this, mStoreId), "Products");
                        break;

                    case Constant.TabOption.MAP:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_STORE_MAP);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getStore().getDescription());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;

                    case Constant.TabOption.POLL:
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        adapter.addFragment(ProfilePollFragment.newInstance(MenuTab.Business.TYPE_PROFILE_POLL, this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.ANNOUNCE:
                        bundle = new Bundle();
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_STORE_ANNOUNCE);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_STORE_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(StoreMemberFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.REVIEW:
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE_REVIEW);
                        adapter.addFragment(PageProfileReviewFragment.newInstance(opt.getName(), this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.SERVICES:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_STORE_SERVICES);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);// mPageId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                        adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                        break;

                    // ToDo
//                    case "associatebusinesses":
//                        adapter.addFragment(BusinessFragment.newInstance(BusinessFragment.TYPE_ASSOCIATE, mStoreId), opt.getLabel());
//                        break;
                    case "claim":
                        map = new HashMap<>();
                        map.put(Constant.KEY_STORE_ID, mStoreId);
                        adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_STORE_CLAIM), opt.getLabel());
                        break;
                    case Constant.TabOption.VIDEO:
                        adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.STORE, mStoreId, this), opt.getLabel());
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

      //  tabLayout.addOnTabSelectedListener(this);
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
                        collapsingToolbar.setTitle(result.getStore().getTitle());
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
        if (result.getStore().canLike()) {

            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(result.getStore().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getStore().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getStore().hasToChangeFollowLike()) {
                    callLikeApi(REQ_FOLLOW, view2, Constant.URL_STORE_FOLLOW, false);
                }
                callLikeApi(REQ_LIKE, view1, Constant.URL_STORE_LIKE, true);
            });
            llTabOptions.addView(view1);
        }

        //add favourite item
        if (result.getStore().canFavourite()) {
            final View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(result.getStore().isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view3, Constant.URL_STORE_FAVORITE, true));
            llTabOptions.addView(view3);
        }

        //add Follow item
        if (mUserId != result.getStore().getOwner_id() && result.getStore().canFollow()) { //don't show follow button to Business Owner

            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(result.getStore().isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, result.getStore().isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).clearColorFilter();
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getStore().hasToChangeFollowLike()) {
                    callLikeApi(REQ_LIKE, view1, Constant.URL_STORE_LIKE, false);
                }
                callLikeApi(REQ_FOLLOW, view2, Constant.URL_STORE_FOLLOW, true);
            });
            llTabOptions.addView(view2);
        }

        //add more item
        if (null != result.getStore().getMenus()) {
            final View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(R.string.TEXT_MORE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.more_profile));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
            view.setOnClickListener(v -> showPopup(result.getStore().getMenus(), view, 2000));
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

    private void updateRelatedBusinessAdapter() {
        try {
            relatedList.clear();
            if (result.getRelatedStores() != null && result.getRelatedStores().size() > 0) {
                v.findViewById(R.id.rlRecent).setVisibility(View.VISIBLE);
                relatedList.addAll(result.getRelatedStores());
            } else {
                v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
            }
            // ToDO
//            adapterRelated.notifyDataSetChanged();
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
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(SpanUtil.getHashTags(result.getStore().getTag(), this));
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
                appBarLayout.setExpanded(false, true);

                viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.INFO));
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
                    showShareDialog(result.getStore().getShare());
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
            if (null != result && null != result.getStore() && result.getStore().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getStore().getShare().getLabel())
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
                opt = result.getStore().getMenus().get(itemId - 1);
            } else if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getStore().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getStore().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_STORE_ID, mStoreId);
                    // ToDo
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditStoreFragment.newInstance(Constant.FormType.EDIT_CLASSROOM, map, Constant.URL_STORE_EDIT, null)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.DASHBOARD:
                    openWebView(opt.getValue(), opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getStore().getShare());
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ResourceType.STORE + "_" + mStoreId);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_STORE_ID, mStoreId);
                    //openInviteForm(map, Constant.URL_BUSINESS_INVITE);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_STORE_INVITE).show(fragmentManager, "social");
                    break;


                case "likeasyourstore":
                    // BusinessLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mStoreId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.BUSINESS);
                        new ApiController(Constant.URL_LIKE_AS_BUSINESS, map, context, this, Constant.Events.LIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case "unlikeasyourbusiness":
                    // BusinessLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mStoreId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.BUSINESS);
                        new ApiController(Constant.URL_UNLIKE_AS_BUSINESS, map, context, this, Constant.Events.UNLIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case "createAssociateBusiness":
                    fetchFormData();
                    break;

                case Constant.OptionType.JOIN_SMOOTHBOX:
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                    showJoinLeaveDialog(opt);
                    break;

                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getStore().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_STORE_ID, mStoreId);
                    map.put(Constant.KEY_ID, mStoreId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.STORE);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_STORE_COVER : Constant.URL_UPLOAD_STORE_PHOTO, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_STORE_ID, mStoreId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_STORE_COVER, result.getStore().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_STORE_PHOTO, result.getStore().getMainImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getStore().getCoverImageUrl(), resourceType, result.getStore().getCoverImageUrl());
                    break;

                case "contact":
                    onClick(v.findViewById(R.id.bContact));
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

    //TODO same method is on ProfileFragment
    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_REMOVE_STORE_COVER : Constant.URL_REMOVE_STORE_PHOTO;
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
                    map.put(Constant.KEY_STORE_ID, mStoreId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.STORE);
                    new ApiController(url, map, context, ViewStoreFragment.this, Constant.Events.REMOVE_PHOTO).execute();
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
                dialogMsg = getStrings(R.string.msg_join_stores);
                buttonTxt = getStrings(R.string.join_store);
                url[0] = Constant.URL_STORE_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_stores);
                buttonTxt = getStrings(R.string.leave_business);
                url[0] = Constant.URL_STORE_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership_store);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_STORE_REQUEST;
                req[0] = REQ_REQUEST;
                break;
        }

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
                callDeleteApi(req[0], url[0]);

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
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.ALBUM));
                    break;

                case R.id.bCallAction:
                    openWebView(result.getCallToAction().getValue(), " ");
                    break;

                case R.id.bContact:
                    super.openStoreContactForm(result.getStore().getOwner_id());
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getStore().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    if (null != result.getStore().getUpdateCoverPhoto())
                        showPopup(result.getStore().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_STORE));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(REQ_DELETE, Constant.URL_DELETE_STORE);
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

    private void callDeleteApi(final int REQ, String url) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_STORE_ID, mStoreId);
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
                                            SuccessResponse res = new Gson().fromJson(response, SuccessResponse.class);
                                            Util.showSnackbar(v, res.getResult().getMessage());
                                            callMusicAlbumApi(REQ_UPDATE_UPPER);
                                        }
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
                    StoreUtil.openViewStoreFragment(fragmentManager, position);
//                    openViewBusinessFragment(position);
                    break;
                case Constant.Events.IMAGE_1:
                    openLighbox(photoList.get(position).getPhotoId(), photoList.get(position).getImages().getNormal(), photoList.get(position).getAlbumId());
                    break;
                case Constant.Events.LIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsBusinessResponse(object2, Constant.URL_LIKE_AS_STORE);
                    break;

                case Constant.Events.UNLIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsBusinessResponse(object2, Constant.URL_UNLIKE_AS_STORE);
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




    private void callFragment(Object value) {
        Options opt= (Options) value;
        Intent intent2=null;
        Bundle bundle = new Bundle();
        HashMap<String, Object> map = new HashMap<>();
        switch (opt.getName()){
            case Constant.TabOption.INFO:
                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_STORE_INFO);
                intent2.putExtra(Constant.KEY_ID, mStoreId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

            //    adapter.addFragment(StoreInfoFragment.newInstance(mStoreId), opt.getLabel());
                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;
            case Constant.TabOption.PRODUCTS:
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_STORE_PRODUCT);
                intent2.putExtra(Constant.KEY_ID, mStoreId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;
         /*   case Constant.TabOption.COMMENT:
             //   adapter.addFragment(CourseFragment.newInstance(CourseFragment.TYPE_CLASSROOM_COURSE, this, mclassroomId), "Courses");
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_STORE_COMMENT);
                intent2.putExtra(Constant.KEY_ID, mStoreId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;*/


            case Constant.TabOption.ALBUM:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map.put(Constant.KEY_STORE_ID, mStoreId);
                map.put(Constant.KEY_URI, Constant.URL_STORE_ALBUM);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
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
               map = new HashMap<>();
                map.put(Constant.KEY_STORE_ID, mStoreId);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE_REVIEW);

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
                bundle.putString(Constant.KEY_URI, Constant.URL_STORE_MAP);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);
                map = new HashMap<>();
                map.put(Constant.KEY_STORE_ID, mStoreId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MAP);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //   adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                break;
            case Constant.TabOption.OVERVIEW:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.TEXT, result.getStore().getDescription());
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
                map.put(Constant.KEY_STORE_ID, mStoreId);

                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_POLL);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.ANNOUNCE:
                bundle = new Bundle();
                map = new HashMap<>();
                //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                map.put(Constant.KEY_STORE_ID, mStoreId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_URI, Constant.URL_STORE_ANNOUNCE);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ANNOUNCE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.SERVICES:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_STORE_SERVICES);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);// mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_STORE_ID, mStoreId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_SERVICE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                break;

            case Constant.TabOption.MEMBERS:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_STORE_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mStoreId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE);
                map = new HashMap<>();
                map.put(Constant.KEY_STORE_ID, mStoreId);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.VIDEO:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_STORE_VIDEO);
                intent2.putExtra(Constant.KEY_ID, mStoreId);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PAGE, mPageId, this), opt.getLabel());
                break;

            case Constant.TabOption.ASSOCIATE:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ASSOCIATE);
                intent2.putExtra(Constant.KEY_ID, mStoreId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                adapter.addFragment(PageFragment.newInstance(PageFragment.TYPE_ASSOCIATE, mStoreId), opt.getLabel());
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


    private void handleLikeAsBusinessResponse(Object response, String url) {
        try {
            if (null != response) {
                ErrorResponse err = new Gson().fromJson((String) response, ErrorResponse.class);
                if (TextUtils.isEmpty(err.getErrorMessage())) {
                    JSONObject resp = new JSONObject((String) response);
                    PageLike res = new Gson().fromJson(resp.optJSONObject("result").toString(), PageLike.class);
                    if (res != null) {
                        res.setId(mStoreId);
                        PageLikeDialogFragment.newInstance(res, url, this, Constant.ResourceType.STORE).show(fragmentManager, Constant.TITLE_PRIVACY);
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
                updateItemLikeFavorite(REQ_CODE, view, result.getStore(), showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mStoreId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.STORE);
                    if (REQ_CODE == REQ_FAVORITE)
                        request.params.put(Constant.KEY_GET_FORM, 1);
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
                                            result.getStore().setMenus(opt);

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
                                        updateItemLikeFavorite(REQ_CODE, view, result.getStore(), false);
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

    public void updateItemLikeFavorite(int REQ_CODE, View view, StoreContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getStore().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
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
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getStore().isContentFollow() ? R.string.unfollow : R.string.follow);
        }

    }


    //TODO same method is on BusinessParent make it common
    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_BUSINESS_CREATE);
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
                                        map.put(Constant.KEY_PARENT_ID, mStoreId);
                                        openSelectCategory(resp.getResult().getCategory(), map, Constant.ResourceType.BUSINESS);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put(Constant.KEY_PARENT_ID, mStoreId);
                                            openStoreCreateForm(vo.getResult(), map);
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
        map.put(Constant.KEY_STORE_ID, mStoreId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.BUSINESS_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

}
