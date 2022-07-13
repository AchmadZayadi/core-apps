package com.sesolutions.ui.clickclick.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityAdapter.ContactHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final List<VideoContent> listCat;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

   /* @Override
    public void onViewAttachedToWindow(ContestCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/

    public SearchActivityAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listCat = null;
        this.context = cntxt;
        this.listener = listenr;
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_discover, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Videos vo = list.get(position);
            holder.tvDesc.setVisibility(View.GONE);
            holder.tvLike.setText(""+vo.getLikeCount());
            holder.tvUser.setText(vo.getUserTitle());
            holder.tvTitle.setText(vo.getTitle());
            Util.showImageWithGlide(holder.ivImageCategory, vo.getImageUrl(), context, R.drawable.placeholder_square);
            Util.showImageWithGlide(holder.ivUser, vo.getUser_image(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v ->
                    listener.onItemClicked(Constant.Events.SEE_MORE, "" + vo.getCurrent_position(), holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvDesc;
        protected AppCompatTextView tvLike;
        protected AppCompatTextView tvUser;
        protected CircleImageView ivUser;
        protected View cvMain;
        protected ImageView ivImageCategory;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvLike = itemView.findViewById(R.id.tvLike);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                ivImageCategory = itemView.findViewById(R.id.ivImageCategory);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
