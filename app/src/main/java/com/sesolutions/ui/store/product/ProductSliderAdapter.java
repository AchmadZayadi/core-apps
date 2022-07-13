package com.sesolutions.ui.store.product;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.store.product.ProductResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class ProductSliderAdapter extends RecyclerView.Adapter<ProductSliderAdapter.ContactHolder> {

    private final List<ProductResponse.SliderImage> list;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Context context;
    private boolean isFull;

    public ProductSliderAdapter(List<ProductResponse.SliderImage> list, Context context, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listener = listenr;
        this.context = context;
    }

    public ProductSliderAdapter(List<ProductResponse.SliderImage> list, Context context, OnUserClickedListener<Integer, Object> listenr, boolean isFull) {
        this(list, context, listenr);
        this.isFull = isFull;
    }

    @NonNull
    @Override
    public ProductSliderAdapter.ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isFull ? R.layout.item_image_slider : R.layout.item_image_200, parent, false);
        return new ProductSliderAdapter.ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductSliderAdapter.ContactHolder holder, int position) {

        try {
            final ProductResponse.SliderImage vo = list.get(position);
            Util.showImageWithGlide(holder.ivSongImage, vo.getValue(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_1, holder.ivSongImage, holder.getAdapterPosition()));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected ImageView ivSongImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivSongImage = itemView.findViewById(R.id.ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}

