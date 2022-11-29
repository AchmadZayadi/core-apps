package com.sesolutions.ui.comment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.imageeditengine.ImageEditActivity;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Emotion;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.User;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.comment.CommentResponse;
import com.sesolutions.responses.comment.Result;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.location.MyLastLocation;
import com.sesolutions.sesdb.SesDB;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.AGvideo.AGVideoActivity;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.ApiHelper;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.postfeed.GifFragment;
import com.sesolutions.ui.postfeed.StickerChildFragment;
import com.sesolutions.ui.postfeed.StickerFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.video.VideoViewActivity2;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.RoundedBackgroundSpan;
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
import java.util.Objects;

public class CommentFragment extends ApiHelper implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_WITH_DATA = 2;
    private static final int REQ_INITIAL = 1;
    private static final int REQ_LOAD_MORE = 3;
    private static final int REQ_CODE_STICKER_CONTENT = 4;
    private static final int REQ_CODE_UNLIKE = 124;
    private static final int REQ_CODE_LIKE = 123;
    private static final int REQ_CODE_EDIT = 125;
    private View v;
    private boolean isLoading;
    private ContentLoadingProgressBar pb;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<CommentData> commentList;
    private Result result;
    private TextView tvTitle;
    private View tvCameraImage;
    private View tvVideoImage;
    private ImageView tvGifImage;
    private View tvStickerImage;
    //private List<String> imageList;
    private List<Object> attachmentList;
    private CommentAttachImageAdapter adapterImage;
    private RecyclerView rvImageAttach;
    private int actionId;
    private AppCompatEditText etComment;
    private ImageView tvPost;
    private View llStickerBottom;
    private StickerFragment stickerFragment;
    private boolean isEmojiSelected;
    private boolean isGIFSelected = false;
    private String resourceType;
    private ImageView ivLikeUpper1;
    private ImageView ivLikeUpper2;
    private ImageView ivLikeUpper3;
    private ImageView ivLikeUpper4;
    private ImageView ivLikeUpper5;
    private String guid;
    private String titleQuote;
    String nameUser;
    String photoUser;
    String datePosting;
    String imagePosting;

    RelativeLayout rlUsersList;
    ProgressBar progress_bar;
    TextView txtMsg;
    RecyclerView recyclerViewuser;
    private ArrayList<Friends> usersList = new ArrayList<>();

    private boolean isSearching = false;
    private HttpRequestHandler requestHandler;
    String finalstring_data = "";

    private ArrayList<Integer> startPositions, userNameLengths, userIds;
    private ArrayList<String> userNamesList;
    private int currentCursorPosition = -1, maxHeight = 0, heightDifference, originalStartPosition = -1, startUserNameSearchKeyword = -1, previousCursorPosition = -1;
    String userNameSearchKeyword = "", beforeTextChanged = "", onTextChanged = "";
    private long last_text_edit = 0, delay = 1000, endTime = 0;
    private Handler handler = new Handler();
    double latitdue = 0;
    double longtitude = 0;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MyLastLocation location = new MyLastLocation();
    TextView tvQuoteTitle;
    TextView tvHeader;
    TextView tvDate;
    ImageView ivProfileImageRound;
    ImageView ivQuoteImage;
    RelativeLayout layoutImage;


    public static CommentFragment newInstance(int actionId, String resourceType, String titleQuote, String nameUser, String datePosting, String photoUser, String imagePosting) {
        CommentFragment frag = new CommentFragment();
        frag.actionId = actionId;
        frag.resourceType = resourceType;
        frag.titleQuote = titleQuote;
        frag.nameUser = nameUser;
        frag.datePosting = datePosting;
        frag.photoUser = photoUser;
        frag.imagePosting = imagePosting;
        return frag;

    }

    public static CommentFragment newInstance(int actionId, String resourceType, String guid, String titleQuote, String nameUser, String datePosting, String photoUser, String imagePosting) {
        CommentFragment frag = new CommentFragment();
        frag.actionId = actionId;
        frag.resourceType = resourceType;
        frag.guid = guid;
        frag.titleQuote = titleQuote;
        frag.nameUser = nameUser;
        frag.datePosting = datePosting;
        frag.photoUser = photoUser;
        frag.imagePosting = imagePosting;
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
            setImageRecyclerView();

            //get location
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

//            showCacheData(page);
            callFeelingApi(REQ_INITIAL);

            v.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);

                int screenHeight = v.getRootView().getHeight();
                heightDifference = screenHeight - r.bottom;
                maxHeight = r.bottom;
                Log.e("Keyboard Size", "Size: " + heightDifference);

                if (heightDifference == 0) {
                    dismissPopup();
                }

            });


            tvPost.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


  /*  private void showCacheData() {
        SesDB.commentDao(context).fetchComments(actionId, resourceType, Constant.RECYCLE_ITEM_THRESHOLD, (Constant.RECYCLE_ITEM_THRESHOLD * (page - 1)))
                .observe(this, commentData -> {
                    if (null != commentData) {
                        if (page == 1)
                            commentList.clear();
                        commentList.addAll(commentData);
                        adapter.notifyDataSetChanged();
                        //callFeelingApi(REQ_WITH_DATA);
                    } else {
                        //callFeelingApi(REQ_INITIAL);
                    }
                });
    }*/

    int page = 1;

    private void showCacheData(int page) {
        List<CommentData> comments = SesDB.commentDao(context).fetchCommentList(actionId, resourceType, Constant.RECYCLE_ITEM_THRESHOLD, (Constant.RECYCLE_ITEM_THRESHOLD * (page - 1)));
        if (null != comments) {
            if (page == 1)
                commentList.clear();
            commentList.addAll(comments);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    private void init() {
        v.findViewById(R.id.llBottom).setVisibility(View.GONE);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvGifImage = v.findViewById(R.id.tvGifImage);
        recyclerViewuser = v.findViewById(R.id.recyclerViewuser);
        progress_bar = v.findViewById(R.id.progress_bar);
        rlUsersList = v.findViewById(R.id.rlUsersList);
        txtMsg = v.findViewById(R.id.txtMsg);
        tvTitle.setText(R.string.TITLE_COMMENT);
        tvCameraImage = v.findViewById(R.id.tvCameraImage);
        tvVideoImage = v.findViewById(R.id.tvVideoImage);
        tvStickerImage = v.findViewById(R.id.tvStickerImage);
        tvQuoteTitle = v.findViewById(R.id.tvQuoteTitle);
        tvHeader = v.findViewById(R.id.tvHeader);
        tvDate = v.findViewById(R.id.tvDate);
        ivProfileImageRound = v.findViewById(R.id.ivProfileImage);
        ivQuoteImage = v.findViewById(R.id.ivQuoteImage);
        layoutImage = v.findViewById(R.id.rlQuoteMedia);

        startPositions = new ArrayList<>();
        userNameLengths = new ArrayList<>();
        userNamesList = new ArrayList<>();
        userIds = new ArrayList<>();

        etComment = v.findViewById(R.id.etComment);

        tvGifImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.gifimageurl = "";
                try {
                    fragmentManager.beginTransaction().replace(R.id.container, GifFragment.newInstance(true)).addToBackStack(null).commit();
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
      /*  etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = etComment.getText().toString();
                if (text.startsWith(" "))
                    etComment.setText(text.trim());

                if (etComment.getText().length() > 0) {
                    tvVideoImage.setVisibility(View.GONE);
                    tvPost.setVisibility(View.VISIBLE);
                } else {
                    tvPost.setVisibility(View.GONE);
                    if (null != result.getAttachmentOptions() && result.getAttachmentOptions().size() > 0) {

                        if (result.getAttachmentOptions().contains(Constant.AttachmentOption.EMOTIONS))
                            tvStickerImage.setVisibility(View.VISIBLE);
                        if (result.getAttachmentOptions().contains(Constant.AttachmentOption.VIDEOS))
                            tvVideoImage.setVisibility(result.getEnable().getVideo() == 1 ? View.VISIBLE : View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


*/


        tvQuoteTitle.setText(Util.getEmojiFromString(titleQuote));
        tvHeader.setText(nameUser);
        tvDate.setText(Util.changeDateFormat(context, datePosting));
        Glide.with(context).load(photoUser).circleCrop().into(ivProfileImageRound);


        if (imagePosting.equals("empty")){
            layoutImage.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(imagePosting).into(ivQuoteImage);
        }



        etComment.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence s, int start, int count,
                                                                           int after) {

                                                 beforeTextChanged = s.toString();

                                             }

                                             @Override
                                             public void onTextChanged(final CharSequence s, int start, int before,
                                                                       int count) {

                                                 onTextChanged = s.toString();

                                                 if (onTextChanged.length() > beforeTextChanged.length()) {
                                                     currentCursorPosition = count + start;
                                                     previousCursorPosition = start;

                                                     if (s.toString().substring(previousCursorPosition, previousCursorPosition + 1).equals("@")) {
                                                         startUserNameSearchKeyword = previousCursorPosition;
                                                         originalStartPosition = startUserNameSearchKeyword;
                                                     }

                                                     if (startUserNameSearchKeyword == -1) {
                                                         for (int i = 0; i < startPositions.size(); i++) {
                                                             if (startPositions.get(i) > currentCursorPosition)
                                                                 startPositions.set(i, startPositions.get(i) + count);
                                                         }
                                                     }
                                                 } else if (beforeTextChanged.length() > onTextChanged.length()) {
                                                     if (beforeTextChanged.length() - onTextChanged.length() == 1) {
                                                         previousCursorPosition = start + before;
                                                         currentCursorPosition = start + count;

                                                         if (startUserNameSearchKeyword == -1) {
                                                             for (int i = 0; i < startPositions.size(); i++) {
                                                                 if (previousCursorPosition == startPositions.get(i) + userNamesList.get(i).length() + 2) {
                                                                     for (int j = i + 1; j < startPositions.size(); j++) {
                                                                         startPositions.set(j, startPositions.get(j) - (userNameLengths.get(i) + 4));
                                                                     }
                                                                     String frontHalfText = onTextChanged.substring(0, currentCursorPosition - (userNameLengths.get(i) + 3));
                                                                     String endHalfText = onTextChanged.substring(currentCursorPosition);
                                                                     onTextChanged = frontHalfText + endHalfText;
                                                                     currentCursorPosition = currentCursorPosition - (userNameLengths.get(i) + 3);
                                                                     startPositions.remove(i);
                                                                     userNameLengths.remove(i);
                                                                     userNamesList.remove(i);
                                                                     userIds.remove(i);
                                                                     SpannableStringBuilder str = new SpannableStringBuilder(onTextChanged);
                                                                     Log.e("STRINGDATAUSER", "" + str);
                                                                     SpannableStringBuilder spannableStringBuilder = getUserTagSpan(str);
                                                                     etComment.setText(spannableStringBuilder);
                                                                     etComment.setSelection(currentCursorPosition);
                                                                     break;
                                                                 } else if (startPositions.get(i) > previousCursorPosition) {
                                                                     startPositions.set(i, startPositions.get(i) - 1);
                                                                 }
                                                             }

                                                         } else {
                                                             if (beforeTextChanged.substring(0, previousCursorPosition).endsWith("@")) {
                                                                 startUserNameSearchKeyword = -1;
                                                                 originalStartPosition = -1;
                                                                 userNameSearchKeyword = "";
                                                             }
                                                         }
                                                     }
                                                 }
                                             }

                                             @Override
                                             public void afterTextChanged(final Editable s) {
                                                 //avoid triggering event when text is empty
                                                 if (s.toString().isEmpty())
                                                     tvPost.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                                 else
                                                     tvPost.setImageTintList(null);

                                                 if (startUserNameSearchKeyword != -1) {
                                                     userNameSearchKeyword = s.toString().substring(originalStartPosition + 1);
                                                     int spaceIndex = userNameSearchKeyword.indexOf(" ");
                                                     if (spaceIndex != -1)
                                                         userNameSearchKeyword = userNameSearchKeyword.substring(0, userNameSearchKeyword.indexOf(" "));
                                                 }

                                                 try {
                                                     handler.removeCallbacks(input_finish_checker);
                                                 } catch (Exception ex) {
                                                     ex.printStackTrace();
                                                 }
                                                 Log.e("userNameSearchKeyword", "" + userNameSearchKeyword.length());
                                                 Log.e("wwwwwwwwwwww", "" + startUserNameSearchKeyword);
                                                 if (userNameSearchKeyword.length() > 0) {


                                                     last_text_edit = System.currentTimeMillis();
                                                     handler.postDelayed(input_finish_checker, delay);
                                                 } else {
                                                     dismissPopup();
                                                 }
                                             }
                                         }
        );


        llStickerBottom = v.findViewById(R.id.llStickerBottom);

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
        recyclerView = v.findViewById(R.id.rvComment);
        rvImageAttach = v.findViewById(R.id.rvImageAttach);
        rvImageAttach.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

        ivLikeUpper1 = v.findViewById(R.id.ivLikeUpper1);
        ivLikeUpper2 = v.findViewById(R.id.ivLikeUpper2);
        ivLikeUpper3 = v.findViewById(R.id.ivLikeUpper3);
        ivLikeUpper4 = v.findViewById(R.id.ivLikeUpper4);
        ivLikeUpper5 = v.findViewById(R.id.ivLikeUpper5);

    }

    private void setRecyclerView() {
        try {
            commentList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            //  mLayoutManager.setReverseLayout(true);
            // mLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLayoutManager);
            adapter = new CommentAdapter(commentList, context, this, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    final Runnable input_finish_checker = () -> {
        if (System.currentTimeMillis() > (last_text_edit + delay - 1000)) {
            if (!userNameSearchKeyword.endsWith(" ")) {
                usersList = new ArrayList<>();
                isSearching = true;
                showPopup();
                callSuggestionApi(userNameSearchKeyword);
            } else {
                startUserNameSearchKeyword = -1;
                userNameSearchKeyword = "";
                dismissPopup();
            }
        }
    };

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

        BaseActivity.backcoverchange = Constant.GO_TO_HOMEFRAGMENT;
        try {
            BaseActivity.commentcount = commentList.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


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
        new Handler().postDelayed(() -> etComment.requestFocus(), 100);
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

        mGoogleApiClient.connect();
        try {
            Log.e("task", "" + activity.taskPerformed2);
            if (activity.taskPerformed2 == Constant.TASK_STICKER) {
                hideStickerLayout();
                attachmentList.clear();
                updateImageAttachAdapter();
                etComment.setText(Constant.EMPTY);
                finalstring_data = "";
                userNamesList.clear();
                isEmojiSelected = true;
                isGIFSelected = false;
                (activity).setTaskPerformed(0);
                submitCommentIfValid();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("gif", "" + BaseActivity.gifimageurl);
        if (BaseActivity.gifimageurl != null && BaseActivity.gifimageurl.length() > 0) {
            isGIFSelected = true;
            isEmojiSelected = false;
            attachmentList.clear();
            updateImageAttachAdapter();
            etComment.setText(Constant.EMPTY);
            finalstring_data = "";
            userNamesList.clear();
            (activity).setTaskPerformed(0);
            submitCommentIfValid();
        }

    }

    private void submitCommentIfValid() {
        boolean isValid = false;
        Map<String, Object> params = new HashMap<>();

        finalstring_data = "" + etComment.getText().toString();
        for (int i = 0; i < startPositions.size(); i++) {
            finalstring_data = finalstring_data.replace("" + userNamesList.get(i), "@_user_" + userIds.get(i));
            finalstring_data = finalstring_data.replaceAll("  ", " ");
        }
        //String body = StringEscapeUtils.escapeHtml4(etBody.getText().toString());

        if (!TextUtils.isEmpty(finalstring_data)) {
            params.put("body", finalstring_data);
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
            params.put("emoji_id", BaseActivity.getEmotion2().getFileId());
            isEmojiSelected = false;
        }

        if (isGIFSelected) {
            isValid = true;
            params.put("image_id", "" + BaseActivity.gifimageurl);
            isGIFSelected = false;
        }

        closeKeyboard();
        if (isValid) {
            callCreateCommentApi(params);
            showBaseLoader(false);
        } else {
            VibratorUtils.vibrate(context);
            startAnimation(tvPost, Techniques.SHAKE, 400);
            Util.showSnackbar(v, "please enter the comment!");
        }
    }

    private void callFeelingApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;

            try {
                if (req == REQ_LOAD_MORE) {
                    pb.show();
                } else if (commentList.size() == 0) {
                    showBaseLoader(false);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_GET_COMMENT);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }
                // request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.VALUE_RESOURCES_TYPE);
                request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);

                request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                page = (int) request.params.get(Constant.KEY_PAGE);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideAllLoaders();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;

                        CustomLog.e("response_comments", "" + response);
                        if (response != null) {
                            CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);
                            result = comResp.getResult();

                            if (TextUtils.isEmpty(comResp.getError())) {
                                if (null != comResp.getResult().getCommentData()) {
                                    //Collections.reverse(comResp.getResult().getCommentData());
                                    commentList.addAll(comResp.getResult().getCommentData());
                                    adapter.notifyDataSetChanged();
//                                    DbHelper.saveComments(context, actionId, resourceType, comResp.getResult().getCommentData());
//                                    showCacheData(page);
                                }
                                updateUpperlayout();
                                updateFeelingAdapter();
                                showHideCommentLayout();
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                                goIfPermissionDenied(comResp.getError());
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideAllLoaders();
            }
        } else {
            notInternetMsg(v);
        }


    }

    private void hideAllLoaders() {
        isLoading = false;
        pb.hide();
        hideBaseLoader();
    }

    private void updateUpperlayout() {
        try {
            if (null != result.getComments()) {
                List<ReactionPlugin> list = result.getComments().getLikes();
                showView(v.findViewById(R.id.llReactionUpper));
                ((TextView) v.findViewById(R.id.tvLikeUpper)).setText(result.getComments().getLikeStats().getLikes_fluent_list());
                if (list != null) {
                    // rlUpperLike.setVisibility(View.VISIBLE);
                    //  tvLikeUpper.setVisibility(View.VISIBLE);
                    //  tvLikeUpper.setText(vo.getReactionUserData());
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
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showHideCommentLayout() {
        v.findViewById(R.id.llBottom).setVisibility(result.getCanComment() ? View.VISIBLE : View.GONE);
        tvStickerImage.setVisibility(View.GONE);
        tvCameraImage.setVisibility(View.GONE);
        tvVideoImage.setVisibility(View.GONE);

        if (null != result.getAttachmentOptions() && result.getAttachmentOptions().size() > 0) {

            if (result.getAttachmentOptions().contains(Constant.AttachmentOption.EMOTIONS)) {
                tvStickerImage.setVisibility(View.VISIBLE);
            }
            if (result.getAttachmentOptions().contains(Constant.AttachmentOption.PHOTOS)) {
                tvCameraImage.setVisibility(result.getEnable().getAlbum() == 1 ? View.VISIBLE : View.GONE);
            }
            if (result.getAttachmentOptions().contains(Constant.AttachmentOption.VIDEOS)) {
                tvVideoImage.setVisibility(result.getEnable().getVideo() == 1 ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void updateFeelingAdapter() {
        updateScreenTitle(result.getTotal());
        try {
            updateScreenTitle(commentList.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        adapter.setCanReply(result.getReplyComment());
        if (result.getEnable().getIs_gif().equalsIgnoreCase("true")) {
            tvGifImage.setVisibility(View.VISIBLE);
        } else {
            tvGifImage.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_COMMENT);
        v.findViewById(R.id.llNoData).setVisibility(commentList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void updateScreenTitle(int size) {
        tvTitle.setText(size > 0 ? (size == 1 ? Constant.TITLE_COMMENT + " (" + size + ")" : Constant.TITLE_COMMENTS + " (" + size + ")") : Constant.TITLE_COMMENT);
    }

    private void updateImageAttachAdapter() {
        adapterImage.notifyDataSetChanged();
        rvImageAttach.setVisibility(attachmentList.size() > 0 ? View.VISIBLE : View.GONE);

    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.FEED_ATTACH_IMAGE_CANCEL:
                attachmentList.remove(postion);
                updateImageAttachAdapter();
                break;
            case Constant.Events.COMMENT_HEADER_IMAGE:
            case Constant.Events.COMMENT_HEADER_TITLE:
                performClick(commentList.get(postion).getPosterType(), commentList.get(postion).getPosterId(), null, false);
                break;
            case Constant.Events.CLICKED_BODY_TAGGED:
            case Constant.Events.CLICKED_BODY_HASH_TAGGED:
                try {
                    int userid = Integer.parseInt("" + value);
                    performClick(commentList.get(postion).getPosterType(), userid, null, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case Constant.Events.CONTENT_EDIT:
                if (null != value) {
                    callEditCommentAPI((String) value, postion);
                }
                break;
            case Constant.Events.REPLY:
                fragmentManager.beginTransaction().replace(R.id.container,
                        CommentReplyFragment.newInstance(actionId,
                                resourceType,
                                guid,
                                result.getClonedObject(commentList.get(postion))
                                , Boolean.parseBoolean("" + value)))
                        .addToBackStack(null)
                        .commit();
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

            case Constant.Events.REPORT:
                String guid = Constant.ResourceType.COMMENT + "_" + "111";
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.REPORT_COMMENT);
                intent2.putExtra(Constant.KEY_GUID, guid);
                startActivity(intent2);
                break;
          /*  case Constant.Events.LIKED:
                ReactionPlugin reactionVo = SPref.getInstance().getReactionPlugins(context).get(Integer.parseInt("" + value));
                commentList.get(postion).updateReaction(reactionVo);
                adapter.notifyItemChanged(postion);
                callLikeUnlikeApi(commentList.get(postion), reactionVo.getReactionId(), postion);
                // callBottomCommentLikeApi(actionId, resourceType, Constant.URL_MUSIC_LIKE);
                break;*/

            case Constant.Events.DELETE_COMMENT:
                showDeleteDialog(commentList.get(postion).getCommentId(), postion);
                break;

            case Constant.Events.ITEM_COMMENT_VIDEO:
                goTo2(Constant.GoTo.VIDEO, postion, Constant.ACTIVITY_TYPE_VIDEO, 102);

                performClick("" + value, postion, "", false);
                break;
            case Constant.Events.ITEM_COMMENT:
                openLighbox(postion, value.toString());
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

    private void openLighbox(int photoId, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_TYPE, Constant.ACTIVITY_TYPE_ALBUM);
        map.put(Constant.KEY_IMAGE, imageUrl);
        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM_PHOTO);
        fragmentManager.beginTransaction().replace(R.id.container, GallaryFragment.newInstance(map))
                .addToBackStack(null).commit();


    }

    private void callEditCommentAPI(String value, int postion) {

        if (isNetworkAvailable(context)) {
            commentList.get(postion).setBody(value);
            adapter.notifyItemChanged(postion);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_RESOURCES_TYPE, resourceType);
            map.put(Constant.KEY_RESOURCE_ID, actionId);
            map.put(Constant.KEY_BODY, value);
            map.put(Constant.KEY_COMMENT_ID, commentList.get(postion).getCommentId());

            new ApiController(Constant.URL_EDIT_COMMENT, map, context, this, REQ_CODE_EDIT).setExtraKey(postion).execute();
        } else {
            notInternetMsg(v);
        }
    }

    private void callLikeUnlikeApi(final String url, int commentId, final int reactionId, final int position) {

        if (isNetworkAvailable(context)) {
            try {


                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put("subjectid", actionId);
                request.params.put("sbjecttype", resourceType);
                if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }
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

    public void showDeleteDialog(final int commentId, final int position) {
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
                        try {
                            String response = (String) msg.obj;

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
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignore) {

            }
        } else {
            notInternetMsg(v);
        }
    }

    private void callCreateCommentApi(final Map<String, Object> params) {
        CustomLog.d("hasilnyaa", "sukes22");
        CustomLog.d("jarak", String.valueOf(latitdue));

        CustomLog.d("jarak22", String.valueOf(longtitude));

        if (isNetworkAvailable(context)) {
            final boolean[] isDummyCommentAdded = {false};
            try {


                HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_COMMENT);

                request.params.putAll(params);
                request.params.put(Constant.KEY_RESOURCE_ID, actionId);
                request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                request.params.put("longitude", longtitude);
                request.params.put("latitude", latitdue);
                if (null != guid) {
                    request.params.put(Constant.KEY_GUID, guid);
                }

                //request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;
                        CustomLog.e("repsonse1", "" + response);

                        BaseActivity.gifimageurl = "";
                        hideBaseLoader();
                        if (params.containsKey("body")) {
                            UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);

                            finalstring_data = "";
                            isDummyCommentAdded[0] = true;
                            commentList.add(0, new CommentData(
                                    (String) etComment.getText().toString(),
                                    userVo.getDisplayname(),
                                    userVo.getPhotoUrl(),
                                    Util.getCurrentdate(Constant.DATE_FROMAT_FEED)));
                            updateFeelingAdapter();
                            etComment.setText(Constant.EMPTY);
                            recyclerView.smoothScrollToPosition(0);
                        } else {
                            //  showBaseLoader(true);
                        }

                        if (response != null) {
                            // response = response.replace("â\u0080\u0099", "'");
                            BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                            //  result = comResp.getResult();

                            if (TextUtils.isEmpty(comResp.getError())) {
                                hideBaseLoader();
                                attachmentList.clear();
                                updateImageAttachAdapter();
                                String itemComment = new JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString();
                                CommentData vo = new Gson().fromJson(itemComment, CommentData.class);

                                if (isDummyCommentAdded[0]) {
                                    commentList.get(0).updateObject(vo);
                                    updateFeelingAdapter();
                                } else {
                                    etComment.setText(Constant.EMPTY);
                                    finalstring_data = "";
                                    commentList.add(0, vo);
                                    updateFeelingAdapter();
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                            }

                            try {
                                userIds.clear();
                                userNamesList.clear();
                                startPositions.clear();
                                currentCursorPosition = -1;
                                maxHeight = 0;
                                originalStartPosition = -1;
                                startUserNameSearchKeyword = -1;
                                previousCursorPosition = -1;
                                userNameSearchKeyword = "";
                                beforeTextChanged = "";
                                onTextChanged = "";
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
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

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
//        Log.e("getPixelValue", "inDP: " + dimenId);
        //        Log.e("getPixelValue", "inPixelValue: " + dp);

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics());

    }

    private void callSuggestionApi(String value) {
        if (TextUtils.isEmpty(value)) {
            if (null != usersList)
                usersList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SUGGEST);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_VALUE, value);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (null != usersList)
                                        usersList.clear();
                                    List<Friends> list = resp.getResult().getFriends();
                                    if (null != list && list.size() > 0) {
                                        usersList.addAll(list);
                                        isSearching = false;
                                        showPopup();
                                    } else {
                                        dismissPopup();
                                    }
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                            // dialog.dismiss();
                            return true;
                        }
                    };
                    requestHandler = new HttpRequestHandler(activity, new Handler(callback));
                    requestHandler.execute(request);

                } catch (Exception e) {

                }
            } else {
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void showPopup() {
        rlUsersList.setVisibility(View.VISIBLE);

        if (usersList.isEmpty()) {
            if (isSearching) {
                progress_bar.setVisibility(View.VISIBLE);
                txtMsg.setVisibility(View.GONE);
            } else {
                progress_bar.setVisibility(View.GONE);
                txtMsg.setVisibility(View.VISIBLE);
            }
            recyclerViewuser.setVisibility(View.GONE);
        } else {
            recyclerViewuser.setVisibility(View.VISIBLE);
            progress_bar.setVisibility(View.GONE);
            txtMsg.setVisibility(View.GONE);

            int recyclerViewHeight = usersList.size() * getPixelValue(getContext(), 28);

            int allowedHeight = maxHeight - getPixelValue(getContext(), 120);

            ViewGroup.LayoutParams params = recyclerViewuser.getLayoutParams();

            if (recyclerViewHeight > allowedHeight) {
                params.height = allowedHeight;
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            recyclerViewuser.setLayoutParams(params);

            UserListAdapter adapter = new UserListAdapter(usersList, (v, position) -> {

                try {
                    String text = Objects.requireNonNull(etComment.getText()).toString();
                    String startText = text.substring(0, originalStartPosition);
                    startText = startText.concat("  ");
                    String userName = usersList.get(position).getLabel();
                    String endText = text.substring(currentCursorPosition);

                    int positionToChange = -1;

                    for (int i = 0; i < startPositions.size(); i++) {
                        if (startPositions.get(i) >= currentCursorPosition) {
                            if (positionToChange == -1)
                                positionToChange = i;
                            startPositions.set(i, startPositions.get(i) + userName.length() + 4);
                        }
                    }

                    if (positionToChange == -1) {
                        startPositions.add(startText.length());
                        userNameLengths.add(userName.length());
                        userIds.add(usersList.get(position).getId());
                        userNamesList.add("" + usersList.get(position).getLabel());
                    } else {
                        startPositions.add(positionToChange, startText.length());
                        userNameLengths.add(positionToChange, userName.length());
                        userIds.add(positionToChange, usersList.get(position).getId());
                        userNamesList.add(positionToChange, "" + usersList.get(position).getLabel());
                    }
                    startUserNameSearchKeyword = -1;
                    originalStartPosition = -1;
                    userNameSearchKeyword = "";

                    String displayText;

                    if (endText.isEmpty())
                        displayText = startText + userName + "   ";
                    else
                        displayText = startText + userName + "  " + endText;
                    currentCursorPosition = startText.length() + userName.length() + 2;

                    SpannableStringBuilder str = new SpannableStringBuilder(displayText);
                    SpannableStringBuilder spannableStringBuilder = getUserTagSpan(str);

                    Log.e("string data", "" + str);
                    etComment.setText(spannableStringBuilder);
                    etComment.setSelection(currentCursorPosition);
                    dismissPopup();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dismissPopup();
                }

            });
            RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerViewuser.setLayoutManager(linearLayoutManager);
            recyclerViewuser.setItemAnimator(new DefaultItemAnimator());
            recyclerViewuser.setAdapter(adapter);
        }
    }

    private void dismissPopup() {
        rlUsersList.setVisibility(View.GONE);
    }

    private SpannableStringBuilder getUserTagSpan(SpannableStringBuilder str) {
        for (int i = 0; i < startPositions.size(); i++) {
            str.setSpan(new StyleSpan(Typeface.BOLD), startPositions.get(i), startPositions.get(i) + userNameLengths.get(i), Spannable.SPAN_INTERMEDIATE);
            str.setSpan(new RoundedBackgroundSpan(getContext()), startPositions.get(i), startPositions.get(i) + userNameLengths.get(i), Spannable.SPAN_INTERMEDIATE);
        }
        return str;
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


}
