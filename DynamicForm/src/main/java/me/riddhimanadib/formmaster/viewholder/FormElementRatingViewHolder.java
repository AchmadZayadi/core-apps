package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementRating;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementRatingViewHolder extends BaseViewHolder {

    private final LinearLayout llMainForm;
    public AppCompatTextView mTextViewTitle;
    public RatingBar mRatingBar;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementRating mFormElementSwitch;
    private int mPosition;

    public FormElementRatingViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mRatingBar = (RatingBar) v.findViewById(R.id.formElementValue);
        mReloadListener = reloadListener;

    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        try {
            mFormElement = formElement;
            mPosition = position;
            mFormElementSwitch = (FormElementRating) mFormElement;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = formElement.isHidden() ? 0 : params.height;
            llMainForm.setLayoutParams(params);

            mTextViewTitle.setText(mFormElementSwitch.getTitle());
            mRatingBar.setOnRatingBarChangeListener((ratingBar, v, fromUser) -> mReloadListener.updateValue(position, "" + v));
            mRatingBar.setRating(Float.parseFloat(mFormElementSwitch.getValue()));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
