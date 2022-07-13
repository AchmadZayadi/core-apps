package com.sesolutions.ui.store.account;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.store.StoreFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class AccountFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean[] isLoaded = {false, false, false, false, false, false, false, false};
    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    /* public boolean isNewsLoaded;
     public boolean isCategoriesLoaded;
     public boolean isMyNewsLoaded;*/
    private ImageView ivSearch;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
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

        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 200);
    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(new OrderFragment(), "My Orders");
        adapter.addFragment(StoreFragment.newInstance("estore_my_stores",this), "My Stores");
        adapter.addFragment(BillingShippingFragment.newInstance(Constant.FormType.CREATE_STORE, null, Constant.URL_BILLING_ADDRESS), "Billing ");
        adapter.addFragment(BillingShippingFragment.newInstance(Constant.FormType.CREATE_STORE, null, Constant.URL_SHIPING_ADDRESS), "Shipping");


        viewPager.setAdapter(adapter);
        viewPager.canScrollHorizontally(0);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    public void updateTitle(String title) {
        tvTitle.setText(title);
    }

    private void applyTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadFragmentIfNotLoaded(tab.getPosition());
//                updateTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    if (tab.getPosition() == 0) {
                        (adapter.getItem(tab.getPosition())).onRefresh();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }

            }
        });
    }

    private void loadFragmentIfNotLoaded(int position) {
        if (!isLoaded[position]) ((adapter.getItem(position))).initScreenData();
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
//                case R.id.ivSearch:
//                    goToSearchNewsFragment();
//                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.SUCCESS:
                isLoaded[2] = false;
                tabLayout.getTabAt(2).select();
                break;
            case Constant.Events.UPDATE_TOTAL:
                // todo handle this event
                //here postion is total count and object2 is selected screen
//                updateTotal(Integer.parseInt(("" + object2)), postion);
                break;
            case Constant.Events.SET_LOADED:
                isLoaded[postion] = true;
                // updateLoadStatus("" + object2, true);
                break;
        }

        return false;
    }
}
