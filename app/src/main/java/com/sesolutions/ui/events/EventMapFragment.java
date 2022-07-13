package com.sesolutions.ui.events;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.page.PageMapAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

public class EventMapFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object> {

    private View v;

    public RecyclerView recyclerView;
    private List<LocationActivity> friendList;
    private PageMapAdapter adapter;
    private CommonResponse.Result result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    private Bundle bundle;
    private String url;
    private int resourceType;
    private int mPageID;
    private LocationActivity vo;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_common, container, false);
        applyTheme(v);

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.mapviewid);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            initScreenData();
        }

        // initScreenData();
        return v;
    }

    @Override
    public void initScreenData() {
        init();
        getBundle();
        setRecyclerView();
        //callNotificationApi(true);
    }

    private void getBundle() {
        if (bundle != null) {
            resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            resourceType = bundle.getInt(Constant.KEY_RESOURCES_TYPE);
            mPageID = bundle.getInt(Constant.KEY_RESOURCE_ID);
            //   url = bundle.getString(Constant.KEY_URI);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        friendList.add(vo);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PageMapAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
              /*  case R.id.ivBack:
                    onBackPressed();
                    break;
*/
                /*case R.id.bRefresh:
                    callNotificationApi(true);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateRecyclerView() {
        isLoading = false;
        // updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_LOCATION);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        /*try {
            if (result != null && !isLoading) {
                CustomLog.e("getCurrentPage", "" + result.getCurrentPage());
                CustomLog.e("getTotalPage", "" + result.getTotalPage());

                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(false);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        CustomLog.e("pagination", "" + adapter.getItemCount());*/
    }


    public static EventMapFragment newInstance(Bundle bundle, LocationActivity vo) {
        EventMapFragment frag = new EventMapFragment();
        frag.bundle = bundle;
        frag.vo = vo;
        return frag;
    }

    boolean istoolbar=false;
    public static EventMapFragment newInstance(Bundle bundle, LocationActivity vo,boolean istoolbar) {
        EventMapFragment frag = new EventMapFragment();
        frag.bundle = bundle;
        frag.vo = vo;
        frag.istoolbar = istoolbar;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.FEED_MAP:
                LocationActivity la = friendList.get(postion);
                if (null != la) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + la.getLat() + "," + la.getLng()));
                    startActivity(intent);
                }
                break;
        }
        return false;
    }

}
