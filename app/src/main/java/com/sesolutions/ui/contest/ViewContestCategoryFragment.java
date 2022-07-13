package com.sesolutions.ui.contest;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.utils.CustomLog;

public class ViewContestCategoryFragment extends ContestFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog_category, container, false);
        applyTheme(v);
        txtNoData = R.string.MSG_NO_CONTEST_CREATED;
        init();
        updateTitle(title);
        setRecyclerView();
        callMusicAlbumApi(1);
        return v;
    }

    public void init() {
        super.init();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setVisibility(View.GONE);
    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }


    @Override
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

    @Override
    public void showHideNoDataView() {
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        int minCount = 0;
        if (null!=result && null != result.getCategory()) {
            minCount = 1;
        }
        v.findViewById(R.id.llNoData).setVisibility(contestList.size() > minCount ? View.GONE : View.VISIBLE);
    }

    public static ViewContestCategoryFragment newInstance(int categoryId, Category category) {
        ViewContestCategoryFragment frag = new ViewContestCategoryFragment();
        frag.listener = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = category.getName();
        frag.selectedScreen = TYPE_VIEW_CATEGORY;
        frag.categoryLevel = category.getCategoryLevel();
        return frag;
    }
}
