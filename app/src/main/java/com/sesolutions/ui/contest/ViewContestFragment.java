package com.sesolutions.ui.contest;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.member.ProfileMemberResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.contest.join.ContestJoinFragment;
import com.sesolutions.ui.events.EventInfoFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.profile.ProfileTabsAdapter;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewContestFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener {


    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_CONTACT = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_UPDATE_UPPER = 406;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ContestResponse.Result result;
    private int mEventId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;

    public static ViewContestFragment newInstance(int contestId) {
        ViewContestFragment frag = new ViewContestFragment();
        frag.mEventId = contestId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.FormType.JOIN:
                    activity.taskPerformed = 0;
                    v.findViewById(R.id.bJoin).setVisibility(View.GONE);
                    appBarLayout.setExpanded(false, true);
                    int pos = getTabPositionByName(Constant.TabOption.ENTRIES);
                    viewPager.setCurrentItem(pos, true);
                    adapter.getItem(pos).onRefresh();

                    goToViewEntryFragment(activity.taskId);

                    break;
                case Constant.TASK_IMAGE_UPLOAD:
                    /*if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getContest().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getContest().setCoverImageUrl(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }*/
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    activity.taskPerformed = 0;
                    break;
                case Constant.FormType.SEO:
                case Constant.FormType.EDIT_CONTACT:
                    activity.taskPerformed = 0;
                    break;
                case Constant.FormType.AWARD:
                case Constant.FormType.OVERVIEW:
                case Constant.FormType.RULES:
                    activity.taskPerformed = 0;
                    onRefresh();
                    break;
                case Constant.FormType.EDIT_CONTEST:
                    activity.taskPerformed = 0;
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    break;
               /* case Constant.FormType.CREATE_REVIEW:
                case Constant.FormType.CREATE_EVENT_VIDEO:
                    activity.taskPerformed = 0;
                    goTo(Constant.GoTo.VIDEO, activity.taskId, Constant.ResourceType.SES_EVENT_VIDEO);
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    viewPager.setCurrentItem(pos, true);
                    ((EventVideoFragment) adapter.getItem(pos)).onRefresh();
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
        v = inflater.inflate(R.layout.fragment_contest_view, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        iconFont = FontManager.getTypeface(context);
        callMusicAlbumApi(1);

        return v;
    }


    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == 1) {
                    showBaseLoader(true);
                } else if (req == REQ_UPDATE_UPPER) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_VIEW);
                request.params.put(Constant.KEY_CONTEST_ID, mEventId);
                //  request.params.put("menus", 1);
                //  request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (!isAdded()) return false;
                        hideAllLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    ContestResponse commonResponse = new Gson().fromJson(response, ContestResponse.class);
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

            v.findViewById(R.id.rlOwnerInfo).setOnClickListener(this);
            v.findViewById(R.id.bCreate).setOnClickListener(this);
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

        //  mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        if (result.getContest() != null) {
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            ContestItem resp = result.getContest();
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

            //   ((TextView) v.findViewById(R.id.tvStatus)).setText(getStatusByKey(resp.getEventStatus()));

            updateCoverPhoto(resp.getCoverImage());
            updateProfilePhoto(resp.getContestImage());
            addUpperTabItems();
            updateOwnerInfo();
            setJoinButton();
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

    private void updateOwnerInfo() {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(ContextCompat.getColor(context, R.color.contest_type));
        v.findViewById(R.id.tvType).setBackground(shape);
        ((TextView) v.findViewById(R.id.tvType)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.tvType)).setText(getIconByType(result.getContest().getContestType()));
        ((TextView) v.findViewById(R.id.tvOwnerTitle)).setText(getString(R.string.by_owner, result.getContest().getOwnerTitle()));
        Util.showImageWithGlide(v.findViewById(R.id.ivOwnerImage), result.getContest().getOwnerImageUrl(), context, R.drawable.placeholder_square);
        v.findViewById(R.id.rlOwnerInfo).setVisibility(View.VISIBLE);


    }

    private String getIconByType(String contestType) {
        try {
            switch ("" + contestType) {
                case "1":
                    return Constant.FontIcon.TEXT;
                case "2":
                    return Constant.FontIcon.ALBUM;
                case "3":
                    return Constant.FontIcon.VIDEO;
                case "4":
                    return Constant.FontIcon.MUSIC;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }

    }


    //set tab bar items
    private void initTablayout() {
       // tabLayout = v.findViewById(R.id.tabs);
        if (result.getMenus() != null) {
            setupViewPager();
         //   tabLayout.clearOnTabSelectedListeners();
          //  tabLayout.setupWithViewPager(viewPager, true);
            applyTabListener();

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
            //tabLayout.setVisibility(View.GONE);
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
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_EVENT_INFO);
                intent2.putExtra(Constant.KEY_ID, mEventId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;

            case Constant.TabOption.ALBUM:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                map.put(Constant.KEY_URI, Constant.URL_BUSINESS_ALBUM);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                bundle.putSerializable(Constant.POST_REQUEST, map);


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ALBUM);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.RULES:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.TEXT, result.getContest().getRules());
                map.put(Constant.BUTTON, null != result.getContest().getRuleOption());
                map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_CONTEST_RULE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.AWARDS:

                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContestAwardFragment.newInstance( result.getContest(),true))
                        .addToBackStack(null)
                        .commit();
                break;
            case "comments":
                goToCommentFragment(mEventId, Constant.ResourceType.CONTEST);
                break;

            case "winners":

                fragmentManager.beginTransaction()
                        .replace(R.id.container, EntryFragment.newInstance( opt.getName(), null, mEventId,true))
                        .addToBackStack(null)
                        .commit();

            //    adapter.addFragment(EntryFragment.newInstance(opt.getName(), null, mEventId), opt.getLabel());
                break;
            case Constant.TabOption.ENTRIES:

                fragmentManager.beginTransaction()
                        .replace(R.id.container, EntryFragment.newInstance( opt.getName(), null, mEventId,true))
                        .addToBackStack(null)
                        .commit();

        //        adapter.addFragment(EntryFragment.newInstance(opt.getName(), null, mEventId), opt.getLabel());
                break;
            case Constant.TabOption.DETAILS:

                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContestInfoFragment.newInstance(result.getContest(),true))
                        .addToBackStack(null)
                        .commit();
                break;



            case Constant.TabOption.MAP:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_MAP);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MAP);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //   adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                break;
            case Constant.TabOption.OVERVIEW:
                bundle = new Bundle();
                map = new HashMap<>();
              //  map.put(Constant.TEXT, result.getBusiness().getDescription());
                map.put(Constant.TEXT, result.getContest().getOverview());
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
                map.put(Constant.KEY_PAGE_ID, mEventId);
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_POLL);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.ANNOUNCE:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_ANNOUNCE);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ANNOUNCE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);


                break;

            case Constant.TabOption.SERVICES:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_SERVICES);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);// mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_SERVICE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                // adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                break;

            case Constant.TabOption.MEMBERS:

                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mEventId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mEventId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                // adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_BUSINUSS_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.VIDEO:


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_BUSINUSS_VIDEOS);
                intent2.putExtra(Constant.KEY_ID, mEventId);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PAGE, mPageId, this), opt.getLabel());
                break;

            case Constant.TabOption.ASSOCIATE:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_BUSINUSS_ASSOCIATE);
                intent2.putExtra(Constant.KEY_ID, mEventId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);


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
                        adapter.addFragment(FeedFragment.newInstance(mEventId, Constant.ResourceType.CONTEST), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.TEXT, result.getContest().getOverview());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;
                    case Constant.TabOption.RULES:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getContest().getRules());
                        map.put(Constant.BUTTON, null != result.getContest().getRuleOption());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, this), opt.getLabel());
                        break;
                    case "awards":
                        adapter.addFragment(ContestAwardFragment.newInstance(result.getContest()), opt.getLabel());
                        break;
                    case "comments":

                        break;

                    case "winners":
                        adapter.addFragment(EntryFragment.newInstance(opt.getName(), null, mEventId), opt.getLabel());
                        break;
                    case Constant.TabOption.ENTRIES:
                        adapter.addFragment(EntryFragment.newInstance(opt.getName(), null, mEventId), opt.getLabel());
                        break;
                    case Constant.TabOption.DETAILS:
                        adapter.addFragment(ContestInfoFragment.newInstance(result.getContest()), opt.getLabel());
                        break;
                   /* default:
                        adapter.addFragment(PageMemberFragment.newInstance(null), opt.getLabel());
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
            (adapter.getItem(tab.getPosition())).onRefresh();
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

    //  private CollapsingToolbarLayout collapsingToolbar;

    private void setJoinButton() {
        v.findViewById(R.id.llAction).setVisibility(View.VISIBLE);
        if (null != result.getContest().getJoin()) {

            v.findViewById(R.id.bJoin).setVisibility(View.VISIBLE);
            ((AppCompatButton) v.findViewById(R.id.bJoin)).setText(result.getContest().getJoin());
            v.findViewById(R.id.bJoin).setOnClickListener(this);
        } else {
            v.findViewById(R.id.bJoin).setVisibility(View.GONE);
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
                        collapsingToolbar.setTitle(result.getContest().getTitle());
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

        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        boolean isLoggedInUser = SPref.getInstance().isLoggedIn(context);
        //add Like item
        if (isLoggedInUser && result.getContest().canLike()) {
            final View view4 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view4.findViewById(R.id.tvOptionText)).setText(result.getContest().isContentLike() ? R.string.unlike : R.string.like);
            ((ImageView) view4.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view4.findViewById(R.id.ivOptionImage)).setColorFilter(result.getContest().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view4.findViewById(R.id.tvOptionText)).setTextColor(color);
            view4.setOnClickListener(v -> callLikeApi(REQ_LIKE, view4, Constant.URL_CONTEST_LIKE));
            llTabOptions.addView(view4);
        }


        //add Favorite item
        if (isLoggedInUser && result.getContest().canFavourite()) {
            final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getContest().isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view1, Constant.URL_PAGE_FAVORITE));
            llTabOptions.addView(view1);
        }

        //add Follow item
        if (isLoggedInUser && result.getContest().canFollow()) {
            final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(result.getContest().isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setColorFilter(result.getContest().isContentFollow() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> callLikeApi(REQ_FOLLOW, view2, Constant.URL_CONTEST_FOLLOW));
            llTabOptions.addView(view2);
        }

        //add Comment item
        if (isLoggedInUser && SPref.getInstance().isLoggedIn(context)) {
            final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_COMMENT));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.comment));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> goToCommentFragment(mEventId, Constant.ResourceType.CONTEST));
            llTabOptions.addView(view1);
        }

        //check permission and add JOIN item
        /*String opt = result.getContest().getJoin();
        if (null != opt) {
            View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(opt);
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_join));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> {
            });

            llTabOptions.addView(view3);
        }*/

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
                    showShareDialog(result.getContest().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getContest().getOptions(), vItem, 10);
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
            if (null != result && null != result.getContest() && result.getContest().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getContest().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getContest().getOptions() != null) {
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
                opt = result.getContest().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getContest().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getContest().getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEditContestFragment.newInstance(Constant.FormType.EDIT_CONTEST, map, Constant.URL_CONTEST_EDIT, null)).addToBackStack(null).commit();
                    break;

                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getContest().getShare());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.CONTEST + "_" + mEventId);
                    break;

                case "editPhoto":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                    goToUploadAlbumImage(Constant.URL_CONTEST_ADD_PHOTO, result.getContest().getContestImage(), opt.getLabel(), map);
                    break;

                case "uploadcover":
                case "changecover":
                case "addcover":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                    goToUploadAlbumImage(Constant.URL_CONTEST_ADD_COVER, result.getContest().getCoverImageUrl(), opt.getLabel(), map);
                    break;

                case "editAwards":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.AWARD, map, Constant.URL_CONTEST_EDIT_AWARD);
                    break;
                case "editSeo":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.SEO, map, Constant.URL_CONTEST_EDIT_SEO);
                    break;
                case "editRules":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.RULES, map, Constant.URL_CONTEST_EDIT_RULES);
                    break;
                case "editInformation":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.EDIT_CONTACT, map, Constant.URL_CONTEST_EDIT_CONTACT);
                    break;
                case "editOverview":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.OVERVIEW, map, Constant.URL_CONTEST_EDIT_OVERVIEW);
                    break;

                case "contactparticipant":
                    if (isNetworkAvailable(context)) {
                        showBaseLoader(true);
                        map = new HashMap<>();
                        map.put(Constant.KEY_CONTEST_ID, mEventId);
                        new ApiController(Constant.URL_CONTEST_PARTICIPANTS, map, context, this, REQ_CONTACT).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getContest().getCoverImageUrl(), resourceType, result.getContest().getCoverImageUrl());
                    break;
                case Constant.OptionType.remove_photo:
                case "removecover":
                    showImageRemoveDialog(isCover);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    //TODO same method is on @ProfileFragment
    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_CONTEST_REMOVE_COVER : Constant.URL_REMOVE_EVENT_PHOTO;
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
                map.put(Constant.KEY_CONTEST_ID, mEventId);
                new ApiController(url, map, context, ViewContestFragment.this, Constant.Events.REMOVE_PHOTO).execute();

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_CREATE);
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
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("ref", mEventId);
                                    if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), map, Constant.ResourceType.CONTEST);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            openContestCreateForm(vo.getResult(), map);
                                        }
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }


                            } else {
                                Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
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

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.ivCamera2:
                    showPopup(result.getContest().getUpdateProfilePhoto(), v.findViewById(R.id.ivCamera2), 100);
                    break;
                case R.id.ivCamera:
                    //   if (null != result.getContest().getUpdateCoverPhoto())
                    showPopup(result.getContest().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
                case R.id.rlOwnerInfo:
                    goToProfileFragment(result.getContest().getOwnerId());
                    break;
                case R.id.bCreate:
                    fetchFormData();
                    break;
                case R.id.bJoin:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ContestJoinFragment.newInstance(result.getContest()))
                            .addToBackStack(null)
                            .commit();
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_CONTEST));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(REQ_DELETE, Constant.URL_CONTEST_DELETE, -1);
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
                request.params.put(Constant.KEY_CONTEST_ID, mEventId);
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
                                    } else if (REQ == REQ_JOIN) {
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



    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        try {
            CustomLog.e("POPUP", "" + object2 + "  " + object2 + "  " + position);
            switch (object1) {
                case Constant.Events.TAB_OPTION_PROFILE:
                    // handleTabOptionClicked("" + value, postion);
                    callFragment(object2);

                    break;
                case Constant.Events.REMOVE_PHOTO:
                    hideBaseLoader();
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    break;
                case Constant.Events.CONTENT_EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    super.openFormFragment(Constant.FormType.RULES, map, Constant.URL_CONTEST_EDIT_RULES);
                    break;
                case REQ_CONTACT:
                    hideAllLoaders();
                    if (object2 != null) {
                        ProfileMemberResponse resp = new Gson().fromJson((String) object2, ProfileMemberResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                if (resp.getResult().getMembers() != null && resp.getResult().getMembers().size() > 0) {
                                    openComposeActivity(resp.getResult().getItemUsers());
                                } else {
                                    Util.showSnackbar(v, getStrings(R.string.no_participant_availiable));
                                }
                            } else {
                                somethingWrongMsg(v);
                            }
                        } else {
                            Util.showSnackbar(v, resp.getErrorMessage());
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


    private void callLikeApi(final int REQ_CODE, final View view, String url) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, result.getContest());
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mEventId);
                    request.params.put(Constant.KEY_CONTEST_ID, mEventId);
                    request.params.put(Constant.KEY_TYPE, "contest");
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
                                            result.getContest().setOptions(opt);

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
                                        updateItemLikeFavorite(REQ_CODE, view, result.getContest());
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
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getContest().isContentLike() ? R.string.unlike : R.string.like);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getContest().isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.colorPrimary : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        }

    }
}
