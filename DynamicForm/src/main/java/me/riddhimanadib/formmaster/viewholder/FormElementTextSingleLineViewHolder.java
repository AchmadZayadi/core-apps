package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.FormItemEditTextListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementTextSingleLineViewHolder extends BaseViewHolder {

    private final LinearLayout llMainForm;
//    public AppCompatTextView mTextViewTitle;
    public AppCompatEditText mEditTextValue;
 //   private final View ivAsteric;
    public FormItemEditTextListener mFormCustomEditTextListener;
    public TextInputLayout formelementTextinput;


    public FormElementTextSingleLineViewHolder(View v, FormItemEditTextListener listener) {
        super(v);
   //     mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        formelementTextinput = (TextInputLayout) v.findViewById(R.id.formelementTextinput);
        mFormCustomEditTextListener = listener;
         llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mEditTextValue.addTextChangedListener(mFormCustomEditTextListener);
        mEditTextValue.setMaxLines(1);
       // ivAsteric = v.findViewById(R.id.ivAsteric);
    }

    @Override
    public FormItemEditTextListener getListener() {
        return mFormCustomEditTextListener;
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
        //hiding item view if hidden
       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);

        if(formElement.isHidden() ){
            llMainForm.setVisibility(View.GONE);
        }else {
            llMainForm.setVisibility(View.VISIBLE);
        }
      //  mTextViewTitle.setText(formElement.getTitle());
      //  ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
        Log.e("value__", "" + formElement.getValue());
         mEditTextValue.setText(formElement.getValue());
    //    mEditTextValue.setHint(formElement.getHint());
        formelementTextinput.setHintAnimationEnabled(false);
        if(formElement.isRequired()){
            String text = "<font color=#4c4c4c>"+formElement.getTitle()+"</font> <font color=#D32F2F>"+"*</font>";
           // formelementTextinput.setHint(Html.fromHtml(text));
            mEditTextValue.setHint(Html.fromHtml(text));
        }else {
            mEditTextValue.setHint(Html.fromHtml(formElement.getTitle()));
        }
        formelementTextinput.setHintAnimationEnabled(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextValue.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditTextValue, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
