package com.sesolutions.ui.comment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.LikeData;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ContactHolder> {

    private final List<LikeData> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    private final int cPrimary;
    private final int cBg;

    private final ThemeManager themeManager;


    public ReactionAdapter(List<LikeData> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        cPrimary = Color.parseColor(Constant.colorPrimary);
        cBg = Color.parseColor(Constant.foregroundColor);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_reaction, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final LikeData vo = list.get(position);
            holder.tvUser.setText(vo.getTitle());

            Util.showImageWithGlide(holder.ivImage, vo.getUserImage(), context, R.drawable.placeholder_square);
            Util.showImageWithGlide(holder.ivReaction, vo.getImage(), context, R.drawable.placeholder_square);
            holder.ivReaction.setBorderColor(cBg);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, vo.getType(), vo.getUserId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvUser;
        protected CircleImageView ivImage;
        protected CircleImageView ivReaction;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivReaction = itemView.findViewById(R.id.ivReaction);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvUser = itemView.findViewById(R.id.tvTitle);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
