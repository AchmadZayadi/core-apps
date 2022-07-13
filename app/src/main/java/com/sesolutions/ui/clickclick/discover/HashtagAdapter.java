package com.sesolutions.ui.clickclick.discover;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;


public class HashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Videos> list;
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

    public HashtagAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover, parent, false);
        return new CategoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, int position) {

        themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);

        try {

            final CategoryHolder holder1 = (CategoryHolder) parentHolder;
            final Videos vo = list.get(position);
            /*set child item list*/
            if(list.get(holder1.getAdapterPosition()).getResult().getVideos().size()>0) {
                holder1.llTop.setVisibility(View.VISIBLE);
                holder1.rvChild.setHasFixedSize(true);
                holder1.tvCategoryName.setText("#"+vo.getTag().getText());
                holder1.tvTrending.setVisibility(View.VISIBLE);
                holder1.tvSeeAll.setText("See All >");
                holder1.tvSeeAll.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_COMPLETED, "" + vo.getTitle(), holder1.getAdapterPosition()));

                //    holder.rvChild.setRecycledViewPool(viewPool);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder1.rvChild.setLayoutManager(layoutManager);
                holder1.adapter = new HashtagVideosAdapter((List<Videos>) list.get(holder1.getAdapterPosition()).getResult().getVideos(), context, listener);
                holder1.rvChild.setAdapter(holder1.adapter);
                holder1.adapter.notifyDataSetChanged();
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


    public static class CategoryHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected LinearLayout llDiscover;
        protected LinearLayout llTop;
        protected HashtagVideosAdapter adapter;
        protected AppCompatTextView tvCategoryName;
        protected AppCompatTextView tvTrending;
        protected AppCompatTextView tvSeeAll;
        //protected Handler handler;
        // public Runnable runnable;

        public CategoryHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            llDiscover = itemView.findViewById(R.id.llDiscover);
            tvSeeAll = itemView.findViewById(R.id.tvSeeAll);
            llTop = itemView.findViewById(R.id.llTop);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvTrending = itemView.findViewById(R.id.tvTrending);
        }
    }

}
