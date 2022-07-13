package com.sesolutions.ui.clickclick.music;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class MusicCategoryAdapter extends RecyclerView.Adapter<MusicCategoryAdapter.ContactHolder> {

    private final List<Albums> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;

    private final String packageName;
    private final int text2;
    private final int foregroundColor;


    public MusicCategoryAdapter(List<Albums> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        packageName = context.getPackageName();
        text2 = Color.parseColor(Constant.text_color_2);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_music_cat, parent, false);
        return new ContactHolder(view);
    }

    int menuTitleActiveColor;
    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            final Albums vo = list.get(position);
            if(vo.getselected()){
                holder.llMain.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_filled_lover));
                holder.tvText.setTextColor(Color.parseColor("#FFFFFF"));
                holder.llMain.setMinimumWidth(50);
            }
            if(!vo.getselected()){
                holder.llMain.setBackground(ContextCompat.getDrawable(context, R.drawable.corner_stroke_black));
                holder.tvText.setTextColor(Color.parseColor("#000000"));
            }
            holder.tvText.setText(vo.getCategory().getName());
            holder.llMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MENU_MAIN, "", holder.getAdapterPosition()));

            GradientDrawable drawable = (GradientDrawable) holder.llMain.getBackground();
            drawable.setColor(menuTitleActiveColor);

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

        protected TextView tvText;
        protected ImageView ivImage;
        protected LinearLayoutCompat llMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                llMain = itemView.findViewById(R.id.llMain);
                tvText = itemView.findViewById(R.id.tvText);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
