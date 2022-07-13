package com.sesolutions.ui.settings;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Settings;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ContactHolder> {

    private final List<Settings> list;
    private final Context context;
    private final OnUserClickedListener<Integer, String> listener;
    private final ThemeManager themeManager;

   /* private final int colorPrimary;
    private final Typeface iconFont;
    private final int colorGrey;*/


    public SettingAdapter(List<Settings> list, Context cntxt, OnUserClickedListener<Integer, String> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        //   this.textColor1 = Color.parseColor(Constant.text_color_1);
        themeManager = new ThemeManager();
        //   this.colorGrey = ContextCompat.getColor(context, R.color.grey_feed);
        //  iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);

            final Settings vo = list.get(position);
            // holder.tvHeader.setTextColor(textColor1);
            holder.tvHeader.setText(vo.getLabel());

            if(vo.getLabel().equalsIgnoreCase("General")){
                holder.iconId.setImageResource(R.drawable.setting);
            }else if(vo.getLabel().equalsIgnoreCase("Privacy")){
                holder.iconId.setImageResource(R.drawable.setting_padlock);
            }else if(vo.getLabel().equalsIgnoreCase("Networks")){
                holder.iconId.setImageResource(R.drawable.settings_wifi);
            }else if(vo.getLabel().equalsIgnoreCase("Notifications")){
                holder.iconId.setImageResource(R.drawable.setting_bell);
            }else if(vo.getLabel().equalsIgnoreCase("Change Password")){
                holder.iconId.setImageResource(R.drawable.setting_view);
            }else if(vo.getLabel().equalsIgnoreCase("Phone Number")){
                holder.iconId.setImageResource(R.drawable.page_phone);
            }else if(vo.getLabel().equalsIgnoreCase("Delete Account")){
                holder.iconId.setImageResource(R.drawable.setting_delete);
            }
            holder.iconId.setColorFilter(ContextCompat.getColor(context,
                    R.color.bulecolor));

            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(-1, vo.getClazz(), holder.getAdapterPosition());
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
        protected ImageView iconId;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                iconId = itemView.findViewById(R.id.icon_id);
                tvHeader = itemView.findViewById(R.id.tvHeader);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
