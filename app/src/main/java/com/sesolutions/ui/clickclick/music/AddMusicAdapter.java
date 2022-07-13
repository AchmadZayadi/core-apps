package com.sesolutions.ui.clickclick.music;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.animate.bounceview.BounceView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class AddMusicAdapter extends RecyclerView.Adapter<AddMusicAdapter.CategoryHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final String TXT_BY;
    private final String TXT_IN;
    private String type;

    int menuTitleActiveColor;

    @Override
    public void onViewAttachedToWindow(@NonNull CategoryHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public AddMusicAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        TXT_IN = context.getResources().getString(R.string.IN_);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);
        themeManager = new ThemeManager();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_music, parent, false);
        return new CategoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            if (vo.isContentFavourite()) {
                holder.ivBookmark.setBackgroundResource(R.drawable.bookmark_filled);
            }
            if (!vo.isContentFavourite()) {
                holder.ivBookmark.setBackgroundResource(R.drawable.bookmark);
            }
            if (vo.isPlaying()) {
                holder.ivPlay2.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.VISIBLE);
                holder.ivPause.setVisibility(View.GONE);
                holder.ivPlay.setVisibility(View.GONE);
                holder.ivPause2.setVisibility(View.VISIBLE);



               // holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, "", holder.getAdapterPosition()));
                holder.ivPause.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, "", holder.getAdapterPosition()));
                holder.ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, "", holder.getAdapterPosition()));
                holder.tvTitle.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, "", holder.getAdapterPosition()));
                holder.ivPause2.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, "", holder.getAdapterPosition()));
            }
            if (!vo.isPlaying()) {
                holder.ivPlay2.setVisibility(View.VISIBLE);
                holder.ivSelect.setVisibility(View.INVISIBLE);
                holder.ivPause.setVisibility(View.GONE);
                holder.ivPlay.setVisibility(View.GONE);
              //  holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + vo.getDuration(), holder.getAdapterPosition()));
                holder.tvTitle.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + vo.getDuration(), holder.getAdapterPosition()));
                holder.ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + vo.getDuration(), holder.getAdapterPosition()));
                holder.ivPlay2.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + vo.getDuration(), holder.getAdapterPosition()));
                holder.ivPause2.setVisibility(View.GONE);
            }

            GradientDrawable drawable = (GradientDrawable) holder.ivSelect.getBackground();
            drawable.setColor(menuTitleActiveColor);

            holder.tvTitle.setText(vo.getTitle());
              try {
                  if (vo.getDuration() < 10) {
                      holder.tvDate.setText("00:0" + vo.getDuration());
                  }
                  else if(vo.getDuration()>=10 && vo.getDuration()<60){
                      holder.tvDate.setText("00:" + vo.getDuration());
                  }
                  else if(vo.getDuration()>60){

                      int mins = vo.getDuration() / 60;
                      int remainder = vo.getDuration() - mins * 60;

                      if (mins < 10) {
                          if(remainder<10){
                              holder.tvDate.setText("0" + mins+":0"+remainder);
                          }else {
                              holder.tvDate.setText("0" + mins+":"+remainder);
                          }

                      } else {
                          if(remainder<10){
                              holder.tvDate.setText(mins+":0"+remainder);
                          }else {
                              holder.tvDate.setText(mins+":"+remainder);
                          }
                      }
                  }
              }catch (Exception ex){
                  ex.printStackTrace();
              }
            Util.showImageWithGlide(holder.ivImage, vo.getMainImageUrl(), context, R.drawable.placeholder_3_2);
            holder.ivSelect.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_MUSIC, vo, vo.getMusicid()));
            holder.ivBookmark.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVOURITE, "", holder.getAdapterPosition()));
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


    public static class CategoryHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvDate;
        protected ImageView ivImage;
        protected AppCompatTextView ivSelect;
        protected ImageView ivPlay;
        protected ImageView ivPause;
        protected ImageView ivPause2;
        protected ImageView ivPlay2;
        protected ImageView ivBookmark;
        protected CardView cvMain;


        public CategoryHolder(View itemView) {
            super(itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivSelect = itemView.findViewById(R.id.ivSelect);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivPause = itemView.findViewById(R.id.ivPause);
            ivPause2 = itemView.findViewById(R.id.ivPause2);
            ivPlay2 = itemView.findViewById(R.id.ivPlay2);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
            BounceView.applyBounceEffectTo(ivPlay);
            BounceView.applyBounceEffectTo(ivSelect);
        }
    }

}
