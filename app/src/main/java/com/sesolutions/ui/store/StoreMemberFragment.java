package com.sesolutions.ui.store;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.member.ProfileMember;
import com.sesolutions.responses.member.ProfileMemberResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter;
import com.sesolutions.ui.page.PageMemberAdapter;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreMemberFragment extends BaseFragment implements OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final int REQ_SEARCH = 2;
    private static final int REQ_LOAD_MORE = 3;
    private static final int REQ_REMOVE = 400;
    private static final int REQ_REJECT = 401;
    private static final int REQ_CANCEL = 402;
    private static final int REQ_APPROVE = 403;
    private View v;

    public RecyclerView recyclerView;
    private List<UserMaster> friendList;
    // private List<UserMaster> totalItems;
    private PageMemberAdapter adapter;
    private ProfileMember result;
    private boolean isLoading;
    private boolean isContentLoaded;
    private ProgressBar pb;
    private int resourceId;
    private Bundle bundle;
    private String url;
    private String resourceType;
    private AppCompatEditText etMusicSearch;
    private String query;
    private Map<String, Object> map;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String waiting;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_member, container, false);
        applyTheme(v);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        return v;
    }

    @Override
    public void initScreenData() {
        if (!isContentLoaded) {
            init();
            getBundle();
            setRecyclerView();
            callNotificationApi(1);
        }
    }

    private void getBundle() {
        if (bundle != null) {
            resourceId = bundle.getInt(Constant.KEY_RESOURCE_ID);
            resourceType = bundle.getString(Constant.KEY_RESOURCES_TYPE);
            url = bundle.getString(Constant.KEY_URI);
            map = (Map<String, Object>) bundle.getSerializable(Constant.POST_REQUEST);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);

        v.findViewById(R.id.cvWaiting).setOnClickListener(this);
        etMusicSearch = v.findViewById(R.id.etMusicSearch);
        etMusicSearch.setHint(R.string.TITLE_SEARCH_MEMBER);

        v.findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
        v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
        final View ivCancel = v.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> {
            query = "";
            etMusicSearch.setText("");
        });
        final View ivMic = v.findViewById(R.id.ivMic);
        ivMic.setOnClickListener(v -> {
            closeKeyboard();
            TTSDialogFragment.newInstance(StoreMemberFragment.this).show(fragmentManager, "tts");
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
                friendList.clear();
                adapter.notifyDataSetChanged();
                callNotificationApi(REQ_SEARCH);
                //   }
                return true;
            }
            return false;
        });

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PageMemberAdapter(friendList, context, this, this);
            adapter.setType(resourceType);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callNotificationApi(Constant.REQ_CODE_REFRESH);
    }

    private void callNotificationApi(final int REQ) {
        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (REQ_LOAD_MORE == REQ) {
                    pb.setVisibility(View.VISIBLE);
                } else if (REQ != Constant.REQ_CODE_REFRESH) {
                    showView(v.findViewById(R.id.pbMain));
                }
                try {
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);
                    if (map != null)
                        request.params.putAll(map);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (!TextUtils.isEmpty(query)) {
                        request.params.put(Constant.KEY_SEARCH, query);
                    }
                    if (null != waiting) {
                        request.params.put("waiting", waiting);
                    }
                    if (REQ == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideAllLoaders();

                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                isContentLoaded = true;
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    ProfileMemberResponse resp = new Gson().fromJson(response, ProfileMemberResponse.class);
                                    result = resp.getResult();

                                    //clear all saved data in case of "Not searching"
                                    if (REQ != REQ_LOAD_MORE) {
                                        friendList.clear();
                                    }
                                    wasListEmpty = friendList.size() == 0;

                                    if (null != resp.getResult().getMembers()) {
                                        friendList.addAll(resp.getResult().getMembers());
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    //goIfPermissionDenied(err.getError());
                                }
                            } else {
                                notInternetMsg(v);
                            }
                            showHideTabOptions();
                            updateRecyclerView();
                        } catch (Exception e) {
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

    private void showHideTabOptions() {

        if (null != result && result.getOptions() != null) {
            v.findViewById(R.id.cvWaiting).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getOptions().getLabel());
        } else {
            v.findViewById(R.id.cvWaiting).setVisibility(View.GONE);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideBaseLoader();
        swipeRefreshLayout.setRefreshing(false);
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
    }

    private void updateRecyclerView() {
        isLoading = false;
        // updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_FOLLOWER);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  public static GroupMemberFragment newInstance(int resourceId) {
        GroupMemberFragment frag = new GroupMemberFragment();
        frag.resourceId = resourceId;
        return frag;
    }*/

    public static StoreMemberFragment newInstance(Bundle bundle) {
        StoreMemberFragment frag = new StoreMemberFragment();
        frag.bundle = bundle;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goToProfileFragment(friendList.get(postion).getUserId(), (PageMemberAdapter.ContactHolder) value, postion);
                break;

            case Constant.Events.FEED_UPDATE_OPTION:
                if (!isNetworkAvailable(context)) {
                    notInternetMsg(v);
                } else if (Constant.ResourceType.SES_EVENT.equals(resourceType)) {
                    performEventMemberOptionClick(Integer.parseInt("" + value), postion);
                } else if (Constant.ResourceType.STORE.equals(resourceType)) {
                    performStoreMemberOptionClick(Integer.parseInt("" + value), postion);
                }
                break;

            case REQ_APPROVE:
            case REQ_CANCEL:
            case REQ_REJECT:
            case REQ_REMOVE:
                hideAllLoaders();
                try {
                    if (value != null) {
                        ErrorResponse err = new Gson().fromJson("" + value, ErrorResponse.class);
                        if (TextUtils.isEmpty(err.getError())) {
                            String message = new JSONObject("" + value).optJSONObject("result").optString("message");
                            Util.showSnackbar(v, message);
                            onRefresh();
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    }
                } catch (JSONException e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                    onRefresh();
                }
                break;

            case Constant.Events.FEED_FILTER_OPTION:
                result.getMenus().get(postion);
                break;

            case Constant.Events.TTS_POPUP_CLOSED:
                query = "" + value;
                etMusicSearch.setText(query);
                result = null;
                friendList.clear();
                adapter.notifyDataSetChanged();
                callNotificationApi(REQ_SEARCH);
                break;
        }

        return false;

    }

    private void performStoreMemberOptionClick(int listPos, int position) {

        int dialogMsg = R.string.EMPTY;
        int buttonTxt = R.string.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        Options opt = friendList.get(listPos).getOptions().get(position);
        switch (opt.getName()) {
            case "cancelinvite":
                dialogMsg = (R.string.msg_request_cancel_store);
                buttonTxt = (R.string.cancel_request);
                url[0] = Constant.URL_STORE_REMOVE_MEMBER;
                req[0] = REQ_CANCEL;
                break;

            case "rejectrequest":
            case "removemember":
                dialogMsg = (R.string.msg_remove_member_store);
                buttonTxt = (R.string.remove_member);
                url[0] = Constant.URL_STORE_REMOVE_MEMBER;
                req[0] = REQ_REMOVE;
                break;

            case "approverequest":
                dialogMsg = (R.string.msg_approve_member_event);
                buttonTxt = (R.string.approve_member);
                url[0] = Constant.URL_STORE_APPROVE_MEMBER;
                req[0] = REQ_APPROVE;
                break;

        }

        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_STORE_ID, resourceId);
            map.put(Constant.KEY_USER_ID, friendList.get(listPos).getUserId());
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
            tvMsg.setText(dialogMsg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(buttonTxt);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showBaseLoader(true);
                new ApiController(url[0], map, context, StoreMemberFragment.this, req[0]).execute();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void performEventMemberOptionClick(int listPos, int position) {


        int dialogMsg = R.string.EMPTY;
        int buttonTxt = R.string.EMPTY;
        final String[] url = {Constant.EMPTY};
        final int[] req = {0};
        Options opt = friendList.get(listPos).getOptions().get(position);
        switch (opt.getName()) {
           /* case "cancelinvite":
                dialogMsg = (R.string.msg_request_cancel_event);
                buttonTxt = (R.string.cancel_request);
                url[0] = Constant.URL_EVENT_REMOVE_MEMBER;
                req[0] = REQ_CANCEL;
                break;
            case Constant.OptionType.REMOVE:
            case Constant.OptionType.REJECT:
                dialogMsg = (R.string.msg_remove_member_event);
                buttonTxt = (R.string.remove_member);
                url[0] = Constant.URL_EVENT_REMOVE_MEMBER;
                req[0] = REQ_REMOVE;
                break;
				*/
            //for STORE members
            case "removemember":
                dialogMsg = (R.string.msg_remove_member_store);
                buttonTxt = (R.string.remove_member);
                url[0] = Constant.URL_STORE_REMOVE_MEMBER;
                req[0] = REQ_REMOVE;
                break;

            case "approve":
                dialogMsg = (R.string.msg_approve_member_event);
                buttonTxt = (R.string.approve_member);
                url[0] = Constant.URL_EVENT_APPROVE_MEMBER;
                req[0] = REQ_APPROVE;
                break;

        }

        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_EVENT_ID, friendList.get(listPos).getResourceId());
            map.put(Constant.KEY_USER_ID, friendList.get(listPos).getUserId());
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
            tvMsg.setText(dialogMsg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(buttonTxt);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showBaseLoader(true);
                new ApiController(url[0], map, context, StoreMemberFragment.this, req[0]).execute();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void goToProfileFragment(int userId, PageMemberAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = friendList.get(position).getDisplayname();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, friendList.get(position).getOwnerPhoto());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ViewProfileFragment.newInstance(userId, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(userId);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvWaiting:
                waiting = result.getOptions().getValue();
                if ("1".equals(waiting)) {
                    v.findViewById(R.id.rlCommentEdittext).setVisibility(View.GONE);
                } else {
                    v.findViewById(R.id.rlCommentEdittext).setVisibility(View.VISIBLE);
                }
                onRefresh();
                break;
        }
    }


}
