package com.sesolutions.ui.poll_core;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.responses.poll.PollResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.SpeakableContent;
import com.sesolutions.ui.common.SpeakableContent_basic;
import com.sesolutions.ui.customviews.fab.FloatingActionButton;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CViewPollFragment extends SpeakableContent_basic implements View.OnClickListener, OnUserClickedListener<Integer, Object>, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    private static final int REQ_DELETE = 300;
    private static final int REQ_CLOSE = 302;
    private static final int VOTE_RESULT = 301;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    public View v;
    public CPollParentFragment parent;
    public int categoryId;
    private Poll poll;
    String poll_images="";
    public List<PollOption> optionList;
    public CPollOptionAdapter adapter;
    public CommentLike.Stats stats;

    public String searchKey;
    // private AppCompatTextView tvQuestion;
    public int loggedinId;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOption;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public String resourceType;
    public int resourceId;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public PollResponse.Result result;
    private int pollId;
    private boolean openComment;
    private boolean isLoggedIn;
    private String selectedModule;
    private boolean isShowingQuestion;
    private FloatingActionButton fabQuestion;
    ImageView ivImage;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_CORE_POLL) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }

    public static CViewPollFragment newInstance(int pollId) {
        CViewPollFragment frag = new CViewPollFragment();
        frag.pollId = pollId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_poll_view_core, container, false);
        applyTheme(v);
        initScreenData();
        callBottomCommentLikeApi(pollId, Constant.ResourceType.VIEW_CORE_POLL, Constant.URL_VIEW_COMMENT_LIKE);
        callMusicAlbumApi(1);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(pollId, Constant.ResourceType.VIEW_CORE_POLL);
        }
        return v;
    }


    public void init() {
        try {
//            callBottomCommentLikeApi(pollId, Constant.ResourceType.VIEW_CORE_POLL, Constant.URL_VIEW_COMMENT_LIKE);
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageResource(R.drawable.vertical_dots);
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_POLLS);
            rvOption = v.findViewById(R.id.rvOption);

            fabQuestion = v.findViewById(R.id.fabQuestion);
            ivImage = v.findViewById(R.id.ivImage22);
            fabQuestion.setOnClickListener(this);
            fabQuestion.setFabColor(Color.parseColor(Constant.colorPrimary));
            fabQuestion.setFabSize(FloatingActionButton.FAB_SIZE_MINI);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            v.findViewById(R.id.llLike).setOnClickListener(this);
            v.findViewById(R.id.llFavorite).setVisibility(View.GONE);
            v.findViewById(R.id.llComment).setOnClickListener(this);
            if (null != poll) {
                updateView();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setRecyclerView() {
        try {
            optionList = new ArrayList<>();
            rvOption.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvOption.setLayoutManager(layoutManager);
            adapter = new CPollOptionAdapter(optionList, context, this, -1);
            rvOption.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.llLike:
                    callLikeApi(REQ_LIKE, Constant.URL_POLL_VIEW_COMMENT_LIKE);
//                    addUpperTabItems();
                   // updateItemLikeFavorite(REQ_LIKE);
//                    updateAdapter();
                   // onRefresh();
                    break;
//                case R.id.llFavorite:
//                    callLikeApi(REQ_FAVORITE, URL_FAVORITE);
//                    break;
                case R.id.llComment:
                    goToCommentFragment(poll.getPollId(), Constant.ResourceType.VIEW_CORE_POLL);
                    break;
                case R.id.fabQuestion:
                    isShowingQuestion = !isShowingQuestion;
                    updateQuestionText();
                    updateAdapter();
                    break;
                case R.id.ivSearch:
                    showPopup(result.getOptions(), v, 100, this);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_POLL_VIEW);
                    request.params.put(Constant.KEY_POLL_ID, pollId);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                PollResponse resp = new Gson().fromJson(response, PollResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    result = resp.getResult();
                                    if (null != resp.getResult().getPoll()) {
                                        poll = resp.getResult().getPoll();
                                        try {
                                            poll_images = resp.getResult().getPollstimage().getMain();
                                        }
                                        catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                       updateView();
                                    }
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    goIfPermissionDenied(resp.getError());
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

                    hideLoaders();

                }

            } else {
                hideLoaders();
                // setRefreshing(swipeRefreshLayout, false);

                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
        }
    }

    private void addUpperTabItems() {
        View llReaction = v.findViewById(R.id.llReaction);

        if (!SPref.getInstance().isLoggedIn(context)) {
            v.findViewById(R.id.llLike).setVisibility(View.GONE);
        }
        llReaction.setVisibility(View.VISIBLE);
        int color = Color.parseColor(Constant.text_color_1);


        ((TextView) v.findViewById(R.id.tvComment)).setText(R.string.comment);
        ((ImageView) v.findViewById(R.id.tvImageComment)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.comment));
        ((TextView) v.findViewById(R.id.tvComment)).setTextColor(color);


    }

    public void hideLoaders() {
        isLoading = false;
        hideBaseLoader();
        setRefreshing(swipeRefreshLayout, false);
        swipeRefreshLayout.setEnabled(false);
    }

    private void updateQuestionText() {
        // updateFabColor(Color.parseColor(Constant.navigationTitleColor));

        fabQuestion.setFabIcon(ContextCompat.getDrawable(context, isShowingQuestion ? R.drawable.poll_result : R.drawable.poll_question));
        fabQuestion.setFabIconColor(Color.parseColor(Constant.navigationTitleColor));
        //tvQuestion.setText(isShowingQuestion ? getStrings(R.string.show_result) : getStrings(R.string.poll_show_question));
    }

    private void updateView() {
        ((TextView) v.findViewById(R.id.tvPollTitle)).setText(poll.getTitle());
        if(poll.getOwnerTitle().length()>0){
            ((TextView) v.findViewById(R.id.tvAuthor)).setText("by " + poll.getOwnerTitle());
            ((TextView) v.findViewById(R.id.tvAuthor)).setVisibility(View.VISIBLE);
        }else {
            ((TextView) v.findViewById(R.id.tvAuthor)).setVisibility(View.GONE);
        }
       ((TextView) v.findViewById(R.id.tv_Views)).setText("" + poll.getViewCount());
        ((TextView) v.findViewById(R.id.tv_votes)).setText("" + poll.getVoteCount());
        ((TextView) v.findViewById(R.id.tv_like)).setText("" + poll.getLikeCount());
        ((TextView) v.findViewById(R.id.tv_comment)).setText("" + poll.getCommentCount());
        ((TextView) v.findViewById(R.id.tvDesc)).setText(poll.getDescription());


        Util.showImageWithGlide(ivImage, poll_images, context, R.drawable.placeholder_square);
        isShowingQuestion = !poll.hasVoted();
        v.findViewById(R.id.ivSearch).setVisibility(null != result.getOptions() && result.getOptions().size() > 0 ? View.VISIBLE : View.GONE);
        updateQuestionText();

        addUpperTabItems();
        hideShowFabQuestion();
        updateAdapter();

    }

    private void hideShowFabQuestion() {
        fabQuestion.setVisibility(poll.getIsClosed() == 0 ? View.VISIBLE : View.GONE);
        if (poll.getIsClosed() != 0) {
            isShowingQuestion = false;
        }

    }

    private void updateAdapter() {
        if (null != poll.getOptions()) {
            optionList.clear();
            optionList.addAll(poll.getOptions());
            wasListEmpty = true;
            adapter.showQuestion(isShowingQuestion);
            adapter.setPoll(poll);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(rvOption);
        } else {

        }
    }

    private void callLikeApi(final int REQ_CODE, String url) {

        if (isNetworkAvailable(context)) {
//            this.resourceId = resourceId;
//            this.resourceType = resourceType;
            if (REQ_CODE == REQ_DELETE) {
                showBaseLoader(false);
            }
           // updateItemLikeFavorite(REQ_CODE);
            try {

                HttpRequestVO request = new HttpRequestVO(url);

//                request.params.put(Constant.KEY_POLL_ID, pollId);
                //request.params.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_RESOURCE_ID, pollId);
                request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.VIEW_CORE_POLL);
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
                                if (REQ_CODE == REQ_DELETE) {
                                    Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                    activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                    onBackPressed();
                                }else {
                                    try {
                                        if (!(new JSONObject(response).get("result") instanceof String)) {
                                            CommentLike resp = new Gson().fromJson(response, CommentLike.class);
                                            if (null != resp.getResult()) {
                                                updateItemLikeFavorite22(resp.getResult().isLike());
                                            }
                                        }
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }

                            } else {
                                //revert changes in case of error
                                try {
                                    if (!(new JSONObject(response).get("result") instanceof String)) {
                                        CommentLike resp = new Gson().fromJson(response, CommentLike.class);
                                        if (null != resp.getResult()) {
                                            updateItemLikeFavorite22(resp.getResult().isLike());
                                        }
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                                Util.showSnackbar(v, err.getErrorMessage());
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
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }

    }

    public void handleResponse(String response) {
        try {
            if (!(new JSONObject(response).get("result") instanceof String)) {
                CommentLike resp = new Gson().fromJson(response, CommentLike.class);
                if (null != resp.getResult() && null != resp.getResult().getStats()) {
                    stats = resp.getResult().getStats();
                    updateItemLikeFavorite22();
                }
            }
        } catch (JSONException e) {
            CustomLog.e(e);
        }
    }

    public void updateItemLikeFavorite22() {
              Log.e("Like","Like");
              ((TextView) v.findViewById(R.id.tvLike)).setText(stats.getIsLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
              ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
               ((TextView) v.findViewById(R.id.tvLike)).setTextColor(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
    }

    public void updateItemLikeFavorite(int REQ_CODE) {
        Log.e("APILike:",""+REQ_LIKE);
        if (REQ_CODE == REQ_LIKE) {
            poll.setContentLike(stats.getIsLike());
            ((TextView) v.findViewById(R.id.tvLike)).setText(stats.getIsLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
            ((TextView) v.findViewById(R.id.tvLike)).setTextColor(Color.parseColor(stats.getIsLike() ? Constant.colorPrimary : Constant.text_color_1));
        }
    }

    public void updateItemLikeFavorite22(boolean isflag) {
        Log.e("Like","Like");
        ((TextView) v.findViewById(R.id.tvLike)).setText(isflag? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
        ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(isflag ? Constant.colorPrimary : Constant.text_color_1));
        ((TextView) v.findViewById(R.id.tvLike)).setTextColor(Color.parseColor(isflag ? Constant.colorPrimary : Constant.text_color_1));
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        switch (object1) {
            case Constant.Events.IMAGE_1:
                // if (TYPE_IMAGE.equals(object2)) {
                if (object2 instanceof ImageView) {
                    // CommonVO vo=videoList.get(position);
                    openSinglePhotoFragment((ImageView) object2, "http://pagestd.socialenginesolutions.com/public/contest/3e/b1/3adaf48fd4a75e447b62b1d4239feafb.jpg", "tagname" + position);
                }
                break;
            case Constant.Events.MORE_MEMBER:
                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_MODULE, selectedModule);
                bundle.putString(Constant.KEY_TITLE, getStrings(R.string.voted_user));
                bundle.putInt(Constant.KEY_POLL_ID, optionList.get(position).getPollOptionId());
                fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
                break;
            case Constant.Events.VOTE:
                try {
                    if (SPref.getInstance().isLoggedIn(context)) {
                        if (isNetworkAvailable(context)) {

                            Map<String, Object> request = new HashMap<>();
                            request.put(Constant.KEY_POLL_ID, poll.getPollId());
                            request.put("token", poll.getToken());
                            request.put("option_id", poll.getOptions().get(position).getPollOptionId());

                            new ApiController(Constant.URL_POLL_VOTE, request, context, this, VOTE_RESULT).setExtraKey(position).execute();

                        } else {
                            Util.showSnackbar(v, "No Internet..");
                        }
                    } else {
//                        Util.showSnackbar(v,"You need to Log in First..");
                    //    fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment()).addToBackStack(null).commit();
                        fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                                .addToBackStack(null)
                                .commit();

                    }
                    break;
                } catch (Exception ignore) {

                }
            case REQ_CLOSE:

                try {
                    JSONObject resp = new JSONObject("" + object2);
                    String message = resp.getJSONObject("result").getString("message");
                    Util.showSnackbar(v, message);
                    onRefresh();
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                }

                break;

            case VOTE_RESULT:
                try {
                    JSONObject resp = new JSONObject("" + object2);
                    String token = resp.getJSONObject("result").getString("token");
                    int votesTotal = resp.getJSONObject("result").getInt("votes_total");
                    poll.setToken(token);
                    poll.setHasVoted(true);
                    poll.setHasVotedId(optionList.get(position).getPollOptionId());
                    poll.setVoteCount(votesTotal);
                    JSONArray votesCount = resp.getJSONObject("result").getJSONArray("vote_detail");
                    try {
                        //update votes count
                        for (int i = 0; i < votesCount.length(); i++) {
                            optionList.get(i).setVotePercent(votesCount.getString(i));
                            optionList.get(i).setVotes(Integer.parseInt(votesCount.getString(i).split(" ")[0]));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    CustomLog.e(e);
//                    somethingWrongMsg(v);
                    onRefresh();
                }

                adapter.setPoll(poll);
                isShowingQuestion = !isShowingQuestion;
                adapter.showQuestion(isShowingQuestion);
                adapter.notifyDataSetChanged();
                break;
        }
        return false;
    }


    public void goToFormFragment(int pollId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_POLL_ID, pollId);
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_CORE_POLL, map, Constant.URL_POLL_EDIT)).addToBackStack(null).commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Options opt = null;
        int itemId = item.getItemId();
        //if (itemId > 100) {
        itemId = itemId - 100;
        opt = result.getOptions().get(itemId - 1);
        // }

        switch (opt.getName()) {
            case Constant.OptionType.EDIT_PRIVACY:
                goToFormFragment(pollId);
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog2();
                break;
            case Constant.OptionType.SHARE:
                showShareDialog(Constant.TXT_SHARE_FEED);
//                showShareDialog(result.getPoll().getShare(Constant.ResourceType.VIEW_CORE_POLL));
                break;

            case Constant.OptionType.REPORT:
                if (SPref.getInstance().isLoggedIn(context)) {
                    goToReportFragment(Constant.ResourceType.VIEW_CORE_POLL + "_" + pollId);
                } else {
                    Util.showSnackbar(v, "Login or Signup to continue..");
                }
                break;
        }
        return false;
    }


    private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareInside(poll.getShare(Constant.ResourceType.VIEW_CORE_POLL), true);

                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(poll.getShare2());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showDeleteDialog2() {
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_POLL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi2();
                    //callSaveFeedApi( Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

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


    private void callDeleteApi2() {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    //  HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "album/delete/" + albumId + Constant.POST_URL);
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
                                        activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                        onBackPressed();
                                        Util.showSnackbar(v, "Poll Deleted Successfully.");
                                        // Util.showSnackbar(v, new JSONObject(response).getString("result"));
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

    public void showDeleteDialog() {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_POLL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(view -> {
                progressDialog.dismiss();
                if (isNetworkAvailable(context)) {
                    Map<String, Object> request = new HashMap<>();
                    request.put(Constant.KEY_POLL_ID, pollId);
                    new ApiController(Constant.URL_POLL_DELETE, request, context, CViewPollFragment.this, REQ_DELETE).execute();
//                    Util.showSnackbar(v, "Successfully Deleted");
                    Deletedgoback(v);
                } else {
                    notInternetMsg(v);
                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            swipeRefreshLayout.setEnabled(true);
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
            callBottomCommentLikeApi(pollId, Constant.ResourceType.VIEW_CORE_POLL, Constant.URL_VIEW_COMMENT_LIKE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
