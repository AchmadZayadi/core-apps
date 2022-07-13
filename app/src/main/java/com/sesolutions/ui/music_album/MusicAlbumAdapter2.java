package com.sesolutions.ui.music_album;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;

import io.gresse.hugo.vumeterlibrary.VuMeterView;


public class MusicAlbumAdapter2 extends RecyclerView.Adapter<MusicAlbumAdapter2.ContactHolder> {

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
    boolean[] arr;



    @Override
    public void onViewAttachedToWindow(@NonNull MusicAlbumAdapter2.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ( (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MusicAlbumAdapter2(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.like_);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_item, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            holder.tvSongTitle.setText(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS ? vo.getName() : vo.getTitle());
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);


            holder.fabPlayLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition());
                }
            });

           // holder.ivArtist.setTypeface(iconFont);
           // holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getUserTitle());

            holder.optionmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu

                    PopupMenu popup = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        popup = new PopupMenu(context, holder.optionmenu, Gravity.END, 0, R.style.MyPopupMenu);
                    }else{
                        popup = new PopupMenu(context, holder.optionmenu);
                    }

                    //inflating menu from xml resource

                    popup.inflate(R.menu.music_item_menu);



                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.likeitem:
                                    listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition());
                                    break;
                                case R.id.favitemid:
                                    listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition());
                                    break;
                                case R.id.addtoplaylistid:
                                    listener.onItemClicked(Constant.Events.MUSIC_ADD, "" + SCREEN_TYPE, holder.getAdapterPosition());
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

       //     holder.fabPlay.setVisibility(TextUtils.isEmpty(vo.getSongUrl()) ? View.INVISIBLE : View.VISIBLE);

            try {
                    if(list.get(position).isPlaying()){
                        holder.vumeter.setVisibility(View.VISIBLE);
                    }else {
                        holder.vumeter.setVisibility(View.GONE);
                    }

                   int songId = ((CommonActivity) context).getCurrentSongId();
                    if (songId == vo.getSongId()) {
                        holder.vumeter.setVisibility(View.VISIBLE);
                    }

            }catch (Exception ex){
                ex.printStackTrace();
            }

            holder.mainlayoutid.setOnClickListener(v -> {
                for (int k=0;k<list.size();k++){
                    if(k==position){
                        list.get(k).setPlaying(true);
                    }else {
                        list.get(k).setPlaying(false);
                    }
                }
                listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + SCREEN_TYPE, holder.getAdapterPosition());
                notifyDataSetChanged();
                }
            );

            holder.fabPlayLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            //holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);




            //    ((CommonActivity) activity).playSong(result.getSongs());
            //    ((CommonActivity) activity).songPicked(result.getSongs().get(0));


            // holder.llArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS ? View.GONE : View.VISIBLE);

           // holder.tvSongDetail.setTypeface(iconFont);
            /*String detail = Constant.EMPTY;

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

            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);

            holder.ivLike.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST ? View.GONE : View.VISIBLE);
            holder.ivAdd.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_ARTISTS || SCREEN_TYPE == Constant.FormType.TYPE_PLAYLIST ? View.GONE : View.VISIBLE);
            holder.fabPlay.setVisibility(TextUtils.isEmpty(vo.getSongUrl()) ? View.INVISIBLE : View.VISIBLE);



            holder.cvMain.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, SCREEN_TYPE);
            });

            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.fabPlay.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "" + SCREEN_TYPE, holder.getAdapterPosition()));*/

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
        protected ImageView fabPlay;
        ImageView optionmenu,fabPlayLike;
        VuMeterView vumeter;
        LinearLayout mainlayoutid;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                mainlayoutid = itemView.findViewById(R.id.mainlayoutid);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                optionmenu = itemView.findViewById(R.id.ivOption);
                fabPlayLike = itemView.findViewById(R.id.fabPlayLike);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                fabPlay = itemView.findViewById(R.id.fabPlay);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                vumeter = itemView.findViewById(R.id.vumeter);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
