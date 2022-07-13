package com.sesolutions.ui.postfeed;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Emotion;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class StickerChildAdapter extends RecyclerView.Adapter<StickerChildAdapter.ContactHolder> {

    private final List<Emotion> list;
    private final Context context;
    private final OnUserClickedListener<String, String> listener;
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;

    @Override
    public void onViewAttachedToWindow(@NonNull StickerChildAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public StickerChildAdapter(List<Emotion> list, Context cntxt, OnUserClickedListener<String, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        this.themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_child, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Emotion vo = list.get(position);
            // holder.tvFeedText.setText(vo.getTitle());
            //Glide.with(context).load(vo.getIcon()).into(holder.ivFeedImage);
            Util.showImageWithGlide(holder.ivFeedImage, vo.getIcon());
            // holder.cvMain.setCardBackgroundColor(Color.parseColor(vo.getColor()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked("", "", holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvFeedText;
        protected ImageView ivFeedImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
