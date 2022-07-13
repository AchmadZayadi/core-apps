package me.riddhimanadib.formmaster.viewholder;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerTime;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementPickerTimeViewHolder extends BaseViewHolder {

    private final View ivAsteric;
    private final LinearLayout llMainForm;
    private AppCompatTextView mTextViewTitle;
    private AppCompatEditText mEditTextValue;
    private View llSelect;
    private View ivDown;
  //  private ImageView ivDate;
    private TimePickerDialog mTimePickerDialog;
    private Calendar mCalendarCurrentTime;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private Drawable dWatch;
    private int mPosition;

    public FormElementPickerTimeViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mEditTextValue = (AppCompatEditText) v.findViewById(R.id.formElementValue);
        llSelect = v.findViewById(R.id.llSelect);
        ivDown = v.findViewById(R.id.ivDown);
      //  ivDate = v.findViewById(R.id.ivDate);
        mReloadListener = reloadListener;
        dWatch = ContextCompat.getDrawable(context, R.drawable.ses_watch);
        mCalendarCurrentTime = Calendar.getInstance();
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mTimePickerDialog = new TimePickerDialog(context,
                time,
                mCalendarCurrentTime.get(Calendar.HOUR),
                mCalendarCurrentTime.get(Calendar.MINUTE),
                false);
        ivAsteric = v.findViewById(R.id.ivAsteric);
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);
        mTextViewTitle.setText(formElement.getTitle());
        mEditTextValue.setText(formElement.getValue());
        ivAsteric.setVisibility(formElement.isRequired()?View.VISIBLE:View.GONE);
        mEditTextValue.setHint(formElement.getHint());
        mEditTextValue.setFocusableInTouchMode(false);
        ivDown.setVisibility(View.GONE);
      //  ivDate.setImageDrawable(dWatch);
        llSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerDialog.show();
            }
        });

        mEditTextValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerDialog.show();
            }
        });
    }

    /**
     * setting up time picker dialog listener
     */
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendarCurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendarCurrentTime.set(Calendar.MINUTE, minute);

            String myFormatTime = ((FormElementPickerTime) mFormElement).getTimeFormat(); // custom format
            SimpleDateFormat sdfTime = new SimpleDateFormat(myFormatTime, Locale.US);

            String currentValue = mFormElement.getValue();
            String newValue = sdfTime.format(mCalendarCurrentTime.getTime());

            // trigger event only if the value is changed
            if (!currentValue.equals(newValue)) {
                mReloadListener.updateValue(mPosition, newValue);
            }
        }
    };

}
