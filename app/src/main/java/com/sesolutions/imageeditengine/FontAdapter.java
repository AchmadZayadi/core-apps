package com.sesolutions.imageeditengine;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidninja.imageeditengine.Constants;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;


public class FontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final List<Font> list;
    private List<Font> contactListFiltered;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private boolean isGrid = false;


    public FontAdapter(List<Font> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.contactListFiltered = new ArrayList<>(list);
        this.context = cntxt;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!isGrid && viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_more, parent, false);
            return new MoreHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_font_big : R.layout.item_font, parent, false);
            return new ContactHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder1, int position) {

        try {
            if (!isGrid && holder1.getItemViewType() == 0) {
                //DO nothing
            } else {
                ContactHolder holder = ((ContactHolder) holder1);
                final Font vo = contactListFiltered.get(position);
                holder.tvFontName.setText(vo.getFontFamily());


                final FontRequest request = new FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        vo.getQueryString(),
                        R.array.com_google_android_gms_fonts_certs);


                final FontsContractCompat.FontRequestCallback callback = new FontsContractCompat
                        .FontRequestCallback() {
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        holder.tvFont.setTypeface(typeface);  // Font has been downloaded successfully
                        //switchFontAlertTextview.setVisibility(View.INVISIBLE);
                        // progressBar.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onTypefaceRequestFailed(int reason) {
                        CustomLog.e("font", "failed at position " + position + " because of reason " + reason);
                    }
                };

                FontsContractCompat
                        .requestFont(context, request, callback,
                                getHandlerThreadHandler());
                //Util.showImageWithGlide(((ContactHolder) holder).ivBg, isGrid ? vo.getRegular() : vo.getSmall(), context, R.drawable.placeholder_3_2);
                holder.itemView.setOnClickListener(v -> listener.onItemClicked(Constants.Events.FONT, vo.getFontFamily(), holder.getAdapterPosition()));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private Handler mHandler = null;

    private Handler getHandlerThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }


    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public void setGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

    public List<Font> getList() {
        return contactListFiltered;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView tvFontName, tvFont;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                tvFontName = itemView.findViewById(R.id.tvFontName);
                // ivPlay = itemView.findViewById(R.id.ivPlay);
                tvFont = itemView.findViewById(R.id.tvFont);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered.clear();
                    contactListFiltered.addAll(list);
                } else {
                    List<Font> filteredList = new ArrayList<>();
                    for (Font row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFontFamily().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered.clear();
                    contactListFiltered.addAll(filteredList);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (List<Font>) filterResults.values;
                notifyDataSetChanged();
                if (null != contactListFiltered && contactListFiltered.size() > 0) {

                } else {
                    // listener.onItemClicked(Constant.Events.UPDATE_TOTAL, charSequence, -1);
                }
                listener.onItemClicked(Constant.Events.UPDATE_TOTAL, charSequence, contactListFiltered.size());
            }
        };
    }


    public class MoreHolder extends RecyclerView.ViewHolder {
        View cvBg;
        ImageView ivBg;


        MoreHolder(View itemView) {
            super(itemView);
            try {
                cvBg = itemView.findViewById(R.id.cvBg);
                // ivPlay = itemView.findViewById(R.id.ivPlay);
                ivBg = itemView.findViewById(R.id.ivBg);
                cvBg.setOnClickListener(v -> listener.onItemClicked(Constants.Events.FONT, null, -1));
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
