package com.sesolutions.ui.resume;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AchivementCurriculerFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

        private RecyclerView recyclerView;
        private boolean isLoading;
        private final int REQ_LOAD_MORE = 2;
        public String searchKey;
        private ProgressBar pb;
        public String txtNoData = Constant.MSG_NO_REFERENCE;
        public SwipeRefreshLayout swipeRefreshLayout;
        com.sesolutions.ui.resume.AcivementsAdpterParent AcivementsAdpterParent;
        public List<AchivementParentModel> achivementParentModels;


    @Override
    public void onResume() {
        super.onResume();
        if(Constant.backresume==342 ||
            Constant.backresume== Constant.FormType.CREATE_RESUME_REFERENCE_EDIT){
            Constant.backresume=0;
            onRefresh();
        }
    }

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
                achivementParentModels = new ArrayList<>();
                recyclerView.setHasFixedSize(true);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                AcivementsAdpterParent = new AcivementsAdpterParent(achivementParentModels, context, this, this);
                recyclerView.setAdapter(AcivementsAdpterParent);
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
            init();
            setRecyclerView();
             callMusicAlbumApi(1,true);
        }


        public void callMusicAlbumApi(final int req, boolean isloadingenable) {
            try {
                if (isNetworkAvailable(context)) {
                    isLoading = true;
                    try {

                        if(req!= Constant.REQ_CODE_REFRESH){
                            if(isloadingenable)
                            showBaseLoader(true);
                        }

                        HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUME_Achivements);
                        Map<String, Object> map = activity.filteredMap;
                        if (null != map) {
                            request.params.putAll(map);
                        }
                        if (!TextUtils.isEmpty(searchKey)) {
                            request.params.put(Constant.KEY_SEARCH, searchKey);
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
                                    setRefreshing(swipeRefreshLayout, false);
                                    CustomLog.e("repsonse1", "" + response);
                                    if (response != null) {
                                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                        if (TextUtils.isEmpty(err.getError())) {
                                            achivementParentModels.clear();
                                            try {
                                                AchivementParentModel resp = new Gson().fromJson(response, AchivementParentModel.class);
                                                 if(resp.getResult()!=null)
                                                    achivementParentModels.add(resp);
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
            AcivementsAdpterParent.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
          //  v.findViewById(R.id.llNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);
         //   v.findViewById(R.id.tvNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);

        }

        int resumeid;
        public static AchivementCurriculerFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen, int resumid) {
            AchivementCurriculerFragment frag = new AchivementCurriculerFragment();
            frag.listener = parent;
            frag.selectedScreen = selectedScreen;
            frag.resumeid = resumid;
            return frag;
        }

        @Override
        public void onLoadMore() {
            try {
              /*  if (result != null && !isLoading) {
                    if (result.getCurrentPage() < result.getTotalPage()) {
                        callMusicAlbumApi(REQ_LOAD_MORE);
                    }
                }*/
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
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH,true);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }



    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.SUCCESS:
                 callMusicAlbumApi(1,true);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:
                AchivementParentModel.ResultBean.AchievementsBean achievementsBean= (AchivementParentModel.ResultBean.AchievementsBean) object2;
              //  updatetitlte(resumesBean.getTitle(),true,postion);
                updatetitlte(""+achievementsBean.getAchievementname(),true,postion,"Edit Achievements","Enter your achievements","Update");


            /*    Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_REFERENCE_EDIT);
                intent.putExtra(Constant.KEY_ID, resumeid);
                intent.putExtra(Constant.KEY_Achievement_ID, achievementsBean.getAchievement_id());
                startActivityForResult(intent, EDIT_CHANNEL_ME);*/

                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                AchivementParentModel.ResultBean.AchievementsBean achievementsBean1= (AchivementParentModel.ResultBean.AchievementsBean) object2;

             //   calldeletetitleAChive(false,Constant.CREDIT_RESUME_DELETE_Achivements,achievementsBean1.getResume_id(),postion);
                showDeleteDialog(context,achievementsBean1.getResume_id(),postion,0);
                break;
            case Constant.Events.CLICKED_CUR_EDIT:
                AchivementParentModel.ResultBean.CurricularsBean curricularsBean= (AchivementParentModel.ResultBean.CurricularsBean) object2;
                //  updatetitlte(resumesBean.getTitle(),true,postion);

                updatetitltecuu(""+curricularsBean.getCurricularname(),true,postion,"Edit Curricular","Enter your curricular activities","Update");

               /* Intent intent11 = new Intent(activity, CommonActivity.class);
                intent11.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_REFERENCE_EDIT);
                intent11.putExtra(Constant.KEY_ID, resumeid);
                intent11.putExtra(Constant.KEY_Curricular_ID, curricularsBean.getCurricular_id());
                startActivityForResult(intent11, EDIT_CHANNEL_ME);*/

                break;
            case Constant.Events.CLICKED_CUR_DELETE:
                AchivementParentModel.ResultBean.CurricularsBean curricularsBean2= (AchivementParentModel.ResultBean.CurricularsBean) object2;

                showDeleteDialog(context,curricularsBean2.getResume_id(),postion,1);


                break;

            case Constant.Events.CLICKED_HEADER_ADD:

                if(postion==0){
                    calltitleresume2(false, Constant.CREDIT_RESUME_ADD_Achivements,postion);
                }else {
                    calltitleresume2(false, Constant.CREDIT_RESUME_ADD_CURRICULAR,postion);
                }


                 break;

        }
        return false;
    }

    private void calldeletetitleCurr(boolean showLoader,String url,int resume_id,int curricular_id) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resume_id);
                    request.params.put("curricular_id", curricular_id);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                           // hideBaseLoader();
                            callMusicAlbumApi(1,false);
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

    public void showDeleteDialog(final Context context, final int rsumeidnew,int itemid,int postflag) {
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
            if(postflag==0) {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_ACHIVE);
            }else {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_CURRICULAR);
            }
            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                if(postflag==0){
                    calldeletetitleAChive(false, Constant.CREDIT_RESUME_DELETE_Achivements,rsumeidnew,itemid);
                }else{
                    calldeletetitleCurr(false, Constant.CREDIT_RESUME_DELETE_CURRICULAR,rsumeidnew,itemid);
                }

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
    private void calldeletetitleAChive(boolean showLoader,String url,int resume_id,int achievement_id) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resume_id);
                    request.params.put("achievement_id", achievement_id);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                           // hideBaseLoader();
                            callMusicAlbumApi(1,false);
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

    private AlertDialog.Builder dialog;
    private void updatetitlte(String title,Boolean editable,int achivementid,String title_sub,String sub_msg,String btn_message) {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.resume_title_layout,null);
        EditText edittitle=view.findViewById(R.id.edittitle);
        TextView titleview=view.findViewById(R.id.titleview);
        Button updateUserBtn = view.findViewById(R.id.updateUserBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        if(editable){
            edittitle.setText(title);
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }else {
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edittitle.getText().toString().length()>0){
                    alertDialog.dismiss();
                    if(editable){
                        calltitleresume(false,edittitle.getText().toString(), Constant.CREDIT_RESUME_EDIT_Achivements,achivementid);
                    }else {
                        calltitleresume(false,edittitle.getText().toString(), Constant.CREDIT_RESUME_ADD_Achivements,0);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updatetitltecuu(String title,Boolean editable,int achivementid,String title_sub,String sub_msg,String btn_message) {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.resume_title_layout,null);
        EditText edittitle=view.findViewById(R.id.edittitle);
        TextView titleview=view.findViewById(R.id.titleview);
        Button updateUserBtn = view.findViewById(R.id.updateUserBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        if(editable){
            edittitle.setText(title);
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }else {
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edittitle.getText().toString().length()>0){
                    alertDialog.dismiss();
                    if(editable){
                        calltitleresume4(false,edittitle.getText().toString(), Constant.CREDIT_RESUME_EDIT_CURRICULAR,achivementid);
                    }else {
                        calltitleresume4(false,edittitle.getText().toString(), Constant.CREDIT_RESUME_ADD_CURRICULAR,0);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private void calltitleresume(boolean showLoader,String title,String url,int achivementid) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("achievementname", title);
                    request.params.put("resume_id", resumeid);
                    if(achivementid!=0){
                        request.params.put("achievement_id", achivementid);
                    }
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                         //   hideBaseLoader();
                            callMusicAlbumApi(1,false);

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
    private void calltitleresume4(boolean showLoader,String title,String url,int achivementid) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("curricularname", title);
                    request.params.put("resume_id", resumeid);
                    if(achivementid!=0){
                        request.params.put("curricular_id", achivementid);
                    }
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                         //   hideBaseLoader();
                            callMusicAlbumApi(1,false);

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
    private void calltitleresume2(boolean showLoader,String url,int postionflag) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resumeid);
                    request.params.put(Constant.KEY_GET_FORM, 1);
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
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        if(postionflag==0){
                                            updatetitlte("",false,postionflag,"Add Achievements","Enter your achievements","Save");
                                        }else {
                                            updatetitltecuu("",false,postionflag,"Add Curricular Activities","Enter your curricular activities","Save");
                                        }
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
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
