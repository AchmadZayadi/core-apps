package com.sesolutions.ui.qna;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import java.util.List;


public class SuggestionQAAdapter extends RecyclerView.Adapter<SuggestionQAAdapter.ContactHolder> {

    private final List<Question> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
 //   private final boolean isRecent;
    private final Typeface iconFont;
    private final LayoutInflater inflater;

    public SuggestionQAAdapter(List<Question> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isRecent) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
       // this.isRecent = isRecent;
        themeManager = new ThemeManager();
        TXT_BY = context.getString(R.string.TXT_BY);
        TXT_IN = context.getString(R.string.IN_);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(/*isRecent ? R.layout.item_page_recent :*/ R.layout.item_suggestion_qa, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Question vo = list.get(position);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(context.getString(R.string.by_owner,vo.getOwnerTitle()));//TXT_BY + vo.getOwnerTitle());
            holder.tvArtist.setVisibility(null != vo.getOwnerTitle() ? View.VISIBLE : View.GONE);
            holder.tvCategory.setText(TXT_IN + vo.getCategoryTitle());
            Util.showImageWithGlide(holder.ivUser, vo.getOwnerImage(), context, R.drawable.placeholder_square);


            holder.tvStats.setTypeface(iconFont);
            String detail = "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    //+ "  \uf06e " + vo.getView_count()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf00c " + vo.getFollowCount();
            //   + "  \uf0c0 " + vo.getMember_count();
            holder.tvStats.setText(detail);
            holder.tvVoteCount.setText(vo.getVoteCount());
            SpanUtil.createHashTagView(inflater, holder.flTags, vo.getTag(), listener);
            //holder.tvTotalVote.setText(context.getResources().getQuantityString(R.plurals.vote_count, vo.getTotalVote(), vo.getTotalVote()));
            // holder.tvAnswerCount.setText(context.getResources().getQuantityString(R.plurals.answers_count, vo.getAnswerCount(), vo.getAnswerCount()));
            // holder.tvViewCount.setText(context.getResources().getQuantityString(R.plurals.views_count, vo.getViewCount(), vo.getViewCount()));


            holder.tvTitle.setText(vo.getTitle());
            holder.tvQDescription.setText(vo.getDescription());
            // holder.tvOwner.setText(vo.getOwnerText(context));

            holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PAGE_SUGGESTION_MAIN, "" + holder, vo.getQuestionId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
        //return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist;
        protected TextView tvCategory;
        protected View rlMain;
        protected ImageView ivUser;


        protected TextView tvQDescription;
        protected TextView tvStats;
        protected ImageView ivVoteUp, ivVoteDown;
        protected TextView tvVoteCount;
        // protected final TextView  tvAnswerCount, tvVoteCount, tvTotalVote;
        protected FlowLayout flTags;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                rlMain = itemView.findViewById(R.id.rlMain);
                // tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                ivUser = itemView.findViewById(R.id.ivUser);


                tvStats = itemView.findViewById(R.id.tvStats);
                //llMain = itemView.findViewById(R.id.llMain);
                tvTitle = itemView.findViewById(R.id.tvQTitle);
                tvQDescription = itemView.findViewById(R.id.tvQDescription);
                // tvOwner = itemView.findViewById(R.id.tvOwner);
                flTags = itemView.findViewById(R.id.flTags);
                //tvTotalVote = itemView.findViewById(R.id.tvTotalVote);
                tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
                //tvAnswerCount = itemView.findViewById(R.id.tvAnswerCount);
                //tvViewCount = itemView.findViewById(R.id.tvViewCount);
                ivVoteUp = itemView.findViewById(R.id.ivVoteUp);
                ivVoteDown = itemView.findViewById(R.id.ivVoteDown);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
