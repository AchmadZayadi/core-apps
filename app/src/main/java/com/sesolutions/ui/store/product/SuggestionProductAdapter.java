package com.sesolutions.ui.store.product;

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
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class SuggestionProductAdapter extends RecyclerView.Adapter<SuggestionProductAdapter.ContactHolder> {

    private final List<StoreContent> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isRecent;

    private String categorytype;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    public SuggestionProductAdapter(List<StoreContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isRecent) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isRecent = isRecent;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
    }

    @NonNull
    @Override
    public SuggestionProductAdapter.ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isRecent ? R.layout.item_page_recent : R.layout.item_page_suggestion, parent, false);
        return new SuggestionProductAdapter.ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SuggestionProductAdapter.ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            final StoreContent vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(TXT_BY + vo.getOwner_title());
            holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
            holder.tvCategory.setText(TXT_IN + vo.getCategory_title());
            Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);


                Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
                holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIEW_PRODUCT, "" + holder, vo.getProductId()));


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setType(String type) {
        this.categorytype = type;
    }


    @Override
    public int getItemCount() {
        return list.size();
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
