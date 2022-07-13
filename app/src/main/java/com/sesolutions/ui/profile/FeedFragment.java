package com.sesolutions.ui.profile;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.FeedActivityAdapter;
import com.sesolutions.ui.dashboard.FeedHelper;
import com.sesolutions.utils.ChildAttachStateChangeListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;


public class FeedFragment extends FeedHelper implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnLoadMoreListener {
    public RecyclerView recycleViewFeedMain;
    public LinearLayoutManager layoutManager;

    private ShimmerFrameLayout mShimmerViewContainer;
    int lastpostion=0;

    public static FeedFragment newInstance(int albumId) {
        FeedFragment frag = new FeedFragment();
        frag.userId = albumId;
        return frag;
    }

    public static FeedFragment newInstance(int rcId, String rcType) {
        FeedFragment frag = new FeedFragment();
        frag.resourceId = rcId;
        frag.resourceType = rcType;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }

        try {
            v = inflater.inflate(R.layout.fragment_common_feed, container, false);

            applyTheme();
            mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);
            mShimmerViewContainer.startShimmerAnimation();

        } catch (Exception e) {
            CustomLog.e(e);
        }

        return v;
    }


    public void initScreenData() {
        super.canCacheData = false;
        callComposerOptionApi();
    }

    private void initComposer() {
        pb = v.findViewById(R.id.pb);
    }

    @Override
    public void initMainRecyclerView() {
        if (recycleViewFeedMain != null) return;
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recycleViewFeedMain = v.findViewById(R.id.recycler_view_feed_main);
        setFeedMainRecycleView();
        recycleViewFeedMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        final View child = layoutManager.findViewByPosition(firstVisiblePosition);
                        try {
                            if (null != child){
                                if(feedActivityList.get(firstVisiblePosition).getAttachment().getAttachmentType().equalsIgnoreCase("video")){
                                    if(lastpostion!=0){
                                        feedActivityList.get(lastpostion).getAttachment().set_can_play(false);
                                        adapterFeedMain.notifyItemChanged(lastpostion);
                                    }
                                    feedActivityList.get(firstVisiblePosition).getAttachment().set_can_play(true);
                                    adapterFeedMain.notifyItemChanged(firstVisiblePosition);
                                    lastpostion=firstVisiblePosition;
                                }else {
                                    feedActivityList.get(firstVisiblePosition).getAttachment().set_can_play(false);
                                    adapterFeedMain.notifyItemChanged(lastpostion);
                                    lastpostion=0;
                                }
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    try {
                        CustomLog.e(e);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void updateComposerUI() {
        try {
            initComposer();
            //  SPref.getInstance().saveReactionPluginType(context, composerOption.getResult().getReaction_plugin());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void updateFeedMainRecycleview() {
        if (null != mShimmerViewContainer && mShimmerViewContainer.getVisibility() == View.VISIBLE) {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            v.findViewById(R.id.rlSimmerMain).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        }

        super.updateFeedMainRecycleview();
        try {
                SPref.getInstance().saveAttributionOptions(context, feedResponse.getResult().getModuleAttribution(resourceType));
                SPref.getInstance().saveAttribution(context, feedResponse.getResult().getActivityAttribution());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    protected void setContentLoaded() {
    }

    public void setFeedMainRecycleView() {
        try {
            feedActivityList = new ArrayList<>();

            //adding first item for composer
            if (null != composerOption) {
                feedActivityList.add(new Activity(Constant.ItemType.COMPOSER));
            }

            recycleViewFeedMain.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            recycleViewFeedMain.setLayoutManager(layoutManager);
            //this will disable blink effect happening on  "on_Item_change"
            ((SimpleItemAnimator) recycleViewFeedMain.getItemAnimator()).setSupportsChangeAnimations(false);
            adapterFeedMain = new FeedActivityAdapter(feedActivityList, context, this);

            adapterFeedMain.setComposer(composerOption);

            recycleViewFeedMain.setAdapter(adapterFeedMain);
            adapterFeedMain.setLoadListener(this);
            adapterFeedMain.setHome(true);

            recycleViewFeedMain.addOnChildAttachStateChangeListener(new ChildAttachStateChangeListener());

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void goToComment(int position) {
        //Activity vo = feedActivityList.get(position);

        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_COMMENT);
        intent.putExtra(Constant.KEY_ACTION_ID, feedActivityList.get(position).getActionId());
        String guid = feedActivityList.get(position).getAttributionGuid();
        if (null != guid) {
            intent.putExtra(Constant.KEY_GUID, guid);
        }
        //  intent.putExtra(Constant.KEY_RESOURCES_TYPE, Constant.VALUE_RESOURCES_TYPE);
        startActivity(intent);

    }

    public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {
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
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
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


    private void callSaveFeedApi(final int reqCode, String url, int actionId, final Options vo, final int actPosition, final int position) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
//                isLoading = true;

                try {
                    // showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_ACTION_ID, actionId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            //  hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    BaseResponse<Object> resp = new Gson().fromJson(response, BaseResponse.class);


                                    if (TextUtils.isEmpty(resp.getError())) {
                                        switch (reqCode) {
                                            case REQ_CODE_OPTION_DELETE:
                                                BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                                feedActivityList.remove(actPosition);
                                                adapterFeedMain.notifyItemRemoved(actPosition);
                                                adapterFeedMain.notifyItemRangeChanged(actPosition, feedActivityList.size());
                                                Util.showSnackbar(v, res.getResult());
                                                break;

                                            case REQ_CODE_OPTION_SAVE:
                                                updateOptionText(actPosition, position, "unsave", Constant.TXT_UNSAVE_FEED);
                                                break;
                                            case REQ_CODE_OPTION_UNSAVE:
                                                updateOptionText(actPosition, position, "save", Constant.TXT_SAVE_FEED);
                                                break;
                                            case REQ_CODE_OPTION_COMMENT_DISABLE:
                                                updateOptionText(actPosition, position, "enable_comment", Constant.TXT_ENABLE_COMMENT);
                                                break;

                                            case REQ_CODE_OPTION_COMMENT_ENABLE:
                                                updateOptionText(actPosition, position, "disable_comment", Constant.TXT_DISABLE_COMMENT);
                                                break;
                                            case REQ_CODE_OPTION_HIDE_FEED:
                                                feedActivityList.get(actPosition).setHidden(true);
                                                feedActivityList.get(actPosition).setReported(false);
                                                adapterFeedMain.notifyItemChanged(actPosition);
                                                break;
                                            case REQ_CODE_OPTION_UNDO:
                                                feedActivityList.get(actPosition).setHidden(false);
                                                feedActivityList.get(actPosition).setReported(false);
                                                adapterFeedMain.notifyItemChanged(actPosition);
                                                break;
                                            case REQ_CODE_OPTION_REPORT:
                                                feedActivityList.get(actPosition).setHidden(true);
                                                feedActivityList.get(actPosition).setReported(true);
                                                adapterFeedMain.notifyItemChanged(actPosition);
                                                break;
                                        }

                                    } else {
                                        Util.showSnackbar(v, resp.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateOptionText(int actPosition, int position, String name, String value) {
        CustomLog.e("values", actPosition + "___" + position + "___");
        feedActivityList.get(actPosition).getOptions().get(position).setValue(value);
        feedActivityList.get(actPosition).getOptions().get(position).setName(name);
        adapterFeedMain.notifyItemChanged(actPosition);
    }

    @Override
    public void onLoadMore() {
        CustomLog.e("feedActivityload", "load");
        if (!isLoading && !feedResponse.getResult().getEndOfFeed()) {
            pb.setVisibility(View.VISIBLE);
            callFeedApi(REQ_CODE_LOADING_MORE_FEED);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callFeedApi(REQ_CODE_REFRESH);
    }

    public void scrollToStart() {
        if (null != feedActivityList && feedActivityList.size() > 0) {
            View visibleChild = recycleViewFeedMain.getChildAt(0);
            int positionOfChild = recycleViewFeedMain.getChildAdapterPosition(visibleChild);
            if (positionOfChild > 0) {
                recycleViewFeedMain.smoothScrollToPosition(0);
            } else {
                onRefresh();
            }
        }
    }

    public void updateComposerProfileImage(String url) {
        try {
            //TODO
            adapterFeedMain.setComposerProfileImage(url);
            adapterFeedMain.notifyItemChanged(0);
            //  Util.showImageWithGlide(ivProfileCompose, url, context, R.drawable.placeholder_3_2);
        } catch (Exception e) {
            //IGNORE THIS ERROR
        }
    }

}
