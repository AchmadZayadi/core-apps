package com.sesolutions.speech;

/**
 * Contains the methods which are called to notify text to speech progress status.
 *
 * @author Aleksandar Gotev
 */
public interface TextToSpeechCallback {
    void onStartPlaying();
    void onCompleted();
    void onError();
}
