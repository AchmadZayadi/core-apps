package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.FormItemEditTextListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementUrl;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementUrlViewHolder extends BaseViewHolder {

    private final LinearLayout llMainForm;
    public AppCompatTextView mTextViewTitle;
    public AppCompatEditText mEditTextValue;
    private final View ivAsteric;
    private final AppCompatButton bUrl;
    public FormItemEditTextListener mFormCustomEditTextListener;

    public FormElementUrlViewHolder(View v, FormItemEditTextListener listener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        bUrl = (AppCompatButton) v.findViewById(R.id.bUrl);
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
    public void bind(int position, BaseFormElement formElement1, final Context context) {
        //hiding item view if hidden
        FormElementUrl formElement = (FormElementUrl) formElement1;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        mTextViewTitle.setText(formElement.getTitle());
        ivAsteric.setVisibility(formElement.isRequired() ? View.VISIBLE : View.GONE);
        Log.e("value__", "" + formElement.getValue());
        mEditTextValue.setText(formElement.getValue());
        mEditTextValue.setHint(formElement.getHint());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextValue.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditTextValue, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        bUrl.setOnClickListener(v->{formElement.getClickListener().onTextClicked(formElement.getTag());});
    }
}
