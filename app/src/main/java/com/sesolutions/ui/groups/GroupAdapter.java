package com.sesolutions.ui.groups;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.groups.GroupContent;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.contest.ContestCategoryAdapter2;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.page.PageAdapter;
import com.sesolutions.ui.page.PageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;


import java.util.List;

import static com.sesolutions.ui.groups.GroupFragment.TYPE_CATEGORY;
import static com.sesolutions.ui.groups.GroupFragment.TYPE_MANAGE;


public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PageVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_CATEGORY_SINGLE = "-4";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final String TXT_BY;
    private final String TXT_IN;
    private String type;
    public Drawable dSave;
    public  Drawable dUnsave;

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    protected GroupAdapter(List<PageVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        //  viewPool = new RecyclerView.RecycledViewPool();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dFollow = ContextCompat.getDrawable(context, R.drawable.follow_artist);
        dFollowSelected = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected);
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

        switch (list.get(viewType).getType()) {
              /*  case VT_BANNER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_banner, parent, false);
                return new BannerHolder(view);*/
            case VT_CATEGORY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                return new CategoryHolder(view);
            case VT_CATEGORY_SINGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                return new CategoryHolder2(view);
            case VT_CATEGORIES:
            case VT_SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            case TYPE_MANAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new MyEventHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page2, parent, false);
                return new ContactHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        // try {
        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);


        try {
            switch (list.get(position).getType()) {
                case VT_CATEGORY:
                    final CategoryHolder holder1 = (CategoryHolder) parentHolder;
                    if (holder1.adapter == null) {
                        /*set child item list*/
                        holder1.rvChild.setHasFixedSize(true);
                        //    holder.rvChild.setRecycledViewPool(viewPool);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                        holder1.rvChild.setLayoutManager(layoutManager);
                        holder1.adapter = new ContestCategoryAdapter2(list.get(holder1.getAdapterPosition()).getValue(), context, listener);
                        holder1.rvChild.setAdapter(holder1.adapter);


                    } else {
                        holder1.adapter.notifyDataSetChanged();
                    }
                    break;
                case VT_CATEGORY_SINGLE:
                    final CategoryHolder2 holder25 = (CategoryHolder2) parentHolder;
                    if (holder25.adapter2 == null) {
                        /*set child item list*/
                        holder25.rvChild.setHasFixedSize(true);
                        //    holder.rvChild.setRecycledViewPool(viewPool);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT2, StaggeredGridLayoutManager.HORIZONTAL);
                        holder25.rvChild.setLayoutManager(layoutManager);
                        holder25.adapter2 = new ContestCategoryAdapter(list.get(holder25.getAdapterPosition()).getValue(), context, listener);
                        holder25.rvChild.setAdapter(holder25.adapter2);
                    } else {
                        holder25.adapter2.notifyDataSetChanged();
                    }
                    break;

                case VT_SUGGESTION:
                    final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                    if (holder2.adapter == null) {
                        holder2.tvMore.setVisibility(View.GONE);
                        /*set child item list*/
                        holder2.tvCategory.setText(R.string.popular_groups);
                        holder2.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new SuggestionGroupAdapter(list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
                        holder2.rvChild.setAdapter(holder2.adapter);
                        holder2.pageIndicatorView.setCount(holder2.adapter.getItemCount());
                        //  holder2.pageIndicatorView.setUnselectedColor(Color.parseColor(Constant.dividerColor));
                        //  holder2.pageIndicatorView.setSelectedColor(Color.parseColor(Constant.colorPrimary));
                        holder2.rvChild.setOnSnapListener(position1 -> holder2.pageIndicatorView.setSelection(position1));
                    } else {
                        holder2.adapter.notifyDataSetChanged();
                        holder2.pageIndicatorView.setSelection(0);
                    }
                    break;
                case VT_CATEGORIES:
                    final SuggestionHolder holder3 = (SuggestionHolder) parentHolder;
                    final PageVo pageVo = list.get(position);
                    final CategoryPage cVo = pageVo.getValue();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionGroupAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }

                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));

                    break;
                case TYPE_MANAGE:
                    final MyEventHolder holder4 = (MyEventHolder) parentHolder;
                    final PageVo page1 = list.get(position);
                    final GroupContent myPage = page1.getValue();

                    holder4.ivCreatepage.setTypeface(iconFont);
                    holder4.ivCreatepage.setText(Constant.FontIcon.CALENDAR);
                    holder4.tvCreatepage.setText(Util.changeFormat(myPage.getCreation_date()));
                    holder4.llCreatepage.setVisibility(myPage.getCreation_date() != null ? View.VISIBLE : View.GONE);

                    holder4.ivArtist.setTypeface(iconFont);
                    holder4.ivArtist.setText(Constant.FontIcon.FOLDER);
                    holder4.tvArtist.setText(myPage.getCategory_title());

                    holder4.llArtist.setVisibility(myPage.getCategory_title() != null ? View.VISIBLE : View.GONE);
                    Util.showImageWithGlide(holder4.ivSongImage, myPage.getMainImageUrl(), context, R.drawable.placeholder_square);
                    holder4.tvSongTitle.setText(myPage.getTitle());
                    holder4.llLocation.setVisibility(View.GONE);
                    holder4.tvStats.setTypeface(iconFont);

                    String detail = "\uf164 " + myPage.getLike_count()
                            + "  \uf075 " + myPage.getComment_count()
                            + "  \uf06e " + myPage.getView_count()
                            + "  \uf004 " + myPage.getFavourite_count()
                            + "  \uf00c " + myPage.getFollow_count()
                            + "  \uf0c0 " + myPage.getMember_count();
                    holder4.tvStats.setText(detail);
                    holder4.ivOption.setVisibility(null != myPage.getButtons() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), myPage.getButtons()));
                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
                    break;
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final PageVo page = list.get(position);
                    final GroupContent vo = page.getValue();
                    holder.ivVerified.setVisibility(vo.getVerified() != 0 ? View.VISIBLE : View.GONE);
                    holder.ivArtist.setTypeface(iconFont);
                    holder.ivArtist.setText(Constant.FontIcon.USER);
                    holder.tvType.setText(vo.getCurrency() + " " + vo.getPrice());
                    holder.tvType.setVisibility(!TextUtils.isEmpty(vo.getPrice()) ? View.VISIBLE : View.GONE);
                    holder.tvStats.setTypeface(iconFont);
                    detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count()
                            + "  \uf004 " + vo.getFavourite_count()
                            + "  \uf00c " + vo.getFollow_count()
                            + "  \uf0c0 " + vo.getMember_count();
                    holder.tvStats.setText(detail);
                    //holder.tvType.setText(Constant.FontIcon.ALBUM);

                    if (!TextUtils.isEmpty(vo.getLocation())) {
                        holder.ivLocation.setTypeface(iconFont);
                        holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);
                        holder.tvLocation.setText(vo.getLocation());
                        holder.tvLocation.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_LOCATION, null, holder.getAdapterPosition()));
                        holder.llLocation.setVisibility(View.VISIBLE);
                    } else {
                        holder.llLocation.setVisibility(View.GONE);
                    }
                    holder.rlHeader.setVisibility(type.equals(PageFragment.TYPE_CATEGORY) ? View.VISIBLE : View.GONE);
                    holder.tvTitle.setText(vo.getTitle());

                    holder.tvTitle.setTextColor(Color.BLACK);
                    holder.tvCategoryName.setTextColor(Color.BLACK);
                    holder.tvArtist.setText(TXT_BY + vo.getOwner_title());
                    holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
                    holder.tvCategoryName.setText(vo.getCategory_title());

                    holder.tvCategoryName.setTypeface(iconFont);

                    if(vo.getCategory_title()!=null && vo.getCategory_title().length()>0 && !vo.getCategory_title().equalsIgnoreCase("null")){
                        String  detail132 = vo.getCategory_title()
                                + "   \uf0c0 " + vo.getMember_count();
                        holder.tvCategoryName.setText(detail132);
                        holder.tvCategoryName.setVisibility(View.VISIBLE);
                    }else {
                        holder.tvCategoryName.setVisibility(View.GONE);
                    }


                /* Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);
                  Util.showImageWithGlide(holder.ivImage, vo.getCoverImageUrl(), context, R.drawable.placeholder_square);*/
                    Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);
                    holder.ivUser.setVisibility(View.GONE);
                    //  Util.showImageWithGlide(holder.ivImage, vo.getCoverImageUrl(), context, R.drawable.placeholder_square);
                    Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
                    //   holder.ivOption.setVisibility(null != vo.getButtons() ? View.VISIBLE : View.GONE);
                    holder.ivOption.setVisibility(View.GONE);

                    if(vo.getButtons()!=null && vo.getButtons().size()==3){
                        String st=vo.getButtons().get(2).getName();
                        holder.buttonJoinid.setVisibility(View.VISIBLE);
                        holder.buttonJoinid.setText(""+vo.getButtons().get(2).getLabel());

                        holder.buttonJoinid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION, "" + holder.getAdapterPosition(), 2);
                            }
                        });

                    }else {
                        holder.buttonJoinid.setVisibility(View.GONE);
                    }



                    holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getButtons()));


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


                    //    holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
                    holder.llReactionOption.setVisibility(View.GONE);
                    holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);
                    holder.sbvFollow.setVisibility(vo.canFollow() ? View.VISIBLE : View.INVISIBLE);

                    if (vo.isShowAnimation() == 1) {
                        vo.setShowAnimation(0);
                        holder.sbvLike.likeAnimation();
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    }
                    if (vo.isShowAnimation() == 2) {
                        vo.setShowAnimation(0);
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                        holder.sbvFavorite.likeAnimation();
                    } else {
                        holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                    }

                    if (vo.isShowAnimation() == 3) {
                        vo.setShowAnimation(0);
                        holder.sbvFollow.likeAnimation();
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    } else {
                        holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
                    }

                    holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + vo, holder.getAdapterPosition()));
                    holder.ivFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, "" + vo, holder.getAdapterPosition()));
                    holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + vo, holder.getAdapterPosition()));
                    holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
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
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvType;
        protected TextView tvCategoryName;
        protected ImageView ivUser;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected View llArtist;
        protected View llLocation;
        protected ImageView ivImage;
        protected View cvMain;
        protected View llStatus;
        protected View vShadow;
        protected View ivOption;
        protected View llReactionOption;
        protected ImageView ivFollow;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        protected ImageView ivVerified;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;
        Button buttonJoinid;
        protected ImageView ivImageShare,ivWhatsAppShare,ivFbShare,ivSaveFeed;


        protected View rlHeader; //show layout in case of category


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                buttonJoinid = itemView.findViewById(R.id.buttonJoinid);
                ivImageShare = itemView.findViewById(R.id.ivImageShare);
                ivWhatsAppShare = itemView.findViewById(R.id.ivWhatsAppShare);
                ivFbShare = itemView.findViewById(R.id.ivFbShare);
                ivSaveFeed = itemView.findViewById(R.id.ivSaveFeed);


                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                llLocation = itemView.findViewById(R.id.llLocation);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvType = itemView.findViewById(R.id.tvType);
                rlHeader = itemView.findViewById(R.id.rlHeader);
                llStatus = itemView.findViewById(R.id.llStatus);
                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivFollow = itemView.findViewById(R.id.ivFollow);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                sbvFollow = itemView.findViewById(R.id.sbvFollow);
                ivVerified = itemView.findViewById(R.id.ivVerified);
               /* tvDate1 = itemView.findViewById(R.id.tvDate1);
                tvDate2 = itemView.findViewById(R.id.tvDate2);
                llDate = itemView.findViewById(R.id.llDate);*/
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected View llArtist;
        protected View llLocation;
        protected View ivOption;

        protected ImageView ivSongImage;
        protected CardView cvMain;


        public BannerHolder(View itemView) {
            super(itemView);
            try {
               /* cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                llLocation = itemView.findViewById(R.id.llLocation);*/
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class MyEventHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvStats,tvCreatepage,ivCreatepage;
        protected View llArtist;
        protected View llLocation,llCreatepage;
        protected View ivOption;

        protected ImageView ivSongImage;
        protected CardView cvMain;


        public MyEventHolder(View itemView) {
            super(itemView);
            try {

                ivCreatepage = itemView.findViewById(R.id.ivCreatepage);
                tvCreatepage = itemView.findViewById(R.id.tvCreatepage);
                llCreatepage = itemView.findViewById(R.id.llCreatepage);

                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                llLocation = itemView.findViewById(R.id.llLocation);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected ContestCategoryAdapter2 adapter;
        //protected Handler handler;
        // public Runnable runnable;

        public CategoryHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

    public static class SuggestionHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected View tvMore;
        protected TextView tvCategory;
        protected SuggestionGroupAdapter adapter;
        protected PageIndicatorView pageIndicatorView;

        public SuggestionHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

        }
    }

    public static class CategoryHolder2 extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected ContestCategoryAdapter adapter2;
        //protected Handler handler;
        // public Runnable runnable;

        public CategoryHolder2(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

}
