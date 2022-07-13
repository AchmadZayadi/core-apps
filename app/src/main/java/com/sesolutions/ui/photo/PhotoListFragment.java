package com.sesolutions.ui.photo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ChannelPhoto;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoListFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private CommonResponse.Result result;
    private ProgressBar pb;
    private PhotoAdapter adapterPhoto;
    private int channelId;
    private List<ChannelPhoto> photoList;
    private String title;
    private ChannelPhoto vo;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        v = inflater.inflate(R.layout.fragment_view_artist, container, false);
        initScreenData();
        return v;
    }

    private void init() {
        applyTheme(v);
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.bRefresh).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setVisibility(View.GONE);
    }

    private void updateToolbar() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title + " (" + result.getTotal() + ")");
        }
        if (null != result.getMenus()) {
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.vertical_dots));
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            v.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
        }
    }


    private void setRecyclerView() {
        try {
            photoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapterPhoto = new PhotoAdapter(photoList, context, this, this, Constant.FormType.TYPE_MUSIC_ALBUM);
            recyclerView.setAdapter(adapterPhoto);

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
                    showPopup(result.getMenus(), v, 10);
                    break;

                case R.id.bRefresh:
                    callMusicAlbumApi(1);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
        PopupMenu menu = new PopupMenu(context, v);
        for (int index = 0; index < menus.size(); index++) {
            Options s = menus.get(index);
            menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
        }
        menu.show();
        menu.setOnMenuItemClickListener(this);
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
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_PHOTO_LIST);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_CHANNEL_ID, channelId);
                    request.params.put(Constant.KEY_RESOURCE_ID, channelId);
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
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {
                                        result = resp.getResult();
                                        if (null != result.getPhotos())
                                            photoList.addAll(result.getPhotos());
                                        updateToolbar();

                                    } else {
                                        Util.showSnackbar(v, resp.getErrorMessage());
                                    }
                                    updateAdapter();
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
        adapterPhoto.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_PHOTO);
        v.findViewById(R.id.llNoData).setVisibility(photoList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static PhotoListFragment newInstance(int artistId, String title) {
        PhotoListFragment frag = new PhotoListFragment();
        frag.channelId = artistId;
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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                vo = photoList.get(postion);
                goToGalleryFragment(Constant.ResourceType.CHANNEL_PHOTO
                        , vo.getAlbumId()
                        , vo.getPhotoId()
                        , Constant.ResourceType.CHANNEL_PHOTO
                        , vo.getImages().getMain()
                );
                break;
            case Constant.Events.MUSIC_LIKE:
                callLikeApi(postion, photoList.get(postion).isContentLike());
                break;


        }
        return false;
    }

    /* private void goToViewCategoryFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(videoList.get(postion).getArtistId()))
                .addToBackStack(null)
                .commit();
    }*/

    private void callLikeApi(final int position, final boolean isLiked) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_MUSIC_LIKE);
                    int resourceId = photoList.get(position).getChanelphotoId();
                    String resourceType = Constant.ResourceType.CHANNEL_PHOTO;

                    request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        photoList.get(position).setContentLike(!isLiked);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId() - 10;
        Options opt = result.getMenus().get(itemId - 1);

        switch (opt.getName()) {
            case Constant.OptionType.ADD_MORE_PHOTOS:
                goToUploadPhoto();
                break;
        }
        return false;
    }

    private void goToUploadPhoto() {
        Map<String, Object> map = new HashMap<>();

        map.put(Constant.KEY_CHANNEL_ID, channelId);
        map.put(Constant.KEY_RESOURCE_ID, channelId);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CHANNEL_PHOTO);

        fragmentManager.beginTransaction()
                .replace(R.id.container, UploadPhotoFragment.newInstance(map, Constant.URL_UPLOAD_CHANNEL_PHOTO, Constant.TITLE_UPLOAD_PHOTOS))
                .addToBackStack(null)
                .commit();
    }
}
