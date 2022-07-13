package com.sesolutions.ui.courses.myaccount;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.store.checkout.CheckoutResponse;
import com.sesolutions.responses.store.checkout.Order;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.profile.SuggestionViewFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseOrderFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<Order> orderList;
    private CourseOrderAdapter adapter;
    private CheckoutResponse.Result result;
    private boolean isLoading;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SuggestionViewFragment parent;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_offset, container, false);
        try {
            applyTheme(v);
            initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    public void initScreenData() {

        init();
        setRecyclerView();
        callOrdersApi(1);
    }

    private void init() {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }


    private void setRecyclerView() {
        try {
            orderList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CourseOrderAdapter(orderList, context, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callOrdersApi(int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == Constant.REQ_CODE_REFRESH) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_COURSEORDERS); //url will change according to screenType
//                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                // used when this screen called from page view -> associated

//                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
//                if (req == Constant.REQ_CODE_REFRESH) {
//                    request.params.put(Constant.KEY_PAGE, 1);
//                }

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("response_orders", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    CheckoutResponse resp = new Gson().fromJson(response, CheckoutResponse.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        orderList.clear();
                                    }

                                    wasListEmpty = orderList.size() == 0;
                                    result = resp.getResult();

                                    if (null != result.getOrders())
                                        orderList.addAll(result.getOrders());

//                                    for (CheckoutResponse.Result.CartData cart : result.getCartData()) {
//                                        productlist.addAll(cart.getProductData());
//                                    }
                                    updateRecyclerView();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            somethingWrongMsg(v);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public boolean onItemClicked(Integer clickType, Object url, int position) {
        try {
            switch (clickType) {

                case Constant.Events.VIEW_ORDER:
//                    StoreUtil.openViewOrderFragment(fragmentManager, position);
//                    CourseUtil.openCourseViewOrderFragment(fragmentManager, position);
                    
                    openWebView(Constant.URL_VIEW_ORDER + position + "/format/smoothbox", "order details" );
                    break;
                case Constant.Events.DELETE:
                    callDeleteOrderApi(position);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
//        parent.isSuggestionLoaded = true;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_ORDER);
        v.findViewById(R.id.llNoData).setVisibility(orderList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {

                    //callFriendRequestApi(false, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteOrderApi(int orderId) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                showBaseLoader(true);
                HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_ORDER);

                // used when this screen called from page view -> associated
                request.params.put("order_id", orderId);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("response_delete_order", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    JSONObject json = new JSONObject(response);

                                    if (json.get(Constant.KEY_RESULT) instanceof String) {
                                        onRefresh();
                                        String result = json.getString(Constant.KEY_RESULT);
                                        Util.showSnackbar(v, result);
                                    }

                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            somethingWrongMsg(v);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }

    }

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callOrdersApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

}
