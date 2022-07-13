package com.sesolutions.ui.clickclick.me;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.ui.clickclick.discover.VideoResponse;
import com.sesolutions.ui.clickclick.notification.VideoNotificationFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sesolutions.ui.classified.ClassifiedHelper.VIEW_CLASSIFIED_DELETE;
import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;
import static com.sesolutions.utils.Constant.Events.MUSIC_MAIN;
import static com.sesolutions.utils.Constant.URL_CHANNEL_VIDEO2;
import static com.sesolutions.utils.Constant.URL_CHANNEL_VIDEO_LIKE;
import static com.sesolutions.utils.URL.POST_URL;

public class MeFragment extends MeHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private int currentChannel;
    private static final int UPDATE_UPPER_LAYOUT = 101;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private Result result;
    private Result result3;
    private ProgressBar pb;
    private AlbumView album;
    public ImageView ivCoverPhoto;
    public ImageView ivAlbumImage;
    public int pos = 0;
    private List<String> selectlist;
    private int albumId;
    private NestedScrollView mScrollView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoggedIn;
    private boolean openComment;
    private Bundle bundle;
    private MaterialSpinner spinner;
    private CircleImageView ivChannelImage;
    private int channelId;
    private List<Videos> channelList;
    public List<Videos> followersList;
    public VideoResponse.Result result2;
    public AppCompatTextView tvAdd;
    public AppCompatTextView tvEdit;
    public AppCompatTextView tvTitleName;
    public AppCompatTextView tvLikes;
    public AppCompatTextView tvFollowers;
    public AppCompatTextView ivHeart,tvFollowbtn;
    public AppCompatTextView ivPlay;
    public AppCompatTextView create;
    public View view22,view21;
    public LinearLayout llTop;
    public int channeId;
    public boolean channelFound = false;
    public boolean isHeart = false;
    public boolean fromNotification = false;
    public final int EDIT_CHANNEL = 69;
    int menuTitleActiveColor;
    TextView tvfollowerscount,tvfollowingcount,tvlikescount,VavciId,tvfollowers,tvfollowing;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_me, container, false);
        v.findViewById(R.id.ivBack).setVisibility(View.VISIBLE);

        tvfollowerscount= v.findViewById(R.id.tvfollowerscount);
        tvfollowingcount= v.findViewById(R.id.tvfollowingcount);
        tvfollowers= v.findViewById(R.id.tvfollowers);
        tvfollowing= v.findViewById(R.id.tvfollowing);
        tvlikescount= v.findViewById(R.id.tvlikescount);
        VavciId= v.findViewById(R.id.VavciId);

        v.findViewById(R.id.tvEdit).setVisibility(View.GONE);
        v.findViewById(R.id.ivOption).setVisibility(View.GONE);
        v.findViewById(R.id.tvAdd).setVisibility(View.GONE);
        v.findViewById(R.id.spinner).setVisibility(View.GONE);
        view21= v.findViewById(R.id.view21);
        view22= v.findViewById(R.id.view22);
        tvFollowbtn= v.findViewById(R.id.tvFollowbtn);

        tvfollowerscount.setOnClickListener(this);
        tvfollowingcount.setOnClickListener(this);
        tvfollowers.setOnClickListener(this);
        tvfollowing.setOnClickListener(this);

        callFreashApi();
        ivPlay= v.findViewById(R.id.ivPlay);
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        ivPlay.setTextColor(menuTitleActiveColor);

        view21.setBackgroundColor(menuTitleActiveColor);
        view22.setBackgroundColor(Color.parseColor("#737171"));

        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_filled_lover);
        gdr.setColor(SesColorUtils.getPrimaryColor(context));
        tvFollowbtn.setBackground(gdr);


        tvFollowbtn.setVisibility(View.GONE);

        return v;
    }

    public static MeFragment newInstance(boolean fromNotification, int channelId) {
        MeFragment fragment = new MeFragment();
        fragment.fromNotification = fromNotification;
        fragment.channeId = channelId;
        return fragment;
    }

    private static final int REQ_LIKE = 100;

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
                case Constant.Events.TICK_VIDEO_LIKE:
                    callLikeApi(REQ_LIKE, pos, Constant.URL_MUSIC_LIKE, Integer.parseInt(screenType.toString()));
                    break;
                case Constant.Events.CONTENT_EDIT:
                    Intent intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_VIDEO_);
                    intent2.putExtra(Constant.KEY_VIDEO_ID, albumsList.get(postion).getVideoId());
                    startActivityForResult(intent2,EDIT_CHANNEL_ME);
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
            //callMusicAlbumApi(1);
            callVideosApi(1,0);

        }
        if (activity.taskPerformed == Constant.FormType.EDIT_CHANNEL) {
            result = null;
            albumsList.clear();
            channelList.clear();
            followersList.clear();
         //   callMusicAlbumApi(1);
            callVideosApi(1,0);

        }
        if (activity.taskPerformed == Constant.FormType.KEY_EDIT_VIDEO) {
            activity.taskPerformed = 0;
            Util.showSnackbar(v, "hi");
        }
    }

    private void init() {

        selectlist = new ArrayList<String>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        recyclerView = v.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        tvAdd = v.findViewById(R.id.tvAdd);
        tvEdit = v.findViewById(R.id.tvEdit);
        tvLikes = v.findViewById(R.id.tvFoll);
        tvFollowers = v.findViewById(R.id.tvFollowers);
        create = v.findViewById(R.id.create);
        spinner = v.findViewById(R.id.spinner);
        llTop = v.findViewById(R.id.llTop);
        ivChannelImage = v.findViewById(R.id.ivAlbumImage);
        tvTitleName = v.findViewById(R.id.tvTitle);
        ivHeart = v.findViewById(R.id.ivHeart);
        ivPlay = v.findViewById(R.id.ivPlay);
        tvAdd.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        ivHeart.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        pb = v.findViewById(R.id.pb);
        v.findViewById(R.id.ivOption).setOnClickListener(this);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mScrollView = v.findViewById(R.id.mScrollView);
        tvTitleName.setVisibility(View.VISIBLE);
        //   setListner();

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                        .getScrollY()));

                if (diff == 0) {
                    // your pagination code
                    loadMore();
                }
            }
        });
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
            adapter = new MeAdapter(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
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
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available on this user.");
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
    }


    private void updateFollowersAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        Followeradapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has followed this user yet.");
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

                case R.id.tvfollowing:
                case R.id.tvfollowingcount:
                    Intent intent23 = new Intent(activity, CommonActivity.class);
                    intent23.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.FOLLOWFOLLOWING_ACTIVITY);
                    intent23.putExtra(Constant.KEY_ID, SPref.getInstance().getLoggedInUserId(context));
                    intent23.putExtra(Constant.KEY_TITLE, "Following");
                    startActivity(intent23);
                    break;

                case R.id.tvfollowers:
                case R.id.tvfollowerscount:
                    Intent intent12 = new Intent(activity, CommonActivity.class);
                    intent12.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.FOLLOWFOLLOWING_ACTIVITY);
                    intent12.putExtra(Constant.KEY_ID, SPref.getInstance().getLoggedInUserId(context));
                    intent12.putExtra(Constant.KEY_TITLE, "Followers");
                    startActivity(intent12);
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
                        ivHeart.setTextColor(menuTitleActiveColor);
                        view22.setBackgroundColor(menuTitleActiveColor);
                        view21.setBackgroundColor(Color.parseColor("#737171"));
                        ivPlay.setTextColor(Color.parseColor("#737171"));
                       // setFollowersList();
                    //    callFollowersApi(1, channelId);

                        setRecyclerView2();
                        callVideosApi234(1, channelId);
                    }
                    break;
                case R.id.ivPlay:
                    isHeart = false;
                    ivHeart.setTextColor(Color.parseColor("#737171"));
                    ivPlay.setTextColor(menuTitleActiveColor);

                    view21.setBackgroundColor(menuTitleActiveColor);
                    view22.setBackgroundColor(Color.parseColor("#737171"));

                    setRecyclerView();
                    callVideosApi(1, channelId);
                    break;
                case R.id.ivOption:
                    showPopup(result.getChannels().get(pos).getMenus(), view, 10);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Options s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt;
            itemId = itemId - 10;
            opt = result.getChannels().get(pos).getMenus().get(itemId - 1);

            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Intent intent2 = new Intent(activity, CommonActivity.class);
                    intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_CHANNEL);
                    intent2.putExtra(Constant.KEY_ID, channelList.get(pos).getChannelId());
                    startActivityForResult(intent2, EDIT_CHANNEL);
                    break;
                case Constant.OptionType.DELETE:
                    if(channelList.get(pos).getIsdefault() == 0){
                        showDeleteDialog(VIEW_CLASSIFIED_DELETE, albumId, 0);
                    } else{
                        Util.showSnackbar(v, "You cannot delete the default channel..");
                    }
                    break;


                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            if (resultCode == EDIT_CHANNEL_ME) {
                CustomLog.e("hello", "hello");
                callFreashApi();
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
                    request.params.put(Constant.KEY_CHANNEL_ID, channelId);
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
                                            callVideosApi(1,0);
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
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_BROWSE);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                request.params.put(Constant.KEY_TYPE, "manage");
                request.params.put(Constant.KEY_FROMTICVIDEO, "1");

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
                                    VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    result = resp.getResult();
                                    channelList = new ArrayList<>();
                                    selectlist.clear();
                                    if (null != result.getChannels()) {
                                        channelList.addAll(result.getChannels());
                                    }
                                    if (channelList.size() > 0) {
                                        channelId = channelList.get(0).getChannelId();
                                        if (channelList.get(0).getUser_follow_count() > 1 || channelList.get(0).getUser_follow_count() == 0) {
                                            tvLikes.setVisibility(View.VISIBLE);
                                            tvLikes.setText(channelList.get(0).getUser_follow_count() + "");
                                        } else {
                                            tvLikes.setVisibility(View.VISIBLE);
                                            tvLikes.setText(channelList.get(0).getUser_follow_count() + "");
                                        }
                                        tvFollowers.setVisibility(View.GONE);
                                        if (channelList.get(0).getLikeCount() > 1 || channelList.get(0).getLikeCount() == 0) {
                                            tvFollowers.setText(channelList.get(0).getLikeCount() + " Likes");
                                        } else {
                                            tvFollowers.setText(channelList.get(0).getLikeCount() + " Like");
                                        }
                                       // callVideosApi(1, channelList.get(0).getChannelId());
                                        updateSpinner();
                                        updateUpperLayout();
//                                        updateAdapter();
                                    } else {
                                        create.setVisibility(View.VISIBLE);
                                        tvLikes.setVisibility(View.GONE);
                                        spinner.setVisibility(View.GONE);
                                        tvFollowers.setVisibility(View.GONE);
                                        tvEdit.setVisibility(View.GONE);
                                        llTop.setVisibility(View.GONE);
                                        v.findViewById(R.id.ivOption).setVisibility(View.GONE);
                                        v.findViewById(R.id.tvNoData).setVisibility(View.GONE);
                                        v.findViewById(R.id.cvSpinner).setVisibility(View.GONE);
                                    }
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

    private void callNotificationChannels(final int req, int channell) {

        if (isNetworkAvailable(context)) {
            isLoading = true;

            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_BROWSE);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
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
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {

                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    showView(v.findViewById(R.id.cvDetail));
                                    VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    result = resp.getResult();
                                    channelList = new ArrayList<>();
                                    selectlist.clear();


                                    if (null != result.getChannels()) {
                                        channelList.addAll(result.getChannels());
                                    }
                                    if (channelList.size() > 0) {
                                        for (int i = 0; i < channelList.size(); i++) {
                                            if (channell == channelList.get(i).getChannelId()) {
                                                currentChannel = i;
                                                channelFound = true;
                                            }
                                        }
                                        if (!channelFound) {
                                            Util.showSnackbar(v, "Channel deleted or is unavailable");
                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    activity.getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.container, new VideoNotificationFragment())
                                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                            .commit();
                                                }
                                            }, 2000);
                                        }
                                        if (channelList.get(currentChannel).getUser_follow_count() > 1 || channelList.get(currentChannel).getUser_follow_count() == 0) {
                                            tvLikes.setVisibility(View.VISIBLE);
                                            tvLikes.setText(channelList.get(currentChannel).getUser_follow_count() + "");
                                        } else {
                                            tvLikes.setVisibility(View.VISIBLE);
                                            tvLikes.setText(channelList.get(currentChannel).getUser_follow_count() + "");
                                        }
                                        tvFollowers.setVisibility(View.GONE);
                                        if (channelList.get(currentChannel).getLikeCount() > 1 || channelList.get(currentChannel).getLikeCount() == 0) {
                                            tvFollowers.setText(channelList.get(currentChannel).getLikeCount() + " likess");
                                        } else {
                                            tvFollowers.setText(channelList.get(currentChannel).getLikeCount() + " like");
                                        }
                                        if (channelFound) {
                                            callVideosApi(1, channell);
                                            updateUpperLayout();
                                            updateSpinnerfromNotification();
                                        }

                                    } else {
                                        create.setVisibility(View.VISIBLE);
                                        tvLikes.setVisibility(View.GONE);
                                        spinner.setVisibility(View.GONE);
                                        tvFollowers.setVisibility(View.GONE);
                                        tvEdit.setVisibility(View.GONE);
                                        llTop.setVisibility(View.GONE);
                                        v.findViewById(R.id.ivOption).setVisibility(View.GONE);
                                        v.findViewById(R.id.tvNoData).setVisibility(View.GONE);
                                        v.findViewById(R.id.cvSpinner).setVisibility(View.GONE);
                                    }
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

    private void RefreshChannel(final int req) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.GONE);
                } else if (req == 1) {
                 //   showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_BROWSE);
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
                                    VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    result = resp.getResult();
                                    RefreshUpperLayout();
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

    public void RefreshUpperLayout() {
        if (result.getChannels().get(pos).getUser_image() != null) {
            Util.showImageWithGlide(ivChannelImage, result.getChannels().get(pos).getUser_image(), context, R.drawable.placeholder_3_2);
        }
        if (result.getChannels().get(pos).getUser_username() != null) {
           tvTitleName.setText(""+result.getChannels().get(pos).getUserTitle());
            tvTitleName.setTextColor(Color.parseColor("#484744"));
        }
        /*if (channelList.get(pos).getUser_follow_count() > 1 || channelList.get(pos).getUser_follow_count() == 0) {
            tvLikes.setText(channelList.get(pos).getUser_follow_count() + "");
        } else {
            tvLikes.setText(channelList.get(pos).getUser_follow_count() + "");
        }
        if (channelList.get(pos).getLikeCount() > 1 || channelList.get(pos).getLikeCount() == 0) {
            tvFollowers.setText(channelList.get(pos).getLikeCount() + " Likes");
        } else {
            tvFollowers.setText(channelList.get(pos).getLikeCount() + " Like");
        }*/

        if (result2.getUser_info().getFollow_count() != null ) {
            tvfollowerscount.setText(""+result2.getUser_info().getFollow_count());
        }
        else {
            tvfollowerscount.setText("0");
        }

        if (result2.getUser_info().getFollowing_count() != null ) {
            tvfollowingcount.setText(""+result2.getUser_info().getFollowing_count());
        }
        else {
            tvfollowerscount.setText("0");
        }

        if (result2.getUser_info().getTotal_video_like_count() != null ) {
            tvlikescount.setText(""+result2.getUser_info().getTotal_video_like_count());
        }
        else {
            tvlikescount.setText("0");
        }


        if (result2.getUser_info().getTick_video_id() != null ) {
            VavciId.setText("ID: "+result2.getUser_info().getTick_video_id());
        }
        else {
            VavciId.setText("0");
            VavciId.setVisibility(View.GONE);
        }




    }

    private void callFollowersApi(final int req, int channel) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            String URL_DATA= URL.URL_CHANNEL_FOLLOWERS2+""+ SPref.getInstance().getLoggedInUserId(context)+""+POST_URL;

            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(URL_DATA);
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


    public void updateSpinner() {
        for (int i = 0; i < channelList.size(); i++) {
            selectlist.add(i, result.getChannels().get(i).getTitle());
        }
        spinner.setItems(selectlist);
        spinner.setSelectedIndex(0);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                pos = position;
                if (result.getChannels().get(pos).getUser_image() != null) {
                    Util.showImageWithGlide(ivChannelImage, result.getChannels().get(pos).getUser_image(), context, R.drawable.placeholder_3_2);
                }
                if (result.getChannels().get(pos).getUser_username() != null) {
                    tvTitleName.setText(""+result.getChannels().get(pos).getUserTitle());
                }
                if (channelList.get(pos).getFollowVideos() > 1 || channelList.get(pos).getFollowVideos() == 0) {
                    tvLikes.setText(channelList.get(pos).getFollowVideos() + "");
                } else {
                    tvLikes.setText(channelList.get(pos).getFollowVideos() + "");
                }
                if (channelList.get(pos).getLikeCount() > 1 || channelList.get(pos).getLikeCount() == 0) {
                    tvFollowers.setText(channelList.get(pos).getFollowVideos() + " Likes");
                } else {
                    tvFollowers.setText(channelList.get(pos).getFollowVideos() + " Like");
                }
                changeRecyclerView(position);
            }
        });
    }

    public void updateSpinnerfromNotification() {
        for (int i = 0; i < channelList.size(); i++) {
            selectlist.add(i, result.getChannels().get(i).getTitle());
        }
        spinner.setItems(selectlist);
        spinner.setSelectedIndex(currentChannel);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                pos = position;

                if (result.getChannels().get(pos).getUser_image() != null) {
                    Util.showImageWithGlide(ivChannelImage, result.getChannels().get(pos).getUser_image(), context, R.drawable.placeholder_3_2);
                }
                if (result.getChannels().get(pos).getUser_username() != null) {
                    tvTitleName.setText(""+result.getChannels().get(pos).getUserTitle());
                }

                if (channelList.get(pos).getFollowVideos() > 1 || channelList.get(pos).getFollowVideos() == 0) {
                    tvLikes.setText(channelList.get(pos).getFollowVideos() + " Followers");
                } else {
                    tvLikes.setText(channelList.get(pos).getFollowVideos() + " Follower");
                }
                if (channelList.get(pos).getLikeCount() > 1 || channelList.get(pos).getLikeCount() == 0) {
                    tvFollowers.setText(channelList.get(pos).getFollowVideos() + " Likes");
                } else {
                    tvFollowers.setText(channelList.get(pos).getFollowVideos() + " Like");
                }
                changeRecyclerView(position);
            }
        });
    }


    private void callVideosApi(final int req, int channelId) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                   //    pb.setVisibility(View.VISIBLE);
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                  //      showView(v.findViewById(R.id.pbMain));
                    }

                    HttpRequestVO request = new HttpRequestVO(URL_CHANNEL_VIDEO2);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                   // request.params.put(Constant.KEY_PLAYLIST_ID, channelId);
                   // request.params.put(Constant.KEY_RESOURCE_ID, channelId);
                    request.params.put(Constant.KEY_FROMTICVIDEO, "1");
                    request.params.put("user_id", SPref.getInstance().getLoggedInUserId(context));
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

                                            if(albumsList!=null && albumsList.size()>0){
                                                for(int k=0;k<albumsList.size();k++){
                                                    albumsList.get(k).setChannelId(channelId);
                                                }

                                                updateUpperLayout();
                                            }else {
                                                if (result2.getUser_info().getUser_image() != null) {
                                                    Util.showImageWithGlide(ivChannelImage, result2.getUser_info().getUser_image(), context, R.drawable.placeholder_3_2);
                                                }
                                                if (result2.getUser_info().getUser_title() != null) {
                                                    tvTitleName.setText(""+result2.getUser_info().getUser_title());
                                                }

                                                if (result2.getUser_info().getFollow_count() != null ) {
                                                    tvfollowerscount.setText(""+result2.getUser_info().getFollow_count());
                                                }
                                                else {
                                                    tvfollowerscount.setText("0");
                                                }

                                                if (result2.getUser_info().getFollowing_count() != null ) {
                                                    tvfollowingcount.setText(""+result2.getUser_info().getFollowing_count());
                                                }
                                                else {
                                                    tvfollowerscount.setText("0");
                                                }

                                                if (result2.getUser_info().getTotal_video_like_count() != null ) {
                                                    tvlikescount.setText(""+result2.getUser_info().getTotal_video_like_count());
                                                }
                                                else {
                                                    tvlikescount.setText("0");
                                                }

                                                if (result2.getUser_info().getTick_video_id() != null ) {
                                                    VavciId.setText("ID: "+result2.getUser_info().getTick_video_id());
                                                }
                                                else {
                                                    VavciId.setText("0");
                                                    VavciId.setVisibility(View.GONE);
                                                }

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
    private void callVideosApi234(final int req, int channelId) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                   //    pb.setVisibility(View.VISIBLE);
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                  //      showView(v.findViewById(R.id.pbMain));
                    }

                    HttpRequestVO request = new HttpRequestVO(URL_CHANNEL_VIDEO_LIKE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                   // request.params.put(Constant.KEY_PLAYLIST_ID, channelId);
                   // request.params.put(Constant.KEY_RESOURCE_ID, channelId);
                    request.params.put(Constant.KEY_FROMTICVIDEO, "1");
                    request.params.put("user_id", SPref.getInstance().getLoggedInUserId(context));
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
                                        updateAdapter2();
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

    private void setRecyclerView2() {
        try {
            albumsList = new ArrayList<>();
            followersList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter2 = new MeAdapter2(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter2);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateAdapter2() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter2.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available on this user.");
        v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
    }


    public void changeRecyclerView(int pos) {
        channelId = channelList.get(pos).getChannelId();
        if (isHeart) {
        //    setFollowersList();
        //    callFollowersApi(1, channelId);

            setRecyclerView2();
            //    RefreshChannel(1);
            callVideosApi234(1, channelId);

        } else {
            setRecyclerView();
            callVideosApi(1, channelId);

        }
    }

    public void updateUpperLayout() {
            if (result2.getVideos() != null && result2.getVideos().size()>0) {
                 if (result2.getVideos().get(0).getUser_image() != null) {
                    Util.showImageWithGlide(ivChannelImage, result2.getVideos().get(0).getUser_image(), context, R.drawable.placeholder_3_2);
                }
                if (result2.getVideos().get(0).getUser_image() != null) {
                    tvTitleName.setText(""+result2.getVideos().get(0).getUserTitle());
                }

                if (result2.getUser_info().getFollow_count() != null ) {
                    tvfollowerscount.setText(""+result2.getUser_info().getFollow_count());
                }
                else {
                    tvfollowerscount.setText("0");
                }

                if (result2.getUser_info().getFollowing_count() != null ) {
                    tvfollowingcount.setText(""+result2.getUser_info().getFollowing_count());
                }
                else {
                    tvfollowerscount.setText("0");
                }

                if (result2.getUser_info().getTotal_video_like_count() != null ) {
                    tvlikescount.setText(""+result2.getUser_info().getTotal_video_like_count());
                }
                else {
                    tvlikescount.setText("0");
                }

                if (result2.getUser_info().getTick_video_id() != null ) {
                    VavciId.setText("ID: "+result2.getUser_info().getTick_video_id());
                }
                else {
                    VavciId.setText("0");
                    VavciId.setVisibility(View.GONE);
                }
                /*
                if (result2.getVideos().get(0).getUser_follow_count() > 1 || result2.getVideos().get(0).getUser_follow_count() == 0) {
                    tvLikes.setVisibility(View.VISIBLE);
                    tvLikes.setText(result2.getVideos().get(0).getUser_follow_count() + "");
                } else {
                    tvLikes.setVisibility(View.VISIBLE);
                    tvLikes.setText(result2.getVideos().get(0).getUser_follow_count() + "");
                }*/
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

    public void loadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                  //  callMusicAlbumApi(REQ_LOAD_MORE);
                    callVideosApi(1,0);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
                                        //callMusicAlbumApi(1);
                                        callVideosApi(1,0);
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
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    public void callFreashApi(){
        try {
            applyTheme();
            init();

            if (isHeart) {
                setRecyclerView2();
             //   callNotificationChannels(1, channeId);
                callVideosApi234(1,0);
            } else {
               // callMusicAlbumApi(1);
                setRecyclerView();
                callVideosApi(1,0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            callFreashApi();
          /*  if (isHeart) {
                //    setFollowersList();
                //    callFollowersApi(1, channelId);

                setRecyclerView2();
                //    RefreshChannel(1);
                callVideosApi234(1, channelId);

            } else {
                setRecyclerView();
                callVideosApi(1, channelId);

            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callLikeApi(final int REQ_CODE, final int position, String url, final int vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    String resourceType = Constant.ResourceType.VIDEO;
                    request.params.put(Constant.KEY_RESOURCE_ID, vo);
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
                                        JSONObject json = new JSONObject(response);
                                        boolean islike = json.getJSONObject(Constant.KEY_RESULT).getBoolean("is_like");
                                        albumsList.get(position).setContentLike(islike);
                                    //    adapter.notifyItemChanged(position);
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



}
