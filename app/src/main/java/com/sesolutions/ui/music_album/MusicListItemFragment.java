package com.sesolutions.ui.music_album;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sesolutions.R;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.CustomLog;

public class MusicListItemFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private Albums albums;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.item_music_list, container, false);
        try {
            new ThemeManager().applyTheme((ViewGroup) v, context);
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);

    //    Glide.with(context).load(albums.getImageUrl()).into((ImageView) v.findViewById(R.id.ivSongImage));
        Glide.with(context).load(albums.getImageUrl()).into((ImageView) v.findViewById(R.id.ivSongImage2));
        ((TextView) v.findViewById(R.id.tvSongTitle)).setText(albums.getTitle());
        ((TextView) v.findViewById(R.id.tvSongTitle)).setSelected(true);
        ((TextView) v.findViewById(R.id.tvSongTitle)).setFocusable(true);
        //initSlide();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static MusicListItemFragment newInstance(Albums albums) {
        MusicListItemFragment frag = new MusicListItemFragment();
        frag.albums = albums;
        return frag;
    }
}
