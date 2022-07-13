package com.sesolutions.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Attachment;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class CommunityAdAdapter extends RecyclerView.Adapter<CommunityAdAdapter.ContactHolder> {

    private final List<Attachment> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int text_color_1;
    private final int foregroundColor;
    private int lastPosition = 0;
    public ThemeManager themeManager;


    public CommunityAdAdapter(List<Attachment> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.text_color_1 = Color.parseColor(Constant.text_color_1);
        this.foregroundColor = Color.parseColor(Constant.foregroundColor.replace("#", "#99"));
        //  this.transparent = ContextCompat.getColor(context, R.color.transparent_black);
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_community_ad, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder01, final int position) {

        try {
            final Attachment vo = list.get(position);
            holder01.tvAdUrl.setText(vo.getUrlDescription());
            holder01.tvAdUrl.setVisibility(null != vo.getUrlDescription() ? View.VISIBLE : View.GONE);

            holder01.tvAdTitle.setText(vo.getTitle());
            holder01.tvAdTitle.setVisibility(null != vo.getTitle() ? View.VISIBLE : View.GONE);
            Util.showAnimatedImageWithGlide(holder01.ivAdImage, vo.getSrc(), context);
            holder01.tvCard.setText(vo.getCallToActionOverlay());
            holder01.tvCard.setVisibility(null != vo.getCallToActionOverlay() ? View.VISIBLE : View.GONE);

            holder01.tvAdDescription.setText(vo.getDescription());
            holder01.tvAdDescription.setVisibility(null != vo.getDescription() ? View.VISIBLE : View.GONE);

            if (null != vo.getCalltoaction()) {
                holder01.bCallToAction.setVisibility(View.VISIBLE);
                holder01.bCallToAction.setText(vo.getCalltoaction().getLabel());
            } else {
                holder01.bCallToAction.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        private final TextView tvCard, tvAdUrl, tvAdTitle, tvAdDescription;
        private final MaterialButton bCallToAction;
        private final ImageView ivAdImage;
        // protected View ivForeground;
        CardView cvMain;

        ContactHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, itemView.getContext());
            //   ivForeground = itemView.findViewById(R.id.ivForeground);
            cvMain = itemView.findViewById(R.id.cvMain);

            // rlAdContent = itemView.findViewById(R.id.rlAdContent);
            ivAdImage = itemView.findViewById(R.id.ivAdImage);
            tvAdUrl = itemView.findViewById(R.id.tvAdUrl);
            tvAdTitle = itemView.findViewById(R.id.tvAdTitle);
            tvAdDescription = itemView.findViewById(R.id.tvAdDescription);
            tvCard = itemView.findViewById(R.id.tvCard);
            bCallToAction = itemView.findViewById(R.id.bCallToAction);

            bCallToAction.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getCalltoaction().getHref(), getAdapterPosition());
            });
            itemView.findViewById(R.id.cvMain).setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.WEBVIEW, list.get(getAdapterPosition()).getHref(), getAdapterPosition());
            });

        }
    }
}
