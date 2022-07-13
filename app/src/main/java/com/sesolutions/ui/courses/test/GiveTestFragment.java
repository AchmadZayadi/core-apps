package com.sesolutions.ui.courses.test;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.Test.TestContent;
import com.sesolutions.responses.Courses.Test.TestResponse2;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.courses.adapters.TestOptionsAdapter;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GiveTestFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    public View v;
    public int categoryId;
    public List<Dummy.Formfields> optionList;
    public List<String> type;
    public List<Integer> pos;
    public TestOptionsAdapter adapter;
    public CommentLike.Stats stats;
    private CountDownTimer countDownTimer;
    public String searchKey;
    public TextView tvTimer;
    public int loggedinId;
    private TestContent album;
    private int currentQuestion = 1;
    private int currentOptions = 1;
    private RecyclerView rvOption;
    public String resourceType;
    public int resourceId;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public TestResponse2.Result result;
    private int testId;

    private LinkedHashMap<String, List<String>> answerMap = new LinkedHashMap<>();
    private List<Answer> answerList = new ArrayList<>();

    public static GiveTestFragment newInstance(int testId) {
        GiveTestFragment frag = new GiveTestFragment();
        frag.testId = testId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_test, container, false);
        applyTheme(v);
        initScreenData();
        callMusicAlbumApi(1);
        return v;
    }

    public void init() {
        try {

            v.findViewById(R.id.ivBack).setOnClickListener(this);
            v.findViewById(R.id.llNext).setOnClickListener(this);
            v.findViewById(R.id.llSkip).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivSearch)).setImageResource(R.drawable.vertical_dots);
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            rvOption = v.findViewById(R.id.recyclerView);
            tvTimer = v.findViewById(R.id.tvTimer);
            tvTimer.setTextColor(Color.parseColor(Constant.red));
            v.findViewById(R.id.tvTimer).setOnClickListener(this);
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
            type = new ArrayList<>();
            pos = new ArrayList<>();
            rvOption.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvOption.setLayoutManager(layoutManager);
            adapter = new TestOptionsAdapter(answerList, type, pos, context, new TestOptionsAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(Object item, int position) {
                    answerMap.put(optionList.get(position).getName(), (List<String>) item);
                }

                @Override
                public void onItemUncheck(Object item, int position) {
                    answerMap.put(optionList.get(position).getName(), (List<String>) item);
                }
            });
            rvOption.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getString(R.string.msg_leave_test));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    countDownTimer.cancel();
                    GiveTestFragment.super.onBackPressed();
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

    public void Submit() {
        showBaseLoader(false);
        callSubmitApi(answerMap);
    }

    public void skipQuestion() {
        if ((currentQuestion) < optionList.size()) {
            updateQuestion(currentQuestion++);
            updateAnswers(currentOptions++);
        } else {
            Submit();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    showDeleteDialog();
                    break;
                case R.id.tvTimer:
                    Util.showSnackbar(v, "Test will be automatically finished when the time is over.");
                    break;
                case R.id.llSkip:
                    skipQuestion();
                    break;
                case R.id.llNext:

                    CustomLog.e("currentQuestion", "" + currentQuestion);
                    if (currentQuestion != optionList.size()) {
                        if (answerMap.get(optionList.get(currentQuestion - 1).getName()).size() > 0) {
                            updateQuestion(currentQuestion++);
                            updateAnswers(currentOptions++);
                        } else {
                            Util.showSnackbar(v, "Please select an option..");
                        }
                    } else
                        Submit();
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

    public void callSubmitApi(Map<String, List<String>> params) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_TEST);
                    request.params.put(Constant.KEY_TEST_ID, testId);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    request.params.putAll(params);
                    request.params.put("submit", 1);
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
                                    super.onBackPressed();
                                    openViewTestResultFragment(result.getTest().getTest_id(), result.getTest().getUserTest_id());
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                    goIfPermissionDenied(resp.getError());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }

                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideLoaders();
                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
        }
    }

    public void callMusicAlbumApi(final int req) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_TEST);
                    request.params.put(Constant.KEY_TEST_ID, testId);
                    request.params.put(Constant.KEY_GET_FORM, 1);
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
                                    if (null != resp.getResult().getTest() && null != resp.getResult().getFormFields()) {
                                        optionList.addAll(result.getFormFields());

                                        for (Dummy.Formfields field : optionList) {
                                            answerMap.put(field.getName(), new ArrayList<>());
                                        }
                                        updateTitle(result.getTest().getTitle());
                                        for (Map.Entry<String, String> entry : optionList.get(0).getMultiOptions().entrySet()) {
                                            answerList.add(new Answer(entry.getKey(), entry.getValue(), false));
                                        }
                                        album = resp.getResult().getTest();
                                        updateAnswers(0);
                                        updateQuestion(0);
                                        startClock();
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

                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideLoaders();
                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
        }
    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    private void startClock() {

        countDownTimer = new CountDownTimer(album.getTime() * 60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(Util.milliSecondsToTimer(millisUntilFinished));
            }

            public void onFinish() {
                tvTimer.setText("done!");
                Submit();
                Util.showSnackbar(v, "Test Finished.");
            }
        }.start();

    }

    public void hideLoaders() {
        hideBaseLoader();
    }

    private void updateQuestion(int pos) {
        if (currentQuestion == optionList.size()) {
            v.findViewById(R.id.llSkip).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.tvNext)).setText("Finish");
            ((View) v.findViewById(R.id.llNext)).setBackgroundColor(Color.parseColor(Constant.red));
            ((TextView) v.findViewById(R.id.tvNext)).setTextColor(Color.parseColor(Constant.white));
        }
        ((TextView) v.findViewById(R.id.tvQuestionId)).setText("Question " + (pos + 1) + " of " + album.getQuestions());

        ((WebView) v.findViewById(R.id.tvQuestion)).loadData(optionList.get(pos).getLabel(), "text/html", "UTF-8");
    }

    private void updateAnswers(int position) {
        type.clear();
        pos.clear();
        Map<String, String> options = result.getFormFields().get(position).getMultiOptions();

        answerList.clear();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            answerList.add(new Answer(entry.getKey(), entry.getValue(), false));
        }
        type.add(result.getFormFields().get(position).getType());
        pos.add(position);
        adapter.setEmptyList();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(rvOption);

    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null) {
            showDeleteDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {

        return false;
    }
}
