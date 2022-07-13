package com.sesolutions.ui.albums_core;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.ui.albums.BrowseAlbumFragment;
import com.sesolutions.ui.albums.SearchAlbumFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class C_SearchAlbumFragment extends BrowseAlbumFragment implements View.OnClickListener, OnLoadMoreListener {


    private AppCompatEditText etMusicSearch;
    private int isBackFrom;
    private boolean hideToolbar;

    public static C_SearchAlbumFragment newInstance(int userId) {
        C_SearchAlbumFragment frag = new C_SearchAlbumFragment();
        frag.loggedinId = userId;
        frag.hideToolbar = true;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                isBackFrom = activity.isBackFrom;
                activity.isBackFrom = 0;
                videoList.clear();
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
        v = inflater.inflate(R.layout.fragment_search_refresh, container, false);
        applyTheme();
        if (!(loggedinId > 0)) {
            initScreenData();
        }
        return v;
    }

    @Override
    public void initScreenData() {
        init();
        txtNoData = Constant.MSG_NO_ALBUM_FOUND;
        setRecyclerView();
        //disable swipe to refresh beacuse it is useless on search...
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setEnabled(false);

        if (loggedinId > 0) {
            callMusicAlbumApi(1);
        } else {
            new Handler().postDelayed(() -> {
                openKeyboard();
                etMusicSearch.requestFocus();
            }, 200);
        }
    }

    public void init() {
        super.init();
        if (hideToolbar) {
            v.findViewById(R.id.toolbar).setVisibility(View.GONE);
        }
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(Constant.TXT_SERACH_ALBUM);

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        final ViewGroup transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCancel.setVisibility(View.GONE);
                etMusicSearch.setText("");
            }
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(this);
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                androidx.transition.TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));
        ((RelativeLayout) v.findViewById(R.id.llsearchbg)).setBackgroundColor(Color.parseColor(Constant.text_color_1));
        etMusicSearch.setTextColor(Color.parseColor(Constant.backgroundColor));

        etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    searchKey = etMusicSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        activity.filteredMap = null;
                        videoList.clear();
                        result = null;
                        callMusicAlbumApi(1);
                    }
                    return true;
                }
                return false;
            }
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
        activity.filteredMap = null;
        //fragmentManager.beginTransaction().replace(R.id.container, new SearchFormFragment()).addToBackStack(null).commit();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_ALBUM, map, Constant.URL_ALBUM_FILTER_FORM))
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
                videoList.clear();
                callMusicAlbumApi(1);
                break;
        }

        return super.onItemClicked(object1, object2, postion);
    }
}
