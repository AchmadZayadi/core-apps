package com.sesolutions.ui.qna;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import java.util.List;


public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ContactHolder> {

    private final List<Question> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    private final int cPrimary, cText2;
    private Drawable dStarSelected, dStar;
    private final int loggedInId;


    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }

    protected AnswerAdapter(List<Question> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        cPrimary = SesColorUtils.getPrimaryColor(context);
        cText2 = SesColorUtils.getText2Color(context);
        themeManager = new ThemeManager();
        loggedInId = SPref.getInstance().getLoggedInUserId(context);
        dStar = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        dStar.setColorFilter(new PorterDuffColorFilter(SesColorUtils.getText2Color(cntxt), PorterDuff.Mode.MULTIPLY));
        dStarSelected = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarSelected.setColorFilter(new PorterDuffColorFilter(SesColorUtils.getPrimaryColor(cntxt), PorterDuff.Mode.MULTIPLY));

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qna_answer, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            final Question vo = list.get(position);

            holder.tvVoteCount.setText(vo.getVoteCount());

            // holder.tvTotalVote.setText(context.getResources().getQuantityString(R.plurals.vote_count, vo.getTotalVote(), vo.getTotalVote()));
            // holder.tvAnswerCount.setText(context.getResources().getQuantityString(R.plurals.answers_count, vo.getAnswerCount(), vo.getAnswerCount()));
            // holder.tvViewCount.setText(context.getResources().getQuantityString(R.plurals.views_count, vo.getViewCount(), vo.getViewCount()));

            /*if (!TextUtils.isEmpty(vo.getLocation())) {
                holder.ivLocation.setTypeface(iconFont);
                holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);
                holder.tvLocation.setText(vo.getLocation());
                holder.tvLocation.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_LOCATION, null, holder.getAdapterPosition()));
                holder.llLocation.setVisibility(View.VISIBLE);
            } else {
                holder.llLocation.setVisibility(View.GONE);
            }*/

            holder.tvOwnerTitle.setText(vo.getOwnerTitle());
            holder.tvVoteCount.setText(vo.getVoteCount());
            holder.nwv.loadData(vo.getDescription(), null, null);
            Util.showImageWithGlide(holder.ivOwnerImage, vo.getOwnerImage(), context, R.drawable.placeholder_square);
            holder.tvDate.setText(Util.getDateDiff(context, vo.getCreationDate()));
            holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
            holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions()));

            holder.rlOwnerInfo.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, null, vo.getOwnerId()));
            holder.ivVoteUp.setColorFilter(vo.hasVoted(Constant.KEY_UP_VOTED) ? cPrimary : cText2);
            holder.ivVoteDown.setColorFilter(vo.hasVoted(Constant.KEY_DOWN_VOTED) ? cPrimary : cText2);
            if (vo.canChooseBestAnswer(loggedInId) || vo.getBestAnswer() == 1) {
                holder.sbvStar.setVisibility(View.VISIBLE);
                if (vo.isShowAnimation() == 1) {
                    vo.setShowAnimation(0);
                    holder.sbvStar.likeAnimation();
                    holder.ivStar.setImageDrawable(vo.getBestAnswer() == 1 ? dStarSelected : dStar);
                } else {
                    holder.ivStar.setImageDrawable(vo.getBestAnswer() == 1 ? dStarSelected : dStar);
                }
            } else {
                holder.sbvStar.setVisibility(View.GONE);
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

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected NestedWebView nwv;
        protected final TextView tvVoteCount, tvOwnerTitle, tvDate;
        //protected final TextView , tvViewCount, tvAnswerCount, tvTotalVote;
        protected View llMain, rlOwnerInfo;
        protected ImageView ivOwnerImage;
        protected View ivOption;
        private SmallBangView sbvStar;

        protected final ImageView /*ivLike, ivFavorite, ivFollow,*/ ivVoteUp, ivVoteDown, ivStar;

        public ContactHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, context);
            nwv = itemView.findViewById(R.id.nwv);
            //nwv.loadData("Lorem ipsum is lorem ipsum ,nonr of your ipsum Lorem ipsum is lorem ipsum ,nonr of your ipsum", null, null);
            tvOwnerTitle = itemView.findViewById(R.id.tvOwnerTitle);
            ivOwnerImage = itemView.findViewById(R.id.ivOwnerImage);
            ivOption = itemView.findViewById(R.id.ivOption);
            ivVoteUp = itemView.findViewById(R.id.ivVoteUp);
            ivVoteDown = itemView.findViewById(R.id.ivVoteDown);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
            tvDate = itemView.findViewById(R.id.tvDate);
            rlOwnerInfo = itemView.findViewById(R.id.rlOwnerInfo);
            ivStar = itemView.findViewById(R.id.ivStar);
            sbvStar = itemView.findViewById(R.id.sbvStar);
            itemView.findViewById(R.id.tvAddComment).setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.COMMENT, null, getAdapterPosition());
            });

            sbvStar.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.ACCEPT, null, getAdapterPosition());
            });
            ivVoteUp.setOnClickListener(v -> {
               /* ivVoteUp.setColorFilter(SesColorUtils.getPrimaryColor(context));
                ivVoteDown.setColorFilter(SesColorUtils.getText2Color(context));*/
                listener.onItemClicked(Constant.Events.VOTE, Constant.KEY_UP_VOTED, getAdapterPosition());
            });

            ivVoteDown.setOnClickListener(v -> {
               /* ivVoteDown.setColorFilter(SesColorUtils.getPrimaryColor(context));
                ivVoteUp.setColorFilter(SesColorUtils.getText2Color(context));*/
                listener.onItemClicked(Constant.Events.VOTE, Constant.KEY_DOWN_VOTED, getAdapterPosition());
            });

        }
    }

}
