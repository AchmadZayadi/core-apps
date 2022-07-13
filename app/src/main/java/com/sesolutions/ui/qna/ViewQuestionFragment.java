package com.sesolutions.ui.qna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.responses.qna.QAResponse;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.editor.EditorExampleActivity;
import com.sesolutions.ui.video.VideoViewActivity;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewQuestionFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object>, PopupMenu.OnMenuItemClickListener {
    private final int VOTE_RESULT = -301;
    private final int REQ_EDITOR = 103;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_QUESTION_DELETE = 101;
    private final int REQ_ANSWER_DELETE = 102;
    private View v;
    private int mQuestionId;
    private TextView tvShowQuestion;
    private RecyclerView recyclerView;
    private AnswerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Question> answerList;
    private BottomSheetBehavior<View> mBottomSheetOptions;
    private Question vo;

   /* public static ViewQuestionFragment newInstance(int id) {
        ViewQuestionFragment frag = new ViewQuestionFragment();
        frag.mQuestionId = id;
        return frag;
    }*/

    public static ViewQuestionFragment newInstance(int id, Question vo) {
        ViewQuestionFragment frag = new ViewQuestionFragment();
        frag.mQuestionId = id;
        frag.vo = vo;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_QA) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_qna, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    @Override
    public void initScreenData() {
        init();
    }

    ImageView ivVoteUp, ivVoteDown;
    TextView tvVoteCount;

    private void init() {
        v.findViewById(R.id.main).setVisibility(View.INVISIBLE);
        if (vo == null) {
            v.findViewById(R.id.main).setVisibility(View.INVISIBLE);
        }
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.bBSHeaderAdd).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerview);
        ivVoteUp = v.findViewById(R.id.ivVoteUp);

        ivVoteDown = v.findViewById(R.id.ivVoteDown);
        tvVoteCount = v.findViewById(R.id.tvVoteCount);
        ivVoteDown.setOnClickListener(this);
        ivVoteUp.setOnClickListener(this);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvShowQuestion = v.findViewById(R.id.tvShowQuestion);
        v.findViewById(R.id.tvShowQuestion).setOnClickListener(this);
        v.findViewById(R.id.ivShare).setOnClickListener(this);
        v.findViewById(R.id.ivOption).setOnClickListener(this);

        setBottomSheets();
       /* if (null != vo) {
            setData();
        }*/
        callApi(1);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        callApi(Constant.REQ_CODE_REFRESH);
    }

    private void setData() {
        try {

            TextView tvStats;
            //final TextView tvOwner, tvViewCount, tvAnswerCount, tvVoteCount, tvTotalVote;
            final FlowLayout flTags = v.findViewById(R.id.flTags);


            ((TextView) v.findViewById(R.id.tvTitle)).setText(vo.getTitle());
            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            tvStats = ((TextView) v.findViewById(R.id.tvStats));
            tvStats.setTypeface(iconFont);
            String detail = "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf00c " + vo.getFollowCount()
                    + "  \uf06e " + vo.getViewCount()
                    + "  \uf0a6 " + vo.getTotalVoteCount();
            tvStats.setText(detail);
            //((TextView) v.findViewById(R.id.tvVoteCount)).setText(vo.getVoteCount());
            if (vo.getMediaType() == 1) {
                v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivMediaType).setVisibility(View.GONE);
            } else if (vo.getMediaType() == 2) {
                v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rlMedia).setOnClickListener(this);
            } else {
                v.findViewById(R.id.rlMedia).setVisibility(View.GONE);
            }

            Util.showAnimatedImageWithGlide(v.findViewById(R.id.ivQImage), vo.getQuestionPhoto(), context);
            SpanUtil.createHashTagView(getLayoutInflater(), flTags, vo.getTag(), this);
            ((TextView) v.findViewById(R.id.tvBSHeader)).setText(context.getResources().getQuantityString(R.plurals.answers_count_view_page, vo.getAnswerCount(), vo.getAnswerCount()));

            ((TextView) v.findViewById(R.id.tvQTitle)).setText(vo.getTitle());
            ((TextView) v.findViewById(R.id.tvQDescription)).setText(SpanUtil.getHtmlString(vo.getDescription()));
            ((TextView) v.findViewById(R.id.tvOwner)).setText(vo.getOwnerText(context));
            isShowingQuestion = (null == vo.getHasVotedId());
            updateVoteArrows();
            updateTextofButton();
            updateToolbarIcons();
            v.findViewById(R.id.main).setVisibility(View.VISIBLE);
            addUpperTabItems();
        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
            onBackPressed();
        }
    }

    private void updateVoteArrows() {
        tvVoteCount.setText(vo.getVoteCount());
        ivVoteUp.setColorFilter(vo.hasVoted(Constant.KEY_UP_VOTED) ? SesColorUtils.getPrimaryColor(context) : SesColorUtils.getText2Color(context));
        ivVoteDown.setColorFilter(vo.hasVoted(Constant.KEY_DOWN_VOTED) ? SesColorUtils.getPrimaryColor(context) : SesColorUtils.getText2Color(context));
    }

    private void updateTextofButton() {
        //tvShowQuestion.setVisibility(null != vo.getHasVotedId() ? View.VISIBLE : View.GONE);
        tvShowQuestion.setText(isShowingQuestion ? R.string.show_result : R.string.poll_show_question);
    }

    private void updateToolbarIcons() {
        v.findViewById(R.id.ivShare).setVisibility(null != vo.getShare() ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.ivOption).setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
    }

    private void callApi(int REQ) {
        if (isNetworkAvailable(context)) {
            if (REQ == 1)
                showBaseLoader(false);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_ID, mQuestionId);
            new ApiController(Constant.URL_QA_VIEW, map, context, this, -1).setExtraKey(REQ).execute();
        } else {
            noInternetGoBack(v);
        }
    }

    private View llBottomSheet;

    private void setBottomSheets() {
        v.findViewById(R.id.cvBSHeader).setOnClickListener(this);
        llBottomSheet = v.findViewById(R.id.llBottomSheet);
        mBottomSheetOptions = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetOptions.setHideable(false);

        mBottomSheetOptions.setPeekHeight(context.getResources().getDimensionPixelSize(R.dimen.height_qa_bottom_sheet));
        mBottomSheetOptions.setBottomSheetCallback(bottomSheetListener);
        mBottomSheetOptions.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setPollRecyclerView();
        setRecyclerView();
    }

    public void setRecyclerView() {
        try {
            answerList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new AnswerAdapter(answerList, context, this);
            recyclerView.setAdapter(adapter);
            //recyclerView.setNestedScrollingEnabled(false);
            // swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            //swipeRefreshLayout.setEnabled(false);
            //swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateAnswerAdapter() {
        adapter.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_answer);
        v.findViewById(R.id.llNoData).setVisibility(answerList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivOption:
                showPopup(vo.getOptions(), view, 10, this);
                break;
            case R.id.ivShare:
                showShareDialog(vo.getShare());
                break;
            case R.id.cvBSHeader:
                if (BottomSheetBehavior.STATE_COLLAPSED == mBottomSheetOptions.getState()) {
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.rlMedia:
                String iFrame = vo.getCode();
                if (null != iFrame) {
                    iFrame = iFrame.replace("//cdn.iframe", "https://cdn.iframe");
                }
                openVideoView(iFrame);
                break;
            case R.id.bBSHeaderAdd:
                startEditorActivity(-1, "");
                break;
            case R.id.tvShowQuestion:
                isShowingQuestion = !isShowingQuestion;
                updateTextofButton();
                adapterPoll.showQuestion(isShowingQuestion);
                adapterPoll.setPoll(vo);
                adapterPoll.notifyDataSetChanged();
                break;

            case R.id.ivVoteUp:
                callUpDownVoting(Constant.KEY_UP_VOTED);
                break;

            case R.id.ivVoteDown:
                callUpDownVoting(Constant.KEY_DOWN_VOTED);
                break;
        }
    }

    private void callUpDownVoting(String voteType) {
        if (!vo.hasVoted(voteType))
            if (isNetworkAvailable(context)) {

                Map<String, Object> map = vo.getGuidMap(voteType, new HashMap<>());
                map.put("userguid", Constant.ResourceType.USER + "_" + SPref.getInstance().getLoggedInUserId(context));
                map.put(Constant.KEY_TYPE, voteType);
                new ApiController(Constant.URL_QA_VOTE_UP_DOWN, map, context, this, -2).execute();
                updateVoteArrows();
            } else {
                notInternetMsg(v);
            }
    }

    private void addUpperTabItems() {
        if (!SPref.getInstance().isLoggedIn(context)) return;

        //add post item
        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        //add favourite item
        if (vo.canLike()) {

            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(vo.isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(vo.isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> {
                callLikeApi(REQ_LIKE, view1, Constant.URL_QA_LIKE, true);
            });
            llTabOptions.addView(view1);
        }

        //add favourite item
        if (vo.canFavourite()) {
            final View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(vo.isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view3, Constant.URL_QA_FAVORITE, true));
            llTabOptions.addView(view3);
        }

        //add Follow item
        if (/*mUserId != vo.getOwner_id() && */vo.canFollow()) { //don't show follow button to Page Owner

            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(vo.isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, vo.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setColorFilter(vo.isContentFollow() ? ContextCompat.getColor(context, R.color.sky_blue) : color);
            //((ImageView) view2.findViewById(R.id.ivOptionImage)).clearColorFilter();
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> {

                callLikeApi(REQ_FOLLOW, view2, Constant.URL_QA_FOLLOW, true);
            });
            llTabOptions.addView(view2);
        }

        //add comment item
        // if (SPref.getInstance().getLoggedInUserId(context) > 0) {
        final View view = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
        ((TextView) view.findViewById(R.id.tvOptionText)).setText(R.string.comment);
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.comment));
        ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(color);
        ((TextView) view.findViewById(R.id.tvOptionText)).setTextColor(color);
        view.setOnClickListener(v -> goToCommentFragment(mQuestionId, Constant.ResourceType.QA));
        llTabOptions.addView(view);
        //}
    }

    private void callLikeApi(final int REQ_CODE, final View view, String url, boolean showAnimation) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, vo, showAnimation);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mQuestionId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.QA);
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

                                    } else {
                                        //revert changes in case of error
                                        updateItemLikeFavorite(REQ_CODE, view, vo, false);
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
        }
    }

    public void updateItemLikeFavorite(int REQ_CODE, View view, Question vo, boolean showAnimation) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(vo.isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            //((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, vo.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.followBlue : Constant.text_color_1));
            if (showAnimation)
                ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(vo.isContentFollow() ? R.string.unfollow : R.string.follow);
        }

    }


    private BottomSheetBehavior.BottomSheetCallback bottomSheetListener = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int state) {
            switch (state) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    //((CardView) view.findViewById(R.id.cvBSHeader)).setCardBackgroundColor(SesColorUtils.getPrimaryColor(context));
                    // ((TextView) view.findViewById(R.id.tvBSHeader)).setTextColor(SesColorUtils.getNavigationTitleColor(context));
                    //((AppCompatButton) view.findViewById(R.id.bBSHeaderAdd)).setTextColor(SesColorUtils.getPrimaryColor(context));
                    // ((AppCompatButton) view.findViewById(R.id.bBSHeaderAdd)).setBackgroundColor(SesColorUtils.getNavigationTitleColor(context));
                    break;

                case BottomSheetBehavior.STATE_EXPANDED:
                    //((CardView) view.findViewById(R.id.cvBSHeader)).setCardBackgroundColor(SesColorUtils.getNavigationTitleColor(context));
                    // ((TextView) view.findViewById(R.id.tvBSHeader)).setTextColor(SesColorUtils.getPrimaryColor(context));
                    // ((AppCompatButton) view.findViewById(R.id.bBSHeaderAdd)).setTextColor(SesColorUtils.getNavigationTitleColor(context));
                    // ((AppCompatButton) view.findViewById(R.id.bBSHeaderAdd)).setBackgroundColor(SesColorUtils.getPrimaryColor(context));
                    break;
                case BottomSheetBehavior.STATE_DRAGGING:
                case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    break;

                case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                case BottomSheetBehavior.STATE_SETTLING:
                    CustomLog.e("llSongOptions", "" + state);
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {
        }
    };

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {

            case Constant.Events.COMMENT:
                goToCommentFragment(answerList.get(position).getAnswerId(), Constant.ResourceType.ANSWER);
                break;
            case -4:
                hideBaseLoader();
                try {
                    String response = (String) data;
                    if (response != null) {
                        QAResponse err = new Gson().fromJson(response, QAResponse.class);
                        if (err.isSuccess()) {
                            if (null != err.getResult().getAnswer()) {
                                answerList.add(err.getResult().getAnswer());
                            }
                            if (position < 0) {
                                updateAnswerAdapter();
                                mBottomSheetOptions.setState(BottomSheetBehavior.STATE_EXPANDED);
                                recyclerView.scrollToPosition(answerList.size() - 1);
                            }
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                            goIfPermissionDenied(err.getError());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                }

                break;
            case -1:
                hideBaseLoader();
                swipeRefreshLayout.setRefreshing(false);
                try {
                    String response = (String) data;
                    if (response != null) {
                        QAResponse err = new Gson().fromJson(response, QAResponse.class);
                        if (err.isSuccess()) {

                            if (position == Constant.REQ_CODE_REFRESH) {
                                answerList.clear();
                                optionList.clear();
                            }
                            vo = err.getResult().getQuestion();
                            adapterPoll.setPoll(vo);
                            setData();
                            if (null != err.getResult().getAnswers()) {
                                answerList.addAll(err.getResult().getAnswers());
                            }
                            if (null != err.getResult().getQuestionOptions()) {
                                tvShowQuestion.setVisibility(View.VISIBLE);
                                isShowingQuestion = vo.getVotedOptionId() == 0;
                                optionList.addAll(err.getResult().getQuestionOptions());
                                adapterPoll.showQuestion(isShowingQuestion);
                                adapterPoll.notifyDataSetChanged();
                            }
                            updateAnswerAdapter();
                        } else {
                            Util.showToast(context, err.getErrorMessage());
                            goIfPermissionDenied(err.getError());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                }

                break;
            case REQ_ANSWER_DELETE:
            case REQ_QUESTION_DELETE:
                hideBaseLoader();
                try {
                    String response = (String) data;
                    if (response != null) {
                        SuccessResponse err = new Gson().fromJson(response, SuccessResponse.class);
                        if (err.isSuccess()) {
                            Util.showSnackbar(v, err.getResult().getSuccessMessage());
                            activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                            onBackPressed();
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                }

                break;
            case Constant.Events.ACCEPT:
                //only owner can mark answer as best answer
                if (answerList.get(position).getOwnerId() == SPref.getInstance().getLoggedInUserId(context))
                    if (isNetworkAvailable(context)) {
                        for (Question vo : answerList) {
                            vo.setBestAnswer(0);
                        }
                        Map<String, Object> map = answerList.get(position).setAsBestAns(new HashMap<>());
                        new ApiController(Constant.URL_QA_MARK_BEST, map, context, this, -3).setExtraKey(position).execute();

                        adapter.notifyItemChanged(position);
                    } else {
                        notInternetMsg(v);
                    }
                break;
            case Constant.Events.OK:
                if (isNetworkAvailable(context)) {

                    Map<String, Object> map = new HashMap<>();
                    String URL;
                    int REQ;
                    if (position > -1) {

                        map.put(Constant.KEY_ANSWER_ID, answerList.get(position).getAnswerId());
                        URL = Constant.URL_QA_DELETE_ANSWER;
                        REQ = REQ_ANSWER_DELETE;
                        answerList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, answerList.size());
                    } else /*if (position == REQ_QUESTION_DELETE)*/ {
                        showBaseLoader(false);
                        map.put(Constant.KEY_ID, mQuestionId);
                        URL = Constant.URL_QA_DELETE;
                        REQ = REQ_QUESTION_DELETE;
                    }
                    new ApiController(URL, map, context, this, REQ).execute();
                } else {
                    notInternetMsg(v);
                }
                break;
            case Constant.Events.PROFILE:
                goToProfileFragment(position);
                break;

            case Constant.Events.VOTE:
                //if data is null then call its 'VOTE_FOR_POLL_OPTION' otherwise its 'UP_VOTE' or 'DOWN_VOTE;
                if (null != data) {
                    if (!answerList.get(position).hasVoted("" + data))
                        if (isNetworkAvailable(context)) {
                            adapter.notifyItemChanged(position);
                            Map<String, Object> map = answerList.get(position).getAnsGuidMap("" + data, new HashMap<>());
                            map.put("userguid", Constant.ResourceType.USER + "_" + SPref.getInstance().getLoggedInUserId(context));
                            map.put(Constant.KEY_TYPE, data);
                            new ApiController(Constant.URL_QA_VOTE_UP_DOWN, map, context, this, -2).setExtraKey(position).execute();
                        } else {
                            notInternetMsg(v);
                        }
                } else {
                    callPollSubmitAPI(position);
                }
                break;
            case VOTE_RESULT:
                try {
                    JSONObject resp = new JSONObject("" + data);
                    //String token = resp.getJSONObject("result").getString("token");
                    int votesTotal = resp.getJSONObject("result").getInt("votes_total");

                    //vo.setHasVotedId(optionList.get(position).getPollOptionId());
                    vo.setVotedOptionId(optionList.get(position).getPollOptionId());
                    vo.setTotalVote(votesTotal);
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
                    somethingWrongMsg(v);
                    onRefresh();
                }

                adapterPoll.setPoll(vo);
                isShowingQuestion = !isShowingQuestion;
                adapterPoll.showQuestion(isShowingQuestion);
                adapterPoll.notifyDataSetChanged();
                break;
            case -2:
                handleVoteResponse(position, (String) data);
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                vo = answerList.get(Integer.parseInt("" + data));
                //get clicked option
                Options opt = vo.getOptions().get(position);

                //open share dialog if share clicked
                switch (opt.getName()) {
                    case Constant.OptionType.DELETE:
                        showDeleteDialog(this, Integer.parseInt("" + data), getString(R.string.msg_delete_answer));
                        break;
                    case Constant.OptionType.EDIT:
                        startEditorActivity(position, vo.getDescription());
                       /* Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_ANSWER_ID, vo.getAnswerId());
                        fragmentManager.beginTransaction()
                                .replace(R.id.container,
                                        FormFragment.newInstance(Constant.FormType.EDIT_ANSWER, map, Constant.URL_QA_EDIT_ANSWER))
                                .addToBackStack(null)
                                .commit();*/
                        break;
                }
                break;
        }
        return false;
    }

    private void callPollSubmitAPI(int position) {
        try {
            if (isNetworkAvailable(context)) {
                Map<String, Object> request = new HashMap<>();
                request.put(Constant.KEY_ID, vo.getQuestionId());
                // request.put("token", poll.getToken());
                request.put("option_id", optionList.get(position).getPollOptionId());
                new ApiController(Constant.URL_QA_VOTE, request, context, this, VOTE_RESULT).setExtraKey(position).execute();
            } else {
                notInternetMsg(v);
            }
        } catch (Exception ignore) {

        }
    }

    public void handleVoteResponse(int position, String response) {

        if (null != response) {
            SuccessResponse resp = new Gson().fromJson(response, SuccessResponse.class);
            if (resp.isSuccess()) {
                // answerList.get(position).updateVoteCount(resp.getResult().getUpvoteCount(), resp.getResult().getDownvoteCount());
                // adapter.notifyItemChanged(position);
            } else {
                Util.showSnackbar(v, resp.getErrorMessage());
            }
        }
    }

    public List<PollOption> optionList;
    private QuestionPollOptionAdapter adapterPoll;
    private boolean isShowingQuestion = true;

    public void setPollRecyclerView() {
        try {
            RecyclerView rvOption = v.findViewById(R.id.rvOption);
            optionList = new ArrayList<>();
            rvOption.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvOption.setLayoutManager(layoutManager);
            adapterPoll = new QuestionPollOptionAdapter(optionList, context, this, -1);
            adapterPoll.showQuestion(isShowingQuestion);
            rvOption.setAdapter(adapterPoll);
            // rvOption.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void startEditorActivity(int position, String value) {
        closeKeyboard();
        Intent intent = new Intent(context, EditorExampleActivity.class);
        String title = getString(position > 0 ? R.string.edit_answer : R.string.create_answer);
        Bundle bundle = new Bundle();
        bundle.putString(EditorExampleActivity.TITLE_PARAM, title);
        bundle.putString(EditorExampleActivity.CONTENT_PARAM, value);
        bundle.putInt(Constant.TAG, position);
        bundle.putString(EditorExampleActivity.TITLE_PLACEHOLDER_PARAM,
                title);
        bundle.putString(EditorExampleActivity.CONTENT_PLACEHOLDER_PARAM,
                title);
        bundle.putInt(EditorExampleActivity.EDITOR_PARAM, EditorExampleActivity.USE_NEW_EDITOR);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQ_EDITOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQ_EDITOR:
                    if (resultCode == -1) {
                        if (data != null) {
                            CustomLog.e("desc", "not null");
                            String desc = data.getStringExtra(Constant.TEXT);
                            int tag = data.getIntExtra(Constant.TAG, -1);
                            CustomLog.e("desc", desc);
                            callCreateAnswerApi(tag, desc);


                        } else {
                            CustomLog.e("desc", "null");
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callCreateAnswerApi(int position, String data) {

        if (isNetworkAvailable(context)) {
            String URL;
            Map<String, Object> map = new HashMap<>();
            if (position > -1) {
                //answer edit : submit this text to server
                URL = Constant.URL_QA_EDIT_ANSWER;
                map.put(Constant.KEY_ANSWER_ID, answerList.get(position).getAnswerId());
                answerList.get(position).setDescription(data);
                adapter.notifyItemChanged(position);
            } else {
                URL = Constant.URL_QA_CREATE_ANSWER;
                map.put(Constant.KEY_QUESTION_ID, mQuestionId);
                showBaseLoader(false);
            }


            map.put(Constant.KEY_DATA, data);
            new ApiController(URL, map, context, this, -4).setExtraKey(position).execute();
        } else {
            notInternetMsg(v);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt;
            itemId = itemId - 10;
            opt = vo.getOptions().get(itemId - 1);
            switch (opt.getName()) {
                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.QA + "_" + vo.getQuestionId());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog(this, -1, getString(R.string.MSG_DELETE_CONFIRMATION_GENERIC, getString(R.string.question)));
                    break;
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_QUESTION_ID, vo.getQuestionId());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditQAFragment.newInstance(Constant.FormType.EDIT_QA, map, Constant.URL_QA_EDIT, null))
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    @Override
    public void showShareDialog(final Share share) {
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
            tvMsg.setText(R.string.TXT_SHARE_FEED);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            // boolean isLoggedIn = SPref.getInstance().isLoggedIn(context);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);

            //Dont apply sharing setting here

            /*if ("1".equals(share.getSetting())) {
                bShareIn.setVisibility(View.VISIBLE);
                bShareOut.setVisibility(View.VISIBLE);
            } */

            bShareOut.setText(R.string.TXT_SHARE_OUTSIDE);
            bShareIn.setText(getString(R.string.txt_share_on, AppConfiguration.SHARE));

            bShareIn.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareInside(share, true);
            });

            bShareOut.setOnClickListener(v -> {
                progressDialog.dismiss();
                shareOutside(share);
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openVideoView(String iframe) {
        Intent intent = new Intent(activity, VideoViewActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIDEO);
        intent.putExtra(Constant.KEY_TYPE, Constant.ResourceType.CONTEST);
        intent.putExtra(Constant.KEY_DATA, iframe);
        // intent.putExtra(Constant.KEY_URI, result.getEntry().getVideo());
        startActivity(intent);
    }
}
