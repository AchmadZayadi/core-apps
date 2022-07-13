package com.sesolutions.ui.common;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;

import java.util.List;


public abstract class RecycleViewAdapter<T extends RecyclerView.ViewHolder, E> extends RecyclerView.Adapter<T> {

    public final List<E> list;
    public final Context context;
    public final OnUserClickedListener<Integer, Object> listener;
    public final ThemeManager themeManager;



    public RecycleViewAdapter(List<E> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public abstract T onCreateViewHolder(@NonNull ViewGroup parent, int viewType);


    @Override
    public abstract void onBindViewHolder(@NonNull final T holder, final int position);


    @Override
    public int getItemCount() {
        return list.size();
    }


   /* public static class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvUser;
        ImageView ivImage;
        Button bAccept;
        Button bIgnore;
        View cvMain;
        TextView tvMutual;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvMutual = itemView.findViewById(R.id.tvMutual);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvUser = itemView.findViewById(R.id.tvTitle);
                bAccept = itemView.findViewById(R.id.bAccept);
                bIgnore = itemView.findViewById(R.id.bIgnore);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }*/
}
