package com.sesolutions.ui.games;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.sesolutions.utils.Util;

import java.util.List;


class GameBrowesAdapter extends RecyclerView.Adapter<GameBrowesAdapter.ContactHolder> {

    private final List<GameBrowsModel.ResultBean.GamesBean> list;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;




    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public GameBrowesAdapter(List<GameBrowsModel.ResultBean.GamesBean> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.loadListener = loadMoreListener;
        this.listener = listener;
        this.isSuggestion = false;
        createRoundedFilled();
        themeManager = new ThemeManager();

    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_browse, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            Util.showImageWithGlide(holder.imageviewid, list.get(position).getImages(), context, R.drawable.placeholder_square);
            holder.titleid.setText(""+list.get(position).getTitle());

            holder.rlrative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_TITLE,list,list.get(position).getGame_id());
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



    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView titleid,titlest;
        CardView cvMain;
        ImageView imageviewid;
        RelativeLayout rlrative;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                imageviewid = itemView.findViewById(R.id.imageviewid);
                rlrative = itemView.findViewById(R.id.rlrative);
                titleid = itemView.findViewById(R.id.titleid);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
