package com.sesolutions.ui.bookings;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Bookings.ProfessionalContent;
import com.sesolutions.responses.Bookings.ProfessionalResponse;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Courses.classroom.ClassroomContent;
import com.sesolutions.responses.Courses.classroom.ClassroomResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.PageLikeDialogFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.video.FollowerFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ViewProfessionalFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, TabLayout.OnTabSelectedListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object> {

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

    private ProfessionalResponse.Result result;
    private int mclassroomId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<ClassroomContent> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewProfessionalFragment newInstance(int pageId) {
        ViewProfessionalFragment frag = new ViewProfessionalFragment();
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
                        result.getProfessional().getImages().setMain(activity.stringValue);
                        updateProfilePhoto(activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getProfessional().getCoverImage().setMain(activity.stringValue);
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
                    Util.showSnackbar(v, "Your review has been successfully edited.");
                    break;
                case Constant.FormType.CREATE_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    goToViewReviewFragment(Constant.ResourceType.PROFESSIONAL_REVIEW, activity.taskId);
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_PRO));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_SERVICE_DELETE);
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
                    request.params.put(Constant.KEY_SERVICE_ID, mclassroomId);
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
                                            Util.showSnackbar(v, "Your service has been successfully deleted.");
                                            activity.taskPerformed = Constant.TASK_DELETE_SERVICE;
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
        v = inflater.inflate(R.layout.fragment_view_professional, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
        callMusicAlbumApi(1);

        return v;
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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_PROFESSIONAL_VIEW);
                    request.params.put(Constant.KEY_PROFESSIONAL_ID, mclassroomId);
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
                                        ProfessionalResponse commonResponse = new Gson().fromJson(response, ProfessionalResponse.class);
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

    private void initTablayout() {
        tabLayout = v.findViewById(R.id.tabs);
        //create a boolean array that can be used in preventing multiple loading of any tab
        isLoaded = new boolean[2];
        setupViewPager();
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        new Handler().postDelayed(() -> loadScreenData(0), 200);
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

    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            Bundle bundle = new Bundle();
            HashMap<String, Object> map = new HashMap<>();
            map = new HashMap<>();
            map.put(Constant.KEY_PROFESSIONAL_ID, mclassroomId);
            map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PROFESSIONAL_REVIEW);
            adapter.addFragment(PageProfileReviewFragment.newInstance("professional_review", this, map, false), "Reviews");
//            adapter.addFragment(ServiceFragment.newInstance("profile_services", mclassroomId), "Services");
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(isLoaded.length);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initUI() {
        try {
            String[] color = {"#A0D5EE", "#EEA0A0", "#B8A0EE",
                    "#6987C9", "#9CEFA5", "#E1E38F", "#E3A38F",
                    "#B4B66D", "#9FB66D", "#EC68E8", "#68ECE4",
                    "#00a8b5", "#0b8457", "#113f67",
                    "#005792", "#c82121", "#930077"};
            Random r = new Random();
            int randomNumber = r.nextInt(color.length);
            v.findViewById(R.id.cl).setVisibility(View.VISIBLE);
            initCollapsingToolbar();
            setUpperUIData();
            initTablayout();

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
                        collapsingToolbar.setTitle(result.getProfessional().getName());
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
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void setRatingStars() {
        ProfessionalContent resp = result.getProfessional();
        v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
        v.findViewById(R.id.llStar).setOnClickListener(v -> {
            appBarLayout.setExpanded(false, true);
            viewPager.setCurrentItem(0);
        });
        Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
        float rating = resp.getIntRating();//.getTotalRatingAverage();
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

    private void setUpInfo() {
        ProfessionalContent resp = result.getProfessional();
        if (resp.getDescription() != null) {
            ((LinearLayoutCompat) v.findViewById(R.id.llDescription)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvInfo)).setText("About");
//            ((TextView) v.findViewById(R.id.tvInfo)).setPaintFlags(((TextView) v.findViewById(R.id.tvInfo)).getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            ((TextView) v.findViewById(R.id.tvQDescription)).setText(SpanUtil.getHtmlString(resp.getDescription()));
        } else {
            ((LinearLayoutCompat) v.findViewById(R.id.llDescription)).setVisibility(View.GONE);

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
                        showShareDialog(result.getShare());
                    } else {
                        shareOutside(result.getShare());
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
            if (null != result && null != result.getProfessional() && result.getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getShare().getLabel())
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
                opt = result.getProfessional().getButtons().get(itemId - 1);
            } else if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getProfessional().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getProfessional().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getOptions().get(itemId - 1);
            }
            switch (opt.getName()) {

                case Constant.OptionType.DASHBOARD:
                    Map<String, Object> map = new HashMap<>();
                    openWebView(opt.getValue(), opt.getLabel());
                    break;

                case Constant.OptionType.SHARE:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        showShareDialog(result.getShare());
                    } else {
                        shareOutside(result.getShare());
                    }
                    break;

                case Constant.OptionType.REPORT:
                case Constant.OptionType.REPORT_SMOOTHBOX:
                    goToReportFragment(Constant.ResourceType.PROFESSIONAL + "_" + mclassroomId);
                    break;

                case Constant.OptionType.INVITE:
                    map = new HashMap<>();
                    map.put(Constant.KEY_CLASSROOM_ID, mclassroomId);
                    //openInviteForm(map, Constant.URL_PAGE_INVITE);
                    InviteDialogFragment.newInstance(this, map, Constant.URL_CLASSROOM_INVITE).show(fragmentManager, "social");
                    break;

                case Constant.OptionType.CREATE_ASSOCIATE_PAGE:
                    fetchFormData();
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
                        goToUploadAlbumImage(Constant.URL_CLASSROOM_UPLOAD_COVER, result.getProfessional().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_CLASSROOM_UPLOAD_MAIN, result.getProfessional().getMainImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.EDIT:
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put(Constant.KEY_SERVICE_ID, mclassroomId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditReview.newInstance(Constant.FormType.EDIT_SERVICE, map1, Constant.URL_SERVICE_EDIT, null, true)).addToBackStack(null).commit();
                    break;
                case Constant.OptionType.view_cover_photo:
                    break;

                case "contact":
                    onClick(v.findViewById(R.id.bContact));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
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
                case R.id.cvContact:
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + result.getProfessional().getCountrycode() + result.getProfessional().getPhone_number()));
                    startActivity(intent);
                    break;
                case R.id.cvServices:
//                    fragmentManager.beginTransaction().replace(R.id.container, ServiceFragment.newInstance("profile_services", mclassroomId)).addToBackStack(null).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container
                                    , ProfileServices.newInstance("profile_services", mclassroomId))
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.cvBook:
                    openWebView(result.getProfessional().getBookUrl() + "?removeSiteHeaderFooter=true", "Book");
                    break;
                case R.id.bContact:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        super.openClassroomContactForm(result.getProfessional().getOwner_id());
                    } else {
                      //  fragmentManager.beginTransaction().add(R.id.container, new SignInFragment()).addToBackStack(null).commit();
                        fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.ivCamera2:
                    showPopup(result.getProfessional().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    if (null != result.getProfessional().getUpdateCoverPhoto())
                        showPopup(result.getProfessional().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        try {
            CustomLog.e("POPUP", "" + object2 + "  " + object2 + "  " + position);
            switch (object1) {
                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    openViewPageFragment(position);
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
                updateItemLikeFavorite(REQ_CODE, view, result.getProfessional(), showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_PROFESSIONAL_ID, mclassroomId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.PROFESSIONAL);
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
                                        if (REQ_CODE == REQ_LIKE) {
                                            JSONObject json = new JSONObject(response);
                                            int count = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("data").getInt("like_count");
                                            updateLikeCount(count);
                                        }
                                        if (REQ_CODE == REQ_FAVORITE) {
                                            JSONObject json = new JSONObject(response);
                                            int count2 = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("data").getInt("favourite_count");
                                            updateFavoriteCount(count2);
                                        }
                                        if (REQ_CODE == REQ_FOLLOW) {
                                            JSONObject json = new JSONObject(response);
                                            int count3 = json.getJSONObject(Constant.KEY_RESULT).getJSONObject("data").getInt("follow_count");
                                            updatefollowcount(count3);
                                        }
                                        if (REQ_CODE > REQ_DELETE && REQ_CODE != REQ_JOIN) {
                                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                            List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                            result.getProfessional().setButtons(opt);
                                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                        }
                                        if (REQ_CODE == REQ_JOIN) {
                                            ClassroomResponse op = new Gson().fromJson(response, ClassroomResponse.class);
                                            Util.showSnackbar(v, op.getResult().getMessage());
                                        }
                                    } else {
                                        //revert changes in case of error
//                                        updateLikeCount();
                                        updateItemLikeFavorite(REQ_CODE, view, result.getProfessional(), false);
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

    public void updateLikeCount(int count) {
        ((TextView) v.findViewById(R.id.tvLikeCount)).setText("" + count);
    }

    public void updateFavoriteCount(int count) {
        ((TextView) v.findViewById(R.id.tvCommentUpper)).setText("" + count);
    }

    public void updatefollowcount(int count) {
        ((TextView) v.findViewById(R.id.tvFollow)).setText("" + count);
    }

    public void setUpperUIData() {

        mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        if (result.getProfessional() != null) {
            int color = Color.parseColor(Constant.text_color_1);
            CustomLog.e("logged in user id: ", "" + SPref.getInstance().getLoggedInUserId(context));

//            ((LinearLayoutCompat) v.findViewById(R.id.llDetail)).setVisibility(View.VISIBLE);
            ((MaterialCardView) v.findViewById(R.id.cvContact)).setOnClickListener(this);
            ((MaterialCardView) v.findViewById(R.id.cvServices)).setOnClickListener(this);

            ((CardView) v.findViewById(R.id.resultCard)).setVisibility(View.VISIBLE);
            ProfessionalContent resp = result.getProfessional();
            if(resp.getAvailable() == 0){
                ((MaterialCardView) v.findViewById(R.id.cvBook)).setVisibility(View.GONE);
            }
            if(resp.getAvailable()>0){
                ((MaterialCardView) v.findViewById(R.id.cvBook)).setOnClickListener(this);
                ((MaterialCardView) v.findViewById(R.id.cvBook)).setVisibility(View.VISIBLE);
            }
            CustomLog.e("title", "" + resp.getName());
            ((TextView) v.findViewById(R.id.tvProfessional)).setText(resp.getName());
            ((TextView) v.findViewById(R.id.tvProfession)).setText(resp.getDesignation());
//            ((TextView) v.findViewById(R.id.ivBook)).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvLikeCount)).setText("" + resp.getLike_count());
            ((TextView) v.findViewById(R.id.tvCommentUpper)).setText("" + resp.getFavourite_count());
            ((TextView) v.findViewById(R.id.tvFollow)).setText("" + resp.getFollow_count());
            ((TextView) v.findViewById(R.id.tvLocation)).setText(resp.getLocation());
//            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivPro), resp.getProfessional_image(), context, R.drawable.placeholder_square);
            ((ImageView) v.findViewById(R.id.ivImage)).setVisibility(View.VISIBLE);
            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), resp.getProfessional_image(), context, R.drawable.placeholder_square);
            if (resp.canLike()) {
                ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(resp.isContentLike() ? Color.parseColor(Constant.followBlue) : color);
                ((ImageView) v.findViewById(R.id.ivImageLike)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like));
                ((ImageView) v.findViewById(R.id.ivImageLike)).setOnClickListener(v -> {
                    callLikeApi(REQ_LIKE, ((ImageView) v.findViewById(R.id.ivImageLike)), Constant.URL_PROFESSIONAL_LIKE, true);
                });
            }
            if (resp.canFavourite()) {
                ((ImageView) v.findViewById(R.id.tvImageFavorite)).setColorFilter(resp.isContentFavourite() ? Color.parseColor(Constant.red) : color);
                ((ImageView) v.findViewById(R.id.tvImageFavorite)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                ((ImageView) v.findViewById(R.id.tvImageFavorite)).setOnClickListener(v -> {
                    callLikeApi(REQ_FAVORITE, ((ImageView) v.findViewById(R.id.tvImageFavorite)), Constant.URL_PROFESSIONAL_FAVORITE, true);
                });
            }
            if (mUserId != result.getProfessional().getOwner_id() && result.getProfessional().canFollow()) {
                ((ImageView) v.findViewById(R.id.ivFollow)).setColorFilter(resp.isContentFollow() ? Color.parseColor(Constant.followBlue) : color);
                ((ImageView) v.findViewById(R.id.ivFollow)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow));
                ((ImageView) v.findViewById(R.id.ivFollow)).setOnClickListener(v -> {
                    callLikeApi(REQ_FOLLOW, ((ImageView) v.findViewById(R.id.ivFollow)), Constant.URL_PROFESSIONAL_FOLLOW, true);
                });
            }
            setRatingStars();
            setUpInfo();
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, View view, ProfessionalContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                if (vo.isContentLike()) {
                    ((ImageView) view.findViewById(R.id.ivImageLike)).setColorFilter(ContextCompat.getColor(context, R.color.follow_blue), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) view.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(Constant.text_color_1));
                }

        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            if (vo.isContentFavourite()) {
                ((ImageView) view.findViewById(R.id.tvImageFavorite)).setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                ((ImageView) view.findViewById(R.id.tvImageFavorite)).setColorFilter(Color.parseColor(Constant.text_color_1));
            }
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            //((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, vo.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            if (vo.isContentFollow()) {
                ((ImageView) view.findViewById(R.id.ivFollow)).setColorFilter(ContextCompat.getColor(context, R.color.follow_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                ((ImageView) view.findViewById(R.id.ivFollow)).setColorFilter(Color.parseColor(Constant.text_color_1));
            }
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


}
