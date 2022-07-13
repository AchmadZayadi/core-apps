package com.sesolutions.ui.group_core;

import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Group;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

/**
 * Created by root on 29/11/17.
 */

public class CGroupHelper extends CommentLikeHelper {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public CGroupParentFragment parent;
    public List<Group> videoList;
    public CGroupAdapter adapter;


    public RelativeLayout hiddenPanel;
    public List<Options> menuItem;


    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        switch (object1) {
            case Constant.Events.MUSIC_ADD:
                if (null != menuItem) {
                    setFeedUpdateRecycleView(postion);
                    slideUpDown();
                }
                break;

            case Constant.Events.LIKED:
                reactionType = stats.getReactionPlugin().get(Integer.parseInt("" + screenType)).getReactionId();
                updateLike(reactionType);
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);

                /*case Constant.Events.FEED_UPDATE_OPTION:
                slideUpDown();
                Options vo = menuItem.get(Integer.parseInt(screenType));
                performFeedOptionClick(videoList.get(postion).getAlbumId(), vo, Integer.parseInt(screenType), postion);
                break;*/

            case Constant.Events.MUSIC_FAVORITE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_LIKE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_MAIN:
                goToViewGroupFragment(screenType, postion);
                break;
        }
        return super.onItemClicked(object1, screenType, postion);
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
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(menuItem, context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void goToViewGroupFragment(Object view, int position) {
        goToViewCGroupFragment(videoList.get(position).getGroupId());
        /*GroupAdapter.Holder holder = (GroupAdapter.Holder) view;
        try {
            String transitionName = videoList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
            ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, videoList.get(position).getImages().getMain());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewGroupFragment.newInstance(videoList.get(position).getGroupId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToViewGroupFragment(videoList.get(position).getGroupId());
        }*/

    }


    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final Group vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_RESOURCE_ID, vo.getGroupId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    // request.headres.put("Content-Type", "application/x-www-form-urlencoded");
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
                    Log.d(Constant.TAG, "Error while login" + e);
                }
                Log.d(Constant.TAG, "login Stop");
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateItemLikeFavorite(int REQ_CODE, int position, Group vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
        }

    }


    public String getDetail(Blog album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
        ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }


}
