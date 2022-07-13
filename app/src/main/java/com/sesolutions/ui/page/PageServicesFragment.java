package com.sesolutions.ui.page;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.page.PageServices;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageServicesFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object> {

    private View v;

    public RecyclerView recyclerView;
    private List<PageServices> friendList;
    private ServicesAdapter adapter;
    private CommonResponse.Result result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    //private int resourceId;
    private Bundle bundle;
    private String url;
    private Map<String, Object> map;
    //private int resourceType;
    //private int mPageID;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_common, container, false);
        applyTheme(v);
        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TXT_SEARCH_Service_txt);
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
            init();
            getBundle();
            setRecyclerView();
            callNotificationApi(true);
        }
    }

    private void getBundle() {
        if (bundle != null) {
            //resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            // resourceType = bundle.getInt(Constant.KEY_RESOURCES_TYPE);
            //mPageID = bundle.getInt(Constant.KEY_RESOURCE_ID);
            url = bundle.getString(Constant.KEY_URI);
            map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ServicesAdapter(friendList, context, this, this);
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
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.bRefresh:
                    callNotificationApi(true);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (showLoader) {
                    showBaseLoader(true);
                    // showView(v.findViewById(R.id.pbMain));
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
                //showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);//Constant.URL_PAGE_SERVICES);
                    request.params.putAll(map);
                    // request.params.put(Constant.KEY_PAGE_ID, mPageID);
                    //   request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    //  request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoaders();

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    isContentLoaded = true;
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();
                                        wasListEmpty = friendList.size() == 0;
                                        if (null != resp.getResult().getServices())
                                            friendList.addAll(resp.getResult().getServices());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
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
        hideBaseLoader();
    }

    private void updateRecyclerView() {
        isLoading = false;
        // updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.no_services_found);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(false);
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

    public static PageServicesFragment newInstance(Bundle bundle) {
        PageServicesFragment frag = new PageServicesFragment();
        frag.bundle = bundle;
        return frag;
    }

    boolean istoolbar=false;
    public static PageServicesFragment newInstance(Bundle bundle,boolean istoolbar) {
        PageServicesFragment frag = new PageServicesFragment();
        frag.bundle = bundle;
        frag.istoolbar = istoolbar;
        return frag;
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.FEED_MAP:
                break;
        }
        return false;
    }

}
