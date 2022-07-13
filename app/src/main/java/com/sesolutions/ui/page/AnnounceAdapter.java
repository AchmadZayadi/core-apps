package com.sesolutions.ui.page;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class AnnounceAdapter extends RecyclerView.Adapter<AnnounceAdapter.ContactHolder> {

    private final List<Announcement> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final Typeface iconFont;

    private boolean owner = false;


    @Override
    public void onViewAttachedToWindow(@NonNull AnnounceAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }


    public AnnounceAdapter(List<Announcement> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.listFriends = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        themeManager = new ThemeManager();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Announcement eventItem = listFriends.get(position);
            holder.tvTitle.setText(eventItem.getTitle());
            holder.tvDesc.setText(eventItem.getDetail());
            holder.ivCalendar.setTypeface(iconFont);
            holder.ivCalendar.setText(Constant.FontIcon.CALENDAR);
            holder.tvCalendar.setText(Util.changeFormat(eventItem.getCreationDate()));
            holder.llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

            holder.ivOption.setVisibility(null != eventItem.getOptions() ? View.VISIBLE : View.GONE);
            holder.ivOption.setOnClickListener(v -> Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), eventItem.getOptions(), listener));
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


    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tvCalendar;
        protected TextView tvDesc;
        protected TextView ivCalendar;
        protected View llMain, ivOption;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvCalendar = itemView.findViewById(R.id.tvCalendar);
                ivCalendar = itemView.findViewById(R.id.ivCalendar);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                llMain = itemView.findViewById(R.id.llMain);
                ivOption = itemView.findViewById(R.id.ivOption);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
