package com.sesolutions.ui.albums;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.album.StaggeredAlbums;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class AlbumPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;

    public void setNestedScroll(boolean nestedScroll) {
        isNestedScroll = nestedScroll;
    }

    private boolean isNestedScroll = true;

    public StaggeredAlbums getStaggeredAlbums() {
        return staggeredAlbums;
    }

    public void setStaggeredAlbums(StaggeredAlbums staggeredAlbums) {
        this.staggeredAlbums = staggeredAlbums;
    }

    private StaggeredAlbums staggeredAlbums;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (!isNestedScroll && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public AlbumPhotoAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
       /* this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();*/
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        /*if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_image_2, parent, false);
            return new StaggeredHolder(view);
        } else {*/
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_photo, parent, false);
        return new ContactHolder(view);
        //  }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderParent, int position) {

        try {
            //themeManager.applyTheme((ViewGroup) holder.itemView, context);
            /*if (holderParent instanceof StaggeredHolder) {

                final StaggeredHolder holder = (StaggeredHolder) holderParent;

                if (staggeredAlbums.getFirstAlbum() != null) {
                    Util.showImageWithGlide(holder.ivImage11, staggeredAlbums.getFirstAlbum().getMain(), context, R.drawable.placeholder_square);
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

                if (staggeredAlbums.getSecondAlbum() != null) {
                    Util.showImageWithGlide(holder.ivImage12, staggeredAlbums.getSecondAlbum().getMain(), context, R.drawable.placeholder_square);
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

                if (staggeredAlbums.getThirdAlbum() != null) {
                    Util.showImageWithGlide(holder.ivImage13, staggeredAlbums.getThirdAlbum().getMain(), context, R.drawable.placeholder_square);
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

                if (staggeredAlbums.getFourthAlbum() != null) {
                    Util.showImageWithGlide(holder.ivImage14, staggeredAlbums.getFourthAlbum().getMain(), context, R.drawable.placeholder_square);
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

            } else {*/
            final ContactHolder holder = (ContactHolder) holderParent;
            final Albums vo = list.get(position);
            Util.showImageWithGlide(holder.ivImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);

            holder.ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_5, "", holder.getAdapterPosition()));
            // }

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

        protected ImageView ivImage;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                ivImage = itemView.findViewById(R.id.ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

   /* public static class StaggeredHolder extends RecyclerView.ViewHolder {

        protected ImageView ivImage11;
        protected ImageView ivImage12;
        protected ImageView ivImage13;
        protected ImageView ivImage14;


        public StaggeredHolder(View itemView) {
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
    }*/
}
