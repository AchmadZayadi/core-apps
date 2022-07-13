package com.sesolutions.ui.events;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.responses.event.Reviews;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewViewFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private final String KEY_REVIEW_ID = "review_id";
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private EventResponse.Result result;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvItemTitle;
    private TextView tvUser;
    private TextView ivUser;
    private TextView tvPros;
    private TextView tvCons;
    private View tv1;
    private View tv2;
    private View tv3;
    private ImageView ivImage;
    private TextView tvDesc;
    private TextView tvRecommend;
    private TextView tvStats;
    private ImageView ivStar1;
    private ImageView ivStar2;
    private ImageView ivStar3;
    private ImageView ivStar4;
    private ImageView ivStar5;
    private LinearLayoutCompat llStar;

    private int mObjectId;
    private boolean openComment;

    public static ReviewViewFragment newInstance(int topicId) {
        ReviewViewFragment frag = new ReviewViewFragment();
        frag.mObjectId = topicId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_review_view, container, false);
        applyTheme(v);
        initScreenData();
        callBottomCommentLikeApi(mObjectId, Constant.ResourceType.SES_EVENT_REVIEW, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(mObjectId, Constant.ResourceType.SES_EVENT_REVIEW);
        }
        return v;
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.vertical_dots));
        ((ImageView) v.findViewById(R.id.ivSearch)).setRotation(90);
        (v.findViewById(R.id.ivSearch)).setOnClickListener(this);
        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        //hiding favorite ,because user can never favourite a review
        v.findViewById(R.id.llFavorite).setVisibility(View.GONE);

        tvItemTitle = v.findViewById(R.id.tvItemTitle);
        tvUser = v.findViewById(R.id.tvUser);
        ivUser = v.findViewById(R.id.ivUser);
        tvPros = v.findViewById(R.id.tvPros);
        tvCons = v.findViewById(R.id.tvCons);
        tv1 = v.findViewById(R.id.tv1);
        tv2 = v.findViewById(R.id.tv2);
        tv3 = v.findViewById(R.id.tv3);
        ivImage = v.findViewById(R.id.ivImage);
        tvDesc = v.findViewById(R.id.tvDesc);
        tvRecommend = v.findViewById(R.id.tvRecommend);
        tvStats = v.findViewById(R.id.tvStats);

        ivStar1 = v.findViewById(R.id.ivStar1);
        ivStar2 = v.findViewById(R.id.ivStar2);
        ivStar3 = v.findViewById(R.id.ivStar3);
        ivStar4 = v.findViewById(R.id.ivStar4);
        ivStar5 = v.findViewById(R.id.ivStar5);

        llStar = v.findViewById(R.id.llStar);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_REVIEW) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }

    //@Override
    public void onRefresh() {
        setRefreshing(swipeRefreshLayout, true);
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.ivSearch:
                    showPopup(result.getReview().getOptions(), v, 10,this);
                    break;
                case R.id.tvUser:
                case R.id.ivImage:
                    goToProfileFragment(result.getReview().getOwnerId());
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            if (req == 1) {
                showBaseLoader(true);
            }
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_REVIEW);
                request.params.put(KEY_REVIEW_ID, mObjectId);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        try {
                            String response = (String) msg.obj;
                            hideAllLoaders();
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    EventResponse resp = new Gson().fromJson(response, EventResponse.class);
                                    result = resp.getResult();
                                    showHideUpperLayout();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideAllLoaders();
                            somethingWrongMsg(v);
                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideAllLoaders();
                somethingWrongMsg(v);
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void showHideUpperLayout() {
        try {
            if (null != result.getReview()) {
                v.findViewById(R.id.cvMain).setVisibility(View.VISIBLE);
                Reviews vo = result.getReview();
                ((TextView) v.findViewById(R.id.tvTitle)).setText(vo.getTitle());
                (v.findViewById(R.id.ivSearch)).setVisibility(result.getOptions() != null ? View.VISIBLE : View.GONE);

                Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);


                if (TextUtils.isEmpty(vo.getViewer_title())) {
                    tvUser.setVisibility(View.GONE);
                    ivUser.setVisibility(View.GONE);
                } else {
                    tvUser.setVisibility(View.VISIBLE);
                    ivUser.setVisibility(View.VISIBLE);
                    ivUser.setTypeface(iconFont);
                    ivUser.setText(Constant.FontIcon.USER);
                    tvUser.setText(vo.getViewer_title());
                }

                tvItemTitle.setText(vo.getTitle());

                Util.showImageWithGlide(ivImage, vo.getOwnerImage(), context, R.drawable.placeholder_square);

                if (null != vo.getPros()) {
                    tv1.setVisibility(View.VISIBLE);
                    tvPros.setVisibility(View.VISIBLE);
                    tvPros.setText(vo.getPros());
                } else {
                    tv1.setVisibility(View.GONE);
                    tvPros.setVisibility(View.GONE);
                }
                if (null != vo.getCons()) {
                    tv2.setVisibility(View.VISIBLE);
                    tvCons.setVisibility(View.VISIBLE);
                    tvCons.setText(vo.getCons());
                } else {
                    tv2.setVisibility(View.GONE);
                    tvCons.setVisibility(View.GONE);
                }
                tvRecommend.setVisibility(vo.isRecommended() ? View.VISIBLE : View.GONE);
                if (vo.isDescriptionsAvailable()) {
                    tv3.setVisibility(View.VISIBLE);
                    tvDesc.setVisibility(View.VISIBLE);
                    tvDesc.setText(vo.getDescription());
                } else {
                    tv3.setVisibility(View.GONE);
                    tvDesc.setVisibility(View.GONE);
                }

                String detail = "\uf164 " + vo.getLikeCount()
                        + "  \uf075 " + vo.getCommentCount()
                        //   + "  \uf004 " + vo.getFavouriteCount()
                        + "  \uf06e " + vo.getViewCount();
                tvStats.setText(detail);
                tvStats.setTypeface(iconFont);

                ivImage.setOnClickListener(this);
                tvUser.setOnClickListener(this);
                Drawable dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
                Drawable dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
                llStar.setVisibility(View.VISIBLE);
                ivStar1.setImageDrawable(vo.getRating() > 0 ? dStarFilled : dStarUnFilled);
                ivStar2.setImageDrawable(vo.getRating() > 1 ? dStarFilled : dStarUnFilled);
                ivStar3.setImageDrawable(vo.getRating() > 2 ? dStarFilled : dStarUnFilled);
                ivStar4.setImageDrawable(vo.getRating() > 3 ? dStarFilled : dStarUnFilled);
                ivStar5.setImageDrawable(vo.getRating() > 4 ? dStarFilled : dStarUnFilled);

                v.findViewById(R.id.ivSearch).setVisibility(vo.getOptions() != null ? View.VISIBLE : View.GONE);

            } else {
                v.findViewById(R.id.cvMain).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        setRefreshing(swipeRefreshLayout, false);
        // hideView(v.findViewById(R.id.pbMain));
        // hideView(pb);
    }


    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case -3:
                try {
                    hideBaseLoader();
                    String response = (String) screenType;
                    CustomLog.e("repsonse1", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (TextUtils.isEmpty(err.getError())) {
                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                            activity.taskPerformed = Constant.Task.DELETE_REVIEW;
                            onBackPressed();
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;

        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public void showDeleteDialog() {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_REVIEW);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();

                    if (isNetworkAvailable(context)) {
                        // categoryList.remove(position);
                        // adapter.notifyItemRemoved(position);
                        showBaseLoader(false);
                        Map<String, Object> map = new HashMap<>();
                        map.put(KEY_REVIEW_ID, mObjectId);
                        map.put(Constant.KEY_TYPE, mObjectId);
                        new ApiController(Constant.URL_DELETE_REVIEW, map, context, ReviewViewFragment.this, -3).execute();
                    } else {
                        notInternetMsg(v);
                    }
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            Options opt = null;
            boolean isCover = false;
            int itemId = item.getItemId();
            /*if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getReview().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getReview().getUpdateProfilePhoto().get(itemId - 1);
            } else {*/
            itemId = itemId - 10;
            opt = result.getReview().getOptions().get(itemId - 1);
            //    }


            switch (opt.getAction()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(KEY_REVIEW_ID, mObjectId);
                    fragmentManager.beginTransaction().replace(R.id.container, ReviewCreateForm.newInstance(Constant.FormType.EDIT_REVIEW, map, Constant.URL_EDIT_REVIEW)).addToBackStack(null).commit();
                    //openFormFragment(Constant.FormType.EDIT_REVIEW, map, Constant.URL_EDIT_REVIEW);
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getReview().getShare());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.SES_EVENT_REVIEW + "_" + mObjectId);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }
}
