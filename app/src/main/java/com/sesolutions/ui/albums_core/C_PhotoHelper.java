package com.sesolutions.ui.albums_core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.AlbumParentFragment;
import com.sesolutions.ui.albums.AlbumPhotoAdapter;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommentLikeHelper_basic;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 29/11/17.
 */

public class C_PhotoHelper extends CommentLikeHelper_basic {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public C_AlbumParentFragment parent;
    public List<Albums> videoList;
    public C_AlbumPhotoAdapter adapter;


    public RelativeLayout hiddenPanel;
    public List<Options> menuItem;

    public void applyTheme() {
        if (v != null) {
            new ThemeManager().applyTheme((ViewGroup) v, context);

        }
    }

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
                goToNextScreen(Integer.valueOf("" + screenType), postion);
                break;
        }
        return false;
    }

    private void updateItemLikeFavorite(int REQ_CODE, int position, Albums vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
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


    private void goToNextScreen(int screenType, int position) {
        switch (screenType) {
            /*case Constant.FormType.TYPE_SONGS:
                Map<String, Object> map = new HashMap<>();
                goToSongsView(map, videoList.get(position).getSongId(), videoList.get(position).getResourceType());
                break;
           */
            case Constant.FormType.TYPE_PHOTO:
                //String url = Constant.BASE_URL + Constant.URL_ALBUM_LIGHTBOX + videoList.get(position).getAlbumId() + Constant.POST_URL;
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_PHOTO_ID, videoList.get(position).getPhotoId());
                map.put(Constant.KEY_TYPE, Constant.ACTIVITY_TYPE_ALBUM);
                map.put(Constant.KEY_IMAGE, videoList.get(position).getImages().getMain());
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM_PHOTO);
                goToGallaryFragment(map);

                break;

            case Constant.FormType.TYPE_MUSIC_ALBUM:
                goToViewAlbumFragment(videoList.get(position).getAlbumId(),false);
                break;
        }
    }

    private void goToGallaryFragment(Map<String, Object> map) {
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();
    }

   /* private void goToViewAlbumFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewAlbumFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }*/

    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final Albums vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    int resourceId = vo.getAlbumId();
                    String resourceType = Constant.ResourceType.ALBUM;
                    if (screenType == Constant.FormType.TYPE_PHOTO) {
                        resourceId = vo.getPhotoId();
                        resourceType = Constant.ResourceType.ALBUM_PHOTO;
                    }/* else if (screenType == Constant.FormType.TYPE_CHANNEL) {
                        resourceId = vo.getChannelId();
                        request.params.put(Constant.KEY_CHANNEL_ID, vo.getChannelId());
                        resourceType = Constant.VALUE_RC_VIDEO_CHANNEL;
                    } else if (screenType == Constant.FormType.TYPE_ARTISTS) {
                        resourceId = vo.getArtistId();
                        resourceType = Constant.VALUE_RC_VIDEO_ARTIST;
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

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public String getDetail(Albums album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
                + "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }


}
