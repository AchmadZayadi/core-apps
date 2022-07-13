package com.sesolutions.ui.blogs_core;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.sesolutions.R;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class C_SearchBlogFragment extends C_BrowseBlogsFragment {


    private AppCompatEditText etMusicSearch;
    private int isBackFrom;

    public static C_SearchBlogFragment newInstance(int userId) {
        C_SearchBlogFragment frag = new C_SearchBlogFragment();
        frag.loggedinId = userId;
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
                final Object value = activity.filteredMap.get(Constant.KEY_SEARCH_TEXT);
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
//        txtNoData = R.string.msg_no_group_found;
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
        }
        return v;
    }

    public void init() {
        super.init();

        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(super.getStrings(R.string.txt_search_blog));
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext));

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
                        FormFragment.newInstance(Constant.FormType.FILTER_BLOG, map, Constant.URL_BLOG_FILTER_FORM))
                .addToBackStack(null)
                .commit();
    }
}
