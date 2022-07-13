package com.sesolutions.ui.contest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.page.PageAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.CustomRunnable;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

public class ContestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String txt_day_left;
    private static String txt_days_left;
    //private static String txt_seconds;
    //private static String txt_minutes;
    //private static String txt_hours;
    private final List<Contest> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_BANNER = "-4";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    public final String VT_CATEGORIES = "-3";
    private static final int VT_NONE = 0;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final String TXT_BY;
    private final String TXT_IN;
    private final int cGreen;
    private final int cBlack54;
    //private final RecyclerView.RecycledViewPool viewPool;
    private String TXT_PHOTO;
    private String TXT_PHOTOS;
    private String type;
    private final int[] dRank = {R.drawable.rank_1
            , R.drawable.rank_2, R.drawable.rank_3
            , R.drawable.rank_4, R.drawable.rank_5
    };
    private Handler handler = new Handler();
    private  Drawable dSave;
    private  Drawable dUnsave;

    public void clearAll() {
        handler.removeCallbacksAndMessages(null);
    }

    /*@Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof ContactHolder) {
            //do not update tile if item is detached from window
            //handler.removeCallbacks(((ContactHolder) holder).customRunnable);
            ((ContactHolder) holder).customRunnable.canUpdateTime(false);
        }
        super.onViewDetachedFromWindow(holder);
    }*/

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    protected ContestAdapter(List<Contest> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_PHOTO = " " + context.getResources().getString(R.string.photo);
        TXT_PHOTOS = " " + context.getResources().getString(R.string.TITLE_PHOTOS);
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
        txt_day_left = context.getResources().getString(R.string.txt_day_left);
        txt_days_left = context.getResources().getString(R.string.txt_days_left);
        //  txt_seconds = context.getResources().getString(R.string.txt_seconds);
        //  txt_minutes = context.getResources().getString(R.string.txt_minutes);
        //  txt_hours = context.getResources().getString(R.string.txt_hours);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);
        cGreen = ContextCompat.getColor(context, R.color.contest_vote);
        cBlack54 = ContextCompat.getColor(context, R.color.black_54);
        themeManager = new ThemeManager();
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("TYPEREAD",""+list.get(viewType).getType());
        switch (list.get(viewType).getType()) {
            case VT_CATEGORY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                return new CategoryHolder(view);
            case VT_SUGGESTION:
            case VT_CATEGORIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            case VT_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_banner, parent, false);
                return new BannerHolder(view);
            case ContestHelper.TYPE_MANAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_contest, parent, false);
                return new MyContestHolder(view);
            case ContestHelper.TYPE_ENTRIES:
            case ContestHelper.TYPE_CONTEST_ENTRIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry2, parent, false);
                return new EntryHolder(view);
            case "winners":
            case ContestHelper.TYPE_WINNERS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_winner, parent, false);
                return new WinnerHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest2, parent, false);
                return new ContactHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

//        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);

        try {
             switch (list.get(position).getType()) {
                case VT_CATEGORY:
                    final CategoryHolder holder1 = (CategoryHolder) parentHolder;
                    if (holder1.adapter == null) {
                        /*set child item list*/
                        holder1.rvChild.setHasFixedSize(true);
                     //   final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                        holder1.rvChild.setLayoutManager(layoutManager);
                        holder1.adapter = new ContestCategoryAdapter2(list.get(holder1.getAdapterPosition()).getCategories(), listener, context);
                        holder1.rvChild.setAdapter(holder1.adapter);
                    } else {
                        holder1.adapter.notifyDataSetChanged();
                    }
                    break;
                case VT_SUGGESTION:
                    final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                    if (holder2.adapter == null) {
                        holder2.tvMore.setVisibility(View.GONE);
                        /*set child item list*/
                        holder2.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new SuggestionContestAdapter(list.get(holder2.getAdapterPosition()).getCategory().getItems(), context, listener, false);
                        holder2.rvChild.setAdapter(holder2.adapter);
                        holder2.pageIndicatorView.setCount(holder2.adapter.getItemCount());
                        holder2.rvChild.setOnSnapListener(position12 -> holder2.pageIndicatorView.setSelection(position12));
                    } else {
                        holder2.adapter.notifyDataSetChanged();
                        holder2.pageIndicatorView.setSelection(0);
                    }
                    break;
                case VT_BANNER:
                    final BannerHolder holder7 = (BannerHolder) parentHolder;
                    Util.showImageWithGlide(holder7.ivImage, list.get(position).getBanner().getImage(), context, R.drawable.placeholder_square);
                    holder7.tvTitle.setText(list.get(position).getBanner().getBannerTitle());
                    holder7.tvDesc.setText(list.get(position).getBanner().getDescription());
                    break;
                case VT_CATEGORIES:
                    final SuggestionHolder holder3 = (SuggestionHolder) parentHolder;
                    final CategoryPage<ContestItem> cVo = list.get(position).getCategory();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionContestAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position1 -> holder3.pageIndicatorView.setSelection(position1));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }
                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));
                    break;

                case ContestHelper.TYPE_MANAGE:
                    final MyContestHolder holder5 = (MyContestHolder) parentHolder;
                    final ContestItem myContest = list.get(position).getItem();

                    holder5.tvArtist.setTypeface(iconFont);
                    holder5.tvArtist.setText(Constant.FontIcon.FOLDER +" "+ myContest.getCategoryTitle());
                    holder5.tvStats.setTypeface(iconFont);
                    holder5.tvStats.setText("\uf164 " + myContest.getLikeCount()
                            + "  \uf004 " + myContest.getFavouriteCount()
                            + "  \uf075 " + myContest.getCommentCount()
                            + "  \uf06e " + myContest.getViewCountInt()
                            + "  \uf0c0 " + myContest.getJoinCount()
                    );
                    holder5.tvStats.setVisibility(View.GONE);

                    holder5.tvSongTitle.setText(myContest.getTitle());
                    if (null != myContest.getContestType()) {
                        holder5.tvStatus.setVisibility(View.VISIBLE);
                        holder5.tvStatus.setText(myContest.getContestStatus().getLabel());
                        holder5.tvStatus.setBackgroundColor(Color.parseColor(myContest.getContestStatus().getValue()));
                    } else {
                        holder5.tvStatus.setVisibility(View.GONE);
                    }
                    holder5.tvType.setTypeface(iconFont);
                    holder5.tvType.setText(getIconByType(myContest.getContestType()));
                    holder5.tvType.setVisibility(TextUtils.isEmpty(myContest.getContestType()) ? View.GONE : View.VISIBLE);


                    Util.showImageWithGlide(holder5.ivSongImage, myContest.getImageUrl(), context, R.drawable.placeholder_square);
                    holder5.ivOption.setVisibility(null != myContest.getOptions() ? View.VISIBLE : View.GONE);
                    holder5.ivOption.setOnClickListener(v -> showOptionsPopUp(holder5.ivOption, holder5.getAdapterPosition(), myContest.getOptions()));
                    holder5.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder5, holder5.getAdapterPosition()));
                    break;

                //Entry List Items
                case ContestHelper.TYPE_ENTRIES:
                case ContestHelper.TYPE_CONTEST_ENTRIES:
                    final EntryHolder holder6 = (EntryHolder) parentHolder;
                    final ContestItem ent = list.get(position).getItem();
                    //holder6.tvType.setTypeface(iconFont);
                    //  holder6.tvType.setText(getIconByType(ent.getContestType()));
                    //  holder6.tvType.setVisibility(TextUtils.isEmpty(ent.getContestType()) ? View.GONE : View.VISIBLE);

                   // holder6.tvArtist.setText(TXT_BY + ent.getOwnerTitle() + " " + TXT_IN + ent.getContestTitle());
                    Util.showImageWithGlide(holder6.ivSongImage, ent.getEntryImage(), context, R.drawable.placeholder_square);
                    Util.showImageWithGlide(holder6.ivUser, ent.getOwnerImageUrl(), context, R.drawable.default_user);

                   // holder6.tvStats.setTypeface(iconFont);

                    try {
                        holder6.ivArtist.setTypeface(iconFont);
                        holder6.ivArtist.setText(Constant.FontIcon.USER);
                        holder6.tvType.setTypeface(iconFont);
                        holder6.tvType.setText(getIconByType(ent.getContestType()));
                        holder6.tvType.setVisibility(TextUtils.isEmpty(ent.getContestType()) ? View.GONE : View.VISIBLE);
                        holder6.tvType.setTextColor(Color.parseColor("#FFFFFF"));
                        holder6.ivArtist.setTextColor(Color.parseColor("#000000"));
                        holder6.rlDate.setVisibility(View.GONE);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    /*
                    holder6.tvStats.setText("\uf0a6 " + ent.getVoteCount()
                            + "  \uf164 " + ent.getLikeCount()
                            + "  \uf004 " + ent.getFavouriteCount()
                            + "  \uf075 " + ent.getCommentCount()
                            + "  \uf06e " + ent.getViewCountInt()
                            + "  \uf0c0 " + ent.getJoinCount()
                    );*/

                    try {
                        Log.e("Title",""+ent.getTitle());
                        holder6.tvTitle.setText(ent.getTitle());
                        holder6.tvArtist.setText(ent.getOwnerTitle());

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }


                    if(SPref.getInstance().isLoggedIn(context)){
                        holder6.cvVote.setVisibility(ent.canShowVote() ? View.VISIBLE : View.GONE);
                    }else {
                        holder6.cvVote.setVisibility(View.GONE);
                    }

                    holder6.cvVote.setCardBackgroundColor(ent.isContentVoted() ? cBlack54 : cGreen);
                    holder6.tvVote.setText(ent.isContentVoted() ? R.string.voted : R.string.vote);

                    holder6.cvVote.setOnClickListener(v -> listener.onItemClicked(Constant.Events.VOTE, null, holder6.getAdapterPosition()));
                    //setting reactions
                    holder6.llReactionOption.setVisibility(isUserLoggedIn ? View.INVISIBLE : View.INVISIBLE);
                    holder6.sbvLike.setVisibility(ent.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder6.sbvFavorite.setVisibility(ent.canFavourite() ? View.VISIBLE : View.INVISIBLE);
                    if (ent.isShowAnimation() == 1) {
                        ent.setShowAnimation(0);
                        // holder.sbvLike.likeAnimation();
                        holder6.ivLike.setImageDrawable(ent.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder6.ivLike.setImageDrawable(ent.isContentLike() ? dLikeSelected : dLike);
                    }

                    if (ent.isShowAnimation() == 2) {
                        ent.setShowAnimation(0);
                        // holder.sbvFavorite.likeAnimation();
                        holder6.ivFavorite.setImageDrawable(ent.isContentFavourite() ? dFavSelected : dFav);

                    } else {
                        holder6.ivFavorite.setImageDrawable(ent.isContentFavourite() ? dFavSelected : dFav);
                    }

                    holder6.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, holder6.getAdapterPosition()));

                    holder6.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, null, holder6.getAdapterPosition()));
                    holder6.ivUser.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder6, holder6.getAdapterPosition()));

                    holder6.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ENTRY, holder6, holder6.getAdapterPosition()));

                    holder6.rlDate.setVisibility(View.GONE);

                    holder6.ivFbShare.setVisibility(ent.getCanShare() == 0 ? View.VISIBLE : View.VISIBLE);
                    holder6.ivWhatsAppShare.setVisibility(ent.getCanShare() == 0 ? View.VISIBLE : View.VISIBLE);
                    holder6.ivImageShare.setVisibility(ent.getCanShare() == 0 ? View.VISIBLE : View.VISIBLE);

                    holder6.ivFbShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, ent.getShare(), 1));
                    holder6.ivWhatsAppShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, ent.getShare(), 2));
                    holder6.ivImageShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, ent.getShare(), 3));


                    try {
                        if(ent.getShortcut_save()!=null){
                            holder6.ivSaveFeed.setImageDrawable(ent.getShortcut_save().isIs_saved() ?  dUnsave:dSave);
                            holder6.ivSaveFeed.setOnClickListener(v -> {
                                if(ent.getShortcut_save().isIs_saved()){
                                    listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder6.getAdapterPosition(), ent.getShortcut_save().getShortcut_id());
                                }else {
                                    listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder6.getAdapterPosition(), 0);
                                }

                            });
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    break;
                case "winners":
                case ContestHelper.TYPE_WINNERS:
                    final WinnerHolder holder8 = (WinnerHolder) parentHolder;
                    final ContestItem win = list.get(position).getItem();
                    holder8.tvType.setTypeface(iconFont);
                    holder8.tvType.setText(getIconByType(win.getContestType()));
                    holder8.tvType.setVisibility(TextUtils.isEmpty(win.getContestType()) ? View.GONE : View.VISIBLE);

                    holder8.tvArtist.setText(TXT_BY + win.getOwnerTitle() + " " + TXT_IN + win.getContestTitle());
                    Util.showImageWithGlide(holder8.ivSongImage, win.getEntryImage(), context, R.drawable.placeholder_square);
                    Util.showImageWithGlide(holder8.ivUser, win.getOwnerImageUrl(), context, R.drawable.placeholder_square);
                    holder8.ivRank.setImageDrawable(ContextCompat.getDrawable(context, dRank[win.getRank() - 1]));
                    holder8.tvStats.setTypeface(iconFont);



                    holder8.tvStats.setText("\uf0a6 " + win.getVoteCount()
                            + "  \uf164 " + win.getLikeCount()
                            + "  \uf004 " + win.getFavouriteCount()
                            + "  \uf075 " + win.getCommentCount()
                            + "  \uf075 " + win.getViewCountInt()
                            + "  \uf0c0 " + win.getJoinCount()
                    );
                    holder8.tvTitle.setText(win.getTitle());

                    //setting reactions
                    holder8.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
                    holder8.sbvLike.setVisibility(win.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder8.sbvFavorite.setVisibility(win.canFavourite() ? View.VISIBLE : View.INVISIBLE);
                    if (win.isShowAnimation() == 1) {
                        win.setShowAnimation(0);
                        // holder.sbvLike.likeAnimation();
                        holder8.ivLike.setImageDrawable(win.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder8.ivLike.setImageDrawable(win.isContentLike() ? dLikeSelected : dLike);
                    }

                    if (win.isShowAnimation() == 2) {
                        win.setShowAnimation(0);
                        // holder.sbvFavorite.likeAnimation();
                        holder8.ivFavorite.setImageDrawable(win.isContentFavourite() ? dFavSelected : dFav);
                    } else {
                        holder8.ivFavorite.setImageDrawable(win.isContentFavourite() ? dFavSelected : dFav);
                    }

                    holder8.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, holder8.getAdapterPosition()));
                    holder8.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, null, holder8.getAdapterPosition()));
                    holder8.ivUser.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder8, holder8.getAdapterPosition()));
                    holder8.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ENTRY, holder8, holder8.getAdapterPosition()));

                    break;


                //Contest List items
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final ContestItem vo = list.get(position).getItem();

                    holder.ivArtist.setTypeface(iconFont);
                    holder.ivArtist.setText(Constant.FontIcon.USER);
                    holder.tvType.setTypeface(iconFont);
                    holder.tvType.setText(getIconByType(vo.getContestType()));
                    holder.tvType.setVisibility(TextUtils.isEmpty(vo.getContestType()) ? View.GONE : View.VISIBLE);
                    holder.tvType.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.ivDate.setTypeface(iconFont);
                    holder.ivDate2.setTypeface(iconFont);

                    holder.rlDate.setVisibility(View.GONE);

                    holder.tvEntryCount.setText(vo.getEntries());
                    if (vo.getTimeLeft() > 0) {
                        if (86400 > vo.getTimeLeft()) {
                            //show count down only if left-time is less than a day
                            handler.removeCallbacks(holder.customRunnable);
                            holder.customRunnable.canUpdateTime(true);
                            holder.customRunnable.setItem(vo);
                            holder.customRunnable.millisUntilFinished = vo.getTimeLeft();
                            handler.postDelayed(holder.customRunnable, 0);
                        } else {
                            holder.tvStatus.setText(getContestDateDiff(vo.getTimeLeft()));
                        }
                    } else {
                        holder.tvStatus.setText(vo.getStatus());
                    }

                    try {
                        if(vo.getVoteCount()<=1){
                            holder.tvVoteCount.setText(vo.getVoteCount()+" Vote");
                        }else {
                            holder.tvVoteCount.setText(vo.getVoteCount()+" Votes");
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    holder.tvVoteCount.setVisibility(null != vo.getVotes() ? View.VISIBLE : View.GONE);
                    holder.ivDate.setText(Constant.FontIcon.CALENDAR);
                    holder.ivDate2.setText(Constant.FontIcon.PLAY);

                    //hide join button if key "join" is null
                    holder.cvJoin.setVisibility(TextUtils.isEmpty(vo.getJoin()) ? View.GONE : View.VISIBLE);
                    holder.tvJoin.setText(vo.getJoin());

                    holder.tvStartTime.setText(vo.getCalanderStartTime());
                    holder.tvEndTime.setText(vo.getCalanderEndTime());

                    holder.tvTitle.setText(vo.getTitle());
                    holder.tvArtist.setText(vo.getOwnerTitle());
                    Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

                    holder.llReactionOption.setVisibility(isUserLoggedIn ? View.INVISIBLE : View.INVISIBLE);
                    holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFollow.setVisibility(vo.canFollow() ? View.VISIBLE : View.INVISIBLE);
                    if (vo.isShowAnimation() == 1) {
                        vo.setShowAnimation(0);
                        // holder.sbvLike.likeAnimation();
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    }



                    if (vo.isShowAnimation() == 2) {
                        vo.setShowAnimation(0);
                        // holder.sbvFavorite.likeAnimation();
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);

                    } else {
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                    }

                    if (vo.isShowAnimation() == 3) {
                        vo.setShowAnimation(0);
                        // holder.sbvFollow.likeAnimation();
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    } else {
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    }

                    holder.ivFbShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
                    holder.ivWhatsAppShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);
                    holder.ivImageShare.setVisibility(vo.getCanShare() == 0 ? View.GONE : View.VISIBLE);

                    holder.ivFbShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 1));
                    holder.ivWhatsAppShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 2));
                    holder.ivImageShare.setOnClickListener(v ->
                            listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 3));

                    holder.ivSaveFeed.setImageDrawable(vo.getShortcut_save().isIs_saved() ?  dUnsave:dSave);



                    holder.ivSaveFeed.setOnClickListener(v -> {
                        if(vo.getShortcut_save().isIs_saved()){
                            listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                        }else {
                            listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), 0);
                        }

                    });


                    holder.cvJoin.setOnClickListener(v -> listener.onItemClicked(Constant.Events.JOIN, null, holder.getAdapterPosition()));
                    holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, holder.getAdapterPosition()));
                    holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, null, holder.getAdapterPosition()));
                    holder.ivFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, null, holder.getAdapterPosition()));
                    holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private static String getIconByType(String contestType) {
        try {
            switch ("" + contestType) {
                case "1":
                    return Constant.FontIcon.TEXT;
                case "2":
                    return Constant.FontIcon.ALBUM;
                case "3":
                    return Constant.FontIcon.VIDEO;
                case "4":
                    return Constant.FontIcon.MUSIC;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
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
        //   HEADER_TYPE = headerType;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvType;
        protected TextView tvStartTime;
        protected TextView tvEndTime;
        protected TextView ivDate;
        protected TextView ivDate2;
        protected TextView tvEntryCount;
        protected TextView tvStatus;
        protected TextView tvVoteCount;
        protected TextView tvJoin;
        protected View llArtist;
        protected RelativeLayout rlDate;
        protected ImageView ivSongImage;
        protected RelativeLayout cvMain;
        protected View cvJoin;

        protected View llStatus;
        // protected View vShadow;

        protected View llReactionOption;
        protected ImageView ivFollow;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;
        //variable used to update time left for each item
        private CustomRunnable customRunnable;

        protected ImageView ivImageShare,ivWhatsAppShare,ivFbShare,ivSaveFeed;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                  cvMain = itemView.findViewById(R.id.cvMain);
                ivImageShare = itemView.findViewById(R.id.ivImageShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);

                tvTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivDate = itemView.findViewById(R.id.ivDate);
                ivDate2 = itemView.findViewById(R.id.ivDate2);
                tvStartTime = itemView.findViewById(R.id.tvStartTime);
                tvEndTime = itemView.findViewById(R.id.tvEndTime);
                rlDate = itemView.findViewById(R.id.rlDate);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                tvType = itemView.findViewById(R.id.tvType);
                cvJoin = itemView.findViewById(R.id.cvJoin);
                tvJoin = itemView.findViewById(R.id.tvJoin);

                llStatus = itemView.findViewById(R.id.llStatus);
                tvEntryCount = itemView.findViewById(R.id.tvEntryCount);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
                // vShadow = itemView.findViewById(R.id.vShadow);

                //Reaction views
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivFollow = itemView.findViewById(R.id.ivFollow);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                sbvFollow = itemView.findViewById(R.id.sbvFollow);

                customRunnable = new CustomRunnable(handler, tvStatus);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class WinnerHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView tvType;
        protected ImageView ivUser;
        protected TextView tvVoteCount;
        protected View llLocation;
        protected ImageView ivSongImage;
        protected CardView cvMain;
        protected ImageView ivRank;
        protected View vShadow;

        protected View llReactionOption;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;


        public WinnerHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvStats = itemView.findViewById(R.id.tvStats);

                llLocation = itemView.findViewById(R.id.llLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                tvType = itemView.findViewById(R.id.tvType);
                //  rlHeader = itemView.findViewById(R.id.rlHeader);
                ivRank = itemView.findViewById(R.id.ivRank);

                ivUser = itemView.findViewById(R.id.ivUser);

                vShadow = itemView.findViewById(R.id.vShadow);

                //Reaction views
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class EntryHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView tvType,ivArtist;
        protected ImageView ivUser;
        protected TextView tvVoteCount;
        protected TextView tvVote;
        protected View llLocation;
        protected ImageView ivSongImage;
        protected RelativeLayout cvMain;
        protected CardView cvVote;
        protected View vShadow;
        RelativeLayout rlDate;

        protected View llReactionOption;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected ImageView ivImageShare,ivWhatsAppShare,ivFbShare,ivSaveFeed;

        public EntryHolder(View itemView) {
            super(itemView);
            try {
                ivImageShare = itemView.findViewById(R.id.ivImageShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivArtist = itemView.findViewById(R.id.ivArtist);

                llLocation = itemView.findViewById(R.id.llLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                tvType = itemView.findViewById(R.id.tvType);
                //  rlHeader = itemView.findViewById(R.id.rlHeader);
                cvVote = itemView.findViewById(R.id.cvVote);
                tvVote = itemView.findViewById(R.id.tvVote);

                ivUser = itemView.findViewById(R.id.ivUser);

                vShadow = itemView.findViewById(R.id.vShadow);
                rlDate = itemView.findViewById(R.id.rlDate);

                //Reaction views
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class BannerHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvDesc;
        protected View v1;
        protected ImageView ivImage;
        protected View cvMain;


        public BannerHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                v1 = itemView.findViewById(R.id.v1);
                ivImage = itemView.findViewById(R.id.ivImage);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class MyContestHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvArtist;
        protected TextView tvType;
        protected TextView tvStats;
        protected TextView tvStatus;
        protected View ivOption;

        protected ImageView ivSongImage;
        protected View cvMain;


        public MyContestHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvType = itemView.findViewById(R.id.tvType);
                tvStats = itemView.findViewById(R.id.tvStats);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected ContestCategoryAdapter2 adapter;
        //protected Handler handler;
        // public Runnable runnable;

        public CategoryHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

    public class SuggestionHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected View tvMore;
        protected TextView tvCategory;
        protected SuggestionContestAdapter adapter;
        protected PageIndicatorView pageIndicatorView;

        public SuggestionHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

        }
    }

    public String getContestDateDiff(long diff) {
        String result;
        try {
            @SuppressWarnings("NumericOverflow")
            long diffDays = diff / (24 * 60 * 60);
            if (diffDays == 0) {
                result = (diff / (60 * 60) % 24) + "h "
                        + (diff / (60) % 60) + "m "
                        + (diff % 60) + "s";

            } else if (diffDays == 1) {
                result = diffDays + " " + txt_day_left;
            } else {
                result = diffDays + " " + txt_days_left;
            }
        } catch (Exception e) {
            result = "";
            CustomLog.e(e);
        }
        return result;
    }
}
