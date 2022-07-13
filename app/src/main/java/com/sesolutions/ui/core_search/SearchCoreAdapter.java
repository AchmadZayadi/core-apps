package com.sesolutions.ui.core_search;

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
import com.sesolutions.responses.SearchVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class SearchCoreAdapter extends RecyclerView.Adapter<SearchCoreAdapter.ContactHolder> {

    private final List<SearchVo> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    private final ThemeManager themeManager;

    @Override
    public void onViewAttachedToWindow(@NonNull SearchCoreAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }


    public SearchCoreAdapter(List<SearchVo> list, Context cntxt, OnLoadMoreListener loadListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.loadListener = loadListener;
        this.context = cntxt;
        this.listener = listener;
        themeManager = new ThemeManager();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_core, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            holder.cvMain.setVisibility(View.VISIBLE);
            final SearchVo vo = list.get(position);
            holder.tvImageTitle.setText(vo.getTitle());
            holder.tvImageDescription.setText(vo.getDescription());
            // holder.tvName.setText(vo.title);

            Util.showImageWithGlide(holder.ivVideoImage, vo.getImages(), context, R.drawable.placeholder_3_2);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "", holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvImageTitle;
        protected TextView tvImageDescription;
        protected ImageView ivVideoImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(this, itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            ivVideoImage = itemView.findViewById(R.id.ivVideoImage);
            tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
            tvImageDescription = itemView.findViewById(R.id.tvImageDescription);
        }
    }
}
