package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class SkillAdpterSub extends RecyclerView.Adapter<SkillAdpterSub.ContactHolder> {

    private final List<SkillParentModel.ResultBean.SkillsBean> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final List<Category> listCat;
    private final boolean isContestSelected;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

   /* @Override
    public void onViewAttachedToWindow(ContestCategoryAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }*/

    public SkillAdpterSub(List<SkillParentModel.ResultBean.SkillsBean> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listCat = null;
        this.context = cntxt;
        this.listener = listenr;
        this.isContestSelected = true;
        // this.loadListener = loadListener;
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_achivement_sub, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            if(position%2==0){
                holder.cvMain.setBackgroundColor(Color.parseColor("#F9F9F9"));
            }else {
                holder.cvMain.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            holder.titleview.setText(""+list.get(position).getSkillname());

            try {
                holder.ratingBar2.setVisibility(View.VISIBLE);
                holder.ratingBar2.setRating(list.get(position).getRating());
            }catch (Exception ex){
                ex.printStackTrace();
            }

            holder.optionmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.optionmenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.view_menu_resume);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editmanue:
                                    //handle menu1 click
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_EDIT, list.get(position), 0);
                                    break;
                                case R.id.deletemanue:
                                    //handle menu2 click
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_DELETE, list.get(position), 0);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView titleview;
        protected ImageView ivImageCategory;
        RelativeLayout cvMain;
        ImageView optionmenu;
        RatingBar ratingBar2;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                titleview = itemView.findViewById(R.id.titleview);
                cvMain = itemView.findViewById(R.id.cvMain);
                optionmenu = itemView.findViewById(R.id.optionmenu);
                ratingBar2 = itemView.findViewById(R.id.ratingBar2);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
