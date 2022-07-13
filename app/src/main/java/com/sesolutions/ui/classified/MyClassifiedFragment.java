package com.sesolutions.ui.classified;


import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;

public class MyClassifiedFragment extends BrowseClassifiedFragment {

  /*  public String searchKey;
    public CommonResponse.Result result;
    public int loggedinId;
    public int categoryId;
    public String txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public ProgressBar pb;*/

    public static MyClassifiedFragment newInstance(ClassifiedParentFragment parent, int loggedInId) {
        MyClassifiedFragment frag = new MyClassifiedFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = -1;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        txtNoData = Constant.MSG_NO_LISTING_CREATED;
        applyTheme();
        return v;
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    @Override
    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ClassifiedAdapter(videoList, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
