package com.sesolutions.ui.member;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.profile.SuggestionViewFragment;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberListFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQ_LOAD_MORE = 100;
    public View v;
    public List<Notifications> albumsList;
    public RecyclerView recyclerView;
    public MemberAdapter adapter;
    public boolean isLoading;
    public CommonResponse.Result result;
    public ProgressBar pb;
    public int userId;
    public String search;
    public SwipeRefreshLayout swipeRefreshLayout;
    private SuggestionViewFragment parent;

    public static MemberListFragment newInstance(int userId, SuggestionViewFragment parent) {
        MemberListFragment frag = new MemberListFragment();
        frag.userId = userId;
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));

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


    public void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MemberAdapter(albumsList, context, this, this);
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
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_MEMBER_BROWSE);
                    if (userId > 0) {
                        request.params.put(Constant.KEY_FRIEND_ID, userId);
                    }

                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    if (!TextUtils.isEmpty(search)) {
                        request.params.put(Constant.KEY_SEARCH_TEXT, search);
                    }

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
                            setRefreshing(swipeRefreshLayout, false);
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            albumsList.clear();
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();

                                        if (null != result.getNotification())
                                            albumsList.addAll(result.getNotification());

                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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


    private void callMemberStatusApi(final String url, final int position, int userId) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);
                    request.params.put(Constant.KEY_BROWSE, 1);

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
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        // result = resp.getResult();
                                        if (null != resp.getResult().getMember()) {
                                         /*   Notifications vo = albumsList.get(position);
                                            vo.setMembership(resp.getResult().getMember().getMembership());
                                            albumsList.add_create(position, vo);*/
                                            if (url.contains("follow")) {
                                                albumsList.get(position).setFollow(resp.getResult().getMember().getFollow());
                                            } else {
                                                albumsList.get(position).setMembership(resp.getResult().getMember().getMembership());
                                            }
                                            adapter.notifyItemChanged(position);
                                        }
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                    CustomLog.e(e);
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

    public void updateAdapter() {
        try {
            isLoading = false;
            pb.setVisibility(View.GONE);

            //  swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            if (parent != null) {
                parent.isFriendLoaded = true;
            }
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_MEMBER);
            v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
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

        try {
            switch (object1) {
                case Constant.Events.MEMBER_ADD:
                    String type = albumsList.get(postion).getMembership().getAction();
                    String url = Constant.EMPTY;
                    if (type.equals("cancel")) {
                        url = Constant.URL_MEMBER_REJECT;
                    } else if (type.equals("add")) {
                        url = Constant.URL_MEMBER_ADD;
                    } else if (type.equals("remove")) {
                        url = Constant.URL_MEMBER_REMOVE;
                    } else if (type.equals("confirm")) {
                        url = Constant.URL_MEMBER_CONFIRM;
                    }

                    callMemberStatusApi(url, postion, albumsList.get(postion).getUserId());
                    break;

                case Constant.Events.MEMBER_FOLLOW:
                    //  type = albumsList.get(postion).getFollow().getAction();
                    callMemberStatusApi(Constant.URL_FOLLOW_MEMBER, postion, albumsList.get(postion).getUserId());
                    break;

                case Constant.Events.CLICKED_HEADER_IMAGE:
                    goToProfileFragment(albumsList.get(postion).getUserId(), (MemberAdapter.ContactHolder) object2, postion);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void goToProfileFragment(int userId, MemberAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = albumsList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, albumsList.get(position).getUserImage());
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
}
