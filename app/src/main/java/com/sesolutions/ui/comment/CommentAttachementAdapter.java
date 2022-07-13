package com.sesolutions.ui.comment;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.comment.AttachmentComment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class CommentAttachementAdapter extends RecyclerView.Adapter<CommentAttachementAdapter.ContactHolder> {

    private final List<AttachmentComment> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int parentPostion;
    // private final int alphaWhite;
    //  private int lastPosition;


    public CommentAttachementAdapter(List<AttachmentComment> list, Context cntxt, OnUserClickedListener<Integer, Object> listener, int parentPostion) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.parentPostion = parentPostion;
        // this.alphaWhite = ContextCompat.getColor(context, R.color.alpha_white);
        //  this.transparent = ContextCompat.getColor(context, R.color.transparent_black);
        // this.loadListener = loadListener;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment_comment, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

        try {
            final AttachmentComment vo = list.get(position);
            Util.showImageWithGlide(holder.ivAttachImage, (vo.getImages().getMain()), context, R.drawable.placeholder_menu);
            holder.ivVideoForground.setVisibility(vo.getType().equalsIgnoreCase(Constant.ACTIVITY_TYPE_ALBUM) ? View.GONE : View.VISIBLE);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(vo.getType().equalsIgnoreCase("video")){
                        listener.onItemClicked(Constant.Events.ITEM_COMMENT_VIDEO, vo.getImages().getNormal(), vo.getId());

                    }else {
                        listener.onItemClicked(Constant.Events.ITEM_COMMENT, vo.getImages().getNormal(), vo.getId());

                    }


                }
            });

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

        protected ImageView ivAttachImage;
        protected ImageView ivVideoForground;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                cvMain = itemView.findViewById(R.id.cvMain);
                ivAttachImage = itemView.findViewById(R.id.ivAttachImage);
                ivVideoForground = itemView.findViewById(R.id.ivVideoForground);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
