package com.sesolutions.ui.clickclick.me;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class MeAdapter extends RecyclerView.Adapter<MeAdapter.ContactHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable addDrawable;
    private final Drawable dFavSelected;
    private final Drawable dFav;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;


    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MeAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_me_videos, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Videos vo = list.get(position);

            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.default_song_img);

            if(vo.getOwnerId()== SPref.getInstance().getLoggedInUserId(context)){
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivEdit.setVisibility(View.VISIBLE);
            }else {
                holder.ivDelete.setVisibility(View.GONE);
                holder.ivEdit.setVisibility(View.GONE);
            }
            holder.tvLike.setText("" + vo.getLikeCount());

            if (vo.isContentLike()) {
                holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.red));
            }else {
                 holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }

            holder.cvMain.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition());
            });
            holder.ivDelete.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.DELETE, holder, holder.getAdapterPosition());
            });
            holder.ivEdit.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.CONTENT_EDIT, holder, holder.getAdapterPosition());
            });

            holder.ivLike.setOnClickListener(v -> {
                //send screen_TYPE in position argument
           //     listener.onItemClicked(Constant.Events.CONTENT_EDIT, holder, holder.getAdapterPosition());

                holder.sbvLike.likeAnimation();

              int likeCountUn;
                if (vo.isContentLike()) {
                    likeCountUn = vo.getLikeCount();
                    if (vo.firstTime || vo.unlikeLoop) {
                        vo.fromUnlike = true;
                        vo.firstTime = false;
                        vo.unlikeLoop = true;
                        likeCountUn = vo.getLikeCount() - 1;
                    }
                    vo.setContentLike(false);
                    holder.tvLike.setText("" + likeCountUn);
                    holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.white));

                } else {
                    likeCountUn = vo.getLikeCount();
                    if (vo.fromUnlike) {
                        likeCountUn = vo.getLikeCount();
                    } else {
                        likeCountUn = vo.getLikeCount() + 1;
                    }
                    vo.firstTime = false;
                    vo.fromUnlike = false;
                    vo.setContentLike(true);
                    holder.tvLike.setText("" + likeCountUn);
                    holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.red));
                }
                listener.onItemClicked(Constant.Events.TICK_VIDEO_LIKE, "" + vo.getVideoId(), holder.getAdapterPosition());
            });
//            holder..setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, "", vo.getOwnerId()));


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View llReactionOption;
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
        protected AppCompatTextView tvLike;
        protected AppCompatImageView ivEdit;
        protected AppCompatImageView ivDelete;
        protected ImageView fabPlay;
        protected SmallBangView sbvLike;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvLike = itemView.findViewById(R.id.tvLike);
                ivLike = itemView.findViewById(R.id.ivLike);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                ivEdit = itemView.findViewById(R.id.ivEdit);
                ivDelete = itemView.findViewById(R.id.ivDelete);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
