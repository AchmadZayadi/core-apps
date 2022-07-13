package com.sesolutions.ui.resume;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
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
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ContactHolder> {

    private static final String TAG = "MyVideoAdapter";

    private int loggedInId;
    private final Context context;
    private final List<Videos> list;
    private final Typeface iconFont;
    private final ThemeManager themeManager;
    private final OnLoadMoreListener loadListener;
    private final OnUserClickedListener<Integer, Object> listener;
    /* private final Drawable dLike;
     private final Drawable dLikeSelected;
     private final Drawable addDrawable;
     private final Drawable dFavSelected;
     private final Drawable dFav;*/
    //  private final int SCREEN_TYPE;.private int loggedInId;

    @Override
    public void onViewAttachedToWindow(@NonNull ContactHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 1 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    public void setLoggedInId(int loggedInId) {
        this.loggedInId = loggedInId;
    }

    public MyVideoAdapter(List<Videos> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, final int SCREEN_TYPE) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.loadListener = loadListener;
        themeManager = new ThemeManager();
        //   this.SCREEN_TYPE = SCREEN_TYPE;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  addDrawable = ContextCompat.getDrawable(context, R.drawable.music_add);
        // dLike = ContextCompat.getDrawable(context, R.drawable.music_like);
        // dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected);
        //  dFav = ContextCompat.getDrawable(context, R.drawable.music_favourite);
        // dFavSelected = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_album, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            final Videos vo = list.get(position);
            holder.tvSongTitle.setText(vo.getTitle());


            holder.ivArtist.setTypeface(iconFont);
            holder.ivArtist.setText(Constant.FontIcon.FOLDER);
            holder.tvArtist.setText(vo.getCategoryTitle());
            holder.llArtist.setVisibility(TextUtils.isEmpty(vo.getCategoryTitle()) ? View.GONE : View.VISIBLE);


            holder.tvSongDetail.setTypeface(iconFont);
            holder.ivCreatepage.setTypeface(iconFont);

            holder.tvSongDetail.setText(vo.getStatsString(false));
            Util.showImageWithGlide(holder.ivSongImage, vo.getImageUrl(), context, R.drawable.placeholder_square);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "", holder.getAdapterPosition()));
            //holder.ivOption.setVisibility(null != vo.getMenus() ? View.VISIBLE : View.GONE);
            holder.ivOption.setVisibility(View.GONE);
            holder.editrlid.setOnClickListener(v ->
                    // Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus(), listener)
                    listener.onItemClicked(Constant.Events.MUSIC_EDIT, holder.getAdapterPosition(), 0)
            );
            holder.deleterlid.setOnClickListener(v ->
                    // Util.showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus(), listener)
                    listener.onItemClicked(Constant.Events.MUSIC_DELETE, holder.getAdapterPosition(), 0)
            );

            holder.ivCreatepage.setText(Constant.FontIcon.CALENDAR);
            holder.tvCreatepage.setText(Util.changeFormat(vo.getCreation_date()));
            holder.llCreatepage.setVisibility(vo.getCreation_date() != null ? View.VISIBLE : View.GONE);



        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvSongTitle;
        protected TextView tvSongDetail;
        protected TextView tvArtist,ivCreatepage,tvCreatepage;
        protected TextView ivArtist;
        protected View llArtist,llCreatepage;
        protected ImageView ivSongImage;
        protected ImageView ivOption;
        protected CardView cvMain;
        RelativeLayout editrlid,deleterlid;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                ivCreatepage = itemView.findViewById(R.id.ivCreatepage);
                tvCreatepage = itemView.findViewById(R.id.tvCreatepage);
                tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                ivArtist = itemView.findViewById(R.id.ivArtist);
                llArtist = itemView.findViewById(R.id.llArtist);
                tvSongDetail = itemView.findViewById(R.id.tvSongDetail);
                ivSongImage = itemView.findViewById(R.id.ivSongImage);
                ivOption = itemView.findViewById(R.id.ivOption);
                llCreatepage = itemView.findViewById(R.id.llCreatepage);
                editrlid = itemView.findViewById(R.id.editrlid);
                deleterlid = itemView.findViewById(R.id.deleterlid);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
