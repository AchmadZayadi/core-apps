package com.sesolutions.ui.classified;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


public class ClassifiedAdapter extends RecyclerView.Adapter<ClassifiedAdapter.ContactHolder> {

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
    public void onViewAttachedToWindow(ClassifiedAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }


    public ClassifiedAdapter(List<Blog> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        themeManager = new ThemeManager();

    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //change item view in case of browse and my listing
        View view = LayoutInflater.from(parent.getContext()).inflate(SCREEN_TYPE == Constant.FormType.TYPE_MY_ALBUMS ? R.layout.item_classified : R.layout.item_classified_grid, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Blog vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getOwnerTitle());
            holder.llArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_MY_ALBUMS ? View.GONE : View.VISIBLE);
            //   holder.ivAdd.setVisibility(SCREEN_TYPE ==Constant.SCRE ? View.VISIBLE : View.GONE);
            holder.ivDate.setTypeface(iconFont);
            holder.ivDate.setText(Constant.FontIcon.CALENDAR);
            holder.tvDate.setText(Util.changeDateFormat(context,vo.getCreationDate()));

            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition()));


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        /*   protected View llReactionOption;
           protected TextView tvSongTitle;
           protected TextView tvBody;
           protected TextView tvArtist;
           protected TextView ivArtist;
           protected TextView tvDate;
           protected TextView ivDate;
           protected ImageView ivSongImage;
           protected CardView cvMain;
           protected View llArtist;
           protected View fabPlay;
   */
        protected TextView tvSongTitle;
        protected TextView tvSongDetail;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected ImageView ivFavorite;
        protected ImageView ivAdd;
        protected ImageView ivLike;
        protected CardView cvMain;
        protected TextView tvDate;
        protected TextView ivDate;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                ivDate = itemView.findViewById(R.id.ivDate);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                llArtist = itemView.findViewById(R.id.llArtist);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
