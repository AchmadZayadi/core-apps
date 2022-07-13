package com.sesolutions.ui.credit;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.credit.Badge;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.credit.holders.CreditParentHolder;
import com.sesolutions.utils.SPref;

import java.util.List;


public class BadgeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<List<Badge>> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    protected BadgeAdapter(List<List<Badge>> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreditParentHolder<BadgeChildAdapter>(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge_parent, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);
        final CreditParentHolder holder3 = (CreditParentHolder) parentHolder;
        holder3.tvCategory.setText(position == 0 ? R.string.my_badges : R.string.all_badges);

        if (holder3.adapter == null) {
            /*set child item list*/
            if (position == 0) {
                holder3.rvChild.setHasFixedSize(true);
                //final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                // holder3.rvChild.setLayoutManager(layoutManager);
                if (null != list.get(position) && list.get(position).size() > 0) {
                    holder3.rvChild.setVisibility(View.VISIBLE);
                    holder3.tvNoData.setVisibility(View.GONE);
                    holder3.adapter = new BadgeChildAdapter(list.get(position), context, listener, false);
                    holder3.rvChild.setAdapter(holder3.adapter);
                    holder3.pageIndicatorView.setVisibility(View.VISIBLE);
                    holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                    holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                } else {
                    holder3.pageIndicatorView.setVisibility(View.GONE);
                    holder3.rvChild.setVisibility(View.GONE);
                    holder3.tvNoData.setVisibility(View.VISIBLE);
                }
            } else {
                holder3.pageIndicatorView.setVisibility(View.GONE);
                if (null != list.get(position) && list.get(position).size() > 0) {
                    holder3.tvNoData.setVisibility(View.GONE);
                    holder3.rvChild2.setHasFixedSize(true);
                    // final StaggeredGridLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    // holder3.rvChild.setLayoutManager(layoutManager);
                    holder3.adapter = new BadgeChildAdapter(list.get(position), context, listener, true);
                    holder3.rvChild2.setAdapter(holder3.adapter);
                } else {
                    holder3.rvChild2.setVisibility(View.GONE);
                    holder3.tvNoData.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder3.adapter.notifyDataSetChanged();
            holder3.pageIndicatorView.setSelection(0);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
