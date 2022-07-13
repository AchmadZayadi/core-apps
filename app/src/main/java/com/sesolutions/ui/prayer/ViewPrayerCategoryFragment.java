package com.sesolutions.ui.prayer;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class ViewPrayerCategoryFragment extends BrowsePrayerFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog_category, container, false);
        //    url = Constant.URL_ARTICLE_SEARCH;
        txtNoData = Constant.MSG_NO_QUOTE_CREATED;
        applyTheme(v);
        init();
        updateTitle(0);
        setRecyclerView();
        callMusicAlbumApi(1);
        return v;
    }

    public void init() {
        super.init();
        //  rvQuotesCategory.setVisibility(View.GONE);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
    }

    @Override
    public void updateTitle(int count) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title + (count > 0 ? " (" + count + ")" : ""));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((TextView) v.findViewById(R.id.tvTitle)).setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            ((TextView) v.findViewById(R.id.tvTitle)).setTransitionName(bundle.getString(Constant.Trans.TEXT));
            ((TextView) v.findViewById(R.id.tvTitle)).setText(bundle.getString(Constant.Trans.IMAGE));
            startPostponedEnterTransition();
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
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

    public static ViewPrayerCategoryFragment newInstance(int categoryId, String categoryName, boolean isTag, Bundle bundle) {
        ViewPrayerCategoryFragment frag = new ViewPrayerCategoryFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        frag.isTag = isTag;
        frag.bundle = bundle;

        return frag;
    }
}
