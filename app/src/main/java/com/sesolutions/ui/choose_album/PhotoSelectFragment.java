package com.sesolutions.ui.choose_album;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.photo.PhotoSelectAdapter;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoSelectFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, String>, OnLoadMoreListener {

    private static final int REQ_FETCH_PHOTO = 101;
    private static final int REQ_SUBMIT_PHOTO = 102;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private Result result;
    private ProgressBar pb;
    private PhotoSelectAdapter adapterPhoto;
    private List<Albums> photoList;
    public View v;
    private String url;
    private Map<String, Object> map;
    private int photoAlbumId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_artist, container, false);
        applyTheme(v);
        initScreenData();
        callGetPhotoApi(REQ_FETCH_PHOTO);
        return v;
    }

    private void init() {
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);

        // v = getView();
        // if (!((MusicParentFragment) getParentFragment()).isBlogLoaded) {

        try {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_SELECT_PHOTO);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            v.findViewById(R.id.bRefresh).setOnClickListener(this);
            //  ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp));
            v.findViewById(R.id.ivSearch).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView() {
        try {
            photoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapterPhoto = new PhotoSelectAdapter(photoList, context, this, this);
            recyclerView.setAdapter(adapterPhoto);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callGetPhotoApi(final int req) {
        String url = Constant.URL_GET_PHOTOS;
        if (req == REQ_SUBMIT_PHOTO) {
            url = this.url;
        }
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
                    if (req == REQ_SUBMIT_PHOTO) {
                        if (url.equals(Constant.URL_MEMBER_UPLOAD_PHOTO)) {
                            /* updating uservo in share preference
                             * ,so that when user goes back to main activity
                             * ,his profile pic will be updated
                             */
                            UserMaster vo = SPref.getInstance().getUserMasterDetail(context);
                            vo.setPhotoUrl((String) map.get(Constant.KEY_URI));
                            SPref.getInstance().saveUserMaster(context, vo,null);
                            map.remove(Constant.KEY_URI);
                        }
                        request.params.putAll(map);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                        request.params.put(Constant.KEY_ALBUM_ID, photoAlbumId);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
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

                                        if (req == REQ_SUBMIT_PHOTO) {
                                            goDoubleback();
                                        } else {
                                            AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);
                                            result = resp.getResult();
                                            if (null != result.getPhotos()) {
                                                photoList.addAll(result.getPhotos());
                                            }
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


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

               /* case R.id.ivSearch:
                    callUploadApi();
                    // showPopup(result.getMenus(), v, 10);
                    break;*/

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView();
    }


    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapterPhoto.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_PHOTO_SELECTED);
        v.findViewById(R.id.tvNoData).setVisibility(photoList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public static PhotoSelectFragment newInstance(Map<String, Object> map, int photoAlbumId, String url) {
        PhotoSelectFragment frag = new PhotoSelectFragment();
        frag.map = map;
        frag.url = url;
        frag.photoAlbumId = photoAlbumId;
        return frag;
    }
    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        map.put(Constant.KEY_PHOTO_ID, photoList.get(postion).getPhotoId());
        map.put(Constant.KEY_URI, photoList.get(postion).getPhotos().getMain());
        callGetPhotoApi(REQ_SUBMIT_PHOTO);
        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callGetPhotoApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
