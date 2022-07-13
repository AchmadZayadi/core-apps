package com.sesolutions.ui.credit.holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

public class CreditParentHolder<T extends  RecyclerView.Adapter> extends RecyclerView.ViewHolder {

    public MultiSnapRecyclerView rvChild;
    public RecyclerView rvChild2;
    public TextView tvNoData;
    public TextView tvCategory;
    public T adapter;
    public PageIndicatorView pageIndicatorView;

    public CreditParentHolder(View itemView) {
        super(itemView);
        rvChild = itemView.findViewById(R.id.rvChild);
        rvChild2 = itemView.findViewById(R.id.rvChild2);
        tvCategory = itemView.findViewById(R.id.tvCategory);
        tvNoData = itemView.findViewById(R.id.tvNoData);
        pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);
    }
}
