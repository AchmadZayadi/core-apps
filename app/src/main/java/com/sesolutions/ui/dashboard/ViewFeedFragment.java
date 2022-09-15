package com.sesolutions.ui.dashboard;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.comment.CommentResponse;
import com.sesolutions.responses.comment.Result;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.comment.CommentAdapter;
import com.sesolutions.ui.comment.CommentAttachImageAdapter;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.core_search.SearchFragment;
import com.sesolutions.ui.postfeed.StickerFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by mrnee on 10/7/2017.
 */

public class ViewFeedFragment extends FeedApiHelper implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnLoadMoreListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private static final boolean COMMENT_ENABLED = false;

    private boolean openComment;
    private int id;
    private String type;
    //Comment layout variables
    private TextView tvCameraImage;
    private TextView tvVideoImage;
    private TextView tvStickerImage;

    private List<Object> attachmentList;
    private CommentAttachImageAdapter adapterImage;
    private RecyclerView rvImageAttach;
    private TextView tvPost;
    private View llStickerBottom;
    private StickerFragment stickerFragment;
    private boolean isEmojiSelected;
    // private boolean isLoading;
    //   private ProgressBar pb;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<CommentData> commentList;
    private Result result;
    double latitdue = 0;
    double longtitude = 0;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    //private Activity actVo;

    public static ViewFeedFragment newInstance(String tag, int actionId, boolean openComment, int resourceId, String resourceType/*, Activity vo*/) {
        ViewFeedFragment frag = new ViewFeedFragment();
        frag.tag = tag;
        frag.actionId = actionId;
        frag.openComment = openComment;
        frag.id = resourceId;
        //frag.actVo = vo;
        frag.type = resourceType;
        return frag;
    }

    public static ViewFeedFragment newInstance(String tag) {
        ViewFeedFragment frag = new ViewFeedFragment();
        frag.tag = tag;
        frag.actionId = 0;

        frag.type = null;
        return frag;
    }

    // private String resourceType;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {


        if (v != null /*&& !openComment*/) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_feed, container, false);
        v.findViewById(R.id.ivMessage).setVisibility(View.GONE);
        applyTheme(v);

        //getLocation
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return v;
    }


    public List<Activity> feedActivityList2;

    private void updateOptionText(int actPosition, int position, String name, String value) {
        CustomLog.e("values56565", actPosition + "___" + position + "___");
        feedActivityList.get(actPosition).getOptions().get(position).setValue(value);
        feedActivityList.get(actPosition).getOptions().get(position).setName(name);
        feedActivityList2 = feedActivityList;
        //    adapterFeedMain.notifyItemChanged(actPosition);
        adapterFeedMain = new FeedActivityAdapter(feedActivityList2, context, this);
        recycleViewFeedMain.setAdapter(adapterFeedMain);
        adapterFeedMain.notifyDataSetChanged();

    }


    @Override
    public void onResume() {
        super.onResume();
        initScreenData();
    }

    public void initScreenData() {
        init();
        setRoundedFilledDrawable(v.findViewById(R.id.rlSearch));

        //hiddenPanel.setOnClickListener(this);
        setFeedMainRecycleView();
        //if (null == actVo)
        callFeedApi(TextUtils.isEmpty(tag) ? REQ_VIEW_FEED : REQ_CODE_TAG);
        if (AppConfiguration.titleHeaderType == 2) {
            v.findViewById(R.id.tvTitleMain).setVisibility(View.GONE);
            v.findViewById(R.id.rlSearch).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.tvTitleMain).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlSearch).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.tvTitleMain)).setText(AppConfiguration.siteTitle);
        }
        v.findViewById(R.id.lay_search).setVisibility(View.VISIBLE);
        if (COMMENT_ENABLED)
            initComment();
        else {
            v.findViewById(R.id.llBottom).setVisibility(View.GONE);
        }


        v.findViewById(R.id.ivCurrency).setVisibility(View.GONE);
        v.findViewById(R.id.ivProfileToolbar).setVisibility(View.GONE);
        v.findViewById(R.id.ivVideo).setVisibility(View.GONE);
        v.findViewById(R.id.ivToolbarSearch).setVisibility(View.GONE);

    }

    @Override
    public void updateFeedMainRecycleview() {
        super.updateFeedMainRecycleview();
        recycleViewFeedMain.setNestedScrollingEnabled(false);
        if (COMMENT_ENABLED && actionId != 0) callFeelingApi(REQ_CODE_FEELING);
    }

    private void initComment() {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        v.findViewById(R.id.llBottom).setVisibility(View.GONE);

        tvCameraImage = v.findViewById(R.id.tvCameraImage);
        tvVideoImage = v.findViewById(R.id.tvVideoImage);
        tvStickerImage = v.findViewById(R.id.tvStickerImage);

        etBody = v.findViewById(R.id.etComment);
        llStickerBottom = v.findViewById(R.id.llStickerBottom);

        tvCameraImage.setTypeface(iconFont);
        tvVideoImage.setTypeface(iconFont);
        tvStickerImage.setTypeface(iconFont);


        tvCameraImage.setText(Constant.FontIcon.CAMERA);
        tvCameraImage.setTextColor(Color.parseColor(Constant.ColorHex.Photo));
        tvVideoImage.setText(Constant.FontIcon.VIDEO);
        tvVideoImage.setTextColor(Color.parseColor(Constant.ColorHex.VIDEO));
        tvStickerImage.setText(Constant.FontIcon.STICKER);
        tvStickerImage.setTextColor(Color.parseColor(Constant.ColorHex.FEELING_ACTIVITY));

        tvCameraImage.setOnClickListener(this);
        tvVideoImage.setOnClickListener(this);
        tvStickerImage.setOnClickListener(this);
        tvPost = v.findViewById(R.id.tvPost);
        tvPost.setOnClickListener(this);
        v.findViewById(R.id.llBottomFake).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);

       /* ivLikeUpper1 = v.findViewById(R.id.ivLikeUpper1);
        ivLikeUpper2 = v.findViewById(R.id.ivLikeUpper2);
        ivLikeUpper3 = v.findViewById(R.id.ivLikeUpper3);
        ivLikeUpper4 = v.findViewById(R.id.ivLikeUpper4);
        ivLikeUpper5 = v.findViewById(R.id.ivLikeUpper5);*/


        pb = v.findViewById(R.id.pb);
        //   etSearch = v.findViewById(R.id.etSearch);
        recyclerView = v.findViewById(R.id.rvComment);
        rvImageAttach = v.findViewById(R.id.rvImageAttach);
        setDrawable();

        setRecyclerView();
        setImageRecyclerView();
        // callFeelingApi(REQ_CODE_FEELING);
    }

    private void setDrawable() {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        // shape.setColor(colorPrimary);
        shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }

    private void setRecyclerView() {
        try {
            commentList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new CommentAdapter(commentList, context, this, this);
            adapter.setIsViewFeed();
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setImageRecyclerView() {
        try {
            attachmentList = new ArrayList<>();
            rvImageAttach.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvImageAttach.setLayoutManager(layoutManager);
            adapterImage = new CommentAttachImageAdapter(attachmentList, context, this);
            rvImageAttach.setAdapter(adapterImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        v.findViewById(R.id.lay_search).setVisibility(View.VISIBLE);
        hiddenPanel = v.findViewById(R.id.hidden_panel);
        ImageView ivProfileToolbar = v.findViewById(R.id.ivProfileToolbar);
        if (SPref.getInstance().isLoggedIn(context)) {
            UserMaster vo = SPref.getInstance().getUserMasterDetail(context);
            ivProfileToolbar.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(ivProfileToolbar, vo.getPhotoUrl(), context, R.drawable.placeholder_3_2);
        } else {
            ivProfileToolbar.setVisibility(View.GONE);
        }
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.rlSearch).setOnClickListener(this);
        ivProfileToolbar.setOnClickListener(this);
        recycleViewFeedMain = v.findViewById(R.id.rvFeedMain);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                /*case R.id.hidden_panel:
                    //hideSlideLayout();
                    break;*/
                case R.id.rlSearch:
                    // openCoreSearchFragment();
                    break;
                case R.id.ivProfileToolbar:
                    if (SPref.getInstance().isLoggedIn(context)) {
                        int id = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
                        goToProfileFragment(id);
                    } else {
                        Util.showSnackbar(v, Constant.MSG_NOT_LOGGED_IN);
                    }
                    break;

                case R.id.tvStickerImage:
                    showBottomSticker();
                    break;

                case R.id.tvCameraImage:
//                    showImageDialog(Constant.MSG_SELECT_IMAGE_SOURCE);
                    showImageChooser();
                    break;
                case R.id.tvVideoImage:
                    showVideoSourceDialog(Constant.MSG_CHOOSE_SOURCE);
                    break;
                case R.id.tvPost:
                    submitCommentIfValid();
                    break;
                case R.id.llBottomFake:
                    hideStickerLayout();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void hideStickerLayout() {
        llStickerBottom.setVisibility(View.GONE);
        v.findViewById(R.id.llBottom).setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> etBody.requestFocus(), 100);
    }

    private void showBottomSticker() {
        closeKeyboard();
        llStickerBottom.setVisibility(View.VISIBLE);
        v.findViewById(R.id.llBottom).setVisibility(View.GONE);
        activity.setTaskPerformed(0);

        if (stickerFragment == null) {
            stickerFragment = new StickerFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.container_comment, stickerFragment).addToBackStack(null).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        AppConfiguration.truncateBody = false;
        if ((activity).taskPerformed == Constant.TASK_STICKER) {
            hideStickerLayout();
            attachmentList.clear();
            updateImageAttachAdapter();
            etBody.setText(Constant.EMPTY);
            isEmojiSelected = true;
            activity.setTaskPerformed(0);
            submitCommentIfValid();
        }
    }

    @Override
    public void onStop() {
        AppConfiguration.truncateBody = true;
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void submitCommentIfValid() {
        boolean isValid = false;
        Map<String, Object> params = new HashMap<>();
        String body = etBody.getText().toString();
        //String body = StringEscapeUtils.escapeHtml4(etBody.getText().toString());

        if (!TextUtils.isEmpty(body)) {
            params.put("body", body);
            isValid = true;
        }
        int imageCount = 0;
        if (attachmentList.size() > 0) {
            isValid = true;
            for (int i = 0; i < attachmentList.size(); i++) {
                if (attachmentList.get(i) instanceof String) {
                    params.put("video[" + i + "]", "photo");
                    params.put(Constant.FILE_TYPE + "attachmentImage[" + imageCount + "]", attachmentList.get(i).toString());
                    imageCount++;
                } else {
                    params.put("video[" + i + "]", ((Video) attachmentList.get(i)).getVideoId());
                }
            }
        }
        if (isEmojiSelected) {
            isValid = true;
            params.put("emoji_id", activity.getEmotion().getFileId());
            isEmojiSelected = false;
        }
        closeKeyboard();
        if (isValid) {
            callCreateCommentApi(params);
        }
    }

   /* private void updateUpperlayout() {
        try {
            if (null != result.getComments()) {
                showView(v.findViewById(R.id.llReactionUpperComment));
                ((TextView) v.findViewById(R.id.tvLikeUpperComment)).setText(result.getComments().getLikeStats().getLikes_fluent_list());
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    private void callFeelingApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } /*else {
                        showBaseLoader(false);
                    }*/
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GET_COMMENT);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (null != type) {
                        request.params.put(Constant.KEY_RESOURCES_TYPE, type);
                    } else {
                        request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ACTIVITY_ACTION);
                    }
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);
                                result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    if (null != comResp.getResult().getCommentData()) {
                                        commentList.addAll(comResp.getResult().getCommentData());
                                        Collections.reverse(commentList);
                                    }

                                    updateUpperlayout();
                                    updateFeelingAdapter();
                                    showHideCommentLayout();
                                } /*else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
                                }*/
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }

                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                }

            } else {
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateUpperlayout() {
        try {
            //hide eaction layout for this screen
            (v.findViewById(R.id.llReactionUpperComment)).setVisibility(View.GONE);
            // return;
            /* if (null != result.getComments()) {
                List<ReactionPlugin> list = result.getComments().getLikes();
                showView(v.findViewById(R.id.llReactionUpperComment));
                ((TextView) v.findViewById(R.id.tvLikeUpperComment)).setText(result.getComments().getLikeStats().getLikes_fluent_list());
               if (list != null) {

                    if (list.size() > 0) {
                        ivLikeUpper1.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(ivLikeUpper1, list.get(0).getImage(), context);
                    } else {
                        ivLikeUpper1.setVisibility(View.GONE);
                    }
                    if (list.size() > 1) {
                        ivLikeUpper2.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(ivLikeUpper2, list.get(1).getImage(), context);
                    } else {
                        ivLikeUpper2.setVisibility(View.GONE);
                    }
                    if (list.size() > 2) {
                        ivLikeUpper3.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(ivLikeUpper3, list.get(2).getImage(), context);
                    } else {
                        ivLikeUpper3.setVisibility(View.GONE);
                    }
                    if (list.size() > 3) {
                        ivLikeUpper4.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(ivLikeUpper4, list.get(3).getImage(), context);
                    } else {
                        ivLikeUpper4.setVisibility(View.GONE);
                    }
                    if (list.size() > 4) {
                        ivLikeUpper5.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(ivLikeUpper5, list.get(4).getImage(), context);
                    } else {
                        ivLikeUpper5.setVisibility(View.GONE);
                    }
                }
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showHideCommentLayout() {
        //  if (!result.getCanComment())
        v.findViewById(R.id.llBottom).setVisibility(result.getCanComment() ? View.VISIBLE : View.GONE);

        if (null != result.getAttachmentOptions()
                && result.getAttachmentOptions().size() > 0
                && result.getAttachmentOptions().contains(Constant.AttachmentOption.STICKERS)) {
            tvStickerImage.setVisibility(View.VISIBLE);
        } else {
            tvStickerImage.setVisibility(View.GONE);
        }

        tvCameraImage.setVisibility(result.getEnable().getAlbum() == 1 ? View.VISIBLE : View.GONE);
        tvVideoImage.setVisibility(result.getEnable().getVideo() == 1 ? View.VISIBLE : View.GONE);
    }
/*

    private void updateScreenTitle(int size) {
        tvTitle.setText(size > 0 ? Constant.TITLE_COMMENT + " (" + size + ")" : Constant.TITLE_COMMENT);
    }
*/

    private void updateFeelingAdapter() {
        //  updateScreenTitle(result.getTotal());
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        pb.setVisibility(View.GONE);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_COMMENT);
        v.findViewById(R.id.tvNoData).setVisibility(commentList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void updateImageAttachAdapter() {
        adapterImage.notifyDataSetChanged();
        ///  pb.setVisibility(View.GONE);
        //  isLoading = false;
        // ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_STICKERS);
        rvImageAttach.setVisibility(attachmentList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    private void openCoreSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchFragment()).addToBackStack(null).commit();

    }

    @Override
    public boolean onItemClicked(Integer clickType, Object value, int postion) {
        switch (clickType) {
            case Constant.Events.FEED_ATTACH_IMAGE_CANCEL:
                //   imageList.remove(postion);
                attachmentList.remove(postion);
                updateImageAttachAdapter();
                break;
            case Constant.Events.COMMENT_HEADER_TITLE:
            case Constant.Events.COMMENT_HEADER_IMAGE:
                goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, commentList.get(postion).getPosterId());
                break;
            case Constant.Events.LIKE_COMMENT:
                if (Integer.parseInt("" + value) > -1) {
                    int reactionId;
                    ReactionPlugin reactionVo = SPref.getInstance().getReactionPlugins(context).get(Integer.parseInt("" + value));
                    reactionId = reactionVo.getReactionId();
                    commentList.get(postion).updateLikeTemp(true, new Like(reactionVo.getImage(), reactionVo.getTitle()));
                    adapter.notifyItemChanged(postion);
                    callLikeUnlikeApi(Constant.URL_LIKE_COMMENT, commentList.get(postion).getCommentId(), reactionId, postion);
                } else {
                    commentList.get(postion).updateLikeTemp(false, new Like());
                    adapter.notifyItemChanged(postion);

                    callLikeUnlikeApi(Constant.URL_UNLIKE_COMMENT, commentList.get(postion).getCommentId(), 0, postion);
                }
                //callLikeUnlikeApi(commentList.get(postion), postion);
                break;

            case Constant.Events.DELETE_COMMENT:
                showDeleteDialog(commentList.get(postion).getCommentId(), postion);
                break;

            case Constant.Events.ITEM_COMMENT:
                performClick("" + value, postion, "", false);
                break;

            case Constant.Events.COMMENT_LINK:
                String href = commentList.get(postion).getLink().getHref();
                String title = commentList.get(postion).getLink().getTitle();
                openWebView(href, title);
                break;
            case Constant.Events.FEED_UPDATE_OPTION:
                //   hideSlideLayout();
                Options vo;
                if (TextUtils.isEmpty(feedActivityList.get(Integer.parseInt("" + value)).getContentType())) {
                    vo = feedActivityList.get(Integer.parseInt("" + value)).getOptions().get(postion);
                } else {
                    vo = feedActivityList.get(Integer.parseInt("" + value)).getMenus().get(postion);
                }
                int actionId = feedActivityList.get(Integer.parseInt("" + value)).getActionId();
                performFeedOptionClick(actionId, vo, Integer.parseInt("" + value), postion);
                break;

        }
        return super.onItemClicked(clickType, value, postion);
    }


    private void performFeedOptionClick(int actionId, Options vo, int actPosition, int position) {
        switch (vo.getName()) {

            case Constant.OptionType.SAVE:
                // showBaseLoader(false);
                callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
            case "feed_link":
                try {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(vo.getUrl(), vo.getUrl());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Util.showSnackbar(v, getString(R.string.copy_clipboard));
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case Constant.OptionType.UNSAVE:
                //showBaseLoader(false);
                callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.DISABLE_COMMENT:
                callFeedEventApi(REQ_CODE_OPTION_COMMENT_DISABLE, Constant.URL_FEED_DISABLE_COMMENT, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.ENABLE_COMMENT:
                callFeedEventApi(REQ_CODE_OPTION_COMMENT_ENABLE, Constant.URL_FEED_DISABLE_COMMENT, actionId, vo, actPosition, position);
                break;

        }
    }


    private void callFeedEventApi(final int reqCode, String url, int actionId, final Options vo, final int actPosition, final int position) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_ACTION_ID, actionId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        //  hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            CustomLog.e("repsonse333333333", "" + response);
                            if (response != null) {
                                BaseResponse<Object> resp = new Gson().fromJson(response, BaseResponse.class);


                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_DELETE:
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            adapterFeedMain.notifyItemRemoved(actPosition);
                                            Util.showSnackbar(v, res.getResult());
                                            break;

                                        case REQ_CODE_OPTION_SAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition, position, "unsave", Constant.TXT_UNSAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            hideBaseLoader();
                                            updateOptionText(actPosition, position, "save", Constant.TXT_SAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_COMMENT_DISABLE:
                                            // feedActivityList.get(actPosition).toggleCommantable();
                                            updateOptionText(actPosition, position, "enable_comment", Constant.TXT_ENABLE_COMMENT);
                                            break;

                                        case REQ_CODE_OPTION_COMMENT_ENABLE:
                                            //feedActivityList.get(actPosition).toggleCommantable();
                                            updateOptionText(actPosition, position, "disable_comment", Constant.TXT_DISABLE_COMMENT);
                                            break;
                                        case REQ_CODE_OPTION_HIDE_FEED:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_UNDO:
                                            feedActivityList.get(actPosition).setHidden(false);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_REPORT:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(true);
                                            adapterFeedMain.notifyItemChanged(actPosition);
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
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }



   /* private void callLikeUnlikeApi(final CommentData commentData, final int position) {
        int reqCode = 0;
        String url;

        try {
            if (isNetworkAvailable(context)) {
                try {

                    url = commentData.getIsLike() ? Constant.URL_UNLIKE_COMMENT : Constant.URL_LIKE_COMMENT;
                    commentList.get(position).toggleLike();
                    adapter.notifyItemChanged(position);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_COMMENT_ID, commentData.getCommentId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, type);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                // response = response.replace("â\u0080\u0099", "'");
                                CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);
                                //  result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    *//*CommentData vo = commentList.get(position);
                                    vo.setLikeCount(comResp.getResult().getLikeCount());
                                    vo.setIsLike(!commentData.getIsLike());
                                    commentList.set(position, vo);
                                    adapter.notifyItemChanged(position);*//*
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }*/

    private void callLikeUnlikeApi(final String url, int commentId, final int reactionId, final int position) {

        if (isNetworkAvailable(context)) {
            try {


                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put("subjectid", actionId);
                request.params.put("sbjecttype", resourceType);
               /* if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }*/
                request.params.put(Constant.KEY_TYPE, reactionId);
                request.params.put(Constant.KEY_COMMENT_ID, commentId);
                request.params.put("longitude", longtitude);
                request.params.put("latitude", latitdue);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);

                            if (TextUtils.isEmpty(comResp.getError())) {
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                                commentList.get(position).updateFinalLike(res.getResult());
                                adapter.notifyItemChanged(position);
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                                //revert the changes made in case og any error
                                commentList.get(position).toggleLike();
                                adapter.notifyItemChanged(position);

                            }
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignore) {
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void showDeleteDialog(final int commentId, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_COMMENT_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(commentId, position);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(int commentId, final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_COMMENT);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                    request.params.put(Constant.KEY_COMMENT_ID, commentId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, type);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                // response = response.replace("â\u0080\u0099", "'");
                                BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                                //  result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                    commentList.remove(position);

                                    adapter.notifyItemRemoved(position);
                                    Util.showSnackbar(v, res.getResult());
                                    // adapter.notifyItemChanged(position);
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }

    }


    private void callCreateCommentApi(Map<String, Object> params) {

        CustomLog.d("hasilnyaa","sukes");
        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    tvPost.setEnabled(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_COMMENT);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.putAll(params);
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put("longitude", longtitude);
                    request.params.put("latitude", latitdue);
                    if (null != type) {
                        request.params.put(Constant.KEY_RESOURCES_TYPE, type);
                    } else {
                        request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ACTIVITY_ACTION);
                    }
                    //request.params.put(Constant.KEY_RESOURCES_TYPE, type);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        tvPost.setEnabled(true);
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;


                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                // response = response.replace("â\u0080\u0099", "'");
                                BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                                //  result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    etBody.setText(Constant.EMPTY);
                                    attachmentList.clear();
                                    updateImageAttachAdapter();
                                    String itemComment = new JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString();
                                    CommentData vo = new Gson().fromJson(itemComment, CommentData.class);

                                    // Collections.reverse(commentList);
                                    commentList.add(vo);
                                    //   Collections.reverse(commentList);
                                    updateFeelingAdapter();
                                    recyclerView.smoothScrollToPosition(commentList.size());
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpImageRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    tvPost.setEnabled(true);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                tvPost.setEnabled(true);
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            tvPost.setEnabled(true);
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }

    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callFeelingApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onResponseSuccess(int reqCode, Object response) {

        switch (reqCode) {

            case REQ_CODE_IMAGE:
                //  imageList.addAll((List<String>) response);
                attachmentList.addAll((List<String>) response);
                updateImageAttachAdapter();
                break;

            case REQ_CODE_VIDEO:
                attachmentList.add(videoDetail);
                updateImageAttachAdapter();
                break;
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
//                Intent intent = new Intent();
//                intent.putExtra("Longitude", mLastLocation.getLongitude());
//                intent.putExtra("Latitude", mLastLocation.getLatitude());
//                setResult(1,intent);
//                finish();


                longtitude = mLastLocation.getLongitude();
                latitdue = mLastLocation.getLatitude();
                //   CustomLog.d("hasilnyaa33",String.valueOf(loca.getLongitude()) + " haadahah");
                ////   CustomLog.d("hasilnyaa", String.valueOf(location.getLongitude()) + "   asdakndkasn");
//                CustomLog.d("hasilnyaa", String.valueOf(mLastLocation.getLatitude()) + " haadahah");
//
//                CustomLog.d("hasilnyaa22", String.valueOf(mLastLocation.getLongitude()) + " haadahah");

            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

  /*  @Override
    public void onRefresh() {
        callFeedApi(REQ_CODE_REFRESH);
    }*/
}
