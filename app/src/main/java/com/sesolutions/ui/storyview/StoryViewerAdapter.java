package com.sesolutions.ui.storyview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class StoryViewerAdapter extends RecycleViewAdapter<StoryViewerAdapter.ContactHolder, StoryModel> {

    public StoryViewerAdapter(List<StoryModel> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        super(list, cntxt, listener);
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_viewers, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StoryModel vo = list.get(position);
            holder.tvUser.setText(vo.getUserTitle());
            Util.showImageWithGlide(holder.ivImage, vo.getUserImage(), context, R.drawable.placeholder_square);

            if (null != vo.getOptions()) {
                holder.bMute.setText(vo.getOptions().getLabel());
                holder.bMute.setVisibility(View.VISIBLE);
                holder.bMute.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, null, holder.getAdapterPosition()));
            } else {
                holder.bMute.setVisibility(View.GONE);
            }

            //holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, vo.getType(), vo.getUserId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvUser, tvCreatedOn;
        protected CircleImageView ivImage;
        protected AppCompatButton bMute;
        //  protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // cvMain = itemView.findViewById(R.id.cvMain);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvUser = itemView.findViewById(R.id.tvTitle);
                tvCreatedOn = itemView.findViewById(R.id.tvCreatedOn);
                bMute = itemView.findViewById(R.id.bMute);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
