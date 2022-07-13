package com.sesolutions.ui.settings;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Networks;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.ContactHolder> {

    private final List<Networks> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final int colorPrimary;
    private final ThemeManager themeManager;

    public void setMine(boolean mine) {
        isMine = mine;
    }

    private boolean isMine;

   /* private final int colorPrimary;
    private final Typeface iconFont;
    private final int colorGrey;*/


    public NetworkAdapter(List<Networks> list, Context cntxt, OnUserClickedListener<Integer, String> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        this.colorPrimary = Color.parseColor(Constant.colorPrimary);
        themeManager = new ThemeManager();
        //   this.colorGrey = ContextCompat.getColor(context, R.color.grey_feed);
        //  iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network_setting, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final Networks vo = list.get(position);
            // holder.tvHeader.setTextColor(colorPrimary);
            holder.tvHeader.setText(vo.getTitle());

            holder.tvHeader22.setText(""+vo.getMemberCount());
            // holder.bJoin.setBackgroundColor(colorPrimary);
            holder.bJoin.setText(isMine ? Constant.TXT_REMOVE : Constant.TXT_JOIN);
            holder.bJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(vo.getNetworkId(), "" + vo.getNetworkId(), holder.getAdapterPosition());
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

        protected TextView tvHeader;
        protected AppCompatButton bJoin;
        protected TextView tvHeader22;

        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // cvMain = itemView.findViewById(R.id.cvMain);
                tvHeader = itemView.findViewById(R.id.tvHeader);
                bJoin = itemView.findViewById(R.id.bJoin);
                tvHeader22=itemView.findViewById(R.id.tvHeader22);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
