package com.sesolutions.ui.message;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;


public class MessageChatFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener {

    private View v;


    public RecyclerView recyclerView;
    private List<MessageInbox> friendList;
    private MessageInboxAdapter adapter;
    private String conversationId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_friend_request, container, false);
        try {


            // setRecyclerView();
            init();
            //  callApi();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        new ThemeManager().applyTheme((ViewGroup) v.findViewById(R.id.main_viewgroup), context);

        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        v.findViewById(R.id.bRefresh).setOnClickListener(this);
       /* ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);*/
        //initSlide();
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MessageInboxAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
            updateRecyclerView();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bRefresh:
                    callApi();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callApi() {
        try {

            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SENT);
                    // request.params.put(Constant.KEY_IMAGE, filePath);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp.getResult().getTotalPage() > 0)
                                        friendList.addAll(resp.getResult().getMessageList());
                                    setRecyclerView();
                                } else {
                                    notInternetMsg(v);
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
                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(String subjectId, String url, int position) {
        callAcceptRejectApi(subjectId, url, position);
        return false;

    }

    private void callAcceptRejectApi(String subjectId, String url, final int position) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, subjectId);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                    Util.showSnackbar(v, resp.getResult());
                                    friendList.remove(position);
                                    updateRecyclerView();
                                } else {
                                    notInternetMsg(v);
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
                    hideBaseLoader();

                }

            } else {
                hideBaseLoader();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SENT_MSG);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    public static Fragment newInstance(String conversationId) {
        MessageChatFragment frag = new MessageChatFragment();
        frag.conversationId = conversationId;
        return frag;
    }

    @Override
    public void onLoadMore() {

    }
}
