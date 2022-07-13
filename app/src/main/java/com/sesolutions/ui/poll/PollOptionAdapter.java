package com.sesolutions.ui.poll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.util.Log;
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


public class PollOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    private final int parentPosition;
    private boolean isShowingQuestion;
    private Poll poll;

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public PollOptionAdapter(List<PollOption> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int parentPosition) {
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
                View view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_option_image_feed : R.layout.item_poll_option_image_feed, parent, false);
                return new OptionImageHolder(view);
            case TYPE_IMAGE_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_result_image_feed : R.layout.item_poll_result_image_feed, parent, false);
                return new ResultImageHolder(view);
            case TYPE_TEXT_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_result_text, parent, false);
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
                    if (poll.isUserVotedThisOption(vo.getPollOptionId())) {
                        holder1.tvTitle.setTextColor(text_1);
                          //holder1.cvMain.setCardBackgroundColor(cGreen);
                         // holder1.cvVote.setCardBackgroundColor(foregroundColor);
                         //   holder1.ivVote.setColorFilter(foregroundColor);
                        holder1.ivVote2.setImageResource(R.drawable.selecttab);
                    } else {
                        holder1.tvTitle.setTextColor(text_1);
                        //  holder1.cvMain.setCardBackgroundColor(foregroundColor);
                        //  holder1.cvVote.setCardBackgroundColor(cGreen);
                       //   holder1.ivVote.setColorFilter(foregroundColor);
                        holder1.ivVote2.setImageResource(R.drawable.unselecttab);
                    }

                    break;
                case TYPE_TEXT_RESULT:
                    ResultHolder holder2 = (ResultHolder) holder;
                    holder2.tvTitle.setText(vo.getPollOption());
                    holder2.sbProgress.setProgress(vo.getProgress(poll.getVoteCount()));



                    try {
                        String currentString =vo.getVotePercent();
                        String separated[] = currentString.split("\\(");
                        holder2.tvvote1.setText(""+separated[0]);
                        holder2.tvPercentage.setText(""+separated[1].replaceAll("\\)",""));
                        holder2.tvPercentage.setTextColor(Color.parseColor("#000000"));
                        holder2.tvvote1.setTextColor(Color.parseColor("#000000"));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }


                    if(position%4==0){
                        Drawable progressDrawable = context.getResources().getDrawable(R.drawable.seekbar_pro4);
                        progressDrawable.setBounds(holder2.sbProgress.getProgressDrawable().getBounds());
                        holder2.sbProgress.setProgressDrawable(progressDrawable);
                    }else if(position%4==1){
                            Drawable progressDrawable = context.getResources().getDrawable(R.drawable.seekbar_pro2);
                            progressDrawable.setBounds(holder2.sbProgress.getProgressDrawable().getBounds());
                            holder2.sbProgress.setProgressDrawable(progressDrawable);
                    }else if(position%4==2){
                        Drawable progressDrawable = context.getResources().getDrawable(R.drawable.seekbar_pro3);
                        progressDrawable.setBounds(holder2.sbProgress.getProgressDrawable().getBounds());
                        holder2.sbProgress.setProgressDrawable(progressDrawable);
                    }else if(position%4==3){
                        Drawable progressDrawable = context.getResources().getDrawable(R.drawable.seekbar_pro1);
                        progressDrawable.setBounds(holder2.sbProgress.getProgressDrawable().getBounds());
                        holder2.sbProgress.setProgressDrawable(progressDrawable);
                    }else {
                        Drawable progressDrawable = context.getResources().getDrawable(R.drawable.seekbar_pro4);
                        progressDrawable.setBounds(holder2.sbProgress.getProgressDrawable().getBounds());
                        holder2.sbProgress.setProgressDrawable(progressDrawable);
                    }

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
                case TYPE_IMAGE:
                    OptionImageHolder holder3 = (OptionImageHolder) holder;
                    holder3.tvTitle.setText(vo.getPollOption());
                    Util.showAnimatedImageWithGlide(holder3.ivImage, vo.getOptionImage(), context);

                    if (poll.isUserVotedThisOption(vo.getPollOptionId())) {
                        //holder3.sbvVote.setVisibility(View.GONE);
                        holder3.tvTitle.setTextColor(text_1);
                      //  holder3.vScrim.setBackground(ContextCompat.getDrawable(context, R.drawable.scrim));
                      //  holder3.cvMain.setCardBackgroundColor(cGreen);
                        holder3.ivVote.setImageResource(R.drawable.selecttab);

                    } else {
                      //  holder3.sbvVote.setVisibility(View.VISIBLE);
                        holder3.tvTitle.setTextColor(text_1);
                      //  holder3.vScrim.setBackground(null);
                        holder3.cvMain.setCardBackgroundColor(foregroundColor);
                        holder3.ivVote.setImageResource(R.drawable.unselecttab);
                    }

                    break;
                case TYPE_IMAGE_RESULT:
                    ResultImageHolder holder4 = (ResultImageHolder) holder;
                    holder4.tvTitle.setText(vo.getPollOption());
                    Util.showAnimatedImageWithGlide(holder4.ivImage, vo.getOptionImage(), context);
                    holder4.sbProgress.setProgress(vo.getProgress(poll.getVoteCount()));
                    holder4.rlProfiles.setVisibility(null != vo.getVotedUser() ? View.VISIBLE : View.GONE);
                    holder4.tvPercentage.setText(vo.getVotePercent());

                    if (null != vo.getVotedUser()) {
                        holder4.rlProfiles.setVisibility(View.VISIBLE);
                        Util.showImageWithGlide(holder4.ivProfile1, vo.getVotedUser().get(0).getUserImage(), context);
                        if (vo.canShowVotedUserImage(1)) {
                            holder4.ivProfile2.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder4.ivProfile2, vo.getVotedUser().get(1).getUserImage(), context);
                        } else {
                            holder4.ivProfile2.setVisibility(View.GONE);
                        }

                        if (vo.canShowVotedUserImage(2)) {
                            holder4.ivProfile3.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder4.ivProfile3, vo.getVotedUser().get(2).getUserImage(), context);
                        } else {
                            holder4.ivProfile3.setVisibility(View.GONE);
                        }

                        if (vo.canShowVotedUserImage(3)) {
                            holder4.ivProfile4.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(holder4.ivProfile4, vo.getVotedUser().get(3).getUserImage(), context);
                        } else {
                            holder4.ivProfile4.setVisibility(View.GONE);
                        }
                    } else {
                        holder4.rlProfiles.setVisibility(View.GONE);
                    }


                    if (poll.isUserVotedThisOption(vo.getPollOptionId())) {
                        //holder3.sbvVote.setVisibility(View.GONE);
                        holder4.tvTitle.setTextColor(text_1);
                        //  holder3.vScrim.setBackground(ContextCompat.getDrawable(context, R.drawable.scrim));
                        //  holder3.cvMain.setCardBackgroundColor(cGreen);
                        holder4.ivVote.setImageResource(R.drawable.selecttab);

                    } else {
                        //  holder3.sbvVote.setVisibility(View.VISIBLE);
                        holder4.tvTitle.setTextColor(text_1);
                        //  holder3.vScrim.setBackground(null);
                         holder4.ivVote.setImageResource(R.drawable.unselecttab);
                    }


                    holder4.ivProfile5.setVisibility(vo.isMoreUserLink() ? View.VISIBLE : View.GONE);
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

    public class ResultImageHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvPercentage,tvvote1;
        protected View rlProfiles;
        protected ImageView ivProfile1;
        protected ImageView ivProfile2;
        protected ImageView ivProfile3;
        protected ImageView ivProfile4;
        protected ImageView ivProfile5;
        protected ImageView ivImage,ivVote;
        protected CardView cvMain;
        protected ProgressBar sbProgress;


        public ResultImageHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                sbProgress = itemView.findViewById(R.id.sbProgress);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPercentage = itemView.findViewById(R.id.tvPercentage);
                tvvote1 = itemView.findViewById(R.id.tvvote1);
                rlProfiles = itemView.findViewById(R.id.rlProfiles);
                ivProfile1 = itemView.findViewById(R.id.ivProfile1);
                ivVote = itemView.findViewById(R.id.ivVote);
                ivProfile2 = itemView.findViewById(R.id.ivProfile2);
                ivProfile3 = itemView.findViewById(R.id.ivProfile3);
                ivProfile4 = itemView.findViewById(R.id.ivProfile4);
                ivProfile5 = itemView.findViewById(R.id.ivProfile5);
                rlProfiles.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MORE_MEMBER, parentPosition, getAdapterPosition()));

            } catch (Exception ignore) {
                // CustomLog.e(e);
            }
        }
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvPercentage,tvvote1;
        protected View rlProfiles;
        protected ImageView ivProfile1;
        protected ImageView ivProfile2;
        protected ImageView ivProfile3;
        protected ImageView ivProfile4;
        protected ImageView ivProfile5,ivVote;
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
                tvvote1 = itemView.findViewById(R.id.tvvote1);
                rlProfiles = itemView.findViewById(R.id.rlProfiles);
                ivVote = itemView.findViewById(R.id.ivVote);
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

    public class OptionImageHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected View vScrim;
        protected SmallBangView sbvVote;
        protected ImageView ivImage;
        protected ImageView ivVote;
        protected CardView cvMain;
        protected CardView cvVote;

        public OptionImageHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                ivImage = itemView.findViewById(R.id.ivImage);
                vScrim = itemView.findViewById(R.id.vScrim);
                sbvVote = itemView.findViewById(R.id.sbvVote);
                ivVote = itemView.findViewById(R.id.ivVote);
                //sbvVote.setVisibility(poll.canVote() ? View.VISIBLE : View.GONE);

                sbvVote.setOnClickListener(v -> {
                    if (poll.canVote()) {
                        sbvVote.likeAnimation(new AnimationAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                              //  cvVote.setCardBackgroundColor(cGreen);
                               ivVote.setColorFilter(foregroundColor);
                                cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                           //     cvVote.setCardBackgroundColor(foregroundColor);
                               ivVote.setColorFilter(cGreen);
                                vScrim.setBackground(ContextCompat.getDrawable(context, R.drawable.scrim));
                                cvMain.setCardBackgroundColor(cGreen);
                                tvTitle.setTextColor(foregroundColor);
                                vScrim.setVisibility(View.VISIBLE);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    int revealX = (int) (sbvVote.getX() + sbvVote.getWidth() / 2);
                                    int revealY = (int) (sbvVote.getY() + sbvVote.getHeight() / 2);
                                    // int width = sbvVote.getWidth();
                                    //int height = view.getHeight();
                                    //double startRadius = Math.sqrt((width * width) + (height * height));
                                    float startRadius = (sbvVote.getWidth()) / 2;
                                    float finalRadius = (float) (Math.max(cvMain.getWidth(), cvMain.getHeight()) * 1.1);

                                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(cvMain, revealX, revealY, startRadius, finalRadius);

                                    circularReveal.setDuration(700);
                                    circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
                                    circularReveal.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                        }
                                    });
                                    circularReveal.start();
                                } else {
                                    listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                }
                            }
                        });
                    } else {
                        Util.showSnackbar(cvVote, context.getString(R.string.msg_cannot_vote));
                    }
                });

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class OptionHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView ivVote,ivVote2;
        protected SmallBangView sbvVote;
        protected SmallBangView sbvVote2;
        protected CardView cvVote;
        protected CardView cvMain;
        public OptionHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                sbvVote = itemView.findViewById(R.id.sbvVote);
                sbvVote2 = itemView.findViewById(R.id.sbvVote2);
                ivVote2 = itemView.findViewById(R.id.ivVote2);
                cvVote = itemView.findViewById(R.id.cvVote);
                ivVote = itemView.findViewById(R.id.ivVote);

                sbvVote.setVisibility(View.GONE);
                sbvVote.setOnClickListener(v -> {
                    if (poll.canVote()) {
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
                                                listener.onItemClicked(Constant.Events.VOTE, parentPosition, getAdapterPosition());
                                            }
                                        });
                                        circularReveal.start();
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
                        Util.showSnackbar(cvVote, context.getString(R.string.msg_cannot_vote));
                    }
                });
                sbvVote2.setOnClickListener(v -> {
                    if (poll.canVote()) {
                        //cvMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contest_vote));
                        sbvVote2.likeAnimation(new AnimationAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cvVote.setCardBackgroundColor(cGreen);
                               // ivVote.setColorFilter(foregroundColor);
                                cvMain.setCardBackgroundColor(cGreen);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cvVote.setCardBackgroundColor(foregroundColor);
                               // ivVote.setColorFilter(cGreen);
                                ivVote2.setImageResource(R.drawable.selecttab);
                                cvMain.setCardBackgroundColor(cGreen);
                                tvTitle.setTextColor(Color.parseColor(Constant.foregroundColor));
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                                    try {
                                        int revealX = (int) (sbvVote2.getX() + sbvVote2.getWidth() / 2);
                                        int revealY = (int) (sbvVote2.getY() + sbvVote2.getHeight() / 2);
                                        // int width = sbvVote.getWidth();
                                        //int height = view.getHeight();
                                        //double startRadius = Math.sqrt((width * width) + (height * height));
                                        float startRadius = (ivVote2.getWidth()) / 2;
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
                        Util.showSnackbar(cvVote, context.getString(R.string.msg_cannot_vote));
                    }
                });

            } catch (Exception e) {
                // CustomLog.e(e);
            }
        }

    }
}
