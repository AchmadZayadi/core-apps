package com.sesolutions.ui.member;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ContactHolder> {

    private final List<Notifications> list;
    private final List<Friends> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;


    @Override
    public void onViewAttachedToWindow(@NonNull MemberAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MemberAdapter(List<Notifications> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.listFriends = null;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        createRoundedFilled();
        themeManager = new ThemeManager();

    }

    public MemberAdapter(List<Friends> list, OnLoadMoreListener loadMoreListener, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            if (isSuggestion) {
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
            } else {
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
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition());
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
        protected TextView tvAge;
        protected TextView tvMutual;
        protected TextView tvName;
        protected View vAge;
        protected View vAddress;
        protected View vMutual;
        protected AppCompatButton bAdd;
        protected View bRemove;
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
                bRemove = itemView.findViewById(R.id.bRemove);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
