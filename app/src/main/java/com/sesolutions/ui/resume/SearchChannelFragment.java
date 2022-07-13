package com.sesolutions.ui.resume;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.video.VideoChannelFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class SearchChannelFragment extends VideoChannelFragment implements View.OnClickListener, OnLoadMoreListener {

    private static final String TAG = "SearchChannelFragment";

    private int isBackFrom;
    private AppCompatEditText etMusicSearch;

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
        v = inflater.inflate(R.layout.fragment_music_search, container, false);
        applyTheme(v);
        init();
        txtNoData = Constant.MSG_NO_CHANNEL_FOUND;
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

        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(Constant.TITLE_CHANNEl_SEARCH);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        final ViewGroup transitionsContainer = v.findViewById(R.id.llOption);
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

        etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    searchKey = etMusicSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchKey)) {
                        videoList.clear();
                        result = null;
                        callMusicAlbumApi(1);
                    }
                    return true;
                }
                return false;
            }
        });
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));

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
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_VIDEO, map, Constant.URL_CHANNEL_SEARCH_FORM))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int position) {
        switch (object1) {
            case Constant.Events.TTS_POPUP_CLOSED:
                searchKey = "" + object2;
                etMusicSearch.setText(searchKey);
                result = null;
                videoList.clear();
                callMusicAlbumApi(1);
                break;
        }

        return super.onItemClicked(object1, object2, position);
    }
}
