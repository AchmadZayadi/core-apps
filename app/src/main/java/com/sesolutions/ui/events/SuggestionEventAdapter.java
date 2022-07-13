package com.sesolutions.ui.events;

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
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class SuggestionEventAdapter extends RecyclerView.Adapter<SuggestionEventAdapter.ContactHolder> {

    private final List<CommonVO> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isRecent;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

   /* @Override
    public void onViewAttachedToWindow(ContestCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/

    public SuggestionEventAdapter(List<CommonVO> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isRecent) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isRecent = isRecent;
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isRecent ? R.layout.item_page_recent : R.layout.item_page_suggestion, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final CommonVO vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(TXT_BY + vo.getOwnerTitle());
            holder.tvCategory.setText(TXT_IN + vo.getCategoryTitle());
            Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);

            Util.showImageWithGlide(holder.ivImage, vo.getCoverImageUrl(), context, R.drawable.placeholder_square);


            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PAGE_SUGGESTION_MAIN, "" + holder, vo.getEventId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
        //return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist;
        protected TextView tvCategory;
        protected View rlMain;
        protected ImageView ivImage;
        protected ImageView ivUser;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivUser = itemView.findViewById(R.id.ivUser);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
