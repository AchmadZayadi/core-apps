package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.model.BaseFormElement;

/**
 * ViewHolder for Header
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementHeader extends BaseViewHolder {
    public AppCompatTextView mTextViewTitle;

    public FormElementHeader(View v) {
        super(v);
        // llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);

        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
       /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);*/
        mTextViewTitle.setText(formElement.getTitle());
    }

}
