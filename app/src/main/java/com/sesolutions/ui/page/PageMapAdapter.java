package com.sesolutions.ui.page;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class PageMapAdapter extends RecyclerView.Adapter<PageMapAdapter.ContactHolder> {

    private final List<LocationActivity> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull PageMapAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public PageMapAdapter(List<LocationActivity> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.listFriends = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_map, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final LocationActivity vo = listFriends.get(position);
            if (null != vo.getTitle()) {
                holder.tvTitle.setText(vo.getTitle());
                holder.tvTitle.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setVisibility(View.GONE);
            }
            holder.tvLocation.setText(vo.getLocation());
            holder.tvState.setText(vo.getState());
            holder.tvCity.setText(vo.getCity());
            holder.tvZip.setText(vo.getZip());
            holder.tvCountry.setText(vo.getCountry());

            Util.showImageWithGlide(holder.ivImage,
                    Constant.URL_MAP_IMAGE_PRE +
                            vo.getLat()
                            + "," +
                            vo.getLng()
                            // + "&markers=color:blue"
                            + Constant.URL_MAP_IMAGE_POST + context.getString(R.string.places_api_key),
                    context, R.drawable.placeholder_3_2);

            holder.llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.FEED_MAP, holder, holder.getAdapterPosition()));


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return listFriends.size();
    }

/*    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }*/


    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvLocation, tvTitle;
        protected TextView tvCity;
        protected TextView tvState;
        protected TextView tvZip;
        protected TextView tvCountry;
        protected ImageView ivImage;
        protected View llMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvCity = itemView.findViewById(R.id.tvCity);
                tvState = itemView.findViewById(R.id.tvState);
                tvZip = itemView.findViewById(R.id.tvZip);
                tvCountry = itemView.findViewById(R.id.tvCountry);
                ivImage = itemView.findViewById(R.id.ivImage);
                llMain = itemView.findViewById(R.id.llMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
