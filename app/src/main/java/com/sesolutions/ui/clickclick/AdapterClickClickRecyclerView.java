package com.sesolutions.ui.clickclick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.clickclick.music.JZMediaExo2;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SPref;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

import cn.jzvd.JzvdStd2;


public class AdapterClickClickRecyclerView extends RecyclerView.Adapter<AdapterClickClickRecyclerView.MyViewHolder> {

    public static final String TAG = "AdapterTikTokRecyclerView";
    private final Context context;
    private Boolean isFollowing;
    private final List<Videos> list;
    private final OnLoadMoreListener loadListener;
    private HttpProxyCacheServer proxy;
    private final OnUserClickedListener<Integer, Object> listener;
    private boolean isLiked;


    private int likeCountLike;
    private int likeCountUn;
    int menuTitleActiveColor;

    public AdapterClickClickRecyclerView(List<Videos> list, Context context, OnUserClickedListener<Integer, Object> listenr, OnLoadMoreListener loadListener, Boolean isFollowing) {
        this.list = list;
        this.context = context;
        this.listener = listenr;
        this.loadListener = loadListener;
        this.isFollowing = isFollowing;
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_tiktok, parent,
                false));
        return holder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((list.size()) - 3 == holder.getAdapterPosition()) {
            loadListener.onLoadMore();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Videos vo = list.get(position);





        proxy = ((MainApplication) context.getApplicationContext()).getProxy(context);

        if (vo.isContentLike()) {
            isLiked = true;
        //    holder.jzvdStd.ivLike.setColorFilter(menuTitleActiveColor);
            holder.jzvdStd.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        if (!vo.isContentLike()) {
            isLiked = false;
          //  holder.jzvdStd.ivLike.setColorFilter(menuTitleActiveColor);
            holder.jzvdStd.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        StringBuilder s = new StringBuilder(100);
        s.append(vo.getDescription());
        if(s!=null && s.toString().length()>0){
            holder.jzvdStd.tvDescription.setText(s);
            holder.jzvdStd.tvDescription.setVisibility(View.VISIBLE);
        }else {
            holder.jzvdStd.tvDescription.setVisibility(View.GONE);
        }

        holder.jzvdStd.tvUserId.setText("@" + vo.getUserTitle());

        try {
            if(vo.getTitle()!=null && vo.getTitle().length()>0){
                holder.jzvdStd.tvTitle.setVisibility(View.VISIBLE);
                String fromtitle = StringEscapeUtils.unescapeJava(vo.getTitle());
                holder.jzvdStd.tvTitle.setText(""+fromtitle);
            }else {
                holder.jzvdStd.tvTitle.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            if(vo.getTags()!=null && vo.getTags().size()>0){

                Log.e("5454","4545");
                String tagsst="";
                for(int j=0;j<vo.getTags().size();j++){
                    if(j==0){
                        tagsst=vo.getTags().get(j).toString();
                        if(!tagsst.startsWith("#")){
                            tagsst="#"+tagsst;
                        }
                        Log.e("tagst",""+tagsst);
                    }else {
                        if(!vo.getTags().get(j).startsWith("#")){
                            tagsst=tagsst+", #"+vo.getTags().get(j).toString();
                        }else {
                            tagsst=tagsst+", "+vo.getTags().get(j).toString();
                        }
                    }
                }


                String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(tagsst);
                holder.jzvdStd.tvTAg.setText(""+fromServerUnicodeDecoded);
                holder.jzvdStd.tvTAg.setVisibility(View.VISIBLE);
            }else {
                holder.jzvdStd.tvTAg.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }



        holder.jzvdStd.ivUser.setOnClickListener(v ->{
            if (vo.getSong() != null) {
                listener.onItemClicked(Constant.Events.ADD_TO_CART, "", holder.getAdapterPosition());
            }
        }
        );


        if (vo.getSong() != null) {
            holder.jzvdStd.tvMusic.setText(" " + vo.getSong().gettitle());
            holder.jzvdStd.tvMusic.setSelected(true);
        } else {
            holder.jzvdStd.ivUser.setVisibility(View.VISIBLE);
            holder.jzvdStd.tvMusic.setVisibility(View.GONE);
        }
        holder.jzvdStd.tvLikeCount.setText("" + vo.getLikeCount());
        holder.jzvdStd.tvViews.setText("" + vo.getViewCount());
        holder.jzvdStd.tvCommentCount.setText("" + vo.getCommentCount());

        if (vo.getOwnerId() == SPref.getInstance().getLoggedInUserId(context)) {
            holder.jzvdStd.ivFollow.setVisibility(View.GONE);
        } else {
            if (!vo.getIsUserChannelFollow()) {
                isFollowing = false;
                holder.jzvdStd.ivFollow.setImageResource(R.drawable.ic_tiktok_create);
                holder.jzvdStd.ivFollow.setVisibility(View.VISIBLE);
            } else {
                isFollowing = true;
                holder.jzvdStd.ivFollow.setImageResource(R.drawable.check);
                holder.jzvdStd.ivFollow.setVisibility(View.GONE);
            }
        }



        holder.jzvdStd.ivUserImage.setOnClickListener(v -> {
            listener.onItemClicked(Constant.Events.TICK_GO_TO_CHANNEL, "", vo.getOwnerId());
        });

        holder.jzvdStd.ivViews.setOnClickListener(v -> {
        });
        holder.jzvdStd.tvViews.setOnClickListener(v -> {
        });

        holder.jzvdStd.ivFollow.setOnClickListener(v -> {
            if (list.get(position).getIsUserChannelFollow()) {
                holder.jzvdStd.ivFollow.setImageResource(R.drawable.ic_tiktok_create);
                isFollowing = false;
                listener.onItemClicked(Constant.Events.MEMBER_FOLLOW, vo, holder.getAdapterPosition());
              /*  for(Videos article : list)
                {
                    if(article.getVideoId() == vo.getVideoId())
                        article.setIsUserChannelFollow(false);
                }*/

            } else {
                holder.jzvdStd.ivFollow.setImageResource(R.drawable.check);
                holder.jzvdStd.ivFollow.setVisibility(View.GONE);
                isFollowing = true;
                listener.onItemClicked(Constant.Events.MEMBER_FOLLOW, vo, holder.getAdapterPosition());
               /* for(Videos article : list)
                {
                    if(article.getVideoId()== vo.getVideoId())
                        article.setIsUserChannelFollow(true);
                }*/
            }
        });

     //   Glide.with(holder.jzvdStd.getContext()).load(vo.getIframeURL()).into(holder.jzvdStd.thumbImageView);




        if(vo.getImages()!=null && vo.getImages().getMain()!=null){
            Glide.with(holder.jzvdStd.getContext()).load(vo.getImages().getMain()).into(holder.jzvdStd.thumbImageView);
        }

   /*    holder.jzvdStd.setUp(vo.getIframeURL()
                ,  vo.getTitle(), JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
*/

       // holder.jzvdStd.setUp(jzDataSource, Jzvd.SCREEN_NORMAL);
        Glide.with(holder.jzvdStd.getContext()).load(vo.getImages().getMain()).into(holder.jzvdStd.thumbImageView);


        try {
            holder.jzvdStd.releaseAllVideos();
            holder.jzvdStd.setUp(proxy.getProxyUrl(vo.getIframeURL())
                    ,vo.getTitle()
                    , JzvdStd2.SCREEN_NORMAL, JZMediaExo2.class);

        }catch (Exception ex){
            ex.printStackTrace();
        }



      /*  holder.jzvdStd.setUp(vo.getIframeURL()
                , vo.getTitle(), Jzvd.SCREEN_NORMAL, JZMediaExo.class);*/



        holder.jzvdStd.tvUserId.setOnClickListener(v -> listener.onItemClicked(Constant.Events.PROFILE, "", vo.getOwnerId()));
        holder.jzvdStd.ivComment.setOnClickListener(v -> listener.onItemClicked(Constant.Events.COMMENT, "", vo.getVideoId()));

        holder.jzvdStd.ivShare.setOnClickListener(v ->
                listener.onItemClicked(Constant.Events.SHARE_FEED2, "", holder.getAdapterPosition())
        );


        Log.e("profileimage",""+vo.getChannel_image());
        Glide.with(holder.jzvdStd.getContext()).load(vo.getUser_image()).into(holder.jzvdStd.ivUserImage);

        //like
        holder.jzvdStd.ivLike.setOnClickListener(v -> {
            if (vo.isContentLike()) {
                likeCountUn = vo.getLikeCount();
                if (vo.firstTime || vo.unlikeLoop) {
                    vo.fromUnlike = true;
                    vo.firstTime = false;
                    vo.unlikeLoop = true;
                    likeCountUn = vo.getLikeCount() - 1;
                }
                vo.setContentLike(false);
                holder.jzvdStd.tvLikeCount.setText("" + likeCountUn);
                holder.jzvdStd.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            } else {
                likeCountLike = vo.getLikeCount();
                if (vo.fromUnlike) {
                    likeCountLike = vo.getLikeCount();
                } else {
                    likeCountLike = vo.getLikeCount() + 1;
                }
                vo.firstTime = false;
                vo.fromUnlike = false;
                vo.setContentLike(true);
                holder.jzvdStd.tvLikeCount.setText("" + likeCountLike);
               // holder.jzvdStd.ivLike.setColorFilter(menuTitleActiveColor);
                holder.jzvdStd.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            listener.onItemClicked(Constant.Events.TICK_VIDEO_LIKE, "" + vo.getVideoId(), holder.getAdapterPosition());
        });

        if (vo.getSong() != null) {
           if(vo.getSong().getImages()!=null){
               Glide.with(holder.jzvdStd.getContext()).load(vo.getSong().getImages().getMain()).into(holder.jzvdStd.ivUser);
           }else {
               Glide.with(holder.jzvdStd.getContext()).load(R.drawable.default_song_img).into(holder.jzvdStd.ivUser);
           }
        } else {
            Glide.with(holder.jzvdStd.getContext()).load(R.drawable.default_song_img).into(holder.jzvdStd.ivUser);
           // holder.jzvdStd.ivUser.setVisibility(View.GONE);
        }


     //   holder.jzvdStd.ivUser.clearAnimation();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        JzvdStdClickClick jzvdStd;

        public MyViewHolder(View itemView) {
            super(itemView);
            jzvdStd = itemView.findViewById(R.id.videoplayer);
        }
    }

}