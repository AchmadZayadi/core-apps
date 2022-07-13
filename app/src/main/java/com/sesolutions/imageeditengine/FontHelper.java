package com.sesolutions.imageeditengine;


import android.app.Activity;
import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.droidninja.imageeditengine.Constants;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class FontHelper implements OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private FontAdapter adapter;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etMusicSearch;
    public String query;

    private List<Font> wallpaperList;
    private OnUserClickedListener<Integer, Object> mListener;


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerView);
        //swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        //swipeRefreshLayout.setEnabled(false);
        //  swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FontAdapter(wallpaperList, v.getContext(), this);
            adapter.setGrid(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        //  callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    public void initScreenData(View view, List<Font> wallpaperList, OnUserClickedListener<Integer, Object> mListener) {
        this.v = view;
        this.wallpaperList = wallpaperList;
        this.mListener = mListener;
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
            TTSDialogFragment.newInstance(FontHelper.this).show(((BaseActivity) v.getContext()).getSupportFragmentManager(), "tts");
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

    public void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = ((Activity) v.getContext()).getCurrentFocus();
            if (view == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateAdapter(boolean hasData, String key) {
        // adapter.notifyDataSetChanged();
        // runLayoutAnimation(recyclerView);
        if (hasData) {
            v.findViewById(R.id.llNoData).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(v.getContext().getString(R.string.msg_no_font_with_name, key));
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
            case Constants.Events.FONT:
                mListener.onItemClicked(object1, object2, postion);
                mListener.onItemClicked(Constants.Events.HIDE_BOTTOM_SHEET, null, Constants.TASK_FONT);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                mListener.onItemClicked(Constants.Events.HIDE_BOTTOM_SHEET, null, Constants.TASK_FONT);
                break;
        }
    }
}
