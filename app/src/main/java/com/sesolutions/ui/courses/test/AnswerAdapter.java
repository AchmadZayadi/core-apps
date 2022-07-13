package com.sesolutions.ui.courses.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.Test.Answer;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Answer> list;
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

    public AnswerAdapter(List<Answer> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int parentPosition) {
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
                View view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_option_image : R.layout.item_poll_option_image_feed, parent, false);
                Util.showSnackbar(view,"sorry");
            case TYPE_IMAGE_RESULT:
                view = LayoutInflater.from(parent.getContext()).inflate(parentPosition == -1 ? R.layout.item_poll_result_image : R.layout.item_poll_result_image_feed, parent, false);
                Util.showSnackbar(view,"sorry");
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
                return new OptionHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            final Answer vo = list.get(position);
            switch (holder.getItemViewType()) {
                case TYPE_TEXT:
                    OptionHolder holder1 = (OptionHolder) holder;

                    try {
                        String QDetails="Q "+(list.size()-(position))+". "+vo.getTestquestion().getQuestion();

                        String result1=QDetails.replaceAll("<p>","");
                        String result2=result1.replaceAll("</p>","");
                        String result3="<p>"+result2+"</p>";
                        holder1.tvQuestion.loadData(result3, "text/html", "UTF-8");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }


                    if(vo.getIs_true() == 0 ){
                        holder1.ivResult.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unchecked));
                        holder1.ivResult.setColorFilter(Color.RED);
                    } else {
                        holder1.ivResult.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tick_sign));
                        holder1.ivResult.setColorFilter(Color.parseColor("#008000"));
                    }
                    if(vo.getisAttempt() == 0){
                        holder1.ivResult.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_forward));
                        holder1.cvMain.setCardBackgroundColor(Color.GRAY);
                    }
                    if(vo.getTestquestion().getCurrectAnswer()!=null) {
                        holder1.tvCorrectAnswer.loadData(vo.getTestquestion().getCurrectAnswer().get(0).getAnswer(), "text/html", "UTF-8");
                        holder1.tvCorrectAnswer2.loadData(vo.getTestquestion().getCurrectAnswer().get(1).getAnswer(), "text/html", "UTF-8");
                        holder1.tvCorrectAnswer3.loadData(vo.getTestquestion().getCurrectAnswer().get(2).getAnswer(), "text/html", "UTF-8");
                        holder1.tvCorrectAnswer3.loadData(vo.getTestquestion().getCurrectAnswer().get(3).getAnswer(), "text/html", "UTF-8");
                    } else {
                        holder1.tvCorrectAnswer.setVisibility(View.GONE);
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

    public void showQuestion(boolean isShowingQuestion) {
        this.isShowingQuestion = isShowingQuestion;
    }

        public class OptionHolder extends RecyclerView.ViewHolder {

        protected WebView tvQuestion;
        protected WebView tvCorrectAnswer;
        protected WebView tvCorrectAnswer2;
        protected WebView tvCorrectAnswer3;
        protected WebView tvCorrectAnswer4;
        protected ImageView ivVote;
        protected ImageView ivResult;
        protected SmallBangView sbvVote;
        protected CardView cvVote;
        protected CardView cvMain;


        public OptionHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvQuestion = itemView.findViewById(R.id.tvQuestion);
                sbvVote = itemView.findViewById(R.id.sbvVote);
                cvVote = itemView.findViewById(R.id.cvVote);
                tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
                tvCorrectAnswer2 = itemView.findViewById(R.id.tvCorrectAnswer2);
                tvCorrectAnswer3 = itemView.findViewById(R.id.tvCorrectAnswer3);
                tvCorrectAnswer4 = itemView.findViewById(R.id.tvCorrectAnswer4);
                ivVote = itemView.findViewById(R.id.ivVote);
                ivResult = itemView.findViewById(R.id.ivResult);
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();

//                );
            } catch (Exception e) {
                // CustomLog.e(e);
            }
        }

    }
}
