package com.sesolutions.ui.credit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidninja.imageeditengine.utils.Utility;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.credit.LeaderBoard;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import java.util.List;


public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ContactHolder> {

    private final List<LeaderBoard> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private GradientDrawable shape;


    @Override
    public void onViewAttachedToWindow(@NonNull LeaderboardAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public LeaderboardAdapter(List<LeaderBoard> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        createRoundedFilled();
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            final LeaderBoard eventItem = list.get(position);
            holder.tvCredit.setText(eventItem.getTotalCredit());
            holder.tvBadge.setText(eventItem.getBadgeCount());
            if (holder.getAdapterPosition() > 4) {
                holder.ivRank.setVisibility(View.GONE);
            } else {
                holder.ivRank.setImageDrawable(getRankByPosition(context, holder.getAdapterPosition()));
                holder.ivRank.setVisibility(View.VISIBLE);
            }
            holder.tvName.setText(eventItem.getDisplayname());
            holder.bAdd.setBackgroundDrawable(shape);
            holder.bFollow.setBackgroundDrawable(shape);

            if (null != eventItem.getUser().getMembership()) {
                holder.bAdd.setVisibility(View.VISIBLE);
                holder.bAdd.setText(eventItem.getUser().getMembership().getLabel());
            } else {
                holder.bAdd.setVisibility(View.GONE);
            }

            if (null != eventItem.getUser().getFollow()) {
                holder.bFollow.setVisibility(View.VISIBLE);
                holder.bFollow.setText(eventItem.getUser().getFollow().getText());
            } else {
                holder.bFollow.setVisibility(View.GONE);
            }

            Util.showImageWithGlide(holder.ivImage, eventItem.getUser().getUserImage(), context, R.drawable.placeholder_square);

            holder.bFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_FOLLOW, "", holder.getAdapterPosition()));
            holder.bAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_ADD, "", holder.getAdapterPosition()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private Drawable getRankByPosition(Context context, int position) {
        if (position == 0) return ContextCompat.getDrawable(context, R.drawable.rank_1);
        if (position == 1) return ContextCompat.getDrawable(context, R.drawable.rank_2);
        if (position == 2) return ContextCompat.getDrawable(context, R.drawable.rank_3);
        if (position == 3) return ContextCompat.getDrawable(context, R.drawable.rank_4);
        else return ContextCompat.getDrawable(context, R.drawable.rank_5);
    }

    private void createRoundedFilled() {
        shape = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_holo_border_primary);
        shape.setStroke(Utility.dpToPx(context, 1), SesColorUtils.getPrimaryColor(context));
        //  shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCredit, tvBadge;
        public AppCompatButton bAdd;
        public AppCompatButton bFollow;
        public ImageView ivImage, ivRank;
        public CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivRank = itemView.findViewById(R.id.ivRank);
                bAdd = itemView.findViewById(R.id.bAdd);
                bFollow = itemView.findViewById(R.id.bFollow);
                tvCredit = itemView.findViewById(R.id.tvCredit);
                tvBadge = itemView.findViewById(R.id.tvBadge);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
