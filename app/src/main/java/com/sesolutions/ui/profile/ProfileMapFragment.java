package com.sesolutions.ui.profile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.page.PageMapAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.SpreadBuilder;

public class ProfileMapFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object> {

    private View v;

    public RecyclerView recyclerView;
    private List<LocationActivity> friendList;
    private PageMapAdapter adapter;
    private CommonResponse.Result result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    private int userId;
    private Bundle bundle;
    private String url;
    private String resourceType;
    private int mPageID;
    private Map<String, Object> map;
    boolean showToolbar=false;

    public static ProfileMapFragment newInstance(Bundle bundle, boolean showToolbar) {
        ProfileMapFragment frag = new ProfileMapFragment();
        frag.bundle = bundle;
        frag.showToolbar = showToolbar;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_common, container, false);
        applyTheme(v);

        if (!showToolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.location_text);
            initScreenData();
        }


        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
            CustomLog.e("loading child", "notification");
            init();
            getBundle();
            setRecyclerView();
            callNotificationApi(true);
        }
    }

    private void getBundle() {
        if (bundle != null) {
            userId = bundle.getInt(Constant.KEY_ID);
            resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            resourceType = bundle.getString(Constant.KEY_RESOURCES_TYPE);
            mPageID = bundle.getInt(Constant.KEY_RESOURCE_ID);
            url = bundle.getString(Constant.KEY_URI);
            map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
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
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.bRefresh:
                    callNotificationApi(true);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader) {
        try {

            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (showLoader) {
                    showBaseLoader(true);
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_PAGE_ID, mPageID);
                    request.params.putAll(map);
//                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
//                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put(Constant.KEY_ID, userId);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoaders();

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    isContentLoaded = true;
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();
                                        wasListEmpty = friendList.size() == 0;
                                        if (null != resp.getResult().getLocation())
                                            friendList.add(resp.getResult().getLocation());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        // goIfPermissionDenied(err.getError());
                                    }
                                } else {
                                    notInternetMsg(v);
                                }
                                updateRecyclerView();
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideAllLoaders();

                }

            } else {
                hideAllLoaders();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }

    }

    private void hideAllLoaders() {
        isLoading = false;
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
        hideBaseLoader();
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
        try {
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
        CustomLog.e("pagination", "" + adapter.getItemCount());
    }

  /*  public static GroupMemberFragment newInstance(int resourceId) {
        GroupMemberFragment frag = new GroupMemberFragment();
        frag.resourceId = resourceId;
        return frag;
    }*/

    public static ProfileMapFragment newInstance(Bundle bundle) {
        ProfileMapFragment frag = new ProfileMapFragment();
        frag.bundle = bundle;
        return frag;
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.FEED_MAP:
                LocationActivity la = friendList.get(postion);
                if (null != la) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + la.getLat() + "," + la.getLng()));
                    startActivity(intent);
                }
                break;
        }
        return false;
    }

}
