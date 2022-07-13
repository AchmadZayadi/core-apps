package com.sesolutions.ui.credit;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.credit.Credit;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class EarnCreditChildAdapter extends RecycleViewAdapter<EarnCreditChildAdapter.ContactHolder, Credit> {// RecyclerView.Adapter<> {


    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull EarnCreditChildAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
       /* if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }*/
    }

    public EarnCreditChildAdapter(List<Credit> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        super(list, cntxt, listener);
        // iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_credit_item, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Credit opt = list.get(position);
            holder.tv1.setText(opt.getActivityType());
            holder.tv14.setText(opt.getFirstActivity());
            holder.tv24.setText(opt.getNextActivity());
            holder.tv34.setText(opt.getMaxPerDay());
            holder.tv44.setText(opt.getDeduction());

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tv1, tv14, tv24, tv34, tv44;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                tv1 = ((TextView) itemView.findViewById(R.id.tv1));
                tv14 = ((TextView) itemView.findViewById(R.id.tv14));
                tv24 = ((TextView) itemView.findViewById(R.id.tv24));
                tv34 = ((TextView) itemView.findViewById(R.id.tv34));
                tv44 = ((TextView) itemView.findViewById(R.id.tv44));

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
