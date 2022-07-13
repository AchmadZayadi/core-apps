package com.sesolutions.ui.message;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class MessageInboxAdapter extends RecyclerView.Adapter<MessageInboxAdapter.ContactHolder> {

    private final List<MessageInbox> list;
    private final Context context;
    private final OnUserClickedListener<String, String> listener;
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private int lastPosition;
    private final int lightGrey;
    private final int foreground;

    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MessageInboxAdapter(List<MessageInbox> list, Context cntxt, OnUserClickedListener<String, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.lastPosition = -1;
        this.listener = listener;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
        lightGrey = Color.parseColor(Constant.colorPrimary.replace("#", "#1d"));
        foreground = Color.parseColor(Constant.foregroundColor);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final MessageInbox vo = list.get(position);
            holder.tvDate.setText(Util.changeDateFormat(context, vo.getDate()));
            holder.tvTitle.setText(vo.getSender());
            holder.cvMain.setCardBackgroundColor(vo.getRead() > 0 ? foreground : lightGrey);
            Util.showImageWithGlide(holder.ivImage, vo.getUserImage(), context, R.drawable.placeholder_3_2);


            holder.tvMsg.setVisibility(TextUtils.isEmpty(vo.getTitle()) ? View.GONE : View.VISIBLE);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked("" + vo.getConversationId(), "" + vo.getRead(), holder.getAdapterPosition()));

            holder.tvMsg.setText(vo.getTitle());


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
        protected TextView tvMsg;
        protected ImageView ivImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(this, itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            ivImage = itemView.findViewById(AppConfiguration.memberImageShapeIsRound ? R.id.ivImage : R.id.ivImage1);
        }
    }
}
