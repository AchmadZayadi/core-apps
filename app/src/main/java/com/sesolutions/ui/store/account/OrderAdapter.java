package com.sesolutions.ui.store.account;

import android.content.Context;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.store.checkout.Order;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ContactHolder> {

    private final List<Order> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    // private final int alphaWhite;
    //  private int lastPosition;


    public OrderAdapter(List<Order> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        // this.alphaWhite = ContextCompat.getColor(context, R.color.alpha_white);
        //  this.transparent = ContextCompat.getColor(context, R.color.transparent_black);
        // this.loadListener = loadListener;
    }

    @NotNull
    @Override
    public OrderAdapter.ContactHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderAdapter.ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

        try {
            final Order vo = list.get(position);
//            Util.showImageWithGlide(holder.ivAttachImage, (vo.getImages().getMain()), context, R.drawable.placeholder_menu);
//            holder.ivVideoForground.setVisibility(vo.getType().equalsIgnoreCase(Constant.ACTIVITY_TYPE_ALBUM) ? View.GONE : View.VISIBLE);

            holder.tvprice.setText(String.valueOf(vo.getTotal()));
            holder.tvid.setText(String.valueOf(vo.getOrderId()));
            holder.tvCreatedOn.setText(Util.changeDateFormat(context,vo.getCreationDate()));
            holder.tvItemCount.setText(String.valueOf(vo.getItemCount()));
            holder.tvstatus.setText(vo.getStatus());
            holder.mcvViewOrder.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIEW_LIKED, "", vo.getOrderId()));
            holder.mcvDelete.setOnClickListener(v -> listener.onItemClicked(Constant.Events.DELETE, "", vo.getOrderId()));

//            holder.cvMain.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClicked(Constant.Events.ITEM_COMMENT, vo.getType(), vo.getId());
//                }
//            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
//        return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected ImageView ivAttachImage;
        protected TextView tvprice, tvid, tvCreatedOn, tvItemCount, tvstatus;
        protected MaterialCardView mcvViewOrder, mcvDelete;


        public ContactHolder(View itemView) {
            super(itemView);

            tvid = itemView.findViewById(R.id.tvorderId);
            tvCreatedOn = itemView.findViewById(R.id.tvCreatedOn);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvprice = itemView.findViewById(R.id.tvPrice);
            tvstatus = itemView.findViewById(R.id.tvStatus);
            mcvViewOrder = itemView.findViewById(R.id.mcvViewOrder);
            mcvDelete = itemView.findViewById(R.id.mcvDelete);

        }
    }
}