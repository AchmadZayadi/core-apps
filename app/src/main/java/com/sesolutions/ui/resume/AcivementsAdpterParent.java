package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


class AcivementsAdpterParent extends RecyclerView.Adapter<AcivementsAdpterParent.ContactHolder> {

    private final List<AchivementParentModel> achivementParentModels;
    private final Context context;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    private final ThemeManager themeManager;
    private boolean isSuggestion = false;
    private boolean isAddRemove = false;
    private boolean owner = false;
    private GradientDrawable shape;
    int FLatatbPostion=0;




    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null != loadListener && getItemCount() - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public AcivementsAdpterParent(List<AchivementParentModel> list, Context cntxt, OnLoadMoreListener loadMoreListener, OnUserClickedListener<Integer, Object> listener) {
        this.achivementParentModels = list;
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
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resume_achivement_parent, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
         //   themeManager.applyTheme((ViewGroup) holder.itemView, context);
            if(position==0){
                holder.titleview.setText("Manage Achivements");
                holder.addachivements.setText("+ Add AChivements");

                holder.notextid.setText("You have not added any Achievements yet.");

                try {
                    if(achivementParentModels.get(0).getResult()!=null && achivementParentModels.get(0).getResult().getAchievements()!=null
                            & achivementParentModels.get(0).getResult().getAchievements().size()>0){
                        holder.rvChild.setVisibility(View.VISIBLE);
                        holder.rvChild.setHasFixedSize(true);
                        //    holder.rvChild.setRecycledViewPool(viewPool);
                        //    final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT1, StaggeredGridLayoutManager.VERTICAL);

                        holder.rvChild.setLayoutManager(layoutManager);

                        holder.rvChild.setLayoutManager(layoutManager);
                        holder.adapter2 = new AchivementsAdpterSub2(achivementParentModels.get(0).getResult().getAchievements(), context, listener);
                        holder.rvChild.setAdapter(holder.adapter2);
                        holder.rvChild.setVisibility(View.VISIBLE);

                        holder.notextid.setVisibility(View.GONE);
                        holder.notextrl.setVisibility(View.GONE);
                    }else {
                        holder.rvChild.setVisibility(View.GONE);

                        holder.notextid.setVisibility(View.VISIBLE);
                        holder.notextrl.setVisibility(View.VISIBLE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.rvChild.setVisibility(View.GONE);
                    holder.notextid.setVisibility(View.VISIBLE);
                    holder.notextrl.setVisibility(View.VISIBLE);
                }



            }else {
                holder.titleview.setText("Manage Curricular Activities");
                holder.addachivements.setText("+ Add Curricular Activities");
                holder.notextid.setText("You have not added any Curricular Activities yet.");

                try {
                    if(achivementParentModels.get(0).getResult()!=null && achivementParentModels.get(0).getResult().getCurriculars()!=null
                            & achivementParentModels.get(0).getResult().getCurriculars().size()>0){
                        holder.rvChild.setHasFixedSize(true);
                        holder.rvChild.setVisibility(View.VISIBLE);
                        //    holder.rvChild.setRecycledViewPool(viewPool);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        holder.rvChild.setLayoutManager(layoutManager);
                        holder.adapter = new AchivementsAdpterSub(achivementParentModels.get(0).getResult().getCurriculars(), context, listener);
                        holder.rvChild.setAdapter(holder.adapter);

                        holder.rvChild.setVisibility(View.VISIBLE);

                        holder.notextid.setVisibility(View.GONE);
                        holder.notextrl.setVisibility(View.GONE);
                    }else {
                        holder.rvChild.setVisibility(View.GONE);
                        holder.notextid.setVisibility(View.VISIBLE);
                        holder.notextrl.setVisibility(View.VISIBLE);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.rvChild.setVisibility(View.GONE);
                    holder.notextid.setVisibility(View.VISIBLE);
                    holder.notextrl.setVisibility(View.VISIBLE);
                }

            }

            holder.addachivements.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_HEADER_ADD, "", position);
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

        return  2;
    }

    public void setAddRemove(boolean addRemove) {
        isAddRemove = addRemove;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }


    public static class ContactHolder extends RecyclerView.ViewHolder {
        protected TextView tvAddress;
        protected TextView titleview,addachivements;
        RecyclerView rvChild;
        protected AchivementsAdpterSub2 adapter2;
        protected AchivementsAdpterSub adapter;
        RelativeLayout notextrl;
        TextView notextid;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                titleview = itemView.findViewById(R.id.titleview);
                addachivements = itemView.findViewById(R.id.addachivements);
                rvChild = itemView.findViewById(R.id.recylerachivementid);
                notextrl = itemView.findViewById(R.id.notextrl);
                notextid = itemView.findViewById(R.id.notextid);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
