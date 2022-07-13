package me.riddhimanadib.formmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.riddhimanadib.formmaster.R;


public class CountryCodeAdapter2 extends RecyclerView.Adapter<CountryCodeAdapter2.ContactHolder> {


    private ArrayList<String> countryFlagList;
    private ArrayList<String> countryCodeList;
    private CountryCodeAdapterClickListener onClickListener;

    public interface CountryCodeAdapterClickListener {
        void onPositionClick(View v, String position);
    }



    @Override
    public void onViewAttachedToWindow(ContactHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    public CountryCodeAdapter2(ArrayList<String> countryFlagList, ArrayList<String> countryCodeList, CountryCodeAdapterClickListener onClickListener) {
        this.countryFlagList = countryFlagList;
        this.countryCodeList = countryCodeList;
        this.onClickListener = onClickListener;
    }

    public void setfilter(ArrayList<String> countryCodeList,ArrayList<String> countryFlagList2){
        this.countryCodeList = countryCodeList;
        this.countryFlagList = countryFlagList2;
        notifyDataSetChanged();
    }
    



    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_code_dropdown_item, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int i) {

        String countryCode = countryCodeList.get(i);
        String countryFlag = countryFlagList.get(i);

        holder.tvCountryFlags.setText(countryFlag);
        holder.tvCountryCode.setText(countryCode);


        holder.mainlayout.setOnClickListener(v ->
                {
                    try {
                        String code = countryCodeList.get(i);
                        onClickListener.onPositionClick(v, code);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
        );

        holder.tvCountryCode.setOnClickListener(v ->
        {
            try {
                String code = countryCodeList.get(i);
                onClickListener.onPositionClick(v, code);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        );

    }


    @Override
    public int getItemCount() {
        return countryCodeList.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        protected View llReactionOption;
        public TextView tvCountryFlags,tvCountryCode;
        LinearLayout mainlayout;


        public ContactHolder(View itemView) {
            super(itemView);
            try {

                tvCountryFlags = itemView.findViewById(R.id.tvCountryFlags);
                tvCountryCode = itemView.findViewById(R.id.tvCountryCode);
                mainlayout = itemView.findViewById(R.id.mainlayout);

            } catch (Exception e) {
               // Log.e("",""+e.printStackTrace(););
            }
        }
    }
}
