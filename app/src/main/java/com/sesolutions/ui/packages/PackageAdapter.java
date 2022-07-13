package com.sesolutions.ui.packages;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.Packages;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ContactHolder> {

    private final List<Packages> list;
    private final OnUserClickedListener<Integer, Object> listener;
    private final Context context;
    private final ThemeManager themeManager;
    private boolean isMyPackageScreen;
    private final int foreground;
    private final int text_color_light;
    private final int text_color_1;
    private final int cPrimary;
    private final int alphaGrey1;

    PackageAdapter(List<Packages> list, Context context, OnUserClickedListener<Integer, Object> listenr) {
        this.list = list;
        this.listener = listenr;
        this.context = context;
        foreground = Color.parseColor(Constant.backgroundColor);
        text_color_light = Color.parseColor(Constant.text_color_light);
        text_color_1 = Color.parseColor(Constant.text_color_1);
        cPrimary = Color.parseColor(Constant.colorPrimary);
        alphaGrey1 = Color.parseColor("#55111111");
        // alphaGrey1 = ContextCompat.getColor(context, R.color.alpha_grey_1);
        themeManager = new ThemeManager();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final Packages vo = list.get(position);

            holder.tvPackageTitle.setText(vo.getTitle());
            holder.tvDesc.setText(vo.getDescription());
            holder.tvMonth.setText(vo.getPaymentType());
            holder.tvPrice.setText(vo.getPrice_type());

            if (vo.hasSubscribed()) {
                holder.tvSubscribedOn.setText(vo.getMapValue("creation_date"));
                holder.tvNextPayment.setText(vo.getMapValue("expiration_date"));
                holder.tvSubscribedOn.setVisibility(View.VISIBLE);
                holder.tvNextPayment.setVisibility(View.VISIBLE);
            } else {
                holder.tvSubscribedOn.setVisibility(View.GONE);
                holder.tvNextPayment.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setMyPackage() {
        isMyPackageScreen = true;
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        protected TextView tvPackageTitle;
        protected TextView tvDesc;
        // protected TextView tvType;
        protected TextView tvMonth;
        protected TextView tvSubscribedOn;
        protected TextView tvNextPayment;
        protected TextView tvPrice;
        protected CardView cvCard;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                //themeManager.applyTheme((ViewGroup) itemView, context);
                tvPackageTitle = itemView.findViewById(R.id.tvPackageTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                // tvType = itemView.findViewById(R.id.tvType);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvMonth = itemView.findViewById(R.id.tvMonth);
                cvCard = itemView.findViewById(R.id.cvCard);
                tvSubscribedOn = itemView.findViewById(R.id.tvSubscribedOn);
                tvNextPayment = itemView.findViewById(R.id.tvNextPayment);

                cvCard.setCardBackgroundColor(isMyPackageScreen ? cPrimary : alphaGrey1);
                //  tvPackageTitle.setTextColor((isMyPackageScreen ? text_color_1 : text_color_light));
                //   tvDesc.setTextColor((isMyPackageScreen ? text_color_1 : text_color_light));
                tvMonth.setTextColor(text_color_1);
                tvPrice.setTextColor(cPrimary);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
