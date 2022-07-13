package com.sesolutions.ui.poll;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
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

public abstract class PollHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    public String URL_POLL_LIKE, URL_POLL_FAVORITE;
    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public int categoryId;
    public List<Poll> videoList;
    public T adapter;
    public List<Category> categoryList;
    public static final int REQ_CODE_OPTION_UNSAVE = 205;
    public static final int REQ_CODE_OPTION_SAVE = 206;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {

                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(videoList.get(postion).getUserId());
                    break;
                case Constant.Events.MUSIC_LIKE:
                    callLikeApi(REQ_LIKE, postion, URL_POLL_LIKE, videoList.get(postion));
                    break;
                case Constant.Events.MUSIC_FAVORITE:
                    callLikeApi(REQ_FAVORITE, postion, URL_POLL_FAVORITE, videoList.get(postion));
                    break;
                case Constant.Events.MUSIC_MAIN:
                    try {
                        openViewPollFragment(videoList.get(postion).getPollId(),videoList.get(postion).getShare2().getUrl());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        openViewPollFragment(videoList.get(postion).getPollId(),"");
                    }

                    break;
                case Constant.Events.CLICKED_BODY_TAGGED:
                  int   id = Integer.parseInt("" + screenType);
                    goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, id);
                    break;

                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        openViewWishCategoryFragment(postion, "" + screenType, false);
                    break;
                case Constant.Events.SHARE_FEED:
                    if (Integer.parseInt("" + screenType) == 1)
                        sharingToSocialMedia2(videoList.get(postion).getShare2(), "com.facebook.katana");
                    else if (Integer.parseInt("" + screenType) == 2)
                        sharingToSocialMedia2(videoList.get(postion).getShare2(), "com.whatsapp");
                    else
                        showShareDialog(videoList.get(postion).getShare(""));
                    break;
                case Constant.Events.FEED_UPDATE_OPTION2:
                     try {
                        boolean isboolen = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().isIs_saved();
                        int actionId = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getResource_id();
                        int shortcutid = 0;
                        if(isboolen){
                            shortcutid = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getShortcut_id();
                        }
                        performFeedOptionClick(actionId,  Integer.parseInt("" + screenType), isboolen,shortcutid,"sespagepoll_poll");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Constant.Events.FEED_UPDATE_OPTION3:
                          try {
                                boolean isboolen = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().isIs_saved();
                                int actionId = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getResource_id();
                                int shortcutid = 0;
                                if(isboolen){
                                    shortcutid = videoList.get(Integer.parseInt("" + screenType)).getShortcut_save().getShortcut_id();
                                }
                                performFeedOptionClick(actionId,  Integer.parseInt("" + screenType), isboolen,shortcutid,"sesgrouppoll_poll");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                    break;
               /* case Constant.Events.CLICKED_OPTION:
                    setFeedUpdateRecycleView(postion);
                    slideUpDown();
                    break;*/
               /* case Constant.Events.FEED_UPDATE_OPTION:
                    String name = videoList.get(Integer.parseInt("" + screenType)).getOptions().get(postion).getName();
                    performFeedOptionClick(name, Integer.parseInt("" + screenType));
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public abstract void openViewPollFragment(int pollId,String sharemsg);

    private void performFeedOptionClick(String name, int actPosition) {
        switch (name) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(actPosition);
                break;
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                // map.put(Constant.KEY_BLOG_ID, blogId);
                // map.put(Constant.KEY_GET_FORM, 1);
                String url = Constant.URL_EDIT_POLL + videoList.get(actPosition).getPollId() + Constant.POST_URL;
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditPollFragment.newInstance(Constant.FormType.EDIT_POLL, map, url)).addToBackStack(null).commit();
                break;
        }
    }

    private void performFeedOptionClick(int actionId, int actPosition,boolean save,int shortcutid,String rctype) {
        if(save){
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_REMOVESHOIRTCUT, actionId,  actPosition,save,shortcutid,rctype);
        }else {
            showBaseLoader(false);
            callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, actionId,  actPosition,save,shortcutid,rctype);
        }


    }

    public void showDeleteDialog(final int position) {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_POLL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(position);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    boolean isLoading=false;
    private void callFeedEventApi(final int reqCode, String url, int actionId,  final int actPosition,boolean issave,int shortcutid,String rctype) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put("resource_id", actionId);
                    request.params.put("resource_type", rctype);


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

    private void updateOptionText(int actPosition,  String name, String value,int shortcutid) {

        if(name.equalsIgnoreCase("save")){
            videoList.get(actPosition).getShortcut_save().setIs_saved(false);
        }else {
            videoList.get(actPosition).getShortcut_save().setIs_saved(true);
            videoList.get(actPosition).getShortcut_save().setShortcut_id(shortcutid);
        }

        adapter.notifyItemChanged(actPosition);
    }



    private void callLikeApi(final int REQ_CODE, final int position, String url, final Poll vo) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ID, vo.getPollId());
                    request.params.put(Constant.KEY_POLL_ID, vo.getPollId());
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
                            hideBaseLoader();
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

    public void updateItemLikeFavorite(int REQ_CODE, int position, Poll vo) {
        if (REQ_CODE == REQ_LIKE) {
            vo.setShowAnimation(1);
            videoList.get(position).setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setShowAnimation(2);
            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
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


    private void callDeleteApi(final int position) {

        try {
            if (isNetworkAvailable(context)) {

                videoList.remove(position);
                adapter.notifyItemRemoved(position);


                try {

                    // showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_POLL + videoList.get(position).getPollId() + Constant.POST_URL);
                    //   request.params.put(Constant.KEY_POLL_ID, videoList.get(position).getPollId());
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
                                       /* if (REQ == VIEW_BLOG_DELETE) {
                                            onBackPressed();
                                        } else {
                                            videoList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                        }*/
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
