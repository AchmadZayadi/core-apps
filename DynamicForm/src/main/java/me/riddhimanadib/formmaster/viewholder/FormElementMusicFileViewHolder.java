package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FlowLayout;
import me.riddhimanadib.formmaster.model.FormElementMusicFile;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementMusicFileViewHolder extends BaseViewHolder {

    private final View ivAsteric;
    public AppCompatTextView mTextViewTitle;
    //    public AppCompatImageView mImage;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementMusicFile mFormElementFile;
    private LinearLayout llMainForm;
    // private View cvChip;
    //private View cvCancel;
    private FlowLayout formElementList;
    //  private AppCompatTextView tvSongName;
    private int mPosition;

    public FormElementMusicFileViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        // cvChip = v.findViewById(R.id.cvChip);
        //cvCancel = v.findViewById(R.id.cvCancel);
        // tvSongName = v.findViewById(R.id.tvSongName);
        formElementList = v.findViewById(R.id.formElementList);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        mReloadListener = reloadListener;
        ivAsteric = v.findViewById(R.id.ivAsteric);
    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        try {
            mFormElement = formElement;
            mPosition = position;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = formElement.isHidden() ? 0 : params.height;
            llMainForm.setLayoutParams(params);
            mFormElementFile = (FormElementMusicFile) mFormElement;
            mTextViewTitle.setText(mFormElementFile.getTitle());
            ivAsteric.setVisibility(mFormElementFile.isRequired()?View.VISIBLE:View.GONE);
            if (mFormElementFile.isFileSelected()) {
                formElementList.setVisibility(View.VISIBLE);
                formElementList.removeAllViews();
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (final String name : mFormElementFile.getMusicList()) {
                    final View view = vi.inflate(R.layout.layout_chip, (ViewGroup) formElementList, false);
                    ((TextView) view.findViewById(R.id.tvSongName)).setText(name.substring(name.lastIndexOf("/")));
                    view.findViewById(R.id.cvCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.setVisibility(View.GONE);
                            mFormElementFile.getMusicList().remove(name);
                        }
                    });
                    formElementList.addView(view);
                }

            } else {
                formElementList.setVisibility(View.GONE);
            }

            mTextViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mFormElementFile.getClickListener()) {
                        mFormElementFile.getClickListener().onTextClicked(mFormElementFile.getTag());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
