package com.sesolutions.ui.quotes;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ContactHolder> {

    private final List<Quote> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    //   private final Drawable addDrawable;
    //  private final Drawable dFavSelected;
    //  private final Drawable dFav;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;


    @Override
    public void onViewAttachedToWindow(QuoteAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public QuoteAdapter(List<Quote> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.like_quote);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.like_active_quote);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        //  dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quotes, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Quote vo = list.get(position);
            holder.tvOwner.setText(vo.getUserTitle());

            Util.showImageWithGlide(holder.ivProfile, vo.getUserImageUrl(), context, R.drawable.placeholder_square);
            holder.ivMediaType.setVisibility(vo.isPhoto() ? View.GONE : View.VISIBLE);

            holder.tvStats.setTypeface(iconFont);
            String detail = Constant.EMPTY;

            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf06e " + vo.getViewCount();

            holder.ivLike.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);

            holder.tvStats.setText(detail);

            if (SCREEN_TYPE == Constant.GoTo.THOUGHT) {
                holder.tvTitle.setText(vo.getThoughtTitle());
            } else if (SCREEN_TYPE == Constant.GoTo.PRAYER) {
                holder.tvTitle.setText(vo.getPrayerTitle());
            } else if (SCREEN_TYPE == Constant.GoTo.WISH) {
                holder.tvTitle.setText(vo.getWishTitle());
            } else {
                holder.tvTitle.setText(vo.getQuotetitle());
            }
            holder.tvCategory.setText("- " + vo.getCategoryTitle());
            holder.tvQuoteBy.setText("- " + vo.getSource());

            holder.tvCategory.setVisibility(TextUtils.isEmpty(vo.getCategoryTitle()) ? View.GONE : View.VISIBLE);
            holder.tvQuoteBy.setVisibility(TextUtils.isEmpty(vo.getSource()) ? View.GONE : View.VISIBLE);
            holder.tvPostedOn.setText(Util.getPostedOnDate(vo.getCreationDate()));

            if (TextUtils.isEmpty(vo.getTitle())) {
                holder.tvDesc.setVisibility(View.GONE);
            } else {
                holder.tvDesc.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder.tvDesc.setText(Html.fromHtml(vo.getTitle(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    holder.tvDesc.setText(Html.fromHtml(vo.getTitle()));
                }
                holder.tvDesc.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (null != vo.getImages()) {
                holder.rlMedia.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(holder.ivImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            } else {
                holder.rlMedia.setVisibility(View.GONE);
            }
            //   holder.fabPlay.setVisibility(View.INVISIBLE);
            //   holder.ivAdd.setVisibility(View.GONE);

            holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            //   holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);

            holder.ivProfile.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.tvOwner.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_TITLE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.tvCategory.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, vo.getCategoryTitle(), vo.getCategoryId()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "" + SCREEN_TYPE, holder.getAdapterPosition()));

            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View llReactionOption;
        protected TextView tvOwner;
        protected TextView tvStats;
        protected TextView tvTitle;
        protected TextView tvDesc;
        protected TextView tvPostedOn;
        protected TextView tvQuoteBy;
        protected TextView tvCategory;
        protected View rlMedia;
        //  protected TextView ivArtist;
        //  protected View llArtist;
        protected ImageView ivImage;
        protected ImageView ivMediaType;
        protected ImageView ivProfile;
        //  protected ImageView ivFavorite;
        //  protected ImageView ivAdd;
        protected ImageView ivLike;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvOwner = itemView.findViewById(R.id.tvOwner);
                ivProfile = itemView.findViewById(R.id.ivProfile);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvPostedOn = itemView.findViewById(R.id.tvPostedOn);
                tvQuoteBy = itemView.findViewById(R.id.tvQuoteBy);
                tvStats = itemView.findViewById(R.id.tvStats);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivMediaType = itemView.findViewById(R.id.ivMediaType);
                ivLike = itemView.findViewById(R.id.ivLike);
                rlMedia = itemView.findViewById(R.id.rlMedia);
                //     ivFavorite = itemView.findViewById(R.id.ivFavorite);
                //       fabPlay = itemView.findViewById(R.id.fabPlay);
                //      ivAdd = itemView.findViewById(R.id.ivAdd);
                // llReactionOption = itemView.findViewById(R.id.llReactionOption);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
