package com.sesolutions.ui.store.product;

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
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.StoreVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

import static com.sesolutions.ui.page.PageFragment.TYPE_MANAGE;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<StoreVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_PRODUCTS = "-4";
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    public static final String WISHLIST = "sesproduct_main_browseplaylist";
    public static final String MY_WISHLIST = "my_wishlist";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav;
    private final Drawable dFavDark;
    private final String TXT_BY;
    private final String TXT_IN;
    private String type;
    private boolean isGrid;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public ProductAdapter(List<StoreVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
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
        dFavDark = ContextCompat.getDrawable(context, R.drawable.music_favourite_dark);
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
            case MY_WISHLIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new MyEventHolder(view);
            case WISHLIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
                return new ContactHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_product : R.layout.item_product_list_view, parent, false);
                return new ProductHolder(view);
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
                        holder2.adapter = new SuggestionProductAdapter((List<StoreContent>) list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
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
                    final StoreVo pageVo = list.get(position);
                    final CategoryPage cVo = pageVo.getValue();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionProductAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }

                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));

                    break;

                case WISHLIST:
                    final ContactHolder holder4 = (ContactHolder) parentHolder;
                    final StoreVo page = list.get(position);
                    final StoreContent vo = page.getValue();
                    holder4.ivArtist.setTypeface(iconFont);
                    holder4.ivArtist.setText(Constant.FontIcon.USER);
                    holder4.tvStats.setTypeface(iconFont);
                    String detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count()
                            + "  \uf004 " + vo.getFavourite_count()
                            + "  \uf00c " + vo.getFollow_count()
                            + "  \uf0c0 " + vo.getMember_count();
                    holder4.tvStats.setText(detail);
                    holder4.tvWishlist.setText(vo.getTitle());
                    holder4.tvArtist.setText(vo.getOwner_title());
                    holder4.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
                    holder4.tvDate.setText(Util.changeDateFormat(context, vo.getCreation_date()));

                    Util.showImageWithGlide(holder4.ivImage, vo.getMainImageUrl(), context, R.drawable.placeholder_square);

                    holder4.ivOption.setVisibility(null != vo.getMenus() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), vo.getMenus()));

                    holder4.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
                    holder4.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
                    holder4.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);

                    if (vo.isShowAnimation() == 1) {
                        vo.setShowAnimation(0);
                        holder4.sbvLike.likeAnimation();
                        holder4.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder4.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    }

                    if (vo.isShowAnimation() == 2) {
                        vo.setShowAnimation(0);
                        holder4.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                        holder4.sbvFavorite.likeAnimation();

                    } else {
                        holder4.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
                    }

                    holder4.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + vo, holder4.getAdapterPosition()));
                    holder4.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + vo, holder4.getAdapterPosition()));
                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.OPEN_WISHLIST, WISHLIST, holder4.getAdapterPosition()));
                    break;
                case TYPE_MANAGE:
//                    final MyEventHolder holder4 = (MyEventHolder) parentHolder;
//                    final PageVo page1 = list.get(position);
//                    final PageContent myPage = page1.getValue();
//                    holder4.ivArtist.setTypeface(iconFont);
//                    holder4.ivArtist.setText(Constant.FontIcon.FOLDER);
//                    holder4.tvArtist.setText(TXT_IN + myPage.getCategory_title());
//                    holder4.llArtist.setVisibility(myPage.getCategory_title() != null ? View.VISIBLE : View.GONE);
//                    Util.showImageWithGlide(holder4.ivSongImage, myPage.getMainImageUrl(), context, R.drawable.placeholder_square);
//                    holder4.tvSongTitle.setText(myPage.getTitle());
//                    holder4.llLocation.setVisibility(View.GONE);
//                    holder4.tvStats.setTypeface(iconFont);
//
//                    String detail = "\uf164 " + myPage.getLike_count()
//                            + "  \uf075 " + myPage.getComment_count()
//                            + "  \uf06e " + myPage.getView_count()
//                            + "  \uf004 " + myPage.getFavourite_count()
//                            + "  \uf00c " + myPage.getFollow_count();
//                    holder4.tvStats.setText(detail);
//                    holder4.ivOption.setVisibility(null != myPage.getButtons() ? View.VISIBLE : View.GONE);
//                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), myPage.getButtons()));
//                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
//                    break;

                case MY_WISHLIST:

                    final MyEventHolder holder5 = (MyEventHolder) parentHolder;
                    final StoreVo page1 = list.get(position);
                    final StoreContent myPage = page1.getValue();
                    holder5.ivArtist.setTypeface(iconFont);
                    holder5.ivArtist.setText(Constant.FontIcon.FOLDER);
                    holder5.tvArtist.setText(TXT_BY + myPage.getOwner_title());
                    holder5.llArtist.setVisibility(myPage.getCategory_title() != null ? View.VISIBLE : View.GONE);
                    Util.showImageWithGlide(holder5.ivSongImage, myPage.getMainImageUrl(), context, R.drawable.placeholder_square);
                    holder5.tvSongTitle.setText(myPage.getTitle());
                    holder5.llLocation.setVisibility(View.GONE);
                    holder5.tvStats.setTypeface(iconFont);

                    String detail2 = "\uf164 " + myPage.getLike_count()
                            + "  \uf075 " + myPage.getComment_count()
                            + "  \uf004 " + myPage.getFavourite_count()
                            + "  \uf06e " + myPage.getView_count()
                            + "  \uf00c " + myPage.getFollow_count();
                    holder5.tvStats.setText(detail2);
                    holder5.ivOption.setVisibility(null != myPage.getMenus() ? View.VISIBLE : View.GONE);
                    holder5.ivOption.setOnClickListener(v -> showOptionsPopUp(holder5.ivOption, holder5.getAdapterPosition(), myPage.getMenus()));
                    holder5.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.OPEN_WISHLIST, MY_WISHLIST, holder5.getAdapterPosition()));
                    break;

                default:
                    try {
                        final ProductHolder holder = (ProductHolder) parentHolder;
                        themeManager.applyTheme((ViewGroup) holder.itemView, context);
                        final StoreVo storeVo = list.get(position);
                        final StoreContent pvo = storeVo.getValue();
                        holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, /*vo.getProductId()*/holder.getAdapterPosition()));
//            holder.tvVerified.setVisibility(vo.getVerified() == 1 ? View.VISIBLE : View.GONE);
                        holder.tvTitle.setText(pvo.getTitle());

                        holder.tvArtist.setText(pvo.getOwner_title());
                        holder.tvArtist.setVisibility(null != pvo.getOwner_title() ? View.VISIBLE : View.GONE);
                        holder.tvCategory.setText(pvo.getCategory_title());

                        if (pvo.getDiscount() == 1) {
                            holder.tvOff.setText(pvo.getProductPrice());
                            holder.tvOff.setVisibility(View.VISIBLE);
                            holder.tvPrice2.setVisibility(View.VISIBLE);
                            try {
                                double newdoble=Double.parseDouble(pvo.getPriceWithDiscount())*100;
                                int newpr= (int) (newdoble);
                                double myprr=((double)newpr)/100.00;
                                holder.tvPrice.setText(pvo.getCurrency() + myprr);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                holder.tvPrice.setText(pvo.getCurrency() + pvo.getPriceWithDiscount());
                            }

                            try {
                                double newdoble=Double.parseDouble(pvo.getPrice())*100;
                                int newpr= (int) (newdoble);
                                double myprr=((double)newpr)/100.00;
                                holder.tvPrice2.setText(pvo.getCurrency() + myprr);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                holder.tvPrice2.setText(pvo.getCurrency() + pvo.getPrice());
                            }

                            StoreUtil.strikeThroughText(holder.tvPrice2);
                        } else {
                            try {
                                double newdoble=Double.parseDouble(pvo.getPrice())*100;
                                int newpr= (int) (newdoble);
                                double myprr=((double)newpr)/100.00;
                                holder.tvPrice.setText(pvo.getCurrency() + myprr);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                holder.tvPrice.setText(pvo.getCurrency() + pvo.getPriceWithDiscount());
                            }
                        }
                        holder.tvRatingCount.setText(pvo.getRating());
                        holder.tvRatingTotal.setText(pvo.getReviewCount());
//            holder.tvStoreDesc.setText(vo.getDescription());
                        Util.showImageWithGlide(holder.ivImage, pvo.getMainImageUrl(), context, R.drawable.placeholder_square);
//            holder.tvCategoryName.setText(vo.getCategory_title());
//            holder.ivOption.setVisibility(null != vo.getButtons() ? View.VISIBLE : View.GONE);
//            holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getButtons()));


//            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
//            holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
                        holder.sbvFavorite.setVisibility(pvo.canFavourite() ? View.VISIBLE : View.INVISIBLE);
//            holder.sbvFollow.setVisibility(vo.canFollow() ? View.VISIBLE : View.INVISIBLE);

//            if (vo.isShowAnimation() == 1) {
//                vo.setShowAnimation(0);
//                holder.sbvLike.likeAnimation();
//                holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
//            } else {
//                holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
//            }

                        if (pvo.isShowAnimation() == 2) {
                            pvo.setShowAnimation(0);
                            holder.ivFavorite.setImageDrawable(pvo.isContentFavourite() ? dFavSelected : dFavDark);
                            holder.sbvFavorite.likeAnimation();

                        } else {
                            holder.ivFavorite.setImageDrawable(pvo.isContentFavourite() ? dFavSelected : dFavDark);
                        }
//
//            if (vo.isShowAnimation() == 3) {
//                vo.setShowAnimation(0);
//                holder.sbvFollow.likeAnimation();
//                holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
//            } else {
//                holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
//            }

                        holder.tvAddToCart.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_CART, holder, holder.getAdapterPosition()));
                        holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, holder, /*vo.getProductId()*/ holder.getAdapterPosition()));


                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
//                    if (isGrid) {
//                        final ProductHolder holder = (ProductHolder) parentHolder;
//                        if (holder.productChildAdapter == null) {
//                            /*set child item list*/
//                            holder.rvCommonProduct.setHasFixedSize(true);
//                            holder.rvCommonProduct.setLayoutManager(new GridLayoutManager(context, Constant.SPAN_COUNT));
//                            ((SimpleItemAnimator)holder.rvCommonProduct.getItemAnimator()).setSupportsChangeAnimations(false);
//                            holder.productChildAdapter = new ProductChildAdapter((List<StoreContent>)list.get(holder.getAdapterPosition()).getValue(), context, listener, true);
//                            holder.rvCommonProduct.setAdapter(holder.productChildAdapter);
//                        } else {
//                            holder.productChildAdapter.notifyDataSetChanged();
//                        }
//                        break;
//                    }
//                    final ProductHolder holder = (ProductHolder) parentHolder;
//                    if (holder.productChildAdapter == null) {
//                        /*set child item list*/
//                        holder.rvCommonProduct.setHasFixedSize(true);
//                        holder.rvCommonProduct.setLayoutManager(new LinearLayoutManager(context));
//                        ((SimpleItemAnimator)holder.rvCommonProduct.getItemAnimator()).setSupportsChangeAnimations(false);
//                        holder.productChildAdapter = new ProductChildAdapter((List<StoreContent>) list.get(holder.getAdapterPosition()).getValue(), context, listener, false);
//                        holder.rvCommonProduct.setAdapter(holder.productChildAdapter);
//                    } else {
//                        holder.productChildAdapter.notifyDataSetChanged();
//                    }
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

    public void setStoreLayoutGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

//    public static class ProductHolder extends RecyclerView.ViewHolder {
//
//        protected RecyclerView rvCommonProduct;
//        protected ProductChildAdapter productChildAdapter;
//
//        public ProductHolder(View itemView) {
//            super(itemView);
//            rvCommonProduct = itemView.findViewById(R.id.rvCommon);
//
//        }
//    }

    public static class ProductHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist, tvOff, tvRatingTotal, tvRatingCount;
        protected ImageView ivImage;
        protected ImageView ivFavorite;

        protected TextView tvStoreDesc, tvType, tvCategory, tvAddToCart, tvPrice, tvPrice2, tvStock;
        protected View cvMain, vShadow;
        protected ImageView ivVerified, ivOption;
        protected SmallBangView sbvFavorite;


        public ProductHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                tvOff = itemView.findViewById(R.id.tvOff);
//            ivArtist = itemView.findViewById(R.id.ivArtist);
//            llArtist = itemView.findViewById(R.id.llArtist);
                tvStock = itemView.findViewById(R.id.tvStock);
//            ivLocation = itemView.findViewById(R.id.ivLocation);
//            tvLocation = itemView.findViewById(R.id.tvLocation);
                tvRatingTotal = itemView.findViewById(R.id.tvRatingTotal);
                tvRatingCount = itemView.findViewById(R.id.tvRatingCount);

                ivImage = itemView.findViewById(R.id.ivImage);
//            tvType = itemView.findViewById(R.id.tvType);
//            llStatus = itemView.findViewById(R.id.llStatus);
//            ivUser = itemView.findViewById(R.id.ivUser);
                tvCategory = itemView.findViewById(R.id.tvCategory);
//            ivOption = itemView.findViewById(R.id.ivOption);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                tvAddToCart = itemView.findViewById(R.id.tvAddToCart);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvPrice2 = itemView.findViewById(R.id.tvPrice2);
//                cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, null, getAdapterPosition()));

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
        protected SuggestionProductAdapter adapter;
        protected PageIndicatorView pageIndicatorView;

        public SuggestionHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

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

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, tvWishlist;
        protected TextView tvStats;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvType;
        protected TextView tvCategoryName;
        protected TextView tvDate;
        protected View llArtist;
        protected View llDate;
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

        protected View rlHeader; //show layout in case of category


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
//                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvWishlist = itemView.findViewById(R.id.tvWishlist);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvStats = itemView.findViewById(R.id.tvStats);
                tvDate = itemView.findViewById(R.id.tvDate);
                llDate = itemView.findViewById(R.id.llDate);
                ivImage = itemView.findViewById(R.id.ivImage);
//                tvType = itemView.findViewById(R.id.tvType);
//                rlHeader = itemView.findViewById(R.id.rlHeader);
//                llStatus = itemView.findViewById(R.id.llStatus);
//                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
//                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
                llReactionOption = itemView.findViewById(R.id.llReactionOption);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
//                ivFollow = itemView.findViewById(R.id.ivFollow);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
//                sbvFollow = itemView.findViewById(R.id.sbvFollow);
                ivVerified = itemView.findViewById(R.id.ivVerified);
               /* tvDate1 = itemView.findViewById(R.id.tvDate1);
                tvDate2 = itemView.findViewById(R.id.tvDate2);
                llDate = itemView.findViewById(R.id.llDate);*/
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
