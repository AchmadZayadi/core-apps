package com.sesolutions.ui.member;

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


public class MoreMemberAdapter extends RecyclerView.Adapter<MoreMemberAdapter.ContactHolder> {

    private final List<UserMaster> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_ADDED_ON;
    private String type;


    @Override
    public void onViewAttachedToWindow(@NonNull MoreMemberAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public MoreMemberAdapter(List<UserMaster> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener, String type) {
        this.listFriends = list;
        this.type = type;
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
            Log.e("NNCLIK","NNCLIK");
          //  themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final UserMaster eventItem = listFriends.get(position);
            switch (type) {
                case "page":
                    holder.tvName.setText(eventItem.getDisplayname());
                    holder.tvJoinedOn.setText(TXT_ADDED_ON + Util.changeFormat(eventItem.getCreationDate()));
                    Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);
                    break;
                case "poll":
                    holder.tvName.setText(eventItem.getDisplayname());
                    holder.tvJoinedOn.setText(eventItem.getUsername());
                    Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);
                    break;
                default:
                    holder.tvName.setText(eventItem.getName());
                    holder.tvJoinedOn.setVisibility(View.GONE);
                    Util.showImageWithGlide(holder.ivImage, eventItem.getProfileImageUrl(), context, R.drawable.placeholder_square);
                    break;

            }

            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("CLIK","CLIK");
                    listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition());
                }
            });


            holder.ivOption.setVisibility(null != eventItem.getOptions() ? View.VISIBLE : View.GONE);
           /* holder.ivOption.setOnClickListener(v -> {
                showOptionsPopUp(v, holder.getAdapterPosition());
            });*/

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

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvName;
        protected TextView tvJoinedOn;
        protected View ivOption;
        protected ImageView ivImage;
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
