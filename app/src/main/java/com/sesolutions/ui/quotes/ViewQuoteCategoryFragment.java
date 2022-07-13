package com.sesolutions.ui.quotes;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class ViewQuoteCategoryFragment extends BrowseQuotesFragment implements View.OnClickListener {


    private String title;

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

    public static ViewQuoteCategoryFragment newInstance(int categoryId, String categoryName, boolean isTag) {
        ViewQuoteCategoryFragment frag = new ViewQuoteCategoryFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        frag.isTag = isTag;
        return frag;
    }
}
