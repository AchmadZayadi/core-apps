package com.sesolutions.ui.storyview;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sesolutions.R;
import com.sesolutions.animate.DepthTransformation;
import com.sesolutions.imageeditengine.StoryPagerAdapter;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.customviews.CustomSwipableViewPager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

public class StoryPlayer extends BaseActivity implements /*StoryPlayerProgressView.StoryPlayerListener,*/ View.OnClickListener, OnUserClickedListener<Integer, Object> {
    List<StoryModel> stories;

    private CustomSwipableViewPager viewPager;
    private boolean[] isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ses_activity_story);
        viewPager = findViewById(R.id.viewPager);

        Intent intent = getIntent();
        if (intent != null) {
            stories = new ArrayList<>();
            String listString = intent.getStringExtra(Constant.STORY_IMAGE_KEY);
            JsonArray arr = new Gson().fromJson(listString, JsonArray.class);
            for (JsonElement ele : arr) {
                stories.add(new Gson().fromJson(ele, StoryModel.class));
            }
            //remove first item ,because its current user data
            stories.remove(0);
            setUpViewPager(intent.getIntExtra(Constant.KEY_POSITION, 0));
        }
    }


    StoryPagerAdapter filePagerAdapter;

    private void setUpViewPager(int selectedPosition) {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(true);
        viewPager.setPageTransformer(true, new DepthTransformation());
        //viewPager.setPageTransformer(true, new FanTransformation());
        //viewPager.setPageTransformer(true, new CubeInRotationTransformation());
        filePagerAdapter = new StoryPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < stories.size(); i++) {
            filePagerAdapter.addFragment(StoryFragment.newInstance(stories.get(i), i, this));
        }

        viewPager.setOffscreenPageLimit(stories.size());
        isLoaded = new boolean[stories.size()];
        viewPager.setAdapter(filePagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //CustomLog.d("viewpager", "onPageScrolled__position" + i + "__positionOffset" + v + "__positionOffsetPixel_" + i1);
            }

            @Override
            public void onPageSelected(int i) {
                if (!isLoaded[i]) {
                    isLoaded[i] = true;
                    filePagerAdapter.getItem(i).initScreenData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_IDLE == state) {
                    CustomLog.d("viewpager", "SCROLL_STATE_IDLE_" + state);
                    filePagerAdapter.getItem(viewPager.getCurrentItem()).resumeProgress();
                } else if (ViewPager.SCROLL_STATE_SETTLING == state) {
                    CustomLog.d("viewpager", "SCROLL_STATE_SETTLING_" + state);
                    filePagerAdapter.getItem(viewPager.getCurrentItem()).pauseProgress();
                } else {
                    CustomLog.d("viewpager", "SCROLL_STATE_DRAGGING" + state);
                    filePagerAdapter.getItem(viewPager.getCurrentItem()).pauseProgress();
                }
            }
        });

        if (selectedPosition - 1 == 0) {
            viewPager.postDelayed(() -> filePagerAdapter.getItem(selectedPosition - 1).initScreenData(), 300);
        } else {
            viewPager.postDelayed(() -> viewPager.setCurrentItem(selectedPosition - 1, false), 300);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case Constant.Events.NEXT:
                if (position < (stories.size() - 1)) {
                    viewPager.setCurrentItem(position + 1, true);
                } else {
                    finish();
                }
                break;
          /*  case Constant.Events.MUSIC_PROGRESS:
                CustomLog.e("duration1", "" + jzVideoPlayerStandard.getDuration());
                break;
            case Constant.Events.MUSIC_PREPARED:
                CustomLog.e("duration", "" + jzVideoPlayerStandard.getDuration());
                storyPlayerProgressView.startProgressFor(position, jzVideoPlayerStandard.getDuration());
                break;*/
        }
        return false;
    }
}
