package com.sesolutions.ui.postfeed;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Gallary;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StickerAddAdapter extends RecyclerView.Adapter<StickerAddAdapter.ContactHolder> {

    private final List<Gallary> list;
    private final Context context;
    private final OnUserClickedListener<String, String> listener;
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;

    @Override
    public void onViewAttachedToWindow(@NotNull StickerAddAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public StickerAddAdapter(List<Gallary> list, Context cntxt, OnUserClickedListener<String, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_add, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Gallary vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvCategory.setText(vo.getCategory());
            Glide.with(context).load(vo.getIcon()).into(holder.ivSticker);
            holder.cbAddSticker.setChecked(vo.isSelected());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked("", "", holder.getAdapterPosition());
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvCategory;
        protected ImageView ivSticker;
        protected CheckBox cbAddSticker;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                cbAddSticker = itemView.findViewById(R.id.cbAddSticker);
                ivSticker = itemView.findViewById(R.id.ivSticker);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
