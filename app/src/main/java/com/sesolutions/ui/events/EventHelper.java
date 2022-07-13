package com.sesolutions.ui.events;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.event.Reviews;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 29/11/17.
 */

public class EventHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // public static final String TYPE_HOME = "sesevent_main_browsehome";
    // public static final String TYPE_BROWSE = "sesevent_main_index";
    public static final String TYPE_CATEGORY = "sesevent_main_browsecategory";
    //public static final String TYPE_UPCOMING = "sesevent_main_upcoming";
    // public static final String TYPE_PAST = "sesevent_main_past";
    public static final String TYPE_BROWSE_LIST = "sesevent_main_browselist";
    public static final String TYPE_BROWSE_HOST = "sesevent_main_indexhost";
    public static final String TYPE_CALENDAR = "sesevent_main_calender";
    public static final String TYPE_MANAGE = "sesevent_main_manage";
    public static final String TYPE_CREATE = "sesevent_main_create";
    public static final String TYPE_REVIEW = "sesevent_main_browsereview";
    public static final String TYPE_VIDEO = "seseventvideo_main_browsehome";
    public static final String TYPE_VIEW_CATEGORY = "category";

    //for EVENT LIST VIEW PAGE
    public static final String TYPE_LIST_OG = "ongoingSPupcomming";
    public static final String TYPE_LIST_LATEST = "latest";
    public static final String TYPE_LIST_ONGOING = "ongoing";
    public static final String TYPE_LIST_PAST = "past";
    public static final String TYPE_LIST_WEEK = "week";
    public static final String TYPE_LIST_WEEKEND = "weekend";
    public static final String TYPE_LIST_MONTH = "month";
    public static final String TYPE_LIST_MJE = "mostjoinevents";


    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_WATCH_LATER = -1;
    public OnUserClickedListener<Integer, Object> parent;
    public int categoryId;
    public int subcategoryId;
    public int subsubcategoryId;
    public List<CommonVO> videoList;
    public List<Videos> eventVideoList;
    public List<Reviews> reviewList;
    public T adapter;
    public String selectedScreen = "";
    //variable used for filter events [eg past,ongoing] on LIST VIEW SCREEN and MANAGE SCREEN
    public String mFilter;


    public List<Category> categoryList;
    public CommonResponse.Result result;

    public RelativeLayout hiddenPanel;


    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(((CommonVO) videoList.get(postion)).getOwnerId());
                    break;
                case Constant.Events.MUSIC_LIKE:
                    if (TYPE_VIDEO.equals(selectedScreen)) {
                        callLikeApi(REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, null);
                    } else {
                        callLikeApi(REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                    }
                    break;

                case Constant.Events.CLICKED_HEADER_LOCATION:
                    super.openGoogleMap(videoList.get(postion).getLat(), videoList.get(postion).getLng());
                    break;
                case Constant.Events.MUSIC_FAVORITE:
                    if (TYPE_VIDEO.equals(selectedScreen)) {
                        callLikeApi(REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, null);
                    } else {
                        callLikeApi(/*Integer.valueOf("" + screenType),*/ REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, videoList.get(postion));
                    }
                    break;
                case Constant.Events.WATCH_LATER:
                    if (isNetworkAvailable(context)) {
                        eventVideoList.get(postion).toggleWatchLaterId();
                        adapter.notifyItemChanged(postion);
                        Map<String, Object> request = new HashMap<>();
                        request.put(Constant.KEY_VIDEO_ID, eventVideoList.get(postion).getVideoId());
                        new ApiController(Constant.URL_EVENT_VIDEO_WATCH_LATER, request, context, this, REQ_WATCH_LATER).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                //case for watch later api response
                case REQ_WATCH_LATER:
                    try {
                        String response = (String) screenType;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (!TextUtils.isEmpty(err.getError())) {
                                Util.showSnackbar(v, err.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                    }
                    break;
                case Constant.Events.MUSIC_ADD:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, videoList.get(postion).getEventId());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container
                                    , AddToPlaylistFragment.newInstance(Constant.FormType.ADD_EVENT_LIST, map, Constant.URL_ADD_EVENT_TO_LIST))
                            .addToBackStack(null)
                            .commit();
                    break;
               /* case Constant.Events.MENU_MAIN:
                    //item clicked on Review Adapter
                    goToViewReviewFragment(reviewList.get(postion).getReview_id());
                    break;*/
                case Constant.Events.MUSIC_MAIN:
                    switch (selectedScreen) {
                        case TYPE_VIDEO:
                            goTo(Constant.GoTo.VIDEO, eventVideoList.get(postion).getVideoId(), Constant.ResourceType.SES_EVENT_VIDEO);
                            break;
                        case TYPE_BROWSE_HOST:
                            openHost(postion);
                            break;
                        case TYPE_BROWSE_LIST:
                            goToViewEventListFragment(videoList.get(postion).getListId());
                            break;
                        default:
                            if ("hosts".equals(mFilter)) {
                                openHost(postion);
                            } else if ("lists".equals(mFilter)) {
                                goToViewEventListFragment(videoList.get(postion).getListId());
                            } else {
                                goToViewEventFragment(videoList.get(postion).getEventId());
                            }
                            break;
                    }
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        // goToCategoryFragment(postion, "" + screenType);
                        goToCategoryFragment((Category) screenType, postion + 1);
                    break;
                case Constant.Events.CLICKED_OPTION:
                    setFeedUpdateRecycleView(postion);
                    // slideUpDown();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    private void openHost(int postion) {
        String type = videoList.get(postion).getType();
        //open profile page if host is onSite user
        if ("site".equals(type)) {
            goToProfileFragment(videoList.get(postion).getUserId());
        } else { //otherwise open custom host view page
            openHostView(videoList.get(postion));
        }
    }

    public void goToViewEventListFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewEventListFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewReviewFragment(int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ReviewViewFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    private void openHostView(CommonVO commonVO) {
        fragmentManager.beginTransaction().replace(R.id.container, OffSiteHostViewFragment.newInstance(commonVO.getHostId())).addToBackStack(null).commit();
    }

    public void performFeedOptionClick(String name, int actPosition) {
        slideUpDown();
        switch (name) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(actPosition);
                break;
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                if ("hosts".equals(mFilter)) {
                    map.put(Constant.KEY_HOST_ID, videoList.get(actPosition).getHostId());
                    openFormFragment(Constant.FormType.EDIT_HOST, map, Constant.URL_HOST_EDIT);
                } else if ("lists".equals(mFilter)) {
                    map.put(Constant.KEY_LIST_ID, videoList.get(actPosition).getListId());
                    openFormFragment(Constant.FormType.EDIT_EVENT_LIST, map, Constant.URL_EDIT_EVENT_LIST);
                } else {
                    map.put(Constant.KEY_EVENT_ID, videoList.get(actPosition).getEventId());
                    fragmentManager.beginTransaction().replace(R.id.container,
                            CreateEditEventFragment.newInstance(Constant.FormType.EDIT_EVENT, map, Constant.URL_EDIT_EVENT))
                            .addToBackStack(null).commit();
                }

                break;
        }
    }

    public void showDeleteDialog(final int position) {
        try {
            int msgId;
            final Map<String, Object> map = new HashMap<>();
            if ("hosts".equals(mFilter)) {
                msgId = R.string.MSG_DELETE_CONFIRMATION_HOST;
                map.put(Constant.KEY_HOST_ID, videoList.get(position).getHostId());
            } else if ("lists".equals(mFilter)) {
                msgId = R.string.MSG_DELETE_CONFIRMATION_EVENT_LIST;
                map.put(Constant.KEY_LIST_ID, videoList.get(position).getListId());
            } else {
                msgId = R.string.MSG_DELETE_CONFIRMATION_EVENT;
                map.put(Constant.KEY_EVENT_ID, videoList.get(position).getEventId());
            }
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msgId);
            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(position, map);

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

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.hidden_panel:
                    slideUpDown();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bootom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            // isPanelShown = true;
        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(context, R.anim.bootom_down);

            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            // isPanelShown = false;
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }


    private void setFeedUpdateRecycleView(int position) {
        try {
            if (result.getMenus() != null) {
                RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
                recycleViewFeedUpdate.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                recycleViewFeedUpdate.setLayoutManager(layoutManager);
                FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(result.getMenus(), context, this, null);
                adapterFeed.setActivityPosition(position);
                recycleViewFeedUpdate.setAdapter(adapterFeed);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callLikeApi(final int REQ_CODE, final int position, String url, final CommonVO vo) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    int resId;
                    String resType;
                    switch (selectedScreen) {
                        case TYPE_BROWSE_HOST:
                        case TYPE_MANAGE:
                            resId = vo.getHostId();
                            resType = Constant.ResourceType.SES_EVENT_HOST;
                            updateItemLikeFavorite(REQ_CODE, position, vo);
                            break;
                        case TYPE_VIDEO:
                            resId = eventVideoList.get(position).getVideoId();
                            resType = Constant.ResourceType.SES_EVENT_VIDEO;
                            updateVideoLikeFavorite(REQ_CODE, position, eventVideoList.get(position));
                            break;
                        default:
                            resId = vo.getEventId();
                            resType = Constant.ResourceType.SES_EVENT;
                            updateItemLikeFavorite(REQ_CODE, position, vo);
                            break;
                    }
                    request.params.put(Constant.KEY_RESOURCE_ID, resId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resType);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    /*if (REQ_CODE == REQ_LIKE) {
                                        videoList.get(position).setContentLike(!vo.isContentLike());
                                    } else if (REQ_CODE == REQ_FAVORITE) {
                                        videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                    }
                                    adapter.notifyItemChanged(position);*/
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, int position, CommonVO vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).setContentLike(!vo.isContentLike());
            videoList.get(position).setShowAnimation(1);
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
            videoList.get(position).setShowAnimation(2);
            adapter.notifyItemChanged(position);
        }

    }

    public void updateVideoLikeFavorite(int REQ_CODE, int position, Videos vo) {
        if (REQ_CODE == REQ_LIKE) {
            eventVideoList.get(position).toggleLike();
            //  eventVideoList.get(position).setShowAnimation(1);
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            eventVideoList.get(position).setContentFavourite(!vo.isContentFavourite());
            //  eventVideoList.get(position).setShowAnimation(2);
            adapter.notifyItemChanged(position);
        }
    }


    public String getDetail(Albums album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
        ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }


    private void callDeleteApi(final int position, Map<String, Object> map) {

        if (isNetworkAvailable(context)) {
            videoList.remove(position);
            adapter.notifyItemRemoved(position);
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_EVENT);
                //  request.params.put(Constant.KEY_EVENT_ID, eventId);
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
                                    adapter.notifyDataSetChanged();
                                    Util.showSnackbar(v, new JSONObject(response).optJSONObject("result").optString("message"));
                                } else {
                                    //if anythis goes wrong than reload list
                                    onRefresh();
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
                CustomLog.e(e);
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }

    }


    @Override
    public void onRefresh() {
        //this method is overriden on its child class
    }
}
