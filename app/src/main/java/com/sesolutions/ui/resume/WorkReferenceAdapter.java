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


class WorkReferenceAdapter extends RecyclerView.Adapter<WorkReferenceAdapter.ContactHolder> {

    private final List<ResumeRefereneceModel.ResultBean.ReferencesBean> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;
    private final Typeface iconFont;




    public WorkReferenceAdapter(List<ResumeRefereneceModel.ResultBean.ReferencesBean> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_reference, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            holder.titlest.setText(""+list.get(position).getName());
            holder.companyname.setText(""+list.get(position).getDesignation());

            holder.descritpition.setTypeface(iconFont);
            holder.email.setTypeface(iconFont);
            holder.phone.setTypeface(iconFont);

            if(list.get(position).getOrg_name().length()>0){
                String detail = "\uf1ad " + list.get(position).getOrg_name();
                holder.descritpition.setText(detail);
                holder.descritpition.setVisibility(View.VISIBLE);
            }else {
                holder.descritpition.setVisibility(View.GONE);
            }

            if(list.get(position).getEmail_id().length()>0){
                String mail =     "\uf0e0 " + list.get(position).getEmail_id();
                holder.email.setText(mail);
                holder.email.setVisibility(View.VISIBLE);
            }else {
                holder.email.setVisibility(View.GONE);
            }

            if(list.get(position).getMobile_number().length()>0){
                String phone = "\uf10b " + list.get(position).getMobile_number();
                holder.phone.setText(phone);
                holder.phone.setVisibility(View.VISIBLE);
            }else {
                holder.phone.setVisibility(View.GONE);
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
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_EDIT, list.get(position), list.get(position).getReference_id());
                                    break;
                                case R.id.deletemanue:
                                    //handle menu2 click
                                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_DELETE, list.get(position), list.get(position).getReference_id());
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

            /*holder.descritpition.setText(""+list.get(position).getCertificateid());

            if(list.get(position).getImage_url()!=null && list.get(position).getImage_url().length()>0){
                Util.showImageWithGlide(holder.imageid, list.get(position).getImage_url(), context, R.drawable.button_tabs);
            }*/

           /* holder.titlest.setText(""+list.get(position).getSchool());
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   listener.onItemClicked(Constant.Events.CLICKED_HEADER_TITLE, list.get(position), list.get(position).getResume_id());
                }
            });
            String secondtitle="";

            if(list.get(position).getDegree()!=null && list.get(position).getDegree().length()>0){
                secondtitle=""+list.get(position).getDegree();
             }*/

/*            if(list.get(position).getField_of_study()!=null && list.get(position).getField_of_study().length()>0){
                if(secondtitle.length()>0){
                    secondtitle=secondtitle+", "+list.get(position).getField_of_study();
                }else {
                    secondtitle=""+list.get(position).getField_of_study();
                }
            }
            if(list.get(position).getGrade()!=null && list.get(position).getGrade().length()>0){
                if(secondtitle.length()>0){
                    secondtitle=secondtitle+", "+list.get(position).getGrade();
                }else {
                    secondtitle=""+list.get(position).getField_of_study();
                }
            }

            if(secondtitle.length()>0){
                holder.companyname.setText(""+secondtitle);
                holder.companyname.setVisibility(View.VISIBLE);
            }else {
                holder.companyname.setVisibility(View.GONE);
            }

            holder.experiencetime.setText(list.get(position).getFromyear()+" "+list.get(position).getToyear());
            holder.cityname.setText(list.get(position).getDescription());
            holder.descritpition.setText(list.get(position).getActivities());*/
          /*

            if(list.get(position).getDescription()!=null && list.get(position).getDescription().length()>0){
                holder.descritpition.setText(""+list.get(position).getDescription());
                holder.descritpition.setVisibility(View.VISIBLE);
            }else {
                holder.descritpition.setVisibility(View.GONE);
            }

            if(list.get(position).getLocation()!=null && list.get(position).getLocation().length()>0){
                holder.cityname.setText(""+list.get(position).getLocation());
                holder.cityname.setVisibility(View.VISIBLE);
            }else {
                holder.cityname.setVisibility(View.GONE);
            }

            try {
                if(list.get(position).getCurrentlywork()==1){
                    holder.experiencetime.setText(""+list.get(position).getFrommonth()+" "+list.get(position).getFromyear()+" Currently Working");
                    holder.experiencetime.setVisibility(View.VISIBLE);
                }else {
                    holder.experiencetime.setText(list.get(position).getFrommonth()+" "+list.get(position).getFromyear()+" to "+list.get(position).getTomonth()+" "+list.get(position).getToyear());
                    holder.experiencetime.setVisibility(View.VISIBLE);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }*/




           /* if (isSuggestion) {
                final Friends eventItem = listFriends.get(position);
                holder.vAddress.setVisibility(View.GONE);
                holder.vAge.setVisibility(View.GONE);
                holder.vMutual.setVisibility(View.GONE);
                holder.tvName.setText(eventItem.getLabel());
                holder.bAdd.setVisibility(View.GONE);
                holder.bRemove.setVisibility(owner && isAddRemove ? View.VISIBLE : View.GONE);
                holder.bRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.MEMBER_REMOVE, "", holder.getAdapterPosition());
                    }
                });

                Util.showImageWithGlide(holder.ivImage, eventItem.getPhoto(), context, R.drawable.placeholder_square);
            }
            else {
                final Notifications eventItem = list.get(position);
                holder.vAge.setVisibility(TextUtils.isEmpty(eventItem.getAge()) ? View.GONE : View.VISIBLE);
                holder.vAddress.setVisibility(TextUtils.isEmpty(eventItem.getLocation()) ? View.GONE : View.VISIBLE);
                holder.vMutual.setVisibility(TextUtils.isEmpty(eventItem.getMutualFriends()) ? View.GONE : View.VISIBLE);
                holder.tvAddress.setText(eventItem.getLocation());
                holder.tvMutual.setText(eventItem.getMutualFriends());
                holder.tvAge.setText(eventItem.getAge());
                holder.tvName.setText(eventItem.getTitle());
                holder.bAdd.setBackgroundDrawable(shape);

                if (null != eventItem.getMembership()) {
                    holder.bAdd.setVisibility(View.VISIBLE);
                    holder.bAdd.setText(eventItem.getMembership().getLabel());
                } else {
                    holder.bAdd.setVisibility(View.GONE);
                }
                Util.showImageWithGlide(holder.ivImage, eventItem.getUserImage(), context, R.drawable.placeholder_square);
                holder.bAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.MEMBER_ADD, "", holder.getAdapterPosition());
                    }
                });
            }

            try {

                holder.cvMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition());
                    }
                });

             *//*   Notifications eventItem = list.get(position);
                holder.cvMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, eventItem.getUserId());
                    }
                });*//*
            }catch (Exception e){
                e.printStackTrace();
            }*/


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



    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView companyname,titlest,experiencetime,cityname,descritpition,email,phone;
        CardView cvMain;
        ImageView optionmenu;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                titlest = itemView.findViewById(R.id.titlest);
                cvMain = itemView.findViewById(R.id.cvMain);
                companyname = itemView.findViewById(R.id.companyname);
                descritpition = itemView.findViewById(R.id.description);
                optionmenu = itemView.findViewById(R.id.optionmenu);
                email = itemView.findViewById(R.id.email);
                phone = itemView.findViewById(R.id.phone);
               /* cityname = itemView.findViewById(R.id.cityname);
                experiencetime = itemView.findViewById(R.id.experiencetime);
                companyname = itemView.findViewById(R.id.companyname);*/

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
