package com.sesolutions.ui.poll;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.responses.poll.PollResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.fab.FloatingActionButton;
import com.sesolutions.ui.groups.ViewGroupFragment;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.message.MessageActivity;
import com.sesolutions.ui.page.ViewPageFragment;
import com.sesolutions.ui.video.CategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.MenuTab;
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

import static android.graphics.Typeface.BOLD;

public class PollViewFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, PopupMenu.OnMenuItemClickListener/*, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener*/ {


    private static final int REQ_DELETE = 300;
    private static final int REQ_CLOSE = 302;
    private static final int VOTE_RESULT = 301;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    /* protected final static String TYPE_TEXT = "text";
     protected final static String TYPE_IMAGE = "image";
     protected final static String TYPE_IMAGE_RESULT = "image_result";
     protected final static String TYPE_TEXT_RESULT = "text_result";*/
    public View v;
    public PollParentFragment parent;
    public int categoryId;
    private Poll poll;
    public List<PollOption> optionList;
    public PollOptionAdapter adapter;
    public  Typeface iconFont;

    public String searchKey;
    // private AppCompatTextView tvQuestion;
    public int loggedinId;

    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOption;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;

    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public PollResponse.Result result;
    private int pollId;
    private boolean isLoggedIn;
    private String selectedModule;
    private boolean isShowingQuestion;
    private FloatingActionButton fabQuestion;
    TextView showresultid;
    String shareurl="";

    public static PollViewFragment newInstance(String selectedModule, int pollId) {
        PollViewFragment frag = new PollViewFragment();
        frag.pollId = pollId;
        frag.selectedModule = selectedModule;
        return frag;
    }

    public static PollViewFragment newInstance(String selectedModule, int pollId,String shareurl) {
        PollViewFragment frag = new PollViewFragment();
        frag.pollId = pollId;
        frag.selectedModule = selectedModule;
        frag.shareurl = shareurl;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_POLL) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_poll_view, container, false);
        this.iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        showresultid=v.findViewById(R.id.showresultid);
        showresultid.setTypeface(iconFont, BOLD);
       // showresultid.setText("");
        Log.e("SHAREURL",""+shareurl);
        showresultid.setText(Html.fromHtml("&#x" + "f059"+" Show Result"));
        applyTheme(v);
        getModuleData();
        initScreenData();
        callMusicAlbumApi(1);
        showresultid.setOnClickListener(this);
        return v;
    }

    private String URL_LIKE, URL_FAVORITE, URL_VOTE, URL_CLOSE, URL_EDIT, URL_DELETE, RC_TYPE, URL_VIEW;

    private void getModuleData() {
        if (TextUtils.isEmpty(selectedModule)) return;

        switch (selectedModule) {
            case Constant.ResourceType.PAGE_POLL:
            case MenuTab.Page.TYPE_BROWSE_POLL:
            case MenuTab.Page.TYPE_PROFILE_POLL:
                URL_LIKE = Constant.URL_PAGE_POLL_LIKE;
                URL_FAVORITE = Constant.URL_PAGE_POLL_FAVORITE;
                URL_VOTE = Constant.URL_PAGE_POLL_VOTE;
                URL_EDIT = Constant.URL_PAGE_POLL_EDIT;
                URL_DELETE = Constant.URL_PAGE_POLL_DELETE;
                URL_CLOSE = Constant.URL_PAGE_POLL_CLOSE;
                URL_VIEW = Constant.URL_PAGE_POLL_VIEW;
                RC_TYPE = Constant.ResourceType.PAGE_POLL;
                break;
            case Constant.ResourceType.GROUP_POLL:
            case MenuTab.Group.TYPE_BROWSE_POLL:
            case MenuTab.Group.TYPE_PROFILE_POLL:
                URL_LIKE = Constant.URL_GROUP_POLL_LIKE;
                URL_FAVORITE = Constant.URL_GROUP_POLL_FAVORITE;
                URL_VOTE = Constant.URL_GROUP_POLL_VOTE;
                URL_EDIT = Constant.URL_GROUP_POLL_EDIT;
                URL_DELETE = Constant.URL_GROUP_POLL_DELETE;
                URL_CLOSE = Constant.URL_GROUP_POLL_CLOSE;
                URL_VIEW = Constant.URL_GROUP_POLL_VIEW;
                RC_TYPE = Constant.ResourceType.GROUP_POLL;
                break;
            case Constant.ResourceType.BUSINESS_POLL:
            case MenuTab.Business.TYPE_BROWSE_POLL:
            case MenuTab.Business.TYPE_PROFILE_POLL:
                URL_LIKE = Constant.URL_BUSINESS_POLL_LIKE;
                URL_FAVORITE = Constant.URL_BUSINESS_POLL_FAVORITE;
                URL_VOTE = Constant.URL_BUSINESS_POLL_VOTE;
                URL_EDIT = Constant.URL_BUSINESS_POLL_EDIT;
                URL_DELETE = Constant.URL_BUSINESS_POLL_DELETE;
                URL_CLOSE = Constant.URL_BUSINESS_POLL_CLOSE;
                URL_VIEW = Constant.URL_BUSINESS_POLL_VIEW;
                RC_TYPE = Constant.ResourceType.BUSINESS_POLL;
                break;
        }
    }

    public void init() {
        try {
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setEnabled(false);
            getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageResource(R.drawable.vertical_dots);
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_POLLS);
            rvOption = v.findViewById(R.id.rvOption);

            fabQuestion = v.findViewById(R.id.fabQuestion);
            fabQuestion.setOnClickListener(this);
            fabQuestion.setFabColor(Color.parseColor(Constant.colorPrimary));
            fabQuestion.setFabSize(FloatingActionButton.FAB_SIZE_MINI);
            fabQuestion.setVisibility(View.GONE);

            v.findViewById(R.id.llLike).setOnClickListener(this);
            v.findViewById(R.id.llFavorite).setOnClickListener(this);
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
            adapter = new PollOptionAdapter(optionList, context, this, -1);
            rvOption.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRecyclerView2() {
        try {
            optionList = new ArrayList<>();
            rvOption.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            rvOption.setLayoutManager(layoutManager);
            adapter = new PollOptionAdapter(optionList, context, this, -1);
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
                    callLikeApi(REQ_LIKE, URL_LIKE);
                    break;
                case R.id.llFavorite:
                    callLikeApi(REQ_FAVORITE, URL_FAVORITE);
                    break;
                case R.id.llComment:
                    goToCommentFragment(poll.getPollId(), RC_TYPE);
                    break;
                case R.id.showresultid:
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
                    if (req == 1) {
                        showBaseLoader(true);
                    }

                    HttpRequestVO request = new HttpRequestVO(URL_VIEW);
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
            llReaction.setVisibility(View.GONE);
            return;
        }
        llReaction.setVisibility(View.VISIBLE);
        int color = Color.parseColor(Constant.text_color_1);

        if (poll.canLike()) {
            ((TextView) v.findViewById(R.id.tvLike)).setText(poll.isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) v.findViewById(R.id.ivImageLike)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(poll.isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) v.findViewById(R.id.tvLike)).setTextColor(color);


        } else {
            v.findViewById(R.id.llLike).setVisibility(View.GONE);
        }

        //add favourite item
        if (poll.canFavourite()) {
            ((TextView) v.findViewById(R.id.tvFavorite)).setText(R.string.TXT_FAVORITE);
            ((ImageView) v.findViewById(R.id.ivImageFavorite)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) v.findViewById(R.id.ivImageFavorite)).setColorFilter(poll.isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) v.findViewById(R.id.tvFavorite)).setTextColor(color);


        } else {
            v.findViewById(R.id.llFavorite).setVisibility(View.GONE);
        }

        //add Comment item
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

        if(isShowingQuestion){
            showresultid.setText(Html.fromHtml("&#x" + "f059"+" Show Result"));
        }else {
            showresultid.setText(Html.fromHtml("&#x" + "f059"+" Show Question"));
        }
        //tvQuestion.setText(isShowingQuestion ? getStrings(R.string.show_result) : getStrings(R.string.poll_show_question));
    }
    TextView tvStats;
    private void updateView() {
        ((TextView) v.findViewById(R.id.tvPollTitle)).setText(poll.getTitle());
        ((TextView) v.findViewById(R.id.tvDesc)).setText(poll.getDescription());

       // ((TextView) v.findViewById(R.id.tvOwner)).setText(poll.getHeaderText1(context));
       // ((TextView) v.findViewById(R.id.tvOwner2)).setText(poll.getHeaderText2(context));

        ((TextView) v.findViewById(R.id.tvOwner2)).setText(Html.fromHtml("in " + "<b><font color=\"#484744\">" + poll.getHeaderText2(context) + "</font></b>"));
        ((TextView) v.findViewById(R.id.tvOwner)).setText(Html.fromHtml("by " + "<b><font color=\"#484744\">" + poll.getHeaderText1(context) + "</font></b>"));

        ((TextView) v.findViewById(R.id.tvOwner)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileFragment(poll.getUserId());
            }
        });

        ((TextView) v.findViewById(R.id.tvOwner2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedModule) {
                    case MenuTab.Page.TYPE_BROWSE_POLL:
                        fragmentManager.beginTransaction()
                                .replace(R.id.container
                                        , ViewPageFragment.newInstance(poll.getPageId()))
                                .addToBackStack(null)
                                .commit();
                        break;
                    case MenuTab.Group.TYPE_BROWSE_POLL:
                        fragmentManager.beginTransaction().replace(R.id.container, ViewGroupFragment.newInstance(poll.getGroupId())).addToBackStack(null).commit();
                        break;
                    case MenuTab.Business.TYPE_BROWSE_POLL:
                        break;
                    case MenuTab.Page.TYPE_PROFILE_POLL:
                        break;
                    case MenuTab.Group.TYPE_PROFILE_POLL:
                        break;
                    case MenuTab.Business.TYPE_PROFILE_POLL:
                        break;
                }
            }
        });


        Util.showImageWithGlide(((ImageView) v.findViewById(R.id.profileviewid)), poll.getImageUrl(), context);

        isShowingQuestion = !poll.hasVoted();
        v.findViewById(R.id.ivSearch).setVisibility(null != result.getOptions() && result.getOptions().size() > 0 ? View.VISIBLE : View.GONE);
        updateQuestionText();
        tvStats = v.findViewById(R.id.tvStats);
        tvStats.setTypeface(FontManager.getTypeface(context));
        String detail = "\uf06e " + poll.getViewCount()
                + "   \uf075 " + poll.getCommentCount()
                + "   \uf164 " + poll.getLikeCount()
                + "   \uf004 " + poll.getFavouriteCount()
                + "   \uf0a6 " + poll.getVoteCount();
        tvStats.setText(detail);
        addUpperTabItems();
        hideShowFabQuestion();
        updateAdapter();
    }

    private void hideShowFabQuestion() {
        fabQuestion.setVisibility(poll.getIsClosed() == 0 ? View.GONE : View.GONE);
        if (poll.getIsClosed() != 0) {
            isShowingQuestion = false;
        }

    }

    private void updateAdapter() {


        if (null != poll.getOptions()) {
            optionList.clear();
            optionList.addAll(poll.getOptions());
            wasListEmpty = true;

            Log.e("IMAGETYPE"," "+optionList.get(0).getImageType());
            if(optionList.size()>0 &&(optionList.get(0).getImageType() != 0)){
                try {
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    rvOption.setLayoutManager(layoutManager);
                    adapter = new PollOptionAdapter(optionList, context, this, -1);
                    rvOption.setAdapter(adapter);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }else {
                try {
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    rvOption.setLayoutManager(layoutManager);
                    adapter = new PollOptionAdapter(optionList, context, this, -1);
                    rvOption.setAdapter(adapter);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            adapter.showQuestion(isShowingQuestion);
            adapter.setPoll(poll);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(rvOption);
        } else {
            // ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
            // v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    private void callLikeApi(final int REQ_CODE, String url) {


        if (isNetworkAvailable(context)) {
            if (REQ_CODE == REQ_DELETE) {
                showBaseLoader(false);
            }
            updateItemLikeFavorite(REQ_CODE);
            try {

                HttpRequestVO request = new HttpRequestVO(url);

                request.params.put(Constant.KEY_ID, pollId);
                //request.params.put(Constant.KEY_TYPE, Constant.ResourceType.PAGE);
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

                                if (REQ_CODE == REQ_DELETE) {
                                       /* JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                        List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                        result.getPage().setButtons(opt);
                                        */
                                    Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                    activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                    onBackPressed();
                                }
                                    /*if (REQ_CODE == REQ_LIKE) {
                                        videoList.get(position).setContentLike(!vo.isContentLike());
                                    } else if (REQ_CODE == REQ_FAVORITE) {
                                        videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                    }
                                    adapter.notifyItemChanged(position);*/
                            } else {
                                //revert changes in case of error
                                updateItemLikeFavorite(REQ_CODE);
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

    public void updateItemLikeFavorite(int REQ_CODE) {

        if (REQ_CODE == REQ_LIKE) {
            poll.setContentLike(!poll.isContentLike());
            ((TextView) v.findViewById(R.id.tvLike)).setText(poll.isContentLike() ? R.string.TXT_UNLIKE : R.string.TXT_LIKE);
            ((ImageView) v.findViewById(R.id.ivImageLike)).setColorFilter(Color.parseColor(poll.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
            int likecount=poll.getLikeCount();
            if(poll.isContentLike()){
                likecount=likecount+1;
                String detail = "\uf06e " + poll.getViewCount()
                        + "   \uf075 " + poll.getCommentCount()
                        + "   \uf164 " + likecount
                        + "   \uf004 " + poll.getFavouriteCount()
                        + "   \uf0a6 " + poll.getVoteCount();
                tvStats.setText(detail);
                poll.setLikeCount(likecount);

            }else {
                likecount=likecount-1;
                String detail = "\uf06e " + poll.getViewCount()
                        + "   \uf075 " + poll.getCommentCount()
                        + "   \uf164 " + likecount
                        + "   \uf004 " + poll.getFavouriteCount()
                        + "   \uf0a6 " + poll.getVoteCount();
                tvStats.setText(detail);
                poll.setLikeCount(likecount);
            }
        } else if (REQ_CODE == REQ_FAVORITE) {
            poll.setContentFavourite(!poll.isContentFavourite());
            ((ImageView) v.findViewById(R.id.ivImageFavorite)).setColorFilter(Color.parseColor(poll.isContentFavourite() ? Constant.red : Constant.text_color_1));

            int favouriteCount=poll.getFavouriteCount();
            if(poll.isContentFavourite()){
                favouriteCount=favouriteCount+1;
                String detail = "\uf06e " + poll.getViewCount()
                        + "   \uf075 " + poll.getCommentCount()
                        + "   \uf164 " + poll.getLikeCount()
                        + "   \uf004 " + favouriteCount
                        + "   \uf0a6 " + poll.getVoteCount();
                tvStats.setText(detail);
                poll.setFavouriteCount(favouriteCount);

            }else {
                favouriteCount=favouriteCount-1;
                String detail = "\uf06e " + poll.getViewCount()
                        + "   \uf075 " + poll.getCommentCount()
                        + "   \uf164 " + poll.getLikeCount()
                        + "   \uf004 " + favouriteCount
                        + "   \uf0a6 " + poll.getVoteCount();
                tvStats.setText(detail);
                poll.setFavouriteCount(favouriteCount);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        switch (object1) {
            case REQ_DELETE:
                activity.taskPerformed = Constant.TASK_DELETE_POLL;
                Toast.makeText(activity,"Page  Poll deleted",Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
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
                bundle.putInt(Constant.KEY_ID, optionList.get(position).getPollOptionId());
                fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
                break;
            case Constant.Events.VOTE:
                try {
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> request = new HashMap<>();
                        request.put(Constant.KEY_ID, poll.getPollId());
                        request.put("token", poll.getToken());
                        request.put("option_id", poll.getOptions().get(position).getPollOptionId());
                        new ApiController(URL_VOTE, request, context, this, VOTE_RESULT).setExtraKey(position).execute();
                    } else {
                        notInternetMsg(v);
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
               /* if (isNetworkAvailable(context)) {
                    Map<String, Object> request = new HashMap<>();
                    request.put(Constant.KEY_ID, poll.getPollId());
                    request.put(Constant.KEY_POLL_ID, poll.getPollId());
                    new ApiController(URL_CLOSE, request, context, this, VOTE_RESULT).setExtraKey(position).execute();
                } else {
                    notInternetMsg(v);
                }*/
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
                            try {
                                int votesvalue=Integer.parseInt(votesCount.getString(i).split(" ")[0]);
                                float percentage=(votesvalue*100)/votesTotal;
                                int per= (int) percentage;
                                optionList.get(i).setVotePercent(votesCount.getString(i)+"("+per+"%)" );
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
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

                adapter.setPoll(poll);
                isShowingQuestion = !isShowingQuestion;
                adapter.showQuestion(isShowingQuestion);
                adapter.notifyDataSetChanged();
                updateQuestionText();

                break;
        }
        return false;
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
            case Constant.OptionType.EDIT:
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_POLL_ID, pollId);
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditPollFragment.newInstance(Constant.FormType.EDIT_POLL, map, URL_EDIT, selectedModule)).addToBackStack(null).commit();
                break;

            case "close":
            case "open":
                if (isNetworkAvailable(context)) {
                    map = new HashMap<>();
                    map.put(Constant.KEY_POLL_ID, pollId);
                    // map.put(Constant.KEY_POLL_ID, pollId);
                    new ApiController(URL_CLOSE, map, context, this, REQ_CLOSE).execute();
                } else {
                    notInternetMsg(v);
                }
                break;
            case Constant.OptionType.DELETE:
                showDeleteDialog();
                break;
            case Constant.OptionType.SHARE:
                showShareDialog(result.getPoll().getShare2(),shareurl);
                break;

            case Constant.OptionType.REPORT:
                goToReportFragment(RC_TYPE + "_" + pollId);
                break;
        }
        return false;
    }


    public void goToComposeMessageFragment(Share vo) {
        Intent intent=new Intent(activity, MessageActivity.class);
        try {
            if(vo.getUrl()!=null && vo.getUrl().length()>0 && !vo.getUrl().equalsIgnoreCase("null")){
                intent.putExtra("DISCRIPTONTAG",""+vo.getUrl());
            }else {
                intent.putExtra("DISCRIPTONTAG",""+vo.getDescription());
            }
        }catch (Exception ex){
            ex.printStackTrace();
            intent.putExtra("DISCRIPTONTAG",""+vo.getDescription());
        }
        context.startActivity(intent);
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
                    request.put(Constant.KEY_ID, pollId);
                    new ApiController(URL_DELETE, request, context, PollViewFragment.this, REQ_DELETE).execute();
                } else {
                    notInternetMsg(v);
                }
            });
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    /* @Override
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
 */
    @Override
    public void onRefresh() {
        try {
            swipeRefreshLayout.setEnabled(true);
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }



  /*  private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
