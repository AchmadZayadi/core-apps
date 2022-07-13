package com.sesolutions.ui.photo;

import android.content.Context;
import androidx.annotation.NonNull;
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
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class VIdeoUploadAdapter extends RecyclerView.Adapter<VIdeoUploadAdapter.ContactHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;


    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    @Override
    public void onViewAttachedToWindow(VIdeoUploadAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && (list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public VIdeoUploadAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, String> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {
        try {
            final Videos vo = list.get(position);
            holder.vView.setVisibility(vo.isSelected() ? View.VISIBLE : View.GONE);
            holder.ivLike.setVisibility(View.GONE);
            holder.tvSongDetail.setText(vo.getTitle());
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + vo.isSelected(), holder.getAdapterPosition()));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {


        protected TextView tvSongDetail;
        protected ImageView ivSongImage;
        protected ImageView ivLike;
        protected CardView cvMain;
        protected View vView;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivLike = itemView.findViewById(R.id.ivLike);
                vView = itemView.findViewById(R.id.vView);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
