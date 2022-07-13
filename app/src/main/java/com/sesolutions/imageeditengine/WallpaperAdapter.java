package com.sesolutions.imageeditengine;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.unsplash.SesWallpaper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SesWallpaper> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private boolean isGrid = false;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }

    public WallpaperAdapter(List<SesWallpaper> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType != 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_more, parent, false);
            return new MoreHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_wallaper_big : R.layout.item_wallpaper_horizontal, parent, false);
            return new ContactHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        try {
            final SesWallpaper vo = list.get(position);
            if (holder.getItemViewType() != 0) {
                //DO nothing
                ((MoreHolder) holder).ivBg.setImageDrawable(ContextCompat.getDrawable(context, vo.getType() == ImageEditor.TYPE_WALLPAPER_GALLERY ? R.drawable.ses_camera_gallery : R.drawable.ses_more_gallery));
            } else {
                Util.showImageWithGlide(((ContactHolder) holder).ivBg, isGrid ? vo.getRegular() : vo.getSmall(), context, R.drawable.placeholder_3_2);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        CardView cvBg;
        ImageView ivBg;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                cvBg = itemView.findViewById(R.id.cvBg);
                // ivPlay = itemView.findViewById(R.id.ivPlay);
                ivBg = itemView.findViewById(R.id.ivBg);
                cvBg.setOnClickListener(v -> listener.onItemClicked(Constant.Events.BG_ATTACH, "", getAdapterPosition()));
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class MoreHolder extends RecyclerView.ViewHolder {
        View cvBg;
        ImageView ivBg;


        MoreHolder(View itemView) {
            super(itemView);
            try {
                cvBg = itemView.findViewById(R.id.cvBg);
                // ivPlay = itemView.findViewById(R.id.ivPlay);
                ivBg = itemView.findViewById(R.id.ivBg2);
                cvBg.setOnClickListener(v -> listener.onItemClicked(Constant.Events.BG_ATTACH, null, getAdapterPosition()));
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
