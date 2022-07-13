package com.sesolutions.ui.storyview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class StoryHighlightAdapter extends RecycleViewAdapter<StoryHighlightAdapter.StoryHolder, StoryContent> {

    public StoryHighlightAdapter(List<StoryContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        super(list, cntxt, listenr);
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_square, parent, false);
        return new StoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryHolder holder, final int position) {
        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StoryContent vo = list.get(position);
            holder.storyView.setVisibility(View.GONE);
            holder.ivProfileAdd.setVisibility(View.GONE);

            Util.showImageWithGlide(holder.ivStoryImage, vo.getMediaUrl(), context, R.drawable.placeholder_square);
            holder.tvTitleStory.setText(vo.getComment());
            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIEW_STORY, list, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class StoryHolder extends RecyclerView.ViewHolder {

        TextView tvTitleStory, tvStats;
        ImageView ivProfileStory, ivProfileAdd, ivStoryImage;
        View rlMain;
        StoryView storyView;


        StoryHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                storyView = itemView.findViewById(R.id.storyView);
                ivProfileStory = itemView.findViewById(R.id.ivProfileStory);
                ivStoryImage = itemView.findViewById(R.id.ivStoryImage);
                tvTitleStory = itemView.findViewById(R.id.tvTitleStory);
                ivProfileAdd = itemView.findViewById(R.id.ivProfileAdd);
                tvStats = itemView.findViewById(R.id.tvStats);
                /* cvMain = itemView.findViewById(R.id.cvMain);
                 */
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
