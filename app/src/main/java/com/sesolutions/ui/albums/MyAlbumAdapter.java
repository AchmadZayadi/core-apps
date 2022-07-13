package com.sesolutions.ui.albums;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class MyAlbumAdapter extends RecyclerView.Adapter<MyAlbumAdapter.ContactHolder> {

    private final List<Albums> list;
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
    public void onViewAttachedToWindow(MyAlbumAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MyAlbumAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
        textColor2 = Color.parseColor(Constant.text_color_2);
        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        // dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        // dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        // dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_album, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());

            holder.ivArtist.setTypeface(iconFont);
            holder.ivCreatepage.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.FOLDER);
            holder.tvArtist.setText(vo.getCategoryTitle());
            holder.llArtist.setVisibility(TextUtils.isEmpty(vo.getCategoryTitle()) ? View.GONE : View.VISIBLE);

            holder.tvSongDetail.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
                    + "  \uf03e " + vo.getPhotoCount();

            // detail = SCREEN_TYPE == Constant.TYPE_ARTISTS ? "\uf004 " + vo.getFavouriteCount() : detail;
            holder.tvSongDetail.setText(detail);
            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            holder.ivOption.setColorFilter(textColor2);

            holder.ivCreatepage.setText(Constant.FontIcon.CALENDAR);
            holder.tvCreatepage.setText(Util.changeFormat(vo.getCreationDate()));


            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
            holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, holder.ivOption, holder.getAdapterPosition()));

            holder.rlleaner.setVisibility(View.GONE);

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
        protected TextView ivArtist,tvCreatepage;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected ImageView ivOption;
        protected CardView cvMain;
        LinearLayout rlleaner;
        TextView ivCreatepage;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvCreatepage = itemView.findViewById(R.id.tvCreatepage);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                rlleaner = itemView.findViewById(R.id.rlleaner);
                ivCreatepage = itemView.findViewById(R.id.ivCreatepage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
