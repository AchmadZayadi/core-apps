package com.sesolutions.ui.page;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.ui.business.CreateEditBusinessFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.contest.CreateEditContestFragment;
import com.sesolutions.ui.groups.CreateEditGroupFragment;
import com.sesolutions.ui.video.CategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCategoriesFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String> {

    public View v;
    // public AlbumParentFragment parent;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    private List<Category> categoryList;
    private CategoryAdapter adapter;
    private Map<String, Object> map;
    private String rcType;
    //private SwipeRefreshLayout swipeRefreshLayout;

    public static SelectCategoriesFragment newInstance(List<Category> categoryList, Map<String, Object> map, String rcType) {
        SelectCategoriesFragment frag = new SelectCategoriesFragment();
        frag.categoryList = categoryList;
        frag.map = map;
        frag.rcType = rcType;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_select_category, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_PAGE
                || activity.taskPerformed == Constant.FormType.CREATE_CONTEST
                || activity.taskPerformed == Constant.FormType.CREATE_GROUP
                ) {
            onBackPressed();
        }
    }

    private void init() {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.select_category);
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void setRecyclerView() {
        try {
            //  categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CategoryAdapter(categoryList, context, this, this, Constant.FormType.CREATE_PAGE);
            recyclerView.setAdapter(adapter);
            // swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
//                }
//            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        //  callMusicAlbumApi(1);

    }

   /* private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_PAGE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
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
                                hideLoaders();

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }
                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult();
                                        categoryList.addAll(result.getCategory());

                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
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
                    hideLoaders();
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                hideLoaders();

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }*/

    public void hideLoaders() {
        isLoading = false;
        // setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

   /* private void updateAdapter() {
        try {
            adapter.setPermission(result.getPermission());
            hideLoaders();
            pb.setVisibility(View.GONE);
            //  swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_CATEGORIES);
            v.findViewById(R.id.llNoData).setVisibility(categoryList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    @Override
    public void onLoadMore() {
       /* try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                if (Constant.ResourceType.PAGE.equals(rcType)) {
                    openPageCreateFragment(postion);
                } else if (Constant.ResourceType.CONTEST.equals(rcType)) {
                    openContestCreateFragment(postion);
                } else if (Constant.ResourceType.GROUP.equals(rcType)) {
                    openGroupCreateFragment(postion);
                } else if (Constant.ResourceType.BUSINESS.equals(rcType)) {
                    openBusinessCreateFragment(postion);
                }
                break;

        }
        return false;
    }

    private void openPageCreateFragment(int postion) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(Constant.KEY_CATEGORY_ID, categoryList.get(postion).getCategoryId());
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditPageFragment.newInstance(Constant.FormType.CREATE_PAGE, map, Constant.URL_PAGE_CREATE, null))
                .addToBackStack(null)
                .commit();
    }

    private void openContestCreateFragment(int postion) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(Constant.KEY_CATEGORY_ID, categoryList.get(postion).getCategoryId());
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditContestFragment.newInstance(Constant.FormType.CREATE_CONTEST, map, Constant.URL_CONTEST_CREATE, null))
                .addToBackStack(null)
                .commit();
    }

    private void openGroupCreateFragment(int postion) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(Constant.KEY_CATEGORY_ID, categoryList.get(postion).getCategoryId());
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditGroupFragment.newInstance(Constant.FormType.CREATE_GROUP, map, Constant.URL_GROUP_CREATE, null))
                .addToBackStack(null)
                .commit();
    }

    private void openBusinessCreateFragment(int postion) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(Constant.KEY_CATEGORY_ID, categoryList.get(postion).getCategoryId());
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditBusinessFragment.newInstance(Constant.FormType.CREATE_BUSINESS, map, Constant.URL_BUSINESS_CREATE, null))
                .addToBackStack(null)
                .commit();
    }
}
