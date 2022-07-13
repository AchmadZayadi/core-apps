package com.sesolutions.ui.currency;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ContactHolder> {

    private final List<Options> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int text1;
    private final String packageName;


    public CurrencyAdapter(List<Options> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        text1 = Color.parseColor(Constant.text_color_1);
        packageName = context.getPackageName();
        //  foregroundColor = Color.parseColor(Constant.foregroundColor);
        // thememanager = new ThemeManager();
        // this.loadListener = loadListener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            // thememanager.applyTheme((ViewGroup) holder.itemView, context);
            final Options vo = list.get(position);
            // holder.tvFeedText.setText(text2);
            // holder.cvMain.setCardBackgroundColor(foregroundColor);
            holder.tvCb.setText(vo.getLabel()+" ("+vo.getName()+")");
            // holder.tvText.setTextColor(text1);
            holder.llCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.MENU_MAIN, vo.getName(), holder.getAdapterPosition());
                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvCb;
        protected View llCb;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                tvCb = itemView.findViewById(R.id.tvCb);
                llCb = itemView.findViewById(R.id.llCb);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
