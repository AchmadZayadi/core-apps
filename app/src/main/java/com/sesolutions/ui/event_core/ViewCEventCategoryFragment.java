package com.sesolutions.ui.event_core;


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

public class ViewCEventCategoryFragment extends CEventFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog_category, container, false);
        applyTheme(v);
        init();
        updateTitle(title);
        setRecyclerView();
        callMusicAlbumApi(1);
        return v;
    }

    public void init() {
        super.init();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        //v.findViewById(R.id.ivSearch).setOnClickListener(this);
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

   /* public static ViewEventCategoryFragment newInstance(int categoryId, String categoryName) {
        ViewEventCategoryFragment frag = new ViewEventCategoryFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        frag.selectedScreen = TYPE_VIEW_CATEGORY;
        return frag;
    }*/

    public static ViewCEventCategoryFragment newInstance(Category category) {
        ViewCEventCategoryFragment frag = new ViewCEventCategoryFragment();
        frag.parent = null;
        frag.categoryId = category.getCategoryId();
        if (frag.categoryId == 0) {
            frag.isUnknownCategory = true;
        }
        frag.subcategoryId = category.getSubCategoryId();
        frag.subsubcategoryId = category.getSubSubCategoryId();
        frag.loggedinId = 0;
        frag.title = category.getLabel();
        frag.selectedScreen = TYPE_VIEW_CATEGORY;
        frag.categoryLevel = category.getCategoryLevel();
        return frag;
    }
}
