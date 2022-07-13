package com.sesolutions.ui.contest.join;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ContactHolder> {

    private final List<SearchVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final boolean isChild;
    //private final RequestManager glide;
    //RequestOptions options;

    @Override
    public void onViewAttachedToWindow(@NonNull LinkAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            listener.onItemClicked(Constant.Events.LOAD_MORE, isChild, holder.getAdapterPosition());
        }
    }


    public LinkAdapter(List<SearchVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listener, boolean isChild) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.isChild = isChild;
        themeManager = new ThemeManager();
        //glide = Glide.with(context);
        /*options = new RequestOptions()
                .centerCrop()
                .error(android.R.drawable.stat_notify_error)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);*/


        //iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isChild ? R.layout.item_link_child : R.layout.layout_search_core, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            holder.cvMain.setVisibility(View.VISIBLE);
            final SearchVo vo = list.get(position);
            holder.tvImageTitle.setText(vo.getTitle());
            holder.tvImageTitle.setVisibility(TextUtils.isEmpty(vo.getTitle()) ? View.GONE : View.VISIBLE);
            holder.tvImageDescription.setText(vo.getDescription());
            Util.showAnimatedImageWithGlide(holder.ivVideoImage, vo.getImages(), context);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, isChild, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvImageTitle;
        protected TextView tvImageDescription;
        protected ImageView ivVideoImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            themeManager.applyTheme((ViewGroup) itemView, context);
            cvMain = itemView.findViewById(R.id.cvMain);
            ivVideoImage = itemView.findViewById(R.id.ivVideoImage);
            tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
            tvImageDescription = itemView.findViewById(R.id.tvImageDescription);
        }
    }
}
