package com.sesolutions.ui.groups;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CreateProfileVideoForm;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.video.VideoAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupVideoFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private final int REQ_LIKE = -100;
    private final int REQ_FAVORITE = -200;
    private final int REQ_DELETE = -300;
    public RecyclerView recyclerView;
    public OnUserClickedListener<Integer, Object> listener;
    private boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public PageResponse.Result result;
    public ProgressBar pb;
    private VideoAdapter adapter;
    public List<Videos> videoList;

    private static final int REQ_WATCH_LATER = -1;
    private String selectedScreen;
    //private String url;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mObjectId;

    public static GroupVideoFragment newInstance(String selectedScreen, String resourceType, OnUserClickedListener<Integer, Object> parent) {
        GroupVideoFragment frag = new GroupVideoFragment();
        frag.selectedScreen = selectedScreen;
        frag.listener = parent;
        frag.resourceType = resourceType;
        return frag;
    }

    public static GroupVideoFragment newInstance(String selectedScreen, String resourceType, int mObjectId, OnUserClickedListener<Integer, Object> parent) {
        GroupVideoFragment frag = new GroupVideoFragment();
        frag.listener = parent;
        frag.resourceType = resourceType;
        frag.selectedScreen = selectedScreen;
        frag.mObjectId = mObjectId;
        return frag;
    }

    boolean istoolbar=false;


    public static GroupVideoFragment newInstance(String selectedScreen, String resourceType, int mObjectId, OnUserClickedListener<Integer, Object> parent, boolean istoolbar) {
        GroupVideoFragment frag = new GroupVideoFragment();
        frag.listener = parent;
        frag.resourceType = resourceType;
        frag.selectedScreen = selectedScreen;
        frag.mObjectId = mObjectId;
        frag.istoolbar = istoolbar;
        return frag;
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);
        applyTheme(v);

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.video);
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

    private String VIEW_VIDEO_RC_TYPE, URL, DELETE_URL, CREATE_URL, KEY_OBJECT_ID, EDIT_URL, WATCH_LATER_URL;

    private void setUpModuleData() {
        switch (resourceType) {

            case Constant.ResourceType.GROUP:
                VIEW_VIDEO_RC_TYPE = Constant.ResourceType.GROUP_VIDEO;
                DELETE_URL = Constant.URL_GROUP_VIDEO_DELETE;
                URL = mObjectId > 0 ? Constant.URL_GROUP_VIDEO_PROFILE : Constant.URL_GROUP_VIDEO_BROWSE;
                CREATE_URL = Constant.URL_GROUP_VIDEO_CREATE;
                KEY_OBJECT_ID = Constant.KEY_GROUP_ID;
                EDIT_URL = Constant.URL_GROUP_VIDEO_EDIT;
                WATCH_LATER_URL = Constant.URL_GROUP_VIDEO_WATCH_LATER;
                break;
            case Constant.ResourceType.BUSINESS:
                VIEW_VIDEO_RC_TYPE = Constant.ResourceType.BUSINESS_VIDEO;
                DELETE_URL = Constant.URL_BUSINESS_VIDEO_DELETE;
                URL = mObjectId > 0 ? Constant.URL_BUSINESS_VIDEO_PROFILE : Constant.URL_BUSINESS_VIDEO_BROWSE;
                CREATE_URL = Constant.URL_BUSINESS_VIDEO_CREATE;
                KEY_OBJECT_ID = Constant.KEY_BUSINESS_ID;
                EDIT_URL = Constant.URL_BUSINESS_VIDEO_EDIT;
                WATCH_LATER_URL = Constant.URL_BUSINESS_VIDEO_WATCH_LATER;
                break;
            case Constant.ResourceType.PRODUCT:
                VIEW_VIDEO_RC_TYPE = Constant.ResourceType.PRODUCT_VIDEO;
                DELETE_URL = Constant.URL_PRODUCT_VIDEO_DELETE;
                URL = mObjectId > 0 ? Constant.URL_PRODUCT_VIDEO_PROFILE : Constant.URL_PRODUCT_VIDEO_BROWSE;
                CREATE_URL = Constant.URL_PRODUCT_VIDEO_CREATE;
                KEY_OBJECT_ID = Constant.KEY_PRODUCT_ID;
                EDIT_URL = Constant.URL_PRODUCT_VIDEO_EDIT;
                WATCH_LATER_URL = Constant.URL_PRODUCT_VIDEO_WATCH_LATER;
                break;
            default:
                //default is page
                VIEW_VIDEO_RC_TYPE = Constant.ResourceType.PAGE_VIDEO;
                DELETE_URL = Constant.URL_PAGE_VIDEO_DELETE;
                URL = mObjectId > 0 ? Constant.URL_PAGE_VIDEO_PROFILE : Constant.URL_PAGE_VIDEO_BROWSE;
                CREATE_URL = Constant.URL_PAGE_VIDEO_CREATE;
                KEY_OBJECT_ID = Constant.KEY_PAGE_ID;
                EDIT_URL = Constant.URL_PAGE_VIDEO_EDIT;
                WATCH_LATER_URL = Constant.URL_PAGE_VIDEO_WATCH_LATER;
                break;
        }
    }

    private void showHideUpperLayout() {
        /*if (null != result.getPostButton()) {
            v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getPostButton().getLabel());
            v.findViewById(R.id.cvCreate).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvCreate).setVisibility(View.GONE);
        }*/

        if (null != result.getSort()) {
            v.findViewById(R.id.llSelect).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.llSelect).setVisibility(View.GONE);
        }
        if (null != result.getButton()) {
            v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getButton().getLabel());

        } else {
            v.findViewById(R.id.cvCreate).setVisibility(View.GONE);
        }
    }

    private String mSort;
    private EditText etMusicSearch;
    private String query;

    public void init() {

        setUpModuleData();

        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

        v.findViewById(R.id.llSelect).setOnClickListener(this);
        v.findViewById(R.id.rlCreate).setOnClickListener(this);

        etMusicSearch = v.findViewById(R.id.etMusicSearch);

        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
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
                query = etMusicSearch.getText().toString();
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

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new VideoAdapter(videoList, context, this, this, Constant.FormType.TYPE_MUSIC_ALBUM);
            adapter.setEvent(true);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);

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
        try {
            switch (v.getId()) {
                case R.id.rlCreate:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_PARENT_ID, mObjectId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateProfileVideoForm.newInstance(Constant.FormType.CREATE_PAGE_VIDEO, map, CREATE_URL)).addToBackStack(null).commit();
                    break;

                case R.id.llSelect:
                    showPopup(result.getSort(), v, 10, this);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL);
                /* if (null != map) {
                    request.params.putAll(map);
                }*/
                Map<String, Object> map1 = activity.filteredMap;
                if (null != map1) {
                    request.params.putAll(map1);
                }

                if (!TextUtils.isEmpty(mSort)) {
                    request.params.put("sort", mSort);
                }
                if (!TextUtils.isEmpty(query)) {
                    request.params.put(Constant.KEY_SEARCH, query);
                }

                if (mObjectId > 0) {
                    request.params.put(KEY_OBJECT_ID, mObjectId);
                }
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;
                        hideAllLoaders();

                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                if (null != listener)
                                    listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
                                PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                if (req == Constant.REQ_CODE_REFRESH) {
                                    videoList.clear();
                                }
                                wasListEmpty = videoList.size() == 0;
                                result = resp.getResult();
                                videoList.addAll(result.getVideos());
                                showHideUpperLayout();
                                updateAdapter();
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        hideAllLoaders();
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideAllLoaders();
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        swipeRefreshLayout.setRefreshing(false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        if (listener != null) {
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_VIDEO);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);

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

    public void showDeleteDialog(final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_VIDEO);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();

                if (isNetworkAvailable(context)) {

                    Map<String, Object> request = new HashMap<>();
                    request.put(Constant.KEY_VIDEO_ID, videoList.get(position).getVideoId());
                    new ApiController(DELETE_URL, request, context, this, REQ_DELETE).execute();
                    videoList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    notInternetMsg(v);
                }

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                goTo(Constant.GoTo.VIDEO, videoList.get(postion).getVideoId(), VIEW_VIDEO_RC_TYPE);
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                //get clicked option
                Options opt = videoList.get(Integer.parseInt("" + object2)).getOptions().get(postion);
                if (opt.getName().equals(Constant.OptionType.DELETE)) {
                    //handle delete video
                    showDeleteDialog(Integer.parseInt("" + object2));
                } else {
                    //handle edit video
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_VIDEO_ID, videoList.get(postion).getVideoId());
                    openFormFragment(Constant.FormType.KEY_EDIT_VIDEO, map, EDIT_URL);
                }
                break;

            case Constant.Events.WATCH_LATER:
                if (isNetworkAvailable(context)) {
                    videoList.get(postion).toggleWatchLaterId();
                    adapter.notifyItemChanged(postion);
                    Map<String, Object> request = new HashMap<>();
                    request.put(Constant.KEY_VIDEO_ID, videoList.get(postion).getVideoId());
                    new ApiController(WATCH_LATER_URL, request, context, this, REQ_WATCH_LATER).execute();
                } else {
                    notInternetMsg(v);
                }
                break;

            //case for watch later api response
            case REQ_WATCH_LATER:
                try {
                    String response = (String) object2;
                    CustomLog.e("repsonse1", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (!TextUtils.isEmpty(err.getError())) {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    }

                } catch (Exception e) {
                    hideBaseLoader();
                    CustomLog.e(e);
                }
                break;
            case Constant.Events.MUSIC_FAVORITE:
                if (isNetworkAvailable(context)) {
                    videoList.get(postion).toggleFavorite();
                    adapter.notifyItemChanged(postion);
                    Map<String, Object> params = new HashMap<>();
                    params.put(Constant.KEY_RESOURCE_ID, videoList.get(postion).getVideoId());
                    params.put(Constant.KEY_RESOURCES_TYPE, VIEW_VIDEO_RC_TYPE);
                    new ApiController(Constant.URL_MUSIC_FAVORITE, params, context, this, REQ_FAVORITE).setExtraKey(postion).execute();
                } else {
                    notInternetMsg(v);
                }
                break;
            case Constant.Events.MUSIC_LIKE:
                if (isNetworkAvailable(context)) {
                    videoList.get(postion).toggleLike();
                    adapter.notifyItemChanged(postion);
                    Map<String, Object> params = new HashMap<>();
                    params.put(Constant.KEY_RESOURCE_ID, videoList.get(postion).getVideoId());
                    params.put(Constant.KEY_RESOURCES_TYPE, VIEW_VIDEO_RC_TYPE);
                    new ApiController(Constant.URL_MUSIC_LIKE/*Constant.URL_PAGE_VIDEO_LIKE*/, params, context, this, REQ_LIKE).setExtraKey(postion).execute();
                } else {
                    notInternetMsg(v);
                }
                break;
            case REQ_DELETE:
            case REQ_FAVORITE:
            case REQ_LIKE:
                try {
                    ErrorResponse err = new Gson().fromJson("" + object2, ErrorResponse.class);
                    if (!err.isSuccess()) {
                        if (object1 == REQ_FAVORITE) {
                            videoList.get(postion).toggleFavorite();
                            adapter.notifyItemChanged(postion);
                        } else if (object1 == REQ_LIKE) {
                            videoList.get(postion).toggleLike();
                            adapter.notifyItemChanged(postion);
                        } else {
                            onRefresh();
                        }

                        Util.showSnackbar(v, err.getErrorMessage());
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                }
                break;
        }
        return super.onItemClicked(object1, object2, postion);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Options opt = result.getSort().get(item.getItemId() - 11);
        ((TextView) v.findViewById(R.id.tvFilter)).setText(opt.getLabel());
        mSort = opt.getName();

        v.findViewById(R.id.tvPost).setVisibility(View.GONE);
        v.findViewById(R.id.tvFilter).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivDown).setVisibility(View.VISIBLE);

        //((TextView) v.findViewById(R.id.tvFilter)).setText(opt.getLabel());
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        return false;
    }

   /* private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
