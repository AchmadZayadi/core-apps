package com.sesolutions.ui.postfeed;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.signup.SignUpFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private View v;

    TextView tvFeeling;
    TextView tvSticker;
    TextView tvActivity;
    EditText etSearch;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private FeelingAdapter adapter;
    private List<Feeling> feelingList;
    private String searchKey = "";
    private CommonResponse.Result result;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
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
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    result = null;
                    feelingList.clear();
                    searchKey = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        callFeelingApi(REQ_CODE_SEARCH);
                    }
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void init() {
        tvFeeling = v.findViewById(R.id.tvFeeling);
        tvSticker = v.findViewById(R.id.tvSticker);
        tvActivity = v.findViewById(R.id.tvActivity);
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
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bSignIn:
                  /*  fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment())
                            .addToBackStack(null)
                            .commit();*/

                    fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                            .addToBackStack(null)
                            .commit();
                    break;

                case R.id.bSignUp:
                    fragmentManager.beginTransaction().replace(R.id.container, new SignUpFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callFeelingApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {

                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_FEELINGS_ACTIVITY);
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
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_ACTIVITIES);
        v.findViewById(R.id.tvNoData).setVisibility(feelingList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(String object1, String object2, int postion) {
        Feeling feel = feelingList.get(postion);
        goToFeelingActivity(feel, null);
        return false;
    }

    private void goToFeelingActivity(Feeling feel, String search) {
        FeelingActivityFragment fragment = FeelingActivityFragment.newInstance(feel, search);
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callFeelingApi(REQ_CODE_FEELING);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
