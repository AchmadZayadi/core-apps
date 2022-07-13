package com.sesolutions.ui.choose_album;


import android.os.Bundle;
import android.os.Handler;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.Result;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectAlbumFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String> {

    private static final int CODE_ALBUM = 100;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result resultA;
    private ProgressBar pb;
    private String uploadUrl;
    private List<Albums> lists;
    private boolean isAlbumSelected;

    public AlbumAdapter adapter;
    public View v;
    private List<Albums> albumsList;
    private Map<String, Object> map;
    /*    private int loggedinId;*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_artist, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void initScreenData() {

        init();
        albumsList = new ArrayList<>();
//        loggedinId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        setRecyclerView();
        callMusicAlbumApi(CODE_ALBUM, Constant.URL_GET_ALBUM);
    }


    private void init() {
        try {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_SELECT_ALBUM);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            v.findViewById(R.id.bRefresh).setOnClickListener(this);
            v.findViewById(R.id.ivSearch).setVisibility(View.GONE);
            v.findViewById(R.id.ivDone).setVisibility(View.GONE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView() {
        try {
            lists = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AlbumAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
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
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callMusicAlbumApi(final int req, String url) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != resultA ? resultA.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);
                                    resultA = resp.getResult();
                                    if (null != resultA.getAlbums()) {
                                        albumsList.addAll(resultA.getAlbums());
                                        lists.addAll(resultA.getAlbums());
                                    }

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
        ((TextView) v.findViewById(R.id.tvNoData)).setText(isAlbumSelected ? R.string.MSG_NO_ALBUMS :
                R.string.MSG_NO_PLAYLIST);
        recyclerView.setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.tvNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public static SelectAlbumFragment newInstance(String uploadUrl, Map<String, Object> map) {
        SelectAlbumFragment frag = new SelectAlbumFragment();
        frag.map = map;
        frag.uploadUrl = uploadUrl;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (isAlbumSelected && resultA != null && !isLoading) {
                if (resultA.getCurrentPage() < resultA.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, Constant.URL_GET_ALBUM);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, String value, int postion) {
        CustomLog.d("" + value, "" + object1);
        switch (object1) {

            case Constant.Events.MUSIC_MAIN:
                geToPhotSelectFragment(lists.get(postion).getAlbumId());
                break;
        }
        return false;
    }

    private void geToPhotSelectFragment(int albumId) {
       /* if (map == null) map = new HashMap<>();
        map.put(Constant.KEY_ALBUM_ID, albumId);*/
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , PhotoSelectFragment.newInstance(map, albumId, uploadUrl))
                .addToBackStack(null)
                .commit();
    }
}
