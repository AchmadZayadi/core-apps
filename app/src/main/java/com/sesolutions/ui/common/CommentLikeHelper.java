package com.sesolutions.ui.common;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.ui.video.VideoViewActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 30/12/17.
 */

public class CommentLikeHelper extends BaseFragment implements OnUserClickedListener<Integer, Object> {

    private static final String TAG = "CommentLikeHelper";

    public CommentLike.Stats stats;
    public View v;
    public int cPrimary;
    public int cRed;
    public int cGrey;
    public int cText1;
    public int cText2;
    public int reactionType = -1;
    public Typeface iconFont;
    public String resourceType;
    public int resourceId;

    // private TextView tvImageFavorite;
    private TextView tvFavorite;
    private TextView tvComment,tvAlbumDetails;
    // private TextView tvImageComment;
    private TextView tvLike;
    private ImageView ivImageLike,like_song,llFavorite_song,llComment_song,ivImageFavorite,tvImageFavorite;
    public boolean isGallery = false;

    public void callBottomCommentLikeApi(int resourceId, final String resourceType, String url) {
        if (!SPref.getInstance().isLoggedIn(context)) {
            // Util.showSnackbar(v, "Please Login");
            return; //don't call this api for guest user
        }

        this.resourceId = resourceId;
        this.resourceType = resourceType;
        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    if (reactionType > -1) {
                        request.params.put(Constant.KEY_REACTION_TYPES, reactionType);
                        reactionType = -1;
                    }

                    request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            Log.e(TAG, "callBottomCommentLikeApi: " + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    handleResponse(response);
                                } else {
                                    onPermissionError();
                                    /*Dont show error msg*/
                                    //Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
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

    public void handleResponse(String response) {
        try {
            if (!(new JSONObject(response).get("result") instanceof String)) {
                CommentLike resp = new Gson().fromJson(response, CommentLike.class);
                if (null != resp.getResult() && null != resp.getResult().getStats()) {
                    stats = resp.getResult().getStats();
                    updateBottomLayout();
                }
            }
        } catch (JSONException e) {
            CustomLog.e(e);
        }
    }

    public void onPermissionError() {
        //override this method on video view fragment
    }

    public void updateBottomLayout() {

        try {
            cPrimary = Color.parseColor(Constant.colorPrimary);
            cText1 = Color.parseColor(Constant.text_color_1);
            cText2 = Color.parseColor(Constant.text_color_2);
            cRed = Color.parseColor(Constant.red);
            // tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
            tvFavorite = v.findViewById(R.id.tvFavorite);
            ivImageFavorite = v.findViewById(R.id.ivImageFavorite);
            tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
            tvComment = v.findViewById(R.id.tvComment);
            //  tvImageComment = v.findViewById(R.id.tvImageComment);
            ivImageLike = v.findViewById(R.id.ivImageLike);
            try {
                like_song = v.findViewById(R.id.like_song);
                llFavorite_song = v.findViewById(R.id.llFavorite_song);
                 llComment_song = v.findViewById(R.id.llComment_song);
                tvAlbumDetails = v.findViewById(R.id.tvAlbumDetails);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            tvLike = v.findViewById(R.id.tvLike);
            v.findViewById(R.id.llLike).setOnLongClickListener(v -> {
                createPopUp(v, -1, CommentLikeHelper.this);
                return false;
            });


            v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
            if (stats.getIsLike()) {
                for (ReactionPlugin vo : stats.getReactionPlugin()) {
                    if (vo.getReactionId() == stats.getReactionType()) {
                        tvLike.setText(vo.getTitle());
                        Util.showImageWithGlide(ivImageLike, vo.getImage(), context, isGallery ? R.drawable.like_white : R.drawable.like);
                        try {
                            Util.showImageWithGlide(like_song, vo.getImage(), context, isGallery ? R.drawable.like_white : R.drawable.like);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        tvLike.setTextColor(cPrimary);
                        break;
                    }
                }
                try {
                    int menuTitleActiveColor = Color.parseColor("#169CD8");
                 //   ivImageLike.setColorFilter(menuTitleActiveColor);
                    tvLike.setTextColor(cPrimary);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            else {
                tvLike.setTextColor(cText1);
                tvLike.setText(R.string.TXT_LIKE);
            }

            //  tvImageFavorite.setTextColor(stats.getIsFavourite() ? cPrimary : cText2 != 0 ? cText2 : cText1);
            tvFavorite.setTextColor(stats.getIsFavourite() ? cRed : cText1);

            try {
                 ivImageFavorite.setImageResource(stats.getIsFavourite()? R.drawable.music_favourite_selected:R.drawable.favorite);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                tvImageFavorite.setImageResource(stats.getIsFavourite()? R.drawable.red_heart:R.drawable.favorite);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                llFavorite_song.setImageResource(stats.getIsFavourite()? R.drawable.music_favourite_selected:R.drawable.favorite);
             }catch (Exception ex){
               ex.printStackTrace();
            }

            tvFavorite.setText(stats.getFavouriteCount() + " " + (stats.getFavouriteCount() == 1 ? Constant.TXT_FAVORITE : Constant.TXT_FAVORITES));
            tvComment.setText(stats.getCommentCount() + " " + (stats.getCommentCount() == 1 ? Constant.TXT_COMMENT : Constant.TXT_COMMENTS));

           //  "+stats.getViewCount()+" Views  "+stats.getCommentCount()+" Comments");


            // tvImageComment.setTypeface(iconFont);
            // tvImageFavorite.setTypeface(iconFont);
            //  tvImageFavorite.setText("\uf004");
            // tvImageComment.setText("\uf075");

            if (activity instanceof VideoViewActivity) {
                updateWatchLater();

            }

            if (null != resourceType && ModuleUtil.getInstance().isCorePlugin(context, resourceType)) {
                showHideFavorite(false);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateWatchLater() {
        //override this method on video view fragments
    }

    public void updateLike(int reactionId) {
        try {
            stats.setReactionType(reactionId);
            if (tvLike == null) {
                ivImageLike = v.findViewById(R.id.ivImageLike);
                try {
                    like_song = v.findViewById(R.id.like_song);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                tvLike = v.findViewById(R.id.tvLike);
                cText1 = Color.parseColor(Constant.text_color_light);
                cPrimary = Color.parseColor(Constant.colorPrimary);
            }
            if (reactionId > 0) {
                stats.setIsLike(true);

                tvLike.setTextColor(cPrimary);
                ivImageLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_active_quote));


                for (ReactionPlugin vo : stats.getReactionPlugin()) {
                    if (vo.getReactionId() == stats.getReactionType()) {
                        tvLike.setText(vo.getTitle());
                        Util.showImageWithGlide(ivImageLike, vo.getImage(), context);
                        try {
                            Util.showImageWithGlide(like_song, vo.getImage(), context);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        tvLike.setTextColor(cPrimary);
                        break;
                    }
                }

            } else {
                stats.setIsLike(false);
                tvLike.setTextColor(cText1);
                tvLike.setText(R.string.TXT_LIKE);
                try {
                    like_song.setImageDrawable(ContextCompat.getDrawable(context, isGallery ? R.drawable.like_white : R.drawable.like));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                ivImageLike.setImageDrawable(ContextCompat.getDrawable(context, isGallery ? R.drawable.like_white : R.drawable.like));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }




    }

    public void updateFavorite() {
        //  TextView tvImageFavorite = v.findViewById(R.id.tvImageFavorite);
        //   SmallBangView smv = v.findViewById(R.id.smvFav);
        try {

            if (!stats.getIsFavourite()) {

              /*  smv.likeAnimation(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);*/
                stats.setIsFavourite(true);
                //  tvImageFavorite.setTextColor(cPrimary);
                tvFavorite.setTextColor(Color.parseColor(Constant.red));

                try {
                     ivImageFavorite.setImageResource(R.drawable.music_favourite_selected);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                try {
                    tvImageFavorite.setImageResource(R.drawable.red_heart);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                try {
                    llFavorite_song.setImageResource(R.drawable.music_favourite_selected);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                stats.increamentFavourite();
                    /*}
                });*/


            } else {
                stats.setIsFavourite(false);
                // tvImageFavorite.setTextColor(cText2 != 0 ? cText2 : cText1);
                tvFavorite.setTextColor(cText1);

                try {
                    ivImageFavorite.setImageResource(R.drawable.favorite);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
               try {
                   tvImageFavorite.setImageResource(R.drawable.favorite);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                try {
                    llFavorite_song.setImageResource(R.drawable.favorite);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                // tvFavorite.setText(stats.decreamentFavourite() + " " + Constant.TXT_FAVORITE);
                stats.decreamentFavourite();
            }
            tvFavorite.setText(stats.getFavouriteCount() + " " + (stats.getFavouriteCount() == 1 ? Constant.TXT_FAVORITE : Constant.TXT_FAVORITES));

            // tvImageFavorite.setTypeface(iconFont);
            // tvImageFavorite.setText("\uf004");
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int position) {
        try {
            if (object1 == Constant.Events.VIEW_LIKED && null != stats) {
                reactionType = SPref.getInstance().getReactionPlugins(context).get(Integer.parseInt("" + screenType)).getReactionId();
                updateLike(reactionType);
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.like_song:
                case R.id.llLike:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        reactionType = stats.getIsLike() ? 0 : 1;

                        try {
                            if(stats.getIsLike()){
                                stats.setLikeCount((stats.getLikeCount()-1));
                                tvAlbumDetails.setText((stats.getLikeCount())+" Likes");
                            }else {
                                stats.setLikeCount((stats.getLikeCount()+1));
                                tvAlbumDetails.setText((stats.getLikeCount())+" Likes");
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        updateLike(reactionType);


                        callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);
                    } else {
                        Util.showSnackbar(v, "Please login to like");
                        return; //don't call this api for guest user
                    }
                    break;

                case R.id.llFavorite_song:
                case R.id.llFavorite:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        updateFavorite();
                        callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_FAVORITE);
                    } else {
                        Util.showSnackbar(v, "Please login to favorite");
                        return; //don't call this api for guest user
                    }
                    break;

                case R.id.llComment_song:
                case R.id.llComment:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        goToCommentFragment(resourceId, resourceType);
                    } else {
                        Util.showSnackbar(v, "Please login to comment");
                        return; //don't call this api for guest user
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    // used in videoView helper
    public void callLaterApi(int videoId, String url, final boolean position) {

        if (isNetworkAvailable(context)) {
            try {
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_VIDEO_ID, videoId);
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

                                // ((ImageView) v.findViewById(R.id.tvImageLater)).setColorFilter(position ? cGrey : cPrimary);

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
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }

    public void showHideFavorite(boolean canFavorite) {
        try {
            v.findViewById(R.id.llFavorite).setVisibility(canFavorite ? View.VISIBLE : View.GONE);

            try {
              //  v.findViewById(R.id.llFavorite_song).setVisibility(canFavorite ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showHideComment(boolean canComment) {
        try {
            v.findViewById(R.id.llComment).setVisibility(canComment ? View.VISIBLE : View.GONE);
            try {
                v.findViewById(R.id.llComment_song).setVisibility(canComment ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
