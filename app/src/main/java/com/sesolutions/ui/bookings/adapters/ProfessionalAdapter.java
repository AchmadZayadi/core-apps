package com.sesolutions.ui.bookings.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Bookings.ProfessionalContent;
import com.sesolutions.responses.Bookings.ProfessionalVo;
import com.sesolutions.responses.Courses.classroom.ClassroomContent;
import com.sesolutions.responses.Courses.classroom.ClassroomVo;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

import static com.sesolutions.ui.page.PageFragment.TYPE_CATEGORY;
import static com.sesolutions.ui.page.PageFragment.TYPE_MANAGE;


public class ProfessionalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ProfessionalVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_HOT = "-1";
    public final String VT_FEATURED = "-4";
    public final String VT_VERIFIED = "-5";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final String TXT_BY;
    private final String TXT_IN;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public ProfessionalAdapter(List<ProfessionalVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);
        themeManager = new ThemeManager();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (list.get(viewType).getType()) {


            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_professional, parent, false);
                return new ContactHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);

        try {
            switch (list.get(position).getType()) {
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final ProfessionalVo page = list.get(position);
                    final ProfessionalContent vo = page.getValue();

                    holder.tvName.setText(vo.getName());
                    if (vo.getDesignation() != null) {
                        holder.tvJob.setText(vo.getDesignation());
                    } else {
                        holder.tvJob.setVisibility(View.GONE);
                    }
                    Util.showImageWithGlide(holder.ivImage, vo.getProfessional_image(), context, R.drawable.placeholder_square);
                    holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VIEW_PROFESSIONAL, holder, holder.getAdapterPosition()));

                    if(vo.getAvailable() == 0){
                        holder.tvBook.setVisibility(View.GONE);
                    }
                    if(vo.getAvailable()>0) {
                        holder.tvBook.setVisibility(View.VISIBLE);
                        holder.tvBook.setOnClickListener(v -> listener.onItemClicked(Constant.Events.OPEN_PRO_BOOK, holder, holder.getAdapterPosition()));
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View cvMain;
        protected TextView tvName;
        protected TextView tvJob;
        protected TextView tvBook;
        protected ImageView ivImage;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvName = itemView.findViewById(R.id.tvName);
                tvBook = itemView.findViewById(R.id.tvBook);
                tvJob = itemView.findViewById(R.id.tvJob);
                ivImage = itemView.findViewById(R.id.ivImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}

