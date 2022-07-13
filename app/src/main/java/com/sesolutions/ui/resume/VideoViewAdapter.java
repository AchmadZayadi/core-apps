package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ContactHolder> {

    private static final String TAG = "VideoViewAdapter";

    private final Context context;
    private final Typeface iconFont;
    private final List<Videos> list;
    //  private final int SCREEN_TYPE;
    private final ThemeManager themeManager;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;

   /* private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable addDrawable;
    private final Drawable dFavSelected;
    private final Drawable dFav;*/

    private boolean isOwner = false;

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public void onViewAttachedToWindow(ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public VideoViewAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        // dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        // dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        // dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            final Videos vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());

            holder.tvArtist.setTypeface(iconFont);
            holder.tvArtist.setText("\uf06e " + vo.getUserTitle());
            holder.tvArtist.setVisibility(TextUtils.isEmpty(vo.getUserTitle()) ? View.GONE : View.VISIBLE);
            holder.ivDelete.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            holder.tvSongDetail.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
            /* + "  \uf001 " + vo.getSongCount()*/;

            holder.tvSongDetail.setText(detail);
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "", holder.getAdapterPosition()));
            holder.ivDelete.setOnClickListener(v -> listener.onItemClicked(Constant.Events.DELETE_PLAYLIST, "", holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvSongDetail;
        protected TextView tvArtist;
        protected ImageView ivSongImage;
        protected ImageView ivDelete;


        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivDelete = itemView.findViewById(R.id.ivDelete);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
