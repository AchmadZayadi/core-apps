package com.sesolutions.ui.courses.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.sesolutions.responses.Courses.Lecture.LectureContent;
import com.sesolutions.responses.Courses.Lecture.LectureVo;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageContent;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.page.SuggestionPageAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

import static com.sesolutions.ui.page.PageFragment.TYPE_CATEGORY;
import static com.sesolutions.ui.page.PageFragment.TYPE_MANAGE;


public class LectureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<LectureVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
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


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public LectureAdapter(List<LectureVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
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
            case VT_SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);

            case VT_CATEGORIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            case TYPE_MANAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new MyEventHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false);
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
                case VT_SUGGESTION:
                    final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                    if (holder2.adapter == null) {
                        holder2.tvMore.setVisibility(View.GONE);
                        /*set child item list*/
                        holder2.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new SuggestionPageAdapter((List<PageContent>) list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
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
                    final LectureVo pageVo = list.get(position);
                    final CategoryPage cVo = pageVo.getValue();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionPageAdapter(cVo.getItems(), context, listener, false);
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
                    final LectureVo page1 = list.get(position);
                    final LectureContent myPage = page1.getValue();
                    holder4.ivArtist.setTypeface(iconFont);
                    holder4.ivArtist.setText(Constant.FontIcon.FOLDER);
//                    holder4.tvArtist.setText(TXT_IN + myPage.getCategory_title());
                    holder4.llArtist.setVisibility(myPage.getCategory_title() != null ? View.VISIBLE : View.GONE);
                    Util.showImageWithGlide(holder4.ivSongImage, myPage.getMainImageUrl(), context, R.drawable.placeholder_square);
                    holder4.tvSongTitle.setText(myPage.getTitle());
                    holder4.llLocation.setVisibility(View.GONE);
                    holder4.tvStats.setTypeface(iconFont);

                    String detail = "\uf164 " + myPage.getLike_count()
                            + "  \uf075 " + myPage.getComment_count()
                            + "  \uf06e " + myPage.getView_count()
                            + "  \uf004 " + myPage.getFavourite_count()
                            + "  \uf00c " + myPage.getFollow_count();
                    //   + "  \uf0c0 " + myPage.getMember_count();
                    holder4.tvStats.setText(detail);
                    holder4.ivOption.setVisibility(null != myPage.getButtons() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), myPage.getButtons()));
                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
                    break;
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    final LectureVo page = list.get(position);
                    final LectureContent vo = page.getValue();
//                    holder.ivVerified.setVisibility(vo.getVerified() != 0 ? View.VISIBLE : View.GONE);
//                    holder.ivArtist.setTypeface(iconFont);
//                    holder.ivArtist.setText(Constant.FontIcon.USER);
//                    holder.tvType.setText(vo.getCurrency() + " " + vo.getPrice());
//                    holder.tvType.setVisibility(!TextUtils.isEmpty(vo.getPrice()) ? View.VISIBLE : View.GONE);
//                    holder.tvStats.setTypeface(iconFont);
                    detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count()
                            + "  \uf004 " + vo.getFavourite_count()
                            + "  \uf00c " + vo.getFollow_count()
                            + "  \uf0c0 " + vo.getMember_count();
//                    holder.tvStats.setText(detail);
                    //holder.tvType.setText(Constant.FontIcon.ALBUM);
//                    holder.tvDescCL.setText(Html.fromHtml(vo.getDescription()));
//                    if (!TextUtils.isEmpty(vo.getCategory_title())) {
//                        holder.ivLocation.setTypeface(iconFont);
//                        holder.ivLocation.setText(Constant.FontIcon.MAP_MARKER);
//                        holder.tvLocation.setText("Posted in "+vo.getCategory_title() + " By " +vo.getOwner_title());
//                        holder.tvLocation.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_LOCATION, null, holder.getAdapterPosition()));
//                        holder.llLocation.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.llLocation.setVisibility(View.GONE);
//                    }

                    holder.tvStats2.setTypeface(iconFont);
                    String Stats = "Video > " + vo.getDuration();

                    if (vo.getDuration().equalsIgnoreCase("0")) {
                        holder.tvStats2.setVisibility(View.GONE);
                    } else {
                        holder.tvStats2.setText(Stats);
                        holder.tvStats2.setVisibility(View.VISIBLE);
                    }
                    holder.rlHeader.setVisibility(type.equals(TYPE_CATEGORY) ? View.VISIBLE : View.GONE);
                    holder.tvTitle.setText(vo.getTitle());
//                    holder.tvArtist.setText(TXT_BY + vo.getOwner_title());
//                    holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
//                    holder.tvCategoryName.setText(TXT_IN + vo.getCategory_title());
//                    Util.showImageWithGlide(holder.ivUser, vo.getImageUrl(), context, R.drawable.placeholder_square);

                    Util.showImageWithGlide(holder.ivImage, vo.getImage(), context, R.drawable.placeholder_square);
                    if (vo.getImage() != null) {
                        Util.showImageWithGlide(holder.ivLecture, vo.getImage(), context, R.drawable.placeholder_square);
                    } else {
                        holder.ivLecture.setVisibility(View.GONE);
                    }
                    if (!vo.getDuration().equalsIgnoreCase("0")) {
                        holder.ivVideo.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivText.setVisibility(View.VISIBLE);
                    }
//                    holder.ivOption.setVisibility(View.VISIBLE);
//                    holder.ivOption.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_WISHLIST, holder, holder.getAdapterPosition()));
//
//                    holder.ivOption2.setVisibility(View.VISIBLE);
//                    holder.ivOption2.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_CART, holder, holder.getAdapterPosition()));

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
//        protected TextView tvType;
        //        protected TextView tvCategoryName;
//        protected ImageView ivUser;
        protected TextView tvLocation;
        protected TextView ivLocation;
        protected TextView tvStats2;
        protected View llArtist;
        protected View llLocation;
        protected ImageView ivImage;
        protected ImageView ivLecture;
        protected View cvMain;
        protected View llStatus;
        protected View vShadow;
        //        protected View ivOption;
        protected View ivOption2;
        protected View llReactionOption;
        protected ImageView ivFollow;
        protected ImageView ivText;
        protected ImageView ivVideo;
        protected ImageView ivFavorite;
        protected ImageView ivLike;
        //        protected ImageView ivVerified;
        protected SmallBangView sbvLike;
        protected SmallBangView sbvFavorite;
        protected SmallBangView sbvFollow;

        protected View rlHeader; //show layout in case of category


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescCL = itemView.findViewById(R.id.tvDescCL);
//                tvArtist = itemView.findViewById(R.id.tvArtist);
//                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
//                tvStats = itemView.findViewById(R.id.tvStats);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                llLocation = itemView.findViewById(R.id.llLocation);
                ivImage = itemView.findViewById(R.id.ivImage);
//                tvType = itemView.findViewById(R.id.tvType);
                rlHeader = itemView.findViewById(R.id.rlHeader);
                ivLecture = itemView.findViewById(R.id.ivLecture);
                llStatus = itemView.findViewById(R.id.llStatus);
                tvStats2 = itemView.findViewById(R.id.tvStats2);
//                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
//                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
//                ivOption = itemView.findViewById(R.id.ivOption);
                ivText = itemView.findViewById(R.id.ivText);
                ivVideo = itemView.findViewById(R.id.ivVideo);
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
        //        protected TextView tvArtist;
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
//                tvArtist = itemView.findViewById(R.id.tvArtist);
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
        protected SuggestionPageAdapter adapter;
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
