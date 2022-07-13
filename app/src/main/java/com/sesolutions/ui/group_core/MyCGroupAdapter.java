package com.sesolutions.ui.group_core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
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
import com.sesolutions.responses.Group;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class MyCGroupAdapter extends RecyclerView.Adapter<MyCGroupAdapter.Holder> {


    private final List<Group> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    //  private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final ThemeManager themeManager;
    private final int textColor2;
   /* private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable addDrawable;
    private final Drawable dFavSelected;
    private final Drawable dFav;*/


    @Override
    public void onViewAttachedToWindow(MyCGroupAdapter.Holder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MyCGroupAdapter(List<Group> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.textColor2 = Color.parseColor(Constant.text_color_2);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();

        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        // dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        // dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        // dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_album_basic, parent, false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Group vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.MEMBERS);
            holder.tvArtist.setText(vo.getMemberCount());
            holder.llArtist.setVisibility(View.VISIBLE);

            holder.tvSongDetail.setVisibility(View.GONE);
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.ivOption.setColorFilter(textColor2);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
            holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, holder.ivOption, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvSongDetail;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected ImageView ivOption;
        protected CardView cvMain;


        public Holder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
