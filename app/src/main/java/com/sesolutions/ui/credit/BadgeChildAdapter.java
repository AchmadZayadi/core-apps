package com.sesolutions.ui.credit;

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
import com.sesolutions.responses.credit.Badge;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class BadgeChildAdapter extends RecyclerView.Adapter<BadgeChildAdapter.ContactHolder> {

    private final List<Badge> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final boolean isGrid;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    public BadgeChildAdapter(List<Badge> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isGrid) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isGrid = isGrid;
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_credit_badge_vertical : R.layout.item_credit_badge, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Badge vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText( vo.getCountLabel());
            Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

            //holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PAGE_SUGGESTION_MAIN, "" + holder, vo.getPage_id()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist;
        protected View rlMain;
        protected ImageView ivImage;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvMembers);
                ivImage = itemView.findViewById(R.id.ivFeedImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
