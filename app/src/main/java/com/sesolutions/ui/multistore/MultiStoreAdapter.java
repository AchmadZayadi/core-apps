package com.sesolutions.ui.multistore;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class MultiStoreAdapter extends RecyclerView.Adapter<MultiStoreAdapter.ContactHolder> {

    private final List<Blog> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFav;
    private final Drawable dStarFilled;
    private final Drawable dStarUnFilled;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private int loggedInId;

    public void setLoggedInId(int loggedInId) {
        this.loggedInId = loggedInId;
    }

    @Override
    public void onViewAttachedToWindow(MultiStoreAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MultiStoreAdapter(List<Blog> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //this.foreground = Color.parseColor(Constant.text_color_2);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=null;
        if(SCREEN_TYPE==Constant.FormType.BROWSE_STOREWISHLIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multistore_wishlist, parent, false);
        }else  if(SCREEN_TYPE==Constant.FormType.BROWSE_MULTISTORE_MYLISTING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_listing, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multistore, parent, false);
        }
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {


        if(SCREEN_TYPE==Constant.FormType.BROWSE_MULTISTORE_MYLISTING){
            holder.tvStats.setTypeface(iconFont);
            holder.tvStats.setText(Constant.FontIcon.VIEWTAG+" 02  "+Constant.FontIcon.MESSAGE+" 08  "+Constant.FontIcon.LIKE+" 05  "+Constant.FontIcon.FAVRATE+" 12  "+Constant.FontIcon.STARMARK+" 21");
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, Constant.FormType.BROWSE_MULTISTORE_MYLISTING, holder.getAdapterPosition()));

        }else {
            holder.tvCategoryName.setTypeface(iconFont);
            holder.tvCategoryName.setText(Constant.FontIcon.VIEWTAG+" 02  "+Constant.FontIcon.MESSAGE+" 08  "+Constant.FontIcon.LIKE+" 05  "+Constant.FontIcon.FAVRATE+" 12  "+Constant.FontIcon.STARMARK+" 21");
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, Constant.FormType.BROWSE_STOREWISHLIST, holder.getAdapterPosition()));
        }

        /*try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Blog vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());


            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getOwnerTitle());
            holder.rlArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_MY_ALBUMS ? View.GONE : View.VISIBLE);
            holder.ivAdd.setVisibility(loggedInId == vo.getOwnerId() ? View.VISIBLE : View.GONE);
            holder.tvBody.setText(vo.getBody());
            holder.ivDate.setTypeface(iconFont);
            holder.ivDate.setText(Constant.FontIcon.CALENDAR);
            holder.tvDate.setText(Util.changeDateFormat(context, vo.getCreationDate()));

            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);

            holder.ivLike.setVisibility(vo.canLike() ? View.VISIBLE : View.GONE);
            holder.ivFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.GONE);
            holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
            if (SCREEN_TYPE == Constant.FormType.TYPE_MUSIC_ALBUM || SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL) {
                holder.llStar.setVisibility(View.VISIBLE);
                holder.ivStar1.setImageDrawable(vo.getIntRating() > 0 ? dStarFilled : dStarUnFilled);
                holder.ivStar2.setImageDrawable(vo.getIntRating() > 1 ? dStarFilled : dStarUnFilled);
                holder.ivStar3.setImageDrawable(vo.getIntRating() > 2 ? dStarFilled : dStarUnFilled);
                holder.ivStar4.setImageDrawable(vo.getIntRating() > 3 ? dStarFilled : dStarUnFilled);
                holder.ivStar5.setImageDrawable(vo.getIntRating() > 4 ? dStarFilled : dStarUnFilled);
            } else {
                holder.llStar.setVisibility(View.GONE);
            }

            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, holder.ivAdd, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View llReactionOption;
        public TextView tvSongTitle;
        protected TextView tvBody;
        protected TextView tvCategoryName;
        protected TextView tvStats;
        protected TextView ivArtist;
        protected TextView tvDate;
        protected TextView ivDate;
        public ImageView ivSongImage;
        protected ImageView ivFavorite;
        protected ImageView ivAdd;
        protected ImageView ivLike;
        protected ImageView ivStar1;
        protected ImageView ivStar2;
        protected ImageView ivStar3;
        protected ImageView ivStar4;
        protected ImageView ivStar5;
        protected CardView cvMain;
        protected LinearLayoutCompat llStar;
        protected View rlArtist;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                ivDate = itemView.findViewById(R.id.ivDate);
                tvBody = itemView.findViewById(R.id.tvBody);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivAdd = itemView.findViewById(R.id.ivAdd);

                tvStats = itemView.findViewById(R.id.tvStats);
                ivStar1 = itemView.findViewById(R.id.ivStar1);
                ivStar2 = itemView.findViewById(R.id.ivStar2);
                ivStar3 = itemView.findViewById(R.id.ivStar3);
                ivStar4 = itemView.findViewById(R.id.ivStar4);
                ivStar5 = itemView.findViewById(R.id.ivStar5);

                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                llStar = itemView.findViewById(R.id.llStar);
                rlArtist = itemView.findViewById(R.id.rlArtist);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
