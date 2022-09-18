package com.sesolutions.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bounceview.BounceView;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.AGvideo.AGVideo;
import com.sesolutions.ui.AGvideo.AGVideoActivity;
import com.sesolutions.ui.AGvideo.mediaplayer.MediaExo;
import com.sesolutions.ui.clickclick.music.JZMediaExo2;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.customviews.ExampleCardPopup;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.customviews.SquareImageView;
import com.sesolutions.ui.customviews.TextViewClickMovement;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.dashboard.composervo.ComposerOptions;
import com.sesolutions.ui.dashboard.composervo.TextColorString;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.ui.poll.PollOptionAdapter;
import com.sesolutions.ui.signup.Constants;
import com.sesolutions.ui.storyview.StoryAdapter;
import com.sesolutions.ui.storyview.StoryModel;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.BetterImageSpan;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomClickableSpan;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.JzvdStd;
import cn.jzvd.JzvdStd2;

import static android.graphics.Typeface.BOLD;

public class FeedActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FEED = 0;
    public static final int TYPE_AD = 1;
    public static final int TYPE_CUSTOM_AD = 2;
    public static final int TYPE_PEOPLE_SUGGESTION = 3;
    public static final int TYPE_COMPOSER = 4;
    public static final int TYPE_COMMUNITY_AD = 5;
    public static final int TYPE_STORY = 6;
    public static final int TYPE_FILTER = 7;
    public final List<Activity> list;
    public final Context context;
    public final OnUserClickedListener<Integer, Object> listener;
    public final Typeface iconFont;
    public final String slashU;
    public final int colorPrimary;
    public final int emojiSize;
    public ThemeManager themeManager;
    public final boolean isLoggedIn;
    public final List<TextColorString> textColorStrings;
    public final RequestOptions requestOptions;
    public final RequestManager glide;
    //  private final int colorGrey;
    public final Drawable dLike, like;
    public final Drawable dSave;
    public final Drawable dUnsave;
    // private final int colorText1;
    public final int colorText2;
    public boolean hasToShowRoundImage;
    public OnLoadMoreListener loadListener;
    public boolean home;
    public final Drawable dMusicPlayer;
    public final Drawable dPlay;
    public boolean canPlay;

    public HttpProxyCacheServer proxy;
    private List<StoryModel> stories;
    boolean islike;
    boolean isSaved;
    public boolean canPlay2 = false;

    public FeedActivityAdapter(List<Activity> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        slashU = "&#x";//context.getString(R.string.slash_u);
        this.colorPrimary = Color.parseColor(Constant.colorPrimary);
        hasToShowRoundImage = AppConfiguration.memberImageShapeIsRound;
        // this.colorGrey = ContextCompat.getColor(context, R.color.grey_feed);
        // this.colorText1 = Color.parseColor(Constant.text_color_1);
        this.colorText2 = Color.parseColor(Constant.text_color_2);
        this.dLike = ContextCompat.getDrawable(context, R.drawable.ic_like);

        this.dMusicPlayer = ContextCompat.getDrawable(context, R.drawable.music_player);
        this.isLoggedIn = SPref.getInstance().isLoggedIn(context);
        this.emojiSize = context.getResources().getInteger(R.integer.header_emoji_size);
        this.islike = false;
        this.isSaved = false;
        this.like = ContextCompat.getDrawable(context, R.drawable.like_active_quote);
        this.dPlay = ContextCompat.getDrawable(context, R.drawable.play);
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);
        textColorStrings = SPref.getInstance().getTextColorString(context);
        themeManager = new ThemeManager();
        requestOptions = new RequestOptions().dontAnimate().dontTransform().placeholder(R.drawable.placeholder_3_2);
        glide = Glide.with(context);


        this.proxy = ((MainApplication) context.getApplicationContext()).getProxy(cntxt);
        /*requestOptions.dontAnimate();//.dontTransform();
        requestOptions.dontTransform();
        requestOptions.placeholder(R.drawable.placeholder_3_2);*/

    }


    public static BetterImageSpan makeImageSpan(/*Context context,*/ Drawable drawable, int size) {
        drawable.mutate();
        drawable.setBounds(0, 0, size, size);
        return new BetterImageSpan(drawable, BetterImageSpan.ALIGN_CENTER);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (home && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public void showImageWithGlide(ImageView ivUserImage, String path, Context context, int drawable) {


        try {
            if (("" + path).endsWith(".gif")) {
                Util.showAnimatedImageWithGlide(ivUserImage, path, context);
                return;
            }

            glide.applyDefaultRequestOptions(requestOptions).load(path).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.e("Complete", "Completed");
                    return false;
                }
            }).placeholder(drawable).into(ivUserImage);

            // glide.applyDefaultRequestOptions(requestOptions).load(path)/*.transition(withCrossFade())*//*thumbnail(0.1f).*/.into(ivUserImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void canPlayFirstVideo(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public void canPlayFirstVideo2(boolean canPlay21) {
        this.canPlay2 = canPlay21;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_AD:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_ad, parent, false);
                return new AdHolder(v);
            case TYPE_CUSTOM_AD:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_se_ad, parent, false);
                return new SeAdHolder(v);
            case TYPE_COMMUNITY_AD:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_commnity_ad, parent, false);
                return new CommunityAd(v);
            case TYPE_STORY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_parent, parent, false);
                return new StoryParentHolder(v);
            case TYPE_COMPOSER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_feed, parent, false);
                return new ComposerHolder(v);
            case TYPE_FILTER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_filter, parent, false);
                return new FeedFilterHolder(v);
            case TYPE_PEOPLE_SUGGESTION:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_suggestion, parent, false);
                return new PeopleHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(AppConfiguration.isFeedCentered ? R.layout.item_feed_all_center : R.layout.item_feed_all, parent, false);
                return new CommonHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            Log.e("position", "" + position);
            switch (list.get(position).getContentType()) {
                case Constant.ItemType.GOOGLE_AD:
                    return TYPE_AD;
                case Constant.ItemType.SE_CUSTOM_AD:
                    return TYPE_CUSTOM_AD;
                case Constant.ItemType.PEOPLE_SUGGESTION:
                    return TYPE_PEOPLE_SUGGESTION;
                case Constant.ItemType.COMPOSER:
                    return TYPE_COMPOSER;
                case Constant.ItemType.STORY:
                    return TYPE_STORY;
                case Constant.ItemType.FEED_FILTER:
                    return TYPE_FILTER;
                case Constant.ItemType.COMM_AD:
                    if (null != list.get(position).getAdType())
                        return TYPE_COMMUNITY_AD;
                    else
                        return TYPE_FEED;
                default:
                    return TYPE_FEED;
            }
        } catch (NullPointerException e) {
            CustomLog.e(e);
            return TYPE_FEED;
        }
    }

    public String unicodeStr(String escapedString) {
        try {
            //return EmojiCompat.get().process(escapedString).toString();
            // return Smileys.getEmojiFromString(escapedString);
            return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(escapedString));
            //return StringEscapeUtils.unescapeJava(escapedString);
        } catch (Exception e) {
            CustomLog.e("warnning", "emoji parsing error at " + escapedString);
        }
        return escapedString;
    }

    public void createPopUp(View v, int position) {
        try {
            ExampleCardPopup popup = new ExampleCardPopup(v.getContext(), position, listener);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            int vertPos = RelativePopupWindow.VerticalPosition.ABOVE;
            int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showOptionsPopUp(View v, List<Options> options, int position) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vHolder, int position) {
        final Activity vo = list.get(position);
        switch (vHolder.getItemViewType()) {
            case TYPE_CUSTOM_AD:
                final SeAdHolder holder = (SeAdHolder) vHolder;
                holder.webview.loadData(vo.getAdContent(), "text/html", "UTF-8");
                break;
            case TYPE_STORY:
                final StoryParentHolder holder02 = (StoryParentHolder) vHolder;
                holder02.adapter = new StoryAdapter(stories, context, listener, holder02.getAdapterPosition());
                holder02.rvChild.setAdapter(holder02.adapter);
                holder02.tvArchive.setOnClickListener(v -> listener.onItemClicked(Constant.Events.STORY_ARCHIVE, null, -1));
                break;
            case TYPE_FILTER:
                try {
                    final FeedFilterHolder feedFilterHolder = (FeedFilterHolder) vHolder;
                    if (null != composerOption.getResult().getFeedSearchOptions()) {
                        feedFilterHolder.recycleViewFeedType.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        feedFilterHolder.recycleViewFeedType.setLayoutManager(layoutManager);
                        feedFilterHolder.adapterFeed = new FeedOptionAdapter(composerOption.getResult().getFeedSearchOptions(), context, listener);
                        feedFilterHolder.recycleViewFeedType.setAdapter(feedFilterHolder.adapterFeed);
                        feedFilterHolder.recycleViewFeedType.setNestedScrollingEnabled(false);
                        feedFilterHolder.recycleViewFeedType.scrollToPosition(StaticShare.LAST_POSITION);
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case TYPE_COMMUNITY_AD:
                final CommunityAd holder01 = (CommunityAd) vHolder;
                if (vo.isHidden()) {
                    holder01.llHiddenView.setVisibility(View.VISIBLE);
                    holder01.llMainView.setVisibility(View.GONE);
                    holder01.tvHideTitle.setText(vo.getHiddenData().getSuccessText());
                   /* holder01.tvHideDescription.setText(vo.isReported() ? vo.getHiddenData().getSuccessText() : vo.getHiddenData().getDescription());
                    holder01.tvUndo.setVisibility(vo.isReported() ? View.GONE : View.VISIBLE);

                    holder01.tvReport.setText(vo.getHiddenData().getOtherText());
                    holder01.tvReport.setVisibility(vo.isReported() ? View.GONE : View.VISIBLE);*/


                } else {
                    holder01.llHiddenView.setVisibility(View.GONE);
                    holder01.llMainView.setVisibility(View.VISIBLE);
                    holder01.ivOption.setVisibility(null != vo.getMenus() ? View.VISIBLE : View.GONE);
                    holder01.ivOption.setOnClickListener(v -> {
                        showOptionsPopUp(v, vo.getMenus(), holder01.getAdapterPosition());
                    });
                    holder01.tvHeader.setText(vo.getTitle());
                    showImageWithGlide(holder01.ivProfileImage, vo.getHeaderImage(), context, R.drawable.default_user);
                    holder01.tvSponsored.setText(vo.getSponsored());
                    holder01.tvSponsored.setVisibility(null != vo.getSponsored() ? View.VISIBLE : View.GONE);
                    if (null != vo.getAttachment()) {
                        holder01.rlAdContent.setVisibility(View.VISIBLE);
                        holder01.tvAdUrl.setText(vo.getAttachment().getUrlDescription());
                        holder01.tvAdUrl.setVisibility(null != vo.getAttachment().getUrlDescription() ? View.VISIBLE : View.GONE);

                        holder01.tvAdTitle.setText(vo.getAttachment().getTitle());
                        holder01.tvAdTitle.setVisibility(null != vo.getAttachment().getTitle() ? View.VISIBLE : View.GONE);

                        holder01.tvCard.setText(vo.getAttachment().getCallToActionOverlay());
                        holder01.tvCard.setVisibility(null != vo.getAttachment().getCallToActionOverlay() ? View.VISIBLE : View.GONE);

                        holder01.tvAdDescription.setText(vo.getAttachment().getDescription());
                        holder01.tvAdDescription.setVisibility(null != vo.getAttachment().getDescription() ? View.VISIBLE : View.GONE);

                        if (null != vo.getAttachment().getCalltoaction()) {
                            holder01.bCallToAction.setVisibility(View.VISIBLE);
                            holder01.bCallToAction.setText(vo.getAttachment().getCalltoaction().getLabel());
                        } else {
                            holder01.bCallToAction.setVisibility(View.GONE);
                        }

                        holder01.cvMain.setOnClickListener(v -> {
                            listener.onItemClicked(Constant.Events.WEBVIEW, vo.getAttachment().getHref(), 1);
                        });
                    } else {
                        holder01.rlAdContent.setVisibility(View.GONE);
                    }

                    if (Constant.ItemType.IMAGE_AD.equals(vo.getAdType())) {
                        holder01.ivAdImage.setVisibility(View.VISIBLE);
                        holder01.rvAd.setVisibility(View.GONE);
                        holder01.jzVideoPlayerStandard.setVisibility(View.GONE);
                        showImageWithGlide(holder01.ivAdImage, vo.getAttachment().getSrc(), context, R.drawable.image_placeholder);

                    } else if (Constant.ItemType.VIDEO_AD.equals(vo.getAdType())) {
                        holder01.ivAdImage.setVisibility(View.GONE);
                        holder01.rvAd.setVisibility(View.GONE);
                        holder01.jzVideoPlayerStandard.setVisibility(View.GONE);
                        holder01.jzVideoPlayerStandard.setUp(proxy.getProxyUrl(vo.getAttachment().getSrc())
                                , null != vo.getAttachment().getTitle() ? vo.getAttachment().getTitle() : " "
                                , JzvdStd2.SCREEN_NORMAL);

                    } else if (Constant.ItemType.CORROSEL_AD.equals(vo.getAdType())) {
                        holder01.ivAdImage.setVisibility(View.GONE);
                        holder01.rvAd.setVisibility(View.VISIBLE);
                        holder01.jzVideoPlayerStandard.setVisibility(View.GONE);
                        // if (holder01.adapter == null) {
                        holder01.rvAd.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder01.rvAd.setLayoutManager(layoutManager);
                        holder01.adapter = new CommunityAdAdapter(vo.getCarouselAttachment(), context, listener);
                        // holder01.adapter.setParentPosition(holder01.getAdapterPosition());
                        holder01.rvAd.setAdapter(holder01.adapter);
                        // } else {
                        holder01.adapter.notifyDataSetChanged();
                        // }
                    }
                }
                break;

            case TYPE_AD:
                break;

            case TYPE_COMPOSER:
                try {
                    final ComposerHolder holder11 = (ComposerHolder) vHolder;
                    // themeManager.applyTheme((ViewGroup) holder11.itemView, context);
                    holder11.tvNoFeed.setVisibility(list.size() > 1 ? View.GONE : View.VISIBLE);
                    if (null != composerOption) {
                        CustomLog.d("j", "j");
                    } else {
                        // holder11.itemView.getLayoutParams().height = 0;
                        ViewGroup.LayoutParams params = holder11.itemView.getLayoutParams();
                        params.height = 0;
                        holder11.itemView.setLayoutParams(params);
                    }
                    //kondisi ikon verivikasi
//                    if (composerOption.getResult().getLevelId() == 3) {
//                        holder11.ivVerify.setImageResource(R.drawable.ic_verified);
//                    }

                    Util.showImageWithGlide(holder11.ivProfileCompose, composerOption.getResult().getUser_image(), context, R.drawable.placeholder_3_2);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;

            case TYPE_PEOPLE_SUGGESTION:
                final PeopleHolder holder2 = (PeopleHolder) vHolder;
                //holder2.tvCategory.setText(skillVo.getName());
                holder2.tvMore.setVisibility(vo.isSeeAll() ? View.VISIBLE : View.GONE);
                holder2.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.SUGGESTION_MAIN, "", -1));
                /*set child item list*/

                if (holder2.adapter == null) {
                    if (vo.getPeoples() != null && vo.getPeoples().size() > 0) {
                        SPref.getInstance().savePeopleSuggestions(context, vo.getPeoples());
                        holder2.rvChild.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new FeedSuggestionAdapter(vo.getPeoples(), context, listener);
                        holder2.adapter.setParentPosition(holder2.getAdapterPosition());
                        holder2.rvChild.setAdapter(holder2.adapter);
                    }
                } else {
                    holder2.adapter.notifyDataSetChanged();
                }
                break;

            default://default is TYPE_FEED
                try {
                    final CommonHolder holderParent = (CommonHolder) vHolder;
                    //themeManager.applyTheme((ViewGroup) vHolder.itemView, context);

                    CustomLog.e("type", " vo.getType(" + position + ") " + vo.getType() + vo.getActionId());
                    if (vo.isHidden()) {
                        holderParent.llHiddenView.setVisibility(View.VISIBLE);
                        holderParent.llMainView.setVisibility(View.GONE);
                        holderParent.tvUndo.setTypeface(iconFont, BOLD);
                        holderParent.tvUndo.setOnClickListener(v -> listener.onItemClicked(Constant.Events.UNDO, "" + position, position));

                        if (vo.isReported()) {
                            holderParent.tvReport.setVisibility(View.VISIBLE);
                            holderParent.tvReport.setTypeface(iconFont, BOLD);
                            holderParent.tvReport.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPORT, "Feed has been reported.", position));

                        } else {
                            holderParent.tvReport.setVisibility(View.GONE);
                        }

                    } else {
                        holderParent.llHiddenView.setVisibility(View.GONE);
                        holderParent.llMainView.setVisibility(View.VISIBLE);
                        holderParent.tvCommentUpper.setText(vo.getCommentCount() > 0 ?
                                (vo.getCommentCount() + (AppConfiguration.theme == 1 ? (vo.getCommentCount() == 1 ? Constant._COMMENT : Constant._COMMENTS) : Constant.EMPTY)) :
                                Constant.EMPTY);
                        holderParent.tvCommentUpper.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, "", position));
                        holderParent.llReactionUpper.setOnClickListener(v -> listener.onItemClicked(Constant.GoTo.REACTION, "", vo.getActionId()));

                        if (AppConfiguration.theme == 2) {
                            holderParent.llReactionUpper.setVisibility((null != vo.getReactionUserData() && !vo.getReactionUserData().isEmpty()) || null != vo.getPostAttribution() ? View.VISIBLE : View.GONE);
                            // TOdo
                            // use like count after fixing the count issue of api
//                            holderParent.tvLikeCount.setText(vo.getLikeCount() > 0 ? vo.getLikeCount() + Constant.EMPTY : Constant.EMPTY);
//                            holderParent.tvLikeCount.setText(vo.isIs_like() ? (vo.getReactionData().size() + 1 + Constant.EMPTY) :
//                                    (vo.getReactionData().size() > 0 ? vo.getReactionData().size() + Constant.EMPTY : Constant.EMPTY));
                        } else {
                            holderParent.llReactionUpper.setVisibility(vo.getCommentCount() > 0
                                    || null != vo.getReactionUserData() /*|| vo.isIs_like()*/ || null != vo.getPostAttribution() ?
                                    View.VISIBLE : View.GONE
                            );
                        }

                        if (vo.getType().equals(Constant.ItemType.UPDATE_STATUS)) {
                            holderParent.flMain.setForeground(ContextCompat.getDrawable(context, R.drawable.scrim_white));
                            holderParent.pbProgressHorizontal.setVisibility(View.VISIBLE);
                            // progress is saved on adId so fetch from adId
                            holderParent.pbProgressHorizontal.setProgress(vo.getAdId());
                            holderParent.pbProgressHorizontal.getProgressDrawable().setColorFilter(Color.parseColor(Constant.colorPrimary), PorterDuff.Mode.SRC_IN);
                            holderParent.rlFeedHeader.setVisibility(View.GONE);
                            holderParent.llMainClick.setVisibility(View.GONE);
                            holderParent.llLikeCommentShare.setVisibility(View.GONE);
                            holderParent.ivSaveFeed.setVisibility(View.GONE);
                        } else {
                            holderParent.flMain.setForeground(null);
                            holderParent.pbProgressCircular.setVisibility(View.GONE);
                            holderParent.pbProgressHorizontal.setVisibility(View.GONE);
                            holderParent.rlFeedHeader.setVisibility(View.VISIBLE);
                            holderParent.llMainClick.setVisibility(View.VISIBLE);
                            holderParent.ivSaveFeed.setVisibility(View.VISIBLE);
                        }

                        holderParent.llLikeCommentShare.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
                        //if (vo.getLikeCount() > 0 || vo.isIs_like()) {
                        if (null != vo.getReactionUserData() && null != vo.getReactionData()) {
                            holderParent.rlUpperLike.setVisibility(View.VISIBLE);
                            holderParent.tvLikeUpper.setVisibility(View.VISIBLE);
                            holderParent.tvLikeUpper.setText(vo.getReactionUserData());
                            if (vo.getReactionData().size() > 0) {
                                holderParent.ivLikeUpper1.setVisibility(View.VISIBLE);
                                Util.showImageWithGlide(holderParent.ivLikeUpper1, vo.getReactionData().get(0).getImageUrl(), context);
                            } else {
                                holderParent.ivLikeUpper1.setVisibility(View.GONE);
                            }
                            if (vo.getReactionData().size() > 1) {
                                holderParent.ivLikeUpper2.setVisibility(View.VISIBLE);
                                Util.showImageWithGlide(holderParent.ivLikeUpper2, vo.getReactionData().get(1).getImageUrl(), context);
                            } else {
                                holderParent.ivLikeUpper2.setVisibility(View.GONE);
                            }
                            if (vo.getReactionData().size() > 2) {
                                holderParent.ivLikeUpper3.setVisibility(View.VISIBLE);
                                Util.showImageWithGlide(holderParent.ivLikeUpper3, vo.getReactionData().get(2).getImageUrl(), context);
                            } else {
                                holderParent.ivLikeUpper3.setVisibility(View.GONE);
                            }
                            if (vo.getReactionData().size() > 3) {
                                holderParent.ivLikeUpper4.setVisibility(View.VISIBLE);
                                Util.showImageWithGlide(holderParent.ivLikeUpper4, vo.getReactionData().get(3).getImageUrl(), context);
                            } else {
                                holderParent.ivLikeUpper4.setVisibility(View.GONE);
                            }
                            if (vo.getReactionData().size() > 4) {
                                holderParent.ivLikeUpper5.setVisibility(View.VISIBLE);
                                Util.showImageWithGlide(holderParent.ivLikeUpper5, vo.getReactionData().get(4).getImageUrl(), context);
                            } else {
                                holderParent.ivLikeUpper5.setVisibility(View.GONE);
                            }
                        } else {
                            if (AppConfiguration.theme == 2) {
                                holderParent.llReactionUpper.setVisibility(View.GONE);
                            } else {
                                holderParent.rlUpperLike.setVisibility(View.INVISIBLE);
                                holderParent.tvLikeUpper.setVisibility(View.INVISIBLE);
                            }
                        }
                        holderParent.tvFeedType.setTypeface(iconFont);
                        if (vo.getActivityIcon().equalsIgnoreCase("f2f6")) {
                            holderParent.tvFeedType.setText(Html.fromHtml(slashU + "f090"));
                        } else {
                            holderParent.tvFeedType.setText(Html.fromHtml(slashU + vo.getActivityIcon()).toString());
                        }


                        Log.e("Activty Icon", "" + vo.getActivityIcon());
                        holderParent.tvDate.setText(Util.changeDateFormat(context, vo.getDate()));

                        holderParent.llShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
                        holderParent.llComment.setVisibility(vo.getCommentable() ? View.VISIBLE : View.GONE);
                        holderParent.llLike.setVisibility(vo.getCommentable() ? View.VISIBLE : View.GONE);

                        if (null != vo.getPostAttribution()) {
                            holderParent.llReactionUpper.setVisibility(View.VISIBLE);
                            holderParent.llAttribution.setVisibility(View.VISIBLE);
                            showImageWithGlide(holderParent.ivAttribution, vo.getAttributionImage(), context, R.drawable.image_placeholder);
                            holderParent.llAttribution.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ATTRIBUTION, holderParent.llAttribution, holderParent.getAdapterPosition()));
                        } else {
                            holderParent.llAttribution.setVisibility(View.GONE);
                        }

                        holderParent.llComment.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, "", holderParent.getAdapterPosition()));
                        holderParent.llLike.setOnLongClickListener(v -> {
                            createPopUp(v, holderParent.getAdapterPosition());
                            return false;
                        });

                        islike = vo.isIs_like();

                        holderParent.llLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.e("like de", "" + islike);
                                if (islike) {
                                    holderParent.tvLike.setTextColor(colorPrimary);
                                    holderParent.ivImageLike.setImageDrawable(dLike);
                                    listener.onItemClicked(Constant.Events.LIKED2, islike ? "-1" : "0", holderParent.getAdapterPosition());
                                    islike = !islike;
                                } else {
                                    holderParent.tvLike.setTextColor(colorText2);
                                    holderParent.ivImageLike.setImageDrawable(like);
                                    listener.onItemClicked(Constant.Events.LIKED2, islike ? "-1" : "0", holderParent.getAdapterPosition());
                                    islike = !islike;
                                }
                            }
                        });

                        // holderParent.llLike.setOnClickListener(v ->);


                        //kondisi ikon verivikasi
//                        if (vo.getItemUser().getLevelId() == 3) {
//                            holderParent.ivVerify.setImageResource(R.drawable.ic_verified);
//                        }
                        holderParent.ivProfileImage.setVisibility(hasToShowRoundImage ? View.INVISIBLE : View.VISIBLE);
                        holderParent.ivProfileImageRound.setVisibility(hasToShowRoundImage ? View.VISIBLE : View.INVISIBLE);
                        showImageWithGlide((hasToShowRoundImage ? holderParent.ivProfileImageRound : holderParent.ivProfileImage), vo.getItemUser().getUser_image(), context, R.drawable.default_user);
                        (hasToShowRoundImage ? holderParent.ivProfileImageRound : holderParent.ivProfileImage).setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", holderParent.getAdapterPosition()));
                        if (vo.isIs_like()) {
                            holderParent.tvLike.setText(vo.getLike().getTitle());
                            holderParent.tvLike.setTextColor(colorPrimary);
                            showImageWithGlide(holderParent.ivImageLike, vo.getLike().getImage(), context, R.drawable.placeholder_menu);

                        } else {
                            holderParent.tvLike.setText(R.string.TXT_LIKE);
                            holderParent.tvLike.setTextColor(colorText2);
                            holderParent.ivImageLike.setImageDrawable(dLike);
                        }

                        holderParent.llShare.setOnClickListener(v -> listener.onItemClicked(Constant.Events.SHARE_FEED, "0", holderParent.getAdapterPosition()));

                        holderParent.ivFbShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
                        holderParent.ivWhatsAppShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);


                        holderParent.ivFbShare.setOnClickListener(v ->
                                listener.onItemClicked(Constant.Events.SHARE_FEED, "" + 1, holderParent.getAdapterPosition()));
                        holderParent.ivWhatsAppShare.setOnClickListener(v ->
                                listener.onItemClicked(Constant.Events.SHARE_FEED, "" + 2, holderParent.getAdapterPosition()));

                        isSaved = false;


                        if (null != vo.getOptions()) {
                            for (Options option : vo.getOptions()) {
                                if (option.getName().equals("save"))
                                    isSaved = true;
                            }
                        }

                        holderParent.ivSaveFeed.setImageDrawable(isSaved ? dSave : dUnsave);

                        holderParent.ivSaveFeed.setOnClickListener(v -> {

                            isSaved = !isSaved;
                            holderParent.ivSaveFeed.setImageDrawable(isSaved ? dSave : dUnsave);

                            int savepos = 0;
                            for (int i = 0; i < vo.getOptions().size(); i++) {
                                if (vo.getOptions().get(i).getName().equals("save") || vo.getOptions().get(i).getName().equals("unsave")) {
                                    savepos = i;
                                    break;
                                }
                            }
                            listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION, "" + holderParent.getAdapterPosition(), savepos);
                        });


                        //icon fb di menu
//                        try {
//                            if (SPref.getInstance().getDefaultInfo(context, Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {
//                                holderParent.llsocialid.setVisibility(View.GONE);
//                            } else {
//                                holderParent.llsocialid.setVisibility(View.VISIBLE);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }


                        String title = vo.getItemUser().getTitle();
                        final int lenTitle = title.length();
                        int startFeelTitle = 0;
                        int endFellTitle = 0;
                        int startLocation = 0;
                        int endLocation = 0;
                        int startTag1 = 0;
                        int endTag1 = 0;
                        int startTag2 = 0;
                        int endTag2 = 0;
                        // final Drawable[] drawable = new Drawable[1];
                        String header = title;
                        if (vo.getFeelings() != null) {
                            String feeling = vo.getFeelings().is_string().replace(" ", "  ");
                            feeling = feeling + " " + (vo.getFeelings().getFeeling_title());
                            startFeelTitle = lenTitle + feeling.length() + 1;
                            feeling = feeling + " " + vo.getFeelings().getTitle();
                            endFellTitle = lenTitle + feeling.length() + 1;
                            header = header + " " + feeling;
                        }

                        if (vo.getTagged() != null) {
                            header = header + Constant._WITH_;
                            startTag1 = header.length();
                            String tagged = vo.getTagged().get(0).getName();
                            header = header + tagged;
                            endTag1 = header.length();
                            if (vo.getTagged().size() > 1) {
                                header = header + Constant._AND_;
                                startTag2 = header.length();
                                header = header + (vo.getTagged().size() - 1) + Constant._OTHERS;
                                endTag2 = header.length();
                            }
                        }


                        if (vo.getLocationActivity() != null) {
                            String location = vo.getLocationActivity().getVenue();
                            header = header + Constant._IN_;
                            startLocation = header.length();
                            header = header + location;
                            endLocation = header.length();
                        }

                        final SpannableString[] spanArr = new SpannableString[1];

                        if (!TextUtils.isEmpty(vo.getActivityTypeContent())) {

                            String content = vo.getActivityTypeContent().replace("\r\n", "");
                            header = header + content;

                            if (vo.getActivityType() != null) {
                                List<ActivityType> actTypelIst = vo.getActivityType();
                                if (actTypelIst.size() > 0) {
                                    for (ActivityType type : actTypelIst) {
                                        if (content.contains(type.getKey())) {
                                            try {
                                                int start;
                                                int end;
                                                if (!TextUtils.isEmpty(type.getSeprator())) {
                                                    header = header.replace(type.getKey(), " " + StringEscapeUtils.unescapeJava(type.getSeprator()) + " " + type.getTitle());
                                                    start = /*headerLength +*/ header.lastIndexOf(type.getTitle()) /*- 5*/;
                                                    end = start + (type.getTitle().length());
                                                } else {
                                                    header = header.replace(type.getKey(), type.getTitle());
                                                    start = /*headerLength +*/ header.lastIndexOf(type.getTitle());
                                                    end = start + (type.getTitle().length());
                                                }

                                                // int end = start + (type.getTitle().length());
                                                type.setEndIndex(end);
                                                type.setStartIndex(start);
                                            } catch (Exception e) {
                                                header = header.replace(type.getKey(), type.getValue());
                                                // int start = /*headerLength +*/ header.lastIndexOf(type.getValue());
                                                // int end = start + (type.getValue().length());
                                                // type.setEndIndex(end);
                                                type.setStartIndex(-1);
                                                // CustomLog.e("EXCEPTION", "EXCEPTIOn IN JSON KEY IS PRESENT BUT TITLE IS NOT PRESENT ");
                                            }
                                        }
                                    }

                                    spanArr[0] = new SpannableString(unicodeStr(header));

                                    for (final ActivityType type : actTypelIst) {
                                        if (type.getStartIndex() > -1) {
                                            spanArr[0].setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_ACTIVITY_TYPE, "" + type.getKey(), holderParent.getAdapterPosition()), type.getStartIndex(), type.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spanArr[0].setSpan(new StyleSpan(BOLD), type.getStartIndex(), type.getEndIndex(), 0);
                                        }
                                    }
                                }
                            }
                        }

                        if (spanArr[0] == null) {
                            spanArr[0] = new SpannableString(header);
                        }

                        final CustomClickableSpan titleSpan = new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_TITLE, null, holderParent.getAdapterPosition());
                        //For Click
                        spanArr[0].setSpan(titleSpan, 0, lenTitle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //For Bold
                        spanArr[0].setSpan(new StyleSpan(BOLD), 0, lenTitle, 0);
                        if (startFeelTitle > 0) {
                            final CustomClickableSpan feelingTitleSpan = new CustomClickableSpan(
                                    listener, Constant.Events.CLICKED_HEADER_FEELING_TITLE, null, holderParent.getAdapterPosition());

                            Glide.with(context).asBitmap()
                                    .load(vo.getFeelings().getIcon())
                                    // .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            CustomLog.e("drawable", "image");
                                            // drawable[0] = new BitmapDrawable(null, resource);
                                            spanArr[0].setSpan(makeImageSpan(/*context, */new BitmapDrawable(null, resource), emojiSize), lenTitle + 4, lenTitle + 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                            holderParent.tvHeader.setText(spanArr[0]);

                                        }
                                    });

                            //headerSpan.setSpan(new ImageSpan(context, R.drawable.like), startFeelTitle, startFeelTitle + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            //headerSpan.setSpan(makeImageSpan(context, drawable[0], 36), lenTitle + 4, lenTitle + 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            //holderParent.tvHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, R.drawable.like, 0);
                            spanArr[0].setSpan(feelingTitleSpan, startFeelTitle, endFellTitle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startFeelTitle, endFellTitle, 0);
                        }
                        if (startLocation > 0) {
                            final CustomClickableSpan locationSpan = new CustomClickableSpan(
                                    listener, Constant.Events.CLICKED_HEADER_LOCATION, null, holderParent.getAdapterPosition());


                            spanArr[0].setSpan(locationSpan, startLocation, endLocation, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startLocation, endLocation, 0);
                        }

                        if (startTag1 > 0) {
                            final CustomClickableSpan tag1Span = new CustomClickableSpan(
                                    listener, Constant.Events.CLICKED_HEADER_TAGGED_1, null, position);


                            spanArr[0].setSpan(tag1Span, startTag1, endTag1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startTag1, endTag1, 0);
                        }

                        if (startTag2 > 0) {
                            final CustomClickableSpan tag2Span = new CustomClickableSpan(
                                    listener, Constant.Events.CLICKED_HEADER_TAGGED_2, null, position);


                            spanArr[0].setSpan(tag2Span, startTag2, endTag2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startTag2, endTag2, 0);
                        }

                        holderParent.tvHeader.setText(spanArr[0]);
                        holderParent.tvHeader.setMovementMethod(LinkMovementMethod.getInstance());


                        Glide.with(context).load(vo.getPrivacyImageUrl()).into(holderParent.ivFeedPrivacy);
                        holderParent.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                        holderParent.ivOption.setOnClickListener(v -> {
                            showOptionsPopUp(v, vo.getOptions(), holderParent.getAdapterPosition());
                        });


                        if (vo.getAttachment() != null && vo.getAttachment().getAttachmentType().equals(Constant.ATTACHMENT_TYPE_ALBUM_PHOTO)) {
                            holderParent.llMultipleImageMain.setVisibility(View.VISIBLE);
                            int count = (vo.getAttachment().getImages() != null) ? vo.getAttachment().getImages().size() : 0;
                            if (count == 1) {
                                holderParent.ivImage11.setVisibility(View.VISIBLE);
                                holderParent.llMultipleImage2.setVisibility(View.GONE);
                                holderParent.llMultipleImage3.setVisibility(View.GONE);
                                holderParent.llMultipleImage4.setVisibility(View.GONE);
                                holderParent.llMultipleImage5.setVisibility(View.GONE);
                                showImageWithGlide(holderParent.ivImage11, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_3_2);
                                holderParent.ivImage11.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, "0", position));
                            } else if (count == 2) {
                                holderParent.ivImage11.setVisibility(View.GONE);
                                holderParent.llMultipleImage2.setVisibility(View.VISIBLE);
                                holderParent.llMultipleImage3.setVisibility(View.GONE);
                                holderParent.llMultipleImage4.setVisibility(View.GONE);
                                holderParent.llMultipleImage5.setVisibility(View.GONE);
                                showImageWithGlide(holderParent.ivImage21, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage22, vo.getAttachment().getImages().get(1).getMain(), context, R.drawable.placeholder_square);
                                holderParent.ivImage21.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, "0", holderParent.getAdapterPosition()));
                                holderParent.ivImage22.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_2, "0", holderParent.getAdapterPosition()));
                            } else if (count == 3) {
                                holderParent.ivImage11.setVisibility(View.GONE);
                                holderParent.llMultipleImage2.setVisibility(View.GONE);
                                holderParent.llMultipleImage3.setVisibility(View.VISIBLE);
                                holderParent.llMultipleImage4.setVisibility(View.GONE);
                                holderParent.llMultipleImage5.setVisibility(View.GONE);
                                showImageWithGlide(holderParent.ivImage31, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.image_placeholder);
                                showImageWithGlide(holderParent.ivImage32, vo.getAttachment().getImages().get(1).getMain(), context, R.drawable.image_placeholder);
                                showImageWithGlide(holderParent.ivImage33, vo.getAttachment().getImages().get(2).getMain(), context, R.drawable.image_placeholder);
                                holderParent.ivImage31.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, "0", holderParent.getAdapterPosition()));
                                holderParent.ivImage32.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_2, "0", holderParent.getAdapterPosition()));
                                holderParent.ivImage33.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_3, "0", holderParent.getAdapterPosition()));
                            } else if (count == 4) {
                                holderParent.ivImage11.setVisibility(View.GONE);
                                holderParent.llMultipleImage2.setVisibility(View.GONE);
                                holderParent.llMultipleImage3.setVisibility(View.GONE);
                                holderParent.llMultipleImage4.setVisibility(View.VISIBLE);
                                holderParent.llMultipleImage5.setVisibility(View.GONE);
                                showImageWithGlide(holderParent.ivImage41, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage42, vo.getAttachment().getImages().get(1).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage43, vo.getAttachment().getImages().get(2).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage44, vo.getAttachment().getImages().get(3).getMain(), context, R.drawable.placeholder_square);
                                holderParent.ivImage41.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, "0", position));
                                holderParent.ivImage42.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_2, "0", position));
                                holderParent.ivImage43.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_3, "0", position));
                                holderParent.ivImage44.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_4, "0", position));

                            } else if (count > 4) {
                                holderParent.ivImage11.setVisibility(View.GONE);
                                holderParent.llMultipleImage2.setVisibility(View.GONE);
                                holderParent.llMultipleImage3.setVisibility(View.GONE);
                                holderParent.llMultipleImage4.setVisibility(View.GONE);
                                holderParent.llMultipleImage5.setVisibility(View.VISIBLE);

                                showImageWithGlide(holderParent.ivImage51, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage52, vo.getAttachment().getImages().get(1).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage53, vo.getAttachment().getImages().get(2).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage54, vo.getAttachment().getImages().get(3).getMain(), context, R.drawable.placeholder_square);
                                showImageWithGlide(holderParent.ivImage55, vo.getAttachment().getImages().get(4).getMain(), context, R.drawable.placeholder_square);


                                holderParent.ivImage51.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, "0", position));
                                holderParent.ivImage52.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_2, "0", position));
                                holderParent.ivImage53.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_3, "0", position));
                                holderParent.ivImage54.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_4, "0", position));


                                if (count > 5) {
                                    holderParent.tvPlus.setVisibility(View.VISIBLE);
                                    holderParent.vPlusCount.setVisibility(View.VISIBLE);
                                    holderParent.tvPlus.setText("+" + (vo.getAttachment().getTotalImagesCount() - 5));
                                } else {
                                    holderParent.tvPlus.setVisibility(View.GONE);
                                    holderParent.vPlusCount.setVisibility(View.GONE);
                                }
                                holderParent.rlLowerlast.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_5, "0", position));
                            } else {
                                holderParent.llMultipleImageMain.setVisibility(View.GONE);
                            }

                        } else {
                            holderParent.llMultipleImageMain.setVisibility(View.GONE);
                        }

                        String body = null;
                        SpannableString span = null;
                        // List<CustomClickableSpan> spanList = new ArrayList<>();
                        if (vo.getActivityType() != null) {
                            List<ActivityType> actTypelIst = vo.getActivityType();
                            if (actTypelIst.size() > 0) {
                                for (ActivityType type : actTypelIst) {
                                    if (type.getKey().equals(Constant.KEY_SPECIAL_BODY)) {
                                        body = unicodeStr(type.getValue());
                                        CustomLog.d("body", body);
                                        try {

                                            if (vo.getMention() != null) {
                                                List<Mention> mentionList = vo.getMention();
                                                List<Mention> list2 = new ArrayList<>();
                                                for (Mention men : mentionList) {
                                                    body = body.replace(men.getWord(), men.getTitle());
                                                    int startMention = body.indexOf(men.getTitle());
                                                    int endMention = men.getTitle().length();
                                                    men.setStartIndex(startMention);
                                                    men.setEndIndex(startMention + endMention);
                                                    list2.add(men);
                                                }

                                                if (span == null) {
                                                    span = new SpannableString(body);
                                                }
                                                //  span = new SpannableString(body);
                                                for (final Mention men : list2) {
                                                     body = body.replace(men.getWord(), men.getTitle());
                                                    if (men.getStartIndex() > -1) {
                                                        span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_TAGGED, "" + men.getUserId(), position)
                                                                , men.getStartIndex(), men.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        span.setSpan(new StyleSpan(BOLD), men.getStartIndex(), men.getEndIndex(), 0);
                                                    }
                                                }
                                            }

                                            if (span == null) {
                                                span = new SpannableString(body);
                                            }
                                            span = getTextColorSpan(span);
                                            if (vo.getHashTags() != null) {
                                                List<String> hashList = vo.getHashTags();
                                                CustomLog.e("body", body);
                                                for (final String men : hashList) {
                                                    int startMention = body.indexOf(men);
                                                    int endMention = startMention + men.length();
                                                    // body = body.replace(men.getWord(), men.getTitle());
                                                    if (startMention > -1) {
                                                        span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_HASH_TAGGED, "" + men, position)
                                                                , startMention, endMention, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        span.setSpan(new StyleSpan(BOLD), startMention, endMention, 0);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            CustomLog.e(e);
                                        }
                                        break;
                                    }
                                }
                            }
                        }

//                        if (null != vo.getBody()) {
//                            // actionId =1 means its a dummy cintent
//                            body = Util.stripHtml(vo.getBody());
//                            span = new SpannableString(body);
//                        }

                        if (!TextUtils.isEmpty(body)) {
                            holderParent.tvBodyText.setVisibility(View.VISIBLE);
                            holderParent.tvBodyText.setTextColor(Color.parseColor("#000000"));

                            if (AppConfiguration.truncateBody && span.length() > AppConfiguration.descriptionTrucationLimitFeed) {
                                holderParent.tvSeeMore.setVisibility(View.VISIBLE);
                                holderParent.tvBodyText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConfiguration.descriptionTrucationLimitFeed)});
                                holderParent.tvSeeMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.SEE_MORE, null, vo.getActionId()));
                            } else {
                                holderParent.tvSeeMore.setVisibility(View.GONE);
                            }
                            if (vo.getFornSize() > 0) {
                                holderParent.tvBodyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, vo.getFornSize());
                            }
                            String bdy = span.toString();
                           // holderParent.tvBodyText.setText(Util.getEmojiFromString(bdy));


                            holderParent.tvBodyText.setText(Util.getEmojiFromString(body));
                            holderParent.tvBodyText.setMovementMethod(LinkMovementMethod.getInstance());

                        } else {
                            holderParent.tvBodyText.setVisibility(View.GONE);
                            holderParent.tvSeeMore.setVisibility(View.GONE);
                        }


                        /*show hide in-list comment UI start*/
                        if (null != vo.getComment()) {
                            holderParent.cvEditText.setCardBackgroundColor(SesColorUtils.getAppBgColor(context));
                            holderParent.cvComment.setCardBackgroundColor(SesColorUtils.getAppBgColor(context));
                            holderParent.rlCommentView.setVisibility(View.VISIBLE);
                            showImageWithGlide(holderParent.ivProfileImageComment, vo.getComment().getUserImage(), context, R.drawable.default_user);
                            showImageWithGlide(holderParent.ivUserImageComment, SPref.getInstance().getUserMasterDetail(context).getPhotoUrl(), context, R.drawable.placeholder_square);
                            holderParent.tvHeaderComment.setText(vo.getComment().getUserTitle());
                            holderParent.tvBodyComment.setText(unicodeStr(vo.getComment().getBody()));


                            holderParent.rlCommentView.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, holderParent.etFeedComment.getText().toString(), holderParent.getAdapterPosition()));

                            if (!TextUtils.isEmpty(vo.getComment().getGif_url()) && vo.getComment().getGif_url().length() > 0) {
                                holderParent.tvImageComment222.setVisibility(View.VISIBLE);
                                holderParent.tvBodyComment.setVisibility(View.GONE);
                                Util.showImageWithGlide(holderParent.tvImageComment222, vo.getComment().getGif_url(), context, R.drawable.placeholder_3_2);
                            } else {
                                holderParent.tvBodyComment.setVisibility(View.VISIBLE);
                                holderParent.tvImageComment222.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(vo.getComment().getBody())) {
                                String body2 = unecodeStr(vo.getComment().getBody());
                                SpannableString span2 = null;
                                try {


                                    List<Mention> mentionList = vo.getComment().getMention();
                                    List<Mention> list2 = new ArrayList<>();
                                    for (Mention men : mentionList) {
                                        body2 = body2.replace(men.getWord(), men.getTitle());
                                        int startMention = body2.indexOf(men.getTitle());
                                        int endMention = men.getTitle().length();
                                        men.setStartIndex(startMention);
                                        men.setEndIndex(startMention + endMention);
                                        list2.add(men);
                                    }

                                    span2 = new SpannableString(body2);
                                    for (final Mention men : list2) {
                                        // body = body.replace(men.getWord(), men.getTitle());
                                        if (men.getStartIndex() > -1) {
                                            span2.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_TAGGED, "" + men.getUserId(), holderParent.getAdapterPosition()), men.getStartIndex(), men.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            span2.setSpan(new StyleSpan(BOLD), men.getStartIndex(), men.getEndIndex(), 0);
                                        }
                                    }

                                    try {
                                        Log.e("Data details", "" + span2);
                                        holderParent.tvBodyComment.setText(span2);
                                        holderParent.tvBodyComment.setMovementMethod(LinkMovementMethod.getInstance());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            //  holderParent.etFeedComment.setKeyListener(null);

                            holderParent.etFeedComment.setClickable(true);
                            holderParent.etFeedComment.setFocusable(false);
                            holderParent.etFeedComment.setInputType(InputType.TYPE_NULL);

                            holderParent.etFeedComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listener.onItemClicked(Constant.Events.COMMENT, "", holderParent.getAdapterPosition());
                                }
                            });


                            holderParent.ivPostIcon.setOnClickListener(v -> {
                                listener.onItemClicked(Constant.Events.FEED_COMMENT,
                                        holderParent.etFeedComment.getText().toString(), holderParent.getAdapterPosition());
                                holderParent.etFeedComment.setText(Constant.EMPTY);
                            });

                        } else {
                            holderParent.rlCommentView.setVisibility(View.GONE);
                        }

                        /*show hide in-list comment UI ends*/
                        if (TextUtils.isEmpty(vo.getHashTagString())) {
                            holderParent.tvFeedTags.setVisibility(View.GONE);
                        } else {
                            holderParent.tvFeedTags.setVisibility(View.VISIBLE);
                            holderParent.tvFeedTags.setText(getClickableTags(vo.getActivityTags(), holderParent.getAdapterPosition()));
                            holderParent.tvFeedTags.setMovementMethod(LinkMovementMethod.getInstance());
                        }

                        //Boost Post
                        holderParent.bBoost.setText(vo.getBoostPostLabel());

                        holderParent.bBoost.setVisibility(null != vo.getBoostPostLabel() ? View.VISIBLE : View.GONE);

                        //handling boosted post : hide date layout and show sponsored text

                        holderParent.llFeedDate.setVisibility(vo.canShowDateAndType() ? View.VISIBLE : View.GONE);

                        if (null != vo.getSponsored()) {
                            holderParent.tvSponsored.setText(vo.getSponsored());
                            holderParent.tvSponsored.setVisibility(View.VISIBLE);

                        } else {
                            holderParent.tvSponsored.setVisibility(View.GONE);
                        }

                        //BG IMAGE
                        if (vo.getBg_image() != null) {
                            holderParent.tvBodyText.setVisibility(View.GONE);
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.VISIBLE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);

                            showImageWithGlide(holderParent.ivBgImage, vo.getBg_image(), context, R.drawable.placeholder_3_2);
                            holderParent.tvImageForegroundText.setText(span);
                            holderParent.tvImageForegroundText.setMovementMethod(LinkMovementMethod.getInstance());

                            if (vo.getFornSize() > 0) {
                                holderParent.tvImageForegroundText.setTextSize(TypedValue.COMPLEX_UNIT_SP, vo.getFornSize());
                            }
                        } else if (vo.getCoverPhotoUrl() != null) {
                            holderParent.tvBodyText.setVisibility(View.GONE);
                            holderParent.llMultipleImageMain.setVisibility(View.GONE);
                            holderParent.rlChangedProfile.setVisibility(View.VISIBLE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);

                            showImageWithGlide(holderParent.ivChangedCover, vo.getCoverPhotoUrl(), context, R.drawable.placeholder_3_2);
                            showImageWithGlide(holderParent.ivChangedProfile, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_3_2);
                            holderParent.ivChangedProfile.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, null, position));
                        }

                        // Poll
                        else if (vo.getPoll() != null) {
                            holderParent.tvBodyText.setVisibility(View.GONE);
                            holderParent.llMultipleImageMain.setVisibility(View.GONE);
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.VISIBLE);

                            holderParent.tvPollTitle.setText(vo.getPoll().getTitle());
                            holderParent.tvPollDesc.setText(vo.getPoll().getDescription());
                            if (holderParent.adapter == null || vo.canUpdate()) {
                                // if (vo.getPoll().getOptions().size() > 0) {
                                vo.setCanUpdate(false);
                                holderParent.rvPoll.setHasFixedSize(true);
                                LinearLayoutManager layoutManager;
                                if (vo.getPoll().getOptions().get(0).getImageType() == 0) {
                                    layoutManager = new LinearLayoutManager(context);
                                } else {
                                    layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                }
                                holderParent.rvPoll.setLayoutManager(layoutManager);
                                holderParent.rvPoll.setNestedScrollingEnabled(false);
                                holderParent.adapter = new PollOptionAdapter(vo.getPoll().getOptions(), context, listener, holderParent.getAdapterPosition());
                                holderParent.adapter.showQuestion(vo.getPoll().setQuestionVisibility());
                                holderParent.adapter.setPoll(vo.getPoll());
                                holderParent.tvPollResult.setText(vo.getPoll().getQuestionVisibility() ? R.string.show_result : R.string.poll_show_question);
                                //holderParent.adapter.setParentPosition(holderParent.getAdapterPosition());
                                holderParent.rvPoll.setAdapter(holderParent.adapter);
                                //  }
                            } else {
                                holderParent.adapter.notifyDataSetChanged();
                            }
                            holderParent.llPollTypeChange.setVisibility(vo.getPoll().getIsClosed() != 0 ? View.GONE : View.VISIBLE);
                            holderParent.llPollTypeChange.setOnClickListener(v -> {
                                holderParent.adapter.showQuestion(vo.getPoll().toggleQuestionVisibility());
                                holderParent.tvPollResult.setText(vo.getPoll().getQuestionVisibility() ? R.string.show_result : R.string.poll_show_question);
                                holderParent.adapter.notifyDataSetChanged();

                            });
                            holderParent.llPoll.setOnClickListener(v ->
                                    listener.onItemClicked(Constant.Events.VIDEO, null, position));
                        }

                        // BUY SELL
                        else if (vo.getType().equalsIgnoreCase(Constant.ACTIVITY_TYPE_BUY_SELL)) {
                            holderParent.llBuySellmain.setVisibility(View.VISIBLE);
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);


                            holderParent.tvBuySellTitle.setText(vo.getAttachment().getTitle());
                            holderParent.tvBuySellPrice.setText(vo.getAttachment().getPrice());
                            holderParent.tvBuySellLocation.setText(vo.getAttachment().getLocation());
                            holderParent.tvBuySellDescription.setText(vo.getAttachment().getDescription());
                            holderParent.llLocation.setVisibility(TextUtils.isEmpty(vo.getAttachment().getLocation()) ? View.GONE : View.VISIBLE);
                            holderParent.tvBuySellPrice.setVisibility(TextUtils.isEmpty(vo.getAttachment().getPrice()) ? View.GONE : View.VISIBLE);
                            holderParent.tvBuySellTitle.setVisibility(TextUtils.isEmpty(vo.getAttachment().getTitle()) ? View.GONE : View.VISIBLE);
                            holderParent.tvBuySellDescription.setVisibility(TextUtils.isEmpty(vo.getAttachment().getDescription()) ? View.GONE : View.VISIBLE);
                            if (vo.getAttachment().isCan_message_owner()) {
                                holderParent.bSold.setVisibility(View.VISIBLE);
                                holderParent.bSold.setText(R.string.MSG_SOLD_MESSAGE);
                            } else if (vo.getAttachment().isCan_mark_sold()) {
                                holderParent.bSold.setVisibility(View.VISIBLE);
                                holderParent.bSold.setText(R.string.MSG_SOLD_MARK);
                            } else {
                                holderParent.bSold.setVisibility(View.GONE);
                            }

                            holderParent.bBuy.setVisibility(TextUtils.isEmpty(vo.getAttachment().getBuyUrl()) ? View.GONE : View.VISIBLE);
                            holderParent.bBuy.setOnClickListener(v -> listener.onItemClicked(Constant.Events.BUY, null, holderParent.getAdapterPosition()));

                            /*if(vo.getAttachment().isSold()){
                                holderParent.tvBuySellSoldOut.setVisibility(View.VISIBLE);
                            }*/

                            //holderParent.bSold.setVisibility(/*!isLoggedIn && */vo.getAttachment().isSold() ? View.GONE : View.VISIBLE);
                            //holderParent.bSold.setText(vo.getAttachment().isCan_message_owner() ? Constant.MSG_SOLD_MESSAGE : Constant.MSG_SOLD_MARK);
                            holderParent.tvSoldBottomText.setVisibility(vo.getAttachment().isCan_message_owner() ? View.VISIBLE : View.GONE);
                            holderParent.tvBuySellSoldOut.setVisibility(/*!isLoggedIn && */vo.getAttachment().isSold() ? View.VISIBLE : View.GONE);
                            holderParent.bSold.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MARK_SOLD, "", position));
                        }

                        // FILE ATTACHMENT
                        else if (vo.getType().equalsIgnoreCase(Constant.ACTIVITY_TYPE_FILE)) {
                            holderParent.llMainFile.setVisibility(View.VISIBLE);
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);

                            if (vo.getAttachment() != null) {
                                holderParent.llMainFile.setVisibility(View.VISIBLE);
                                holderParent.tvFileName.setVisibility(TextUtils.isEmpty(vo.getAttachment().getTitle()) ? View.GONE : View.VISIBLE);
                                holderParent.tvFileType.setVisibility(TextUtils.isEmpty(vo.getAttachment().getFile_type()) ? View.GONE : View.VISIBLE);
                                holderParent.tvFileType.setText(vo.getAttachment().getFile_type());
                                holderParent.tvFileName.setText(vo.getAttachment().getTitle());
                                showImageWithGlide(holderParent.ivFileType, vo.getAttachment().getFile_type_image(), context, R.drawable.placeholder_square);

                                holderParent.tvFilePreview.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_FILE_PREVIEW, vo.getAttachment().getPreview_url(), position));
                            } else {
                                holderParent.llMainFile.setVisibility(View.GONE);
                            }

                        }

                        // QUOTE
                        else if (vo.getType().equals(Constant.ACTIVITY_TYPE_QUOTE_1) || vo.getType().equals(Constant.ACTIVITY_TYPE_QUOTE_2)) {
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.VISIBLE);
                            if (null != vo.getAttachment().getImages()) {
                                holderParent.rlQuoteMedia.setVisibility(View.VISIBLE);
                                showImageWithGlide(holderParent.ivQuoteImage, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_3_2);
                                holderParent.ivQuoteMediaType.setVisibility(vo.getAttachment().isPhoto() ? View.GONE : View.VISIBLE);
                            } else {
                                holderParent.rlQuoteMedia.setVisibility(View.GONE);
                            }
                            holderParent.tvQuoteTitle.setText(vo.getAttachment().getTitle());
                            //  holderParent.tvQuoteDesc.setText(vo.getAttachment().getDescription());
                            if (TextUtils.isEmpty(vo.getAttachment().getDescription())) {
                                holderParent.tvQuoteDesc.setVisibility(View.GONE);
                            } else {
                                holderParent.tvQuoteDesc.setVisibility(View.VISIBLE);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    holderParent.tvQuoteDesc.setText(Html.fromHtml(vo.getAttachment().getDescription(), Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    holderParent.tvQuoteDesc.setText(Html.fromHtml(vo.getAttachment().getDescription()));
                                }
                                holderParent.tvQuoteDesc.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            holderParent.tvQuoteCategory.setText("- " + vo.getAttachment().getSource());

                            holderParent.tvQuoteTitle.setVisibility(TextUtils.isEmpty(vo.getAttachment().getTitle()) ? View.GONE : View.VISIBLE);
                            holderParent.tvQuoteCategory.setVisibility(TextUtils.isEmpty(vo.getAttachment().getSource()) ? View.GONE : View.VISIBLE);


                            // holderParent.tvQuoteTags.setText("#Love #Happiness");
                            holderParent.llQuoteMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition()));

                  /*holderParent.tvQuoteCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onItemClicked(Constant.Events.CATEGORY, "Relations", 13);
                        }
                    });*/
                        } else {
                            //VIDEO HOLDER
                            holderParent.rlChangedProfile.setVisibility(View.GONE);
                            holderParent.llMainFile.setVisibility(View.GONE);
                            holderParent.llBuySellmain.setVisibility(View.GONE);
                            holderParent.llVideoMain.setVisibility(View.GONE);
                            holderParent.llSingleImage.setVisibility(View.GONE);
                            holderParent.llBgImage.setVisibility(View.GONE);
                            holderParent.llPoll.setVisibility(View.GONE);
                            holderParent.llQuoteMain.setVisibility(View.GONE);
                            if (vo.getAttachment() != null && !vo.getAttachment().getAttachmentType().equals(Constant.ATTACHMENT_TYPE_ALBUM_PHOTO)) {

                                if (vo.getAttachment().getReaction_image() != null) {
                                    holderParent.llVideoMain.setVisibility(View.GONE);
                                    holderParent.llSingleImage.setVisibility(View.VISIBLE);

                                    if (vo.getAttachment().getReaction_image().endsWith(".gif")) {
                                        holderParent.ivStickerImage.setVisibility(View.GONE);
                                        holderParent.ivSingleImage.setVisibility(View.GONE);
                                        holderParent.ivGIF.setVisibility(View.VISIBLE);
                                        holderParent.ivMarker.setVisibility(View.GONE);
                                        showImageWithGlide(holderParent.ivGIF, vo.getAttachment().getReaction_image(), context, R.drawable.image_placeholder);

                                    } else if (vo.getAttachment().getAttachmentType().equals("sesadvancedcomment_emotionfile")) {
                                        holderParent.ivStickerImage.setVisibility(View.VISIBLE);
                                        holderParent.ivSingleImage.setVisibility(View.GONE);
                                        holderParent.ivMarker.setVisibility(View.GONE);
                                        holderParent.ivGIF.setVisibility(View.GONE);
                                        showImageWithGlide(holderParent.ivStickerImage, vo.getAttachment().getReaction_image(), context, R.drawable.placeholder_3_2);

                                    } else {
                                        holderParent.ivStickerImage.setVisibility(View.GONE);
                                        holderParent.ivSingleImage.setVisibility(View.VISIBLE);
                                        holderParent.ivGIF.setVisibility(View.GONE);
                                        holderParent.ivMarker.setVisibility(View.GONE);
                                        showImageWithGlide(holderParent.ivSingleImage, vo.getAttachment().getReaction_image(), context, R.drawable.placeholder_3_2);
                                    }
                                } else {
                                    try {
                                        holderParent.llSingleImage.setVisibility(View.GONE);
                                        /*  hide video layout if no image ,title or description */
                                        holderParent.llVideoMain.setVisibility(vo.getAttachment().getImages() == null && TextUtils.isEmpty(vo.getAttachment().getTitle())
                                                && TextUtils.isEmpty(vo.getAttachment().getDescription()) ? View.GONE : View.VISIBLE);
                                        if (null != vo.getAttachment().getImages() && vo.getAttachment().getImages().size() > 0) {
                                            if (null != vo.getAttachment().getVideoUrl()) {

                                                JZDataSource mJzDataSource = new JZDataSource(vo.getAttachment().getVideoUrl(), vo.getAttachment().getTitle());

                                                //playing video in feed
                                                if (!canPlay && !canPlay2) {
                                                    holderParent.ivImage.setVisibility(View.VISIBLE);
                                                    holderParent.ivVideoPlaceholder.setVisibility(View.GONE);

                                                    holderParent.ivImage.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition());
                                                        }
                                                    });
                                                } else {
                                                    holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                                    holderParent.ivImage.setVisibility(View.GONE);
                                                }
                                                holderParent.agPlayer.setVisibility(View.GONE);
                                                //  holderParent.ivImage.setVisibility(View.GONE);
                                                //holderParent.jzVideoPlayerStandard.setVisibility(View.GONE);

                                                if (vo.getAttachment().is_can_play()) {
                                                    holderParent.agPlayer.setVisibility(View.VISIBLE);
                                                    holderParent.ivImage.setVisibility(View.GONE);
                                                    holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                                    holderParent.rlVideoImage.setVisibility(View.GONE);
                                                    holderParent.jzVideoPlayerStandard.setVisibility(View.GONE);
                                                    holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                                    try {
                                                        holderParent.agPlayer.destroyDrawingCache();
                                                        holderParent.agPlayer.reset();
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }

                                                    holderParent.agPlayer.setUp(mJzDataSource
                                                            , JzvdStd.SCREEN_NORMAL, MediaExo.class);
                                                    holderParent.agPlayer.startVideo();
                                                    Util.showImageWithGlide(holderParent.agPlayer.posterImageView, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_3_2);
                                                } else {
                                                    try {
                                                        holderParent.agPlayer.destroyDrawingCache();
                                                        holderParent.agPlayer.reset();
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                    holderParent.jzVideoPlayerStandard.setVisibility(View.GONE);
                                                    holderParent.agPlayer.setVisibility(View.GONE);
                                                    holderParent.ivImage.setVisibility(View.VISIBLE);
                                                    holderParent.ivVideoPlaceholder.setVisibility(View.VISIBLE);
                                                }
                                                holderParent.rlVideoImage.setVisibility(View.VISIBLE);
                                                if (null != vo.getAttachment().getImages() && vo.getAttachment().getImages().size() > 0) {
                                                    Util.showImageWithGlide(holderParent.ivImage, vo.getAttachment().getImages().get(0).getMain(), context);
                                                    Glide.with(holderParent.jzVideoPlayerStandard.getContext()).load(vo.getAttachment().getImages().get(0).getMain()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                                    //Glide.with(context).load(vo.getAttachment().getImages().get(0).getMain()).apply(new RequestOptions()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                                } else {
                                                    Util.showImageWithGlide(holderParent.ivImage, "", context);
                                                    Glide.with(holderParent.jzVideoPlayerStandard.getContext()).load("").into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                                    //Glide.with(context).load("").apply(new RequestOptions()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                                }
                                                holderParent.jzVideoPlayerStandard.setUp(proxy.getProxyUrl(vo.getAttachment().getVideoUrl())
                                                        , null != vo.getAttachment().getTitle() ? vo.getAttachment().getTitle() : " "
                                                        , JzvdStd2.SCREEN_NORMAL);
                                                //  holderParent.jzVideoPlayerStandard.startVideo();
                                                if (canPlay) {
                                                    holderParent.jzVideoPlayerStandard.startButton.performClick();
                                                } else {
                                                    //do nothing here
                                                }
                                            } else {
                                                holderParent.jzVideoPlayerStandard.setVisibility(View.GONE);
                                                holderParent.ivImage.setVisibility(View.VISIBLE);
                                                holderParent.rlVideoImage.setVisibility(View.VISIBLE);
                                                showImageWithGlide(holderParent.ivImage, vo.getAttachment().getImages().get(0).getMain(), context, R.drawable.placeholder_3_2);
                                            }
                                        } else {
                                            holderParent.ivImage.setVisibility(View.GONE);
                                            holderParent.rlVideoImage.setVisibility(View.GONE);
                                            holderParent.agPlayer.changeUiToPauseShow();
                                            holderParent.agPlayer.setVisibility(View.GONE);
                                        }
                                        holderParent.tvImageDescription.setText(vo.getAttachment().getDescription());
                                        holderParent.tvImageDescription.setMovementMethod(new TextViewClickMovement(listener, context, holderParent.getAdapterPosition()));
                                        //  URLEncoder.encode(vo.getAttachment().getDescription(), "utf-8");
                                        holderParent.tvImageTitle.setText(unicodeStr(vo.getAttachment().getTitle()));
                                        //  holderParent.tvImageTitle.setTypeface(Typeface.DEFAULT_BOLD);
                                        holderParent.tvImageTitle.setVisibility(TextUtils.isEmpty(vo.getAttachment().getTitle()) ? View.GONE : View.VISIBLE);
                                        holderParent.tvImageDescription.setVisibility(TextUtils.isEmpty(vo.getAttachment().getDescription()) ? View.GONE : View.VISIBLE);

                                        holderParent.tvImageTitle.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition());
                                            }
                                        });


                                        holderParent.llVideoMain.setOnClickListener(v ->
                                                        //  listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition())
                                                {


                                                    Log.e("AttachmentType", "" + vo.getAttachment().getAttachmentType());
                                                    if (vo.getAttachment().getAttachmentType().equalsIgnoreCase("video")) {

                                                        listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition());
                                                     /*  JZDataSource mJzDataSource = new JZDataSource(vo.getAttachment().getVideoUrl(), vo.getAttachment().getTitle());
                                                       try {
                                                           holderParent.agPlayer.destroyDrawingCache();
                                                           holderParent.agPlayer.reset();
                                                       }catch (Exception ex){
                                                           ex.printStackTrace();
                                                       }
                                                       holderParent.agPlayer.setUp(mJzDataSource
                                                               , JzvdStd.SCREEN_NORMAL, MediaExo.class);
                                                       holderParent.agPlayer.startVideo();
                                                       holderParent.agPlayer.setVisibility(View.VISIBLE);
                                                       holderParent.ivImage.setVisibility(View.GONE);
                                                       holderParent.rlVideoImage.setVisibility(View.GONE);*/

                                                    } else {
                                                        listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition());
                                                    }
                                                }
                                        );
                                    } catch (Exception e) {
                                        CustomLog.e(e);
                                    }
                                }
                                switch (vo.getAttachment().getAttachmentType()) {
                                    case Constant.ATTACHMENT_TYPE_VIDEO:
                                        if (canPlay && null != vo.getAttachment().getVideoUrl()) {
                                            canPlay = false;
                                            canPlay2 = false;
                                            holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                        } else if (canPlay2 && null != vo.getAttachment().getVideoUrl()) {
                                            holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                        } else {
                                            canPlay2 = false;
                                            holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                            holderParent.ivVideoPlaceholder.setImageDrawable(dPlay);
                                        }
                                        break;

                                    case Constant.ATTACHMENT_TYPE_MUSIC_ALBUM:
                                    case Constant.ACTIVITY_TYPE_ALBUM_SONG:
                                        holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                        holderParent.ivVideoPlaceholder.setImageDrawable(dMusicPlayer);
                                        break;

                                    default:
                                        holderParent.ivVideoPlaceholder.setVisibility(View.GONE);
                                        break;

                                }
                            } else {
                                holderParent.llVideoMain.setVisibility(View.GONE);
                                if (vo.getAttachment() == null && vo.getLocationActivity() != null) {

                                    CustomLog.e("location_feed", "" + position);
                                    holderParent.llSingleImage.setVisibility(View.VISIBLE);
                                    holderParent.ivSingleImage.setVisibility(View.VISIBLE);
                                    holderParent.ivStickerImage.setVisibility(View.GONE);
                                    holderParent.ivMarker.setVisibility(View.VISIBLE);
                                    holderParent.ivGIF.setVisibility(View.GONE);
                                    showImageWithGlide(holderParent.ivSingleImage,
                                            Constant.URL_MAP_IMAGE_PRE +
                                                    vo.getLocationActivity().getLat()
                                                    + "," +
                                                    vo.getLocationActivity().getLng()
                                                    // + "&markers=color:blue"
                                                    + Constant.URL_MAP_IMAGE_POST + context.getString(R.string.places_api_key),
                                            context, R.drawable.placeholder_3_2);
                                    holderParent.ivSingleImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.FEED_MAP, "", holderParent.getAdapterPosition()));
                                } else {
                                    holderParent.llSingleImage.setVisibility(View.GONE);
                                    holderParent.ivSingleImage.setVisibility(View.GONE);
                                    holderParent.ivStickerImage.setVisibility(View.GONE);
                                    holderParent.ivMarker.setVisibility(View.GONE);
                                    holderParent.ivGIF.setVisibility(View.GONE);
                                }
                            }

                            if (vo.getGif_url() != null && vo.getGif_id().equalsIgnoreCase("true")) {
                                holderParent.llVideoMain.setVisibility(View.GONE);
                                holderParent.llSingleImage.setVisibility(View.VISIBLE);
                                holderParent.ivStickerImage.setVisibility(View.GONE);
                                holderParent.ivSingleImage.setVisibility(View.GONE);
                                holderParent.ivGIF.setVisibility(View.VISIBLE);
                                holderParent.ivMarker.setVisibility(View.GONE);
                                Util.showAnimatedImageWithGlide(holderParent.ivGIF, vo.getGif_url(), context);
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;

        }
    }

    public SpannableString getClickableTags(List<Tags> tags, final int position) {
        StringBuilder artist = new StringBuilder();
        for (Tags art : tags) {
            artist.append(" ").append(art.getText());
        }

        SpannableString span = new SpannableString(artist.toString().trim());
        try {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                int start = s.length();
                s.append(" ").append(tags.get(i).getText());
                int end = s.length();
                //   final int index = i;
                //  final int catId = tags.get(i).getTagId();
                final String tag = tags.get(i).getText();
                span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_HASH_TAGGED, tag, position), start, end - 1, 0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return span;
    }

    private SpannableString fontColor(SpannableString raw, String text, int color) {
        //Spannable raw=new SpannableString(textView.getText());
        int index = TextUtils.indexOf(raw, text);
        while (index >= 0) {
            raw.setSpan(new ForegroundColorSpan(color), index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            raw.setSpan(new StyleSpan(BOLD), index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = TextUtils.indexOf(raw, text, index + text.length());
        }
        return raw;
    }

    public SpannableString getTextColorSpan(SpannableString spannableString) {
        //check if greetings color available if not simply return
        if (!AppConfiguration.isFeedGreetingsAvailable) return spannableString;
        try {
            for (TextColorString nv : textColorStrings) {
                spannableString = fontColor(spannableString, nv.getName(), Color.parseColor(nv.getColor()));
                break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return spannableString;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public void setLoadListener(OnLoadMoreListener loadListener) {
        this.loadListener = loadListener;
    }

    public void setComposer(ComposerOption composer) {
        this.composerOption = composer;
    }

    public void setComposerProfileImage(String composerProfileImage) {
        composerOption.getResult().setUser_image(composerProfileImage);
    }

    public void setStories(List<StoryModel> stories) {
        this.stories = stories;
    }

    static class AdHolder extends RecyclerView.ViewHolder {
        private final AdView mAdView;

        AdHolder(View itemView) {
            super(itemView);
            mAdView = itemView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    private ComposerOption composerOption;

    class ComposerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // private AppCompatTextView tvOption1;
        //private AppCompatTextView tvOption2;
        //private AppCompatTextView tvOption3;
        public TextView tvImage1;
        public TextView tvImage2;
        public TextView tvImage3;
        public TextView tvImage4;
        public TextView tvNoFeed;
        private ImageView ivProfileCompose;
        private ImageView ivVerify;
        private AppCompatTextView tvPostSomething;
        private LinearLayoutCompat llComposer;

        ComposerHolder(View v) {
            super(v);
            try {

//                if (AppConfiguration.memberImageShapeIsRound) {
                v.findViewById(R.id.ivProfile).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivProfile1).setVisibility(View.GONE);
                ivProfileCompose = v.findViewById(R.id.ivProfile);
                ivVerify = v.findViewById(R.id.iv_verify);

//                } else {
//                    v.findViewById(R.id.ivProfile).setVisibility(View.GONE);
//                    v.findViewById(R.id.ivProfile1).setVisibility(View.VISIBLE);
//                    ivProfileCompose = v.findViewById(R.id.ivProfile1);
//                }
                Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
                themeManager.applyTheme((ViewGroup) v, context);
                llComposer = v.findViewById(R.id.llComposer);
                llComposer.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                // tvOption1 = v.findViewById(R.id.tvOption1);
                //tvOption2 = v.findViewById(R.id.tvOption2);
                //tvOption3 = v.findViewById(R.id.tvOption3);
                tvImage1 = (AppCompatTextView) v.findViewById(R.id.tvImage1);
                tvImage2 = (AppCompatTextView) v.findViewById(R.id.tvImage2);
                tvImage3 = (AppCompatTextView) v.findViewById(R.id.tvImage3);
//                tvImage4 = (AppCompatTextView) v.findViewById(R.id.tvImage4);
                tvNoFeed = v.findViewById(R.id.tvNoData);

                tvImage1.setTypeface(iconFont);
                tvImage2.setTypeface(iconFont);
                tvImage3.setTypeface(iconFont);
//                tvImage4.setTypeface(iconFont);
                tvPostSomething = v.findViewById(R.id.tvPostSomething);
                tvPostSomething.setTextColor(Color.parseColor(Constant.text_color_2));
                //pb = v.findViewById(R.id.pb);

                v.findViewById(R.id.ivPostPhoto).setOnClickListener(this);
                v.findViewById(R.id.llOption1).setOnClickListener(this);
                v.findViewById(R.id.llOption2).setOnClickListener(this);
                v.findViewById(R.id.llOption3).setOnClickListener(this);
                ((ImageView) v.findViewById(R.id.ivPostPhoto)).setColorFilter(Color.parseColor(Constant.text_color_1));
                //  v.findViewById(R.id.llPostFeed).setVisibility(View.VISIBLE);
                if (composerOption.getResult().getEnableComposer()) {
                    llComposer.setVisibility(View.VISIBLE);
                    llComposer.setOnClickListener(this);
                    ivProfileCompose.setOnClickListener(this);

                    if (null != composerOption.getResult().getWelcomeHtml()) {
                        v.findViewById(R.id.rlWelcome).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.nvWelcome).setNestedScrollingEnabled(false);
                        ((NestedWebView) v.findViewById(R.id.nvWelcome)).loadData(composerOption.getResult().getWelcomeHtml(), null, null);
                        v.findViewById(R.id.ivCancel1).setOnClickListener(this);
                    } else {
                        v.findViewById(R.id.rlWelcome).setVisibility(View.GONE);
                    }

                    if (null != composerOption.getResult().getFriendHtml()) {
                        v.findViewById(R.id.rlFriend).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.nvFriend).setNestedScrollingEnabled(false);
                        ((NestedWebView) v.findViewById(R.id.nvFriend)).loadData(composerOption.getResult().getFriendHtml(), null, null);
                        ((NestedWebView) v.findViewById(R.id.nvFriend)).setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                listener.onItemClicked(Constant.Events.GREETING_OPTION, composerOption.getResult().getFriendHtml(), 0);
                                return true;
                            }
                        });
                        v.findViewById(R.id.ivCancel2).setOnClickListener(this);
                    } else {
                        v.findViewById(R.id.rlFriend).setVisibility(View.GONE);
                    }

                    if (null != composerOption.getResult().getDobHtml()) {
                        v.findViewById(R.id.rlDob).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.nvDob).setNestedScrollingEnabled(false);
                        ((NestedWebView) v.findViewById(R.id.nvDob)).loadData(composerOption.getResult().getDobHtml(), null, null);
                        ((NestedWebView) v.findViewById(R.id.nvDob)).setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                listener.onItemClicked(Constant.Events.WEBVIEW, url, -1);
                                return true;
                            }
                        });
                        v.findViewById(R.id.ivCancel3).setOnClickListener(this);
                    } else {
                        v.findViewById(R.id.rlDob).setVisibility(View.GONE);
                    }
                    if (null != composerOption.getResult().getUserBirthdayHtml()) {
                        v.findViewById(R.id.rlFriendBirthday).setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            v.findViewById(R.id.nvFriendBirthday).setNestedScrollingEnabled(false);
                        }
                        ((NestedWebView) v.findViewById(R.id.nvFriendBirthday)).loadData(composerOption.getResult().getUserBirthdayHtml(), null, null);
                        ((NestedWebView) v.findViewById(R.id.nvFriendBirthday)).setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                listener.onItemClicked(Constant.Events.WEBVIEW, url, -1);
                                return true;
                            }
                        });
                        v.findViewById(R.id.ivCancel4).setOnClickListener(this);
                    } else {
                        v.findViewById(R.id.rlFriendBirthday).setVisibility(View.GONE);
                    }

                    if (null != composerOption.getResult().getViewerBirthdayHtml()) {
                        v.findViewById(R.id.rlViewerBirthday).setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            v.findViewById(R.id.nvViewerBirthday).setNestedScrollingEnabled(false);
                        }
                        ((NestedWebView) v.findViewById(R.id.nvViewerBirthday)).loadData(composerOption.getResult().getViewerBirthdayHtml(), null, null);
                        ((NestedWebView) v.findViewById(R.id.nvViewerBirthday)).setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                listener.onItemClicked(Constant.Events.WEBVIEW, url, -1);
                                return true;
                            }
                        });
                        v.findViewById(R.id.ivCancel5).setOnClickListener(this);
                    } else {
                        v.findViewById(R.id.rlViewerBirthday).setVisibility(View.GONE);
                    }

                    final List<ComposerOptions> list1 = composerOption.getResult().getComposerOptions();

                    if (AppConfiguration.isLiveStreamingEnabled && (list1.get(0).getName().equals("elivestreaming"))) {
                        ((TextView) v.findViewById(R.id.tvOption1)).setText(list1.get(0).getValue());
                        v.findViewById(R.id.ivLiveIcon).setVisibility(View.VISIBLE);
                        tvImage1.setVisibility(View.GONE);
                    } else {
                        if (list1.size() > 0) {
                            ((TextView) v.findViewById(R.id.tvOption1)).setText(list1.get(0).getValue());
                            tvImage1.setText(Util.getCode(list1.get(0).getName(), false));
                            tvImage1.setTextColor(Color.parseColor(Util.getCode(list1.get(0).getName(), true)));
//                        tvImage4.setText(Util.getCode(list1.get(0).getName(), false));
//                        tvImage4.setTextColor(Color.RED);
                        }
                    }
                    if (list1.size() > 1) {
                        ((TextView) v.findViewById(R.id.tvOption2)).setText(list1.get(1).getValue());
                        tvImage2.setText(Util.getCode(list1.get(1).getName(), false));
                        tvImage2.setTextColor(Color.parseColor(Util.getCode(list1.get(1).getName(), true)));
                    }

                    if (list1.size() > 2) {
                        ((TextView) v.findViewById(R.id.tvOption3)).setText(list1.get(2).getValue());
                        tvImage3.setText(Util.getCode(list1.get(2).getName(), false));
                        tvImage3.setTextColor(Color.parseColor(Util.getCode(list1.get(2).getName(), true)));
                    }

                } else {
                    llComposer.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivCancel1:
                    itemView.findViewById(R.id.rlWelcome).setVisibility(View.GONE);
                    break;
                case R.id.ivCancel2:
                    itemView.findViewById(R.id.rlFriend).setVisibility(View.GONE);
                    break;
                case R.id.ivCancel3:
                    itemView.findViewById(R.id.rlDob).setVisibility(View.GONE);
                    break;
                case R.id.ivCancel4:
                    itemView.findViewById(R.id.rlFriendBirthday).setVisibility(View.GONE);
                    break;
                case R.id.ivCancel5:
                    itemView.findViewById(R.id.rlViewerBirthday).setVisibility(View.GONE);
                    break;
                case R.id.llComposer:
                    listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, null, -1);
                    break;
                case R.id.ivPostPhoto:
                    listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, null, 1);
                    break;
                case R.id.tvOption1:
                case R.id.llOption1:
                    if (AppConfiguration.isLiveStreamingEnabled && composerOption.getResult().getComposerOptions().get(0).getName().equals("elivestreaming"))
                        context.startActivity(new Intent(context, LiveVideoActivity.class));
                    else
                        listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, null, 0);
                    break;
                case R.id.tvOption2:
                case R.id.llOption2:
                    listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, null, 1);
                    // goToPostFeed(composerOption, 1);
                    break;
                case R.id.tvOption3:
                case R.id.llOption3:
                    listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, null, 2);
                    break;
//                case R.id.llOption4:
//                    context.startActivity(new Intent(context, LiveVideoActivity.class));
//                    break;
                case R.id.ivProfile:
                    listener.onItemClicked(Constant.Events.PROFILE, v, SPref.getInstance().getUserMasterDetail(context).getUserId());
                    // goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, SPref.getInstance().getUserMasterDetail(context).getUserId());
                    break;
               /* default:
                    listener.onItemClicked(Constant.Events.COMPOSER_OPTIONS, v, 0);
                    break;*/
            }
        }
    }

    class SeAdHolder extends RecyclerView.ViewHolder {
        public final NestedWebView webview;

        SeAdHolder(View itemView) {
            super(itemView);
            webview = itemView.findViewById(R.id.webview);
            webview.setNestedScrollingEnabled(false);
            /*webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setBuiltInZoomControls(false);
            webview.getSettings().setSupportZoom(false);
            webview.setNestedScrollingEnabled(false);*/
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    listener.onItemClicked(Constant.Events.WEBVIEW, url, -1);
                    return true;
                }
            });
        }
    }

    public class CommunityAd extends RecyclerView.ViewHolder {

        private final MultiSnapRecyclerView rvAd;
        private final ImageView ivAdImage, ivProfileImage;
        private final AppCompatButton bCallToAction;
        private final TextView tvHeader, tvSponsored;
        private final TextView tvCard, tvAdUrl, tvAdTitle;
        private final TextView tvHideTitle, tvAdDescription/*, tvHideDescription, tvUndo, tvReport*/;
        private final JzvdStd2 jzVideoPlayerStandard;
        private final View rlAdContent, cvMain;
        private final View ivOption;
        public final View llMainView, llHiddenView;


        private CommunityAdAdapter adapter;


        CommunityAd(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            ivAdImage = itemView.findViewById(R.id.ivAdImage);
            cvMain = itemView.findViewById(R.id.cvMain);
            ivOption = itemView.findViewById(R.id.ivOption);

            tvSponsored = itemView.findViewById(R.id.tvSponsored);

            rlAdContent = itemView.findViewById(R.id.rlAdContent);
            tvAdUrl = itemView.findViewById(R.id.tvAdUrl);
            tvAdTitle = itemView.findViewById(R.id.tvAdTitle);
            tvAdDescription = itemView.findViewById(R.id.tvAdDescription);
            bCallToAction = itemView.findViewById(R.id.bCallToAction);

            bCallToAction.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getAttachment().getCalltoaction().getHref(), getAdapterPosition());
            });
            tvCard = itemView.findViewById(R.id.tvCard);

            rvAd = itemView.findViewById(R.id.rvAd);
            jzVideoPlayerStandard = itemView.findViewById(R.id.videoplayer);
            ivProfileImage = itemView.findViewById(AppConfiguration.memberImageShapeIsRound ? R.id.ivProfileImageRound : R.id.ivProfileImage);
            tvHeader = itemView.findViewById(R.id.tvHeader);

            tvHeader.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getUrl(), getAdapterPosition());
            });
            ivProfileImage.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getUrl(), getAdapterPosition());
            });

            rvAd.setNestedScrollingEnabled(false);

            //hidden views
            tvHideTitle = itemView.findViewById(R.id.tvHideTitle);
            //tvHideDescription = itemView.findViewById(R.id.tvHideDescription);
            // tvUndo = itemView.findViewById(R.id.tvUndo);
            // tvReport = itemView.findViewById(R.id.tvReport);

            llMainView = itemView.findViewById(R.id.llMainView);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);

            //tvUndo.setOnClickListener(v -> listener.onItemClicked(Constant.Events.UNDO, null, getAdapterPosition()));
            // tvReport.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPORT, null, getAdapterPosition()));
        }
    }

    public class FeedFilterHolder extends RecyclerView.ViewHolder {

        RecyclerView recycleViewFeedType;
        FeedOptionAdapter adapterFeed;

        FeedFilterHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            recycleViewFeedType = itemView.findViewById(R.id.rvFeedType);
            recycleViewFeedType.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

        }
    }

    public class PeopleHolder extends RecyclerView.ViewHolder {

        final MultiSnapRecyclerView rvChild;
        // --Commented out by Inspection (23-08-2018 20:55):final TextView tvCategory;
        final TextView tvMore;
        FeedSuggestionAdapter adapter;

        PeopleHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            //tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }

    public class StoryParentHolder extends RecyclerView.ViewHolder {

        private final RecyclerView rvChild;
        final TextView tvArchive;
        private StoryAdapter adapter;


        StoryParentHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            rvChild = itemView.findViewById(R.id.rvChild);
            tvArchive = itemView.findViewById(R.id.tvArchive);
            rvChild.setNestedScrollingEnabled(false);
            // tvMore = itemView.findViewById(R.id.tvMore);
        }
    }

    private class CommonHolder extends RecyclerView.ViewHolder {

        private TextView tvFeedTags;
        private TextView tvDate;
        private TextView tvFeedType;
        private TextView tvLike;
        private TextView tvHeader;
        private ImageView ivFeedPrivacy;
        private ImageView ivOption;
        private ImageView ivImageLike;
        private ImageView ivProfileImage;
        private ImageView ivProfileImageRound;
        // private CardView llMain;
        private LinearLayoutCompat llShare;
        private LinearLayoutCompat llComment;
        private LinearLayoutCompat llLike;
        LinearLayout llsocialid;
        private AppCompatImageView ivFbShare;
        private AppCompatImageView ivWhatsAppShare;
        private AppCompatImageView ivSaveFeed;
        private View llAttribution;
        private ImageView ivAttribution;
        private LinearLayoutCompat llMultipleImageMain;


        private TextView tvPlus;
        private View vPlusCount;
        private ImageView ivImage11;
        private SquareImageView ivImage21;
        private SquareImageView ivImage22;
        private ImageView ivImage31;
        private SquareImageView ivImage32;
        private SquareImageView ivImage33;
        private SquareImageView ivImage41;
        private SquareImageView ivImage42;
        private SquareImageView ivImage43;
        private SquareImageView ivImage44;
        private SquareImageView ivImage51;
        private SquareImageView ivImage52;
        private SquareImageView ivImage53;
        private SquareImageView ivImage54;
        private SquareImageView ivImage55;

        //    protected LinearLayoutCompat llLowerImage;
        private LinearLayoutCompat llMultipleImage2;
        private LinearLayoutCompat llMultipleImage3;
        private LinearLayoutCompat llMultipleImage4;
        private LinearLayoutCompat llMultipleImage5;
        //  protected FrameLayout reactView;
        // protected RelativeLayout rlLowerlast;
        //public PopupWindow popupWindow;


        //BUY SELL
        private TextView tvBuySellLocation;
        private TextView tvBuySellPrice;
        private TextView tvBuySellDescription;
        private TextView tvBuySellTitle;
        private TextView tvBuySellSoldOut;
        private TextView tvSoldBottomText;
        private LinearLayoutCompat llLocation;
        private AppCompatButton bSold;
        private AppCompatButton bBuy;
        private LinearLayoutCompat llBuySellmain;


        //BG IMAGE
        private TextView tvImageForegroundText;
        private ImageView ivBgImage;
        private LinearLayoutCompat llBgImage;


        //QUOTE TYPE
        private TextView tvQuoteTitle;
        private TextView tvQuoteDesc;
        private TextView tvQuoteCategory;
        //     private TextView tvQuoteTags;
        private View rlQuoteMedia;
        private View llQuoteMain;
        private ImageView ivQuoteMediaType;
        private ImageView ivQuoteImage;

        private View llPoll;
        private TextView tvPollTitle;
        private TextView tvPollDesc;
        private MultiSnapRecyclerView rvPoll;
        private PollOptionAdapter adapter;
        private View llPollTypeChange;
        private TextView tvPollResult;

        //BOOST POST
        private View llFeedDate;
        private Button bBoost;
        private TextView tvSponsored;

        //FILE TYPE
        private TextView tvFileType;
        private TextView tvFilePreview;
        private TextView tvFileName;
        private ImageView ivFileType;
        private LinearLayoutCompat llMainFile;

        //VIDEO HOLDER
        private TextView tvImageDescription;
        private TextView tvImageTitle;
        private ImageView ivImage;
        private View rlVideoImage;
        private TextView tvBodyText;
        private TextView tvSeeMore;
        private TextView tvUndo;
        private TextView tvReport;
        private View llSingleImage;
        private View ivMarker;
        private LinearLayoutCompat llVideoMain;
        private View llMainView;
        private ImageView ivVerify;

        private LinearLayoutCompat llHiddenView;
        private ImageView ivSingleImage;
        private ImageView ivStickerImage;
        private ImageView ivGIF;
        private ImageView ivVideoPlaceholder;
        //   protected ReactionView reactionView;
        private TextView tvLikeUpper;
        private TextView tvLikeCount;
        private View rlUpperLike;
        private ImageView ivLikeUpper1;
        private ImageView ivLikeUpper2;
        private ImageView ivLikeUpper3;
        private ImageView ivLikeUpper4;
        private ImageView ivLikeUpper5;
        private TextView tvCommentUpper;
        private View llReactionUpper;
        private View llLikeCommentShare;
        private View rlLowerlast;
        private View rlChangedProfile;
        private ImageView ivChangedProfile;
        private ImageView ivChangedCover;
        private View rlCommentView;

        //used for inline video playback
        private JzvdStd2 jzVideoPlayerStandard;

        //comment UI members
        private ImageView ivProfileImageComment, ivUserImageComment, tvImageComment222;
        private TextView tvHeaderComment, tvBodyComment;
        private EditText etFeedComment;
        private CardView cvEditText, cvComment;
        private View ivPostIcon, ivStickerIcon;
        private FrameLayout flMain;
        private ProgressBar pbProgressHorizontal, pbProgressCircular;
        private RelativeLayout rlFeedHeader;
        private LinearLayoutCompat llMainClick;
        //for live video into feed
        private LinearLayoutCompat llLiveVideo;
        private AppCompatImageView ivLiveVideoImage;
        private AGVideo agPlayer;


        CommonHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            try {

                bBoost = itemView.findViewById(R.id.bBoost);
                agPlayer = itemView.findViewById(R.id.ag_player);
                llFeedDate = itemView.findViewById(R.id.llFeedDate);
                llFeedDate = itemView.findViewById(R.id.llFeedDate);
                tvSponsored = itemView.findViewById(R.id.tvSponsored);
                bBoost.setOnClickListener(v -> {
                    listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getBoostPostUrl(), getAdapterPosition());
                });

                int buttoncolor = Color.parseColor(Constant.menuButtonActiveTitleColor);
                int buttoncolor2 = Color.parseColor(Constant.menuButtonBackgroundColor);
                bBoost.setBackgroundTintList(ColorStateList.valueOf(buttoncolor));
                bBoost.setTextColor(buttoncolor2);

                ivOption = itemView.findViewById(R.id.ivOption);
                tvFeedType = itemView.findViewById(R.id.tvFeedType);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivFeedPrivacy = itemView.findViewById(R.id.ivFeedPrivacy);

                llLikeCommentShare = AppConfiguration.theme == 1 ? itemView.findViewById(R.id.llLikeCommentShare) : itemView.findViewById(R.id.llLikeCommentShare2);
                //llMain = itemView.findViewById(R.id.font_awesome_container);
                llShare = itemView.findViewById(R.id.llShare);
                llsocialid = itemView.findViewById(R.id.llsocialid);
                llComment = itemView.findViewById(R.id.llComment);
                llLike = itemView.findViewById(R.id.llLike);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);

                //Add animation to a view inside recycler view
                BounceView.applyBounceEffectTo(llLike);
                BounceView.applyBounceEffectTo(llComment);
                BounceView.applyBounceEffectTo(llShare);
                BounceView.applyBounceEffectTo(ivFbShare);
                BounceView.applyBounceEffectTo(ivWhatsAppShare);
                BounceView.applyBounceEffectTo(ivSaveFeed);

                tvLike = itemView.findViewById(R.id.tvLike);
                ivImageLike = itemView.findViewById(R.id.ivImageLike);
                ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
                ivProfileImageRound = itemView.findViewById(R.id.ivProfileImageRound);
                tvHeader = itemView.findViewById(R.id.tvHeader);
                ivMarker = itemView.findViewById(R.id.ivMarker);
                tvFeedTags = itemView.findViewById(R.id.tvFeedTags);

                ivImage11 = itemView.findViewById(R.id.ivImage11);
                ivImage21 = itemView.findViewById(R.id.ivImage21);
                ivImage22 = itemView.findViewById(R.id.ivImage22);
                ivImage31 = itemView.findViewById(R.id.ivImage31);
                ivImage32 = itemView.findViewById(R.id.ivImage32);
                ivImage33 = itemView.findViewById(R.id.ivImage33);
                ivImage41 = itemView.findViewById(R.id.ivImage41);
                ivImage42 = itemView.findViewById(R.id.ivImage42);
                ivImage43 = itemView.findViewById(R.id.ivImage43);
                ivImage44 = itemView.findViewById(R.id.ivImage44);
                ivImage51 = itemView.findViewById(R.id.ivImage51);
                ivImage52 = itemView.findViewById(R.id.ivImage52);
                ivImage53 = itemView.findViewById(R.id.ivImage53);
                ivImage54 = itemView.findViewById(R.id.ivImage54);
                ivImage55 = itemView.findViewById(R.id.ivImage55);
                rlLowerlast = itemView.findViewById(R.id.rlLowerlast);
                tvPlus = itemView.findViewById(R.id.tvPlus);
                vPlusCount = itemView.findViewById(R.id.vPlusCount);
                ivAttribution = itemView.findViewById(R.id.ivAttribution);
                llAttribution = itemView.findViewById(R.id.llAttribution);


                //  llLowerImage = itemView.findViewById(R.id.llLowerImage);
                //   rlLowerlast = itemView.findViewById(R.id.rlLowerlast);
                llMultipleImageMain = itemView.findViewById(R.id.llMultipleImageMain);
                llMultipleImage2 = itemView.findViewById(R.id.llMultipleImage2);
                llMultipleImage3 = itemView.findViewById(R.id.llMultipleImage3);
                llMultipleImage4 = itemView.findViewById(R.id.llMultipleImage4);
                llMultipleImage5 = itemView.findViewById(R.id.llMultipleImage5);
                //reactView = itemView.findViewById(R.id.reactView);

                //BUY SELL
                tvBuySellDescription = itemView.findViewById(R.id.tvBuySellDescription);
                tvBuySellLocation = itemView.findViewById(R.id.tvBuySellLocation);
                tvBuySellPrice = itemView.findViewById(R.id.tvBuySellPrice);
                tvBuySellTitle = itemView.findViewById(R.id.tvBuySellTitle);
                tvBuySellSoldOut = itemView.findViewById(R.id.tvBuySellSoldOut);
                llLocation = itemView.findViewById(R.id.llLocation);
                tvSoldBottomText = itemView.findViewById(R.id.tvSoldBottomText);
                bSold = itemView.findViewById(R.id.bSold);
                bBuy = itemView.findViewById(R.id.bBuy);
                llBuySellmain = itemView.findViewById(R.id.llBuySellmain);


                //BG IMAGE
                tvImageForegroundText = itemView.findViewById(R.id.tvImageForegroundText);
                ivBgImage = itemView.findViewById(R.id.ivBgImage);
                llBgImage = itemView.findViewById(R.id.llBgImage);


                //FILE TYPE
                tvQuoteTitle = itemView.findViewById(R.id.tvQuoteTitle);
                tvQuoteDesc = itemView.findViewById(R.id.tvQuoteDesc);
                tvQuoteCategory = itemView.findViewById(R.id.tvQuoteCategory);
                //  tvQuoteTags = itemView.findViewById(R.id.tvQuoteTags);
                rlQuoteMedia = itemView.findViewById(R.id.rlQuoteMedia);
                llQuoteMain = itemView.findViewById(R.id.llQuoteMain);
                ivQuoteMediaType = itemView.findViewById(R.id.ivQuoteMediaType);
                ivQuoteImage = itemView.findViewById(R.id.ivQuoteImage);

                //FILE TYPE
                llMainFile = itemView.findViewById(R.id.llMainFile);
                ivFileType = itemView.findViewById(R.id.ivFileType);
                tvFileName = itemView.findViewById(R.id.tvFileName);
                tvFilePreview = itemView.findViewById(R.id.tvFilePreview);
                tvFileType = itemView.findViewById(R.id.tvFileType);


                //VIDEO HOLDER
                ivVideoPlaceholder = itemView.findViewById(R.id.ivVideoPlaceholder);
                tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
                tvImageDescription = itemView.findViewById(R.id.tvImageDescription);
                ivImage = itemView.findViewById(R.id.ivVideoImage);
                rlVideoImage = itemView.findViewById(R.id.rlVideoImage);
                ivSingleImage = itemView.findViewById(R.id.ivSingleImage);
                llSingleImage = itemView.findViewById(R.id.llSingleImage);
                ivGIF = itemView.findViewById(R.id.ivGIF);
                llVideoMain = itemView.findViewById(R.id.llVideoMain);
                tvBodyText = itemView.findViewById(R.id.tvBodyText);
                ivVerify = itemView.findViewById(R.id.iv_verify);
                tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
                llMainView = itemView.findViewById(R.id.llMainView);
                llHiddenView = itemView.findViewById(R.id.llHiddenView);
                tvUndo = itemView.findViewById(R.id.tvUndo);
                tvReport = itemView.findViewById(R.id.tvReport);
                tvLikeUpper = itemView.findViewById(R.id.tvLikeUpper);
                tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
                tvCommentUpper = itemView.findViewById(R.id.tvCommentUpper);
                llReactionUpper = itemView.findViewById(R.id.llReactionUpper);
                rlUpperLike = itemView.findViewById(R.id.rlUpperLike);
                ivLikeUpper1 = itemView.findViewById(R.id.ivLikeUpper1);
                ivLikeUpper2 = itemView.findViewById(R.id.ivLikeUpper2);
                ivLikeUpper3 = itemView.findViewById(R.id.ivLikeUpper3);
                ivLikeUpper4 = itemView.findViewById(R.id.ivLikeUpper4);
                ivLikeUpper5 = itemView.findViewById(R.id.ivLikeUpper5);
                rlChangedProfile = itemView.findViewById(R.id.rlChangedProfile);
                ivStickerImage = itemView.findViewById(R.id.ivStickerImage);
                ivChangedProfile = itemView.findViewById(R.id.ivChangedProfile);
                ivChangedCover = itemView.findViewById(R.id.ivChangedCover);

                jzVideoPlayerStandard = itemView.findViewById(R.id.videoplayer);

                llPoll = itemView.findViewById(R.id.llPoll);
                tvPollTitle = itemView.findViewById(R.id.tvPollTitle);
                tvPollDesc = itemView.findViewById(R.id.tvPollDesc);
                rvPoll = itemView.findViewById(R.id.rvPoll);
                llPollTypeChange = itemView.findViewById(R.id.llPollTypeChange);
                tvPollResult = itemView.findViewById(R.id.tvPollResult);

                rlCommentView = itemView.findViewById(R.id.rlCommentView);
                ivProfileImageComment = itemView.findViewById(R.id.ivProfileImageComment);
                tvHeaderComment = itemView.findViewById(R.id.tvHeaderComment);
                tvBodyComment = itemView.findViewById(R.id.tvBodyComment);
                tvImageComment222 = itemView.findViewById(R.id.tvImageComment222);
                ivUserImageComment = itemView.findViewById(R.id.ivUserImageComment);
                etFeedComment = itemView.findViewById(R.id.etFeedComment);
                cvEditText = itemView.findViewById(R.id.cvEditText);
                cvComment = itemView.findViewById(R.id.cvComment);
                ivPostIcon = itemView.findViewById(R.id.ivPostIcon);
                ivStickerIcon = itemView.findViewById(R.id.ivStickerIcon);

                // Live Video
                llLiveVideo = itemView.findViewById(R.id.llLiveVideo);
                ivLiveVideoImage = itemView.findViewById(R.id.ivLiveVideoImage);

                rlFeedHeader = itemView.findViewById(R.id.rlFeedHeader);
                pbProgressCircular = itemView.findViewById(R.id.pbProgressCircular);
                pbProgressHorizontal = itemView.findViewById(R.id.pbProgressHorizontal);
                flMain = itemView.findViewById(R.id.flMain);
                llMainClick = itemView.findViewById(R.id.llMainClick);

                //  reactionView = (ReactionView) itemView.findViewById(R.id.activity_main_reactView);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }



        /*private void applyBounceEffect(View view) {
            BounceView.addAnimTo(view)
                    .setPopOutAnimDuration(150)
                    .setScaleForPushInAnim(.95f, .85f)
                    .setScaleForPopOutAnim(1.05f, 1.15f);
        }*/
    }

    private String unecodeStr(String escapedString) {
        try {
            return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(escapedString));
        } catch (Exception e) {
            CustomLog.d("warnning", "emoji parsing error at " + escapedString);
        }
        return escapedString;
    }

/*    final Runnable input_finish_checker = () -> {
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
    };*/


}
