package com.sesolutions.ui.resume;

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


class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ContactHolder> {

    private final List<PreviewModel.ResultBean.TemplateIdBean> list;
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

    public PreviewAdapter(List<PreviewModel.ResultBean.TemplateIdBean> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_preview, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            Util.showImageWithGlide(holder.imageviewid, list.get(position).getUrl(), context, R.drawable.placeholder_square);

            if(list.get(position).getIscheck()){
                holder.rlrative.setBackgroundResource(R.drawable.button_tabs);
            }else {
                holder.rlrative.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            holder.imageviewid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int k=0;k<list.size();k++){
                        list.get(k).setIscheck(false);
                        if(k==(list.size()-1)){
                            list.get(position).setIscheck(true);
                            listener.onItemClicked(Constant.Events.CLICKED_HEADER_TITLE,list,position);
                         }
                    }



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

    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView tvAge,titlest;
        CardView cvMain;
        ImageView imageviewid;
        RelativeLayout rlrative;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                imageviewid = itemView.findViewById(R.id.imageviewid);
                rlrative = itemView.findViewById(R.id.rlrative);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
