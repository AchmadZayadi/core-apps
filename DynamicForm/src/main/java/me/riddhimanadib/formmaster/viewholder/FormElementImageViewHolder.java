package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.squareup.picasso.Picasso;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementFile;
import me.riddhimanadib.formmaster.model.FormElementImage;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementImageViewHolder extends BaseViewHolder {

    //public AppCompatTextView mTextViewTitle;
    public ImageView mainimageviewid;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementImage mFormElementFile;
    private LinearLayout llMainForm;
 //   private View ivAsteric;
    private int mPosition;

    public FormElementImageViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
   //     mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mainimageviewid = (ImageView) v.findViewById(R.id.mainimageviewid);
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

        mFormElementFile = (FormElementImage) mFormElement;
        Log.e("filepathlength",""+mFormElementFile.getValue().length());
        Log.e("filepath",""+mFormElementFile.getValue());

        if (mFormElementFile.getValue()!=null && mFormElementFile.getValue().length()>0) {
            mainimageviewid.setVisibility(View.VISIBLE);
            llMainForm.setVisibility(View.VISIBLE);
           // mainimageviewid.setImageDrawable(Drawable.createFromPath(filePath));
            Picasso.get().load(mFormElementFile.getValue()).into(mainimageviewid);
            mainimageviewid.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else {
            llMainForm.setVisibility(View.GONE);
            mainimageviewid.setVisibility(View.GONE);
        }

    }


}
