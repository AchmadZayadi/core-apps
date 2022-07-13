package com.sesolutions.ui.music_core;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.music.MusicBrowse;
import com.sesolutions.responses.music.Result;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCMusicFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object> {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result resultA;
    private ProgressBar pb;
    private OnUserClickedListener<Integer, Object> parent;
    private List<Albums> lists;
    public MyMusicCAdapter adapter;
    public View v;
    private String selectedScreen;
    private SwipeRefreshLayout swipeRefreshLayout;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_album3, container, false);
        applyTheme(v);
        init();
        setRecyclerView();

        return v;
    }


    private void init() {
        try {
            recyclerView = v.findViewById(R.id.rvSetting);
            pb = v.findViewById(R.id.pb);
//            ((SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout)).setEnabled(false);
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
            adapter = new MyMusicCAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {

        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(URL.CMUSIC_BROWSE);

                    request.params.put(Constant.KEY_TYPE, "manage");
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_TYPE, Constant.VALUE_MANAGE);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.params.put(Constant.KEY_PAGE, null != resultA ? resultA.getNextPage() : 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            setRefreshing(swipeRefreshLayout, false);
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        lists.clear();
                                    }
                                    MusicBrowse resp = new Gson().fromJson(response, MusicBrowse.class);


                                    resultA = resp.getResult();
                                    if (null != resultA.getAlbums()) {
                                        lists.addAll(resultA.getAlbums());
                                    }

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
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    setRefreshing(swipeRefreshLayout, false);
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                setRefreshing(swipeRefreshLayout, false);
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            setRefreshing(swipeRefreshLayout, false);
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public void showDeleteDialog(final int optionPosition, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_PLAYLIST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(optionPosition, position);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int listPosition, final int optionPosition) {

        try {
            if (isNetworkAvailable(context)) {


                try {

                    int id = lists.get(listPosition).getPlaylistId();
                    lists.remove(listPosition);
                    updateAdapter();

                    HttpRequestVO request = new HttpRequestVO(URL.CMUSIC_DELETE);
                    request.params.put(Constant.KEY_PLAYLIST_ID, id);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
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
                    hideBaseLoader();
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);

        runLayoutAnimation(recyclerView);
        setRefreshing(swipeRefreshLayout, false);
//        setRefreshing(swipeRefreshLayout, false);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, resultA.getTotal());
            parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_PLAYLIST);
        v.findViewById(R.id.llNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
      //  adapter = new MyMusicCAdapter(lists, context, this, this, Constant.FormType.TYPE_MY_ALBUMS);
        adapter = new MyMusicCAdapter(lists, context, this, this,101);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public static MyCMusicFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> parent) {
        MyCMusicFragment frag = new MyCMusicFragment();
        frag.parent = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (resultA != null && !isLoading) {
                if (resultA.getCurrentPage() < resultA.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                CMusicUtil.openViewFragment(fragmentManager, lists.get(postion).getPlaylistId());
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                Options vo = lists.get(Integer.parseInt("" + value)).getMenus().get(postion);
                performMusicOptionClick(vo, Integer.parseInt("" + value), postion);
                break;
        }
        return false;
    }


    private void performMusicOptionClick(Options vo, int listPosition, int postion) {
        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_MODULE, Constant.ModuleName.MUSIC);
                map.put(Constant.KEY_PLAYLIST_ID, lists.get(listPosition).getPlaylistId());
                fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_MUSIC_PLAYLIST, map, URL.CMUSIC_EDIT)).addToBackStack(null).commit();

                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(listPosition, postion);
                break;
        }
    }

    private void gotoFormFragment(Map<String, Object> map) {

    }
}
