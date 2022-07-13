package com.sesolutions.ui.multistore;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.ui.blogs.SearchBlogFragment;
import com.sesolutions.utils.CustomLog;

public class ViewMultiStoreExpertFragment extends MutistoreFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_expert_multireview, container, false);
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

    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchBlogFragment()).addToBackStack(null).commit();
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

    public static ViewMultiStoreExpertFragment newInstance(int categoryId, String categoryName) {
        ViewMultiStoreExpertFragment frag = new ViewMultiStoreExpertFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        return frag;
    }
}
