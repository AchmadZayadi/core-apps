package com.sesolutions.ui.music_album;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sesolutions.R;
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


public class MusicSongArtistAdapter extends RecyclerView.Adapter<MusicSongArtistAdapter.ContactHolder> {

    private final List<Albums> list;
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
    public void onViewAttachedToWindow(@NonNull MusicSongArtistAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ( (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MusicSongArtistAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_artist_item, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            holder.tvSongTitle.setText(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS ? vo.getName() : vo.getTitle());

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getUserTitle());
            holder.llArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS ? View.GONE : View.VISIBLE);

            holder.tvSongDetail.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += ((SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST) ? "" :
                    "\uf164 " + vo.getLikeCount()
                            + "  \uf075 " + vo.getCommentCount() + "  ")
                    + "\uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
                    + (SCREEN_TYPE == Constant.FormType.TYPE_MUSIC_ALBUM || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST
                    ? "  \uf001 " + vo.getSongCount()
                    : "  \uf04b " + vo.getPlayCount());

            detail = SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS ? "\uf004 " + vo.getFavouriteCount() : detail;
            holder.tvSongDetail.setText(detail);
            holder.tvSongDetail.setVisibility(View.VISIBLE);

            if(vo.getFavouriteCount()>1){
                holder.tvSongDetail.setText(vo.getFavouriteCount()+" Likes");
            }
            else if(vo.getFavouriteCount()==1){
                holder.tvSongDetail.setText(vo.getFavouriteCount()+" Like");
            }
            else {
                holder.tvSongDetail.setText("0+ Like");
            }


            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.GONE : View.GONE);

            holder.ivLike.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST ? View.VISIBLE : View.GONE);
            holder.ivAdd.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST ? View.GONE : View.VISIBLE);
            holder.fabPlay.setVisibility(TextUtils.isEmpty(vo.getSongUrl()) ? View.INVISIBLE : View.VISIBLE);

            holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);


            holder.cvMain.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, SCREEN_TYPE);
            });

            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v ->{
                if(vo.isContentFavourite()){
                    holder.tvSongDetail.setText((vo.getFavouriteCount()-1)+" Like");
                    vo.setFavouriteCount((vo.getFavouriteCount()-1));
                }else {
                    holder.tvSongDetail.setText((vo.getFavouriteCount()+1)+" Like");
                    vo.setFavouriteCount((vo.getFavouriteCount()+1));
                }
                listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition());
            });
            holder.fabPlay.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + SCREEN_TYPE, holder.getAdapterPosition()));

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
        protected RelativeLayout cvMain;
        protected ImageView fabPlay;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                fabPlay = itemView.findViewById(R.id.fabPlay);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
