package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.text.Html;
import android.text.InputType;
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

public class FormElementTextNumberViewHolder extends BaseViewHolder {

   // private final View ivAsteric;
  //  public AppCompatTextView mTextViewTitle;
    public AppCompatEditText mEditTextValue;
    public FormItemEditTextListener mFormCustomEditTextListener;
    public TextInputLayout formelementTextinput;
  //  private LinearLayout llMainForm;

    public FormElementTextNumberViewHolder(View v, FormItemEditTextListener listener) {
        super(v);
     //   mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        formelementTextinput = (TextInputLayout) v.findViewById(R.id.formelementTextinput);
        mFormCustomEditTextListener = listener;
        mEditTextValue.addTextChangedListener(mFormCustomEditTextListener);
        mEditTextValue.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
     //   ivAsteric = v.findViewById(R.id.ivAsteric);
     //   llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
    }

    @Override
    public FormItemEditTextListener getListener() {
        return mFormCustomEditTextListener;
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
       // mTextViewTitle.setText(formElement.getTitle());
        mEditTextValue.setText(formElement.getValue());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;

        Log.e("HiddenNumber",""+formElement.isHidden());
     //   llMainForm.setLayoutParams(params);
    //    ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
       // mEditTextValue.setHint(formElement.getHint());
        formelementTextinput.setHintAnimationEnabled(false);

        if(formElement.isHidden()){
            formelementTextinput.setVisibility(View.GONE);
        }else {
            formelementTextinput.setVisibility(View.VISIBLE);
        }

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
