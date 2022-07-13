package com.sesolutions.ui.storyview;

import android.content.Context;
import android.content.SharedPreferences;

import com.sesolutions.BuildConfig;
import com.sesolutions.utils.Constant;

public class StoryPreference {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private static final String PREF_NAME = "ses_storyview_cache_pref_" +BuildConfig.APP_NAME.replace(" ", "");
    private static final int PREF_MODE_PRIVATE = 1;

    public StoryPreference(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    public void clearStoryPreferences() {
        editor.clear();
        editor.apply();
    }

    public void setStoryVisited(String uri) {
        editor.putBoolean(uri, true);
        editor.apply();
    }

    public void setStoryVisited(int storyId) {
        editor.putBoolean("SES_STORY_" + storyId, true);
        editor.apply();
    }

    public boolean isStoryVisited(String uri) {
        return preferences.getBoolean(uri, false);
    }

    public boolean isStoryVisited(int storyId) {
        return preferences.getBoolean("SES_STORY_" + storyId, false);
    }
}
