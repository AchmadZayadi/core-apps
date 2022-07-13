package com.sesolutions.ui.games;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameBrowseFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    public String searchKey;
    public com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    public String txtNoData = Constant.MSG_NO_GAME_FOUND;
    public SwipeRefreshLayout swipeRefreshLayout;
    TextView tvTitle;
    ImageView ivSearch;



    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);

        applyTheme(v);

        v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
        tvTitle=v.findViewById(R.id.tvTitle);
        ivSearch=v.findViewById(R.id.ivSearch);
        tvTitle.setText(getStrings(R.string.TAB_GAME1));

        ivSearch.setVisibility(View.VISIBLE);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container, new SearchGameFragment()).addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        initScreenData();


        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
    }

    public GameBrowesAdapter adapter23;
    public List<GameBrowsModel.ResultBean.GamesBean> videoList231;

    public void setRecyclerView() {
        try {
            videoList231 = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter23 = new GameBrowesAdapter(videoList231, context, this, this);
            recyclerView.setAdapter(adapter23);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
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
        init();
        setRecyclerView();
        result = null;
        callMusicAlbumApi(1);
    }



    public void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        if (req != Constant.REQ_CODE_REFRESH)
                            showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GAME_BROWES);


                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    }
                    request.params.put(Constant.KEY_LIMIT, 100);

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
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;
                                setRefreshing(swipeRefreshLayout, false);
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList231.clear();
                                        }
                                        GameBrowsModel resp = new Gson().fromJson(response, GameBrowsModel.class);

                                        try {
                                            if(resp.getResult().getGames()!=null && resp.getResult().getGames().size()>0){
                                                videoList231.addAll(resp.getResult().getGames());

                                            }
                                        }catch (Exception ex){
                                            ex.printStackTrace();
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
                setRefreshing(swipeRefreshLayout, false);

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        hideLoaders();
        //adapter.setPermission(result.getPermission());
        adapter23.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        /*if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }*/
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList231.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.tvNoData).setVisibility(videoList231.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static GameBrowseFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        GameBrowseFragment frag = new GameBrowseFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
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
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_TITLE:

                fragmentManager.beginTransaction().replace(R.id.container,
                        ViewGameFragment.newInstance(this,postion
                        )).addToBackStack(null).commit();
                break;

        }
        return false;
    }



   /*

    private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
