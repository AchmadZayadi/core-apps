package com.sesolutions.ui.music_core;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class MyMusicCAdapter extends RecyclerView.Adapter<MyMusicCAdapter.ContactHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    //  private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final ThemeManager themeManager;
   /* private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable addDrawable;
    private final Drawable dFavSelected;
    private final Drawable dFav;*/


    @Override
    public void onViewAttachedToWindow(MyMusicCAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (/*(list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) &&*/ (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MyMusicCAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //   this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
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

            /*holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.VIEWS);
            holder.tvArtist.setText(vo.getUserTitle());
            holder.llArtist.setVisibility(TextUtils.isEmpty(vo.getUserTitle()) ? View.GONE : View.VISIBLE);
*/
            holder.tvSongDetail.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
                    + "  \uf001 " + vo.getSongCount();

            // detail = SCREEN_TYPE == Constant.TYPE_ARTISTS ? "\uf004 " + vo.getFavouriteCount() : detail;
            holder.tvSongDetail.setText(detail);
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.ivCreatepage.setTypeface(iconFont);
            //  holder.ivLike.setVisibility(SCREEN_TYPE == Constant.TYPE_ARTISTS || SCREEN_TYPE == Constant.TYPE_PLAYLIST ? View.GONE : View.VISIBLE);
            // holder.ivAdd.setVisibility(SCREEN_TYPE == Constant.TYPE_ARTISTS || SCREEN_TYPE == Constant.TYPE_PLAYLIST ? View.GONE : View.VISIBLE);
            // holder.fabPlay.setVisibility(TextUtils.isEmpty(vo.getSongUrl()) ? View.GONE : View.VISIBLE);

            //   holder.ivLike.setImageDrawable(vo.getIsContentLike() ? dLikeSelected : dLike);
            //  holder.ivFavorite.setImageDrawable(vo.getIsContentFavourite() ? dFavSelected : dFav);
            holder.ivCreatepage.setText(Constant.FontIcon.CALENDAR);
            holder.tvCreatepage.setText(Util.changeFormat(vo.getCreationDate()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "", holder.getAdapterPosition()));
            //holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, "", holder.getAdapterPosition()));
            holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus(), listener));

            holder.ivOption.setVisibility(View.VISIBLE);
            holder.editrlid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MUSIC_EDIT, holder.getAdapterPosition(), 0);
                }
            });

            holder.deleterlid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MUSIC_DELETE, holder.getAdapterPosition(), 0);
                }
            });

            holder.editrlid.setVisibility(View.GONE);
            holder.deleterlid.setVisibility(View.GONE);
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
        protected TextView ivArtist,tvCreatepage,ivCreatepage;
        protected View llArtist;
        protected ImageView ivSongImage;
        protected ImageView ivOption;
        protected CardView cvMain;
        RelativeLayout editrlid,deleterlid;

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
                tvCreatepage = itemView.findViewById(R.id.tvCreatepage);
                ivCreatepage = itemView.findViewById(R.id.ivCreatepage);
                ivOption = itemView.findViewById(R.id.ivOption);
                editrlid = itemView.findViewById(R.id.editrlid);
                deleterlid = itemView.findViewById(R.id.deleterlid);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
