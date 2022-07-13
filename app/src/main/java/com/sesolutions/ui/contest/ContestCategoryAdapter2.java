package com.sesolutions.ui.contest;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class ContestCategoryAdapter2 extends RecyclerView.Adapter<ContestCategoryAdapter2.ContactHolder> {

    private final List<CategoryPage> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final List<Category> listCat;
    private final boolean isContestSelected;

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

    public ContestCategoryAdapter2(List<CategoryPage> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listCat = null;
        this.context = cntxt;
        this.listener = listenr;
        this.isContestSelected = true;
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

    public ContestCategoryAdapter2(List<Category> list, OnUserClickedListener<Integer, Object> listenr, Context context) {
        this.listCat = list;
        this.list = null;
        this.context = context;
        this.listener = listenr;
        this.isContestSelected = false;
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_category2, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            if (isContestSelected) {
                final CategoryPage vo = list.get(position);
                holder.tvTitle.setText(vo.getCategoryName());
                //holder.tvDesc.setText(vo.getCategoryName());

                Log.e("contest category",""+vo.getTotal_contest_categories());
                Log.e("CategoryName",""+vo.getCategoryName());
                try {
                    if(vo.getTotal_contest_categories()!=null && !vo.getTotal_contest_categories().equalsIgnoreCase("0")){
                        holder.tvDesc.setText(vo.getTotal_contest_categories()+" Contests");;
                        holder.tvDesc.setVisibility(View.VISIBLE);
                    }else {
                        holder.tvDesc.setVisibility(View.GONE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.placeholder_square);
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, "" + vo.getCategoryName(), vo.getCategoryId()));
            } else {
                final Category vo = listCat.get(position);
                holder.tvTitle.setText(vo.getName());
                try {
                    if(vo.getTotal_contest_categories()!=null && !vo.getTotal_contest_categories().equalsIgnoreCase("0")){
                        holder.tvDesc.setText(vo.getTotal_contest_categories()+" Contests");;
                        holder.tvDesc.setVisibility(View.VISIBLE);
                    }else {
                        holder.tvDesc.setVisibility(View.GONE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.placeholder_square);
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, vo, vo.getCategoryId()));
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return isContestSelected ? list.size() : listCat.size();
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
