package com.sesolutions.ui.photo;


import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ChannelPhoto;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.dashboard.ApiHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPhotoFragment extends ApiHelper implements View.OnClickListener,OnUserClickedListener<Integer,Object> {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private CommonResponse.Result result;
    private ProgressBar pb;
    private PhotoAdapter adapterPhoto;
    private List<ChannelPhoto> photoList;
    private String title;
    public View v;
    private String url;
    private Map<String, Object> map;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_artist, container, false);
        applyTheme(v);
        initScreenData();
//        showImageDialog(Constant.MSG_SELECT_IMAGE_SOURCE);
        askForPermission(Manifest.permission.CAMERA);
        return v;
    }

    private void init() {

        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.bRefresh).setOnClickListener(this);
        ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow));
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
    }

    private void updateToolbar() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title + " (" + result.getTotal() + ")");
        }

    }

    private void setRecyclerView() {
        try {
            photoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapterPhoto = new PhotoAdapter(photoList, context, this, null, Constant.FormType.TYPE_PHOTO);
            recyclerView.setAdapter(adapterPhoto);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
                    callUploadApi();
                    // showPopup(result.getMenus(), v, 10);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callUploadApi() {

        if (photoList.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            int i = 0;
            for (ChannelPhoto s : photoList) {
                params.put(Constant.FILE_TYPE + "attachmentImage[" + i + "]", s.getTitle());
                i = i + 1;
            }
            //  params.put(Constant.KEY_CHANNEL_ID, channelId);
            callUploadPhotoApi(params);
        } else {
            Util.showSnackbar(v, getStrings(R.string.MSG_NO_PHOTO_SELECTED2));
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
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_PHOTO_SELECTED);
        v.findViewById(R.id.tvNoData).setVisibility(photoList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static UploadPhotoFragment newInstance(Map<String, Object> map, String url, String title) {
        UploadPhotoFragment frag = new UploadPhotoFragment();
        frag.map = map;
        frag.url = url;
        frag.title = title;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        return false;
    }

    /* private void goToViewCategoryFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(videoList.get(postion).getArtistId()))
                .addToBackStack(null)
                .commit();
    }*/

    private void callUploadPhotoApi(Map<String, Object> params) {

        try {
            if (isNetworkAvailable(context)) {

                //  showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.putAll(params);
                    request.params.putAll(map);
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
                                        activity.taskPerformed = Constant.TASK_ADD_MORE_PHOTO;
                                        onBackPressed();
                                        // photoList.get(position).setContentLike(!isLiked);
                                        //updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpImageRequestHandler(activity, new Handler(callback), true).run(request);

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
    public void onResponseSuccess(int reqCode, Object response) {
        switch (reqCode) {

            case REQ_CODE_IMAGE:
                //  imageList.addAll((List<String>) response);
                List<String> list = (List<String>) response;
                for (String path : list) {
                    photoList.add(new ChannelPhoto(path));
                }
                updateAdapter();
                break;
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }
}
