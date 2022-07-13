package com.sesolutions.ui.store.account;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.store.wishlist.MyWishlistFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;

public class MyAccountFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private TabLayout tabLayout;
    private BillingShippingFragment billing;
    private BillingShippingFragment shipping;
    private MyStoreFragment myStore;
    private OrderFragment myOrders;
    private MyWishlistFragment myWishlist;
    private PageProfileReviewFragment myReview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_account, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.text_color_1), Color.parseColor(Constant.colorPrimary));
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {

        billing = BillingShippingFragment.newInstance(Constant.FormType.CREATE_STORE, null, Constant.URL_BILLING_ADDRESS);
        shipping = BillingShippingFragment.newInstance(Constant.FormType.CREATE_STORE, null, Constant.URL_SHIPING_ADDRESS);

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_STORE_ID, 10);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORE_REVIEW);
        myReview = PageProfileReviewFragment.newInstance("My Reviews", this, map);

        myOrders = new OrderFragment();
        myWishlist = new MyWishlistFragment();
//        myStore = StoreFragment.newInstance(MenuTab.Store.MY_STORE, this);
        myStore = new MyStoreFragment();

        tabLayout.addTab(tabLayout.newTab().setText("Billings"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Shipping"));
        tabLayout.addTab(tabLayout.newTab().setText("My Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("My WishList"));
//        tabLayout.addTab(tabLayout.newTab().setText("My Reviews"));
        tabLayout.addTab(tabLayout.newTab().setText("My Stores"));
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
    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(billing);
                break;
            case 1 :
                replaceFragment(shipping);
                break;
            case 2:
                replaceFragment(myOrders);
                break;
            case 3:
                replaceFragment(myWishlist);
                break;
//            case 4:
//                replaceFragment(myReview);
//                break;
            case 4:
                replaceFragment(myStore);
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

    @Override
    public void onClick(View v) {

    }


}

