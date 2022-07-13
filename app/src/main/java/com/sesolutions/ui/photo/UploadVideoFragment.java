package com.sesolutions.ui.photo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class UploadVideoFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener, OnUserClickedListener<Integer, String> {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private CommonResponse.Result result;
    private ProgressBar pb;
    private VIdeoUploadAdapter adapterPhoto;
    private int channelId;
    private List<Videos> photoList;
    private String title;
    private View v;
    private String key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        /*if (v != null) {
            return v;
        }*/
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
        v.findViewById(R.id.ivDone).setVisibility(View.VISIBLE);
    }

    private void updateToolbar() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title + " (" + result.getTotal() + ")");
        }
        if (null != result.getMenus()) {
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.filter));
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            v.findViewById(R.id.ivDone).setOnClickListener(this);
            v.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        try {
            photoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapterPhoto = new VIdeoUploadAdapter(photoList, context, this, this);
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

                case R.id.ivDone:
                    getSelectedVideoIds();
                    break;

                case R.id.bRefresh:
                    callMusicAlbumApi(1, null);
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
        callMusicAlbumApi(1, null);
    }

    private void callMusicAlbumApi(final int req, String filterKey) {

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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_LIST);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (null != filterKey) {
                        request.params.put(Constant.KEY_DATA, filterKey);

                    }
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

                                        //  if (null != result.getVideoList())
                                        photoList.addAll(result.getVideoList());
                                        for (int i = 0; i < photoList.size(); i++) {
                                            photoList.get(i).setSelected(photoList.get(i).isAlreadyAdded());
                                        }
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
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_VIDEO);
        v.findViewById(R.id.tvNoData).setVisibility(photoList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public static UploadVideoFragment newInstance(int artistId, String title) {
        UploadVideoFragment frag = new UploadVideoFragment();
        frag.channelId = artistId;
        frag.title = title;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, key);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                photoList.get(postion).setSelected(!(Boolean.parseBoolean(object2)));
                adapterPhoto.notifyItemChanged(postion);
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

    private void callSubmitVideoApi(String selectedIds, String deletedIds) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_SUBMIT_VIDEO);

                    request.params.put(Constant.KEY_CHANNEL_ID, channelId);
                    request.params.put(Constant.KEY_VIDEO_IDS, selectedIds);
                    request.params.put(Constant.KEY_DELETED_VIDEO_IDS, deletedIds);
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
                                    BaseResponse<String> err = new Gson().fromJson(response, BaseResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        Util.showSnackbar(v, err.getResult());
                                        onBackPressed();
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

    private void getSelectedVideoIds() {
        String selectedIds = "";
        String deletedIds = "";
        for (Videos vo : photoList) {
            if (vo.isSelected() && !vo.isAlreadyAdded()) {
                selectedIds += "," + vo.getVideoId();
            } else if (!vo.isSelected() && vo.isAlreadyAdded()) {
                deletedIds += "," + vo.getVideoId();
            }
        }
        boolean b1 = TextUtils.isEmpty(selectedIds);
        boolean b2 = TextUtils.isEmpty(deletedIds);
        if (!b1) {
            selectedIds = selectedIds.substring(1);
        }
        if (!b2) {
            deletedIds = deletedIds.substring(1);
        }

        if (b1 && b2) {
            Util.showSnackbar(v, "nothing selcted or diselected");
        } else {
            callSubmitVideoApi(selectedIds, deletedIds);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId() - 10;
        Options opt = result.getMenus().get(itemId - 1);
        photoList.clear();
        result = null;
        key = opt.getName();
        callMusicAlbumApi(1, key);
        /*switch (opt.getName()) {
            case Constant.OptionType.CREATED:
                callMusicAlbumApi(1,key);
                break;
            case Constant.OptionType.RATED:

                callMusicAlbumApi(1,key);
                break;
            case Constant.OptionType.WATCH_LATER:

                callMusicAlbumApi(1,key);
                break;
            case Constant.OptionType.LIKED:

                callMusicAlbumApi(1,key);
                break;
        }*/
        return false;
    }

   /* private void goToUploadPhoto() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, UploadPhotoFragment.newInstance(channelId, Constant.TITLE_UPLOAD_PHOTOS))
                .addToBackStack(null)
                .commit();
    }*/
}
