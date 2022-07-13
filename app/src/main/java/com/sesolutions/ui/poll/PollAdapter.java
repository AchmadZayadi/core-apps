package com.sesolutions.ui.poll;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomClickableSpan;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ContactHolder> {

    private final List<Poll> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFav;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private String type;
    public  Drawable dSave;
    public  Drawable dUnsave;
    boolean isSaved=false;

    @Override
    public void onViewAttachedToWindow(@NonNull PollAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }

    public PollAdapter(List<Poll> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
        this.isSaved=false;
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {


            final Poll vo = list.get(position);
            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvTitle.setVisibility(View.GONE);
            } else {
                holder.tvTitle.setVisibility(View.VISIBLE);
                holder.tvTitle.setText(vo.getTitle());
            }

            if(vo.getDescription().length()>0){
                holder.tvDesc.setText(vo.getDescription());
                holder.tvDesc.setVisibility(View.GONE);
            }else {
                holder.tvDesc.setVisibility(View.GONE);
            }

            holder.tvStats.setTypeface(iconFont);
            String detail = "\uf06e " + vo.getViewCount()
                    + "   \uf075 " + vo.getCommentCount()
                    + "   \uf164 " + vo.getLikeCount()
                    + "   \uf004 " + vo.getFavouriteCount()
                    + "   \uf0a6 " + vo.getVoteCount();
            holder.tvStats.setText(detail);

         //   holder.tvOwner.setText(vo.getHeaderText(context));
           // holder.tvOwner.setText(vo.getHeaderText1(context));
           // holder.tvOwner2.setText(vo.getHeaderText2(context));

            holder.tvOwner2.setText(Html.fromHtml("in " + "<b><font color=\"#484744\">" + vo.getHeaderText2(context) + "</font></b>"));
            holder.tvOwner.setText(Html.fromHtml("by " + "<b><font color=\"#484744\">" + vo.getHeaderText1(context) + "</font></b>"));
           // holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
            //holder.llReactionOption.setVisibility(type.equals(MenuTab.Poll.TYPE_BROWSE) ? View.VISIBLE : View.GONE);
            Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

            holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
            holder.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);

            if (vo.isShowAnimation() == 1) {
                vo.setShowAnimation(0);
                holder.sbvLike.likeAnimation();
               // holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                holder.ivLike.setColorFilter(vo.isContentLike() ? R.color.grey_dark_4: R.color.colorPrimarywelcome);
            } else {
                //holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                holder.ivLike.setColorFilter(vo.isContentLike() ? R.color.grey_dark_4: R.color.colorPrimarywelcome);
            }

            if (vo.isShowAnimation() == 2) {
                vo.setShowAnimation(0);
                //  holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                holder.ivFavorite.setColorFilter(vo.isContentLike() ? R.color.grey_dark_4: R.color.colorPrimarywelcome);
               //   holder.sbvFavorite.likeAnimation();
            } else {
                //  holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                holder.ivFavorite.setColorFilter(vo.isContentLike() ? R.color.grey_dark_4: R.color.colorPrimarywelcome);
            }
             // holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions()));
            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

            holder.tvOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "" + 1, holder.getAdapterPosition());
                }
            });

            holder.tvOwner2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE3, "" + 1, holder.getAdapterPosition());
                }
            });


        /*    SpannableString ss = new SpannableString(vo.getHeaderText(context));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
               //     startActivity(new Intent(MyActivity.this, NextActivity.class));

                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan,2, vo.getHeaderText(context).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
           //  holder.ivFbShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
          //   holder.ivWhatsAppShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);

            holder.ivFbShare.setOnClickListener(v ->
                    listener.onItemClicked(Constant.Events.SHARE_FEED, "" + 1, holder.getAdapterPosition()));
            holder.ivWhatsAppShare.setOnClickListener(v ->
                    listener.onItemClicked(Constant.Events.SHARE_FEED, "" + 2, holder.getAdapterPosition()));

            isSaved = false;

            if (null != vo.getOptions()) {
                for (PollOption option : vo.getOptions()) {
                    if (option.getName().equals("save"))
                        isSaved = true;
                }
            }


           holder.ivSaveFeed.setImageDrawable(vo.getShortcut_save().isIs_saved() ?  dUnsave:dSave);
            holder.ivSaveFeed.setOnClickListener(v -> {

                if(type.equalsIgnoreCase(MenuTab.Page.TYPE_BROWSE_POLL)){
                    if(vo.getShortcut_save().isIs_saved()){
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                    }else {
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), 0);
                    }
                }else {
                    if(vo.getShortcut_save().isIs_saved()){
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION3, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                    }else {
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION3, "" + holder.getAdapterPosition(), 0);
                    }
                }

            });

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
            popup.showOnAnchor(v, vertPos, horizPos, false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setType(String type) {
        this.type = type;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvDesc;
        protected TextView tvStats;
        protected TextView tvOwner,tvOwner2;

        protected View ivOption;
        protected View rlMain;
        protected View llReactionOption;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected ImageView ivVerified;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected ImageView ivImage,ivSaveFeed,ivFbShare,ivWhatsAppShare;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvStats = itemView.findViewById(R.id.tvStats);
                tvOwner = itemView.findViewById(R.id.tvOwner);
                tvOwner2 = itemView.findViewById(R.id.tvOwner2);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                rlMain = itemView.findViewById(R.id.rlMain);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                llReactionOption.setVisibility(isUserLoggedIn ? View.GONE : View.GONE);

                ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, getAdapterPosition()));
                ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, null, getAdapterPosition()));
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
