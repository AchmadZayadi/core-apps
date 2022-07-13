package com.sesolutions.ui.qna;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.qna.QAResponse;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.responses.qna.QuestionVo;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.sesolutions.utils.VibratorUtils;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 29/11/17.
 */

public class QAHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_DELETE = 400;
    public OnUserClickedListener<Integer, Object> parent;
    public int categoryId;
    public List<QuestionVo> videoList;
    public T adapter;
    public List<Category> categoryList;
    public QAResponse.Result result;


    public void handleVoteResponse(int position, String response) {

        if (null != response) {
            SuccessResponse resp = new Gson().fromJson(response, SuccessResponse.class);
            if (resp.isSuccess()) {
                // ((Question) videoList.get(position).getContent()).updateVoteCount(resp.getResult().getUpvoteCount(), resp.getResult().getDownvoteCount());
                // adapter.notifyItemChanged(position);
            } else {
                Util.showSnackbar(v, resp.getErrorMessage());
            }
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    Question vo = videoList.get(postion).getContent();
                    goToProfileFragment(vo.getOwnerId());
                    break;
                case Constant.Events.VOTE:
                    if (!((Question) videoList.get(postion).getContent()).hasVoted("" + screenType))
                        if (isNetworkAvailable(context)) {

                            Map<String, Object> map = ((Question) videoList.get(postion).getContent()).getGuidMap("" + screenType, new HashMap<>());
                            map.put("userguid", Constant.ResourceType.USER + "_" + SPref.getInstance().getLoggedInUserId(context));
                            map.put(Constant.KEY_TYPE, screenType);
                            adapter.notifyItemChanged(postion);
                            new ApiController(Constant.URL_QA_VOTE_UP_DOWN, map, context, this, -2).setExtraKey(postion).execute();
                        } else {
                            notInternetMsg(v);
                        }
                    break;
                case -2:
                    handleVoteResponse(postion, (String) screenType);
                    break;
               /* case Constant.Events.CLICKED_HEADER_LOCATION:
                    JsonElement la = videoList.get(postion).getLocationObject();
                    if (null != la && la.isJsonObject()) {
                        Locations vo = new Gson().fromJson(la, Locations.class);
                        if (vo.canShowMap()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + vo.getLat() + "," + vo.getLng()));
                            startActivity(intent);
                        }
                    }
                    break;*/
                case Constant.Events.MUSIC_LIKE:
                    callLikeApi(REQ_LIKE, postion, Constant.URL_QA_LIKE, -1);
                    break;
                case Constant.Events.MUSIC_FAVORITE:
                    callLikeApi(REQ_FAVORITE, postion, Constant.URL_QA_FAVORITE, -1);
                    break;
                case Constant.Events.MUSIC_ADD:
                    callLikeApi(REQ_FOLLOW, postion, Constant.URL_QA_FOLLOW, -1);
                    break;
                case Constant.Events.MUSIC_MAIN:
                    vo = videoList.get(postion).getContent();
                    openViewQuestionFragment(vo.getQuestionId(), vo);
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        openViewQACategoryFragment(postion, "" + screenType);
                    else VibratorUtils.vibrate(context);
                    break;

                case Constant.Events.LOAD_MORE:
                    onLoadMore();
                    break;

                case Constant.Events.FEED_UPDATE_OPTION:
                    vo = videoList.get(Integer.parseInt("" + screenType)).getContent();
                    //get clicked option
                    Options opt = vo.getOptions().get(postion);

                    //open share dialog if share clicked
                    switch (opt.getName()) {
                        case Constant.OptionType.SHARE:
                            showShareDialog(vo.getShare());
                            break;
                        case Constant.OptionType.DELETE:
                            showDeleteDialog(Integer.parseInt("" + screenType));
                            break;
                        case Constant.OptionType.EDIT:
                            Map<String, Object> map = new HashMap<>();
                            map.put(Constant.KEY_QUESTION_ID, vo.getQuestionId());
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container,
                                            CreateEditQAFragment.newInstance(Constant.FormType.EDIT_QA, map, Constant.URL_QA_EDIT, null))
                                    .addToBackStack(null)
                                    .commit();
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public void showDeleteDialog(final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getString(R.string.MSG_DELETE_CONFIRMATION_GENERIC, getString(R.string.question)));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callLikeApi(REQ_DELETE, position, Constant.URL_QA_DELETE, -1);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void onLoadMore() {
        //override this method on child classes
    }

    @Override
    public void onClick(View v) {
    }

    private void callLikeApi(final int REQ_CODE, final int position, String url, final int optionPosition) {


        if (isNetworkAvailable(context)) {
            final Question vo = videoList.get(position).getContent();
            if (REQ_CODE >= REQ_DELETE) {/* >= means join,leave,request and delete*/
                showBaseLoader(false);
            } else {
                //update icon and show animation
                updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition);
            }
            try {
                HttpRequestVO request = new HttpRequestVO(url);
                // request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.QA);
                // request.params.put(Constant.KEY_RESOURCE_ID, vo.getQuestionId());
                request.params.put(Constant.KEY_ID, vo.getQuestionId());
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
                                    if (REQ_CODE == REQ_DELETE) {
                                        videoList.remove(position);
                                        try {
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position, videoList.size());
                                        } catch (Exception e) {
                                            /*update all items in case of any animation*/
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                        /*if (REQ_CODE == REQ_LIKE) {
                                            videoList.get(position).setContentLike(!vo.isContentLike());
                                        } else if (REQ_CODE == REQ_FAVORITE) {
                                            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                        }
                                        adapter.notifyItemChanged(position);*/
                                } else {
                                    //revert changes in case of error
                                    updateItemLikeFavorite(REQ_CODE, position, vo, false);
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
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
    }

    public void updateItemLikeFavorite(int REQ_CODE, int position, Question vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            ((Question) videoList.get(position).getContent()).setShowAnimation(showAnimation ? 1 : 0);
            ((Question) videoList.get(position).getContent()).setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            ((Question) videoList.get(position).getContent()).setShowAnimation(showAnimation ? 2 : 0);
            ((Question) videoList.get(position).getContent()).setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FOLLOW) {
            ((Question) videoList.get(position).getContent()).setShowAnimation(showAnimation ? 3 : 0);
            ((Question) videoList.get(position).getContent()).setContentFollow(!vo.isContentFollow());
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
                                            onBackPressed();
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
