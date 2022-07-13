package com.sesolutions.ui.music_core;

import androidx.fragment.app.FragmentManager;

import com.sesolutions.R;

public class CMusicUtil {
    public static void openViewFragment(FragmentManager fragmentManager, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCMusicFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    static void openSearchFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchCPlaylistFragment()).addToBackStack(null).commit();
    }

    public static void openBrowseFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CMusicParentFragment())
                .addToBackStack(null)
                .commit();
    }
}
