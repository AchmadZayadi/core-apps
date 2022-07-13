package com.sesolutions.ui.event_core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.events.EventCategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;


public class CEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CommonVO> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    /*   private final Drawable dLike;
       private final Drawable dLikeSelected;
       private final Drawable addDrawable;
       private final Drawable dFavSelected;
       private final Drawable dFav;*/
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final String TXT_BY;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private String type;
    private String subType = "";
    private boolean hasOptions = true;

    public void setHasOptions(boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public CEventAdapter(List<CommonVO> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        themeManager = new ThemeManager();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type) {
            case CEventHelper.TYPE_MANAGE:

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new MyEventHolder(view);


           /* case UpcomingEventFragment.TYPE_VIEW_CATEGORY:
                switch (list.get(viewType).getItemType()) {
                    case 1:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_banner, parent, false);
                        return new BannerHolder(view);
                    case 2:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                        return new SuggestionHolder(view);
                    default:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                        return new ContactHolder(view);
                }*/
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                return new ContactHolder(view);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);

            final CommonVO vo = list.get(position);
            if (parentHolder instanceof ContactHolder) {
                final ContactHolder holder = (ContactHolder) parentHolder;
                holder.ivArtist.setTypeface(iconFont);
                holder.ivArtist.setText(Constant.FontIcon.CALENDAR);
                holder.ivArtist2.setTypeface(iconFont);
                holder.ivArtist2.setText(Constant.FontIcon.PLAY);
                holder.ivLocation.setTypeface(iconFont);
                holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);

                holder.tvStats.setTypeface(iconFont);
                String dateString = Util.changeDate(vo.getStartTime());
                try {
                    holder.llDate.setVisibility(View.VISIBLE);
                    holder.llDate.setBackgroundColor(Color.parseColor(Constant.colorPrimary));
                    holder.tvDate1.setText(dateString.split(" ")[1]);
                    holder.tvDate2.setText(dateString.split(" ")[0]);
                } catch (Exception e) {
                    //it means date is null so hide date layout
                    holder.llDate.setVisibility(View.GONE);
                }
                if (TextUtils.isEmpty(vo.getTitle())) {
                    holder.tvSongTitle.setVisibility(View.GONE);
                } else {
                    holder.tvSongTitle.setVisibility(View.VISIBLE);
                    holder.tvSongTitle.setText(vo.getTitle());
                }

                holder.tvStartTime.setText(Util.changeDateFormat(vo.getStartTime(), "MMM dd, yyyy hh:mm a"));
                holder.tvEndTime.setText(Util.changeDateFormat(vo.getEndTime(), "MMM dd, yyyy hh:mm a"));
                holder.tvLocation.setText(vo.getLocationString());

                holder.llLocation.setVisibility(TextUtils.isEmpty(vo.getLocationString()) ? View.GONE : View.VISIBLE);
                holder.tvFeatured.setVisibility(vo.isFeatured() ? View.VISIBLE : View.GONE);
                holder.tvSponsored.setVisibility(vo.isSponsored() ? View.VISIBLE : View.GONE);
                holder.tvHot.setVisibility(vo.isVerified() ? View.VISIBLE : View.INVISIBLE);


                String detail = "\uf164 " + vo.getLikeCount()
                        + "  \uf075 " + vo.getCommentCount()
                        //   + "  \uf004 " + vo.getFavouriteCount()
                        + "  \uf06e " + vo.getViewCount();
                holder.tvStats.setText(detail);
                Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);

                holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
                holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.GONE);
                if (vo.isShowAnimation() == 1) {
                    vo.setShowAnimation(0);
                    holder.sbvLike.likeAnimation();
                    holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                } else {
                    holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                }

                holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + vo, holder.getAdapterPosition()));

                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

            } else if (parentHolder instanceof BannerHolder) {
                final BannerHolder holder = (BannerHolder) parentHolder;
                Util.showImageWithGlide(holder.ivImage, vo.getCategory().getImageUrl(), context, R.drawable.placeholder_square);
                holder.tvTitle.setText(vo.getCategory().getName());
                holder.tvDesc.setText(vo.getCategory().getDescription());
            } else if (parentHolder instanceof SuggestionHolder) {
                final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                if (holder2.adapter == null) {
                    holder2.tvCategory.setText(R.string.text_sub_category);
                    holder2.tvMore.setVisibility(View.GONE);
                    /*set child item list*/
                    holder2.rvChild.setHasFixedSize(true);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    holder2.rvChild.setLayoutManager(layoutManager);
                    holder2.adapter = new EventCategoryAdapter(list.get(holder2.getAdapterPosition()).getSubCategory(), listener, context);
                    holder2.adapter.setCategoryLevel(list.get(holder2.getAdapterPosition()).getCategoryLevel());
                    holder2.rvChild.setAdapter(holder2.adapter);
                    holder2.pageIndicatorView.setCount(holder2.adapter.getItemCount());
                    holder2.rvChild.setOnSnapListener(position1 -> holder2.pageIndicatorView.setSelection(position1));
                } else {
                    holder2.adapter.notifyDataSetChanged();
                    holder2.pageIndicatorView.setSelection(0);
                }
            } else if (parentHolder instanceof ArtistHolder) {
                final ArtistHolder holder = (ArtistHolder) parentHolder;
                holder.tvStats.setTypeface(iconFont);

                if (TextUtils.isEmpty(vo.getHostName())) {
                    holder.tvSongTitle.setVisibility(View.GONE);
                } else {
                    holder.tvSongTitle.setVisibility(View.VISIBLE);
                    holder.tvSongTitle.setText(vo.getHostName());
                }

                String detail = "\uf073 " + vo.getEventCount()
                        + "   \uf0c0 " + vo.getFollowCount()
                        + "   \uf06e " + vo.getViewCount();
                // + "   \uf004 " + vo.getFavouriteCount();
                holder.tvStats.setText(detail);
                Util.showImageWithGlide(holder.ivSongImage, vo.getImage(), context, R.drawable.placeholder_square);

                holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);

                holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + vo, holder.getAdapterPosition()));

                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

                holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions(), listener));

            } else if (parentHolder instanceof MyEventHolder) {
                final MyEventHolder holder = (MyEventHolder) parentHolder;
                holder.ivArtist.setTypeface(iconFont);
                holder.ivArtist.setText(Constant.FontIcon.CALENDAR);
                holder.ivLocation.setTypeface(iconFont);
                holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);

                holder.tvArtist.setText(Util.changeDate(vo.getStartTime()));
                holder.tvLocation.setText(vo.getLocationString());
                Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
                holder.llLocation.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_LOCATION, null, holder.getAdapterPosition()));
                holder.llLocation.setVisibility(TextUtils.isEmpty(vo.getLocationString()) ? View.GONE : View.VISIBLE);

                holder.ivOption.setVisibility(View.GONE);

                if (TextUtils.isEmpty(vo.getTitle())) {
                    holder.tvSongTitle.setVisibility(View.GONE);
                } else {
                    holder.tvSongTitle.setVisibility(View.VISIBLE);
                    holder.tvSongTitle.setText(vo.getTitle());
                }

                holder.ivOption.setVisibility(hasOptions ? View.GONE : View.GONE);
                holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_OPTION, holder.ivOption, holder.getAdapterPosition()));
                //holder.ivOption.setOnClickListener(v -> );


                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
            } else if (parentHolder instanceof BrowseListHolder) {
                final BrowseListHolder holder = (BrowseListHolder) parentHolder;
                holder.tvStats.setTypeface(iconFont);
                String detail = "\uf073 " + vo.getEventCount()
                        //   + "  \uf004 " + vo.getFavouriteCount()
                        + "  \uf06e " + vo.getViewCount()
                        + "  \uf164 " + vo.getLikeCount();
                holder.tvStats.setText(detail);
                holder.tvUserTitle.setText(TXT_BY + vo.getOwnerTitle());
                holder.tvUserTitle.setVisibility(TextUtils.isEmpty(vo.getOwnerTitle()) ? View.GONE : View.VISIBLE);
                Util.showImageWithGlide(holder.ivImage, vo.getImage(), context, R.drawable.placeholder_square);
                Util.showImageWithGlide(holder.ivUserImage, vo.getOwnerImageUrl(), context, R.drawable.placeholder_square);


                if (TextUtils.isEmpty(vo.getTitle())) {
                    holder.tvTitle.setVisibility(View.GONE);
                } else {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(vo.getTitle());
                }

                holder.ivUserImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

                holder.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getOptions(), listener));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setType(String type) {
        this.type = type;
        //this.layoutId = type.equals(UpcomingEventFragment.TYPE_MANAGE) ? R.layout.item_my_event : R.layout.item_event;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvStats;
        protected TextView tvEndTime;
        protected TextView tvStartTime;
        protected TextView ivArtist;
        protected TextView ivArtist2;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected TextView tvDate1;
        protected TextView tvDate2;
        protected TextView tvFeatured;
        protected TextView tvSponsored;
        protected TextView tvHot;
        // protected View rlArtist;
        protected View llLocation;
        protected ImageView ivSongImage;
        protected ImageView ivLike;
        // protected ImageView ivFavorite;
        // protected ImageView ivAdd;
        protected CardView cvMain;
        protected View llReactionOption;
        protected View llDate;
        protected SmallBangView sbvLike;
        //  protected SmallBangView sbvFavorite;
        // protected SmallBangView sbvFollow;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvStartTime = itemView.findViewById(R.id.tvStartTime);
                tvEndTime = itemView.findViewById(R.id.tvEndTime);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                ivArtist2 = itemView.findViewById(R.id.ivArtist2);
                //itemView.findViewById(R.id.rlArtist).setVisibility(View.GONE);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                llLocation = itemView.findViewById(R.id.llLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                tvDate1 = itemView.findViewById(R.id.tvDate1);
                tvDate2 = itemView.findViewById(R.id.tvDate2);
                llDate = itemView.findViewById(R.id.llDate);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                tvFeatured = itemView.findViewById(R.id.tvFeatured);
                tvSponsored = itemView.findViewById(R.id.tvSponsored);
                tvHot = itemView.findViewById(R.id.tvHot);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                itemView.findViewById(R.id.sbvFavorite).setVisibility(View.GONE);
                //  sbvFollow = itemView.findViewById(R.id.sbvFollow);

                ivLike = itemView.findViewById(R.id.ivLike);
                // ivFavorite = itemView.findViewById(R.id.ivFavorite);
                itemView.findViewById(R.id.sbvFollow).setVisibility(View.GONE);


            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class ArtistHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvStats;
        protected ImageView ivSongImage;

        protected ImageView ivFavorite;
        protected CardView cvMain;
        protected View llReactionOption;
        protected View ivOption;
        protected SmallBangView sbvFavorite;


        public ArtistHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivOption = itemView.findViewById(R.id.ivOption);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class MyEventHolder extends RecyclerView.ViewHolder {

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


        public MyEventHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                llLocation = itemView.findViewById(R.id.llLocation);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {


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

    public static class SuggestionHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected View tvMore;
        protected TextView tvCategory;
        protected EventCategoryAdapter adapter;
        protected PageIndicatorView pageIndicatorView;

        public SuggestionHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

        }
    }

    public static class BrowseListHolder extends RecyclerView.ViewHolder {

        protected TextView tvUserTitle;
        protected TextView tvTitle;
        protected TextView tvStats;
        protected ImageView ivUserImage;
        protected ImageView ivImage;
        protected View ivOption;
        protected View cvMain;


        public BrowseListHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvUserTitle = itemView.findViewById(R.id.tvUserTitle);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivUserImage = itemView.findViewById(R.id.ivUserImage);
                ivOption = itemView.findViewById(R.id.ivOption);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }


}
