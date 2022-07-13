package com.sesolutions.ui.common;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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

public class CommentLikeHelper_basic extends BaseFragment implements OnUserClickedListener<Integer, Object> {
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

    //private TextView tvImageFavorite;
    private TextView tvFavorite;
    public TextView tvComment;
    // private TextView tvImageComment;
    public TextView tvLike;
    private ImageView ivImageLike;
    public boolean isGallery = false;

    public void callBottomCommentLikeApi(int resourceId, final String resourceType, String url) {
        if (!SPref.getInstance().isLoggedIn(context))
            return; //don't call this api for guest user

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

                            CustomLog.e("response_bottom_LikeComment", "" + response);
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


    public void handleResponse123(String response) {
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



    public void updateItemLikeFavorite1() {
        ((TextView) v.findViewById(R.id.tvLike)).setText(stats.getIsLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
        ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(stats.getIsLike()  ? Constant.colorPrimary : Constant.text_color_1));
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
            tvComment = v.findViewById(R.id.tvComment);
            //  tvImageComment = v.findViewById(R.id.tvImageComment);
            ivImageLike = v.findViewById(R.id.ivImageLike);
            tvLike = v.findViewById(R.id.tvLike);
//            v.findViewById(R.id.llLike).setOnLongClickListener(v -> {
//                createPopUp(v, -1, CommentLikeHelper.this);
//                return false;
//            });

            v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
            if (stats.getIsLike()) {
                tvLike.setTextColor(cPrimary);
                ivImageLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_active_quote));

//                for (ReactionPlugin vo : stats.getReactionPlugin()) {
//                    if (vo.getReactionId() == stats.getReactionType()) {
//                        tvLike.setText(vo.getTitle());
//                        Util.showImageWithGlide(ivImageLike, vo.getImage(), context, isGallery ? R.drawable.like_white : R.drawable.like);
//                        tvLike.setTextColor(cPrimary);
//                        break;
//                    }
//                }
            } else {
                tvLike.setTextColor(cText1);
                tvLike.setText(R.string.TXT_LIKE);
            }

            //  tvImageFavorite.setTextColor(stats.getIsFavourite() ? cPrimary : cText2 != 0 ? cText2 : cText1);
            tvFavorite.setTextColor(stats.getIsFavourite() ? cRed : cText1);
            tvFavorite.setText(stats.getFavouriteCount() + " " + (stats.getFavouriteCount() == 1 ? Constant.TXT_FAVORITE : Constant.TXT_FAVORITES));
            tvComment.setText(/*stats.getCommentCount() + " " +*/ (stats.getCommentCount() == 1 ? Constant.TXT_COMMENT : Constant.TXT_COMMENTS));
            // tvImageComment.setTypeface(iconFont);
            // tvImageFavorite.setTypeface(iconFont);
            //  tvImageFavorite.setText("\uf004");
            // tvImageComment.setText("\uf075");

            if (null != resourceType && ModuleUtil.getInstance().isCorePlugin(context, resourceType)) {
                showHideFavorite(false);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateLike(int reactionId) {
        try {
            stats.setReactionType(reactionId);
            if (tvLike == null) {
                ivImageLike = v.findViewById(R.id.ivImageLike);
                tvLike = v.findViewById(R.id.tvLike);
                cText1 = Color.parseColor(Constant.text_color_light);
                cPrimary = Color.parseColor(Constant.colorPrimary);
            }
            if (reactionId > 0) {
                stats.setIsLike(true);
                tvLike.setTextColor(cPrimary);
                ivImageLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_active_quote));

                try {
                    Log.e("Likecount",""+stats.getLikeCount());
                    int likecount=stats.getLikeCount()+1;
                    stats.setLikeCount(likecount);
                    if(stats.getLikeCount()>0){
                        tvLike.setText(likecount + " " + (likecount == 1 ? getString(R.string.TXT_LIKE) : getString(R.string.TXT_LIKES) ));
                    }else {
                        tvLike.setText(getString(R.string.TXT_LIKE));
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
//                for (ReactionPlugin vo : stats.getReactionPlugin()) {
//                    if (vo.getReactionId() == stats.getReactionType()) {
//                        tvLike.setText(vo.getTitle());
//                        Util.showImageWithGlide(ivImageLike, vo.getImage(), context);
//                        tvLike.setTextColor(cPrimary);
//                        break;
//                    }
//                }
            } else {

                Log.e("Likecount22",""+stats.getLikeCount());

                stats.setIsLike(false);
                tvLike.setTextColor(cText1);
                tvLike.setText(R.string.TXT_LIKE);
                ivImageLike.setImageDrawable(ContextCompat.getDrawable(context, isGallery ? R.drawable.like_white : R.drawable.like));
                try {

                    int likecount=stats.getLikeCount()-1;
                    stats.setLikeCount(likecount);
                    if(stats.getLikeCount()>0){
                        tvLike.setText(likecount +" " + (likecount == 1 ? getString(R.string.TXT_LIKE) : getString(R.string.TXT_LIKES) ));
                    }else {
                        tvLike.setText(getString(R.string.TXT_LIKE));
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }


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
                stats.increamentFavourite();
                    /*}
                });*/


            } else {
                stats.setIsFavourite(false);
                // tvImageFavorite.setTextColor(cText2 != 0 ? cText2 : cText1);
                tvFavorite.setTextColor(cText1);
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
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
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
                case R.id.llLike:
                    reactionType = stats.getIsLike() ? 0 : 1;
                    updateLike(reactionType);
                    callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);
                    break;

                case R.id.llFavorite:
                    updateFavorite();
                    callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_FAVORITE);
                    break;

                case R.id.llComment:
                    goToCommentFragment(resourceId, resourceType);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //used in videoview helper
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
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showHideComment(boolean canComment) {
        try {
            v.findViewById(R.id.llComment).setVisibility(canComment ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
