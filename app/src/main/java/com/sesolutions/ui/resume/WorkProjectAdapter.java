package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.sesolutions.utils.FontManager;

import java.util.List;


class WorkProjectAdapter extends RecyclerView.Adapter<WorkProjectAdapter.ContactHolder> implements  PopupMenu.OnMenuItemClickListener {

    private final List<Resume_project.ResultBean.ProjectsBean> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;
    private final Typeface iconFont;
    String DownloadUrl="";



    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public WorkProjectAdapter(List<Resume_project.ResultBean.ProjectsBean> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
         this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        createRoundedFilled();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_experiencelist, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            holder.titlest.setText(""+list.get(position).getTitle());

            if(list.get(position).getProject_url()!=null && list.get(position).getProject_url().length()>0){
                holder.seemore.setVisibility(View.VISIBLE);
            }else {
                holder.seemore.setVisibility(View.GONE);
            }

            holder.seemore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_SEEMORE, list.get(position), list.get(position).getProject_id());
                }
            });



            try {
                if(list.get(position).getCurrentlywork()==1){
                    holder.companyname.setText(""+list.get(position).getFrommonth()+" "+list.get(position).getFromyear()+"- Currently Project Ongoing");
                    holder.companyname.setVisibility(View.VISIBLE);
                }else {

                    if(list.get(position).getFromyear().length()<1 || list.get(position).getToyear().length()<1){
                        holder.companyname.setVisibility(View.GONE);
                    }else {
                        holder.companyname.setText(list.get(position).getFrommonth()+" "+list.get(position).getFromyear()+" - "+list.get(position).getTomonth()+" "+list.get(position).getToyear());
                        holder.companyname.setVisibility(View.VISIBLE);
                    }


                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            holder.cityname.setVisibility(View.GONE);
            holder.experiencetime.setVisibility(View.GONE);

            if(list.get(position).getDescription()!=null && list.get(position).getDescription().length()>0){
                holder.descritpition.setText(""+list.get(position).getDescription());
                holder.descritpition.setVisibility(View.VISIBLE);
            }else {
                holder.descritpition.setVisibility(View.GONE);
            }

            holder.optionmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //   listener.onItemClicked(Constant.Events.CLICKED_HEADER_EDIT, list.get(position), list.get(position).getProject_id());

                    DownloadUrl="";
                //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.optionmenu);
                    //inflating menu from xml resource
                    for(int k=0;k<list.get(position).getMenus().size();k++){
                        if(list.get(position).getMenus().get(k).getName().equalsIgnoreCase("download")){
                         DownloadUrl=list.get(position).getMenus().get(k).getImage_url();
                        }
                   }

                    if(DownloadUrl.length()>0){
                        popup.inflate(R.menu.view_menu_resume_list);
                        popup.getMenu().findItem(R.id.editmanueinfo).setTitle("Download");
                    }else {
                        popup.inflate(R.menu.view_menu_resume);
                    }

                    //adding click listener




                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.editmanue:
                                    //handle menu1 click
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_EDIT, list.get(position), list.get(position).getProject_id());
                                    break;
                                case R.id.editmanueinfo:
                                    //handle menu1 click
                                    listener.onItemClicked(Constant.Events.CLICKED_PREVIEW_DOWNLOAD, list.get(position), list.get(position).getProject_id());
                                    break;
                                case R.id.deletemanue:
                                    //handle menu2 click
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_DELETE, list.get(position), list.get(position).getProject_id());
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
        return  list.size();
    }

    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }




    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView companyname,titlest,experiencetime,cityname,descritpition,seemore;
        CardView cvMain;
        ImageView optionmenu;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                titlest = itemView.findViewById(R.id.titlest);
                cvMain = itemView.findViewById(R.id.cvMain);
                descritpition = itemView.findViewById(R.id.descritpition);
                cityname = itemView.findViewById(R.id.cityname);
                experiencetime = itemView.findViewById(R.id.experiencetime);
                companyname = itemView.findViewById(R.id.companyname);
                optionmenu = itemView.findViewById(R.id.optionmenu);
                seemore = itemView.findViewById(R.id.seemore);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    private void showPopup(List<Resume_project.ResultBean.ProjectsBean.MenusBean> menus, View v, int idPrefix) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Resume_project.ResultBean.ProjectsBean.MenusBean s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

}
