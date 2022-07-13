package com.sesolutions.ui.video;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
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

public class VideoHelper extends BaseFragment implements OnUserClickedListener<Integer, Object> {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public OnUserClickedListener<Integer, Object> listener;
    public String selectedScreen;
    public List<Videos> videoList;
    public Videos video;
    public List<Videos> videoList2;
    public VideoAdapter adapter;
    public View v;
    public Typeface iconFont;
    private String resourceType;
    private int resourceId;
    private int colorPrimary;
    private int text2;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        switch (object1) {
            case Constant.Events.MUSIC_ADD:
                if (Integer.valueOf("" + screenType) == Constant.FormType.TYPE_CHANNEL) {
                    callLikeApi(Integer.valueOf("" + screenType), REQ_FOLLOW, postion, Constant.URL_CHANNEL_FOLLOW, videoList.get(postion));
                } else {
                    goToFormFragment(Integer.valueOf("" + screenType), postion);
                }
                break;
            case Constant.Events.WATCH_LATER:
                callLaterApi(videoList.get(postion).getVideoId(), postion);
                // playMusic(videoList.get(postion));
                break;
            case Constant.Events.MUSIC_FAVORITE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_LIKE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_MAIN:
                goToNextScreen(Integer.valueOf("" + screenType), postion);
                break;
            case Constant.Events.SHARE_FEED:

                com.sesolutions.responses.feed.Share share= (com.sesolutions.responses.feed.Share) screenType;
                if (Integer.parseInt("" + postion) == 1)
                    sharingToSocialMedia(share, "com.facebook.katana");
                else if (Integer.parseInt("" + postion) == 2)
                    sharingToSocialMedia(share, "com.whatsapp");
                else
                    showShareDialog(share);
                break;
            case Constant.Events.FEED_UPDATE_OPTION2:
                //   Log.e("888889","9999999"+videoList.get(Integer.parseInt("" + screenType)).getItem().getCategory_title());
                try {
                    boolean isboolen = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().isIs_saved();
                    int actionId = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getResource_id();
                    int shortcutid = 0;
                    if(isboolen){
                        shortcutid = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getShortcut_id();
                    }
                    performFeedOptionClick(actionId,  Integer.parseInt("" + screenType), isboolen,shortcutid);
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

        }
        return false;
    }

    public void goToFormFragment(int screenType, int position) {
        /*if (screenType == Constant.FormType.TYPE_CHANNEL) {
            callLikeApi(screenType, REQ_FAVORITE, position, Constant.URL_CHANNEL_FOLLOW, videoList.get(position));

        } else {*/

        Map<String, Object> map = new HashMap<>();
        int type = 0;
        //  if (screenType == Constant.FormType.TYPE_MUSIC_ALBUM) {
        type = Constant.FormType.ADD_VIDEO;
        map.put(Constant.KEY_VIDEO_ID, videoList.get(position).getVideoId());
        map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
        //  }

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(type, map, Constant.URL_CREATE_VIDEO_PLAYLIST))
                .addToBackStack(null)
                .commit();
        //  }
    }


    private void goToNextScreen(int screenType, int position) {
        switch (screenType) {
            /*case Constant.TYPE_SONGS:
                Map<String, Object> map = new HashMap<>();
                goToSongsView(map, videoList.get(position).getSongId(), videoList.get(position).getResourceType());
                break;
            case Constant.TYPE_LYRICS:
                map = new HashMap<>();
                map.put(Constant.KEY_LYRICS, 1);
                goToSongsView(map, videoList.get(position).getSongId(), videoList.get(position).getResourceType());
                break;*/

            case Constant.FormType.TYPE_MUSIC_ALBUM:
                goToViewVideoAlbumFragment(position);
                break;
            case Constant.FormType.TYPE_CHANNEL:
                goToViewChannelFragment(position);
                break;
            case Constant.FormType.TYPE_PLAYLIST:
                goToViewPlaylistFragment(position);
                break;

            case Constant.FormType.TYPE_ARTISTS:
                goToViewArtistFragment(position);
                break;
        }
    }

    private void goToViewVideoAlbumFragment(int postion) {
        goTo(Constant.GoTo.VIDEO, videoList.get(postion).getVideoId());
        /*fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewVideoFragment.newInstance(videoList.get(postion).getVideoId()))
                .addToBackStack(null)
                .commit();*/
    }

    private void goToViewPlaylistFragment(int postion) {
        goTo(Constant.GoTo.VIEW_VIDEO_PLAYLIST, videoList.get(postion).getPlaylistId());
        /*fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPlaylistVideoFragment.newInstance(videoList.get(postion).getPlaylistId(), null, null))
                .addToBackStack(null)
                .commit();*/
    }

    private void goToViewChannelFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewChannelFragment.newInstance(videoList.get(postion).getChannelId()))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewArtistFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(videoList.get(postion).getArtistId(), videoList.get(postion).getName()))
                .addToBackStack(null)
                .commit();
    }


  /*  private void playMusic(Video albums) {
        ((HelperActivity) activity).showMusicLayout();
        ((HelperActivity) activity).songPicked(albums);

    }*/


    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final Videos vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    int resourceId = vo.getVideoId();
                    String resourceType = Constant.ResourceType.VIDEO;
                    if (screenType == Constant.FormType.TYPE_PLAYLIST) {
                        resourceId = vo.getPlaylistId();
                        resourceType = Constant.ResourceType.VIDEO_PLAYLIST;
                    } else if (screenType == Constant.FormType.TYPE_CHANNEL) {
                        resourceId = vo.getChannelId();
                        request.params.put(Constant.KEY_CHANNEL_ID, vo.getChannelId());
                        resourceType = Constant.ResourceType.VIDEO_CHANNEL;
                    } else if (screenType == Constant.FormType.TYPE_ARTISTS) {
                        resourceId = vo.getArtistId();
                        resourceType = Constant.ResourceType.VIDEO_ARTIST;
                    }
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
                                        if (REQ_CODE == REQ_LIKE) {
//                                            videoList.get(position).toggleLike();
                                        } else if (REQ_CODE == REQ_FAVORITE) {
//                                            videoList.get(position).toggleFavorite();
                                        } else {
                                            videoList.get(position).setIsFollow(vo.getIsFollow() == 0 ? 1 : 0);
                                        }
                                        adapter.notifyItemChanged(position);
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


    public void callBottomCommentLikeApi(int resourceId, String resourceType, String url) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
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

                                        CommentLike resp = new Gson().fromJson(response, CommentLike.class);


                                        if (null != resp.getResult() && null != resp.getResult().getStats()) {

                                            updateBottomLayout(resp.getResult().getStats());
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

    public void updateBottomLayout(CommentLike.Stats stats) {
        colorPrimary = Color.parseColor(Constant.colorPrimary);
        text2 = Color.parseColor(Constant.text_color_2);
        //  TextView tvImageFavorite;
        TextView tvFavorite;
        TextView tvComment;
        // TextView tvImageComment;
        TextView tvLike;
        ImageView ivImageLike;

        // tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
        tvFavorite = v.findViewById(R.id.tvFavorite);
        tvComment = v.findViewById(R.id.tvComment);
        //  tvImageComment = v.findViewById(R.id.tvImageComment);
        ivImageLike = v.findViewById(R.id.ivImageLike);
        tvLike = v.findViewById(R.id.tvLike);


        v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
        if (stats.getIsLike()) {
            for (ReactionPlugin vo : stats.getReactionPlugin()) {
                if (vo.getReactionId() == stats.getReactionType()) {
                    tvLike.setText(vo.getTitle());
                    Util.showImageWithGlide(ivImageLike, vo.getImage(), context, R.drawable.like);
                    tvLike.setTextColor(colorPrimary);
                    break;
                }
            }
        } else {
            tvLike.setTextColor(text2);
            tvLike.setText(Constant.TXT_LIKE);
        }

        // tvImageFavorite.setTextColor(colorPrimary);
        tvFavorite.setTextColor(colorPrimary);
        tvFavorite.setText(stats.getFavouriteCount() + " " + Constant.TXT_FAVORITE);
        tvComment.setText(stats.getCommentCount() + " " + Constant.TXT_COMMENT);
        // tvImageComment.setTypeface(iconFont);
        // tvImageFavorite.setTypeface(iconFont);
        // tvImageFavorite.setText("\uf004");
        // tvImageComment.setText("\uf075");
    }

    public String getDetail(AlbumView album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount() + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() + (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount() + (album.getViewCount() != 1 ? " Views" : " View");
        // + "  \uf001 " + album.getSongCount() + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }

    public void callLaterApi(int videoId, final int position) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_WATCH_LATER);
                    request.params.put(Constant.KEY_VIDEO_ID, videoId);
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
                                        videoList.get(position).toggleWatchLaterId();
                                        adapter.notifyItemChanged(position);
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
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLike:
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);
                break;

            case R.id.llFavorite:
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_FAVORITE);
                break;

            case R.id.llComment:
                goToCommentFragment(resourceId, resourceType);
                break;
        }
    }

    public static final int REQ_CODE_OPTION_UNSAVE = 205;
    public static final int REQ_CODE_OPTION_SAVE = 206;
    private void performFeedOptionClick(int actionId, int actPosition,boolean save,int shortcutid) {
        if(save){
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_REMOVESHOIRTCUT, actionId,  actPosition,save,shortcutid);
        }else {
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, actionId,  actPosition,save,shortcutid);
        }

    }

    boolean isLoading=false;
    private void callFeedEventApi(final int reqCode, String url, int actionId,  final int actPosition,boolean issave,int shortcutid) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put("resource_id", actionId);
                    request.params.put("resource_type", "video");
                    if(issave){
                        request.params.put("shortcut_id", shortcutid);
                    }
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
                                PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_SAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition,  "save", Constant.TXT_UNSAVE_FEED,0);
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition,  "unsave", Constant.TXT_SAVE_FEED,resp.getResult().getShortcut_id23());
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
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateOptionText(int actPosition2,  String name, String value,int shortcutid) {

        if(name.equalsIgnoreCase("save")){
            videoList.get(actPosition2).getShortcut_save().setIs_saved(false);
        }else {
            videoList.get(actPosition2).getShortcut_save().setIs_saved(true);
            videoList.get(actPosition2).getShortcut_save().setShortcut_id(shortcutid);
        }

        adapter.notifyItemChanged(actPosition2);
    }



}
