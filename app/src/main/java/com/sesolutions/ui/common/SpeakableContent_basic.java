package com.sesolutions.ui.common;

import android.view.MenuItem;

import com.sesolutions.R;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.speech.Speech;
import com.sesolutions.speech.TextToSpeechCallback;
import com.sesolutions.ui.customviews.PulsatorLayout;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.CustomLog;

public class SpeakableContent_basic extends CommentLikeHelper_basic implements TextToSpeechCallback {
    public Blog album;
    public PulsatorLayout pulsator;
    private boolean isViewPage = false;

    public void init() {
        pulsator = (PulsatorLayout) v.findViewById(R.id.pulsator);
        isViewPage = true;
    }

    @Override
    public void onStartPlaying() {
        pulsator.start();
    }

    @Override
    public void onCompleted() {
        pulsator.stop();
    }

    @Override
    public void onError() {
        Speech.getInstance().say(getString(R.string.msg_tts_content_invalid));
        pulsator.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isViewPage) {
            try {
                switch (item.getItemId()) {


//                    case R.id.speak:
//                        if (pulsator.isStarted()) {
//                            Speech.getInstance().stopTextToSpeech();
//                            pulsator.stop();
//                        } else {
//                            Speech.getInstance().say(album.getBody(), this);
//                        }
//                        break;
                }
            } catch (Exception e) {
                //  CustomLog.e(e);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfiguration.IS_BLOG_TTS_EBANBLED && isViewPage) Speech.init(context);
    }

    @Override
    public void onStop() {
        if (!isViewPage) {
            super.onStop();
            return;
        }
        try {
            if (null != pulsator && pulsator.isStarted()) {
                Speech.getInstance().stopTextToSpeech();
                pulsator.stop();
            }
            Speech.getInstance().shutdown();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onStop();
    }
}
