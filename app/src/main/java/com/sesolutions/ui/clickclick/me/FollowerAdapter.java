package com.sesolutions.ui.clickclick.me;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ContactHolder> {

    private final List<Videos> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable addDrawable;
    private final Drawable dFavSelected;
    private final Drawable dFav;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;


    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public FollowerAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_follower, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Videos vo = list.get(position);

            try {
                if (vo.getFollow().getAction().equalsIgnoreCase("unfollow")) {
                    GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.back_follow);
                    gdr.setColor(Color.GRAY);
                    holder.llFollow.setBackground(gdr);
                    holder.tvAdd.setText("UNFOLLOW");
                    holder.addtextid.setVisibility(View.GONE);
                }
                if (vo.getFollow().getAction().equalsIgnoreCase("follow")) {
                    GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.back_follow);
                    gdr.setColor(Color.parseColor(Constant.colorPrimary));
                    holder.llFollow.setBackground(gdr);
                    holder.tvAdd.setText("FOLLOW");
                    holder.addtextid.setVisibility(View.VISIBLE);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                holder.llFollow.setVisibility(View.GONE);
            }

            Util.showImageWithGlide(holder.ivUserImage, vo.getUser_image(), context, R.drawable.placeholder_square);
            holder.tvUsername.setText(vo.getTitle());

            holder.tvUserId.setText(vo.getUsername());
            holder.cvMain.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.USER_SELECT, holder, holder.getAdapterPosition());
            });
            holder.llFollow.setOnClickListener(v -> {
                //send screen_TYPE in position argument
                listener.onItemClicked(Constant.Events.FOLLOW_USER, holder, holder.getAdapterPosition());
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void changeText(String text) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected CardView cvMain;
        protected CircleImageView ivUserImage;
        protected AppCompatTextView tvUsername;
        protected LinearLayout llFollow;
        protected AppCompatTextView tvAdd;
        protected AppCompatTextView tvUserId;
        ImageView addtextid;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                addtextid = itemView.findViewById(R.id.addtextid);
                llFollow = itemView.findViewById(R.id.llFollow);
                tvUserId = itemView.findViewById(R.id.tvUserId);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvAdd = itemView.findViewById(R.id.tvAdd);
                ivUserImage = itemView.findViewById(R.id.ivUserImage);
                tvUsername = itemView.findViewById(R.id.tvUsername);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
