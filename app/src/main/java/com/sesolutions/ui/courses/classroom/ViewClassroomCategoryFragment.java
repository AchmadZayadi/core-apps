package com.sesolutions.ui.courses.classroom;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.utils.CustomLog;

public class ViewClassroomCategoryFragment extends ClassroomFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
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
        v.findViewById(R.id.ivSearch).setOnClickListener(this);

    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

//    private void goToSearchFragment() {
//        fragmentManager.beginTransaction().replace(R.id.container, new SearchBlogFragment()).addToBackStack(null).commit();
//    }


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

    public static ViewClassroomCategoryFragment newInstance(int categoryId, String categoryName) {
        ViewClassroomCategoryFragment frag = new ViewClassroomCategoryFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        return frag;
    }
}
