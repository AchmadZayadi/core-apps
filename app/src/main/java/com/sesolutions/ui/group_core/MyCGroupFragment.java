package com.sesolutions.ui.group_core;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sesolutions.responses.Group;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CreateEditCoreForm;
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

public class MyCGroupFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object> {

    private static final int CODE_ALBUM = 100;
    private static final int CODE_PLAYLISTS = 200;
    public MyCGroupAdapter adapter;
    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private CommonResponse.Result result;
    private ProgressBar pb;
    private CGroupParentFragment parent;
    private List<Group> lists;
    private boolean isAlbumSelected;
    private int colorPrimary;
    private TextView tvAlbums;
    private TextView tvPlaylists;
    private boolean isAlbumLoaded;
    private boolean isPlaylistLoaded;
    // private List<Blog> albumsList;
    private RelativeLayout hiddenPanel;
    private int loggedinId;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static MyCGroupFragment newInstance(CGroupParentFragment parent) {
        MyCGroupFragment frag = new MyCGroupFragment();
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_album, container, false);
        applyTheme(v);
        return v;
    }


    public void initScreenData() {
        init();
        isAlbumSelected = true;
        loggedinId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        setRecyclerView();
        callMusicAlbumApi(CODE_ALBUM, Constant.BASE_URL + Constant.URL_MY_CGROUP + loggedinId + Constant.POST_URL);
    }

    private void init() {

        try {
            recyclerView = v.findViewById(R.id.rvSetting);
            v.findViewById(R.id.llToggle).setVisibility(View.GONE);
            pb = v.findViewById(R.id.pb);

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void setRecyclerView() {
        try {
            lists = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MyCGroupAdapter(lists, context, this, this);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void callMusicAlbumApi(final int req, String url) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == CODE_ALBUM) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    request.params.put(Constant.KEY_TYPE, Constant.VALUE_MANAGE);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    // request.headres.put("Content-Type", "application/x-www-form-urlencoded");
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
                                        parent.isMyAlbumLoaded = true;
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            lists.clear();
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);

                                        isAlbumLoaded = true;
                                        result = resp.getResult();
                                        if (null != result.getGroups()) {
                                            lists.addAll(result.getGroups());
                                        }

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
                }
                Log.d(Constant.TAG, "login Stop");
            } else {
                hideLoaders();
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
        }


    }


    //public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideBaseLoader();
    }

    public void showDeleteDialog(final int albumId, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_ALBUM);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(albumId, position);
                    //callSaveFeedApi( Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int albumId, final int position) {

        try {
            if (isNetworkAvailable(context)) {

                lists.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, lists.size());

                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + Constant.URL_DELETE_CGROUP /*+ albumId */ + Constant.POST_URL);
                    request.params.put(Constant.KEY_GROUP_ID, albumId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    // Util.showSnackbar(v, new JSONObject(response).getString("result"));
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
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideBaseLoader();
                    Log.d(Constant.TAG, "Error while login" + e);
                }
                Log.d(Constant.TAG, "login Stop");
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_group_created_you);
        recyclerView.setVisibility(lists.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.llNoData).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, Constant.BASE_URL + Constant.URL_MY_CGROUP + loggedinId + Constant.POST_URL);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        CustomLog.d("" + value, "" + object1);
        switch (object1) {
            case Constant.Events.CLICKED_OPTION:
                Util.showOptionsPopUp((View) value, postion, result.getGroups().get(postion).getMenus(), this);
                break;

            case Constant.Events.MUSIC_MAIN:
                goToViewGroupFragment(value, postion);
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                //slideUpDown();
                Options vo = lists.get(Integer.parseInt("" + value)).getMenus().get(postion);
                int albumId = lists.get(Integer.parseInt("" + value)).getGroupId();
                performMusicOptionClick(albumId, vo, Integer.parseInt("" + value), postion);

                break;
        }
        return false;
    }

    private void goToViewGroupFragment(Object view, int position) {
        goToViewCGroupFragment(lists.get(position).getGroupId());
        /*GroupAdapter.Holder holder = (GroupAdapter.Holder) view;
        try {
            String transitionName = lists.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
            ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);

            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, lists.get(position).getImages().getMain());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewGroupFragment.newInstance(lists.get(position).getGroupId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToViewGroupFragment(lists.get(position).getGroupId());
        }*/

    }

    private void performMusicOptionClick(int albumId, Options vo, int listPosition, int postion) {
        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                fragmentManager.beginTransaction().replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.EDIT_GROUP, null, Constant.BASE_URL + Constant.URL_EDIT_CGROUP + albumId + Constant.POST_URL
                        , lists.get(listPosition).getGroupId())).addToBackStack(null).commit();

                /*gotoFormFragment(Constant.FormType.EDIT_GROUP, albumId
                        , Constant.BASE_URL + Constant.URL_EDIT_CGROUP + albumId + Constant.POST_URL
                        , lists.get(listPosition).getGroupId());*/
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(lists.get(listPosition).getGroupId(), listPosition);
                break;
        }
    }

    private void gotoFormFragment(int editGroup, int categoryId, String url, int albumId) {

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_MODULE, Constant.ModuleName.CORE_GROUP);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(editGroup, map, url, albumId)).addToBackStack(null).commit();
    }


    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH, Constant.BASE_URL + Constant.URL_MY_CGROUP + loggedinId + Constant.POST_URL);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
