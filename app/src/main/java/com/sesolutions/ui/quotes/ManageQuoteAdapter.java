package com.sesolutions.ui.quotes;

import android.content.Context;
import android.graphics.Typeface;
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
import com.sesolutions.responses.quote.Quote;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class ManageQuoteAdapter extends RecyclerView.Adapter<ManageQuoteAdapter.ContactHolder> {

    private final List<Quote> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final ThemeManager themeManager;


    @Override
    public void onViewAttachedToWindow(ManageQuoteAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public ManageQuoteAdapter(List<Quote> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        // dLike = ContextCompat.getDrawable(context, R.drawable.like_quote);
        // dLikeSelected = ContextCompat.getDrawable(context, R.drawable.like_active_quote);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        //  dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_quotes, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Quote vo = list.get(position);

            holder.tvStats.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf06e " + vo.getViewCount();


            holder.tvStats.setText(detail);

            if (SCREEN_TYPE == Constant.FormType.CREATE_THOUGHT) {
                holder.tvTitle.setText(vo.getThoughtTitle());
            } else if (SCREEN_TYPE == Constant.FormType.CREATE_PRAYER) {
                holder.tvTitle.setText(vo.getPrayerTitle());
            } else if (SCREEN_TYPE == Constant.FormType.CREATE_WISH) {
                holder.tvTitle.setText(vo.getWishTitle());
            } else {
                holder.tvTitle.setText(vo.getQuotetitle());
            }
            holder.tvCategory.setText("- " + vo.getCategoryTitle());

            if (null != vo.getImages()) {
                holder.ivQuoteImage.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(holder.ivQuoteImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            } else {
                holder.ivQuoteImage.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvDesc.setVisibility(View.GONE);
            } else {
                holder.tvDesc.setVisibility(View.VISIBLE);
                holder.tvDesc.setText(vo.getTitle());
            }

            holder.tvCategory.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, vo.getCategoryTitle(), vo.getCategoryId()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus(), listener));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* public SpannableString addClickableTags(List<Friends> tags) {
        String artist = "";
        for (Tags art : tags) {
            artist += " " + art.getTitle();
        }

        SpannableString span = new SpannableString(artist.trim());
        try {
            String s = "";
            for (int i = 0; i < tags.size(); i++) {
                int start = s.length();
                s += "\n" + tags.get(i).getTitle();
                int end = s.length();
                final int index=i;
                //  final int catId = tags.get(i).getTagId();
                //  final String catName = tags.get(i).getTitle();
                span.setSpan(new CustomClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        listener.onItemClicked(catId, catName, index);
                    }
                }, start, end - 1, 0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return span;
    }*/


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View llReactionOption;
        protected TextView tvTitle;
        protected TextView tvDesc;
        protected TextView tvStats;
        protected TextView tvCategory;
        protected ImageView ivQuoteImage;
        protected View ivOption;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivOption = itemView.findViewById(R.id.ivOption);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                ivQuoteImage = itemView.findViewById(R.id.ivQuoteImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
