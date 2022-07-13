package com.sesolutions.ui.comment;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Video;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.io.File;
import java.util.List;

public class CommentAttachImageAdapter<T> extends RecyclerView.Adapter<CommentAttachImageAdapter.ContactHolder> {

    private final List<T> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;


    public CommentAttachImageAdapter(List<T> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_attach_image, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            if (list.get(position) instanceof String) {
                // holder.ivAttachImage.setImageDrawable(Drawable.createFromPath(list.get(position).toString()));
                Util.showImageWithGlide(holder.ivAttachImage, new File((String) list.get(position)), context);
                holder.ivMediaType.setVisibility(("" + list.get(position)).contains(".mp4") ? View.VISIBLE : View.GONE);
            } else {
                Util.showImageWithGlide(holder.ivAttachImage, ((Video) list.get(position)).getSrc(), context, R.drawable.placeholder_menu);
                holder.ivMediaType.setVisibility(View.VISIBLE);
            }

            // holder.ivAttachImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.ivAttachCancel.setOnClickListener(v -> listener.onItemClicked(Constant.Events.FEED_ATTACH_IMAGE_CANCEL, "", holder.getAdapterPosition()));
            holder.ivAttachImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_5, "", holder.getAdapterPosition()));

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

        protected ImageView ivAttachImage;
        protected ImageView ivAttachCancel, ivMediaType;
        // protected LinearLayoutCompat llMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                //     cvMain = itemView.findViewById(R.id.llMain);
                ivMediaType = itemView.findViewById(R.id.ivMediaType);
                ivAttachImage = itemView.findViewById(R.id.ivAttachImage);
                ivAttachCancel = itemView.findViewById(R.id.ivAttachCancel);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
