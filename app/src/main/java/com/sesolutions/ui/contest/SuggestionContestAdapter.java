package com.sesolutions.ui.contest;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class SuggestionContestAdapter extends RecyclerView.Adapter<SuggestionContestAdapter.ContactHolder> {

    private final List<ContestItem> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isRecent;
    private final Typeface iconFont;
    private final boolean isUserLoggedIn;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;

    private static String txt_day_left;
    private static String txt_days_left;
   /* private static String txt_seconds;
    private static String txt_minutes;
    private static String txt_hours;*/

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

   /* @Override
    public void onViewAttachedToWindow(ContestCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/

    public SuggestionContestAdapter(List<ContestItem> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isRecent) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isRecent = isRecent;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);

        txt_day_left = context.getResources().getString(R.string.txt_day_left);
        txt_days_left = context.getResources().getString(R.string.txt_days_left);
       /* txt_seconds = context.getResources().getString(R.string.txt_seconds);
        txt_minutes = context.getResources().getString(R.string.txt_minutes);
        txt_hours = context.getResources().getString(R.string.txt_hours);*/
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final ContestItem vo = list.get(position);


            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvType.setTypeface(iconFont);
            holder.tvType.setText(getIconByType(vo.getContestType()));
            holder.tvType.setVisibility(TextUtils.isEmpty(vo.getContestType()) ? View.GONE : View.VISIBLE);
            holder.ivDate.setTypeface(iconFont);
            holder.ivDate2.setTypeface(iconFont);

            //hiding Status and shadow
            holder.vShadow.setVisibility(View.GONE);
            holder.llStatus.setVisibility(View.INVISIBLE);

            holder.tvEntryCount.setText(vo.getEntries());
            if (vo.getTimeLeft() > 0) {
                holder.tvStatus.setText(getContestDateDiff(vo.getTimeLeft()));
            } else {
                holder.tvStatus.setText(vo.getStatus());
            }
            holder.tvVoteCount.setText(vo.getVotes());
            holder.tvVoteCount.setVisibility(null != vo.getVotes() ? View.VISIBLE : View.GONE);
            holder.ivDate.setText(Constant.FontIcon.CALENDAR);
            holder.ivDate2.setText(Constant.FontIcon.PLAY);

            //hide join button if key "join" is null
            holder.cvJoin.setVisibility(TextUtils.isEmpty(vo.getJoin()) ? View.GONE : View.VISIBLE);
            holder.tvJoin.setText(vo.getJoin());

            holder.tvStartTime.setText(vo.getCalanderStartTime());
            holder.tvEndTime.setText(vo.getCalanderEndTime());

            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(TXT_BY + vo.getOwnerTitle());
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

            holder.llReactionOption.setVisibility(View.INVISIBLE);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PAGE_SUGGESTION_MAIN, null, vo.getContestId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private static String getIconByType(String contestType) {
        try {
            switch ("" + contestType) {
                case "1":
                    return Constant.FontIcon.TEXT;
                case "2":
                    return Constant.FontIcon.ALBUM;
                case "3":
                    return Constant.FontIcon.VIDEO;
                case "4":
                    return Constant.FontIcon.MUSIC;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvType;
        protected TextView tvStartTime;
        protected TextView tvEndTime;
        protected TextView ivDate;
        protected TextView ivDate2;
        protected TextView tvEntryCount;
        protected TextView tvStatus;
        protected TextView tvVoteCount;
        protected TextView tvJoin;
        protected View llArtist;
        protected View rlDate;
        protected ImageView ivSongImage;
        protected CardView cvMain;
        protected View cvJoin;

        protected View llStatus;
        protected View vShadow;

        protected View llReactionOption;
        protected ImageView ivFollow;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivDate = itemView.findViewById(R.id.ivDate);
                ivDate2 = itemView.findViewById(R.id.ivDate2);
                tvStartTime = itemView.findViewById(R.id.tvStartTime);
                tvEndTime = itemView.findViewById(R.id.tvEndTime);
                rlDate = itemView.findViewById(R.id.llLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                tvType = itemView.findViewById(R.id.tvType);
                cvJoin = itemView.findViewById(R.id.cvJoin);
                tvJoin = itemView.findViewById(R.id.tvJoin);

                llStatus = itemView.findViewById(R.id.llStatus);
                tvEntryCount = itemView.findViewById(R.id.tvEntryCount);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
                vShadow = itemView.findViewById(R.id.vShadow);

                //Reaction views
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivFollow = itemView.findViewById(R.id.ivFollow);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                sbvFollow = itemView.findViewById(R.id.sbvFollow);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public String getContestDateDiff(long diff) {
        String result;
        try {
            @SuppressWarnings("NumericOverflow")
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays == 0) {
                result = (diff / (60 * 60 * 1000) % 24) + "h "
                        + (diff / (60 * 1000) % 60) + "m "
                        + (diff / (1000) % 60) + "s";

            } else if (diffDays == 1) {
                result = diffDays + "\n" + txt_day_left;
            } else {
                result = diffDays + "\n" + txt_days_left;
            }
        } catch (Exception e) {
            result = "";
            CustomLog.e(e);
        }
        return result;
    }

}
