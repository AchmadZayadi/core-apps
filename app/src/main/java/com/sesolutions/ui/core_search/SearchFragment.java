package com.sesolutions.ui.core_search;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener {

    private AppCompatEditText etMusicSearch;
    private View v;
    private View ivMic;
    private View ivCancel;
    private int REQ_LOAD_MORE = 2;
    private ProgressBar pb;
    RelativeLayout llsearchbg;
    private CommonResponse.Result result;
    private List<SearchVo> searchList;
    private SearchCoreAdapter adapter;
    private RecyclerView recyclerView;
    private String query;
    private boolean isLoading;
    private ViewGroup transitionsContainer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                query = "";
                activity.isBackFrom = 0;
                searchList.clear();
                final Object value = activity.filteredMap.get(Constant.KEY_QUERY);
                if (null != value) {
                    query = value.toString();
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
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));

      try {
            new ThemeManager().applyTheme((ViewGroup) v, context);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        init();
        setRecyclerView();
      /*  childFm.beginTransaction().replace(R.id.container_search, new SearchAlbumFragment())
                .commit();*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openKeyboard();
                etMusicSearch.requestFocus();
            }
        }, 100);
        return v;
    }

    public void init() {
        pb = v.findViewById(R.id.pb);
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        llsearchbg = v.findViewById(R.id.llsearchbg);
        ivCancel = v.findViewById(R.id.ivCancel);
        ivMic = v.findViewById(R.id.ivMic);
        etMusicSearch.setHint(getStrings(R.string.TITLE_SEARCH));
        etMusicSearch.setTextColor(Color.parseColor(Constant.backgroundColor));
        llsearchbg.setBackgroundColor(Color.parseColor(Constant.text_color_1));
        recyclerView = v.findViewById(R.id.recyclerview);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivFilter).setOnClickListener(this);
        transitionsContainer = (ViewGroup) v.findViewById(R.id.llOption);
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
                    query = etMusicSearch.getText().toString();
                    if (!TextUtils.isEmpty(query)) {
                        result = null;
                        searchList.clear();
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
                case R.id.ivCancel:
                    ivCancel.setVisibility(View.GONE);
                    etMusicSearch.setText("");
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


    private void setRecyclerView() {
        try {
            searchList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new SearchCoreAdapter(searchList, context, this, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToMusicSearchForm() {
        Map<String, Object> map = new HashMap<>();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_CORE, map, Constant.URL_CORE_SEARCH_FORM))
                .addToBackStack(null)
                .commit();
    }

    public void callMusicAlbumApi(final int REQ) {

        if (isNetworkAvailable(context)) {
            try {
                isLoading = true;
                if (REQ == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CORE_SEARCH);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                if (!TextUtils.isEmpty(query)) {
                    request.params.put(Constant.KEY_QUERY, query);
                }
                Map<String, Object> map = activity.filteredMap;
                if (null != map)
                    request.params.putAll(map);
                request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    result = resp.getResult();
                                    wasListEmpty = searchList.size() == 0;
                                    if (null != result.getSearch()) {
                                        searchList.addAll(result.getSearch());

                                    }
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            pb.setVisibility(View.GONE);
                            isLoading = false;
                            CustomLog.e(e);
                        }
                        return false;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
                isLoading = false;
                pb.setVisibility(View.GONE);


            }

        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SEARCH_ITEM);
        v.findViewById(R.id.llNoData).setVisibility(searchList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                SearchVo vo = searchList.get(postion);
                performClick(vo.getType(),
                        vo.getId(),
                        vo.getHref(),
                        false);
                break;
            case Constant.Events.TTS_POPUP_CLOSED:
                query = "" + object2;
                etMusicSearch.setText(query);
                result = null;
                searchList.clear();
                callMusicAlbumApi(1);
                break;
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
