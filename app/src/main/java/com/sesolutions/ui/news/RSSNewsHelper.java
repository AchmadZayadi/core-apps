package com.sesolutions.ui.news;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.view.ViewCompat;
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
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.news.News;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.SpeakableContent;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.ui.music_album.FormFragment;
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

public class RSSNewsHelper extends SpeakableContent {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public static final int VIEW_NEWS_DELETE = 400;
    public OnUserClickedListener<Integer, Object> parent;
    public List<News> videoList;
    public RSSNewsAdapter adapter;
    public RelativeLayout hiddenPanel;
    public List<Options> menuItem;

    public void applyTheme() {
        if (v != null) {
            new ThemeManager().applyTheme((ViewGroup) v, context);
        }
    }

    private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, this, options);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        switch (object1) {
            case Constant.Events.MUSIC_ADD:
                if (null != menuItem) {
                    //setFeedUpdateRecycleView(postion);
                    // slideUpDown();
                    showOptionsPopUp((View) screenType, postion, menuItem);
                }
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                // slideUpDown();
                Options vo = menuItem.get(postion);
                performFeedOptionClick(videoList.get(Integer.valueOf("" + screenType)).getNewsId(), vo, Integer.parseInt("" + screenType), postion);
                break;

            case Constant.Events.MUSIC_FAVORITE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_LIKE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_MAIN:
                goToNextScreen((NewsAdapter.ContactHolder) screenType);
                break;
            case Constant.Events.LIKED:
                reactionType = stats.getReactionPlugin().get(Integer.parseInt("" + screenType)).getReactionId();
                updateLike(reactionType);
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);

        }
        return super.onItemClicked(object1, screenType, postion);
    }

    private void performFeedOptionClick(int newsId, Options vo, int actPosition, int position) {
        switch (vo.getName()) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(1, newsId, actPosition);
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.EDIT:
                goToFormFragment(newsId);
                // callSaveFeedApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
        }
    }

    public void showDeleteDialog(final int REQ, final int newsId, final int position) {
        try {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_NEWS);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ, newsId, position);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, final int newsId, final int position) {

        try {
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_NEWS);
                    request.params.put(Constant.KEY_NEWS_ID, newsId);
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
                                        if (REQ == VIEW_NEWS_DELETE) {
                                            onBackPressed();
                                        } else {
                                            videoList.remove(position);
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


    public void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bootom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            // isPanelShown = true;
        } else {
            hideSlidePanel();
            // isPanelShown = false;
        }
    }

    public void hideSlidePanel() {
        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(context, R.anim.bootom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
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

    private void goToFormFragment(int screenType, int position) {
        /*if (screenType == Constant.TYPE_CHANNEL) {
            callLikeApi(screenType, REQ_FAVORITE, position, Constant.URL_CHANNEL_FOLLOW, videoList.get(position));

        } else {*/

        Map<String, Object> map = new HashMap<>();
        int type = 0;
        if (screenType == Constant.FormType.TYPE_MUSIC_ALBUM) {
            type = Constant.FormType.ADD_VIDEO;
            map.put(Constant.KEY_NEWS_ID, videoList.get(position).getNewsId());
        } /*else {
            type = Constant.FormType.TYPE_ADD_SONG;
            //map.put(Constant.KEY_SONG_ID, videoList.get(position).getSongId());
        }*/

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(type, map, Constant.URL_CREATE_VIDEO_PLAYLIST, 0))
                .addToBackStack(null)
                .commit();
        //  }
    }

    public void goToFormFragment(int newsId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_NEWS_ID, newsId);
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.TYPE_NEWS_EDIT, map, Constant.URL_EDIT_NEWS)).addToBackStack(null).commit();
    }


    private void goToNextScreen(NewsAdapter.ContactHolder holder) {
        int position = holder.getAdapterPosition();
        try {

            //adding transition name in image and title
            String transitionName = videoList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
            ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.ivFavorite, transitionName + Constant.Trans.ICON);

            //put all transition names in bundle pass to next screen
            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            //bundle.putString(Constant.Trans.ICON, transitionName + Constant.Trans.ICON);
            bundle.putString(Constant.Trans.IMAGE_URL, videoList.get(position).getImages().getMain());


            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewNewsFragment.newInstance(videoList.get(position).getNewsId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , ViewNewsFragment.newInstance(videoList.get(position).getNewsId()))
                    .addToBackStack(null)
                    .commit();
        }

    }

   /* private void goToViewNewsFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewNewsFragment.newInstance(videoList.get(postion).getNewsId()))
                .addToBackStack(null)
                .commit();
    }*/

    private void updateItemLikeFavorite(int REQ_CODE, int position, News vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).toggleLike();
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).toggleFavorite();
            adapter.notifyItemChanged(position);
        }

    }

    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final News vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);

                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    int resourceId = vo.getNewsId();
                    String resourceType = vo.getResourceType();
                    /*if (screenType == Constant.FormType.TYPE_PLAYLIST) {
                        resourceId = vo.getPlaylistId();
                        resourceType = Constant.ResourceType.VIDEO_PLAYLIST;
                    } else if (screenType == Constant.FormType.TYPE_CHANNEL) {
                        resourceId = vo.getChannelId();
                        request.params.put(Constant.KEY_CHANNEL_ID, vo.getChannelId());
                        resourceType = Constant.ResourceType.VIDEO_CHANNEL;
                    } else if (screenType == Constant.FormType.TYPE_ARTISTS) {
                        resourceId = vo.getArtistId();
                        resourceType = Constant.ResourceType.VIDEO_ARTIST;
                    }*/
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
                                       /* if (REQ_CODE == REQ_LIKE) {
                                            videoList.get(position).setIsContentLike(!vo.getIsContentLike());
                                        } else if (REQ_CODE == REQ_FAVORITE) {
                                            videoList.get(position).setIsContentFavourite(!vo.getIsContentFavourite());
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

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public String getDetail(News album) {
        String detail = "";
        if (context.getResources().getBoolean(R.bool.isTab)) {
            detail += "\uf164 " + album.getLikeCount() + (album.getLikeCount() != 1 ? Constant._LIKES : Constant._LIKE)
                    + "  \uf075 " + album.getCommentCount() + (album.getCommentCount() != 1 ? Constant._COMMENTS : Constant._COMMENT)
                    + "  \uf004 " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? Constant._FAVORITES : Constant._FAVORITE)
                    + "  \uf06e " + album.getViewCount() + (album.getViewCount() != 1 ? Constant._VIEWS : Constant._VIEW)
                    + "  \uf005 " + album.getIntRating() + (album.getViewCount() != 1 ? Constant._RATINGS : Constant._RATING);
            // + "  \uf001 " + album.getSongCount() + (album.getSongCount() > 1 ? " Songs" : " Song");
        } else {
            detail += "\uf164 " + album.getLikeCount() //+ (album.getLikeCount() != 1 ? " Likes" : " Like")
                    + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                    + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                    + "  \uf06e " + album.getViewCount()//+ (album.getViewCount() != 1 ? " Views" : " View");
                    + "  \uf005 " + album.getIntRating();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        }
        return detail;
    }

}
