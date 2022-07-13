package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.ContactHolder> {

    private final List<ResumeMoel.ResultBean.ResumesBean> list;
    private final List<ResumeMoel.ResultBean.ResumesBean> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;




    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public ResumeAdapter(List<ResumeMoel.ResultBean.ResumesBean> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.listFriends = null;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        createRoundedFilled();
        themeManager = new ThemeManager();

    }

    public ResumeAdapter(List<ResumeMoel.ResultBean.ResumesBean> list, OnLoadMoreListener loadMoreListener, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.listFriends = list;
        this.list = null;
        this.context = cntxt;
        this.isSuggestion = true;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        createRoundedFilled();
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_list, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            holder.titlest.setText(""+list.get(position).getTitle());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_TITLE, list.get(position), list.get(position).getResume_id());
                }
            });

            holder.previewid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_PREVIEW_RESUME, list.get(position), list.get(position).getResume_id());
                }
            });

            holder.downloadid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_PREVIEW_DOWNLOAD, list.get(position), list.get(position).getResume_id());
                }
            });




            if(list.get(position).getMenus()!=null && list.get(position).getMenus().size()>0){
                holder.optionmenu.setVisibility(View.VISIBLE);
            }else {
                holder.optionmenu.setVisibility(View.GONE);
            }


            holder.optionmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.optionmenu);
                    //inflating menu from xml resource



                    if(list.get(position).getMenus().size()==3){
                        popup.inflate(R.menu.view_menu_resume_list);
                    }
                    else  if(list.get(position).getMenus().size()==2){
                        popup.inflate(R.menu.view_menu_resume_2);
                    }else  if(list.get(position).getMenus().size()==1){
                        popup.inflate(R.menu.view_menu_resume_3);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editmanue:
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_EDIT, list.get(position), list.get(position).getResume_id());
                                    break;
                                case R.id.editmanueinfo:
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_SEEMORE, list.get(position), list.get(position).getResume_id());
                                    break;
                                case R.id.deletemanue:
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_DELETE, list.get(position), list.get(position).getResume_id());
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

    private void createRoundedFilled() {
        shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setColor(Color.parseColor(Constant.colorPrimary));
        //  shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }


    @Override
    public int getItemCount() {
        return isSuggestion ? listFriends.size() : list.size();
    }

    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView tvAge,titlest;
        CardView cvMain;
        ImageView optionmenu;
        RelativeLayout previewid,downloadid;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                titlest = itemView.findViewById(R.id.titlest);
                cvMain = itemView.findViewById(R.id.cvMain);
                optionmenu = itemView.findViewById(R.id.optionmenu);
                previewid = itemView.findViewById(R.id.previewid);
                downloadid = itemView.findViewById(R.id.downloadid);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
