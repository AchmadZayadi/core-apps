package com.sesolutions.ui.signup;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.DefaultDataVo;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class DemoUserAdapter extends RecyclerView.Adapter<DemoUserAdapter.ContactHolder> {

    private final List<DefaultDataVo.Result.DemoUser.Users> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;


    public DemoUserAdapter(List<DefaultDataVo.Result.DemoUser.Users> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_demo_user, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final DefaultDataVo.Result.DemoUser.Users vo = list.get(position);

            Util.showImageWithGlide(holder.ivImage, vo.getImage_url(), context, 1);

            holder.ivImage.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, Constant.Events.MUSIC_MAIN, vo.getUser_id()));


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

        protected ImageView ivImage;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                ivImage = itemView.findViewById(R.id.ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
