package com.sesolutions.ui.clickclick;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.listeners.onLoadCommentsListener;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ClickClickCommentAdapter extends RecyclerView.Adapter<ClickClickCommentAdapter.CategoryHolder> {

    private final List<CommentData> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final onLoadCommentsListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;


    @Override
    public void onViewAttachedToWindow(@NonNull CategoryHolder holder) {
        super.onViewAttachedToWindow(holder);
//        if ((list.size()) - 1 == holder.getAdapterPosition()) {
        loadListener.onLoadMoreComments();
//        }
    }

    public ClickClickCommentAdapter(List<CommentData> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, onLoadCommentsListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clickclick_comment, parent, false);
        return new CategoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final CommentData vo = list.get(position);
            if (vo.getIsLike()) {
                holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart));
                holder.ivLike.setColorFilter(Color.parseColor("#ff0099"));
            } else {
                holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite));
                holder.ivLike.setColorFilter(Color.parseColor("#000000"));
            }
            String date = Util.getDateDiff(context, vo.getCreationDate());
            if (vo.getUserImage() != null) {
                holder.tvUsername.setText(vo.getUserTitle());
            } else {
                holder.tvUsername.setVisibility(View.GONE);
            }
            if (vo.getUserImage() != null) {
                Util.showImageWithGlide(holder.ivUser, vo.getUserImage(), context, R.drawable.placeholder_3_2);
            } else {
                holder.ivUser.setVisibility(View.GONE);
            }
            if (vo.getCanDelete()) {
                holder.tvReport.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivDelete.setOnClickListener(v -> listener.onItemClicked(Constant.Events.DELETE_COMMENT, "", holder.getAdapterPosition()));
                holder.tvEdit.setVisibility(View.VISIBLE);
                holder.tvEdit.setOnClickListener(v -> listener.onItemClicked(Constant.Events.TICK_COMMENT_EDIT, "", holder.getAdapterPosition()));

            } else {
                holder.tvReport.setVisibility(View.VISIBLE);
                holder.tvReport.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPORT, "", holder.getAdapterPosition()));
                holder.tvEdit.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
            }
            holder.tvBody.setText(vo.getBody());
            holder.tvDate.setText(date);
            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.LIKE_COMMENT, vo.getIsLike() ? "-1" : "0", holder.getAdapterPosition()));
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class CategoryHolder extends RecyclerView.ViewHolder {

        protected CardView cvMain;
        protected AppCompatTextView tvUsername;
        protected AppCompatTextView tvBody;
        protected AppCompatTextView tvDate;
        protected AppCompatTextView tvReport;

        protected AppCompatImageView ivLike;
        protected AppCompatImageView ivDelete;
        protected AppCompatTextView tvEdit;
        protected CircleImageView ivUser;


        public CategoryHolder(View itemView) {
            super(itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            tvReport = itemView.findViewById(R.id.tvReport);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvEdit = itemView.findViewById(R.id.tvEdit);
        }
    }
}
