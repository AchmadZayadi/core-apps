package com.sesolutions.ui.store;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.comment.AttachmentComment;
import com.sesolutions.utils.CustomLog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductThumbAdapter extends RecyclerView.Adapter<ProductThumbAdapter.ContactHolder> {

    private final List<AttachmentComment> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    // private final int alphaWhite;
    //  private int lastPosition;


    public ProductThumbAdapter(List<AttachmentComment> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        // this.alphaWhite = ContextCompat.getColor(context, R.color.alpha_white);
        //  this.transparent = ContextCompat.getColor(context, R.color.transparent_black);
        // this.loadListener = loadListener;
    }

    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_thumbnail, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

        try {
            /*final AttachmentComment vo = list.get(position);
            Util.showImageWithGlide(holder.ivAttachImage, (vo.getImages().getMain()), context, R.drawable.placeholder_menu);
            holder.ivVideoForground.setVisibility(vo.getType().equalsIgnoreCase(Constant.ACTIVITY_TYPE_ALBUM) ? View.GONE : View.VISIBLE);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.ITEM_COMMENT, vo.getType(), vo.getId());
                }
            });*/

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return 5;
        //return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected ImageView ivAttachImage;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                ivAttachImage = itemView.findViewById(R.id.ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
