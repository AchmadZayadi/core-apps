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
import com.sesolutions.responses.credit.CreditResult;
import com.sesolutions.responses.credit.Transaction;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionFragment extends BaseFragment implements OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final int REQ_SEARCH = 2;
    private static final int REQ_LOAD_MORE = 3;
    private View v;

    public RecyclerView recyclerView;
    private List<Transaction> friendList;
    private TransactionAdapter adapter;
    private CreditResult result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    //  private Bundle bundle;
    private String url;
    private String resourceType;
    // private AppCompatEditText etMusicSearch;
    //private String query;
    //    private Map<String, Object> map;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedScreen;
    private OnUserClickedListener<Integer, Object> listener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setBackgroundColor(SesColorUtils.getForegroundColor(context));
        swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setEnabled(false);
        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
            init();
            // getBundle();
            setRecyclerView();
            callNotificationApi(1, null);
        }
    }

   /* private void getBundle() {
        if (bundle != null) {
            resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            resourceType = bundle.getString(Constant.KEY_RESOURCES_TYPE);
            url = bundle.getString(Constant.KEY_URI);
            map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
        }
    }*/

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new TransactionAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callNotificationApi(Constant.REQ_CODE_REFRESH, null);
    }

    private void callNotificationApi(final int REQ, Map<String, Object> map) {
        if (isNetworkAvailable(context)) {
            isLoading = true;
            if (REQ_LOAD_MORE == REQ) {
                pb.setVisibility(View.VISIBLE);
            } else if (REQ != Constant.REQ_CODE_REFRESH) {
                showView(v.findViewById(R.id.pbMain));
            }
            try {
                //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                //     dialog.setCancelable(true);
                HttpRequestVO request = new HttpRequestVO(URL.CREDIT_TRANSACTION);
                if (map != null)
                    request.params.putAll(map);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
               /* if (!TextUtils.isEmpty(query)) {
                    request.params.put(Constant.KEY_SEARCH, query);
                }*/

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
                            SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                            if (TextUtils.isEmpty(resp.getError())) {
                                result = resp.getResult(CreditResult.class);

                                //clear all saved data in case of "Not searching"
                                if (REQ != REQ_LOAD_MORE) {
                                    friendList.clear();
                                }
                                wasListEmpty = friendList.size() == 0;


                                if (result.isTransactionEmpty()) {
                                    if (wasListEmpty) {
                                        //add object for header
                                        friendList.add(new Transaction());
                                    }
                                    friendList.addAll(result.getTransactions());
                                }

                            } else {
                                Util.showSnackbar(v, resp.getErrorMessage());
                                //goIfPermissionDenied(err.getError());
                            }
                        } else {
                            notInternetMsg(v);
                        }
                        showHideUpperButton();
                        updateRecyclerView();
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
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
    }

    private void showHideUpperButton() {
        /*if (null != result.getButton()) {
            v.findViewById(R.id.rlCommentEdittext).setVisibility(View.GONE);
            v.findViewById(R.id.cvSelect).setVisibility(View.GONE);
            v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getButton().getLabel());
            v.findViewById(R.id.cvCreate).setOnClickListener(this);

        } else {
            v.findViewById(R.id.rlFilter).setVisibility(View.GONE);
        }*/
        v.findViewById(R.id.rlFilter).setVisibility(View.GONE);
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        swipeRefreshLayout.setRefreshing(false);
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
    }

    private void updateRecyclerView() {
        try {
            isLoading = false;
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_transaction);
            v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
            if (null != listener) {
                listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
                listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
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
                    callNotificationApi(REQ_LOAD_MORE, null);
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

    public static TransactionFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> listener) {
        TransactionFragment frag = new TransactionFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_IMAGE:
                // goToProfileFragment(friendList.get(postion).getUserId(), (PageMemberAdapter.ContactHolder) value, postion);
                break;
        }
        return false;

    }

    @Override
    public void onClick(View view) {

    }

    public void onFilterClick() {
        friendList.clear();
        result = null;
        //final Object value = activity.filteredMap.get(Constant.KEY_SEARCH);

        callNotificationApi(Constant.REQ_CODE_REFRESH, activity.filteredMap);
    }
}
