package com.sesolutions.ui.dashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.ui.AGvideo.AGVideoActivity;
import com.sesolutions.ui.comment.CommentFragment;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.receivers.HttpNotificationBroadcast;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Attachment;
import com.sesolutions.responses.feed.AttachmentData;
import com.sesolutions.responses.feed.Attribution;
import com.sesolutions.responses.feed.FeedResponse;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.sesdb.SesDB;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.customviews.AttributionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.music_album.HelperFragment;
import com.sesolutions.ui.storyview.StoryModel;
import com.sesolutions.ui.storyview.StoryPlayer;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.jzvd.Jzvd;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
/**
 * Created by root on 7/12/17.
 */
abstract public class FeedHelper extends HelperFragment {

    public static final int REQ_CODE_LOADING_MORE_FEED = 100;
    public static final int REQ_CODE_FEED = 200;
    public static final int REQ_CODE_OPTION_DELETE = 204;
    public static final int REQ_CODE_OPTION_UNSAVE = 205;
    public static final int REQ_CODE_OPTION_SAVE = 206;
    public static final int REQ_CODE_OPTION_COMMENT_DISABLE = 207;
    public static final int REQ_CODE_OPTION_COMMENT_ENABLE = 208;
    public static final int REQ_CODE_OPTION_HIDE_FEED = 209;
    public static final int REQ_CODE_OPTION_REPORT = 210;
    public static final int REQ_CODE_OPTION_UNDO = 211;
    private static final int REQ_CODE_FILTER_FEED = 212;
    public static final int REQ_CODE_REFRESH = 213;
    public static final int REQ_CODE_REFRESH_DATA = 298;
    static final int REQ_VIEW_FEED = 214;
    static final int REQ_CODE_TAG = 215;
    private static final int REQ_MARK_SOLD = 218;
    public final int REQ_STORY = 995;
    final int REQ_CODE_POLL_VOTE_RESULT = 996;

    public ComposerOption composerOption;

    public boolean isLoading;
    private String selectedFeedType = "all";
    public ProgressBar pb;
    public List<Activity> feedActivityList;
    public SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout hiddenPanel;

    public FeedActivityAdapter adapterFeedMain;
    public FeedResponse feedResponse;
    public int userId = 0;
    int actionId;  //used to load view page from its child class[ViewFeedFragment]
    String tag;

    private HttpNotificationBroadcast broadcastReceiver;
    private int dummyFeedPosition;

    //variable used to use to fetch or save latest data
    public boolean canCacheData = false;

    public void callComposerOptionApi() {
        callComposerOptionApi(true);
    }

    void callComposerOptionApi(final boolean isInForeground) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    //   showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_COMPOSE_OPTION);

                    if (null != resourceType) {
                        request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                        request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    }

                    if (userId > 0) {
                        request.params.put(Constant.KEY_RESOURCE_ID, userId);
                        request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.USER);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {


                                composerOption = new Gson().fromJson(response, ComposerOption.class);
                                if (null != composerOption.getResult().getFeedSearchOptions())
                                    composerOption.getResult().getFeedSearchOptions().get(0).setSelected(true);
                                //save latest composer option json in cache
                                if (canCacheData) {
                                    SPref.getInstance().saveComposerOptions(context, response);
                                    //save latest TextColorString option json in cache
                                    SPref.getInstance().saveTextColorString(context, composerOption.getResult().getTextStringColor());
                                }

                                updateComposerUI();
                                //set recycle view and call feed item api ,only if composerOption not saved previously foreground
                                if (isInForeground) {
                                    callFeedApi(REQ_CODE_FEED);
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
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
    public void listenNotificationEvent(int TYPE) {
        if (TYPE == 1) {

//            Activity feed = SesDB.daoInstance(context).getFeedById(1);
            dummyFeedPosition = AppConfiguration.isStoryEnabled ? 2 : 1;
//            dummyFeedPosition = 1;
//            feedActivityList.add(dummyFeedPosition, feed);
            Activity feed = new Activity(Constant.ItemType.UPDATE_STATUS);
//            UserMaster userMaster = SPref.getInstance().getUserMasterDetail(context);
//            Item_user user = new Item_user(userMaster.getUserId(), Objects.requireNonNull(userMaster.getMemberTitle()), Objects.requireNonNull(userMaster.getProfileImageUrl()));
//            feed.setItemUser(user);
            feed.setActivityIcon("f0e5");
            feed.setType(Constant.ItemType.UPDATE_STATUS);

            feedActivityList.add(dummyFeedPosition, feed);
            adapterFeedMain.notifyItemInserted(dummyFeedPosition);
            scrollToPosition(dummyFeedPosition);

            final IntentFilter intentFilter = new IntentFilter();
            //adding some filters
            intentFilter.addAction(HttpNotificationBroadcast.NOTIFY_PROGRESS);
            intentFilter.addAction(HttpNotificationBroadcast.NOTIFY_FINISHED);

            broadcastReceiver = new HttpNotificationBroadcast(this);
            context.registerReceiver(broadcastReceiver, intentFilter);

        } else {
            context.unregisterReceiver(broadcastReceiver);
        }
    }


    public void addDummyStory() {
        //Override this method on child class
    }

    public void callFeedApi(final int REQ_CODE) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_ACTIVITY);

                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (REQ_CODE == REQ_CODE_LOADING_MORE_FEED) {
                    request.params.put(Constant.KEY_NEXT_ID, feedResponse.getResult().getNextid());
                    request.params.put(Constant.KEY_FILTER_FEED, selectedFeedType);
                    request.params.put(Constant.KEY_CONTENT_COUNTER, feedResponse.getResult().getContentCounter());
                } else if (REQ_CODE == REQ_CODE_FILTER_FEED || REQ_CODE == REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_NEXT_ID, 0);
                    request.params.put(Constant.KEY_FILTER_FEED, selectedFeedType);
                    //  request.params.put(Constant.KEY_HASH_TAG, feedResponse.getResult().getNextid());
                }

                //show loader if user clicks on filter icons
                if (REQ_CODE == REQ_CODE_FILTER_FEED)
                    showBaseLoader(true);

                //params for single view feed

                if (SPref.getInstance().getDefaultInfo(context, Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
                    if (REQ_CODE == REQ_VIEW_FEED) {
                        request.params.put(Constant.KEY_ACTION_ID, actionId);
                      //  request.params.put("allvideos", "1");
                        showBaseLoader(false);
                        request.params.put(Constant.KEY_LIMIT, 1);
                        feedActivityList.clear();
                    }
                }else
                {
                    if (REQ_CODE == REQ_VIEW_FEED) {
                        request.params.put(Constant.KEY_ACTION_ID, actionId);
                        showBaseLoader(false);
                        feedActivityList.clear();
                    }
                }


                if (REQ_CODE == REQ_CODE_TAG) {
                    request.params.put(Constant.KEY_HASH_TAG, tag);
                    feedActivityList.clear();
                }


                if (null != resourceType) {
                    request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                }
                if (userId > 0) {
                    request.params.put(Constant.KEY_RESOURCE_ID, userId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.USER);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    if (canLoadStories(REQ_CODE))
                        callStoryApi();
                    try {
                        String response = (String) msg.obj;
                        //  String response = URLDecoder.decode((String) msg.obj, "UTF-8");
                        if (REQ_CODE == REQ_CODE_REFRESH)
                            updateNotificationCount(0);
                        isLoading = false;
                        setRefreshing(false);
                      CustomLog.e("response_feed", "" + response);
                        if (response != null) {
                            //always save latest response
                            if (canCacheData && REQ_CODE == REQ_CODE_REFRESH) {
                                SPref.getInstance().saveFeedItems(context, response);
                            }
                            setContentLoaded();
                            feedResponse = new Gson().fromJson(response, FeedResponse.class);
                            initMainRecyclerView();
                            if (REQ_CODE == REQ_CODE_FILTER_FEED || REQ_CODE == REQ_CODE_REFRESH || REQ_CODE == REQ_VIEW_FEED || REQ_CODE == REQ_CODE_TAG) {
                                feedActivityList.clear();
                                if(REQ_CODE != REQ_CODE_REFRESH){
                                    addComposerToList();
                                }
                                if (canLoadStories(REQ_CODE)) {
                                    addDummyStory();
                                }
                                addFilterList();
                                adapterFeedMain.notifyDataSetChanged();
                            } else if (REQ_CODE == REQ_CODE_FILTER_FEED || REQ_CODE == REQ_CODE_REFRESH_DATA) {
                                if (SPref.getInstance().getDefaultInfo(getContext(), Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
                                    feedActivityList.subList(2, feedActivityList.size()).clear();
                                    adapterFeedMain.notifyDataSetChanged();
                                } else {
                                    feedActivityList.subList(3, feedActivityList.size()).clear();
                                    adapterFeedMain.notifyDataSetChanged();
                                }
                            }
                            int size = feedActivityList.size();
                            if (null != feedResponse.getResult().getActivity()) {
                                feedActivityList.addAll(feedResponse.getResult().getActivity());
                                //adding ad-items at every  @AD_POS position ,so ad can be shown
                                int newItems = feedResponse.getResult().getActivity().size();
                                try {
                                    if (AppConfiguration.isAdEnabled) {
                                        for (int i = newItems - 1; i > 0; i--) {
                                            if (i % AppConfiguration.AD_POS == 0) {
                                                feedActivityList.add(size + i, new Activity(Constant.ItemType.GOOGLE_AD));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    CustomLog.e(e);
                                }
                                if (REQ_CODE == REQ_CODE_FILTER_FEED && newItems == 0)
                                    adapterFeedMain.notifyItemRangeRemoved(3, feedActivityList.size() - 3);
                                else if (REQ_CODE == REQ_CODE_FILTER_FEED)
                                    adapterFeedMain.notifyItemRangeChanged(3, feedActivityList.size());
                                else if (newItems > 0)
                                    adapterFeedMain.notifyItemRangeInserted(size, newItems);
                                else
                                    adapterFeedMain.notifyDataSetChanged();

                            }
                            updateFeedMainRecycleview();
                        }
                    } catch (Exception e) {
                        hideBaseLoader();
                        pb.setVisibility(View.GONE);
                        setRefreshing(false);
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);
            } catch (Exception e) {
                isLoading = false;
                pb.setVisibility(View.GONE);
                hideBaseLoader();
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }

    private boolean canLoadStories(int REQ_CODE) {
        return ((REQ_CODE == REQ_CODE_FILTER_FEED || REQ_CODE == REQ_CODE_FEED || REQ_CODE == REQ_CODE_REFRESH_DATA || REQ_CODE == REQ_CODE_REFRESH) && REQ_CODE != REQ_CODE_TAG && REQ_CODE != REQ_VIEW_FEED && AppConfiguration.isStoryEnabled && SPref.getInstance().isLoggedIn(context));
    }

    public void callStoryApi() {
        //handle this on HomeFragment
    }

    private void addComposerToList() {
        if (null != composerOption) {
            feedActivityList.add(new Activity(Constant.ItemType.COMPOSER));
            adapterFeedMain.setComposer(composerOption);
        }
    }

    private void addFilterList() {
        if (null != composerOption) {
            feedActivityList.add(new Activity(Constant.ItemType.FEED_FILTER));
//            adapterFeedMain.setComposer(composerOption);
        }
    }

    private void showAttributionPopUp(View v, int position) {
        try {
            String postAttribution = feedActivityList.get(position).getPostAttribution();
            if (TextUtils.isEmpty(postAttribution)) {
                return;
            }
            List<Attribution> attributionList = feedResponse.getResult().getModuleAttribution(postAttribution);
            if (null == attributionList) return;
            AttributionPopup popup = new AttributionPopup(v.getContext(), position, this, attributionList);
            popup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.CENTER, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {

                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        ArrayList<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));

                        String compString = "{}";
//                        if (null != composerOption) {
                        compString = new Gson().toJson(SPref.getInstance().getComposerOptions(context));
//                        }
                        Intent intent = new Intent(activity, CommonActivity.class);
                        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_POST_FEED);
                        intent.putExtra(Constant.KEY_NAME, 0);
                        intent.putExtra(Constant.KEY_TITLE, compString);
                        intent.putStringArrayListExtra("photopath", photoPaths);
                        startActivity(intent);
                    }
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer clickType, Object value, int postion) {
        CustomLog.d("FEED_HELPER", clickType + "_" + value + "_" + postion);
        try {
            switch (clickType) {
                case Constant.Events.PROGRESS_UPDATE:
                    feedActivityList.get(dummyFeedPosition).setAdId(postion);
                    adapterFeedMain.notifyItemChanged(dummyFeedPosition);
                    break;
                case Constant.Events.SUCCESS:
                    // it means status is successfully uploaded , so refresh the feed list
                    listenNotificationEvent(2);
                    callFeedApi(REQ_CODE_REFRESH);
                    break;
                case Constant.Events.CLICKED_HEADER_TITLE:
                case Constant.Events.CLICKED_HEADER_IMAGE:
                    Item_user iu = feedActivityList.get(postion).getItemUser();
                    if (null != iu && userId != iu.getUser_id()) {
                        performClick(iu.getType(), iu.getUser_id(), null, false);
                        //goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, id);
                    }
                    break;

                case Constant.Events.STORY_ARCHIVE:
                    goTo(Constant.GoTo.ARCHIVE, Constant.KEY_ID, -1);
                    //openFormFragment(Constant.FormType.STORY_ARCHIVE,null,Constant.);
                    break;
                case Constant.Events.VIEW_STORY:
                    if (postion == 0) {

                        List<StoryModel> list = (List<StoryModel>) value;
                        if (null != list.get(0).getImages()) {
                            Intent intent = new Intent(context, CommonActivity.class);

                            //String model = new Gson().toJson(value);
                            intent.putExtra(Constant.STORY_IMAGE_KEY, new Gson().toJson(list.get(0)));
                            intent.putExtra(Constant.KEY_USER_ID, true);
                            intent.putExtra(Constant.KEY_POSITION, postion);
                            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.MY_STORY);
                            //intent.putParcelableArrayListExtra(StoryPlayer.STORY_IMAGE_KEY, (ArrayList<StoryModel>) value);
                            startActivity(intent);
                        }
                        else {
                            startActivityForResult(
                                    new ImageEditor.Builder(getActivity())
                                            .setStickerAssets("stickers")
                                            // .setQuote(title)
                                            // .setQuoteSource(source)
                                            .getMultipleEditorIntent(),
                                    ImageEditor.RC_IMAGE_EDITOR);
                        }


                    }
                    else {
                        List<StoryModel> list = (List<StoryModel>) value;
                        StoryModel model = list.get(postion);
                        if (null != model.isLive()) {
                            if (model.isLive()) {
                                Intent i = new Intent(context, LiveVideoActivity.class);
                                i.putExtra(Constant.KEY_HOST_ID, model.getUserId());
                                i.putExtra(Constant.KEY_ACTIVITY_ID, model.getActivityId());
                                i.putExtra(Constant.STORY_IMAGE_KEY, "" + value);
                                i.putExtra(Constant.KEY_POSITION, postion);
                                startActivity(i);
                                break;
                            }
                        }
                        Intent intent = new Intent(context, StoryPlayer.class);
                        //String model = new Gson().toJson(value);
                        intent.putExtra(Constant.STORY_IMAGE_KEY, new Gson().toJson(value));
                        intent.putExtra(Constant.KEY_POSITION, postion);
                        //intent.putParcelableArrayListExtra(StoryPlayer.STORY_IMAGE_KEY, (ArrayList<StoryModel>) value);
                        startActivity(intent);
                    }
                    break;

                case Constant.Events.ATTRIBUTION:
                    showAttributionPopUp((View) value, postion);
                    break;
                case Constant.Events.ATTRIBUTION_OPTION_CLICK:
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> map = new HashMap<>();
                        //  int actionId = feedActivityList.get(Integer.parseInt("" + value)).getActionId();
                        // String guid = feedResponse.getResult().getPageAttribution().get(postion).getGuid();
                        String attrString = feedActivityList.get(Integer.parseInt("" + value)).getPostAttribution();
                        map.put(Constant.KEY_ACTION_ID, feedActivityList.get(Integer.parseInt("" + value)).getActionId());
                        map.put("guid", feedResponse.getResult().getModuleAttribution(attrString).get(postion).getGuid());
                        feedActivityList.get(Integer.parseInt("" + value)).updateAttribution(feedResponse.getResult().getModuleAttribution(attrString).get(postion));
                        adapterFeedMain.notifyItemChanged(Integer.parseInt("" + value));
                        new ApiController(Constant.URL_CHANGE_ATTRIBUTION, map, context, this, Constant.Events.ATTRIBUTION_CHANGE).setExtraKey(Integer.parseInt("" + value)).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case Constant.Events.MORE_MEMBER:

                    if (activity instanceof MainActivity) {
                        Intent intent = new Intent(activity, CommonActivity.class);
                        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.MORE_MEMBER);
                        intent.putExtra(Constant.KEY_MODULE, feedActivityList.get(Integer.parseInt("" + value)).getPoll().getResourceType());
                        intent.putExtra(Constant.KEY_TITLE, getStrings(R.string.voted_user));
                        intent.putExtra(Constant.KEY_ID, feedActivityList.get(Integer.parseInt("" + value)).getPoll().getOptions().get(postion).getPollOptionId());
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
                        startActivity(intent, options.toBundle());
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_MODULE, feedActivityList.get(Integer.parseInt("" + value)).getPoll().getResourceType());
                        bundle.putString(Constant.KEY_TITLE, getStrings(R.string.voted_user));
                        bundle.putInt(Constant.KEY_ID, feedActivityList.get(Integer.parseInt("" + value)).getPoll().getOptions().get(postion).getPollOptionId());
                        fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
                    }
                    break;

                case Constant.Events.ATTRIBUTION_CHANGE:
                    try {
                        String response = (String) value;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                // update these items : reactionUserData,reactionData,is_like,like
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                                feedActivityList.get(postion).updateFinalLike(res.getResult());
                                adapterFeedMain.notifyItemChanged(postion);
                                        /*feedActivityList.get(position).toggleLike(reactionVo);
                                        adapter.notifyItemChanged(position);*/
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        somethingWrongMsg(v);
                        CustomLog.e(e);
                    }
                    break;

                //suggestion profile view and composer profile view
                case Constant.Events.PROFILE:
                    goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, postion);
                    break;
                case Constant.Events.COMPOSER_OPTIONS:
                    new TedPermission(getContext())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {

                                   getComposerdata(postion);
                                }

                                @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                                }
                            })
                            .setDeniedMessage(getString(R.string.MSG_PERMISSION_DENIED))
                            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();


                    break;
                case Constant.Events.MEMBER_ADD:
                    callAddMemberApi(postion, "" + value);
                    break;
                case Constant.Events.SEE_MORE:
                    String voJson = new Gson().toJson(value);
                    openViewFeed(Constant.ResourceType.ACTIVITY_ACTION, postion, voJson);
                    /*Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
                    intent.putExtra(Constant.KEY_ACTION_ID, postion);
                    intent.putExtra(Constant.KEY_COMMENT_ID, true);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
                    startActivity(intent, options.toBundle());*/
                    //goTo(Constant.GO_TO_VIEW_FEED, Constant.KEY_ACTION_ID, postion);
                    break;
                case Constant.Events.CLICKED_HEADER_TAGGED_1:
                    int id = feedActivityList.get(postion).getTagged().get(0).getUserId();
                    if (userId != id)
                        goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, id);
                    break;
                case Constant.Events.CLICKED_HEADER_TAGGED_2:
                    id = feedActivityList.get(postion).getTagged().get(1).getUserId();
                    if (userId != id)
                        goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, id);
                    break;
                case Constant.Events.CLICKED_BODY_TAGGED:
                    id = Integer.parseInt("" + value);
                    if (userId != id)
                        goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, id);
                    break;
                case Constant.Events.WEBVIEW:
                    openWebView("" + value, "" + value);
                    break;
                case Constant.Events.GREETING_OPTION:
                    if (postion == 0) {
                        goTo(Constant.GO_TO_MEMBER, "0", 0);
                    }
                    break;
                case Constant.Events.LIKED:

                    if (postion > -1) { //this is to handle like click on view video channel page
                        if (Integer.parseInt("" + value) > -1) {
                            int reactionId;
                            ReactionPlugin reactionVo;
                            if (null != composerOption) {
                                reactionVo = composerOption.getResult().getReaction_plugin().get(Integer.parseInt("" + value));
                            } else {
                                reactionVo = SPref.getInstance().getReactionPlugins(context).get(Integer.parseInt("" + value));
                            }

                            reactionId = reactionVo.getReactionId();
                            feedActivityList.get(postion).updateLikeTemp(true, new Like(reactionVo.getImage(), reactionVo.getTitle()));

                            adapterFeedMain.notifyItemChanged(postion);
                            callFeedLikeApi(Constant.URL_LIKE_COMMENT, reactionId, postion);
                        } else {
                            feedActivityList.get(postion).updateLikeTemp(false, new Like());
                            adapterFeedMain.notifyItemChanged(postion);
                            callFeedLikeApi(Constant.URL_UNLIKE_COMMENT, 0, postion);
                        }
                    }

                    break;
                case Constant.Events.LIKED2:

                    if (postion > -1) { //this is to handle like click on view video channel page
                        if (Integer.parseInt("" + value) > -1) {
                            int reactionId;
                            ReactionPlugin reactionVo;
                            if (null != composerOption) {
                                reactionVo = composerOption.getResult().getReaction_plugin().get(Integer.parseInt("" + value));
                            } else {
                                reactionVo = SPref.getInstance().getReactionPlugins(context).get(Integer.parseInt("" + value));
                            }

                            reactionId = reactionVo.getReactionId();
                            feedActivityList.get(postion).updateLikeTemp(true, new Like(reactionVo.getImage(), reactionVo.getTitle()));
                         //   adapterFeedMain.canPlayFirstVideo2(true);
                            callFeedLikeApi2(Constant.URL_LIKE_COMMENT, reactionId, postion);
                        } else {
                            feedActivityList.get(postion).updateLikeTemp(false, new Like());
                       //     adapterFeedMain.canPlayFirstVideo2(true);
                            callFeedLikeApi2(Constant.URL_UNLIKE_COMMENT, 0, postion);
                        }
                    }

                    break;

                case Constant.Events.POPUP:
                    if (activity instanceof MainActivity /*&& activity.currentFragment instanceof DashboardFragment*/) {
                        ((MainActivity) activity).setViewPagerSwipable(Boolean.parseBoolean("" + value));
                    }
                    break;
                case Constant.Events.BUY:
                    openWebView(feedActivityList.get(postion).getAttachment().getBuyUrl(), feedActivityList.get(postion).getAttachment().getTitle());
                    break;
                case Constant.Events.CLICKED_FILE_PREVIEW:
                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                    intent.putExtra(Constant.KEY_URI, "" + value);
                    intent.putExtra(Constant.KEY_TITLE, getStrings(R.string.TITLE_FILE_PREVIEW));
                    startActivity(intent);
                    break;
                case Constant.Events.CLICKED_POST_SOMETHING:
                    goToPostFeed(composerOption, -1);
                    break;
                case Constant.Events.CLICKED_BODY_HASH_TAGGED:
                    goToTagFeed("" + value);
                    break;

                case Constant.Events.CATEGORY:
                    openViewQuoteCategoryFragment(postion, "" + value, false);
                    break;
                case Constant.Events.FEED_COMMENT:
                    closeKeyboard();
                    Map<String, Object> params = new HashMap<>();
                    params.put("body", value.toString());
                    callCreateCommentApi(params, feedActivityList.get(postion).getActionId());
                    break;
                case Constant.Events.COMMENT:
                    goToComment(postion);
                    break;
                case Constant.GoTo.REACTION:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ACTIVITY_ACTION);
                    map.put(Constant.KEY_ID, postion);
                    openReactionViewfragment(map);
                    break;
                case Constant.Events.CLICKED_OPTION:
                    setFeedUpdateRecycleView(postion);
                    // slideUpDown();
                    break;
                case Constant.Events.FEED_UPDATE_OPTION:
                    //   hideSlideLayout();
                    Options vo;
                    if (TextUtils.isEmpty(feedActivityList.get(Integer.parseInt("" + value)).getContentType())) {
                        vo = feedActivityList.get(Integer.parseInt("" + value)).getOptions().get(postion);
                    } else {
                        vo = feedActivityList.get(Integer.parseInt("" + value)).getMenus().get(postion);
                    }
                    int actionId = feedActivityList.get(Integer.parseInt("" + value)).getActionId();
                    performFeedOptionClick(actionId, vo, Integer.parseInt("" + value), postion);
                    break;
                case Constant.Events.UNDO:
                    actionId = feedActivityList.get(postion).getActionId();
                    callFeedEventApi(REQ_CODE_OPTION_UNDO, Constant.URL_FEED_HIDDEN, actionId, null, postion, postion);
                    break;
                case Constant.Events.REPORT:
                    String guid = feedActivityList.get(postion).getGuid();
                    goToReportFragment(guid);
                    break;

                case Constant.Events.SHARE_FEED:
                    if (Integer.parseInt("" + value) == 1)
                        sharingToSocialMedia(feedActivityList.get(postion).getShare(), "com.facebook.katana");
                    else if (Integer.parseInt("" + value) == 2)
                        sharingToSocialMedia(feedActivityList.get(postion).getShare(), "com.whatsapp");
                    else
                        showShareDialog(feedActivityList.get(postion).getShare());
                    break;
                case Constant.Events.FEED_FILTER_OPTION:
                    if (!isRefreshing()) {
                        // selectedFeedType = adapterFeed.getList().get(postion).getKey();
//                        selectedFeedType = "" + value;
                        selectedFeedType = composerOption.getResult().getFeedSearchOptions().get(postion).getKey();
                        callFeedApi(REQ_CODE_FILTER_FEED);
                        /*adapterFeed.getList().get(Integer.parseInt("" + value)).setSelected(false);
                        adapterFeed.getList().get(postion).setSelected(true);
                        adapterFeed.setLastPosition(postion);
                        adapterFeed.notifyDataSetChanged();*/
                        if (activity instanceof MainActivity)
                            ((MainActivity) activity).dashboardFragment.filterFeedType = selectedFeedType;

                    }
                    break;
                case Constant.Events.FEED_MAP:
                    LocationActivity la = feedActivityList.get(postion).getLocationActivity();
                    if (null != la) {
                        intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + la.getLat() + "," + la.getLng()));
                        startActivity(intent);
                    }
                    break;

                case Constant.Events.SUGGESTION_MAIN:
                    goTo(Constant.GoTo.SUGGESTION, Constant.KEY_ID, 0);
                    break;

                case Constant.Events.MARK_SOLD:
                    if (feedActivityList.get(postion).getAttachment().isCan_message_owner()) {
                        List<Item_user> list = new ArrayList<>();
                        list.add(feedActivityList.get(postion).getItemUser());
                        openComposeActivity(list);
                    } else {
                        actionId = feedActivityList.get(postion).getActionId();
                        callFeedEventApi(REQ_MARK_SOLD, Constant.URL_MARK_SOLD, actionId, null, postion, -1);
                    }
                    break;

                //poll clicked
                case Constant.Events.VOTE:
                    if (isNetworkAvailable(context)) {
                        int parentPosition = Integer.parseInt("" + value);
                        Poll poll = feedActivityList.get(parentPosition).getPoll();

                        Map<String, Object> request = new HashMap<>();
                        request.put(Constant.KEY_ID, poll.getPollId());
                        request.put("token", poll.getToken());
                        request.put("option_id", poll.getOptions().get(postion).getPollOptionId());
                        String URL_VOTE = ModuleUtil.getInstance().fetchVoteUrl(poll.getResourceType());
                        //String URL_VOTE = ModuleUtil.getInstance().fetchVoteUrl(Constant.ResourceType.PAGE_POLL);
                        poll.setHasVotedId(poll.getOptions().get(postion).getPollOptionId());
                        feedActivityList.get(parentPosition).setPoll(poll);
                        new ApiController(URL_VOTE, request, context, this, REQ_CODE_POLL_VOTE_RESULT).setExtraKey(parentPosition).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case REQ_STORY:


                    StoryResponse res = new Gson().fromJson("" + value, StoryResponse.class);
                    if (res.isSuccess()) {
                        List<StoryModel> storyList = new ArrayList<>();
                        if (null != res.getResult().getMyStory()) {
                            storyList.add(res.getResult().getMyStory());
                        }
                        if (null != res.getResult().getStories()) {
                            storyList.addAll(res.getResult().getStories());

                        }
                        if (storyList.size() > 0) {
                            adapterFeedMain.setStories(storyList);
                            //if (Constant.ItemType.STORY.equals(feedActivityList.get(0).getContentType())) {
                            //it means story item are already added . so only update items
                            adapterFeedMain.notifyItemChanged(1);

                            /*} else {
                                feedActivityList.add(0, new Activity(Constant.ItemType.STORY));
                                adapterFeedMain.notifyItemInserted(0);
                                scrollToPosition(0);
                            }*/
                        }

                        /* else {
                            somethingWrongMsg(v);
                        }*/

                    } else {
                        Util.showSnackbar(v, res.getMessage());
                    }
                    break;

                case REQ_CODE_POLL_VOTE_RESULT:
                    try {
                        ErrorResponse err = new Gson().fromJson("" + value, ErrorResponse.class);
                        if (err.isSuccess()) {
                            JSONObject resp = new JSONObject("" + value);
                            String token = resp.getJSONObject("result").getString("token");
                            int votesTotal = resp.getJSONObject("result").getInt("votes_total");
                            Poll poll = feedActivityList.get(postion).getPoll();
                            poll.setToken(token);
                            poll.setHasVoted(true);
                            // poll.setHasVotedId(optionList.get(position).getPollOptionId());
                            poll.setVoteCount(votesTotal);
                            JSONArray votesCount = resp.getJSONObject("result").getJSONArray("vote_detail");
                            try {
                                for (int i = 0; i < votesCount.length(); i++) {
                                    poll.getOptions().get(i).setVotePercent(votesCount.getString(i));
                                    poll.getOptions().get(i).setVotes(Integer.parseInt(votesCount.getString(i).split(" ")[0]));
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            feedActivityList.get(postion).setPoll(poll);
                        } else {
                            Util.showSnackbar(v, err.getMessage());
                        }
                    } catch (JSONException e) {
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                        // onRefresh();
                    }

                    adapterFeedMain.notifyItemChanged(postion);
                    // adapter.setPoll(poll);
                    // isShowingQuestion = !isShowingQuestion;
                    // adapter.showQuestion(isShowingQuestion);
                    // adapter.notifyDataSetChanged();
                    break;

                case Constant.Events.IMAGE_1:
                case Constant.Events.IMAGE_2:
                case Constant.Events.IMAGE_3:
                case Constant.Events.IMAGE_4:
                case Constant.Events.IMAGE_5:
                case Constant.Events.VIDEO:

                    StaticShare.FEED_ACTIVITY = feedActivityList.get(postion);
                    if (feedActivityList.get(postion).getType().equals(Constant.ResourceType.VIDEO_FEED_GO_LIVE)) {
                        intent = new Intent(activity, LiveVideoActivity.class);
                        intent.putExtra(Constant.KEY_HOST_ID, feedActivityList.get(postion).getSubjectId());
                        intent.putExtra(Constant.KEY_ACTIVITY_ID, feedActivityList.get(postion).getActionId());
                        intent.putExtra(Constant.KEY_TYPE, feedActivityList.get(postion).getType());
                        intent.putExtra(Constant.KEY_OBJECTID_Data, feedActivityList.get(postion).getObjectId());
                        startActivity(intent);
                        break;
                    }
                    Attachment voAt = feedActivityList.get(postion).getAttachment();
                    if (null != voAt)
                        onFeedImageClicked(clickType, voAt, null, postion);
                    break;
                case Constant.Events.CLICKED_HEADER_ACTIVITY_TYPE:
                    List<ActivityType> actTypeList = feedActivityList.get(postion).getActivityType();
                    for (ActivityType vo1 : actTypeList) {
                        if (value.equals(vo1.getKey())) {
                            if(vo1.getType().equalsIgnoreCase(""+Constant.ACTIVITY_TYPE_VIDEO)){
                                Attachment voAt2 = feedActivityList.get(postion).getAttachment();
                                goTo(Constant.GoTo.VIDEO, feedActivityList.get(postion).getObjectId(), ""+voAt2.getAttachmentType());
                            }else {
                                onFeedImageClicked(clickType, null, vo1, postion);
                            }
                        }
                    }
                    break;
                case Constant.Events.VIDEO_Title:
                    Attachment voAt2 = feedActivityList.get(postion).getAttachment();
                    goTo(Constant.GoTo.VIDEO, feedActivityList.get(postion).getActionId(), ""+voAt2.getAttachmentType());
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(clickType, value, postion);
    }

    private void getComposerdata(int postion) {
        if (postion == 1) {
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .showFolderView(true)
                    .enableImagePicker(true)
                    .enableVideoPicker(false)
                    .pickPhoto(this);

        } else
            goToPostFeed(composerOption, postion, resourceId, resourceType);
    }

    public void scrollToPosition(int index) {
        //override this on HomeFragment
    }

    private boolean isRefreshing() {
        return null != swipeRefreshLayout && swipeRefreshLayout.isRefreshing();
    }

    public void setRefreshing(boolean isRefreshing) {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    private void callFeedLikeApi(final String url, final int likeType, final int position) {

        if (isNetworkAvailable(context)) {
            try {

                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_ACTIVITY_ID, feedActivityList.get(position).getActionId());
                request.params.put(Constant.KEY_TYPE, likeType);
                String guid = feedActivityList.get(position).getAttributionGuid();
                if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;

                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                // update these items : reactionUserData,reactionData,is_like,like
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                            //    adapterFeedMain.canPlayFirstVideo2(true);
                                feedActivityList.get(position).updateFinalLike(res.getResult());
                                adapterFeedMain.notifyItemChanged(position);
                            }
                        }

                    } catch (Exception e) {
                        // somethingWrongMsg(v);
                        CustomLog.e(e);
                    }

                    // dialog.dismiss();
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignored) {

            }
        } else {
            notInternetMsg(v);
        }

    }
    private void callFeedLikeApi2(final String url, final int likeType, final int position) {

        if (isNetworkAvailable(context)) {
            try {

                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_ACTIVITY_ID, feedActivityList.get(position).getActionId());
                request.params.put(Constant.KEY_TYPE, likeType);
                String guid = feedActivityList.get(position).getAttributionGuid();
                if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;

                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                // update these items : reactionUserData,reactionData,is_like,like
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                            //    adapterFeedMain.canPlayFirstVideo2(true);
                                feedActivityList.get(position).updateFinalLike(res.getResult());
                             //   adapterFeedMain.notifyItemChanged(position);
                            }
                        }

                    } catch (Exception e) {
                        // somethingWrongMsg(v);
                        CustomLog.e(e);
                    }

                    // dialog.dismiss();
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignored) {

            }
        } else {
            notInternetMsg(v);
        }

    }


    private void setFeedUpdateRecycleView(int position) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(feedActivityList.get(position).getOptions(), context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callFeedEventApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void performFeedOptionClick(int actionId, Options vo, int actPosition, int position) {
        switch (vo.getName()) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(context, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.SAVE:
              //  showBaseLoader(false);
                callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
            case "feed_link":
                try {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(vo.getUrl(), vo.getUrl());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Util.showSnackbar(v, getString(R.string.copy_clipboard));
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case Constant.OptionType.UNSAVE:
                //showBaseLoader(false);
                callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.DISABLE_COMMENT:
                callFeedEventApi(REQ_CODE_OPTION_COMMENT_DISABLE, Constant.URL_FEED_DISABLE_COMMENT, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.ENABLE_COMMENT:
                callFeedEventApi(REQ_CODE_OPTION_COMMENT_ENABLE, Constant.URL_FEED_DISABLE_COMMENT, actionId, vo, actPosition, position);
                break;
            case "hide_ad":
                //feedActivityList.get(actPosition).setHidden(true);
                //adapterFeedMain.notifyItemChanged(actPosition);
                int adId = feedActivityList.get(actPosition).getAdId();
                ReportAdDialog.newInstance(feedActivityList.get(actPosition).getHiddenData(), this, actPosition, adId).show(getChildFragmentManager(), "AdHide");
                //showAdReportDialog(feedActivityList.get(actPosition).getHiddenData(), actPosition);
                break;
            case "ad_useful":
                callAdUsefulApi(actPosition, position);
                break;
            case Constant.OptionType.HIDE:
                callFeedEventApi(REQ_CODE_OPTION_HIDE_FEED, Constant.URL_FEED_HIDDEN, actionId, vo, actPosition, position);
                break;

            case Constant.OptionType.REPORT:
                callFeedEventApi(REQ_CODE_OPTION_REPORT, Constant.URL_FEED_HIDDEN, actionId, vo, actPosition, position);
                break;

            case Constant.OptionType.EDIT:
                Constant.ACTIVITY = feedActivityList.get(actPosition);
                goToEditFeed(feedActivityList.get(actPosition).getBody());
                break;
        }
    }

    private void callAdUsefulApi(int actPosition, int position) {
        // new ApiController(Constant.URL_USEFUL_CUMMUNITY_AD, map, getContext(), this, -1).setExtraKey(position).execute();
        if (!isNetworkAvailable(context)) {
            notInternetMsg(v);
            return;
        }

        feedActivityList.get(actPosition).getMenus().get(position).toggleUseful(getStrings(R.string.lbl_useful_ad_1), getStrings(R.string.lbl_useful_ad_2));
        adapterFeedMain.notifyItemChanged(actPosition);

        HttpRequestVO request = new HttpRequestVO(Constant.URL_USEFUL_CUMMUNITY_AD);
        request.params.put(Constant.KEY_AD_ID, feedActivityList.get(actPosition).getAdId());
        request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
        request.headres.put(Constant.KEY_COOKIE, getCookie());
        request.requestMethod = HttpPost.METHOD_NAME;
        Handler.Callback callback = msg -> {
            try {
                String response = (String) msg.obj;
                CustomLog.e("repsonse1", "" + response);

            } catch (Exception e) {
                CustomLog.e(e);
                //revert changes in case of any error
                feedActivityList.get(actPosition).getMenus().get(position).toggleUseful(getStrings(R.string.lbl_useful_ad_1), getStrings(R.string.lbl_useful_ad_2));
                adapterFeedMain.notifyItemChanged(actPosition);
            }
            return true;
        };
        new HttpRequestHandler(context, new Handler(callback)).run(request);

    }

    private void goToEditFeed(String bodydata) {
        try {
            if (activity instanceof MainActivity) {
                Intent intent = new Intent(context, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_EDIT_FEED);
                intent.putExtra(Constant.KEY_BODY, bodydata);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_EDIT_FEED);
                intent.putExtra(Constant.KEY_BODY, bodydata);
                startActivity(intent);
              //  fragmentManager.beginTransaction().replace(R.id.container, new PostEditFragment()).addToBackStack(null).commit();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callCreateCommentApi(final Map<String, Object> params, int actionId) {

        if (isNetworkAvailable(context)) {
            final boolean[] isDummyCommentAdded = {false};
            try {
                showBaseLoader(true);
                if (params.containsKey("body")) {
//                    UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
//                    isDummyCommentAdded[0] = true;
//                    commentList.add(0, new CommentData(
//                            (String) params.get("body"),
//                            userVo.getDisplayname(),
//                            userVo.getPhotoUrl(),
//                            Util.getCurrentdate(Constant.DATE_FROMAT_FEED)));
//                    updateFeelingAdapter();
//                    recyclerView.smoothScrollToPosition(0);
                } else {
                    showBaseLoader(true);
                }

                HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_COMMENT);

                request.params.putAll(params);
                request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ACTIVITY_ACTION);

//                if (null != guid) {
//                    request.params.put(Constant.KEY_GUID, guid);
//                }

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;
                        CustomLog.e("response_feed_commment", "" + response);
                        if (response != null) {
                            // response = response.replace("â\u0080\u0099", "'");
                            BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                            //  result = comResp.getResult();

                            if (TextUtils.isEmpty(comResp.getError())) {

//                                attachmentList.clear();
//                                updateImageAttachAdapter();
                                String itemComment = new JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString();
                                CommentData vo = new Gson().fromJson(itemComment, CommentData.class);

//                                if (isDummyCommentAdded[0]) {
//                                    commentList.get(0).updateObject(vo);
//                                    updateFeelingAdapter();
//                                } else {
//                                    etBody.setText(Constant.EMPTY);
//                                    commentList.add(0, vo);
//                                    updateFeelingAdapter();
//                                    recyclerView.smoothScrollToPosition(0);
//                                }
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();

                        CustomLog.e(e);
                    }

                    return true;
                };
                new HttpImageRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void callFeedEventApi(final int reqCode, String url, int actionId, final Options vo, final int actPosition, final int position) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_ACTION_ID, actionId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        //  hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                BaseResponse<Object> resp = new Gson().fromJson(response, BaseResponse.class);


                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_DELETE:
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            adapterFeedMain.notifyItemRemoved(actPosition);
                                            Util.showSnackbar(v, res.getResult());
                                            break;
                                        case REQ_MARK_SOLD:
                                            feedActivityList.get(actPosition).getAttachment().toggleSold();
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;

                                        case REQ_CODE_OPTION_SAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition, position, "unsave", Constant.TXT_UNSAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition, position, "save", Constant.TXT_SAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_COMMENT_DISABLE:
                                            feedActivityList.get(actPosition).toggleCommantable();
                                            updateOptionText(actPosition, position, "enable_comment", Constant.TXT_ENABLE_COMMENT);
                                            break;

                                        case REQ_CODE_OPTION_COMMENT_ENABLE:
                                            feedActivityList.get(actPosition).toggleCommantable();
                                            updateOptionText(actPosition, position, "disable_comment", Constant.TXT_DISABLE_COMMENT);
                                            break;
                                        case REQ_CODE_OPTION_HIDE_FEED:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_UNDO:
                                            feedActivityList.get(actPosition).setHidden(false);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_REPORT:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(true);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                    }

                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
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

    private void updateOptionText(int actPosition, int position, String name, String value) {
        CustomLog.e("values", actPosition + "___" + position + "___" + name);
        feedActivityList.get(actPosition).getOptions().get(position).setValue(value);
        feedActivityList.get(actPosition).getOptions().get(position).setName(name);
        adapterFeedMain.notifyItemChanged(actPosition);
    }

    private void onFeedImageClicked(Integer clickType, Attachment voAt, ActivityType actType, int actPosition) {
        String type = Constant.EMPTY;
        int id = 0;
        if (actType != null) {

            type = actType.getType();
            id = actType.getId();
        } else if (voAt != null) {
            type = voAt.getAttachmentType();
            id = voAt.getAttachment_id();
        }
        switch (type) {
            case Constant.ACTIVITY_TYPE_ALBUM:
            case Constant.ATTACHMENT_TYPE_ALBUM_PHOTO2:
                if (voAt != null) {
                    AttachmentData voAd =null;
                    try {
                        voAd = voAt.getImages().get(clickType - 38).getAttachmentData();
                        String atType = voAd.getType();//Constant.ACTIVITY_TYPE_ALBUM;
                        int albumOrActionId;
                        if (voAt.getBuysell_id() > 0) {
                            atType = Constant.ACTIVITY_TYPE_BUY_SELL;
                            albumOrActionId = feedActivityList.get(actPosition).getActionId();
                        } else {
                            albumOrActionId = voAd.getAlbumId();
                        }
                        goToGalleryFragment(atType, albumOrActionId, voAd.getPhotoId(), voAd.getType(), voAt.getImages().get(clickType - 38).getMain());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        voAd = voAt.getImages().get(0).getAttachmentData();
                        String atType = voAd.getType();//Constant.ACTIVITY_TYPE_ALBUM;
                        int albumOrActionId;
                        if (voAt.getBuysell_id() > 0) {
                            atType = Constant.ACTIVITY_TYPE_BUY_SELL;
                            albumOrActionId = feedActivityList.get(actPosition).getActionId();
                        } else {
                            albumOrActionId = voAd.getAlbumId();
                        }
                        goToGalleryFragment(atType, albumOrActionId, voAd.getPhotoId(), voAd.getType(), voAt.getImages().get(0).getMain());
                    }
                } else {
                    goToGalleryFragment(actType.getId(), actType.getType(), "");
                }
                break;


            case Constant.ACTIVITY_TYPE_VIDEO:
            case Constant.ACTIVITY_TYPE_VIDEO2:
            case Constant.ACTIVITY_TYPE_VIDEO3:
                if (null != voAt.getVideoUrl()) {
                    StaticShare.ITEM_POSITION = actPosition;
                    StaticShare.FEED_ACTIVITY = feedActivityList.get(actPosition);
                    //   goTo(Constant.GoTo.VIDEO_FEED, Constant.KEY_ID, id);
                    goTo2(Constant.GoTo.VIDEO, feedActivityList.get(actPosition).getActionId(),type,101);
                } else {
                    //goTo(Constant.GoTo.VIDEO, id);
                    goTo2(Constant.GoTo.VIDEO, feedActivityList.get(actPosition).getAttachment().getAttachment_id(),type,102);
                }
                break;
            case Constant.ResourceType.VIDEO_PLAYLIST:
                goTo(Constant.GoTo.VIEW_VIDEO_PLAYLIST, id);
                break;
            case Constant.ACITIVITY_ACTION:
            case "sesadvancedactivity":
                //goTo(Constant.GO_TO_VIEW_FEED, Constant.KEY_ACTION_ID, id);
                openViewFeed(type, id, null);
                break;
            case Constant.ResourceType.PAGE_POLL:
            case Constant.ResourceType.GROUP_POLL:
            case Constant.ResourceType.BUSINESS_POLL:
                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_POLL);
                intent.putExtra(Constant.KEY_ID, id);
                intent.putExtra(Constant.KEY_TYPE, type);
                startActivity(intent);
                break;
            case Constant.ResourceType.VIEW_CORE_POLL:
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_CPOLL);
                intent2.putExtra(Constant.KEY_ID, id);
                intent2.putExtra(Constant.KEY_TYPE, type);
                startActivity(intent2);
                break;
            default:
                int MODULE = ModuleUtil.getInstance().fetchDestination(type);
                if (-1 < MODULE) {
                    goTo(MODULE, Constant.KEY_ID, id);
                } else if (actType != null) {
                    openWebView(actType.getHref(), getStrings(R.string.TITLE_ACTIVITY_FEED));
                } else if (voAt != null) {
                    openWebView(voAt.getHref(), getStrings(R.string.TITLE_ACTIVITY_FEED));
                }
                break;
        }
    }

    private void goToTagFeed(String tag) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIEW_FEED);
            intent.putExtra(Constant.KEY_HASH_TAG, tag);
            intent.putExtra(Constant.KEY_ACTION_ID, 0);
            intent.putExtra(Constant.KEY_COMMENT_ID, false);
            intent.putExtra(Constant.KEY_RESOURCE_ID, 0);
            intent.putExtra(Constant.KEY_RESOURCES_TYPE, "");
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, ViewFeedFragment.newInstance(tag)).addToBackStack(null).commit();
        }
    }

    private void openViewFeed(String type, int id, String voStr) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIEW_FEED);
            intent.putExtra(Constant.KEY_ACTION_ID, id);
            intent.putExtra(Constant.KEY_RESOURCE_ID, id);
            intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
            intent.putExtra(Constant.KEY_COMMENT_ID, true);
            intent.putExtra(Constant.KEY_DATA, voStr);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container,
                            ViewFeedFragment.newInstance
                                    (null, id, true, id, type)).addToBackStack(null).commit();
            //fragmentManager.beginTransaction().replace(R.id.container, ViewFeedFragment.newInstance(tag)).addToBackStack(null).commit();
        }

    }


    @Override
    public void onPause() {
        try {
            Jzvd.releaseAllVideos();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try {
            if (Jzvd.backPress()) {
                return;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onBackPressed();
    }

    protected abstract void setContentLoaded();

    abstract public void updateComposerUI();

    protected abstract void goToComment(int postion);

    public void updateFeedMainRecycleview() {
        try {
            isLoading = false;
            pb.setVisibility(View.GONE);
            setRefreshing(false);
            // adapterFeedMain.notifyDataSetChanged();
            if (activity instanceof MainActivity && feedActivityList.size() > 3) {
                ((MainActivity) activity).dashboardFragment.firstFeedId = feedActivityList.get(3).getActionId();
            }



            if (Objects.requireNonNull(feedResponse.getResult()).getActivity().size() == 0) {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_RESULT);
                try {
                    v.findViewById(R.id.tvNoData).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.tvNoData323).setVisibility(View.VISIBLE);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
              } else {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_FEED_DATA);
                v.findViewById(R.id.tvNoData).setVisibility(feedActivityList.size() > 0 ? View.GONE : View.VISIBLE);
                try {
                    v.findViewById(R.id.tvNoData323).setVisibility(View.GONE);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initMainRecyclerView() {
        //Override this method on its child class like [@HomeFragment,@ProfileFrgment.@VideoChannelFragment,@ViewFeedFragment]
    }

    private void callAddMemberApi(int userId, String url) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    isLoading = true;
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                           /* if (response != null) {
                                BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                Util.showSnackbar(v, resp.getResult());
                                friendList.remove(position);
                                updateRecyclerView();
                            } else {
                                notInternetMsg(v);
                            }*/
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                    isLoading = false;

                }

            } else {
                hideBaseLoader();
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            hideBaseLoader();
            CustomLog.e(e);
        }
    }


}
