package com.sesolutions.ui.poll_core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class CpollAdapter extends RecyclerView.Adapter<CpollAdapter.CContactHolder> {
    private final List<Poll> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;

    private int loggedInId;

    @Override
    public void onViewAttachedToWindow(@NonNull CpollAdapter.CContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {

            loadListener.onLoadMore();
        }
    }

    public CpollAdapter(List<Poll> list, Context cntxt, OnLoadMoreListener loadListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public CpollAdapter.CContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_core, parent, false);
        return new CpollAdapter.CContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CpollAdapter.CContactHolder holder, int position) {

        try {
            final Poll vo = list.get(position);
            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvTitle.setVisibility(View.GONE);
            } else {
                holder.tvTitle.setVisibility(View.VISIBLE);
                holder.tvTitle.setText(vo.getTitle());
            }

            if (vo.isclosed() != 0) {
                holder.ivlock.setVisibility(View.VISIBLE);
                holder.ivlock.setOnClickListener(v -> Util.showSnackbar(v, "This poll is currently closed."));
            } else {
                holder.ivlock.setVisibility(View.GONE);
            }

            holder.tvDate.setText(Util.changeDateFormat(context, vo.getCreationDate()));
            holder.tvOwner.setText(vo.getOwnerTitle());
            Util.showImageWithGlide(holder.ivImage, vo.getBrImages().getMain(), context, R.drawable.placeholder_square);
            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLoggedInId(int loggedInId) {
        this.loggedInId = loggedInId;
    }

    public class CContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvDesc;
        protected TextView tvStats;
        protected TextView tvOwner;
        protected TextView tvDate;
        protected View ivOption;
        protected View rlMain;
        protected ImageView ivImage;
        protected ImageView ivlock;

        public CContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                tvTitle = itemView.findViewById(R.id.tvTitlepoll);
                tvOwner = itemView.findViewById(R.id.tvOwnerpoll);
                tvDate = itemView.findViewById(R.id.tvDate5);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                rlMain = itemView.findViewById(R.id.rlMain);
                ivlock = itemView.findViewById(R.id.lock);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}





