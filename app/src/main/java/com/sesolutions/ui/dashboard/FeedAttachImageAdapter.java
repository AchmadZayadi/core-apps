package com.sesolutions.ui.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class FeedAttachImageAdapter extends RecyclerView.Adapter<FeedAttachImageAdapter.ContactHolder> {

    private final List<String> list;
    // --Commented out by Inspection (23-08-2018 20:55):private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    Context mcontext;
    // private final int alphaWhite;
    //  private int lastPosition;


    public FeedAttachImageAdapter(List<String> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.mcontext = cntxt;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attach_image, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {
            // BitmapFactory.Options options = new BitmapFactory.Options();
           /* options.inSampleSize = 6;
            Bitmap bitmapFile = BitmapFactory.decodeFile(list.get(position), options);
            holder.ivAttachImage.setImageBitmap(bitmapFile);*/
            // holder.ivAttachImage.setImageDrawable(Drawable.createFromPath(list.get(position)));

            //   Util.showImageWithGlide(holder.ivAttachImage, Drawable.createFromPath(list.get(position));
            Glide.with(mcontext).load(Drawable.createFromPath(list.get(position))).into(holder.ivAttachImage);

            holder.ivAttachImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.ivAttachCancel.setOnClickListener(v -> listener.onItemClicked(Constant.Events.FEED_ATTACH_IMAGE_CANCEL, "", holder.getAdapterPosition()));
            holder.ivAttachEdit.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CONTENT_EDIT, null, holder.getAdapterPosition()));

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

        ImageView ivAttachImage, ivAttachEdit;
        ImageView ivAttachCancel;
        // protected LinearLayoutCompat llMain;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                //     cvMain = itemView.findViewById(R.id.llMain);
                ivAttachImage = itemView.findViewById(R.id.ivAttachImage);
                ivAttachEdit = itemView.findViewById(R.id.ivAttachEdit);
                ivAttachCancel = itemView.findViewById(R.id.ivAttachCancel);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
