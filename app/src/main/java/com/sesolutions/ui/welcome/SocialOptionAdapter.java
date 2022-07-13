package com.sesolutions.ui.welcome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class SocialOptionAdapter extends RecyclerView.Adapter<SocialOptionAdapter.ContactHolder> {

    private final List<SearchVo> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int text1;
    private final String packageName;


    public SocialOptionAdapter(List<SearchVo> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_image, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            // thememanager.applyTheme((ViewGroup) holder.itemView, context);
            final SearchVo vo = list.get(position);
            // holder.tvFeedText.setText(text2);
            // holder.cvMain.setCardBackgroundColor(foregroundColor);
            holder.tvText.setText(vo.getTitle());
            // holder.tvText.setTextColor(text1);
            holder.tvText.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            //holder.tvText.setTextColor(Color.parseColor(Constant.colorPrimary));
            //holder.cvMain.setCardBackgroundColor(Color.parseColor(Constant.colorPrimary));

            //  Glide.with(context).load(vo.getImage()).into(holder.ivFeedImage);

            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(Constant.Events.CLICKED_OPTION, "", holder.getAdapterPosition());
                }
            });

            int id = context.getResources().getIdentifier("social_" + vo.getName().replace("-", "_"), "drawable", packageName);
            holder.ivIcon.setImageResource(id);
            Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                    id);
            Palette.from(image).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(@NonNull Palette palette) {
                    holder.cvMain.setCardBackgroundColor(palette.getVibrantColor(0x000000));
                    holder.tvText.setTextColor(palette.getVibrantColor(0x000000));
                    /*Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    if (vibrantSwatch != null) {
                        holder.cvMain.setCardBackgroundColor(vibrantSwatch.get());
                        holder.tvText.setTextColor(vibrantSwatch.getRgb());
                        //bodyText.setTextColor(vibrantSwatch.getBodyTextColor());
                    }*/
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

        protected TextView tvText;
        protected ImageView ivIcon;
        protected CardView cvMain;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                // ButterKnife.bind(this, itemView);
                cvMain = itemView.findViewById(R.id.cvMain);
                tvText = itemView.findViewById(R.id.tvText);
                ivIcon = itemView.findViewById(R.id.ivIcon);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
