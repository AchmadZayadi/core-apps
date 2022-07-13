package com.sesolutions.ui.postfeed;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Emotion;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class UserEmotionAdapter extends RecyclerView.Adapter<UserEmotionAdapter.ContactHolder> {

    private final List<Emotion> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;

   /* @Override
    public void onViewAttachedToWindow(UserEmotionAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD * 2 - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/

    public UserEmotionAdapter(List<Emotion> list, Context cntxt, OnUserClickedListener<Integer, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_emotion, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {

            final Emotion vo = list.get(position);
            Glide.with(context).load(vo.getIcon()).into(holder.ivFeedImage);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.STICKER_USER, "", holder.getAdapterPosition());
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

        protected TextView tvFeedText;
        protected ImageView ivFeedImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                cvMain = itemView.findViewById(R.id.llMain);
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
