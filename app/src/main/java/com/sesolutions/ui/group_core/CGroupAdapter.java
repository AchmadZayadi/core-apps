package com.sesolutions.ui.group_core;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class CGroupAdapter extends RecyclerView.Adapter<CGroupAdapter.Holder> {

    private final List<Group> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private String TXT_PHOTO;
    private String TXT_PHOTOS;


    @Override
    public void onViewAttachedToWindow(@NonNull CGroupAdapter.Holder holder) {
        super.onViewAttachedToWindow(holder);
        if (/*(list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 2)) && */(list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public CGroupAdapter(List<Group> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_PHOTO = " " + context.getResources().getString(R.string.photo);
        TXT_PHOTOS = " " + context.getResources().getString(R.string.TITLE_PHOTOS);
       /* addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);*/
        themeManager = new ThemeManager();

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Group vo = list.get(position);
            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvSongTitle.setVisibility(View.GONE);
            } else {
                holder.tvSongTitle.setVisibility(View.VISIBLE);
                holder.tvSongTitle.setText(vo.getTitle());
            }

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getCreatedBy());

            holder.ivDetail.setTypeface(iconFont);
            holder.ivDetail.setText(Constant.FontIcon.MEMBERS);


            holder.tvDetail.setText(vo.getMemberCount());
            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);


            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition());
                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvDetail;
        protected TextView ivDetail;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected CardView cvMain;


        public Holder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvDetail = itemView.findViewById(R.id.tvDetail);
                ivDetail = itemView.findViewById(R.id.ivDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
