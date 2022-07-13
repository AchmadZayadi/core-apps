package com.sesolutions.ui.courses.cart;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Courses.course.CheckoutResponse2;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CourseCartAdapter extends RecyclerView.Adapter<CourseCartAdapter.ContactHolder> {

    private final List<CheckoutResponse2.Result.CartData.ProductData> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    // private final int alphaWhite;
    //  private int lastPosition;


    public CourseCartAdapter(List<CheckoutResponse2.Result.CartData.ProductData> list, Context cntxt, OnUserClickedListener<Integer, Object> listener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        // this.alphaWhite = ContextCompat.getColor(context, R.color.alpha_white);
        //  this.transparent = ContextCompat.getColor(context, R.color.transparent_black);+
        // this.loadListener = loadListener;
    }

    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_course, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

        try {
            final CheckoutResponse2.Result.CartData.ProductData vo = list.get(position);
            Util.showImageWithGlide(holder.ivImage, (vo.getCourseImages().getMain()), context, R.drawable.placeholder_menu);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvprice.setText(vo.getPrice());
            holder.tvprice2.setText(vo.getPrice());
            holder.tvTax2.setText(""+ vo.getTaxes().getTotal_tax());

//            holder.tvQty.setText(""+vo.getQuantity());
            holder.mcvRemove.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MEMBER_REMOVE, "", vo.getButtons().get(0).getId()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, "", vo.getProductId()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
//        return 10;
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected ImageView ivImage;
        protected TextView tvTitle;
        protected View cvMain;
        protected TextView tvprice,tvprice2, tvQty, tvTax2;
        protected ImageView mcvRemove;


        public ContactHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            cvMain = itemView.findViewById(R.id.cvMain);
            ivImage = itemView.findViewById(R.id.ivImage2);
            tvprice = itemView.findViewById(R.id.tv_price);
            tvprice2 = itemView.findViewById(R.id.tvPrice);
            tvTax2 = itemView.findViewById(R.id.tvTax2);
//            tvQty = itemView.findViewById(R.id.tvQty);
            mcvRemove = itemView.findViewById(R.id.delete);


        }
    }
}
