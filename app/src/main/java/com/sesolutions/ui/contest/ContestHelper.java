package com.sesolutions.ui.contest;

import android.app.ProgressDialog;
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
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.contest.join.ContestJoinFragment;
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

public class ContestHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    public static final String TYPE_HOME = "sescontest_main_home";
    public static final String TYPE_ENTRIES = "sescontest_main_entries-browse";
    public static final String TYPE_WINNERS = "sescontest_main_winner-browse";
    public static final String TYPE_CATEGORY = "sescontest_main_categories";
    public static final String TYPE_PHOTO = "sescontest_main_photocontest";
    public static final String TYPE_TEXT = "sescontest_main_textcontest";
    public static final String TYPE_VIDEO = "sescontest_main_videocontest";
    public static final String TYPE_AUDIO = "sescontest_main_audiocontest";
    public static final String TYPE_MANAGE = "sescontest_main_manage";
    public static final String TYPE_BROWSE = "sescontest_main_browse";

    public static final String TYPE_MY_PACKAGE = "sescontest_main_manage_package";
    public static final String TYPE_ENDED = "sescontest_main_endedcontest";
    public static final String TYPE_COMING_SOON = "sescontest_main_comingsooncontest";
    public static final String TYPE_ACTIVE = "sescontest_main_activecontest";
    public static final String TYPE_CREATE = "sescontest_main_create";
    public static final String TYPE_MY_ORDERS = "sescontestjoinfees_main_myorders";
    public static final String TYPE_PINBOARD = "sescontest_main_pinboard";

    public static final String TYPE_VIEW_CATEGORY = "view_category";
    public static final String TYPE_CONTEST_ENTRIES = "entries";

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_VOTE = 400;
    public OnUserClickedListener<Integer, Object> listener;
    public int categoryId;
    public List<Contest> contestList;
    public T adapter;
    public List<Category> categoryList;
    public ContestResponse.Result result;
    public String selectedScreen;
    public String mFilter;


    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(contestList.get(postion).getItem().getOwnerId());
                    break;
                case Constant.Events.MUSIC_LIKE:
                    callLikeApi(REQ_LIKE, postion, Constant.URL_CONTEST_LIKE);
                    break;
                case Constant.Events.FEED_UPDATE_OPTION2:
                    //   Log.e("888889","9999999"+videoList.get(Integer.parseInt("" + screenType)).getItem().getCategory_title());
                    try {
                        boolean isboolen = contestList.get(Integer.parseInt("" + screenType)).getItem().getShortcut_save().isIs_saved();
                        int actionId = contestList.get(Integer.parseInt("" + screenType)).getItem().getShortcut_save().getResource_id();
                        int shortcutid = 0;
                        if(isboolen){
                            shortcutid = contestList.get(Integer.parseInt("" + screenType)).getItem().getShortcut_save().getShortcut_id();
                        }
                        performFeedOptionClick(actionId,  Integer.parseInt("" + screenType), isboolen,shortcutid);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

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

                case Constant.Events.MUSIC_FAVORITE:
                    callLikeApi(REQ_FAVORITE, postion, Constant.URL_CONTEST_FAVOURITE);
                    break;
                case Constant.Events.MUSIC_ADD:
                    callLikeApi(REQ_FOLLOW, postion, Constant.URL_CONTEST_FOLLOW);
                    break;
                case Constant.Events.ENTRY:
                    goToViewEntryFragment(contestList.get(postion).getItem().getParticipantId());
                    break;
                case Constant.Events.JOIN:
                    Map<String, Object> map = new HashMap<>();
                   /* map.put(Constant.KEY_CONTEST_ID, contestList.get(postion).getItem().getContestId());
                    openFormFragment(Constant.FormType.JOIN, map, Constant.URL_CONTEST_JOIN);*/
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ContestJoinFragment.newInstance(contestList.get(postion).getItem()))
                            .addToBackStack(null)
                            .commit();
                    break;

                //calling Entry vote api
                case Constant.Events.VOTE:
                    //do not vote if already voted
                    if (!contestList.get(postion).getItem().isContentVoted() && isNetworkAvailable(context)) {
                        /*Map<String, Object>*/
                        map = new HashMap<>();
                        map.put(Constant.KEY_CONTEST_ID, contestList.get(postion).getItem().getContestId());
                        map.put(Constant.KEY_ID, contestList.get(postion).getItem().getParticipantId());

                        //toggle vote button text
                        contestList.get(postion).getItem().toggleVote();
                        adapter.notifyItemChanged(postion);
                        //calling api
                        new ApiController(Constant.URL_ENTRY_VOTE, map, context, this, REQ_VOTE).execute();
                    }

                    break;

                //RESPONSE of  Entry vote api
                case REQ_VOTE:
                    try {
                        String response = (String) screenType;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (err.isSuccess()) {
                                SuccessResponse succ = new Gson().fromJson(response, SuccessResponse.class);
                                Util.showSnackbar(v, succ.getResult().getSuccessMessage());
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                                //refresh list in case of any error
                                onRefresh();
                            }
                        }
                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                    }
                    break;
                case Constant.Events.MUSIC_MAIN:
                    goToViewContestFragment(contestList.get(postion).getItem().getContestId());
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        openViewCategoryFragment(ViewContestCategoryFragment.newInstance(postion, (Category) screenType));
                    break;

                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    goToViewContestFragment(postion);
                    break;

                case Constant.Events.FEED_UPDATE_OPTION:
                    Options opt = contestList.get(Integer.parseInt("" + screenType)).getItem().getOptions().get(postion);
                    performFeedOptionClick(opt, Integer.parseInt("" + screenType));
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public void performFeedOptionClick(Options opt, int actPosition) {
        switch (opt.getName()) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(actPosition);
                break;
            case Constant.TabOption.MAKE_PAYMENT:
                openWebView(opt.getValue(), opt.getLabel());
                break;
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_CONTEST_ID, contestList.get(actPosition).getItem().getContestId());
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditContestFragment.newInstance(Constant.FormType.EDIT_CONTEST, map, Constant.URL_CONTEST_EDIT, null)).addToBackStack(null).commit();
                break;
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_CONTEST);

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

    private void callLikeApi(final int REQ_CODE, final int position, String url) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, contestList.get(position).getItem());
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    String type;
                    int id;
                    if (TYPE_ENTRIES.equals(selectedScreen)
                            || TYPE_CONTEST_ENTRIES.equals(selectedScreen)
                            || TYPE_WINNERS.equals(selectedScreen)) {
                        type = Constant.ResourceType.ENTRY;
                        id = contestList.get(position).getItem().getParticipantId();

                    } else {
                        type = Constant.ResourceType.CONTEST;
                        id = contestList.get(position).getItem().getContestId();
                    }
                    request.params.put(Constant.KEY_CONTEST_ID, id);

                    request.params.put(Constant.KEY_ID, id);
                    request.params.put(Constant.KEY_TYPE, type);

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
                                        contestList.get(position).setContentLike(!vo.isContentLike());
                                    } else if (REQ_CODE == REQ_FAVORITE) {
                                        contestList.get(position).setContentFavourite(!vo.isContentFavourite());
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

    public void updateItemLikeFavorite(int REQ_CODE, int position, CommonVO vo) {
        if (REQ_CODE == REQ_LIKE) {
            contestList.get(position).getItem().setContentLike(!vo.isContentLike());
            contestList.get(position).getItem().setShowAnimation(1);
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            contestList.get(position).getItem().setContentFavourite(!vo.isContentFavourite());
            contestList.get(position).getItem().setShowAnimation(2);
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FOLLOW) {
            contestList.get(position).getItem().setContentFollow(!vo.isContentFollow());
            contestList.get(position).getItem().setShowAnimation(3);
            adapter.notifyItemChanged(position);
        }

    }

    private void callDeleteApi(final int position) {


        if (isNetworkAvailable(context)) {
            int contestId = contestList.get(position).getItem().getContestId();
            contestList.remove(position);
            adapter.notifyDataSetChanged();

            HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_DELETE);
            request.params.put(Constant.KEY_CONTEST_ID, contestId);
            request.headres.put(Constant.KEY_COOKIE, getCookie());
            request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
            request.requestMethod = HttpPost.METHOD_NAME;

            //contestList.remove(position);
            // adapter.notifyItemRemoved(position);
            //adapter.notifyDataSetChanged();


            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
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
                                            contestList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                        }*/
                            } else {

                                Util.showSnackbar(v, err.getErrorMessage());
                                //refresh list in case of any error
                                onRefresh();
                            }
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }

                    return true;
                }
            };
            new HttpRequestHandler(activity, new Handler(callback)).run(request);

        } else {
            notInternetMsg(v);
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
                    request.params.put("resource_type", "contest");
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
            contestList.get(actPosition).getItem().getShortcut_save().setIs_saved(false);
        }else {
            contestList.get(actPosition).getItem().getShortcut_save().setIs_saved(true);
            contestList.get(actPosition).getItem().getShortcut_save().setShortcut_id(shortcutid);
        }
        adapter.notifyItemChanged(actPosition);
    }


}
