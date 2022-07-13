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


public class CMyPollsAdapter extends RecyclerView.Adapter<CMyPollsAdapter.CMyContactHolder> {
    private final List<Poll> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;

    private int loggedInId;

    @Override
    public void onViewAttachedToWindow(@NonNull CMyPollsAdapter.CMyContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {

            loadListener.onLoadMore();
        }
    }

    public CMyPollsAdapter(List<Poll> list, Context cntxt, OnLoadMoreListener loadListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public CMyPollsAdapter.CMyContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_poll, parent, false);
        return new CMyPollsAdapter.CMyContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CMyPollsAdapter.CMyContactHolder holder, int position) {

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
            } else {
                holder.ivlock.setVisibility(View.GONE);
            }

//            if(vo.isclosed()=""){
//                holder.ivlock.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.ivlock.setVisibility(View.GONE);
//            }

            holder.tvViews1.setText("" + vo.getVoteCount() + " votes, " + "" + vo.getViewCount() + " views.");
            holder.tvDate.setText(Util.changeDateFormat(context, vo.getCreationDate()));
            holder.tvOwner.setText(vo.getOwnerTitle());
            Util.showImageWithGlide(holder.ivImage, vo.getBrImages().getMain(), context, R.drawable.placeholder_square);
            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
//            holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.get(), listener));
//            holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, holder.ivOption, holder.getAdapterPosition()));
            holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus(), listener));
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

    public class CMyContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvDesc;
        protected TextView tvStats;
        protected TextView tvOwner;
        protected TextView tvDate;
        protected TextView tvViews1;
        //        protected View ivOption;
        protected ImageView ivlock;
        protected View rlMain;
        protected ImageView ivOption;
        protected ImageView ivImage;

        public CMyContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                tvTitle = itemView.findViewById(R.id.tvTitlepoll);
                tvOwner = itemView.findViewById(R.id.tvOwnerpoll);
                tvDate = itemView.findViewById(R.id.tvDate5);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                ivlock = itemView.findViewById(R.id.lock);
                rlMain = itemView.findViewById(R.id.rlMain);
                tvViews1 = itemView.findViewById(R.id.tvViews1);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}





