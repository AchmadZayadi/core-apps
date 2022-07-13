package com.sesolutions.ui.courses.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class LectureViewAdapter extends RecyclerView.Adapter<LectureViewAdapter.ContactHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    private final ThemeManager themeManager;

    private boolean isOwner = false;
    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public void onViewAttachedToWindow(LectureViewAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture_view, parent, false);
        return new ContactHolder(view);
    }

    public LectureViewAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
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
                    + "  \uf06e " + vo.getViewCount();

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
