package com.sesolutions.ui.store;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Locations;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.StoreResponse;
import com.sesolutions.responses.store.StoreVo;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.page.CreateEditPageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreBrowseHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_CANCEL = 403;
    public int categoryId;
    public List<StoreVo> videoList;
    public T adapter;
    public List<Category> categoryList;
    public StoreResponse.Result result;

    public int selectedVo = 1;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(videoList.get(postion).getItem().getOwner_id());
                    break;
                case Constant.Events.CLICKED_HEADER_LOCATION:
                    JsonElement la = videoList.get(postion).getItem().getLocationObject();
                    if (null != la && la.isJsonObject()) {
                        Locations vo = new Gson().fromJson(la, Locations.class);
                        if (vo.canShowMap()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + vo.getLat() + "," + vo.getLng()));
                            startActivity(intent);
                        }
                    }
                    break;
                case Constant.Events.MUSIC_LIKE:

                    //if likeFollow setting enabled then also call follow api
                    if (videoList.get(postion).getItem().hasToChangeFollowLike()) {
                        callLikeApi(REQ_FOLLOW, postion, Constant.URL_STORE_FOLLOW, -2);
                    }
                    callLikeApi(REQ_LIKE, postion, Constant.URL_STORE_LIKE, -1);
                    break;
                case Constant.Events.MUSIC_FAVORITE:
                    callLikeApi(REQ_FAVORITE, postion, Constant.URL_STORE_FAVORITE, -1);
                    break;
                case Constant.Events.MUSIC_ADD:

                    //if likeFollow setting enabled then also call like api
                    if (videoList.get(postion).getItem().hasToChangeFollowLike()) {
                        callLikeApi(REQ_LIKE, postion, Constant.URL_STORE_LIKE, -2);
                    }
                    callLikeApi(REQ_FOLLOW, postion, Constant.URL_STORE_FOLLOW, -1);
                    break;
                case Constant.Events.MUSIC_MAIN:
//                    openViewPageFragment(videoList.get(postion).getItem().getStoreId());
                    StoreUtil.openViewStoreFragment(fragmentManager, postion /*videoList.get(postion).getItem().getStoreId()*/);
                    break;
                case Constant.Events.PAGE_SUGGESTION_MAIN:
//                    openViewPageFragment(postion);
                    StoreUtil.openViewStoreFragment(fragmentManager, postion);
                    break;
                case Constant.Events.VIEW_PRODUCT:
                    StoreUtil.openViewProductFragment(fragmentManager, postion);
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) { //do not open same category again
                        StoreUtil.openViewStoreCategoryFragment(fragmentManager, postion, "" + screenType);
                    }
//                        openViewPageCategoryFragment(postion, "" + screenType);
                    break;

                case Constant.Events.FEED_UPDATE_OPTION:

                    //get clicked option
                    Options opt = videoList.get(Integer.parseInt("" + screenType)).getItem().getMenus().get(postion);

                    //open share dialog if share clicked
                    switch (opt.getName()) {
                        case Constant.OptionType.SHARE:
                            showShareDialog(videoList.get(Integer.parseInt("" + screenType)).getItem().getShare());
                            break;
                        case Constant.OptionType.DELETE:
                            showDeleteDialog(Integer.parseInt("" + screenType));
                            break;
                        case Constant.TabOption.MAKE_PAYMENT:
                            openWebView(opt.getValue(), opt.getLabel());
                            break;
                        case Constant.OptionType.CONTACT:
                            super.openStoreContactForm(videoList.get(Integer.parseInt("" + screenType)).getItem().getOwner_id());
                            break;
                        case Constant.OptionType.JOIN_SMOOTHBOX:
                        case Constant.OptionType.LEAVE_SMOOTHBOX:
                        case Constant.OptionType.REQUEST:
                            showJoinLeaveDialog(postion, Integer.parseInt("" + screenType));
                            break;
                        case Constant.OptionType.CANCEL:
                            showJoinLeaveDialog(postion, Integer.parseInt("" + screenType));
                            break;
                        default:
                            //check if user has permissoion to view details
                            boolean openLoginForm = videoList.get(Integer.parseInt("" + screenType)).getItem().isShowLoginForm();

                            if (openLoginForm) {
                                //open sign-in screen
                                goToWelcome(1);
                            } else {
                                int ownerId = videoList.get(Integer.parseInt("" + screenType)).getItem().getOwner_id();
                                performOptionClick(postion, ownerId, Integer.parseInt("" + screenType));
                            }
                            break;
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

        Options opt = videoList.get(position).getItem().getMenus().get(optionPosition);
        String dialogMsg = Constant.EMPTY;
        String buttonTxt = Constant.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        switch (opt.getName()) {
            case Constant.OptionType.JOIN_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_join_page);
                buttonTxt = getStrings(R.string.join_page);
                url[0] = Constant.URL_STORE_JOIN;
                req[0] = REQ_JOIN;
                break;
            case Constant.OptionType.LEAVE_SMOOTHBOX:
                dialogMsg = getStrings(R.string.msg_leave_page);
                buttonTxt = getStrings(R.string.leave_page);
                url[0] = Constant.URL_STORE_LEAVE;
                req[0] = REQ_LEAVE;
                break;
            case Constant.OptionType.REQUEST:
                dialogMsg = getStrings(R.string.msg_request_membership_store);
                buttonTxt = getStrings(R.string.send_request);
                url[0] = Constant.URL_STORE_REQUEST;
                req[0] = REQ_REQUEST;
                break;
            case Constant.OptionType.CANCEL:
                dialogMsg = getStrings(R.string.msg_request_cancel_store);
                buttonTxt = getStrings(R.string.cancel_request);
                url[0] = Constant.URL_STORE_CANCEL_MEMBER;
                req[0] = REQ_CANCEL;
                break;


        }

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
            tvMsg.setText(dialogMsg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(buttonTxt);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callLikeApi(req[0], position, url[0], optionPosition);

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

    public void performOptionClick(final int optionPosition, int ownerId, int position) {
        try {
            Options opt = videoList.get(position).getItem().getMenus().get(optionPosition);
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
                    super.openPageContactForm(ownerId);
                    break;
              /*  case Constant.OptionType.DELETE:
                    callLikeApi(REQ_DELETE, position, Constant.URL_STORE_DELETE,videoList.get(selectedVo).getProducts().get(position));
                    break;*/
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_STORE_ID, videoList.get(position).getItem().getStoreId());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditPageFragment.newInstance(Constant.FormType.EDIT_STORE, map, Constant.URL_STORE_EDIT, null))
                            .addToBackStack(null)
                            .commit();
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
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.MSG_DELETE_CONFIRMATION_STORE));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callLikeApi(REQ_DELETE, position, Constant.STORE_DELETE, -1);

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
            final StoreContent vo = videoList.get(position).getItem();
            if (REQ_CODE >= REQ_DELETE) {/* >= means join,leave,request and delete*/
                showBaseLoader(false);
            } else {
                //update icon and show animation
                updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition);
            }
            try {

                HttpRequestVO request = new HttpRequestVO(url);

                request.params.put(Constant.KEY_ID, vo.getStoreId());
                request.params.put(Constant.KEY_STORE_ID, vo.getStoreId());
                request.params.put(Constant.KEY_TYPE, Constant.ResourceType.STORE);
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
                                    } else if (REQ_CODE > REQ_REQUEST) {
                                        /*> means join,leave,request*/
                                        // JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("menus");
                                        StoreResponse opt = new Gson().fromJson(response, StoreResponse.class);
                                        videoList.get(position).getItem().setMenus(opt.getResult().getMenus());
                                        adapter.notifyItemChanged(position);
                                        Util.showSnackbar(v, opt.getResult().getMessage());
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

    public void updateItemLikeFavorite(int REQ_CODE, int position, StoreContent vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).getItem().setShowAnimation(showAnimation ? 1 : 0);
            videoList.get(position).getItem().setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).getItem().setShowAnimation(showAnimation ? 2 : 0);
            videoList.get(position).getItem().setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
        } else if (REQ_CODE == REQ_FOLLOW) {
            videoList.get(position).getItem().setShowAnimation(showAnimation ? 3 : 0);
            videoList.get(position).getItem().setContentFollow(!vo.isContentFollow());
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


}
