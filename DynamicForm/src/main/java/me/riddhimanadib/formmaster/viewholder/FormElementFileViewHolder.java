package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementFile;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementFileViewHolder extends BaseViewHolder {

    //public AppCompatTextView mTextViewTitle;
    public AppCompatTextView tvHint,videofilepath;
    public AppCompatImageView mImage,ivDate;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementFile mFormElementFile;
    private LinearLayout llMainForm;
    private LinearLayout lldocument;
    private TextView formElementDocumenttext;
 //   private View ivAsteric;
    private int mPosition;

    public FormElementFileViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
   //     mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mImage = (AppCompatImageView) v.findViewById(R.id.formElementImage);
        ivDate = (AppCompatImageView) v.findViewById(R.id.ivDate);
        tvHint = v.findViewById(R.id.tvHint);
        videofilepath = v.findViewById(R.id.videofilepath);
        lldocument = v.findViewById(R.id.lldocument);
    //    ivAsteric = v.findViewById(R.id.ivAsteric);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);
        formElementDocumenttext = (TextView) v.findViewById(R.id.formElementDocumenttext);
        mReloadListener = reloadListener;
    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        mFormElement = formElement;
        mPosition = position;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = formElement.isHidden() ? 0 : params.height;
        llMainForm.setLayoutParams(params);

        mFormElementFile = (FormElementFile) mFormElement;
        Log.e("filepath",""+mFormElementFile.getHint());
        Log.e("Thumbnail",""+mFormElementFile.getName());


                if(mFormElementFile.getHint().equalsIgnoreCase("Upload only PDF or Word Document Only.")){
                    ivDate.setImageResource(R.drawable.file_process);
                }else {
                    ivDate.setImageResource(R.drawable.camera_st);
                }

   //     ivAsteric.setVisibility(mFormElementFile.isRequired() ? View.VISIBLE : View.GONE);
        tvHint.setText(mFormElementFile.getHint());
        if (!TextUtils.isEmpty(mFormElementFile.getValue())) {
            String filePath = !TextUtils.isEmpty(mFormElementFile.getThumbnail()) ? mFormElementFile.getThumbnail() : mFormElementFile.getValue();
            if (filePath.endsWith("mp4")) {
                // if file type is video(mp4) then show dummy image
                String filename=filePath.substring(filePath.lastIndexOf("/")+1);
              //  mImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_square));
                videofilepath.setText(""+filename);
                mImage.setVisibility(View.GONE);
                lldocument.setVisibility(View.GONE);
                videofilepath.setVisibility(View.VISIBLE);
                ivDate.setImageResource(R.drawable.camera_st);
            }else if (filePath.endsWith("pdf") || filePath.endsWith("html")|| filePath.endsWith("excel")|| filePath.endsWith("point")|| filePath.endsWith("word")
                    || filePath.endsWith("plane")|| filePath.endsWith("text")) {
                // if file type is video(mp4) then show dummy image
                formElementDocumenttext.setText(""+getFileName(filePath));
                lldocument.setVisibility(View.VISIBLE);
                videofilepath.setVisibility(View.GONE);
                mImage.setVisibility(View.GONE);
                ivDate.setImageResource(R.drawable.file_process);
            } else {
                videofilepath.setVisibility(View.GONE);
                mImage.setVisibility(View.VISIBLE);
            //    Picasso.with(context).load(filePath).into(mImage);
                Log.e("filepath",""+filePath);
                mImage.setImageDrawable(Drawable.createFromPath(filePath));
                lldocument.setVisibility(View.GONE);
                ivDate.setImageResource(R.drawable.camera_st);
            }
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);/*CENTER_CROP*/
        }
      //  mTextViewTitle.setText(mFormElementFile.getTitle());
        //  mImage.setChecked(mFormElementSwitch.getValue().equals("1"));
        /*mTextViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mFormElementFile.getClickListener()) {
                    mFormElementFile.getClickListener().onTextClicked(mFormElementFile.getTag());
                }
            }
        });*/
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mFormElementFile.getClickListener()) {
                    mFormElementFile.getClickListener().onTextClicked(mFormElementFile.getTag());
                }
            }
        });
    }

    public static String getFileName(String url) {
        String fileName;
        int slashIndex = url.lastIndexOf("/");
        int qIndex = url.lastIndexOf("?");
        if (qIndex > slashIndex) {//if has parameters
            fileName = url.substring(slashIndex + 1, qIndex);
        } else {
            fileName = url.substring(slashIndex + 1);
        }
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        return fileName;
    }

}
