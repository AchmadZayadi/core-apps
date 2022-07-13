package com.sesolutions.ui.events;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
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
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.profile.ProfileTabsAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {


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

    private Typeface fontIcon;
    private boolean isLoading;
    private List<CommonVO> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewEventFragment newInstance(int groupId) {
        ViewEventFragment frag = new ViewEventFragment();
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
                        result.getEvent().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getEvent().setCoverImageUrl(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }
                    activity.taskPerformed = 0;
                    break;
                case Constant.FormType.CREATE_DISCUSSTION:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    int pos = getTabPositionByName(Constant.TabOption.DISCUSSIONS);
                    viewPager.setCurrentItem(pos, true);
                    ((DiscussionFragment) adapter.getItem(pos)).onRefresh();
                    break;
                case Constant.FormType.CREATE_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    goToViewReviewFragment(Constant.ResourceType.SES_EVENT_REVIEW, activity.taskId);
                    break;
                case Constant.FormType.EDIT_REVIEW:
                    //case Constant.Task.DELETE_REVIEW:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.REVIEWS);
                    viewPager.setCurrentItem(pos, true);
                    ((ReviewFragment) adapter.getItem(pos)).onRefresh();
                    break;
                case Constant.FormType.CREATE_EVENT_VIDEO:
                    activity.taskPerformed = 0;
                    goTo(Constant.GoTo.VIDEO, activity.taskId, Constant.ResourceType.SES_EVENT_VIDEO);
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    viewPager.setCurrentItem(pos, true);
                    ((EventVideoFragment) adapter.getItem(pos)).onRefresh();
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
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivCoverPhoto), url, context, R.drawable.placeholder_square);
    }

    private void updateProfilePhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivPageImage), url, context, R.drawable.placeholder_square);
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
        fontIcon = FontManager.getTypeface(context);

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
            isLoading = true;
            try {
                if (req == 1) {
                    showBaseLoader(true);
                } else if (req == REQ_UPDATE_UPPER) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_EVENT);
                request.params.put(Constant.KEY_EVENT_ID, mEventId);
                request.params.put("menus", 1);
                request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (!isAdded()) return false;

                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    EventResponse commonResponse = new Gson().fromJson(response, EventResponse.class);
                                    if (commonResponse.getResult() != null) {
                                        //if screen is refreshed then clear previous data
                                       /* if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }

                                        wasListEmpty = videoList.size() == 0;
                                        result = resp.getResult();
                                        if (null != result.getGroups())
                                            videoList.addAll(result.getGroups());

                                        updateAdapter();*/
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
                    }
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
        if (result.getEvent() != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            CommonVO resp = result.getEvent();
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

            ((TextView) v.findViewById(R.id.tvStatus)).setText(getStatusByKey(resp.getEventStatus()));
            // ImageView ivCoverFoto = v.findViewById(R.id.ivCoverPhoto);
            //  ImageView ivPageImage = v.findViewById(R.id.ivPageImage);

            updateCoverPhoto(resp.getCoverImageUrl());
            updateProfilePhoto(resp.getImageUrl());
            //Util.showImageWithGlide(ivCoverFoto, resp.getCoverImageUrl(), context, 1);

            // Util.showImageWithGlide(ivPageImage, resp.getMainImageUrl(), context, 1);


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

            if (null != result.getEvent().getRSVP()) {
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
            for (Options opt : result.getEvent().getRSVP()) {
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

        List<Options> rsvp = result.getEvent().getRSVP();
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
        //  tabLayout = v.findViewById(R.id.tabs);
        //   tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        //   tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        if (result.getMenus() != null) {


            setupViewPager();
            //     tabLayout.clearOnTabSelectedListeners();
            //     tabLayout.setupWithViewPager(viewPager, true);
            //    applyTabListener();

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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadScreenData(0);
                }
            }, 200);
        } else {
            //   tabLayout.setVisibility(View.GONE);
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
                        adapter.addFragment(EventInfoFragment.newInstance(mEventId), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
                        adapter.addFragment(FeedFragment.newInstance(mEventId, Constant.ResourceType.SES_EVENT), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.VIDEO:
                        map = new HashMap<>();
                        map.put(Constant.KEY_PARENT_ID, mEventId);
                        map.put("parent_type", Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(EventVideoFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.DISCUSSIONS:
                        map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_DISCUSSION);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(DiscussionFragment.newInstance(map), opt.getLabel());
                        break;
                    case Constant.TabOption.REVIEWS:
                        map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_REVIEWS);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(ReviewFragment.newInstance(map, null), opt.getLabel());
                        break;
                    case Constant.TabOption.LOCATION:
                        LocationActivity vo = result.getEvent().getLocationObject();
                        if (null != vo) {
                            Bundle bundle = new Bundle();
                            //  bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                            bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                            bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                            adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
                        }
                        break;

                    case Constant.TabOption.MEMBERS:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putInt(Constant.KEY_EVENT_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);

                        map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(EventMemberFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getEvent().getOverview());
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
            if (!TextUtils.isEmpty(result.getEvent().getCustomTnC())) {
                Options opt = new Options(Constant.TabOption.TnC, getStrings(R.string.terms_and_condition));
                result.getMenus().add(opt);
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.TEXT, result.getEvent().getCustomTnC());
                map.put(Constant.KEY_ERROR, getStrings(R.string.no_tnc));
                adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
            }

            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[result.getMenus().size()];
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
                        collapsingToolbar.setTitle(result.getEvent().getTitle());
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
        LinearLayoutCompat llAbout = v.findViewById(R.id.llAbout);
        if (result.getAbout() != null) {
            llAbout.setVisibility(View.VISIBLE);
            //add about layout items
            for (final Options opt : result.getAbout()) {
                switch (opt.getName()) {
                    case Constant.OptionType.SEE_ALL:
                        View view = getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llAbout, false);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performAboutOptionClick(opt);
                            }
                        });
                        llAbout.addView(view);
                        break;
                    case Constant.OptionType.CREATE_DATE:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(Util.changeDate(opt.getValue()));
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_post));
                        llAbout.addView(view);
                        break;
                    default:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(opt.getValue());
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performAboutOptionClick(opt);
                            }
                        });
                        llAbout.addView(view);
                        break;
                }
            }
        } else {
            llAbout.setVisibility(View.GONE);
        }
    }


    private void performAboutOptionClick(@NonNull Options opt) {
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

    public String getDetail(CommonVO album) {
        String detail = "";
        try {
            detail += album.getLikeCount() + (album.getLikeCount() != 1 ? getStrings(R.string._LIKES) : getString(R.string._LIKE))
                    + ", " + album.getCommentCount() + (album.getCommentCount() != 1 ? getString(R.string._COMMENTS) : getString(R.string._COMMENT))
                    + ", " + album.getViewCountInt() + (album.getViewCountInt() != 1 ? getString(R.string._VIEWS) : getString(R.string._VIEW))
                    + ", " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? getString(R.string._FAVORITES) : getString(R.string._FAVORITE))
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
                    showShareDialog(result.getEvent().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getEvent().getOptions(), vItem, 10);
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
            if (null != result && null != result.getEvent() && result.getEvent().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getEvent().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getEvent().getOptions() != null) {
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
                opt = result.getEvent().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getEvent().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getEvent().getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditEventFragment.newInstance(Constant.FormType.EDIT_EVENT, map, Constant.URL_EDIT_EVENT)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.DASHBOARD:
                    openWebView(Constant.URL_EVENT_DASHBOARD + mEventId, opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getEvent().getShare());
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ModuleName.SES_EVENT + "_" + mEventId);
                    break;
                case "messagemembers":
                    openComposeActivity(null);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_EVENT_INVITE).show(fragmentManager, "social");
                    //  openInviteForm(map,Constant.URL_EVENT_INVITE );
                    break;

                /*case Constant.OptionType.LIKE_AS_PAGE:
                    // PageLike
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(false);
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                        new ApiController(Constant.URL_LIKE_AS_PAGE, map, context, this, Constant.Events.LIKE_AS_PAGE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;*/
               /* case Constant.OptionType.CREATE_ASSOCIATE_PAGE:
                    fetchFormData();
                    break;*/

                case Constant.OptionType.JOIN_SMOOTHBOX:
                    showJoinDialog(Constant.URL_EVENT_JOIN);
                    break;
                case Constant.OptionType.ACCEPT:
                    showJoinDialog(Constant.URL_EVENT_ACCEPT_INVITE);
                    break;
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                case Constant.OptionType.CANCEL:
                case Constant.OptionType.REJECT:
                    showJoinLeaveDialog(opt);
                    break;

                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getEvent().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_ID, mEventId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_EVENT_COVER : Constant.URL_UPLOAD_EVENT_PHOTO, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_COVER, result.getEvent().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_PHOTO, result.getEvent().getImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getEvent().getCoverImageUrl(), resourceType, result.getEvent().getCoverImageUrl());
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
                    new ApiController(url, map, context, ViewEventFragment.this, Constant.Events.REMOVE_PHOTO).execute();
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
        switch (opt.getName()) {
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_event);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_EVENT_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.REJECT:
                dialogMsg = getStrings(R.string.msg_invite_reject_event);
                buttonTxt = getStrings(R.string.reject_invite);
                url[0] = Constant.URL_EVENT_REJECT_INVITE;
                req[0] = REQ_JOIN;
                break;


            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_event);
                buttonTxt = getStrings(R.string.leave_event);
                url[0] = Constant.URL_EVENT_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership_event);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_EVENT_JOIN;
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

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(req[0], url[0], -1);
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
                    addToCalendar(context, result.getEvent());
                    // CalendarEventHelper.addToCalenderr(context, result.getEvent());
                    // Util.showSnackbar(v,getStrings(R.string.msg_added_to_calendar));
                    break;
                case R.id.bSave:
                    //call save/unsave API
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                        //send contentId only if event is saved
                        if (result.getEvent().isContentSaved()) {
                            map.put("contentId", result.getEvent().getSaveId());
                        }
                        result.getEvent().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getEvent().isContentSaved() ? R.string.unsave_event : R.string.save_event);
                        new ApiController(Constant.URL_SAVE_EVENT, map, context, this, REQ_SAVE).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case R.id.bContact:
                    // super.openPageContactForm(result.getEvent().getOwnerId());
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getEvent().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    //   if (null != result.getEvent().getUpdateCoverPhoto())
                    showPopup(result.getEvent().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
               /* case R.id.like_heart:
                    callReactionApi(AppConstantSes.URL_LIKE + mEventId, view);
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
                    callReactionApi(AppConstantSes.URL_APPRECIATE + mEventId, v);
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_EVENT));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(REQ_DELETE, Constant.URL_DELETE_EVENT, -1);
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

    private void callDeleteApi(final int REQ, String url, int rsvp) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false);
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_EVENT_ID, mEventId);
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
                                        String message = new JSONObject(response).optJSONObject("result").optString("message");
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


     /*    case Constant.TabOption.INFO:
            adapter.addFragment(EventInfoFragment.newInstance(mEventId), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
            adapter.addFragment(FeedFragment.newInstance(mEventId, Constant.ResourceType.SES_EVENT), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
    HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.VIDEO:
                        map = new HashMap<>();
                        map.put(Constant.KEY_PARENT_ID, mEventId);
                        map.put("parent_type", Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(EventVideoFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.DISCUSSIONS:
    map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_DISCUSSION);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(DiscussionFragment.newInstance(map), opt.getLabel());
                        break;
                    case Constant.TabOption.REVIEWS:
    map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_REVIEWS);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(ReviewFragment.newInstance(map, null), opt.getLabel());
                        break;
                    case Constant.TabOption.LOCATION:
    LocationActivity vo = result.getEvent().getLocationObject();
                        if (null != vo) {
        Bundle bundle = new Bundle();
        //  bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
        adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
    }
                        break;

                    case Constant.TabOption.MEMBERS:
    Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putInt(Constant.KEY_EVENT_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);

    map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(EventMemberFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
    map = new HashMap<>();
                        map.put(Constant.TEXT, result.getEvent().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;
                   *//* default:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());
                        break;*//*

}
            }*/

    private void callFragment(Object value) {
        Options opt= (Options) value;
        Intent intent2=null;
        Bundle bundle = new Bundle();
        HashMap<String, Object> map = new HashMap<>();
        switch (opt.getName()){
            case Constant.TabOption.INFO:


                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_EVENT_INFO);
                intent2.putExtra(Constant.KEY_ID, mEventId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;

            case Constant.TabOption.ALBUM:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_EVENT_ID, mEventId);
                map.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
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
                map.put(Constant.KEY_EVENT_ID, mEventId);
                map.put(Constant.KEY_URI, Constant.URL_EVENT_REVIEWS);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_REVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.LOCATION:
                bundle = new Bundle();
                LocationActivity vo = result.getEvent().getLocationObject();
                if (null != vo) {
                    //  bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                    bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                    bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                    bundle.putSerializable("MyClass", (Serializable) vo);


                    intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_EVENT_MAP);
                    intent2.putExtra(Constant.KEY_BUNDEL, bundle);

                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                }




                //   adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                break;
            case Constant.TabOption.OVERVIEW:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.TEXT, result.getEvent().getOverview());
                map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));

                bundle.putSerializable(Constant.POST_REQUEST, map);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_OVERVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;
            case Constant.TabOption.MEMBERS:

                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                bundle.putInt(Constant.KEY_EVENT_ID, mEventId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                map = new HashMap<>();
                map.put(Constant.KEY_EVENT_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.DISCUSSIONS:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_EVENT_ID, mEventId);
                map.put(Constant.KEY_URI, Constant.URL_EVENT_DISCUSSION);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_EVENT_DISCUSSION);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;

            case Constant.TabOption.VIDEO:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_PARENT_ID, mEventId);
                map.put("parent_type", Constant.ResourceType.SES_EVENT);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_EVENT_VIDEO);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PAGE, mPageId, this), opt.getLabel());

                break;


           /* case Constant.TabOption.POLL:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_POLL);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.ANNOUNCE:
                bundle = new Bundle();

                //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_URI, Constant.URL_PAGE_ANNOUNCE);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ANNOUNCE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.SERVICES:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_PAGE_SERVICES);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);// mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
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
                bundle.putString(Constant.KEY_URI, Constant.URL_PAGE_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                // adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;


            case Constant.TabOption.ASSOCIATE:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ASSOCIATE);
                intent2.putExtra(Constant.KEY_ID, mPageId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                adapter.addFragment(PageFragment.newInstance(PageFragment.TYPE_ASSOCIATE, mPageId), opt.getLabel());
                break;*/

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
                        result.getEvent().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getEvent().isContentSaved() ? R.string.unsave_event : R.string.save_event);
                    } else {
                        SuccessResponse suc = new Gson().fromJson("" + object2, SuccessResponse.class);
                        if (null != suc.getResult()) {
                            result.getEvent().setSaveId(suc.getResult().getSaved_id());
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
               /* case Constant.Events.POPUP_SOWN:
                    //  fabCreateMedia.setRotation(45);
                    break;
                case Constant.Events.POPUP_HIDE:
                    // fabCreateMedia.setRotation(0);
                    this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
                    this.v.findViewById(R.id.fabCreateMedia).setVisibility(View.VISIBLE);
                    break;
                case Constant.Events.POPUP_ITEM:
                    this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
                    this.v.findViewById(R.id.fabCreateMedia).setVisibility(View.VISIBLE);
                    switch (position) {
                        case Constant.FormType.CREATE_ALBUM:
                            String url = AppConstantSes.URL_CREATE_ALBUM + mEventId;
                            Map<String, Object> map = new HashMap<>();
                            map.put("page_id", "" + mEventId);
                            fragmentManager.beginTransaction().replace(R.id.container_view, CreateAlbumForm.newInstance(Constant.FormType.CREATE_ALBUM, map, url, context.getResources().getString(R.string.title_activity_create_new_album))).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_VIDEO:
                            url = AppConstantSes.URL_CREATE_VIDEO + "page_id/" + mEventId;

                            fragmentManager.beginTransaction().replace(R.id.container_view,
                                    CreateVideoForm.newInstance(Constant.FormType.CREATE_VIDEO, url)).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_MUSIC:
                            break;
                        case Constant.FormType.CREATE_NOTE:
                            url = AppConstantSes.URL_CREATE_WRITING + "page_id/" + mEventId;
                            super.openForm(Constant.FormType.CREATE_NOTE, null, url, context.getResources().getString(R.string.title_create_note));
                            break;
                    }
                    break;*/
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
                updateItemLikeFavorite(REQ_CODE, view, result.getEvent());
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mEventId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
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
                                            result.getEvent().setOptions(opt);

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
                                        updateItemLikeFavorite(REQ_CODE, view, result.getEvent());
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

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.colorPrimary : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        }

    }


    private void openLighbox(int photoId, String imageUrl, int albumId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_EVENT_ID, mEventId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        //map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

}
