package com.sesolutions.ui.packages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.Transaction;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SpanUtil;

import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ContactHolder> {

    private final List<Transaction> list;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Context context;
    private final ThemeManager themeManager;
    private final Typeface iconFont;
    private boolean isMyPackageScreen;
    private final int foreground;
    private final int text_color_light;
    private final int text_color_1;
    private final int cPrimary;
    private final int alphaGrey1;

    public TransactionAdapter(List<Transaction> list, Context context, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listener = listenr;
        this.context = context;
        foreground = Color.parseColor(Constant.backgroundColor);
        text_color_light = Color.parseColor(Constant.text_color_light);
        text_color_1 = Color.parseColor(Constant.text_color_1);
        cPrimary = Color.parseColor(Constant.colorPrimary);
        alphaGrey1 = ContextCompat.getColor(context, R.color.alpha_grey_1);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TransactionAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, null, -1);
        }
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final Transaction vo = list.get(position);
            holder.tvTransId.setText(SpanUtil.getHtmlString(context.getString(R.string.trans_id, vo.getTransaction_id())));
            holder.tvPackageTitle.setText(SpanUtil.getHtmlString(context.getString(R.string.trans_package, vo.getPackaze())));
            holder.tvContestTitle.setText(SpanUtil.getHtmlString(context.getString(R.string.trans_contest, vo.getTitle())));
            holder.tvPrice.setText(vo.getAmount());
            holder.tvStats.setTypeface(iconFont);
            holder.tvStats.setText(Constant.FontIcon.CALENDAR);
            holder.tvDate.setText(vo.getDate());
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, null, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvPackageTitle;
        protected TextView tvContestTitle;
        protected TextView tvDate;
        protected TextView tvStats;
        protected TextView tvPrice;
        protected TextView tvTransId;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                tvPackageTitle = itemView.findViewById(R.id.tvPackageTitle);
                tvContestTitle = itemView.findViewById(R.id.tvContestTitle);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvStats = itemView.findViewById(R.id.tvStats);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTransId = itemView.findViewById(R.id.tvTransId);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
