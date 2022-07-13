package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementTitle;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementTitleHolder extends BaseViewHolder {

    private final LinearLayout llMainForm;
    private final View ivAsteric;
    public AppCompatTextView mTextViewTitle;

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View.OnClickListener onClickListener;

    public FormElementTitleHolder(View v) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        ivAsteric = v.findViewById(R.id.ivAsteric);
        //  mEditTextValue.setEnabled(false);
        // mEditTextValue.addTextChangedListener(mFormCustomEditTextListener);
    }

/*    @Override
    public FormItemEditTextListener getListener() {
        return mFormCustomEditTextListener;
    }*/

    @Override
    public void bind(int position, final BaseFormElement formElement, final Context context) {
        //hiding item view if hidden
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        mTextViewTitle.setText(formElement.getTitle());
        try {
            mTextViewTitle.setTypeface(null, ((FormElementTitle) formElement).isBoldText() ? Typeface.BOLD : Typeface.NORMAL);
        }catch (Exception e){
            e.printStackTrace();
         }

        ivAsteric.setVisibility(formElement.isRequired() ? View.VISIBLE : View.GONE);

    }
}