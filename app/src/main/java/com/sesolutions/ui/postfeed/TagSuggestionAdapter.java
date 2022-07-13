package com.sesolutions.ui.postfeed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;

import java.util.List;


public class TagSuggestionAdapter extends RecyclerView.Adapter<TagSuggestionAdapter.ContactHolder> {

    private final List<Friends> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final Typeface iconFont;
    private final int text1;
    // private final OnLoadMoreListener loadListener;
    private int lastPosition;

/*
    @Override
    public void onViewAttachedToWindow(ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }
*/

    public TagSuggestionAdapter(List<Friends> list, Context cntxt, OnUserClickedListener<Integer, String> listener) {
        this.list = list;
        this.context = cntxt;
        this.lastPosition = -1;
        this.listener = listener;
        this.text1 = Color.parseColor(Constant.text_color_1);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        // this.loadListener = loadListener;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mention, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {

            final Friends vo = list.get(position);
            // FontManager.markAsIconContainer(holder.itemView, iconFont);
            holder.tvFeedText.setTextColor(Color.parseColor(Constant.text_color_1));
            holder.tvFeedText.setText(vo.getLabel());
            holder.llMain.setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            holder.tvFeedText.setTextColor(Color.parseColor(Constant.text_color_1));

            Glide.with(context).load(vo.getPhoto()).into(holder.ivFeedImage);

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(0, "", holder.getAdapterPosition());
                }
            });

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
        protected LinearLayoutCompat llMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                llMain = itemView.findViewById(R.id.llMain);
                tvFeedText = itemView.findViewById(R.id.tvFeedText);
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
