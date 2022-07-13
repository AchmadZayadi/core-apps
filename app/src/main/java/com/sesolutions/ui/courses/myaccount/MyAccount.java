package com.sesolutions.ui.courses.myaccount;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.courses.classroom.MyClassroomFragment;
import com.sesolutions.ui.courses.course.MyCourseFragment;
import com.sesolutions.ui.courses.test.MyTestFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.store.account.BillingShippingFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;

public class MyAccount extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private TabLayout tabLayout;
    private BillingShippingFragment billing;
    private BillingShippingFragment shipping;
    private MyClassroomFragment myClassroomFragment;
    private MyCourseFragment myCourse;
    private MyTestFragment myTestFragment;
    private CourseOrderFragment myOrders;
    private Toolbar toolbar;
    private MyCourseWishlist myWishlist;
    private PageProfileReviewFragment myReview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_account_course, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.text_color_1), Color.parseColor(Constant.colorPrimary));
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setVisibility(View.GONE);
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {

        billing = BillingShippingFragment.newInstance(Constant.FormType.CREATE_CLASSROOM, null, Constant.URL_COURSE_BILLING);
        shipping = BillingShippingFragment.newInstance(Constant.FormType.CREATE_CLASSROOM, null, Constant.URL_SHIPING_ADDRESS);

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_STORE_ID, 10);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE_REVIEW);
        myReview = PageProfileReviewFragment.newInstance("My Reviews", this, map);

        myOrders = new CourseOrderFragment();
        myWishlist = new MyCourseWishlist();
//        myStore = StoreFragment.newInstance(MenuTab.Store.MY_STORE, this);
        myClassroomFragment = new MyClassroomFragment();
        myCourse = new MyCourseFragment();
        myTestFragment = new MyTestFragment();

        tabLayout.addTab(tabLayout.newTab().setText("Billings"),true);
//        tabLayout.addTab(tabLayout.newTab().setText("Shipping"));
        tabLayout.addTab(tabLayout.newTab().setText("My Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("My WishList"));
//        tabLayout.addTab(tabLayout.newTab().setText("My Reviews"));
        tabLayout.addTab(tabLayout.newTab().setText("My Classrooms"));
        tabLayout.addTab(tabLayout.newTab().setText("My Courses"));
        tabLayout.addTab(tabLayout.newTab().setText("My Tests"));

    }
    private void bindWidgetsWithAnEvent()
    {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(billing);
                break;
            case 1:
                replaceFragment(myOrders);
                break;
            case 2:
                replaceFragment(myWishlist);
                break;
//            case 4:
//                replaceFragment(myReview);
//                break;
            case 3:
                replaceFragment(myClassroomFragment);
//                myStore.initScreenData();
                break;
                case 4:
                replaceFragment(myCourse);
//                myStore.initScreenData();
                break;
                case 5:
                replaceFragment(myTestFragment);
//                myStore.initScreenData();
                break;
        }
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        return false;
    }




}

