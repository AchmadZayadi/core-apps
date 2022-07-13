package com.sesolutions.ui.qna;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.transition.TransitionManager;
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
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class SearchQAFragment extends QAFragment implements View.OnClickListener, OnLoadMoreListener {


    private AppCompatEditText etMusicSearch;
    private int isBackFrom;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                isBackFrom = activity.isBackFrom;
                activity.isBackFrom = 0;
                videoList.clear();
                result = null;
                final Object value = activity.filteredMap.get(Constant.KEY_SEARCHTEXT);
                if (null != value) {
                    searchKey = value.toString();
                    new Handler().postDelayed(() -> etMusicSearch.setText(value.toString()), 200);
                }
                callMusicAlbumApi(1);
            }
            return v;
        }
        v = inflater.inflate(R.layout.fragment_search_refresh, container, false);
        applyTheme(v);
        init();
        txtNoData = R.string.MSG_NO_QUESTION_FOUND;
        setRecyclerView();
        //disable swipe to refresh beacuse it is useless on search...
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setEnabled(false);
            new Handler().postDelayed(() -> {
                openKeyboard();
                etMusicSearch.requestFocus();
            }, 200);
        return v;
    }

    public void init() {
        super.init();
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(R.string.TXT_SERACH_QUESTION);

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        final ViewGroup transitionsContainer = v.findViewById(R.id.llOption);
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
                    videoList.clear();
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
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_QA, map, Constant.URL_QA_SEARCH))
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
