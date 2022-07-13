package com.sesolutions.ui.member;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MemberGridAdapter extends RecyclerView.Adapter<MemberGridAdapter.ContactHolder> {

    private final List<Notifications> list;
    private final List<Friends> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private int lastPosition;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private boolean isGrid = false;
    int ListType=0;
    int menuTitleActiveColor;


    public void showAsGrid() {
        isGrid = true;
    }


    @Override
    public void onViewAttachedToWindow(MemberGridAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && /*(getItemCount() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && */(getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MemberGridAdapter(List<Notifications> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener,int listtype) {
        this.list = list;
        this.listFriends = null;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        this.lastPosition = -1;
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        themeManager = new ThemeManager();
        this.ListType=listtype;
    }





    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if(ListType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_grid, parent, false);
        }

        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NotNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Notifications eventItem = list.get(position);
            holder.vAge.setVisibility(TextUtils.isEmpty(eventItem.getAge()) ? View.GONE : View.VISIBLE);
            holder.vAddress.setVisibility(TextUtils.isEmpty(eventItem.getLocation()) ? View.GONE : View.VISIBLE);
            //  holder.vMutual.setVisibility(TextUtils.isEmpty(eventItem.getMutualFriends()) ? View.GONE : View.VISIBLE);
            holder.tvAddress.setText(eventItem.getLocation());
            holder.tvMutual.setText(eventItem.getMutualFriends());
            holder.vMutual.setVisibility(eventItem.getMutualFriends() != null && !eventItem.getMutualFriends().startsWith("0") ? View.VISIBLE : View.GONE);
            holder.tvAge.setText(eventItem.getAge());
            holder.tvName.setText(eventItem.getTitle());


            holder.bAddRelative.setOnClickListener(v ->{
                listener.onItemClicked(Constant.Events.MEMBER_ADD, "", holder.getAdapterPosition());
            } );

            holder.bFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_FOLLOW, "", holder.getAdapterPosition()));

            holder.cvMain.setOnClickListener(v -> {
                //sending image object for transition
                listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition());
            });


            try {
                if(eventItem.getBlock().getText().equalsIgnoreCase("Block")){
                //    holder.blockiconid.setColorFilter(Color.parseColor(Constant.ButtonTitleColor));
                    //  holder.blockiconid.setImageResource(R.drawable.blockuser);
                    holder.blockiconid.setImageResource(R.drawable.ic_baseline_block_24);
                    if(ListType==1){
                        holder.blocktext.setTextColor(menuTitleActiveColor);
                        holder.blocktext.setText("Block");
                    }
                }
                else {
                   // holder.blockiconid.setColorFilter(Color.GRAY);
                     holder.blockiconid.setImageResource(R.drawable.unblockuser);
                   // holder.blockiconid.setImageResource(R.drawable.ic_baseline_block_24);
                    if(ListType==1){
                        holder.blocktext.setTextColor(Color.GRAY);
                        holder.blocktext.setText("Unblock");
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }


           /* if (null != eventItem.getFollow()) {
                holder.bFollow.setVisibility(View.VISIBLE);
                holder.bFollow.setText(eventItem.getFollow().getText());
            } else {
                holder.bFollow.setVisibility(View.GONE);
            }*/
            Util.showImageWithGlide(holder.ivImage, eventItem.getUserImage(), context, R.drawable.placeholder_square);


            holder.vAddress.setVisibility(View.GONE);
            holder.bFollow.setVisibility(View.GONE);



            GradientDrawable drawable = (GradientDrawable) holder.blockrelativeid.getBackground();
           // drawable.setColor(menuTitleActiveColor);
            drawable.setStroke(1,menuTitleActiveColor);
          //  holder.blockiconid.setColorFilter(menuTitleActiveColor);

            holder.blockrelativeid.setOnClickListener(v ->{
            if(eventItem.getBlock().getText().equalsIgnoreCase("Block")){
                listener.onItemClicked(Constant.Events.MEMBER_BLOCK, "", holder.getAdapterPosition());
            }else {
                listener.onItemClicked(Constant.Events.MEMBER_UNBLOCK, "", holder.getAdapterPosition());
            }}
            );
            holder.vAddress.setVisibility(View.GONE);


         //   GradientDrawable drawable32 = (GradientDrawable) holder.bAddRelative.getBackground();
         //   drawable32.setColor(menuTitleActiveColor);
            //drawable.setStroke(1,menuTitleActiveColor);


       /*  try {
                SomeDrawable drawable21 = new SomeDrawable(Color.parseColor(Constant.ButtonBackgroundColor),Color.parseColor(Constant.ButtonBackgroundColor),Color.parseColor(Constant.ButtonBackgroundColor),1,Color.parseColor(Constant.ButtonBackgroundColor),8);
                holder.bAddRelative.setBackgroundDrawable(drawable21);
                holder.blockrelativeid.setBackgroundDrawable(drawable21);

            }catch (Exception ex){
                ex.printStackTrace();
            }*/

            if (null != eventItem.getMembership()) {
                holder.bAddRelative.setVisibility(View.VISIBLE);
                holder.baddtext.setText(eventItem.getMembership().getLabel());
                holder.baddtext.setTextColor(Color.parseColor(Constant.ButtonTitleColor));
            } else {
                holder.bAddRelative.setVisibility(View.GONE);
            }


            Log.e("eventitem",""+eventItem.getUserId());
            Log.e("userid",""+SPref.getInstance().getUserMasterDetail(context).getUserId());
            try {
                if(eventItem.getUserId()==SPref.getInstance().getUserMasterDetail(context).getUserId()){
                    holder.bAddRelative.setVisibility(View.GONE);
                    holder.blockrelativeid.setVisibility(View.GONE);
                }else {
                      holder.rltabid.setVisibility(View.VISIBLE);
                      holder.blockrelativeid.setVisibility(View.VISIBLE);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                  holder.rltabid.setVisibility(View.VISIBLE);
                  holder.blockrelativeid.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
        protected TextView tvAge;
        protected TextView tvMutual;
        protected LinearLayoutCompat llMutual;
        protected TextView tvName;
        protected View vAge;
        protected View vAddress;
        protected View vMutual;
        protected RelativeLayout bAddRelative;
        protected AppCompatButton bFollow;
        protected View bRemove;
        protected View llMain;
        protected ImageView ivImage;
        protected CardView cvMain;
        RelativeLayout blockrelativeid;
        ImageView blockiconid,baddicon;
        TextView blocktext,baddtext;
        LinearLayout rltabid;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                blockiconid = itemView.findViewById(R.id.blockiconid);

                blocktext = itemView.findViewById(R.id.blocktext);
                rltabid = itemView.findViewById(R.id.rltabid);
                tvAddress = itemView.findViewById(R.id.tvAddress);
                tvAge = itemView.findViewById(R.id.tvAge);
                tvMutual = itemView.findViewById(R.id.tvMutual);
                vAge = itemView.findViewById(R.id.llAge);
                llMutual = itemView.findViewById(R.id.llMutual);
                vAddress = itemView.findViewById(R.id.llAddress);
                vMutual = itemView.findViewById(R.id.llMutual);
                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                baddicon = itemView.findViewById(R.id.baddicon);
                bAddRelative = itemView.findViewById(R.id.bAddRelative);
                baddtext = itemView.findViewById(R.id.baddtext);
                bFollow = itemView.findViewById(R.id.bFollow);
                bRemove = itemView.findViewById(R.id.bRemove);
                cvMain = itemView.findViewById(R.id.cvMain);
                llMain = itemView.findViewById(R.id.llMain);
                blockrelativeid = itemView.findViewById(R.id.blockrelativeid);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
