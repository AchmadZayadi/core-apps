package com.sesolutions.ui.member;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreMemberFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQ_LOAD_MORE = 202;
    private View v;

    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private MoreMemberAdapter adapter;
    private List<UserMaster> emotionList;
    private CommonResponse.Result result;
    private Bundle bundle;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list3, container, false);
        try {
            applyTheme(v);
            init();
            setRecyclerView();
            callStickerApi(1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        try {
            pb = v.findViewById(R.id.pb);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(bundle.getString("title", getStrings(R.string._members)));
            recyclerView = v.findViewById(R.id.recyclerView);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            setUpModuleData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private String URL, ADAPTER_TYPE;
    private Map<String, Object> requestMap;

    private void setUpModuleData() {
        String selectedModule = bundle.getString(Constant.KEY_MODULE, "");
        requestMap = new HashMap<>();
        switch (selectedModule) {
            case Constant.ResourceType.PAGE:
            case Constant.ResourceType.PAGE_POLL:
            case MenuTab.Page.TYPE_BROWSE_POLL:
            case MenuTab.Page.TYPE_PROFILE_POLL:
                URL = Constant.URL_PAGE_POLL_VOTED_USER;
                requestMap.put("option_id", bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = "poll";
                break;
            case Constant.ResourceType.GROUP:
            case Constant.ResourceType.GROUP_POLL:
            case MenuTab.Group.TYPE_BROWSE_POLL:
            case MenuTab.Group.TYPE_PROFILE_POLL:
                URL = Constant.URL_GROUP_POLL_VOTED_USER;
                requestMap.put("option_id", bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = "poll";
                break;
            case Constant.ResourceType.BUSINESS:
            case Constant.ResourceType.BUSINESS_POLL:
            case MenuTab.Business.TYPE_BROWSE_POLL:
            case MenuTab.Business.TYPE_PROFILE_POLL:
                URL = Constant.URL_BUSINESS_POLL_VOTED_USER;
                requestMap.put("option_id", bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = "poll";
                break;
            case MenuTab.Page.INFO:
                URL = Constant.URL_PAGE_INFO_MEMBER;
                requestMap.put(Constant.KEY_TYPE, bundle.getString(Constant.KEY_TYPE));
                requestMap.put(Constant.KEY_PAGE_ID, bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = bundle.getString(Constant.KEY_TITLE);
                break;
            case MenuTab.Group.INFO:
                URL = Constant.URL_GROUP_INFO_MEMBER;
                requestMap.put(Constant.KEY_TYPE, bundle.getString(Constant.KEY_TYPE));
                requestMap.put(Constant.KEY_GROUP_ID, bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = bundle.getString(Constant.KEY_TITLE);
                break;
            case MenuTab.Business.INFO:
                URL = Constant.URL_BUSINESS_INFO_MEMBER;
                requestMap.put(Constant.KEY_TYPE, bundle.getString(Constant.KEY_TYPE));
                requestMap.put(Constant.KEY_BUSINESS_ID, bundle.getInt(Constant.KEY_ID));
                ADAPTER_TYPE = bundle.getString(Constant.KEY_TITLE);
                break;

        }
    }

    private void setRecyclerView() {
        try {
            emotionList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MoreMemberAdapter(emotionList, context, this, this, ADAPTER_TYPE);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        callStickerApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callStickerApi(final int req) {

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
                    request.params.putAll(requestMap);

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        pb.setVisibility(View.GONE);
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {

                                if (req == Constant.REQ_CODE_REFRESH) {
                                    emotionList.clear();
                                }
                                CommonResponse comResp = new Gson().fromJson(response, CommonResponse.class);
                                result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    if (null != comResp.getResult().getGroupMembers()) {
                                        emotionList.addAll(comResp.getResult().getGroupMembers());
                                    }
                                    updateFeelingAdapter();
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
                                    goIfPermissionDenied(comResp.getError());
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
        try {
            adapter.notifyDataSetChanged();
            pb.setVisibility(View.GONE);
            isLoading = false;
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_USER);
            v.findViewById(R.id.llNoData).setVisibility(emotionList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                goToProfileFragment((MoreMemberAdapter.ContactHolder) object2, postion);
                break;
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callStickerApi(REQ_LOAD_MORE);

                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void goToProfileFragment(MoreMemberAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = emotionList.get(position).getName();
            String imageUrl = emotionList.get(position).getProfileImageUrl();
            if (TextUtils.isEmpty(transitionName)) {
                transitionName = emotionList.get(position).getDisplayname();
                imageUrl = emotionList.get(position).getOwnerPhoto();
            }
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, imageUrl);
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    //.addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ViewProfileFragment.newInstance(emotionList.get(position).getUserId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(emotionList.get(position).getUserId());
        }
    }

    public static MoreMemberFragment newInstance(Bundle bundle) {
        MoreMemberFragment frag = new MoreMemberFragment();
        frag.bundle = bundle;
        return frag;
    }
}
