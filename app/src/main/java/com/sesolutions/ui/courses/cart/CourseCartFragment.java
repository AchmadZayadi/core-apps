package com.sesolutions.ui.courses.cart;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sesolutions.responses.Courses.course.CheckoutResponse2;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
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

import static com.sesolutions.utils.URL.URL_COURSE_CHECKOUT;

public class CourseCartFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<CheckoutResponse2.Result.CartData.ProductData> productlist;
    private CourseCartAdapter adapter;
    private CheckoutResponse2.Result result;
    private int REQ_LOAD_MORE = 2;
    private int REQ_REMOVE_PRODUCT = 3;
    private int REQ_REMOVE_ALL = 4;
    private boolean isLoading;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SuggestionViewFragment parent;
    private LinearLayoutCompat llClearall, ll_checkout, llBottom;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_course_cart, container, false);
        try {
            applyTheme(v);
            initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            switch (activity.taskPerformed) {
                case 99:
                    onBackPressed();
                    break;
                case Constant.TASK_EMPTY_CART:
                    onRefresh();
                    break;
            }

        } catch (Exception e) {
            CustomLog.e("onStart", CourseCartFragment.class.getSimpleName(), e);
        }
    }

    public void initScreenData() {

        init();
        setRecyclerView();
        callMyCartApi(1);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        callMyCartApi(Constant.REQ_CODE_REFRESH);
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText("My Cart");
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        llBottom = v.findViewById(R.id.llBottom);
        llClearall = v.findViewById(R.id.ll_clear_all);
        ll_checkout = v.findViewById(R.id.ll_checkout);
        llClearall.setOnClickListener(this);
        ll_checkout.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }


    private void setRecyclerView() {
        try {
            productlist = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CourseCartAdapter(productlist, context, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void callMyCartApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == Constant.REQ_CODE_REFRESH) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_COURSE_CART); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
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
                            CustomLog.e("Course_cart_respone", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    CheckoutResponse2 resp = new Gson().fromJson(response, CheckoutResponse2.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        productlist.clear();
                                    }
                                    wasListEmpty = productlist.size() == 0;
                                    result = resp.getResult();

                                    // todo handle null pointer exception
                                    if (null != result.getCartData()) {
                                        llBottom.setVisibility(View.VISIBLE);
                                        for (CheckoutResponse2.Result.CartData cart : result.getCartData()) {
                                            productlist.addAll(cart.getProductData());
                                            updatePrice();
                                            updateTitle(resp.getResult().getExtraParams().getCart_count());
                                        }
                                    }
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

    private void updateTitle(int total) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText("My Cart" + "(" + total + ")");
    }

    private void updatePrice() {
        ((TextView) v.findViewById(R.id.llprice2)).setText(result.getGrand_total());
    }

    private void callClearAllApi(int productId, final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {

                showBaseLoader(true);
                HttpRequestVO request = new HttpRequestVO(Constant.URL_COURSE_CART_DELETE); //url will change according to screenType

                // used when this screen called from page view -> associated
                if (productId > 0)
                    request.params.put(Constant.KEY_ID, productId);

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
                            CustomLog.e("response_remove", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    JSONObject json = new JSONObject(response);

                                    if (req == REQ_REMOVE_ALL) {
                                        if (json.get(Constant.KEY_RESULT) instanceof String) {
                                            String result = json.getString(Constant.KEY_RESULT);
                                            Util.showSnackbar(v, result);
                                            onBackPressed();
                                        }
                                    } else if (req == REQ_REMOVE_PRODUCT) {
                                        if (json.get(Constant.KEY_RESULT) instanceof String) {
                                            String result = json.getString(Constant.KEY_RESULT);
                                            Util.showSnackbar(v, result);
                                            onRefresh();
                                        }
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
    public boolean onItemClicked(Integer clickType, Object url, int position) {
        try {
            switch (clickType) {
                case Constant.Events.MEMBER_REMOVE:
                    showDeleteDialog(position);
                    break;
                case Constant.Events.MUSIC_MAIN:
                    openViewCourseFragment(position);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ll_checkout:
                openWebView(URL_COURSE_CHECKOUT, "Checkout");
                break;

            case R.id.ll_clear_all:
                callClearAllApi(0, REQ_REMOVE_ALL);
                break;
        }
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.EMPTY_CART_MSG);
        v.findViewById(R.id.llNoData).setVisibility(productlist.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.llTotal).setVisibility(productlist.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.llBottom).setVisibility(productlist.size() > 0 ? View.VISIBLE : View.GONE);
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

    public void showDeleteDialog(final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.msg_remove_course_cart));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callClearAllApi(position, REQ_REMOVE_PRODUCT);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
