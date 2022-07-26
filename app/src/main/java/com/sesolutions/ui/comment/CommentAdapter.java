package com.sesolutions.ui.comment;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.ExampleCardPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomClickableSpan;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Typeface.BOLD;


public class CommentAdapter<T> extends RecyclerView.Adapter<CommentHolder> {

    private final List<CommentData> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int colorPrimary;
    //private final Typeface iconFont;
    private final int text2;
    private final int cBackground, cForeground;
    private final ThemeManager themeManager;
    private boolean isViewFeed;
    private boolean canReply;

    public CommentAdapter(List<CommentData> list, Context cntxt, OnUserClickedListener<Integer, Object> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        this.colorPrimary = Color.parseColor(Constant.colorPrimary);
        this.text2 = Color.parseColor(Constant.text_color_2);
        this.cBackground = Color.parseColor(Constant.backgroundColor);
        this.cForeground = Color.parseColor(Constant.foregroundColor);
        themeManager = new ThemeManager();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull CommentHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_feed, parent, false);
        return new CommentHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CommentHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final CommentData vo = list.get(position);
            holder.tvHeader.setText(vo.getUserTitle());
            holder.tvHeader.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT_HEADER_TITLE, "", holder.getAdapterPosition()));
            holder.ivProfileImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT_HEADER_IMAGE, "", holder.getAdapterPosition()));



            if (vo.getLevelId() == 3){
                holder.ivVerify.setImageResource(R.drawable.ic_verified);
            }
            holder.tvLikeCount.setText("" + vo.getLikeCount());
            holder.tvDate.setText(Util.getDateDifference(context, vo.getCreationDate()));

            holder.tvLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.LIKE_COMMENT, vo.getIsLike() ? "-1" : "0", holder.getAdapterPosition()));
            holder.tvReply.setVisibility(canReply ? View.VISIBLE : View.GONE);
            holder.tvReply.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPLY, "true", holder.getAdapterPosition()));

            if (vo.getCanDelete()) {
                holder.tvDelete.setVisibility(View.VISIBLE);
                holder.tvDelete.setOnClickListener(v -> listener.onItemClicked(Constant.Events.DELETE_COMMENT, "", holder.getAdapterPosition()));
            } else {
                holder.tvDelete.setVisibility(View.GONE);
            }

            Util.showImageWithGlide(holder.ivProfileImage, vo.getUserImage(), context, R.drawable.placeholder_3_2);
            if (TextUtils.isEmpty(vo.getEmojiImage())) {
                holder.ivSticker.setVisibility(View.GONE);
            } else {
                holder.ivSticker.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(holder.ivSticker, vo.getEmojiImage(), context, R.drawable.placeholder_3_2);

            }


            if (!TextUtils.isEmpty(vo.getBody())) {
                holder.cvComment.setCardBackgroundColor(cBackground);
                String body = unecodeStr(vo.getBody());
                SpannableString span = null;
                try {
                    if (vo.hasMention()) {
                        List<Mention> mentionList = vo.getMention();
                        List<Mention> list2 = new ArrayList<>();
                        for (Mention men : mentionList) {
                            body = body.replace(men.getWord(), men.getTitle());
                            int startMention = body.indexOf(men.getTitle());
                            int endMention = men.getTitle().length();
                            men.setStartIndex(startMention);
                            men.setEndIndex(startMention + endMention);
                            list2.add(men);
                        }

                        span = new SpannableString(body);
                        for (final Mention men : list2) {
                            // body = body.replace(men.getWord(), men.getTitle());
                            if (men.getStartIndex() > -1) {
                                span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_TAGGED, "" + men.getUserId(), holder.getAdapterPosition()), men.getStartIndex(), men.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                span.setSpan(new StyleSpan(BOLD), men.getStartIndex(), men.getEndIndex(), 0);
                            }
                        }
                    }

                    if (span == null) {
                        span = new SpannableString(body);
                    }

                    if (vo.hasTags()) {
                        List<String> hashList = vo.getHashtags();

                        CustomLog.e("body", body);

                        for (final String men : hashList) {
                            int startMention = body.indexOf(men);
                            int endMention = startMention + men.length();
                            if (startMention > -1) {
                                span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_HASH_TAGGED, "" + men, holder.getAdapterPosition()), startMention, endMention, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                span.setSpan(new StyleSpan(BOLD), startMention, endMention, 0);
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }

                String bdy = span.toString();
                holder.tvBody.setVisibility(View.VISIBLE);
              //  holder.tvBody.setText(span);
                holder.tvBody.setText(Util.getEmojiFromString(bdy));
                holder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());
//                holder.tvBody.setMovementMethod(new TextViewClickMovement(listener, context, holder.getAdapterPosition()));
            } else {

                holder.tvBody.setVisibility(View.GONE);
                holder.cvComment.setCardBackgroundColor(cForeground);
            }

            if (!TextUtils.isEmpty(vo.getGif_url()) && vo.getGif_url().length()>0) {
                holder.ivSticker.setVisibility(View.VISIBLE);
                holder.tvBody.setVisibility(View.GONE);
                Util.showImageWithGlideGIF(holder.ivSticker, vo.getGif_url(), context, R.drawable.placeholder_3_2);
            }

            if (null != vo.getAttachPhotoVideo() && vo.getAttachPhotoVideo().size() > 0) {
                holder.rvCommentAttachment.setVisibility(View.VISIBLE);
                holder.rvCommentAttachment.setHasFixedSize(true);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                //holder.rvCommentAttachment.setLayoutManager(layoutManager);
                holder.rvCommentAttachment.setAdapter(new CommentAttachementAdapter(vo.getAttachPhotoVideo(), context, listener, position));
            } else {
                holder.rvCommentAttachment.setVisibility(View.GONE);
            }

            if (null != vo.getLink()) {
                holder.llLinkAttachment.setVisibility(View.VISIBLE);
                holder.tvImageTitle.setText(vo.getLink().getTitle());
                holder.tvImageDescription.setText(vo.getLink().getDescription());
                Util.showImageWithGlide(holder.ivLinkImage, vo.getLink().getImages().getMain(), context, R.drawable.placeholder_menu);
                holder.llLinkAttachment.setOnClickListener(view -> listener.onItemClicked(Constant.Events.COMMENT_LINK, "", holder.getAdapterPosition()));
            } else {
                holder.llLinkAttachment.setVisibility(View.GONE);
            }

          //  holder.cvMain.setOnClickListener(v -> listener.onItemClicked(0, "", holder.getAdapterPosition()));

            if (null != vo.getOptions()) {
                holder.cvMain.setOnLongClickListener(v -> {
                    Util.showOptionsPopUp(holder.cvComment, holder.getAdapterPosition(), vo.getOptions(), listener);
                    return false;
                });

                holder.tvBody.setOnLongClickListener(v -> {
                    Util.showOptionsPopUp(holder.cvComment, holder.getAdapterPosition(), vo.getOptions(), listener);
                    return false;
                });
            } else {
                //no options available
                holder.cvMain.setOnLongClickListener(null);
                holder.tvBody.setOnLongClickListener(null);
            }
            holder.tvLike.setOnLongClickListener(v -> {
                createPopUp(v, holder.getAdapterPosition());
                return false;
            });

            //SET CHILD DATA; starts
            if (vo.getReplyCount() > 0) {
                holder.rlCommentChild.setVisibility(View.VISIBLE);
                holder.tvReplyCount.setText(context.getResources().getQuantityString(R.plurals.view_more_reply_count, vo.getReplyCount(), vo.getReplyCount()));
                holder.cvCommentChild.setCardBackgroundColor(cBackground);
                holder.tvBodyChild.setText(unecodeStr(vo.getReplies().get(0).getBody()));
                holder.tvHeaderChild.setText(unecodeStr(vo.getReplies().get(0).getUserTitle()));
                Util.showImageWithGlide(holder.ivProfileChild, vo.getReplies().get(0).getUserImage(), context, R.drawable.placeholder_3_2);
                holder.rlCommentChild.setOnClickListener(v -> listener.onItemClicked(Constant.Events.REPLY, "false", holder.getAdapterPosition()));
                if (vo.getReplies().get(0).getLevelId() == 3){
                    holder.ivVerifyChild.setImageResource(R.drawable.ic_verified);
                }
                if (!TextUtils.isEmpty(vo.getReplies().get(0).getBody())) {
                    String body = unecodeStr(vo.getReplies().get(0).getBody());
                    SpannableString span = null;
                    try {
                        if (vo.getMention() != null) {
                            List<Mention> mentionList = vo.getReplies().get(0).getMention();
                            List<Mention> list2 = new ArrayList<>();
                            for (Mention men : mentionList) {
                                body = body.replace(men.getWord(), men.getTitle());
                                int startMention = body.indexOf(men.getTitle());
                                int endMention = men.getTitle().length();
                                men.setStartIndex(startMention);
                                men.setEndIndex(startMention + endMention);
                                list2.add(men);
                            }

                            span = new SpannableString(body);
                            for (final Mention men : list2) {
                                // body = body.replace(men.getWord(), men.getTitle());
                                if (men.getStartIndex() > -1) {
                                    span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_TAGGED, "" + men.getUserId(), holder.getAdapterPosition()), men.getStartIndex(), men.getEndIndex(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    span.setSpan(new StyleSpan(BOLD), men.getStartIndex(), men.getEndIndex(), 0);
                                }
                            }
                        }

                        if (span == null) {
                            span = new SpannableString(body);
                        }

                        if (vo.getReplies().get(0).getHashtags() != null && vo.getReplies().get(0).getHashtags().size() > 0) {
                            List<String> hashList = vo.getReplies().get(0).getHashtags();

                            CustomLog.e("body", body);

                            for (final String men : hashList) {
                                int startMention = body.indexOf(men);
                                int endMention = startMention + men.length();
                                if (startMention > -1) {
                                    span.setSpan(new CustomClickableSpan(listener, Constant.Events.CLICKED_BODY_HASH_TAGGED, "" + men, holder.getAdapterPosition()), startMention, endMention, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    span.setSpan(new StyleSpan(BOLD), startMention, endMention, 0);
                                }
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    holder.tvBodyChild.setVisibility(View.VISIBLE);
                    holder.tvBodyChild.setText(span);
                    holder.tvBodyChild.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    holder.tvBodyChild.setVisibility(View.GONE);
                }

            } else {
                holder.rlCommentChild.setVisibility(View.GONE);
            }
            //SET CHILD DATA; ends

            if (vo.getIsLike() && null != vo.getLike()) {
                holder.tvLike.setText(vo.getLike().getTitle());
                holder.tvLike.setTextColor(colorPrimary);
                //showImageWithGlide(holderParent.ivImageLike, vo.getLike().getImage(), context, R.drawable.placeholder_menu);

            } else {
                holder.tvLike.setText(R.string.TXT_LIKE);
                holder.tvLike.setTextColor(text2);
                // holderParent.ivImageLike.setImageDrawable(dLike);
            }

            if (/*null != vo.getReactionUserData() &&*/ null != vo.getReactionData()) {
                holder.cvReaction.setVisibility(View.VISIBLE);
                // holder.tvLikeCount.setText(vo.getReactionUserData());
                if (vo.getReactionData().size() > 0) {
                    holder.ivLikeUpper1.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(holder.ivLikeUpper1, vo.getReactionData().get(0).getImageUrl(), context);
                } else {
                    holder.ivLikeUpper1.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 1) {
                    holder.ivLikeUpper2.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(holder.ivLikeUpper2, vo.getReactionData().get(1).getImageUrl(), context);
                } else {
                    holder.ivLikeUpper2.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 2) {
                    holder.ivLikeUpper3.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(holder.ivLikeUpper3, vo.getReactionData().get(2).getImageUrl(), context);
                } else {
                    holder.ivLikeUpper3.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 3) {
                    holder.ivLikeUpper4.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(holder.ivLikeUpper4, vo.getReactionData().get(3).getImageUrl(), context);
                } else {
                    holder.ivLikeUpper4.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 4) {
                    holder.ivLikeUpper5.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(holder.ivLikeUpper5, vo.getReactionData().get(4).getImageUrl(), context);
                } else {
                    holder.ivLikeUpper5.setVisibility(View.GONE);
                }
            } else {
                holder.cvReaction.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private String unecodeStr(String escapedString) {
        try {
            return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(escapedString));
        } catch (Exception e) {
            CustomLog.d("warnning", "emoji parsing error at " + escapedString);
        }

        return escapedString;
    }

    public void createPopUp(View v, int position) {
        try {
            ExampleCardPopup popup = new ExampleCardPopup(v.getContext(), position, listener, Constant.Events.LIKE_COMMENT);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            int vertPos = RelativePopupWindow.VerticalPosition.ABOVE;
            int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setIsViewFeed() {
        isViewFeed = true;
    }

    public void setCanReply(boolean replyComment) {
        canReply = replyComment;
    }
}
