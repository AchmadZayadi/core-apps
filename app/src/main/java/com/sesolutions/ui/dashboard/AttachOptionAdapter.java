package com.sesolutions.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.dashboard.composervo.ComposerOptions;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;

import java.util.List;


public class AttachOptionAdapter extends RecyclerView.Adapter<AttachOptionAdapter.ContactHolder> {

    private final List<ComposerOptions> list;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;


    public AttachOptionAdapter(List<ComposerOptions> list, Context context, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.listener = listener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attach_option_1, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            final ComposerOptions vo = list.get(position);
            //  FontManager.markAsIconContainer(holder.itemView, iconFont);
            holder.tvFeedText.setText(vo.getValue());
            if (vo.getName().equals("elivestreaming") && AppConfiguration.isLiveStreamingEnabled) {
                holder.tvFeedImage.setVisibility(View.GONE);
                holder.ivLiveIcon.setVisibility(View.VISIBLE);
                holder.ivLiveIcon.setImageResource(R.drawable.ic_live_video);
            }

            if (vo.getName().equals("addGif")) {
                holder.tvFeedImage.setVisibility(View.GONE);
                holder.ivLiveIcon.setVisibility(View.VISIBLE);
                holder.ivLiveIcon.setImageResource(R.drawable.ses_gif);
            }

            holder.tvFeedImage.setText(vo.getImageCode());
            holder.tvFeedImage.setTypeface(iconFont);
            holder.tvFeedImage.setTextColor(Color.parseColor(vo.getColorCode()));

            holder.llMain.setOnClickListener(v -> listener.onItemClicked(0, "" + vo.getValue(), holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvFeedText;
        TextView tvFeedImage;
        AppCompatImageView ivLiveIcon;
        LinearLayoutCompat llMain;

        ContactHolder(View itemView) {
            super(itemView);
            try {
                llMain = itemView.findViewById(R.id.llMain);
                tvFeedText = itemView.findViewById(R.id.tvFeedText);
                tvFeedImage = itemView.findViewById(R.id.tvFeedImage);
                ivLiveIcon = itemView.findViewById(R.id.ivLiveIcon);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
