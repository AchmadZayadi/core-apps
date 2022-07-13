package com.sesolutions.ui.postfeed;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Feeling;
import com.sesolutions.responses.feed.Feelings;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class FeelingFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private View v;

    TextView tvTitle;

    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private FeelingAdapter adapter;
    private List<Feeling> feelingList;
    private String searchKey = "";
    private CommonResponse.Result result;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_feeling, container, false);
        try {
            applyTheme(v);
            init();
            callFeelingApi(REQ_CODE_FEELING);
            setRecyclerView();
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        closeKeyboard();
                        searchKey = etSearch.getText().toString();
                        if (!TextUtils.isEmpty(searchKey)) {
                            callFeelingApi(REQ_CODE_SEARCH);
                        }
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        pb = v.findViewById(R.id.pb);
        etSearch = v.findViewById(R.id.etSearch);
        recyclerView = v.findViewById(R.id.rvFeeling);

        GradientDrawable gdr = (GradientDrawable) etSearch.getBackground();
        gdr.setColor(SesColorUtils.getForegroundColor(context));
        etSearch.setBackground(gdr);
    }

    private void setRecyclerView() {
        try {
            feelingList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FeelingAdapter(feelingList, context, this, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
    }


    private void callFeelingApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_FEELINGS);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD * 2);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);


                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
//                                    response = response.replace("â\u0080\u0099", "'");
                                    CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                    result = comResp.getResult();
                                    if (req == REQ_CODE_SEARCH) {
                                        feelingList.clear();
                                    }
                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        feelingList.addAll(comResp.getResult().getFeelings());
                                        updateFeelingAdapter();
                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
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

    private void updateFeelingAdapter() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        pb.setVisibility(View.GONE);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_FEELINGS);
        v.findViewById(R.id.tvNoData).setVisibility(feelingList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(String object1, String object2, int postion) {
        Feeling vo = feelingList.get(postion);
        Feelings vos = new Feelings();
        vos.setTitle(vo.getTitle());
        vos.setIcon(vo.getIcon());
        vos.set_string(Constant.IS_);
        vos.setFeeling_title(Constant.FEELING);
        (activity).activity.setFeelings(vos);
        (activity).setFeelings(vo);
        onBackPressed();
        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callFeelingApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
