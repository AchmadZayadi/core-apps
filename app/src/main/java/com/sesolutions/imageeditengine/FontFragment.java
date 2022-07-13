package com.sesolutions.imageeditengine;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.droidninja.imageeditengine.Constants;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class FontFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private FontAdapter adapter;
    private View pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etMusicSearch;
    public String query;

    private List<Font> wallpaperList;
    private OnUserClickedListener<Integer, Object> mListener;

    public static FontFragment newInstance(List<Font> wallpaperList) {
        FontFragment fragment = new FontFragment();
        fragment.wallpaperList = wallpaperList;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.layout_search_list_filter_offset, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        //((TextView)v.findViewById(R.id.tvTitle)).setText(R.string.wallpaper);
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        //  swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FontAdapter(wallpaperList, context, this);
            adapter.setGrid(true);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        //  callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        // callMusicAlbumApi(REQ_CODE);


        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(R.string.search_wallpaper);
        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(FontFragment.this).show(fragmentManager, "tts");
        });
        etMusicSearch.addTextChangedListener(new CustomTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // android.support.transition.TransitionManager.beginDelayedTransition(transitionsContainer);
                ivCancel.setVisibility(s != null && s.length() != 0 ? View.VISIBLE : View.GONE);
                ivMic.setVisibility(s != null && s.length() != 0 ? View.GONE : View.VISIBLE);
                adapter.getFilter().filter(s);
            }
        });

        etMusicSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard();
                adapter.getFilter().filter(etMusicSearch.getText());
                return true;
            }
            return false;
        });

    }


    private void updateAdapter(boolean hasData, String key) {
        // adapter.notifyDataSetChanged();
        // runLayoutAnimation(recyclerView);
        if (hasData) {
            v.findViewById(R.id.llNoData).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(getString(R.string.msg_no_font_with_name, key));
        }

    }

    public void onLoadMore() {
       /* try {
            if (!isLoading) {
                callMusicAlbumApi(REQ_LOAD_MORE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.LOAD_MORE:
                onLoadMore();
                break;
            case Constant.Events.MUSIC_MAIN:
                mListener.onItemClicked(Constants.Events.TASK, adapter.getList().get(postion).getQueryString(), Constants.TASK_FONT);
                onBackPressed();
                break;
            case Constant.Events.UPDATE_TOTAL:
                updateAdapter(postion > 0, "" + object2);
                break;

            case Constant.Events.TTS_POPUP_CLOSED:
                query = "" + object2;
                etMusicSearch.setText(query);
                // adapter.getFilter().filter(query);
                break;

        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserClickedListener) {
            mListener = (OnUserClickedListener<Integer, Object>) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onBackPressed() {
        activity.currentFragment = null;
        fragmentManager.popBackStack();
    }
}
