package com.sesolutions.ui.music_core;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.transition.TransitionManager;

import com.sesolutions.R;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import java.util.HashMap;

public class SearchCPlaylistFragment extends CMusicPlaylistFragment implements View.OnClickListener {


    private AppCompatEditText etMusicSearch;
    private int isBackFrom;

    public static SearchCPlaylistFragment newInstance(int loggedinId) {
        SearchCPlaylistFragment frag = new SearchCPlaylistFragment();

        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                isBackFrom = activity.isBackFrom;
                activity.isBackFrom = 0;
                albumsList.clear();
                result = null;
                final Object value = activity.filteredMap.get(Constant.KEY_SEARCH);
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
        v = inflater.inflate(R.layout.fragment_search, container, false);
        applyTheme(v);
        init();
        txtNoData = R.string.MSG_NO_POLL_FOUND;
        setRecyclerView();
        //disable swipe to refresh beacuse it is useless on search...
        if (SPref.getInstance().isLoggedIn(context)) {
            callMusicAlbumApi(1);
        } else {
            new Handler().postDelayed(() -> {
                openKeyboard();
                etMusicSearch.requestFocus();
            }, 200);
        }
        return v;
    }

    public void init() {
        super.init();
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(getStrings(R.string.TXT_SEARCH_MUSIC));

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        final ViewGroup transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            ivCancel.setVisibility(View.GONE);
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(this);
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));

        etMusicSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard();
                searchKey = etMusicSearch.getText().toString();
                if (!TextUtils.isEmpty(searchKey)) {
                    activity.filteredMap = null;
                    albumsList.clear();
                    result = null;
                    callMusicAlbumApi(1);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        activity.filteredMap = null;
        super.onBackPressed();

    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.ivFilter:
                    activity.filteredMap = null;
                    openFormFragment(Constant.FormType.FILTER_MUSIC_PLAYLIST,new HashMap<>(), Constant.CMUSIC_SEARCH_FORM);
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
