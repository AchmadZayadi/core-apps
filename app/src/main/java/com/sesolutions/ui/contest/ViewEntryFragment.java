package com.sesolutions.ui.contest;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.contest.ContestGraph;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.video.VideoViewActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEntryFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object>, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener, MediaPlayer.OnPreparedListener {


    private final int REQ_LIKE = 100;
    private final int REQ_FAVORITE = 200;
    private final int REQ_FOLLOW = 300;
    private final int REQ_DELETE = 400;
    private final int REQ_VOTE = 401;
    private final int REQ_GRAPH_DAFAULT = 402;
    private final int REQ_GRAPH = 403;

    private final int REQ_UPDATE_UPPER = 406;

    private ContestResponse.Result result;
    private int mEventId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ContestGraph graphVo;
    private List<Options> graphOptions;
    private float maxYRang;
    private List<String> dateList;
    private MediaPlayer mediaPlayer;

    public static ViewEntryFragment newInstance(int contestId) {
        ViewEntryFragment frag = new ViewEntryFragment();
        frag.mEventId = contestId;
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
                case Constant.TASK_IMAGE_UPLOAD:
                    /*if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
                        result.getEntry().getImages().setMain(Constant.BASE_URL + activity.stringValue);
                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
                        result.getEntry().setCoverImageUrl(Constant.BASE_URL + activity.stringValue);
                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
                    }*/
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    activity.taskPerformed = 0;
                    break;
                case Constant.FormType.EDIT_ENTRY:
                    activity.taskPerformed = 0;
                    onRefresh();
                    break;

               /* case Constant.Task.ALBUM_DELETED:
                case Constant.Task.NOTE_DELETED:
                    activity.taskPerformed = 0;
                    swipeRefreshLayout.setEnabled(true);
                    onRefresh();
                    break;*/
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateCoverPhoto(String url) {
        Util.showImageWithGlide(v.findViewById(R.id.ivCoverPhoto), url, context, R.drawable.placeholder_square);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_entry_view, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

        callMusicAlbumApi(1);

        return v;
    }


    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            try {
                if (req == 1) {
                    showBaseLoader(true);
                } else if (req == REQ_UPDATE_UPPER) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_ENTRY_VIEW);
                request.params.put(Constant.KEY_ID, mEventId);
                //  request.params.put("menus", 1);
                //  request.params.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (!isAdded()) return false;

                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    ContestResponse commonResponse = new Gson().fromJson(response, ContestResponse.class);
                                    if (commonResponse.getResult() != null) {
                                        //if screen is refreshed then clear previous data
                                       /* if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }

                                        wasListEmpty = videoList.size() == 0;
                                        result = resp.getResult();
                                        if (null != result.getGroups())
                                            videoList.addAll(result.getGroups());

                                        updateAdapter();*/
                                        result = commonResponse.getResult();
                                    }

                                    if (req == REQ_UPDATE_UPPER) {
                                        setUpperUIData();
                                    } else {
                                        initUI();
                                    }
                                    hideAllLoaders();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            somethingWrongMsg(v);
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
                somethingWrongMsg(v);
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void hideAllLoaders() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            hideBaseLoader();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initUI() {
        try {

            v.findViewById(R.id.cl).setVisibility(View.VISIBLE);
            // getActivity().invalidateOptionsMenu();

            initCollapsingToolbar();
            v.findViewById(R.id.vScrim).setOnClickListener(this);
            setUpperUIData();
            initChart();
            callGraphApi("hourly", REQ_GRAPH_DAFAULT);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callGraphApi(String type, int req) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ID, mEventId);
        map.put(Constant.KEY_TYPE, type);
        new ApiController(Constant.URL_ENTRY_GRAPH, map, context, this, req).execute();
    }

    LineChart mChart;

    private void initChart() {


        mChart = v.findViewById(R.id.lcChart);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBorders(false);


        mChart.getAxisRight().setDrawAxisLine(false);
        mChart.getAxisRight().setDrawGridLines(true);
        mChart.getAxisLeft().setGranularity(1f);
        //mChart.getAxisLeft().setGranularityEnabled(true);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.getXAxis().setValueFormatter((value, axis) -> {
            try {
                return dateList.get((int) value); // xVal is a string array
            } catch (Exception e) {
                CustomLog.e(e);
                return "";
            }
        });
        mChart.getAxisLeft().setEnabled(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
       /* mChart.getAxisLeft().setValueFormatter((value, axis) -> {
            return "" + (int) value; // yVal is a string array
        });*/
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.animateX(2000, Easing.EasingOption.EaseInExpo);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // mSeekBarX.setProgress(20);
        //  mSeekBarY.setProgress(100);

        Legend l = mChart.getLegend();
        //  l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //  l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //  l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setEnabled(false);
        //  l.setDrawInside(false);

        //setChartData();
    }

    private void setSpinnerData() {

        AppCompatSpinner spinner = v.findViewById(R.id.spinner);
        if (null != graphOptions) {
            List<String> graphOptionsList = new ArrayList<>();
            for (int i = 0; i < graphOptions.size(); i++) {
                graphOptionsList.add(graphOptions.get(i).getLabel());
            }
            ArrayAdapter<String> graphOptionsAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, graphOptionsList);
            graphOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinner.setAdapter(graphOptionsAdapter);
            spinner.setOnItemSelectedListener(this);
            spinner.setVisibility(View.VISIBLE);
        } else {
            spinner.setVisibility(View.GONE);
        }
    }

    private void updateChartData() {

        v.findViewById(R.id.pbMain).setVisibility(View.GONE);
        if (null != graphVo) {

            ((TextView) v.findViewById(R.id.tvGraphTitle)).setText(graphVo.getHeadingTitle());

            v.findViewById(R.id.rlChartMain).setVisibility(View.VISIBLE);
            mChart.setVisibility(View.VISIBLE);
            mChart.resetTracking();
            if (null != mChart.getData()) {
                mChart.getData().getDataSets().clear();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }

            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(getChartData(graphVo.getVoteCount(), R.color.chart_vote));
            dataSets.add(getChartData(graphVo.getLikeCount(), R.color.chart_like));
            dataSets.add(getChartData(graphVo.getCommentCount(), R.color.chart_comment));
            dataSets.add(getChartData(graphVo.getFavouriteCount(), R.color.chart_favourite));
            dataSets.add(getChartData(graphVo.getViewCount(), R.color.chart_view));


            // make the first DataSet dashed
            //((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
            //  ((LineDataSet) dataSets.get(0)).setColors(Color.parseColor("#2ecc71"));
            // ((LineDataSet) dataSets.get(1)).setColors(Color.parseColor("#f1c40f"));
            // ((LineDataSet) dataSets.get(2)).setColors(Color.parseColor("#e74c3c"));
            // ((LineDataSet) dataSets.get(3)).setColors(Color.parseColor("#3498db"));
            // ((LineDataSet) dataSets.get(4)).setColors(Color.parseColor("#c51162"));
            // ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.MATERIAL_COLORS);

            //  LineData data =new LineData(dataSets) ;

            mChart.setData(new LineData(dataSets));

            mChart.invalidate();
        } else {
            mChart.setVisibility(View.GONE);
        }
    }

    private LineDataSet getChartData(List<Float> list, int colorId) {

        ArrayList<Entry> values = new ArrayList<Entry>();
        float largest = Collections.max(list);
        if (largest > maxYRang)
            maxYRang = largest;

        for (int i = 0; i < dateList.size(); i++) {
            values.add(new Entry(i, list.get(i)));
        }
        LineDataSet d = new LineDataSet(values, null);
        d.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        d.setLineWidth(2f);//2.5f);
        d.setCircleRadius(3f);

        int color = ContextCompat.getColor(context, colorId);
        d.setColor(color);
        d.setCircleColor(color);

        return d;
    }

    // private String[] mColors = {"#2ecc71", "#f1c40f", "#e74c3c", "#3498db", "#c51162"};


    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    private void setUpperUIData() {

        try {
            if (result.getEntry() != null) {
                v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
                ContestItem resp = result.getEntry();
                ((TextView) v.findViewById(R.id.tvEntryTitle)).setText(resp.getTitle());
                ((TextView) v.findViewById(R.id.tvContestTitle)).setText(getString(R.string.in_content, resp.getContestTitle()));

                //((TextView) v.findViewById(R.id.tvStats)).setText(getDetail(resp));
                if (resp.getUpdateCoverPhoto() != null) {
                    v.findViewById(R.id.ivCamera).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.ivCamera).setOnClickListener(this);
                }

                Util.showImageWithGlide(v.findViewById(R.id.ivUserImage),  result.getEntry().getOwnerImageUrl(), context, R.drawable.placeholder_square);

                v.findViewById(R.id.ivUserImage).setOnClickListener(this);
                ((TextView) v.findViewById(R.id.tvOwnerTitle)).setText(resp.getOwnerTitle());
                ((TextView) v.findViewById(R.id.tvDate)).setText(getString(R.string.on_date, Util.changeFormat(resp.getCreationDate())));

                setIconByType();
                updateCoverPhoto(resp.getImage());
                setAboutUI();
                addUpperTabItems();
                if (result.getEntry().canShowVote()) {
                    updateFabColor();
                    v.findViewById(R.id.fabJoin).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.fabJoin).setOnClickListener(this);
                } else {
                    v.findViewById(R.id.fabJoin).setVisibility(View.GONE);
                }
                if (null != resp.getDescription()) {
                    v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
                    ((TextView) v.findViewById(R.id.tvDetail)).setText(resp.getDescription());
                } else {
                    v.findViewById(R.id.llDetail).setVisibility(View.GONE);
                }

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        //  mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);

    }

    private void updateFabColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!result.getEntry().isContentVoted()) {
                ((FloatingActionButton) v.findViewById(R.id.fabJoin)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Constant.colorPrimary)));
            } else {
                DrawableCompat.setTintList(DrawableCompat.wrap(((FloatingActionButton) v.findViewById(R.id.fabJoin)).getDrawable()), ColorStateList.valueOf(Color.parseColor(Constant.text_color_1)));
                ((FloatingActionButton) v.findViewById(R.id.fabJoin)).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Constant.backgroundColor)));
            }
        }
    }

    private void setIconByType() {

        if (null != result.getEntry().getContestType()) {
            v.findViewById(R.id.vScrim).setVisibility(View.GONE);
            v.findViewById(R.id.ivMediaType).setVisibility(View.GONE);
            TextView tv = v.findViewById(R.id.tvType);
            tv.setTypeface(FontManager.getTypeface(context));
            switch (result.getEntry().getContestType()) {
                case "1":
                    tv.setText(Constant.FontIcon.TEXT);
                    break;
                case "2":
                    tv.setText(Constant.FontIcon.ALBUM);
                    break;
                case "3":
                    tv.setText(Constant.FontIcon.VIDEO);
                    v.findViewById(R.id.vScrim).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
                    ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                    break;
                case "4":
                    tv.setText(Constant.FontIcon.MUSIC);
                    v.findViewById(R.id.vScrim).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
                    ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_rounded_blue));
                    break;
            }
        }
    }

    private void setAboutUI() {
        LinearLayoutCompat llBasic = v.findViewById(R.id.llAbout);
        llBasic.setVisibility(View.VISIBLE);
        // llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

        //adding media type
        if (null != result.getEntry().getMediaType()) {
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.media_type);
            ((TextView) view.findViewById(R.id.tv2)).setText(result.getEntry().getMediaType().getLabel());
            llBasic.addView(view);
        }

        //adding category
        if (null != result.getEntry().getCategoryTitle()) {
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.category);
            ((TextView) view.findViewById(R.id.tv2)).setText(result.getEntry().getCategoryString());
            llBasic.addView(view);
        }

        //adding Stats
        /*  if (null != result.getEntry().getCategoryTitle())*/
        {
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.stats);
            ((TextView) view.findViewById(R.id.tv2)).setText(getDetail(result.getEntry()));
            llBasic.addView(view);
        }

        //adding Stats
        if (null != result.getEntry().getTag()) {
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.text_tags);
            ((TextView) view.findViewById(R.id.tv2)).setText(SpanUtil.getHashTags(result.getEntry().getTag(), this));
            llBasic.addView(view);
        }

        //adding Voting start/End time
        if (null != result.getEntry().getVotingStartTime()) {
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.txt_vote_start_end_date);
            ((TextView) view.findViewById(R.id.tv2)).setText(result.getEntry().getVotingStartTime() + " - " + result.getEntry().getVotingEndTime());
            llBasic.addView(view);
        }
    }

    private void addUpperTabItems() {
        if (!SPref.getInstance().isLoggedIn(context)) return;

        //add post item
        LinearLayoutCompat llTabOptions = v.findViewById(R.id.llTabOptions);
        llTabOptions.setVisibility(View.VISIBLE);
        llTabOptions.removeAllViews();
        int color = Color.parseColor(Constant.text_color_1);

        //add Follow item
        if (result.getEntry().canLike()) {
            final View view2 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, llTabOptions, false);
            ((TextView) view2.findViewById(R.id.tvOptionText)).setText(result.getEntry().isContentLike() ? R.string.unlike : R.string.like);
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            ((ImageView) view2.findViewById(R.id.ivOptionImage)).setColorFilter(result.getEntry().isContentLike() ? Color.parseColor(Constant.colorPrimary) : color);
            ((TextView) view2.findViewById(R.id.tvOptionText)).setTextColor(color);
            view2.setOnClickListener(v -> callLikeApi(REQ_LIKE, view2, Constant.URL_CONTEST_LIKE));
            llTabOptions.addView(view2);
        }

        //add favourite item
        if (result.getEntry().canFavourite()) {
            final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, llTabOptions, false);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(getString(R.string.TXT_FAVORITE));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(result.getEntry().isContentFavourite() ? Color.parseColor(Constant.red) : color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> callLikeApi(REQ_FAVORITE, view1, Constant.URL_CONTEST_FAVOURITE));
            llTabOptions.addView(view1);
        }

        //add Comment item
        if (result.getEntry().canComment() && SPref.getInstance().isLoggedIn(context)) {
            final View view1 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, (ViewGroup) llTabOptions, false);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setText(R.string.TXT_COMMENT);
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.comment));
            ((ImageView) view1.findViewById(R.id.ivOptionImage)).setColorFilter(color);
            ((TextView) view1.findViewById(R.id.tvOptionText)).setTextColor(color);
            view1.setOnClickListener(v -> goToCommentFragment(mEventId, Constant.ResourceType.ENTRY));
            llTabOptions.addView(view1);
        }


       /* //check permission and show Vote icon
        if (result.getEntry().canShowVote()) {
            View view3 = getLayoutInflater().inflate(R.layout.layout_text_image_vertical, llTabOptions, false);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setText(result.getEntry().isContentVoted() ? R.string.voted : R.string.vote);
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.contest_vote));
            ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(result.getEntry().isContentVoted() ? Color.parseColor(Constant.green) : color);
            ((TextView) view3.findViewById(R.id.tvOptionText)).setTextColor(color);
            view3.setOnClickListener(v -> {
                //do not vote if already voted
                if (!result.getEntry().isContentVoted() && isNetworkAvailable(context)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, result.getEntry().getContestId());
                    map.put(Constant.KEY_ID, result.getEntry().getParticipantId());

                    //toggle vote button text
                    result.getEntry().toggleVote();
                    ((TextView) view3.findViewById(R.id.tvOptionText)).setText(result.getEntry().isContentVoted() ? R.string.voted : R.string.vote);
                    ((ImageView) view3.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.contest_vote));
                    ((ImageView) view3.findViewById(R.id.ivOptionImage)).setColorFilter(result.getEntry().isContentVoted() ? Color.parseColor(Constant.green) : color);

                    //calling api
                    new ApiController(Constant.URL_ENTRY_VOTE, map, context, ViewEntryFragment.this, REQ_VOTE).execute();
                }
            });

            llTabOptions.addView(view3);
        }*/

    }


    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
        //toolbar.setTitle(" ");
        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    //  swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getEntry().getTitle());
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
    }


    //open view category page
    private void openViewCategory() {

    }

    public String getDetail(ContestItem album) {
        String detail = "";
        try {
            detail += album.getVoteCount() + (album.getVoteCount() != 1 ? getStrings(R.string._VOTES) : getString(R.string._VOTE))
                    + ", " + album.getLikeCount() + (album.getLikeCount() != 1 ? getStrings(R.string._LIKES) : getString(R.string._LIKE))
                    + ", " + album.getCommentCount() + (album.getCommentCount() != 1 ? getString(R.string._COMMENTS) : getString(R.string._COMMENT))
                    + ", " + album.getViewCountInt() + (album.getViewCountInt() != 1 ? getString(R.string._VIEWS) : getString(R.string._VIEW))
                    + ", " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? getString(R.string._FAVORITES) : getString(R.string._FAVORITE))
            //   + ", " + album.getMemberCount() + (album.getMemberCount() != 1 ? getString(R.string._members) : getString(R.string._member))
            ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return detail;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(result.getEntry().getShare());
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getEntry().getOptions(), vItem, 10);
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);

    }


    private void showPopup(List<Options> menus, View v, int idPrefix) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Options s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        try {
            // Not showing the option menu if the share is null.
            if (null != result && null != result.getEntry() && result.getEntry().getShare() != null) {

                menu.add(Menu.NONE, R.id.share, Menu.FIRST, result.getEntry().getShare().getLabel())
                        .setIcon(R.drawable.share_music)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            if (null != result && result.getEntry().getOptions() != null) {
                menu.add(Menu.NONE, R.id.option, Menu.FIRST, "options")
                        .setIcon(R.drawable.vertical_dots)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
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
            if (itemId > 1000) {
                itemId = itemId - 1000;
                opt = result.getEntry().getUpdateCoverPhoto().get(itemId - 1);
                isCover = true;
            } else if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getEntry().getUpdateProfilePhoto().get(itemId - 1);
            } else {
                itemId = itemId - 10;
                opt = result.getEntry().getOptions().get(itemId - 1);
            }


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_ID, mEventId);
                    openFormFragment(Constant.FormType.EDIT_ENTRY, map, Constant.URL_ENTRY_EDIT);
                    break;
                case Constant.OptionType.DASHBOARD:
                    openWebView(Constant.URL_EVENT_DASHBOARD + mEventId, opt.getLabel());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.SHARE:
                    showShareDialog(result.getEntry().getShare());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.ENTRY + "_" + mEventId);
                    break;

                case "takephoto":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                    goToUploadAlbumImage(Constant.URL_CONTEST_ADD_PHOTO, result.getEntry().getContestImage(), opt.getLabel(), map);
                    break;

                case "uploadcover":
                case "addcover":
                case "changecover":
                    map = new HashMap<>();
                    map.put(Constant.KEY_CONTEST_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                    goToUploadAlbumImage(Constant.URL_CONTEST_ADD_COVER, result.getEntry().getCoverImageUrl(), opt.getLabel(), map);
                    break;

                case Constant.OptionType.view_profile_photo:
                    // goToGalleryFragment(result.getEntry().getPhotoId(), resourceType, result.getProfile().getUserPhoto());
                    break;
                case Constant.OptionType.ALBUM:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_ID, mEventId);
                    map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                    openSelectAlbumFragment(isCover ? Constant.URL_UPLOAD_EVENT_COVER : Constant.URL_UPLOAD_EVENT_PHOTO, map);
                    break;

                case Constant.OptionType.UPLOAD:
                    map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mEventId);
                    map.put(Constant.KEY_IMAGE, "Filedata");
                    if (isCover) {
                        map.put(Constant.KEY_TYPE, Constant.TASK_COVER_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_COVER, result.getEntry().getCoverImageUrl(), opt.getLabel(), map);
                    } else {
                        map.put(Constant.KEY_TYPE, Constant.TASK_PHOTO_UPLOAD);
                        goToUploadAlbumImage(Constant.URL_UPLOAD_EVENT_PHOTO, result.getEntry().getImageUrl(), opt.getLabel(), map);
                    }
                    break;
                case Constant.OptionType.view_cover_photo:
                    // goToGalleryFragment(result.getEntry().getCoverImageUrl(), resourceType, result.getEntry().getCoverImageUrl());
                    break;
                case "removecover":
                    showImageRemoveDialog(true);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    //TODO same method is on ProfileFragment
    public void showImageRemoveDialog(boolean isCover) {
        try {
            final String url = isCover ? Constant.URL_CONTEST_REMOVE_COVER : Constant.URL_REMOVE_EVENT_PHOTO;
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(isCover ? R.string.MSG_COVER_DELETE_CONFIRMATION : R.string.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? R.string.TXT_REMOVE_COVER : R.string.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_EVENT_ID, mEventId);
                // map.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT);
                new ApiController(url, map, context, ViewEntryFragment.this, Constant.Events.REMOVE_PHOTO).execute();
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
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
    public void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_rounded_blue));
            mediaPlayer = null;
        }
        super.onStop();
    }

    private void playAudio() {

        //String pathToFile = "http://www.xyz.com/Audio/sample.mp3";
        //Uri uri = Uri.parse(pathToFile);
        //mediaPlayer = MediaPlayer.create(this, uri);

        v.findViewById(R.id.pbLoader).setVisibility(View.VISIBLE);
        ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_rounded_bluew));
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
        }
        try {
            mediaPlayer.setDataSource(result.getEntry().getAudio());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        //  mediaPlayer.start();
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.ivUserImage:
                    goToProfileFragment(result.getEntry().getOwnerId());
                    break;
                case R.id.ivCamera:
                    //   if (null != result.getEntry().getUpdateCoverPhoto())
                    showPopup(result.getEntry().getUpdateCoverPhoto(), v.findViewById(R.id.ivCamera), 1000);
                    break;
                case R.id.fabJoin:
                    if (!result.getEntry().isContentVoted()) {
                        if (!isNetworkAvailable(context)) {
                            notInternetMsg(v);
                            return;
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_CONTEST_ID, result.getEntry().getContestId());
                        map.put(Constant.KEY_ID, result.getEntry().getParticipantId());
                        new ApiController(Constant.URL_ENTRY_VOTE, map, context, ViewEntryFragment.this, REQ_VOTE).execute();
                    } else {
                        Util.showSnackbar(v, getString(R.string.msg_already_voted));
                    }
                    break;
                case R.id.vScrim:
                    if ("3".equals(result.getEntry().getContestType())) {
                        Intent intent = new Intent(activity, VideoViewActivity.class);
                        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIDEO);
                        intent.putExtra(Constant.KEY_TYPE, Constant.ResourceType.CONTEST);
                        intent.putExtra(Constant.KEY_DATA, result.getEntry().getRichContent());
                        intent.putExtra(Constant.KEY_URI, result.getEntry().getVideo());
                        startActivity(intent);
                    } else {
                        if (null != mediaPlayer) {
                            if (mediaPlayer.isPlaying()) {
                                ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_rounded_blue));
                                mediaPlayer.pause();
                            } else {
                                ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_rounded_bluew));
                                mediaPlayer.start();
                            }
                        } else {
                            playAudio();
                        }
                    }
                    break;
               /* case R.id.like_heart:
                    callReactionApi(AppConstantSes.URL_LIKE + mEventId, view);
                    resp.toggleLike();
                    ((ImageView) v.findViewById(R.id.ivLike)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentLike() ? R.drawable.gallery_like_active : R.drawable.gallery_like));

                    if (resp.isContentLike()) {
                        // view.setSelected(true);
                        ((SmallBangView) view).likeAnimation();
                    }

                    break;
                case R.id.favorite_heart:
                    callReactionApi(AppConstantSes.URL_FAVORITE, v);
                    resp.toggleFav();
                    ((ImageView) view.findViewById(R.id.ivFavorite)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFavourite() ? R.drawable.gallery_fav_selected : R.drawable.gallery_fav_unselected));
                    if (resp.isContentFavourite()) {
                        ((SmallBangView) view).likeAnimation();
                    }
                    break;
                case R.id.follow_heart:
                    callReactionApi(AppConstantSes.URL_FOLLOW, v);
                    resp.toggleFollow();
                    ((ImageView) view.findViewById(R.id.ivFollow)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFollow() ? R.drawable.gallery_follow_active : R.drawable.gallery_follow));
                    if (resp.isContentFollow()) {
                        ((SmallBangView) view).likeAnimation();
                    }
                    break;
                case R.id.appreciate_heart:
                    callReactionApi(AppConstantSes.URL_APPRECIATE + mEventId, v);
                    resp.toggleFollow();
                    ((ImageView) view.findViewById(R.id.ivAppreciate)).setImageDrawable(ContextCompat.getDrawable(context, resp.isContentFollow() ? R.drawable.gallery_appreciate : R.drawable.gallery_appreciate));
                    // if (resp.isContentFollow()) {
                    ((SmallBangView) view).likeAnimation();
                    //  }
                    break;*/

               /* case R.id.tvOwnerTitle:
                    int userId = resp.getOwnerId();

                    break;
                case R.id.ivCoverPhoto:
                    isCoverRequest = true;
                    if (null != resp.getCoverImageOptions())
                        showPopup(resp.getCoverImageOptions(), tvCoverOption, 1000);
                    break;

                case R.id.ivProfileImage:
                    isCoverRequest = false;
                    if (null != resp.getProfileImageOptions())
                        showPopup(resp.getProfileImageOptions(), tvProfileOption, 100);
                    // mGutterMenuUtils.showPopup(tvCoverOption, resp.getProfileOptionAsArray(), mBrowseList, ConstantVariables.USER_MENU_TITLE);
                    break;
    */
            }
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText((R.string.MSG_DELETE_CONFIRMATION_ENTRY));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_ENTRY_DELETE);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, String url) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false);
                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_CONTEST_ID, result.getEntry().getContestId());
                request.params.put(Constant.KEY_ID, result.getEntry().getParticipantId());
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
                                    if (REQ == REQ_DELETE) {
                                        CommonResponse res = new Gson().fromJson(response, CommonResponse.class);
                                        Util.showSnackbar(v, res.getResult().getSuccessMessage());
                                        activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                        onBackPressed();
                                    }

                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                somethingWrongMsg(v);
                                //updating upper layout ,if something went wrong
                                callMusicAlbumApi(REQ_UPDATE_UPPER);
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

    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        try {
            CustomLog.e("POPUP", "" + object2 + "  " + object2 + "  " + position);
            switch (object1) {


                case Constant.Events.REMOVE_PHOTO:
                    hideBaseLoader();
                    callMusicAlbumApi(REQ_UPDATE_UPPER);
                    break;
                //RESPONSE of  Entry vote api
                case REQ_VOTE:
                    try {
                        String response = (String) object2;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (err.isSuccess()) {
                                SuccessResponse succ = new Gson().fromJson(response, SuccessResponse.class);
                                Util.showSnackbar(v, succ.getResult().getSuccessMessage());
                                result.getEntry().toggleVote();
                                updateFabColor();
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                                //refresh list in case of any error
                                // onRefresh();
                            }
                        }
                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                    }
                    break;
                //RESPONSE of  Entry GRAPH api
                case REQ_GRAPH_DAFAULT:
                case REQ_GRAPH:
                    try {
                        String response = (String) object2;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (err.isSuccess()) {
                                ContestResponse graph = new Gson().fromJson(response, ContestResponse.class);
                                graphVo = graph.getResult().getGraph();
                                dateList = graphVo.getDate();

                                //set spinner only once
                                if (REQ_GRAPH_DAFAULT == object1) {
                                    graphOptions = graph.getResult().getGraphOptions();
                                    setSpinnerData();
                                }
                            }

                            updateChartData();
                        }
                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                    }
                    break;
               /* case Constant.Events.POPUP_SOWN:
                    //  fabCreateMedia.setRotation(45);
                    break;
                case Constant.Events.POPUP_HIDE:
                    // fabCreateMedia.setRotation(0);
                    this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
                    this.v.findViewById(R.id.fabCreateMedia).setVisibility(View.VISIBLE);
                    break;
                case Constant.Events.POPUP_ITEM:
                    this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
                    this.v.findViewById(R.id.fabCreateMedia).setVisibility(View.VISIBLE);
                    switch (position) {
                        case Constant.FormType.CREATE_ALBUM:
                            String url = AppConstantSes.URL_CREATE_ALBUM + mEventId;
                            Map<String, Object> map = new HashMap<>();
                            map.put("page_id", "" + mEventId);
                            fragmentManager.beginTransaction().replace(R.id.container_view, CreateAlbumForm.newInstance(Constant.FormType.CREATE_ALBUM, map, url, context.getResources().getString(R.string.title_activity_create_new_album))).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_VIDEO:
                            url = AppConstantSes.URL_CREATE_VIDEO + "page_id/" + mEventId;

                            fragmentManager.beginTransaction().replace(R.id.container_view,
                                    CreateVideoForm.newInstance(Constant.FormType.CREATE_VIDEO, url)).addToBackStack(null).commit();
                            break;
                        case Constant.FormType.CREATE_MUSIC:
                            break;
                        case Constant.FormType.CREATE_NOTE:
                            url = AppConstantSes.URL_CREATE_WRITING + "page_id/" + mEventId;
                            super.openForm(Constant.FormType.CREATE_NOTE, null, url, context.getResources().getString(R.string.title_create_note));
                            break;
                    }
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }


    private void callLikeApi(final int REQ_CODE, final View view, String url) {

        try {
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, view, result.getEntry());
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ID, mEventId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.ENTRY);
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

                                        /*if (REQ_CODE > REQ_DELETE) {
                                            JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("join");
                                            List<Options> opt = new Gson().fromJson(obj.toString(), List.class);
                                            result.getEntry().setOptions(opt);

                                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").optString("message"));
                                        }*/

                                    } else {
                                        //revert changes in case of error
                                        updateItemLikeFavorite(REQ_CODE, view, result.getEntry());
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

    public void updateItemLikeFavorite(int REQ_CODE, View view, CommonVO vo) {

        if (REQ_CODE == REQ_LIKE) {
            vo.setContentLike(!vo.isContentLike());
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
            ((TextView) view.findViewById(R.id.tvOptionText)).setText(result.getEntry().isContentLike() ? R.string.unlike : R.string.like);
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentLike() ? Constant.colorPrimary : Constant.text_color_1));
        } else if (REQ_CODE == REQ_FAVORITE) {
            vo.setContentFavourite(!vo.isContentFavourite());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFavourite() ? Constant.red : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        } else if (REQ_CODE == REQ_FOLLOW) {
            vo.setContentFollow(!vo.isContentFollow());
            ((ImageView) view.findViewById(R.id.ivOptionImage)).setColorFilter(Color.parseColor(vo.isContentFollow() ? Constant.colorPrimary : Constant.text_color_1));
            ((SmallBangView) view.findViewById(R.id.vBang)).likeAnimation();
        }

    }

    //graph callbacks
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    //graph callbacks
    @Override
    public void onNothingSelected() {

    }


    //Spinner callbacks
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isNetworkAvailable(context)) {
            v.findViewById(R.id.rlChartMain).setVisibility(View.GONE);
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
            callGraphApi(graphOptions.get(position).getName(), REQ_GRAPH);
        } else {
            notInternetMsg(v);
        }
    }

    //Spinner callbacks
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //music player callback
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!isAdded()) return;
        v.findViewById(R.id.pbLoader).setVisibility(View.GONE);
        // ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_rounded_bluew));
        mp.start();
    }
}
