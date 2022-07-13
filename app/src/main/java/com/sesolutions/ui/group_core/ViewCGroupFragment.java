/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.sesolutions.ui.group_core;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
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
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Group;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCGroupFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {

    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Group resp;
    private int mGroupId;
   // private SwipeRefreshLayout swipeRefreshLayout;


    private AppBarLayout appBarLayout;

    public static ViewCGroupFragment newInstance(int groupId) {
        ViewCGroupFragment frag = new ViewCGroupFragment();
        frag.mGroupId = groupId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {

                case Constant.FormType.CREATE_DISCUSSTION:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    int pos = getTabPositionByName(Constant.TabOption.DISCUSSIONS);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.FormType.CREATE_GROUP_VIDEO:
                    activity.taskPerformed = 0;
                    goTo(Constant.GoTo.VIDEO, activity.taskId, Constant.ResourceType.EVENT_VIDEO);
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.TASK_ADD_MORE_PHOTO:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.PHOTOS);
                    //collapse app bar layout
                    appBarLayout.setExpanded(false, true);
                    //set album tab and refresh data
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();

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

    private int getTabPositionByName(String name) {
        int position = 0;
        for (int i = 0; i < resp.getProfileTabs().size(); i++) {
            if (resp.getProfileTabs().get(i).getName().equals(name)) {
                position = i;
                break;
            }
        }
        return position;
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
        v = inflater.inflate(R.layout.fragment_view_group, container, false);
        applyTheme(v);
        //swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setEnabled(false);

        callMusicAlbumApi(1);
        return v;
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_CGROUP);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_ID, mGroupId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {

                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    CommonResponse commonResponse = new Gson().fromJson(response, CommonResponse.class);
                                    if (commonResponse.getResult().getGroupContent() != null) {
                                        resp = commonResponse.getResult().getGroupContent();
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

                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void hideAllLoaders() {
        try {
           // swipeRefreshLayout.setRefreshing(false);
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
            setUpperUIData();
            initTablayout();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    //listener for gutter menu item click

    @Override
    public void onRefresh() {
       /* if (swipeRefreshLayout.isEnabled() && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }*/
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void setUpperUIData() {


        // mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);

        ((TextView) v.findViewById(R.id.tvGroupTitle)).setText(resp.getTitle());
        ((TextView) v.findViewById(R.id.tvMemberCount)).setText(resp.getMemberCount());

        ((TextView) v.findViewById(R.id.tvOwnerTitle)).setText(resp.getOwnerTitle());
        v.findViewById(R.id.tvOwnerTitle).setOnClickListener(this);
        ImageView ivCoverFoto = v.findViewById(R.id.ivCoverPhoto);
        ImageView ivOwnerImage = v.findViewById(R.id.ivOwnerImage);
        ImageView ivProfileImage = v.findViewById(R.id.ivProfileImage);

        Util.showImageWithGlide(ivCoverFoto, resp.getCoverPhoto(), context, R.drawable.placeholder_square);
        Util.showImageWithGlide(ivOwnerImage, resp.getOwnerPhoto(), context, R.drawable.default_user);
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


    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        if (resp.isProfileTabsValid()) {
            setupViewPager();
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
            List<Options> list = resp.getProfileTabs();
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {
                    case Constant.TabOption.INFO:
                        adapter.addFragment(CGroupInfoFragment.newInstance(resp.getGroupId(), false), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
                        adapter.addFragment(FeedFragment.newInstance(resp.getGroupId(), Constant.ResourceType.CORE_GROUP), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_CGROUP_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CORE_GROUP);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(CGroupGuestFragment.newInstance(bundle), opt.getLabel());
                        // adapter.addFragment(CGroupMemberFragment.newInstance(resp.getGroupId()), opt.getLabel());
                        break;
                    case Constant.TabOption.DISCUSSIONS:
                        map = new HashMap<>();
                        map.put(Constant.KEY_ID, mGroupId);
                        map.put(Constant.KEY_URI, Constant.URL_CGROUP_DISCUSSION);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CORE_GROUP);
                        adapter.addFragment(CoreGroupDiscussionFragment.newInstance(map), opt.getLabel());
                        break;

                    case /*"photos":*/ Constant.TabOption.PHOTOS:
                        adapter.addFragment(CGroupPhotoFragment.newInstance(this, mGroupId, opt.getName()), opt.getLabel());
                        break;

 /*           case "about":
                fragment = AboutFragment.newInstance(mPageId);
                break;

            case "creativemedia":
                fragment = CreativeMediaFragment.newInstance(mPageId);
                break;


            case "sponsors":
                fragment = SponsersFragment.newInstance(mPageId);
                break;*/
                }
            }

            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(5);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void applyTabListener() {
        // tabLayout.removeOnTabSelectedListener(this);
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
        (adapter.getItem(position)).initScreenData();
    }

    private CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(SesColorUtils.getPrimaryColor(context));
        //toolbar.setTitle(" ");
        appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    //swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {

                        collapsingToolbar.setTitle(resp.getTitle());

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


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
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
                    showShareDialog(resp.getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(resp.getGutterMenu(), vItem, 10);
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
            if (null != resp && resp.getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, resp.getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != resp && resp.getGutterMenu() != null) {
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
        JSONArray menus;
        int itemId = item.getItemId();
        if (itemId > 1000) {
            itemId = itemId - 1000;
        } else if (itemId > 100) {
            itemId = itemId - 100;
        } else {
            itemId = itemId - 10;
        }

        Options opt = resp.getGutterMenu().get(itemId - 1);
        switch (opt.getParamsAction()) {
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_MODULE, Constant.ModuleName.CORE_GROUP);
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.EDIT_GROUP, map, Constant.BASE_URL + Constant.URL_EDIT_CGROUP + resp.getGroupId() + Constant.POST_URL, resp.getGroupId())).addToBackStack(null).commit();
                break;
            case Constant.OptionType.DELETE:
                showDeleteDialog();
                break;
            case Constant.OptionType.REPORT:
            case Constant.OptionType.REPORT_SMOOTHBOX:
                goToReportFragment(Constant.ModuleName.CORE_GROUP + "_" + resp.getGroupId());
                break;

            case Constant.OptionType.JOIN_SMOOTHBOX:
            case Constant.OptionType.LEAVE_SMOOTHBOX:
            case Constant.OptionType.REQUEST:
                showJoinLeaveDialog(opt.getParamsAction());
                break;

            case Constant.OptionType.INVITE:
                map = new HashMap<>();
                map.put(Constant.KEY_ID, mGroupId);
                InviteDialogFragment.newInstance(this, map, Constant.URL_CGROUP_INVITE).show(fragmentManager, "social");
                break;


              /*  case Constant.OptionType.CHANGE_PHOTO:
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_ARTICLE_PHOTO, album.getArticleImages().getMain(), Constant.TITLE_EDIT_ARTICLE_PHOTO);
                    break;*/
        }
        return false;
    }

    private void showJoinLeaveDialog(String optionType) {

        String dialogMsg = Constant.EMPTY;
        String buttonTxt = Constant.EMPTY;
        final String[] url = {Constant.EMPTY};
        switch (optionType) {
            case Constant.OptionType.JOIN_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_join_group);
                buttonTxt = getStrings(R.string.join_group);
                url[0] = Constant.URL_CGROUP_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_group);
                buttonTxt = getStrings(R.string.leave_group);
                url[0] = Constant.URL_CGROUP_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_CGROUP_REQUEST;
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
                    callDeleteApi(url[0]);

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
        switch (view.getId()) {
           /* case R.id.like_heart:
                callReactionApi(AppConstantSes.URL_LIKE + mPageId, view);
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
                callReactionApi(AppConstantSes.URL_APPRECIATE + mPageId, v);
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
                callDeleteApi(Constant.URL_DELETE_CGROUP);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final String url) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + url /*+ resp.getGroupId()*/ + Constant.POST_URL);
                    request.params.put(Constant.KEY_ID, resp.getGroupId());
                    request.params.put(Constant.KEY_GROUP_ID, resp.getGroupId());

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
                                    CommonResponse res = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(res.getError())) {
                                        if (!url.contains("delete")) {
                                            Util.showSnackbar(v, res.getResult().getSuccessMessage());
                                            if (null != res.getResult().getGutterMenu()) {
                                                resp.setGutterMenu(res.getResult().getGutterMenu());
                                            }
                                        } else {
                                            activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                            onBackPressed();
                                        }

                                    } else {
                                        Util.showSnackbar(v, res.getErrorMessage());
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
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
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
            /*switch (object1) {
                case Constant.Events.POPUP_SOWN:
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
                            String url = AppConstantSes.URL_CREATE_ALBUM + mPageId;
                            Map<String, Object> map = new HashMap<>();
                            map.put("page_id", "" + mPageId);
                            fragmentManager.beginTransaction().replace(R.id.container_view, CreateAlbumForm.newInstance(Constant.FormType.CREATE_ALBUM, map, url, context.getResources().getString(R.string.title_activity_create_new_album))).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_VIDEO:
                            url = AppConstantSes.URL_CREATE_VIDEO + "page_id/" + mPageId;

                            fragmentManager.beginTransaction().replace(R.id.container_view,
                                    CreateVideoForm.newInstance(Constant.FormType.CREATE_VIDEO, url)).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_MUSIC:
                            break;
                        case Constant.FormType.CREATE_NOTE:
                            url = AppConstantSes.URL_CREATE_WRITING + "page_id/" + mPageId;
                            super.openForm(Constant.FormType.CREATE_NOTE, null, url, context.getResources().getString(R.string.title_create_note));
                            break;
                    }
                    break;
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }
}
