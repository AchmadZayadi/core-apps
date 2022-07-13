package com.sesolutions.ui.events;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.event.Discussion;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class DiscussionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Discussion> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    //  private final Typeface iconFont;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    private final ThemeManager themeManager;
    // private final boolean isUserLoggedIn;
    private String TXT_PHOTO;
    private String TXT_PHOTOS;
    private boolean isDiscussion = false;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public DiscussionAdapter(List<Discussion> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
       /* addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);*/
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isDiscussion) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discussion, parent, false);
            return new DiscussionHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discussion_view, parent, false);
            return new TopicHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);
            final Discussion vo = list.get(position);
            if (isDiscussion) {
                final DiscussionHolder holder = (DiscussionHolder) parentHolder;
                if (TextUtils.isEmpty(vo.getTitle())) {
                    holder.tvTitle.setVisibility(View.GONE);
                } else {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(vo.getTitle());
                }

                holder.tvReply.setText(vo.getReplyLabel());
                holder.tvCount.setText(vo.getReplyCount());

                holder.tvDesc.setText(vo.getDesc());
                if (null != vo.getLastPost()) {
                    holder.rlUser.setVisibility(View.VISIBLE);
                    holder.tvUser.setText(vo.getLastPost().getLabel());
                    Util.showImageWithGlide(holder.ivUser, vo.getLastPost().getImage(), context, R.drawable.placeholder_square);
                    holder.tvDate.setText(Util.changeDateFormat(context,vo.getCreation_date()));
                } else {
                    holder.rlUser.setVisibility(View.GONE);
                }

                holder.llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

                holder.rlUser.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
                holder.cvMain.setBackgroundColor(Color.parseColor(Constant.text_color_1));
                holder.tvCount.setTextColor(Color.parseColor(Constant.backgroundColor));
                holder.tvReply.setTextColor(Color.parseColor(Constant.backgroundColor));


            } else {
                final TopicHolder holder = (TopicHolder) parentHolder;

                holder.tvUser.setText(vo.getTitle());
                holder.tvDesc.setText(vo.getBody());
                Util.showImageWithGlide(holder.ivUser, vo.getUserPhoto(), context, R.drawable.placeholder_square);
                holder.tvDate.setText(Util.changeDateFormat(context,vo.getModified_date()));

                holder.tvDesc.setTextColor(Color.parseColor(Constant.backgroundColor));
                ((ImageView)holder.ivOption).setColorFilter(Color.parseColor(Constant.backgroundColor));

                holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                holder.ivOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions());
                    }
                });
                holder.rlUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition());
                    }
                });
                holder.cvMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition());
                    }
                });
                holder.cvMain.setBackgroundColor(Color.parseColor(Constant.text_color_1));

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

    public void setDiscussion(boolean discussion) {
        this.isDiscussion = discussion;
    }

    public static class DiscussionHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvUser;
        protected TextView tvDate;
        protected TextView tvDesc;
        protected TextView tvReply;
        protected TextView tvCount;
        protected ImageView ivUser;
        protected View cvMain;
        protected View llMain;
        protected View rlUser;


        public DiscussionHolder(View itemView) {
            super(itemView);
            try {
                llMain = itemView.findViewById(R.id.llMain);
                rlUser = itemView.findViewById(R.id.rlUser);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvReply = itemView.findViewById(R.id.tvReply);
                tvCount = itemView.findViewById(R.id.tvCount);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class TopicHolder extends RecyclerView.ViewHolder {

        protected TextView tvUser;
        protected TextView tvDate;
        protected TextView tvDesc;
        protected ImageView ivUser;
        protected View ivOption;
        protected View cvMain;
        protected View rlUser;


        public TopicHolder(View itemView) {
            super(itemView);
            try {
                rlUser = itemView.findViewById(R.id.rlUser);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivOption = itemView.findViewById(R.id.ivOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
