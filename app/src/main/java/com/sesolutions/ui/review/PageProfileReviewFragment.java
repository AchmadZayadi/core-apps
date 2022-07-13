package com.sesolutions.ui.review;


import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Review;
import com.sesolutions.responses.ReviewResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.events.ReviewCreateForm;
import com.sesolutions.ui.page.PageReviewViewFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageProfileReviewFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public View v;
    public String searchKey;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ReviewResponse.Result result;
    private ProgressBar pb;
    public List<Review> reviewList;
    public PageReviewAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public boolean isEdit;
    private Map<String, Object> map;
    //private String url;
    //private int mObjectId;
    public String selectedScreen;
    private EditText etMusicSearch;
    private OnUserClickedListener<Integer, Object> listener;
    private Boolean showsearch = true;
    private boolean showToolbar=false;

    public static PageProfileReviewFragment newInstance(Boolean showToolbar,String selectedScreen, OnUserClickedListener<Integer, Object> listener, Map<String, Object> map) {
        PageProfileReviewFragment frag = new PageProfileReviewFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        frag.map = map;
        frag.showToolbar = showToolbar;
        return frag;
    }

    public static PageProfileReviewFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> listener, Map<String, Object> map) {
        PageProfileReviewFragment frag = new PageProfileReviewFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        frag.map = map;
        return frag;
    }


    public static PageProfileReviewFragment newInstance(String selectedScreen, OnUserClickedListener<Integer, Object> listener, Map<String, Object> map, Boolean showSearch) {
        PageProfileReviewFragment frag = new PageProfileReviewFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = listener;
        frag.map = map;
        frag.showsearch = showSearch;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);
        applyTheme(v);

        if (!showToolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.reviewtext);
            initScreenData();
        }


        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

        //v.findViewById(R.id.llSelect).setOnClickListener(this);
        v.findViewById(R.id.cvCreate).setOnClickListener(this);

        etMusicSearch = v.findViewById(R.id.etMusicSearch);

        if (showsearch) {
            v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.rlCommentEdittext).setVisibility(View.INVISIBLE);
        }

        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(this).show(fragmentManager, "tts");
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
                reviewList.clear();
                adapter.notifyDataSetChanged();
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
                //   }
                return true;
            }
            return false;
        });
    }

    public void setRecyclerView() {
        try {
            reviewList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PageReviewAdapter(reviewList, context, this, this, resourceType);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //@Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.cvCreate:
                    String url = null;
                    int reviewId = result.getButton().getprofreview_id();
                    int SreviewId = result.getButton().getreview_id();
                    if (result.getButton().getName().equalsIgnoreCase("edit") || result.getButton().getName().equalsIgnoreCase("updatereview")) {
                        CustomLog.e("review_id_user:", "" + reviewId);
                        CustomLog.e("review_id_user:", "" + SreviewId);
                        isEdit = true;
                    } else {
                        isEdit = false;
                    }
                    int REQ = isEdit ? Constant.FormType.EDIT_REVIEW : Constant.FormType.CREATE_REVIEW;
                    Map<String, Object> map1 = new HashMap<>(map);
                    if (isEdit && resourceType == Constant.ResourceType.PROFESSIONAL_REVIEW) {
                        map1.put(Constant.KEY_REVIEW_ID, reviewId);
                    } else if (isEdit && resourceType == Constant.ResourceType.SERVICE_REVIEW) {
                        map1.put(Constant.KEY_REVIEW_ID, SreviewId);
                    } else if (isEdit) {
                        map1.put(Constant.KEY_REVIEW_ID, mObjectId);
                    }

                    switch (resourceType) {
                        case Constant.ResourceType.PAGE_REVIEW:
                            url = isEdit ? Constant.URL_PAGE_REVIEW_EDIT : Constant.URL_PAGE_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.SERVICE_REVIEW:
                            url = isEdit ? Constant.URL_SERVICE_REVIEW_EDIT : Constant.URL_SERVICE_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.PROFESSIONAL_REVIEW:
                            url = isEdit ? Constant.URL_PROFESSIONAL_REVIEW_EDIT : Constant.URL_PROFESSIONAL_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.COURSE_REVIEW:
                            url = isEdit ? Constant.URL_COURSE_EDIT_REVIEW : Constant.URL_COURSE_CREATE_REVIEW;
                            break;
                        case Constant.ResourceType.CLASSROOM_REVIEW:
                            url = isEdit ? Constant.URL_CLASSROOM_EDIT_REVIEW : Constant.URL_CLASSROOM_CREATE_REVIEW;
                            break;
                        case Constant.ResourceType.GROUP_REVIEW:
                            url = isEdit ? Constant.URL_GROUP_REVIEW_EDIT : Constant.URL_GROUP_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.BUSINESS_REVIEW:
                            url = isEdit ? Constant.URL_BUSINESS_REVIEW_EDIT : Constant.URL_BUSINESS_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.STORE_REVIEW:
                            url = isEdit ? Constant.URL_STORE_REVIEW_EDIT : Constant.URL_STORE_REVIEW_CREATE;
                            break;
                        case Constant.ResourceType.PRODUCT_REVIEW:
                            url = isEdit ? Constant.URL_PRODUCT_REVIEW_EDIT : Constant.URL_PRODUCT_REVIEW_CREATE;
                            break;
                    }


                    fragmentManager.beginTransaction().replace(R.id.container, ReviewCreateForm.newInstance(REQ, map1, url)).addToBackStack(null).commit();
                    //fragmentManager.beginTransaction().replace(R.id.container, ReviewCreateForm.newInstance(Constant.FormType.CREATE_REVIEW, map, URL_CREATE)).addToBackStack(null).commit();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        getMapValues();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private void updateUpperLayout() {

//        if (!resourceType.equals(Constant.ResourceType.STORE_REVIEW)) {
        v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
       /* if (null != result.getButton()) {
            v.findViewById(R.id.llSelect).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.llSelect).setVisibility(View.GONE);
        }*/
        v.findViewById(R.id.cvSelect).setVisibility(View.GONE);
        if (null != result.getButton()) {
            v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getButton().getLabel());
        } else {
            v.findViewById(R.id.cvCreate).setVisibility(View.GONE);
            v.findViewById(R.id.rlFilter).setVisibility(View.GONE);
        }
//        }

    }

    private String URL, resourceType, URL_CREATE;
    private int mObjectId;

    private void getMapValues() {
        try {

            resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
            switch (resourceType) {
                case Constant.ResourceType.PAGE_REVIEW:
                    URL = Constant.URL_PAGE_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_PAGE_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_PAGE_ID);
                    break;
                case Constant.ResourceType.CLASSROOM_REVIEW:
                    URL = Constant.URL_CLASSROOM_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_CLASSROOM_CREATE_REVIEW;
                    mObjectId = (int) map.get(Constant.KEY_CLASSROOM_ID);
                    break;
                case Constant.ResourceType.SERVICE_REVIEW:
                    URL = Constant.URL_SERVICE_REVIEW_BROWSE;
                    URL_CREATE = Constant.URL_SERVICE_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_SERVICE_ID);
                    break;
                case Constant.ResourceType.PROFESSIONAL_REVIEW:
                    URL = Constant.URL_PROFESSIONAL_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_PROFESSIONAL_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_PROFESSIONAL_ID);
                    break;
                case Constant.ResourceType.COURSE_REVIEW:
                    URL = Constant.URL_COURSE_PROFILE_REVIEW;
                    URL_CREATE = Constant.URL_COURSE_CREATE_REVIEW;
                    mObjectId = (int) map.get(Constant.KEY_COURSE_ID);
                    break;
                case Constant.ResourceType.PRODUCT_REVIEW:
                    URL = Constant.URL_PRODUCT_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_PRODUCT_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_PRODUCT_ID);
                    break;
                case Constant.ResourceType.STORE_REVIEW:
                    URL = Constant.URL_STORE_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_STORE_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_STORE_ID);
                    break;
                case Constant.ResourceType.BUSINESS_REVIEW:
                    URL = Constant.URL_BUSINESS_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_BUSINESS_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_BUSINESS_ID);
                    break;
                case Constant.ResourceType.GROUP_REVIEW:
                    URL = Constant.URL_GROUP_REVIEW_PROFILE;
                    URL_CREATE = Constant.URL_GROUP_REVIEW_CREATE;
                    mObjectId = (int) map.get(Constant.KEY_BUSINESS_ID);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                try {

                    HttpRequestVO request = new HttpRequestVO(URL);

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.putAll(map);

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH_TEXT, searchKey);
                    }
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            hideAllLoaders();

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        reviewList.clear();
                                    }
                                    wasListEmpty = reviewList.size() == 0;
                                    ReviewResponse resp = new Gson().fromJson(response, ReviewResponse.class);
                                    result = resp.getResult();
                                    if (null != result.getReviews())
                                        reviewList.addAll(result.getReviews());
                                    updateUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }

                        return true;
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
        //hideView(v.findViewById(R.id.pbMain));
        pb.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateAdapter() {

        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_review);
        v.findViewById(R.id.llNoData).setVisibility(reviewList.size() > 0 ? View.GONE : View.VISIBLE);

        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }

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
        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                    goToProfileFragment(reviewList.get(postion).getOwnerId());
                    return false;
                case Constant.Events.MENU_MAIN:
                    if (resourceType.equalsIgnoreCase("professional_review")) {
                        int id = reviewList.get(postion).getContent(resourceType).getId();
                        if (mObjectId != reviewList.get(postion).getContent(resourceType).getId()) {
                            goToViewReviewFragment(id);
                        }
                    } else {
                        int id = reviewList.get(postion).getContent(resourceType).getId();
                        if (mObjectId != reviewList.get(postion).getContent(resourceType).getId()) {
                            goToViewReviewFragment(id);
                        }
                    }
                    return false;
                case Constant.Events.MUSIC_MAIN:
                    if (resourceType.equalsIgnoreCase("professional_review")) {
                        goToViewReviewFragment(reviewList.get(postion).getProreviewId());
                    } else {
                        goToViewReviewFragment(reviewList.get(postion).getReviewId());

                    }
                    return false;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void goToViewReviewFragment(int reviewId) {
        fragmentManager.beginTransaction().replace(R.id.container, PageReviewViewFragment.newInstance(resourceType, reviewId)).addToBackStack(null).commit();
    }


}
