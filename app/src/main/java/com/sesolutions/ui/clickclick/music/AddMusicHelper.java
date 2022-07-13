package com.sesolutions.ui.clickclick.music;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

/**
 * 6
 * Created by root on 29/11/17.
 */

public class AddMusicHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {


    public int categoryId;
    public List<com.sesolutions.responses.music.Albums> albumsList;
    public T adapter;
    private ResultView result;
    public List<Category> categoryList;
    private static final int ADD_MUSIC = 7999;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                    break;
                case Constant.Events.CLICKED_HEADER_LOCATION:
                    break;



                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    openViewPageFragment(postion);
                    break;

                case Constant.Events.CATEGORY:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    if (categoryId != postion) //do not open same category again
                        openViewPageCategoryFragment(postion, "" + screenType);
                    break;


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    void playMusic(com.sesolutions.responses.music.Albums albums) {
        ((AddMusicActivity) activity).songPicked(albums);

    }

    void pauseMusic() {
        ((AddMusicActivity) activity).pause();
    }


    @Override
    public void onClick(View v) {
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


    /*private void callDeleteApi(final int position) {

        try {
            if (isNetworkAvailable(context)) {
                videoList.remove(position);
                adapter.notifyItemRemoved(position);


                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_EVENT + videoList.get(position).getEventId() + Constant.POST_URL);
                    request.params.put(Constant.KEY_EVENT_ID, videoList.get(position).getEventId());
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
                                        Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                       *//* if (REQ == VIEW_BLOG_DELETE) {
                                            baseOnBackPressed();
                                        } else {
                                            videoList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                        }*//*
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
    }*/


}
