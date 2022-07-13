package com.sesolutions.ui.credit;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.credit.EarnCredit;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.ui.credit.holders.CreditParentHolder;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class EarnCreditAdapter extends RecycleViewAdapter<CreditParentHolder, EarnCredit> {// RecyclerView.Adapter<> {

    private final OnLoadMoreListener loadListener;
    // private final Typeface iconFont;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull CreditParentHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public EarnCreditAdapter(List<EarnCredit> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
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
    public CreditParentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ? R.layout.layout_credit_header : R.layout.item_badge_parent, parent, false);
        return new CreditParentHolder<EarnCreditChildAdapter>(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CreditParentHolder holder3, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder3.itemView, context);
            if (holder3.getAdapterPosition() != 0) {
                holder3.tvCategory.setText(list.get(position).getLabel() );
                if (holder3.adapter == null) {
                    /*set child item list*/

                    holder3.rvChild2.setHasFixedSize(true);
                    holder3.rvChild2.setLayoutManager(new LinearLayoutManager(context));
                    holder3.rvChild2.setVisibility(View.VISIBLE);
                    holder3.rvChild.setVisibility(View.GONE);
                    holder3.tvNoData.setVisibility(View.GONE);
                    holder3.adapter = new EarnCreditChildAdapter(list.get(position).getValue(), context, listener);
                    holder3.rvChild2.setAdapter(holder3.adapter);
                    holder3.pageIndicatorView.setVisibility(View.GONE);

                } else {
                    holder3.adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

}
