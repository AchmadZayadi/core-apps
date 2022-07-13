package com.sesolutions.ui.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ContactHolder> {

    private static final String TAG = "CategoryAdapter";

    private final List<Category> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final ThemeManager themeManager;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    @Override
    public void onViewAttachedToWindow(@NonNull CategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public CategoryAdapter(List<Category> list, Context cntxt, OnUserClickedListener<Integer, String> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=null;
        if(SCREEN_TYPE == Constant.FormType.CREATE_PAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_category,parent, false);
        }else if(SCREEN_TYPE == Constant.FormType.TYPE_CATEGORY_JOB){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_category, parent, false);
        }else if(SCREEN_TYPE == Constant.FormType.TYPE_MULTISTORE_CATEGORY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_video, parent, false);
        }else if(SCREEN_TYPE == Constant.FormType.TYPE_MULTISTORE_EXPERT_CATEGORY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expert_category, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_video, parent, false);
        }
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final Category vo = list.get(position);
            if (SCREEN_TYPE == Constant.FormType.CREATE_PAGE) {
                holder.tvTitle.setText(vo.getName());
                Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.subtitle_category_selected);
            } else if (SCREEN_TYPE == Constant.FormType.CREATE_EVENT) {
                holder.tvTitle.setText(vo.getName());
                Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.subtitle_category_selected);


                if(vo.getCount()!=null){
                    holder.tvVideoCount2.setText(vo.getCount());
                }else {
                    holder.tvVideoCount2.setText(vo.getTotaleventcategories());
                }

            } else if (SCREEN_TYPE == Constant.FormType.EDIT_EVENT) {
                //EDIT_EVENT means core plugin
                holder.tvTitle.setText(vo.getLabel());
                Util.showImageWithGlide(holder.ivImageCategory, vo.getThumbnail(), context, R.drawable.subtitle_category_selected);
                holder.tvVideoCount.setText("");
            } else if (SCREEN_TYPE == Constant.FormType.TYPE_CATEGORY_JOB) {
                //EDIT_EVENT means core plugin
                holder.tvTitle.setText(vo.getLabel());
                Util.showImageWithGlide(holder.ivImageCategory, vo.getCategory_images().getMain(), context, R.drawable.subtitle_category_selected);
                //holder.ivImageCategory.setImageResource(R.drawable.bank_1);
                holder.tvVideoCount2.setText("11 Jobs");
                holder.tvVideoCount2.setVisibility(View.GONE);
            }else {
                holder.tvTitle.setText(vo.getLabel());
                Util.showImageWithGlide(holder.ivImageCategory, vo.getThumbnail(), context, R.drawable.subtitle_category_selected);
                holder.tvVideoCount2.setText(vo.getCount());
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvVideoCount,tvVideoCount2;
        protected View cvMain;
        protected ImageView ivImageCategory;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                cvMain.setOnClickListener(v -> {
                    listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, getAdapterPosition());
                });
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvVideoCount = itemView.findViewById(R.id.tvVideoCount);
                tvVideoCount2 = itemView.findViewById(R.id.tvDesc);
                ivImageCategory = itemView.findViewById(R.id.ivImageCategory);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

}
