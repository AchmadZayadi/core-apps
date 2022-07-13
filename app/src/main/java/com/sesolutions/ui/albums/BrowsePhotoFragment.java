package com.sesolutions.ui.albums;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.sesolutions.responses.album.StaggeredAlbums;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class BrowsePhotoFragment extends PhotoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public String searchKey;
    public Result result;
    public int loggedinId;
    public int categoryId;
    public OnUserClickedListener<Integer, Object> listener;
    public String txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private ProgressBar pb;
    private StaggeredAlbums staggeredAlbums;
    private NestedScrollView mScrollView;
    private String selectedScreen;
/*    public List<Albums> videoList;
    public AlbumPhotoAdapter adapter;*/
    AppCompatImageView ivImage11_st,ivImage12_st,ivImage13_st,ivImage14_st;
    androidx.cardview.widget.CardView rlImage11,rlImage12,rlImage13,rlImage14;


    public static BrowsePhotoFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        BrowsePhotoFragment frag = new BrowsePhotoFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        frag.loggedinId = -1;
        frag.categoryId = -1;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_browse_photo, container, false);
        txtNoData = Constant.MSG_NO_ALBUM_AVAILABLE;
        applyTheme(v);



        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
    }

    private void updateHeaderView() {
        ivImage11_st = v.findViewById(R.id.ivImage11);
        ivImage12_st = v.findViewById(R.id.ivImage12);
        ivImage13_st = v.findViewById(R.id.ivImage13);
        ivImage14_st = v.findViewById(R.id.ivImage14);

        if (staggeredAlbums.getFirstAlbum() != null) {

            ivImage11_st.setVisibility(View.VISIBLE);

                Util.showImageWithGlide(ivImage11_st, staggeredAlbums.getFirstAlbum().getImages().getMain(), context, R.drawable.placeholder_square);

                ivImage11_st.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(Constant.Events.IMAGE_1, "1", 0);
                    }
                });

        } else {
            ivImage11_st.setVisibility(View.GONE);
         }
        if (staggeredAlbums.getSecondAlbum() != null) {
            Util.showImageWithGlide(ivImage12_st, staggeredAlbums.getSecondAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
            ivImage12_st.setVisibility(View.VISIBLE);
            ivImage12_st.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(Constant.Events.IMAGE_2, "2", 0);
                }
            });
        } else {
            ivImage12_st.setVisibility(View.GONE);
        }

        if (staggeredAlbums.getThirdAlbum() != null) {
            Util.showImageWithGlide(ivImage13_st, staggeredAlbums.getThirdAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
            ivImage13_st.setVisibility(View.VISIBLE);
            ivImage13_st.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(Constant.Events.IMAGE_3, "3", 0);
                }
            });
        } else {
            ivImage13_st.setVisibility(View.GONE);
        }

        if (staggeredAlbums.getFourthAlbum() != null) {
            Util.showImageWithGlide(ivImage14_st, staggeredAlbums.getFourthAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
            ivImage14_st.setVisibility(View.VISIBLE);
            ivImage14_st.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(Constant.Events.IMAGE_4, "4", 0);
                }
            });
        } else {
            ivImage14_st.setVisibility(View.GONE);
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AlbumPhotoAdapter(videoList, context, this, this, Constant.FormType.TYPE_PHOTO);
            //adapter.setLoggedInId(SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID));
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            recyclerView.setNestedScrollingEnabled(false);
            mScrollView = v.findViewById(R.id.mScrollView);

            mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                    int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                            .getScrollY()));
                     if (diff <= 20) {
                        // your pagination code
                        onLoadMore();
                    }
                }
            });
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
        /*try {
            switch (v.getId()) {


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_ALBUM_BROWSE_PHOTO);
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
                                            videoList.clear();
                                        }
                                        AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);
                                        result = resp.getResult();
                                        if (null != result.getHeaderPhotos()) {
                                            // videoList.addAll(result.getHeaderPhotos());
                                            staggeredAlbums = new StaggeredAlbums();
                                            List<Albums> list = result.getHeaderPhotos();
                                            for (int i = 0; i < list.size(); i++) {
                                                if (i == 0 && list.size() > 0) {
                                                    staggeredAlbums.setFirstAlbum(list.get(i));
                                                } else if (i == 1 && list.size() > 1) {
                                                    staggeredAlbums.setSecondAlbum(list.get(i));
                                                } else if (i == 2 && list.size() > 2) {
                                                    staggeredAlbums.setThirdAlbum(list.get(i));
                                                } else if (i == 3 && list.size() > 3) {
                                                    staggeredAlbums.setFourthAlbum(list.get(i));
                                                } else {
                                                    videoList.add(list.get(i));
                                                }
                                            }
                                            updateHeaderView();
                                        }

                                        wasListEmpty = videoList.size() == 0;
                                        if (null != result.getPhotos()) {
                                            //if (videoList.size() > 0) {
                                            videoList.addAll(result.getPhotos());
                                          /*  } else {
                                                staggeredAlbums = new StaggeredAlbums();
                                                List<Albums> list = result.getPhotos();
                                                for (int i = 0; i < list.size(); i++) {
                                                    if (i == 0 && list.size() > 0) {
                                                        staggeredAlbums.setFirstAlbum(list.get(i));
                                                    } else if (i == 1 && list.size() > 1) {
                                                        staggeredAlbums.setSecondAlbum(list.get(i));
                                                    } else if (i == 2 && list.size() > 2) {
                                                        staggeredAlbums.setThirdAlbum(list.get(i));
                                                    } else if (i == 3 && list.size() > 3) {
                                                        staggeredAlbums.setFourthAlbum(list.get(i));
                                                    } else {
                                                        videoList.add(list.get(i));
                                                    }
                                                }
                                                //set top view content of list
                                                // adapter.setStaggeredAlbums(staggeredAlbums);

                                                updateHeaderView();
                                            }*/

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
                        }
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

    private void updateAdapter() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);

        if(staggeredAlbums.getFirstAlbum()!=null || staggeredAlbums.getSecondAlbum()!=null && staggeredAlbums.getThirdAlbum()!=null || staggeredAlbums.getFourthAlbum()!=null){
            v.findViewById(R.id.llNoData).setVisibility(View.GONE);
        }else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
        }
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
            case Constant.Events.IMAGE_1:
                photoId = staggeredAlbums.getFirstAlbum().getPhotoId();
                imageUrl = staggeredAlbums.getFirstAlbum().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_2:
                photoId = staggeredAlbums.getSecondAlbum().getPhotoId();
                imageUrl = staggeredAlbums.getSecondAlbum().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_3:
                photoId = staggeredAlbums.getThirdAlbum().getPhotoId();
                imageUrl = staggeredAlbums.getThirdAlbum().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_4:
                photoId = staggeredAlbums.getFourthAlbum().getPhotoId();
                imageUrl = staggeredAlbums.getFourthAlbum().getMain();
                openLighbox(photoId, imageUrl);
                break;
            case Constant.Events.IMAGE_5:
                photoId = videoList.get(postion).getPhotoId();
                imageUrl = videoList.get(postion).getMain();
                openLighbox(photoId, imageUrl);
                break;
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    private void openLighbox(int photoId, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_TYPE, Constant.ACTIVITY_TYPE_ALBUM);
        map.put(Constant.KEY_IMAGE, imageUrl);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM_PHOTO);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }
}
