package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.FormItemEditTextListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementLocationSuggest;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementLocationSuggestViewHolder extends BaseViewHolder {

    private final View ivAsteric;
    private final LinearLayout llMainForm;
    public AppCompatTextView mTextViewTitle;
    public AppCompatTextView mEditTextValue;
    public View ivCancel;
    public FormItemEditTextListener mFormCustomEditTextListener;

    public FormElementLocationSuggestViewHolder(View v, FormItemEditTextListener listener) {
        super(v);
        // context = v.getContext();
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatTextView) v.findViewById(R.id.formElementValue);
        ivCancel = v.findViewById(R.id.ivCancel);
        mFormCustomEditTextListener = listener;
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mEditTextValue.addTextChangedListener(mFormCustomEditTextListener);
        mEditTextValue.setMaxLines(1);
        ivAsteric = v.findViewById(R.id.ivAsteric);
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
        mTextViewTitle.setText(formElement.getTitle());
        ivAsteric.setVisibility(formElement.isRequired() ? View.VISIBLE : View.GONE);

        mEditTextValue.setText(formElement.getValue());
        ivCancel.setVisibility(TextUtils.isEmpty(formElement.getValue()) ? View.GONE : View.VISIBLE);
        ivCancel.setOnClickListener(v -> {
            formElement.setValue("");
            mEditTextValue.setText("");
            v.setVisibility(View.GONE);
        });
        mEditTextValue.setHint(formElement.getHint());
        mEditTextValue.setOnClickListener(v -> {
           /* mEditTextValue.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditTextValue, InputMethodManager.SHOW_IMPLICIT);*/
            ((FormElementLocationSuggest) formElement).getClickListener().onTextClicked(formElement.getTag());
        });
    }
}

