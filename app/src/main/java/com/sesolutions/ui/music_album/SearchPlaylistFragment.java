package com.sesolutions.ui.music_album;


import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class SearchPlaylistFragment extends PlaylistFragment implements View.OnClickListener, OnLoadMoreListener {


    private AppCompatEditText etMusicSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                // isBackFrom = activity.isBackFrom;
                activity.isBackFrom = 0;
                albumsList.clear();
                result = null;
                final Object value = activity.filteredMap.get(Constant.KEY_TITLE_NAME);
                if (null != value) {
                    searchKey = value.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            etMusicSearch.setText(value.toString());
                        }
                    }, 200);
                }
                callMusicAlbumApi(1);
            }
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_search, container, false);
        txtNoData = Constant.MSG_NO_PLAYLIST_FOUND;
        applyTheme();
        init();
        //  apiUrl = Constant.URL_MUSIC_SEARCH;
        setRecyclerView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openKeyboard();
                etMusicSearch.requestFocus();
            }
        }, 200);
        return v;
    }

    public void init() {
        super.init();
       /* recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);*/
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(Constant.TXT_SERACH_PLAYLIST);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        final ViewGroup transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);        final View ivCancel = v.findViewById(R.id.ivCancel);        ivCancel.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                ivCancel.setVisibility(View.GONE);                etMusicSearch.setText("");            }        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(this);
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                androidx.transition.TransitionManager.beginDelayedTransition(transitionsContainer);                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });

        etMusicSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard();
                searchKey = etMusicSearch.getText().toString();
                if (!TextUtils.isEmpty(searchKey)) {
                    albumsList.clear();
                    result = null;
                    callMusicAlbumApi(1);
                }
                return true;
            }
            return false;
        });
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.ivFilter:
                    goToMusicSearchForm();
                    break;
                case R.id.ivMic:
                    closeKeyboard();
                    TTSDialogFragment.newInstance(this).show(fragmentManager, "tts");
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToMusicSearchForm() {
        Map<String, Object> map = new HashMap<>();

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_MUSIC_PLAYLIST, map, Constant.URL_MUSIC_SEARCH_FILTER_PLAYLIST_FORM))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.TTS_POPUP_CLOSED:
                searchKey = "" + object2;
                etMusicSearch.setText(searchKey);
                result = null;
                albumsList.clear();
                callMusicAlbumApi(1);
                break;
        }

        return super.onItemClicked(object1, object2, postion);
    }
}
