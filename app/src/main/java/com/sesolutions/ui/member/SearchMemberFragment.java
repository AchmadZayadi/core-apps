package com.sesolutions.ui.member;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class SearchMemberFragment extends MemberFragment {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    EditText etMusicSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                //  isBackFrom = activity.isBackFrom;
                activity.isBackFrom = 0;
                albumsList.clear();
                result = null;
                final Object value = activity.filteredMap.get(Constant.KEY_SEARCH_TEXT);
                if (null != value) {
                    search = value.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            etMusicSearch.setText(value.toString());

                        }
                    }, 100);
                }
                callMusicAlbumApi(1);
            }
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_search, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
        try {
            applyTheme(v);
            init();
            setRecyclerView(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    etMusicSearch.setFocusableInTouchMode(true);
                    etMusicSearch.setFocusable(true);
                    etMusicSearch.requestFocus();
                    try {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etMusicSearch, InputMethodManager.SHOW_IMPLICIT);
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                }
            }, 500);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    @Override
    public void init() {

        try {
            //  super.init();
            //dont call super init() because swiperefreshlayout not available view id is different
            pb = v.findViewById(R.id.pb);
            //setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));

            etMusicSearch = v.findViewById(R.id.etMusicSearch);
            recyclerView = v.findViewById(R.id.recyclerview);
            etMusicSearch.setHint(getStrings(R.string.TITLE_SEARCH_MEMBER));
         //   v.findViewById(R.id.tvDone).setVisibility(View.GONE);
            v.findViewById(R.id.ivFilter).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivFilter).setOnClickListener(this);
            final ViewGroup transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);        final View ivCancel = v.findViewById(R.id.ivCancel);
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
                    androidx.transition.TransitionManager.beginDelayedTransition(transitionsContainer);                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
                }
            });
            v.findViewById(R.id.ivBack).setOnClickListener(this);

            etMusicSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        closeKeyboard();
                        search = etMusicSearch.getText().toString();
                        if (!TextUtils.isEmpty(search)) {
                            result = null;
                            albumsList.clear();
                            callMusicAlbumApi(REQ_CODE_SEARCH);
                        }
                        return true;
                    }
                    return false;
                }
            });
            setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.TTS_POPUP_CLOSED:
                search = "" + object2;
                etMusicSearch.setText(search);
                result = null;
                albumsList.clear();
                callMusicAlbumApi(1);
                break;
        }

        return super.onItemClicked(object1, object2, postion);
    }
}
