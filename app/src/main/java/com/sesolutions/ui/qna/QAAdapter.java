package com.sesolutions.ui.qna;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.responses.qna.QuestionVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.qna.holders.SuggestionHolder;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.SpanUtil;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

public class QAAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<QuestionVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final LayoutInflater inflater;
    private String type;
    private final boolean canShowReaction;
    private final int cPrimary, cText2;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }

    protected QAAdapter(List<QuestionVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean canShowReaction) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.inflater = LayoutInflater.from(context);
        cPrimary = SesColorUtils.getPrimaryColor(context);
        cText2 = SesColorUtils.getText2Color(context);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        this.canShowReaction = canShowReaction && SPref.getInstance().isLoggedIn(context);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLike.setColorFilter(new PorterDuffColorFilter(SesColorUtils.getText2Color(context), PorterDuff.Mode.MULTIPLY));
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFav.setColorFilter(new PorterDuffColorFilter(SesColorUtils.getText2Color(context), PorterDuff.Mode.MULTIPLY));
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollow.setColorFilter(new PorterDuffColorFilter(SesColorUtils.getText2Color(context), PorterDuff.Mode.MULTIPLY));
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
            case VT_CATEGORY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                return new CategoryHolder(view);
            /*case VT_SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);*/

            case VT_CATEGORIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qna, parent, false);
                return new ContactHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        try {
            switch (list.get(position).getType()) {
                case VT_CATEGORY:
                    final CategoryHolder holder1 = (CategoryHolder) parentHolder;
                    if (holder1.adapter == null) {
                        /*set child item list*/
                        holder1.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder1.rvChild.setLayoutManager(layoutManager);
                        holder1.adapter = new ContestCategoryAdapter((List<CategoryPage>) list.get(holder1.getAdapterPosition()).getContent(), context, listener);
                        holder1.rvChild.setAdapter(holder1.adapter);
                    } else {
                        holder1.adapter.notifyDataSetChanged();
                    }
                    break;

                case VT_CATEGORIES:

                    final SuggestionHolder holder3 = (SuggestionHolder) parentHolder;
                    themeManager.applyTheme((ViewGroup) holder3.itemView, context);
                    //final PageVo pageVo = list.get(position);
                    final CategoryPage cVo = list.get(position).getContent();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionQAAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }

                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));
                    break;
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final Question vo = list.get(position).getContent();

                    holder.tvStats.setTypeface(iconFont);
                    String detail = "\uf164 " + vo.getLikeCount()
                            + "  \uf075 " + vo.getCommentCount()
                            + "  \uf06e " + vo.getViewCount()
                            + "  \uf004 " + vo.getFavouriteCount()
                            + "  \uf00c " + vo.getFollowCount()
                            + "  \uf0a6 " + vo.getTotalVote();
                    holder.tvStats.setText(detail);
                    holder.tvVoteCount.setText(vo.getVoteCount());

                    holder.tvTotalVote.setText(context.getResources().getQuantityString(R.plurals.vote_count, vo.getTotalVoteCount(), vo.getTotalVoteCount()));
                    holder.tvAnswerCount.setText(context.getResources().getQuantityString(R.plurals.answers_count, vo.getAnswerCount(), vo.getAnswerCount()));
                    holder.tvViewCount.setText(context.getResources().getQuantityString(R.plurals.views_count, vo.getViewCount(), vo.getViewCount()));


                    holder.tvTitle.setText(vo.getTitle());
                    holder.tvQDescription.setText(SpanUtil.getHtmlString(vo.getDescription()));
                    holder.tvOwner.setText(vo.getOwnerText(context));
                    // Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);

                    holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                    holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions()));

                    holder.llReactionOption.setVisibility(canShowReaction ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFollow.setVisibility(vo.canFollow() ? View.VISIBLE : View.INVISIBLE);
                    SpanUtil.createHashTagView(inflater, holder.flTags, vo.getTag(), listener);

                    if (null != vo.getLocation()) {
                        holder.llLocation.setVisibility(View.VISIBLE);
                        holder.tvLocation.setText(vo.getLocation());
                    } else {
                        holder.llLocation.setVisibility(View.GONE);
                    }

                    if (vo.isShowAnimation() == 1) {
                        vo.setShowAnimation(0);
                        holder.sbvLike.likeAnimation();
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    }

                    if (vo.isShowAnimation() == 2) {
                        vo.setShowAnimation(0);
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                        holder.sbvFavorite.likeAnimation();
                    } else {
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                    }

                    if (vo.isShowAnimation() == 3) {
                        vo.setShowAnimation(0);
                        holder.sbvFollow.likeAnimation();
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    } else {
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    }

                    holder.ivVoteUp.setColorFilter(vo.hasVoted(Constant.KEY_UP_VOTED) ? cPrimary : cText2);
                    holder.ivVoteDown.setColorFilter(vo.hasVoted(Constant.KEY_DOWN_VOTED) ? cPrimary : cText2);
                    holder.llPoll.setVisibility(null != vo.getPollLabel() ? View.VISIBLE : View.GONE);
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
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

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvQDescription;
        protected TextView tvStats;
        protected final TextView tvOwner, tvViewCount, tvAnswerCount, tvVoteCount, tvTotalVote;
        protected FlowLayout flTags;
        protected TextView tvLocation, ivLocation;
        protected View llLocation;
        protected View llMain, llPoll;
        protected View ivOption;
        protected View llReactionOption;
        protected final ImageView ivLike, ivFavorite, ivFollow, ivVoteUp, ivVoteDown;

        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;

        public ContactHolder(View itemView) {
            super(itemView);

            themeManager.applyTheme((ViewGroup) itemView, context);
            tvStats = itemView.findViewById(R.id.tvStats);
            llMain = itemView.findViewById(R.id.llMain);
            tvTitle = itemView.findViewById(R.id.tvQTitle);
            tvQDescription = itemView.findViewById(R.id.tvQDescription);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            flTags = itemView.findViewById(R.id.flTags);
            tvTotalVote = itemView.findViewById(R.id.tvTotalVote);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
            tvAnswerCount = itemView.findViewById(R.id.tvAnswerCount);
            tvViewCount = itemView.findViewById(R.id.tvViewCount);
            ivVoteUp = itemView.findViewById(R.id.ivVoteUp);
            ivVoteDown = itemView.findViewById(R.id.ivVoteDown);
            llPoll = itemView.findViewById(R.id.llPoll);
            llLocation = itemView.findViewById(R.id.llLocation);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivLocation = itemView.findViewById(R.id.ivLocation);
            ivLocation.setTypeface(iconFont);
            ivLocation.setText(Constant.FontIcon.MAP_MARKER);

                /*ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                llLocation = itemView.findViewById(R.id.llLocation);*/

            ivOption = itemView.findViewById(R.id.ivOption);
            llReactionOption = itemView.findViewById(R.id.llReactionOption);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            ivFollow = itemView.findViewById(R.id.ivFollow);
            sbvLike = itemView.findViewById(R.id.sbvLike);
            sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
            sbvFollow = itemView.findViewById(R.id.sbvFollow);

            ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, getAdapterPosition()));
            ivFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, null, getAdapterPosition()));
            ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, null, getAdapterPosition()));
            llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, null, getAdapterPosition()));

            ivVoteUp.setOnClickListener(v -> {
                if(SPref.getInstance().isLoggedIn(context)){
                    listener.onItemClicked(Constant.Events.VOTE, Constant.KEY_UP_VOTED, getAdapterPosition());
                }else {
                    Intent intent = new Intent(context, WelcomeActivity.class);
                    intent.putExtra(Constant.KEY_TYPE, 1);
                    context.startActivity(intent);
                }


                // ivVoteUp.setColorFilter(SesColorUtils.getPrimaryColor(context));
                // ivVoteDown.setColorFilter(SesColorUtils.getText2Color(context));
            });

            ivVoteDown.setOnClickListener(v -> {
                if(SPref.getInstance().isLoggedIn(context)){
                    listener.onItemClicked(Constant.Events.VOTE, Constant.KEY_DOWN_VOTED, getAdapterPosition());
                }else {
                    Intent intent = new Intent(context, WelcomeActivity.class);
                    intent.putExtra(Constant.KEY_TYPE, 1);
                    context.startActivity(intent);
                }

                //ivVoteDown.setColorFilter(SesColorUtils.getPrimaryColor(context));
                //ivVoteUp.setColorFilter(SesColorUtils.getText2Color(context));

            });


        }
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected ContestCategoryAdapter adapter;
        //protected Handler handler;
        // public Runnable runnable;

        public CategoryHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

}
