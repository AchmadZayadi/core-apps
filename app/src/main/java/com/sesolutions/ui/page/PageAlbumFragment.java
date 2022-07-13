package com.sesolutions.ui.page;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.sesolutions.responses.album.AlbumResponse;
import com.sesolutions.responses.album.Result;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.albums.AlbumAdapter;
import com.sesolutions.ui.albums.AlbumHelper;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageAlbumFragment extends AlbumHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    //public String searchKey;
    public Result result;
    public int loggedinId;
    public int mObjectId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public OnUserClickedListener<Integer, Object> parent;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public Map<String, Object> map;
    private String url;
    private String selectedScreen;
    private boolean isRefreshEnabled = false;
    private String mSort;
    private EditText etMusicSearch;
    public String query;
    public boolean istoolbar;


    public static PageAlbumFragment newInstance(OnUserClickedListener<Integer,Object> parent, int loggedInId, int categoryId) {
        PageAlbumFragment frag = new PageAlbumFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.mObjectId = categoryId;
        return frag;
    }

    public static PageAlbumFragment newInstance(OnUserClickedListener<Integer,Object> parent, Map<String, Object> map) {
        PageAlbumFragment frag = newInstance(parent, 0, -1);
        frag.map = map;
        return frag;
    }


    public static PageAlbumFragment newInstance(Map<String, Object> map,boolean istoolbar) {
        PageAlbumFragment frag = newInstance(null, 0, -1);
        frag.map = map;
        frag.istoolbar = istoolbar;
        return frag;
    }


    public static PageAlbumFragment newInstance(Map<String, Object> map,Boolean flagdata) {
        PageAlbumFragment frag = newInstance(null, 0, -1);
        frag.map = map;
        return frag;
    }

    public static PageAlbumFragment newInstance(Map<String, Object> map) {
        PageAlbumFragment frag = newInstance(null, 0, -1);
        frag.map = map;
        return frag;
    }

    public static PageAlbumFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_album, container, false);
        txtNoData = R.string.MSG_NO_ALBUM_CREATED;
        applyTheme();

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.albums);
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

    public void init() {
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
            TTSDialogFragment.newInstance(PageAlbumFragment.this).show(fragmentManager, "tts");
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
            adapter = new AlbumAdapter(videoList, context, this, this);

            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setEnabled(isRefreshEnabled);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    //override this method and send to viewPageAlbum fragment
    @Override
    public void goToViewAlbumFragment(Object view, int position) {


        AlbumAdapter.ContactHolder holder = (AlbumAdapter.ContactHolder) view;
        Map<String, Object> map1 = new HashMap<>(map);
        map1.put(Constant.KEY_ALBUM_ID, videoList.get(position).getAlbumId());
        String rcType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
        if (Constant.ResourceType.PAGE.equals(rcType)) {
            map1.put(Constant.KEY_PAGE_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_URI, Constant.URL_PAGE_ALBUM_VIEW);
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE_ALBUM);

        } else if (Constant.ResourceType.SES_EVENT.equals(rcType)) {// map1.put(Constant.KEY_EVENT_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT_ALBUM);
            map1.put(Constant.KEY_URI, Constant.URL_EVENT_ALBUM_VIEW);

        } else if (Constant.ResourceType.GROUP.equals(rcType)) {// map1.put(Constant.KEY_EVENT_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP_ALBUM);
            map1.put(Constant.KEY_URI, Constant.URL_GROUP_ALBUM_VIEW);

        } else if (Constant.ResourceType.BUSINESS.equals(rcType)) {// map1.put(Constant.KEY_EVENT_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS_ALBUM);
            map1.put(Constant.KEY_URI, Constant.URL_BUSINESS_ALBUM_VIEW);

        }else if (Constant.ResourceType.CLASSROOM.equals(rcType)) {// map1.put(Constant.KEY_EVENT_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CLASSROOM);
            map1.put(Constant.KEY_URI, Constant.URL_CLASSROOM_ALBUMVIEW);

        } else if (Constant.ResourceType.PRODUCT.equals(rcType)) {// map1.put(Constant.KEY_EVENT_ID, videoList.get(position).getPageId());
            map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_ALBUM);
            map1.put(Constant.KEY_URI, Constant.URL_BUSINESS_ALBUM_VIEW);

        }
        try {

            String transitionName = videoList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
            ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, videoList.get(position).getImages().getMain());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewPageAlbumFragment.newInstance(map1, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , ViewPageAlbumFragment.newInstance(map1, null))
                    .addToBackStack(null)
                    .commit();
        }

    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.rlCreate:
                    String url = "";
                    Map<String, Object> map = new HashMap<>();
                    /*case Constant.ResourceType.PAGE:
                            map.put(Constant.KEY_PAGE_ID, mObjectId);
                            url
                            break;*/
                    if (Constant.ResourceType.SES_EVENT.equals(resourceType)) {
                        map.put(Constant.KEY_EVENT_ID, mObjectId);
                        url = Constant.URL_EVENT_ALBUM_CREATE;

                    } else if (Constant.ResourceType.GROUP.equals(resourceType)) {
                        map.put(Constant.KEY_GROUP_ID, mObjectId);
                        url = Constant.URL_GROUP_ALBUM_CREATE;

                    }else if (Constant.ResourceType.CLASSROOM.equals(resourceType)) {
                        map.put(Constant.KEY_CLASSROOM_ID, mObjectId);
                        url = Constant.URL_CLASSROOM_ALBUMCREATE;

                    } else if (Constant.ResourceType.BUSINESS.equals(resourceType)) {
                        map.put(Constant.KEY_BUSINESS_ID, mObjectId);
                        url = Constant.URL_BUSINESS_ALBUM_CREATE;

                    } else {
                        map.put(Constant.KEY_PAGE_ID, mObjectId);
                        url = Constant.URL_PAGE_ALBUM_CREATE;

                    }

                    fragmentManager.beginTransaction()
                            .replace(R.id.container
                                    , AddToPlaylistFragment.newInstance(Constant.FormType.CREATE_ALBUM_OTHERS, map, url))
                            .addToBackStack(null)
                            .commit();
                    break;

                case R.id.llSelect:
                    showPopup(result.getMenus(), v, 10, this);
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

    public void getMapValues() {
        try {
            if (map != null) {
                resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
                url = (String) map.get(Constant.KEY_URI);
                map.remove(Constant.KEY_URI);

                if (map.containsKey(Constant.SELECT)) {
                    selectedScreen = (String) map.get(Constant.SELECT);
                    map.remove(Constant.SELECT);
                    //this menas showing in browse screens ,enable SwipeToRefresh
                    isRefreshEnabled = true;
                } else {
                    selectedScreen = "";
                    isRefreshEnabled = false;
                }

                if (Constant.ResourceType.PAGE.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_PAGE_ID))
                        mObjectId = (int) map.get(Constant.KEY_PAGE_ID);

                } else if (Constant.ResourceType.SES_EVENT.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_EVENT_ID))
                        mObjectId = (int) map.get(Constant.KEY_EVENT_ID);

                }else if (Constant.ResourceType.CLASSROOM.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_CLASSROOM_ID))
                        mObjectId = (int) map.get(Constant.KEY_CLASSROOM_ID);

                } else if (Constant.ResourceType.BUSINESS.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_BUSINESS_ID))
                        mObjectId = (int) map.get(Constant.KEY_BUSINESS_ID);

                } else if (Constant.ResourceType.PRODUCT.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_PRODUCT_ID))
                        mObjectId = (int) map.get(Constant.KEY_PRODUCT_ID);

                } else if (Constant.ResourceType.GROUP.equals(resourceType)) {
                    if (map.containsKey(Constant.KEY_GROUP_ID))
                        mObjectId = (int) map.get(Constant.KEY_GROUP_ID);

                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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

                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (map != null) {
                    request.params.putAll(map);
                }

                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                }

                if (!TextUtils.isEmpty(mSort)) {
                    request.params.put("sort", mSort);
                }
                if (!TextUtils.isEmpty(query)) {
                    request.params.put(Constant.KEY_SEARCH, query);
                }

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    if (null != parent)
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                                    AlbumResponse resp = new Gson().fromJson(response, AlbumResponse.class);

                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        videoList.clear();
                                    }

                                    wasListEmpty = videoList.size() == 0;
                                    result = resp.getResult();
                                    if (null != result.getAlbums())
                                        videoList.addAll(result.getAlbums());

                                    updateUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideLoaders();
                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void updateUpperLayout() {
        if (!isRefreshEnabled) {
            if (null != result.getMenus()) {
                v.findViewById(R.id.llSelect).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rlFilter).setVisibility(View.VISIBLE);
            } else {
                v.findViewById(R.id.llSelect).setVisibility(View.GONE);
            }
            v.findViewById(R.id.cvCreate).setVisibility(result.getCanCreate() ? View.VISIBLE : View.GONE);

            //TODO do not create button always
            //if (Constant.ResourceType.EVENT.equals(resourceType))
            // v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
        }
    }

    public void hideLoaders() {
        isLoading = false;
        hideBaseLoader();
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Options opt = result.getMenus().get(item.getItemId() - 11);
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
