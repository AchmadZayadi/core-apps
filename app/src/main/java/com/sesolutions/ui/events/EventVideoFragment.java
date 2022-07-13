package com.sesolutions.ui.events;


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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.ui.video.VideoAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class EventVideoFragment extends EventHelper<VideoAdapter> implements View.OnClickListener, OnLoadMoreListener {

    public RecyclerView recyclerView;
    private boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public String searchKey;
    public EventResponse.Result result;
    public ProgressBar pb;
    public int loggedinId;
    private Map<String, Object> map;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_discussion, container, false);
        applyTheme(v);
        selectedScreen = UpcomingEventFragment.TYPE_VIDEO;

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.video);
            initScreenData();
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }

        return v;
    }

    private void showHideUpperLayout() {
        if (null != result.getPostButton()) {
            v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getPostButton().getLabel());
            v.findViewById(R.id.cvPost).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvPost).setVisibility(View.GONE);
        }
    }

    public void init() {

        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

    }

    public void setRecyclerView() {
        try {
            eventVideoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideoAdapter(eventVideoList, context, this, this, Constant.FormType.TYPE_MUSIC_ALBUM);
            adapter.setEvent(true);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.cvPost:
                    // Map<String, Object> map = new HashMap<>();
                    //  map.put(Constant.KEY_PARENT_ID, );
                    //   super.openFormFragment(Constant.FormType.CREATE_VIDEO, map, Constant.URL_CREATE_EVENT_VIDEO);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateEventVideoForm.newInstance(Constant.FormType.CREATE_EVENT_VIDEO, map, Constant.URL_CREATE_EVENT_VIDEO)).addToBackStack(null).commit();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_EVENT_VIDEOS);
                    if (null != map) {
                        request.params.putAll(map);
                    }
                    Map<String, Object> map1 = activity.filteredMap;
                    if (null != map1) {
                        request.params.putAll(map1);
                    }
                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    }

                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (null != parent)
                                            parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
                                        // parent.updateLoadStatus(selectedScreen, true);
                                        EventResponse resp = new Gson().fromJson(response, EventResponse.class);
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            eventVideoList.clear();
                                        }
                                        wasListEmpty = eventVideoList.size() == 0;
                                        result = resp.getResult();
                                        eventVideoList.addAll(result.getVideos());
                                        showHideUpperLayout();
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

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
            // parent.updateTotal(0, result.getTotal());
        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_VIDEO);
        v.findViewById(R.id.llNoData).setVisibility(eventVideoList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static EventVideoFragment newInstance(EventParentFragment parent) {
        EventVideoFragment frag = new EventVideoFragment();
        frag.parent = parent;
        return frag;
    }

    public static EventVideoFragment newInstance(Map<String, Object> map) {
        EventVideoFragment frag = new EventVideoFragment();
        frag.map = map;
        return frag;
    }

    boolean istoolbar=false;
    public static EventVideoFragment newInstance(Map<String, Object> map,boolean istoolbar) {
        EventVideoFragment frag = new EventVideoFragment();
        frag.map = map;
        frag.istoolbar = istoolbar;
        return frag;
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

   /* public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                goToViewFragment(postion);
                break;

        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(eventVideoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
