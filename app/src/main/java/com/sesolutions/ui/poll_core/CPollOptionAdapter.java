package com.sesolutions.ui.poll_core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class CPollOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PollOption> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int TYPE_TEXT = 0;
    private final int TYPE_TEXT_RESULT = 1;
    private final int text_1;
    private final int cGreen;
    private final Typeface iconFont;
    public FragmentManager fragmentManager;
    private final int TYPE_IMAGE = 2;
    private final int TYPE_IMAGE_RESULT = 3;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final int foregroundColor;
    private int loggedinId;
    private final int parentPosition;
    private boolean isShowingQuestion;
    private Poll poll;

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public CPollOptionAdapter(List<PollOption> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int parentPosition) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        text_1 = Color.parseColor(Constant.text_color_1);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
        cGreen = ContextCompat.getColor(context, R.color.contest_vote);
        themeManager = new ThemeManager();

        //variable used to identify feed item position
        this.parentPosition = parentPosition;

    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).getImageType() != 0) ? (!isShowingQuestion /*&& poll.hasVoted()*/ ? TYPE_IMAGE_RESULT : TYPE_IMAGE) : (!isShowingQuestion /*&& poll.hasVoted()*/ ? TYPE_TEXT_RESULT : TYPE_TEXT);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                View view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_option_image_feed : R.layout.item_poll_option_image_feed_basic, parent, false);
                Util.showSnackbar(view, "sorry");
            case TYPE_IMAGE_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_result_image_basic : R.layout.item_poll_result_image_feed_basic, parent, false);
                Util.showSnackbar(view, "sorry");
            case TYPE_TEXT_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_result_text_basic, parent, false);
                return new ResultHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_option_basic, parent, false);
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
                    if (poll.isUserVotedThisOption(vo.getPollOptionId())) {

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
                    holder2.tvPercentage.setText(vo.getVotePercent());
                    holder2.sbProgress.setProgress(vo.getProgress(poll.getVoteCount()));
                    if (null != vo.getVotedUser()) {
                        holder2.rlProfiles.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(holder2.ivProfile1, vo.getVotedUser().get(0).getUserImage(), context);
                        if (vo.canShowVotedUserImage(1)) {
                            holder2.ivProfile2.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder2.ivProfile2, vo.getVotedUser().get(1).getUserImage(), context);
                        } else {
                            holder2.ivProfile2.setVisibility(View.GONE);
                        }

                        if (vo.canShowVotedUserImage(3)) {
                            holder2.ivProfile3.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder2.ivProfile3, vo.getVotedUser().get(2).getUserImage(), context);
                        } else {
                            holder2.ivProfile3.setVisibility(View.GONE);
                        }

                        if (vo.canShowVotedUserImage(4)) {
                            holder2.ivProfile4.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder2.ivProfile4, vo.getVotedUser().get(3).getUserImage(), context);
                        } else {
                            holder2.ivProfile4.setVisibility(View.GONE);
                        }
                    } else {
                        holder2.rlProfiles.setVisibility(View.GONE);
                    }

                    holder2.ivProfile5.setVisibility(vo.isMoreUserLink() ? View.VISIBLE : View.GONE);
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
                rlProfiles.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MORE_MEMBER, parentPosition, getAdapterPosition()));
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
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();

                sbvVote.setOnClickListener(v -> {
                    if (SPref.getInstance().isLoggedIn(context)) {
                        if (poll.isclosed() == 0) {
                            if (poll.canChangevotes() || !poll.hasVoted()) {
//                                listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
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
                                                        listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                                    }
                                                });
                                                circularReveal.start();
                                                Util.showSnackbar(v, "Successfully Voted.");
                                            } catch (Exception e) {
                                                CustomLog.e(e);
                                                cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                                                listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                            }
                                        } else {
                                            cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                                            // tvTitle.setTextColor(Color.WHITE);
                                            listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                        }
                                    }
                                });
                            } else {
                                Util.showSnackbar(v, "You don't have permission to change your vote..");
                            }
                        } else {
                            Util.showSnackbar(cvVote, "This Poll is Closed.");
                        }
                    } else {

                        Util.showSnackbar(v, "You need to login first..");
                    }
                });
//                );
            } catch (Exception e) {
                // CustomLog.e(e);
            }
        }

    }
}
