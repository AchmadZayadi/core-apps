package com.sesolutions.ui.comment;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.comment.CommentResponse;
import com.sesolutions.responses.comment.Result;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.dashboard.ApiHelper;
import com.sesolutions.ui.postfeed.StickerFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;
import com.sesolutions.utils.VibratorUtils;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentReplyFragment extends ApiHelper implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    private static final int REQ_LOAD_MORE = 3;
    private static final int REQ_CODE_STICKER_CONTENT = 4;
    private static final int REQ_CODE_UNLIKE = 124;
    private static final int REQ_CODE_LIKE = 123;
    private View v;
    private boolean isLoading;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private CommentReplyAdapter adapter;
    private List<CommentData> commentList;
    private Result result;
    private TextView tvTitle;
    private View tvCameraImage;
    private View tvVideoImage;
    private View tvStickerImage;
    //private List<String> imageList;
    private List<Object> attachmentList;
    private CommentAttachImageAdapter adapterImage;
    private RecyclerView rvImageAttach;
    private int actionId;
    //private AppCompatEditText etComment;
    private ImageView tvPost;
    private View llStickerBottom;
    private StickerFragment stickerFragment;
    private boolean isEmojiSelected;
    private String resourceType;
    private String guid;
    private Result parentComment;
    private boolean isReplied;

    /*public static CommentReplyFragment newInstance(int actionId, String resourceType, Result parentComment) {
        CommentReplyFragment frag = new CommentReplyFragment();
        frag.actionId = actionId;
        frag.resourceType = resourceType;
        frag.parentComment = parentComment;
        return frag;
    }*/

    public static CommentReplyFragment newInstance(int actionId, String resourceType, String guid, Result parentComment, boolean isReplied) {
        CommentReplyFragment frag = new CommentReplyFragment();
        frag.actionId = actionId;
        frag.resourceType = resourceType;
        frag.guid = guid;
        frag.isReplied = isReplied;
        frag.parentComment = parentComment;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_comment, container, false);
        try {
            applyTheme(v);
            init();
            setRecyclerView();
            commentList.addAll(parentComment.getCommentData());
            updateFeelingAdapter();
            showHideCommentLayout();
            setImageRecyclerView();
            callFeelingApi(REQ_CODE_FEELING);

            new Handler().postDelayed(() -> {
                openKeyboard();
                etBody.requestFocus();
            }, 400);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        v.findViewById(R.id.llBottom).setVisibility(View.GONE);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.title_replies);
        tvCameraImage = v.findViewById(R.id.tvCameraImage);
        tvVideoImage = v.findViewById(R.id.tvVideoImage);
        tvStickerImage = v.findViewById(R.id.tvStickerImage);

        etBody = v.findViewById(R.id.etComment);
        etBody.setHintTextColor(Color.parseColor(Constant.text_color_2));
        etBody.setTextColor(Color.parseColor(Constant.text_color_1));
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPost.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                tvStickerImage.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);

                if (s.length() > 0) {
                    tvStickerImage.setVisibility(View.GONE);
                    tvPost.setVisibility(View.VISIBLE);
                } else {
                    tvPost.setVisibility(View.GONE);
                    tvStickerImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        llStickerBottom = v.findViewById(R.id.llStickerBottom);
        llStickerBottom.setBackgroundColor(Color.parseColor(Constant.foregroundColor));


        tvCameraImage.setOnClickListener(this);
        tvVideoImage.setOnClickListener(this);
        tvStickerImage.setOnClickListener(this);
        tvPost = v.findViewById(R.id.tvPost);
        tvPost.setOnClickListener(this);
        ((CardView) v.findViewById(R.id.cvEditText)).setCardBackgroundColor(SesColorUtils.getAppBgColor(context));
        v.findViewById(R.id.llBottomFake).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.llReactionUpper).setOnClickListener(this);

        pb = v.findViewById(R.id.pb);
        //   etSearch = v.findViewById(R.id.etSearch);
        recyclerView = v.findViewById(R.id.rvComment);
        rvImageAttach = v.findViewById(R.id.rvImageAttach);
        rvImageAttach.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

    }


    private void setRecyclerView() {
        try {
            commentList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            // mLayoutManager.setReverseLayout(true);
            // mLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLayoutManager);
            adapter = new CommentReplyAdapter(commentList, context, this, this);
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

    @Override
    public void onBackPressed() {
        if (llStickerBottom.getVisibility() == View.VISIBLE) {
            hideStickerLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.tvStickerImage:
                    showBottomSticker();
                    break;

                case R.id.tvCameraImage:
//                    showImageDialog(getStrings(R.string.MSG_SELECT_IMAGE_SOURCE));
                    openImagePicker();
                    break;
                case R.id.tvVideoImage:
                    showVideoSourceDialog(getStrings(R.string.MSG_CHOOSE_SOURCE));
                    break;
                case R.id.tvPost:
                    submitCommentIfValid();
                    break;
                case R.id.llBottomFake:
                    hideStickerLayout();
                    break;
                case R.id.llReactionUpper:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    map.put(Constant.KEY_ID, actionId);
                   /* getChildFragmentManager().beginTransaction()
                            .replace(R.id.container
                                    , ReactionViewFragment.newInstance(map))
                            .addToBackStack(null)
                            .commit();*/
                    openReactionViewfragment(map);
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
        (activity).setTaskPerformed(0);

        if (stickerFragment == null) {
            stickerFragment = StickerFragment.newInstance(true);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.container_comment, stickerFragment).addToBackStack(null).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (activity.taskPerformed == Constant.TASK_STICKER) {
                hideStickerLayout();
                attachmentList.clear();
                updateImageAttachAdapter();
                etBody.setText(Constant.EMPTY);
                isEmojiSelected = true;
                (activity).setTaskPerformed(0);
                submitCommentIfValid();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
            params.put("emoji_id", (activity).getEmotion().getFileId());
            isEmojiSelected = false;
        }
        closeKeyboard();
        if (isValid) {
            callCreateCommentApi(params);
        } else {
            VibratorUtils.vibrate(context);
            startAnimation(tvPost, Techniques.SHAKE, 400);
        }
    }

    private void callFeelingApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        // showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GET_COMMENT_REPLIES);
                    request.params.put("limit_data", Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                    if (null != guid) {
                        request.params.put(Constant.KEY_GUID, guid);
                    }
                    request.params.put(Constant.KEY_COMMENT_ID, parentComment.getCommentData().get(0).getCommentId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);
                                    result = comResp.getResult();

                                    if (TextUtils.isEmpty(comResp.getError())) {
                                        if (null != comResp.getResult().getReplies()) {
                                            // Collections.reverse(comResp.getResult().getReplies());
                                            commentList.addAll(comResp.getResult().getReplies());
                                        }
                                        updateFeelingAdapter();

                                    } else {
                                        Util.showSnackbar(v, comResp.getErrorMessage());
                                        goIfPermissionDenied(comResp.getError());
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

    private void showHideCommentLayout() {
        //  if (!result.getCanComment())
        v.findViewById(R.id.llBottom).setVisibility(parentComment.getCanComment() ? View.VISIBLE : View.GONE);
        tvStickerImage.setVisibility(View.GONE);
        tvCameraImage.setVisibility(View.GONE);
        tvVideoImage.setVisibility(View.GONE);

        if (null != parentComment.getAttachmentOptions() && parentComment.getAttachmentOptions().size() > 0) {

            if (parentComment.getAttachmentOptions().contains(Constant.AttachmentOption.STICKERS)) {
                tvStickerImage.setVisibility(View.VISIBLE);
            }
            if (parentComment.getAttachmentOptions().contains(Constant.AttachmentOption.PHOTOS)) {
                tvCameraImage.setVisibility(parentComment.getEnable().getAlbum() == 1 ? View.VISIBLE : View.GONE);
            }
            if (parentComment.getAttachmentOptions().contains(Constant.AttachmentOption.VIDEOS)) {
                tvVideoImage.setVisibility(parentComment.getEnable().getVideo() == 1 ? View.VISIBLE : View.GONE);
            }

        }


    }

    private void updateFeelingAdapter() {
        // updateScreenTitle(result.getTotal());
        adapter.setCanReply(parentComment.getReplyComment());
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        pb.setVisibility(View.GONE);
        isLoading = false;
    }

    private void updateScreenTitle(int size) {
        tvTitle.setText(size > 0 ? Constant.TITLE_COMMENT + " (" + size + ")" : Constant.TITLE_COMMENT);
    }

    private void updateImageAttachAdapter() {
        adapterImage.notifyDataSetChanged();
        rvImageAttach.setVisibility(attachmentList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        CustomLog.d("itemClicked", "click code : " + object1 + " at postition : " + postion);
        switch (object1) {
            case Constant.Events.FEED_ATTACH_IMAGE_CANCEL:
                //   imageList.remove(postion);
                attachmentList.remove(postion);
                updateImageAttachAdapter();
                break;
            case Constant.Events.COMMENT_HEADER_IMAGE:
            case Constant.Events.COMMENT_HEADER_TITLE:
                performClick(commentList.get(postion).getPosterType(), commentList.get(postion).getPosterId(), null, false);
                //  goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, commentList.get(postion).getPosterId());
                break;//
            case Constant.Events.REPLY:
                openKeyboard();
                etBody.requestFocus();
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
                break;
            case Constant.Events.FEED_UPDATE_OPTION:
                int listPosition = Integer.parseInt("" + value);
                Options vo = commentList.get(listPosition).getOptions().get(postion);
                if (Constant.OptionType.EDIT.equals(vo.getName())) {
                    EditDialogFragment.newInstance(this, listPosition, commentList.get(listPosition)).show(fragmentManager, "comment");
                } else {
                    showDeleteDialog(commentList.get(listPosition).getCommentId(), listPosition);
                }
                break;

            case Constant.Events.ITEM_COMMENT:
                performClick("" + value, postion, "", false);
                break;
            case Constant.Events.COMMENT_LINK:
                String href = commentList.get(postion).getLink().getHref();
                String title = commentList.get(postion).getLink().getTitle();
                openWebView(href, title);
                break;
        }
        return false;
    }

    private void callLikeUnlikeApi(final String url, int commentId, final int reactionId, final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {


                    HttpRequestVO request = new HttpRequestVO(url);
                    if (resourceType.equals("activity_action") || resourceType.equals("sesadvancedactivity_action"))
                        request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    else {
                        request.params.put("subjectid", actionId);
                        request.params.put("sbjecttype", resourceType);
                    }
                    if (null != guid) {
                        request.params.put(Constant.KEY_GUID, guid);
                    }
                    request.params.put(Constant.KEY_TYPE, reactionId);
                    request.params.put(Constant.KEY_COMMENT_ID, commentId);

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

    public void showDeleteDialog(final int commentId, final int position) {
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
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_COMMENT_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(commentId, position);

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

    private void callDeleteApi(int commentId, final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_COMMENT);
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                    request.params.put(Constant.KEY_COMMENT_ID, commentId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);

                    if (null != guid) {
                        request.params.put(Constant.KEY_GUID, guid);
                    }

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
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
                        }
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

        try {
            if (isNetworkAvailable(context)) {
                int[] posDummy = {-1};
                try {
                    if (params.containsKey("body")) {
                        UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
                        posDummy[0] = commentList.size();
                        etBody.setText(Constant.EMPTY);
                        commentList.add(new CommentData(
                                (String) params.get("body"),
                                userVo.getDisplayname(),
                                userVo.getPhotoUrl(),
                                Util.getCurrentdate(Constant.DATE_FROMAT_FEED)));
                        updateFeelingAdapter();
                        recyclerView.smoothScrollToPosition(posDummy[0]);
                    } else {
                        showBaseLoader(true);
                    }

                    tvPost.setEnabled(false);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_REPLY_COMMENT);

                    request.params.putAll(params);
                    request.params.put(Constant.KEY_COMMENT_ID, parentComment.getCommentData().get(0).getCommentId());
                    request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);

                    if (null != guid) {
                        request.params.put(Constant.KEY_GUID, guid);
                    }

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

                                    attachmentList.clear();
                                    updateImageAttachAdapter();
                                    String itemComment = new JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString();
                                    CommentData vo = new Gson().fromJson(itemComment, CommentData.class);

                                    if (posDummy[0] > -1) {
                                        commentList.get(posDummy[0]).updateObject(vo);
                                        updateFeelingAdapter();
                                    } else {
                                        etBody.setText(Constant.EMPTY);
                                        commentList.add(vo);
                                        updateFeelingAdapter();
                                        recyclerView.smoothScrollToPosition(commentList.size() - 1);
                                    }

                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }

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
                attachmentList.addAll((List<String>) response);
                updateImageAttachAdapter();
                break;

            case REQ_CODE_VIDEO_LINK:
                attachmentList.add(videoDetail);
                updateImageAttachAdapter();
                break;

        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }
}
