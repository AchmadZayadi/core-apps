package com.sesolutions.ui.photo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ChannelPhoto;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ContactHolder> {

    private final List<ChannelPhoto> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final boolean isUserLoggedIn;


    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    @Override
    public void onViewAttachedToWindow(@NonNull PhotoAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && /*(list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && */(list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public PhotoAdapter(List<ChannelPhoto> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {

            final ChannelPhoto vo = list.get(position);
            if (SCREEN_TYPE == Constant.FormType.TYPE_PHOTO) {
                holder.tvSongDetail.setVisibility(View.GONE);
                holder.ivLike.setVisibility(View.GONE);
                holder.ivSongImage.setImageDrawable(Drawable.createFromPath(vo.getTitle()));
                holder.ivSongImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
               /* BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 6;
                Bitmap bitmapFile = BitmapFactory.decodeFile(vo.getTitle(), options);
                holder.ivSongImage.setImageBitmap(bitmapFile);
                holder.ivSongImage.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
            } else {
                holder.tvSongDetail.setTypeface(iconFont);
                String detail = Constant.EMPTY;
                detail += "\uf164 " + vo.getLikeCount()
                        + "  \uf075 " + vo.getCommentCount()
                        + "  \uf004 " + vo.getFavouriteCount()
                        + "  \uf06e " + vo.getViewCount()
                ;

                holder.tvSongDetail.setText(detail);
                Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
                holder.ivLike.setVisibility(isUserLoggedIn ? View.VISIBLE : View.GONE);
                holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);

                holder.cvMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition());
                    }
                });

                holder.ivLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition());
                    }
                });

            }
            holder.tvSongDetail.setTextColor(Color.parseColor("#FFFFFF"));

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


        protected TextView tvSongDetail;

        protected ImageView ivSongImage;

        protected ImageView ivLike;

        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivLike = itemView.findViewById(R.id.ivLike);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
