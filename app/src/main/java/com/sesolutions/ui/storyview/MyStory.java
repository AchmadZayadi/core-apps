package com.sesolutions.ui.storyview;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.customviews.CustomJzVideo_story;
import com.sesolutions.ui.dashboard.StaticShare;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.SesGestureAdapter;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newd.Jzvd;


public class MyStory extends BaseFragment implements StoryPlayerProgressView.StoryPlayerListener, View.OnClickListener, OnUserClickedListener<Integer, Object> {
    private static final int REQ_VIEWERS = 668;
    private static final int REQ_DELETE = 669;
    private View v;
    public static final String STORY_IMAGE_KEY = "storyImages";
    StoryPlayerProgressView storyPlayerProgressView;
    ImageView imageView;
    private TextView tvStoryContent;
    TextView time;
    StoryPreference storyPreference;
    private View reverse, center, skip;
    private CustomJzVideo_story jzVideoPlayerStandard;
    private HttpProxyCacheServer proxy;
    private StoryModel model;
    private StoryViewerAdapter adapter;
    private boolean hasPausedInvisible;
    private boolean isOwner;
    ProgressBar progressBar;


    public static MyStory newInstance(StoryModel model, boolean isOwner) {
        MyStory frag = new MyStory();
        frag.model = model;
        frag.isOwner = isOwner;
        return frag;
    }

    private List<StoryModel> viewerList;

    private void setViewerRecyclerView() {
        try {
            RecyclerView recyclerView = llBottomSheet.findViewById(R.id.recyclerView);
            viewerList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new StoryViewerAdapter(viewerList, context, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_story, container, false);
        initScreenData();
        return v;
    }

    private BottomSheetBehavior<View> mBottomSheetOptions;
    private View llBottomSheet;
    private ProgressBar pbImageLoader;
    private TextView tvBsViewCount;

    private void setUpBottomSheet() {
        tvBsViewCount = v.findViewById(R.id.tvBsViewCount);
        tvBsViewCount.setOnClickListener(this);
        pbImageLoader = v.findViewById(R.id.pbImageLoader);
        llBottomSheet = v.findViewById(R.id.rlBottomSheet);

        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_filled_upper_primary);
        gdr.setColor(SesColorUtils.getPrimaryColor(context));
        v.findViewById(R.id.llBSHeader).setBackground(gdr);

        setViewerRecyclerView();
        llBottomSheet.findViewById(R.id.ivRefresh).setOnClickListener(this);
        llBottomSheet.findViewById(R.id.ivDelete).setOnClickListener(this);
        updateRefreshIcon(false);
        mBottomSheetOptions = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetOptions.setPeekHeight(isOwner ? context.getResources().getDimensionPixelSize(R.dimen.height_my_story_bottom_sheet) : 0);
        mBottomSheetOptions.setBottomSheetCallback(bottomSheetListener);
        mBottomSheetOptions.setHideable(!isOwner);
        mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void updateRefreshIcon(boolean isLoading) {
        if (isLoading) {
            llBottomSheet.findViewById(R.id.ivRefresh).setVisibility(View.GONE);
            llBottomSheet.findViewById(R.id.pbLoader).setVisibility(View.VISIBLE);
        } else {
            llBottomSheet.findViewById(R.id.pbLoader).setVisibility(View.GONE);
            llBottomSheet.findViewById(R.id.ivRefresh).setVisibility(View.VISIBLE);
        }
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetListener = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int state) {
            try {
                switch (state) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (isOwner)
                            fab.show();
                        resumeProgress();
                        llBottomSheet.setOnClickListener(null);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        getViewers(false);
                        llBottomSheet.setOnClickListener(MyStory.this);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (isOwner)
                            fab.hide();
                        pauseProgress();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        CustomLog.e("llSongOptions", "" + state);
                        break;
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {
            tvBsViewCount.setAlpha(1 - v);
        }
    };

    private void getViewers(boolean isUserClicked) {
        if (isUserClicked) {
            viewerList.clear();
            adapter.notifyDataSetChanged();
        } else if (viewerList.size() > 0) return;

        if (isNetworkAvailable(context)) {
            updateRefreshIcon(true);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_STORY_ID, model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getStoryId());
            new ApiController(Constant.URL_STORY_VIEWERS, map, context, this, REQ_VIEWERS).setExtraKey(REQ_VIEWERS).execute();
        } else {
            ((TextView) llBottomSheet.findViewById(R.id.tvNoData)).setText("No Internet Connection");
            llBottomSheet.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
        }
    }

    private void clearViewerListUI() {
        viewerList.clear();
        adapter.notifyDataSetChanged();
        llBottomSheet.findViewById(R.id.llNoData).setVisibility(View.GONE);
    }

    private FloatingActionButton fab;
    long startTime = 0;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            float percentage=((30-seconds)*100)/30;
            int per= (int) percentage;
            Log.e("per:",""+percentage);
            Log.e("per:",""+per);

            progressBar.setProgress(per);

            timerHandler.postDelayed(this, 1000);
        }
    };

    private void init() {
        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        updateFabColor(fab);
        if (!isOwner)
            fab.hide();

        //v.findViewById(R.id.tvStoryContent).setVisibility(View.GONE);
        storyPlayerProgressView = v.findViewById(R.id.progressBarView);
        Util.showImageWithGlide(v.findViewById(R.id.ivProfileStory), model.getUserImage(), context, R.drawable.placeholder_square);
        ((TextView) v.findViewById(R.id.storyUserName)).setText(model.getUsername());
        tvStoryContent = v.findViewById(R.id.tvStoryContent);
        // v.findViewById(R.id.ivOption).setOnClickListener(this);
        v.findViewById(R.id.ivOption).setVisibility(View.INVISIBLE);
        time = v.findViewById(R.id.storyTime);
        pbImageLoader = v.findViewById(R.id.pbImageLoader);
        reverse = v.findViewById(R.id.reverse);
        center = v.findViewById(R.id.center);
        skip = v.findViewById(R.id.skip);
        jzVideoPlayerStandard = v.findViewById(R.id.storyVideo);
        progressBar = v.findViewById(R.id.progressBar);
        jzVideoPlayerStandard.setListener(this);

        proxy = ((MainApplication) context.getApplicationContext()).getProxy(context);
        skip.setOnClickListener(this);
        reverse.setOnClickListener(this);
        storyPlayerProgressView.setSingleStoryDisplayTime(5000);
        imageView = v.findViewById(R.id.storyImage);
        storyPreference = new StoryPreference(context);
        initStoryProgressView();

        try {
            timerHandler.removeCallbacks(timerRunnable);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void initScreenData() {
        //if (isOwner)
        setUpBottomSheet();
        init();

    }

    private void playVideo(int index) {
        jzVideoPlayerStandard.setUp(proxy.getProxyUrl(model.getImages().get(index).getMediaUrl())
                , " "
                , Jzvd.SCREEN_WINDOW_NORMAL);
        jzVideoPlayerStandard.setIndex(index);
        jzVideoPlayerStandard.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        Jzvd.clearSavedProgress(context, (String) jzVideoPlayerStandard.jzDataSource.getCurrentUrl());

        jzVideoPlayerStandard.startButton.performClick();
        //jzVideoPlayerStandard.startVideo();
        //  storyPlayerProgressView.startProgressFor(index, jzVideoPlayerStandard.getDuration());

    }

    public void pauseProgress() {
        if (null != storyPlayerProgressView)
            storyPlayerProgressView.pauseProgress();
    }

    public void resumeProgress() {
        if (null != storyPlayerProgressView)
            storyPlayerProgressView.resumeProgress();
    }

    @Override
    public void onPause() {
        if (null != storyPlayerProgressView) {
            hasPausedInvisible = true;
            storyPlayerProgressView.pauseProgress();
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
        if (hasPausedInvisible) {
            hasPausedInvisible = false;
            resumeProgress();
        }

        if (StaticShare.TASK_PERFORMED == Constant.Events.STORY_CREATE) {
            onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (storyPlayerProgressView.progressAnimator != null) {
            storyPlayerProgressView.progressAnimator.cancel(); //cancelAnimation();
        }
    }

    private void initStoryProgressView() {
        if (model != null && model.getImages().size() > 0) {
            storyPlayerProgressView.setStoryPlayerListener(this);
            storyPlayerProgressView.setProgressBarsCount(model.getImages().size());
            setTouchListener();
        }
    }

    private void setTouchListener() {
        GestureDetector gestureDetector = new GestureDetector(context, new SesGestureAdapter());

        reverse.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                storyPlayerProgressView.playPrevious();
                return true;
            } else {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //pause
                    storyPlayerProgressView.pauseProgress();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //resume
                    storyPlayerProgressView.resumeProgress();
                    return true;
                } else {
                    return false;
                }
            }
        });

        skip.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                storyPlayerProgressView.playNext();
                return true;
            } else {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //pause
                    storyPlayerProgressView.pauseProgress();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //resume
                    storyPlayerProgressView.resumeProgress();
                    return true;
                } else {
                    return false;
                }
            }
        });


        center.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                storyPlayerProgressView.playNext();
                return true;
            } else {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //pause
                    storyPlayerProgressView.pauseProgress();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //resume
                    storyPlayerProgressView.resumeProgress();
                    return true;
                } else {
                    return false;
                }
            }
        });

    }


    @Override
    public void onStartedPlaying(int index) {
        time.setText(Util.getDateDiff(context, model.getImages().get(index).getCreatedDate()));
        storyPreference.setStoryVisited(model.getImages().get(index).getStoryId());
    }

    @Override
    public void onPausePlaying(int index) {
        if (model.getImages().get(index).isVideo()) {
            if (jzVideoPlayerStandard.currentState == Jzvd.CURRENT_STATE_PLAYING)
                jzVideoPlayerStandard.startButton.performClick();
        }
    }

    public void onResumePlaying(int index) {
        if (model.getImages().get(index).isVideo()) {
            if (jzVideoPlayerStandard.currentState == Jzvd.CURRENT_STATE_PAUSE)
                jzVideoPlayerStandard.startButton.performClick();
        }
    }

    public void onCancelPlaying(int index) {
        if (model.getImages().get(index).isVideo()) {
            Jzvd.releaseAllVideos();
            // jzVideoPlayerStandard.playOnThisJzvd();
        }
    }

    private void updateViewCountUI(int index) {
        if (isOwner) {
            tvBsViewCount.setText("" + model.getImages().get(index).getViewCount());
            ((TextView) llBottomSheet.findViewById(R.id.tvBSTitle)).setText(getString(R.string.viewed_by_count, model.getImages().get(index).getViewCount()));
        }
    }

    @Override
    public void onPreStartPlaying(int index) {
        try {
            updateViewCountUI(index);
            clearViewerListUI();
            if (null != model.getImages().get(index).getComment()) {
                tvStoryContent.setVisibility(View.VISIBLE);
                tvStoryContent.setText(model.getImages().get(index).getComment());
            } else {
                tvStoryContent.setVisibility(View.GONE);
            }
            if (model.getImages().get(index).isVideo()) {
                storyPlayerProgressView.setCurrentProgressIndex(index);
                playVideo(index);
            } else
                loadImage(index);

            new StoryReactionHelper().init(model.getImages().get(index), v.findViewById(R.id.llBottomLike));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

        /*@Override
        public void onBackPressed() {
            super.onBackPressed();
        }*/

    @Override
    public void onFinishedPlaying() {
        showNextStory();
    }

    private void showNextStory() {
        onBackPressed();
    }

    private void loadImage(int index) {
        try {
            jzVideoPlayerStandard.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            pbImageLoader.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(model.getImages().get(index).getMediaUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            pbImageLoader.setVisibility(View.GONE);
                            storyPlayerProgressView.playNext();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pbImageLoader.setVisibility(View.GONE);
                            storyPlayerProgressView.startProgressFor(index);
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reverse:
                storyPlayerProgressView.playPrevious();
                break;
            case R.id.ivRefresh:
                getViewers(true);
                break;
            case R.id.ivDelete:
                showDeleteDialog(this, storyPlayerProgressView.getCurrentIndex()
                        , getString(R.string.msg_story_delete)
                        , R.string.delete
                        , R.string.cancel);
                break;
            case R.id.skip:
                storyPlayerProgressView.playNext();
                break;
            case R.id.fab:
                startActivityForResult(
                        new ImageEditor.Builder(getActivity())
                                .setStickerAssets("stickers")
                                // .setQuote(title)
                                // .setQuoteSource(source)
                                .getMultipleEditorIntent(),
                        ImageEditor.RC_IMAGE_EDITOR);
                break;
            case R.id.ivOption:
                pauseProgress();
                Util.showOptionsPopUp(v, -1, model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getOptions(), this);
                break;
            case R.id.tvBsViewCount:
                if (isOwner)
                    fab.hide();
                pauseProgress();
                mBottomSheetOptions.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
        }
    }

    private void deleteCurrentStory(int position) {
        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_STORY_ID, model.getImages().get(position).getStoryId());
            new ApiController(Constant.URL_STORY_DELETE, map, context, this, REQ_DELETE).setExtraKey(position).execute();

            if (model.getImages().size() > 1) {
                model.getImages().remove(position);
                mBottomSheetOptions.setBottomSheetCallback(null);
                mBottomSheetOptions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                init();
            } else {
                onBackPressed();
            }

        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case Constant.Events.MUSIC_PROGRESS:
                //CustomLog.e("duration1", "" + jzVideoPlayerStandard.getDuration());
                break;
            case Constant.Events.MUSIC_PREPARED:
                //CustomLog.e("duration", "" + jzVideoPlayerStandard.getDuration());
                storyPlayerProgressView.startProgressFor(position, jzVideoPlayerStandard.getDuration());
                break;
            case Constant.Events.NEXT:
                storyPlayerProgressView.playNext();
                break;
            case Constant.Events.OK:
                deleteCurrentStory(position);
                break;
            case REQ_DELETE:
                if (null != data) {
                    StoryResponse res = new Gson().fromJson("" + data, StoryResponse.class);
                    if (res.isSuccess()) {
                            /*if (null != res.getResult().getViewers()) {
                                viewerList.addAll(res.getResult().getViewers());
                            }
                            updateViewerListAdapter();*/
                    }
                }
                break;

            case REQ_VIEWERS:
                updateRefreshIcon(false);
                if (null != data) {
                    StoryResponse res = new Gson().fromJson("" + data, StoryResponse.class);
                    if (res.isSuccess()) {
                        if (null != res.getResult().getViewers()) {
                            viewerList.addAll(res.getResult().getViewers());
                        }
                        updateViewerListAdapter();
                    }
                }
                break;
        }
        return false;
    }

    private void updateViewerListAdapter() {
        adapter.notifyDataSetChanged();
        if (viewerList.size() > 0) {
            llBottomSheet.findViewById(R.id.llNoData).setVisibility(View.GONE);
        } else {
            ((TextView) llBottomSheet.findViewById(R.id.tvNoData)).setText(R.string.msg_no_viewers);
            llBottomSheet.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
        }

    }
}
