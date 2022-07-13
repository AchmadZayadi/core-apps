package com.sesolutions.ui.common;

import com.google.android.gms.ads.AdListener;
import com.sesolutions.listeners.OnUserClickedListener;

public class AdMobAdapter extends AdListener {
    private final OnUserClickedListener<Integer, Object> listener;

    public AdMobAdapter(OnUserClickedListener<Integer, Object> listener) {
        super();
        this.listener = listener;
    }

    public void onAdClosed() {
    }

    public void onAdFailedToLoad(int var1) {
    }

    public void onAdLeftApplication() {
    }

    public void onAdOpened() {
    }

    public void onAdLoaded() {
    }

    public void onAdClicked() {
    }

    public void onAdImpression() {
    }
}
