package com.sesolutions.ui.member;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.sesolutions.R;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;

public class SearchMemberSuggestionFragment extends MemberListFragment {

    private static final int REQ_CODE_SEARCH = 2;
    private static final int REQ_CODE_FEELING = 1;
    EditText etMusicSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            /*if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
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
                    }, 200);
                }
                callMusicAlbumApi(1);
            }*/
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_member, container, false);
        try {
            applyTheme(v);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        init();
        setRecyclerView();
    }

    @Override
    public void init() {
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        v.findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
        albumsList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(getStrings(R.string.TITLE_SEARCH_MEMBER));
        setRoundedHoloDrawable(v.findViewById(R.id.rlCommentEdittext));
        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
      /*  ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_FOLLOWERS);
        v.findViewById(R.id.ivBack).setOnClickListener(this);*/

        //  transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ivCancel.setVisibility(View.GONE);
                etMusicSearch.setText("");
            }
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(SearchMemberSuggestionFragment.this).show(fragmentManager, "tts");
        });
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // android.support.transition.TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
            }
        });

        etMusicSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard();
                search = etMusicSearch.getText().toString();
                // if (!TextUtils.isEmpty(query)) {
                result = null;
                albumsList.clear();
                adapter.notifyDataSetChanged();
                callMusicAlbumApi(1);
                //   }
                return true;
            }
            return false;
        });
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
