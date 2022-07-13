package com.sesolutions.ui.albums_core;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.ui.albums.BrowseAlbumFragment;
import com.sesolutions.ui.albums.SearchPhotoFragment;
import com.sesolutions.ui.albums.ViewAlbumCategoryFragment;
import com.sesolutions.utils.CustomLog;

public class C_ViewAlbumCategoryFragment extends BrowseAlbumFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog_category, container, false);
        applyTheme();
        init();
        updateTitle(title);
        setRecyclerView();
        callMusicAlbumApi(1);
        return v;
    }

    public void init() {
        super.init();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
        // v.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchPhotoFragment()).addToBackStack(null).commit();
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

    public static C_ViewAlbumCategoryFragment newInstance(int categoryId, String categoryName) {
        C_ViewAlbumCategoryFragment frag = new C_ViewAlbumCategoryFragment();
        frag.listener = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        return frag;
    }
}
