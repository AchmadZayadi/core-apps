package com.sesolutions.ui.common;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.animate.YoYo;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Emotion;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.ui.albums.AlbumParentFragment;
import com.sesolutions.ui.albums.BrowseAlbumFragment;
import com.sesolutions.ui.albums.BrowsePhotoFragment2;
import com.sesolutions.ui.albums.SearchAlbumFragment;
import com.sesolutions.ui.albums.ViewAlbumFragment;
import com.sesolutions.ui.albums_core.C_AlbumParentFragment;
import com.sesolutions.ui.albums_core.C_ViewAlbumFragment;
import com.sesolutions.ui.articles.ArticleParentFragment;
import com.sesolutions.ui.articles.ViewArticleFragment;
import com.sesolutions.ui.blogs.BlogParentFragment;
import com.sesolutions.ui.blogs.BrowseBlogsFragment;
import com.sesolutions.ui.blogs.SearchBlogFragment;
import com.sesolutions.ui.blogs.ViewBlogFragment;
import com.sesolutions.ui.blogs_core.C_BlogParentFragment;
import com.sesolutions.ui.blogs_core.C_ViewBlogFragment;
import com.sesolutions.ui.bookings.BookingParentFragment;
import com.sesolutions.ui.bookings.ViewProfessionalFragment;
import com.sesolutions.ui.bookings.ViewServiceFragment;
import com.sesolutions.ui.business.BusinessFragment;
import com.sesolutions.ui.business.BusinessInfoFragment;
import com.sesolutions.ui.business.BusinessMemberFragment;
import com.sesolutions.ui.business.BusinessParentFragment;
import com.sesolutions.ui.business.ViewBusinessFragment;
import com.sesolutions.ui.classified.ClassifiedParentFragment;
import com.sesolutions.ui.classified.ViewClassifiedFragment;
import com.sesolutions.ui.clickclick.FollowingFragment;
import com.sesolutions.ui.clickclick.channel.ChannelFragment;
import com.sesolutions.ui.clickclick.me.CreateEditChannelFragment;
import com.sesolutions.ui.clickclick.me.FollowFollowingParent;
import com.sesolutions.ui.clickclick.me.FollowFollowingUser;
import com.sesolutions.ui.clickclick.me.OtherFragment;
import com.sesolutions.ui.comment.CommentFragment;
import com.sesolutions.ui.comment.ReactionViewFragment;
import com.sesolutions.ui.contest.ContestAwardFragment;
import com.sesolutions.ui.contest.ContestParentFragment;
import com.sesolutions.ui.contest.ViewContestFragment;
import com.sesolutions.ui.contest.ViewEntryFragment;
import com.sesolutions.ui.core_forum.CoreForumUtil;
import com.sesolutions.ui.core_search.SearchFragment;
import com.sesolutions.ui.courses.classroom.ClassroomParentFragment;
import com.sesolutions.ui.courses.classroom.ViewClassroomFragment;
import com.sesolutions.ui.courses.course.CourseFragment;
import com.sesolutions.ui.courses.course.CourseInfoFragment;
import com.sesolutions.ui.courses.course.CourseParentFragment;
import com.sesolutions.ui.courses.course.ViewCourseFragment;
import com.sesolutions.ui.courses.lecture.LectureFragment;
import com.sesolutions.ui.courses.test.TestFragment;
import com.sesolutions.ui.credit.CreditUtil;
import com.sesolutions.ui.crowdfunding.CrowdUtil;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.dashboard.PostEditFragment;
import com.sesolutions.ui.dashboard.PostFeedFragment;
import com.sesolutions.ui.dashboard.RateFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.dashboard.ShareSEFragment;
import com.sesolutions.ui.dashboard.TnCFragment;
import com.sesolutions.ui.dashboard.ViewFeedFragment;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.event_core.CEventParentFragment;
import com.sesolutions.ui.event_core.CoreEventDiscussionView;
import com.sesolutions.ui.event_core.ViewCEventFragment;
import com.sesolutions.ui.events.DiscussionFragment;
import com.sesolutions.ui.events.EventInfoFragment;
import com.sesolutions.ui.events.EventMapFragment;
import com.sesolutions.ui.events.EventParentFragment;
import com.sesolutions.ui.events.EventVideoFragment;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.events.ViewEventFragment;
import com.sesolutions.ui.forum.ForumUtil;
import com.sesolutions.ui.games.GameBrowseFragment;
import com.sesolutions.ui.group_core.CGroupParentFragment;
import com.sesolutions.ui.group_core.CoreGroupDiscussionView;
import com.sesolutions.ui.group_core.ViewCGroupFragment;
import com.sesolutions.ui.groups.GroupInfoFragment2;
import com.sesolutions.ui.groups.GroupParentFragment;
import com.sesolutions.ui.groups.ViewGroupFragment;
import com.sesolutions.ui.job.JobParentFragment;
import com.sesolutions.ui.member.MemberFragment;
import com.sesolutions.ui.member.MemberRecentviewedFragment;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.member.TagSuggestionFragment;
import com.sesolutions.ui.message.MessageDashboardFragment;
import com.sesolutions.ui.multistore.MultiStoreParentFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.music_album.MusicAlbumFragment;
import com.sesolutions.ui.music_album.MusicListFragment;
import com.sesolutions.ui.music_album.MusicParentFragment;
import com.sesolutions.ui.music_album.SearchMusicAlbumFragment;
import com.sesolutions.ui.music_album.ViewArtistFragment;
import com.sesolutions.ui.music_album.ViewMusicAlbumFragment;
import com.sesolutions.ui.music_album.ViewPlaylistFragment;
import com.sesolutions.ui.music_album.ViewSongFragment;
import com.sesolutions.ui.music_core.CMusicUtil;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.ui.news.NewsParentFragment;
import com.sesolutions.ui.news.ViewNewsFragment;
import com.sesolutions.ui.notification.NotificationFragment;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageFragment;
import com.sesolutions.ui.page.PageInfoFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.page.PageMemberFragment;
import com.sesolutions.ui.page.PageParentFragment;
import com.sesolutions.ui.page.PageServicesFragment;
import com.sesolutions.ui.page.PageVideoFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.page.ViewPageFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.poll.PollParentFragment;
import com.sesolutions.ui.poll.PollViewFragment;
import com.sesolutions.ui.poll.profile_poll.ProfilePollFragment;
import com.sesolutions.ui.poll_core.CPollParentFragment;
import com.sesolutions.ui.poll_core.CViewPollFragment;
import com.sesolutions.ui.postfeed.StickerChildFragment;
import com.sesolutions.ui.prayer.PrayerParentFragment;
import com.sesolutions.ui.prayer.ViewPrayerCategoryFragment;
import com.sesolutions.ui.prayer.ViewPrayerFragment;
import com.sesolutions.ui.profile.InfoFragment;
import com.sesolutions.ui.profile.ProfileMapFragment;
import com.sesolutions.ui.profile.SuggestionViewFragment;
import com.sesolutions.ui.profile.VideoFeedFragment;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.ui.qna.QAParentFragment;
import com.sesolutions.ui.qna.ViewQuestionFragment;
import com.sesolutions.ui.quotes.QuotesParentFragment;
import com.sesolutions.ui.quotes.ViewPhotoQuoteFragment;
import com.sesolutions.ui.quotes.ViewQuoteCategoryFragment;
import com.sesolutions.ui.recipe.RecipeParentFragment;
import com.sesolutions.ui.recipe.ViewRecipeFragment;
import com.sesolutions.ui.resume.CreateEditExperienceFragment;
import com.sesolutions.ui.resume.MyresumeList;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.settings.ContactUsFragment;
import com.sesolutions.ui.settings.SettingFragment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.ui.store.StoreInfoFragment;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.ui.store.ViewStoreFragment;
import com.sesolutions.ui.store.product.ProductAdapter;
import com.sesolutions.ui.store.product.ProductFragment;
import com.sesolutions.ui.store.product.ProductInfoFragment;
import com.sesolutions.ui.storyview.ArchiveFragment;
import com.sesolutions.ui.storyview.MyStory;
import com.sesolutions.ui.storyview.StoryModel;
import com.sesolutions.ui.thought.ThoughtParentFragment;
import com.sesolutions.ui.thought.ViewThoughtCategoryFragment;
import com.sesolutions.ui.thought.ViewThoughtFragment;
import com.sesolutions.ui.video.SearchVideoFragment;
import com.sesolutions.ui.video.VideoAlbumFragment;
import com.sesolutions.ui.video.VideoParentFragment;
import com.sesolutions.ui.video.ViewChannelFragment;
import com.sesolutions.ui.video.ViewPlaylistVideoFragment;
import com.sesolutions.ui.wish.ViewPhotoWishFragment;
import com.sesolutions.ui.wish.ViewWishCategoryFragment;
import com.sesolutions.ui.wish.WishParentFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommonActivity extends BaseActivity implements View.OnClickListener, MediaController.MediaPlayerControl, OnUserClickedListener<Integer, Object> {


    public CardView cvMusicMain;
    private ProgressDialog progressDialog;

    private FragmentManager fragmentManager;
    //service
    private MusicService musicSrv;
    // private Intent playIntent;
    //binding
    private boolean musicBound = true;//false;
    //activity and playback pause flagsGO_TO_EDIT_FEED
    private boolean paused = false, playbackPaused = false;
    private ProgressBar seekbar;
    private TextView tvSongTitle;
    private ImageView fabPlay;
    private ImageView ivSongImage;
    private Drawable dPause;
    private Drawable dPlay;
    private Albums pendingSong;
    private ProgressBar pbLoad;
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(new ArrayList<Albums>());
            musicBound = true;
            //  musicSrv.removeAllListeners();
            musicSrv.setProgressListener(Constant.Listener.COMMON, CommonActivity.this);
            ((MainApplication) getApplication()).setMusicService(musicSrv);
            if (null != pendingSong) {
                showMusicLayout();
                playSong(pendingSong);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("my_pid", -1) == android.os.Process.myPid()) {
                // app was not killed
                CustomLog.e("app_state_form_common_activity", "app process was not killed");

            } else {
                // app was killed
                CustomLog.e("app_state_from_common_activity", "app process was killed");
                Intent intent = new Intent(this, SplashAnimatedActivity.class);
                intent.putExtra(Constant.KEY_COOKIE, Constant.SESSION_ID);
                finish();
                startActivity(intent);
            }
        }
        setContentView(R.layout.activity_welcome);

        if (AppConfiguration.isAdEnabled) {
            MobileAds.initialize(this, getResources().getString(R.string.ad_mob_id));
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_mob_unit_interstial_id));
            mInterstitialAd.setAdListener(adListener);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

        findViewById(R.id.main).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        fragmentManager = getSupportFragmentManager();
        dPause = ContextCompat.getDrawable(this, R.drawable.pause_rounded_bluew);
        dPlay = ContextCompat.getDrawable(this, R.drawable.play_rounded_blue);
        init();

        try {
            Intent in = getIntent();
            Uri data = in.getData();
            CallUriMethod(data.toString());

        } catch (Exception e) {
            e.printStackTrace();
            getBundle(getIntent());
        }
    }

    DeeplinkingModel pmodel;

    private void CallUriMethod(String urlmethod) {
        if (isNetworkAvailable(this)) {
            try {
                HttpRequestVO request = new HttpRequestVO(urlmethod + "?getDeepLinkingParams=1");
                //  request.headres.put(Constant.KEY_COOKIE, getCookie());
                //    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("r233333333", "" + response);
                            if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/page-directory")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openViewPageFragment(pmodel.getPage_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/albums/photo/view/album_id")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToGallaryFragment(
                                        pmodel.getPhoto_id(),
                                        pmodel.getAlbum_id()
                                        , pmodel.getResource_type()
                                        , pmodel.getResource_type(),
                                        "https://vavci-social.s3.amazonaws.com/public/album_photo/f7/16/9516d07178d29ec2b93672473b381d1f.jpg"
                                );
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/video/channel")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToViewChannelFragment(pmodel.getChanel_id(), false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/music/album")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                Log.e("album id", "" + pmodel.getAlbum_id());
                                goToViewMusicAlbumFragment(pmodel.getAlbum_id(),
                                        false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/music/song")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToSongsView(pmodel.getAlbumsong_id(), false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/booking/service")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                Log.e("getService_id", "" + pmodel.getService_id());
                                gotoclassroom(pmodel.getService_id(), false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/booking/professional")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                Log.e("getService_id", "" + pmodel.getService_id());
                                goToProfessionalView(pmodel.getProfessional_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/albums")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"album")){
                                    goToViewAlbumFragment(pmodel.getAlbum_id(), false);
                                }else {
                                    goToViewAlbumBasicFragment(pmodel.getAlbum_id(), false);
                                }
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/video")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openVideoFeedFragment2(pmodel.getVideo_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/video")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openVideoFeedFragment2(pmodel.getVideo_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/blog")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"blog")){
                                    goToViewBlogFragment(pmodel.getBlog_id(), false);
                                }else {
                                    goToViewBlogBasicFragment(pmodel.getBlog_id(), false);
                                }
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/prayers")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToPrayerView(pmodel.getPrayer_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/thoughts")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToThoughtView(pmodel.getThought_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/recipe")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openViewRecipeFragment(pmodel.getRecipe_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/wishes")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToWishView(pmodel.getWishe_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/quotes")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToQuoteView(pmodel.getQuote_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/contest")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                viewContestFragment(pmodel.getContest_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/forums/topic")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                Log.e("gfhhf", "" + pmodel.getTopic_id());
                                ForumUtil.INSTANCE.openViewTopicFragment(fragmentManager, pmodel.getTopic_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/business-directory")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openViewBusinessFragment(pmodel.getBusiness_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/store")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openViewStoreFragment(pmodel.getStore_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/question")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToQAView(pmodel.getQuestion_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/news/index/view")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToViewNewsFragment(pmodel.getNews_id(), false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/article")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                goToViewArticleFragment(pmodel.getArticle_id(), false);
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/event")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openEventViewFragment(pmodel.getEvent_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/group-communit")) {
                                Gson g = new Gson();
                                pmodel = g.fromJson(response, DeeplinkingModel.class);
                                openGroupViewFragment(pmodel.getGroup_id());
                            } else if (urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/core/link/index") ||
                                    urlmethod.toString().contains("https://demo4se.socialnetworking.solutions/groupvideo")) {
                                String url = urlmethod;
                                String title = "Feed Activity";
                                openWebView(url, title);
                            } else {
                                getBundle(getIntent());
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(this, new Handler(callback)).run(request);

            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("my_pid", android.os.Process.myPid());
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Code to be executed when an ad finishes loading.
            CustomLog.e("adMob", "onAdLoaded");
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
            CustomLog.e("adMob", "onAdFailedToLoad");
        }

        @Override
        public void onAdOpened() {
            // Code to be executed when the ad is displayed.
            CustomLog.e("adMob", "onAdOpened");
        }

        @Override
        public void onAdLeftApplication() {
            // Code to be executed when the user has left the app.
            CustomLog.e("adMob", "onAdLeftApplication");
        }

        @Override
        public void onAdClosed() {
            // Code to be executed when when the interstitial ad is closed.
            CustomLog.e("adMob", "onAdClosed");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getBundle(intent);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    public int getCurrentSongId() {
        if (musicSrv != null && musicBound)
            return musicSrv.getCurrentSongId();
        return 0;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(Util.progressToTimer(pos, getDuration()));
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    //user song select
    public void songPicked(Albums song) {
        if (null != musicSrv) {
            //   boolean isSongPending = false;
            pendingSong = null;
            playSong(song);
            //  controller.show(0);

        } else {
            //   boolean isSongPending = true;
            pendingSong = song;
            initService();
        }
        showSongDetail(song);
    }




    public void playSong(Albums song) {
        int position = musicSrv.updateSongList(song);
        musicSrv.setSong(position - 1);
        musicSrv.playSong();
        if (playbackPaused) {
            //    setController();
            playbackPaused = false;
        }
    }

    public void playSong(List<Albums> song) {

        if(musicSrv!=null){
            musicSrv.setList(song);
        }else {
            initService();
            musicSrv.setList(song);
        }

    }


    public boolean isPaused() {
        return playbackPaused;
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        try {
            initService();
        } catch (Exception e) {
            CustomLog.e(e);
        }
       /* if (playIntent == null) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }*/
    }

    public void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            //  setController();
            playbackPaused = false;
        }
        // controller.show(0);
    }

    public void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused) {
            // setController();
            playbackPaused = false;
        }
        // controller.show(0);
    }


    @Override
    protected void onDestroy() {

        if (musicSrv != null) {
            musicSrv.removeListener(Constant.Listener.COMMON);
            //musicSrv.removeAllListeners();
        }
        super.onDestroy();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_PROGRESS:
                    seekbar.setProgress(postion);
                    break;
                case Constant.Events.MUSIC_PREPARED:
                    pbLoad.setVisibility(View.GONE);
                    fabPlay.setEnabled(true);
                    break;
                case Constant.Events.MUSIC_COMPLETED:
                   // pbLoad.setVisibility(View.GONE);
                   // fabPlay.setEnabled(true);
                    hideMusicLayout();
                    break;
                case Constant.Events.MUSIC_CHANGED:
                    pbLoad.setVisibility(View.VISIBLE);
                    fabPlay.setEnabled(false);
                    showSongDetail(musicSrv.getCurrentSong());
                    break;
                case Constant.Events.PLAY:
                    fabPlay.setImageDrawable(dPause);
                    break;
                case Constant.Events.PAUSE:
                    fabPlay.setImageDrawable(dPlay);
                    break;
                case Constant.Events.STOP:
                    musicSrv.removeListener(Constant.Listener.COMMON);
                    hideMusicLayout();
                    stopMusicPlayer();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void stopMusicPlayer() {
        musicSrv.callListeners(Constant.Events.STOP, "", 0);
        musicSrv = null;
        ((MainApplication) getApplication()).stopMusic();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvMusicMain:
                hideMusicLayout();
                goToMusiListFragment();
                break;
            case R.id.fabPlay:
                CustomLog.e("duration1", "" + musicSrv.getDur());
                //  CustomLog.e("duration2", "" + getDuration());
                if (musicSrv.isPng()) {
                    fabPlay.setImageDrawable(dPlay);
                    pause();
                    // musicSrv.pausePlayer();
                } else {
                    fabPlay.setImageDrawable(dPause);
                    musicSrv.go();
                }
                break;
        }
    }

    public void goToMusiListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MusicListFragment()).addToBackStack(null).commit();
    }

    private void init() {
        cvMusicMain = findViewById(R.id.cvMusicMain);
        seekbar = findViewById(R.id.seekbar);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        fabPlay = findViewById(R.id.fabPlay);
        pbLoad = findViewById(R.id.pbLoad);
        ivSongImage = findViewById(R.id.ivSongImage);
        fabPlay.setOnClickListener(this);
        cvMusicMain.setOnClickListener(this);
    }

  /*  public void showMusicLayout() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cvMusicMain.setVisibility(View.VISIBLE);
            }
        }, 200);
        *//*if (cvMusicMain.getVisibility() != View.VISIBLE) {
            cvMusicMain.setVisibility(View.VISIBLE);
        }*//*
    }*/

    public void showMusicLayout() {
        try {
            Techniques technique = Techniques.values()[Techniques.SLIDE_IN_UP];
            YoYo.with(technique)
                    .duration(200)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new AnimationAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            cvMusicMain.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cvMusicMain.setVisibility(View.VISIBLE);
                            // mLayout.addPanelSlideListener(PostFeedFragment.this);
                        }
                    })
                    .playOn(cvMusicMain);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideMusicLayout() {
        try {
            if (cvMusicMain.getVisibility() != View.VISIBLE) return;
            Techniques technique = Techniques.values()[Techniques.SLIDE_OUT_DOWN];
            YoYo.with(technique)
                    .duration(200)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new AnimationAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cvMusicMain.setVisibility(View.GONE);
                            // mLayout.addPanelSlideListener(PostFeedFragment.this);
                        }
                    })
                    .playOn(cvMusicMain);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

 /*   public void hideMusicLayout() {
        if (cvMusicMain.getVisibility() == View.VISIBLE) {
            cvMusicMain.setVisibility(View.GONE);
        }
    }*/


    private void getBundle(Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            int dest = bundle.getInt(Constant.DESTINATION_FRAGMENT);
            switch (dest) {
                case Constant.GO_TO_WEBVIEW:
                    String url = bundle.getString(Constant.KEY_URI);
                    String title = bundle.getString(Constant.KEY_TITLE);
                    openWebView(url, title);
                    break;

                case Constant.GO_TO_CONTACT_US:
                    // openEventFragment();
                    openContactFragment();
                    break;

                case Constant.GoTo.EVENT:
                    openEventFragment();
                    break;

                case Constant.GoTo.CORE_EVENT:
                    openCoreEventFragment();
                    break;
                case Constant.GoTo.PAGE:
                    openPageFragment();
                    break;
                case Constant.GoTo.BUSINESS:
                    openBusinessFragment();
                    break;

                case Constant.GoTo.PROFILE_INFO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, InfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_INFO_FRIEND:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, MemberFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_INFO_RECNTVIEW:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, MemberRecentviewedFragment.newInstance(bundle.getInt(Constant.KEY_ID), true,1))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.GAME_BUILDER:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    new GameBrowseFragment())
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_INFO_RECNTVIEWME:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, MemberRecentviewedFragment.newInstance(bundle.getInt(Constant.KEY_ID), true,2))
                            .addToBackStack(null)
                            .commit();
                    break;



                case Constant.GoTo.PROFILE_INFO_BLOCK:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BrowseBlogsFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.BOOKING:
                    openBookings();
                    break;
                case Constant.GoTo.PROFILE_INFO_ALBUM:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BrowseAlbumFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_INFO_MUSIC:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, MusicAlbumFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.RESUME_BUILDER:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    new MyresumeList())
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_INFO_VIDEO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, VideoAlbumFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_INFO_PHOTO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BrowsePhotoFragment2.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.FOLLOWFOLLOWING_ACTIVITY:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, FollowFollowingParent.newInstance(bundle.getInt(Constant.KEY_ID), bundle.getString(Constant.KEY_TITLE)))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.FOLLOWER_ACTIVITY:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, FollowFollowingParent.newInstance(bundle.getInt(Constant.KEY_ID), bundle.getString(Constant.KEY_TITLE)))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_INFO_MAP:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProfileMapFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();

                    break;

                case Constant.GoTo.PROFILE_GROUP_ViIDEO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageVideoFragment.newInstance(bundle.getString(Constant.KEY_NAME),Constant.ResourceType.GROUP, bundle.getInt(Constant.KEY_ID), this,true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_PAGE_INFO:

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();

                    break;

                case Constant.GoTo.PROFILE_STORE_INFO:

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, StoreInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();

                    break;

                case Constant.GoTo.PROFILE_MESSAGE_DASHBOARD:

                  /*  fragmentManager.beginTransaction()
                            .replace(R.id.container, MessageDashboardFragment.newInstance())
                            .commit();*/

                    break;

                case Constant.GoTo.PROFILE_TAGIMAGE:
                    goToTagSuggestion(bundle.getBoolean("isAddRemove"), bundle.getBoolean("isOwner"), bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.PROFILE_GRIUP_INFO:

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, GroupInfoFragment2.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();


                    break;

                case Constant.GO_TO_CFORUM:
                    CoreForumUtil.INSTANCE.openCoreForumHomeFragment(fragmentManager);
                    break;

                case Constant.GoTo.PROFILE_PAGE_ALBUM:

                    Map<String, Object> map2 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageAlbumFragment.newInstance(map2, true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_PAGE_REVIEW:

                    Map<String, Object> map24 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageProfileReviewFragment.newInstance(true, bundle.getString(Constant.KEY_NAME), this, map24))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_PAGE_MAP:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageMapFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_PAGE_OVERVIEW:
                    Map<String, Object> map28 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, HtmlTextFragment.newInstance(map28, null, true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_PAGE_POLL:
                    Map<String, Object> map29 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProfilePollFragment.newInstance(MenuTab.Page.TYPE_PROFILE_POLL, this, map29, true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_PAGE_ANNOUNCE:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, AnnouncementFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_PAGE_SERVICE:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageServicesFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_PAGE_MEMBERS:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageMemberFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();
                    break;
              case Constant.GoTo.PROFILE_BUSINUSS_MEMBERS:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BusinessMemberFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), true))
                            .addToBackStack(null)
                            .commit();
                    break;
              case Constant.GoTo.PROFILE_PAGE_ViIDEO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageVideoFragment.newInstance(bundle.getString(Constant.KEY_NAME), Constant.ResourceType.PAGE, bundle.getInt(Constant.KEY_ID), this, true))
                            .addToBackStack(null)
                            .commit();
                break;
                case Constant.GoTo.PROFILE_BUSINUSS_VIDEOS:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageVideoFragment.newInstance(bundle.getString(Constant.KEY_NAME), Constant.ResourceType.BUSINESS, bundle.getInt(Constant.KEY_ID), this, true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_S_PRODUCT_VIDEO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageVideoFragment.newInstance(bundle.getString(Constant.KEY_NAME), Constant.ResourceType.PRODUCT, bundle.getInt(Constant.KEY_ID), this, true))
                            .addToBackStack(null)
                            .commit();
                    break;


                case Constant.GoTo.PROFILE_STORE_VIDEO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageVideoFragment.newInstance(bundle.getString(Constant.KEY_NAME), Constant.ResourceType.STORE, bundle.getInt(Constant.KEY_ID), this, true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_EVENT_VIDEO:
                    Map<String, Object> map31 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, EventVideoFragment.newInstance(map31, true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_CONTEST_RULE:
                    Map<String, Object> map38 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, HtmlTextFragment.newInstance(map38, this, true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_GROUP_POLL:
                    Map<String, Object> map291 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProfilePollFragment.newInstance(MenuTab.Group.TYPE_PROFILE_POLL, this, map291, true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_STORE_PRODUCT:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProductFragment.newInstance(MenuTab.Store.STORE_PRODUCT, this, bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_S_PRODUCT_UPSELL:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProductFragment.newInstance("upsell_product", bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_EVENT_INFO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, EventInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_COURSE_INFO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, CourseInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_S_PRODUCT_INFO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProductInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_COURSE_UPSELL:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, CourseFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .commit();
                    break;

                case Constant.GoTo.PROFILE_COURSE_LECTURES:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, LectureFragment.newInstance(LectureFragment.LECTURES, bundle.getInt(Constant.KEY_ID), true))
                            .commit();

                    break;
                case Constant.GoTo.PROFILE_COURSE_TEST:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, TestFragment.newInstance(LectureFragment.LECTURES, bundle.getInt(Constant.KEY_ID), true))
                            .commit();


                    break;


                case Constant.GoTo.PROFILE_EVENT_DISCUSSION:
                    Map<String, Object> map30 = (Map<String, Object>) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable(Constant.POST_REQUEST);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, DiscussionFragment.newInstance(map30, true))
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_PAGE_ASSOCIATE:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, PageFragment.newInstance(PageFragment.TYPE_ASSOCIATE, bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_BUSINUSS_ASSOCIATE:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BusinessFragment.newInstance(BusinessFragment.TYPE_ASSOCIATE, bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.PROFILE_EVENT_MAP:
                    LocationActivity locationActivity = (LocationActivity) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable("MyClass");
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, EventMapFragment.newInstance(bundle.getBundle(Constant.KEY_BUNDEL), locationActivity, true))
                            .addToBackStack(null)
                            .commit();
                    //   adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
                    break;

                case Constant.GoTo.PROFILE_EVENT_STICKER:
                    Emotion emotion = new Emotion();
                    try {
                        emotion.setGalleryId(bundle.getBundle(Constant.KEY_BUNDEL).getInt(Constant.KEY_FIELDID));
                        emotion.setTitle(bundle.getBundle(Constant.KEY_BUNDEL).getString(Constant.KEY_TITLE));
                        emotion.setIcon(bundle.getBundle(Constant.KEY_BUNDEL).getString(Constant.KEY_FIELDICON));
                        emotion.setColor(bundle.getBundle(Constant.KEY_BUNDEL).getString(Constant.KEY_TEXT_COLOR_STRING));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String retype234 = bundle.getBundle(Constant.KEY_BUNDEL).getString(Constant.KEY_RESOURCES_TYPE);

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, StickerChildFragment.newInstance(emotion, retype234, true))
                            .addToBackStack(null)
                            .commit();

                    //   adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
                    break;
                case Constant.GoTo.PROFILE_CONTEST_AWARTS:
                    ContestItem contestItem = (ContestItem) bundle.getBundle(Constant.KEY_BUNDEL).getSerializable("MyClass");
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ContestAwardFragment.newInstance(contestItem, true))
                            .addToBackStack(null)
                            .commit();
                    //   adapter.addFragment(EventMapFragment.newInstance(bundle, vo), opt.getLabel());
                    break;
                case Constant.GoTo.PROFILE_BUSINUSS_INFO:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, BusinessInfoFragment.newInstance(bundle.getInt(Constant.KEY_ID), true))
                            .addToBackStack(null)
                            .commit();
                    break;
                case Constant.GoTo.VIEW_BUSINESS:
                    openViewBusinessFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.RECIPE:
                    openRecipeFragment();
                    break;
                case Constant.GoTo.CREATE_CHANNEL:
                    Map<String, Object> map = new HashMap<>();
                    map.put("moduleName", "sesvideo");
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditChannelFragment.newInstance(Constant.FormType.ADD_CHANNEL, map, Constant.URL_CHANNEL_CREATE, null, true))
                            .commit();
                    break;
                case Constant.GoTo.REPORT_COMMENT:
                    String guid = bundle.getString(Constant.KEY_GUID);
                    fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid, true)).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.EDIT_CHANNEL:
                    Map<String, Object> map3 = new HashMap<>();

                    int channel_id = bundle.getInt(Constant.KEY_ID);
                    map3.put(Constant.KEY_CHANNEL_ID, channel_id);
                    fragmentManager.beginTransaction().
                            replace(R.id.container,
                                    CreateEditChannelFragment.newInstance(Constant.FormType.EDIT_CHANNEL, map3, Constant.URL_EDIT_CHANNEL, null, true))
                            .addToBackStack(null)
                            .commit();
                    break;

                case Constant.GoTo.EDIT_VIDEO_:
                    Map<String, Object> map4 = new HashMap<>();
                    map4.put(Constant.KEY_VIDEO_ID, bundle.getInt(Constant.KEY_VIDEO_ID));
                    String url1 = Constant.URL_EDIT_VIDEO;
                    map4.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
                    fragmentManager.beginTransaction().replace(R.id.container,
                            FormFragment.newInstance(Constant.FormType.KEY_EDIT_VIDEO, map4, url1)).addToBackStack(null).commit();

                    break;
                case Constant.GoTo.VIEW_RECIPE:
                    openViewRecipeFragment(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GO_TO_SETTINGS:
                    openSettingFragment();
                    break;
                case Constant.GO_TO_NOTIFICATION:
                    fragmentManager.beginTransaction().replace(R.id.container, new NotificationFragment()).addToBackStack(null).commit();
                    break;

                case Constant.GoTo.TnC:
                    openTermsPrivacyFragment(bundle.getString(Constant.KEY_URI));
                    break;

                case Constant.GO_TO_MUSIC:
                    if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"music")){
                        openMusicFragment(0);
                    }else {
                        CMusicUtil.openBrowseFragment(fragmentManager);
                    }

                   /* if (ModuleUtil.getInstance().isCorePlugin(this, "music")) {
                        CMusicUtil.openBrowseFragment(fragmentManager);
                    } else {
                        openMusicFragment(0);
                    }*/
                    break;
                case Constant.GO_TO_MUSIC_SONG:
                    openMusicFragment(1);
                    break;
                case Constant.GO_TO_MUSIC_PLAYLIST:
                    openMusicFragment(2);
                    break;

                case Constant.GO_TO_VIDEO:
                    openVideoFragment(0);
                    break;
                case Constant.GO_TO_VIDEO_CHANNEL:
                    openVideoFragment(1);
                    break;
                case Constant.GO_TO_VIDEO_PLAYLIST:
                    openVideoFragment(2);
                    break;
                case Constant.GoTo.ARTICLE:
                    openArticleFragment();
                    break;
                case Constant.GO_TO_BLOG:
                    if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"blog")){
                        openBlogFragment();
                    }else {
                        openBlogbasicFragment();
                    }

                    break;
                case Constant.GO_TO_JOBS:
                        openJobFragment();
                    break;
                case Constant.GO_TO_MULTISTORE:
                    openMultistoreFragment();
                    break;
                case Constant.GO_TO_NEWS:
                    openNewsFragment();
                    break;
                case Constant.GO_TO_FORUM:
                    ForumUtil.INSTANCE.openForumHomeFragment(fragmentManager);
                    break;
                case Constant.GoTo.CLASSIFIED:
                    openClassifiedFragment();
                    break;
                case Constant.GoTo.VIEW_CONTEST:
                    viewContestFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.CREDIT:
                    CreditUtil.openParentFragment(fragmentManager);
                    break;
                case Constant.GoTo.VIEW_CREDIT:
                    CreditUtil.openViewFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_ENTRY:
                    viewEntryFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.MY_STORY:
                    fragmentManager.beginTransaction().replace(R.id.container,
                            MyStory.newInstance(
                                    new Gson().fromJson(bundle.getString(Constant.STORY_IMAGE_KEY), StoryModel.class)
                                    , bundle.getBoolean(Constant.KEY_USER_ID)
                            )).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.REACTION:
                    Map<String, Object> map5 = new HashMap<>();
                    map5.put(Constant.KEY_RESOURCES_TYPE, bundle.getString(Constant.KEY_RESOURCES_TYPE));
                    map5.put(Constant.KEY_ID, bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction().replace(R.id.container,
                            ReactionViewFragment.newInstance(map5)).addToBackStack(null).commit();
                    break;

                case Constant.GoTo.QA:
                    openQAFragment();
                    break;
                case Constant.GoTo.COURSE:
                    openCourses();
                    break;
                case Constant.GoTo.CLASSROOM:
                    openClassrooms();
                    break;
                case Constant.GoTo.VIEW_QA:
                    goToQAView(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.CONTEST:
                    openContestFragment();
                    break;

                case Constant.GO_TO_MEMBER:
                    openMemberFragment();
                    break;

                case Constant.GoTo.CORE_SEARCH:
                    openCoreSearchFragment();
//                    startActivity(new Intent(this, FireVideoActivity.class));
                    break;
                case Constant.GoTo.CORE_DASHBAORDMESSAGE:
                /*    fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment())
                            .addToBackStack(null)
                            .commit();*/

                    fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                            .addToBackStack(null)
                            .commit();
                    //                    startActivity(new Intent(this, FireVideoActivity.class));
                    break;

                case Constant.GO_TO_POLL:
                    openCpollsfragment();
                    break;
                case Constant.GoTo.STORE:
                    StoreUtil.openParentFragment(fragmentManager);
                    break;
                case Constant.GoTo.TICK_VIEW_CHANNEL:
                    int channelId = bundle.getInt(Constant.KEY_CHANNEL_ID);
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, ChannelFragment.newInstance(channelId))
                            .commit();
                    break;
                case Constant.GoTo.TICK_VIEW_CHANNEL3:
                    int channelId2 = bundle.getInt(Constant.KEY_CHANNEL_ID);
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container, OtherFragment.newInstance(false, channelId2))
                            .commit();
                    break;
                case Constant.GoTo.VIEW_CORE_GROUP:
                    fragmentManager.beginTransaction().replace(R.id.container, ViewGroupFragment.newInstance(bundle.getInt(Constant.KEY_ID))).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.VIEW_GROUP:
                    openGroupViewFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_GROUP_TOPIC:
                    fragmentManager.beginTransaction().replace(R.id.container, CoreGroupDiscussionView.newInstance(bundle.getInt(Constant.KEY_ID))).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.VIEW_CGROUP:
                    openCGroupViewFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_FUND:
                    CrowdUtil.openViewFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.FUND:
                    CrowdUtil.openParentFragment(fragmentManager);
                    break;
                case Constant.GoTo.SUGGESTION:
                    openSuggestionFragment();
                    break;
                case Constant.GoTo.WISH:
                    openWishFragment();
                    break;

                case Constant.GO_TO_ALBUM:
                    if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"album")){
                        openAlbumFragment();
                    }else {
                        openAlbumbasicFragment();
                    }
                    break;
                case Constant.GO_TO_RATE_US:
                    openRateFragment();
                    break;
                case Constant.GoTo.QUOTE:
                    openQuoteFragment();
                     break;
                case Constant.GoTo.POLLSNET:
                    openPollFragment();
                    break;

                case Constant.GoTo.CORE_GROUP:
                    fragmentManager.beginTransaction().replace(R.id.container, new CGroupParentFragment()).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.GROUP:
                    openGroupFragment();
                    break;
                case Constant.GoTo.PRAYER:
                    openPrayerFragment();
                    break;
                case Constant.GoTo.VIDEO_FEED:
                    openVideoFeedFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.THOUGHT:
                    openThoughtFragment();
                    break;

                case Constant.GoTo.QUOTE_CATEGORY:
                    openQuoteCategoryFragment(bundle.getInt(Constant.KEY_ID)
                            , bundle.getString(Constant.KEY_TITLE)
                            , bundle.getBoolean(Constant.KEY_IS_TAG)
                    );
                    break;
                case Constant.GoTo.PRAYER_CATEGORY:
                    openPrayerCategoryFragment(bundle.getInt(Constant.KEY_ID)
                            , bundle.getString(Constant.KEY_TITLE)
                            , bundle.getBoolean(Constant.KEY_IS_TAG)
                    );
                    break;
                case Constant.GoTo.WISH_CATEGORY:
                    openWishCategoryFragment(bundle.getInt(Constant.KEY_ID)
                            , bundle.getString(Constant.KEY_TITLE)
                            , bundle.getBoolean(Constant.KEY_IS_TAG)
                    );
                    break;
                case Constant.GoTo.THOUGHT_CATEGORY:
                    openThoughtCategoryFragment(bundle.getInt(Constant.KEY_ID)
                            , bundle.getString(Constant.KEY_TITLE)
                            , bundle.getBoolean(Constant.KEY_IS_TAG)
                    );
                    break;

                case Constant.GO_TO_POST_FEED:
                    ComposerOption cmpVo = new Gson().fromJson(bundle.getString(Constant.KEY_TITLE), ComposerOption.class);
                    if (bundle.getInt(Constant.KEY_NAME) == 0) {
                        fragmentManager.beginTransaction().replace(R.id.container,
                                PostFeedFragment.newInstance(cmpVo, 1, bundle.getStringArrayList("photopath"))).addToBackStack("1").commit();
                    } else {
                        openPostFeedFragment(cmpVo, bundle.getInt(Constant.KEY_NAME));
                    }
                    break;
                case Constant.GO_TO_EDIT_FEED:
                    fragmentManager.beginTransaction().replace(R.id.container, PostEditFragment.newInstance(bundle.getString(Constant.KEY_BODY))).addToBackStack(null).commit();
                    break;
                case Constant.GO_TO_EDIT_SHARE_SE:
                    title = bundle.getString(Constant.KEY_TITLE);
                    fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(title)).addToBackStack(null).commit();
                    break;


                case Constant.GoTo.GO_TO_BOOKING:
                    int classroomid = bundle.getInt(Constant.KEY_ID);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container
                                    , ViewServiceFragment.newInstance(classroomid))
                            .addToBackStack(null)
                            .commit();

                    break;


                case Constant.GO_TO_VIEW_FEED:
                    String tag = bundle.getString(Constant.KEY_HASH_TAG);
                    boolean openComment = bundle.getBoolean(Constant.KEY_COMMENT_ID);
                    int actionId = bundle.getInt(Constant.KEY_ACTION_ID);
                    String resourceType = bundle.getString(Constant.KEY_RESOURCES_TYPE);
                    int resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    ViewFeedFragment.newInstance
                                            (tag, actionId, openComment, resourceId, resourceType)).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.ARCHIVE:
                    fragmentManager.beginTransaction().replace(R.id.container, new ArchiveFragment()).addToBackStack(null).commit();
                    break;
                case Constant.GO_TO_SHARE_SE:
                    fragmentManager.beginTransaction().replace(R.id.container, ShareSEFragment.newInstance(new Gson().fromJson(bundle.getString(Constant.KEY_TITLE), Share.class))).addToBackStack(null).commit();
                    // openPostFeedFragment((ComposerOption) bundle.getSerializable(Constant.KEY_TITLE));
                    break;

                case Constant.GO_TO_COMMENT:
                    //fragmentManager.beginTransaction().replace(R.id.container, CommentFragment.newInstance(bundle.getInt(Constant.KEY_ACTION_ID), Constant.VALUE_RESOURCES_TYPE, bundle.getString(Constant.KEY_GUID))).addToBackStack(null).commit();
                    // openPostFeedFragment((ComposerOption) bundle.getSerializable(Constant.KEY_TITLE));

                    if (SPref.getInstance().getDefaultInfo(this, Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
                        fragmentManager.beginTransaction().replace(R.id.container, CommentFragment.newInstance(bundle.getInt(Constant.KEY_ACTION_ID), Constant.VALUE_RESOURCES_TYPE2, bundle.getString(Constant.KEY_GUID))).addToBackStack(null).commit();
                    } else {
                        fragmentManager.beginTransaction().replace(R.id.container, CommentFragment.newInstance(bundle.getInt(Constant.KEY_ACTION_ID), Constant.VALUE_RESOURCES_TYPE, bundle.getString(Constant.KEY_GUID))).addToBackStack(null).commit();
                    }


                    break;

                case Constant.GoTo.EDIT_USER:
                    fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_USER, new HashMap<String, Object>(), Constant.URL_EDIT_PROFILE)).addToBackStack(null).commit();
                    break;

                case Constant.GoTo.GALLARY:
                    goToGallaryFragment(
                            bundle.getInt(Constant.KEY_ID),
                            bundle.getInt(Constant.KEY_ALBUM_ID)
                            , bundle.getString(Constant.KEY_TYPE)
                            , bundle.getString(Constant.KEY_RESOURCES_TYPE),
                            bundle.getString(Constant.KEY_IMAGE)
                    );
                    break;
               /* case Constant.GoTo.VIDEO:
                    goToViewVideoFragment(bundle.getInt(Constant.KEY_ID));
                    break;*/
                case Constant.GoTo.VIEW_MUSIC_ALBUM:
                    if (ModuleUtil.getInstance().isCorePlugin(this, "music")) {
                        CMusicUtil.openViewFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    } else {
                        goToViewMusicAlbumFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    }
                    break;
                case Constant.GoTo.VIEW_SONG:
                    goToSongsView(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    break;
                case Constant.GoTo.VIEW_QUOTE:
                    goToQuoteView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CLASSIFIED:
                    goToClassifiedView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CLASSROOM:
                    goToClassroomView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_COURSE:
                    goToViewCourseFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_PRAYER:
                    goToPrayerView(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.GO_TO_TAGSUGGEST:
                    goToTagSuggestion(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_ISREMNOVE), bundle.getBoolean(Constant.KEY_ISOWENER));

                    //   goToPrayerView(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.MORE_MEMBER:
                    // Bundle bundle = new Bundle();
                    // bundle.putString(Constant.KEY_TITLE, getStrings(R.string.voted_user));
                    // bundle.putInt(Constant.KEY_ID, optionList.get(position).getPollOptionId());
                    fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
                    break;

                case Constant.GoTo.VIEW_POLL:
                    goToPollView(bundle.getString(Constant.KEY_TYPE), bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CPOLL:
                    goToCPollView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_WISH:
                    goToWishView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_THOUGHT:
                    goToThoughtView(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_BLOG:

                    if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"blog")){
                        goToViewBlogFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    }else {
                        goToViewBlogBasicFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    }
                    break;
                case Constant.GoTo.VIEW_NEWS:
                    goToViewNewsFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    break;
                case Constant.GoTo.VIEW_FORUM:
                    ForumUtil.INSTANCE.openViewForumFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CFORUM:
                    CoreForumUtil.INSTANCE.openViewForumFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_FORUM_TOPIC:
                    ForumUtil.INSTANCE.openViewTopicFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CFORUM_TOPIC:
                    CoreForumUtil.INSTANCE.openCoreViewTopicFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_FORUM_CATEGORY:
                    ForumUtil.INSTANCE.openViewForumCategoryFragment(fragmentManager, "", bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_STORE:
                    StoreUtil.openViewStoreFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_PRODUCT:
                    StoreUtil.openViewProductFragment(fragmentManager, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_WISHLIST:
                    StoreUtil.openViewWishlistFragmnet(fragmentManager, ProductAdapter.WISHLIST, bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_ARTICLE:
                    goToViewArticleFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    break;
                case Constant.GoTo.PROFILE:
                    goToProfileFragment(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.VIEW_MUSIC_PLAYLIST:
                    goToViewPlaylistFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_PAGE:
                    openViewPageFragment(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.VIEW_EVENT:
                    openEventViewFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CORE_EVENT:
                    openCEventViewFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_EVENT_TOPIC:
                    fragmentManager.beginTransaction().replace(R.id.container, CoreEventDiscussionView.newInstance(bundle.getInt(Constant.KEY_ID))).addToBackStack(null).commit();
                    break;
                case Constant.GoTo.VIEW_MUSIC_ARTIST:
                    goToViewArtistFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_CHANNEL:
                    goToViewChannelFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    break;
                case Constant.GoTo.VIEW_ALBUM:


                    if(!SPref.getInstance().isBasicPlugins(getBaseContext(),"album")){
                        goToViewAlbumFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    }else {
                        goToViewAlbumBasicFragment(bundle.getInt(Constant.KEY_ID), bundle.getBoolean(Constant.KEY_COMMENT_ID));
                    }
                    break;
                case Constant.GoTo.VIEW_VIDEO_PLAYLIST:
                    goToVideoPlaylistFragment(bundle.getInt(Constant.KEY_ID));
                    break;
                case Constant.GoTo.VIEW_PROFILE:
                    goToProfileFragment(bundle.getInt(Constant.KEY_ID));
                    break;

                case Constant.GoTo.CREATE_RESUME_EXPERIENCE:
                    Map<String, Object> map27 = new HashMap<>();
                    map27.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_EXPRIENCE, map27, Constant.CREDIT_RESUME_ADDWORKEXPERIENCE, null, true))
                            .commit();
                    break;
                case Constant.GoTo.CREATE_RESUME_EXPERIENCE_EDIT:
                    Map<String, Object> map51 = new HashMap<>();
                    map51.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    map51.put(Constant.KEY_EXPERENCE_ID, bundle.getInt(Constant.KEY_EXPERENCE_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT, map51, Constant.CREDIT_RESUME_EDIT_WORKEXPERIENCE, null, true))
                            .commit();
                    break;

                case Constant.GoTo.CREATE_RESUME_EDUCATION:
                    Map<String, Object> map26 = new HashMap<>();
                    map26.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_EDUCATION, map26, Constant.CREDIT_RESUME_ADDEDUCTAION, null, true))
                            .commit();
                    break;
                case Constant.GoTo.CREATE_RESUME_EDUCATION_EDIT:
                    Map<String, Object> map91 = new HashMap<>();
                    map91.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    map91.put(Constant.KEY_EDUCATION_ID, bundle.getInt(Constant.KEY_EDUCATION_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_EDUCATION_EDIT, map91, Constant.CREDIT_RESUME_EDIT_EDUCTAION, null, true))
                            .commit();
                    break;

                case Constant.GoTo.CREATE_RESUME_PROJECT:
                    Map<String, Object> map25 = new HashMap<>();
                    map25.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_PROJECT, map25, Constant.CREDIT_RESUME_ADDPROJECT, null, true))
                            .commit();
                    break;

                case Constant.GoTo.CREATE_RESUME_PROJECT_EDIT:
                    Map<String, Object> map61 = new HashMap<>();
                    map61.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    map61.put(Constant.KEY_PROJECT_ID, bundle.getInt(Constant.KEY_PROJECT_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_PROJECT_EDIT, map61, Constant.CREDIT_RESUME_EDITPROJECT, null, true))
                            .commit();
                    break;
                case Constant.GoTo.CREATE_RESUME_CERTIFICATE:
                    Map<String, Object> map23 = new HashMap<>();
                    map23.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_CERTIFICATE, map23, Constant.CREDIT_RESUME_ADD_CERTIFICATE, null, true))
                            .commit();
                    break;
                case Constant.GoTo.CREATE_RESUME_CERTIFICATE_EDIT:
                    Map<String, Object> map44 = new HashMap<>();
                    map44.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    map44.put(Constant.KEY_CERTIFICATE_ID, bundle.getInt(Constant.KEY_CERTIFICATE_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_CERTIFICATE_EDIT, map44, Constant.CREDIT_RESUME_EDIT_CERTIFICATE, null, true))
                            .commit();
                    break;


                case Constant.GoTo.CREATE_RESUME_REFERENCE:
                    Map<String, Object> map21 = new HashMap<>();
                    map21.put("resume_id", bundle.getInt(Constant.KEY_ID));


                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_REFERENCE, map21, Constant.CREDIT_RESUME_ADD_REFERENCE, null, true))
                            .commit();
                    break;
                case Constant.GoTo.CREATE_RESUME_REFERENCE_EDIT:
                    Map<String, Object> map35 = new HashMap<>();
                    map35.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    map35.put("reference_id", bundle.getInt(Constant.KEY_REFERENCE_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_REFERENCE_EDIT, map35, Constant.CREDIT_RESUME_EDIT_REFERENCE, null, true))
                            .commit();
                    break;

                case Constant.GoTo.CREATE_RESUME_Career:
                    Map<String, Object> map22 = new HashMap<>();
                    map22.put("resume_id", bundle.getInt(Constant.KEY_ID));
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditExperienceFragment.newInstance(Constant.FormType.CREATE_RESUME_CARIOROBJECT, map22, Constant.CREDIT_RESUME_ADD_OBJECTIVES, null, true))
                            .commit();
                    break;



                case Constant.GoTo.GO_TO_REPORT:
                    openReportvideo(bundle.getString(Constant.KEY_TITLE));
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openQAFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new QAParentFragment()).addToBackStack(null).commit();
    }


    private void openReportvideo(String title) {
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(title)).addToBackStack(null).commit();
    }

    private void openCourses() {
        fragmentManager.beginTransaction().replace(R.id.container, new CourseParentFragment()).addToBackStack(null).commit();
    }

    private void openClassrooms() {
        fragmentManager.beginTransaction().replace(R.id.container, new ClassroomParentFragment()).addToBackStack(null).commit();
    }

    private void openPrayerFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new PrayerParentFragment()).addToBackStack(null).commit();

    }

    private void openCpollsfragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new CPollParentFragment()).addToBackStack(null).commit();
    }

    private void openVideoFeedFragment(int videoActionId) {
        fragmentManager.beginTransaction().replace(R.id.container, VideoFeedFragment.newInstance(videoActionId)).addToBackStack(null).commit();
    }

    private void openVideoFeedFragment2(int videoActionId) {
        fragmentManager.beginTransaction().replace(R.id.container, VideoFeedFragment.newInstance(videoActionId)).addToBackStack(null).commit();
    }


    private void openQuoteFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new QuotesParentFragment()).addToBackStack(null).commit();
    }

    private void openGroupFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new GroupParentFragment()).addToBackStack(null).commit();
    }

    private void openGroupViewFragment(int groupId) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewGroupFragment.newInstance(groupId)).addToBackStack(null).commit();
    }

    private void openCGroupViewFragment(int groupId) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewCGroupFragment.newInstance(groupId)).addToBackStack(null).commit();
    }

    private void openThoughtFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new ThoughtParentFragment()).addToBackStack(null).commit();
    }

    private void openQuoteCategoryFragment(int id, String title, boolean isTag) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewQuoteCategoryFragment.newInstance(id, title, isTag))
                .addToBackStack(null)
                .commit();

    }

    private void openPrayerCategoryFragment(int id, String title, boolean isTag) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewPrayerCategoryFragment.newInstance(id, title, isTag, null))
                .addToBackStack(null)
                .commit();

    }

    private void openThoughtCategoryFragment(int id, String title, boolean isTag) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewThoughtCategoryFragment.newInstance(id, title, isTag))
                .addToBackStack(null)
                .commit();

    }

    private void openWishCategoryFragment(int id, String title, boolean isTag) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewWishCategoryFragment.newInstance(id, title, isTag))
                .addToBackStack(null)
                .commit();

    }

    private void goToSongsView(int songId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewSongFragment.newInstance(new HashMap<String, Object>(), songId, Constant.ACTIVITY_TYPE_ALBUM_SONG, openComment))
                .addToBackStack(null)
                .commit();

    }

    private void goToQuoteView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewPhotoQuoteFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }


    private void goToQAView(int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewQuestionFragment.newInstance(id, null))
                .addToBackStack(null)
                .commit();

    }

    private void goToPrayerView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewPrayerFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }

    private void goToTagSuggestion(int pageid, boolean isAddRemove, boolean isOwner) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, TagSuggestionFragment.newInstance(pageid, isAddRemove, isOwner))
                .addToBackStack(null)
                .commit();
    }


    private void openBookings() {
        fragmentManager.beginTransaction().replace(R.id.container, new BookingParentFragment()).addToBackStack(null).commit();
    }

    private void goToPollView(String type, int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PollViewFragment.newInstance(type, songId))
                .addToBackStack(null)
                .commit();

    }

    private void goToCPollView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, CViewPollFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }

    private void goToWishView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewPhotoWishFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }

    private void goToThoughtView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewThoughtFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }

    private void goToViewPlaylistFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPlaylistFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewAlbumFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewAlbumFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewAlbumBasicFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , C_ViewAlbumFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();


    }


    private void goToProfessionalView(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewProfessionalFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToProfileFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewProfileFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewArtistFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewChannelFragment(int channelId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewChannelFragment.newInstance(channelId, openComment))
                .addToBackStack(null)
                .commit();
    }

    private void goToVideoPlaylistFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPlaylistVideoFragment.newInstance(videoId, null, null))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewArticleFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArticleFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();
    }

    private void goToClassifiedView(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewClassifiedFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToClassroomView(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewClassroomFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewCourseFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCourseFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewBlogFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewBlogFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();
    }

    private void goToViewBlogBasicFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , C_ViewBlogFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();
    }


    private void goToViewNewsFragment(int videoId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewNewsFragment.newInstance(videoId, openComment))
                .addToBackStack(null)
                .commit();
    }

    private void goToTagSuggestion(boolean isAddRemove, boolean isOwner, int photoid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, TagSuggestionFragment.newInstance(photoid, isAddRemove, isOwner))
                .addToBackStack(null)
                .commit();
    }

  /*  private void goToViewVideoFragment(int videoId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewVideoFragment.newInstance(videoId))
                .addToBackStack(null)
                .commit();
    }*/

    private void goToViewMusicAlbumFragment(int albumId, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(albumId, openComment))
                .addToBackStack(null)
                .commit();
    }


    private void gotoclassroom(int classroomid, boolean openComment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewServiceFragment.newInstance(classroomid))
                .addToBackStack(null)
                .commit();
    }

    private void goToGallaryFragment(int photoId, int albumId, String type, String resourceType, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_PHOTO_ID, photoId);
        map.put(Constant.KEY_ALBUM_ID, albumId);
        map.put(Constant.KEY_TYPE, type);
        map.put(Constant.KEY_IMAGE, imageUrl);
        map.put(Constant.KEY_RESOURCES_TYPE, resourceType);

        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , GallaryFragment.newInstance(map))
                .addToBackStack(null)
                .commit();
    }

    private void openMemberFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new MemberFragment()).addToBackStack(null).commit();

    }

    private void openMemberFragmentRECENT(int userid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, MemberRecentviewedFragment.newInstance(userid, true))
                .addToBackStack(null)
                .commit();
    }





    private void openCoreSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchFragment()).addToBackStack(null).commit();

    }

    private void openSuggestionFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SuggestionViewFragment()).addToBackStack(null).commit();

    }

    private void openAlbumFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new AlbumParentFragment()).addToBackStack(null).commit();

    }

    private void openAlbumbasicFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new C_AlbumParentFragment()).addToBackStack(null).commit();
    }


    private void openWishFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new WishParentFragment()).addToBackStack(null).commit();
    }

    private void openPollFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new PollParentFragment()).addToBackStack(null).commit();
    }

    private void openEventFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new EventParentFragment()).addToBackStack(null).commit();
    }

    private void openCoreEventFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new CEventParentFragment()).addToBackStack(null).commit();
    }

    private void openEventViewFragment(int id) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewEventFragment.newInstance(id)).addToBackStack(null).commit();
    }

    private void openCEventViewFragment(int id) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewCEventFragment.newInstance(id)).addToBackStack(null).commit();
    }

    private void openRateFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new RateFragment()).addToBackStack(null).commit();

    }

    private void openMusicFragment(int index) {
        fragmentManager.beginTransaction().replace(R.id.container, MusicParentFragment.newInstance(index)).addToBackStack(null).commit();
    }

    private void openVideoFragment(int index) {
        fragmentManager.beginTransaction().replace(R.id.container, VideoParentFragment.newInstance(index)).addToBackStack(null).commit();
    }

    private void openBlogFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new BlogParentFragment()).addToBackStack(null).commit();
    }


    private void openJobFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new JobParentFragment()).addToBackStack(null).commit();
    }

    private void openMultistoreFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new MultiStoreParentFragment()).addToBackStack(null).commit();
    }

    private void openBlogbasicFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new C_BlogParentFragment()).addToBackStack(null).commit();
    }


    private void openNewsFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new NewsParentFragment()).addToBackStack(null).commit();
    }

    private void openContestFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new ContestParentFragment()).addToBackStack(null).commit();
    }

    private void viewContestFragment(int id) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewContestFragment.newInstance(id)).addToBackStack(null).commit();
    }

    private void viewEntryFragment(int id) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewEntryFragment.newInstance(id)).addToBackStack(null).commit();
    }

    private void openClassifiedFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new ClassifiedParentFragment()).addToBackStack(null).commit();
    }

    private void openArticleFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new ArticleParentFragment()).addToBackStack(null).commit();

    }

    private void openSettingFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SettingFragment()).addToBackStack(null).commit();
    }

    private void openPageFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new PageParentFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openBusinessFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new BusinessParentFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openRecipeFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new RecipeParentFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openViewPageFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPageFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }

    private void openViewBusinessFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewBusinessFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }


    private void openViewStoreFragment(int storeId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewStoreFragment.newInstance(storeId))
                .addToBackStack(null)
                .commit();
    }


    private void openViewRecipeFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewRecipeFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }

    private void openPostFeedFragment(ComposerOption response, int selectedoption) {
        fragmentManager.beginTransaction().replace(R.id.container, PostFeedFragment.newInstance(response, selectedoption)).addToBackStack("1").commit();

    }

    private void openContactFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new ContactUsFragment()).addToBackStack(null).commit();

    }


    public void openWebView(String url, String title) {
        fragmentManager.beginTransaction().replace(R.id.container, WebViewFragment.newInstance(url, title)).addToBackStack(null).commit();
    }


    @Override
    protected void onHomePressed() {
        onBackPressed();
    }

    public void openTermsPrivacyFragment(String url) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, TnCFragment.newInstance(url))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void hideBaseLoader() {
        try {
            if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void initService() {
        try {
            musicSrv = ((MainApplication) getApplication()).onStart(musicConnection);
            if (musicSrv != null && (musicSrv.isPng() || musicSrv.isBuffering())) {
                //  musicSrv.removeAllListeners();
                musicSrv.setProgressListener(Constant.Listener.COMMON, this);
                showMusicLayout();
                showSongDetail(musicSrv.getCurrentSong());
            } else {
                hideMusicLayout();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void showSongDetail(Albums currentSong) {
        tvSongTitle.setText(currentSong.getTitle());
        Glide.with(this).load(currentSong.getImageUrl()).into(ivSongImage);
    }

    public void removeSong(int currentItem) {
        musicSrv.removeSongAtPosition(currentItem);
    }
}
