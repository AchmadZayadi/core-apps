package com.sesolutions.ui.store;

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
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import static com.sesolutions.ui.page.PageFragment.TYPE_MANAGE;


public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<StoreVo> list;
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
    private static String type;
    private boolean isGrid;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public StoreAdapter(List<StoreVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, boolean isGrid) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.isGrid = isGrid;
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

        Log.e("TYPEDATA",""+list.get(viewType).getType());
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
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_common_vertical_list, parent, false);
                view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_store : R.layout.item_store_list_view, parent, false);
//                return new StoreHolder(view);
                return new ContactHolder2(view);
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
                        holder2.adapter = new SuggestionStoreAdapter((List<StoreContent>) list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
                        holder2.adapter.setType(type);
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
                    final StoreVo storeVo = list.get(position);
                    final CategoryPage cVo = storeVo.getValue();
                    if (holder3.adapter == null) {
                        holder3.tvCategory.setText(cVo.getCategoryName());
                        holder3.tvMore.setVisibility(cVo.isSeeAll() ? View.VISIBLE : View.GONE);
                        /*set child item list*/
                        holder3.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionStoreAdapter(cVo.getItems(), context, listener, false);
                        holder3.adapter.setType(type);
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
                    final StoreVo page1 = list.get(position);
                    final StoreContent myPage = page1.getValue();
                    holder4.ivArtist.setTypeface(iconFont);
                    Log.e("My pages",""+myPage.getCategory_title());
                    holder4.ivArtist.setText(Constant.FontIcon.FOLDER);
                    holder4.tvArtist.setText(TXT_IN + myPage.getCategory_title());
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
                    holder4.ivOption.setVisibility(null != myPage.getMenus() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), myPage.getMenus()));
                    holder4.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
                    break;
                default:
                    final ContactHolder2 holder = (ContactHolder2) parentHolder;
                        themeManager.applyTheme((ViewGroup) holder.itemView, context);
                        final StoreVo content = list.get(position);
                        // todo casting of list to storecontent
                        final StoreContent vo = content.getValue();

                        holder.ivVerified.setVisibility(vo.getVerified() == 1 ? View.VISIBLE : View.GONE);
                        holder.tvTitle.setText(vo.getTitle());
                        holder.tvArtist.setText(vo.getOwner_title());
                        holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);

                        if (!TextUtils.isEmpty(vo.getDescription())) {
                            holder.tvStoreDesc.setVisibility(View.VISIBLE);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                holder.tvStoreDesc.setText(Html.fromHtml(unecodeStr(vo.getDescription()), Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                holder.tvStoreDesc.setText(Html.fromHtml(unecodeStr(vo.getDescription())));
                            }
                            holder.tvStoreDesc.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            holder.tvStoreDesc.setVisibility(View.GONE);
                        }

                        Util.showImageWithGlide(holder.ivImage, vo.getMainImageUrl(), context, R.drawable.placeholder_square);
                        holder.tvCategoryName.setText(vo.getCategory_title());
                        holder.ivOption.setVisibility(null != vo.getMenus() ? View.VISIBLE : View.GONE);
                        holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus()));
                        holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, vo.getStoreId() /*holder.getAdapterPosition()*/));


//                    if (isGrid) {
//                        final StoreHolder holder = (StoreHolder) parentHolder;
//                        if (holder.storeChildAdapter == null) {
//                            /*set child item list*/
//                            holder.rvCommonStore.setHasFixedSize(true);
//                            holder.rvCommonStore.setLayoutManager(new GridLayoutManager(context, Constant.SPAN_COUNT));
//                            ((SimpleItemAnimator)holder.rvCommonStore.getItemAnimator()).setSupportsChangeAnimations(false);
//                            holder.storeChildAdapter = new StoreChildAdapter((List<StoreContent>)list.get(holder.getAdapterPosition()).getValue(), context, listener, true);
//                            holder.rvCommonStore.setAdapter(holder.storeChildAdapter);
//                        } else {
//                            holder.storeChildAdapter.notifyDataSetChanged();
//                        }
//                        break;
//                    }
//                    final StoreHolder holder = (StoreHolder) parentHolder;
//                    if (holder.storeChildAdapter == null) {
//                        /*set child item list*/
//                        holder.rvCommonStore.setHasFixedSize(true);
//                        holder.rvCommonStore.setLayoutManager(new LinearLayoutManager(context));
//                        ((SimpleItemAnimator)holder.rvCommonStore.getItemAnimator()).setSupportsChangeAnimations(false);
//                        holder.storeChildAdapter = new StoreChildAdapter((List<StoreContent>) list.get(holder.getAdapterPosition()).getValue(), context, listener, false);
//                        holder.rvCommonStore.setAdapter(holder.storeChildAdapter);
//                    } else {
//                        holder.storeChildAdapter.notifyDataSetChanged();
//                    }
                    break;
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

    public static class StoreHolder extends RecyclerView.ViewHolder {

        protected RecyclerView rvCommonStore;
        protected StoreChildAdapter storeChildAdapter;

        public StoreHolder(View itemView) {
            super(itemView);
            rvCommonStore = itemView.findViewById(R.id.rvCommon);

        }
    }

    public static class ContactHolder2 extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist, tvOff;
        protected ImageView ivImage;
        protected ImageView ivUser;

        protected TextView tvStoreDesc,tvType,tvCategoryName;
        protected View cvMain,vShadow;
        protected ImageView ivVerified,ivOption;


        public ContactHolder2(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvStoreDesc = itemView.findViewById(R.id.tvStoreDesc);
                tvArtist = itemView.findViewById(R.id.tvArtist);
//                tvOff = itemView.findViewById(R.id.tvOff);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvType = itemView.findViewById(R.id.tvType);
                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
                ivVerified = itemView.findViewById(R.id.ivVerified);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, tvStoreDesc;
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

        protected View rlHeader; //show layout in case of category


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvStoreDesc = itemView.findViewById(R.id.tvStoreDesc);
                tvArtist = itemView.findViewById(R.id.tvArtist);
//                ivArtist = itemView.findViewById(R.id.ivArtist);
//                llArtist = itemView.findViewById(R.id.llArtist);
//                tvStats = itemView.findViewById(R.id.tvStats);
//                ivLocation = itemView.findViewById(R.id.ivLocation);
//                tvLocation = itemView.findViewById(R.id.tvLocation);
//                llLocation = itemView.findViewById(R.id.llLocation);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvType = itemView.findViewById(R.id.tvType);
//                rlHeader = itemView.findViewById(R.id.rlHeader);
//                llStatus = itemView.findViewById(R.id.llStatus);
                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
//                llReactionOption = itemView.findViewById(R.id.llReactionOption);
//                ivLike = itemView.findViewById(R.id.ivLike);
//                ivFavorite = itemView.findViewById(R.id.ivFavorite);
//                ivFollow = itemView.findViewById(R.id.ivFollow);
//                sbvLike = itemView.findViewById(R.id.sbvLike);
//                sbvFavorite = itemView.findViewById(R.id.sbvFavorite);
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
        protected SuggestionStoreAdapter adapter;
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
