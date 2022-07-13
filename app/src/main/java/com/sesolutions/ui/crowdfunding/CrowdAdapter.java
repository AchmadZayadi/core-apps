package com.sesolutions.ui.crowdfunding;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.animate.bang.SmallBangView;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.ContestCategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;

import static com.sesolutions.ui.crowdfunding.CrowdFragment.TYPE_MANAGE;
import static com.sesolutions.ui.crowdfunding.CrowdFragment.TYPE_MY_DONATION;
import static com.sesolutions.ui.crowdfunding.CrowdFragment.TYPE_RECIEVED_DONATION;


public class CrowdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PageVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_BANNER = "-4";
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    protected CrowdAdapter(List<PageVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
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
        switch (list.get(viewType).getType()) {
          /*  case VT_BANNER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_banner, parent, false);
                return new BannerHolder(view);*/
            case VT_CATEGORY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                return new CategoryHolder(view);
            case VT_BANNER:
            case VT_SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);

            case VT_CATEGORIES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_page_suggestion, parent, false);
                return new SuggestionHolder(view);
            case TYPE_MY_DONATION:
            case TYPE_RECIEVED_DONATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
                return new DonationHolder(view);
            case TYPE_MANAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crowd_manage, parent, false);
                return new ContactHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crowdfunding, parent, false);
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
                        //final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        // holder1.rvChild.setLayoutManager(layoutManager);
                        holder1.adapter = new ContestCategoryAdapter(list.get(holder1.getAdapterPosition()).getValue(), context, listener);
                        holder1.rvChild.setAdapter(holder1.adapter);
                    } else {
                        holder1.adapter.notifyDataSetChanged();
                    }
                    break;
               /* case VT_BANNER:
                    final CategoryHolder<IconCategoryAdapter> holder6 = (CategoryHolder) parentHolder;
                    if (holder6.adapter == null) {
                        holder6.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                         holder1.rvChild.setLayoutManager(layoutManager);
                        holder6.adapter = new IconCategoryAdapter(list.get(holder6.getAdapterPosition()).getValue(), context, listener);
                        holder6.rvChild.setAdapter(holder6.adapter);
                    } else {
                        holder6.adapter.notifyDataSetChanged();
                    }
                    break;*/
                case VT_SUGGESTION:
                    final SuggestionHolder holder2 = (SuggestionHolder) parentHolder;
                    if (holder2.adapter == null) {
                        holder2.tvMore.setVisibility(View.GONE);
                        /*set child item list*/
                        holder2.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder2.rvChild.setLayoutManager(layoutManager);
                        holder2.adapter = new SuggestionFundAdapter(list.get(holder2.getAdapterPosition()).getValue(), context, listener, false);
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
                        //final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        // holder3.rvChild.setLayoutManager(layoutManager);
                        holder3.adapter = new SuggestionFundAdapter(cVo.getItems(), context, listener, false);
                        holder3.rvChild.setAdapter(holder3.adapter);
                        holder3.pageIndicatorView.setCount(holder3.adapter.getItemCount());
                        holder3.rvChild.setOnSnapListener(position12 -> holder3.pageIndicatorView.setSelection(position12));
                    } else {
                        holder3.adapter.notifyDataSetChanged();
                        holder3.pageIndicatorView.setSelection(0);
                    }

                    holder3.tvMore.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CATEGORY, cVo.getCategoryName(), cVo.getCategoryId()));

                    break;

                case TYPE_MY_DONATION:
                case TYPE_RECIEVED_DONATION:
                    final DonationHolder holder7 = (DonationHolder) parentHolder;
                    FundContent vo = list.get(position).getValue();
                    holder7.ivArtist.setTypeface(iconFont);
                    holder7.ivLocation.setTypeface(iconFont);
                    holder7.ivArtist.setText(Constant.FontIcon.FOLDER);
                    holder7.tvArtist.setText(context.getString(TYPE_RECIEVED_DONATION.equals(list.get(position).getType()) ? R.string.donated_by_donor : R.string.received_by_sender, vo.getOwnerTitle()));
                    Util.showImageWithGlide(holder7.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
                    holder7.tvSongTitle.setText(vo.getCategoryTitle());
                    //  holder7.llLocation.setVisibility(View.GONE);
                    holder7.tvLocation.setText(context.getString(R.string.on_date, Util.changeFormatDonation(vo.getCreationDate())));
                    holder7.ivLocation.setText(Constant.FontIcon.CALENDAR);

                    holder7.tvStats.setText(vo.getDonationLabel());
                    holder7.tvStats.setTextColor(SesColorUtils.getText1Color(context));
                    holder7.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                    holder7.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder7.ivOption, holder7.getAdapterPosition(), vo.getOptions(), listener));
                    holder7.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, null, holder7.getAdapterPosition()));
                    break;
                case TYPE_MANAGE:
                    final ContactHolder holder4 = (ContactHolder) parentHolder;
                    // final PageVo page = list.get(position);
                    vo = list.get(position).getValue();
                    holder4.ivArtist.setTypeface(iconFont);
                    holder4.ivArtist.setText(Constant.FontIcon.USER);
                    holder4.tvStats.setTypeface(iconFont);
                    String detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count();
                    holder4.tvStats.setText(detail);
                    holder4.tvStatus.setText(vo.getStatusLabel());
                    holder4.tvStatus.setTextColor(SesColorUtils.getCrowdTextColor(context, vo.isExpired()));
                    holder4.tvRaised.setText(vo.getGainAmount());
                    holder4.tvDonor.setText(vo.getDonorCountStr());
                    holder4.tvGoal.setText(vo.getTotalAmount());

                    holder4.sbProgress.setProgress(vo.getProgressPercent());
                    holder4.tvTitle.setText(vo.getTitle());
                    holder4.tvArtist.setText(context.getString(R.string.by_owner, vo.getOwnerTitle()));
                    holder4.ivCategory.setTypeface(iconFont);
                    holder4.ivCategory.setText(Constant.FontIcon.FOLDER);
                    holder4.tvCategoryName.setText(context.getString(R.string.in_content, vo.getCategoryTitle()));

                    Util.showImageWithGlide(holder4.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

                    holder4.ivOption.setVisibility(null != vo.getOptions() ? View.VISIBLE : View.GONE);
                    holder4.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder4.ivOption, holder4.getAdapterPosition(), vo.getOptions(), listener));
                    holder4.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder4, holder4.getAdapterPosition()));
                    break;
                default:
                    final ContactHolder holder = (ContactHolder) parentHolder;
                    // final PageVo page = list.get(position);
                    vo = list.get(position).getValue();
                    holder.ivArtist.setTypeface(iconFont);
                    holder.ivArtist.setText(Constant.FontIcon.USER);
                    holder.tvStats.setTypeface(iconFont);
                    detail = "\uf164 " + vo.getLike_count()
                            + "  \uf075 " + vo.getComment_count()
                            + "  \uf06e " + vo.getView_count();
                    holder.tvStats.setText(detail);
                    holder.tvStatus.setText(vo.getStatusLabel());
                    holder.tvStatus.setTextColor(SesColorUtils.getCrowdTextColor(context, vo.isExpired()));
                    holder.tvRaised.setText(vo.getGainAmount());
                    holder.tvDonor.setText(vo.getDonorCountStr());
                    holder.tvGoal.setText(vo.getTotalAmount());

                    holder.sbProgress.setProgress(vo.getProgressPercent());
                    holder.tvTitle.setText(vo.getTitle());
                    holder.tvArtist.setText(context.getString(R.string.by_owner, vo.getOwnerTitle()));
                    holder.ivCategory.setTypeface(iconFont);
                    holder.ivCategory.setText(Constant.FontIcon.FOLDER);
                    holder.tvCategoryName.setText(context.getString(R.string.in_content, vo.getCategoryTitle()));

                    Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);

                    //holder.ivOption.setVisibility(null != vo.getButtons() ? View.VISIBLE : View.GONE);
                    //holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getButtons()));

                    holder.sbvLike.setVisibility(vo.canLike() ? View.VISIBLE : View.GONE);

                    if (vo.isShowAnimation() == 1) {
                        vo.setShowAnimation(0);
                        holder.sbvLike.likeAnimation();
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    } else {
                        holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
                    }

                    holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + vo, holder.getAdapterPosition()));
                    holder.rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));
                    break;
            }
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
        //protected TextView tvType;
        protected TextView tvCategoryName, ivCategory, tvStatus, tvRaised, tvDonor, tvGoal;
        //protected TextView tvLocation;
        //  protected TextView ivLocation;
        // protected View llLocation;
        protected ImageView ivImage;
        protected View rlMain;
        protected View llStatus;
        //  protected View vShadow;
        protected View ivOption;
        protected ImageView ivLike;
        protected SmallBangView sbvLike;
        protected ProgressBar sbProgress;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rlMain = itemView.findViewById(R.id.rlMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvOwnerName);
                ivArtist = itemView.findViewById(R.id.ivOwner);
                tvStats = itemView.findViewById(R.id.tvStats);
                ivImage = itemView.findViewById(R.id.ivImage);
                llStatus = itemView.findViewById(R.id.llStatus);
                //vShadow = itemView.findViewById(R.id.vShadow);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivCategory = itemView.findViewById(R.id.ivCategory);

                ivLike = itemView.findViewById(R.id.ivLike);
                sbvLike = itemView.findViewById(R.id.sbvLike);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvRaised = itemView.findViewById(R.id.tvRaised);
                tvDonor = itemView.findViewById(R.id.tvDonor);
                tvGoal = itemView.findViewById(R.id.tvGoal);

                ivOption = itemView.findViewById(R.id.ivOption);
                sbProgress = itemView.findViewById(R.id.sbProgress);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

   /* public static class BannerHolder extends RecyclerView.ViewHolder {

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
               *//* cvMain = itemView.findViewById(R.id.cvMain);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                llLocation = itemView.findViewById(R.id.llLocation);*//*
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }*/

    public static class DonationHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvArtist;
        protected TextView ivArtist;
        protected TextView tvStats, ivLocation, tvLocation;
        protected View llArtist;
        protected View llLocation;
        protected View ivOption;

        protected ImageView ivSongImage;
        protected CardView cvMain;


        DonationHolder(View itemView) {
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
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivLocation = itemView.findViewById(R.id.ivLocation);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    public static class CategoryHolder<T extends RecyclerView.Adapter> extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected T adapter;

        CategoryHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

    public static class SuggestionHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected View tvMore;
        protected TextView tvCategory;
        protected SuggestionFundAdapter adapter;
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
