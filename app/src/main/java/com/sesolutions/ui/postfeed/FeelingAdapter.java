package com.sesolutions.ui.postfeed;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Feeling;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FeelingAdapter extends RecyclerView.Adapter<FeelingAdapter.ContactHolder> {

    private final List<Feeling> list;
    private final Context context;
    private final OnUserClickedListener<String, String> listener;
    private final OnLoadMoreListener loadListener;
    private final int text1;
    private final ThemeManager themeManager;

    @Override
    public void onViewAttachedToWindow(FeelingAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public FeelingAdapter(List<Feeling> list, Context cntxt, OnUserClickedListener<String, String> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.loadListener = loadListener;
        this.text1 = SesColorUtils.getText1Color(context);
        themeManager = new ThemeManager();
    }

    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attach_option_2, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NotNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Feeling vo = list.get(position);
            holder.tvFeedText.setTextColor(text1);
            holder.tvFeedText.setText(vo.getTitle());
            holder.ivArrowRight.setVisibility(vo.getFeelingiconId() > 0 ? View.GONE : View.VISIBLE);
            Glide.with(context).load(vo.getIcon()).into(holder.ivFeedImage);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked("", "", holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvFeedText;
        protected ImageView ivFeedImage;
        protected ImageView ivArrowRight;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvFeedText = itemView.findViewById(R.id.tvFeedText);
                ivArrowRight = itemView.findViewById(R.id.ivArrowRight);
                ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
