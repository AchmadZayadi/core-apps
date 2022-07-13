package com.sesolutions.ui.albums;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.Util;

import java.util.List;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ContactHolder> {

    private final List<Albums> list;
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
    // private final boolean isUserLoggedIn;
    private final boolean isCorePlugin;
    //  private String TXT_PHOTO;
    // private String TXT_PHOTOS;
    @Override
    public void onViewAttachedToWindow(@NonNull AlbumAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public AlbumAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
        isCorePlugin = ModuleUtil.getInstance().isCoreAlbumEnabled(context);

    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album2, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvSongTitle.setVisibility(View.GONE);
            } else {
                holder.tvSongTitle.setVisibility(View.VISIBLE);
                holder.tvSongTitle.setText(vo.getTitle());//+ (vo.getPhotoCount() != 0 ? "(" + vo.getPhotoCount() + ")");
            }

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getUserTitle());

            holder.tvStats.setTypeface(iconFont);
            holder.gallerid.setText(""+vo.getPhotoCount());

            holder.tvStats.setText(vo.getStatsString(!isCorePlugin));
            holder.tvStats.setVisibility(View.GONE);
            holder.ivArtist.setVisibility(View.GONE);
            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
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

        public TextView tvSongTitle;
        public TextView tvDetail;
        public TextView ivDetail;
        public TextView tvArtist;
        public TextView ivArtist;
        public TextView tvStats;
        public TextView gallerid;
        public View llArtist;
        public ImageView ivSongImage;
        public RelativeLayout cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvDetail = itemView.findViewById(R.id.tvDetail);
                ivDetail = itemView.findViewById(R.id.ivDetail);
                tvStats = itemView.findViewById(R.id.tvStats);
                gallerid = itemView.findViewById(R.id.gallerid);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
