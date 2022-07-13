package com.sesolutions.ui.review;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Review;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class PageReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Review> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    private final Drawable dStarFilled;
    private final Drawable dStarUnFilled;
    private final ThemeManager themeManager;
    private final String resourceType;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public PageReviewAdapter(List<Review> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, String resourceType) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.resourceType = resourceType;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_page, parent, false);
        return new DiscussionHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        try {

            final Review vo = list.get(position);

            final DiscussionHolder holder = (DiscussionHolder) parentHolder;
            if (null != vo.getContent(resourceType)) {
                holder.tvUser.setVisibility(View.VISIBLE);
                holder.tvUser.setText(context.getString(R.string.for_owner_about_time, vo.getContent(resourceType).getTitle(), Util.getDateDiff(context, vo.getCreationDate())));
            } else {
                holder.tvUser.setVisibility(View.GONE);
            }

            holder.tvTitle.setText(vo.getTitle());

            Util.showImageWithGlide(holder.ivImage, vo.getOwnerImage(), context, R.drawable.placeholder_square);

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


    public class DiscussionHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvUser;
        //protected TextView ivUser;
        //protected TextView tvPros;
        //protected TextView tvCons;
        protected TextView tvStats;
        //protected TextView tv1;
        //protected TextView tv2;
        protected TextView tv3;
        protected TextView tvDesc;
        protected TextView tvRecommend;
        protected ImageView ivImage;
        protected ImageView ivStar1;
        protected ImageView ivStar2;
        protected ImageView ivStar3;
        protected ImageView ivStar4;
        protected ImageView ivStar5;
        protected View llMain;
        protected LinearLayoutCompat llStar;


        public DiscussionHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, context);
            try {
                llMain = itemView.findViewById(R.id.llMain);
                tvTitle = itemView.findViewById(R.id.tvItemTitle);
                tvUser = itemView.findViewById(R.id.tvUser);
                // ivUser = itemView.findViewById(R.id.ivUser);
                //tv1 = itemView.findViewById(R.id.tv1);
                //tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                //tvPros = itemView.findViewById(R.id.tvPros);
                //tvCons = itemView.findViewById(R.id.tvCons);
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
                llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, this, getAdapterPosition()));
                ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, this, getAdapterPosition()));
                tvUser.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MENU_MAIN, this, getAdapterPosition()));

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
