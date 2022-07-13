package com.sesolutions.ui.group_core;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.Result;
import com.sesolutions.ui.albums.AlbumPhotoAdapter;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.photo.UploadPhotoFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CGroupPhotoFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String searchKey;
    public Result result;
    public int loggedinId;
    public int categoryId;
    public OnUserClickedListener<Integer, Object> listener;
    public String txtNoData;
    //public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ProgressBar pb, pbMain;
    private String selectedScreen;
    private List<Albums> videoList;
    private AlbumPhotoAdapter adapter;
    private int mGroupId;
/*    public List<Albums> videoList;
    public AlbumPhotoAdapter adapter;*/


    public static CGroupPhotoFragment newInstance(OnUserClickedListener<Integer, Object> parent, int mGroupId, String selectedScreen) {
        CGroupPhotoFragment frag = new CGroupPhotoFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        frag.mGroupId = mGroupId;
        frag.loggedinId = -1;
        frag.categoryId = -1;
        return frag;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_discussion, container, false);
        txtNoData = Constant.MSG_NO_PHOTO_FOUND;
        applyTheme(v);
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        pbMain = v.findViewById(R.id.pbMain);

    }


    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AlbumPhotoAdapter(videoList, context, this, this, Constant.FormType.TYPE_PHOTO);
            adapter.setNestedScroll(false);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }
    /*activity.taskPerformed = Constant.TASK_ADD_MORE_PHOTO;*/
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.cvPost:

                    Map<String, Object> map = new HashMap<>();

                    map.put(Constant.KEY_GROUP_ID, mGroupId);
                    //  map.put(Constant.KEY_RESOURCE_ID, albumId);
                    //map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM);

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, UploadPhotoFragment.newInstance(map,
                                    Constant.URL_CGROUP_PHOTO_UPLOAD, getString(R.string.TITLE_UPLOAD_PHOTOS)))
                            .addToBackStack(null)
                            .commit();

                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showHideUpperLayout() {
        if (null != result.getOptions()) {
            v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getOptions().getLabel());
            v.findViewById(R.id.cvPost).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvPost).setVisibility(View.GONE);
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
                        pbMain.setVisibility(View.VISIBLE);
                        //showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CGROUP_PHOTO);
                    request.params.put(Constant.KEY_ID, mGroupId);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    } else if (categoryId > 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    }

                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);

                    if (req == Constant.REQ_CODE_REFRESH)
                        request.params.put(Constant.KEY_PAGE, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        pbMain.setVisibility(View.GONE);
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            // hideView(R.id.progress_view, false);

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }
                                    AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);
                                    result = resp.getResult();

                                    wasListEmpty = videoList.size() == 0;
                                    if (null != result.getPhotos()) {
                                        videoList.addAll(result.getPhotos());
                                    }
                                    showHideUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    // goIfPermissionDenied(err.getError());
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
                    // setRefreshing(swipeRefreshLayout, false);
                    pb.setVisibility(View.GONE);
                    pbMain.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                //setRefreshing(swipeRefreshLayout, false);

                pb.setVisibility(View.GONE);
                pbMain.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            //setRefreshing(swipeRefreshLayout, false);
            pb.setVisibility(View.GONE);
            pbMain.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateAdapter() {
        isLoading = false;
        //setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        pbMain.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        String imageUrl;
        int photoId;
        switch (object1) {

            case Constant.Events.MUSIC_MAIN:

                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_PHOTO_ID, videoList.get(postion).getPhotoId());
                map.put(Constant.KEY_TYPE, Constant.ResourceType.CORE_GROUP_PHOTO);
                map.put(Constant.KEY_IMAGE, videoList.get(postion).getImages().getMain());
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CORE_GROUP_PHOTO);

                fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                        .addToBackStack(null).commit();
                break;

        }
        return false;
    }
}
