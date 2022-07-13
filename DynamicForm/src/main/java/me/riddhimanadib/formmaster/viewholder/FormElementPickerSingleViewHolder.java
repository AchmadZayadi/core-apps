package me.riddhimanadib.formmaster.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.adapter.CountryCodeAdapter;
import me.riddhimanadib.formmaster.adapter.CountryCodeAdapter2;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementPickerSingleViewHolder extends BaseViewHolder {

    private final View ivAsteric;
    private AppCompatTextView mTextViewTitle;
    private AppCompatEditText mEditTextValue;
    private ReloadListener mReloadListener;
    private View llSelect;
//    private View ivDate;
    Context Mcontext;
    private BaseFormElement mFormElement;
    private FormElementPickerSingle mFormElementPickerSingle;
    private LinearLayout llMainForm;
    private int mPosition;
    private ArrayList<String> countryList, unicodeList, flagList;
    private int selectedPosition = -1;


    public FormElementPickerSingleViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        llSelect = v.findViewById(R.id.llSelect);
     //   ivDate = v.findViewById(R.id.ivDate);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mReloadListener = reloadListener;
        ivAsteric = v.findViewById(R.id.ivAsteric);
        Mcontext=context;


        countryList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.countryCodes)));
        unicodeList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.countryUniCodes)));
        flagList = new ArrayList<>();
        for (int i = 0; i < unicodeList.size(); i++) {
            int flagOffset = 0x1F1E6;
            int asciiOffset = 0x41;
            String country = unicodeList.get(i);
            int firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset;
            int secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset;
            String flag = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
            flagList.add(flag);
        }

    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        mFormElementPickerSingle = (FormElementPickerSingle) mFormElement;

        mTextViewTitle.setText(formElement.getTitle());
        ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
        mEditTextValue.setText(formElement.getValue());
        mEditTextValue.setHint(formElement.getHint());
        mEditTextValue.setFocusableInTouchMode(false);

        //hiding item view if hidden
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = mFormElementPickerSingle.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        //ivDate.setVisibility(View.GONE);
        try {
            if((mFormElementPickerSingle.getName().contains("subcat_id") || mFormElementPickerSingle.getName().contains("subsubcat_id"))){
                boolean bol =mFormElementPickerSingle.getValue().equalsIgnoreCase("");
                params.height = bol ? 0 : params.height;
                llMainForm.setLayoutParams(params);
            }
          }catch (Exception ex){
            ex.printStackTrace();
          }
        // reformat the options in format needed
        final CharSequence[] options = new CharSequence[mFormElementPickerSingle.getOptions().size()];

        try {
            if(formElement.getName().equalsIgnoreCase("category_id")){
                for (int i = 0; i < mFormElementPickerSingle.getOptions().size(); i++) {
                    if(!mFormElementPickerSingle.getOptions().get(i).contains("Choose Category")){
                        options[i] = mFormElementPickerSingle.getOptions().get(i);
                    }else {
                        options[i] ="";
                    }
                }
            }else {
                for (int i = 0; i < mFormElementPickerSingle.getOptions().size(); i++) {
                    options[i] = mFormElementPickerSingle.getOptions().get(i);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


      if(formElement.getTitle().equalsIgnoreCase("Country Code")){
          countryList.clear();
          countryList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.countryCodes)));
            llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view,0);
                }
            });
            mEditTextValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view,0);
                }
            });
        }
      else if(formElement.getName().equalsIgnoreCase("1_1_18")){

            countryList.clear();
            countryList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.countryname)));

            llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view,1);
                }
            });
            mEditTextValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view,1);
                }
            });
        }
      else {
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(mFormElementPickerSingle.getPickerTitle())
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mEditTextValue.setText(options[which]);
                            mFormElementPickerSingle.setValue(options[which].toString());
                            mReloadListener.updateValue(position, options[which].toString());
                        }
                    })
                    .create();
            llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                      dialog.show();
                }
            });
            mEditTextValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     dialog.show();
                }
            });
        }


      /* */



    }

    private PopupWindow popupWindowObj;
    private void showPopup(View view,int flag) {
        popupWindowObj = popupDisplayCategory(flag);
        popupWindowObj.showAtLocation(view, 0, 0, Gravity.CENTER);
    }

    CountryCodeAdapter2 adapter2;
    CountryCodeAdapter adapter1;

    private PopupWindow popupDisplayCategory(int flagdat) {
        final PopupWindow popupWindow = new PopupWindow(Mcontext);

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.country_code_selected_view, null);

        EditText edittextid123= (EditText)view.findViewById(R.id.edittextid123);
        ImageView backarrow= (ImageView)view.findViewById(R.id.backarrow);

        RecyclerView recyclerView = view.findViewById(R.id.rvCountryCode);
        if(flagdat==1){
            adapter2 = new CountryCodeAdapter2(flagList, countryList, (v, position) -> {

                Log.e("pos",""+position);
                mEditTextValue.setText(""+position);
                mFormElementPickerSingle.setValue(""+position);
                //   mReloadListener.updateValue(position, ""+code);
                // binding.textView2.setText(flagList.get(position).concat(" ").concat(code));
                popupWindowObj.dismiss();
            });

        }else {
            adapter1 = new CountryCodeAdapter(flagList, countryList, (v, position) -> {

                Log.e("pos",""+position);
                mEditTextValue.setText(""+position);
                mFormElementPickerSingle.setValue(""+position);
                //   mReloadListener.updateValue(position, ""+code);
                // binding.textView2.setText(flagList.get(position).concat(" ").concat(code));
                popupWindowObj.dismiss();
            });

        }

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowObj.dismiss();
            }
        });

    edittextid123.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && s.toString().length()>0){
                    try {
                        ArrayList<String> filterdNames = new ArrayList<>();
                        ArrayList<String> flaglistdata = new ArrayList<>();
                        for(int j=0;j<countryList.size();j++){
                            String st_data1=countryList.get(j).toLowerCase();
                            String st_data2=s.toString().toLowerCase();
                            if(st_data1.contains(st_data2)){
                                filterdNames.add(countryList.get(j));
                                flaglistdata.add(flagList.get(j));
                            }
                        }
                        if(flagdat==1){
                            adapter2.setfilter(filterdNames,flaglistdata);
                        }else {
                            adapter1.setfilter(filterdNames,flaglistdata);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Mcontext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(flagdat==1){
            recyclerView.setAdapter(adapter2);
        }else {
            recyclerView.setAdapter(adapter1);
        }


        if (selectedPosition != -1)
            recyclerView.scrollToPosition(selectedPosition);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(Mcontext.getResources().getDrawable(R.drawable.transparent_background));

        return popupWindow;
    }

    private void filter(String text) {

    }

}
