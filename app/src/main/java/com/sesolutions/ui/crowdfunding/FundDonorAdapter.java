package com.sesolutions.ui.crowdfunding;

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
import com.sesolutions.responses.fund.Donor;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class FundDonorAdapter extends RecyclerView.Adapter<FundDonorAdapter.ContactHolder> {

    private final List<Donor> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_ADDED_ON;

    private boolean owner = false;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull FundDonorAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public FundDonorAdapter(List<Donor> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.listFriends = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.TXT_ADDED_ON = context.getResources().getString(R.string.added_on_);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Donor eventItem = listFriends.get(position);

            holder.tvName.setText(eventItem.getTitle());
            holder.tvJoinedOn.setText(Util.changeFormat(eventItem.getCreationDate()));
            Util.showImageWithGlide(holder.ivImage, eventItem.getPhoto());
            holder.tvPrice.setText(eventItem.getTotalAmount());

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public void setType(String type) {
        this.type = type;
    }

/*    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }*/


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvJoinedOn, tvPrice;
        public ImageView ivImage;
        public CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvName = itemView.findViewById(R.id.tvName);
                tvJoinedOn = itemView.findViewById(R.id.tvJoinedOn);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
