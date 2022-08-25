package com.sesolutions.ui.dashboard;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;

import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.BuildConfig;
import com.sesolutions.R;
import com.sesolutions.firebase.AppVersion;
import com.sesolutions.firebase.FirebaseHelper;
import com.sesolutions.http.HttpImageNotificationRequest;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.materialtaptargetprompt.MaterialTapTargetPrompt;
import com.sesolutions.materialtaptargetprompt.MaterialTapTargetSequence;
import com.sesolutions.materialtaptargetprompt.extras.focals.RectanglePromptFocal;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SesResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.ActivityClickClick;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.currency.CurrencyDialog;
import com.sesolutions.ui.customviews.CustomSwipableViewPager;
import com.sesolutions.ui.events.InviteDialogFragment;
import com.sesolutions.ui.friend.FriendRequestFragment;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.ui.member.MemberFragment;
import com.sesolutions.ui.message.MessageDashboardFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.notification.NotificationFragment;
import com.sesolutions.ui.price.PriceActivity;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.Jzvd;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;


public class DashboardFragment extends BaseFragment implements View.OnClickListener, Handler.Callback, OnUserClickedListener<Integer, Object>, BottomNavigationView.OnNavigationItemSelectedListener {

    private View v;
    private boolean[] isLoaded = {false, false, false, false};
    private String[] tabItem = {MenuTab.Dashboard.HOME, MenuTab.Dashboard.REQUEST, MenuTab.Dashboard.MESSAGE, MenuTab.Dashboard.NOTIFICATION};
    public boolean isDrawerOpen = false;
    public boolean isHomeContentLoaded = false;
    public boolean isRequestContentLoaded = false;
    public boolean isMessageContentLoaded = false;
    public boolean isNotificationContentLoaded = false;
    private CustomSwipableViewPager viewPager;
    private TabLayout tabLayout;
    private MessageDashboardViewPagerAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private CircleImageView ivProfileToolbar;
    private final int[] inactiveIcon = {R.drawable.icon_home, R.drawable.ic_friends, R.drawable.ic_message, R.drawable.ic_notifications};
    public final int[] unreadCount = {0, 0, 0, 0};
    private int menuTitleActiveColor;
    private int menuBackgroundColor;
    private int menuTitleColor;
    private Timer timer;
    private long backPressed;
    public String filterFeedType = "all";

    public int firstFeedId;
    private final String subject = Constant.EMPTY;
    ImageView ivactionmap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private AdView mAdView;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private View ivBack;
    public static final int LOCATION_PERMISSION_REQUEST = 8;
    public static AppCompatTextView icCurrrency;
    String first_name;
    String kecamatan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }


        v = inflater.inflate(AppConfiguration.SHOW_TAB_AT_TOP ? R.layout.fragment_dashboard_2 : R.layout.fragment_dashboard, container, false);

        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
        try {
            applyTheme(v);
            init();
            //  initAdMob();
            FirebaseHelper.getInstance().getAppVersion(this);
            FirebaseHelper.getInstance().getFirebaseId(this);
            initFab();


//            if(checkLocationPermission()){
//                fusedLocationProviderClient = getFusedLocationProviderClient(getActivity());
//                // Acquire a reference to the system Location Manager
//                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                // Define a listener that responds to location updates
//                LocationListener locationListener = new LocationListener() {
//
//                    public void onLocationChanged(Location location) {
//
//                        // Called when a new location is found by the network location provider.
//                      //  Toast.makeText(getContext(), "location is:"+location, Toast.LENGTH_LONG).show();
//                      //  Log.e("Locationtag1",""+location.getLatitude());
//                     //   Log.e("Locationtag2",""+location.getLongitude());
//
//                    }
//
//                    public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//                    public void onProviderEnabled(String provider) {}
//
//                    public void onProviderDisabled(String provider) {}
//                };
//                // Register the listener with the Location Manager to receive location updates
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//            }


            if (AppConfiguration.IS_APP_TOUR_ENABLED)
                v.postDelayed(this::showAppTour, 2000);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

//    private boolean checkLocationPermission() {
////        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(getActivity(),
////                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                    LOCATION_PERMISSION_REQUEST);
////            return false;
////        } else {
////            return true;
////        }
//    }

    private void showDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bOk = progressDialog.findViewById(R.id.bCamera);
            bOk.setText(R.string.TXT_OK);
            AppCompatButton bCancel = progressDialog.findViewById(R.id.bGallary);
            bCancel.setText(R.string.TXT_CANCEL);
            bCancel.setVisibility(View.GONE);
            bOk.setText("Ubah Profile");
            bOk.setOnClickListener(v -> {
                progressDialog.dismiss();
                openUserProfileEditForm();

            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
//    public void openEditProfie() {
//        Intent intent = new Intent(context, CommonActivity.class);
//        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CORE_DASHBAORDMESSAGE);
//        startActivity(intent);
//    }

    public void openmessageFragment() {
        Intent intent = new Intent(context, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CORE_DASHBAORDMESSAGE);
        startActivity(intent);
    }

    private void showAppTour() {
        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                                .setTarget(ivBack)
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setPrimaryText(R.string.profile)
                                .setFocalRadius(R.dimen.button_height_medium)
                                .setBackgroundColour(SesColorUtils.getPrimaryDarkColor(context))
                                .setFocalColour(SesColorUtils.getPrimaryColor(context))
                                .setSecondaryText(R.string.profile_d)
                        /*.create(), 6000*/)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                                .setTarget(v.findViewById(R.id.rlSearch))
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setPrimaryText(R.string.navigation_item)
                                //.setPrimaryTextColour(SesColorUtils.getPrimaryColor(context))
                                //.setSecondaryTextColour(SesColorUtils.getPrimaryColor(context))
                                .setPromptFocal(new RectanglePromptFocal())
                                // .setPromptBackground(new RectanglePromptBackground())
                                .setBackgroundColour(SesColorUtils.getPrimaryDarkColor(context))
                                // .setFocalColour(SesColorUtils.getColor(context, R.color.transparent))
                                .setFocalColour(SesColorUtils.getPrimaryColor(context))
                                .setSecondaryText(R.string.navigation_item_d)
                                //.setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                .setFocalPadding(R.dimen.margin_super)
                        //.setIcon(R.drawable.ic_search)
                        /* .create(), 16000*/)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                                .setTarget(ivProfileToolbar)
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setPrimaryText(R.string.profile)
                                .setBackgroundColour(SesColorUtils.getPrimaryDarkColor(context))
                                .setFocalRadius(R.dimen.button_height_medium)
                                .setFocalColour(SesColorUtils.getPrimaryColor(context))
                                .setSecondaryText(R.string.profile_d)
                        //.setFocalPadding(R.dimen.margin_super)
                        /*.create(), 6000*/)
                .show();

    }

    private void initTourGuide() {

        /*TourGuide mTourGuideHandler =
                TourGuide.init(getActivity()).with(TourGuide.Technique.CLICK)
                        .setPointer(new Pointer())
                        .setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click on Get Started to begin..."));
        mTourGuideHandler.setOverlay(new Overlay());
        mTourGuideHandler.playOn(ivBack);*/
        // Rect rect = new Rect(card.getLeft(), card.getTop(), card.getRight(), card.getBottom());
        //Rect rect=new Rect((int)card.getX(),(int)card.getY(),(int)card.getX()+card.getWidth(),(int)card.getY()+card.getHeight());
        new TapTargetSequence(getActivity())
                .continueOnCancel(true)
                .targets(
                        getSesDefaultTargetView(ivBack, "Navigation", "We have the best targets, believe me"),
                        getSesDefaultTargetView(ivProfileToolbar, "Profile", "We have the best targets, believe me")
                                .tintTarget(false),
                        getSesDefaultTargetView(v.findViewById(R.id.ivToolbarSearch), "Search", "We have the best targets, believe me")
                                .targetRadius(24).cancelable(false))
                //.icon(ContextCompat.getDrawable(context, R.drawable.ic_search)))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        CustomLog.d("AppIntro_onSequenceFinish", "targetClicked");
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        CustomLog.d("AppIntro_onSequenceStep", "targetClicked=" + targetClicked);
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        CustomLog.d("AppIntro_onSequenceCanceled", "targetClicked");
                    }
                }).start();

    }

    private TapTarget getSesDefaultTargetView(View view, String title, String description) {
        return TapTarget.forView(view, title, description)
                .dimColor(R.color.transparent_black_light)
                .outerCircleColorInt(SesColorUtils.getPrimaryColor(context))
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .tintTarget(true)
                .transparentTarget(false)
                .targetRadius((int) (view.getWidth() / (getResources().getDisplayMetrics().density * 2)))
                .textColor(android.R.color.white);
    }

    private void initFab() {
        if (SPref.getInstance().isLoggedIn(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((FloatingActionButton) v.findViewById(R.id.fabCompose)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Constant.colorPrimary)));
            } else {
                ((FloatingActionButton) v.findViewById(R.id.fabCompose)).setBackgroundColor(Color.parseColor(Constant.colorPrimary));
            }

            //     ((FloatingActionButton) v.findViewById(R.id.fabCompose)).setImageDrawable(getResources().getDrawable(R.drawable.ic_full_sad));
            //  v.findViewById(R.id.fabCompose).setVisibility(View.VISIBLE);
            v.findViewById(R.id.fabCompose).setOnClickListener(this);
            updateFabColor(v.findViewById(R.id.fabCompose));
            ((FloatingActionButton) v.findViewById(R.id.fabCompose)).setImageTintList(ColorStateList.valueOf(Color.parseColor(Constant.backgroundColor)));

        }
    }

    private void showUpdateDialog(AppVersion data) {
        if (!data.canUpdate()) {
            return;
        }
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(!data.isForceUpdate());
            progressDialog.setCancelable(!data.isForceUpdate());
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_update_app);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogTitle))
                    .setText(data.getUpdateTitle());
            ((CheckBox) progressDialog.findViewById(R.id.checkbox))
                    .setText(data.getDontShowText());
            ((TextView) progressDialog.findViewById(R.id.tvDialogText))
                    .setText(data.getUpdateDescription());
            progressDialog.findViewById(R.id.checkbox).setVisibility(data.isCanShowCheckbox() ? View.VISIBLE : View.GONE);
            TextView bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(data.getUpdateCancelText());
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(data.getUpdateButtonText());
            bCamera.setVisibility(data.isForceUpdate() ? View.GONE : View.VISIBLE);

            bGallary.setOnClickListener(v -> {
                if (!data.isForceUpdate()) {
                    progressDialog.dismiss();
                }
                try {
                    Intent rateIntent = rateIntentForUrl("market://details");
                    startActivity(rateIntent);
                } catch (ActivityNotFoundException e) {
                    Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
                    startActivity(rateIntent);
                }
            });

            bCamera.setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case Constant.Events.APP_VERSION_CHECK:
                if (null != data) {
                    showUpdateDialog((AppVersion) data);
                }
                break;
            case Constant.Events.SET_LOADED:
                isLoaded[Arrays.asList(tabItem).indexOf("" + data)] = true;
                CustomLog.e("isLoaded", isLoaded.toString());
                break;
            case Constant.Events.OK:
                openmessageFragment();
                break;
        }
        return false;
    }

    private void initAdMob() {
        if (!AppConfiguration.isAdEnabled)
            return; //Do not initilize adMob if it is disabled from admin
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(adListener);
    }

    private final AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Code to be executed when an ad finishes loading.
            CustomLog.e("AdMob", "onAdLoaded");
            mAdView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
            CustomLog.e("AdMob", "onAdFailedToLoad");
            mAdView.setVisibility(View.GONE);
        }

        @Override
        public void onAdOpened() {
            // Code to be executed when an ad opens an overlay that
            // covers the screen.
            CustomLog.e("AdMob", "onAdOpened");
        }

        @Override
        public void onAdLeftApplication() {
            // Code to be executed when the user has left the app.
            CustomLog.e("AdMob", "onAdLeftApplication");
        }

        @Override
        public void onAdClosed() {
            // to the app after tapping on an ad.
            CustomLog.e("AdMob", "onAdClosed");
        }
    };

    private boolean isLoggedIn;
    private View llTabLayout;

    private void init() {
        try {
            menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
            menuTitleColor = Color.parseColor(Constant.menuButtonTitleColor);//Constant.menuButtonTitleColor);
            menuBackgroundColor = Color.parseColor(Constant.menuButtonBackgroundColor);//Constant.menuButtonTitleColor);
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
            ivProfileToolbar = v.findViewById(R.id.ivProfileToolbar);
            ivactionmap = v.findViewById(R.id.ivactionmap);
            ivProfileToolbar.setOnClickListener(this);

            ivBack = v.findViewById(R.id.ivBack);
            icCurrrency = v.findViewById(R.id.ivCurrency);
            ivBack.setOnClickListener(this);


            if (isLoggedIn){
                checkUser();

            }
            try {
                if (SPref.getInstance().isBasicPlugins(getContext(), "seslocation")) {
                    ivactionmap.setVisibility(View.VISIBLE);
                } else {
                    ivactionmap.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ivactionmap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Onlocation", "LOCATION");
                    onLocationActionClicked();
                }
            });

            ivProfileToolbar.setVisibility(AppConfiguration.enableLoggedinUserphoto && isLoggedIn ? View.VISIBLE : View.GONE);
            tabLayout = v.findViewById(R.id.tabs);
            tabLayout.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
            //hiding tabindicator
            tabLayout.setSelectedTabIndicatorColor(AppConfiguration.SHOW_TAB_AT_TOP ? menuTitleActiveColor : Color.parseColor(Constant.backgroundColor));

            tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
            llTabLayout = v.findViewById(R.id.llTabLayout);
            llTabLayout.setBackgroundColor(menuBackgroundColor);
            viewPager = v.findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            setHeaderLayout();
            if (!AppConfiguration.SHOW_TAB_AT_TOP) {
                viewPager.setPagingEnabled(false);
                v.findViewById(R.id.llCreatePost).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llCreatePost).setOnClickListener(this);
                tabLayout.setVisibility(View.GONE);
                llTabLayout.setVisibility(View.VISIBLE);
                initCustomTablayout();
                changeTabColor(0, true);
                changeTabColor(1, false);
                changeTabColor(2, false);
                changeTabColor(3, false);
            }

//            bottomNavigationView = v.findViewById(R.id.bottom_navbar);
//            bottomNavigationView.setOnNavigationItemSelectedListener(this);

            v.findViewById(R.id.ivToolbarSearch).setOnClickListener(this);
            v.findViewById(R.id.ivMessage).setOnClickListener(this);
            v.findViewById(R.id.ivVideo).setOnClickListener(this);

            if (AppConfiguration.isMulticurrencyEnabled) {
                v.findViewById(R.id.ivCurrency).setOnClickListener(this);
                ((AppCompatTextView) (v.findViewById(R.id.ivCurrency))).setText(AppConfiguration.DEFAULT_CURRENCY);
                v.findViewById(R.id.ivCurrency).setVisibility(View.VISIBLE);

                icCurrrency.setTextColor(Color.parseColor(Constant.text_color_light));

            } else {
                v.findViewById(R.id.ivCurrency).setVisibility(View.GONE);
            }

            v.findViewById(R.id.ivVideo).setVisibility(View.GONE);


//            if (SPref.getInstance().getDefaultInfo(context, Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
//                v.findViewById(R.id.ivMessage).setVisibility(View.GONE);
//            } else {
//                v.findViewById(R.id.ivMessage).setVisibility(View.VISIBLE);
//            }

            tabLayout.setupWithViewPager(viewPager, true);
            if (AppConfiguration.SHOW_TAB_ICONS) {
                setupTabIcons();
                changeTabIcon(0);
            }
            if (!isLoggedIn) {
                viewPager.setPagingEnabled(false);
                //    tabLayout.setVisibility(View.GONE);
                //    llTabLayout.setVisibility(View.GONE);

                // v.findViewById(R.id.llCreatePost).setVisibility(View.GONE);

                // ((View) (tabLayout.getTabAt(1).getCustomView().getParent())).setEnabled(false);
                // ((View) (tabLayout.getTabAt(2).getCustomView().getParent())).setEnabled(false);
                // ((View) (tabLayout.getTabAt(3).getCustomView().getParent())).setEnabled(false);
            }
            applyTabListener();

            new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 100);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void initCustomTablayout() {
        llTabLayout.findViewById(R.id.rlTab1).setOnClickListener(this);
        llTabLayout.findViewById(R.id.rlTab2).setOnClickListener(this);
        llTabLayout.findViewById(R.id.rlTab3).setOnClickListener(this);
        llTabLayout.findViewById(R.id.rlTab4).setOnClickListener(this);
    }

    public void setHeaderLayout(/*View view*/) {
        // GradientDrawable shape2 = new GradientDrawable();
        //  shape2.setShape(GradientDrawable.RECTANGLE);
        // int x = 10;
        // shape2.setCornerRadii(new float[]{x, x, x, x, x, x, x, x});
        // shape2.setColor(Color.parseColor(Constant.alpha_black_light));
        //shape2.setStroke(2, cPrimary);
        // view.setBackground(shape2);
        if (AppConfiguration.titleHeaderType == 2) {
            v.findViewById(R.id.tvTitleMain).setVisibility(View.GONE);
            v.findViewById(R.id.rlSearch).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.tvTitleMain).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlSearch).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.tvTitleMain)).setText(AppConfiguration.siteTitle);
        }
        v.findViewById(R.id.lay_search).setVisibility(View.VISIBLE);
    }

    private final int[] tab_title = {R.string.TAB_TITLE_HOME, R.string.TAB_TITLE_REQUEST, R.string.TAB_TITLE_MESSAGE, R.string.TAB_TITLE_NOTIFICATION};

    private void setupViewPager(ViewPager viewPager) {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(AppConfiguration.enableTabbarTitle);
      //  adapter.addFragment(HomeFragment.newInstance(this), getString(tab_title[0]));
        adapter.addFragment(PriceActivity.newInstance(this), getString(tab_title[0]));

        adapter.addFragment(FriendRequestFragment.newInstance(this), getString(tab_title[1]));
        adapter.addFragment(MessageDashboardFragment.newInstance(this), getString(tab_title[2]));
        adapter.addFragment(NotificationFragment.newInstance(this), getString(tab_title[3]));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private void setupTabIcons() {
        try {
            for (int i = 0; i < inactiveIcon.length; i++) {
                TabLayout.Tab tabitem = tabLayout.getTabAt(i);
                Objects.requireNonNull(tabitem).setCustomView(prepareTabView(i));
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private View prepareTabView(int pos) {
        View view = getLayoutInflater().inflate(R.layout.tab_items, null);
        ImageView ivTab = view.findViewById(R.id.ivTab);
        //TextView tv_count = view.findViewById(R.id.tvTabCount);
        TextView tvTabTitle = view.findViewById(R.id.tvTabTitle);
        tvTabTitle.setVisibility(AppConfiguration.enableTabbarTitle ? View.VISIBLE : View.GONE);
        ivTab.setImageDrawable(ContextCompat.getDrawable(context, inactiveIcon[pos]));
        tvTabTitle.setText(tab_title[pos]);
        ivTab.setColorFilter(menuTitleColor);
        tvTabTitle.setTextColor(menuTitleColor);
       /* if (unreadCount[pos] > 0) {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText("" + unreadCount[pos]);

        } else
            tv_count.setVisibility(View.GONE);*/
        return view;
    }

    public void updateTabBadgeCount(int pos) {
        try {
            TextView tv_count;
            if (AppConfiguration.theme == 2) {
                switch (pos) {
                    case 1:
                        tv_count = ((TextView) llTabLayout.findViewById(R.id.tvTabCount2));
                        break;
                    case 2:
                        tv_count = ((TextView) llTabLayout.findViewById(R.id.tvTabCount3));
                        break;
                    case 3:
                        tv_count = ((TextView) llTabLayout.findViewById(R.id.tvTabCount4));
                        break;
                    default:
                        tv_count = ((TextView) llTabLayout.findViewById(R.id.tvTabCount1));
                        break;
                }

            } else {
                tv_count = Objects.requireNonNull(tabLayout.getTabAt(pos).getCustomView()).findViewById(R.id.tvTabCount);
            }
            if (unreadCount[pos] > 1) {
                tv_count.setVisibility(View.VISIBLE);
                tv_count.setText(String.valueOf(unreadCount[pos] - 1));
            } else
                tv_count.setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (AppConfiguration.SHOW_TAB_AT_TOP)
                    changeTabIcon(tab.getPosition());
                else
                    changeTabColor(tab.getPosition(), true);

                showHideFab(tab.getPosition());
                // updateToolbar(tab.getPosition());
                loadFragmentIfNotLoaded(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (AppConfiguration.SHOW_TAB_AT_TOP)
                    unSelect(tab.getPosition());
                else
                    changeTabColor(tab.getPosition(), false);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    if (tab.getPosition() == 0) {
                        ((HomeFragment) adapter.getItem(0)).scrollToStart();
                    } else if (tab.getPosition() == 3) {
                        ((NotificationFragment) adapter.getItem(3)).scrollToStart();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
    }

    private void showHideFab(int position) {
        v.findViewById(R.id.fabCompose).setVisibility(2 == position ? View.VISIBLE : View.GONE);
    }

    private void loadFragmentIfNotLoaded(int position) {

        try {
            if (AppConfiguration.SHOW_TAB_AT_TOP) {
                switch (position) {
                    case 0:
                        if (!isHomeContentLoaded)
                            ((adapter.getItem(position))).initScreenData();
                        break;
                    case 1:
                        if (!isRequestContentLoaded)
                            (adapter.getItem(position)).initScreenData();
                        break;
                    case 2:
                        if (!isMessageContentLoaded)
                            (adapter.getItem(position)).initScreenData();
                        break;
                    case 3:
                        if (!isNotificationContentLoaded)
                            (adapter.getItem(position)).initScreenData();
                        break;
                }
            } else {
                if (!isLoaded[position]) {
                    ((adapter.getItem(position))).initScreenData();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void changeTabIcon(int position) {
        try {
            if (position != 2) {
                if (AppConfiguration.SHOW_TAB_ICONS) {
                    final ImageView iv = Objects.requireNonNull(tabLayout.getTabAt(position).getCustomView()).findViewById(R.id.ivTab);
                    iv.setColorFilter(menuTitleActiveColor);
                    if (!AppConfiguration.enableTabbarTitle) return;

                    final TextView tv = Objects.requireNonNull(tabLayout.getTabAt(position).getCustomView()).findViewById(R.id.tvTabTitle);
                    tv.setTextColor(menuTitleActiveColor);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void changeTabColor(int position, boolean isSelected) {
        try {

            TextView tabTitle;
            ImageView tabIcon;

            switch (position) {
                case 0:
                    tabIcon = ((ImageView) llTabLayout.findViewById(R.id.ivTab1));
                    tabTitle = ((TextView) llTabLayout.findViewById(R.id.tvTabTitle1));
                    break;
                case 1:
                    tabIcon = ((ImageView) llTabLayout.findViewById(R.id.ivTab2));
                    tabTitle = ((TextView) llTabLayout.findViewById(R.id.tvTabTitle2));
                    break;
                case 2:
                    tabIcon = ((ImageView) llTabLayout.findViewById(R.id.ivTab3));
                    tabTitle = ((TextView) llTabLayout.findViewById(R.id.tvTabTitle3));
                    break;

                default:
                    tabIcon = ((ImageView) llTabLayout.findViewById(R.id.ivTab4));
                    tabTitle = ((TextView) llTabLayout.findViewById(R.id.tvTabTitle4));
                    break;
            }

            tabIcon.setColorFilter(isSelected ? menuTitleActiveColor : menuTitleColor);
            // if (!AppConfiguration.enableTabbarTitle) return;
            // final TextView tv = Objects.requireNonNull(tabLayout.getTabAt(position).getCustomView()).findViewById(R.id.tvTabTitle);
            //   tv.setVisibility(View.VISIBLE);
            tabTitle.setTextColor(isSelected ? menuTitleActiveColor : menuTitleColor);
            tabTitle.setVisibility(AppConfiguration.enableTabbarTitle ? View.VISIBLE : View.INVISIBLE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    synchronized private void unSelect(int position) {
        try {
            if (AppConfiguration.SHOW_TAB_ICONS) {
                ((ImageView) Objects.requireNonNull(tabLayout.getTabAt(position).getCustomView()).findViewById(R.id.ivTab)).setColorFilter(menuTitleColor);

                if (!AppConfiguration.enableTabbarTitle) return;
                final TextView tv = Objects.requireNonNull(tabLayout.getTabAt(position).getCustomView()).findViewById(R.id.tvTabTitle);
                //   tv.setVisibility(View.VISIBLE);
                tv.setTextColor(menuTitleColor);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    void checkUser() {

        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            try {

                HttpRequestVO request = new HttpRequestVO(Constant.URL_EDIT_PROFILE);

                request.params.put(Constant.KEY_GET_FORM, "fields");

                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                           // CustomLog.e("repsonsezayadi", "" + msg.obj);


                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("formFields");
                          //  CustomLog.e("repsonsezayadi22", "" + obj.getJSONObject(0));

                            first_name = obj.getJSONObject(0).getString("value");
                            kecamatan = obj.getJSONObject(7).getString("value");


                            if (first_name.equals("")){
                                showDialog("Lengkapi data diri Anda terlebih dahulu, terima kasih");
                            }
                          //  CustomLog.e("repsonsezayadi223", "aselole" + loudScreaming);

                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                somethingWrongMsg(v);
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
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }



    private void callAsynchronousTask() {
        final Handler handler = new Handler();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (firstFeedId == 0) {
                        return;
                    }
                    try {
                        HttpRequestVO request = new HttpRequestVO(Constant.URL_UPDATES);
                        request.headres.put(Constant.KEY_COOKIE, getCookie());
                        request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                        request.requestMethod = HttpPost.METHOD_NAME;
                        new HttpRequestHandler(activity, new Handler(DashboardFragment.this)).
                                run(request);
                        HttpRequestVO request1 = new HttpRequestVO(Constant.URL_FEED_ACTIVITY);
                        request1.headres.put(Constant.KEY_COOKIE, getCookie());
                        request1.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                        request1.params.put(Constant.KEY_FEED_ONLY, true);
                        request1.params.put(Constant.KEY_NO_LAYOUT, true);
                        request1.params.put(Constant.KEY_CHECK_UPDATE, true);
                        request1.params.put(Constant.KEY_SUBJECT, subject);
                        //adding +1 custom logic to prevent count issue on tab bar
                        request1.params.put(Constant.KEY_MIN_ID, firstFeedId + 1);
                        request1.params.put(Constant.KEY_FILTER_FEED, filterFeedType);
                        request1.requestMethod = HttpPost.METHOD_NAME;
                        new HttpRequestHandler(activity, new Handler(DashboardFragment.this)).
                                run(request1);

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, Constant.UPDATE_API_CALL_INTERVAL); //execute in every 50000 ms
    }


    String Latidute = "", Longitude = "", LocationData = "";

    private void calllocationset() {
        try {
            HttpRequestVO request1 = new HttpRequestVO(Constant.URL_LOCATION_SET);
            request1.headres.put(Constant.KEY_COOKIE, getCookie());
            request1.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

            //adding +1 custom logic to prevent count issue on tab bar
            request1.params.put(Constant.KEY_location_lat, Latidute);
            request1.params.put(Constant.KEY_location_lng, Longitude);
            request1.params.put(Constant.KEY_location_data, LocationData);
            request1.requestMethod = HttpPost.METHOD_NAME;
            new HttpRequestHandler(activity, new Handler(DashboardFragment.this)).
                    run(request1);

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    public void changePagePoistion(int postion) {
        try {
            TabLayout.Tab tab = tabLayout.getTabAt(postion);
            if (!Objects.requireNonNull(tab).isSelected())
                tab.select();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != StaticShare.FEED_ACTIVITY) {
            StaticShare.FEED_ACTIVITY = null;
            ((HomeFragment) adapter.getItem(0)).updateFeedItem(StaticShare.ITEM_POSITION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            callAsynchronousTask();
            if (Constant.TASK_POST) {
                Constant.TASK_POST = false;
//                ((adapter.getItem(0))).listenNotificationEvent(1);
                ((HomeFragment) adapter.getItem(0)).onRefresh();
            }
            if (Constant.TASK_POST_EDIT) {
                Constant.TASK_POST_EDIT = false;
                ((HomeFragment) adapter.getItem(0)).scrollToStart();
            }
            if (Constant.MESSAGE_DELETED) {
                Constant.MESSAGE_DELETED = false;
                ((MessageDashboardFragment) adapter.getItem(2)).toggleTab();
            }

            if (StaticShare.TASK_PERFORMED == Constant.Events.STORY_CREATE) {
                StaticShare.TASK_PERFORMED = -1;
                ((HomeFragment) adapter.getItem(0)).callStoryApi();
            }


            String url = SPref.getInstance().getUserMasterDetail(context).getPhotoUrl();
            setToolbarImage(url);
            try {
                ((HomeFragment) adapter.getItem(0)).updateComposerProfileImage(url);
            } catch (Exception e) {
                //IGNORE THIS ERROR
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTask();
    }

    private void stopTask() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setToolbarImage(String user_image) {
        if (ivProfileToolbar == null) return;
        Util.showImageWithGlide(ivProfileToolbar, user_image, context, R.drawable.placeholder_menu);
    }

    private int getSelectedPagePosition() {
        return tabLayout.getSelectedTabPosition();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    isDrawerOpen = true;
                    openDrawer();
                    break;
                case R.id.fabCompose:
                    super.goToComposeMessageFragment();
                    break;
                case R.id.ivToolbarSearch:
                    openCoreSearchFragment();
                    break;
                case R.id.ivMessage:
                    //  super.goToComposeMessageFragment();

                    if (isLoggedIn) {
                        TictokActivity();
                    } else {
                        showLoginDialog();
                    }

                    break;
                case R.id.ivVideo:
                    /*fragmentManager.beginTransaction()
                            .replace(R.id.container, MessageDashboardFragment.newInstance(this))
                            .addToBackStack(null)
                            .commit();*/


                    if (isLoggedIn) {
                        if (getSelectedPagePosition() != 2)
                            tabLayout.getTabAt(2).select();
                        changeTabColor(2, false);
                        // openmessageFragment();
                    } else {
                        showLoginDialog();
                    }
                    break;
                case R.id.ivCurrency:

                    CurrencyDialog dialog = new CurrencyDialog();
                    dialog.show(fragmentManager, "CurrencyDialog");

                    fragmentManager.executePendingTransactions();
                    dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            ((AppCompatTextView) (v.findViewById(R.id.ivCurrency))).setText(AppConfiguration.DEFAULT_CURRENCY);
                        }
                    });
                    break;
                case R.id.ivProfileToolbar:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        int id = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
                        goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, id);
                    } else {
                        Util.showSnackbar(v, getStrings(R.string.MSG_NOT_LOGGED_IN));
                    }
                    break;

                case R.id.rlTab1:
                    if (getSelectedPagePosition() != 0) {
                        tabLayout.getTabAt(0).select();
                    } else
                        ((HomeFragment) adapter.getItem(0)).scrollToStart();
                    break;
                case R.id.rlTab2:
                    if (isLoggedIn) {
                        if (getSelectedPagePosition() != 1)
                            tabLayout.getTabAt(1).select();
                    } else {
                        showLoginDialog();
                    }


                    break;
                case R.id.llCreatePost:

                    if (isLoggedIn) {
                        //  TictokActivity();
                        if (AppConfiguration.isStoryEnabled || AppConfiguration.isLiveStreamingEnabled) {
                            showBottomSheetDialog();
                        } else {
                            goToPostFeed(SPref.getInstance().getComposerOptions(context), -1);
                        }
                    } else {
                        showLoginDialog();
                    }


                    break;
                case R.id.rlTab3:
                    if (isLoggedIn) {
                        if (getSelectedPagePosition() != 2)
                            tabLayout.getTabAt(2).select();
                    } else {
                        showLoginDialog();
                    }
                   /* if (isLoggedIn) {
                        TictokActivity();
                    } else {
                        showLoginDialog();
                    }*/


                    break;
                case R.id.rlTab4:
                    if (isLoggedIn) {
                        if (getSelectedPagePosition() != 3)
                            tabLayout.getTabAt(3).select();
                    } else {
                        showLoginDialog();
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void TictokActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
            } else {
                startActivity(new Intent(context, ActivityClickClick.class));
            }
        } else {
            startActivity(new Intent(context, ActivityClickClick.class));
        }
    }


    public static final int PERMISSION_CONSTANT = 1059;

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Per" + permissions, "Request:" + requestCode);
        switch (requestCode) {
            case PERMISSION_CONSTANT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TictokActivity();
                }
                break;
            case LOCATION_PERMISSION_REQUEST:
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        Toast.makeText(getContext(), "location is:" + location, Toast.LENGTH_LONG).show();
                        Log.e("Locationtag1", "" + location.getLatitude());
                        Log.e("Locationtag2", "" + location.getLongitude());
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };
                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                break;
        }
    }

    public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(context, ActivityClickClick.class));
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    //open sign-in screen
    private void showLoginDialog() {
        showDeleteDialog(this, -1, getString(R.string.login_required));
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {

        if (isDrawerOpen) {
            ((MainActivity) activity).drawerLayout.closeDrawer(Gravity.START);
        } else {
            if (getSelectedPagePosition() != 0) {
                viewPager.setCurrentItem(0, true);
            } else {
                if (Jzvd.backPress()) {
                    return;
                }

                if (backPressed + 2000 > System.currentTimeMillis()) {
                    activity.finish();
                } else {
                    Util.showToast(context, getStrings(R.string.MSG_PRESS_AGAIN));
                }
                backPressed = System.currentTimeMillis();
            }
        }
    }

    public void openCoreSearchFragment() {
        Intent intent = new Intent(context, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CORE_SEARCH);
        startActivity(intent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            String response = (String) msg.obj;
            CustomLog.e("response_updates12121", "" + response);
            if (response != null && new JSONObject(response).get("result") instanceof JSONObject) {
                Log.e("da221ta", "da221ta");
                try {
                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                    if (resp.getResult().isNewItemAvailable()) {
                        int _0 = resp.getResult().getNotificationCount();
                        int _1 = resp.getResult().getFriendReqCount();
                        int _2 = resp.getResult().getMessageCount();
                        //  int _3 = resp.getResult().getTotalNotification();
                    /*if (_0 > 0) {
                        unreadCount[0] = _0;
                        updateTabBadgeCount(0);
                    }*/

                        if (_1 > 0) {
                            unreadCount[1] = _1;
                            updateTabBadgeCount(1);
                            if (AppConfiguration.SHOW_TAB_AT_TOP)
                                isRequestContentLoaded = false;
                            else
                                isLoaded[Arrays.asList(tabItem).indexOf(MenuTab.Dashboard.REQUEST)] = false;
                        }
                        if (_2 > 0) {
                            unreadCount[2] = _2;
                            updateTabBadgeCount(2);
                        }
                        if (_0 > 0) {
                            unreadCount[3] = _0;
                            updateTabBadgeCount(3);
                        }
                    }
                    try {
                        unreadCount[0] = resp.getResult().getActivityCount();
                        Log.e("data", " " + unreadCount[0]);
                        updateTabBadgeCount(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        ActivtyResponseModel resp = new Gson().fromJson(response, ActivtyResponseModel.class);
                        unreadCount[0] = resp.getResult().getActivityCount();
                        Log.e("data", " " + unreadCount[0]);
                        updateTabBadgeCount(0);
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }

            } else {


                BaseResponse<Double> base = new Gson().fromJson(response, BaseResponse.class);
                unreadCount[0] = base.getResult().intValue();
                Log.e("data", "data" + base.getResult().intValue());
                updateTabBadgeCount(0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void setViewPagerSwipable(boolean b) {
        viewPager.setPagingEnabled(b);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                break;
            case R.id.navigation_requests:
                break;
            case R.id.navigation_post:
                showBottomSheetDialog();
                break;
            case R.id.navigation_message:
                break;
            case R.id.navigation_notification:
                fragmentManager.beginTransaction().replace(R.id.container, new NotificationFragment()).addToBackStack(null).commit();
                break;
        }
        return true;
    }


    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_comment_create, null);

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();


        if (AppConfiguration.isStoryEnabled) {
            view.findViewById(R.id.ll1).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ll1).setOnClickListener(v -> {
                dialog.dismiss();
                startActivityForResult(
                        new ImageEditor.Builder(getActivity())
                                .setStickerAssets("stickers")
                                // .setQuote(title)
                                // .setQuoteSource(source)
                                .getMultipleEditorIntent(),
                        ImageEditor.RC_IMAGE_EDITOR);

            });
        } else {
            view.findViewById(R.id.ll1).setVisibility(View.GONE);
        }
        view.findViewById(R.id.ll2).setOnClickListener(v1 -> {
            dialog.dismiss();
            goToPostFeed(SPref.getInstance().getComposerOptions(context), -1);
        });
        if (AppConfiguration.isLiveStreamingEnabled) {
            view.findViewById(R.id.ll3).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ll3).setOnClickListener(v3 -> {
                dialog.dismiss();
                context.startActivity(new Intent(context, LiveVideoActivity.class));
            });
        } else {
            view.findViewById(R.id.ll3).setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    public void onLocationActionClicked() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST);
//            Log.e("Tag","Tag");
//        } else {
//            Log.e("Test","Test");
//            fusedLocationProviderClient = getFusedLocationProviderClient(getActivity());
//            locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
//            boolean provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            if (!provider) {
//                turnOnLocation();
//            } else {
//                getLocation();
//            }
//        }
    }

    public static final int LOCATION = 14;

    private void turnOnLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.turn_on_gps));
        builder.setPositiveButton(getString(R.string.on), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();

                    JSONObject customData = new JSONObject();
                    try {
                        customData.put("latitude", lat);
                        customData.put("longitude", lon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //  initAlert(customData);


                    Log.e("LONG: " + lon, "LAT: " + lat);

                } else {
                    Toast.makeText(context, getString(R.string.unable_to_get_location), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    TextView address_st;
//    private void initAlert(JSONObject customData) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        View view = LayoutInflater.from(context).inflate(R.layout.map_share_layout,null);
//        builder.setView(view);
//        try {
//             Latidute=""+customData.getDouble("latitude");
//              Longitude=""+customData.getDouble("longitude");
//
//         } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        address_st = view.findViewById(R.id.address);
//          try {
//              address_st.setText("Address: "+getAddress(context,customData.getDouble("latitude"),customData.getDouble("longitude")));
//          } catch (JSONException e) {
//              e.printStackTrace();
//          }
//          ImageView mapView = view.findViewById(R.id.map_vw);
//        String mapUrl = MAPS_URL +Latidute+","+Longitude+"&key="+
//          MAP_ACCESS_KEY;
//        Glide.with(this)
//                .load(mapUrl)
//                .into(mapView);
//
//        builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                LocationData=address_st.getText().toString();
//                calllocationset();
//            }
//        }).setNegativeButton(getString(R.string.no_), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create();
//        builder.show();
//    }


    public static final String MAPS_URL = "https://maps.googleapis.com/maps/api/staticmap?zoom=16&size=380x220&markers=color:red|";
    public static final String MAP_ACCESS_KEY = "AIzaSyAA9OwjM6OAKwBVWE3DZB_PVqgGWywt0po";

    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
