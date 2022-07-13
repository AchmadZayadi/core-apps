package com.sesolutions.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.bounceview.BounceView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.music.JZMediaExo2;
import com.sesolutions.ui.common.DefaultDataVo;
import com.sesolutions.ui.customviews.TextViewClickMovement;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomClickableSpan;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd2;
import cn.jzvd.JzvdStd2;

import static android.graphics.Typeface.BOLD;

public class VideoFeedAdapter extends FeedActivityAdapter {

    private final String blackTheme = "{\"result\":{\"theme_styling\":[{\"key\":\"fontSizeNormal\",\"value\":10},{\"key\":\"fontSizeMedium\",\"value\":12},{\"key\":\"fontSizeLarge\",\"value\":14},{\"key\":\"fontSizeVeryLarge\",\"value\":16},{\"key\":\"fontSizeNormal_ipad\",\"value\":12},{\"key\":\"fontSizeMedium_ipad\",\"value\":14},{\"key\":\"fontSizeLarge_ipad\",\"value\":16},{\"key\":\"fontSizeVeryLarge_ipad\",\"value\":18},{\"key\":\"navigationColor\",\"value\":\"#252627\"},{\"key\":\"navigationTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"appBackgroundColor\",\"value\":\"#070707\"},{\"key\":\"appforgroundcolor\",\"value\":\"#070707\"},{\"key\":\"tableViewSeparatorColor\",\"value\":\"#3A3C3D\"},{\"key\":\"appFontColor\",\"value\":\"#F5F5F5\"},{\"key\":\"activityFeedLinkColor\",\"value\":\"#FFFFFF\"},{\"key\":\"appSepratorColor\",\"value\":\"#3A3C3D\"},{\"key\":\"noDataLabelTextColor\",\"value\":\"#FFFFFF\"},{\"key\":\"navigationDisabledColor\",\"value\":\"#FFFFFF\"},{\"key\":\"navigationActiveColor\",\"value\":\"#FFFFFF\"},{\"key\":\"statsTextColor\",\"value\":\"#C5C5C5\"},{\"key\":\"titleLightColor\",\"value\":\"#C5C5C5\"},{\"key\":\"starColor\",\"value\":\"#FFAD08\"},{\"key\":\"placeholdercolor\",\"value\":\"#FFFFFF\"},{\"key\":\"menuGradientColor1\",\"value\":\"#EB3349\"},{\"key\":\"menuGradientColor2\",\"value\":\"#EB3349\"},{\"key\":\"menuGradientColor3\",\"value\":\"#F45C43\"},{\"key\":\"menuGradientColor4\",\"value\":\"#F45C43\"},{\"key\":\"menuGradientColor5\",\"value\":\"#F45C43\"},{\"key\":\"buttonBackgroundColor\",\"value\":\"#B63A6B\"},{\"key\":\"buttonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"buttonRadius\",\"value\":5},{\"key\":\"buttonBorderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarTextColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarPlaceHolderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarIconColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentProfilePageTabTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentProfilePageTabActiveColor\",\"value\":\"#B63A6B\"},{\"key\":\"contentProfilePageTabBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"menuButtonBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"menuButtonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"menuButtonActiveTitleColor\",\"value\":\"#B63A6B\"},{\"key\":\"contentScreenTitleBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"contentScreenTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentScreenActiveColor\",\"value\":\"#B63A6B\"},{\"key\":\"outsideNavigationTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsidePlaceHolderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideButtonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideButtonBackgroundColor\",\"value\":\"#B63A6B\"},{\"key\":\"fontSizeNormal\",\"value\":10},{\"key\":\"fontSizeMedium\",\"value\":12},{\"key\":\"fontSizeLarge\",\"value\":14},{\"key\":\"fontSizeVeryLarge\",\"value\":16},{\"key\":\"fontSizeNormal_ipad\",\"value\":12},{\"key\":\"fontSizeMedium_ipad\",\"value\":14},{\"key\":\"fontSizeLarge_ipad\",\"value\":16},{\"key\":\"fontSizeVeryLarge_ipad\",\"value\":18},{\"key\":\"navigationColor\",\"value\":\"#252627\"},{\"key\":\"navigationTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"appBackgroundColor\",\"value\":\"#070707\"},{\"key\":\"appforgroundcolor\",\"value\":\"#070707\"},{\"key\":\"tableViewSeparatorColor\",\"value\":\"#3A3C3D\"},{\"key\":\"appFontColor\",\"value\":\"#dddddd\"},{\"key\":\"activityFeedLinkColor\",\"value\":\"#FFFFFF\"},{\"key\":\"appSepratorColor\",\"value\":\"#3A3C3D\"},{\"key\":\"noDataLabelTextColor\",\"value\":\"#FFFFFF\"},{\"key\":\"navigationDisabledColor\",\"value\":\"#FFFFFF\"},{\"key\":\"navigationActiveColor\",\"value\":\"#FFFFFF\"},{\"key\":\"statsTextColor\",\"value\":\"#C5C5C5\"},{\"key\":\"titleLightColor\",\"value\":\"#cccccc\"},{\"key\":\"starColor\",\"value\":\"#FFAD08\"},{\"key\":\"placeholdercolor\",\"value\":\"#FFFFFF\"},{\"key\":\"menuGradientColor1\",\"value\":\"#EB3349\"},{\"key\":\"menuGradientColor2\",\"value\":\"#EB3349\"},{\"key\":\"menuGradientColor3\",\"value\":\"#F45C43\"},{\"key\":\"menuGradientColor4\",\"value\":\"#F45C43\"},{\"key\":\"menuGradientColor5\",\"value\":\"#F45C43\"},{\"key\":\"buttonBackgroundColor\",\"value\":\"#B63A6B\"},{\"key\":\"buttonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"buttonRadius\",\"value\":5},{\"key\":\"buttonBorderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarTextColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarPlaceHolderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"searchBarIconColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentProfilePageTabTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentProfilePageTabActiveColor\",\"value\":\"#B63A6B\"},{\"key\":\"contentProfilePageTabBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"menuButtonBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"menuButtonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"menuButtonActiveTitleColor\",\"value\":\"#B63A6B\"},{\"key\":\"contentScreenTitleBackgroundColor\",\"value\":\"#252627\"},{\"key\":\"contentScreenTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"contentScreenActiveColor\",\"value\":\"#B63A6B\"},{\"key\":\"outsideNavigationTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsidePlaceHolderColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideButtonTitleColor\",\"value\":\"#FFFFFF\"},{\"key\":\"outsideButtonBackgroundColor\",\"value\":\"#B63A6B\"}]}}";
    List<Activity> list=new ArrayList<>();
    Context cntxt;
    boolean isSaved = false;
    OnUserClickedListener<Integer, Object> listener;
    public VideoFeedAdapter(List<Activity> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        super(list, cntxt, listener);
        this.list=list;
        this.listener=listener;
        DefaultDataVo datVo = new Gson().fromJson(blackTheme, DefaultDataVo.class);
        themeManager = new ThemeManager(SPref.getInstance().createThemeColors(false, datVo.getResult().getThemeStyling()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (home && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case TYPE_AD:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_ad, parent, false);
                holder = new AdHolder(v);
                break;
            case TYPE_CUSTOM_AD:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_se_ad, parent, false);
                holder = new SeAdHolder(v);
                break;
            case TYPE_COMPOSER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_feed, parent, false);
                holder = new ComposerHolder(v);
                break;
            case TYPE_PEOPLE_SUGGESTION:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_suggestion, parent, false);
                holder = new PeopleHolder(v);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_feed, parent, false);
                holder = new VideoHolder(v);
                break;
        }
        return holder;
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

            case TYPE_PEOPLE_SUGGESTION:
                final PeopleHolder holder2 = (PeopleHolder) vHolder;
                //holder2.tvCategory.setText(skillVo.getName());
                holder2.tvMore.setVisibility(vo.isSeeAll() ? View.VISIBLE : View.GONE);
                holder2.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.SUGGESTION_MAIN, "", -1));
                /*set child item list*/

                if (holder2.adapter == null) {
                    if (vo.getPeoples() != null && vo.getPeoples().size() > 0) {
                        SPref.getInstance().savePeopleSuggestions(context, vo.getPeoples());
                        // Collections.reverse(skillList);
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
                    final VideoHolder holderParent = (VideoHolder) vHolder;
                    themeManager.applyDarkTheme((ViewGroup) vHolder.itemView, context);

                    //    CustomLog.e("type", " vo.getType(" + position + ") " + vo.getType());
                    if (vo.isHidden()) {
                        holderParent.llHiddenView.setVisibility(View.VISIBLE);
                        holderParent.llMainView.setVisibility(View.GONE);
                        holderParent.tvUndo.setTypeface(iconFont, BOLD);
                        holderParent.tvUndo.setOnClickListener(v -> listener.onItemClicked(Constant.Events.UNDO, "" + position, position));

                        if (vo.isReported()) {
                            holderParent.tvReport.setVisibility(View.VISIBLE);
                            holderParent.tvReport.setTypeface(iconFont, BOLD);
                            holderParent.tvReport.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPORT, "" + holderParent.getAdapterPosition(), position));
                        } else {
                            holderParent.tvReport.setVisibility(View.GONE);
                        }

                    } else {
                        holderParent.llHiddenView.setVisibility(View.GONE);
                        holderParent.llMainView.setVisibility(View.VISIBLE);

                        holderParent.tvCommentUpper.setText(vo.getCommentCount() > 0 ? (vo.getCommentCount() + (vo.getCommentCount() == 1 ? Constant._COMMENT : Constant._COMMENTS)) :
                                Constant.EMPTY);
                        holderParent.llReactionUpper.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, "", position));

                        holderParent.llReactionUpper.setVisibility(vo.getCommentCount() > 0
                                || null != vo.getReactionUserData() /*|| vo.isIs_like()*/ || null != vo.getPostAttribution() ?
                                View.VISIBLE : View.GONE
                        );

                        holderParent.llLikeCommentShare.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
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
                            holderParent.rlUpperLike.setVisibility(View.INVISIBLE);
                            holderParent.tvLikeUpper.setVisibility(View.INVISIBLE);
                        }
                        holderParent.tvFeedType.setTypeface(iconFont);
                        holderParent.tvFeedType.setText(Html.fromHtml(slashU + vo.getActivityIcon()).toString());
                        holderParent.tvDate.setText(Util.changeDateFormat(context, vo.getDate()));
                        holderParent.llShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
                        holderParent.llComment.setVisibility(vo.getCommentable() ? View.VISIBLE : View.GONE);
                        holderParent.llLike.setVisibility(vo.getCommentable() ? View.VISIBLE : View.GONE);

                        if (null != vo.getPostAttribution()) {
                            holderParent.llAttribution.setVisibility(View.VISIBLE);
                            showImageWithGlide(holderParent.ivAttribution, vo.getAttributionImage(), context, R.drawable.placeholder_square);
                            holderParent.llAttribution.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ATTRIBUTION, holderParent.llAttribution, holderParent.getAdapterPosition()));
                        } else {
                            holderParent.llAttribution.setVisibility(View.GONE);
                        }

                        holderParent.llComment.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, "", holderParent.getAdapterPosition()));
                        holderParent.llLike.setOnLongClickListener(v -> {
                            createPopUp(v, holderParent.getAdapterPosition());
                            return false;
                        });
                        holderParent.llLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.LIKED, vo.isIs_like() ? "-1" : "0", holderParent.getAdapterPosition()));
                        holderParent.ivProfileImage.setVisibility(hasToShowRoundImage ? View.INVISIBLE : View.VISIBLE);
                        holderParent.ivProfileImageRound.setVisibility(hasToShowRoundImage ? View.VISIBLE : View.INVISIBLE);
                        showImageWithGlide((hasToShowRoundImage ? holderParent.ivProfileImageRound : holderParent.ivProfileImage), vo.getItemUser().getUser_image(), context, R.drawable.placeholder_3_2);
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
                        holderParent.ivSaveFeed.setOnClickListener(v -> {
                            int savepos = 0;
                            for (int i = 0; i < vo.getOptions().size(); i++) {
                                if (vo.getOptions().get(i).getName().equals("save") || vo.getOptions().get(i).getName().equals("unsave")) {
                                    savepos = i;
                                    break;
                                }
                            }
                            listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION, "" + holderParent.getAdapterPosition(), savepos);

                            isSaved=!isSaved;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(isSaved){
                                        holderParent.ivSaveFeed.setImageDrawable(dSave);
                                    }else {
                                        holderParent.ivSaveFeed.setImageDrawable(dUnsave);
                                    }
                                }
                            }, 2000);

                        });


                        for (Options option : vo.getOptions()) {
                            if (option.getName().equals("save"))
                                isSaved = true;
                        }

                        holderParent.ivSaveFeed.setImageDrawable(isSaved ? dSave : dUnsave);


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
                                                    header = header.replace(type.getKey(), " " + type.getSeprator() + " " + type.getTitle());
                                                    start = /*headerLength +*/ header.lastIndexOf(type.getTitle()) - 5;
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
                                        try {
                                            if (type.getStartIndex() > -1) {
                                                spanArr[0].setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_ACTIVITY_TYPE, "" + type.getKey(), holderParent.getAdapterPosition()), type.getStartIndex(), type.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                spanArr[0].setSpan(new StyleSpan(BOLD), type.getStartIndex(), type.getEndIndex(), 0);
                                            }
                                        }catch (Exception ex){
                                            ex.printStackTrace();
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
                            final CustomClickableSpan feelingTitleSpan = new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_FEELING_TITLE, null, holderParent.getAdapterPosition());
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
                            final CustomClickableSpan locationSpan = new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_LOCATION, null, position);

                            spanArr[0].setSpan(locationSpan, startLocation, endLocation, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startLocation, endLocation, 0);
                        }

                        if (startTag1 > 0) {
                            final CustomClickableSpan tag1Span = new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_TAGGED_1, null, position);
                            spanArr[0].setSpan(tag1Span, startTag1, endTag1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startTag1, endTag1, 0);
                        }

                        if (startTag2 > 0) {
                            final CustomClickableSpan tag2Span = new CustomClickableSpan(listener, Constant.Events.CLICKED_HEADER_TAGGED_2, null, position);

                            spanArr[0].setSpan(tag2Span, startTag2, endTag2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanArr[0].setSpan(new StyleSpan(BOLD), startTag2, endTag2, 0);
                        }

                        holderParent.tvHeader.setText(spanArr[0]);
                        holderParent.tvHeader.setMovementMethod(LinkMovementMethod.getInstance());


                        Glide.with(context).load(vo.getPrivacyImageUrl()).into(holderParent.ivFeedPrivacy);
                        holderParent.ivOption.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
                        holderParent.ivOption.setOnClickListener(v -> {
                            showOptionsPopUp(v, vo.getOptions(), holderParent.getAdapterPosition());
                            //  listener.onItemClicked(Constant.Events.CLICKED_OPTION, "", holderParent.getAdapterPosition());
                        });

                        String body = null;
                        SpannableString span = null;
                        if (vo.getActivityType() != null) {
                            List<ActivityType> actTypelIst = vo.getActivityType();
                            if (actTypelIst.size() > 0) {
                                for (ActivityType type : actTypelIst) {
                                    if (type.getKey().equals(Constant.KEY_SPECIAL_BODY)) {
                                        body = unicodeStr(type.getValue());
                                        CustomLog.e("body", body);
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
                                                    // body = body.replace(men.getWord(), men.getTitle());
                                                    if (men.getStartIndex() > -1) {
                                                        span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_TAGGED, "" + men.getUserId(), position), men.getStartIndex(), men.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                                                        span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_HASH_TAGGED, "" + men, position), startMention, endMention, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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


                        if (!TextUtils.isEmpty(body)) {
                            holderParent.tvBodyText.setVisibility(View.VISIBLE);

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
                            holderParent.tvBodyText.setText(span);
                            holderParent.tvBodyText.setMovementMethod(LinkMovementMethod.getInstance());

                        } else {
                            holderParent.tvBodyText.setVisibility(View.GONE);
                        }

                        if (TextUtils.isEmpty(vo.getHashTagString())) {
                            holderParent.tvFeedTags.setVisibility(View.GONE);
                        } else {
                            holderParent.tvFeedTags.setVisibility(View.VISIBLE);
                            holderParent.tvFeedTags.setText(getClickableTags(vo.getActivityTags(), holderParent.getAdapterPosition()));
                            holderParent.tvFeedTags.setMovementMethod(LinkMovementMethod.getInstance());
                        }


                        //VIDEO HOLDER

                        holderParent.llVideoMain.setVisibility(View.GONE);
                        if (vo.getAttachment() != null) {


                            /*  hide video layout if no image ,title or description */
                            holderParent.llVideoMain.setVisibility(vo.getAttachment().getImages() == null && TextUtils.isEmpty(vo.getAttachment().getTitle()) && TextUtils.isEmpty(vo.getAttachment().getDescription()) ? View.GONE : View.VISIBLE);
                            if (null != vo.getAttachment().getImages() && vo.getAttachment().getImages().size() > 0) {
                                Glide.with(holderParent.jzVideoPlayerStandard.getContext()).load(vo.getAttachment().getImages().get(0).getMain()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                //Glide.with(context).load(vo.getAttachment().getImages().get(0).getMain()).apply(new RequestOptions()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                            } else {
                                Glide.with(holderParent.jzVideoPlayerStandard.getContext()).load("").into(holderParent.jzVideoPlayerStandard.thumbImageView);
                                //Glide.with(context).load("").apply(new RequestOptions()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                            }

                            // Glide.with(context).load(vo.getAttachment().getImages()).apply(new RequestOptions().override(50, 50)).into(holderParent.jzVideoPlayerStandard.thumbImageView);

                            holderParent.tvImageDescription.setText(vo.getAttachment().getDescription());
                            holderParent.tvImageDescription.setMovementMethod(new TextViewClickMovement(listener, context, holderParent.getAdapterPosition()));
                            holderParent.tvImageTitle.setText(vo.getAttachment().getTitle());
                            holderParent.tvImageTitle.setVisibility(TextUtils.isEmpty(vo.getAttachment().getTitle()) ? View.GONE : View.VISIBLE);
                            holderParent.tvImageDescription.setVisibility(TextUtils.isEmpty(vo.getAttachment().getDescription()) ? View.GONE : View.VISIBLE);


                            //   Glide.with(holderParent.jzVideoPlayerStandard.getContext()).load(vo.getAttachment().getImages().get(0).getMain()).into(holderParent.jzVideoPlayerStandard.thumbImageView);
                            holderParent.jzVideoPlayerStandard.releaseAllVideos();
                            holderParent.jzVideoPlayerStandard.setUp(vo.getAttachment().getVideoUrl()
                                    , vo.getTitle(), Jzvd2.SCREEN_NORMAL, JZMediaExo2.class);


                            if (canPlay) {
                                holderParent.jzVideoPlayerStandard.startVideo();
                            } else {
                                //do nothing
                            }



                            holderParent.jzVideoPlayerStandard.ivFollowId.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.ivLike.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.tvLikeCount.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.ivComment.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.tvCommentCount.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.ivShare.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.ivViews.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.tvViews.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.tvShare.setVisibility(View.INVISIBLE);
                            holderParent.jzVideoPlayerStandard.tvMusic.setVisibility(View.INVISIBLE);
                            //holderParent.llVideoMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIDEO, "", holderParent.getAdapterPosition()));

                            //always show play icon
                            //holderParent.ivVideoPlaceholder.setVisibility(View.VISIBLE);
                            //holderParent.ivVideoPlaceholder.setImageDrawable(dPlay);

                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
        }
    }


    public static class VideoHolder extends RecyclerView.ViewHolder {

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

        private LinearLayoutCompat llShare;
        private LinearLayoutCompat llComment;
        private LinearLayoutCompat llLike;
        private AppCompatImageView ivFbShare;
        private AppCompatImageView ivWhatsAppShare;
        private AppCompatImageView ivSaveFeed;

        //VIDEO HOLDER
        private TextView tvImageDescription;
        private TextView tvImageTitle;
        // private ImageView ivImage;
        // private View rlVideoImage;
        private TextView tvBodyText;
        private TextView tvSeeMore;
        private TextView tvUndo;
        private TextView tvReport;
        // private View llSingleImage;

        private LinearLayoutCompat llVideoMain;
        private View llMainView;
        private LinearLayoutCompat llHiddenView;

        //private ImageView ivVideoPlaceholder;
        private TextView tvLikeUpper;
        private View rlUpperLike;
        private ImageView ivLikeUpper1;
        private ImageView ivLikeUpper2;
        private ImageView ivLikeUpper3;
        private ImageView ivLikeUpper4;
        private ImageView ivLikeUpper5;
        private TextView tvCommentUpper;
        private View llReactionUpper;
        private View llLikeCommentShare;

        private View llAttribution;
        private ImageView ivAttribution;
        private JzvdStd2 jzVideoPlayerStandard;

        VideoHolder(View itemView) {
            super(itemView);
            try {

                ivOption = itemView.findViewById(R.id.ivOption);
                tvFeedType = itemView.findViewById(R.id.tvFeedType);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivFeedPrivacy = itemView.findViewById(R.id.ivFeedPrivacy);
                //llMain = itemView.findViewById(R.id.font_awesome_container);
                llShare = itemView.findViewById(R.id.llShare);
                llComment = itemView.findViewById(R.id.llComment);
                llLike = itemView.findViewById(R.id.llLike);
                tvLike = itemView.findViewById(R.id.tvLike);
                ivImageLike = itemView.findViewById(R.id.ivImageLike);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);
                BounceView.applyBounceEffectTo(ivFbShare);
                BounceView.applyBounceEffectTo(ivWhatsAppShare);
                BounceView.applyBounceEffectTo(ivSaveFeed);

                ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
                ivProfileImageRound = itemView.findViewById(R.id.ivProfileImageRound);
                tvHeader = itemView.findViewById(R.id.tvHeader);

                tvFeedTags = itemView.findViewById(R.id.tvFeedTags);

                //VIDEO HOLDER
                // ivVideoPlaceholder = itemView.findViewById(R.id.ivVideoPlaceholder);
                tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
                tvImageDescription = itemView.findViewById(R.id.tvImageDescription);
                // ivImage = itemView.findViewById(R.id.ivVideoImage);
                //rlVideoImage = itemView.findViewById(R.id.rlVideoImage);

                // llSingleImage = itemView.findViewById(R.id.llSingleImage);
                llVideoMain = itemView.findViewById(R.id.llVideoMain);
                tvBodyText = itemView.findViewById(R.id.tvBodyText);
                tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
                llMainView = itemView.findViewById(R.id.llMainView);
                llHiddenView = itemView.findViewById(R.id.llHiddenView);
                tvUndo = itemView.findViewById(R.id.tvUndo);
                tvReport = itemView.findViewById(R.id.tvReport);
                tvLikeUpper = itemView.findViewById(R.id.tvLikeUpper);
                tvCommentUpper = itemView.findViewById(R.id.tvCommentUpper);
                llReactionUpper = itemView.findViewById(R.id.llReactionUpper);
                rlUpperLike = itemView.findViewById(R.id.rlUpperLike);
                ivLikeUpper1 = itemView.findViewById(R.id.ivLikeUpper1);
                ivLikeUpper2 = itemView.findViewById(R.id.ivLikeUpper2);
                ivLikeUpper3 = itemView.findViewById(R.id.ivLikeUpper3);
                ivLikeUpper4 = itemView.findViewById(R.id.ivLikeUpper4);
                ivLikeUpper5 = itemView.findViewById(R.id.ivLikeUpper5);
                llLikeCommentShare = AppConfiguration.theme == 1 ? itemView.findViewById(R.id.llLikeCommentShare) : itemView.findViewById(R.id.llLikeCommentShare2);

                llAttribution = itemView.findViewById(R.id.llAttribution);
                ivAttribution = itemView.findViewById(R.id.ivAttribution);

                jzVideoPlayerStandard = itemView.findViewById(R.id.videoplayer);
//                jzVideoPlayerStandard.widthRatio = 16;
//                jzVideoPlayerStandard.heightRatio = 9;

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }


}
