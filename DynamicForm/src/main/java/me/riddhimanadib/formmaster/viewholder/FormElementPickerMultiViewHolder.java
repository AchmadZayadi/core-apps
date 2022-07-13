package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.crowdfire.cfalertdialog.CFAlertDialog;

import java.util.ArrayList;
import java.util.List;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementPickerMultiViewHolder extends BaseViewHolder  {

    private final View ivAsteric;
    private final LinearLayout llMainForm;
    private AppCompatTextView mTextViewTitle;
    private AppCompatEditText mEditTextValue;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private View llSelect;
 //   private View ivDate;
    private FormElementPickerMulti mFormElementPickerMulti;
    CFAlertDialog.Builder builder;
    private int mPosition;

    public FormElementPickerMultiViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        llSelect = v.findViewById(R.id.llSelect);
  //      ivDate = v.findViewById(R.id.ivDate);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mReloadListener = reloadListener;
        ivAsteric = v.findViewById(R.id.ivAsteric);
    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        mFormElementPickerMulti = (FormElementPickerMulti) mFormElement;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        mTextViewTitle.setText(formElement.getTitle());
        mEditTextValue.setText(formElement.getValue());
        ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
        mEditTextValue.setHint(formElement.getHint());
        mEditTextValue.setFocusableInTouchMode(false);

        // reformat the options in format needed
        final CharSequence[] options = new CharSequence[mFormElementPickerMulti.getOptions().size()];
        final String[] options2 = new String[mFormElementPickerMulti.getOptions().size()];
        final boolean[] optionsSelected = new boolean[mFormElementPickerMulti.getOptions().size()];
        final ArrayList<Integer> mSelectedItems = new ArrayList();
      //  ivDate.setVisibility(View.GONE);
        for (int i = 0; i < mFormElementPickerMulti.getOptions().size(); i++) {
            options[i] = mFormElementPickerMulti.getOptions().get(i);
            options2[i] = mFormElementPickerMulti.getOptions().get(i);
            optionsSelected[i] = false;

            if (mFormElementPickerMulti.getOptionsSelected().contains(options[i])) {
                optionsSelected[i] = true;
                mSelectedItems.add(i);
            }
        }

        builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        LayoutInflater inflater = LayoutInflater.from(context);
        View vskjs = inflater.inflate(R.layout.dialog_page_like2, null, false);


       // SampleFooterView view=new SampleFooterView(context,this);
        builder.setFooterView(vskjs);
        String title=mFormElementPickerMulti.getPickerTitle();

        if(mFormElementPickerMulti.getPickerTitle()!=null){
            builder.setTitle(title);
            Log.e("title",""+title);
        }

         Button canel=vskjs.findViewById(R.id.canselid);
         Button postive=vskjs.findViewById(R.id.okbutton);
       //  postive.setText(""+mFormElementPickerMulti.getPositiveText());
       //  canel.setText(""+mFormElementPickerMulti.getNegativeText());
         postive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> selectedOptions = new ArrayList<>();
                String s = "";
                for (int i = 0; i < mSelectedItems.size(); i++) {
                    selectedOptions.add(options[mSelectedItems.get(i)].toString());
                    s += options[mSelectedItems.get(i)];

                    if (i < mSelectedItems.size() - 1) {
                        s += ", ";
                    }
                }
                mFormElementPickerMulti.setOptionsSelected(selectedOptions);
                mEditTextValue.setText(s);
                mReloadListener.updateValue(position, s);
                builder.dismissi();
            }
        });

        canel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.dismissi();
            }
        });

       // builder.setTitle(mFormElementPickerMulti.getPickerTitle());
        builder.setMultiChoiceItems(options2, optionsSelected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
            //    Toast.makeText(context, "Row:"+index+" "+(b? "Selected":"Unselected"), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    mSelectedItems.add(which);
                } else if (mSelectedItems.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    mSelectedItems.remove(Integer.valueOf(which));
                }
            }
        });
      /*  builder.addButton(mFormElementPickerMulti.getPositiveText(), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int flagd) {


                dialogInterface.dismiss();
            }
        });*/
    /*    builder.addButton(mFormElementPickerMulti.getPositiveText(), Color.parseColor("#FFFFFF"), Color.parseColor("#014493"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             //   Toast.makeText(context, "Upgrade tapped", Toast.LENGTH_SHORT).show();
                List<String> selectedOptions = new ArrayList<>();
                String s = "";
                for (int i = 0; i < mSelectedItems.size(); i++) {
                    selectedOptions.add(options[mSelectedItems.get(i)].toString());
                    s += options[mSelectedItems.get(i)];

                    if (i < mSelectedItems.size() - 1) {
                        s += ", ";
                    }
                }
                mFormElementPickerMulti.setOptionsSelected(selectedOptions);
                mEditTextValue.setText(s);
                mReloadListener.updateValue(position, s);
                dialog.dismiss();
            }
        });
        builder.addButton(mFormElementPickerMulti.getNegativeText(), Color.parseColor("#FFFFFF"), Color.parseColor("#737171"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             //   Toast.makeText(context, "Upgrade tapped", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    */

        // prepare the dialog
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(mFormElementPickerMulti.getPickerTitle())
                .setMultiChoiceItems(options, optionsSelected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(mFormElementPickerMulti.getPositiveText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> selectedOptions = new ArrayList<>();
                        String s = "";
                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            selectedOptions.add(options[mSelectedItems.get(i)].toString());
                            s += options[mSelectedItems.get(i)];

                            if (i < mSelectedItems.size() - 1) {
                                s += ", ";
                            }
                        }
                        mFormElementPickerMulti.setOptionsSelected(selectedOptions);
                        mEditTextValue.setText(s);
                        mReloadListener.updateValue(position, s);
                    }
                })
                .setNegativeButton(mFormElementPickerMulti.getNegativeText(), null)
                .create();

        llSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                //dialog.show();
            }
        });
        mEditTextValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                //dialog.show();
            }
        });
    }

}
