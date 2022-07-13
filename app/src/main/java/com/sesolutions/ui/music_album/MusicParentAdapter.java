
package com.sesolutions.ui.music_album;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SongParent;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;


public class MusicParentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private List<SongParent> treeMap;
    private Context context;
    private final OnUserClickedListener<Integer, Object> listener;


    public MusicParentAdapter(Context context, List<SongParent> list, OnUserClickedListener<Integer, Object> listener, OnLoadMoreListener loadListener) {
        this.treeMap = list;
        this.context = context;
        this.listener = listener;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }

   /* @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }*/

    @Override
    public int getItemViewType(int position) {
        return treeMap.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case 1:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_banner, parent, false);
                holder = new BannerHolder(view);
                break;
            case Constant.ItemType.CUSTOM_AD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_child_custom_ad, parent, false);
                holder = new CustomAdHolder(view);
                break;
            case Constant.ItemType.SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_suggestion, parent, false);
                holder = new TitleHolder(view);
                break;

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_parent, parent, false);
                holder = new TitleHolder(view);
                break;
        }
        return holder;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder parentHolder, final int position) {
        try {
            themeManager.applyTheme((ViewGroup) parentHolder.itemView, context);


            final SongParent skillVo = treeMap.get(position);
            switch (skillVo.getType()) {
                case 1:

                    final BannerHolder holder = (BannerHolder) parentHolder;
                    if (holder.adapter == null) {
                        /*set child item list*/
                        holder.rvChild.setHasFixedSize(true);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        holder.rvChild.setLayoutManager(layoutManager);
                        holder.adapter = new MusicChildBannerAdapter(skillVo.getChildList(), context, listener);
                        holder.rvChild.setAdapter(holder.adapter);
                        holder.handler = new Handler();
                        holder.runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    holder.rvChild.smoothScrollToPosition((layoutManager.findFirstCompletelyVisibleItemPosition() + 1) % skillVo.getChildList().size());
                                } finally {
                                    if (null != holder.runnable)
                                        holder.handler.postDelayed(holder.runnable, AppConfiguration.SLIDE_TIME);
                                }
                            }
                        };
                        holder.handler.postDelayed(holder.runnable, AppConfiguration.SLIDE_TIME);
                    }
                    break;

                case Constant.ItemType.CUSTOM_AD:
                    final CustomAdHolder holder1 = (CustomAdHolder) parentHolder;
                    // Util.showImageWithGlide(holder1.ivImage,);
                    break;

                default: {
                    final TitleHolder holder2 = (TitleHolder) parentHolder;
                    holder2.tvCategory.setText(skillVo.getName());
                    holder2.tvMore.setVisibility(skillVo.isChildValid() ? View.VISIBLE : View.GONE);
                    holder2.tvMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    /*set child item list*/
                    setChildView(skillVo.getChildList(), holder2, skillVo.getType());
                    break;
                }
            }


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setChildView(List<Albums> skillList, final TitleHolder holder, int type) {

        try {
            if (holder.adapter == null)
                if (skillList != null && skillList.size() > 0) {
                    // Collections.reverse(skillList);
                    holder.rvChild.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    holder.rvChild.setLayoutManager(layoutManager);
                    holder.adapter = new MusicChildAdapter(skillList, context, listener, type);
                    holder.rvChild.setAdapter(holder.adapter);
                } else {

                }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return treeMap.size();
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {

        protected RecyclerView rvChild;
        protected TextView tvCategory;
        protected TextView tvMore;
        protected MusicChildAdapter adapter;


        public TitleHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
            tvMore = itemView.findViewById(R.id.tvMore);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {

        protected MultiSnapRecyclerView rvChild;
        protected MusicChildBannerAdapter adapter;
        protected Handler handler;
        public Runnable runnable;

        public BannerHolder(View itemView) {
            super(itemView);
            rvChild = itemView.findViewById(R.id.rvChild);
        }
    }

    public static class CustomAdHolder extends RecyclerView.ViewHolder {

        protected ImageView ivImage;

        public CustomAdHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}

