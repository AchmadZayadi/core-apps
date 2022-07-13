package com.sesolutions.ui.crowdfunding;

import android.content.Context;
import android.graphics.Typeface;
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
import com.sesolutions.responses.fund.Donor;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class FundRewardAdapter extends RecycleViewAdapter<FundRewardAdapter.ContactHolder, Donor> {// RecyclerView.Adapter<> {

    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull FundRewardAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public FundRewardAdapter(List<Donor> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        super(list, cntxt, listener);
        this.loadListener = loadMoreListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crowd_reward, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Donor eventItem = list.get(position);

            holder.tvName.setText(eventItem.getTitle());
            holder.ivDate.setTypeface(iconFont);
            holder.ivDate.setText(Constant.FontIcon.CALENDAR);
            holder.tvDate.setText(Util.changeFormat(eventItem.getCreationDate()));
            Util.showImageWithGlide(holder.ivImage, eventItem.getPhoto(), context, R.drawable.placeholder_square);
            holder.tvPrice.setText(eventItem.getMinAmount());
            holder.tvDesc.setText(eventItem.getBody());

            //  holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvDesc, tvPrice, ivDate, tvDate;
        public ImageView ivImage;
        public CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvName = itemView.findViewById(R.id.tvName);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivDate = itemView.findViewById(R.id.ivDate);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
