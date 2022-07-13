package com.sesolutions.ui.storyview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class StoryAdapter extends RecycleViewAdapter<StoryAdapter.StoryHolder, StoryModel> {
    // private final Typeface iconFont;
    private int parentPosition;

    public StoryAdapter(List<StoryModel> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int parentPosition) {
        super(list, cntxt, listenr);
        // this.iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        this.parentPosition = parentPosition;
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
            final StoryModel vo = list.get(position);

            if (null != vo.getImages()) {
                holder.storyView.setImageUris(vo.getImages(), vo.getUserImage());
            } else {
                holder.storyView.setImageUris(new ArrayList<>(), vo.getUserImage());
            }
            //holder.storyView.setImageUris(new ArrayList<>(list));
            if (position == 0) {
                holder.ivProfileAdd.setVisibility(View.VISIBLE);
                holder.storyView.setVisibility(View.GONE);

            } else {
                holder.ivProfileAdd.setVisibility(View.GONE);
                holder.storyView.setVisibility(View.VISIBLE);

            }

            if (null != vo.isLive()) {
                if (vo.isLive()) {
                    holder.llAnimation.setVisibility(View.VISIBLE);
                    holder.liveAnimation.playAnimation();
                    holder.storyView.setVisibility(View.GONE);
                    holder.ivProfileAdd.setVisibility(View.GONE);
//                    holder.cvLive.setVisibility(View.VISIBLE);
                }
            }

            Util.showImageWithGlide(holder.ivStoryImage, vo.getFirstStoryImage(), context, R.drawable.placeholder_square);
            holder.tvTitleStory.setText(vo.getUsername());

            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIEW_STORY, list, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class StoryHolder extends RecyclerView.ViewHolder {

        TextView tvTitleStory, tvStats;
        ImageView ivProfileStory, ivProfileAdd, ivStoryImage;
        View rlMain, ivLiveVideoImage, llAnimation, cvLive;
        LottieAnimationView liveAnimation;
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
                ivLiveVideoImage = itemView.findViewById(R.id.ivLiveVideoImage);
                liveAnimation = itemView.findViewById(R.id.liveAnimation);
                llAnimation = itemView.findViewById(R.id.llAnimation);
                cvLive = itemView.findViewById(R.id.cvLive);
                tvStats = itemView.findViewById(R.id.tvStats);
                /* cvMain = itemView.findViewById(R.id.cvMain);
                 */

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
