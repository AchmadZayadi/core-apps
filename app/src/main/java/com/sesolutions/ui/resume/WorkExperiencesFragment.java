package com.sesolutions.ui.resume;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class WorkExperiencesFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    public String searchKey;
    public com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    public String txtNoData = Constant.MSG_NO_EXPERENCE;
    public SwipeRefreshLayout swipeRefreshLayout;
    WorkExprienceAdapter workExprienceAdapter;
    public List<WorkexprienceModel.ResultBean.ExperiencesBean> workexperincelist;
    int resumeid=0;

  /*  @Override
    public void onResume() {
        super.onResume();
        Log.e("TASK RESUME",""+Constant.backresume);
        if(Constant.backresume==Constant.FormType.CREATE_RESUME_EXPRIENCE || Constant.backresume==Constant.FormType.CREATE_RESUME_EXPRIENCE_EDIT){
            Constant.backresume=0;
            if(Constant.resumeid!=0){
                resumeid=Constant.resumeid;
                initScreenData();
                Constant.resumeid=0;
            }
        }
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
        applyTheme(v);

        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
    }

    public void setRecyclerView() {
        try {
            workexperincelist = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            workExprienceAdapter = new WorkExprienceAdapter(workexperincelist, context, this, this);
            recyclerView.setAdapter(workExprienceAdapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        if(Constant.resumeid!=0){
            resumeid= Constant.resumeid;
            Constant.resumeid=0;
        }
        init();
        setRecyclerView();
        result = null;
        callMusicAlbumApi(1);
    }


    public void callMusicAlbumApi(final int req) {
        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {

                    if(!ishide){
                         if (req != Constant.REQ_CODE_REFRESH)
                            showBaseLoader(true);
                    }else {
                        ishide=false;
                    }

                    HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUME_WORKEXPERIENCE);

                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put("resume_id", resumeid);

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
                                isLoading = false;
                                setRefreshing(swipeRefreshLayout, false);
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        workexperincelist=new ArrayList<>();
                                        workexperincelist.clear();

                                        try {
                                            WorkexprienceModel resp = new Gson().fromJson(response, WorkexprienceModel.class);
                                            if(resp.getResult().getExperiences()!=null)
                                            workexperincelist.addAll(resp.getResult().getExperiences());
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                        CustomLog.e("LISTSIZE", "" + workexperincelist.size());
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                setRefreshing(swipeRefreshLayout, false);

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        hideLoaders();

        runLayoutAnimation(recyclerView);

        workExprienceAdapter = new WorkExprienceAdapter(workexperincelist, context, this, this);
        recyclerView.setAdapter(workExprienceAdapter);
        workExprienceAdapter.notifyDataSetChanged();
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.tvNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);

    }


    public static WorkExperiencesFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen, int resumid) {
        WorkExperiencesFragment frag = new WorkExperiencesFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        frag.resumeid = resumid;
        return frag;
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                   callMusicAlbumApi(1);
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
            init();
            setRecyclerView();
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.SUCCESS:
                callMusicAlbumApi(1);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:
                WorkexprienceModel.ResultBean.ExperiencesBean resumesBean= (WorkexprienceModel.ResultBean.ExperiencesBean) object2;
                //  updatetitlte(resumesBean.getTitle(),true,postion);
                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_EXPERIENCE_EDIT);
                intent.putExtra(Constant.KEY_ID, resumeid);
                intent.putExtra(Constant.KEY_EXPERENCE_ID, resumesBean.getExperience_id());
                startActivityForResult(intent, EDIT_CHANNEL_ME);

                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                showDeleteDialog(context,postion);

                break;

        }
        return false;
    }


    public void showDeleteDialog(final Context context, final int experience_id) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_EX);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                calldeletetitle(false, Constant.CREDIT_RESUME_DELETE_WORKEXPERIENCE,experience_id);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    boolean ishide=false;
    private void calldeletetitle(boolean showLoader,String url,int experience_id) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("experience_id", experience_id);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            ishide=true;
                            //hideBaseLoader();
                            init();
                            setRecyclerView();
                            callMusicAlbumApi(1);

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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }
}
