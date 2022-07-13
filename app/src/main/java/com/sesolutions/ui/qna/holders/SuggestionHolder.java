package com.sesolutions.ui.qna.holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.ui.qna.SuggestionQAAdapter;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

public class SuggestionHolder extends RecyclerView.ViewHolder {

    public MultiSnapRecyclerView rvChild;
    public View tvMore;
    public TextView tvCategory;
    public SuggestionQAAdapter adapter;
    public PageIndicatorView pageIndicatorView;

    public SuggestionHolder(View itemView) {
        super(itemView);

        rvChild = itemView.findViewById(R.id.rvChild);
        tvMore = itemView.findViewById(R.id.tvMore);
        tvCategory = itemView.findViewById(R.id.tvCategory);
        pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

    }
}