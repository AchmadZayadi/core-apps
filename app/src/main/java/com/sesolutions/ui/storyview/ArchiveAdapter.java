package com.sesolutions.ui.storyview;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class ArchiveAdapter extends RecycleViewAdapter<ArchiveAdapter.StoryHolder, StoryContent> {
    private final Typeface iconFont;
    private boolean isHighlighting;

    public ArchiveAdapter(List<StoryContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        super(list, cntxt, listenr);
        this.iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ArchiveAdapter.StoryHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }


    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_archived, parent, false);
        return new StoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final StoryHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StoryContent vo = list.get(position);

            Util.showImageWithGlide(holder.ivStoryImage, vo.getMediaUrl(), context, R.drawable.placeholder_square);
            holder.tvCreatedOn.setText(Util.changeDateFormat(vo.getCreatedDate(), Constant.FORMAT_DATE_STORY));
            holder.tvStats.setTypeface(iconFont);

            String detail = "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf06e " + vo.getViewCount();

            holder.tvStats.setText(detail);
            // Util.showImageWithGlide(holder.ivImage, vo.getUser_image(), context, R.drawable.placeholder_square);

            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.STORY_ARCHIVE, list, holder.getAdapterPosition()));

            if (isHighlighting) {
                holder.vStats.setVisibility(View.GONE);
                holder.vHighlight.setVisibility(View.VISIBLE);
                holder.sbvHighlight.setVisibility(View.VISIBLE);
                if (vo.showAnimation() == 1) {
                    vo.setAnimation(0);
                    holder.ivHighlight.setImageDrawable(ContextCompat.getDrawable(context, vo.isHighlighted() ? R.drawable.rating_star_filled : R.drawable.rating_star_unfilled));
                    holder.sbvHighlight.likeAnimation();
                } else {
                    holder.ivHighlight.setImageDrawable(ContextCompat.getDrawable(context, vo.isHighlighted() ? R.drawable.rating_star_filled : R.drawable.rating_star_unfilled));
                }
            } else {
                holder.vStats.setVisibility(View.VISIBLE);
                holder.vHighlight.setVisibility(View.GONE);
                holder.sbvHighlight.setVisibility(View.GONE);
            }

            holder.sbvHighlight.setOnClickListener(v -> listener.onItemClicked(Constant.Events.USER_SELECT, null, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setHighlighting(boolean highlighting) {
        isHighlighting = highlighting;
    }


    public static class StoryHolder extends RecyclerView.ViewHolder {

        TextView tvCreatedOn, tvStats;
        ImageView ivProfileStory, ivStoryImage, ivHighlight;
        View rlMain, vHighlight, vStats;
        SmallBangView sbvHighlight;


        StoryHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                ivProfileStory = itemView.findViewById(R.id.ivProfileStory);
                ivStoryImage = itemView.findViewById(R.id.ivStoryImage);

                tvStats = itemView.findViewById(R.id.tvStats);
                tvCreatedOn = itemView.findViewById(R.id.tvCreatedOn);
                vHighlight = itemView.findViewById(R.id.vHighlight);
                sbvHighlight = itemView.findViewById(R.id.sbvHighlight);
                ivHighlight = itemView.findViewById(R.id.ivHighlight);
                vStats = itemView.findViewById(R.id.vStats);
                /* cvMain = itemView.findViewById(R.id.cvMain);
                 */

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
