package com.sesolutions.ui.group_core;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.event_core.CEventInfoFragment;
import com.sesolutions.ui.event_core.CEventMemberFragment;
import com.sesolutions.ui.event_core.CEventPhotoFragment;
import com.sesolutions.ui.event_core.CoreEventDiscussionFragment;
import com.sesolutions.ui.events.EventMapFragment;
import com.sesolutions.ui.events.EventVideoFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.events.SuggestionEventAdapter;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGroupViewFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {


    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_RSVP = 404;
    private final int REQ_SAVE = 405;
    private final int REQ_UPDATE_UPPER = 406;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private EventResponse.Result result;
    private int mEventId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    // private Typeface fontIcon;
    //private boolean isLoading;
    private List<CommonVO> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static CGroupViewFragment newInstance(int groupId) {
        CGroupViewFragment frag = new CGroupViewFragment();
        frag.mEventId = groupId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getEventContent().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getEventContent().setCoverImageUrl(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }
                    activity.taskPerformed = 0;
                    break;
                case Constant.FormType.CREATE_DISCUSSTION:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    int pos = getTabPositionByName(Constant.TabOption.DISCUSSIONS);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.FormType.CREATE_EVENT_VIDEO:
                    activity.taskPerformed = 0;
                    goTo(Constant.GoTo.VIDEO, activity.taskId, Constant.ResourceType.EVENT_VIDEO);
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.FormType.CREATE_ALBUM_OTHERS:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.ALBUM);
                    //collapse app bar layout
                    appBarLayout.setExpanded(false, true);
                    //set album tab and refresh data
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();

                    //open view album
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put(Constant.KEY_EVENT_ID, mEventId);
                    map1.put(Constant.KEY_ALBUM_ID, activity.taskId);
                    map1.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM_VIEW);
                    map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT_ALBUM);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ViewPageAlbumFragment.newInstance(map1, null))
                            .addToBackStack(null).commit();
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
                int pos = getTabPositionByName(Constant.TabOption.UPDATES);
                viewPager.setCurrentItem(pos, true);
                (adapter.getItem(pos)).onRefresh();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateCoverPhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivCoverPhoto), url, context, 1);
    }

    private void updateProfilePhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivPageImage), url, context, 1);
    }
/*
    @Override
    public void onStop() {
        activity.isHomePageVisible = false;
        super.onStop();
    }*/


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
        v = inflater.inflate(R.layout.fragment_view_event, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        // fontIcon = FontManager.getTypeface(context);

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
        // /pageIndicatorView.setCount(adapter.getItemCount());
       /* rvPhotos.setOnSnapListener(new OnSnapListener() {
            @Override
            public void snapped(int position) {
                pageIndicatorView.setSelection(position);
            }
        });*/
    }

    private SuggestionEventAdapter adapterRelated;

    private void initRelatedPageUI() {
        MultiSnapRecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
        relatedList = new ArrayList<CommonVO>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterRelated = new SuggestionEventAdapter(relatedList, context, this, true);
        rvPhotos.setAdapter(adapterRelated);
        // /pageIndicatorView.setCount(adapter.getItemCount());

    }


    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == 1) {
                    showBaseLoader(true);
                } else if (req == REQ_UPDATE_UPPER) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_CEVENT);
                request.params.put(Constant.KEY_ID, mEventId);
                // request.params.put("menus", 1);
                // request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    if (!isAdded()) return false;

                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {

                                EventResponse commonResponse = new Gson().fromJson(response, EventResponse.class);
                                if (commonResponse.getResult() != null) {
                                    result = commonResponse.getResult();
                                }

                                if (req == REQ_UPDATE_UPPER) {
                                    setUpperUIData();
                                } else {
                                    initUI();
                                }
                                hideAllLoaders();
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                                goIfPermissionDenied(err.getError());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        somethingWrongMsg(v);
                        CustomLog.e(e);
                    }

                    // dialog.dismiss();
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
                somethingWrongMsg(v);
            }
        } else {
            notInternetMsg(v);
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
            initRelatedPageUI();
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
        if (result.getEventContent() != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            CommonVO resp = result.getEventContent();
            ((TextView) v.findViewById(R.id.tvPageTitle)).setText(resp.getTitle());

            v.findViewById(R.id.bSave).setOnClickListener(this);
            v.findViewById(R.id.bAddTo).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvStats)).setText(getDetail(resp));
            if (resp.getUpdateCoverPhoto() != null) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera).setOnClickListener(this);
            }
            if (resp.getUpdateProfilePhoto() != null) {
                v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera2).setOnClickListener(this);
            }


            v.findViewById(R.id.tvStatus).setVisibility(View.GONE);
            //((TextView) v.findViewById(R.id.tvStatus)).setText(getStatusByKey(resp.getEventStatus()));


            updateCoverPhoto(resp.getCoverPhoto());
            updateProfilePhoto(resp.getOwnerPhoto());


            v.findViewById(R.id.seeAllPhotos).setOnClickListener(this);
            v.findViewById(R.id.bSave).setVisibility(resp.canSave() ? View.VISIBLE : View.GONE);
            ((AppCompatButton) v.findViewById(R.id.bSave)).setText(resp.isContentSaved() ? R.string.unsave_event : R.string.save_event);
            // if (resp.isContentSaved())
           /* if (result.getCallToAction() != null) {
                v.findViewById(R.id.bCallAction).setVisibility(View.VISIBLE);
                ((AppCompatButton) v.findViewById(R.id.bCallAction)).setText(result.getCallToAction().getLabel());
                v.findViewById(R.id.bCallAction).setOnClickListener(this);
            } else {
                v.findViewById(R.id.bCallAction).setVisibility(View.GONE);
            }*/
            //addUpperTabItems();
            setAboutUI();

            if (null != result.getEventContent().getRSVP()) {
                v.findViewById(R.id.llrsvp).setVisibility(View.VISIBLE);
                setRSVPCardColor();
            } else {
                v.findViewById(R.id.llrsvp).setVisibility(View.GONE);
            }

            v.findViewById(R.id.cvAttend).setOnClickListener(this);
            v.findViewById(R.id.cvMayAttend).setOnClickListener(this);
            v.findViewById(R.id.cvNotAttend).setOnClickListener(this);
            updatePhotoAdapter();
            updateRelatedPageAdapter();
        }
        //  Util.showImageWithGlide(ivProfileImage, resp.getProfilePhoto(), context, 1);


        /*if (resp.isSelf(mUserId)) {
            tvCoverOption.setVisibility(View.VISIBLE);
            tvCoverOption.setTypeface(fontIcon);
            tvCoverOption.setText(Constant.FontIcon.CAMERA);
            tvProfileOption.setVisibility(View.VISIBLE);
            tvProfileOption.setTypeface(fontIcon);
            tvProfileOption.setText(Constant.FontIcon.CAMERA);
            ivCoverFoto.setOnClickListener(this);
            ivProfileImage.setOnClickListener(this);
        }*/
    }

    private void setRSVPstatus(String name) {
        try {
            for (Options opt : result.getEventContent().getRSVP()) {
                if (opt.getName().equals(name)) {
                    opt.setValue("true");
                } else {
                    opt.setValue("false");
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callStatusApi(String option) {
        if (isNetworkAvailable(context)) {
            setRSVPstatus(option);

            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_EVENT_ID, mEventId);
            map.put("option_id", option);
            new ApiController(Constant.URL_CHANGE_RSVP_STATUS, map, context, this, REQ_RSVP).execute();
            setRSVPCardColor();
        } else {
            notInternetMsg(v);
        }

    }

    private void setRSVPCardColor() {

        List<Options> rsvp = result.getEventContent().getRSVP();
        int txt1 = Color.parseColor(Constant.text_color_1);
        int foreground = Color.parseColor(Constant.foregroundColor);
        for (Options opt : rsvp) {
            switch (opt.getName()) {
                case "2":
                    if ("true".equals(opt.getValue())) {
                        ((CardView) v.findViewById(R.id.cvAttend)).setCardBackgroundColor(ContextCompat.getColor(context, R.color.leaf_green));
                        ((TextView) v.findViewById(R.id.tvAttend)).setTextColor(Color.WHITE);
                    } else {
                        ((CardView) v.findViewById(R.id.cvAttend)).setCardBackgroundColor(foreground);
                        ((TextView) v.findViewById(R.id.tvAttend)).setTextColor(txt1);
                    }
                    break;
                case "1":
                    if ("true".equals(opt.getValue())) {
                        ((CardView) v.findViewById(R.id.cvMayAttend)).setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_type));
                        ((TextView) v.findViewById(R.id.tvMayAttend)).setTextColor(Color.WHITE);
                    } else {
                        ((CardView) v.findViewById(R.id.cvMayAttend)).setCardBackgroundColor(foreground);
                        ((TextView) v.findViewById(R.id.tvMayAttend)).setTextColor(txt1);
                    }
                    break;
                case "0":
                    if ("true".equals(opt.getValue())) {
                        ((CardView) v.findViewById(R.id.cvNotAttend)).setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        ((TextView) v.findViewById(R.id.tvNotAttend)).setTextColor(Color.WHITE);
                    } else {
                        ((CardView) v.findViewById(R.id.cvNotAttend)).setCardBackgroundColor(foreground);
                        ((TextView) v.findViewById(R.id.tvNotAttend)).setTextColor(txt1);
                    }
                    break;
            }
        }

    }

    //set tab bar items
    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        if (result.getEventContent().getProfileTabs() != null) {


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
            List<Options> list = result.getEventContent().getProfileTabs();
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {
                    case Constant.TabOption.INFO:
                        adapter.addFragment(CEventInfoFragment.newInstance(mEventId), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
                        adapter.addFragment(FeedFragment.newInstance(mEventId, Constant.ResourceType.EVENT), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.EVENT);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.VIDEO:
                        map = new HashMap<>();
                        map.put(Constant.KEY_PARENT_ID, mEventId);
                        map.put("parent_type", Constant.ResourceType.EVENT);
                        adapter.addFragment(EventVideoFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.DISCUSSIONS:
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_CEVENT_DISCUSSION);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.EVENT);
                        adapter.addFragment(CoreEventDiscussionFragment.newInstance(map), opt.getLabel());
                        break;

                    case "photos":
                        adapter.addFragment(CEventPhotoFragment.newInstance(this, mEventId, "photos"), opt.getLabel());
                        break;

                    case Constant.TabOption.LOCATION:
                        LocationActivity vo = result.getEventContent().getLocationObject();
                        if (null != vo) {
                            Bundle bundle = new Bundle();
                            //  bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                            bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                            bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.EVENT);
                            adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
                        }
                        break;

                    case Constant.TabOption.MEMBERS:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_CEVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putInt(Constant.KEY_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.EVENT);

                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(CEventMemberFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getEventContent().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;
                   /* default:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());
                        break;*/

                }
            }

            //Add T&C tab manually
            if (!TextUtils.isEmpty(result.getEventContent().getCustomTnC())) {
                Options opt = new Options(Constant.TabOption.TnC, getStrings(R.string.terms_and_condition));
                result.getEventContent().getProfileTabs().add(opt);
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.TEXT, result.getEventContent().getCustomTnC());
                map.put(Constant.KEY_ERROR, getStrings(R.string.no_tnc));
                adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
            }

            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[adapter.getCount()];
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(adapter.getCount());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void applyTabListener() {

        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //  viewPager.onRefresh();
               /* updateToolbarIcons(tab.getPosition());
                loadFragmentIfNotLoaded(tab.getPosition());
                updateTitle(tab.getPosition());*/


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
        try {
            if (!isLoaded[position] && isNetworkAvailable(context)) {
                isLoaded[position] = true;
                adapter.getItem(position).initScreenData();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.backgroundColor));
        appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getEventContent().getTitle());
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

    private void updatePhotoAdapter() {
        View rlPhotos = v.findViewById(R.id.rlPhotos);
        if (result.getPhoto() != null && result.getPhoto().size() > 0) {
            rlPhotos.setVisibility(View.VISIBLE);
            for (Albums vo : result.getPhoto()) {
                photoList.add(vo);
                adapterPhoto.notifyItemInserted(photoList.size() - 1);
            }
        } else {
            rlPhotos.setVisibility(View.GONE);
        }
    }

    private void updateRelatedPageAdapter() {
        View rlRecent = v.findViewById(R.id.rlRecent);
        ((TextView) v.findViewById(R.id.tvRelated)).setText(R.string.related_events);
        if (result.getRelatedPages() != null && result.getRelatedPages().size() > 0) {
            rlRecent.setVisibility(View.VISIBLE);
            for (CommonVO vo : result.getRelatedPages()) {
                relatedList.add(vo);
                adapterRelated.notifyItemInserted(relatedList.size() - 1);
            }
        } else {
            rlRecent.setVisibility(View.GONE);
        }
    }

    private void setAboutUI() {

        v.findViewById(R.id.llAbout).setVisibility(View.GONE);
    }


    public String getDetail(CommonVO album) {
        String detail = "";
        try {
            detail += album.getLikeCount() + (album.getLikeCount() != 1 ? getStrings(R.string._LIKES) : getString(R.string._LIKE))
                    + ", " + album.getCommentCount() + (album.getCommentCount() != 1 ? getString(R.string._COMMENTS) : getString(R.string._COMMENT))
                    + ", " + album.getViewCountInt() + (album.getViewCountInt() != 1 ? getString(R.string._VIEWS) : getString(R.string._VIEW))
                    // + ", " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? getString(R.string._FAVORITES) : getString(R.string._FAVORITE))
                    + ", " + album.getFollowCount() + (album.getFollowCount() != 1 ? getString(R.string._followers) : getString(R.string._follower))
            //   + ", " + album.getMemberCount() + (album.getMemberCount() != 1 ? getString(R.string._members) : getString(R.string._member))
            ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return detail;
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
                    showShareDialog(result.getEventContent().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getEventContent().getGutterMenu(), vItem, 10);
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
            if (null != result && null != result.getEventContent() && result.getEventContent().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getEventContent().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && null != result.getEventContent() && result.getEventContent().getGutterMenu() != null) {
                menu.add(Menu.NONE, R.id.option, Menu.FIRST, "options")
                        .setIcon(R.drawable.vertical_dots)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private int getStatusByKey(String name) {
        try {
            int id;
            switch (name) {
                case "notStarted":
                    id = R.string.status_not_started;
                    break;
                case "expire":
                    id = R.string.status_expire;
                    break;
                default:
                    id = R.string.status_ongoing;
                    break;
            }
            return id;
        } catch (Exception e) {
            CustomLog.e(e);
            return R.string.status_ongoing;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            Options opt = null;
            boolean isCover = false;
            int itemId = item.getItemId();
            if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getEventContent().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getEventContent().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getEventContent().getGutterMenu().get(itemId - 1);
            }


            switch (opt.getParamsAction()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.EDIT_EVENT, map, Constant.URL_EDIT_CEVENT)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.DASHBOARD:
                    openWebView(Constant.URL_EVENT_DASHBOARD + mEventId, opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getEventContent().getShare());
                    break;

                case Constant.OptionType.REPORT:
                case "create":
                    goToReportFragment(Constant.ModuleName.EVENT + "_" + mEventId);
                    break;
                case "compose":
                    openComposeActivity(null);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_ID, mEventId);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_CEVENT_INVITE).show(fragmentManager, "social");
                    //  openInviteForm(map,Constant.URL_EVENT_INVITE );
                    break;

                case Constant.OptionType.JOIN_SMOOTHBOX:
                    showJoinDialog(Constant.URL_CEVENT_JOIN);
                    break;
                case Constant.OptionType.ACCEPT:
                    showJoinDialog(Constant.URL_CEVENT_ACCEPT_INVITE);
                    break;
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                case Constant.OptionType.CANCEL:
                case Constant.OptionType.REJECT:
                    showJoinLeaveDialog(opt);
                    break;

                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getEventContent().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_ID, mEventId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.EVENT);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_EVENT_COVER : Constant.URL_UPLOAD_EVENT_PHOTO, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_COVER, result.getEventContent().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_PHOTO, result.getEventContent().getImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getEventContent().getCoverImageUrl(), resourceType, result.getEventContent().getCoverImageUrl());
                    break;
                case Constant.OptionType.remove_photo:
                    showImageRemoveDialog(isCover);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void showJoinDialog(final String url) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_options_radio);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.msg_join_event));

            final RadioGroup radioGroup = (RadioGroup) progressDialog.findViewById(R.id.radioGroup);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{Color.parseColor(Constant.menuButtonTitleColor)} //enabled
                        },
                        new int[]{Color.parseColor(Constant.outsideButtonBackgroundColor)}
                );

                AppCompatRadioButton rbAttending = (AppCompatRadioButton) progressDialog.findViewById(R.id.rbAttending);
                AppCompatRadioButton rbAttendingMayBe = (AppCompatRadioButton) progressDialog.findViewById(R.id.rbAttendingMayBe);
                AppCompatRadioButton rbAttendingNot = (AppCompatRadioButton) progressDialog.findViewById(R.id.rbAttendingNot);

                rbAttending.setButtonTintList(colorStateList);
                rbAttendingMayBe.setButtonTintList(colorStateList);
                rbAttendingNot.setButtonTintList(colorStateList);
            }

            progressDialog.findViewById(R.id.bJoin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    int id = radioGroup.getCheckedRadioButtonId();
                    int rsvp = 0;
                    if (id == R.id.rbAttending) {
                        rsvp = 2;
                    } else if (id == R.id.rbAttendingMayBe) {
                        rsvp = 1;
                    }
                    callDeleteApi(REQ_JOIN, url, rsvp);
                }
            });

            progressDialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //TODO same method is on ProfileFragment
    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_REMOVE_EVENT_COVER : Constant.URL_REMOVE_EVENT_PHOTO;
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
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                    new ApiController(url, map, context, CGroupViewFragment.this, Constant.Events.REMOVE_PHOTO).execute();
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

        String dialogMsg = Constant.EMPTY;
        String buttonTxt = Constant.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        switch (opt.getParamsAction()) {
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_event);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_CEVENT_CANCEL;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.REJECT:
                dialogMsg = getStrings(R.string.msg_invite_reject_event);
                buttonTxt = getStrings(R.string.reject_invite);
                url[0] = Constant.URL_CEVENT_REJECT;
                req[0] = REQ_JOIN;
                break;


            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_event);
                buttonTxt = getStrings(R.string.leave_event);
                url[0] = Constant.URL_CEVENT_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership_event);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_CEVENT_REQUEST;
                req[0] = REQ_JOIN;
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
                callDeleteApi(req[0], url[0], -1);
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
                case R.id.cvAttend:
                    callStatusApi("2");
                    break;
                case R.id.cvMayAttend:
                    callStatusApi("1");
                    break;
                case R.id.cvNotAttend:
                    callStatusApi("0");
                    break;

                case R.id.seeAllPhotos:
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.ALBUM));
                    break;

                case R.id.bCallAction:
                    openWebView(result.getCallToAction().getValue(), " ");
                    break;
                case R.id.bAddTo:
                    addToCalendar(context, result.getEventContent());
                    // CalendarEventHelper.addToCalenderr(context, result.getEventContent());
                    // Util.showSnackbar(v,getStrings(R.string.msg_added_to_calendar));
                    break;
                case R.id.bSave:
                    //call save/unsave API
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                        //send contentId only if event is saved
                        if (result.getEventContent().isContentSaved()) {
                            map.put("contentId", result.getEventContent().getSaveId());
                        }
                        result.getEventContent().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getEventContent().isContentSaved() ? R.string.unsave_event : R.string.save_event);
                        new ApiController(Constant.URL_SAVE_EVENT, map, context, this, REQ_SAVE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case R.id.bContact:
                    // super.openPageContactForm(result.getEventContent().getOwnerId());
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getEventContent().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    //   if (null != result.getEventContent().getUpdateCoverPhoto())
                    showPopup(result.getEventContent().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void addToCalendar(Context context, CommonVO vo) {

        String title = vo.getTitle();
        String addInfo = vo.getDescription();
        String place = vo.getLocationString();
        int status = 1;
        long startDate = Util.getDateStringInMillis(vo.getStartTime());
        long endDate = Util.getDateStringInMillis(vo.getEndTime());
        boolean needReminder = true;
        boolean needMailService = true;
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", title);
        intent.putExtra("description", addInfo);
        intent.putExtra("beginTime", startDate);
        intent.putExtra("endTime", endDate);
        context.startActivity(intent);
    }

    private int getTabPositionByName(String name) {
        int position = 0;
        for (int i = 0; i < result.getEventContent().getProfileTabs().size(); i++) {
            if (result.getEventContent().getProfileTabs().get(i).getClazz().equals(name)) {
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_EVENT));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_DELETE_CEVENT, -1);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, String url, int rsvp) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false);
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_ID, mEventId);
                if (rsvp > -1) {
                    request.params.put(Constant.KEY_RSVP, rsvp);
                }
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
                                    } else if (REQ == REQ_JOIN || REQ == REQ_LEAVE) {
                                        String message = new JSONObject(response).optJSONObject("result").optString("success_message");
                                        Util.showSnackbar(v, message);
                                        callMusicAlbumApi(REQ_UPDATE_UPPER);
                                    }

                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                somethingWrongMsg(v);
                                //updating upper layout ,if something went wrong
                                callMusicAlbumApi(REQ_UPDATE_UPPER);
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

    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        try {
            CustomLog.e("POPUP", "" + object2 + "  " + object2 + "  " + position);
            switch (object1) {

                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    //here position is event id
                    goToViewEventFragment(position);
                    break;
                case Constant.Events.IMAGE_1:
                    openLighbox(photoList.get(position).getPhotoId(), photoList.get(position).getImages().getNormal(), photoList.get(position).getAlbumId());
                    break;

                case REQ_SAVE:
                    ErrorResponse err = new Gson().fromJson("" + object2, ErrorResponse.class);
                    if (!err.isSuccess()) {
                        Util.showSnackbar(v, err.getMessage());
                        //in case of error ,revert save changes
                        result.getEventContent().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getEventContent().isContentSaved() ? R.string.unsave_event : R.string.save_event);
                    } else {
                        SuccessResponse suc = new Gson().fromJson("" + object2, SuccessResponse.class);
                        if (null != suc.getResult()) {
                            result.getEventContent().setSaveId(suc.getResult().getSaved_id());
                            Util.showSnackbar(v, suc.getResult().getMessage());
                        }
                    }
                    break;
                case REQ_RSVP:
                    err = new Gson().fromJson("" + object2, ErrorResponse.class);
                    if (!err.isSuccess()) {
                        Util.showSnackbar(v, err.getMessage());
                        //in case of error ,revert RSVP changes
                        setRSVPstatus(""); //empty name means ,nothing selected
                        setRSVPCardColor();
                    }
                    break;
               /* case Constant.Events.LIKE_AS_PAGE:
                    hideBaseLoader();
                    handleLikeAsPageResponse(object2);
                    break;*/

                case Constant.Events.REMOVE_PHOTO:
                    hideBaseLoader();
                    handleResponses(object2);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }

    private void handleResponses(Object response) {

    }

    private void callLikeApi(final int REQ_CODE, final View view, String url) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, result.getEventContent());
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mEventId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.EVENT);
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
                                            result.getEventContent().setGutterMenu(opt);

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
                                        updateItemLikeFavorite(REQ_CODE, view, result.getEventContent());
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

    public void updateItemLikeFavorite(int REQ_CODE, View view, CommonVO vo) {

        switch (REQ_CODE) {
            case REQ_LIKE:
                vo.setContentLike(!vo.isContentLike());
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
                break;
            case REQ_FAVORITE:
                vo.setContentFavourite(!vo.isContentFavourite());
                ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
                break;
            case REQ_FOLLOW:
                vo.setContentFollow(!vo.isContentFollow());
                ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.colorPrimary : Constant.text_color_1));
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
                break;
        }

    }


    private void openLighbox(int photoId, String imageUrl, int albumId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_EVENT_ID, mEventId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.EVENT_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        //map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

}
