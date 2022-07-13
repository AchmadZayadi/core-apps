package com.sesolutions.ui.clickclick.channel;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.ResultView;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.VideoView;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.responses.videos.ViewVideo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.ui.clickclick.discover.VideoResponse;
import com.sesolutions.ui.clickclick.me.FollowerAdapter;
import com.sesolutions.ui.clickclick.me.MeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sesolutions.ui.classified.ClassifiedHelper.VIEW_CLASSIFIED_DELETE;
import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;
import static com.sesolutions.utils.Constant.Events.MUSIC_MAIN;
import static com.sesolutions.utils.Constant.URL_CHANNEL_VIDEO;

public class ChannelFragment extends MeHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private int currentChannel;
    private static final int UPDATE_UPPER_LAYOUT = 101;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private ResultView result;
    private Result result3;
    private ProgressBar pb;
    private AlbumView album;
    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage;
    public int pos = 0;
    private int albumId;
    private NestedScrollView mScrollView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private AppCompatImageView ivChannelImage;
    private int channelId;
    private int ChannelId;
    private List<Videos> channelList;
    public ChannelAdapter adapter;
    public List<Videos> followersList;
    public VideoResponse.Result result2;
    public AppCompatTextView tvAdd;
    public AppCompatTextView tvEdit;
    public AppCompatImageView ivBack;
    public AppCompatTextView tvFollowers;
    public AppCompatImageView ivHeart;
    public AppCompatImageView ivHeart2;
    public AppCompatImageView ivPlay;
    public AppCompatTextView create;
    public CardView llAlbumImage;
    public LinearLayout llTop;
    public int channeId;
    public boolean channelFound = false;
    public boolean isHeart = false;
    public boolean fromNotification = false;
    private ViewVideo videoVo;
    public final int EDIT_CHANNEL = 69;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_channel, container, false);
        try {
            applyTheme();
            init();
            setRecyclerView();
            callMusicAlbumApi(1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public static ChannelFragment newInstance(int channelId) {
        ChannelFragment fragment = new ChannelFragment();
        fragment.ChannelId = channelId;
        return fragment;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {
                case MUSIC_MAIN:
                    //  fragmentManager.beginTransaction().replace(R.id.container, ClickClickFragment.newInstance(albumsList, true, true, postion)).addToBackStack(null).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ClickClickFragment.newInstance(albumsList,
                                    true, postion, true)).addToBackStack(null)
                            .commit();
                    break;
                case Constant.Events.USER_SELECT:
                    goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, followersList.get(postion).getUserId());
                    break;
                case Constant.Events.DELETE:
                    showVideoDeleteDialog(postion);
                    break;
                case Constant.Events.CONTENT_EDIT:
                    Intent intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_VIDEO_);
                    intent2.putExtra(Constant.KEY_VIDEO_ID, albumsList.get(postion).getVideoId());
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                    break;
            }
            return super.onItemClicked(object1, screenType, postion);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.TASK_SONG_DELETED) {
            result = null;
            albumsList.clear();
            channelList.clear();
            followersList.clear();
            callMusicAlbumApi(1);
        }
        if (activity.taskPerformed == Constant.FormType.EDIT_CHANNEL) {
            result = null;
            albumsList.clear();
            channelList.clear();
            followersList.clear();
            callMusicAlbumApi(1);

        }
        if (activity.taskPerformed == Constant.FormType.KEY_EDIT_VIDEO) {
            activity.taskPerformed = 0;
            Util.showSnackbar(v, "hi");
        }
    }

    private void init() {

        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        recyclerView = v.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        tvAdd = v.findViewById(R.id.tvAdd);
        tvEdit = v.findViewById(R.id.tvEdit);
        tvFollowers = v.findViewById(R.id.tvFollowers);
        create = v.findViewById(R.id.create);
        llTop = v.findViewById(R.id.llTop);
        llAlbumImage = v.findViewById(R.id.llAlbumImage);
        ivChannelImage = v.findViewById(R.id.ivAlbumImage);
        ivBack = v.findViewById(R.id.ivBack);
        ivHeart = v.findViewById(R.id.ivHeart);
        ivPlay = v.findViewById(R.id.ivPlay);
        tvAdd.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivHeart.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        pb = v.findViewById(R.id.pb);
//        v.findViewById(R.id.ivOption).setOnClickListener(this);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mScrollView = v.findViewById(R.id.mScrollView);
        //   setListner();


    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                showShareDialog(Constant.TXT_SHARE_FEED);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            followersList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ChannelAdapter(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setFollowersList() {
        try {
            followersList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            Followeradapter = new FollowerAdapter(followersList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(Followeradapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available on this channel.");
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
    }


    private void updateFollowersAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        Followeradapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has followed this channel yet.");
        v.findViewById(R.id.tvNoData).setVisibility(followersList.size() > 0 ? View.GONE : View.VISIBLE);

    }


    private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareInside(album.getShare(), true);
            });

            bShareOut.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareOutside(album.getShare());
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivShare:
                    showShareDialog(album.getShare());
                    break;
                case R.id.tvEdit:
                    Intent intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_CHANNEL);
                    intent2.putExtra(Constant.KEY_ID, channelList.get(pos).getChannelId());
                    startActivityForResult(intent2, EDIT_CHANNEL_ME);
                    break;
                case R.id.tvAdd:
                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_CHANNEL);
                    startActivityForResult(intent, EDIT_CHANNEL_ME);
                    break;
                case R.id.ivHeart:
                    if (!isHeart) {
                        isHeart = true;
                        ivHeart.setColorFilter(Color.parseColor("#ff0099"));
                        ivPlay.setColorFilter(Color.parseColor("#777777"));
                        setFollowersList();
                        callFollowersApi(1, ChannelId);
                    }
                    break;
                case R.id.ivPlay:
                    isHeart = false;
                    ivHeart.setColorFilter(Color.parseColor("#777777"));
                    ivPlay.setColorFilter(Color.parseColor("#ff0099"));
                    setRecyclerView();
                    callVideosApi(1, ChannelId);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            if (resultCode == EDIT_CHANNEL_ME) {
                CustomLog.e("hello", "hello");
                onRefresh();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showDeleteDialog(final int REQ, final int classifiedId, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_CHANNEL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(REQ, classifiedId, position);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, final int classifiedId, final int position) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_CHANNEL);
                    request.params.put(Constant.KEY_CHANNEL_ID, ChannelId);
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
                                        if (REQ == VIEW_CLASSIFIED_DELETE) {
                                            activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                            channelList.clear();
                                            callMusicAlbumApi(1);
                                        } else {
                                            channelList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                        }
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


    private void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;

            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_CHANNEL_VIEW);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_CHANNEL_ID, ChannelId);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {

                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    showView(v.findViewById(R.id.cvDetail));
                                    VideoView resp = new Gson().fromJson(response, VideoView.class);
                                    result = resp.getResult();
                                    updateLayout();
                                    callVideosApi(1, ChannelId);
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }

    public void updateLayout() {
        Util.showImageWithGlide(ivChannelImage, result.getChannel().getImages().getMain(), context, R.drawable.placeholder_3_2);
        ((AppCompatTextView) v.findViewById(R.id.tvTitle)).setText(result.getChannel().getTitle());
        ((AppCompatTextView) v.findViewById(R.id.tvFoll)).setText(""+result.getChannel().getFollowCount());
    }

    private void RefreshChannel(final int req) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_CHANNEL_VIEW);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                request.params.put(Constant.KEY_TYPE, "manage");

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    VideoView resp = new Gson().fromJson(response, VideoView.class);
                                    result = resp.getResult();
                                    videoVo = result.getChannel();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }


    private void callFollowersApi(final int req, int channel) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(URL.URL_CHANNEL_FOLLOWERS);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_CHANNEL_ID_VIDEO, channel);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    showView(v.findViewById(R.id.cvDetail));
                                    VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    result3 = resp.getResult();
                                    if (null != result3.getNotifications()) {
                                        followersList.addAll(result3.getNotifications());
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                                updateFollowersAdapter();

                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }

    private void callVideosApi(final int req, int channelId) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                        showView(v.findViewById(R.id.pbMain));
                    }

                    HttpRequestVO request = new HttpRequestVO(URL_CHANNEL_VIDEO);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_PLAYLIST_ID, channelId);
                    request.params.put(Constant.KEY_RESOURCE_ID, channelId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, "sesvideo_chanel");

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
                                hideLoaders();
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(mScrollView);
                                        VideoResponse resp = new Gson().fromJson(response, VideoResponse.class);
                                        result2 = resp.getResult();
                                        albumsList.clear();
                                        if (null != result2.getVideos()) {
                                            albumsList.addAll(result2.getVideos());
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


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }


    @Override
    public void onLoadMore() {
    }


    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getAlbumId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }


    public void showDeleteDialog(int p) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_ALBUM);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi();

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    String url = Constant.URL_DELETE_MUSIC_ALBUM;//: Constant.URL_DELETE_MUSIC_PLAYLIST;
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ALBUM_ID, albumId);

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

                                        activity.taskPerformed = Constant.TASK_PLAYLIST_DELETED;
                                        channelList.clear();
                                        callMusicAlbumApi(1);
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

    public void showVideoDeleteDialog(int p) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_VIDEO);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callVideoDeleteApi(p);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callVideoDeleteApi(int posit) {

        try {
            if (isNetworkAvailable(context)) {

                showBaseLoader(false);

                try {
                    String url = "";
                    String key = "";
                    int value;

                    url = Constant.URL_DELETE_VIDEO;
                    key = Constant.KEY_VIDEO_ID;
                    value = albumsList.get(posit).getVideoId();

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(key, value);
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
                                        activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                        albumsList.remove(posit);
                                        adapter.notifyItemRemoved(posit);
                                        adapter.notifyItemRangeChanged(posit, albumsList.size());
                                        //VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);


                                        // updateAdapter();
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
    public void onRefresh() {
        try {
            if (isHeart) {
                setFollowersList();
                callFollowersApi(1, ChannelId);
            } else {
                setRecyclerView();
                callVideosApi(1, ChannelId);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


}
