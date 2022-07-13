

package com.sesolutions.ui.events;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.fragment.app.Fragment;
import androidx.core.app.ShareCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
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
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventListFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {

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

    public static ViewEventListFragment newInstance(int groupId) {
        ViewEventListFragment frag = new ViewEventListFragment();
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
                        result.getList().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getList().setCoverImageUrl(Constant.BASE_URL + activity.stringValue);
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
        v = inflater.inflate(R.layout.fragment_view_event_list, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        fontIcon = FontManager.getTypeface(context);

        callMusicAlbumApi(1);

        return v;
    }


    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_EVENT_LIST);
                request.params.put(Constant.KEY_LIST_ID, mEventId);
                request.params.put("menus", 1);
                // request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);

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
                                    initUI();
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


   /* @Override
    public void onBackPressed() {

        activity.finish();
    }*/

    private void initUI() {
        try {
            v.findViewById(R.id.cl).setVisibility(View.VISIBLE);
            // getActivity().invalidateOptionsMenu();
            initCollapsingToolbar();
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
        if (result.getList() != null) {
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            CommonVO resp = result.getList();
            ((TextView) v.findViewById(R.id.tvPageTitle)).setText(resp.getTitle());


            ((TextView) v.findViewById(R.id.tvStats)).setText(getDetail(resp));
            if (resp.getUpdateCoverPhoto() != null) {
                v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera).setOnClickListener(this);
            }
            if (resp.getUpdateProfilePhoto() != null) {
                v.findViewById(R.id.ivCamera2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCamera2).setOnClickListener(this);
            }
            // ImageView ivCoverFoto = v.findViewById(R.id.ivCoverPhoto);
            //  ImageView ivPageImage = v.findViewById(R.id.ivPageImage);

            updateCoverPhoto(resp.getImageUrl());
            updateProfilePhoto(resp.getOwnerImageUrl());
            //Util.showImageWithGlide(ivCoverFoto, resp.getCoverImageUrl(), context, 1);

            // Util.showImageWithGlide(ivPageImage, resp.getMainImageUrl(), context, 1);


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

    //set tab bar items
    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        if (result.getMenus() != null) {


            setupViewPager();
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.setupWithViewPager(viewPager, true);
            applyTabListener();
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
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            List<Options> list = result.getMenus();
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {

                   /* case Constant.TabOption.INFO:

                        adapter.addFragment(EventInfoFragment.newInstance(mEventId), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
                        adapter.addFragment(ProfileUpdatesFragment.newInstance(mEventId, Constant.ResourceType.SES_EVENT), opt.getLabel());
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
                        // map.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM);
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
                    case Constant.TabOption.MAP:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_EVENT_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
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
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getList().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map), opt.getLabel());
                        break;*/
                    default:
                        adapter.addFragment(UpcomingEventFragment.newInstance(opt.getName(), mEventId), opt.getLabel());
                        break;

 /*           case "about":
                fragment = AboutFragment.newInstance(mEventId);
                break;

            case "creativemedia":
                fragment = CreativeMediaFragment.newInstance(mEventId);
                break;


            case "sponsors":
                fragment = SponsersFragment.newInstance(mEventId);
                break;*/
                }
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
                    /*if (tab.getPosition() == 0) {
                        ((VideoHelper) adapter.getItem(tab.getPosition())).scrollToStart();
                    }*/
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

    private CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        // collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
        //toolbar.setTitle(" ");
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
                        collapsingToolbar.setTitle(result.getList().getTitle());
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

   /* private void addUpperTabItems() {

        //add post item
        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);
        View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        ((TextView) view.findViewById(R.id.tvOptionText)).setText(getString(R.string.post));
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.post));
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
        ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposerOption composerOption = SPref.getInstance().getComposerOptions(context);
                if (null != composerOption) {
                    goToPostFeed(composerOption, -1, mEventId, Constant.ResourceType.SES_EVENT);
                }
            }
        });
        llTabOptions.addView(view);

        //add Like item
        final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        ((TextView) view1.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
        ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(result.getList().isContentFavourite() ? Color.parseColor(Constant.red) : color);
        ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLikeApi(REQ_FAVORITE, view1, Constant.URL_PAGE_FAVORITE);
            }
        });
        llTabOptions.addView(view1);

        //add Follow item
        final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        ((TextView) view2.findViewById(R.id.tvOptionText)).setText(getString(R.string.follow));
        ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow));
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(result.getList().isContentFollow() ? Color.parseColor(Constant.red) : color);
        ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLikeApi(REQ_FOLLOW, view2, Constant.URL_EVENT_FOLLOW);
            }
        });
        llTabOptions.addView(view2);

        //check permission and add JOIN item
        Options opt = result.getList().getFirstJoinOption();
        if (null != opt) {
            View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(opt.getLabel());
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.add_create));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showJoinLeaveDialog(result.getList().getFirstJoinOption());
                }
            });

            llTabOptions.addView(view3);
        }

    }*/


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
            detail += album.getLikeCount() + (album.getLikeCount() != 1 ? getStrings(R.string._LIKES) : getStrings(R.string._LIKE))
                    + ", " + album.getCommentCount() + (album.getCommentCount() != 1 ? getStrings(R.string._COMMENTS) : getStrings(R.string._COMMENT))
                    + ", " + album.getViewCountInt() + (album.getViewCountInt() != 1 ? getStrings(R.string._VIEWS) : getStrings(R.string._VIEW))
                    + ", " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? getStrings(R.string._FAVORITES) : getStrings(R.string._FAVORITE))
                    + ", " + album.getFollowCount() + (album.getFollowCount() != 1 ? getStrings(R.string._followers) : getStrings(R.string._follower))
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
                    showShareDialog(result.getList().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getList().getOptions(), vItem, 10);
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
            if (null != result && null != result.getList() && result.getList().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getList().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getList().getOptions() != null) {
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
            Options opt = null;
            boolean isCover = false;
            int itemId = item.getItemId();
            if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getList().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getList().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getList().getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                case "editlist":
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_LIST_ID, mEventId);
                    openFormFragment(Constant.FormType.EDIT_EVENT_LIST, map, Constant.URL_EDIT_EVENT_LIST);
                    //fragmentManager.beginTransaction().replace(R.id.container, CreateEditPageFragment.newInstance(Constant.FormType.EDIT_EVENT, map, Constant.URL_EDIT_EVENT, null)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.DELETE:
                case "deletelist":
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getList().getShare());
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ResourceType.SES_EVENT_LIST + "_" + mEventId);
                    break;

               /* case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    openInviteForm(map, Constant.URL_EVENT_INVITE);
                    break;*/


                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getList().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
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
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_COVER, result.getList().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_PHOTO, result.getList().getImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getList().getCoverImageUrl(), resourceType, result.getList().getCoverImageUrl());
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
                    new ApiController(url, map, context, ViewEventListFragment.this, Constant.Events.REMOVE_PHOTO).execute();
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
                case R.id.bSave:
                    //call save/unsave API
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_ID, mEventId);
                        map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                        //send contentId only if event is saved
                        if (result.getList().isContentSaved()) {
                            map.put("contentId", mEventId);
                        }
                        result.getList().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getList().isContentSaved() ? R.string.unsave_event : R.string.save_event);
                        new ApiController(Constant.URL_SAVE_EVENT, map, context, this, -1).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case R.id.bContact:
                    // super.openPageContactForm(result.getList().getOwnerId());
                    break;

                case R.id.ivCamera2:
                    showPopup(result.getList().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    //   if (null != result.getList().getUpdateCoverPhoto())
                    showPopup(result.getList().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_EVENT_LIST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(REQ_DELETE, Constant.URL_DELETE_EVENT_LIST, -1);
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

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIST_ID, mEventId);
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

        } catch (
                Exception e)

        {

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
                    //here position is event id
                    goToViewEventFragment(position);
                    break;
                case Constant.Events.IMAGE_1:
                    openLighbox(photoList.get(position).getPhotoId(), photoList.get(position).getImages().getNormal(), photoList.get(position).getAlbumId());
                    break;

                case -1:
                    ErrorResponse err = new Gson().fromJson("" + object2, ErrorResponse.class);
                    if (!err.isSuccess()) {
                        Util.showSnackbar(v, err.getMessage());
                        //in case of error ,revert save changes
                        result.getList().toggleSave();
                        ((AppCompatButton) v.findViewById(R.id.bSave)).setText(result.getList().isContentSaved() ? R.string.unsave_event : R.string.save_event);
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
