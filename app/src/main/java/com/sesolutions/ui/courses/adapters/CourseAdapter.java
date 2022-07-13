package com.sesolutions.ui.courses.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
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
import com.sesolutions.responses.Courses.course.CourseContent;
import com.sesolutions.responses.Courses.course.CourseVo;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

import jp.shts.android.library.TriangleLabelView;

import static com.sesolutions.ui.page.PageFragment.TYPE_CATEGORY;
import static com.sesolutions.ui.page.PageFragment.TYPE_MANAGE;


public class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CourseVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_HOT = "-1";
    public final String VT_FEATURED = "-4";
    public final String VT_VERIFIED = "-5";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable addDrawable;
    private final Drawable dLike;
    public static final String MY_WISHLIST = "my_wishlist";
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final String TXT_BY;
    private final String TXT_IN;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public CourseAdapter(List<CourseVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
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
            case VT_HOT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_classroom_hot, parent, false);
                return new SuggestionHolder(view);
            case VT_FEATURED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_classroom_featured, parent, false);
                return new SuggestionHolder(view);
            case VT_VERIFIED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_classroom_verified, parent, false);
                return new SuggestionHolder(view);
            case MY_WISHLIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_wishlist, parent, false);
                return new MyEventHolder(view);
            case VT_CATEGORIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            case TYPE_MANAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new MyEventHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
                return new ContactHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);

        try {
            switch (list.get(position).getType()) {
                case VT_CATEGORY:
                    final CategoryHolder holder1 = (CategoryHolder) parentHolder;
                    if (holder1.adapter == null) {
                        /*set child item list*/
                        holder1.rvChild.setHasFixedSize(true);
                        //    holder.rvChild.setRecycledViewPool(viewPool);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder1.rvChild.setLayoutManager(layoutManager);
                        holder1.adapter = new ContestCategoryAdapter((List<CategoryPage>) list.get(holder1.getAdapterPosition()).getValue(), context, listener);
                        holder1.rvChild.setAdapter(holder1.adapter);
                    } else {
                        holder1.adapter.notifyDataSetChanged();
                    }
                    break;
                case VT_HOT:
                case VT_FEATURED:
                case VT_VERIFIED:
                    final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                    if (holder2.adapter == null) {
                        holder2.tvMore.setVisibility(View.GONE);
                        /*set child item list*/
                        holder2.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new SuggestionCourseAdapter((List<CourseContent>) list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
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
                    final CourseVo pageVo = list.get(position);
                    final CategoryPage cVo = pageVo.getValue();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionCourseAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }

                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));

                    break;
                case MY_WISHLIST:
                    final MyEventHolder holder5 = (MyEventHolder) parentHolder;
                    final CourseVo page1 = list.get(position);
                    final CourseContent myPage = page1.getValue();
                    holder5.ivArtist.setTypeface(iconFont);
                    holder5.ivArtist.setText(Constant.FontIcon.FOLDER);
                    holder5.tvArtist.setText(TXT_BY + myPage.getOwner_title());
                    holder5.llArtist.setVisibility(myPage.getCategory_title() != null ? View.VISIBLE : View.GONE);
                    Util.showImageWithGlide(holder5.ivSongImage, myPage.getShare().getImageUrl(), context, R.drawable.placeholder_square);
                    holder5.tvSongTitle.setText(myPage.getTitle());
                    holder5.llLocation.setVisibility(View.GONE);
                    holder5.tvStats.setTypeface(iconFont);

                    String detail2 =
//                            + "  \uf075 " + myPage.getComment_count()
//                            + "  \uf004 " + myPage.getFavourite_count()
                            "  \uf06e " + myPage.getView_count()
                                    + "  \uf07c " + myPage.getCourse_count();
                    holder5.tvStats.setText(detail2);
                    holder5.ivOption.setVisibility(null != myPage.getMenus() ? View.VISIBLE : View.GONE);
                    holder5.ivOption.setOnClickListener(v -> showOptionsPopUp(holder5.ivOption, holder5.getAdapterPosition(), myPage.getMenus()));
                    holder5.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.OPEN_WISHLIST, MY_WISHLIST, holder5.getAdapterPosition()));
                    break;
                case TYPE_MANAGE:
                    final MyEventHolder holder4 = (MyEventHolder) parentHolder;
                    final CourseVo page2 = list.get(position);
                    final CourseContent myPage2 = page2.getValue();
                    holder4.ivArtist.setTypeface(iconFont);
                    holder4.ivArtist.setText(Constant.FontIcon.FOLDER);
//                    holder4.tvArtist.setText(TXT_IN + myPage.getCategory_title());
                    holder4.llArtist.setVisibility(myPage2.getCategory_title() != null ? View.VISIBLE : View.GONE);
                    Util.showImageWithGlide(holder4.ivSongImage, myPage2.getMainImageUrl(), context, R.drawable.placeholder_square);
                    holder4.tvSongTitle.setText(myPage2.getTitle());
                    holder4.llLocation.setVisibility(View.GONE);
                    holder4.tvStats.setTypeface(iconFont);

                    String detail = "\uf164 " + myPage2.getLike_count()
                            + "  \uf075 " + myPage2.getComment_count()
                            + "  \uf06e " + myPage2.getView_count()
                            + "  \uf004 " + myPage2.getFavourite_count()
                            + "  \uf00c " + myPage2.getFollow_count();
                    //   + "  \uf0c0 " + myPage.getMember_count();
                    holder4.tvStats.setText(detail);
                    holder4.ivOption.setVisibility(null != myPage2.getButtons() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), myPage2.getButtons()));
                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
                    break;
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final CourseVo page = list.get(position);
                    final CourseContent vo = page.getValue();
                    if (!TextUtils.isEmpty(vo.getOriginalPrice())) {
                        holder.tvOriginalPrice.setText(vo.getOriginalPrice());
                        holder.view20.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvOriginalPrice.setVisibility(View.GONE);
                        holder.view20.setVisibility(View.GONE);
                    }

                    holder.tvDiscount.setVisibility(View.GONE);

                    if (!TextUtils.isEmpty(vo.getDiscount())) {
                        holder.tvDiscount.setText(vo.getDiscount());
                        holder.percantageoffdata.setPrimaryText(""+vo.getDiscount());
                        holder.percantageoffdata.setVisibility(View.VISIBLE);
                        holder.tvDiscount.setVisibility(View.GONE);
                    } else {
                        holder.tvDiscount.setVisibility(View.GONE);
                        holder.percantageoffdata.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(vo.getDiscountedPrice())) {
                        holder.tvDiscountedPrice.setText(vo.getDiscountedPrice());
                    } else {
                        holder.tvDiscountedPrice.setVisibility(View.GONE);

                    }
       //            holder.Dprice.setVisibility(!TextUtils.isEmpty(vo.getDPrice()) ? View.VISIBLE : View.GONE);
                    holder.tvDiscount.setVisibility(!TextUtils.isEmpty(vo.getDiscount()) ? View.GONE : View.GONE);

                    detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count()
                            + "  \uf004 " + vo.getFavourite_count()
                            + "  \uf00c " + vo.getFollow_count()
                            + "  \uf0c0 " + vo.getMember_count();

                    holder.tvDescCL.setText(Html.fromHtml(vo.getDescription()));
                    if (!TextUtils.isEmpty(vo.getCategory_title())) {
//                        holder.ivLocation.setTypeface(iconFont);
//                        holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);
                        String desc2 = "Posted in "+ "<b>" + vo.getCategory_title() + "</b>" ;
                        holder.tvLocation.setText(Html.fromHtml(desc2));
//                        holder.tvLocation.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_LOCATION, null, holder.getAdapterPosition()));
                        holder.llLocation.setVisibility(View.VISIBLE);
                    } else {
                        holder.llLocation.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(vo.getOwner_title())) {
                        String desc1 = "By " + "<b>" + vo.getOwner_title() + "</b>";
                        holder.tvCat.setText(Html.fromHtml(desc1));
                    } else {
                        holder.tvCat.setVisibility(View.GONE);
                    }
                    holder.rlHeader.setVisibility(type.equals(TYPE_CATEGORY) ? View.VISIBLE : View.GONE);
                    holder.tvTitle.setText(vo.getTitle());

                    Util.showImageWithGlide(holder.ivImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);

                    if(SPref.getInstance().isLoggedIn(context)){
//                        holder.ivOption.setVisibility(View.GONE);
                        holder.ivOption2.setVisibility(View.VISIBLE);
//                        holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_WISHLIST, holder, holder.getAdapterPosition()));
                        holder.ivOption2.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_CART, holder, holder.getAdapterPosition()));
                    } else {
//                        holder.ivOption.setVisibility(View.GONE);
                        holder.ivOption2.setVisibility(View.GONE);
                    }

                    holder.llReactionOption.setVisibility(isUserLoggedIn ? View.INVISIBLE : View.INVISIBLE);
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
        protected TextView tvDescCL;
        //        protected TextView tvStats;
//        protected TextView tvArtist;
//        protected TextView ivArtist;
        protected TextView tvOriginalPrice;
        protected TextView tvDiscountedPrice;
        TriangleLabelView percantageoffdata;
        protected TextView tvDiscount;
        protected View llCategory;
        //        protected TextView tvCategoryName;
//        protected ImageView ivUser;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected View llArtist;
        protected View llLocation;
        protected ImageView ivImage;
        protected ImageView ivFree;
        protected TextView tvCat;
        protected View cvMain;
        protected View llStatus;
        protected View vShadow;
        protected ImageView ivOption;
        protected ImageView ivOption2;
        protected View llReactionOption;
        protected ImageView ivFollow;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        //        protected ImageView ivVerified;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;

        protected View rlHeader; //show layout in case of category
        protected View view20; //show layout in case of category


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescCL = itemView.findViewById(R.id.tvDescCL);
                tvCat = itemView.findViewById(R.id.tvCat);
//                tvArtist = itemView.findViewById(R.id.tvArtist);
//                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                llCategory = itemView.findViewById(R.id.llCategory);
//                tvStats = itemView.findViewById(R.id.tvStats);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                llLocation = itemView.findViewById(R.id.llLocation);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
                percantageoffdata = itemView.findViewById(R.id.percantageoffdata);
                tvDiscountedPrice = itemView.findViewById(R.id.tvDiscountedPrice);
                tvDiscount = itemView.findViewById(R.id.tvDiscount);
                view20 = itemView.findViewById(R.id.view20);

                rlHeader = itemView.findViewById(R.id.rlHeader);
                llStatus = itemView.findViewById(R.id.llStatus);
//                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
//                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
                ivOption2 = itemView.findViewById(R.id.ivOption2);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                ivFollow = itemView.findViewById(R.id.ivFollow);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                sbvFollow = itemView.findViewById(R.id.sbvFollow);
//                ivVerified = itemView.findViewById(R.id.ivVerified);
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
        //        protected TextView tvArtist;
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
        protected TextView tvStats;
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
        protected ContestCategoryAdapter adapter;
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
        protected SuggestionCourseAdapter adapter;
        protected PageIndicatorView pageIndicatorView;

        public SuggestionHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

        }
    }
}
