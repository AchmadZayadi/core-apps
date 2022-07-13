package com.sesolutions.ui.postfeed;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.dashboard.composervo.PrivacyOptions;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;

import java.util.List;


public class FeedPrivacyAdapter extends RecyclerView.Adapter<FeedPrivacyAdapter.ContactHolder> {

    private final List<PrivacyOptions> list;
    private final int foregroundColor;
    private String selectedPosition = "";
    private final OnUserClickedListener<Integer, Object> listener;
    // private final Typeface iconFont;
    private final int text1;


    FeedPrivacyAdapter(List<PrivacyOptions> list, Context context, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        //Context context = cntxt;
        this.listener = listener;
        this.text1 = SesColorUtils.getText1Color(context);
        this.foregroundColor = SesColorUtils.getForegroundColor(context);
        // iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        // this.loadListener = loadListener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_privacy, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {

            final PrivacyOptions vo = list.get(position);
            holder.tvOption.setTextColor(text1);
            holder.tvOption.setText(vo.getValue());
            holder.cbOption.setChecked(vo.getName().equals(selectedPosition));
            holder.cvMain.setCardBackgroundColor(foregroundColor);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(0, "", holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    void setSelectedPosition(String selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        CheckBox cbOption;
        TextView tvOption;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                cbOption = itemView.findViewById(R.id.cbOption);
                tvOption = itemView.findViewById(R.id.tvOption);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
