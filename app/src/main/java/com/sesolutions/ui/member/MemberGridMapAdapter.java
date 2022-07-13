package com.sesolutions.ui.member;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MemberGridMapAdapter extends RecyclerView.Adapter<MemberGridMapAdapter.ContactHolder> {

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


    public void showAsGrid() {
        isGrid = true;
    }


    @Override
    public void onViewAttachedToWindow(MemberGridMapAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && /*(getItemCount() > (Constant.RECYCLE_ITEM_THRESHOLD - 1)) && */(getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MemberGridMapAdapter(List<Notifications> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener, int listtype) {
        this.list = list;
        this.listFriends = null;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        this.lastPosition = -1;
        themeManager = new ThemeManager();
        this.ListType=listtype;
    }





    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_favorite_guides, parent, false);
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
            if (null != eventItem.getMembership()) {
                holder.bAdd.setVisibility(View.VISIBLE);
                holder.bAdd.setText(eventItem.getMembership().getLabel());
            } else {
                holder.bAdd.setVisibility(View.GONE);
            }

            if (null != eventItem.getFollow()) {
                holder.bFollow.setVisibility(View.VISIBLE);
                holder.bFollow.setText(eventItem.getFollow().getText());
            } else {
                holder.bFollow.setVisibility(View.GONE);
            }
            Util.showImageWithGlide(holder.ivImage, eventItem.getUserImage(), context, R.drawable.placeholder_square);
            holder.bAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_ADD, "", holder.getAdapterPosition()));

            holder.bFollow.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_FOLLOW, "", holder.getAdapterPosition()));

            holder.ivImage.setOnClickListener(v -> {
                //sending image object for transition
                listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE2, holder, holder.getAdapterPosition());
            });

            holder.tvName.setOnClickListener(v -> {
                //sending image object for transition
                listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE2, holder, holder.getAdapterPosition());
            });

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
        protected TextView tvName;
        protected View vAge;
        protected View vAddress;
        protected View vMutual;
        protected AppCompatButton bAdd;
        protected AppCompatButton bFollow;
        protected View bRemove;
        protected View llMain;
        protected ImageView ivImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                tvAddress = itemView.findViewById(R.id.tvAddress);
                tvAge = itemView.findViewById(R.id.tvAge);
                tvMutual = itemView.findViewById(R.id.tvMutual);
                vAge = itemView.findViewById(R.id.llAge);
                vAddress = itemView.findViewById(R.id.llAddress);
                vMutual = itemView.findViewById(R.id.llMutual);
                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                bAdd = itemView.findViewById(R.id.bAdd);
                bFollow = itemView.findViewById(R.id.bFollow);
                bRemove = itemView.findViewById(R.id.bRemove);
                cvMain = itemView.findViewById(R.id.cvMain);
                llMain = itemView.findViewById(R.id.llMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
