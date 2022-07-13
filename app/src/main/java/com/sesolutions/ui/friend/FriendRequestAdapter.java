package com.sesolutions.ui.friend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Notifications;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ContactHolder> {

    private final List<Notifications> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final OnLoadMoreListener loadListener;
    private final int cPrimary;
    private final int cText1;
    private int lastPosition;
    private GradientDrawable shape;
    private GradientDrawable shape2;

    public FriendRequestAdapter(List<Notifications> list, Context cntxt, OnUserClickedListener<Integer, String> listener, OnLoadMoreListener loadListenr) {
        this.list = list;
        this.context = cntxt;
        this.lastPosition = -1;
        this.listener = listener;
        this.loadListener = loadListenr;
        cPrimary = Color.parseColor(Constant.menuButtonActiveTitleColor);
        cText1 = Color.parseColor(Constant.text_color_1);
        createRoundedFilled();
        createRoundedHolo();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull FriendRequestAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ContactHolder(view);
    }

    private void createRoundedFilled() {
        shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setColor(cPrimary);
        //  shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }

    private void createRoundedHolo() {
        shape2 = new GradientDrawable();
        shape2.setShape(GradientDrawable.RECTANGLE);
        shape2.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        // shape.setColor(colorPrimary);
        shape2.setStroke(2, cPrimary);
        // v.findViewById(R.id.llCommentEditetext).setBackground(shape);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {

            final Notifications vo = list.get(position);

            holder.tvTitle.setTextColor(cText1);
            holder.tvTitle.setText(vo.getTitle());
            holder.bAccept.setBackground(shape);
            holder.bIgnore.setBackground(shape2);
            holder.bIgnore.setTextColor(cPrimary);
            holder.bAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MEMBER_ADD, Constant.URL_ADD_FRIEND, holder.getAdapterPosition());
                }
            });
            holder.bIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MEMBER_ADD, Constant.URL_REJECT, holder.getAdapterPosition());
                }
            });

            Util.showImageWithGlide(holder.ivImage, vo.getUserImage(), context, R.drawable.placeholder_3_2);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", holder.getAdapterPosition());
                }
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //   return 10;
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvTitle;
        protected AppCompatButton bAccept;
        protected AppCompatButton bIgnore;
        protected ImageView ivImage;
        protected View cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            cvMain = itemView.findViewById(R.id.cvMain);
            bAccept = itemView.findViewById(R.id.bAccept);
            bIgnore = itemView.findViewById(R.id.bIgnore);
            ivImage = itemView.findViewById(AppConfiguration.memberImageShapeIsRound ? R.id.ivImage : R.id.ivImage1);
        }
    }
}
