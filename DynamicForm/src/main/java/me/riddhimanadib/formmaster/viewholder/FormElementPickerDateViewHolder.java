package me.riddhimanadib.formmaster.viewholder;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;

/**
 * ViewHolder for DatePicker
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementPickerDateViewHolder extends BaseViewHolder {

    private final View ivAsteric;
    private final LinearLayout llMainForm;
    private AppCompatTextView mTextViewTitle;
    private AppCompatEditText mEditTextValue;
    private View llSelect;
    private View ivDown;
    private DatePickerDialog mDatePickerDialog;
    private Calendar mCalendarCurrentDate;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private int mPosition;

    public FormElementPickerDateViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        llSelect = v.findViewById(R.id.llSelect);
        ivDown = v.findViewById(R.id.ivDown);
        mReloadListener = reloadListener;
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mCalendarCurrentDate = Calendar.getInstance();
        ivAsteric = v.findViewById(R.id.ivAsteric);
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        ivDown.setVisibility(View.GONE);
        mDatePickerDialog = new DatePickerDialog(context,
                date,
                mCalendarCurrentDate.get(Calendar.YEAR),
                mCalendarCurrentDate.get(Calendar.MONTH),
                mCalendarCurrentDate.get(Calendar.DAY_OF_MONTH));

        mTextViewTitle.setText(formElement.getTitle());
        mEditTextValue.setText(formElement.getValue());
       // mEditTextValue.setHintTextColor(Color.parseColor(""));
        ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
        mEditTextValue.setHint(formElement.getHint());
        mEditTextValue.setFocusableInTouchMode(false);

        llSelect.setOnClickListener(v -> mDatePickerDialog.show());

        mEditTextValue.setOnClickListener(v -> mDatePickerDialog.show());
    }

    /**
     * setting up date picker dialog listener
     */
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendarCurrentDate.set(Calendar.YEAR, year);
            mCalendarCurrentDate.set(Calendar.MONTH, monthOfYear);
            mCalendarCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormatDate = ((FormElementPickerDate) mFormElement).getDateFormat();
            SimpleDateFormat sdfDate = new SimpleDateFormat(myFormatDate, Locale.US);

            String currentValue = mFormElement.getValue();
            String newValue = sdfDate.format(mCalendarCurrentDate.getTime());

            // trigger event only if the value is changed
            if (!currentValue.equals(newValue)) {
                mReloadListener.updateValue(mPosition, newValue);
            }
        }

    };

}
