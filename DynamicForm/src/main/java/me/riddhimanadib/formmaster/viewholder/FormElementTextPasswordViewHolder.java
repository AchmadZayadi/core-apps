package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.FormItemEditTextListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementTextPasswordViewHolder extends BaseViewHolder {

  //  private final View ivAsteric;
    private final Drawable dHide;
    private final Drawable dShow;
   // public AppCompatTextView mTextViewTitle;
    public AppCompatEditText mEditTextValue;
    public TextInputLayout formelementTextinput;
//    public AppCompatImageView ivShowHide;
    public FormItemEditTextListener mFormCustomEditTextListener;

    public FormElementTextPasswordViewHolder(View v, FormItemEditTextListener listener) {
        super(v);
  //      mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        formelementTextinput = (TextInputLayout) v.findViewById(R.id.formelementTextinput);
   //     ivShowHide = (AppCompatImageView) v.findViewById(R.id.ivShowHide);
        mFormCustomEditTextListener = listener;
        dHide = v.getContext().getResources().getDrawable(R.drawable.ses_password_hide);
        dShow = v.getContext().getResources().getDrawable(R.drawable.ses_password_show);
        mEditTextValue.addTextChangedListener(mFormCustomEditTextListener);
        mEditTextValue.setTransformationMethod(new PasswordTransformationMethod());
     //   ivAsteric = v.findViewById(R.id.ivAsteric);

        //mEditTextValue.setRawInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    public FormItemEditTextListener getListener() {
        return mFormCustomEditTextListener;
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
    //    mTextViewTitle.setText(formElement.getTitle());
        mEditTextValue.setText(formElement.getValue());
    //    ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
    //    mEditTextValue.setHint(formElement.getHint());
        formelementTextinput.setHintAnimationEnabled(false);
      //  formelementTextinput.setHint(formElement.getTitle());
        formelementTextinput.setHintAnimationEnabled(true);
        formelementTextinput.setPasswordVisibilityToggleEnabled(true);
        formelementTextinput.setPasswordVisibilityToggleDrawable(R.drawable.password_toggle_drawable);
        if(formElement.isRequired()){
            String text = "<font color=#4c4c4c>"+formElement.getTitle()+"</font> <font color=#D32F2F>"+"*</font>";
            // formelementTextinput.setHint(Html.fromHtml(text));
            mEditTextValue.setHint(Html.fromHtml(text));
        }else {
            mEditTextValue.setHint(Html.fromHtml(formElement.getTitle()));
        }

   //     ivShowHide.setVisibility(View.VISIBLE);
      /*  ivShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTextValue.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    mEditTextValue.setTransformationMethod(null);
                    ivShowHide.setImageDrawable(dHide);
                } else {
                    mEditTextValue.setTransformationMethod(new PasswordTransformationMethod());
                    ivShowHide.setImageDrawable(dShow);
                }
                mEditTextValue.setSelection(mEditTextValue.getText().length());

            }
        });*/
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
