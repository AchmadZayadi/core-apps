package com.sesolutions.ui.dashboard;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.dashboard.composervo.FeedBg;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class AttachBgAdapter extends RecyclerView.Adapter<AttachBgAdapter.ContactHolder> {

    private final List<FeedBg> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private boolean isGrid = false;


/*
    @Override
    public void onViewAttachedToWindow(ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && (list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }
*/

    public AttachBgAdapter(List<FeedBg> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        // this.loadListener = loadListener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_attach_bg_grid : R.layout.item_attach_bg, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            final FeedBg vo = list.get(position);
            // holder.cvBg.setCardBackgroundColor(Color.parseColor(vo));
            // holder.ivPlay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            Util.showImageWithGlide(holder.ivBg, vo.getPhoto(), context, R.drawable.placeholder_3_2);
            holder.cvBg.setOnClickListener(v -> listener.onItemClicked(Constant.Events.BG_ATTACH, "", holder.getAdapterPosition()));

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        CardView cvBg;
        ImageView ivBg;

        ContactHolder(View itemView) {
            super(itemView);
            try {
                cvBg = itemView.findViewById(R.id.cvBg);
               // ivPlay = itemView.findViewById(R.id.ivPlay);
                ivBg = itemView.findViewById(R.id.ivBg);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
