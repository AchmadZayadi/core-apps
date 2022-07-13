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
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;


public class MusicChildAdapter extends RecyclerView.Adapter<MusicChildAdapter.ContactHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Typeface iconFont;
    private final ThemeManager themeManager;
    private int type;


    public MusicChildAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, int type) {
        this.list = list;
        this.context = cntxt;
        this.type = type;
        this.listener = listenr;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (type) {
            case Constant.ItemType.RECENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_child_recent, parent, false);
                break;
            case Constant.ItemType.SUGGESTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peaple_suggestion_child, parent, false);
                break;
            case Constant.ItemType.DISCOVER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_child_discover, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_child, parent, false);
                break;
        }

        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {

            final Albums vo = list.get(position);
            holder.tvName.setText(vo.getTitle());

            Util.showImageWithGlide(holder.ivImage, vo.getImageUrl(), context, R.drawable.placeholder_square);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {


        protected TextView tvName;

        protected ImageView ivImage;
        protected View rlMain;
        protected ImageView fabPlay;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                themeManager.applyTheme((ViewGroup) itemView, context);
                rlMain = itemView.findViewById(R.id.rlMain);
                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                fabPlay = itemView.findViewById(R.id.ivMediaType);

                rlMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, this, -1));
                fabPlay.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAB_PLAY, "", getAdapterPosition()));

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
