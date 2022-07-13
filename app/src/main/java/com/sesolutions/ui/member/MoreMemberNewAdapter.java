package com.sesolutions.ui.member;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class MoreMemberNewAdapter extends RecyclerView.Adapter<MoreMemberNewAdapter.ContactHolder> {

    private final List<UserMaster> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;


    @Override
    public void onViewAttachedToWindow(@NonNull MoreMemberNewAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MoreMemberNewAdapter(List<UserMaster> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
         this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        themeManager = new ThemeManager();

    }



    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_page2, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(ContactHolder holder, final int position) {

        holder.rleativedatal.setOnClickListener(v -> {
                  listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition());
                });
       /* holder.rleativedatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLIK","CLIK");

            }
        });*/
    }


    @Override
    public int getItemCount() {
        return  list.size();
    }



    public static class ContactHolder extends RecyclerView.ViewHolder {

       private RelativeLayout rleativedatal;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                rleativedatal = itemView.findViewById(R.id.rleativedatal);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
