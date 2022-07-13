package com.sesolutions.ui.common;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class IconCategoryAdapter extends RecyclerView.Adapter<IconCategoryAdapter.ContactHolder> {

    private final List<Category> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    @Override
    public void onViewAttachedToWindow(@NonNull IconCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public IconCategoryAdapter(List<Category> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_quote, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Category vo = list.get(position);
            holder.tvTitle.setText(vo.getLabel());
            Util.showImageWithGlide(holder.ivImageCategory, vo.getCatIcon(), context, R.drawable.placeholder_square);
            //  holder.tvVideoCount.setText(vo.getCount());

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, "" + vo.getLabel(), vo.getCategoryId()));

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
        //  protected TextView tvVideoCount;
        protected View cvMain;
        protected ImageView ivImageCategory;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.rlMain);
                tvTitle = itemView.findViewById(R.id.tvFeedText);
                //  tvVideoCount = itemView.findViewById(R.id.tvVideoCount);
                ivImageCategory = itemView.findViewById(R.id.ivFeedImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
