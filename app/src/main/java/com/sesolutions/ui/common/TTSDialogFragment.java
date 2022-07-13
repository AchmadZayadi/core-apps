package com.sesolutions.ui.common;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.speech.GoogleVoiceTypingDisabledException;
import com.sesolutions.speech.Speech;
import com.sesolutions.speech.SpeechDelegate;
import com.sesolutions.speech.SpeechRecognitionNotAvailable;
import com.sesolutions.speech.ui.SpeechProgressView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TTSDialogFragment extends AppCompatDialogFragment implements View.OnClickListener, SpeechDelegate {

    private View v;

    private TextView tvResult;
    private TextView tvTitle;
    private TextView tvLanguage;

    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private SpeechProgressView progress;
    private String result;
    private View ivTryAgain;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_tts, container, false);
        try {
            context = getContext();
            Speech.init(context, context.getPackageName());
            progress = (SpeechProgressView) v.findViewById(R.id.progress);
            tvTitle = v.findViewById(R.id.tvDialogText);
            tvResult = v.findViewById(R.id.tvResult);
            tvLanguage = v.findViewById(R.id.tvLanguage);
            ivTryAgain = v.findViewById(R.id.ivTryAgain);
            ivTryAgain.setOnClickListener(this);
            startListening();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void startListening() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            onRecordAudioPermissionGranted();
        } else {
            askForPermission(Manifest.permission.RECORD_AUDIO);
            /*RxPermissions.getInstance(this)
                    .request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            onRecordAudioPermissionGranted();
                        } else {
                            Util.showToast(context, context.getResources().getString(R.string.MSG_PERMISSION_DENIED));
                        }
                    });*/
        }
        //  onRecordAudioPermissionGranted();
    }

    public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(context.getResources().getString(R.string.MSG_PERMISSION_DENIED))
                    .setPermissions(Manifest.permission.RECORD_AUDIO)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                onRecordAudioPermissionGranted();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            onDismiss();
        }
    };


    private void onRecordAudioPermissionGranted() {
        //   button.setVisibility(View.GONE);
        //  linearLayout.setVisibility(View.VISIBLE);

        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().setStopListeningAfterInactivity(6000).startListening(progress, this);
            tvLanguage.setText(Locale.getDefault().getDisplayName());
        } catch (SpeechRecognitionNotAvailable exc) {
            // showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            //showEnableGoogleVoiceTyping();
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // Speech.getInstance().shutdown();
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        }
        if (!TextUtils.isEmpty(result)) {
            listener.onItemClicked(Constant.Events.TTS_POPUP_CLOSED, result, 0);
            result = null;
        }
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static TTSDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener) {
        TTSDialogFragment frag = new TTSDialogFragment();
        frag.listener = listener;
        return frag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivTryAgain:
                progress.setVisibility(View.VISIBLE);
                ivTryAgain.setVisibility(View.GONE);
                tvTitle.setText(context.getResources().getString(R.string.tts_say));
                tvResult.setText(" ");
                startListening();
                break;
        }
        // onDismiss();
    }

    @Override
    public void onStartOfSpeech() {
        CustomLog.e("onstart", "sss");
        tvTitle.setText(context.getResources().getString(R.string.tts_say));
        tvResult.setText(" ");
        progress.play();
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {

        try {
            String result = "";
            for (String s : results) {
                CustomLog.e("onPartial", "" + s);
                result += " " + s;
            }
            tvResult.setText(result);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onSpeechResult(String result) {
        progress.stop();
        CustomLog.e("onSpeechResult", "." + result);
        this.result = result;
        if (TextUtils.isEmpty(result)) {
            tvTitle.setText(context.getResources().getString(R.string.tts_error));
            tvResult.setText(context.getResources().getString(R.string.tts_try_again));
            CustomLog.e("onSpeechResult", "__Nothing___" + result);
            if (Speech.getInstance().isListening()) {
                Speech.getInstance().stopListening();
            }

            progress.setVisibility(View.GONE);
            ivTryAgain.setVisibility(View.VISIBLE);
        } else {
            onDismiss();
        }
    }
}
