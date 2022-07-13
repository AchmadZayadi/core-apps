package com.sesolutions.ui.qna;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class QuestionPollOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PollOption> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int TYPE_TEXT = 0;
    private final int TYPE_TEXT_RESULT = 1;
    private final int TYPE_IMAGE = 2;
    private final int TYPE_IMAGE_RESULT = 3;
    private final int text_1;
    private final int cGreen;
    private final Typeface iconFont;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final int foregroundColor;

    private boolean isShowingQuestion;
    private Question poll;

    public void setPoll(Question poll) {
        this.poll = poll;
    }

    public QuestionPollOptionAdapter(List<PollOption> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int parentPosition) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        text_1 = Color.parseColor(Constant.text_color_1);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
        cGreen = ContextCompat.getColor(context, R.color.contest_vote);
        themeManager = new ThemeManager();

    }


    @Override
    public int getItemViewType(int position) {
        return (!isShowingQuestion ? TYPE_TEXT_RESULT : TYPE_TEXT);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
           /* case TYPE_IMAGE:
                View view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_option_image : R.layout.item_poll_option_image_feed, parent, false);
                return new OptionImageHolder(view);
            case TYPE_IMAGE_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_result_image : R.layout.item_poll_result_image_feed, parent, false);
                return new ResultImageHolder(view);*/
            case TYPE_TEXT_RESULT:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_result_text, parent, false);
                return new ResultHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_option, parent, false);
                return new OptionHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        try {

            final PollOption vo = list.get(position);
            switch (holder.getItemViewType()) {
                case TYPE_TEXT:
                    OptionHolder holder1 = (OptionHolder) holder;
                    holder1.tvTitle.setText(vo.getPollOption());
                    // if (vo.hasVoted()) {
                    if (poll.hasUserVotedThisOption(vo.getPollOptionId())) {
                        holder1.tvTitle.setTextColor(foregroundColor);
                        holder1.cvMain.setCardBackgroundColor(cGreen);
                        holder1.cvVote.setCardBackgroundColor(foregroundColor);
                        holder1.ivVote.setColorFilter(cGreen);

                    } else {
                        holder1.tvTitle.setTextColor(text_1);
                        holder1.cvMain.setCardBackgroundColor(foregroundColor);
                        holder1.cvVote.setCardBackgroundColor(cGreen);
                        holder1.ivVote.setColorFilter(foregroundColor);
                    }

                    break;
                case TYPE_TEXT_RESULT:
                    ResultHolder holder2 = (ResultHolder) holder;
                    holder2.tvTitle.setText(vo.getPollOption());
                    holder2.tvPercentage.setText(context.getString(R.string.vote_percent_with_count,vo.getVotePercent(),vo.getVotes()));
                    holder2.sbProgress.setProgress(vo.getProgress(poll.getOptionVoteCount()));
                    holder2.rlProfiles.setVisibility(View.GONE);
                    holder2.ivProfile5.setVisibility(View.GONE);
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

    public void showQuestion(boolean isShowingQuestion) {
        this.isShowingQuestion = isShowingQuestion;
    }


    public class ResultHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvPercentage;
        protected View rlProfiles;
        protected ImageView ivProfile1;
        protected ImageView ivProfile2;
        protected ImageView ivProfile3;
        protected ImageView ivProfile4;
        protected ImageView ivProfile5;
        protected CardView cvMain;
        protected ProgressBar sbProgress;


        public ResultHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                sbProgress = itemView.findViewById(R.id.sbProgress);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPercentage = itemView.findViewById(R.id.tvPercentage);
                rlProfiles = itemView.findViewById(R.id.rlProfiles);
                ivProfile1 = itemView.findViewById(R.id.ivProfile1);
                ivProfile2 = itemView.findViewById(R.id.ivProfile2);
                ivProfile3 = itemView.findViewById(R.id.ivProfile3);
                ivProfile4 = itemView.findViewById(R.id.ivProfile4);
                ivProfile5 = itemView.findViewById(R.id.ivProfile5);
                //rlProfiles.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MORE_MEMBER, null, getAdapterPosition()));
            } catch (Exception e) {
                // CustomLog.e(e);
            }
        }
    }


    public class OptionHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView ivVote;
        protected SmallBangView sbvVote;
        protected CardView cvVote;
        protected CardView cvMain;

        public OptionHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                sbvVote = itemView.findViewById(R.id.sbvVote);
                cvVote = itemView.findViewById(R.id.cvVote);
                ivVote = itemView.findViewById(R.id.ivVote);


                sbvVote.setOnClickListener(v -> {
                    if (poll.canVotePoll()) {
                        //cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                        sbvVote.likeAnimation(new AnimationAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cvVote.setCardBackgroundColor(cGreen);
                                ivVote.setColorFilter(foregroundColor);
                                cvMain.setCardBackgroundColor(cGreen);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cvVote.setCardBackgroundColor(foregroundColor);
                                ivVote.setColorFilter(cGreen);
                                cvMain.setCardBackgroundColor(cGreen);
                                tvTitle.setTextColor(Color.parseColor(Constant.foregroundColor));
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                                    try {
                                        int revealX = (int) (sbvVote.getX() + sbvVote.getWidth() / 2);
                                        int revealY = (int) (sbvVote.getY() + sbvVote.getHeight() / 2);
                                        // int width = sbvVote.getWidth();
                                        //int height = view.getHeight();
                                        //double startRadius = Math.sqrt((width * width) + (height * height));
                                        float startRadius = (cvVote.getWidth()) / 2;
                                        float finalRadius = (float) (Math.max(cvMain.getWidth(), cvMain.getHeight()) * 1.1);

                                        Animator circularReveal = ViewAnimationUtils.createCircularReveal(cvMain, revealX, revealY, startRadius, finalRadius);

                                        circularReveal.setDuration(800);
                                        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
                                        circularReveal.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                                                //tvTitle.setTextColor(Color.WHITE);
                                                listener.onItemClicked(Constant.Events.VOTE, null, getAdapterPosition());
                                            }
                                        });
                                        circularReveal.start();
                                    } catch (Exception e) {
                                        CustomLog.e(e);
                                        cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                                        listener.onItemClicked(Constant.Events.VOTE, null, getAdapterPosition());
                                    }
                                } else {
                                    cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                                    // tvTitle.setTextColor(Color.WHITE);
                                    listener.onItemClicked(Constant.Events.VOTE, null, getAdapterPosition());
                                }
                            }
                        });
                    } else {
                        Util.showSnackbar(cvVote, context.getString(R.string.msg_cannot_vote));
                    }
                });

            } catch (Exception e) {
                // CustomLog.e(e);
            }
        }

    }
}
