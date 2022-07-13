package com.sesolutions.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.PeopleSuggestion;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class FeedSuggestionAdapter extends RecyclerView.Adapter<FeedSuggestionAdapter.ContactHolder> {

    private final List<PeopleSuggestion> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int cPrimary;
    private final String txtRemove;
    private final int buttonTitleColor;
    private boolean isSuggestionView;
    private final ThemeManager themeManager;
    private GradientDrawable shape;
    private GradientDrawable shape2;
    private int parentPosition;


    public FeedSuggestionAdapter(List<PeopleSuggestion> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        createRoundedFilled();
        createRoundedHolo();
        txtRemove = context.getResources().getString(R.string.remove);
        cPrimary = Color.parseColor(Constant.menuButtonActiveTitleColor);
        buttonTitleColor = Color.parseColor(Constant.outsideButtonTitleColor);
        themeManager = new ThemeManager();
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isSuggestionView ? R.layout.item_friend_request : R.layout.item_peaple_suggestion_child, parent, false);
        return new ContactHolder(view);
    }

    private void createRoundedFilled() {
        shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        //  shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }

    private void createRoundedHolo() {
        shape2 = new GradientDrawable();
        shape2.setShape(GradientDrawable.RECTANGLE);
        shape2.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        // shape.setColor(colorPrimary);
        shape2.setStroke(2, Color.parseColor(Constant.menuButtonActiveTitleColor));
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final PeopleSuggestion vo = list.get(position);
            holder.tvUser.setText(vo.getTitle());
            holder.tvMutual.setText(vo.getMutualFriends());

            holder.bAccept.setBackground(shape);
            holder.bAccept.setTextColor(buttonTitleColor);
            holder.bIgnore.setBackground(shape2);
            holder.bIgnore.setTextColor(cPrimary);
            holder.bIgnore.setText(txtRemove);
            holder.bAccept.setOnClickListener(v -> {
                listener.onItemClicked(Constant.Events.MEMBER_ADD, Constant.URL_MEMBER_ADD, vo.getUserId());
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            });
            holder.bIgnore.setOnClickListener(v -> {
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                // notifyItemRangeChanged(position);
                // notifyDataSetChanged();
                //listener.onItemClicked(Constant.Events.MEMBER_REMOVE, Constant.URL_REJECT, holder.getAdapterPosition());
            });

            Util.showImageWithGlide(holder.ivImage, vo.getUser_image(), context, R.drawable.placeholder_square);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, holder, vo.getUserId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setSuggestionView(boolean suggestionView) {
        isSuggestionView = suggestionView;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvUser;
        ImageView ivImage;
        Button bAccept;
        Button bIgnore;
        View cvMain;
        TextView tvMutual;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvMutual = itemView.findViewById(R.id.tvMutual);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvUser = itemView.findViewById(R.id.tvTitle);
                bAccept = itemView.findViewById(R.id.bAccept);
                bIgnore = itemView.findViewById(R.id.bIgnore);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
