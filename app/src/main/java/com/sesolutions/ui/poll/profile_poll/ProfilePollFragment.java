package com.sesolutions.ui.poll.profile_poll;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.poll.CreateEditPollFragment;
import com.sesolutions.ui.poll.PollFragment;
import com.sesolutions.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class ProfilePollFragment extends PollFragment implements PopupMenu.OnMenuItemClickListener {

    private EditText etMusicSearch;
    boolean istoolbar=false;

    public static PollFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent, Map<String, Object> map,boolean istoolbar) {
        ProfilePollFragment frag = new ProfilePollFragment();
        frag.parent = parent;
        frag.selectedScreen = TYPE;
        frag.requestMap = map;
        frag.istoolbar = istoolbar;
        return frag;
    }

    public static PollFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent, Map<String, Object> map) {
        ProfilePollFragment frag = new ProfilePollFragment();
        frag.parent = parent;
        frag.selectedScreen = TYPE;
        frag.requestMap = map;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);
        txtNoData = R.string.MSG_NO_POLL_FOUND;
        applyTheme(v);

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.poll);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            initScreenData();
        }

        return v;
    }

    @Override
    public void init() {
        super.init();
        ((TextView) v.findViewById(R.id.tvPost)).setText(R.string.title_create_poll);
        v.findViewById(R.id.llSelect).setOnClickListener(this);
        v.findViewById(R.id.rlCreate).setOnClickListener(this);

        etMusicSearch = v.findViewById(R.id.etMusicSearch);

        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> etMusicSearch.setText(""));
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(ProfilePollFragment.this).show(fragmentManager, "tts");
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
                searchKey = etMusicSearch.getText().toString();
                // if (!TextUtils.isEmpty(query)) {
                result = null;
                videoList.clear();
                adapter.notifyDataSetChanged();
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
                //   }
                return true;
            }
            return false;
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rlCreate:
                Map<String, Object> map = new HashMap<>(requestMap);
                //  map.put(Constant.KEY_PAGE_ID, blogId);
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditPollFragment.newInstance(Constant.FormType.CREATE_POLL, map, URL_CREATE,selectedScreen)).addToBackStack(null).commit();
                break;
            case R.id.llSelect:
                showPopup(result.getSort(), v, 10, this);
                break;
        }
    }

    @Override
    public void updateUpperLayout() {
        if (null != result.getSort()) {
            v.findViewById(R.id.llSelect).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.llSelect).setVisibility(View.GONE);
        }

        if (result.canCreatePoll()) {
            v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.cvCreate).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Options opt = result.getSort().get(item.getItemId() - 11);
        ((TextView) v.findViewById(R.id.tvFilter)).setText(opt.getLabel());
        mSort = opt.getName();

        v.findViewById(R.id.tvPost).setVisibility(View.GONE);
        v.findViewById(R.id.tvFilter).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivDown).setVisibility(View.VISIBLE);

        ((TextView) v.findViewById(R.id.tvFilter)).setText(opt.getLabel());
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        return false;
    }
}
