package com.sesolutions.ui.albums_core;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.AlbumAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.Util;

import java.util.List;


public class C_AlbumAdapter extends RecyclerView.Adapter<C_AlbumAdapter.ContactHolder> {

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
    public void onViewAttachedToWindow(@NonNull C_AlbumAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public C_AlbumAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
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

            holder.tvStats.setText(vo.getStatsString(isCorePlugin));


            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);


            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

           // comment_count

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
        public View llArtist;
        public ImageView ivSongImage;
        public CardView cvMain;


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
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
