package com.sesolutions.ui.packages;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.contest.Transaction;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrowseTransactionFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private EventResponse.Result result;
    private List<Transaction> categoryList;
    private TransactionAdapter adapter;
    private View pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String rcType, URL;

    public static BrowseTransactionFragment newInstance(String rcType) {
        BrowseTransactionFragment fragment = new BrowseTransactionFragment();
        fragment.rcType = rcType;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.layout_toolbar_list_refresh_offset, container, false);
        applyTheme(v);
        setModuleData();
        initScreenData();
        return v;
    }

    private void setModuleData() {
        switch (rcType) {
            case Constant.ResourceType.BUSINESS:
                URL = Constant.URL_BUSINESS_TRANSACTION;
                break;
            case Constant.ResourceType.GROUP:
                URL = Constant.URL_GROUP_TRANSACTION;
                break;
            case Constant.ResourceType.PAGE:
                URL = Constant.URL_PAGE_TRANSACTION;
                break;
            default:
                URL = Constant.URL_CONTEST_TRANSACTION;
                break;
        }
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.title_transactions);
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        try {
            categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new TransactionAdapter(categoryList, context, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showUpdateDialog(Transaction data) {

        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_transaction);
            addViews(progressDialog, data);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void addViews(ProgressDialog v, Transaction data) {
        LinearLayoutCompat llBasic = v.findViewById(R.id.llBottom);
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.transaction_id);
            ((TextView) view1.findViewById(R.id.tv2)).setText("#" + data.getTransaction_id());
            llBasic.addView(view1);
        }

        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.contest_id);
            ((TextView) view1.findViewById(R.id.tv2)).setText("#" + data.getId());
            llBasic.addView(view1);
        }

        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.contest_title);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getTitle());
            llBasic.addView(view1);
        }
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.packaze);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getPackaze());
            llBasic.addView(view1);
        }
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.gateway);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getGateway());
            llBasic.addView(view1);
        }
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.status);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getStatus());
            llBasic.addView(view1);
        }
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.amount);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getAmount());
            llBasic.addView(view1);
        }
        {
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.date);
            ((TextView) view1.findViewById(R.id.tv2)).setText(data.getDate());
            llBasic.addView(view1);
        }
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    public void initScreenData() {
        //getMapValues();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }

                try {
                    HttpRequestVO request = new HttpRequestVO(URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                hideAllLoaders();

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }
                                        EventResponse resp = new Gson().fromJson(response, EventResponse.class);
                                        result = resp.getResult();
                                        if (null != result.getTransactions())
                                            categoryList.addAll(result.getTransactions());
                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }

                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
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
        swipeRefreshLayout.setRefreshing(false);
    }

   /* public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }*/

    private void updateAdapter() {
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.no_package_available);
        v.findViewById(R.id.llNoData).setVisibility(categoryList.size() > 0 ? View.GONE : View.VISIBLE);

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
        switch (object1) {

            case Constant.Events.MUSIC_MAIN:
                showUpdateDialog(categoryList.get(postion));
                break;

        }
        return false;
    }


}
