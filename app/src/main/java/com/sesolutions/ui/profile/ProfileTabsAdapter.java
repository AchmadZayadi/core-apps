package com.sesolutions.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.profile.ProfileInfo;
import com.sesolutions.ui.common.RecycleViewAdapter;
import com.sesolutions.ui.member.SomeDrawable;
import com.sesolutions.ui.storyview.StoryContent;
import com.sesolutions.ui.storyview.StoryView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class ProfileTabsAdapter extends RecyclerView.Adapter<ProfileTabsAdapter.ProfileHolder> {
    private final List<Options> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;

    private final String packageName;
    private final int text2;
    private final int foregroundColor;


    public ProfileTabsAdapter(List<Options> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {

        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        packageName = context.getPackageName();
        text2 = Color.parseColor(Constant.text_color_2);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
    }

    @NonNull
    @Override
    public ProfileTabsAdapter.ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tabs_profile, parent, false);
        return new ProfileTabsAdapter.ProfileHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProfileTabsAdapter.ProfileHolder holder, int position) {

        try {
            final Options vo = list.get(position+1);
            holder.tvTag.setText(vo.getLabel());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(Constant.Events.TAB_OPTION_PROFILE,vo,position);
                }
            });
            try {
                SomeDrawable drawable21 = new SomeDrawable(Color.parseColor(Constant.text_color_1),Color.parseColor(Constant.text_color_1),Color.parseColor(Constant.text_color_1),1,Color.parseColor(Constant.text_color_1),50);
                holder.rlmainround.setBackgroundDrawable(drawable21);
                holder.tvTag.setTextColor(Color.parseColor(Constant.ButtonBackgroundColor));
            }catch (Exception ex){
                ex.printStackTrace();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return (list.size()-1);
    }

    public class ProfileHolder extends RecyclerView.ViewHolder {

        protected TextView tvTag;
        protected LinearLayout cvMain;
        RelativeLayout rlmainround;

        public ProfileHolder(View itemView) {
            super(itemView);
            try {
                tvTag = itemView.findViewById(R.id.tvTag);
                cvMain = itemView.findViewById(R.id.cvMain);
                rlmainround = itemView.findViewById(R.id.rlmainround);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
