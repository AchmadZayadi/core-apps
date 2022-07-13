package com.sesolutions.ui.storyview;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.Transaction;
import com.sesolutions.responses.story.Result;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArchiveFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private final int REQ_HIGHLIGHT = 670;
    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result result;
    private List<StoryContent> categoryList;
    private ArchiveAdapter adapter;
    private View pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivHighlight;
    private boolean isHighlighting = false;

   /* public static ArchiveFragment newInstance(String rcType) {
        ArchiveFragment fragment = new ArchiveFragment();
        fragment.rcType = rcType;
        return fragment;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(SesColorUtils.getPrimaryDarkColor(context));
        super.onStop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_archive, container, false);
        applyTheme(v);
        setBlackTheme();
        initScreenData();
        return v;
    }

    private void setBlackTheme() {
        v.findViewById(R.id.toolbar).setBackgroundColor(Color.BLACK);
        v.findViewById(R.id.swipeRefreshLayout).setBackgroundColor(Color.BLACK);
    }


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.title_story_archive);
        recyclerView = v.findViewById(R.id.recyclerView);
        ivHighlight = v.findViewById(R.id.ivShare);
        ivHighlight.setOnClickListener(this);
        v.findViewById(R.id.fab).setOnClickListener(this);
        updateHighlightIcon();
        ivHighlight.setVisibility(View.VISIBLE);
        ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.setting));
        v.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void updateHighlightIcon() {
        if (!isHighlighting) {
            ivHighlight.setBackground(null);
            ivHighlight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rating_star_filled));
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        } else {
            ivHighlight.setBackground(ContextCompat.getDrawable(context, R.drawable.fab_circle_shadow_mini));
            ivHighlight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rating_star_unfilled));
            v.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        try {
            categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT + 1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ArchiveAdapter(categoryList, context, this);
            //this will disable blink effect happening on  on_Item_change
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private BottomSheetBehavior<View> mBottomSheetOptions;
    private View llBottomSheet;

    private void setUpBottomSheet() {

        llBottomSheet = v.findViewById(R.id.llBottomSheet);
        //  setViewerRecyclerView();
        llBottomSheet.findViewById(R.id.ll1).setOnClickListener(this);
        llBottomSheet.findViewById(R.id.ll2).setOnClickListener(this);
        // updateRefreshIcon(false);
        mBottomSheetOptions = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetOptions.setPeekHeight(context.getResources().getDimensionPixelSize(R.dimen.height_my_story_bottom_sheet));
        // mBottomSheetOptions.setBottomSheetCallback(bottomSheetListener);
        mBottomSheetOptions.setHideable(true);
        mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showUpdateDialog(Transaction data) {

        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_transaction);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivShare:
                isHighlighting = !isHighlighting;
                if (!isHighlighting) {
                    //if user won't submit selection then revert it back
                    revertChanges();
                }
                adapter.setHighlighting(isHighlighting);
                updateHighlightIcon();
                adapter.notifyDataSetChanged();
                break;
            case R.id.ivSearch:
                if (isHighlighting) return;

                if (mBottomSheetOptions.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

                break;
            case R.id.ll1:
                mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
                Map<String, Object> map = new HashMap<>();
                map.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
                openFormFragment(Constant.FormType.STORY_ARCHIVE, map, Constant.URL_STORY_SETTING);
                break;
            case R.id.ll2:
                mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
                fragmentManager.beginTransaction().replace(R.id.container, new MutedMemberFragment()).addToBackStack(null).commit();
                break;
            case R.id.fab:
                callHighlightAPI();
                break;
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        setUpBottomSheet();
        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }

                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_STORY_BROWSE);
                    request.params.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
                    request.params.put("userarchivedstories", 1);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                hideAllLoaders();

                                CustomLog.e("repsonse_archived_story", "" + response);
                                if (response != null) {
                                    StoryResponse resp = new Gson().fromJson(response, StoryResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {

                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }
                                        wasListEmpty = categoryList.size() == 0;
                                        result = resp.getResult();
                                        if (null != result.getStories()) {
                                            categoryList.addAll(result.getStories().get(0).getImages());
                                            saveHighlightedItem(result.getStories().get(0).getImages());
                                        }
                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, resp.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideAllLoaders();
                }

            } else {
                hideAllLoaders();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }


    private void hideAllLoaders() {
        isLoading = false;
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
        swipeRefreshLayout.setRefreshing(false);
    }


    private void updateAdapter() {
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_archive_description);
        v.findViewById(R.id.llNoData).setVisibility(categoryList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    Map<Integer, Integer> highlightMap = new HashMap<>();
    //Map<Integer, Integer> changedMap = new HashMap<>();
    List<String> changedMap = new ArrayList<>();

    private void saveHighlightedItem(List<StoryContent> list) {
        for (StoryContent vo : list) {
            if (vo.isHighlighted())
                highlightMap.put(vo.getStoryId(), vo.getStoryId());
        }
    }

   /* private void unHighlightStory(int id) {
        changedMap.remove("" + id);
    }*/

    private void highlightStory(int id) {
        if (changedMap.contains("" + id)) {
            changedMap.remove("" + id);
        } else {
            changedMap.add("" + id);
        }
    }

    private void revertChanges() {
        for (StoryContent vo : categoryList) {
            // vo.setUpdated(false);
            vo.setHighlight(highlightMap.containsKey(vo.getStoryId()) ? 1 : 0);
        }
        changedMap.clear();
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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {

            case Constant.Events.LOAD_MORE:
                onLoadMore();
                break;

            case Constant.Events.STORY_ARCHIVE:
                if (isHighlighting) return false;
                StoryModel model = result.getStories().get(0);
                StoryModel model2 = new StoryModel(model.getUserImage(), model.getUsername(), model.getUserId());
                List<StoryContent> list = new ArrayList<StoryContent>();
                list.add(categoryList.get(postion));
                model2.setImages(list);
                fragmentManager.beginTransaction().replace(R.id.container, MyStory.newInstance(model2, true)).addToBackStack(null).commit();
                break;
            case Constant.Events.USER_SELECT:
                categoryList.get(postion).toggleHighlight();
                highlightStory(categoryList.get(postion).getStoryId());
                adapter.notifyItemChanged(postion);
                break;
        }
        return false;
    }

    private void callHighlightAPI() {
        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < changedMap.size(); i++) {
                map.put("story_id[" + i + "]", changedMap.get(i));
            }
            changedMap.clear();

            new ApiController(Constant.URL_STORY_HIGHLIGHT, map, context, this, REQ_HIGHLIGHT).execute();
            isHighlighting = !isHighlighting;
            updateHighlightIcon();
            adapter.setHighlighting(isHighlighting);
            adapter.notifyDataSetChanged();
        } else {
            notInternetMsg(v);
        }
    }
}
