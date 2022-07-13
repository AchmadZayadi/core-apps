package com.sesolutions.ui.resume;

import android.graphics.Color;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.ui.video.SearchVideoFragment;
import com.sesolutions.ui.video.VideoAdapter;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;

public class ViewCategoriesFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener {

    private static final String TAG = "ViewCategoriesFragment";

    private String title;
    private int categoryId;
    private ProgressBar pb;
    private String searchKey;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private RecyclerView recyclerView;
    private com.sesolutions.responses.videos.Result result;

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

    private void init() {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search));
        ((ImageView) v.findViewById(R.id.ivSearch)).setColorFilter(Color.parseColor("#000000"));
        v.findViewById(R.id.ivSearch).setOnClickListener(this);

    }

    private void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideoAdapter(videoList, context, this, this, Constant.FormType.TYPE_MUSIC_ALBUM);
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
                case R.id.ivSearch:
                    goToSearchFragment();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchVideoFragment()).addToBackStack(null).commit();
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_BROWSE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    request.params.put(Constant.KEY_RESOURCE_ID, categoryId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.VIDEO_ARTIST);

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
                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult();
                                        if (null != result.getVideos())
                                            videoList.addAll(result.getVideos());

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
        adapter.setPermission(result.getPermission());
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        //  ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SENT_MSG);
        //  v.findViewById(R.id.llNoData).setVisibility(feedActivityList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static ViewCategoriesFragment newInstance(int artistId, String title) {
        ViewCategoriesFragment frag = new ViewCategoriesFragment();
        frag.categoryId = artistId;
        frag.title = title;
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
                goToViewCategoryFragment(postion);
                break;

        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void goToViewCategoryFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(videoList.get(postion).getArtistId()))
                .addToBackStack(null)
                .commit();
    }*/
}
