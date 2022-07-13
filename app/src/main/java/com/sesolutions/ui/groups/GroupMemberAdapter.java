package com.sesolutions.ui.groups;

import android.content.Context;
import androidx.annotation.NonNull;
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
import com.sesolutions.responses.groups.GroupMember;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ContactHolder> {

    private final List<GroupMember> listFriends;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private final String TXT_ADDED_ON;

    private boolean IS_SHOWING_APPROVED_MEMBER;


    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((getItemCount()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public GroupMemberAdapter(List<GroupMember> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final GroupMember eventItem = listFriends.get(position);

            holder.tvName.setText(eventItem.getDisplayname());
            Util.showImageWithGlide(holder.ivImage, eventItem.getOwnerPhoto(), context, R.drawable.placeholder_square);


            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, holder, holder.getAdapterPosition()));

            if (IS_SHOWING_APPROVED_MEMBER) {
                holder.tvQuestion1.setVisibility(View.GONE);
                holder.tvQuestion2.setVisibility(View.GONE);
                holder.tvQuestion3.setVisibility(View.GONE);
                holder.tvQuestion4.setVisibility(View.GONE);
                holder.tvQuestion5.setVisibility(View.GONE);
                holder.tvAnswer1.setVisibility(View.GONE);
                holder.tvAnswer2.setVisibility(View.GONE);
                holder.tvAnswer3.setVisibility(View.GONE);
                holder.tvAnswer4.setVisibility(View.GONE);
                holder.tvAnswer5.setVisibility(View.GONE);
            } else {
                holder.tvQuestion1.setVisibility(!TextUtils.isEmpty(eventItem.getQuestion_1()) ? View.VISIBLE : View.GONE);
                holder.tvQuestion2.setVisibility(!TextUtils.isEmpty(eventItem.getQuestion_2()) ? View.VISIBLE : View.GONE);
                holder.tvQuestion3.setVisibility(!TextUtils.isEmpty(eventItem.getQuestion_3()) ? View.VISIBLE : View.GONE);
                holder.tvQuestion4.setVisibility(!TextUtils.isEmpty(eventItem.getQuestion_4()) ? View.VISIBLE : View.GONE);
                holder.tvQuestion5.setVisibility(!TextUtils.isEmpty(eventItem.getQuestion_5()) ? View.VISIBLE : View.GONE);

                holder.tvAnswer1.setVisibility(!TextUtils.isEmpty(eventItem.getAnswer_1()) ? View.VISIBLE : View.GONE);
                holder.tvAnswer2.setVisibility(!TextUtils.isEmpty(eventItem.getAnswer_2()) ? View.VISIBLE : View.GONE);
                holder.tvAnswer3.setVisibility(!TextUtils.isEmpty(eventItem.getAnswer_3()) ? View.VISIBLE : View.GONE);
                holder.tvAnswer4.setVisibility(!TextUtils.isEmpty(eventItem.getAnswer_4()) ? View.VISIBLE : View.GONE);
                holder.tvAnswer5.setVisibility(!TextUtils.isEmpty(eventItem.getAnswer_5()) ? View.VISIBLE : View.GONE);

                holder.tvQuestion1.setText(eventItem.getQuestion_1());
                holder.tvQuestion2.setText(eventItem.getQuestion_2());
                holder.tvQuestion3.setText(eventItem.getQuestion_3());
                holder.tvQuestion4.setText(eventItem.getQuestion_4());
                holder.tvQuestion5.setText(eventItem.getQuestion_5());

                holder.tvAnswer1.setText(eventItem.getAnswer_1());
                holder.tvAnswer2.setText(eventItem.getAnswer_2());
                holder.tvAnswer3.setText(eventItem.getAnswer_3());
                holder.tvAnswer4.setText(eventItem.getAnswer_4());
                holder.tvAnswer5.setText(eventItem.getAnswer_5());
            }


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

    public void setType(boolean b) {
        this.IS_SHOWING_APPROVED_MEMBER = b;
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        protected TextView tvJoinedOn;
        protected View ivOption;
        public ImageView ivImage;
        protected CardView cvMain;
        private TextView tvQuestion1;
        private TextView tvQuestion2;
        private TextView tvQuestion3;
        private TextView tvQuestion4;
        private TextView tvQuestion5;

        private TextView tvAnswer1;
        private TextView tvAnswer2;
        private TextView tvAnswer3;
        private TextView tvAnswer4;
        private TextView tvAnswer5;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvName = itemView.findViewById(R.id.tvName);
                tvQuestion1 = itemView.findViewById(R.id.tvQuestion1);
                tvQuestion2 = itemView.findViewById(R.id.tvQuestion2);
                tvQuestion3 = itemView.findViewById(R.id.tvQuestion3);
                tvQuestion4 = itemView.findViewById(R.id.tvQuestion4);
                tvQuestion5 = itemView.findViewById(R.id.tvQuestion5);

                tvAnswer1 = itemView.findViewById(R.id.tvAnswer1);
                tvAnswer2 = itemView.findViewById(R.id.tvAnswer2);
                tvAnswer3 = itemView.findViewById(R.id.tvAnswer3);
                tvAnswer4 = itemView.findViewById(R.id.tvAnswer4);
                tvAnswer5 = itemView.findViewById(R.id.tvAnswer5);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                cvMain = itemView.findViewById(R.id.cvMain);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
