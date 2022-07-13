package com.sesolutions.ui.events;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.event.Discussion;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscussionViewFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_POST_ID = "post_id";
    private final String KEY_TOPIC_ID = "topic_id";
    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private EventResponse.Result result;
    private ProgressBar pb;
    private List<Discussion> categoryList;
    private DiscussionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int mObjectId;

    public static DiscussionViewFragment newInstance(int topicId) {
        DiscussionViewFragment frag = new DiscussionViewFragment();
        frag.mObjectId = topicId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_discussion_view, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
    }

    private void setRecyclerView() {
        try {
            categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DiscussionAdapter(categoryList, context, this, this);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //@Override
    public void onRefresh() {
        setRefreshing(swipeRefreshLayout, true);
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.cvPost:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_EVENT_ID, mObjectId);
                    // super.openFormFragment(Constant.FormType.CREATE_DISCUSSTION, map, Constant.URL_VIEW_DISCUSSION);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        // getMapValues();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

  /*  private void getMapValues() {
        try {
            if (map != null) {
                resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
                url = (String) map.get(Constant.KEY_URI);
                map.remove(Constant.KEY_URI);
                switch (resourceType) {
                    case Constant.ResourceType.PAGE:
                        mObjectId = (int) map.get(Constant.KEY_PAGE_ID);
                        break;
                    case Constant.ResourceType.SES_EVENT:
                        mObjectId = (int) map.get(Constant.KEY_EVENT_ID);
                        break;
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    private void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            if (req == REQ_LOAD_MORE) {
                pb.setVisibility(View.VISIBLE);
            } else if (req == 1) {
                showBaseLoader(true);
            }
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_DISCUSSION);
                request.params.put(KEY_TOPIC_ID, mObjectId);
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
                        try {
                            String response = (String) msg.obj;
                            hideAllLoaders();
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {

                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        categoryList.clear();
                                    }
                                    EventResponse resp = new Gson().fromJson(response, EventResponse.class);
                                    result = resp.getResult();
                                    if (null != result.getPosts())
                                        categoryList.addAll(result.getPosts());
                                    showHideUpperLayout();
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }

                            }

                        } catch (Exception e) {
                            hideAllLoaders();
                            somethingWrongMsg(v);
                            CustomLog.e(e);
                        }

                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideAllLoaders();
                somethingWrongMsg(v);
            }
        } else {
            notInternetMsg(v);
        }

    }

    private void showHideUpperLayout() {
        if (null != result.getTopic()) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(result.getTopic().getLabel());
           /* v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getPostButton().getLabel());
            v.findViewById(R.id.cvPost).setOnClickListener(this);*/
            List<Options> list = result.getTopic().getValueList();
            FlowLayout flOptions = v.findViewById(R.id.flOption);
            flOptions.removeAllViews();
            flOptions.setVisibility(View.VISIBLE);
            if (list != null) {

                for (final Options opt : list) {

                    View view = getLayoutInflater().inflate(R.layout.layout_image_option, flOptions, false);
                    ((TextView) view.findViewById(R.id.tvPost)).setText(opt.getLabel());
                    ((ImageView) view.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                    view.findViewById(R.id.cvPost).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClicked(-1, opt.getName(), 0);
                        }
                    });
                    applyTheme(view);
                    flOptions.addView(view);
                }
            } else {
                flOptions.setVisibility(View.GONE);
            }
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        setRefreshing(swipeRefreshLayout, false);
        // hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
    }

    private void updateAdapter() {
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        if (categoryList.size() > 0) {
            recyclerView.smoothScrollToPosition(categoryList.size() - 1);
        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_topic);
        v.findViewById(R.id.llNoData).setVisibility(categoryList.size() > 0 ? View.GONE : View.VISIBLE);

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
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.REPLY_TOPIC) {
            activity.taskPerformed = 0;
            onRefresh();
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goToProfileFragment(categoryList.get(postion).getUserId());
                break;
            case -3:
                try {
                    String response = (String) screenType;
                    CustomLog.e("repsonse1", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (TextUtils.isEmpty(err.getError())) {
                            Util.showSnackbar(v, new JSONObject(response).getJSONObject("result").getString("message"));
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case -2:
                hideBaseLoader();
                CustomLog.e("response", "" + screenType);
                break;
            case -1:
                handleOptions("" + screenType);
                break;
            case Constant.Events.MUSIC_MAIN:
                //  super.goToCategoryFragment(categoryList.get(postion).getCategoryId(), categoryList.get(postion).getName());
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                int listPosition = Integer.parseInt("" + screenType);
                Options opt = categoryList.get(listPosition).getOptions().get(postion);
                if (Constant.OptionType.EDIT.equals(opt.getName())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(KEY_POST_ID, categoryList.get(listPosition).getPostId());
                    super.openFormFragment(Constant.FormType.EDIT_TOPIC, map, Constant.URL_EDIT_POST);
                } else {
                    showDeleteDialog(listPosition);
                }
                break;

        }
        return false;
    }

    public void showDeleteDialog(final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_POST);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();

                    if (isNetworkAvailable(context)) {

                        Map<String, Object> map = new HashMap<>();
                        map.put(KEY_POST_ID, categoryList.get(position).getPostId());
                        new ApiController(Constant.URL_DELETE_POST, map, context, DiscussionViewFragment.this, -3).execute();
                        categoryList.remove(position);
                        adapter.notifyItemRemoved(position);
                    } else {
                        notInternetMsg(v);
                    }

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

    private void handleOptions(String name) {
        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_TOPIC_ID, mObjectId);

            if (Constant.OptionType.POST_REPLY.equals(name)) {
                openFormFragment(Constant.FormType.REPLY_TOPIC, map, Constant.URL_REPLY_TOPIC);
            } else {
                map.put("watch", Constant.OptionType.STOP_WATCH_TOPIC.equals(name) ? 1 : 0);
                showBaseLoader(true);
                new ApiController(Constant.URL_TOPIC_WATCH, map, context, this, -2).execute();
            }
        } else {
            notInternetMsg(v);
        }
    }


}
