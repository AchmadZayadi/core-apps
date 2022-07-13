package com.sesolutions.ui.page;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.crowdfunding.CrowdUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQ_DELETE = 304;
    private static final int REQ_LOAD_MORE = 305;
    private View v;

    public RecyclerView recyclerView;
    private List<Announcement> friendList;
    private AnnounceAdapter adapter;
    private PageResponse.Result result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    private Bundle bundle;
    private String url;
    private String resourceType;
    //private int mContentId;
    private HashMap<String, Object> map;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.MSG_ANNOUNCE_TXT);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            initScreenData();
        }


        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
            applyTheme(v);
            init();
            getBundle();
            setRecyclerView();
            callNotificationApi(1);
        }
    }

    private void getBundle() {
        if (bundle != null) {
            resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            resourceType = bundle.getString(Constant.KEY_RESOURCES_TYPE);
            //mContentId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            url = bundle.getString(Constant.KEY_URI);
            map = new HashMap<String, Object>();
            map.putAll((Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST));
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AnnounceAdapter(friendList, context, this, this);
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

                case R.id.cvCreate:
                    if (Constant.ResourceType.FUND.equals(resourceType)) {
                        CrowdUtil.openCreateAnnouncementForm(fragmentManager, resourceId);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        callNotificationApi(Constant.REQ_CODE_REFRESH);
    }

    private void callNotificationApi(int REQ) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (REQ != REQ_LOAD_MORE) {
                    swipeRefreshLayout.setRefreshing(true);
                    // showView(v.findViewById(R.id.pbMain));
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);//Constant.URL_PAGE_ANNOUNCE);
                    //request.params.put(Constant.KEY_PAGE_ID, mContentId);
                    //map values are key mandatory
                    request.params.putAll(map);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (REQ == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideAllLoaders();

                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                isContentLoaded = true;
                                PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    result = resp.getResult();
                                    if (REQ == Constant.REQ_CODE_REFRESH) {
                                        friendList.clear();
                                    }
                                    wasListEmpty = friendList.size() == 0;
                                    if (null != resp.getResult().getAnnouncements())
                                        friendList.addAll(resp.getResult().getAnnouncements());
                                    showHideUpperbutton();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    // goIfPermissionDenied(err.getError());
                                }
                            } else {
                                notInternetMsg(v);
                            }
                            updateRecyclerView();
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
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

    private void showHideUpperbutton() {
        if (null != result.getButton()) {
            v.findViewById(R.id.rlCommentEdittext).setVisibility(View.GONE);
            v.findViewById(R.id.cvSelect).setVisibility(View.GONE);
            v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getButton().getLabel());
            v.findViewById(R.id.cvCreate).setOnClickListener(this);

        } else {
            v.findViewById(R.id.rlFilter).setVisibility(View.GONE);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        //hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void updateRecyclerView() {
        isLoading = false;
        // updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_ANNOUNCE);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  public static GroupMemberFragment newInstance(int resourceId) {
        GroupMemberFragment frag = new GroupMemberFragment();
        frag.resourceId = resourceId;
        return frag;
    }*/
    boolean istoolbar=false;
    public static AnnouncementFragment newInstance(Bundle bundle,Boolean istoolbar) {
        AnnouncementFragment frag = new AnnouncementFragment();
        frag.bundle = bundle;
        frag.istoolbar = istoolbar;
        return frag;
    }

    public static AnnouncementFragment newInstance(Bundle bundle) {
        AnnouncementFragment frag = new AnnouncementFragment();
        frag.bundle = bundle;
        return frag;
    }


    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.OK:
                callDeleteApi(postion);
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                if (!isNetworkAvailable(context)) {
                    notInternetMsg(v);
                } else if (Constant.ResourceType.FUND.equals(resourceType)) {
                    performCrowdOptionClick(Integer.parseInt("" + value), postion);
                }
                break;
        }
        return false;
    }

    private void callDeleteApi(int postion) {


        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ID, friendList.get(postion).getAnnouncementId());
        map.put(Constant.KEY_FUND_ID, resourceId);
        new ApiController(Constant.URL_FUND_ANNOUNCEMENT_DELETE, map, context, this, REQ_DELETE).setExtraKey(postion).execute();
        friendList.remove(postion);
        adapter.notifyItemRemoved(postion);
    }


    private void performCrowdOptionClick(int listPos, int position) {
        Options opt = friendList.get(listPos).getOptions().get(position);
        switch (opt.getName()) {
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_ID, friendList.get(position).getAnnouncementId());
                map.put(Constant.KEY_FUND_ID, resourceId);
                openFormFragment(Constant.FormType.EDIT_ANNOUNCEMENT, map, Constant.URL_FUND_ANNOUNCEMENT_EDIT);
                break;
            case Constant.OptionType.DELETE:
                showDeleteDialog(this, listPos, getString(R.string.MSG_DELETE_CONFIRMATION_GENERIC, getString(R.string.crowdfunding_announcement)));
                break;
        }
    }

}
