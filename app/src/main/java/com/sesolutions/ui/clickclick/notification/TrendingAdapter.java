package com.sesolutions.ui.clickclick.notification;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;

import cn.jzvd.Jzvd2;
import de.hdodenhof.circleimageview.CircleImageView;


public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.CategoryHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final Typeface iconFont;
    public final String VT_CATEGORIES = "-3";
    public final String VT_CATEGORY = "-2";
    public final String VT_SUGGESTION = "-1";
    private final ThemeManager themeManager;
    public HttpProxyCacheServer proxy;
    int menuTitleActiveColor;


    @Override
    public void onViewAttachedToWindow(@NonNull CategoryHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public TrendingAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.proxy = ((MainApplication) context.getApplicationContext()).getProxy(cntxt);
        //  viewPool = new RecyclerView.RecycledViewPool();
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending, parent, false);
        return new CategoryHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Videos vo = list.get(position);
            if (vo.getIsUserChannelFollow()) {
                holder.btFollow.setText("Following");
            }
            if (!vo.getIsUserChannelFollow()) {
                holder.btFollow.setText("Follow");
            }
            holder.tvUser.setText(vo.getTitle());
            holder.tvUserName.setText("@" + vo.getTitle());
            Util.showImageWithGlide(holder.ivUserImage, vo.getUser_image(), context, R.drawable.placeholder_3_2);
            holder.ivCross.setOnClickListener(v -> listener.onItemClicked(Constant.Events.TTS_POPUP_CLOSED, "", holder.getAdapterPosition()));
            holder.btFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_FOLLOW2, "", position));

        //    GradientDrawable drawable = (GradientDrawable) holder.btFollow.getBackground();
       //     drawable.setColor(menuTitleActiveColor);

            holder.btFollow.setBackgroundColor(menuTitleActiveColor);
           /* JZDataSource jzDataSource = new JZDataSource(proxy.getProxyUrl(vo.getVideo().getIframeURL()),
                    vo.getTitle());
            jzDataSource.looping = true;
            holder.jzVideoPlayerStandard.setUp(jzDataSource, Jzvd.SCREEN_NORMAL);
            holder.jzVideoPlayerStandard.startVideo();*/
            holder.jzVideoPlayerStandard.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class CategoryHolder extends RecyclerView.ViewHolder {

        private Jzvd2 jzVideoPlayerStandard;
        protected CircleImageView ivUserImage;
        protected AppCompatImageView ivChannelImage;
        protected AppCompatImageView ivCross;
        protected AppCompatTextView tvUser;
        protected AppCompatTextView btFollow;
        protected AppCompatTextView tvUserName;
        protected CardView cvMain;
        protected CardView cvFollow;


        public CategoryHolder(View itemView) {
            super(itemView);
            jzVideoPlayerStandard = itemView.findViewById(R.id.videoplayer);
            cvMain = itemView.findViewById(R.id.cvMain);
            cvFollow = itemView.findViewById(R.id.cvFollow);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            btFollow = itemView.findViewById(R.id.btFollow);
            ivCross = itemView.findViewById(R.id.ivCross);

        }
    }

}
