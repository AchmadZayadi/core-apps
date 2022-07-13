package com.sesolutions.ui.storyview;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.customviews.CustomJzVideo_story;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesGestureAdapter;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newd.Jzvd;


public class StoryFragment extends BaseFragment implements StoryPlayerProgressView.StoryPlayerListener, View.OnClickListener, OnUserClickedListener<Integer, Object> {
    private static final int REQ_STORY_MUTE = 669;
    private static final int REQ_STORY_LIKE = 670;
    private View v;
    StoryPlayerProgressView storyPlayerProgressView;
    ImageView imageView;
    private TextView tvStoryContent;
    TextView time;
    // StoryModel stories;
    StoryPreference storyPreference;
    private View reverse, center, skip;
    private CustomJzVideo_story jzVideoPlayerStandard;
    private HttpProxyCacheServer proxy;
    private StoryModel model;
    private int position;
    private OnUserClickedListener<Integer, Object> listener;
    private boolean hasPausedInvisible;
    //private CircularProgressBar pbImageLoader;
    private ProgressBar pbImageLoader;
    ProgressBar progressBar;

    public static StoryFragment newInstance(StoryModel model, int position, OnUserClickedListener<Integer, Object> listener) {
        StoryFragment frag = new StoryFragment();
        frag.model = model;
        frag.position = position;
        frag.listener = listener;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.ses_activity_story_player, container, false);
        // initScreenData();
        return v;
    }


    @Override
    public void initScreenData() {
        storyPlayerProgressView = v.findViewById(R.id.progressBarView);
        Util.showImageWithGlide(v.findViewById(R.id.ivProfileStory), model.getUserImage(), context, R.drawable.placeholder_square);
        ((TextView) v.findViewById(R.id.storyUserName)).setText(model.getUsername());
        tvStoryContent = v.findViewById(R.id.tvStoryContent);
        v.findViewById(R.id.ivOption).setOnClickListener(this);
        time = v.findViewById(R.id.storyTime);
        progressBar=v.findViewById(R.id.progressBar);
        pbImageLoader = v.findViewById(R.id.pbImageLoader);
        reverse = v.findViewById(R.id.reverse);
        center = v.findViewById(R.id.center);
        skip = v.findViewById(R.id.skip);
        jzVideoPlayerStandard = v.findViewById(R.id.storyVideo);
        jzVideoPlayerStandard.setListener(this);

        proxy = ((MainApplication) context.getApplicationContext()).getProxy(context);
        skip.setOnClickListener(this);
        //center.setOnClickListener(this);
        reverse.setOnClickListener(this);
        v.findViewById(R.id.llBottomLike).setOnClickListener(this);
        storyPlayerProgressView.setSingleStoryDisplayTime(5000);
        imageView = v.findViewById(R.id.storyImage);
        storyPreference = new StoryPreference(context);
        initReactionView();
        initStoryProgressView();

        /*Intent intent = getActivity().getIntent();
        if (intent != null) {
            stories = intent.getParcelableArrayListExtra(STORY_IMAGE_KEY);
            initStoryProgressView();
        }*/
    }

    private RecyclerView rvReply;
    private ReactionReplyAdapter reactionAdapter;
    private List<ReactionPlugin> reactionList;

    private void initReactionView() {
        rvReply = v.findViewById(R.id.rvReply);
        reactionList = SPref.getInstance().getReactionPlugins(context);
        if (null != reactionList && reactionList.size() > 0) {
            reactionList.add(0, new ReactionPlugin());
            reactionAdapter = new ReactionReplyAdapter(reactionList, context, this);
            rvReply.setAdapter(reactionAdapter);
        }
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
        if (hasPausedInvisible) {
            hasPausedInvisible = false;
            resumeProgress();
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

        markAsViewed(index);
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
        //rvReply.setVisibility(View.VISIBLE);
        if (model.getImages().get(index).isVideo()) {
            Jzvd.releaseAllVideos();
            // jzVideoPlayerStandard.playOnThisJzvd();
        }
    }

    @Override
    public void onPreStartPlaying(int index) {
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

        rvReply.setVisibility(model.getImages().get(index).canReact() ? View.VISIBLE : View.GONE);
        new StoryReactionHelper().init(model.getImages().get(index), v.findViewById(R.id.llBottomLike));
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
        listener.onItemClicked(Constant.Events.NEXT, null, position);
    }

    private void loadImage(int index) {
        try {
            jzVideoPlayerStandard.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            // Util.showImageWithProgress(imageView, pbLoader, model.getImages().get(index).getMediaUrl(), index, this);
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

    private void markAsViewed(int index) {
        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_STORY_ID, model.getImages().get(index).getStoryId());
            map.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
            new ApiController(Constant.URL_STORY_VIEWED, map, context, null, -1).execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reverse:
                storyPlayerProgressView.playPrevious();
                break;
            case R.id.skip:
                storyPlayerProgressView.playNext();
                break;
            case R.id.llBottomLike:
                try {
                   /* Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORY);
                    map.put(Constant.KEY_ID, model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getStoryId());
                    openReactionViewfragment(map);*/

                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.REACTION);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORY);
                    intent.putExtra(Constant.KEY_ID, model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getStoryId());
                    startActivity(intent);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case R.id.ivOption:
                pauseProgress();
                Util.showOptionsPopUp(v, storyPlayerProgressView.getCurrentIndex(), model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getOptions(), this);
                break;
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
            case Constant.Events.POPUP:
                resumeProgress();
                break;
            case Constant.Events.NEXT:
                CustomLog.e("onStateErrorCustom", "NEXT");
                // storyPlayerProgressView.setCurrentProgressIndex(position);
                storyPlayerProgressView.playNext();
                break;
            case Constant.Events.FEED_UPDATE_OPTION:
                handleOptionClick(Integer.parseInt("" + data), position);
                break;
            case REQ_STORY_MUTE:
                if (null != data) {
                    StoryResponse res = new Gson().fromJson("" + data, StoryResponse.class);
                    if (res.isSuccess()) {
                        model.toggleMuteOption(res.getResult().getOption());
                    }
                }
                break;
            case REQ_STORY_LIKE:
                if (null != data) {
                    FeedLikeResponse res = new Gson().fromJson("" + data, FeedLikeResponse.class);
                    if (res.isSuccess()) {
                        model.getImages().get(position).updateFinalLike(res.getResult());
                        if (storyPlayerProgressView.getCurrentIndex() == position)
                            new StoryReactionHelper().init(model.getImages().get(position), v.findViewById(R.id.llBottomLike));
                    }
                }
                break;

            case Constant.Events.REPLY:
                List<Item_user> list = new ArrayList<>();
                list.add(new Item_user(model.getUserId(), model.getUsername(), model.getUserImage()));
                openComposeActivity(list, getString(R.string.replied_to_story));
                break;
            case Constant.Events.IMAGE_5:
                updateReactionText(position);
                ReactionPlugin reactionVo = reactionList.get(position);

                // reactionId = reactionVo.getReactionId();
                // feedActivityList.get(position).updateLikeTemp(true, new Like(reactionVo.getImage(), reactionVo.getTitle()));
                //adapterFeedMain.notifyItemChanged(postion);
                callLikeApi(reactionVo.getReactionId(), position);
                break;
           /* case Constant.Events.DECLINE:
                //it means current image has failed to load try to show to next story
                storyPlayerProgressView.playNext();
                break;
            case Constant.Events.SET_LOADED:
                //it means current image has successfully loaded so start progress
                storyPlayerProgressView.startProgressFor(position);
                break;*/

        }
        return false;
    }

    private void callLikeApi(int reactionId, int position) {
        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_REACTION_TYPES, reactionId);
            map.put(Constant.KEY_RESOURCE_ID, model.getImages().get(storyPlayerProgressView.getCurrentIndex()).getStoryId());
            map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.STORY);
            new ApiController(Constant.URL_MUSIC_LIKE, map, context, this, REQ_STORY_LIKE).setExtraKey(position).execute();
        }
    }

    private void updateReactionText(int position) {

    }

    private void handleOptionClick(int storyPosition, int optionPosition) {
        Options opt = model.getImages().get(storyPosition).getOptions().get(optionPosition);
        switch (opt.getName()) {
            case Constant.OptionType.REPORT:
                goToReport(Constant.ResourceType.USER + "_" + model.getUserId());
                break;
            case Constant.OptionType.MUTE:
                callMuteAPI(storyPosition);
                break;
            case Constant.OptionType.UNMUTE:
                callUnMuteAPI(storyPosition, opt.getMuteId());
                break;
        }
    }

    private void callMuteAPI(int storyPosition) {
        //change label of "mute"
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_USER_ID, model.getUserId());
        new ApiController(Constant.URL_STORY_MUTE, map, context, this, REQ_STORY_MUTE).setExtraKey(storyPosition).execute();
    }

    private void callUnMuteAPI(int storyPosition, int muteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("mute_id", muteId);
        new ApiController(Constant.URL_STORY_UNMUTE, map, context, this, REQ_STORY_MUTE).setExtraKey(storyPosition).execute();
    }
}
