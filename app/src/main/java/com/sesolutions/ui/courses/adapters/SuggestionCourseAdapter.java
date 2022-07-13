package com.sesolutions.ui.courses.adapters;

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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.course.CourseContent;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

import jp.shts.android.library.TriangleLabelView;


public class SuggestionCourseAdapter extends RecyclerView.Adapter<SuggestionCourseAdapter.ContactHolder> {

    private final List<CourseContent> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isRecent;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    public SuggestionCourseAdapter(List<CourseContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isRecent) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isRecent = isRecent;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isRecent ? R.layout.item_page_recent : R.layout.item_course_suggestion, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final CourseContent vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(TXT_BY + vo.getOwner_title());
            holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
            holder.tvCategory.setVisibility(null != vo.getCategory_title() ? View.VISIBLE : View.GONE);
            holder.tvCategory.setText(TXT_IN + vo.getCategory_title());
//            Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.tvType.setText(vo.getCurrency() + " " + vo.getPrice());
            if(vo.getPrice().equalsIgnoreCase("0")){
                holder.tvType.setVisibility(View.GONE);
            }else {
                holder.tvType.setVisibility(View.VISIBLE);
            }
            holder.Dprice.setText(vo.getDiscountedPrice());
            holder.Dprice.setVisibility(!TextUtils.isEmpty(vo.getDiscountedPrice()) ? View.VISIBLE : View.GONE);
            holder.tvDiscount.setText(vo.getDiscount());
            holder.tvDiscount.setVisibility(!TextUtils.isEmpty(vo.getDiscount()) ? View.GONE : View.GONE);
            Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

            if (!TextUtils.isEmpty(vo.getDiscount())) {
                holder.percantageoffdata.setPrimaryText(""+vo.getDiscount());
                holder.percantageoffdata.setVisibility(View.VISIBLE);
            } else {
                holder.percantageoffdata.setVisibility(View.GONE);
            }

            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PAGE_SUGGESTION_MAIN, "" + holder, vo.getCourse_id()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist;
        protected TextView tvCategory;
        protected TextView Dprice;
        protected TextView tvType;
        protected TextView tvDiscount;
        protected View rlMain;
        protected ImageView ivImage;
        protected ImageView ivUser;
        TriangleLabelView percantageoffdata;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvDiscount = itemView.findViewById(R.id.tvDiscount);
                percantageoffdata = itemView.findViewById(R.id.percantageoffdata);
                Dprice = itemView.findViewById(R.id.Dprice);
                tvType = itemView.findViewById(R.id.tvType);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
