package com.sesolutions.ui.courses.classroom;


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

import android.text.Html;
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
import com.sesolutions.responses.Courses.classroom.ClassroomContent;
import com.sesolutions.responses.Courses.classroom.ClassroomResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.courses.adapters.SuggestionClassroomAdapter;
import com.sesolutions.ui.courses.course.CourseFragment;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.groups.ClaimFormFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageLikeDialogFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.page.PageServicesFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.store.StoreInfoFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
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

public class ViewClassroomFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {

    private static final int REQ_UPDATE_UPPER = 99;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_CANCEL = 404;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ClassroomResponse.Result result;
    private int mclassroomId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<ClassroomContent> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewClassroomFragment newInstance(int pageId) {
        ViewClassroomFragment frag = new ViewClassroomFragment();
        frag.mclassroomId = pageId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getClassroom().getImages().setMain(activity.stringValue);
                        updateProfilePhoto(activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getClassroom().getCoverImage().setMain(activity.stringValue);
                        updateCoverPhoto(activity.stringValue);
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

                    Map<String, Object> map1 = new HashMap<>();
                    map1.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    map1.put(Constant.KEY_ALBUM_ID, activity.taskId);
                    map1.put(Constant.KEY_URI, Constant.URL_CLASSROOM_ALBUMVIEW);
                    map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ViewPageAlbumFragment.newInstance(map1, null))
                            .addToBackStack(null).commit();
                    break;
                case Constant.FormType.EDIT_CLASSROOM:
                    activity.taskPerformed = 0;
                    onRefresh();
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
                    goToViewReviewFragment(Constant.ResourceType.CLASSROOM_REVIEW, activity.taskId);
                    break;

                case Constant.FormType.CREATE_POLL:
                case Constant.TASK_ALBUM_DELETED:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.ALBUM);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
//                    openViewClassroomFragment(activity.taskId);
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
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_classroom, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
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

//    private SuggestionClassroomAdapter adapterRelated;
//
//    private void initRelatedPageUI() {
//        MultiSnapRecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
//        relatedList = new ArrayList<ClassroomContent>();
//        rvPhotos.setHasFixedSize(true);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        rvPhotos.setLayoutManager(layoutManager);
//        adapterRelated = new SuggestionClassroomAdapter(relatedList, context, this, true);
//        rvPhotos.setAdapter(adapterRelated);
//    }


    public void callMusicAlbumApi(final int req) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req == 1) {
                        showBaseLoader(true);
                    } else if (req == REQ_UPDATE_UPPER) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CLASSROOM_VIEW);
                    request.params.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
//                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);

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
                                        ClassroomResponse commonResponse = new Gson().fromJson(response, ClassroomResponse.class);
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

    private void initUI() {
        try {
            v.findViewById(R.id.cl).setVisibility(View.VISIBLE);
            // getActivity().invalidateOptionsMenu();
            initCollapsingToolbar();
            initPhoto();
//            initRelatedPageUI();
            setUpperUIData();
            initTablayout();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void setUpperUIData() {

        mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        if (result.getClassroom() != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            ClassroomContent resp = result.getClassroom();
            ((TextView) v.findViewById(R.id.tvPageTitle)).setText(resp.getTitle());
            ((TextView) v.findViewById(R.id.tvDesc)).setText(Html.fromHtml(resp.getDescription()));
            ((TextView) v.findViewById(R.id.tvRating)).setText(resp.getRating());
            v.findViewById(R.id.tvDesc).setOnClickListener(this);

            ((TextView) v.findViewById(R.id.tvCategory)).setText(resp.getCategory_title());
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

            if (resp.getVerified() == 1) {
                v.findViewById(R.id.ivVerified).setVisibility(View.VISIBLE);
            }
            if (resp.getHot() == 1) {
                v.findViewById(R.id.ivHot).setVisibility(View.VISIBLE);
            }
            if (resp.getFeatured() == 1) {
                v.findViewById(R.id.tvFeatured).setVisibility(View.VISIBLE);
            }
            if (resp.canContact()) {
                v.findViewById(R.id.bContact).setVisibility(View.VISIBLE);
                v.findViewById(R.id.bContact).setOnClickListener(this);
            } else {
                v.findViewById(R.id.bContact).setVisibility(View.GONE);
            }

            updateCoverPhoto(resp.getCoverImageUrl());
            updateProfilePhoto(resp.getMainImageUrl());
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
//            updateRelatedPageAdapter();
        }
    }

    //set tab bar items
    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        if (result.getMenus() != null) {
            //create a boolean array that can be used in preventing multiple loading of any tab
            isLoaded = new boolean[result.getMenus().size()];

            setupViewPager();
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.setupWithViewPager(viewPager, true);
            applyTabListener();
            new Handler().postDelayed(() -> loadScreenData(0), 200);
        } else {
            tabLayout.setVisibility(View.GONE);
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
                        adapter.addFragment(ClassroomInfoFragment.newInstance(mclassroomId), opt.getLabel());
                        HashMap<String, Object> map2 = new HashMap<>();
                        map2 = new HashMap<>();
                        map2.put(Constant.TEXT, result.getClassroom().getDescription());
                        map2.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_description_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map2, null), "Description");
                        break;

                    case Constant.TabOption.POSTS:
                        adapter.addFragment(FeedFragment.newInstance(mclassroomId, Constant.ResourceType.CLASSROOM), opt.getLabel());
                        break;

                    case Constant.TabOption.COMMENT:
                        adapter.addFragment(CourseFragment.newInstance(CourseFragment.TYPE_CLASSROOM_COURSE, this, mclassroomId), "Courses");
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        map.put(Constant.KEY_URI, Constant.URL_CLASSROOM_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;


                    case Constant.TabOption.MAP:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_CLASSROOM_MAP);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mclassroomId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
                        adapter.addFragment(com.sesolutions.ui.courses.classroom.ClassroomMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.REVIEW:
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM_REVIEW);
                        adapter.addFragment(PageProfileReviewFragment.newInstance(opt.getName(), this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getClassroom().getDescription());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_description_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;
                    case Constant.TabOption.ANNOUNCE:
                        bundle = new Bundle();

                        //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_CLASSROOM_ANNOUNCEMENT);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case Constant.TabOption.SERVICES:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_CLASSROOM_SERVICES);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mclassroomId);// mPageId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
                        adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case "claim":
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM_CLASS, map, Constant.URL_CLASSROOM_CLAIM), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_CLASSROOM_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mclassroomId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOMS);
                        map = new HashMap<>();
                        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(com.sesolutions.ui.courses.classroom.ClassroomMemberFragment.newInstance(bundle), opt.getLabel());
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
        tabLayout.addOnTabSelectedListener(this);
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
            adapter.getItem(tab.getPosition()).onRefresh();
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
                    swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getClassroom().getTitle());
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
        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        if (result.getClassroom().canLike()) {

            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(result.getClassroom().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getClassroom().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getClassroom().hasToChangeFollowLike()) {
                    callLikeApi(REQ_FOLLOW, view2, Constant.URL_CLASSROOM_FOLLOW, false);
                }
                callLikeApi(REQ_LIKE, view1, Constant.URL_CLASSROOM_LIKE, true);
            });
            llTabOptions.addView(view1);
        }

        //add favourite item
        if (result.getClassroom().canFavourite()) {
            final View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(result.getClassroom().isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view3, Constant.URL_CLASSROOM_FAVORITE, true));
            llTabOptions.addView(view3);
        }

        //add Follow item
        if (mUserId != result.getClassroom().getOwner_id() && result.getClassroom().canFollow()) { //don't show follow button to Page Owner

            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(result.getClassroom().isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unfollow));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setColorFilter(result.getClassroom().isContentFollow() ? ContextCompat.getColor(context, R.color.sky_blue) : color);
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> {

                //if likeFollow setting enabled then also call like api
                if (result.getClassroom().hasToChangeFollowLike()) {
                    callLikeApi(REQ_LIKE, view1, Constant.URL_CLASSROOM_LIKE, false);
                }
                callLikeApi(REQ_FOLLOW, view2, Constant.URL_CLASSROOM_FOLLOW, true);
            });
            llTabOptions.addView(view2);
        }

        //add more item
        if (null != result.getClassroom().getButtons()) {
            final View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(R.string.TEXT_MORE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.more_profile));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
            view.setOnClickListener(v -> showPopup(result.getClassroom().getButtons(), view, 2000));
            llTabOptions.addView(view);
        }
    }


    private void updatePhotoAdapter() {
        try {
            photoList.clear();
            if (result.getPhoto() != null && result.getPhoto().size() > 0) {
                v.findViewById(R.id.rlPhotos).setVisibility(View.GONE);
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

    private void setAboutUI() {
        LinearLayoutCompat llAbout = v.findViewById(R.id.llAbout);
        llAbout.removeAllViews();
        if (result.getAbout() != null) {
            llAbout.setVisibility(View.VISIBLE);
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
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(SpanUtil.getHashTags(result.getClassroom().getTag(), this));
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
            applyTheme(llAbout);
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
                    if (SPref.getInstance().isLoggedIn(context)) {
                        showShareDialog(result.getClassroom().getShare());
                    } else {
                        shareOutside(result.getClassroom().getShare());
                    }

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
            if (null != result && null != result.getClassroom() && result.getClassroom().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getClassroom().getShare().getLabel())
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
                opt = result.getClassroom().getButtons().get(itemId - 1);
            } else if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getClassroom().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getClassroom().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getOptions().get(itemId - 1);
            }
            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditClassroomFragment.newInstance(Constant.FormType.EDIT_CLASSROOM, map, Constant.URL_EDIT_CLASSROOM, null)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.DASHBOARD:
                    openWebView(opt.getValue(), opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        showShareDialog(result.getClassroom().getShare());
                    } else {
                        shareOutside(result.getClassroom().getShare());
                    }
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ResourceType.CLASSROOM + "_" + mclassroomId);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    //openInviteForm(map, Constant.URL_PAGE_INVITE);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_CLASSROOM_INVITE).show(fragmentManager, "social");
                    break;

                case Constant.OptionType.LIKE_AS_PAGE:
                    // PageLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mclassroomId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);
                        new ApiController(Constant.URL_LIKE_AS_PAGE, map, context, this, Constant.Events.LIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case Constant.OptionType.UNLIKE_AS_PAGE:
                    // PageLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mclassroomId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);
                        new ApiController(Constant.URL_UNLIKE_AS_PAGE, map, context, this, Constant.Events.UNLIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case Constant.OptionType.CREATE_ASSOCIATE_PAGE:
                    fetchFormData();
                    break;
                case "accept":
                case "reject":
                case Constant.OptionType.JOIN_SMOOTHBOX:
                case Constant.OptionType.CANCEL_SMOOTHBOX:
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                    showJoinLeaveDialog(opt);
                    break;

                case Constant.OptionType.view_profile_photo:
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    map.put(Constant.KEY_ID, mclassroomId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.CLASSROOM);
                    openSelectAlbumFragment(isCover ? Constant.URL_CLASSROOM_UPLOAD_COVER : Constant.URL_CLASSROOM_UPLOAD_MAIN, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_CLASSROOM_UPLOAD_COVER, result.getClassroom().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_CLASSROOM_UPLOAD_MAIN, result.getClassroom().getMainImageUrl(), opt.getLabel(), map);
                    }
                    break;

                case Constant.OptionType.view_cover_photo:
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
            final String url = isCover ? Constant.URL_CLASSROOM_REMOVE_COVER : Constant.URL_CLASSROOM_REMOVE_MAIN;
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
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);
                    new ApiController(url, map, context, ViewClassroomFragment.this, Constant.Events.REMOVE_PHOTO).execute();
                    onRefresh();
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
                dialogMsg = getStrings(R.string.msg_join_classroom);
                buttonTxt = getStrings(R.string.join_classroom);
                url[0] = Constant.URL_CLASSROOM_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_classroom);
                buttonTxt = getStrings(R.string.leave_classroom);
                url[0] = Constant.URL_CLASSROOM_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership_classroom);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_CLASSROOM_JOIN;
                req[0] = REQ_REQUEST;
                break;
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_classroom);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_CANCEL_MEMBER;
                req[0] = REQ_CANCEL;
                break;
            case "accept":
                dialogMsg = getStrings(R.string.msg_request_accept_classroom);
                buttonTxt = "accept request";
                url[0] = Constant.URL_CLASSROOM_ACCEPT;
                req[0] = REQ_CANCEL;
                break;
            case "reject":
                dialogMsg = getStrings(R.string.msg_request_reject_classroom);
                buttonTxt = "reject request";
                url[0] = Constant.URL_CLASSROOM_REJECT;
                req[0] = REQ_CANCEL;
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
                case R.id.tvDesc:
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(3);
                    break;

                case R.id.bCallAction:
                    openWebView(result.getCallToAction().getValue(), " ");
                    break;


                case R.id.bContact:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        super.openClassroomContactForm(result.getClassroom().getOwner_id());
                    } else {
                       // fragmentManager.beginTransaction().add(R.id.container, new SignInFragment()).addToBackStack(null).commit();
                        fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                                .addToBackStack(null)
                                .commit();
                    }
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getClassroom().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    if (null != result.getClassroom().getUpdateCoverPhoto())
                        showPopup(result.getClassroom().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_CLASSROOM));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_CLASSROOM_DELETE);
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
                    request.params.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    request.params.put(Constant.KEY_USER_ID, SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId());
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

                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    openViewPageFragment(position);
                    break;
                case Constant.Events.IMAGE_1:
                    openLighbox(photoList.get(position).getPhotoId(), photoList.get(position).getImages().getNormal(), photoList.get(position).getAlbumId());
                    break;
                case Constant.Events.LIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsPageResponse(object2, Constant.URL_LIKE_AS_PAGE);
                    break;
                case Constant.Events.UNLIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsPageResponse(object2, Constant.URL_UNLIKE_AS_PAGE);
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


    private void handleLikeAsPageResponse(Object response, String url) {
        try {
            if (null != response) {
                ErrorResponse err = new Gson().fromJson((String) response, ErrorResponse.class);
                if (TextUtils.isEmpty(err.getErrorMessage())) {
                    JSONObject resp = new JSONObject((String) response);
                    PageLike res = new Gson().fromJson(resp.optJSONObject("result").toString(), PageLike.class);
                    if (res != null) {
                        res.setId(mclassroomId);
                        PageLikeDialogFragment.newInstance(res, url, this, Constant.ResourceType.CLASSROOM).show(fragmentManager, Constant.TITLE_PRIVACY);
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
                updateItemLikeFavorite(REQ_CODE, view, result.getClassroom(), showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.CLASSROOM);
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

                                        if (REQ_CODE > REQ_DELETE && REQ_CODE != REQ_JOIN) {
                                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                            List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                            result.getClassroom().setButtons(opt);
                                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                        }
                                        if (REQ_CODE == REQ_JOIN) {
                                            ClassroomResponse op = new Gson().fromJson(response, ClassroomResponse.class);
                                            Util.showSnackbar(v, op.getResult().getMessage());
                                        }
                                    } else {
                                        //revert changes in case of error
                                        updateItemLikeFavorite(REQ_CODE, view, result.getClassroom(), false);
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

    public void updateItemLikeFavorite(int REQ_CODE, View view, ClassroomContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getClassroom().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            //((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, vo.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.followBlue : Constant.text_color_1));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getClassroom().isContentFollow() ? R.string.unfollow : R.string.follow);
        }
    }

    //TODO same method is on PageParent make it common
    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_CLASSROOM);
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
                                        map.put(Constant.KEY_PARENT_ID, mclassroomId);
                                        openSelectCategory(resp.getResult().getCategory(), map, Constant.ResourceType.PAGE);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put(Constant.KEY_PARENT_ID, mclassroomId);
                                            openPageCreateForm(vo.getResult(), map);
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
        map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

}
