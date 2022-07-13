package com.sesolutions.ui.common;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.speech.Speech;
import com.sesolutions.speech.TextToSpeechCallback;
import com.sesolutions.speech.ui.SpeechProgressView;
import com.sesolutions.utils.CustomLog;

public class TTSSpeakDialogFragment extends AppCompatDialogFragment implements View.OnClickListener, TextToSpeechCallback {

    private View v;

    private TextView tvStop;
    private TextView tvPlay;
    private TextView tvTitle;
    private TextView tvLanguage;

    private Context context;
    private SpeechProgressView progress;
    private String title;
    private String text;
    // private View ivTryAgain;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_tts_speak, container, false);
        try {
            context = getContext();
            Speech.init(context, context.getPackageName())/*.setTextToSpeechPitch(0.2f)*/;
            progress = (SpeechProgressView) v.findViewById(R.id.progress);
            tvTitle = v.findViewById(R.id.tvDialogText);
            tvTitle.setText(title);
            tvStop = v.findViewById(R.id.tvStop);
            tvPlay = v.findViewById(R.id.tvPlay);
            tvLanguage = v.findViewById(R.id.tvLanguage);
            progress.setVisibility(View.VISIBLE);
            tvStop.setOnClickListener(this);
            tvPlay.setOnClickListener(this);
            //progress.stop();
            new Handler().postDelayed(this::startSpeaking, 1000);
            //new Handler().postDelayed(()->Speech.getInstance().setTextToSpeechPitch(0.5f), 10000);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void startSpeaking() {

        //HashMap<String, String> onlineSpeech = new HashMap<>();
        //onlineSpeech.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");
        Speech.getInstance().say(text, this);
    }

    @Override
    public void onStartPlaying() {
        //progress=v.findViewById(R.id.progress);
        progress.play();
        tvPlay.setVisibility(View.GONE);
        // tvPlay.setText(R.string.txt_pause);
    }

    @Override
    public void onCompleted() {
        tvPlay.setVisibility(View.VISIBLE);
        progress.stop();
        tvPlay.setText(R.string.txt_replay);
    }

    @Override
    public void onError() {
        //tvPlay.setVisibility(View.VISIBLE);
        //progress.stop();
        //tvPlay.setText(R.string.txt_play);
        dismiss();
    }


    public static TTSSpeakDialogFragment newInstance(String title, String text) {
        TTSSpeakDialogFragment frag = new TTSSpeakDialogFragment();
        frag.title = title;
        frag.text = text;
        return frag;
    }

    @Override
    public void onStop() {
        // if(!isStopClicked) {
        try {
            if (!isStopClicked) {
                tvPlay.setVisibility(View.VISIBLE);
                tvPlay.setText(R.string.txt_replay);
                progress.stop();
                Speech.getInstance().stopTextToSpeech();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //  Speech.getInstance().shutdown();
        // dismiss();
        //   }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        isStopClicked = false;
    }

    boolean isStopClicked = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPlay:
                startSpeaking();
                break;
            case R.id.tvStop:
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isStopClicked = true;
        Speech.getInstance().stopTextToSpeech();
        Speech.getInstance().shutdown();
        super.onDismiss(dialog);
    }
}
