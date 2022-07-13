package com.sesolutions.ui.crowdfunding;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.fund.Donor;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FundDonorFragment extends BaseFragment implements OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final int REQ_SEARCH = 2;
    private static final int REQ_LOAD_MORE = 3;
    private View v;

    public RecyclerView recyclerView;
    private List<Donor> friendList;
    private FundDonorAdapter adapter;
    private FundResponse.Result result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    private Bundle bundle;
    private String url;
    private String resourceType;
    private AppCompatEditText etMusicSearch;
    private String query;
    private Map<String, Object> map;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String waiting;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_member, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
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
            url = bundle.getString(Constant.KEY_URI);
            map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

        v.findViewById(R.id.cvWaiting).setOnClickListener(this);
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(R.string.search_donor);

        v.findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            query = "";
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(FundDonorFragment.this).show(fragmentManager, "tts");
        });
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // android.support.transition.TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });

        etMusicSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard();
                query = etMusicSearch.getText().toString();
                // if (!TextUtils.isEmpty(query)) {
                result = null;
                friendList.clear();
                adapter.notifyDataSetChanged();
                callNotificationApi(REQ_SEARCH);
                //   }
                return true;
            }
            return false;
        });

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FundDonorAdapter(friendList, context, this, this);
            adapter.setType(resourceType);
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
        callNotificationApi(Constant.REQ_CODE_REFRESH);
    }

    private void callNotificationApi(final int REQ) {
        try {
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
                    HttpRequestVO request = new HttpRequestVO(url);
                    if (map != null)
                        request.params.putAll(map);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (!TextUtils.isEmpty(query)) {
                        request.params.put(Constant.KEY_SEARCH, query);
                    }
                    if (null != waiting) {
                        request.params.put("waiting", waiting);
                    }
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
                                FundResponse resp = new Gson().fromJson(response, FundResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    result = resp.getResult();

                                    //clear all saved data in case of "Not searching"
                                    if (REQ != REQ_LOAD_MORE) {
                                        friendList.clear();
                                    }
                                    wasListEmpty = friendList.size() == 0;

                                    if (null != resp.getResult().getDonors()) {
                                        friendList.addAll(resp.getResult().getDonors());
                                    }
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    //goIfPermissionDenied(err.getError());
                                }
                            } else {
                                notInternetMsg(v);
                            }
                            showHideTabOptions();
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
        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void showHideTabOptions() {

        if (null != result && result.getOptions() != null) {
            v.findViewById(R.id.cvWaiting).setVisibility(View.VISIBLE);
            //((TextView) v.findViewById(R.id.tvPost)).setText(result.getOptions().getLabel());
        } else {
            v.findViewById(R.id.cvWaiting).setVisibility(View.GONE);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        swipeRefreshLayout.setRefreshing(false);
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
    }

    private void updateRecyclerView() {
        isLoading = false;
        // updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_donor);
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

    public static FundDonorFragment newInstance(Bundle bundle) {
        FundDonorFragment frag = new FundDonorFragment();
        frag.bundle = bundle;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goToProfileFragment(friendList.get(postion).getUserId(), (FundDonorAdapter.ContactHolder) value, postion);
                break;

            case Constant.Events.FEED_FILTER_OPTION:
                result.getMenus().get(postion);
                break;

            case Constant.Events.TTS_POPUP_CLOSED:
                query = "" + value;
                etMusicSearch.setText(query);
                result = null;
                friendList.clear();
                adapter.notifyDataSetChanged();
                callNotificationApi(REQ_SEARCH);
                break;
        }

        return false;

    }

    public void goToProfileFragment(int userId, FundDonorAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = friendList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, friendList.get(position).getPhoto());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ViewProfileFragment.newInstance(userId, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(userId);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvWaiting:
                break;
        }
    }
}
