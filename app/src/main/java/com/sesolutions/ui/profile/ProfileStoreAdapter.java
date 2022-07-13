package com.sesolutions.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class ProfileStoreAdapter extends RecyclerView.Adapter<ProfileStoreAdapter.ProfileHolder> {
    private final List<Options> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;

    private final String packageName;
    private final int text2;
    private final int foregroundColor;


    public ProfileStoreAdapter(List<Options> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {

        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        packageName = context.getPackageName();
        text2 = Color.parseColor(Constant.text_color_2);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
    }

    @NonNull
    @Override
    public ProfileStoreAdapter.ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tabs_profile, parent, false);
        return new ProfileStoreAdapter.ProfileHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProfileStoreAdapter.ProfileHolder holder, int position) {

        try {
            final Options vo = list.get(position+2);
            holder.tvTag.setText(vo.getLabel());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(Constant.Events.TAB_OPTION_PROFILE,vo,position);
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return (list.size()-2);
    }

    public class ProfileHolder extends RecyclerView.ViewHolder {

        protected TextView tvTag;
        protected LinearLayout cvMain;


        public ProfileHolder(View itemView) {
            super(itemView);
            try {
                tvTag = itemView.findViewById(R.id.tvTag);
                cvMain = itemView.findViewById(R.id.cvMain);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
