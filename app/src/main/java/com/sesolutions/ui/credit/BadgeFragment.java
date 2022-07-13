package com.sesolutions.ui.credit;


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
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SesResponse;
import com.sesolutions.responses.credit.Badge;
import com.sesolutions.responses.credit.CreditResult;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.member.MemnerFilterFormFragment;
import com.sesolutions.ui.member.SearchMemberFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class BadgeFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQ_LOAD_MORE = 100;
    public View v;
    public List<List<Badge>> albumsList;
    public RecyclerView recyclerView;
    public BadgeAdapter adapter;
    public boolean isLoading;
    public CreditResult result;
    public ProgressBar pb;
    public String selectedScreen;
    //public String title = Constant.TITLE_MEMBER;
    public String search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnUserClickedListener<Integer, Object> listener;

    public static BadgeFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> listener) {
        BadgeFragment frag = new BadgeFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        applyTheme(v);
        return v;
    }

    @Override
    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }


    @Override
    public void onRefresh() {
        if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    public void init() {
        try {
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            pb = v.findViewById(R.id.pb);
            recyclerView = v.findViewById(R.id.recyclerview);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    public void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new BadgeAdapter(albumsList, context, this, this);
            recyclerView.setAdapter(adapter);

            // recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
                    goToSearchMember();
                    break;
                case R.id.ivFilter:
                    openForm();
                    break;
                case R.id.ivMic:
                    closeKeyboard();
                    TTSDialogFragment.newInstance(this).show(fragmentManager, "tts");
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openForm() {
        fragmentManager.beginTransaction().replace(R.id.container,
                MemnerFilterFormFragment.newInstance(Constant.FormType.FILTER_MEMBER, null, Constant.URL_MEMBER_SEARCH_FILTER))
                .addToBackStack(null)
                .commit();
    }

    private void goToSearchMember() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new SearchMemberFragment())
                .addToBackStack(null)
                .commit();
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(URL.CREDIT_BADGES);

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        setRefreshing(swipeRefreshLayout, false);
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        albumsList.clear();
                                    }
                                    result = resp.getResult(CreditResult.class);

                                    // if (null != result.getCurrentBadge() && result.getCurrentBadge().size() > 0)
                                    albumsList.add(result.getCurrentBadge());

                                    // if (null != result.getAllBadges() && result.getAllBadges().size() > 0)
                                    albumsList.add(result.getAllBadges());

                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                    CustomLog.e(e);
                }
            } else {
                setRefreshing(swipeRefreshLayout, false);
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    private void updateAdapter() {
        try {
            isLoading = false;
            pb.setVisibility(View.GONE);

            //  swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_MEMBER);
            v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);

            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {

        return false;
    }
}
