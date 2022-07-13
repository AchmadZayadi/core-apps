package com.sesolutions.ui.job;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.responses.jobs.JobsResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;


public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ContactHolder> {

    private final List<JobsResponse> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final OnLoadMoreListener loadListener;
    private final int SCREEN_TYPE;
    private final Typeface iconFont;
    private final Drawable dLike;
    private final Drawable dLikeSelected;
    private final Drawable dFavSelected;
    private final Drawable dFav;
    private final Drawable dStarFilled;
    private final Drawable dStarUnFilled;
    private final ThemeManager themeManager;
    private final boolean isUserLoggedIn;
    private int loggedInId;
    public  Drawable dSave;
    public  Drawable dUnsave;


    public void setLoggedInId(int loggedInId) {
        this.loggedInId = loggedInId;
    }

    @Override
    public void onViewAttachedToWindow(JobAdapter.ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public JobAdapter(List<JobsResponse> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.SCREEN_TYPE = SCREEN_TYPE;
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //this.foreground = Color.parseColor(Constant.text_color_2);
        dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
        dStarFilled = ContextCompat.getDrawable(context, R.drawable.star_filled);
        dStarUnFilled = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        themeManager = new ThemeManager();
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);

    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=null;
        if(SCREEN_TYPE==Constant.FormType.MY_TYPE_JOB){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myjobs, parent, false);
        } else if(SCREEN_TYPE==Constant.FormType.MY_TYPE_BROWSE_COMPANY_JOB){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browsecompany, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jobs, parent, false);
        }
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {
        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final JobsResponse vo = list.get(position);

            if(SCREEN_TYPE==Constant.FormType.MY_TYPE_JOB){
                holder.companyname.setTypeface(iconFont);
                holder.expriense.setTypeface(iconFont);
                holder.userlocation.setTypeface(iconFont);
               // holder.companyname.setText(Constant.FontIcon.FOLDER+"  "+"Science and Technology");
                holder.expriense.setText(Constant.FontIcon.SHOTCASE+"  "+vo.getExperience());
                holder.jobtitle.setText(""+vo.getTitle());
                holder.companyname.setText(Constant.FontIcon.FOLDER+""+vo.getIndustry_title());
                if(vo.getLocation().length()>0){
                    holder.userlocation.setText(""+Constant.FontIcon.MAP_MARKER+"  "+vo.getLocation());
                    holder.userlocation.setVisibility(View.VISIBLE);
                }else {
                    holder.userlocation.setVisibility(View.GONE);
                }
                holder.postedbyid.setText("Posted by "+Util.changeDateFormat(context,vo.getPublishDate()));
                holder.ivOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(context, holder.ivOption);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.view_myjob_menu);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.editjobs:
                                        listener.onItemClicked(Constant.Events.CLICKED_EDIT_JOB,position, list.get(position).getJob_id());
                                        break;
                                    case R.id.deletejobs:
                                        listener.onItemClicked(Constant.Events.CLICKED_DELETE_JOB, position, list.get(position).getJob_id());
                                        break;
                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();
                    }
                });

            }else if(SCREEN_TYPE==Constant.FormType.MY_TYPE_BROWSE_COMPANY_JOB){
                holder.userlocation.setTypeface(iconFont);
                holder.expriense.setTypeface(iconFont);
                holder.companyname.setTypeface(iconFont);
                holder.expriense.setText(Constant.FontIcon.CHECKDATA+" "+vo.getSubscribe_count()+"   "+Constant.FontIcon.SHOTCASE+"  "+vo.getJob_count());
                holder.expriense.setVisibility(View.VISIBLE);

              /*  if(vo.getLocation().length()>0){
                    holder.userlocation.setText(""+Constant.FontIcon.MAP_MARKER+"  "+vo.getLocation());
                    holder.userlocation.setVisibility(View.VISIBLE);
                }else {
                    holder.userlocation.setVisibility(View.GONE);
                }*/
                holder.userlocation.setVisibility(View.GONE);

                holder.jobtitle.setText(""+vo.getCompany_name());
                holder.companyname.setText(Constant.FontIcon.FOLDER+""+vo.getIndustry_title());
                holder.postedbyid.setText("Posted by "+Util.changeDateFormat(context,vo.getPublishDate()));
                Util.showImageWithGlide(holder.comanyimageid, vo.getCompany_image(), context, R.drawable.placeholder_square);
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMPANY_VIEW, holder, holder.getAdapterPosition()));

                holder.ivFbShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 1));
                holder.ivWhatsAppShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 2));
                holder.ivImageShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 3));

                try {
                    holder.ivSaveFeed.setImageDrawable(vo.getShortcut_save().isIs_saved() ?  dUnsave:dSave);
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.ivSaveFeed.setVisibility(View.GONE);
                    holder.ivFbShare.setVisibility(View.GONE);
                    holder.ivWhatsAppShare.setVisibility(View.GONE);
                    holder.ivImageShare.setVisibility(View.GONE);
                }

                holder.ivSaveFeed.setOnClickListener(v -> {
                    if(vo.getShortcut_save().isIs_saved()){
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                    }else {
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), 0);
                    }
                });

             }else {
                holder.userlocation.setTypeface(iconFont);
                holder.expriense.setTypeface(iconFont);
                holder.companyname.setTypeface(iconFont);
                holder.expriense.setText(Constant.FontIcon.SHOTCASE+"  "+vo.getExperience());
                holder.expriense.setVisibility(View.VISIBLE);

                if(vo.getLocation().length()>0){
                    holder.userlocation.setText(""+Constant.FontIcon.MAP_MARKER+"  "+vo.getLocation());
                    holder.userlocation.setVisibility(View.VISIBLE);
                }else {
                    holder.userlocation.setVisibility(View.GONE);
                }

                holder.jobtitle.setText(""+vo.getTitle());
                holder.companyname.setText(Constant.FontIcon.FOLDER+""+vo.getCompany_name());
                holder.postedbyid.setText("Posted by "+Util.changeDateFormat(context,vo.getPublishDate()));
                Util.showImageWithGlide(holder.comanyimageid, vo.getJobs_image(), context, R.drawable.placeholder_square);
                holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, holder.getAdapterPosition()));

                holder.ivFbShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 1));
                holder.ivWhatsAppShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 2));
                holder.ivImageShare.setOnClickListener(v ->
                        listener.onItemClicked(Constant.Events.SHARE_FEED, vo.getShare(), 3));

                try {
                    holder.ivSaveFeed.setImageDrawable(vo.getShortcut_save().isIs_saved() ?  dUnsave:dSave);
                }catch (Exception ex){
                    ex.printStackTrace();
                    holder.ivSaveFeed.setVisibility(View.GONE);
                    holder.ivFbShare.setVisibility(View.GONE);
                    holder.ivWhatsAppShare.setVisibility(View.GONE);
                    holder.ivImageShare.setVisibility(View.GONE);
                }

                holder.ivSaveFeed.setOnClickListener(v -> {
                    if(vo.getShortcut_save().isIs_saved()){
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), vo.getShortcut_save().getShortcut_id());
                    }else {
                        listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION2, "" + holder.getAdapterPosition(), 0);
                    }
                });

           }



        /*    holder.tvSongTitle.setText(vo.getTitle());

            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.USER);
            holder.tvArtist.setText(vo.getOwnerTitle());
            holder.rlArtist.setVisibility(SCREEN_TYPE == Constant.FormType.TYPE_MY_ALBUMS ? View.GONE : View.VISIBLE);
            holder.ivAdd.setVisibility(loggedInId == vo.getOwnerId() ? View.VISIBLE : View.GONE);
            holder.tvBody.setText(vo.getBody());
            holder.ivDate.setTypeface(iconFont);
            holder.ivDate.setText(Constant.FontIcon.CALENDAR);
            holder.tvDate.setText(Util.changeDateFormat(context, vo.getCreationDate()));

            Util.showImageWithGlide(holder.ivSongImage, vo.getImages().getMain(), context, R.drawable.placeholder_square);
            holder.llReactionOption.setVisibility(isUserLoggedIn ? View.VISIBLE : View.INVISIBLE);

            holder.ivLike.setVisibility(vo.canLike() ? View.VISIBLE : View.GONE);
            holder.ivFavorite.setVisibility(vo.canFavourite() ? View.VISIBLE : View.GONE);
            holder.ivLike.setImageDrawable(vo.isContentLike() ? dLikeSelected : dLike);
            holder.ivFavorite.setImageDrawable(vo.isContentFavourite() ? dFavSelected : dFav);
            if (SCREEN_TYPE == Constant.FormType.TYPE_MUSIC_ALBUM || SCREEN_TYPE == Constant.FormType.TYPE_CHANNEL) {
                holder.llStar.setVisibility(View.VISIBLE);
                holder.ivStar1.setImageDrawable(vo.getIntRating() > 0 ? dStarFilled : dStarUnFilled);
                holder.ivStar2.setImageDrawable(vo.getIntRating() > 1 ? dStarFilled : dStarUnFilled);
                holder.ivStar3.setImageDrawable(vo.getIntRating() > 2 ? dStarFilled : dStarUnFilled);
                holder.ivStar4.setImageDrawable(vo.getIntRating() > 3 ? dStarFilled : dStarUnFilled);
                holder.ivStar5.setImageDrawable(vo.getIntRating() > 4 ? dStarFilled : dStarUnFilled);
            } else {
                holder.llStar.setVisibility(View.GONE);
            }
            holder.ivLike.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, "" + SCREEN_TYPE, holder.getAdapterPosition()));
            holder.ivAdd.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_ADD, holder.ivAdd, holder.getAdapterPosition()));
            holder.ivFavorite.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, "" + SCREEN_TYPE, holder.getAdapterPosition()));*/

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
       TextView userlocation,expriense,companyname,jobtitle,postedbyid;
       CardView cvMain;
       ImageView comanyimageid;
       ImageView ivOption;

        private AppCompatImageView ivFbShare;
        private AppCompatImageView ivWhatsAppShare;
        private AppCompatImageView ivImageShare;
        private AppCompatImageView ivSaveFeed;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                userlocation=itemView.findViewById(R.id.userlocation);
                expriense=itemView.findViewById(R.id.expriense);
                comanyimageid=itemView.findViewById(R.id.comanyimageid);
                postedbyid=itemView.findViewById(R.id.postedbyid);
                companyname=itemView.findViewById(R.id.companyname);
                cvMain=itemView.findViewById(R.id.cvMain);
                jobtitle=itemView.findViewById(R.id.jobtitle);
                ivOption=itemView.findViewById(R.id.ivOption);

                ivFbShare=itemView.findViewById(R.id.ivFbShare);
                ivWhatsAppShare=itemView.findViewById(R.id.ivWhatsAppShare);
                ivImageShare=itemView.findViewById(R.id.ivImageShare);
                ivSaveFeed=itemView.findViewById(R.id.ivSaveFeed);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
