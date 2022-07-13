package com.sesolutions.ui.contest;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.transition.TransitionManager;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class SearchContestFragment extends ContestFragment {


    private AppCompatEditText etMusicSearch;
    private String filterUrl;
    private String SEARCH_KEY;


    public static SearchContestFragment newInstance(String selectedScreen) {
        SearchContestFragment frag = new SearchContestFragment();
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                activity.isBackFrom = 0;
                contestList.clear();
                result = null;
                final Object value = activity.filteredMap.get(SEARCH_KEY);
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
        applyTheme(v);
        init();
        txtNoData = R.string.MSG_NO_CONTEST_FOUND;
        setRecyclerView();
        //disable swipe to refresh beacuse it is useless on search...
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setEnabled(false);

        if (loggedinId > 0) {
            callMusicAlbumApi(1);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openKeyboard();
                    etMusicSearch.requestFocus();
                }
            }, 200);
        }
        return v;
    }

    public void init() {
        super.init();
        int hintId;
        switch (selectedScreen) {
            case TYPE_ENTRIES:

                hintId = R.string.TXT_SERACH_ENTRIES;
                SEARCH_KEY = Constant.KEY_TITLE_NAME;
                filterUrl = Constant.URL_FILTER_SEARCH_ENTRY;
                break;
            case TYPE_WINNERS:
                hintId = R.string.TXT_SERACH_WINNERS;
                SEARCH_KEY = Constant.KEY_TITLE_NAME;
                filterUrl = Constant.URL_FILTER_SEARCH_ENTRY;
                break;
            default:
                hintId = R.string.TXT_SERACH_CONTEST;
                filterUrl = Constant.URL_FILTER_SEARCH_CONTEST;
                SEARCH_KEY = Constant.KEY_SEARCH_TEXT;
                break;
        }

        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(hintId);

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
                TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));

        etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    searchKey = etMusicSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        activity.filteredMap = null;
                        contestList.clear();
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
                    closeKeyboard();
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
                        FormFragment.newInstance(Constant.FormType.FILTER_EVENT, map, filterUrl))
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
                contestList.clear();
                callMusicAlbumApi(1);
                break;
        }

        return super.onItemClicked(object1, object2, postion);
    }
}
