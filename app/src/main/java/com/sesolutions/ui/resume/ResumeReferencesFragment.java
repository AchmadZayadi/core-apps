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

public class ResumeReferencesFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

        private RecyclerView recyclerView;
        private boolean isLoading;
        private final int REQ_LOAD_MORE = 2;
        public String searchKey;
        public String txtNoData = Constant.MSG_NO_REFERENCE;
        public SwipeRefreshLayout swipeRefreshLayout;
        com.sesolutions.ui.resume.WorkReferenceAdapter WorkReferenceAdapter;
        public List<ResumeRefereneceModel.ResultBean.ReferencesBean> referencesBeanList;


/*    @Override
    public void onResume() {
        super.onResume();
        Log.e("TASK RESUME",""+Constant.backresume);
        if(Constant.backresume==342 ||
            Constant.backresume==Constant.FormType.CREATE_RESUME_REFERENCE_EDIT){
            Constant.backresume=0;
            if(Constant.resumeid!=0){
                resumeid=Constant.resumeid;
                init();
                setRecyclerView();
                callMusicAlbumApi1234(1);
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
        }

        public void setRecyclerView() {
            try {
                referencesBeanList = new ArrayList<>();
                recyclerView.setHasFixedSize(true);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                WorkReferenceAdapter = new WorkReferenceAdapter(referencesBeanList, context, this, this);
                recyclerView.setAdapter(WorkReferenceAdapter);
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
            callMusicAlbumApi1234(1);
        }


        public void callMusicAlbumApi1234(final int req) {
            try {
                if (isNetworkAvailable(context)) {
                    isLoading = true;
                    try {
                        if(!isdelete){
                            if (req != Constant.REQ_CODE_REFRESH)
                                showBaseLoader(true);
                        }else {
                            isdelete=false;
                        }


                        HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUME_REFERENCE);
                        Map<String, Object> map = activity.filteredMap;
                        if (null != map) {
                            request.params.putAll(map);
                        }
                       request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                        request.params.put("resume_id", resumeid);
                       // request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                    CustomLog.e("repsonse1", "" + response);
                                    swipeRefreshLayout.setRefreshing(false);
                                    if (response != null) {
                                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                        if (TextUtils.isEmpty(err.getError())) {
                                           try {
                                               referencesBeanList = new ArrayList<>();
                                               referencesBeanList.clear();
                                                ResumeRefereneceModel resp = new Gson().fromJson(response, ResumeRefereneceModel.class);
                                                if(resp.getResult().getReferences()!=null){
                                                    referencesBeanList.addAll(resp.getResult().getReferences());
                                                }

                                            }catch (Exception ex){
                                                ex.printStackTrace();
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
                        isLoading = false;
                        hideBaseLoader();

                    }

                } else {
                    isLoading = false;
                    setRefreshing(swipeRefreshLayout, false);

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
        }

        private void updateAdapter() {
            hideLoaders();

            runLayoutAnimation(recyclerView);
            WorkReferenceAdapter = new WorkReferenceAdapter(referencesBeanList, context, this, this);
            recyclerView.setAdapter(WorkReferenceAdapter);
            WorkReferenceAdapter.notifyDataSetChanged();

            ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
            v.findViewById(R.id.llNoData).setVisibility(referencesBeanList.size() > 0 ? View.GONE : View.VISIBLE);
            v.findViewById(R.id.tvNoData).setVisibility(referencesBeanList.size() > 0 ? View.GONE : View.VISIBLE);

        }

        int resumeid;
        public static ResumeReferencesFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen, int resumid) {
            ResumeReferencesFragment frag = new ResumeReferencesFragment();
            frag.listener = parent;
            frag.selectedScreen = selectedScreen;
            frag.resumeid = resumid;
            return frag;
        }

        @Override
        public void onLoadMore() {
            try {
            //    callMusicAlbumApi(REQ_LOAD_MORE);
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

            callMusicAlbumApi1234(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }




    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.SUCCESS:
                callMusicAlbumApi1234(1);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:
                ResumeRefereneceModel.ResultBean.ReferencesBean resumesBean= (ResumeRefereneceModel.ResultBean.ReferencesBean) object2;
              //  updatetitlte(resumesBean.getTitle(),true,postion);

                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_REFERENCE_EDIT);
                intent.putExtra(Constant.KEY_ID, resumeid);
                intent.putExtra(Constant.KEY_REFERENCE_ID, resumesBean.getReference_id());
                startActivityForResult(intent, EDIT_CHANNEL_ME);

                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                showDeleteDialog(context,postion);

                break;

        }
        return false;
    }

    public void showDeleteDialog(final Context context, final int refereneceid) {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_REFR);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                calldeletetitle(false, Constant.CREDIT_RESUME_DELETE_REFERENCE,refereneceid);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    boolean isdelete=false;
    private void calldeletetitle(boolean showLoader,String url,int referenceid) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("reference_id", referenceid);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            isdelete=true;
                          //  hideBaseLoader();
                            init();
                            setRecyclerView();
                            callMusicAlbumApi1234(1);

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



   /* public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                goToViewFragment(postion);
                break;

        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(workexperincelist.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
    }
