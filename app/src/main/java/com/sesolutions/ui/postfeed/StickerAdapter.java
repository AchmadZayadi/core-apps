package com.sesolutions.ui.postfeed;

import android.content.Context;
import android.graphics.Color;
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
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ContactHolder> {

    private final List<Emotion> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;
    private final int text1;

    @Override
    public void onViewAttachedToWindow(StickerAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD * 2 - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public StickerAdapter(List<Emotion> list, Context cntxt, OnUserClickedListener<Integer, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        this.text1 = Color.parseColor(Constant.text_color_1);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {
        try {
            final Emotion vo = list.get(position);
            holder.tvFeedText.setTextColor(text1);
            holder.tvFeedText.setText(vo.getTitle());
            Util.showImageWithGlide(holder.ivFeedImage, vo.getIcon(), context);
            // Glide.with(context).load(vo.getIcon()).into(holder.ivFeedImage);
            holder.cvMain.setCardBackgroundColor(Color.parseColor(vo.getColor()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.STICKER, "", holder.getAdapterPosition()));

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

        protected TextView tvFeedText;
        protected ImageView ivFeedImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvFeedText = itemView.findViewById(R.id.tvFeedText);
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
