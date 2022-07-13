package com.sesolutions.ui.music_album;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;


public class ShowLyricsFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private String lyrics;
    private String title;
    private boolean isOverview;


    public static ShowLyricsFragment newInstance(String lyrics, String title, boolean isOverview) {

        ShowLyricsFragment fragment = new ShowLyricsFragment();
        fragment.lyrics = lyrics;
        fragment.title = title;
        fragment.isOverview = isOverview;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_lyrics, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        try {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            if (isOverview && TextUtils.isEmpty(lyrics)) {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_OVERVIEW);
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            } else {
                ((TextView) v.findViewById(R.id.tvLyrics)).setText(isOverview ? Html.fromHtml(lyrics) : lyrics);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
