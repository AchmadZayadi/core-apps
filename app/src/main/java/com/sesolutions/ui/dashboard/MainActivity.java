package com.sesolutions.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.SplashAnimatedActivity;
import com.sesolutions.ui.drawer.DrawerFragment;
import com.sesolutions.ui.drawer.DrawerModel;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;


public class MainActivity extends BaseActivity implements DialogInterface.OnDismissListener, DrawerLayout.DrawerListener {

    public DrawerLayout drawerLayout;
    public DashboardFragment dashboardFragment;
    private DrawerModel drawerModel;
    private DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("my_pid", -1) == android.os.Process.myPid()) {
                // app was not killed
                CustomLog.e("app_state", "app process was not killed");

            } else {
                // app was killed
                CustomLog.e("app_state", "app process was killed");
                Intent intent = new Intent(this, SplashAnimatedActivity.class);
                intent.putExtra(Constant.KEY_COOKIE, Constant.SESSION_ID);
                finish();
                startActivity(intent);
            }
        }

        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.ad_mob_id));
        drawerLayout = findViewById(R.id.drawer_home);
        drawerLayout.addDrawerListener(this);
        // findViewById(R.id.container).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        drawerModel = SPref.getInstance().getNavigationMenus(this);
        if (null != drawerModel) {
            callNavigationApi(false);
            addDrawerFragment();
            init();
        } else {
            callNavigationApi(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("my_pid", android.os.Process.myPid());
    }

    private void init() {
        dashboardFragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboardFragment).addToBackStack(null).commit();
    }

    /* call navigation api for drawer menus ,if isInForeground=true means previous menus are save in cache
             so don't show loader'*/
    private void callNavigationApi(final boolean isInForeground) {
        try {
            if (isNetworkAvailable(this)) {

                try {

                    if (isInForeground) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_NAVIGATION);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                SPref.getInstance().saveNavigationMenus(MainActivity.this, response);
                                drawerModel = new Gson().fromJson(response, DrawerModel.class);
                                if (isInForeground) {
                                    addDrawerFragment();
                                    init();
                                } else {
                                    drawerFragment.setDrawerModel(drawerModel);
                                    drawerFragment.setData();
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        return true;
                    };
                    new HttpRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    private void addDrawerFragment() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawerFragment = DrawerFragment.getInstance(drawerModel);
        getSupportFragmentManager().beginTransaction().replace(R.id.lay_drawer, drawerFragment).commit();

    }

   /* @Override
    protected int getHomeIcon() {
        return R.drawable.menu;
    }*/

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Bundle bundle = intent.getExtras();
            int dest = bundle.getInt(Constant.DESTINATION_FRAGMENT);
            switch (dest) {
                case Constant.GoTo.REQUESTS:
                    dashboardFragment.changePagePoistion(1);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        closeKeyboard();
        dashboardFragment.isDrawerOpen = true;

        drawerFragment.showIntro();
       /* if (null != currentFragment && currentFragment instanceof DashboardFragment) {
            ((DashboardFragment) currentFragment).isDrawerOpen = true;
        }*/
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        closeKeyboard();
        dashboardFragment.isDrawerOpen = false;
       /* if (null != currentFragment && currentFragment instanceof DashboardFragment) {
            ((DashboardFragment) currentFragment).isDrawerOpen = false;
        }*/
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    /*@Override
    protected void onHomePressed() {
        drawerLayout.openDrawer(Gravity.START);
    }*/

    public void changeCurrentFragment() {
        currentFragment = dashboardFragment;
    }

    public void setViewPagerSwipable(boolean viewPagerSwipable) {
        if (null != dashboardFragment) {
            this.dashboardFragment.setViewPagerSwipable(viewPagerSwipable);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }
}
