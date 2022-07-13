package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ContactHolder> {

    private static final String TAG = "VideoAdapter";

    private final int cPrimary;
    private final String _VIEW;
    private final String _VIEWS;
    private final Drawable dFav;
    private final String TXT_BY;
    private final Drawable dLike;
    private final Context context;
    private final int SCREEN_TYPE;
    private final boolean isUserLoggedin;
    private final Drawable dFollow;
    private final Typeface iconFont;
    private final List<Videos> list;
    private boolean isEvent = false;
    private final boolean isCorePlugin;
    private final Drawable dStarFilled;
    private final Drawable dFavSelected;
    private final Drawable dLikeSelected;
    private final Drawable dStarUnFilled;
    private final Drawable dFollowSelected;
    private final ThemeManager themeManager;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    public final Drawable dSave;
    public final Drawable dUnsave;

    /*method used to hide addTo icon for event video screen*/
    public void setEvent(boolean canShow) {
        isEvent = canShow;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public VideoAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        _VIEW = context.getResources().getString(R.string._VIEW);
        _VIEWS = context.getResources().getString(R.string._VIEWS);
        isUserLoggedin = SPref.getInstance().isLoggedIn(context);
        cPrimary = Color.parseColor(Constant.colorPrimary);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        isCorePlugin = ModuleUtil.getInstance().isCoreVideoEnabled(context);

        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (SCREEN_TYPE) {
            case Constant.FormType.TYPE_MUSIC_ALBUM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                break;
           /* case Constant.FormType.CREATE_EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                break;*/
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_channel, parent, false);
                break;

        }
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {
        try {

            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Videos vo = list.get(position);
            //  holder.tvSongTitle.setText((SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS) ? vo.getName() : vo.getTitle());
            holder.tvSongTitle.setText(vo.getNameOrTitle());
            holder.tvSongTitle.setVisibility(null != vo.getNameOrTitle() ? View.VISIBLE : View.GONE);

            holder.ivArtist.setTypeface(iconFont);
            holder.ivLocation.setTypeface(iconFont);
            holder.tvLocation.setText(Constant.FontIcon.MAP_MARKER);
            holder.tvLocation.setText(vo.getLocation());
            holder.llArtist.setVisibility(vo.hasLocation() ? View.VISIBLE : View.GONE);
            holder.ivArtist.setText(isEvent ? Constant.FontIcon.FOLDER : Constant.FontIcon.USER);

            if(vo.getCategoryTitle()!=null && vo.getCategoryTitle().length()>0){
                holder.tvArtist.setText(isEvent ? vo.getEventTitle() : vo.getCategoryTitle());
            }else {
                holder.tvArtist.setText(isEvent ? vo.getEventTitle() : vo.getUserTitle());
            }

            holder.llArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS /*|| SCREEN_TYPE == Constant.FormType.CREATE_EVENT*/ ? View.GONE : View.VISIBLE);

            holder.llReactionOption.setVisibility(isUserLoggedin /*&& SCREEN_TYPE != Constant.FormType.CREATE_EVENT*/ ? View.VISIBLE : View.INVISIBLE);
            if (SCREEN_TYPE != Constant.FormType.TYPE_MUSIC_ALBUM) {
                //showing duration for Event Videos
                holder.tvDuration.setVisibility(/*SCREEN_TYPE == Constant.FormType.CREATE_EVENT ? View.VISIBLE :*/
                        View.GONE);
                holder.ivWatchLater.setVisibility(View.GONE);

                if (vo.getIsFollowActive() == 1) {
                    holder.ivAdd.setVisibility(View.VISIBLE);
                    holder.ivAdd.setImageDrawable(vo.getIsFollow() == 1 ? dFollowSelected : dFollow);
                } else {
                    holder.ivAdd.setVisibility(canShowAddTo() ? View.GONE : View.VISIBLE);
                }


            }else if(SCREEN_TYPE== Constant.FormType.TYPE_ARTISTS){
                holder.tvDuration.setVisibility(/*SCREEN_TYPE == Constant.FormType.CREATE_EVENT ? View.VISIBLE :*/
                        View.GONE);
                holder.ivWatchLater.setVisibility(View.GONE);

                if (vo.getIsFollowActive() == 1) {
                    holder.ivAdd.setVisibility(View.VISIBLE);
                    holder.ivAdd.setImageDrawable(vo.getIsFollow() == 1 ? dFollowSelected : dFollow);
                } else {
                    holder.ivAdd.setVisibility(canShowAddTo() ? View.GONE : View.VISIBLE);
                }
                holder.llStar.setVisibility(View.GONE);

            } else{
                holder.llArtist.setVisibility(View.VISIBLE);
                holder.llStar.setVisibility(View.GONE);
                holder.ivArtist.setVisibility(View.GONE);
                holder.llReactionOption.setVisibility(View.GONE);
                holder.ivWatchLater.setVisibility(View.GONE);
                holder.tvDuration.setVisibility(View.VISIBLE);
                // holder.ivWatchLater.setVisibility(isUserLoggedin ? View.VISIBLE : View.GONE);
                // holder.ivWatchLater.setColorFilter(vo.getWatchlaterId() == 0 ? Color.WHITE : cPrimary);
                holder.tvDuration.setText(vo.getDuration());
                //  holder.ivWatchLater.setOnClickListener(v -> listener.onItemClicked(Constant.Events.WATCH_LATER, null, holder.getAdapterPosition()));*/
                holder.tvSongDetail.setVisibility(View.GONE);


            }

            holder.tvSongDetail.setTypeface(iconFont);
            String detail = Constant.EMPTY;
            holder.tvSongDetail.setText(detail);
            detail +=/* "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()*/
                    vo.getStatsString(isCorePlugin)
                            + (SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL
                            ? "  \uf03e " + vo.getPhotos()
                            : "")
                            + (SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL
                            ? "  \uf0c0 " + vo.getFollowVideos()
                            : "");

            detail = (SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS) ? "\uf004 " + vo.getFavouriteCount() : detail;
            holder.tvSongDetail.setText(detail);


            //  }
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.filled_curve_cool);

            holder.ivLike.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS /*|| SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST*/ ? View.GONE : View.VISIBLE);
            //  holder.ivFavorite.setVisibility(permission.getCanalbumaddfavourite() == 1 ? View.VISIBLE : View.GONE);

            holder.ivLike.setVisibility(vo.canLike() ? View.VISIBLE : View.GONE);
            holder.ivFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.GONE);

            holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
            if (SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL /*|| SCREEN_TYPE == Constant.FormType.CREATE_EVENT*/) {
                holder.llStar.setVisibility(View.VISIBLE);
                holder.ivStar1.setImageDrawable(vo.getIntRating() > 0 ? dStarFilled : dStarUnFilled);
                holder.ivStar2.setImageDrawable(vo.getIntRating() > 1 ? dStarFilled : dStarUnFilled);
                holder.ivStar3.setImageDrawable(vo.getIntRating() > 2 ? dStarFilled : dStarUnFilled);
                holder.ivStar4.setImageDrawable(vo.getIntRating() > 3 ? dStarFilled : dStarUnFilled);
                holder.ivStar5.setImageDrawable(vo.getIntRating() > 4 ? dStarFilled : dStarUnFilled);
            }else  {
                //    holder.llStar.setVisibility(View.GONE);
            }

            holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
            holder.ivOption.setOnClickListener(v -> showOptionsPopUp(v, holder.getAdapterPosition(), vo.getOptions()));
            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition()));


            if (SCREEN_TYPE != Constant.FormType.TYPE_MUSIC_ALBUM) {
                holder.ivAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, "" + SCREEN_TYPE, holder.getAdapterPosition()));
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            }else
            {

                if(vo.getCategoryTitle()!=null && vo.getCategoryTitle().length()>0){
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : vo.getCategoryTitle());
                }else {
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : vo.getUserTitle());
                }



                holder.rlMiddle.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition()));
                holder.ivFbShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 1));
                holder.ivWhatsAppShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 2));
                holder.ivImageShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 3));

                holder.ivSaveFeed.setImageDrawable(vo.getShortcut_save().isIs_saved() ?  dUnsave:dSave);


                holder.ivSaveFeed.setOnClickListener(v -> {
                    if(vo.getShortcut_save().isIs_saved()){
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                    }else {
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), 0);
                    }
                });
            }
            holder.llvideocount.setVisibility(View.GONE);


            if(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS){
                holder.tvSongDetail.setVisibility(View.GONE);
                holder.llReactionOption.setVisibility(View.GONE);
                holder.llStar.setVisibility(View.GONE);
                holder.llReactionOption.setVisibility(View.GONE);
            }else if(SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL){
                holder.ivArtist.setVisibility(View.GONE);
                holder.llLocation.setVisibility(View.GONE);
                holder.tvSongDetail.setVisibility(View.GONE);
                holder.llReactionOption.setVisibility(View.GONE);


                if(vo.getCategoryTitle()!=null && vo.getCategoryTitle().length()>0){
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : "By "+vo.getCategoryTitle());
                }else {
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : "By "+vo.getUserTitle());
                }

            }else if(SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST){
                holder.ivArtist.setVisibility(View.GONE);
                holder.llLocation.setVisibility(View.GONE);
                if(vo.getVideosCount()!=0){
                    holder.llvideocount.setVisibility(View.VISIBLE);
                    holder.videocountid.setText(""+vo.getVideosCount());
                }else {
                    holder.llvideocount.setVisibility(View.GONE);
                }
                holder.llReactionOption.setVisibility(View.GONE);

                holder.llStar.setVisibility(View.GONE);
                holder.tvSongDetail.setVisibility(View.GONE);
                if(vo.getCategoryTitle()!=null && vo.getCategoryTitle().length()>0){
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : "By "+vo.getCategoryTitle());
                }else {
                    holder.tvArtist.setText(isEvent ? vo.getEventTitle() : "By "+vo.getUserTitle());
                }
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private boolean canShowAddTo() {
        return isEvent || SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST;
    }

    private String getViewText(int count) {
        return ", " + count + (count == 1 ? _VIEW : _VIEWS);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvSongDetail;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected View llLocation;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected ImageView ivFavorite;
        protected ImageView ivAdd;
        protected ImageView ivLike;
        protected ImageView ivStar1;
        protected ImageView ivStar2;
        protected ImageView ivStar3;
        protected ImageView ivStar4;
        protected ImageView ivStar5;
        protected ImageView ivWatchLater;
        protected TextView tvDuration,videocountid;
        protected RelativeLayout cvMain;
        CardView rlMiddle;
        protected View ivOption;
        protected View llReactionOption;
        protected LinearLayoutCompat llStar;
        protected ImageView ivImageShare,ivWhatsAppShare,ivFbShare,ivSaveFeed;
        LinearLayout llvideocount;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rlMiddle = itemView.findViewById(R.id.rlMiddle);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                llvideocount = itemView.findViewById(R.id.llvideocount);
                videocountid = itemView.findViewById(R.id.videocountid);

                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                ivWatchLater = itemView.findViewById(R.id.ivWatchLater);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivOption = itemView.findViewById(R.id.ivOption);

                ivStar1 = itemView.findViewById(R.id.ivStar1);
                ivStar2 = itemView.findViewById(R.id.ivStar2);
                ivStar3 = itemView.findViewById(R.id.ivStar3);
                ivStar4 = itemView.findViewById(R.id.ivStar4);
                ivStar5 = itemView.findViewById(R.id.ivStar5);
                llStar = itemView.findViewById(R.id.llStar);

                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                llLocation = itemView.findViewById(R.id.llLocation);

                ivImageShare = itemView.findViewById(R.id.ivImageShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);


            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
