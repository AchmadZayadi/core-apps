package com.sesolutions.ui.qna;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class AddOptionAdapter extends RecyclerView.Adapter<AddOptionAdapter.ContactHolder> {

    private final List<String> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int text_color_1;
    private final int foregroundColor;
    private int lastPosition = 0;



    public AddOptionAdapter(List<String> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.text_color_1 = Color.parseColor(Constant.text_color_1);
        this.foregroundColor = Color.parseColor(Constant.foregroundColor.replace("#", "#99"));
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_question_item, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {

        try {

            ((AppCompatEditText) holder.itemView.findViewById(R.id.etBody)).setText(list.get(position));
            /*final Attribution vo = list.get(position);
            holder.tvFeedText.setTextColor(text_color_1);
            holder.tvFeedText.setText(vo.getTitle());
            holder.cvMain.setCardBackgroundColor(foregroundColor);

            Util.showImageWithGlide(holder.ivFeedImage, vo.getPhoto(), context, R.drawable.placeholder_3_2);

            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.ATTRIBUTION_OPTION_CLICK, "" + lastPosition, holder.getAdapterPosition()));*/

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvFeedText;
        ImageView ivFeedImage;
        // protected View ivForeground;
        CardView cvMain;

        ContactHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.ivRemove).setOnClickListener(view1 -> {
                listener.onItemClicked(Constant.Events.DELETE,null,getAdapterPosition());

            });
            try {
               /* tvFeedText = itemView.findViewById(R.id.tvFeedText);
                ivFeedImage = itemView.findViewById(R.id.ivIcon);
                cvMain = itemView.findViewById(R.id.cvMain);*/
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
