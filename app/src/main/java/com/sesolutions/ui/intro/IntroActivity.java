package com.sesolutions.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SPref;

import java.util.List;

public class IntroActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private List<SlideShowImage> list;
    View ivPrev, ivNext, tvSignIn, tvSkip;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_layout);
        list = SPref.getInstance().getIntroImages(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ivNext = findViewById(R.id.ivNext);
        ivPrev = findViewById(R.id.ivPrev);
        tvSkip = findViewById(R.id.tvPrev);
        tvSignIn = findViewById(R.id.tvNext);

        ivNext.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(list, getSupportFragmentManager()));
        ((PageIndicatorView) findViewById(R.id.pageIndicatorView)).setViewPager(mViewPager);
        // Set a PageTransformer
        // mViewPager.setPageTransformer(false, new IntroPageTransformer());
        mViewPager.setPageTransformer(false, new ParallaxPageTransformer());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedItem = position;
                if ((list.size() - 1) == position) {
                    ivPrev.setVisibility(View.VISIBLE);
                    tvSkip.setVisibility(View.GONE);
                    tvSignIn.setVisibility(View.VISIBLE);
                    ivNext.setVisibility(View.GONE);
                } else if (position == 0) {
                    ivPrev.setVisibility(View.GONE);
                    ivNext.setVisibility(View.VISIBLE);
                    tvSignIn.setVisibility(View.GONE);
                    tvSkip.setVisibility(View.VISIBLE);
                } else {
                    ivPrev.setVisibility(View.VISIBLE);
                    tvSkip.setVisibility(View.VISIBLE);
                    ivNext.setVisibility(View.VISIBLE);
                    tvSignIn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivNext:
                mViewPager.setCurrentItem(selectedItem + 1, true);
                break;
            case R.id.ivPrev:
                mViewPager.setCurrentItem(selectedItem - 1, true);
                break;
            case R.id.tvPrev:
                goToWelcome(0);
                break;
            case R.id.tvNext:
                goToWelcome(1);
                break;

        }
    }

    public void goToWelcome(int screen) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra(Constant.KEY_TYPE, screen);
        startActivity(intent);
        finish();
    }
}
