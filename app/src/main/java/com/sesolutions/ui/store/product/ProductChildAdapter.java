package com.sesolutions.ui.store.product;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class ProductChildAdapter extends RecyclerView.Adapter<ProductChildAdapter.ContactHolder> {

    private final List<StoreContent> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final Drawable addDrawable;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFollow;
    private final Drawable dFollowSelected;
    private final Drawable dFav, dFavDark;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isGrid;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    public ProductChildAdapter(List<StoreContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isGrid) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isGrid = isGrid;
        themeManager = new ThemeManager();
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
    }

    @NonNull
    @Override
    public ProductChildAdapter.ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_product : R.layout.item_product_list_view, parent, false);
        return new ProductChildAdapter.ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductChildAdapter.ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StoreContent vo = list.get(position);

//            holder.tvVerified.setVisibility(vo.getVerified() == 1 ? View.VISIBLE : View.GONE);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(vo.getOwner_title());
            holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
            holder.tvCategory.setText(vo.getCategory_title());

            if(vo.getDiscount() == 1) {
                holder.tvOff.setText(vo.getProductPrice());
                holder.tvOff.setVisibility(View.VISIBLE);
                holder.tvPrice2.setVisibility(View.VISIBLE);
                try {
                    double newdoble=Double.parseDouble(vo.getPriceWithDiscount())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    holder.tvPrice.setText(vo.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.tvPrice.setText(vo.getCurrency() + vo.getPriceWithDiscount());
                }

                try {
                    double newdoble=Double.parseDouble(vo.getPrice())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    holder.tvPrice2.setText(vo.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.tvPrice2.setText(vo.getCurrency() + vo.getPrice());
                }
                StoreUtil.strikeThroughText(holder.tvPrice2);
            } else{
                try {
                    double newdoble=Double.parseDouble(vo.getPrice())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    holder.tvPrice.setText(vo.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.tvPrice.setText(vo.getCurrency() + vo.getPrice());
                }
            }

            holder.tvRatingCount.setText(vo.getRating());
            holder.tvRatingTotal.setText(vo.getReviewCount());
//            holder.tvStoreDesc.setText(vo.getDescription());
            Util.showImageWithGlide(holder.ivImage, vo.getMainImageUrl(), context, R.drawable.placeholder_square);
//            holder.tvCategoryName.setText(vo.getCategory_title());
//            holder.ivOption.setVisibility(null != vo.getButtons() ? View.VISIBLE : View.GONE);
//            holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getButtons()));


//            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);
//            holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.INVISIBLE);
            holder.sbvFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.INVISIBLE);
//            holder.sbvFollow.setVisibility(vo.canFollow() ? View.VISIBLE : View.INVISIBLE);

//            if (vo.isShowAnimation() == 1) {
//                vo.setShowAnimation(0);
//                holder.sbvLike.likeAnimation();
//                holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
//            } else {
//                holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
//            }

            if (vo.isShowAnimation() == 2) {
                vo.setShowAnimation(0);
                holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFavDark);
                holder.sbvFavorite.likeAnimation();

            } else {
                holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFavDark);
            }
//
//            if (vo.isShowAnimation() == 3) {
//                vo.setShowAnimation(0);
//                holder.sbvFollow.likeAnimation();
//                holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
//            } else {
//                holder.ivFollow.setImageDrawable(vo.isContentFollow() ? dFollowSelected : dFollow);
//            }

            holder.ivFavorite.setVisibility(View.GONE);
            holder.tvAddToCart.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ADD_TO_CART, holder, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, holder, /*vo.getProductId()*/ holder.getAdapterPosition()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, /*vo.getProductId()*/vo.getProductId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist, tvOff,tvRatingTotal,tvRatingCount;
        protected ImageView ivImage;
        protected ImageView ivFavorite;

        protected TextView tvStoreDesc, tvType, tvCategory, tvAddToCart, tvPrice, tvPrice2, tvStock;
        protected View cvMain, vShadow;
        protected ImageView ivVerified, ivOption;
        protected SmallBangView sbvFavorite;


        public ContactHolder(View itemView) {
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
}
