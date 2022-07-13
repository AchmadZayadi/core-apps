package com.sesolutions.ui.events;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sesolutions.responses.event.Reviews;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Reviews> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    private final Drawable dStarFilled;
    private final Drawable dStarUnFilled;
    private final ThemeManager themeManager;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public ReviewAdapter(List<Reviews> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new DiscussionHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);
            final Reviews vo = list.get(position);

            final DiscussionHolder holder = (DiscussionHolder) parentHolder;
            if (TextUtils.isEmpty(vo.getViewer_title())) {
                holder.tvUser.setVisibility(View.GONE);
                holder.ivUser.setVisibility(View.GONE);
            } else {
                holder.tvUser.setVisibility(View.VISIBLE);
                holder.ivUser.setVisibility(View.VISIBLE);
                holder.ivUser.setTypeface(iconFont);
                holder.ivUser.setText(Constant.FontIcon.USER);
                holder.tvUser.setText(vo.getViewer_title());
            }

            holder.tvTitle.setText(vo.getTitle());

            Util.showImageWithGlide(holder.ivImage, vo.getImage(), context, R.drawable.placeholder_square);

            if (null != vo.getPros()) {
                holder.tv1.setVisibility(View.VISIBLE);
                holder.tvPros.setVisibility(View.VISIBLE);
                holder.tvPros.setText(vo.getPros());
            } else {
                holder.tv1.setVisibility(View.GONE);
                holder.tvPros.setVisibility(View.GONE);
            }
            if (null != vo.getCons()) {
                holder.tv2.setVisibility(View.VISIBLE);
                holder.tvCons.setVisibility(View.VISIBLE);
                holder.tvCons.setText(vo.getCons());
            } else {
                holder.tv2.setVisibility(View.GONE);
                holder.tvCons.setVisibility(View.GONE);
            }
            holder.tvRecommend.setVisibility(vo.isRecommended() ? View.VISIBLE : View.GONE);
            if (vo.isDescriptionsAvailable()) {
                holder.tv3.setVisibility(View.VISIBLE);
                holder.tvDesc.setVisibility(View.VISIBLE);
                holder.tvDesc.setText(vo.getDescription());
            } else {
                holder.tv3.setVisibility(View.GONE);
                holder.tvDesc.setVisibility(View.GONE);
            }

            String detail = "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf06e " + vo.getViewCount();
            holder.tvStats.setText(detail);
            holder.tvStats.setTypeface(iconFont);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MENU_MAIN, holder, holder.getAdapterPosition()));
            holder.ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

            holder.llStar.setVisibility(View.VISIBLE);
            holder.ivStar1.setImageDrawable(vo.getRating() > 0 ? dStarFilled : dStarUnFilled);
            holder.ivStar2.setImageDrawable(vo.getRating() > 1 ? dStarFilled : dStarUnFilled);
            holder.ivStar3.setImageDrawable(vo.getRating() > 2 ? dStarFilled : dStarUnFilled);
            holder.ivStar4.setImageDrawable(vo.getRating() > 3 ? dStarFilled : dStarUnFilled);
            holder.ivStar5.setImageDrawable(vo.getRating() > 4 ? dStarFilled : dStarUnFilled);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class DiscussionHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvUser;
        protected TextView ivUser;
        protected TextView tvPros;
        protected TextView tvCons;
        protected TextView tvStats;
        protected TextView tv1;
        protected TextView tv2;
        protected TextView tv3;
        protected TextView tvDesc;
        protected TextView tvRecommend;
        protected ImageView ivImage;
        protected ImageView ivStar1;
        protected ImageView ivStar2;
        protected ImageView ivStar3;
        protected ImageView ivStar4;
        protected ImageView ivStar5;
        protected View cvMain;
        protected LinearLayoutCompat llStar;


        public DiscussionHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvItemTitle);
                tvUser = itemView.findViewById(R.id.tvUser);
                ivUser = itemView.findViewById(R.id.ivUser);
                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tvPros = itemView.findViewById(R.id.tvPros);
                tvCons = itemView.findViewById(R.id.tvCons);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvRecommend = itemView.findViewById(R.id.tvRecommend);
                tvStats = itemView.findViewById(R.id.tvStats);

                ivStar1 = itemView.findViewById(R.id.ivStar1);
                ivStar2 = itemView.findViewById(R.id.ivStar2);
                ivStar3 = itemView.findViewById(R.id.ivStar3);
                ivStar4 = itemView.findViewById(R.id.ivStar4);
                ivStar5 = itemView.findViewById(R.id.ivStar5);

                llStar = itemView.findViewById(R.id.llStar);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class TopicHolder extends RecyclerView.ViewHolder {

        protected TextView tvUser;
        protected TextView tvDate;
        protected TextView tvDesc;
        protected ImageView ivUser;
        protected View ivOption;
        protected View cvMain;
        protected View rlUser;


        public TopicHolder(View itemView) {
            super(itemView);
            try {
                rlUser = itemView.findViewById(R.id.rlUser);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivOption = itemView.findViewById(R.id.ivOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
