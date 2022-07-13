package com.sesolutions.ui.groups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ShareCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.groups.GroupContent;
import com.sesolutions.responses.groups.GroupResponse;
import com.sesolutions.responses.page.Locations;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.welcome.Dummy;
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

public class GroupHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_CANCEL = 403;
    public OnUserClickedListener<Integer, Object> parent;
    public int categoryId;
    public List<PageVo> videoList;
    public T adapter;
    public List<Category> categoryList;
    public GroupResponse.Result result;


    private void updateOptionText(int actPosition,  String name, String value,int shortcutid) {

        if(name.equalsIgnoreCase("save")){
            videoList.get(actPosition).getGroup().getShortcut_save().setIs_saved(false);
        }else {
            videoList.get(actPosition).getGroup().getShortcut_save().setIs_saved(true);
            videoList.get(actPosition).getGroup().getShortcut_save().setShortcut_id(shortcutid);
        }

        adapter.notifyItemChanged(actPosition);
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {

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
                      Log.e("888889"+screenType,"9999999");
                    try {
                        boolean isboolen = videoList.get(Integer.parseInt("" + screenType)).getGroup().getShortcut_save().isIs_saved();
                        Log.e("isboolen",""+isboolen);
                        int actionId = videoList.get(Integer.parseInt("" + screenType)).getGroup().getShortcut_save().getResource_id();
                        Log.e("actionId",""+actionId);
                        int shortcutid = 0;
                        if(isboolen){
                            shortcutid = videoList.get(Integer.parseInt("" + screenType)).getGroup().getShortcut_save().getShortcut_id();
                        }
                        Log.e("888889"+screenType,"9999999"+shortcutid);
                        performFeedOptionClick(actionId,  Integer.parseInt("" + screenType), isboolen,shortcutid);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;


                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(videoList.get(postion).getGroup().getOwner_id());
                    break;
                case Constant.Events.CLICKED_HEADER_LOCATION:
                    try {
                        JsonElement la = videoList.get(postion).getGroup().getLocationObject();
                        if (null != la && la.isJsonObject()) {
                            Locations vo = new Gson().fromJson(la, Locations.class);
                            if (vo.canShowMap()) {
                                super.openGoogleMap(vo.getLat(), vo.getLng());
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        CustomLog.e(e);
                    }
                    break;
                case Constant.Events.MUSIC_LIKE:

                    //if likeFollow setting enabled then also call follow api
                    if (videoList.get(postion).getGroup().hasToChangeFollowLike()) {
                        callLikeApi(REQ_FOLLOW, postion, Constant.URL_GROUP_FOLLOW, -2);
                    }
                    callLikeApi(REQ_LIKE, postion, Constant.URL_GROUP_LIKE, -1);
                    break;
                case Constant.Events.MUSIC_FAVORITE:
                    callLikeApi(REQ_FAVORITE, postion, Constant.URL_GROUP_FAVORITE, -1);
                    break;
                case Constant.Events.MUSIC_ADD:

                    //if likeFollow setting enabled then also call like api
                    if (videoList.get(postion).getGroup().hasToChangeFollowLike()) {
                        callLikeApi(REQ_LIKE, postion, Constant.URL_GROUP_LIKE, -2);
                    }
                    callLikeApi(REQ_FOLLOW, postion, Constant.URL_GROUP_FOLLOW, -1);
                    break;
                case Constant.Events.MUSIC_MAIN:
                    goToViewGroupFragment(videoList.get(postion).getGroup().getGroup_id());
                    break;
                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    goToViewGroupFragment(postion);
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        openViewGroupCategoryFragment(postion, "" + screenType);
                    break;

                case Constant.Events.FEED_UPDATE_OPTION:

                    //get clicked option
                    Options opt = videoList.get(Integer.parseInt("" + screenType)).getGroup().getButtons().get(postion);

                    //open share dialog if share clicked
                    if (opt.getName().equals(Constant.OptionType.SHARE)) {
                        showShareDialog(videoList.get(Integer.parseInt("" + screenType)).getGroup().getShare());
                    } else if (opt.getName().equals(Constant.OptionType.DELETE)) {
                        showDeleteDialog(Integer.parseInt("" + screenType));
                    } else if (Constant.OptionType.CONTACT.equals(opt.getName())) {
                        super.openGroupContactForm(videoList.get(Integer.parseInt("" + screenType)).getGroup().getOwner_id());
                    } else {
                        //check if user has permissoion to view details
                        boolean openLoginForm = videoList.get(Integer.parseInt("" + screenType)).getGroup().isShowLoginForm();

                        if (openLoginForm) {
                            //open sign-in screen
                            goToWelcome(1);
                        } else {
                            int ownerId = videoList.get(Integer.parseInt("" + screenType)).getGroup().getOwner_id();
                            performOptionClick(postion, ownerId, Integer.parseInt("" + screenType));
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    private void showJoinLeaveDialog(final int optionPosition, final int position) {

        //in case of public user ,send him to sign-in screen
        if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1);
            return;
        }

        activity.taskId = position;
        Options opt = videoList.get(position).getGroup().getButtons().get(optionPosition);
        String dialogMsg = Constant.EMPTY;
        String buttonTxt = Constant.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        switch (opt.getName()) {
            case Constant.OptionType.JOIN_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_join_group);
                buttonTxt = getStrings(R.string.join_group);
                url[0] = Constant.URL_GROUP_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_group);
                buttonTxt = getStrings(R.string.leave_group);
                url[0] = Constant.URL_GROUP_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_GROUP_JOIN;
                req[0] = REQ_REQUEST;
                break;
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_group);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_GROUP_CANCEL_MEMBER;
                req[0] = REQ_CANCEL;
                break;
        }

        /*if (Constant.OptionType.LEAVE_SMOOTHBOX.equals(opt.getName())
                || Constant.OptionType.JOIN_SMOOTHBOX.equals(opt.getName())
                || Constant.OptionType.CANCEL.equals(opt.getName())
                ) {*/

        if (!Constant.OptionType.REQUEST.equals(opt.getName())) {
            showLeaveDialog(dialogMsg, buttonTxt, url[0], req[0], position);
        } else {
            // fetchRequestFormData(req[0], position, url[0], optionPosition);
            Map<String, Object> map1 = new HashMap<String, Object>();
            final GroupContent group = videoList.get(position).getGroup();
            map1.put(Constant.KEY_ID, group.getGroup_id());
            map1.put(Constant.KEY_GROUP_ID, group.getGroup_id());
            map1.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
            openGroupJoinForm(null, map1, url[0], buttonTxt, dialogMsg);
        }
    }


    private void showLeaveDialog(String dialogMsg, String buttonTxt, String url, int REQ, int position) {
        try {

            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(dialogMsg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(buttonTxt);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callLikeApi(REQ, position, url, -1);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  public void showDeleteDialog(final int position) {
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
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_EVENT));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(position);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

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


    public void performOptionClick(final int optionPosition, int ownerId, int position) {
        try {
            Options opt = videoList.get(position).getGroup().getButtons().get(optionPosition);
            switch (opt.getName()) {
                case Constant.OptionType.MAIL:
                    ShareCompat.IntentBuilder.from(activity)
                            .setType("message/rfc822")
                            .addEmailTo(opt.getValue())
                            .setSubject("")
                            .setText("")
                            //.setHtmlText(body) //If you are using HTML in your body text
                            .setChooserTitle(opt.getLabel())
                            .startChooser();

                    break;
                case Constant.OptionType.WEBSITE:
                    String url = opt.getValue();
                    if (!TextUtils.isEmpty(url)) {
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        openWebView(url, opt.getValue());
                    } else {
                        Util.showSnackbar(v, getStrings(R.string.invalid_url));
                    }
                    break;
              /*  case Constant.OptionType.SHARE:
                    showShareDialog(opt.getValue(), "");
                    break;*/
                case Constant.OptionType.PHONE:
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + opt.getValue()));
                    startActivity(intent);
                    break;
                case Constant.OptionType.CONTACT:
                    super.openGroupContactForm(ownerId);
                    break;
              /*  case Constant.OptionType.DELETE:
                    callLikeApi(REQ_DELETE, position, Constant.URL_GROUP_DELETE, videoList.get(position).getGroup());
                    break;*/
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_GROUP_ID, videoList.get(position).getGroup().getGroup_id());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditGroupFragment.newInstance(Constant.FormType.EDIT_GROUP, map, Constant.URL_GROUP_EDIT, null))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.OptionType.JOIN_SMOOTHBOX:
                case Constant.OptionType.LEAVE_SMOOTHBOX:
                case Constant.OptionType.REQUEST:
                case Constant.OptionType.CANCEL:
                    showJoinLeaveDialog(optionPosition, position);
                    break;
                default:
                    CustomLog.e("option_name", "" + opt.getName());
                    break;
            }


        } catch (Exception e) {
            CustomLog.e(e);
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_GROUP));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callLikeApi(REQ_DELETE, position, Constant.URL_GROUP_DELETE, -1);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
    }

    private void callLikeApi(final int REQ_CODE, final int position, String url, final int optionPosition) {


        if (isNetworkAvailable(context)) {
            final GroupContent vo = videoList.get(position).getGroup();
            if (REQ_CODE >= REQ_DELETE) {/* >= means join,leave,request and delete*/
                showBaseLoader(false);
            } else {
                //update icon and show animation
                updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition);
            }
            try {

                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_ID, vo.getGroup_id());
                request.params.put(Constant.KEY_GROUP_ID, vo.getGroup_id());
                request.params.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "@" + response);
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
                                    } else if (REQ_CODE > REQ_DELETE) {
                                        /*> means join,leave,request*/
                                        // JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("menus");
                                        updateItemOption(response, position);

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

    public void updateItemOption(String response, int position) {
        try {
            GroupResponse opt = new Gson().fromJson(response, GroupResponse.class);
            //PageVo vo = videoList.get(position);
            //CustomLog.e("pagevo", new Gson().toJson(vo));
            videoList.get(position).getGroup().setButtons(opt.getResult().getMenus());
            adapter.notifyItemChanged(position);
            Util.showSnackbar(v, opt.getResult().getMessage());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, int position, GroupContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).getGroup().setShowAnimation(showAnimation ? 1 : 0);
            videoList.get(position).getGroup().setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).getGroup().setShowAnimation(showAnimation ? 2 : 0);
            videoList.get(position).getGroup().setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FOLLOW) {
            videoList.get(position).getGroup().setShowAnimation(showAnimation ? 3 : 0);
            videoList.get(position).getGroup().setContentFollow(!vo.isContentFollow());
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

    private void fetchRequestFormData(final int REQ_CODE, final int position, String url, final int optionPosition) {
        try {
            if (isNetworkAvailable(context)) {

                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_GET_FORM, 1);

                    final GroupContent group = videoList.get(position).getGroup();
                    request.params.put(Constant.KEY_ID, group.getGroup_id());
                    request.params.put(Constant.KEY_GROUP_ID, group.getGroup_id());
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.GROUP);

                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        //openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.GROUP);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            //openGroupCreateForm(vo.getResult(), new HashMap<String, Object>());
                                        }
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }


                            } else {
                                Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {

                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
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
                    request.params.put("resource_type", "sesgroup_group");
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



}
