package com.sesolutions.ui.comment;


import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.palette.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.LikeData;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionViewFragment extends GlobalTabHelper {
    private static final int _DEFAULT = 100;
    private List<LikeData> tempMenu;
    private Map<String, Object> map;
   /* public boolean isRequestLoaded;
    public boolean isSearchLoaded;
    public boolean isSuggestionLoaded;
    public boolean isFriendLoaded;*/

    public static ReactionViewFragment newInstance(Map<String, Object> map) {
        ReactionViewFragment fragment = new ReactionViewFragment();
        fragment.map = (map);
        return fragment;
    }

    public void setupTabIcons() {
        try {
            for (int i = 0; i < tempMenu.size(); i++) {
                TabLayout.Tab tabitem = tabLayout.getTabAt(i);
                tabitem.setCustomView(prepareTabView(i));
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateTabIcon(int pos) {

        View view = tabLayout.getTabAt(pos).getCustomView();
        ImageView ivTab = view.findViewById(R.id.ivTab);
        final TextView tvTabTitle = view.findViewById(R.id.tvTabTitle);

        if ("all".equals(tempMenu.get(pos).getType())) {
            //  tvTabTitle.setText(getString(R.string.all, tempMenu.get(pos).getCount()));
            //  ivTab.setVisibility(View.GONE);
            tvTabTitle.setTextColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(tempMenu.get(pos).getImage())
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
                            CustomLog.e("asdf", "onLoadFailed1");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            CustomLog.e("asdf", "onResourceReady2");
                            if (resource != null) {
                                int color = Palette.from(resource).generate().getVibrantColor(Color.parseColor(Constant.colorPrimary));
                                tabLayout.setSelectedTabIndicatorColor(color);
                                tvTabTitle.setTextColor(color);
                                CustomLog.e("asdf", "onResourceReady3");
                            }
                            return false;
                        }
                    })
                    .into(ivTab);
        }
    }

    private View prepareTabView(int pos) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab_reaction, null);
        ImageView ivTab = view.findViewById(R.id.ivTab);
        TextView tvTabTitle = view.findViewById(R.id.tvTabTitle);

        if ("all".equals(tempMenu.get(pos).getType())) {
            tvTabTitle.setText(getString(R.string.all, tempMenu.get(pos).getCount()));
            tvTabTitle.setTextColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
            ivTab.setVisibility(View.GONE);
        } else {
            tvTabTitle.setText("" + tempMenu.get(pos).getCount());
            tvTabTitle.setTextColor(Color.parseColor(Constant.menuButtonTitleColor));
            Util.showImageWithGlide(ivTab, tempMenu.get(pos).getImage(), context, 1);
        }
        return view;
    }


    @Override
    public void init() {
        // tvTitle.setText(R.string.people_who_reacted);
        //  ivSearch.setVisibility(View.GONE);
        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            Map<String, Object> map1 = new HashMap<>(map);
            map1.put("item_id", 0);
            new ApiController(Constant.URL_USER_REACTION, map1, context, this, _DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }
    }


    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        for (LikeData opt : tempMenu) {
            adapter.addFragment(ReactionListFragment.newInstance(this, map, opt.getType()), getStrings(R.string.EMPTY));
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tempMenu.size());
    }

    @Override
    public void updateToolbarIcons(int position) {
        updateTabIcon(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        try {
            ((TextView) tab.getCustomView().findViewById(R.id.tvTabTitle)).setTextColor(Color.parseColor(Constant.text_color_2));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void updateTitle(int index) {
        tvTitle.setText(R.string.people_who_reacted);
    }

    @Override
    public void refreshScreenByPosition(int position) {
    }

    @Override
    public void loadFragmentIfNotLoaded(int position) {
        try {
            if (!tabLoaded[position]) {
                adapter.getItem(position).initScreenData();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void openCreateForm() {

    }

    @Override
    public void goToSearchFragment() {
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case _DEFAULT:
                hideBaseLoader();
                if (object2 != null) {
                    CommonResponse resp = new Gson().fromJson((String) object2, CommonResponse.class);
                    if (resp.getResult() != null) {
                        tempMenu = resp.getResult().getLikeData();
                        super.init();
                        updateTitle(0);
                        ivSearch.setVisibility(View.GONE);
                        setupTabIcons();
                    } else {
                        somethingWrongMsg(v);
                    }
                } else {
                    somethingWrongMsg(v);
                }
                break;
            case Constant.Events.SET_LOADED:
                for (int i = 0; i < tempMenu.size(); i++) {
                    if (("" + object2).equals(tempMenu.get(i).getType())) {
                        tabLoaded[i] = true;
                        break;
                    }
                }
                break;
        }
        return super.onItemClicked(object1, object2, postion);
    }
}
