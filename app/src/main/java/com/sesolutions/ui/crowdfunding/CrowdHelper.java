package com.sesolutions.ui.crowdfunding;

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
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

/**
 * Created by root on 29/11/17.
 */

public class CrowdHelper<T extends RecyclerView.Adapter> extends CommentLikeHelper implements View.OnClickListener {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    private static final int REQ_DELETE = 400;
    private final int REQ_REQUEST = 401;
    private final int REQ_JOIN = 402;
    private final int REQ_LEAVE = 403;
    private final int REQ_CANCEL = 403;
    public int categoryId;
    public List<PageVo> videoList;
    public T adapter;
    public List<Category> categoryList;
    public FundResponse.Result result;


    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
              /*  case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(videoList.get(postion).getFund().getOwner_id());
                    break;
                case Constant.Events.CLICKED_HEADER_LOCATION:
                    JsonElement la = videoList.get(postion).getFund().getLocationObject();
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
                    callLikeApi(REQ_LIKE, postion, Constant.URL_FUND_LIKE, -1);
                    break;
                case Constant.Events.MUSIC_MAIN:
                    CrowdUtil.openViewFragment(fragmentManager, videoList.get(postion).getFund().getFundId());
                    break;
                case Constant.Events.PAGE_SUGGESTION_MAIN:
                    CrowdUtil.openViewFragment(fragmentManager, postion);
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) //do not open same category again
                        CrowdUtil.openViewCategoryFragment(fragmentManager, postion, "" + screenType);
                    break;

                case Constant.Events.FEED_UPDATE_OPTION:

                    //get clicked option
                    Options opt = videoList.get(Integer.parseInt("" + screenType)).getFund().getOptions().get(postion);

                    switch (opt.getName()) {
                        case Constant.OptionType.SHARE:
                            //open share dialog if share clicked
                            showShareDialog(videoList.get(Integer.parseInt("" + screenType)).getFund().getShare());
                            break;
                        case Constant.OptionType.DELETE:
                            showDeleteDialog(Integer.parseInt("" + screenType));
                            break;
                        case Constant.TabOption.MAKE_PAYMENT:
                            openWebView(opt.getValue(), opt.getLabel());
                            break;

                        case Constant.OptionType.EDIT:
                            CrowdUtil.openEditFragment(fragmentManager, videoList.get(postion).getFund().getFundId());
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
            tvMsg.setText(getString(R.string.MSG_DELETE_CONFIRMATION_GENERIC, getString(R.string.crowdfinding)));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callLikeApi(REQ_DELETE, position, Constant.URL_FUND_DELETE, -1);

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
            final FundContent vo = videoList.get(position).getFund();
            if (REQ_CODE >= REQ_DELETE) {/* >= means join,leave,request and delete*/
                showBaseLoader(false);
            } else {
                //update icon and show animation
                updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition);
            }
            try {

                HttpRequestVO request = new HttpRequestVO(url);

                // request.params.put(Constant.KEY_ID, vo.getFundId());
                request.params.put(Constant.KEY_FUND_ID, vo.getFundId());
                //request.params.put(Constant.KEY_TYPE, Constant.ResourceType.FUND);
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

    public void updateItemLikeFavorite(int REQ_CODE, int position, FundContent vo, boolean showAnimation) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).getFund().setShowAnimation(showAnimation ? 1 : 0);
            videoList.get(position).getFund().setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        }
    }


    public String getDetail(Albums album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
        ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }
}
