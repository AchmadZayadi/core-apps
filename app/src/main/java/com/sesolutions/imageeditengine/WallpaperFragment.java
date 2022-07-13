package com.sesolutions.imageeditengine;


import android.content.Context;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.droidninja.imageeditengine.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.unsplash.SesWallpaper;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import org.apache.http.client.methods.HttpGet;

import java.util.ArrayList;
import java.util.List;

public class WallpaperFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private WallpaperAdapter adapter;
    private View pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etMusicSearch;
    public String query;

    private List<SesWallpaper> wallpaperList;
    private OnUserClickedListener<Integer, Object> mListener;

    public static WallpaperFragment newInstance(List<SesWallpaper> wallpaperList) {
        WallpaperFragment fragment = new WallpaperFragment();
        fragment.wallpaperList = wallpaperList;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        //  v = inflater.inflate(R.layout.layout_toolbar_list_refresh_offset, container, false);
        v = inflater.inflate(R.layout.layout_search_list_filter_offset, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        //((TextView)v.findViewById(R.id.tvTitle)).setText(R.string.wallpaper);
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        //  swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        try {

            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new WallpaperAdapter(wallpaperList, context, this);
            adapter.setGrid(true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    public void initScreenData() {
        int REQ_CODE;
        if (null != wallpaperList) {
            REQ_CODE = REQ_LOAD_MORE;
        } else {
            wallpaperList = new ArrayList<>();
            REQ_CODE = 1;
        }

        init();
        setRecyclerView();
        callMusicAlbumApi(REQ_CODE);


        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(R.string.search_wallpaper);
        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(WallpaperFragment.this).show(fragmentManager, "tts");
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
                wallpaperList.clear();
                adapter.notifyDataSetChanged();
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH);

                return true;
            }
            return false;
        });

    }

    private void callMusicAlbumApi(final int req) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            if (req == REQ_LOAD_MORE) {
                pb.setVisibility(View.VISIBLE);
            } else if (req == 1) {
                showBaseLoader(true);
            }

            try {
                String url;
                int mPage = (wallpaperList.size() / 10) + 1;
                if (!TextUtils.isEmpty(query)) {
                    url = Constant.URL_UNPLASH_PHOTOS_SEARCH + mPage + "&query=" + query;
                } else {
                    url = Constant.URL_UNPLASH_PHOTOS + mPage;
                }

                HttpRequestVO request = new HttpRequestVO(url);
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                }

                request.requestMethod = HttpGet.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideAllLoaders();
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            JsonElement element = new Gson().fromJson(response, JsonElement.class);
                            if (element.isJsonArray()) {
                                JsonArray arr = element.getAsJsonArray();
                                for (JsonElement jsonElement : arr) {
                                    wallpaperList.add(new Gson().fromJson(jsonElement, SesWallpaper.class));
                                }

                            } else if (element.isJsonObject()) {
                                if (element.getAsJsonObject().has("results")) {
                                    JsonArray arr = element.getAsJsonObject().get("results").getAsJsonArray();
                                    for (JsonElement jsonElement : arr) {
                                        wallpaperList.add(new Gson().fromJson(jsonElement, SesWallpaper.class));
                                    }
                                }
                            }

                            updateAdapter();
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideAllLoaders();
                CustomLog.e(e);
            }

        } else {
            notInternetMsg(v);
        }

    }


    private void hideAllLoaders() {
        hideBaseLoader();
        isLoading = false;
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
        swipeRefreshLayout.setRefreshing(false);
    }


    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_wallpaper);
        v.findViewById(R.id.llNoData).setVisibility(wallpaperList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public void onLoadMore() {
        try {
            if (!isLoading) {
                callMusicAlbumApi(REQ_LOAD_MORE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.LOAD_MORE:
                onLoadMore();
                break;
            case Constant.Events.BG_ATTACH:
                mListener.onItemClicked(Constants.Events.TASK, wallpaperList.get(postion).getDownload(), Constants.TASK_WALLPAPER);
                onBackPressed();
                break;

            case Constant.Events.TTS_POPUP_CLOSED:
                query = "" + object2;
                etMusicSearch.setText(query);
                wallpaperList.clear();
                adapter.notifyDataSetChanged();
                callMusicAlbumApi(1);
                break;

        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserClickedListener) {
            mListener = (OnUserClickedListener<Integer, Object>) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onBackPressed() {
        activity.currentFragment = null;
        fragmentManager.popBackStack();
    }
}
