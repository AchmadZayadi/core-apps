package com.sesolutions.ui.poll_core;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.SpeakableContent;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
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

public abstract class CPollsHelper extends SpeakableContent implements View.OnClickListener {

    public CpollAdapter adapter;
    //    public CMyPollsAdapter adapter2;
    public List<Poll> videoList;
    public List<Options> menuItem;
    public RelativeLayout hiddenPanel;
    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    public static final int VIEW_POLL_DELETE = 400;
    public CPollParentFragment parent;

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
                    showOptionsPopUp((View) screenType, postion, menuItem);
                }
                break;

//            case Constant.Events.FEED_UPDATE_OPTION:
//                // slideUpDown();
//                Options vo = menuItem.get(postion);
//                performFeedOptionClick(videoList.get(Integer.valueOf("" + screenType)).getPollId(), vo, Integer.parseInt("" + screenType), postion);
//                break;
            case Constant.Events.MUSIC_FAVORITE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_FAVORITE, postion, Constant.URL_MUSIC_FAVORITE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_LIKE:
                callLikeApi(Integer.valueOf("" + screenType), REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                break;
            case Constant.Events.MUSIC_MAIN:
                openCViewPollFragment(videoList.get(postion).getPollId());
                break;

            case Constant.Events.LIKED:
                reactionType = stats.getReactionPlugin().get(Integer.parseInt("" + screenType)).getReactionId();
                updateLike(reactionType);
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);

        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public abstract void openCViewPollFragment(int pollId);

    private void performFeedOptionClick(int pollId, Options vo, int actPosition, int position) {
        switch (vo.getName()) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(1, pollId, actPosition);
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.EDIT:
                goToFormFragment(pollId);
                // callSaveFeedApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
        }
    }

    public void showDeleteDialog(final int REQ, final int pollId, final int position) {
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
                callDeleteApi(REQ, pollId, position);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, final int pollId, final int position) {

        try {
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_POLL_DELETE);
                    request.params.put(Constant.KEY_POLL_ID, pollId);
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
                                        if (REQ == VIEW_POLL_DELETE) {
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

    public void goToFormFragment(int pollId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_POLL_ID, pollId);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_CORE_POLL, map, Constant.URL_POLL_EDIT)).addToBackStack(null).commit();
    }

    private void updateItemLikeFavorite(int REQ_CODE, int position, Poll vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).toggleLike();
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).toggleFavorite();
            adapter.notifyItemChanged(position);
        }
    }

    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final Poll vo) {

        try {

            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);

                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    int resourceId = vo.getPollId();
                    String resourceType = vo.getResourceType();

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


}
