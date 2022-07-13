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
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class EventCategoryAdapter extends RecyclerView.Adapter<EventCategoryAdapter.ContactHolder> {

    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final List<Category> listCat;
    //O : category
    //1 : sub_category
    //2 : sub_subcategory
    private int categoryLevel = 0;


   /* @Override
    public void onViewAttachedToWindow(ContestCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/


    public EventCategoryAdapter(List<Category> list, OnUserClickedListener<Integer, Object> listenr, Context context) {
        this.listCat = list;
        this.context = context;
        this.listener = listenr;
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_category, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Category vo = listCat.get(position);
            holder.tvTitle.setText(vo.getName());
            holder.tvDesc.setText(vo.getCount());
            holder.tvDesc.setVisibility(null != vo.getCount() ? View.VISIBLE : View.GONE);
            Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, vo, categoryLevel));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return listCat.size();
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvDesc;
        protected View cvMain;
        protected ImageView ivImageCategory;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivImageCategory = itemView.findViewById(R.id.ivImageCategory);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
