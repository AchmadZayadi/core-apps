package com.sesolutions.ui.music_album;

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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class MusicChildBannerAdapter extends RecyclerView.Adapter<MusicChildBannerAdapter.ContactHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    private final ThemeManager themeManager;


    public MusicChildBannerAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_child_banner, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Albums vo = list.get(position);
            // holder.tvName.setText(vo.getTitle());


            Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.tvCount.setText((position + 1) + "/" + list.size());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, -1);
                }
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {


        protected TextView tvCount;

        protected ImageView ivImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvCount = itemView.findViewById(R.id.tvCount);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
