package com.sesolutions.ui.albums_core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.StaggeredAlbums;
import com.sesolutions.ui.albums.ViewAlbumAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;
import java.util.Random;


public class C_ViewAlbumAdapter extends RecyclerView.Adapter<C_ViewAlbumAdapter.ContactHolder> {

    private final List<StaggeredAlbums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    //   private final Typeface iconFont;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    //   private final ThemeManager themeManager;

    private String TXT_PHOTO;
    private String TXT_PHOTOS;


    @Override
    public void onViewAttachedToWindow(C_ViewAlbumAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public C_ViewAlbumAdapter(List<StaggeredAlbums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        //   iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

       /* addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);*/
        //  themeManager = new ThemeManager();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
        // return super.getItemViewType(position);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType % 4) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_image_1, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_image_2, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(new Random().nextBoolean() ? R.layout.layout_staggered_image_5 : R.layout.layout_staggered_image_3, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_image_4, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_image_1, parent, false);
                break;
        }
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            // themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StaggeredAlbums vo = list.get(position);
            if (vo.getFirstAlbum() != null) {
                Util.showImageWithGlide(holder.ivImage11, vo.getFirstAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
                holder.ivImage11.setVisibility(View.VISIBLE);
                holder.ivImage11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.IMAGE_1, "1", holder.getAdapterPosition());
                    }
                });
            } else {
                holder.ivImage11.setVisibility(View.GONE);
            }

            if (vo.getSecondAlbum() != null) {
                Util.showImageWithGlide(holder.ivImage12, vo.getSecondAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
                holder.ivImage12.setVisibility(View.VISIBLE);
                holder.ivImage12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.IMAGE_2, "2", holder.getAdapterPosition());
                    }
                });
            } else {
                holder.ivImage12.setVisibility(View.GONE);
            }

            if (vo.getThirdAlbum() != null) {
                Util.showImageWithGlide(holder.ivImage13, vo.getThirdAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
                holder.ivImage13.setVisibility(View.VISIBLE);
                holder.ivImage13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.IMAGE_3, "3", holder.getAdapterPosition());
                    }
                });
            } else {
                holder.ivImage13.setVisibility(View.GONE);
            }

            if (vo.getFourthAlbum() != null) {
                Util.showImageWithGlide(holder.ivImage14, vo.getFourthAlbum().getImages().getMain(), context, R.drawable.placeholder_square);
                holder.ivImage14.setVisibility(View.VISIBLE);
                holder.ivImage14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.IMAGE_4, "4", holder.getAdapterPosition());
                    }
                });
            } else {
                holder.ivImage14.setVisibility(View.GONE);
            }


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


        protected ImageView ivImage11;
        protected ImageView ivImage12;
        protected ImageView ivImage13;
        protected ImageView ivImage14;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                ivImage11 = itemView.findViewById(R.id.ivImage11);
                ivImage12 = itemView.findViewById(R.id.ivImage12);
                ivImage13 = itemView.findViewById(R.id.ivImage13);
                ivImage14 = itemView.findViewById(R.id.ivImage14);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
