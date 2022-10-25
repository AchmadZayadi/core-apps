package com.sesolutions.ui.comment;

import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;

public class CommentHolder extends RecyclerView.ViewHolder {

    final protected TextView tvHeader, tvReplyCount, tvHeaderChild, tvReply,tvReportComment;
    final protected TextView tvBodyChild;
    final protected TextView tvBody;
    final protected TextView tvLike;
    final protected TextView tvLikeCount;
    final protected TextView tvDelete;
    final protected TextView tvDate;
    final protected TextView tvImageTitle;
    final protected TextView tvImageDescription;
    final protected ImageView ivProfileImage, ivProfileChild;
    final protected ImageView ivSticker;
    final protected ImageView ivLinkImage;
    final protected View cvMain, rlCommentChild;
    final protected CardView cvComment, cvCommentChild;
    final protected LinearLayoutCompat llLinkAttachment;
    final protected RecyclerView rvCommentAttachment;
    final protected ImageView ivLikeUpper1;
    final protected ImageView ivLikeUpper2;
    final protected ImageView ivLikeUpper3;
    final protected ImageView ivLikeUpper4;
    final protected ImageView ivLikeUpper5;
    final protected View cvReaction;
    final protected ImageView ivVerify,ivVerifyChild;

    public CommentHolder(View itemView) {
        super(itemView);

        cvMain = itemView.findViewById(R.id.cvMain);
        tvHeader = itemView.findViewById(R.id.tvHeader);
        tvBody = itemView.findViewById(R.id.tvBody);
        tvLike = itemView.findViewById(R.id.tvLike);
        tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        tvDelete = itemView.findViewById(R.id.tvDelete);
        tvDate = itemView.findViewById(R.id.tvDate);
        ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        ivSticker = itemView.findViewById(R.id.ivSticker);
        rvCommentAttachment = itemView.findViewById(R.id.rvCommentAttachment);
        llLinkAttachment = itemView.findViewById(R.id.llLinkAttachment);
        ivLinkImage = itemView.findViewById(R.id.ivLinkImage);
        tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
        tvImageDescription = itemView.findViewById(R.id.tvImageDescription);

        cvComment = itemView.findViewById(R.id.cvComment);
        tvHeaderChild = itemView.findViewById(R.id.tvHeaderChild);
        tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
        tvBodyChild = itemView.findViewById(R.id.tvBodyChild);
        ivProfileChild = itemView.findViewById(R.id.ivProfileChild);
        cvCommentChild = itemView.findViewById(R.id.cvCommentChild);
        rlCommentChild = itemView.findViewById(R.id.rlCommentChild);
        tvReply = itemView.findViewById(R.id.tvReply);
        tvReportComment = itemView.findViewById(R.id.tvReport);

        cvReaction = itemView.findViewById(R.id.cvReaction);
        ivLikeUpper1 = itemView.findViewById(R.id.ivLikeUpper1);
        ivLikeUpper2 = itemView.findViewById(R.id.ivLikeUpper2);
        ivLikeUpper3 = itemView.findViewById(R.id.ivLikeUpper3);
        ivLikeUpper4 = itemView.findViewById(R.id.ivLikeUpper4);
        ivLikeUpper5 = itemView.findViewById(R.id.ivLikeUpper5);


        ivVerify =itemView.findViewById(R.id.iv_verify_comments);
        ivVerifyChild = itemView.findViewById(R.id.iv_verify_comments_child);
    }
}