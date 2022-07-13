

package com.sesolutions.ui.crowdfunding;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCrowdFragment extends BaseFragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object>, RatingBar.OnRatingBarChangeListener {

    private static final int REQ_UPDATE_UPPER = 99;
    private static final int REQ_DONATE = 501;
    private final int REQ_LIKE = 100;
    private final int REQ_DELETE = 400;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View v;

    private FundResponse.Result result;
    private int mPageId;

    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<FundContent> relatedList;
    private List<Albums> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewCrowdFragment newInstance(int pageId) {
        ViewCrowdFragment frag = new ViewCrowdFragment();
        frag.mPageId = pageId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getCampaign().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getCampaign().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }
                    activity.taskPerformed = 0;
                    break;

                case Constant.FormType.EDIT_FUND:
                    activity.taskPerformed = 0;
                    swipeRefreshLayout.setRefreshing(true);
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    break;
                case Constant.TASK_ADD_MORE_PHOTO:
                    activity.taskPerformed = 0;
                    //collapse app bar layout
                    appBarLayout.setExpanded(false, true);
                    //set album tab and refresh data
                    // viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(getTabPositionByName(Constant.TabOption.DESCRIPTION))).onRefresh();
                    break;
                case Constant.FormType.EDIT_ANNOUNCEMENT:
                case Constant.FormType.CREATE_ANNOUNCEMENT:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    adapter.getItem(getTabPositionByName(Constant.TabOption.ANNOUNCE)).onRefresh();
                    break;

                case Constant.TASK_ALBUM_DELETED:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                   /* pos = getTabPositionByName(Constant.TabOption.POLL);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    openViewPollFragment(MenuTab.Page.TYPE_PROFILE_POLL, activity.taskId);*/
                    break;
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
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivCoverPhoto), url, context, 1);
    }

    private void updateProfilePhoto(String url) {
        Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivPageImage), url, context, 1);
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
        v = inflater.inflate(R.layout.fragment_view_crowd, container, false);
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
        //photoList.add(new Albums(result.getCampaign().getImages()));
        rvPhotos.setHasFixedSize(true);
        //final LinearLayoutManager layoutManager       = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        //rvPhotos.setLayoutManager(layoutManager);
        adapterPhoto = new PagePhotoAdapter(photoList, context, this, true);
        rvPhotos.setAdapter(adapterPhoto);
        v.findViewById(R.id.pageIndicatorView).setVisibility(photoList.size() > 1 ? View.VISIBLE : View.GONE);
    }

  /*    private SuggestionPageAdapter adapterRelated;

  private void initRelatedPageUI() {
        MultiSnapRecyclerView rvPhotos = v.findViewById(R.id.rvRecent);
        relatedList = new ArrayList<FundContent>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        adapterRelated = new SuggestionPageAdapter(relatedList, context, this, true);
        rvPhotos.setAdapter(adapterRelated);
    }*/


    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req == 1) {
                        showBaseLoader(true);
                    } else if (req == REQ_UPDATE_UPPER) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FUND_VIEW);
                    request.params.put(Constant.KEY_FUND_ID, mPageId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.FUND);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideAllLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    FundResponse commonResponse = new Gson().fromJson(response, FundResponse.class);
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
            //  initRelatedPageUI();
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
        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        try {
            if (result.getCampaign() != null) {
                //v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
                v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
                FundContent vo = result.getCampaign();

                ((TextView) v.findViewById(R.id.ivOwner)).setTypeface(iconFont);
                ((TextView) v.findViewById(R.id.ivOwner)).setText(Constant.FontIcon.USER);
                ((TextView) v.findViewById(R.id.tvStats)).setTypeface(iconFont);
                String detail = "\uf164 " + vo.getLike_count()
                        + "  \uf075 " + vo.getComment_count()
                        + "  \uf06e " + vo.getView_count();
                ((TextView) v.findViewById(R.id.tvStats)).setText(detail);
                ((TextView) v.findViewById(R.id.tvStatus)).setText(vo.getStatusLabel());
                ((TextView) v.findViewById(R.id.tvStatus)).setTextColor(SesColorUtils.getCrowdTextColor(context, vo.isExpired()));
                ((TextView) v.findViewById(R.id.tvRaised)).setText(vo.getGainAmount());
                ((TextView) v.findViewById(R.id.tvDonor)).setText(vo.getDonorCountStr());
                ((TextView) v.findViewById(R.id.tvGoal)).setText(vo.getTotalAmount());

                ProgressBar sbProgress = v.findViewById(R.id.sbProgress);
                sbProgress.setProgress(vo.getProgressPercent());
                ((TextView) v.findViewById(R.id.tvTitle)).setText(vo.getTitle());
                ((TextView) v.findViewById(R.id.tvOwnerName)).setText(context.getString(R.string.by_owner, vo.getOwnerTitle()));
                ((TextView) v.findViewById(R.id.ivCategory)).setTypeface(iconFont);
                ((TextView) v.findViewById(R.id.ivCategory)).setText(Constant.FontIcon.FOLDER);
                ((TextView) v.findViewById(R.id.tvCategoryName)).setText(context.getString(R.string.in_content, vo.getCategoryTitle()));


                if (vo.getUpdateCoverPhoto() != null) {
                    v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.ivCamera).setOnClickListener(this);
                }

                ((RatingBar) v.findViewById(R.id.rb)).setRating(vo.getRating());
                ((RatingBar) v.findViewById(R.id.rb)).setOnRatingBarChangeListener(this);
                ((TextView) v.findViewById(R.id.tvRatingCount)).setText(context.getResources().getQuantityString(R.plurals.rating_count, vo.getRatingCount(), vo.getRatingCount()));

                addUpperTabItems();
                updatePhotoAdapter();
                // updateRelatedPageAdapter();
            }
        } catch (Exception e) {
            CustomLog.e(e);
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


    List<Options> tabMenus;

    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            List<Options> list = result.getMenus();
            tabMenus = new ArrayList<>();

            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {
                    case Constant.TabOption.ABOUT:
                        tabMenus.add(opt);
                        Bundle bundle = new Bundle();
                        adapter.addFragment(FundInfoFragment.newInstance(mPageId), opt.getLabel());
                        break;
                    case Constant.TabOption.DESCRIPTION:
                        tabMenus.add(opt);
                        adapter.addFragment(FundDescriptionFragment.newInstance(mPageId), opt.getLabel());
                        break;

                    case Constant.TabOption.UPDATES:
                        tabMenus.add(opt);
                        adapter.addFragment(FeedFragment.newInstance(mPageId, Constant.ResourceType.FUND), opt.getLabel());
                        break;

                   /* case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_FUND_ID, mPageId);
                        map.put(Constant.KEY_URI, Constant.URL_FUND_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.FUND);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;*/

                    case Constant.TabOption.MAP:
                        tabMenus.add(opt);
                        bundle = new Bundle();
                        // bundle.putString(Constant.KEY_URI, Constant.URL_FUND_MAP);
                        //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_FUND_ID, mPageId);
                        map.put(Constant.KEY_DATA, new Gson().toJson(result.getCampaign().getLocation()));
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.FUND);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        tabMenus.add(opt);
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getCampaign().getOverview());
                        map.put(Constant.KEY_FILTER, SPref.getInstance().isUserOwner(context, result.getCampaign().getOwner_id()));
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, this), opt.getLabel());
                        break;

                    case Constant.TabOption.ANNOUNCE:
                        tabMenus.add(opt);
                        bundle = new Bundle();

                        bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.FUND);
                        map = new HashMap<>();
                        map.put(Constant.KEY_FUND_ID, mPageId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_FUND_ANNOUNCEMENT);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;


                    case Constant.TabOption.DONORS:
                        tabMenus.add(opt);
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_FUND_DONOR);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.FUND);
                        map = new HashMap<>();
                        map.put(Constant.KEY_FUND_ID, mPageId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(FundDonorFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.REWARDS:
                        tabMenus.add(opt);
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_FUND_REWARD);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.FUND);
                        map = new HashMap<>();
                        map.put(Constant.KEY_FUND_ID, mPageId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(FundRewardFragment.newInstance(bundle), opt.getLabel());
                        break;
                    default:
                        CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Lable: " + opt.getLabel());
                        /*bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());*/
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
                    //  swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getCampaign().getTitle());
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
        LinearLayoutCompat view1 = v.findViewById(R.id.llTabOptions);
        //  llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        //  final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        //  final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        //add favourite item
        if (result.getCampaign().canLike()) {

            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(result.getCampaign().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getCampaign().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.findViewById(R.id.vMain).setOnClickListener(v -> {
                callLikeApi(REQ_LIKE, view1, Constant.URL_FUND_LIKE, true);
            });
            //llTabOptions.addView(view1);
        } else {
            view1.findViewById(R.id.vMain).setVisibility(View.GONE);
        }

        //add more item
        Options opt = result.getCampaign().getButton();
        if (null != opt && "donate".equals(opt.getName())) {
            view1.findViewById(R.id.llDonate).setVisibility(View.VISIBLE);
            //final View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view1.findViewById(R.id.tvDonate)).setText(result.getCampaign().getButton().getLabel());
            // ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_donate));
            // ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(SesColorUtils.getPrimaryColor(context));
            //((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
            // view.setOnClickListener(v -> showPopup(result.getCampaign().getButton(), view, 2000));
            // llTabOptions.addView(view);
            view1.findViewById(R.id.llDonate).setOnClickListener(this);
        } else {
            view1.findViewById(R.id.llDonate).setVisibility(View.GONE);
        }
    }


    private void updatePhotoAdapter() {
        try {
            photoList.clear();
            //  if (result.getPhoto() != null && result.getPhoto().size() > 0) {
            //v.findViewById(R.id.rlPhotos).setVisibility(View.VISIBLE);
            photoList.add(new Albums(result.getCampaign().getImages()));
            //photoList.addAll(result.getPhoto());
            adapterPhoto.notifyDataSetChanged();
            //   } else {
            // v.findViewById(R.id.rlPhotos).setVisibility(View.GONE);
            //   }
            //  adapterPhoto.notifyDataSetChanged();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* private void updateRelatedPageAdapter() {
        try {
            relatedList.clear();
            if (result.getRelatedPages() != null && result.getRelatedPages().size() > 0) {
                v.findViewById(R.id.rlRecent).setVisibility(View.VISIBLE);
                relatedList.addAll(result.getRelatedPages());
            } else {
                v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
            }
            adapterRelated.notifyDataSetChanged();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    private void performAboutOptionClick(Options opt) {
        switch (opt.getName()) {
            case Constant.OptionType.CATEGORY:
                openViewCategory();
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
                    showShareDialog(result.getCampaign().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getButtons(), vItem, 10);
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
            if (null != result && null != result.getCampaign() && result.getCampaign().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getCampaign().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getButtons() != null) {
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
            int itemId = item.getItemId();
            if (itemId > 2000) {
                itemId = itemId - 2000;
                opt = result.getButtons().get(itemId - 1);
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getCampaign().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getButtons().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    CrowdUtil.openEditFragment(fragmentManager, mPageId);
                    break;
                case "seo":
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_FUND_ID, mPageId);
                    openFormFragment(Constant.FormType.SEO, map, Constant.URL_FUND_SEO);
                    break;
                case "updatecontactinfo":
                    map = new HashMap<>();
                    map.put(Constant.KEY_FUND_ID, mPageId);
                    openFormFragment(Constant.FormType.EDIT_CONTACT, map, Constant.URL_FUND_CONTACT);
                    break;

                case Constant.OptionType.DASHBOARD:
                    openWebView(opt.getValue(), opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getCampaign().getShare());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.FUND + "_" + mPageId);
                    break;


                case Constant.OptionType.view_profile_photo:
                    //  goToGalleryFragment(result.getCampaign().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;

                case Constant.OptionType.UPLOAD:
                   /* map = new HashMap<>();
                    map.put(Constant.KEY_FUND_ID, mPageId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_FUND_COVER, result.getCampaign().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_FUND_PHOTO, result.getCampaign().getMainImageUrl(), opt.getLabel(), map);
                    }*/
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getCampaign().getCoverImageUrl(), resourceType, result.getCampaign().getCoverImageUrl());
                    break;

                case Constant.OptionType.remove_photo:
                    // showImageRemoveDialog(isCover);
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

    /*//TODO same method is on ProfileFragment
    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_REMOVE_FUND_COVER : Constant.URL_REMOVE_FUND_PHOTO;
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

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_FUND_ID, mPageId);
                map.put(Constant.KEY_TYPE, Constant.ResourceType.FUND);
                new ApiController(url, map, context, ViewCrowdFragment.this, Constant.Events.REMOVE_PHOTO).execute();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
*/
    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.seeAllPhotos:
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.ALBUM));
                    break;


                case R.id.ivCamera2:
                    showPopup(result.getCampaign().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    if (null != result.getCampaign().getUpdateCoverPhoto())
                        showPopup(result.getCampaign().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;

                case R.id.llDonate:
                    fetchDonateForm();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void fetchDonateForm() {
        if (isNetworkAvailable(context)) {
            showBaseLoader(true);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_FUND_ID, mPageId);
            new ApiController(Constant.URL_FUND_DONATE_FORM, map, context, this, REQ_DONATE).execute();
        } else {
            notInternetMsg(v);
        }
    }

    private int getTabPositionByName(String name) {
        int position = 0;
        for (int i = 0; i < tabMenus.size(); i++) {
            if (tabMenus.get(i).getName().equals(name)) {
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
            tvMsg.setText(getString(R.string.MSG_DELETE_CONFIRMATION_GENERIC, getString(R.string.crowdfinding)));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FUND_DELETE);
                    request.params.put(Constant.KEY_FUND_ID, mPageId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    CommonResponse res = new Gson().fromJson(response, CommonResponse.class);
                                    Util.showSnackbar(v, res.getResult().getSuccessMessage());
                                    activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                    onBackPressed();
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
                case Constant.Events.REMOVE_PHOTO:
                    hideBaseLoader();
                    break;
                //called from overview tab
                case Constant.Events.CONTENT_EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_FUND_ID, mPageId);
                    openFormFragment(Constant.FormType.OVERVIEW, map, Constant.URL_FUND_OVERVIEW_EDIT);
                    break;

                case Constant.Events.ACCEPT:
                    openWebView("" + object2, getString(R.string.donate));
                    break;
                case REQ_DONATE:
                    hideBaseLoader();
                    if (null != object2) {
                        Dummy resp = new Gson().fromJson("" + object2, Dummy.class);
                        if (null != resp) {
                            DonateDialogFragment.newInstance(ViewCrowdFragment.this, mPageId, resp.getResult()).show(fragmentManager, "donate");
                        }
                    } else {
                        somethingWrongMsg(v);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }

    private void callLikeApi(final int REQ_CODE, final View view, String url, boolean showAnimation) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, result.getCampaign(), showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mPageId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.FUND);
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

                                       /* if (REQ_CODE > REQ_DELETE) {
                                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                            List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                            result.getCampaign().setButtons(opt);

                                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                        }*/

                                    } else {
                                        //revert changes in case of error
                                        updateItemLikeFavorite(REQ_CODE, view, result.getCampaign(), false);
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
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, View view, FundContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getCampaign().isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        }

    }

    private void openLighbox(int photoId, String imageUrl, int albumId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_FUND_ID, mPageId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, Constant.ResourceType.FUND_PHOTO);
        map.put(Constant.KEY_IMAGE, imageUrl);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (fromUser) {
            if (result.getCampaign().isRated()) {
                Util.showSnackbar(v, getString(R.string.txt_rated_already));
                ratingBar.setRating(result.getCampaign().getRating());
            } else {
                int totalUserRated = result.getCampaign().updateRatingCount(rating);
                ((TextView) v.findViewById(R.id.tvRatingCount)).setText(context.getResources().getQuantityString(R.plurals.rating_count, totalUserRated, totalUserRated));
                CrowdUtil.calRatingApi(result.getCampaign().getFundId(), rating, context);
            }
        }
    }
}
