package com.sesolutions.ui.courses.test;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.Test.Answer;
import com.sesolutions.responses.Courses.Test.Test;
import com.sesolutions.responses.Courses.Test.TestResponse2;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.ui.common.SpeakableContent;
import com.sesolutions.ui.customviews.fab.FloatingActionButton;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultPage extends SpeakableContent implements View.OnClickListener, OnUserClickedListener<Integer, Object>, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    private static final int REQ_DELETE = 300;
    private static final int REQ_CLOSE = 302;
    private static final int VOTE_RESULT = 301;
    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    public View v;
    public int categoryId;
    private Test test;
    public List<Answer> optionList;
    public AnswerAdapter adapter;
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
    public TestResponse2.Result result;
    private int pollId;
    private int userid;
    private boolean openComment;
    private boolean isLoggedIn;
    private String selectedModule;
    private boolean isShowingQuestion;
    private FloatingActionButton fabQuestion;
    LinearLayout ll1;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_CORE_POLL) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }
    //    public static CViewPollFragment newInstance(String selectedModule, int pollId) {
//        CViewPollFragment frag = new CViewPollFragment();
//        frag.pollId = pollId;
//        frag.selectedModule = selectedModule;
//        return frag;
//    }
    public static TestResultPage newInstance( int pollId, int userId) {
        TestResultPage frag = new TestResultPage();
        frag.pollId = pollId;
        frag.userid = userId;
        return frag;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_result, container, false);
        applyTheme(v);
        initScreenData();
        callMusicAlbumApi(1);

        return v;
    }


    public void init() {
        try {
//            callBottomCommentLikeApi(pollId, Constant.ResourceType.VIEW_CORE_POLL, Constant.URL_VIEW_COMMENT_LIKE);
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageResource(R.drawable.vertical_dots);
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.Test_Result);
            rvOption = v.findViewById(R.id.rvOption);
            ll1 = v.findViewById(R.id.ll1);


            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            int backcolor=Color.parseColor(Constant.menuButtonActiveTitleColor);//Constant.menuButtonTitleColor);
            ll1.setBackgroundColor(backcolor);
            if (null != test) {
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, true);
            rvOption.setLayoutManager(layoutManager);
            adapter = new AnswerAdapter(optionList, context, this, -1);
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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_COURSE_RESULT);
                    request.params.put(Constant.KEY_TEST_ID, pollId);
                    request.params.put(Constant.KEY_USERTEST_ID, userid);
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
                                TestResponse2 resp = new Gson().fromJson(response, TestResponse2.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    result = resp.getResult();
                                    if (null != resp.getResult().getTestresult()) {
                                        test = resp.getResult().getTestresult();
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


    public void hideLoaders() {
        isLoading = false;
        hideBaseLoader();
        setRefreshing(swipeRefreshLayout, false);
        swipeRefreshLayout.setEnabled(false);
    }



    private void updateView() {
        ((TextView) v.findViewById(R.id.tvTitle2)).setText(test.getTitle());
        ((TextView) v.findViewById(R.id.tvTotal)).setText("Total Questions: " + test.getTotal());
        if(test.getIs_passed() == 1){
            ((ImageView)v.findViewById(R.id.ivResult)).setVisibility(View.VISIBLE);
            ((ImageView)v.findViewById(R.id.ivResult)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tick_sign));
            ((ImageView)v.findViewById(R.id.ivResult)).setColorFilter(Color.parseColor("#008000"));
        } else {
            ((ImageView)v.findViewById(R.id.ivResult)).setVisibility(View.VISIBLE);
            ((ImageView)v.findViewById(R.id.ivResult)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unchecked));
            ((ImageView)v.findViewById(R.id.ivResult)).setColorFilter(Color.RED);
        }
        if(test.getIs_passed() == 1){
            ((TextView)v.findViewById(R.id.tvResult)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tvResult)).setText("Passed!");
            ((TextView)v.findViewById(R.id.tvResult2)).setText("Congratulations! You have successfully passed this test :)");
            ((TextView)v.findViewById(R.id.tvResult3)).setText("You Scored: "+test.getTotal_marks());
            ((TextView)v.findViewById(R.id.tvResult)).setTextColor(Color.parseColor("#008000"));

        } else {
            ((TextView)v.findViewById(R.id.tvResult)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tvResult)).setText("Failed!");
            ((TextView)v.findViewById(R.id.tvResult2)).setText("Sorry! You don't have enough score to qualify for this test :(");
            ((TextView)v.findViewById(R.id.tvResult3)).setText("You Scored: "+test.getTotal_marks());
            ((TextView)v.findViewById(R.id.tvResult)).setTextColor(Color.RED);
        }
        isShowingQuestion = !test.hasVoted();
        v.findViewById(R.id.ivSearch).setVisibility(null != result.getOptions() && result.getOptions().size() > 0 ? View.VISIBLE : View.GONE);

//        addUpperTabItems();
        updateAdapter();

    }

    private void updateAdapter() {
        if (null != result.getUsertest()) {
            optionList.clear();
            optionList.addAll(result.getUsertest());
            wasListEmpty = true;
            adapter.showQuestion(isShowingQuestion);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(rvOption);
        } else {
            // ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
            // v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        }
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


            case Constant.OptionType.REPORT:
                if(SPref.getInstance().isLoggedIn(context)) {
                    goToReportFragment(Constant.ResourceType.VIEW_CORE_POLL + "_" + pollId);
                }
                else
                {
                    Util.showSnackbar(v,"Login or Signup to continue..");
                }
                break;
        }
        return false;
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
