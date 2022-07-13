package com.sesolutions.ui.page;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class PageMemberAdapter extends RecyclerView.Adapter<PageMemberAdapter.ContactHolder> {

    private final List<UserMaster> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_ADDED_ON;

    private boolean owner = false;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull PageMemberAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public PageMemberAdapter(List<UserMaster> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.listFriends = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.TXT_ADDED_ON = context.getResources().getString(R.string.added_on_);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_page, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final UserMaster eventItem = listFriends.get(position);

            Log.e("TYPEDATA",""+type);
            if (Constant.ResourceType.GROUP.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setVisibility(View.GONE);
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);

            } else if (Constant.ResourceType.PAGE.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setText(TXT_ADDED_ON + Util.changeFormat(eventItem.getCreationDate()));
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);

            }else if (Constant.ResourceType.CLASSROOM.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setText(TXT_ADDED_ON + Util.changeFormat(eventItem.getCreationDate()));
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);

            }else if (Constant.ResourceType.STORE.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setText(TXT_ADDED_ON + Util.changeFormat(eventItem.getCreationDate()));
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);

            } else if (Constant.ResourceType.SES_EVENT.equals(type)) {
                holder.tvName.setText(eventItem.getMemberTitle());
                holder.tvJoinedOn.setText(eventItem.getRSVP());
                Util.showImageWithGlide(holder.ivImage, eventItem.getMemberPhoto(), context, R.drawable.placeholder_square);

            } else if (Constant.ResourceType.EVENT.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setText(eventItem.getRSVP());
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);
            }

            else if (Constant.ResourceType.BUSINESS.equals(type)) {
                holder.tvName.setText(eventItem.getDisplayname());
                holder.tvJoinedOn.setText(eventItem.getRSVP());
                Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);
            }


            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

            holder.ivOption.setVisibility(null != eventItem.getOptions() ? View.VISIBLE : View.GONE);
            holder.ivOption.setOnClickListener(v -> {
                showOptionsPopUp(v, holder.getAdapterPosition());
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showOptionsPopUp(View v, int position) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, listFriends.get(position).getOptions());
            int vertPos = RelativePopupWindow.VerticalPosition.BELOW;
            int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public void setType(String type) {
        this.type = type;
    }

/*    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }*/


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        protected TextView tvJoinedOn;
        protected View ivOption;
        public ImageView ivImage;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvName = itemView.findViewById(R.id.tvName);
                tvJoinedOn = itemView.findViewById(R.id.tvJoinedOn);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
