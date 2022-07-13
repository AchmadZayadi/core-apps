package com.sesolutions.ui.page;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class PagePhotoAdapter extends RecyclerView.Adapter<PagePhotoAdapter.ContactHolder> {

    private final List<Albums> list;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Context context;
    private boolean isFund;

    public PagePhotoAdapter(List<Albums> list, Context context, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listener = listenr;
        this.context = context;
    }

    public PagePhotoAdapter(List<Albums> list, Context context, OnUserClickedListener<Integer, Object> listenr, boolean isFund) {
        this(list, context, listenr);
        this.isFund = isFund;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isFund ? R.layout.item_image_200 : R.layout.item_page_photo, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final Albums vo = list.get(position);
            Util.showImageWithGlide(holder.ivSongImage, vo.getPhotoUrl(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, holder.ivSongImage, holder.getAdapterPosition()));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected ImageView ivSongImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivSongImage = itemView.findViewById(R.id.ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
