package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementGroupQuestion;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementGroupQuestionViewHolder extends BaseViewHolder {

    //public AppCompatTextView mTextViewTitle;
    public AppCompatTextView tvHint;
    //public AppCompatImageView mImage;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementGroupQuestion mFormElementFile;
    private LinearLayout llMainForm;
   // private View ivAsteric;
    private int mPosition;

    public FormElementGroupQuestionViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        //mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        //mImage = (AppCompatImageView) v.findViewById(R.id.formElementImage);
        tvHint = v.findViewById(R.id.tvHint);
       // ivAsteric = v.findViewById(R.id.ivAsteric);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mReloadListener = reloadListener;
    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);

        mFormElementFile = (FormElementGroupQuestion) mFormElement;
        //ivAsteric.setVisibility(mFormElementFile.isRequired()?View.VISIBLE:View.GONE);
        tvHint.setText(mFormElementFile.getHint());

        //mTextViewTitle.setText(mFormElementFile.getTitle());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mFormElementFile.getClickListener()) {
                    mFormElementFile.getClickListener().onTextClicked(mFormElementFile.getTag());
                }
            }
        });
    }
}
