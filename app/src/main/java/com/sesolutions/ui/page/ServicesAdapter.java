package com.sesolutions.ui.page;

import android.content.Context;
import android.graphics.Typeface;
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
import com.sesolutions.responses.page.PageServices;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ContactHolder> {

    private final List<PageServices> listServices;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final Typeface iconFont;
    private final boolean isAnnouncement;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull ServicesAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }


    public ServicesAdapter(List<PageServices> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.listServices = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.isAnnouncement = false;
        this.listener = listener;
        themeManager = new ThemeManager();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_services, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final PageServices eventItem = listServices.get(position);
            holder.tvTitle.setText(eventItem.getTitle());
            holder.tvDesc.setText(eventItem.getDescription());
            holder.tvPrice.setText("" + eventItem.getPrice());
            holder.tvPrice.setVisibility(eventItem.getPrice() > 0 ? View.VISIBLE : View.GONE);
            Util.showImageWithGlide(holder.ivImage, eventItem.getImages() != null ? eventItem.getImages().getMain() : " ", context, R.drawable.placeholder_square);
            holder.tvHours.setText(eventItem.getDurationString());
            holder.tvHours.setVisibility(eventItem.getDuration() > 0 ? View.VISIBLE : View.GONE);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition());
                }
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return listServices.size();
    }

/*    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }*/


    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvPrice;
        protected TextView tvDesc;
        protected TextView tvHours;
        protected ImageView ivImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvHours = itemView.findViewById(R.id.tvHours);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivImage = itemView.findViewById(R.id.ivImage);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
