package com.sesolutions.ui.postfeed;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Emotion;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class GifViewAdapter extends RecyclerView.Adapter<GifViewAdapter.ContactHolder> {

    private final List<GifResponsemodel.ResultDTO.GifDTO> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;

    @Override
    public void onViewAttachedToWindow(GifViewAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD * 2 - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public GifViewAdapter(List<GifResponsemodel.ResultDTO.GifDTO> list, Context cntxt, OnUserClickedListener<Integer, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gifview, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {
        try {

            if (!TextUtils.isEmpty(list.get(position).imgUrl) && list.size()>0) {
                Util.showImageWithGlideGIF(holder.ivFeedImage, list.get(position).imgUrl, context, R.drawable.placeholder_3_2);
            }

         //   holder.tvFeedText.setTextColor(text1);
         //   holder.tvFeedText.setText(vo.getTitle());
   //         Util.showImageWithGlide(holder.ivFeedImage, vo.getIcon(), context);
            // Glide.with(context).load(vo.getIcon()).into(holder.ivFeedImage);
     //       holder.cvMain.setCardBackgroundColor(Color.parseColor(vo.getColor()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.GIFSTIKER, ""+list.get(position).imgUrl, holder.getAdapterPosition()));

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
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
