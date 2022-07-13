package com.sesolutions.ui.clickclick.notification;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import java.util.List;


public class VideoNotificationAdapter extends RecyclerView.Adapter<VideoNotificationAdapter.ContactHolder> {

    private final List<Notifications> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, String> listener;
    private final Typeface iconFont;
    private final String slashU = "&#x";
    private final ThemeManager themeManager;
    private final int lightGrey;
    private final int foreground;
    private final int cPrimary;

    @Override
    public void onViewAttachedToWindow(@NonNull VideoNotificationAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }


    public VideoNotificationAdapter(List<Notifications> list, Context cntxt, OnLoadMoreListener loadListener, OnUserClickedListener<Integer, String> listener) {
        this.list = list;
        this.loadListener = loadListener;
        this.context = cntxt;
        this.listener = listener;
        themeManager = new ThemeManager();
        lightGrey = Color.parseColor(Constant.colorPrimary.replace("#", "#1d"));
        cPrimary = SesColorUtils.getPrimaryColor(context);
        foreground = SesColorUtils.getForegroundColor(context);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_notification, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Notifications vo = list.get(position);
            holder.tvDate.setText(Util.changeDateFormat(context, vo.getDate()));
            holder.tvTitle.setText(vo.getTitle());
            holder.cvMain.setCardBackgroundColor(vo.getRead() > 0 ? foreground : lightGrey);

            Util.showImageWithGlide(holder.ivImage, vo.getUserImage(), context, R.drawable.placeholder_3_2);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(vo.getObjectId(), vo.getObjectType(), holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvTitle;
        protected TextView tvDate;
        protected ImageView ivImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
