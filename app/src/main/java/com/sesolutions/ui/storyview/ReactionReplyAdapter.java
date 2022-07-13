package com.sesolutions.ui.storyview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class ReactionReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ReactionPlugin> list;
    private final OnUserClickedListener<Integer, Object> listener;


    public ReactionReplyAdapter(List<ReactionPlugin> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_reply_1, parent, false);
            return new HeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reaction_icon, parent, false);
            return new ReactionHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        try {
            if (position == 0) {
                ((HeaderHolder) holder).tvReply.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPLY, null, 0));
            } else {
                Util.showImageWithGlide(((ReactionHolder) holder).ivReaction, list.get(holder.getAdapterPosition()).getImage());
                //((ReactionHolder) holder).ivReaction.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_5, null, holder.getAdapterPosition()));
                ((ReactionHolder) holder).sbvReaction.setOnClickListener(v -> {
                    listener.onItemClicked(Constant.Events.IMAGE_5, null, holder.getAdapterPosition());
                    ((SmallBangView) v).likeAnimation();
                });

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvReply;


        HeaderHolder(View itemView) {
            super(itemView);
            try {
                tvReply = itemView.findViewById(R.id.tvReply);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class ReactionHolder extends RecyclerView.ViewHolder {

        ImageView ivReaction;
        SmallBangView sbvReaction;

        ReactionHolder(View itemView) {
            super(itemView);
            try {
                ivReaction = itemView.findViewById(R.id.ivReaction);
                sbvReaction = itemView.findViewById(R.id.sbvReaction);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
