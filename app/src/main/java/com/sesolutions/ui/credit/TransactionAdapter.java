package com.sesolutions.ui.credit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.credit.Transaction;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class TransactionAdapter extends RecycleViewAdapter<TransactionAdapter.ContactHolder, Transaction> {// RecyclerView.Adapter<> {

    private final OnLoadMoreListener loadListener;
   // private final Typeface iconFont;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull TransactionAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public TransactionAdapter(List<Transaction> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        super(list, cntxt, listener);
        this.loadListener = loadMoreListener;
       // iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ? R.layout.item_transaction_header : R.layout.item_credit_transaction, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            if (holder.getAdapterPosition() != 0) {
                final Transaction opt = list.get(position);

                holder.tvPointType.setText(opt.getLanguage());
                holder.tvPositive.setText(opt.getPositive());
                holder.tvNegetive.setText(opt.getNegetive());
                holder.tvDate.setText(opt.getDateStr());

                //  holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvPointType, tvPositive, tvNegetive, tvDate;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                tvPointType = ((TextView) itemView.findViewById(R.id.tv1));
                tvPositive = ((TextView) itemView.findViewById(R.id.tv2));
                tvNegetive = ((TextView) itemView.findViewById(R.id.tv3));
                tvDate = ((TextView) itemView.findViewById(R.id.tv4));

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
